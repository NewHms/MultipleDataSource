package geektime.spring.hello.config;

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
