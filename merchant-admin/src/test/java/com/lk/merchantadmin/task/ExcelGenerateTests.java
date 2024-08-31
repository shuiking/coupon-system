package com.lk.merchantadmin.task;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.util.ListUtils;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

/**
 * 百万 Excel 文件生成单元测试
 *
 * @Author : lk
 * @create 2024/8/31
 */

@SpringBootTest
public class ExcelGenerateTests {

    /**
     * 写入优惠券推送示例 Excel 的数据，自行控制即可
     */
    private final int writeNum = 100000;
    private final Faker faker = new Faker(Locale.CHINA);
    private final String excelPath = "D:\\数据";

    @Test
    public void testExcelGenerate() {
        if (!FileUtil.exist(excelPath)) {
            FileUtil.mkdir(excelPath);
        }
        String fileName = excelPath + "/coupon任务推送Excel.xlsx";
        EasyExcel.write(fileName, ExcelGenerateDemoData.class).sheet("优惠券推送列表").doWrite(data());
    }

    private List<ExcelGenerateDemoData> data() {
        List<ExcelGenerateDemoData> list = ListUtils.newArrayList();
        for (int i = 0; i < writeNum; i++) {
            ExcelGenerateDemoData data = ExcelGenerateDemoData.builder()
                    .mail(faker.number().digits(10) + "@163.com")
                    .phone(faker.phoneNumber().cellPhone())
                    .userId(IdUtil.getSnowflakeNextIdStr())
                    .build();
            list.add(data);
        }
        return list;
    }


    /**
     * 百万 Excel 生成器示例数据模型
     * <p>
     * 作者：马丁
     * 加项目群：早加入就是优势！500人内部项目群，分享的知识总有你需要的 <a href="https://t.zsxq.com/cw7b9" />
     * 开发时间：2024-07-12
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    static class ExcelGenerateDemoData {

        @ColumnWidth(30)
        @ExcelProperty("用户ID")
        private String userId;

        @ColumnWidth(20)
        @ExcelProperty("手机号")
        private String phone;

        @ColumnWidth(30)
        @ExcelProperty("邮箱")
        private String mail;
    }
}
