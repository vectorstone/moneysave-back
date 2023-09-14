package com.atguigu.accounting;

import com.atguigu.accounting.entity.Book;
import com.atguigu.accounting.service.BookService;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.List;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 7/29/2023 8:36 AM
 */
@SpringBootTest
public class TestReadAndWrite {
    @Resource
    private BookService bookService;
    @Test
    void test(){
        Book book = new Book();
        book.setAmount(new BigDecimal(33.0));
        bookService.save(book);
    }
    @Test
    void test2(){
        Book book = new Book();
        book.setAmount(new BigDecimal(33.0));
        bookService.save(book);
    }
    @Test
    void test3(){
        List<Book> list = bookService.list();
        System.out.println(list);
    }


    //获取加密后的密文
    @Autowired
    private StringEncryptor encryptor;
    @Test
    void test4(){
        List<String> properties = new ArrayList<>();
        properties.add("");
        properties.add("");
        properties.add("");
        properties.add("");
        properties.forEach(property -> {
            System.out.println(property + encryptor.encrypt(property));
        });
    }
}
