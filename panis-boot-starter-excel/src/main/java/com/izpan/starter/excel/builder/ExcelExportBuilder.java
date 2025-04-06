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
import cn.idev.excel.write.builder.ExcelWriterBuilder;
import cn.idev.excel.write.handler.WriteHandler;
import com.izpan.starter.common.pool.StringPools;
import com.izpan.starter.excel.exception.ExcelException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Excel导出构建器
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.excel.builder.ExcelExportBuilder
 * @CreateTime 2025/4/5 - 13:16
 */

@Slf4j
public class ExcelExportBuilder<T> {
    /**
     * 导出文件名
     */
    private String fileName;
    /**
     * 导出sheet名称
     */
    private String sheetName = "Sheet1";
    /**
     * 导出数据
     */
    private List<T> data;
    /**
     * 导出数据模型类
     */
    private final Class<T> pojoClass;
    /**
     * 自定义写处理器
     */
    private WriteHandler writeHandler;

    public ExcelExportBuilder(Class<T> pojoClass) {
        this.pojoClass = pojoClass;
    }

    public ExcelExportBuilder<T> fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ExcelExportBuilder<T> sheetName(String sheetName) {
        this.sheetName = StringUtils.isBlank(sheetName) ? "Sheet1" : sheetName;
        return this;
    }

    public ExcelExportBuilder<T> data(List<T> data) {
        this.data = data;
        return this;
    }

    public ExcelExportBuilder<T> writeHandler(WriteHandler writeHandler) {
        this.writeHandler = writeHandler;
        return this;
    }

    /**
     * 导出Excel到文件
     *
     * @param filePath 导出文件路径
     * @author payne.zhuang
     * @CreateTime 2025-04-05 - 14:28:36
     */
    public void toFile(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            throw new ExcelException("Export file path cannot be empty");
        }

        // 检查文件后缀
        if (!StringUtils.endsWithIgnoreCase(filePath, StringPools.XLS_SUFFIX) &&
                !StringUtils.endsWithIgnoreCase(filePath, StringPools.XLSX_SUFFIX)) {
            throw new ExcelException("Export file must be Excel format with .xls or .xlsx extension");
        }

        try {
            // 创建导出对象
            ExcelWriterBuilder writer = createWriter(filePath, pojoClass, writeHandler);
            writer.sheet(sheetName).doWrite(data);
        } catch (Exception e) {
            handleExportException(e, "Failed to export Excel to file");
        }
    }

    /**
     * 导出Excel到输出流
     *
     * @param outputStream 输出流
     * @author payne.zhuang
     * @CreateTime 2025-04-05 - 14:28:52
     */
    public void toOutputStream(OutputStream outputStream) {
        if (null == outputStream) {
            throw new ExcelException("Output stream cannot be null");
        }

        try {
            // 创建导出对象
            ExcelWriterBuilder writer = createWriter(outputStream, pojoClass, writeHandler);
            writer.sheet(sheetName).doWrite(data);
        } catch (Exception e) {
            handleExportException(e, "Failed to export Excel to output stream");
        }
    }

    /**
     * 导出Excel到HttpServletResponse
     *
     * @param response HttpServletResponse对象
     * @author payne.zhuang
     * @CreateTime 2025-04-05 - 14:29:07
     */
    public void toResponse(HttpServletResponse response) {
        if (null == response) {
            throw new ExcelException("HttpServletResponse cannot be null");
        }

        // 处理文件名
        String validFileName = getValidFileName(fileName);

        try {
            // 设置响应头
            setResponseHeaders(response, validFileName);

            // 导出Excel
            ExcelWriterBuilder writer = createWriter(response.getOutputStream(), pojoClass, writeHandler);
            writer.sheet(sheetName).doWrite(data);
        } catch (Exception e) {
            handleExportException(e, "Failed to export Excel to response stream");
        }
    }

    /**
     * 获取有效的文件名
     *
     * @param fileName 文件名
     * @return 有效的文件名
     */
    private static String getValidFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            fileName = "export_%d".formatted(System.currentTimeMillis());
        }

        // 确保文件名有正确的后缀
        if (!StringUtils.endsWithIgnoreCase(fileName, StringPools.XLS_SUFFIX) &&
                !StringUtils.endsWithIgnoreCase(fileName, StringPools.XLSX_SUFFIX)) {
            fileName += StringPools.XLSX_SUFFIX;
        }

        return fileName;
    }

    /**
     * 创建Excel写入器
     *
     * @param source       输出源（文件路径或输出流）
     * @param pojoClass    数据模型类
     * @param writeHandler 自定义写处理器
     * @param <T>          数据类型
     * @return Excel写入器构建器
     */
    private static <T> ExcelWriterBuilder createWriter(Object source, Class<T> pojoClass, WriteHandler writeHandler) {
        ExcelWriterBuilder writer;

        switch (source) {
            case String string -> writer = FastExcelFactory.write(string, pojoClass);
            case OutputStream outputStream -> writer = FastExcelFactory.write(outputStream, pojoClass);
            default ->
                    throw new ExcelException("Unsupported output source type: %s".formatted(source.getClass().getName()));
        }

        if (null != writeHandler) {
            writer.registerWriteHandler(writeHandler);
        }

        return writer;
    }

    /**
     * 设置响应头
     *
     * @param response 响应对象
     * @param fileName 文件名
     */
    private static void setResponseHeaders(HttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 处理文件名中文编码问题
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName);
    }

    /**
     * 处理导出异常
     *
     * @param e       异常
     * @param message 错误消息
     */
    private static void handleExportException(Exception e, String message) {
        log.error("{}: {}", message, e.getMessage(), e);
        throw new ExcelException("%s: %s".formatted(message, e.getMessage()), e);
    }
}
