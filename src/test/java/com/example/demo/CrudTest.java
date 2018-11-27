package com.example.demo;

import com.example.demo.dao.LdapAccountRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrudTest {

    @Autowired
    private LdapAccountRepository repository;

    @Test
    public void findAll(){
       repository.findAll().forEach(p->{
           System.out.println(p);
       });
    }
}
