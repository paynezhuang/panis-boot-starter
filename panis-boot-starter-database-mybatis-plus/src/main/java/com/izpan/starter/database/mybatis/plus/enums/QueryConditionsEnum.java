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

package com.izpan.starter.database.mybatis.plus.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 查询条件枚举
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.database.mybatis.plus.enums.QueryConditionsEnum
 * @CreateTime 2025/5/30 - 17:23
 */

@Getter
@AllArgsConstructor
public enum QueryConditionsEnum {

    EQUAL("equal", "=", "等于", "?", ParameterTypeEnum.SCALAR),
    NO_EQUAL("noEqual", "!=", "不等于", "?", ParameterTypeEnum.SCALAR),
    LIKE("like", "LIKE", "模糊匹配", "CONCAT('%', ?, '%')", ParameterTypeEnum.SCALAR),
    LEFT_LIKE("leftLike", "LIKE", "左模糊匹配", "CONCAT('%', ?)", ParameterTypeEnum.SCALAR),
    RIGHT_LIKE("rightLike", "LIKE", "右模糊匹配", "CONCAT(?, '%')", ParameterTypeEnum.SCALAR),
    GREATER_THAN("greaterThan", ">", "大于", "?", ParameterTypeEnum.SCALAR),
    GREATER_THAN_OR_EQUAL("greaterThanOrEqual", ">=", "大于等于", "?", ParameterTypeEnum.SCALAR),
    LESS_THAN("lessThan", "<", "小于", "?", ParameterTypeEnum.SCALAR),
    LESS_THAN_OR_EQUAL("lessThanOrEqual", "<=", "小于等于", "?", ParameterTypeEnum.SCALAR),
    IN("in", "IN", "包含", "(?)", ParameterTypeEnum.LIST),
    NOT_IN("notIn", "NOT IN", "不包含", "(?)", ParameterTypeEnum.LIST),
    BETWEEN("between", "BETWEEN", "在区间内", "? AND ?", ParameterTypeEnum.RANGE),
    NO_BETWEEN("noBetween", "NOT BETWEEN", "不在区间内", "? AND ?", ParameterTypeEnum.RANGE),
    IS_NULL("isNull", "IS NULL", "为空", "", ParameterTypeEnum.NONE),
    IS_NOT_NULL("isNotNull", "IS NOT NULL", "不为空", "", ParameterTypeEnum.NONE);

    /**
     * 查询条件编码（前后端交互用）
     */
    private final String code;

    /**
     * SQL操作符（直接用于SQL拼接）
     */
    private final String sqlOperator;

    /**
     * 查询条件描述（前端展示用）
     */
    private final String description;

    /**
     * 参数格式模板（用于构建SQL）
     */
    private final String valuePattern;

    /**
     * 参数类型 单值、列表、范围、无参数
     */
    private final ParameterTypeEnum parameterType;

    /**
     * 根据名称获取 查询条件枚举
     *
     * @param code 名称
     * @return {@link QueryConditionsEnum } 查询条件枚举
     * @author payne.zhuang
     * @CreateTime 2025-06-02 11:59:44
     */
    public static QueryConditionsEnum of(String code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown QueryConditionsEnum code: " + code));
    }
}
