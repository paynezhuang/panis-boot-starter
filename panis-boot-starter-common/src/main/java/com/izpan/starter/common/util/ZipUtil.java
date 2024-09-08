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

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Zip 压缩包工具类
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.common.util.ZipUtil
 * @CreateTime 2024/9/5 - 11:10
 */

@Slf4j
public class ZipUtil {

    private ZipUtil() {

    }

    /**
     * 创建 zip 文件并返回文件流
     *
     * @param sourceDirPath 原始文件路径
     * @return {@link ByteArrayOutputStream} zip 文件的字节数组输出流
     * @author payne.zhuang
     * @CreateTime 2024-09-05 - 11:13:47
     */
    public static ByteArrayOutputStream createZipFileAsStream(String sourceDirPath) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            addFilesToZip(sourceDirPath, zipOutputStream);
        }
        return byteArrayOutputStream;
    }

    /**
     * 创建 zip 文件并保存到指定路径
     *
     * @param sourceDirPath 生成代码的目录路径
     * @param zipFilePath   输出的 zip 文件路径
     * @author payne.zhuang
     * @CreateTime 2024-09-05 - 11:14:25
     */
    public static void createZipFileToPath(String sourceDirPath, String zipFilePath) throws IOException {
        Path zipFile = Files.createFile(Paths.get(zipFilePath));
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            addFilesToZip(sourceDirPath, zipOutputStream);
        }
    }

    /**
     * 将文件添加到 zip 压缩包中
     *
     * @param sourceDirPath   原文件目录路径
     * @param zipOutputStream zip 输出流
     * @author payne.zhuang
     * @CreateTime 2024-09-05 - 11:14:42
     */
    private static void addFilesToZip(String sourceDirPath, ZipOutputStream zipOutputStream) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(sourceDirPath))) {
            paths.filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        // 创建 zip 条目
                        ZipEntry zipEntry = new ZipEntry(Paths.get(sourceDirPath).relativize(path).toString());
                        try {
                            // 将 zip 条目添加到 zip 输出流
                            zipOutputStream.putNextEntry(zipEntry);
                            // 将文件内容写入 zip 输出流
                            Files.copy(path, zipOutputStream);
                            // 关闭当前 zip 条目
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            log.error("无法压缩文件: {} - {}", path, e.getMessage());
                        }
                    });
        }
    }
}
