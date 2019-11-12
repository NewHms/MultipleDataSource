package geektime.spring.hello.hellospring.dao.test01;

import geektime.spring.hello.hellospring.bean.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface User1Dao {
    List<User> getUserInfo1();
}
