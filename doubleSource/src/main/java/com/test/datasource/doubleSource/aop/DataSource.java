package com.test.datasource.doubleSource.aop;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    // 默认主数据源
    String value() default "PrimaryDataSource";
}
