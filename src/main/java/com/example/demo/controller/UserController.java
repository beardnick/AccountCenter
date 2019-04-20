package com.example.demo.controller;

import com.example.demo.dao.LdapAccountRepository;
import com.example.demo.dao.MysqlAccountRepository;
import com.example.demo.model.LdapAccount;
import com.example.demo.model.MysqlAccount;
import com.example.demo.util.ResultMap;
import com.unboundid.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.Name;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    MysqlAccountRepository repository;

    @Autowired
    LdapTemplate ldapTemplate;

    @Autowired
    LdapAccountRepository ldapAccountRepository;

    Name baseDn = LdapNameBuilder.newInstance("ou=feidian,dc=ldap,dc=52feidian,dc=com").build();

    //ldap:MD5加密
    private static String EncodeByMd5(String s) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(s.getBytes());
            byte[] bs = md5.digest();
            String base64MD5Password = Base64.encode(bs);
            return "{md5}" + base64MD5Password;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/login")
    public ResultMap login(String email, String password) {
//        response.addHeader("Access-Control-Allow-Origin", "*");
        if (containBean(email, password)) {
            return ResultMap.success();
        } else {
            return ResultMap.error("帐号,密码错误或帐号不存在");
        }
    }

    @PostMapping("/register")
    public ResultMap register(String email,
                              String password,
                              String firstName,
                              String lastName,
                              String group,
                              String id,
                              //拼音
                              String spell
    ) {
        String ldapGroup = selectGroup(group);
        Name newDn = LdapNameBuilder.newInstance(baseDn).add("ou", ldapGroup).add("cn", spell).build();
        String name = firstName + lastName;
        if (containBean(email)) {
            return ResultMap.error("邮箱已经被注册过了");
        } else if (LdapContainBean(email, spell)) {
            return ResultMap.error("该组中可能已有相同拼音");
        } else {
            //注册数据库
            MysqlAccount account = new MysqlAccount();
            account.setEmail(email);
            account.setPassword(password);
            account.setName(name);
            account.setGroup(group);
            account.setInTime(new Date());
            account.setId(id);
            account.setAdmin(false);
            account.setFirstName(firstName);
            account.setLastName(lastName);
            account.setSpell(spell);

            //ldap
            LdapAccount ldapAccount = new LdapAccount();
            ldapAccount.setDn(newDn);
            ldapAccount.setMail(email);
            ldapAccount.setCn(spell);
            //中文姓
            ldapAccount.setGivenName(firstName);
            //中文名
            ldapAccount.setSn(lastName);
            ldapAccount.setUserPassword(EncodeByMd5(password));
            try {
                ldapTemplate.create(ldapAccount);
                repository.save(account);
            } catch (Exception e) {
                return ResultMap.error("注册失败！");
            }
            return ResultMap.success();
        }
    }

    @RequestMapping("/all")
    public ResultMap getAll() {
        return ResultMap.success(repository.findAll());
    }

    @RequestMapping("/byname")
    public ResultMap findByname(String name) {
        MysqlAccount account;
        if ((account = repository.findByName(name)) == null) {
            return ResultMap.error("未查到名字为" + name + "的用户");
        } else {
            return ResultMap.success(account);
        }
    }

    @RequestMapping("/byemail")
    public ResultMap findByemail(String email) {
        MysqlAccount account = null;
        if ((account = repository.findByEmail(email)) == null) {
            return ResultMap.error("未查到邮箱为" + email + "的用户");
        } else {
            return ResultMap.success(account);
        }
    }

    @RequestMapping("/bygroup")
    public ResultMap byGroup(String group) {
        List<MysqlAccount> accounts;
        if ((accounts = repository.findAllByGroup(group)) == null) {
            return ResultMap.error("未查到名字为" + group + "的组");
        } else {
            return ResultMap.success(accounts);
        }
    }

    @RequestMapping("/change")
    public ResultMap update(MysqlAccount account) {
        if (containBean(account.getEmail())) {
            LdapAccount ldapAccount = new LdapAccount();
            Name dn = LdapNameBuilder.newInstance(baseDn).add("ou", selectGroup(account.getGroup())).add("cn", account.getSpell()).build();
            ldapAccount.setDn(dn);
            ldapAccount.setUserPassword(EncodeByMd5(account.getPassword()));
            ldapAccount.setGivenName(account.getFirstName());
            ldapAccount.setCn(account.getSpell());
            ldapAccount.setSn(account.getLastName());
            ldapAccount.setMail(account.getEmail());
            //修改ldap部分
            ldapAccountRepository.save(ldapAccount);
            //修改数据库部分
            account.setName(account.getFirstName() + account.getLastName());
            repository.save(account);

            return ResultMap.success("修改成功");
        } else {
            return ResultMap.error("不存在email为" + account.getEmail() + "的用户");
        }
    }

    @RequestMapping("/upload/avatar")
    public ResultMap editAvatar(
            @RequestParam("file") MultipartFile file
    ) {
        if (file.isEmpty()) {
            return ResultMap.error("文件为空");
        }
        String path = "/data/avatar/";
        File serverFile = new File(path + file.getOriginalFilename());
        File dir = new File(path);
        System.out.println("开始上传");
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            file.transferTo(serverFile);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultMap.error("上传失败");
        }
//        return ResultMap.success("http://api.52feidian.com/api/avatar/" + file.getOriginalFilename());
        return ResultMap.success("http://192.168.3.96:8080/api/avatar/" + file.getOriginalFilename());
    }

    /*
    @Transactional
    @RequestMapping("/delete")
    public ResultMap delete(String email, String spell) {
        if (containBean(email) && LdapContainBean(email, spell)) {
            ldapAccountRepository.deleteByMailAndCn(email, spell);
            if (!LdapContainBean(email,spell)){
                repository.deleteByEmail(email);
                ldapAccountRepository.deleteByMailAndCn(email, spell);
                return ResultMap.success("删除成功！");
            } else return ResultMap.error("删除失败");
        } else {
            return ResultMap.error("没有找到email为" + email + "的用户或者Cn为" + spell + "的用户");
        }
    }
     */

    private boolean containBean(String email) {
        return repository.findByEmail(email) != null;
    }

    private boolean containBean(String email, String password) {
        return repository.findByEmailAndPassword(email, password) != null;
    }

    private boolean LdapContainBean(String email, String spell) {
        return !ldapAccountRepository.findByMailAndCn(email, spell).isEmpty();
    }

    //中文组名对应的ldap英文组名
    private String selectGroup(String group) {
        if (group.equals("大前端"))
            return "web";
        else if (group.equals("信息安全"))
            return "security";
        else return group;
    }

    @RequestMapping("/search/name/{name}")
    public ResultMap searchByName(@PathVariable("name") String name) {
        MysqlAccount account;
        if ((account = repository.findByName(name)) == null) {
            return ResultMap.error("未查到名字为" + name + "的用户");
        } else {
            return ResultMap.success(account);
        }
    }

    @RequestMapping("/search/email/{email}")
    public ResultMap searchByEmail(@PathVariable("email") String email) {
        MysqlAccount account = null;
        if ((account = repository.findByEmail(email)) == null) {
            return ResultMap.error("未查到邮箱为" + email + "的用户");
        } else {
            return ResultMap.success(account);
        }
    }

    @RequestMapping("/search/group/{group}")
    public ResultMap searchByGroup(@PathVariable("group") String group) {
        List<MysqlAccount> accounts;
        if ((accounts = repository.findAllByGroup(group)) == null) {
            return ResultMap.error("未查到名字为" + group + "的组");
        } else {
            return ResultMap.success(accounts);
        }
    }
}
