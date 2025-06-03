/*
 * All Rights Reserved: Copyright [2025] [Zhuang Pan (paynezhuang@gmail.com)]
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

package com.izpan.starter.database.mybatis.plus.resolver;

import com.izpan.starter.common.pool.StringPools;
import com.izpan.starter.database.mybatis.plus.enums.QueryConditionsEnum;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据权限变量解析器（Starter层通用实现）
 * <p>
 * 核心功能：
 * 1. 解析变量占位符：支持 #{variableName} 格式的变量替换
 * 2. 值类型转换：将各种类型的变量值转换为SQL可用的字符串格式
 * 3. SQL格式化：根据操作符的valuePattern进行正确的SQL格式化
 * 4. 统一处理：同时支持固定值和变量值的处理
 * 5. 错误降级：解析失败时提供安全的降级策略
 * </p>
 * <p>
 * 支持的操作符类型：
 * - 标量操作符：EQUAL, NOT_EQUAL, GT, LT, GE, LE, LIKE, LEFT_LIKE, RIGHT_LIKE
 * - 集合操作符：IN, NOT_IN
 * - 范围操作符：BETWEEN, NO_BETWEEN
 * - 空值操作符：IS_NULL, IS_NOT_NULL
 * </p>
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.database.mybatis.plus.resolver.DataScopeVariableResolver
 * @CreateTime 2025/6/2 - 16:20
 */
@Slf4j
@UtilityClass
public class DataScopeVariableResolver {

    /**
     * 变量占位符正则表达式，匹配 #{variableName} 格式
     */
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("#\\{([^}]+)}");

    /**
     * 标准日期时间格式化器：yyyy-MM-dd HH:mm:ss
     */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 解析并替换变量占位符，并根据操作符进行格式化
     * <p>
     * 处理流程：
     * 1. 检查是否包含变量占位符
     * 2. 如果不包含，直接按固定值格式化
     * 3. 如果包含，进行变量替换后再格式化
     * 4. 异常时降级为原值格式化
     * </p>
     *
     * @param ignore        用户ID（保留参数，便于扩展）
     * @param value         包含变量占位符或固定值的字符串
     * @param variableValue 变量的实际值（来自上下文）
     * @param operator      查询条件操作符，用于SQL格式化
     * @return 格式化后的SQL值
     * @author payne.zhuang
     * @CreateTime 2025-06-02 - 16:21
     */
    @SuppressWarnings("unused")
    public static String resolveVariables(Long ignore, String value, Object variableValue, QueryConditionsEnum operator) {
        // 固定值直接格式化
        if (value == null || !value.contains("#{")) {
            return formatValueWithPattern(value, operator);
        }

        try {
            // 替换变量占位符
            Matcher matcher = VARIABLE_PATTERN.matcher(value);
            StringBuilder result = new StringBuilder();
            while (matcher.find()) {
                String replacement = variableValue != null ? formatValue(variableValue) : StringPools.NULL;
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(result);
            String resolvedValue = result.toString();

            // 根据操作符格式化
            resolvedValue = formatValueWithPattern(resolvedValue, operator);

            log.debug("[DataScope] 变量替换成功: 原值={}, 替换后={}, 操作符={}", value, resolvedValue, operator.getCode());
            return resolvedValue;

        } catch (Exception e) {
            log.warn("[DataScope] 变量替换失败: value={}, operator={}, error={}", value, operator.getCode(), e.getMessage());
            return formatValueWithPattern(value, operator); // 降级返回原值
        }
    }

    /**
     * 格式化变量值为SQL可用的字符串格式
     * <p>
     * 支持的数据类型：
     * - null: 转换为 "NULL"
     * - String: 用单引号包围
     * - LocalDateTime: 格式化为 'yyyy-MM-dd HH:mm:ss' 格式
     * - LocalDateTime[]: 时间数组，格式化为逗号分隔的时间字符串
     * - Object[]: 其他数组类型，递归格式化为逗号分隔格式
     * - Iterable: 转换为SQL IN格式的括号形式 (value1, value2)
     * - 其他类型: 转换为字符串表示
     * </p>
     *
     * @param value 待格式化的值
     * @return 格式化后的字符串
     * @author payne.zhuang
     * @CreateTime 2025-06-02 - 16:22
     */
    private static String formatValue(Object value) {
        return switch (value) {
            // 处理null值，直接返回NULL
            case null -> StringPools.NULL;
            // 处理String类型，加单引号
            case String ignore -> StringPools.SINGLE_QUOTE + value + StringPools.SINGLE_QUOTE;
            // 处理LocalDateTime类型，格式化为标准时间格式并加单引号
            case LocalDateTime dateTime ->
                    StringPools.SINGLE_QUOTE + dateTime.format(DATETIME_FORMATTER) + StringPools.SINGLE_QUOTE;
            // 处理LocalDateTime数组类型，格式化为逗号分隔的时间字符串
            case LocalDateTime[] dateTimeArray -> {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < dateTimeArray.length; i++) {
                    if (i > 0) sb.append(StringPools.COMMA);
                    // 格式化每个LocalDateTime并加单引号
                    sb.append(StringPools.SINGLE_QUOTE)
                            .append(dateTimeArray[i].format(DATETIME_FORMATTER))
                            .append(StringPools.SINGLE_QUOTE);
                }
                yield sb.toString();
            }
            // 处理其他数组类型，递归格式化为逗号分隔格式
            case Object[] array -> {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < array.length; i++) {
                    if (i > 0) sb.append(StringPools.COMMA);
                    // 对数组元素进行递归格式化
                    sb.append(formatValue(array[i]));
                }
                yield sb.toString();
            }
            // 处理Iterable类型，转换为SQL IN格式的括号形式 (value1, value2)
            case Iterable<?> collection -> collection.toString()
                    .replace(StringPools.LEFT_SQ_BRACKET, StringPools.LEFT_BRACKET)
                    .replace(StringPools.RIGHT_SQ_BRACKET, StringPools.RIGHT_BRACKET);
            default -> String.valueOf(value);
        };
    }

    /**
     * 根据操作符的valuePattern格式化值
     * <p>
     * 核心逻辑：
     * 1. 获取操作符的valuePattern（如LIKE的"CONCAT('%', ?, '%')"）
     * 2. 根据操作符类型进行特殊处理
     * 3. 对于标准操作符，直接替换valuePattern中的"?"占位符
     * 4. 对于特殊操作符（IN、BETWEEN等），进行特定的格式转换
     * </p>
     *
     * @param value    已解析或固定值（如 "'admin'", "1,2", "100"）
     * @param operator 查询条件操作符（如EQUAL, LIKE, IN）
     * @return 格式化后的SQL值（如 "'admin'", "CONCAT('%', 'admin', '%')", "(1,2)"）
     * @author payne.zhuang
     * @CreateTime 2025-06-02 - 16:23
     */
    private static String formatValueWithPattern(String value, QueryConditionsEnum operator) {
        // 处理空值或NULL，直接返回
        if (value == null || value.equals(StringPools.NULL)) {
            return value;
        }

        // 获取操作符的valuePattern，如EQUAL为"?"，LIKE为"CONCAT('%', ?, '%')"
        String valuePattern = operator.getValuePattern();
        // 如果valuePattern为空，直接返回value（通常已由formatValue加引号）
        if (valuePattern.isEmpty()) {
            return value;
        }

        switch (operator) {
            // 处理IN和NOT_IN操作符
            // 逻辑：确保值被括号包裹，用于SQL IN语句
            // 输入示例：value="1,2" 或 "(1,2)"
            // 输出示例："(1,2)"
            case IN, NOT_IN:
                return value.startsWith(StringPools.LEFT_BRACKET) && value.endsWith(StringPools.RIGHT_BRACKET)
                        ? value : StringPools.LEFT_BRACKET + value + StringPools.RIGHT_BRACKET;

            // 处理BETWEEN和NO_BETWEEN操作符
            // 逻辑：将逗号分隔的值转为"start AND end"格式
            // 输入示例：value="1,2" 或 "2023-01-01,2023-12-31"
            // 输出示例："1 AND 2" 或 "'2023-01-01' AND '2023-12-31'"
            case BETWEEN, NO_BETWEEN:
                if (value.contains(StringPools.AND)) {
                    return value; // 已格式化为 "start AND end"，直接返回
                }
                String[] parts = value.split(StringPools.COMMA + "\\s*");
                if (parts.length >= 2) {
                    return parts[0].trim() + StringPools.AND + parts[1].trim();
                }
                // 降级到valuePattern
                return valuePattern.replace(StringPools.AND_PATTERN, value);

            // 处理IS_NULL和IS_NOT_NULL操作符
            // 逻辑：这些操作符不需要值，SQL直接使用"field IS NULL"
            // 输出：空字符串
            case IS_NULL, IS_NOT_NULL:
                return StringPools.EMPTY;

            // 处理其他标准操作符（EQUAL, NOT_EQUAL, GT, LT, GE, LE, LIKE, LEFT_LIKE, RIGHT_LIKE）
            // 逻辑：直接替换valuePattern中的"?"占位符
            // 输入示例：value="'admin'"，operator=LIKE，valuePattern="CONCAT('%', ?, '%')"
            // 输出示例："CONCAT('%', 'admin', '%')"
            default:
                return valuePattern.replace(StringPools.QUESTION_MARK, value);
        }
    }
} 