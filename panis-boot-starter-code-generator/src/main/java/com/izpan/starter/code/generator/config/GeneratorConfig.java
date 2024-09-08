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

package com.izpan.starter.code.generator.config;

import com.izpan.starter.code.generator.entity.TableColumn;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 代码生成 - 全局配置
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.code.generator.config.GlobalConfig
 * @CreateTime 2024/8/26 - 16:27
 */

@Getter
@Setter
@Builder
public class GeneratorConfig {

    /**
     * 父包名 eg: com.izpan
     */
    private String parentPackage;

    /**
     * 作者 eg: payne.zhuang <paynezhuang@gmail.com>
     */
    private String author;

    /**
     * 输出目录
     */
    private String outPutDir;

    /**
     * 表列列表
     */
    private List<TableColumn> tableColumnList;

    /**
     * 包配置
     */
    private Package packageConfig;

    /**
     * 策略配置
     */
    private Strategy strategyConfig;

    /**
     * 注入配置
     */
    private Injection injectionConfig;

    @Getter
    @Setter
    @Builder
    public static class Package {

        /**
         * 生成模块
         */
        private String module;
    }

    @Getter
    @Setter
    @Builder
    public static class Strategy {

        /**
         * 表名
         */
        private String tableName;

        /**
         * 表前缀
         */
        private String tablePrefix;

        /**
         * 父类
         */
        private Class<?> superClass;
    }

    @Getter
    @Setter
    @Builder
    public static class Injection {

        /**
         * 自定义参数
         */
        private Map<String, Object> customMap;

    }
}
