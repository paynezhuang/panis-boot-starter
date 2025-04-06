/*
 * All Rights Reserved: Copyright [2024] [Zhuang Pan (paynezhuang@gmail.com)]
 * Open Source Agreement: Apache License, Version 2.0
 * For educational purposes only, commercial use shall comply with the author's copyright information.
 * The author does not guarantee or assume any responsibility for the risks of using software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.izpan.starter.excel.listener;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.exception.ExcelDataConvertException;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * FastExcel 读取监听器
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.excel.listener.DataListener
 * @CreateTime 2024/12/19 - 22:30
 */

@Getter
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class DataListener<T> extends AnalysisEventListener<T> {

    /**
     * 缓存的数据列表
     */
    private final List<T> rows = Lists.newArrayList();

    /**
     * 读取excel数据前操作(只有不读取表头数据时才会触发此方法)
     *
     * @param headMap 第一行数据
     * @param context 文件上下文
     * @author payne.zhuang
     * @CreateTime 2024-12-19 22:32:07
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("======================================================");
        log.info("[Excel 第一行数据]{}", new Gson().toJson(headMap));
        log.info("======================================================");
    }

    /**
     * 读取excel数据操作
     *
     * @param object  对象
     * @param context 文件上下文
     * @author payne.zhuang
     * @CreateTime 2024-12-19 22:32:10
     */
    @Override
    public void invoke(T object, AnalysisContext context) {
        rows.add(object);
        log.info("[Excel [{}]行读取成功]{}", rows.size(), object);
    }

    /**
     * 读取完excel数据后的操作
     *
     * @param context 文件上下文
     * @author payne.zhuang
     * @CreateTime 2024-12-19 22:32:13
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("[Excel读取完成]读取[{}]条数据", rows.size());
    }

    /**
     * 在读取excel异常 获取其他异常下会调用本接口。抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行。
     *
     * @param exception 异常信息
     * @param context   文件上下文
     * @author payne.zhuang
     * @CreateTime 2022-09-07 09:24
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.info("[Excel解析失败]但是继续解析下一行:{}", exception.getMessage());
        if (exception instanceof ExcelDataConvertException ex) {
            log.error("[Excel解析异常]它第{}行，第{}列，数据为:{}", ex.getRowIndex(), ex.getColumnIndex(), ex.getCellData());
        }
    }
}
