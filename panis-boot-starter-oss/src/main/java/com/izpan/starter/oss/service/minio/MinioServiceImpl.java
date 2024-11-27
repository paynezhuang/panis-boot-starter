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

package com.izpan.starter.oss.service.minio;

import cn.hutool.core.util.IdUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.izpan.starter.common.pool.StringPools;
import com.izpan.starter.common.util.FileUtil;
import com.izpan.starter.oss.config.OssProperties;
import com.izpan.starter.oss.domain.OssFile;
import com.izpan.starter.oss.enums.OssEnum;
import com.izpan.starter.oss.enums.PolicyEnum;
import com.izpan.starter.oss.exception.OSSException;
import com.izpan.starter.oss.manage.OssManager;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

/**
 * Minio 对象存储服务实现
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.oss.service.minio.MinioServiceImpl
 * @CreateTime 2024/11/15 - 17:02
 */

@Slf4j
@AllArgsConstructor
public class MinioServiceImpl implements MinioService, InitializingBean {

    /**
     * Minio 客户端
     */
    private final MinioClient client;

    /**
     * 配置类
     */
    private final OssProperties properties;

    @Override
    public void afterPropertiesSet() {
        OssManager.registerService(OssEnum.MINIO.getName(), this);
        log.info("OSS Register Minio Service success");
    }

    @Override
    @SneakyThrows
    public void makeBucket(String bucketName) {
        try {
            if (!bucketExists(bucketName)) {
                // 创建存储桶
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                // 设置存储桶策略（只读）
                client.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(policy(bucketName, PolicyEnum.WRITE_ONLY))
                        .build());
            }
        } catch (Exception e) {
            throw new OSSException("Minio create bucket error", e);
        }
    }

    @Override
    public boolean bucketExists(String bucketName) {
        try {
            return client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new OSSException("Minio exists bucket error", e);
        }
    }

    @Override
    public void removeBucket(String bucketName) {
        try {
            client.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new OSSException("Minio remove bucket error", e);
        }
    }

    @Override
    public OssFile putFile(File file) {
        return putFile(properties.getBucketName(), file);
    }

    @Override
    public OssFile putFile(String bucketName, File file) {
        try (InputStream stream = new FileInputStream(file)) {
            return putFile(bucketName, file.getName(), stream);
        } catch (Exception e) {
            throw new OSSException("Minio put file error", e);
        }
    }

    @Override
    public OssFile putFile(String fileName, InputStream stream) {
        return putFile(properties.getBucketName(), fileName, stream);
    }

    @Override
    public OssFile putFile(String bucketName, String fileName, InputStream stream) {
        try {
            makeBucket(bucketName);
            String uuid = IdUtil.fastSimpleUUID();
            String path = FileUtil.path(fileName, uuid);
            client.putObject(PutObjectArgs.builder().bucket(bucketName)
                    .object(path)
                    .stream(stream, stream.available(), -1)
                    .contentType(StringPools.OCTET_STREAM)
                    .build());
            return OssFile.builder()
                    .name(fileName)
                    .path(path)
                    .uuid(uuid)
                    .location(OssEnum.MINIO.getCode())
                    .contentType(StringPools.OCTET_STREAM)
                    .build();
        } catch (Exception e) {
            throw new OSSException("Minio put file error", e);
        }
    }

    @Override
    public void removeFile(String fileName) {
        removeFile(properties.getBucketName(), fileName);
    }

    @Override
    public void removeFile(String bucketName, String fileName) {
        try {
            if (syncDelete()) return;
            client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            throw new OSSException("Minio remove file error", e);
        }
    }

    @Override
    public void removeFiles(List<String> fileNames) {
        removeFiles(properties.getBucketName(), fileNames);
    }

    @Override
    @SneakyThrows
    public void removeFiles(String bucketName, List<String> fileNames) {
        if (syncDelete()) return;
        Stream<DeleteObject> stream = fileNames.stream().map(DeleteObject::new);
        Iterable<Result<DeleteError>> results = client.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(stream::iterator).build());
        // Lazy iterator contains object removal status.
        while (results.iterator().hasNext()) {
            Result<DeleteError> result = results.iterator().next();
            DeleteError error = result.get();
            log.error("Error in deleting object {}; {}", error.objectName(), error.message());
        }
        log.info("Minio Files removed successfully from object name: {}", fileNames);
    }

    @Override
    public String preview(String fileName) {
        return preview(properties.getBucketName(), fileName, properties.getExpiry());
    }

    @Override
    public String preview(String fileName, int expiry) {
        return preview(properties.getBucketName(), fileName, expiry);
    }

    @Override
    public String preview(String bucketName, String fileName) {
        return preview(bucketName, fileName, properties.getExpiry());
    }

    @Override
    public String preview(String bucketName, String fileName, int expiry) {
        if (StringUtils.isAnyEmpty(bucketName, fileName)) return StringPools.EMPTY;
        try {
            return client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(expiry)
                            .build());
        } catch (Exception e) {
            throw new OSSException("Minio preview url error", e);
        }
    }

    /**
     * 是否同步删除，false 为不删除
     *
     * @return {@link Boolean } 是否同步删除
     * @author payne.zhuang
     * @CreateTime 2024-11-26 - 18:53:02
     */
    public boolean syncDelete() {
        return Boolean.FALSE.equals(properties.getSyncDelete());
    }

    /**
     * MINIO 策略 <a href="https://min.io/docs/minio/macos/administration/identity-access-management/policy-based-access-control.html#minio-policy-built-in">相关文档</a>
     *
     * @param bucketName 存储桶名称
     * @param policy     策略枚举
     * @return {@link String } 策略相关权限
     * @author payne.zhuang
     * @CreateTime 2024-11-25 - 17:43:25
     */
    @SuppressWarnings("SameParameterValue")
    private String policy(@NotNull String bucketName, @NotNull PolicyEnum policy) {
        // 创建一个JsonObject对象，用于存储策略的主要内容
        JsonObject object = new JsonObject();
        // 设置策略版本
        object.addProperty("Version", "2012-10-17");

        // 创建一个JsonArray对象，用于存储策略的声明
        JsonArray statements = new JsonArray();

        // 存储桶级别操作
        statements.add(createBucketStatement(bucketName, policy));

        // 对象级别操作的声明
        statements.add(createObjectStatement(bucketName, policy));

        // 将声明数值添加到策略对象中
        object.add("Statement", statements);
        // 记录策略信息
        log.info("Minio Policy: {}", object);
        // 返回策略的字符串表示
        return object.toString();
    }

    /**
     * 创建存储桶级别操作的声明
     *
     * @param bucketName 存储桶名称
     * @param policy     策略枚举
     * @return {@link JsonObject } 存储桶级别操作的声明
     * @author payne.zhuang
     * @CreateTime 2024-11-26 - 10:14:33
     */
    private JsonObject createBucketStatement(@NotNull String bucketName, @NotNull PolicyEnum policy) {
        // 创建一个JsonObject对象，用于存储存储桶级别操作的声明
        JsonObject bucketStatement = new JsonObject();
        // 设置声明的效果为允许
        bucketStatement.addProperty("Effect", "Allow");
        // 设置声明的主体为所有人
        bucketStatement.addProperty("Principal", "*");
        // 创建一个JsonArray对象，用于存储存储桶级别的操作
        JsonArray bucketActions = new JsonArray();
        // 根据策略类型添加相应的存储桶级别操作
        switch (policy) {
            case READ_ONLY:
                // 添加只读权限：获取存储桶位置
                bucketActions.add("s3:GetBucketLocation");
                // 添加只读权限：列出存储桶中的对象
                bucketActions.add("s3:ListBucket");
                break;
            case WRITE_ONLY:
                // 添加只写权限：列出多部分上传
                bucketActions.add("s3:ListBucketMultipartUploads");
                break;
            case READ_WRITE:
                // 添加读写权限：获取存储桶位置
                bucketActions.add("s3:GetBucketLocation");
                // 添加读写权限：列出存储桶中的对象
                bucketActions.add("s3:ListBucket");
                // 添加读写权限：列出多部分上传
                bucketActions.add("s3:ListBucketMultipartUploads");
                break;
            default:
                // 如果策略类型无效，抛出异常
                throw new IllegalArgumentException("Invalid policy type: " + policy.getType());
        }
        // 将操作添加到声明中
        bucketStatement.add("Action", bucketActions);
        // 设置声明的资源路径
        bucketStatement.addProperty("Resource", "arn:aws:s3:::" + bucketName);
        // 返回存储桶级别操作的声明
        return bucketStatement;
    }

    /**
     * 创建对象级别操作的声明
     *
     * @param bucketName 存储桶名称
     * @param policy     策略枚举
     * @return {@link JsonObject } 对象级别操作的声明
     * @author payne.zhuang
     * @CreateTime 2024-11-26 - 10:11:23
     */
    private JsonObject createObjectStatement(@NotNull String bucketName, @NotNull PolicyEnum policy) {
        // 创建一个JsonObject对象，用于存储对象级别操作的声明
        JsonObject objectStatement = new JsonObject();
        // 设置声明的效果为允许
        objectStatement.addProperty("Effect", "Allow");
        // 设置声明的主体为所有人
        objectStatement.addProperty("Principal", "*");
        // 创建一个JsonArray对象，用于存储策略的操作
        JsonArray objectActions = new JsonArray();
        // 根据策略类型添加相应的操作
        switch (policy) {
            case READ_ONLY:
                // 添加只读权限：获取对象
                objectActions.add("s3:GetObject");
                break;
            case WRITE_ONLY:
                // 添加只写权限：上传对象
                objectActions.add("s3:PutObject");
                // 添加只写权限：删除对象
                objectActions.add("s3:DeleteObject");
                // 添加只写权限：中止多部分上传
                objectActions.add("s3:AbortMultipartUpload");
                break;
            case READ_WRITE:
                // 添加读写权限：获取对象
                objectActions.add("s3:GetObject");
                // 添加读写权限：上传对象
                objectActions.add("s3:PutObject");
                // 添加读写权限：删除对象
                objectActions.add("s3:DeleteObject");
                // 添加读写权限：中止多部分上传
                objectActions.add("s3:AbortMultipartUpload");
                break;
            default:
                // 如果策略类型无效，抛出异常
                throw new IllegalArgumentException("Invalid policy type: " + policy.getType());
        }
        // 将操作添加到声明中
        objectStatement.add("Action", objectActions);
        // 设置声明的资源路径
        objectStatement.addProperty("Resource", "arn:aws:s3:::" + bucketName + "/*");
        // 返回对象级别操作的声明
        return objectStatement;
    }
}
