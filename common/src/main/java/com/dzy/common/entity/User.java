package com.dzy.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User{
    private Long id;
    private String username;         // 默认同手机号
    private String password;         // 对应数据库 password_hash
    private String phone;
    private String role;             // CUSTOMER / MERCHANT / ADMIN
    private Integer status;          // 1正常 0禁用
}