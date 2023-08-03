package com.atguigu.accounting.controller;


import com.atguigu.accounting.entity.Book;
import com.atguigu.accounting.entity.vo.SearchVo;
import com.atguigu.accounting.result.R;
import com.atguigu.accounting.service.BookService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Atguigu
 * @since 2023-07-28
 */
@RestController
@RequestMapping("/admin/book")
@Api(tags = "账单模块")
@CrossOrigin
public class BookController {
    @Resource
    BookService bookService;
    @ApiOperation("分页查询")
    @PostMapping ("/{pageNum}/{pageSize}")
    public R getList(/* @RequestParam(value = "searchVo",required = false) SearchVo searchVo */
            @RequestBody SearchVo searchVo,
                     @PathVariable("pageNum")Integer pageNum,
                     @PathVariable("pageSize")Integer pageSize){
        Page page = bookService.getPageList(pageNum,pageSize,searchVo);
        return R.ok().data("items",page);
    }
    @ApiOperation("账单excel表格的上传功能")
    @PostMapping("/import")
    public R importBook(@RequestParam("file")MultipartFile file){
        bookService.importBook(file);
        return R.ok();
    }
    @ApiOperation("账单导出为excel")
    @GetMapping("/export")
    //这个地方不能有返回值,否则会覆盖服务器给前端的response响应
    public void download(HttpServletResponse response){
        bookService.exportAccount(response);
    }
    @ApiOperation("根据id删除账单")
    @DeleteMapping("/{id}")
    public R removeById(@PathVariable("id")String id){
        bookService.removeById(id);
        return R.ok();
    }
    @ApiOperation("根据id修改账单")
    @PutMapping
    public R updateById(@RequestBody Book book){
        bookService.updateById(book);
        return R.ok();
    }
    @ApiOperation("新增账单")
    @PostMapping
    public R save(@RequestBody Book book){
        bookService.save(book);
        return R.ok();
    }
    @ApiOperation("每月支出情况统计echarts")
    @GetMapping("/echarts/cost/monthly/{start}/{end}")
    public R costMonthly(@PathVariable("start")String start,
                         @PathVariable("end")String end){
        Map<String,List<String>> map = bookService.getMonthlyCost(start,end);
        return R.ok().data("items",map);
    }
    @ApiOperation("每天支出情况统计echarts")
    //不需要参数,汇总统计的是所有的数据
    @GetMapping("/echarts/cost/largeArea")
    public R largeArea(){
        Map<String,List<String>> map = bookService.getLargeAreaData();
        return R.ok().data("items",map);
    }
}

