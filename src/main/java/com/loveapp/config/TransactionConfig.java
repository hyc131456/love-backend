package com.loveapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 事务配置类
 * 开启声明式事务管理，在Service方法上使用@Transactional注解即可
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfig {
    
    /**
     * 配置事务管理器
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        // 设置事务超时时间（秒）
        transactionManager.setDefaultTimeout(30);
        return transactionManager;
    }
}
