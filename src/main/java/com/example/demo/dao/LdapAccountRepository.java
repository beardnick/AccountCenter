package com.example.demo.dao;

import com.example.demo.model.LdapAccount;

import org.springframework.data.repository.CrudRepository;

import javax.naming.Name;

public interface LdapAccountRepository extends CrudRepository<LdapAccount, Long> {
}
