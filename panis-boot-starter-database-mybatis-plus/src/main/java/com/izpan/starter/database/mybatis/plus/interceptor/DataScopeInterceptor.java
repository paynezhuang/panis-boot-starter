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
        // 检查是否忽略数据权限
        boolean strategy = InterceptorIgnoreHelper.willIgnoreDataPermission(ms.getId());
        if (strategy) {
            log.debug("[DataScope] 忽略数据权限，跳过处理, msId={}", ms.getId());
            return;
        }

        try {
            // 仅处理 SELECT 语句
            if (ms.getSqlCommandType() != SqlCommandType.SELECT) {
                log.debug("[DataScope] 非 SELECT 语句，跳过处理, msId={}", ms.getId());
                return;
            }

            // 获取权限标识
            String permissionCode = dataScopeHandler.getPermissionCode();
            if (permissionCode == null || permissionCode.isEmpty()) {
                log.debug("[DataScope] 权限标识为空，跳过处理, msId={}", ms.getId());
                return;
            }

            // 获取数据权限
            DataScope dataScope = dataScopeHandler.getDataScope(permissionCode);

            if (null == dataScope) {
                log.warn("[DataScope] 数据权限未配置，跳过处理, msId={}", ms.getId());
                return;
            }

            if (DataScopeTypeEnum.ALL.getType().equals(dataScope.getScopeType())) {
                log.debug("[DataScope] 数据权限为 ALL，无需处理, msId={}", ms.getId());
                return;
            }

            // 检查缓存
            String cacheKey = dataScopeHandler.buildCacheKey(dataScope.getCurrentUserId(), permissionCode, ms.getId(), dataScope.getScopeType());
            String processedSql = dataScopeHandler.getCachedSql(cacheKey);
            if (null != processedSql) {
                log.debug("[DataScope] 使用缓存 SQL, msId={}", ms.getId());
                PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
                mpBs.sql(processedSql);
                return;
            }

            // 构建带权限条件的 SQL
            PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
            String originalSql = mpBs.sql();
            processedSql = buildDataScopeSql(originalSql, dataScope);
            mpBs.sql(processedSql);

            // 缓存处理后的 SQL
            if (!processedSql.equals(originalSql)) {
                dataScopeHandler.cacheSql(cacheKey, processedSql);
                log.debug("[DataScope] SQL 已处理并缓存, msId={}, cacheKey={}", ms.getId(), cacheKey);
            }
            log.debug("[DataScope] SQL 处理完成, msId={}, originalSql={}, processedSql={}", ms.getId(), originalSql, processedSql);
        } catch (Exception e) {
            log.error("[DataScope] 处理 SQL 出错, msId={}, sql={}, error={}", ms.getId(), boundSql.getSql(), e.getMessage(), e);
        }
    }

    /**
     * 构建带权限条件的 SQL
     *
     * @param originalSql 原始 SQL
     * @param dataScope   数据权限信息
     * @return 处理后的 SQL，若无需处理则返回原 SQL
     */
    private String buildDataScopeSql(String originalSql, DataScope dataScope) {
        try {
            Select select = (Select) CCJSqlParserUtil.parse(originalSql);
            processSelect(select, dataScope);
            return select.toString();
        } catch (JSQLParserException e) {
            log.error("[DataScope] 解析 SQL 失败, sql={}, error={}", originalSql, e.getMessage(), e);
            return originalSql; // 解析失败返回原 SQL
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
                    log.warn("[DataScope] 复杂查询中包含非 PlainSelect 类型，跳过处理, scopeType={}", dataScope.getScopeType());
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
            log.debug("[DataScope] WHERE 条件注入完成, scopeType={}, userId={}", dataScope.getScopeType(), dataScope.getCurrentUserId());
        }
    }

    /**
     * 构建数据权限的 SQL 表达式
     *
     * @param dataScope 数据权限信息
     * @return 权限表达式，若无权限条件则返回 null
     * @author payne.zhuang
     * @CreateTime 2025-05-29 - 12:17:44
     */
    private Expression buildScopeExpression(DataScope dataScope) {
        String scopeType = dataScope.getScopeType();

        // 处理 SELF 类型（当前用户）
        if (DataScopeTypeEnum.SELF.getType().equals(scopeType)) {
            return new EqualsTo(new Column(USER_ID_COLUMN), new LongValue(dataScope.getCurrentUserId()));
        }

        // 处理 CUSTOM 类型（自定义规则条件）
        if (DataScopeTypeEnum.CUSTOM.getType().equals(scopeType) && StringUtils.hasLength(dataScope.getCustomRules())) {
            try {
                return CCJSqlParserUtil.parseCondExpression(dataScope.getCustomRules());
            } catch (JSQLParserException e) {
                log.error("[DataScope] 解析自定义规则失败, customRules={}, error={}", dataScope.getCustomRules(), e.getMessage(), e);
                return null; // 明确回退逻辑
            }
        }

        // 处理用户 ID 集合
        Set<Long> scopeUserIds = dataScope.getScopeUserIds();
        if (!CollectionUtils.isEmpty(scopeUserIds)) {
            Column column = new Column(USER_ID_COLUMN);
            List<Expression> expressions = scopeUserIds.stream()
                    .map(id -> (Expression) new LongValue(id))
                    .toList();
            return new InExpression(column, new ParenthesedExpressionList<>(expressions));
        }

        return null;
    }
}
