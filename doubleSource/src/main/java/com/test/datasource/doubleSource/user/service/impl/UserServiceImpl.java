package com.test.datasource.doubleSource.user.service.impl;

import com.test.datasource.doubleSource.user.bean.User;
import com.test.datasource.doubleSource.user.dao.User1Dao;
import com.test.datasource.doubleSource.user.service.UserServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserServcie {

    @Autowired
    private User1Dao user1Mapper;


    @Override
    public List<User> getUserInfo1() {
        return user1Mapper.getUserInfo1();
    }

    @Override
    public List<User> getUserInfo2() {
        return user1Mapper.getUserInfo2();
    }
}
