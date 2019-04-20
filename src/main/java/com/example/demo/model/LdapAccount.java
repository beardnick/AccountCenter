package com.example.demo.model;


import org.springframework.ldap.odm.annotations.*;

import javax.naming.Name;

@Entry(objectClasses = {"inetOrgPerson", "top"}, base = "ou=feidian, dc=ldap, dc=52feidian, dc=com")
public class LdapAccount {

    @Id
    private Name dn;

    @Attribute(name = "cn")
    private String cn;

    @Attribute(name = "sn")
    private String sn;

    @Attribute(name = "mail")
    private String mail;

    @Attribute(name = "givenName")
    private String givenName;

    @Attribute(name = "userPassword")
    private String userPassword;

    public Name getDn() {
        return dn;
    }

    public void setDn(Name dn) {
        this.dn = dn;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String toString() {
        return "LdapAccount{" +
                "dn=" + dn +
                ", cn='" + cn + '\'' +
                ", sn='" + sn + '\'' +
                ", mail='" + mail + '\'' +
                ", givenName='" + givenName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                '}';
    }
}
