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

package com.izpan.starter.oss.service.local;

import com.izpan.starter.oss.config.OssProperties;
import com.izpan.starter.oss.domain.OssFile;
import com.izpan.starter.oss.enums.OssEnum;
import com.izpan.starter.oss.exception.OSSException;
import com.izpan.starter.oss.manage.OssManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * 本地服务接口实现类
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.oss.service.local.LocalServiceImpl
 * @CreateTime 2024/11/21 - 09:55
 */

@Slf4j
@AllArgsConstructor
public class LocalServiceImpl implements LocalService, InitializingBean {

    /**
     * 配置类
     */
    private final OssProperties properties;

    @Override
    public void afterPropertiesSet() {
        OssManager.registerService(OssEnum.LOCAL.getName(), this);
        log.info("OSS Register Local Service success");
    }

    @Override
    public void makeBucket(String bucketName) {
        throw new OSSException("Local Service not support makeBucket");
    }

    @Override
    public boolean bucketExists(String bucketName) {
        return false;
    }

    @Override
    public void removeBucket(String bucketName) {
        throw new OSSException("Local Service not support removeBucket");
    }

    @Override
    public OssFile putFile(File file) {
        throw new OSSException("Local Service not support putFile");
    }

    @Override
    public OssFile putFile(String bucketName, File file) {
        throw new OSSException("Local Service not support putFile");
    }

    @Override
    public OssFile putFile(String fileName, InputStream stream) {
        throw new OSSException("Local Service not support putFile");
    }

    @Override
    public OssFile putFile(String bucketName, String fileName, InputStream stream) {
        throw new OSSException("Local Service not support putFile");
    }

    @Override
    public void removeFile(String fileName) {
        throw new OSSException("Local Service not support removeFile");
    }

    @Override
    public void removeFile(String bucketName, String fileName) {
        throw new OSSException("Local Service not support removeFile");
    }

    @Override
    public void removeFiles(List<String> fileNames) {
        throw new OSSException("Local Service not support removeFiles");
    }

    @Override
    public void removeFiles(String bucketName, List<String> fileNames) {
        throw new OSSException("Local Service not support removeFiles");
    }

    @Override
    public String preview(String fileName) {
        throw new OSSException("Local Service not support preview");
    }

    @Override
    public String preview(String fileName, int expiry) {
        throw new OSSException("Local Service not support preview");
    }

    @Override
    public String preview(String bucketName, String fileName) {
        throw new OSSException("Local Service not support preview");
    }

    @Override
    public String preview(String bucketName, String fileName, int expiry) {
        throw new OSSException("Local Service not support preview");
    }
}
