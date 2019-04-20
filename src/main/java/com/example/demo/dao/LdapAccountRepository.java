package com.example.demo.dao;

import com.example.demo.model.LdapAccount;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LdapAccountRepository extends CrudRepository<LdapAccount, Long> {
    public List<LdapAccount> findByMail(String mail);
    @Transactional
    public List<LdapAccount> deleteByMail(String mail);
}
