package com.example.mapper;

import org.apache.ibatis.annotations.Select;

public interface MainMapper {
    @Select("select username from xxcl_user where id = 1")
    public String hello();
}