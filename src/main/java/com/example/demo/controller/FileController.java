package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api")
public class FileController {

        @RequestMapping("/avatar/{name:.+}")
        public void getAvatar(@PathVariable("name")String name,
                              HttpServletResponse response){
                response.addHeader("Access-Control-Allow-Origin", "*");
//                response.setContentType("image/png");
                System.out.println(name);
                String imType = name.substring(name.lastIndexOf('.') + 1);
                System.out.println("imType:" + imType);
        response.setContentType("image/" + imType);
                try {
                        FileInputStream fromServer = new FileInputStream(
                                new File("/data/avatar/" + name)
                        );
                        OutputStream toClient = response.getOutputStream();
                        byte[] avatar = new byte[fromServer.available()];
                        fromServer.read(avatar);
                        toClient.write(avatar);
                        toClient.flush();
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
}
