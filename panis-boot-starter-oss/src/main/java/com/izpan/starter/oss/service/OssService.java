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

package com.izpan.starter.oss.service;

import com.izpan.starter.oss.domain.OssFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * OSS 公共服务接口
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.oss.service.OssService
 * @CreateTime 2024/11/15 - 15:23
 */

public interface OssService {

    /**
     * 创建 存储桶
     *
     * @param bucketName 存储桶名称
     * @return {@link boolean} 是否创建成功
     * @author payne.zhuang
     * @CreateTime 2024-11-15 - 17:06:50
     */
    void makeBucket(String bucketName);

    /**
     * 判断存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return {@link boolean} 是否存在存储
     * @author payne.zhuang
     * @CreateTime 2024-11-21 - 10:28:44
     */
    boolean bucketExists(String bucketName);

    /**
     * 删除 存储桶
     *
     * @param bucketName 存储桶名称
     * @author payne.zhuang
     * @CreateTime 2024-11-21 - 16:29:12
     */
    void removeBucket(String bucketName);

    /**
     * 上传文件
     *
     * @param file 文件 {@link File}
     * @return {@link OssFile} 文件信息
     * @author payne.zhuang
     * @CreateTime 2024-11-21 - 17:15:00
     */
    OssFile putFile(File file);

    /**
     * 上传文件
     *
     * @param bucketName 存储桶名称
     * @param file       文件
     * @return {@link OssFile} 文件信息
     * @author payne.zhuang
     * @CreateTime 2024-11-21 - 17:15:20
     */
    OssFile putFile(String bucketName, File file);

    /**
     * 上传文件
     *
     * @param fileName 文件名
     * @param stream   文件流
     * @return {@link OssFile} 文件信息
     * @author payne.zhuang
     * @CreateTime 2024-11-25 - 14:42:17
     */
    OssFile putFile(String fileName, InputStream stream);

    /**
     * 上传文件
     *
     * @param bucketName 存储桶名称
     * @param fileName   文件名
     * @param stream     文件流
     * @return {@link OssFile} 文件信息
     * @author payne.zhuang
     * @CreateTime 2024-11-25 - 14:42:28
     */
    OssFile putFile(String bucketName, String fileName, InputStream stream);

    /**
     * 删除文件
     *
     * @param fileName 文件名
     * @author payne.zhuang
     * @CreateTime 2024-11-25 - 14:12:10
     */
    void removeFile(String fileName);

    /**
     * 删除文件
     *
     * @param bucketName 存储桶名称
     * @param fileName   文件名
     * @author payne.zhuang
     * @CreateTime 2024-11-25 - 14:12:19
     */
    void removeFile(String bucketName, String fileName);

    /**
     * 批量删除文件
     *
     * @param fileNames 文件名集合
     * @author payne.zhuang
     * @CreateTime 2024-11-25 - 14:12:54
     */
    void removeFiles(List<String> fileNames);

    /**
     * 批量删除文件
     *
     * @param bucketName 存储桶名称
     * @param fileNames  文件名集合
     * @CreateTime 2024-11-25 - 14:13:04
     */
    void removeFiles(String bucketName, List<String> fileNames);

    /**
     * 文件外链链接
     *
     * @param fileName 文件名
     * @return {@link String } 外链链接
     * @author payne.zhuang
     * @CreateTime 2024-11-26 - 21:50:17
     */
    String preview(String fileName);

    /**
     * 文件外链链接
     *
     * @param fileName 文件名
     * @param expiry   有效期（单位：秒）
     * @return {@link String } 外链链接
     * @author payne.zhuang
     * @CreateTime 2024-11-26 - 21:59:02
     */
    String preview(String fileName, int expiry);

    /**
     * 文件外链链接
     *
     * @param bucketName 存储桶名称
     * @param fileName   文件名
     * @return {@link String } 外链链接
     * @author payne.zhuang
     * @CreateTime 2024-11-26 - 21:50:24
     */
    String preview(String bucketName, String fileName);

    /**
     * 文件外链链接
     *
     * @param bucketName 存储桶名称
     * @param fileName   文件名
     * @param expiry     有效期（单位：秒）
     * @return {@link String } 外链链接
     * @CreateTime 2024-11-26 - 21:59:02
     */
    String preview(String bucketName, String fileName, int expiry);
}
