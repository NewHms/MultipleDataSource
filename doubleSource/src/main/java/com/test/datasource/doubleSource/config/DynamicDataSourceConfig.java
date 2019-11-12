package com.test.datasource.doubleSource.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DynamicDataSourceConfig {

    // 将数据源放入spring容器中
    @Bean(name = "PrimaryDataSource")
    // 设置为默认数据源
    @Primary
    // 从配置文件读取前缀为 test1 的数据源
    @ConfigurationProperties(prefix = "spring.datasource.test1")
    public DataSource getDataSource1(){
        return DataSourceBuilder.create().build();
    }

    // 第二个数据源
    @Bean(name = "SecondDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.test2")
    public DataSource getDataSource2(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "dynamicDataSource")
    public DynamicDataSource DataSource(@Qualifier("PrimaryDataSource")DataSource primary,
                                        @Qualifier("SecondDataSource")DataSource second){
        // 很核心的地方，存储这数据源的键值对
        Map<Object,Object> targetDataSource = new HashMap<Object, Object>();
        targetDataSource.put(DataSourceType.DataBaseType.Primary,primary);
        targetDataSource.put(DataSourceType.DataBaseType.Second,second);
        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSource);
        dataSource.setDefaultTargetDataSource(primary); // 设置默认对象
        return dataSource;
    }


    @Bean("SqlSessionFactory")
    public SqlSessionFactory SqlSessionFactory(
            @Qualifier("dynamicDataSource") DataSource dataSource)throws Exception{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().
                getResources("classpath*:mapper/*.xml"));
        return bean.getObject();
    }


}
