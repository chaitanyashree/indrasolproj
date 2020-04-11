package com.indrasol.essbase.essbasesheetplugin.controller;

import java.util.List;
import java.util.Map;

import com.essbase.api.metadata.IEssDimension;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.indrasol.essbase.essbasesheetplugin.model.*;
import com.indrasol.essbase.essbasesheetplugin.service.LoginService;
import com.indrasol.essbase.essbasesheetplugin.util.EssbaseHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/essbase")


public class LoginController {

    private LoginService loginService;

    @Value("${essbase.userName}")
    private String s_userName;

    @Value("${essbase.password}")
    private String s_password;

    @Value("${essbase.s_olapSvrName}")
    private String s_olapSvrName;

    @Value("${essbase.s_provider}")
    private String s_provider;

    @Value("${essbase.plugin.version}")
    private String sVersion;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }


    @GetMapping("/hello")
    public String sayHello() {
        return "Hello Essbase plugin version: "+sVersion;
    }

    @GetMapping("/testlogin")
    public Credentials testLogin() {

        //String s_userName = "admin";
        //String s_password = "admin123";   
        //String s_olapSvrName = "ec2-13-127-165-106.ap-south-1.compute.amazonaws.com";
        //String s_provider = "http://ec2-13-127-165-106.ap-south-1.compute.amazonaws.com:28080/aps/JAPI"; 

        //String s_olapSvrName = "ec2-3-7-12-73.ap-south-1.compute.amazonaws.com";
        //String s_provider = "http://ec2-3-7-12-73.ap-south-1.compute.amazonaws.com:28080/aps/JAPI"; 


        Credentials credentials = new Credentials(this.s_userName, this.s_password, this.s_olapSvrName, this.s_provider);
        loginService.login(credentials);
        System.out.println("login sucessfully...");
        //loginService.loadData();

        credentials = loginService.getEssbaseConnection().getCredentials();

        return credentials;
    }

    @PostMapping("/login")
    //@ResponseStatus(HttpStatus.CREATED)
    public Credentials login(@RequestBody Credentials credentials) {
        Credentials essbaseCredentials = new Credentials();
        essbaseCredentials.setOlapServerName(credentials.getOlapServerName());
        essbaseCredentials.setPassword(credentials.getPassword());
        essbaseCredentials.setUrl(credentials.getUrl());
        essbaseCredentials.setUserName(credentials.getUserName());
        loginService.login(essbaseCredentials);
        System.out.println("LOGIN TO ESSBASE SUCCESSFUL..." + essbaseCredentials.getUserName());
        essbaseCredentials = loginService.getEssbaseConnection().getCredentials();

        return essbaseCredentials;

    }


    @GetMapping("/logout")
    public String logout() {
        loginService.disconnect();
        return "Logout";
    }

    @GetMapping("/loaddata")
    public List<IEssDimension> loadData() {
        return loginService.loadData();
    }

    @GetMapping("/applications")
    public List<EApplication> getAllApplications() {
        return loginService.getAllApplications();
    }

    @GetMapping("/applications/{applicationName}/{cubeName}/dimensions")
    public List<Dimension> getAllDimensions(@PathVariable("applicationName") String applicationName, @PathVariable("cubeName") String cubeName) {
        return loginService.getAllDimensions(applicationName,cubeName);

    }

    @GetMapping("/applications/{applicationName}/{cubeName}/defaultGrid")
    public DataGrid getDefaultGrid(@PathVariable("applicationName") String applicationName, @PathVariable("cubeName") String cubeName) {
        return loginService.getDefaultGrid(applicationName,cubeName);
    }

    @PostMapping("/applications/{applicationName}/{cubeName}/zoomIn/{startRow}/{startColumn}")
    public DataGrid getZoomInOperation(@RequestBody DataGrid dataGrid, @PathVariable("applicationName") String applicationName,
                                       @PathVariable("cubeName") String cubeName,
                                       @PathVariable("startRow") Integer startRow,
                                       @PathVariable("startColumn") Integer startColumn) {
        return loginService.getZoomInOperation(applicationName,cubeName,dataGrid,startRow,startColumn, LoginService.NEXT_LEVEL);
    }

    @PostMapping("/applications/{applicationName}/{cubeName}/zoomInBottom/{startRow}/{startColumn}")
    public DataGrid getZoomInOperationBottom(@RequestBody DataGrid dataGrid, @PathVariable("applicationName") String applicationName,
                                       @PathVariable("cubeName") String cubeName,
                                       @PathVariable("startRow") Integer startRow,
                                       @PathVariable("startColumn") Integer startColumn) {
        return loginService.getZoomInOperation(applicationName,cubeName,dataGrid,startRow,startColumn, LoginService.BOTTOM_LEVEL);
    }

    @PostMapping("/applications/{applicationName}/{cubeName}/zoomInAll/{startRow}/{startColumn}")
    public DataGrid getZoomInOperationAll(@RequestBody DataGrid dataGrid, @PathVariable("applicationName") String applicationName,
                                       @PathVariable("cubeName") String cubeName,
                                       @PathVariable("startRow") Integer startRow,
                                       @PathVariable("startColumn") Integer startColumn) {
        return loginService.getZoomInOperation(applicationName,cubeName,dataGrid,startRow,startColumn, LoginService.ALL_LEVEL);
    }

    @PostMapping("/applications/{applicationName}/{cubeName}/zoomOut/{startRow}/{startColumn}")
    public DataGrid getZoomOutOperation(@RequestBody DataGrid dataGrid, @PathVariable("applicationName") String applicationName,
                                       @PathVariable("cubeName") String cubeName,
                                       @PathVariable("startRow") Integer startRow,
                                       @PathVariable("startColumn") Integer startColumn) {
        return loginService.getZoomOutOperation(applicationName,cubeName,dataGrid,startRow,startColumn);
    }
    @GetMapping("/dimensions/{dimensionName}/members")
    public List<EMembers> getAllMembersForDimensions(@PathVariable("dimensionName") String dimName) {
        return loginService.getAllMembersForDimensions(dimName);
    }

    @PostMapping("/applications/{applicationName}/{cubeName}/refresh")
    public DataGrid getRefreshOperation(@RequestBody DataGrid dataGrid, @PathVariable("applicationName") String applicationName,
                                       @PathVariable("cubeName") String cubeName) {
        return loginService.getRefreshOperation(applicationName,cubeName,dataGrid);
    }

    @PostMapping("/applications/{applicationName}/{cubeName}/keepOnly")
    public DataGrid getKeepOnlyOperation(@RequestBody DataGrid dataGrid, @PathVariable("applicationName") String applicationName,
                                       @PathVariable("cubeName") String cubeName) {
        return loginService.getKeepOnlyOperation(applicationName,cubeName,dataGrid);
    }

    // @PostMapping("/applications/{applicationName}/{cubeName}/keepOnly/{startRow}/{startColumn}/{countRows}/{countColumns}")
    // public DataGrid getKeepOnlyRangeOperation(@RequestBody DataGrid dataGrid, @PathVariable("applicationName") String applicationName,
    //                                    @PathVariable("cubeName") String cubeName,
    //                                    @PathVariable("startRow") Integer startRow,
    //                                    @PathVariable("startColumn") Integer startColumn,
    //                                    @PathVariable("countRows") Integer countRows,
    //                                    @PathVariable("countColumns") Integer countColumns) {
    //     return loginService.getKeepOnlyOperation(applicationName,cubeName,dataGrid,startRow,startColumn,countRows,countColumns);
    // }    
    

    @PostMapping("/applications/{applicationName}/{cubeName}/removeOnly")
    public DataGrid getRemoveOnlyOperation(@RequestBody DataGrid dataGrid, @PathVariable("applicationName") String applicationName,
                                       @PathVariable("cubeName") String cubeName) {
        return loginService.getRemoveOnlyOperation(applicationName,cubeName,dataGrid);
    }    

    // @PostMapping("/applications/{applicationName}/{cubeName}/removeOnly/{startRow}/{startColumn}/{countRows}/{countColumns}")
    // public DataGrid getRemoveOnlyRangeOperation(@RequestBody DataGrid dataGrid, @PathVariable("applicationName") String applicationName,
    //                                    @PathVariable("cubeName") String cubeName,
    //                                    @PathVariable("startRow") Integer startRow,
    //                                    @PathVariable("startColumn") Integer startColumn,
    //                                    @PathVariable("countRows") Integer countRows,
    //                                    @PathVariable("countColumns") Integer countColumns) {
    //     return loginService.getRemoveOnlyOperation(applicationName,cubeName,dataGrid,startRow,startColumn,countRows,countColumns);
    // }     
}