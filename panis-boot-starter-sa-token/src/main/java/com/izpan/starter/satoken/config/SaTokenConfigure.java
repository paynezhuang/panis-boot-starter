package com.izpan.starter.satoken.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Sa Token 全局配置
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.satoken.config.SaTokenConfigure
 * @CreateTime 2024/4/19 - 10:14
 */

@Configuration
public class SaTokenConfigure {

    /**
     * Sa-Token 参数配置，参考文档：<a href="https://sa-token.cc">https://sa-token.cc</a> <br/>
     * 此配置会覆盖 application.yml 中的配置
     */
    @Bean
    @Primary
    public SaTokenConfig getSaTokenConfigPrimary() {
        return new SaTokenConfig()
                // jwt秘钥
                .setJwtSecretKey("GtztRxC5JUpd5bfAibJuTKGJDVZaRfBR")
                // token 前缀
                .setTokenPrefix("Bearer")
                // token 名称（同时也是 cookie 名称）
                .setTokenName("Authorization")
                // token 风格 https://sa-token.cc/doc.html#/up/token-style
                .setTokenStyle("random-32")
                // token 有效期（单位：秒），默认1天，-1代表永不过期
                .setTimeout(86400)
                // token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
                .setActiveTimeout(-1)
                // 是否允许同一账号多地同时登录（为 true 时允许一起登录，为 false 时新登录挤掉旧登录）
                .setIsConcurrent(true)
                // 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token，为 false 时每次登录新建一个 token）
                // Simple 模式，此项无效
                .setIsShare(true)
                // 是否尝试从 cookie 里读取 Token，此值为 false 后，StpUtil.login(id) 登录时也不会再往前端注入Cookie
                .setIsReadCookie(false)
                // 是否在初始化配置时打印版本字符画
                .setIsPrint(false)
                // 是否输出操作日志
                .setIsLog(true);
    }

    /**
     * Sa-Token 整合 jwt (Simple 简单模式) <br/>
     * <a href="https://sa-token.cc/doc.html#/plugin/jwt-extend">https://sa-token.cc/doc.html#/plugin/jwt-extend</a>
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}
