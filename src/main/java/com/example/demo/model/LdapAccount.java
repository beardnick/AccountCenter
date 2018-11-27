package com.example.demo.model;


import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

@Entry(objectClasses ="inetOrgPerson" , base = "ou=feidian, dc=ldap, dc=52feidian, dc=com")
public class LdapAccount {

    @Id
    private Long id;

    @Attribute(name="cn")
    private String name;

    @Attribute(name="mail")
    private String mail;

    @Attribute(name="givenName")
    private String firstName;

    @Attribute(name = "sn")
    private String lastName;

    @Attribute(name = "userPassword")
    private String password;

}
