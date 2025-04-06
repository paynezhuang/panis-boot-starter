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

package com.izpan.starter.excel.builder;

import cn.idev.excel.FastExcelFactory;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.read.builder.ExcelReaderSheetBuilder;
import com.izpan.starter.excel.exception.ExcelException;
import com.izpan.starter.excel.listener.DataListener;
import com.izpan.starter.excel.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Excel无实体类读取构建器
 * <p>
 * 支持两种读取模式：
 * 1. 同步模式：调用sync()方法启用，fromFile/fromInputStream方法直接返回读取结果
 * 2. 异步模式：默认模式，通过监听器处理数据，fromFile/fromInputStream方法返回空列表<p>
 * <p>
 * 异步模式通常配合自定义监听器使用，适合处理大数据量Excel或需要特殊处理逻辑的情况。<p>
 * 如需在异步模式下获取所有数据，可自定义监听器收集数据。<p>
 * <p>
 * 该构建器不需要预定义的实体类，直接将Excel行数据读取为Map<Integer, String>格式。<p>
 * 其中Map的key为列索引（从0开始），value为单元格的字符串值。<p>
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.excel.builder.ExcelReadMapBuilder
 * @CreateTime 2025/4/5 - 14:30
 */
@Slf4j
public class ExcelReadMapBuilder {
    /**
     * 需要读取的sheet索引，默认0
     */
    private Integer sheetNo = 0;

    /**
     * 表头行号，默认1
     * 表头行号指Excel中表头所在行的位置，从1开始计数
     */
    private Integer headRowNum = 1;

    /**
     * 数据解析监听器
     * 在异步模式下，通过监听器处理解析的数据
     */
    private AnalysisEventListener<Map<Integer, String>> listener;

    /**
     * 是否同步读取(建议数据少时使用)
     * true: 同步模式，读取完成后返回完整数据列表
     * false: 异步模式，通过监听器处理数据
     */
    private boolean sync = false;

    /**
     * 构造函数
     * 初始化默认监听器
     */
    public ExcelReadMapBuilder() {
        this.listener = new DataListener<>();
    }

    /**
     * 设置sheet索引
     *
     * @param sheetNo sheet索引
     * @return 当前构建器
     */
    public ExcelReadMapBuilder sheetNo(Integer sheetNo) {
        this.sheetNo = null == sheetNo ? 0 : sheetNo;
        return this;
    }

    /**
     * 设置表头行号
     *
     * @param headRowNum 表头行号
     * @return 当前构建器
     */
    public ExcelReadMapBuilder headRowNum(Integer headRowNum) {
        this.headRowNum = null == headRowNum ? 1 : headRowNum;
        return this;
    }

    /**
     * 设置自定义监听器
     *
     * @param listener 自定义监听器
     * @return 当前构建器
     */
    public ExcelReadMapBuilder listener(AnalysisEventListener<Map<Integer, String>> listener) {
        this.listener = null == listener ? new DataListener<>() : listener;
        return this;
    }

    /**
     * 设置为同步读取模式
     *
     * @return 当前构建器
     */
    public ExcelReadMapBuilder sync() {
        this.sync = true;
        return this;
    }

    /**
     * 从文件路径读取Excel
     *
     * @param filePath 文件路径
     * @return 读取结果列表，异步模式下返回空列表
     * @author payne.zhuang
     * @CreateTime 2025-04-05 - 14:28:36
     */
    public List<Map<Integer, String>> fromFile(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            throw new ExcelException("Excel file path cannot be empty");
        }

        File excel = new File(filePath);
        return fromFile(excel);
    }

    /**
     * 从File对象读取Excel
     *
     * @param file Excel文件
     * @return 读取结果列表，异步模式下返回空列表
     * @author payne.zhuang
     * @CreateTime 2025-04-05 - 14:28:36
     */
    public List<Map<Integer, String>> fromFile(File file) {
        ExcelUtil.checkFile(file);

        try {
            ExcelReaderSheetBuilder reader = FastExcelFactory.read(file, listener)
                    .sheet(sheetNo)
                    .headRowNumber(headRowNum);
            if (sync) {
                return reader.doReadSync();
            } else {
                reader.doRead();
                return Collections.emptyList();
            }
        } catch (Exception e) {
            throw handleReadException(e, "Failed to read Excel from file");
        }
    }

    /**
     * 从输入流读取Excel
     *
     * @param inputStream Excel输入流
     * @return 读取结果列表，异步模式下返回空列表
     * @author payne.zhuang
     * @CreateTime 2025-04-05 - 14:28:36
     */
    public List<Map<Integer, String>> fromInputStream(InputStream inputStream) {
        if (null == inputStream) {
            throw new ExcelException("Excel input stream cannot be null");
        }

        try {
            ExcelReaderSheetBuilder reader = FastExcelFactory.read(inputStream, listener)
                    .sheet(sheetNo)
                    .headRowNumber(headRowNum);
            if (sync) {
                return reader.doReadSync();
            } else {
                reader.doRead();
                return Collections.emptyList();
            }
        } catch (Exception e) {
            throw handleReadException(e, "Failed to read Excel from input stream");
        }
    }

    /**
     * 处理读取异常
     *
     * @param e       异常
     * @param message 错误消息
     * @return 包装后的Excel异常
     */
    private static ExcelException handleReadException(Exception e, String message) {
        String detailedMessage = message + ": " + e.getMessage();
        log.error(detailedMessage, e);
        return new ExcelException(detailedMessage, e);
    }
}