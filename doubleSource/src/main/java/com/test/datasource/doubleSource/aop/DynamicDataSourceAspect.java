package com.test.datasource.doubleSource.aop;

import com.test.datasource.doubleSource.config.DataSourceType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 第二种方法
 * 通过注解方式来指定不同的数据源
 * 需要dao层接口上配置注解，并指定数据源，如果未默认则不指定。
 * 这种做法是粒度最小的方法。配置好了后就只需要根据注解来区分数据源。
 */

@Aspect
@Component
public class DynamicDataSourceAspect {

    @Before("@annotation(dataSource)")
    public void changeDataSource(DataSource dataSource)throws Exception{
        String value = dataSource.value();
        if("SecondDataSource".equals(value)){
            DataSourceType.setDataBaseType(DataSourceType.DataBaseType.Second);
        }else {
            // 默认使用主数据源
            DataSourceType.setDataBaseType(DataSourceType.DataBaseType.Primary);
        }
    }

    @After("@annotation(dataSource)")
    public void restoreDataSource(DataSource dataSource){
        DataSourceType.cleanDataBaseType();
    }
}
