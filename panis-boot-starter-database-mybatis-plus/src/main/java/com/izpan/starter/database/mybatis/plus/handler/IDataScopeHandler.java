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

package com.izpan.starter.database.mybatis.plus.handler;

import com.google.common.cache.CacheStats;
import com.izpan.starter.database.mybatis.plus.domain.DataScope;

/**
 * 数据权限处理器接口
 * 用于获取数据权限信息
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.database.mybatis.plus.handler.IDataScopeHandler
 * @CreateTime 2025/5/12 - 10:38
 */
public interface IDataScopeHandler {

    /**
     * 获取当前请求权限标识
     *
     * @return {@link String }  权限标识
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:23:04
     */
    String getPermissionCode();

    /**
     * 获取当前登录用户 ID
     *
     * @return {@link Long }  当前登录用户 ID
     * @author payne.zhuang
     * @CreateTime 2025-05-28 22:20:54
     */
    Long getCurrentUserId();

    /**
     * 获取数据权限信息
     *
     * @param permissionCode 权限标识
     * @return 数据权限信息
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:22:35
     */
    DataScope getDataScope(String permissionCode);

    /**
     * 使指定用户的所有权限缓存失效
     * 当用户角色或权限发生变更时调用此方法，确保下次请求获取最新权限
     *
     * @param userId 用户ID
     * @author payne.zhuang
     * @CreateTime 2025-05-15 - 10:23:15
     */
    void invalidateUserCache(Long userId);

    /**
     * 使指定权限的所有用户缓存失效
     * 当权限规则或数据范围定义发生变更时调用此方法，确保所有用户获取最新权限规则
     *
     * @param permissionCode 权限标识
     * @author payne.zhuang
     * @CreateTime 2025-05-15 - 10:24:32
     */
    void invalidatePermissionCache(String permissionCode);

    /**
     * 获取缓存统计信息
     * 用于监控缓存性能，包括命中率、加载次数、加载时间等指标
     *
     * @return {@link CacheStats} 缓存统计信息
     * @author payne.zhuang
     * @CreateTime 2025-05-15 - 10:25:48
     */
    CacheStats getCacheStats();

    /**
     * 获取缓存的SQL
     *
     * @param cacheKey 缓存键
     * @return 缓存的SQL
     * @author payne.zhuang
     * @CreateTime 2025-05-28 - 23:00:39
     */
    String getCachedSql(String cacheKey);

    /**
     * 缓存SQL
     *
     * @param cacheKey 缓存键
     * @param sql      SQL
     * @author payne.zhuang
     * @CreateTime 2025-05-28 - 23:01:04
     */
    void cacheSql(String cacheKey, String sql);

    /**
     * 构建缓存键
     *
     * @param userId         用户ID
     * @param permissionCode 权限标识
     * @param msId           MappedStatement ID
     * @param scopeType      数据权限类型
     * @return 缓存键
     * @author payne.zhuang
     * @CreateTime 2025-05-28 - 23:01:06
     */
    String buildCacheKey(Long userId, String permissionCode, String msId, String scopeType);

    /**
     * 清理SQL缓存
     * <p>
     * 清理DataScopeInterceptor中使用的SQL_CACHE缓存
     * 当数据权限配置变更时，相关的SQL缓存应该被清理以确保使用最新的权限逻辑
     * </p>
     *
     * @author payne.zhuang
     * @CreateTime 2025-06-02 - 23:35:00
     */
    void invalidateSqlCache();

    /**
     * 按缓存键前缀清理SQL缓存
     * <p>
     * 根据缓存键的前缀模式清理相关的SQL缓存
     * 缓存键格式：userId:permissionCode:msId:scopeType
     * </p>
     *
     * @param keyPrefix 缓存键前缀，如 "1001:" 或 "1001:user:list:"
     * @author payne.zhuang
     * @CreateTime 2025-06-02 - 23:36:00
     */
    void invalidateSqlCacheByPrefix(String keyPrefix);

    /**
     * 清理所有缓存
     * <p>
     * 清理数据权限结果缓存和SQL缓存，用于系统维护或重大配置变更
     * </p>
     *
     * @author payne.zhuang
     * @CreateTime 2025-06-02 - 23:37:00
     */
    void invalidateAllCache();
}
