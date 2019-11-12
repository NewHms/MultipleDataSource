package com.test.datasource.doubleSource.user.dao;

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
