package com.lk.settlement.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lk.framework.config.RedisDistributedProperties;
import com.lk.framework.exception.ClientException;
import com.lk.settlement.common.context.UserContext;
import com.lk.settlement.dto.req.QueryCouponGoodsReqDTO;
import com.lk.settlement.dto.req.QueryCouponsReqDTO;
import com.lk.settlement.dto.resp.CouponTemplateQueryRespDTO;
import com.lk.settlement.dto.resp.QueryCouponsDetailRespDTO;
import com.lk.settlement.dto.resp.QueryCouponsRespDTO;
import com.lk.settlement.service.CouponQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.lk.settlement.common.constant.EngineRedisConstant.COUPON_TEMPLATE_KEY;
import static com.lk.settlement.common.constant.EngineRedisConstant.USER_COUPON_TEMPLATE_LIST_KEY;


/**
 * 查询用户可用优惠券列表接口
 *
 * @Author : lk
 * @create 2024/9/10
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponQueryServiceImpl implements CouponQueryService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisDistributedProperties redisDistributedProperties;

    // cpu密集型任务，所以核心线程数和最大线程数设置为cpu核心数
    private final ExecutorService executorService = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(),
            9999,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Override
    public QueryCouponsRespDTO listQueryUserCoupons(QueryCouponsReqDTO requestParam) {
        // 1、获取 Redis 中的用户优惠卷列表
        Set<String> rangeUserCoupons = stringRedisTemplate.opsForZSet().range(String.format(USER_COUPON_TEMPLATE_LIST_KEY, UserContext.getUserId()), 0, -1);

        if (rangeUserCoupons == null || rangeUserCoupons.isEmpty()) {
            return QueryCouponsRespDTO.builder()
                    .availableCouponList(new ArrayList<>())
                    .notAvailableCouponList(new ArrayList<>())
                    .build();
        }

        // 构建 Redis Key 列表
        List<String> couponTemplateIds = rangeUserCoupons.stream()
                .map(each -> StrUtil.split(each, "_").get(0))
                .map(each -> redisDistributedProperties.getPrefix() + String.format(COUPON_TEMPLATE_KEY, each))
                .collect(Collectors.toList());

        // 同步获取 Redis 数据并进行解析、转换和分区
        List<Object> rawCouponDataList = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            couponTemplateIds.forEach(each -> connection.hashCommands().hGetAll(each.getBytes()));
            return null;
        });

        // 解析 Redis 数据，并按 `goods` 字段进行分区处理
        Map<Boolean, List<CouponTemplateQueryRespDTO>> partitioned = JSON.parseArray(JSON.toJSONString(rawCouponDataList), CouponTemplateQueryRespDTO.class)
                .stream()
                .collect(Collectors.partitioningBy(coupon -> StrUtil.isEmpty(coupon.getGoods())));

        // goods 为空的列表
        List<CouponTemplateQueryRespDTO> goodsEmptyList = partitioned.get(true);
        // goods 不为空的列表
        List<CouponTemplateQueryRespDTO> goodsNotEmptyList = partitioned.get(false);

        // 针对当前订单可用/不可用的优惠券列表
        List<QueryCouponsDetailRespDTO> availableCouponList = Collections.synchronizedList(new ArrayList<>());
        List<QueryCouponsDetailRespDTO> notAvailableCouponList = Collections.synchronizedList(new ArrayList<>());

        // 2、并行处理 goodsEmptyList 和 goodsNotEmptyList 中的每个元素
        CompletableFuture<Void> emptyGoodsTasks = CompletableFuture.allOf(
                goodsEmptyList.stream()
                        .map(each -> CompletableFuture.runAsync(() -> {
                            QueryCouponsDetailRespDTO resultCouponDetail = BeanUtil.toBean(each, QueryCouponsDetailRespDTO.class);
                            JSONObject jsonObject = JSON.parseObject(each.getConsumeRule());
                            handleCouponLogic(resultCouponDetail, jsonObject, requestParam.getOrderAmount(), availableCouponList, notAvailableCouponList);
                        }, executorService))
                        .toArray(CompletableFuture[]::new)
        );

        Map<String, QueryCouponGoodsReqDTO> goodsRequestMap = requestParam.getGoodsList().stream()
                .collect(Collectors.toMap(QueryCouponGoodsReqDTO::getGoodsNumber, Function.identity()));
        CompletableFuture<Void> notEmptyGoodsTasks = CompletableFuture.allOf(
                goodsNotEmptyList.stream()
                        .map(each -> CompletableFuture.runAsync(() -> {
                            QueryCouponsDetailRespDTO resultCouponDetail = BeanUtil.toBean(each, QueryCouponsDetailRespDTO.class);
                            QueryCouponGoodsReqDTO couponGoods = goodsRequestMap.get(each.getGoods());
                            if (couponGoods == null) {
                                notAvailableCouponList.add(resultCouponDetail);
                            } else {
                                JSONObject jsonObject = JSON.parseObject(each.getConsumeRule());
                                handleCouponLogic(resultCouponDetail, jsonObject, couponGoods.getGoodsAmount(), availableCouponList, notAvailableCouponList);
                            }
                        }, executorService))
                        .toArray(CompletableFuture[]::new)
        );

        // 3、待两个异步任务集合完成
        CompletableFuture.allOf(emptyGoodsTasks, notEmptyGoodsTasks)
                .thenRun(() -> {
                    // 与业内标准一致，按最终优惠力度从大到小排序
                    availableCouponList.sort((c1, c2) -> c2.getCouponAmount().compareTo(c1.getCouponAmount()));
                })
                .join();

        // 构建最终结果并返回
        return QueryCouponsRespDTO.builder()
                .availableCouponList(availableCouponList)
                .notAvailableCouponList(notAvailableCouponList)
                .build();
    }

    // 优惠券判断逻辑，根据条件判断放入可用或不可用列表
    private void handleCouponLogic(QueryCouponsDetailRespDTO resultCouponDetail, JSONObject jsonObject, BigDecimal amount,
                                   List<QueryCouponsDetailRespDTO> availableCouponList, List<QueryCouponsDetailRespDTO> notAvailableCouponList) {
        BigDecimal termsOfUse = jsonObject.getBigDecimal("termsOfUse");
        BigDecimal maximumDiscountAmount = jsonObject.getBigDecimal("maximumDiscountAmount");

        switch (resultCouponDetail.getType()) {
            case 0: // 立减券
                resultCouponDetail.setCouponAmount(maximumDiscountAmount);
                availableCouponList.add(resultCouponDetail);
                break;
            case 1: // 满减券
                if (amount.compareTo(termsOfUse) >= 0) {
                    resultCouponDetail.setCouponAmount(maximumDiscountAmount);
                    availableCouponList.add(resultCouponDetail);
                } else {
                    notAvailableCouponList.add(resultCouponDetail);
                }
                break;
            case 2: // 折扣券
                if (amount.compareTo(termsOfUse) >= 0) {
                    BigDecimal discountRate = jsonObject.getBigDecimal("discountRate");
                    BigDecimal multiply = amount.multiply(discountRate);
                    if (multiply.compareTo(maximumDiscountAmount) >= 0) {
                        resultCouponDetail.setCouponAmount(maximumDiscountAmount);
                    } else {
                        resultCouponDetail.setCouponAmount(multiply);
                    }
                    availableCouponList.add(resultCouponDetail);
                } else {
                    notAvailableCouponList.add(resultCouponDetail);
                }
                break;
            default:
                throw new ClientException("无效的优惠券类型");
        }
    }
}
