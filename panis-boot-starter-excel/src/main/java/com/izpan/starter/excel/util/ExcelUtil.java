package com.izpan.starter.excel.util;

import com.izpan.starter.common.pool.StringPools;
import com.izpan.starter.excel.builder.ExcelExportBuilder;
import com.izpan.starter.excel.builder.ExcelReadBuilder;
import com.izpan.starter.excel.builder.ExcelReadMapBuilder;
import com.izpan.starter.excel.exception.ExcelException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

@Slf4j
public class ExcelUtil {

    private ExcelUtil() {
    }

    // ========================== 无 JAVA 模型读取 excel 数据 Begin ==========================

    /**
     * 创建Excel读取构建器
     *
     * @return Excel读取构建器
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2025-04-05 13:24:56
     */
    public static ExcelReadMapBuilder read() {
        return new ExcelReadMapBuilder();
    }

    // ========================== 无 JAVA 模型读取 excel 数据 End ==========================

    // ========================== 将 excel 数据同步到 JAVA 模型属性 Begin ==========================

    /**
     * 创建Excel读取构建器
     *
     * @param pojoClass 数据模型类
     * @param <T>       数据类型
     * @return Excel读取构建器
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2025-04-05 13:24:56
     */
    public static <T> ExcelReadBuilder<T> read(Class<T> pojoClass) {
        return new ExcelReadBuilder<>(pojoClass);
    }

    // ========================== 将 excel 数据同步到 JAVA 模型属性 End ==========================

    // ========================== Excel 导出功能 Begin ==========================

    /**
     * 创建Excel导出构建器
     *
     * @param pojoClass 数据模型类
     * @param <T>       数据类型
     * @return Excel导出构建器
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2025-04-05 13:24:56
     */
    public static <T> ExcelExportBuilder<T> export(Class<T> pojoClass) {
        return new ExcelExportBuilder<>(pojoClass);
    }

    // ========================== Excel 导出功能 End ==========================

    /**
     * 检查文件有效性
     *
     * @param file 待检查的文件
     * @author payne.zhuang
     * @CreateTime 2025-04-05 - 14:28:36
     */
    public static void checkFile(File file) {
        if (null == file) {
            throw new ExcelException("Excel file cannot be null");
        }

        if (!file.exists()) {
            throw new ExcelException("Excel file does not exist: " + file.getAbsolutePath());
        }

        if (!file.isFile()) {
            throw new ExcelException("Not a valid file: " + file.getAbsolutePath());
        }

        String filename = file.getName();
        if (!StringUtils.endsWithIgnoreCase(filename, StringPools.XLS_SUFFIX) &&
                !StringUtils.endsWithIgnoreCase(filename, StringPools.XLSX_SUFFIX)) {
            throw new ExcelException("Invalid Excel file format. File '" + filename + "' must have .xls or .xlsx extension");
        }
    }
}