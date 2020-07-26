package com.example.controller;

import com.example.service.MainService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController{

    @Autowired
    MainService mainService;

    @RequestMapping("hello")
    @ResponseBody
    public String hello(){
        return mainService.hello();
    }
}