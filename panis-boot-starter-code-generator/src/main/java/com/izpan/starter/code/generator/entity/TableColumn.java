/*
 * All Rights Reserved: Copyright [2024] [Zhuang Pan (paynezhuang@gmail.com)]
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

package com.izpan.starter.code.generator.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 代码生成 Entity 实体类
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.code.generator.entity.TableColumn
 * @CreateTime 2024/8/28 - 16:03
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TableColumn {

    /**
     * 表列名称
     */
    private String columnName;

    /**
     * 表列位置
     */
    private Integer ordinalPosition;

    /**
     * 多语言(0:否,1:是)
     */
    private String i18n;

    /**
     * 必填(0:否,1:是)
     */
    private String required;

    /**
     * 列表(0:否,1:是)
     */
    private String list;

    /**
     * 查询(0:否,1:是)
     */
    private String search;

    /**
     * 查询类型
     */
    private String searchType;

    /**
     * 新增(0:否,1:是)
     */
    private String added;

    /**
     * 编辑更新(0:否,1:是)
     */
    private String edit;

    /**
     * 关联字典代码
     */
    private String dictCode;

    /**
     * 渲染类型(select,radio)
     */
    private String renderType;

    /**
     * 名称（小驼峰）
     */
    private String propertyName;

    /**
     * 字段描述
     */
    private String columnComment;

    /**
     * java 类型
     */
    private String javaType;

    /**
     * type script 类型
     */
    private String typeScriptType;

}
