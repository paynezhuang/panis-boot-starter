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

package com.izpan.starter.code.generator.service;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import com.baomidou.mybatisplus.generator.query.SQLQuery;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.izpan.starter.code.generator.config.GeneratorConfig;
import com.izpan.starter.code.generator.constants.GeneratorConstants;
import com.izpan.starter.code.generator.engine.EnhanceFreemarkerTemplateEngine;
import com.izpan.starter.code.generator.entity.TableColumn;
import com.izpan.starter.code.generator.enums.QueryConditionsEnum;
import com.izpan.starter.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 代码生成服务
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.code.generator.service.CodeGeneratorService
 * @CreateTime 2024/8/26 - 10:58
 */

@Slf4j
public class CodeGeneratorService {

    // 后端模板路径
    private static final String BACKEND = "/templates/backend/";
    // 前端模板路径
    private static final String FRONTEND = "/templates/frontend/";
    private CodeGeneratorService() {

    }

    /**
     * 生成代码
     *
     * @param dataSource      数据源
     * @param generatorConfig 生成器配置
     * @author payne.zhuang
     * @CreateTime 2024-09-05 - 11:35:12
     */
    public static void create(DataSource dataSource, GeneratorConfig generatorConfig) {
        // 数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(dataSource)
                .databaseQueryClass(SQLQuery.class)
                .typeConvert(new MySqlTypeConvert())
                .keyWordsHandler(new MySqlKeyWordsHandler())
                .build();

        // 生成代码
        AutoGenerator autoGenerator = new AutoGenerator(dataSourceConfig);

        // 构建全局配置
        buildGlobalConfig(autoGenerator, generatorConfig);

        // 构建包配置
        buildPackageConfig(autoGenerator, generatorConfig);

        // 构建策略配置
        buildStrategyConfig(autoGenerator, generatorConfig);

        // 构建注入配置
        buildInjectionConfig(autoGenerator, generatorConfig);

        // 执行生成
        autoGenerator.execute(new EnhanceFreemarkerTemplateEngine());

    }

    /**
     * 构建全局配置
     *
     * @param autoGenerator   代码生成器
     * @param generatorConfig 生成配置
     * @author payne.zhuang
     * @CreateTime 2024-08-26 - 16:53:53
     */
    private static void buildGlobalConfig(AutoGenerator autoGenerator, GeneratorConfig generatorConfig) {
        // 定义输出路径，如有传入则使用传入的路径，否则使用默认路径
        generatorConfig.setOutPutDir(StringUtils.defaultIfEmpty(generatorConfig.getOutPutDir(), "target/code-generator"));

        // 设置作者
        GlobalConfig globalConfig = new GlobalConfig.Builder()
                .author(generatorConfig.getAuthor())
                // 开启 Springdoc 模式 默认值:false
                .enableSpringdoc()
                // 禁止打开输出目录 默认值:true
                .disableOpenDir()
                // 注释日期
                .commentDate("yyyy-MM-dd - HH:mm:ss")
                // 定义生成的实体类中日期类型 DateType.ONLY_DATE 默认值: DateType.TIME_PACK
                .dateType(DateType.TIME_PACK)
                // 指定输出目录
                .outputDir(generatorConfig.getOutPutDir())
                .build();

        autoGenerator.global(globalConfig);
    }

    /**
     * 构建包配置
     *
     * @param autoGenerator   代码生成器
     * @param generatorConfig 生成配置
     * @author payne.zhuang
     * @CreateTime 2024-08-26 - 17:11:11
     */
    private static void buildPackageConfig(AutoGenerator autoGenerator, GeneratorConfig generatorConfig) {
        // 获取生成模块名称
        String module = generatorConfig.getPackageConfig().getModule();
        PackageConfig packageConfig = new PackageConfig.Builder()
                // 父包模块名
                .parent(generatorConfig.getParentPackage())
                // Controller 包名 默认值:controller
                .controller("admin.controller.%s".formatted(module))
                // Entity 包名 默认值:modules
                .entity("modules.%s.domain.entity".formatted(module))
                // Service 包名 默认值:service
                .service("modules.%s.service".formatted(module))
                // ServiceImpl 包名 默认值:service.impl
                .serviceImpl("modules.%s.service.impl".formatted(module))
                // Mapper 包名 默认值:mapper
                .mapper("modules.%s.repository.mapper".formatted(module))
                // Mapper xml 输出
                .xml("modules.%s.repository.mapper".formatted(module))
                .build();

        autoGenerator.packageInfo(packageConfig);
    }

    /**
     * 构建策略配置
     *
     * @param autoGenerator   代码生成器
     * @param generatorConfig 生成配置
     * @author payne.zhuang
     * @CreateTime 2024-08-26 - 18:28:23
     */
    private static void buildStrategyConfig(AutoGenerator autoGenerator, GeneratorConfig generatorConfig) {
        GeneratorConfig.Strategy strategy = generatorConfig.getStrategyConfig();
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
                .addInclude(strategy.getTableName())

                // Entity 策略配置
                .entityBuilder()
                // 父类
                .superClass(strategy.getSuperClass())
                // 文件覆盖
                .enableFileOverride()
                // 开启 lombok
                .enableLombok()
                // 开启移除 is 前缀
                .enableRemoveIsPrefix()
                // 逻辑删除字段
                .logicDeleteColumnName(GeneratorConstants.LOGIC_DELETE_COLUMN)
                // 添加父类公共字段
                .addSuperEntityColumns(GeneratorConstants.SUPER_ENTITY_COLUMNS)
                // 模板路径
                .javaTemplate(BACKEND + "entity.java")

                // Controller 策略配置
                .controllerBuilder()
                // 文件覆盖
                .enableFileOverride()
                // 开启 RestController 注解
                .enableRestStyle()
                // 格式化 Controller 文件名称
                .formatFileName("%sController")
                // 模板路径
                .template(BACKEND + "controller.java")

                // Service 策略配置
                .serviceBuilder()
                // 文件覆盖
                .enableFileOverride()
                // 格式化 Service 接口文件名称
                .formatServiceFileName("I%sService")
                // 模板路径
                .serviceTemplate(BACKEND + "service.java")
                // 格式化 Service 实现类文件名称
                .formatServiceImplFileName("%sServiceImpl")
                // 模板路径
                .serviceImplTemplate(BACKEND + "serviceImpl.java")

                // Mapper 策略配置
                .mapperBuilder()
                // 文件覆盖
                .enableFileOverride()
                // 开启 BaseResultMap
                .enableBaseResultMap()
                // 开启 BaseColumnList
                .enableBaseColumnList()
                // 格式化 Mapper 文件名称
                .formatMapperFileName("%sMapper")
                // 模板路径
                .mapperTemplate(BACKEND + "mapper.java")
                // 格式化 Mapper xml 实现类文件名称
                .formatXmlFileName("%sMapper")
                // 模板路径
                .mapperXmlTemplate(BACKEND + "mapper.xml")

                .build();

        autoGenerator.strategy(strategyConfig);
    }

    /**
     * 构建注入配置
     *
     * @param autoGenerator   代码生成器
     * @param generatorConfig 生成配置
     * @author payne.zhuang
     * @CreateTime 2024-08-26 - 18:30:38
     */
    private static void buildInjectionConfig(AutoGenerator autoGenerator, GeneratorConfig generatorConfig) {
        GeneratorConfig.Injection injection = generatorConfig.getInjectionConfig();

        // 构建查询条件
        buildQueryCondition(generatorConfig.getTableColumnList());

        // 自定义参数
        Map<String, Object> customMap = Optional.ofNullable(injection.getCustomMap()).orElseGet(Maps::newHashMap);
        customMap.put("parent", generatorConfig.getParentPackage());
        customMap.put("module", StringUtil.toUpperCamel(generatorConfig.getPackageConfig().getModule()));
        customMap.put("tableColumnList", generatorConfig.getTableColumnList());

        // 获取生成模块名称
        String module = generatorConfig.getPackageConfig().getModule();

        // 自定义文件
        List<CustomFile> customFiles = Lists.newArrayList();

        // DTO 自定义 package 名
        String dtoPackageName = "modules.%s.domain.dto".formatted(module);

        // VO
        buildCustomFile(customFiles, "%sVO.java", BACKEND + "entityVO.java.ftl", "modules.%s.domain.vo".formatted(module));
        // 搜索 DTO 对象
        buildCustomFile(customFiles, "%sSearchDTO.java", BACKEND + "entitySearchDTO.java.ftl", dtoPackageName);
        // 新增 DTO 对象
        buildCustomFile(customFiles, "%sAddDTO.java", BACKEND + "entityAddDTO.java.ftl", dtoPackageName);
        // 编辑更新 DTO 对象
        buildCustomFile(customFiles, "%sUpdateDTO.java", BACKEND + "entityUpdateDTO.java.ftl", dtoPackageName);
        // 删除 DTO 对象
        buildCustomFile(customFiles, "%sDeleteDTO.java", BACKEND + "entityDeleteDTO.java.ftl", dtoPackageName);
        // BO 业务对象
        buildCustomFile(customFiles, "%sBO.java", BACKEND + "entityBO.java.ftl", "modules.%s.domain.bo".formatted(module));
        // facade 门面接口
        buildCustomFile(customFiles, "I%sFacade.java", BACKEND + "facade.java.ftl", "modules.%s.facade".formatted(module));
        // facade impl 门面实现
        buildCustomFile(customFiles, "%sFacadeImpl.java", BACKEND + "facadeImpl.java.ftl", "modules.%s.facade.impl".formatted(module));

        // 前端
        GeneratorConfig.Strategy strategy = generatorConfig.getStrategyConfig();
        String removePrefixTableName = strategy.getTableName().replace(strategy.getTablePrefix(), "");
        String dashTableName = removePrefixTableName.replace("_", "-");
        buildCustomFile(customFiles, "%s.d.ts".formatted(dashTableName), FRONTEND + "typings.api.d.ts.ftl", "src.typings.api.%s".formatted(module));
        buildCustomFile(customFiles, "index.ts", FRONTEND + "service.api.index.ts.ftl", "src.service.api");
        buildCustomFile(customFiles, "%s.ts".formatted(dashTableName), FRONTEND + "service.api.ts.ftl", "src.service.api.%s".formatted(module));
        buildCustomFile(customFiles, "index.ts", FRONTEND + "service.api.module.index.ts.ftl", "src.service.api.%s".formatted(module));
        buildCustomFile(customFiles, "app.d.ts", FRONTEND + "typings.app.d.ts.ftl", "src.typings");
        buildCustomFile(customFiles, "zh-cn.ts", FRONTEND + "locales.langs.zh-cn.ts.ftl", "src.locales.langs");
        buildCustomFile(customFiles, "en-us.ts", FRONTEND + "locales.langs.en-us.ts.ftl", "src.locales.langs");
        buildCustomFile(customFiles, "index.vue", FRONTEND + "views.index.vue.ftl", "src.views.%s.%s".formatted(module, dashTableName));
        buildCustomFile(customFiles, "%s-search.vue".formatted(dashTableName), FRONTEND + "views.modules.search.vue.ftl", "src.views.%s.%s.modules".formatted(module, dashTableName));
        buildCustomFile(customFiles, "%s-operate-drawer.vue".formatted(dashTableName), FRONTEND + "views.modules.drawer.vue.ftl", "src.views.%s.%s.modules".formatted(module, dashTableName));

        // 格式化表名
        customMap.put("dashTableName", dashTableName);
        customMap.put("propertyTableName", StringUtil.toLowerCamel(removePrefixTableName));
        customMap.put("upperPropertyTableName", StringUtil.toUpperCamel(removePrefixTableName));

        injection.setCustomMap(customMap);
        InjectionConfig injectionConfig = new InjectionConfig.Builder()
                .customMap(customMap)
                .customFile(customFiles)
                .build();
        autoGenerator.injection(injectionConfig);
    }

    /**
     * 构建自定义文件
     *
     * @param fileName     文件名
     * @param templatePath 模板路径
     * @param packageName  包名称
     * @author payne.zhuang
     * @CreateTime 2024-08-28 - 18:27:53
     */
    private static void buildCustomFile(List<CustomFile> customFiles, String fileName, String templatePath, String packageName) {
        customFiles.add(new CustomFile.Builder().fileName(fileName)
                .enableFileOverride()
                .templatePath(templatePath)
                .packageName(packageName)
                .build());
    }

    /**
     * 构建查询条件，渲染在模板中
     *
     * @param tableColumnList 表列列表
     * @author payne.zhuang
     * @CreateTime 2024-09-05 - 10:31:07
     */
    private static void buildQueryCondition(List<TableColumn> tableColumnList) {
        tableColumnList.stream()
                .filter(column -> "1".equals(column.getSearch()))
                .forEach(column -> {
                    String searchType = StringUtils.defaultIfEmpty(column.getSearchType(), QueryConditionsEnum.EQUAL.getCode());
                    column.setSearchType(QueryConditionsEnum.getMpValueByCode(searchType));
                });

    }
}
