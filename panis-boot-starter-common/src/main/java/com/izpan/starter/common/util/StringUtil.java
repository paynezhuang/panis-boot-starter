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

package com.izpan.starter.common.util;

import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;

/**
 * 字符串工具类
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.common.util.StringUtil
 * @CreateTime 2024/9/2 - 16:33
 */

@Slf4j
public class StringUtil {

    private StringUtil() {

    }

    /**
     * 下划线转驼峰（小骆驼）
     *
     * @param str 字符串
     * @return {@link String } 驼峰字符串
     * @author payne.zhuang
     * @CreateTime 2024-07-21 - 19:13:35
     */
    public static String toLowerCamel(String str) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
    }

    /**
     * 下划线转驼峰（大骆驼）
     *
     * @param str 字符串
     * @return {@link String } 驼峰字符串
     * @author payne.zhuang
     * @CreateTime 2024-08-28 - 23:05:20
     */
    public static String toUpperCamel(String str) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, str);
    }

    public static void main(String[] args) {
        log.info(toLowerCamel("typescript_type"));
        log.info(toUpperCamel("typescript_type"));
    }

    /**
     * 移除 is_ 前缀，eg: is_query -> query
     *
     * @param str str
     * @return {@link String }
     * @author payne.zhuang
     * @CreateTime 2024-09-04 - 10:31:49
     */
    public static String removeIsPrefix(String str) {
        if (str.startsWith("is_")) {
            return str.substring(3);
        }
        return str;
    }

    /**
     * 将 MySQL 数据类型转换为 Java 类型
     *
     * @param mysqlType MySQL 数据类型
     * @return {@link String } Java 类型
     * @author payne.zhuang
     * @CreateTime 2024-09-02 - 16:44:28
     */
    public static String convertMySQLTypeToJavaType(String mysqlType) {
        return switch (mysqlType.toLowerCase()) {
            case "varchar", "char", "text", "mediumtext", "longtext" -> "String";
            case "int", "integer", "smallint", "tinyint", "mediumint" -> "Integer";
            case "bigint" -> "Long";
            case "float" -> "Float";
            case "double", "real" -> "Double";
            case "decimal", "numeric" -> "BigDecimal";
            case "bit" -> "Boolean";
            case "date" -> "LocalDate";
            case "time", "datetime", "timestamp" -> "LocalDateTime";
            case "blob", "mediumblob", "longblob" -> "byte[]";
            default -> "Object";
        };
    }

    /**
     * 将 Java 类型转换为 TypeScript 类型
     *
     * @param javaType java类型
     * @return {@link String } TypeScript 类型
     * @author payne.zhuang
     * @CreateTime 2024-08-28 - 21:52:47
     */
    public static String convertJavaTypeToTypeScriptType(String javaType) {
        return switch (javaType) {
            case "String", "LocalDate", "LocalDateTime" -> "string";
            case "int", "Integer", "long", "Long", "float", "Float", "double", "Double", "BigDecimal" -> "number";
            case "boolean", "Boolean" -> "boolean";
            case "List", "Set" -> "Array<any>";
            default -> "any";
        };
    }

}
