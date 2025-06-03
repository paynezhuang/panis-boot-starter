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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据权限条件对象
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.database.mybatis.plus.domain.DataScopeCondition
 * @CreateTime 2025/5/13 - 21:45
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataScopeCondition implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字段名
     */
    private String field;

    /**
     * 操作符
     */
    private String operator;

    /**
     * 值
     */
    private String value;

    /**
     * 逻辑关系（AND/OR）
     */
    private String logic;

    /**
     * 设置变量名称
     * 用于标识该条件的唯一性
     */
    private String variable;
}
