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

import java.io.Serializable;
import java.util.Arrays;

/**
 * 数据权限类型枚举
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.database.mybatis.plus.enums.DataScopeTypeEnum
 * @CreateTime 2025/5/10 - 20:15
 */

@Getter
@AllArgsConstructor
public enum DataScopeTypeEnum implements Serializable {
    /**
     * 全部数据权限
     */
    ALL("1", "全部数据权限", 1),

    /**
     * 本组织单位及下级组织单位数据权限
     */
    UNIT_AND_CHILD("2", "本组织单位及下级组织单位数据权限", 2),

    /**
     * 本组织单位数据权限
     */
    UNIT("3", "本组织单位数据权限", 3),

    /**
     * 本人及下级组织单位数据权限
     */
    SELF_AND_CHILD("4", "本人及下级组织单位数据权限", 4),

    /**
     * 自定义数据权限（范围动态评估）
     */
    CUSTOM("5", "自定义数据权限", 5),

    /**
     * 仅本人数据权限
     */
    SELF("6", "仅本人数据权限", 6);

    private final String type;
    private final String name;
    /**
     * 默认优先级（值越小优先级越高）
     * ALL > UNIT_AND_CHILD > UNIT > SELF_AND_CHILD > CUSTOM > SELF
     * CUSTOM 的优先级需动态评估
     */
    private final int priority;

    /**
     * 比较数据权限类型的优先级
     *
     * @param other 其他数据权限类型
     * @return 如果当前类型优先级更高返回-1，如果优先级相同返回0，如果优先级更低返回1
     */
    public int comparePriority(DataScopeTypeEnum other) {
        return Integer.compare(this.priority, other.priority);
    }

    /**
     * 根据类型获取数据权限类型枚举
     *
     * @param type 名称
     * @return {@link DataScopeTypeEnum } 对象存储枚举
     * @author payne.zhuang
     * @CreateTime 2025-05-22 20:56:37
     */
    public static DataScopeTypeEnum of(String type) {
        return Arrays.stream(values())
                .filter(typeEnum -> typeEnum.getType().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown DataScopeTypeEnum type: " + type));
    }

    /**
     * 根据类型获取数据权限类型名称
     *
     * @param type 类型
     * @return {@link String } 数据权限类型名称
     * @author payne.zhuang
     * @CreateTime 2025-05-22 20:57:48
     */
    public static String type(String type) {
        return of(type).getName();
    }
}
