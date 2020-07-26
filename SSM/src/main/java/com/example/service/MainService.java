package com.example.service;

import com.example.mapper.MainMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MainService{

    @Autowired 
    MainMapper mainMapper;
    public String hello(){
        return mainMapper.hello();
    }
}