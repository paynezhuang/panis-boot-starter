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

import lombok.Getter;

import java.io.Serializable;

/**
 * 参数类型枚举，定义操作符和变量的值类型
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.database.mybatis.plus.enums.ParameterTypeEnum
 * @CreateTime 2025/5/30 - 18:09
 */

@Getter
public enum ParameterTypeEnum implements Serializable {
    SCALAR,     // 单值类型（如用户 ID、名称）
    LIST,       // 集合类型（如角色 ID 列表）
    RANGE,      // 区间类型（如时间范围）
    DATETIME,   // 时间类型（支持区间）
    NONE        // 无参数类型（如 IS NULL）
}
