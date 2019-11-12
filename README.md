# MultipleDataSource
springboot多数据源配置的两种方式，配置文件配置和AOP配置



### 多数据源练习


这篇文章记录一下配置多数据源的步骤，也是踩了不少坑，在此记录一下。
首先看下application.properties,在配置文件中，我们配置了两个数据源，用test1和test2来区分。然后指定一下端口号，非常简单的配置。

```properties
server.port=9090

spring.datasource.test1.jdbc-url=jdbc:mysql://xx.xx.xx.xx:3306/multipledatasource1?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
spring.datasource.test1.username=root
spring.datasource.test1.password=root
spring.datasource.test1.driver-class-name=com.mysql.jdbc.Driver


spring.datasource.test2.jdbc-url=jdbc:mysql://xx.xx.xx.xx:3306/multipledatasource2?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
spring.datasource.test2.username=root
spring.datasource.test2.password=root
spring.datasource.test2.driver-class-name=com.mysql.jdbc.Driver

```


#### 1、分包方式 通过配置多个DataSource来指定多数据源。

这种方法比较简单，取消掉spring的自动注入数据源，让spring加载我们自己手写的数据源配置，那么就可以达到多数据源的效果了（因为我配置了两个数据源，所以有两个配置文件）。通过配置文件配置的SQLSessionFactory加载mapper路径不一样，就走不一样的数据源。
在看代码之前先看下目录结构
[![image]](https://github.com/NewHms/MultipleDataSource/blob/master/images/structure-1.jpg)

这里要注意下，mapper是与java文件放到了一起。没放到resources下。所以要在pom文件中加一点配置。在<build>下要加一点配置。这样就可以访问到mapper了，如果这样的目录结构没有配置<build> 那么在启动时就会报错。
```xml
<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>

		<resources>
			<resource>
				<directory>src/main/java</directory>
				<excludes>
					<exclude>**/*.java</exclude>
				</excludes>
			</resource>
			<resource>
				<directory>src/main/resources</directory>
				<includes>
					<include>**/*.*</include>
				</includes>
			</resource>
		</resources>
	</build>
```


下面来看下启动类。

```java

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@RestController

public class HelloSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloSpringApplication.class, args);
	}
}

```

很简单的启动类，不同的是需要在 @SpringBootApplication 注解中取消掉自动配置数据源 DataSourceAutoConfiguration.class（这个类就是自动配置数据源的类。）

我们再来看下配置DataSource的代码

**配置第一个数据源**
```java
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

// 表示这是一个配置类
@Configuration
// 指定该数据源对应的sql文件夹路径。
@MapperScan(basePackages = "geektime.spring.hello.hellospring.dao.test01",
        sqlSessionFactoryRef = DataSourceConfig1.SQL_SESSION_FACTORY_1)
public class DataSourceConfig1 {

    public static final String SQL_SESSION_FACTORY_1 = "test1SelSessionFactory";

    // 将该对象放入spring 容器中
    @Bean(name = "test1DataSource")
    // 表示这是默认的数据源
    @Primary
    // 读取application.properties中的参数映射成一个对象
    // prefix 为前缀
    @ConfigurationProperties(prefix = "spring.datasource.test1")
    public DataSource getDataSource1(){
        return DataSourceBuilder.create().build();
    }


    @Bean(name = DataSourceConfig1.SQL_SESSION_FACTORY_1)
    // 默认数据源
    @Primary
    // @Qualifier 表示从容器中查找名字为 test1DataSource 的对象

    public SqlSessionFactory test1SelSessionFactory(@Qualifier("test1DataSource") DataSource dataSource)
            throws Exception{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().
                getResources("classpath*:geektime/spring/hello/hellospring/mapper/test01/*.xml"));

        return bean.getObject();
    }


    @Bean(name = "test1SqlSessionTemplate")
    @Primary
    public SqlSessionTemplate test1SqlSessionTemplate(
            @Qualifier("test1SelSessionFactory")SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

```

**第二个数据源配置**
```java
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "geektime.spring.hello.hellospring.dao.test02",
        sqlSessionFactoryRef = DataSourceConfig2.SQL_SESSION_FACTORY_2)
public class DataSourceConfig2 {

    public static final String SQL_SESSION_FACTORY_2 = "test2SqlSessionFactory";

    @Bean(name = "test2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.test2")
    public DataSource getDataSource2(){
        return DataSourceBuilder.create().build();
    }

    @Bean(DataSourceConfig2.SQL_SESSION_FACTORY_2)
    public SqlSessionFactory test2SqlSessionFactory(@Qualifier("test2DataSource") DataSource dataSource)
            throws Exception{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().
                getResources("classpath*:geektime/spring/hello/hellospring/mapper/test02/*.xml"));
        return bean.getObject();
    }


    @Bean("test2SqlSessionTemplate")
    public SqlSessionTemplate test2SqlSessionTemplate(
            @Qualifier("test2SqlSessionFactory")SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
```

需要注意的是在写 SqlSessionFactory 的时候，需要配置下setMapperLocations，这里指定的就是xml文件的地址，指定了这里，SQLSessionFactory就知道从哪读取xml文件了。

我在这里还遇到了一个问题，并且困扰了我两天，之前参考网上的案例，他们的@MapperScan指定的都是mapper路径，这样就导致了我第一个数据源好用，但是第二个数据源报 
> Invalid bound statement (not found) 异常。

然后我修改成了指向dao，就好了。真的是苦大坑深。。。。

那么当mapper在resources下，需要修改下SQLSessionFactory的配置。
目录结构
[![image]](https://github.com/NewHms/MultipleDataSource/blob/master/images/structure-2.jpg)

只需要修改一下SqlSessionFactory指定的路径（用DataSource1来举例），就可以了。
> bean.setMapperLocations(new PathMatchingResourcePatternResolver().
                getResources("classpath*:/mapper1/*.xml"));
                
> 参考 https://blog.csdn.net/tuesdayma/article/details/81081666           


#### 2 AOP方式

AOP方式有两种，一种是指定具体的controller方法（或者controller类）来区分哪些方法使用什么数据源。另一种是通过注解的方式，在dao层进行区分，这种方式粒度是最细的。
以下的例子我将mapper的xml放在了resource下，所以配置的SQLSessionFactory为
> classpath*:mapper/*.xml

并且配置文件和第一种方式是一样的，就不贴了。

首先是数据源配置类。
```java
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

```
核心的就是DynamicDataSource，这个类是自定义的动态切换数据源的类，AbstractRoutingDataSource类内部维护了一个名为targetDataSources的Map，并提供了setter方法用于设置数据源关键字与数据源的关系，实现类被要求实现其determineCurrentLookupKey方法，由此方法的返回值来决定具体是从哪个数据源中获取的连接，同时，AbstractRoutingDataSource类提供了程序运行时动态切换数据源的方法，在dao类或方法上标注需要访问的数据源的关键字，就可以切换数据源。

```java
package com.test.datasource.doubleSource.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType.DataBaseType dataBaseType = DataSourceType.getDataBaseType();
        return dataBaseType;
    }
}

```
DataSourceType 具体代码
```java

public class DataSourceType {

    // 内部枚举类，用于选择特定的数据类型
    public enum DataBaseType{
        Primary, Second
    }

    // 使用ThreadLocal来保证线程安全
    private static final ThreadLocal<DataBaseType> TYPE = new ThreadLocal<DataBaseType>();

    // 往当前线程里设置数据源类型
    public static void setDataBaseType(DataBaseType dataBaseType){
        if(dataBaseType == null){
            throw new NullPointerException();
        }

        TYPE.set(dataBaseType);
    }

    // 获取数据源类型
    public static DataBaseType getDataBaseType(){
        DataBaseType dataBaseType = TYPE.get() == null ? DataBaseType.Primary : TYPE.get();
        return dataBaseType;
    }


    // 清空数据源
    public static void cleanDataBaseType(){
        TYPE.remove();
    }

}

```

那么现在看下第一种方式的代码,指定了controller中具体的方法，通过@Before注解来区分走哪个数据源。
```java

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

```

再看下第二种方式，通过自定义注解，来区分数据源。
```java

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

```

注解代码
```java
import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    // 默认主数据源
    String value() default "PrimaryDataSource";
}

```

特别的再贴下dao层代码。这是使用了第二种方式的dao，如果使用第一种方式，去掉@DataSource就可以了。

```java
import com.test.datasource.doubleSource.aop.DataSource;
import com.test.datasource.doubleSource.user.bean.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface User1Dao {
    @DataSource
    List<User> getUserInfo1();

    @DataSource("SecondDataSource")
    List<User> getUserInfo2();
}
```


通过这几种方式发现，还是通过注解+AOP的形式粒度最小，对未来开发也改动最小，属于较好的方案。
