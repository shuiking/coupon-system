package com.lk.merchantadmin.service.handler.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Getter;

/**
 * Excel 行数统计监听器
 *
 * @Author : lk
 * @create 2024/8/26
 */

public class RowCountListener extends AnalysisEventListener<Object> {

    @Getter
    private int rowCount = 0;

    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {
        rowCount++;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
