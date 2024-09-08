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

package com.izpan.starter.code.generator.constants;

/**
 * 代码生成器常量
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.code.generator.constants.GeneratorConstants
 * @CreateTime 2024/8/28 - 15:31
 */

@SuppressWarnings("squid:S2386")
public final class GeneratorConstants {

    // 逻辑删除字段
    public static final String LOGIC_DELETE_COLUMN = "is_deleted";
    // 无需处理的默认字段
    public static final String[] IGNORE_COLUMNS = {"id", "create_user_id", "update_user", "update_user_id", "update_time", LOGIC_DELETE_COLUMN};
    // 父级实体默认字段
    public static final String[] SUPER_ENTITY_COLUMNS = {"id", "create_user", "create_user_id", "create_time", "update_user", "update_user_id", "update_time", LOGIC_DELETE_COLUMN};
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String DOT = ".";
    public static final String DASH = "-";
    public static final String EMPTY = "";
    public static final String UNDERSCORE = "_";
    public static final String Y = "1";
    public static final String N = "0";
    public static final String INPUT = "input";
    public static final String SELECT = "select";
    public static final String RADIO = "radio";
    public static final String EQUAL = "equal";
    public static final String NO_EQUAL = "no_equal";
    public static final String LIKE = "like";
    public static final String NO_LIKE = "no_like";
    private GeneratorConstants() {

    }
}
