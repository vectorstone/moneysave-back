package com.atguigu.accounting;

import com.atguigu.accounting.utils.MD5;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 9/27/2023 11:32 AM
 */
@SpringBootTest
public class PasswordMD5 {
    @Test
    void test1(){
        String encrypt = MD5.encrypt("123456");
        System.out.println("encrypt = " + encrypt);
        String xxxx = MD5.encrypt("xxxx");
        System.out.println("xxxx = " + xxxx);
    }
    @Autowired
    PasswordEncoder passwordEncoder;
    @Test
    void test2(){
        String xxxx = passwordEncoder.encode("xxxx");
        System.out.println("xxxx = " + xxxx);
    }
}
