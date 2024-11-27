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

package com.izpan.starter.oss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 对象存储枚举
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.oss.enums.OssEnum
 * @CreateTime 2024/11/20 - 22:13
 */

@Getter
@AllArgsConstructor
public enum OssEnum {

    /**
     * 本地对象存储
     */
    LOCAL("local", "1"),

    /**
     * Minio对象存储
     */
    MINIO("minio", "2"),

    ;

    /**
     * 名称
     */
    final String name;

    /**
     * 编码
     */
    final String code;

    /**
     * 根据名称获取对象存储枚举
     *
     * @param name 名称
     * @return {@link OssEnum } 对象存储枚举
     * @author payne.zhuang
     * @CreateTime 2024-11-26 - 09:46:58
     */
    public static OssEnum of(String name) {
        return Arrays.stream(values())
                .filter(ossEnum -> ossEnum.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown OssEnum name: " + name));
    }

    /**
     * 根据名称获取对象存储编码
     *
     * @param name 名称
     * @return {@link String } 对象存储编码
     * @author payne.zhuang
     * @CreateTime 2024-11-26 - 09:47:23
     */
    public static String code(String name) {
        return of(name).getCode();
    }
}
