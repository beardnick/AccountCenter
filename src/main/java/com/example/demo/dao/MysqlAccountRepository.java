package com.example.demo.dao;

import com.example.demo.model.MysqlAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MysqlAccountRepository extends JpaRepository<MysqlAccount,String> {

    MysqlAccount findByEmailAndPassword(String email, String password);

    MysqlAccount findByEmail(String email);

    MysqlAccount findByName(String name);

    List<MysqlAccount> findAllByGroup(String group);
    @Transactional
    String deleteByEmail(String email);
}
