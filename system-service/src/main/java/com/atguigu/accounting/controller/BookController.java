package com.atguigu.accounting.controller;


import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.atguigu.accounting.entity.Book;
import com.atguigu.accounting.entity.SysUser;
import com.atguigu.accounting.entity.vo.SearchVo;
import com.atguigu.accounting.result.R;
import com.atguigu.accounting.result.ResponseEnum;
import com.atguigu.accounting.service.BookService;
import com.atguigu.accounting.utils.BusinessException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Autowired
    RedisTemplate redisTemplate;
    @PreAuthorize("hasAnyAuthority('bnt.account.list')")
    @ApiOperation("分页查询")
    @PostMapping ("/{pageNum}/{pageSize}")
    public R getList(/* @RequestParam(value = "searchVo",required = false) SearchVo searchVo */
            @RequestBody SearchVo searchVo,
                     @PathVariable("pageNum")Integer pageNum,
                     @PathVariable("pageSize")Integer pageSize,
            @RequestHeader("token")String token){
        //所有的账单的查询必须只能查询自己的,实现多用户的账单数据的隔离
        SysUser sysUser = (SysUser)redisTemplate.boundValueOps(token).get();
        Long userId = sysUser.getId();
        Page page = bookService.getPageList(pageNum,pageSize,searchVo,userId);
        return R.ok().data("items",page);
    }
    @PreAuthorize("hasAnyAuthority('bnt.account.upload')")
    @ApiOperation("账单excel表格的上传功能")
    @PostMapping("/import")
    public R importBook(@RequestParam("file")MultipartFile file,@RequestHeader("token")String token){
        //excel也是只能上传自己的账单数据,不能上传别人的数据
        SysUser sysUser = (SysUser)redisTemplate.boundValueOps(token).get();
        Long userId = sysUser.getId();
        bookService.importBook(file,userId);
        return R.ok();
    }
    @PreAuthorize("hasAnyAuthority('bnt.account.download')")
    @ApiOperation("账单导出为excel")
    @GetMapping("/export")
    //这个地方不能有返回值,否则会覆盖服务器给前端的response响应
    public void download(HttpServletResponse response,@RequestHeader("token")String token){
        SysUser sysUser = (SysUser)redisTemplate.boundValueOps(token).get();
        Long userId = sysUser.getId();
        bookService.exportAccount(response,userId);
    }
    @PreAuthorize("hasAnyAuthority('bnt.account.remove')")
    @ApiOperation("根据id删除账单")
    @DeleteMapping("/{id}")
    public R removeById(@PathVariable("id")String id,@RequestHeader("token")String token){
        SysUser sysUser = (SysUser)redisTemplate.boundValueOps(token).get();
        Long userId = sysUser.getId();
        //加个判断,如果修改的不是自己的账单的数据抛出异常
        Book book = bookService.getById(id);
        if (book.getUser().longValue() != userId.longValue()){
            throw new BusinessException(ResponseEnum.NOT_YOUSELF_ACCOUNT);
        }
        bookService.removeById(id);
        return R.ok();
    }
    @PreAuthorize("hasAnyAuthority('bnt.account.update')")
    @ApiOperation("根据id修改账单")
    @PutMapping
    public R updateById(@RequestBody Book book,@RequestHeader("token")String token){
        SysUser sysUser = (SysUser)redisTemplate.boundValueOps(token).get();
        Long userId = sysUser.getId();
        //加个判断,如果修改的不是自己的账单的数据抛出异常
        if (book.getUser().longValue() != userId.longValue()){
            throw new BusinessException(ResponseEnum.NOT_YOUSELF_ACCOUNT);
        }
        bookService.updateById(book);
        return R.ok();
    }
    @PreAuthorize("hasAnyAuthority('bnt.account.add')")
    @ApiOperation("新增账单")
    @PostMapping
    public R save(@RequestBody Book book,@RequestHeader("token")String token){
        SysUser sysUser = (SysUser)redisTemplate.boundValueOps(token).get();
        Long userId = sysUser.getId();
        book.setUser(userId);
        bookService.save(book);
        return R.ok();
    }
    @PreAuthorize("hasAnyAuthority('bnt.echarts.list')")
    @ApiOperation("每月支出情况统计echarts")
    @GetMapping("/echarts/cost/monthly/{start}/{end}")
    public R costMonthly(@PathVariable("start")String start,
                         @PathVariable("end")String end,@RequestHeader("token")String token){
        SysUser sysUser = (SysUser)redisTemplate.boundValueOps(token).get();
        Long userId = sysUser.getId();
        Map<String,List<String>> map = bookService.getMonthlyCost(start,end,userId);
        return R.ok().data("items",map);
    }
    @PreAuthorize("hasAnyAuthority('bnt.echarts.list')")
    @ApiOperation("每天支出情况统计echarts")
    //不需要参数,汇总统计的是所有的数据
    @GetMapping("/echarts/cost/largeArea")
    public R largeArea(@RequestHeader("token")String token){
        SysUser sysUser = (SysUser)redisTemplate.boundValueOps(token).get();
        Long userId = sysUser.getId();
        String largeAreaData = redisTemplate.boundValueOps("largeAreaData").get().toString();
        if (StringUtils.isNotBlank(largeAreaData)){
            Map map = JSON.parseObject(largeAreaData, Map.class);
            return R.ok().data("items",map);
        }
        Map<String,List<String>> map = bookService.getLargeAreaData(userId);
        return R.ok().data("items",map);
    }
}

