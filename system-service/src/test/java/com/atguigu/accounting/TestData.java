package com.atguigu.accounting;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.atguigu.accounting.entity.vo.BookVo;
import com.atguigu.accounting.listener.EmployeeExcelListener;
import com.atguigu.accounting.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 8/3/2023 2:13 PM
 */
@SpringBootTest
@Slf4j
public class TestData {
    @Resource
    BookService bookService;
    //简单读取数据测试
    @Test
    void readFileTest(){
        //D:/Desktop/oldData230803.xlsx
        File file = new File("D:/Desktop/oldData230803.xlsx");
        EasyExcel.read(file, BookVo.class, new EmployeeExcelListener(bookService)).excelType(ExcelTypeEnum.XLSX)
                .sheet().doRead();
    }
    @Test
    void test1(){
        DateTime dateTime = new DateTime(new Date());
        String s = dateTime.plusSeconds(30).plusMinutes(10).plusHours(1).toDate().toString();
        System.out.println("s = " + s);
        int i = RandomUtils.nextInt(1, 60);
        System.out.println("i = " + i);
    }
    @Test
    void test2(){
        Date createTime = new Date();
        DateTime dateTime = new DateTime(createTime);
        // dateTime.plusSeconds(RandomUtils.nextInt(0,60));
        // dateTime.plusMinutes(RandomUtils.nextInt(0,60));
        DateTime dateTime1 = dateTime.plusHours(4);
        System.out.println(dateTime1);
    }
}
