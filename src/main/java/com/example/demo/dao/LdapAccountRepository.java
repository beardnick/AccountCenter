package com.example.demo.dao;

import com.example.demo.model.LdapAccount;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LdapAccountRepository extends CrudRepository<LdapAccount, Long> {
    List<LdapAccount> findByMailAndCn(String mail, String cn);

    LdapAccount deleteByMailAndCn(String mail, String cn);
}
