package geektime.spring.hello.hellospring.controller;

import geektime.spring.hello.hellospring.bean.User;
import geektime.spring.hello.hellospring.service.UserServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/userInfo")
public class UserController {

    @Autowired
    private UserServcie userServcie;

    @RequestMapping("/info1")
    public String userInfo1(){
        List<User> user = userServcie.getUserInfo1();
        return user.get(0).getName()+" "+user.get(0).getAge()+" "+user.get(0).getSex();
    }

    @RequestMapping("/info2")
    public String userInfo2(){
        List<User> user = userServcie.getUserInfo2();
        return user.get(0).getName()+" "+user.get(0).getAge()+" "+user.get(0).getSex();
    }
}
