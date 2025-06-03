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

package com.izpan.starter.database.mybatis.plus.builder;

import com.izpan.starter.database.mybatis.plus.enums.ParameterTypeEnum;
import com.izpan.starter.database.mybatis.plus.enums.QueryConditionsEnum;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * 查询条件构建器，用于构建允许的操作符集合
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.database.mybatis.plus.builder.QueryConditionBuilder
 * @CreateTime 2025/5/30 - 21:39
 */
public class QueryConditionBuilder implements Serializable {

    @Serial
    private static final long serialVersionUID = -2686168205840546288L;

    // 存储操作符集合，使用 EnumSet 优化性能
    private final Set<QueryConditionsEnum> conditions = EnumSet.noneOf(QueryConditionsEnum.class);

    // 构建器的代码标识，便于调试和日志记录
    private final String buildCode;
    // 当前 Builder 的参数类型，用于校验
    private final ParameterTypeEnum builderType;

    /**
     * 根据参数类型创建构建器，初始化默认操作符
     * <p>SCALAR: 相等、包含、空值判断</p>
     * <p>LIST: 包含</p>
     * <p>RANGE: 区间</p>
     * <p>NONE: 不允许</p>
     *
     * @param type 参数类型
     * @return QueryConditionBuilder 新实例
     * @author payne.zhuang
     * @CreateTime 2025-05-30 - 22:36:31
     */
    public static QueryConditionBuilder of(String code, ParameterTypeEnum type) {
        if (type == ParameterTypeEnum.NONE) {
            throw new IllegalArgumentException("NONE 类型不允许用于变量操作符构建");
        }
        QueryConditionBuilder builder = new QueryConditionBuilder(code, type);
        // 根据参数类型添加默认操作符
        switch (type) {
            case SCALAR -> builder.conditions.addAll(SCALAR_DEFAULT);
            case LIST -> builder.conditions.addAll(LIST_DEFAULT);
            case RANGE -> builder.conditions.addAll(RANGE_DEFAULT);
            case DATETIME -> builder.conditions.addAll(DATETIME_DEFAULT);
            default -> throw new IllegalArgumentException("不支持的参数类型: " + type);
        }
        return builder;
    }

    // 私有构造函数，记录 Builder 类型
    private QueryConditionBuilder(String code, ParameterTypeEnum type) {
        this.buildCode = code;
        this.builderType = type;
    }

    /**
     * 添加单个操作符
     *
     * @param condition 操作符
     * @return QueryConditionBuilder 构建器自身
     * @author payne.zhuang
     * @CreateTime 2025-05-30 - 22:37:01
     */
    public QueryConditionBuilder add(QueryConditionsEnum condition) {
        validateConditionType(condition);
        conditions.add(condition);
        return this;
    }

    /**
     * 添加多个操作符
     *
     * @param conditions 操作符数组
     * @return QueryConditionBuilder 构建器自身
     * @author payne.zhuang
     * @CreateTime 2025-05-30 - 22:37:12
     */
    public QueryConditionBuilder add(QueryConditionsEnum... conditions) {
        for (QueryConditionsEnum condition : conditions) {
            validateConditionType(condition);
        }
        this.conditions.addAll(Set.of(conditions));
        return this;
    }

    /**
     * 移除指定操作符
     *
     * @param condition 操作符
     * @return QueryConditionBuilder 构建器自身
     * @author payne.zhuang
     * @CreateTime 2025-05-30 - 22:37:22
     */
    public QueryConditionBuilder remove(QueryConditionsEnum condition) {
        conditions.remove(condition);
        return this;
    }

    /**
     * 添加预定义的操作符组
     *
     * @param set 操作符组
     * @return QueryConditionBuilder 构建器自身
     * @throws IllegalArgumentException 如果操作符类型不匹配
     * @author payne.zhuang
     * @CreateTime 2025-05-30 - 22:37:32
     */
    public QueryConditionBuilder addSet(Set<QueryConditionsEnum> set) {
        if (set.isEmpty()) {
            throw new IllegalArgumentException("操作符集合不可为空");
        }
        for (QueryConditionsEnum condition : set) {
            validateConditionType(condition);
        }
        conditions.addAll(set);
        return this;
    }

    /**
     * 构建操作符集合
     *
     * @return Set<QueryConditionsEnum> 不可修改的操作符集合
     * @author payne.zhuang
     * @CreateTime 2025-05-30 - 22:37:42
     */
    public Set<QueryConditionsEnum> build() {
        return Collections.unmodifiableSet(conditions);
    }

    // 校验操作符类型是否匹配 Builder 类型
    private void validateConditionType(QueryConditionsEnum condition) {
        ParameterTypeEnum conditionType = condition.getParameterType();
        if (conditionType != ParameterTypeEnum.NONE && conditionType != builderType) {
            throw new IllegalArgumentException(
                    String.format("[CODE : %s] 操作符 %s 的类型 %s 不匹配 Builder 类型 %s",
                            buildCode, condition, conditionType, builderType)
            );
        }
    }

    // 单值默认操作符组，支持相等、包含、空值判断
    public static final Set<QueryConditionsEnum> SCALAR_DEFAULT = Set.of(
            QueryConditionsEnum.EQUAL, QueryConditionsEnum.NO_EQUAL,
            QueryConditionsEnum.IS_NULL, QueryConditionsEnum.IS_NOT_NULL
    );

    // 单值字符串操作符组，支持模糊匹配
    public static final Set<QueryConditionsEnum> SCALAR_STRING = Set.of(
            QueryConditionsEnum.EQUAL, QueryConditionsEnum.NO_EQUAL,
            QueryConditionsEnum.LIKE, QueryConditionsEnum.LEFT_LIKE, QueryConditionsEnum.RIGHT_LIKE,
            QueryConditionsEnum.IS_NULL, QueryConditionsEnum.IS_NOT_NULL
    );

    // 单值数值操作符组，支持大小比较
    public static final Set<QueryConditionsEnum> SCALAR_NUMBER = Set.of(
            QueryConditionsEnum.EQUAL, QueryConditionsEnum.NO_EQUAL,
            QueryConditionsEnum.GREATER_THAN, QueryConditionsEnum.LESS_THAN,
            QueryConditionsEnum.GREATER_THAN_OR_EQUAL, QueryConditionsEnum.LESS_THAN_OR_EQUAL,
            QueryConditionsEnum.IS_NULL, QueryConditionsEnum.IS_NOT_NULL
    );

    // 日期默认操作符组，支持大小和区间
    public static final Set<QueryConditionsEnum> DATETIME_DEFAULT = Set.of(
            QueryConditionsEnum.GREATER_THAN, QueryConditionsEnum.LESS_THAN,
            QueryConditionsEnum.GREATER_THAN_OR_EQUAL, QueryConditionsEnum.LESS_THAN_OR_EQUAL,
            QueryConditionsEnum.BETWEEN, QueryConditionsEnum.NO_BETWEEN
    );

    // 集合默认操作符组，仅支持包含
    public static final Set<QueryConditionsEnum> LIST_DEFAULT = Set.of(
            QueryConditionsEnum.IN, QueryConditionsEnum.NOT_IN
    );

    // 区间默认操作符组，仅支持区间
    public static final Set<QueryConditionsEnum> RANGE_DEFAULT = Set.of(
            QueryConditionsEnum.BETWEEN, QueryConditionsEnum.NO_BETWEEN
    );
}
