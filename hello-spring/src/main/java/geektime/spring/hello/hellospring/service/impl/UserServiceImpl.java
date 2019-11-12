package geektime.spring.hello.hellospring.service.impl;

import geektime.spring.hello.hellospring.bean.User;
import geektime.spring.hello.hellospring.dao.test01.User1Dao;
import geektime.spring.hello.hellospring.dao.test02.User2Dao;
import geektime.spring.hello.hellospring.service.UserServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserServcie {

    @Autowired
    private User1Dao user1Mapper;

    @Autowired
    private User2Dao user2Mapper;


    @Override
    public List<User> getUserInfo1() {
        return user1Mapper.getUserInfo1();
    }

    @Override
    public List<User> getUserInfo2() {
        return user2Mapper.getUserInfo2();
    }
}
