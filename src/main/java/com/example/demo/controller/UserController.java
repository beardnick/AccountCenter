package com.example.demo.controller;

import com.example.demo.dao.MysqlAccountRepository;
import com.example.demo.model.MysqlAccount;
import com.example.demo.util.ResultMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class UserController {

    @Autowired
    MysqlAccountRepository repository;

//    @PostMapping("/login")
//    public ResultMap getAll(String email, String password, HttpServletResponse response){
//        response.addHeader("Access-Control-Allow-Origin", "*");
//        if(repository.findByEmailAndPassword(email, password) != null){
//            return ResultMap.success(repository.findAll());
//        }else{
//            return ResultMap.error("帐号,密码错误或帐号不存在");
//        }
//    }


    @PostMapping("/login")
    public ResultMap login(String email, String password, HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        if(repository.findByEmailAndPassword(email, password) != null){
            return ResultMap.success();
        }else{
            return ResultMap.error("帐号,密码错误或帐号不存在");
        }
    }

    @PostMapping("/register")
    public ResultMap register(String email,
                              String password,
                              String name,
                              String group,
                              HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
//        if(! password.equals(confirmPass)){
//            return ResultMap.error("两次密码不一致");
//        }
        if(repository.findByEmail(email) != null){
            return ResultMap.error("邮箱已经被注册过了");
        }else{
            MysqlAccount account = new MysqlAccount();
            account.setEmail(email);
            account.setPassword(password);
            account.setName(name);
            account.setGroup(group);
            return  ResultMap.success();
        }
    }


    @RequestMapping("/all")
    public ResultMap getAll(HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        return ResultMap.success(repository.findAll());
    }

    @RequestMapping("/byname")
    public ResultMap findByname(@PathParam("name")String name, HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        MysqlAccount account = null;
        if((account = repository.findByName(name)) == null){
            return ResultMap.error("未查到名字为" + name + "的用户");
        }else{
            return ResultMap.success(account);
        }
    }

}
