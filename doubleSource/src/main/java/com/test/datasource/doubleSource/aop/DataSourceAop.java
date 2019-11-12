package com.test.datasource.doubleSource.aop;

import com.test.datasource.doubleSource.config.DataSourceType;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 第一种方法
 * 通过AOP让容器当做切面来读取，并根据方法的不同，而设置不同的数据源。
 * 粒度稍大，根据方法来区分。这种方法的缺点是如果有多个不同的方法，那么需要配好多。
 */

/**
 * 因为使用了 DynamicDataSourceAspect （第二种方式），所以将@Aspect和@Component注释掉，这样容器就不会读取了，这个方法也
 * 相当于没有用了。
 */

// 把当前类标识成一个切面供容器读取
//@Aspect
//@Component
public class DataSourceAop {

    // 在主数据源前执行
    @Before("execution(* com.test.datasource.doubleSource.user.userController.UserController.userInfo1(..))")
    public void setDataSource1(){
        System.out.println("数据源111111111");
        DataSourceType.setDataBaseType(DataSourceType.DataBaseType.Primary);
    }

    // 在主数据源前执行
    @Before("execution(* com.test.datasource.doubleSource.user.userController.UserController.userInfo2(..))")
    public void setDataSource2(){
        System.out.println("数据源2222222");
        DataSourceType.setDataBaseType(DataSourceType.DataBaseType.Second);
    }
}
