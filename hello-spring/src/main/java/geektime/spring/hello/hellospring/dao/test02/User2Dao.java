package geektime.spring.hello.hellospring.dao.test02;

import geektime.spring.hello.hellospring.bean.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface User2Dao {
    List<User> getUserInfo2();
}
