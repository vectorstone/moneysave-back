package com.atguigu.accounting;

import com.atguigu.accounting.entity.Book;
import com.atguigu.accounting.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;

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
}
