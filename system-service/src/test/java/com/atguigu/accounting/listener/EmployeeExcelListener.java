package com.atguigu.accounting.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.accounting.entity.Book;
import com.atguigu.accounting.entity.vo.BookVo;
import com.atguigu.accounting.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Slf4j
public class EmployeeExcelListener extends AnalysisEventListener<BookVo> {

    BookService bookService;

    public EmployeeExcelListener(BookService bookService) {
        this.bookService = bookService;
    }

    // 这个方法,每一条数据来都会调用
    @Override
    public void invoke(BookVo bookVo, AnalysisContext analysisContext) {
        Date createTime = bookVo.getCreateTime();
        DateTime dateTime = new DateTime(createTime).plusHours(RandomUtils.nextInt(1, 24))
                .plusMinutes(RandomUtils.nextInt(0, 60))
                .plusSeconds(RandomUtils.nextInt(0, 60));
        bookVo.setCreateTime(dateTime.toDate());
        log.info("解析到一条数据:{}",bookVo.getCreateTime());
        Book book = new Book();
        BeanUtils.copyProperties(bookVo,book);
        bookService.save(book);
    }
    // 所有数据都解析完成了会来调用
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    log.info("所有数据解析完成了!");
    }
}