package com.izpan.starter.database.mybatis.plus.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.izpan.starter.database.mybatis.plus.handler.IDataScopeHandler;
import com.izpan.starter.database.mybatis.plus.interceptor.DataScopeInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 全局配置
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.starter.database.mybatis.plus.config.MyBatisPlusConfig
 * @CreateTime 2023/7/6 - 15:11
 */
@Configuration
@AllArgsConstructor
public class MyBatisPlusConfig {

    @Bean
    @ConditionalOnBean(IDataScopeHandler.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor(IDataScopeHandler dataScopeHandler) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 数据权限
        interceptor.addInnerInterceptor(new DataScopeInterceptor(dataScopeHandler));
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 防止全表更新与删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }
}
