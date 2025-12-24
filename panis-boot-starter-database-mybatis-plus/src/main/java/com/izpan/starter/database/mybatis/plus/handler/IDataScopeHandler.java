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

import com.izpan.starter.database.mybatis.plus.domain.DataScope;
import org.apache.ibatis.mapping.MappedStatement;

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
     * @param ms             MyBatis 映射语句对象
     * @param permissionCode 权限标识
     * @return 数据权限信息
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:22:35
     */
    DataScope getDataScope(MappedStatement ms, String permissionCode);

}
