package com.example.batchExample.infrastructure.config;//package com.example.batchExample.infrastructure.config;
//
//
//import javax.sql.DataSource;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.support.JdbcDefaultBatchConfiguration;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
//@Configuration
//@EnableBatchProcessing
//public class BatchInfraConfig extends JdbcDefaultBatchConfiguration {
//
//    private final DataSource dataSource;
//    private final PlatformTransactionManager transactionManager;
//
//    public BatchInfraConfig(DataSource dataSource,
//                            PlatformTransactionManager transactionManager) {
//        this.dataSource = dataSource;
//        this.transactionManager = transactionManager;
//    }
//
//    @Override
//    protected DataSource getDataSource() {
//        return this.dataSource;
//    }
//
//    @Override
//    protected PlatformTransactionManager getTransactionManager() {
//        return this.transactionManager;
//    }
//}
