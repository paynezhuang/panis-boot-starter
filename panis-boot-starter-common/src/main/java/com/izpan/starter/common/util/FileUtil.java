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

package com.izpan.starter.common.util;

import cn.hutool.core.date.DateUtil;
import com.izpan.starter.common.pool.StringPools;
import org.apache.commons.lang3.ObjectUtils;

import java.time.LocalDateTime;

/**
 * 文件工具类
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.common.util.FileUtil
 * @CreateTime 2024/11/25 - 16:45
 */

public class FileUtil {

    private FileUtil() {

    }

    private static final long KB = 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;
    private static final long TB = GB * 1024;

    /**
     * 格式化文件大小
     *
     * @param length 文件大小（字节）
     * @return {@link String } 格式化后的文件大小字符串 eg: 1.23 MB
     * @author payne.zhuang
     * @CreateTime 2024-11-25 - 23:31:00
     */
    public static String readableFileSize(long length) {
        if (length >= TB) {
            return String.format("%.2f TB", (double) length / TB);
        } else if (length >= GB) {
            return String.format("%.2f GB", (double) length / GB);
        } else if (length >= MB) {
            return String.format("%.2f MB", (double) length / MB);
        } else if (length >= KB) {
            return String.format("%.2f KB", (double) length / KB);
        } else {
            return String.format("%d B", length);
        }
    }

    /**
     * 获取文件后缀
     *
     * @param fileName 文件名
     * @return {@link String } 文件后缀 eg: jpg、png、txt等等，如果没有后缀则返回空字符串
     * @author payne.zhuang
     * @CreateTime 2024-11-26 - 09:43:55
     */
    public static String extension(String fileName) {
        if (ObjectUtils.isEmpty(fileName) || fileName.lastIndexOf(StringPools.DOT) == -1) {
            return StringPools.EMPTY;
        }
        return fileName.substring(fileName.lastIndexOf(StringPools.DOT) + 1);
    }

    /**
     * 组装文件路径
     *
     * @param fileName 文件名
     * @param uuid     文件uuid
     * @return {@link String } 文件路径 eg: upload/2024/11/25/uuid_xxx.jpg
     * @author payne.zhuang
     * @CreateTime 2024-11-25 - 17:05:10
     */
    public static String path(String fileName, String uuid) {
        String dateNow = DateUtil.format(LocalDateTime.now(), "yyyy/MM/dd");
        return "upload" + StringPools.SLASH + dateNow + StringPools.SLASH + uuid + StringPools.UNDERSCORE + fileName;
    }
}
