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

package com.izpan.starter.database.mybatis.plus.interceptor;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.izpan.starter.database.mybatis.plus.domain.DataScope;
import com.izpan.starter.database.mybatis.plus.enums.DataScopeTypeEnum;
import com.izpan.starter.database.mybatis.plus.handler.IDataScopeHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SetOperationList;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * 数据权限拦截器
 * 用于拦截SQL并添加数据权限条件
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.database.mybatis.plus.interceptor.DataScopeInterceptor
 * @CreateTime 2025/5/12 - 11:26
 */
@Slf4j
@RequiredArgsConstructor
public class DataScopeInterceptor implements InnerInterceptor {

    // 用户ID列名，用于权限过滤
    private static final String USER_ID_COLUMN = "create_user_id";

    // 数据权限处理器，负责获取权限信息和缓存 SQL
    private final IDataScopeHandler dataScopeHandler;

    /**
     * 拦截 SQL 查询，添加数据权限条件
     *
     * @param executor      MyBatis 执行器
     * @param ms            MappedStatement，包含 SQL 信息
     * @param parameter     查询参数
     * @param rowBounds     分页参数
     * @param resultHandler 结果处理器
     * @param boundSql      绑定 SQL
     * @author payne.zhuang
     * @CreateTime 2025-05-29 - 12:17:24
     */
    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        log.debug("[DataScope] msId={} - SQL 拦截器开始执行", ms.getId());
        // 检查是否忽略数据权限
        boolean strategy = InterceptorIgnoreHelper.willIgnoreDataPermission(ms.getId());
        if (strategy) {
            log.debug("[DataScope] msId={} - 忽略数据权限策略", ms.getId());
            return;
        }

        String permissionCode = null;
        try {
            // 仅处理 SELECT 语句
            if (ms.getSqlCommandType() != SqlCommandType.SELECT) {
                log.debug("[DataScope] msId={} - 非 SELECT 语句, 命令类型={}, 跳过处理", ms.getId(), ms.getSqlCommandType());
                return;
            }

            // 获取权限标识
            permissionCode = dataScopeHandler.getPermissionCode();
            if (permissionCode == null || permissionCode.isEmpty()) {
                log.debug("[DataScope] msId={} - 权限标识为空, 跳过处理", ms.getId());
                return;
            }

            // 获取数据权限
            DataScope dataScope = dataScopeHandler.getDataScope(ms, permissionCode);
            if (null == dataScope) {
                log.debug("[DataScope] 权限码={} msId={} - 数据权限未配置, 跳过处理", permissionCode, ms.getId());
                return;
            }

            Long userId = dataScope.getCurrentUserId();
            if (DataScopeTypeEnum.ALL.equals(dataScope.getScopeType())) {
                log.debug("[DataScope] 用户ID={} 权限码={} msId={} - 数据权限为 ALL, 无需处理",
                        userId, permissionCode, ms.getId());
                return;
            }

            // 构建带权限条件的 SQL（移除SQL缓存，因为不同查询参数会导致缓存错误）
            PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
            String originalSql = mpBs.sql();
            String processedSql = buildDataScopeSql(originalSql, dataScope);
            mpBs.sql(processedSql);

            // 记录 SQL 处理日志
            log.info("[DataScope] 用户ID={} 权限码={}, 权限类型={}, msId={} - SQL 处理完成",
                    userId, permissionCode, dataScope.getScopeType(), ms.getId());
        } catch (Exception e) {
            log.error("[DataScope] 权限码={} msId={} - SQL 处理异常, sql={}, 错误={}",
                    permissionCode, ms.getId(), boundSql.getSql(), e.getMessage(), e);
        }
    }

    /**
     * 构建带权限条件的 SQL
     *
     * @param originalSql 原始 SQL
     * @param dataScope   数据权限信息
     * @return {@link String} 处理后的 SQL，若无需处理则返回原 SQL
     */
    private String buildDataScopeSql(String originalSql, DataScope dataScope) {
        try {
            Select select = (Select) CCJSqlParserUtil.parse(originalSql);
            processSelect(select, dataScope);
            return select.toString();
        } catch (JSQLParserException e) {
            log.error("[DataScope] 用户ID={} 权限码={} - SQL 解析失败, sql={}, 错误={}",
                    dataScope.getCurrentUserId(), dataScope.getPermissionCode(), originalSql, e.getMessage(), e);
            // 解析失败返回原 SQL
            return originalSql;
        }
    }

    /**
     * 处理 SELECT 语句，注入权限条件
     *
     * @param select    解析后的 SELECT 语句
     * @param dataScope 数据权限信息
     * @author payne.zhuang
     * @CreateTime 2025-05-29 - 13:48:38
     */
    private void processSelect(Select select, DataScope dataScope) {
        if (select.getPlainSelect() != null) {
            setWhere(select.getPlainSelect(), dataScope);
        } else if (select.getSetOperationList() != null) {
            SetOperationList setOperationList = select.getSetOperationList();
            setOperationList.getSelects().forEach(s -> {
                if (s instanceof PlainSelect plainSelect) {
                    setWhere(plainSelect, dataScope);
                } else {
                    log.warn("[DataScope] 用户ID={} 权限码={} 权限类型={} - 复杂查询包含非 PlainSelect 类型, 跳过处理",
                            dataScope.getCurrentUserId(), dataScope.getPermissionCode(), dataScope.getScopeType());
                }
            });
        }
    }

    /**
     * 为 PlainSelect 注入 WHERE 条件
     *
     * @param plainSelect 简单 SELECT 语句
     * @param dataScope   数据权限信息
     * @author payne.zhuang
     * @CreateTime 2025-05-29 - 12:17:37
     */
    private void setWhere(PlainSelect plainSelect, DataScope dataScope) {
        Expression whereExpression = plainSelect.getWhere();
        Expression dataScopeExpression = buildScopeExpression(dataScope);
        if (dataScopeExpression != null) {
            if (whereExpression != null) {
                plainSelect.setWhere(new AndExpression(whereExpression, dataScopeExpression));
            } else {
                plainSelect.setWhere(dataScopeExpression);
            }
            log.debug("[DataScope] 用户ID={} 权限码={} scopeType={} - WHERE 条件注入完成", dataScope.getCurrentUserId(), dataScope.getPermissionCode(), dataScope.getScopeType());
        }
    }

    /**
     * 构建数据权限的 SQL 表达式
     *
     * @param dataScope 数据权限信息
     * @return {@link Expression} 权限表达式，若无权限条件则返回 null
     * @author payne.zhuang
     * @CreateTime 2025-05-29 - 12:17:44
     */
    private Expression buildScopeExpression(DataScope dataScope) {
        DataScopeTypeEnum scope = dataScope.getScopeType();

        Long userId = dataScope.getCurrentUserId();
        // 未知类型：返回恒假表达式 1 = 0，避免访问表字段并且不返回数据
        if (scope == DataScopeTypeEnum.UN_KNOWN) {
            log.warn("[DataScope] 用户ID={} 权限码={} - 未知权限类型, 权限类型={}, 返回恒假条件", userId, dataScope.getPermissionCode(), scope);
            return new EqualsTo(new LongValue(1), new LongValue(0));
        }

        // 处理 SELF 类型（当前用户）
        if (scope == DataScopeTypeEnum.SELF) {
            return new EqualsTo(new Column(USER_ID_COLUMN), new LongValue(userId));
        }

        // 处理 CUSTOM 类型（自定义规则）
        if (scope == DataScopeTypeEnum.CUSTOM && StringUtils.hasLength(dataScope.getCustomRules())) {
            try {
                return CCJSqlParserUtil.parseCondExpression(dataScope.getCustomRules());
            } catch (JSQLParserException e) {
                log.error("[DataScope] 用户ID={} 权限码={} 权限类型={} - 自定义规则解析失败, customRules={}, 错误={}",
                        userId, dataScope.getPermissionCode(), dataScope.getScopeType(),
                        dataScope.getCustomRules(), e.getMessage(), e);
                return null;
            }
        }

        // 处理用户 ID 集合
        Set<Long> scopeUserIds = dataScope.getScopeUserIds();
        if (!CollectionUtils.isEmpty(scopeUserIds)) {
            Column column = new Column(USER_ID_COLUMN);
            List<Expression> expressions = scopeUserIds.stream().map(id -> (Expression) new LongValue(id)).toList();
            return new InExpression(column, new ParenthesedExpressionList<>(expressions));
        }

        return null;
    }
}
