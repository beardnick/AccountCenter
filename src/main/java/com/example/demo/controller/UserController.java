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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.Name;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class UserController {

    @Autowired
    MysqlAccountRepository repository;

    @Autowired
    LdapTemplate ldapTemplate;

    @Autowired
    LdapAccountRepository ldapAccountRepository;

    Name baseDn = LdapNameBuilder.newInstance("ou=feidian,dc=ldap,dc=52feidian,dc=com").build();

//    @PostMapping("/login")
//    public ResultMap getAll(String email, String password, HttpServletResponse response){
//        response.addHeader("Access-Control-Allow-Origin", "*");
//        if(repository.findByEmailAndPassword(email, password) != null){
//            return ResultMap.success(repository.findAll());
//        }else{
//            return ResultMap.error("帐号,密码错误或帐号不存在");
//        }
//    }

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

    @RequestMapping("/login")
    public ResultMap login(String email, String password) {
//        response.addHeader("Access-Control-Allow-Origin", "*");
        if (containBean(email, password)) {
            return ResultMap.success();
        } else {
            return ResultMap.error("帐号,密码错误或帐号不存在");
        }
    }

    @RequestMapping("/register")
    public ResultMap register(String email,
                              String password,
                              String zh_firstName,
                              String zh_lastName,
                              String group,
                              String id,
                              //拼音
                              String ldapCn
    ) {
//        response.addHeader("Access-Control-Allow-Origin", "*");
//        if(! password.equals(confirmPass)){
//            return ResultMap.error("两次密码不一致");
//        }
        String ldapGroup = selectGroup(group);
        if (ldapGroup == null)
            ResultMap.error("错误的组");
        Name newDn = LdapNameBuilder.newInstance(baseDn).add("ou", ldapGroup).add("cn", ldapCn).build();
        String name = zh_firstName + zh_lastName;
        System.out.println(newDn);
        if (containBean(email)) {
            return ResultMap.error("邮箱已经被注册过了");
        } else if (ldapContainBean(newDn)) {
            return ResultMap.error("该组中已有相同拼音");
        } else {
            //数据库
            MysqlAccount account = new MysqlAccount();
            account.setEmail(email);
            account.setPassword(password);
            account.setName(name);
            account.setGroup(group);
            account.setInTime(new Date());
            account.setId(id);
            account.setAdmin(false);
            repository.save(account);

            //ldap
            LdapAccount ldapAccount = new LdapAccount();
            ldapAccount.setDn(newDn);
            ldapAccount.setMail(email);
            ldapAccount.setCn(ldapCn);
            //中文姓
            ldapAccount.setGivenName(zh_firstName);
            //中文名
            ldapAccount.setSn(zh_lastName);
            ldapAccount.setUserPassword(EncodeByMd5(password));
            ldapTemplate.create(ldapAccount);

            return ResultMap.success();
        }
    }

    @RequestMapping("/all")
    public ResultMap getAll() {
//        response.addHeader("Access-Control-Allow-Origin", "*");
        return ResultMap.success(repository.findAll());
    }

    @RequestMapping("/byname")
    public ResultMap findByname(String name) {
//        response.addHeader("Access-Control-Allow-Origin", "*");
        MysqlAccount account;
        if ((account = repository.findByName(name)) == null) {
            return ResultMap.error("未查到名字为" + name + "的用户");
        } else {
            return ResultMap.success(account);
        }
    }

    @RequestMapping("/byemail")
    public ResultMap findByemail(String email) {
//        response.addHeader("Access-Control-Allow-Origin", "*");
        MysqlAccount account = null;
        if ((account = repository.findByEmail(email)) == null) {
            return ResultMap.error("未查到邮箱为" + email + "的用户");
        } else {
            return ResultMap.success(account);
        }
    }

    @RequestMapping("/bygroup")
    public ResultMap byGroup(String group) {
//        response.addHeader("Access-Control-Allow-Origin", "*");
        List<MysqlAccount> accounts;
        if ((accounts = repository.findAllByGroup(group)) == null) {
            return ResultMap.error("未查到名字为" + group + "的组");
        } else {
            return ResultMap.success(accounts);
        }
    }

    @RequestMapping("/change")
    public ResultMap update(MysqlAccount account) {
//        response.addHeader("Access-Control-Allow-Origin", "*");
        System.out.println(account.getEmail() +
                account.getName() +
                account.getAvatar() +
                account.getGroup());
//        account.setEmail(account.getEmail());
        if (containBean(account.getEmail()) && ldapContainBean(account.getEmail())) {
            LdapAccount ldapAccount = new LdapAccount();
            Name dn = LdapNameBuilder.newInstance(baseDn).add("ou", selectGroup(account.getGroup())).add("cn", account.getFirstName()).build();
            ldapAccount.setDn(dn);
            ldapAccount.setUserPassword(EncodeByMd5(account.getPassword()));
            ldapAccount.setSn(account.getLastName());
            ldapAccount.setMail(account.getEmail());
            try {
                ldapAccountRepository.save(ldapAccount);
            } catch (Exception e) {
                return ResultMap.error("保存失败，检查是否修改英文名。ldap无法修改英文名!");
            }
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
//        response.addHeader("Access-Control-Allow-Origin", "*");
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
        return ResultMap.success("http://123.207.19.172:8080/api/avatar/" + file.getOriginalFilename());
    }

    @RequestMapping("/delete")
    public ResultMap delete(String email) {
//        response.addHeader("Access-Control-Allow-Origin", "*");
        if (containBean(email) && ldapContainBean(email)) {
            repository.deleteByEmail(email);
            ldapAccountRepository.deleteByMail(email);
            return ResultMap.success(repository.findAll());
        } else {
            return ResultMap.error("没有找到email为" + email + "的用户");
        }
    }

    private boolean containBean(String email) {
        return repository.findByEmail(email) != null;
    }

    private boolean containBean(String email, String password) {
        return repository.findByEmailAndPassword(email, password) != null;
    }

    private boolean ldapContainBean(Name dn) {
        return ldapTemplate.findByDn(dn, LdapAccount.class) != null;
    }

    private boolean ldapContainBean(String email) {
        return ldapAccountRepository.findByMail(email) != null;
    }

    //中文组名对应的ldap英文组名
    private String selectGroup(String group) {
        if (group.equals("大前端"))
            return "web";
        else if (group.equals("安卓"))
            return "java";
        else if (group.equals("信息安全"))
            return "security";
        else if (group.equals("ios"))
            return "ios";
        return null;
    }
    //    @RequestMapping("/avatar/{name}")
//    public void getAvatar(@PathVariable("name")String name,
//                          HttpServletResponse response){
//        response.addHeader("Access-Control-Allow-Origin", "*");
//        response.setContentType("image/png");
//        System.out.println(name);
//        String imType = name.substring(name.lastIndexOf('.') + 1);
//        System.out.println("imType:" + imType);
////        response.setContentType("image/" + imType);
//        try {
//            FileInputStream fromServer = new FileInputStream(
//                    new File("/data/avatar/" + name)
//            );
//            OutputStream toClient = response.getOutputStream();
//            byte[] avatar = new byte[fromServer.available()];
//            fromServer.read(avatar);
//            toClient.write(avatar);
//            toClient.flush();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    @RequestMapping("/search/name/{name}")
    public ResultMap searchByName(@PathVariable("name") String name) {
//        response.addHeader("Access-Control-Allow-Origin", "*");
        MysqlAccount account;
        if ((account = repository.findByName(name)) == null) {
            return ResultMap.error("未查到名字为" + name + "的用户");
        } else {
            return ResultMap.success(account);
        }
    }

    @RequestMapping("/search/email/{email}")
    public ResultMap searchByEmail(@PathVariable("email") String email) {
//        response.addHeader("Access-Control-Allow-Origin", "*");
        MysqlAccount account = null;
        if ((account = repository.findByEmail(email)) == null) {
            return ResultMap.error("未查到邮箱为" + email + "的用户");
        } else {
            return ResultMap.success(account);
        }
    }

    @RequestMapping("/search/group/{group}")
    public ResultMap searchByGroup(@PathVariable("group") String group) {
//        response.addHeader("Access-Control-Allow-Origin", "*");
        List<MysqlAccount> accounts;
        if ((accounts = repository.findAllByGroup(group)) == null) {
            return ResultMap.error("未查到名字为" + group + "的组");
        } else {
            return ResultMap.success(accounts);
        }
    }

}
