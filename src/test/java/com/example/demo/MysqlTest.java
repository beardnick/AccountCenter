package com.example.demo;

import com.example.demo.dao.MysqlAccountRepository;
import com.example.demo.model.MysqlAccount;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MysqlTest {

    @Autowired
    MysqlAccountRepository repository;

    @Test
    public void getAll(){
        repository.findAll().forEach(p->
                System.out.println(p));
    }

    @Test
    public void insertOne(){
        MysqlAccount account = new MysqlAccount();
        account.setName("test");
        account.setFirstName("cheng");
        account.setLastName("shikun");
        account.setEmail("1685437606@qq.com");
        account.setPassword("test");
        repository.save(account);
    }

}
