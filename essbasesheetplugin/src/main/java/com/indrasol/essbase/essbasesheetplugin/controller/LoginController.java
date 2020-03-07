package com.indrasol.essbase.essbasesheetplugin.controller;

import com.indrasol.essbase.essbasesheetplugin.model.Credentials;
import com.indrasol.essbase.essbasesheetplugin.service.LoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/essbase")
public class LoginController {

    private LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }


    @GetMapping("/login")
    public Credentials login() {
        String s_userName = "admin";
        String s_password = "admin123";   
        String s_olapSvrName = "ec2-13-127-165-106.ap-south-1.compute.amazonaws.com";
        String s_provider = "http://ec2-13-127-165-106.ap-south-1.compute.amazonaws.com:28080/aps/JAPI"; 


        Credentials credentials = new Credentials(s_userName, s_password, s_olapSvrName, s_provider);
        loginService.login(credentials);
        System.out.println("login sucessfully...");
        //loginService.loadData();
        
        credentials =  loginService.getEssbaseConnection().getCredentials();

        return credentials;
    } 

    @GetMapping("/logout")
    public String logout() {
        loginService.disconnect();
        return "Logout";
    }

    @GetMapping("/loaddata") 
    public String loadData(){
        return loginService.loadData();
    }

}