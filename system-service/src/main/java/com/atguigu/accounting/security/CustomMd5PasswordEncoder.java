// package com.atguigu.accounting.security;
//
// import com.atguigu.accounting.service.SysUserService;
// import com.atguigu.accounting.utils.MD5;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;
//
// import javax.annotation.Resource;
//
// @Component
// public class CustomMd5PasswordEncoder implements PasswordEncoder {
//     @Resource
//     SysUserService sysUserService;
//
//     public String encode(CharSequence rawPassword) {
//         return MD5.encrypt(rawPassword.toString());
//     }
//
//     public boolean matches(CharSequence rawPassword, String encodedPassword) {
//         // return encodedPassword.equals(MD5.encrypt(rawPassword.toString()));
//         return encodedPassword.equals(encode(rawPassword));
//     }
// }