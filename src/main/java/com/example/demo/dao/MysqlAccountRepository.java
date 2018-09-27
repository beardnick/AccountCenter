package com.example.demo.dao;

import com.example.demo.model.MysqlAccount;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MysqlAccountRepository extends JpaRepository<MysqlAccount,Long > {

    public MysqlAccount findByEmailAndPassword(String email, String password);
    public MysqlAccount findByEmail(String email);
    public MysqlAccount findByName(String name);
}
