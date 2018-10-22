package com.example.demo.dao;

import com.example.demo.model.MysqlAccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MysqlAccountRepository extends JpaRepository<MysqlAccount,String> {

    public MysqlAccount findByEmailAndPassword(String email, String password);
    public MysqlAccount findByEmail(String email);
    public MysqlAccount findByName(String name);
    public List<MysqlAccount> findAllByGroup(String group);
    @Transactional
    public String deleteByEmail(String email);
//    public MysqlAccount findByEmail(String email);
}
