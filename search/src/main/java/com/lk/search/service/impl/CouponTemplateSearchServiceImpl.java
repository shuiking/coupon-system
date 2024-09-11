package com.lk.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lk.framework.errorcode.BaseErrorCode;
import com.lk.framework.exception.ClientException;
import com.lk.search.dao.entity.CouponTemplateDoc;
import com.lk.search.dto.req.CouponTemplatePageQueryReqDTO;
import com.lk.search.dto.resp.CouponTemplatePageQueryRespDTO;
import com.lk.search.service.CouponTemplateSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 优惠券模板搜索业务逻辑实现层
 *
 * @Author : lk
 * @create 2024/9/11
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponTemplateSearchServiceImpl implements CouponTemplateSearchService {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    @Override
    public IPage<CouponTemplatePageQueryRespDTO> pageQueryCouponTemplate(CouponTemplatePageQueryReqDTO requestParam) {
        // ES 不支持跳页式的深分页，如果不考虑跳页，可使用 Search After 分页方式提高性能
        if (requestParam.getCurrent() * requestParam.getSize() > 10000) {
            throw new ClientException(BaseErrorCode.SEARCH_AMOUNT_EXCEEDS_LIMIT);
        }

        // 构建条件分页查询条件
        Criteria criteria = new Criteria();
        if (StrUtil.isNotEmpty(requestParam.getName())) {
            criteria = criteria.and("name").matches(requestParam.getName());
        }

        if (StrUtil.isNotBlank(requestParam.getGoods())) {
            criteria = criteria.and("goods").matches(requestParam.getGoods());
        }

        if (Objects.nonNull(requestParam.getType())) {
            criteria = criteria.and("type").is(requestParam.getType());
        }
        if (Objects.nonNull(requestParam.getTarget())) {
            criteria = criteria.and("target").is(requestParam.getTarget());
        }

        Query query = CriteriaQuery.builder(criteria).build();
        query.setPageable(PageRequest.of((int) (requestParam.getCurrent() - 1), (int) requestParam.getSize()));

        // 执行分页查询
        SearchHits<CouponTemplateDoc> couponTemplatePageResult = elasticsearchTemplate.search(query, CouponTemplateDoc.class);
        List<CouponTemplatePageQueryRespDTO> couponTemplatePageRecords = couponTemplatePageResult.stream()
                .map(each -> BeanUtil.copyProperties(each.getContent(), CouponTemplatePageQueryRespDTO.class))
                .collect(Collectors.toList());

        // 手动组装返回值
        IPage<CouponTemplatePageQueryRespDTO> pageResult = new Page<>(requestParam.getCurrent(), requestParam.getSize());
        pageResult.setRecords(couponTemplatePageRecords);
        pageResult.setTotal(couponTemplatePageResult.getTotalHits());
        pageResult.setPages((long) Math.ceil(couponTemplatePageResult.getTotalHits() * 1.0 / requestParam.getSize()));
        return pageResult;
    }
}
