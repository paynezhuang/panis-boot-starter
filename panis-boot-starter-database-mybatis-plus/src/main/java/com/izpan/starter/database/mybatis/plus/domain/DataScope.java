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

package com.izpan.starter.database.mybatis.plus.domain;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * 数据权限
 * 用于存储数据权限相关的信息
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.database.mybatis.plus.domain.DataScope
 * @CreateTime 2025/5/12 - 10:38
 */

@Data
@Builder
public class DataScope implements Serializable {

    @Serial
    private static final long serialVersionUID = -2024640053806172796L;

    /**
     * 数据权限类型
     * 对应 DataScopeTypeEnum 枚举值
     */
    private String scopeType;

    /**
     * 当前登录用户 ID
     */
    private Long currentUserId;

    /**
     * 权限用户 IDs
     */
    private Set<Long> scopeUserIds;

    /**
     * 权限编码
     * 用于存储权限编码
     */
    private String permissionCode;

    /**
     * 自定义可见字段
     * 用于存储可见字段
     */
    private String customFields;

    /**
     * 自定义规则条件
     * 当数据权限类型为自定义时使用
     */
    private String customRules;
}
