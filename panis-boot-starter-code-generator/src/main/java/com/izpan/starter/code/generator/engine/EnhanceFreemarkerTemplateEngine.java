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

package com.izpan.starter.code.generator.engine;

import com.baomidou.mybatisplus.generator.config.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Mybatis Plus 自定义代码生成引擎
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.code.generator.engine.EnhanceFreemarkerTemplateEngine
 * @CreateTime 2024/8/26 - 20:38
 */

@Slf4j
public class EnhanceFreemarkerTemplateEngine extends FreemarkerTemplateEngine {

    /**
     * 获取包名
     *
     * @param tableInfo 表信息
     * @param file      文件信息
     * @param customMap 自定义参数
     * @return {@link String} 包名
     * @author payne.zhuang
     * @CreateTime 2024-08-28 - 17:07:56
     */
    private static String getString(TableInfo tableInfo, CustomFile file, Map<String, Object> customMap) {
        String packageName = file.getPackageName();
        String parent = (String) customMap.get("parent");

        if (packageName.contains(".dto")) {
            // 如果是dto，把包名中的下划线替换成点，形成多级包名，以及再加上子包名
            String subPackageName = tableInfo.getName().substring(tableInfo.getName().indexOf("_") + 1).replace("_", ".");
            packageName = packageName + "." + subPackageName;
            putKey(customMap, "dtoPackageName", parent, packageName);
        } else if (packageName.contains(".facade")) {
            putKey(customMap, "facadePackageName", parent, packageName);
        } else if (packageName.contains(".vo")) {
            putKey(customMap, "voPackageName", parent, packageName);
        }

        return packageName;
    }

    /**
     * 参数赋值
     *
     * @param customMap   自定义参数
     * @param key         Key
     * @param parent      父级包名
     * @param packageName 包名称
     * @author payne.zhuang
     * @CreateTime 2024-08-28 - 21:51:39
     */
    private static void putKey(Map<String, Object> customMap, String key, String parent, String packageName) {
        customMap.put(key, parent + "." + packageName);
    }

    /**
     * 输出自定义文件
     *
     * @param customFiles 自定义文件
     * @param tableInfo   表信息
     * @param objectMap   对象映射
     * @author payne.zhuang
     * @CreateTime 2024-08-28 - 17:06:56
     */
    @Override
    protected void outputCustomFile(List<CustomFile> customFiles, TableInfo tableInfo, @NonNull Map<String, Object> objectMap) {
        // 配置文件
        ConfigBuilder config = (ConfigBuilder) objectMap.get("config");
        InjectionConfig injectionConfig = config.getInjectionConfig();
        assert injectionConfig != null;

        // 提取前期定义的自定义参数，填充到 objectMap 中，构建完整参数
        Map<String, Object> customMap = injectionConfig.getCustomMap();

        // 权限参数
        customMap.put("permission", tableInfo.getName().replace("_", ":"));

        // 填充自定义参数
        objectMap.putAll(customMap);

        // 实体名称 以及 父路径
        String entityName = tableInfo.getEntityName();
        String parentPath = this.getPathInfo(OutputFile.parent);

        // 遍历文件
        for (CustomFile file : customFiles) {
            String filePath = getFilePath(file, parentPath, tableInfo, objectMap);
            String fileName = filePath + File.separator + String.format(file.getFileName(), entityName);
            this.outputFile(new File(fileName), objectMap, file.getTemplatePath(), file.isFileOverride());
        }
    }

    /**
     * 获取文件路径
     *
     * @param file       自定义文件
     * @param parentPath 父路径
     * @param tableInfo  表信息
     * @param customMap  自定义参数
     * @return {@link String }
     * @author payne.zhuang
     * @CreateTime 2024-08-28 - 21:57:45
     */
    private String getFilePath(CustomFile file, String parentPath, TableInfo tableInfo, Map<String, Object> customMap) {
        String filePath = StringUtils.isNotBlank(file.getFilePath()) ? file.getFilePath() : parentPath;
        if (StringUtils.isNotBlank(file.getPackageName())) {
            String packageName = getString(tableInfo, file, customMap);
            filePath = filePath + File.separator + packageName;
            filePath = filePath.replace(".", File.separator);
        }
        return filePath;
    }

}
