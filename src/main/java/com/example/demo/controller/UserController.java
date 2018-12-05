package com.example.demo.controller;

import com.example.demo.dao.MysqlAccountRepository;
import com.example.demo.model.MysqlAccount;
import com.example.demo.util.ResultMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile; import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.websocket.server.PathParam;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class UserController {

    @Autowired
    MysqlAccountRepository repository;

//    @PostMapping("/login")
//    public ResultMap getAll(String email, String password, HttpServletResponse response){
//        response.addHeader("Access-Control-Allow-Origin", "*");
//        if(repository.findByEmailAndPassword(email, password) != null){
//            return ResultMap.success(repository.findAll());
//        }else{
//            return ResultMap.error("帐号,密码错误或帐号不存在");
//        }
//    }


    @PostMapping("/login")
    public ResultMap login(String email, String password){
//        response.addHeader("Access-Control-Allow-Origin", "*");
        if(containBean(email,password)){
            return ResultMap.success();
        }else{
            return ResultMap.error("帐号,密码错误或帐号不存在");
        }
    }

    @PostMapping("/register")
    public ResultMap register(String email,
                              String password,
                              String name,
                              String group,
                              String id
                              ){
//        response.addHeader("Access-Control-Allow-Origin", "*");
//        if(! password.equals(confirmPass)){
//            return ResultMap.error("两次密码不一致");
//        }
        if(containBean(email)){
            return ResultMap.error("邮箱已经被注册过了");
        }else{
            MysqlAccount account = new MysqlAccount();
            account.setEmail(email);
            account.setPassword(password);
            account.setName(name);
            account.setGroup(group);
            account.setInTime(new Date());
            account.setId(id);
            account.setAdmin(false);
            repository.save(account);
            return  ResultMap.success();
        }
    }


    @RequestMapping("/all")
    public ResultMap getAll(){
//        response.addHeader("Access-Control-Allow-Origin", "*");
        return ResultMap.success(repository.findAll());
    }

    @RequestMapping("/byname")
    public ResultMap findByname(@PathParam("name")String name){
//        response.addHeader("Access-Control-Allow-Origin", "*");
        MysqlAccount account;
        if((account = repository.findByName(name)) == null){
            return ResultMap.error("未查到名字为" + name + "的用户");
        }else{
            return ResultMap.success(account);
        }
    }

    @RequestMapping("/byemail")
    public ResultMap findByemail(@PathParam("email")String email){
//        response.addHeader("Access-Control-Allow-Origin", "*");
        MysqlAccount account = null;
        if((account = repository.findByEmail(email)) == null){
            return ResultMap.error("未查到邮箱为" + email + "的用户");
        }else{
            return ResultMap.success(account);
        }
    }

    @RequestMapping("/bygroup")
    public ResultMap byGroup(@PathParam("group")String group){
//        response.addHeader("Access-Control-Allow-Origin", "*");
        List<MysqlAccount> accounts;
        if((accounts = repository.findAllByGroup(group)) == null){
            return ResultMap.error("未查到名字为" + group + "的组");
        }else{
            return ResultMap.success(accounts);
        }
    }

    @RequestMapping("/change")
    public ResultMap update(MysqlAccount account){
//        response.addHeader("Access-Control-Allow-Origin", "*");
        System.out.println(account.getEmail() +
                account.getName() +
                account.getAvatar() +
                account.getGroup());
//        account.setEmail(account.getEmail());
        if(containBean(account.getEmail())){
            repository.save(account);
            return ResultMap.success("修改成功");
        }else {
            return ResultMap.error("不存在email为" + account.getEmail() + "的用户");
        }
    }

    @RequestMapping("/upload/avatar")
    public ResultMap editAvatar(
            @RequestParam("file")MultipartFile file
            ){
//        response.addHeader("Access-Control-Allow-Origin", "*");
        if(file.isEmpty()){
            return ResultMap.error("文件为空");
        }
        String path = "/data/avatar/";
        File serverFile = new File(path + file.getOriginalFilename());
        File dir = new File(path);
        System.out.println("开始上传");
        if(! dir.exists()){
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
    public ResultMap delete(String email){
//        response.addHeader("Access-Control-Allow-Origin", "*");
       if(containBean(email)){
           repository.deleteByEmail(email);
           return ResultMap.success(repository.findAll());
       }else {
           return ResultMap.error("没有找到email为" + email + "的用户");
       }
    }

    private boolean containBean(String email){
        return repository.findByEmail(email) != null;
    }

    private boolean containBean(String email, String password){
        return repository.findByEmailAndPassword(email, password) != null;
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
    public ResultMap searchByName(@PathParam("name")String name){
//        response.addHeader("Access-Control-Allow-Origin", "*");
        MysqlAccount account;
        if((account = repository.findByName(name)) == null){
            return ResultMap.error("未查到名字为" + name + "的用户");
        }else{
            return ResultMap.success(account);
        }
    }

    @RequestMapping("/search/email/{email}")
    public ResultMap searchByEmail(@PathParam("email")String email){
//        response.addHeader("Access-Control-Allow-Origin", "*");
        MysqlAccount account = null;
        if((account = repository.findByEmail(email)) == null){
            return ResultMap.error("未查到邮箱为" + email + "的用户");
        }else{
            return ResultMap.success(account);
        }
    }

    @RequestMapping("/search/group/{group}")
    public ResultMap searchByGroup(@PathParam("group")String group){
//        response.addHeader("Access-Control-Allow-Origin", "*");
        List<MysqlAccount> accounts;
        if((accounts = repository.findAllByGroup(group)) == null){
            return ResultMap.error("未查到名字为" + group + "的组");
        }else{
            return ResultMap.success(accounts);
        }
    }

}
