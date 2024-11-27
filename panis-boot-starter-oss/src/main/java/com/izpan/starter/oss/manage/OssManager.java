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

package com.izpan.starter.oss.manage;

import com.google.common.collect.Maps;
import com.izpan.starter.oss.config.OssProperties;
import com.izpan.starter.oss.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * OSS 管理器
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.oss.manage.OssManager
 * @CreateTime 2024/11/20 - 23:21
 */

@Slf4j
@Component
public class OssManager {

    /**
     * 服务映射
     */
    private static final Map<String, OssService> serviceMap = Maps.newHashMap();
    
    /**
     * OSS 配置
     */
    private final OssProperties ossProperties;

    @Autowired
    public OssManager(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    /**
     * 获取配置对应的 OSS 服务
     *
     * @return {@link OssService } OSS 服务
     * @author payne.zhuang
     * @CreateTime 2024-11-20 - 22:22:09
     */
    public OssService service() {
        return service(ossProperties.getName());
    }

    /**
     * 获取对应的 OSS 服务
     *
     * @param name OSS 服务名称
     * @return {@link OssService } OSS 服务
     * @author payne.zhuang
     * @CreateTime 2024-11-20 - 23:04:58
     */
    public OssService service(String name) {
        return serviceMap.get(name);
    }

    /**
     * 策略注册方法
     *
     * @param name    OSS 服务名称
     * @param service OSS 服务
     * @author payne.zhuang
     * @CreateTime 2024-11-20 - 22:19:12
     */
    public static void registerService(String name, OssService service) {
        Assert.notNull(name, "注册 OSS 服务名称不能为空");
        serviceMap.putIfAbsent(name, service);
    }
}
