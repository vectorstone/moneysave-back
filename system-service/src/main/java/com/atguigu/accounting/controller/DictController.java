package com.atguigu.accounting.controller;


import com.atguigu.accounting.entity.Dict;
import com.atguigu.accounting.result.R;
import com.atguigu.accounting.service.DictService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author Atguigu
 * @since 2023-07-30
 */
@RestController
@RequestMapping("/admin/dict")
@Api(tags = "分类的数据字典管理模块")
@CrossOrigin
public class DictController {
    @Resource
    DictService dictService;
    @ApiOperation("categoryDict字典导入模块")
    @PostMapping("/import")
    public R importDict(@RequestParam("file") MultipartFile file){
        dictService.importDict(file);
        return R.ok();
    }
    @ApiOperation(value = "下载字典文件")
    @GetMapping("/export")
    //这个地方不能有返回值,否则会覆盖服务器给前端的response响应
    public void downLoad(HttpServletResponse response){
        dictService.exportDicts(response);
    }

    @ApiOperation(value = "根据pid查询信息")
    @GetMapping("/{pid}")
    public R getByPid(@PathVariable("pid") Long pid){
        List<Dict> dicts = dictService.getByPid(pid);
        return R.ok().data("items",dicts);
    }
    @ApiOperation(value = "根据name查询该字典对应的子字典")
    @GetMapping("/getsub")
    public R getByPname(@RequestParam("name") String name){
        //先根据name查出来这个name对应的父节点的id
        Dict parentDict = dictService.getOne(Wrappers.lambdaQuery(Dict.class).eq(Dict::getName, name));
        Long pid = parentDict.getId();
        return this.getByPid(pid);
    }

    //新增下一级数据字典
    @ApiOperation(value = "新增下一级的数据字典")
    @PostMapping
    public R save(@RequestBody Dict dict){
        dictService.saveWithSync(dict);
        return R.ok();
    }

    //根据id删除数据字典
    @ApiOperation(value = "根据id删除数据字典")
    @DeleteMapping("/{id}")
    public R deleteById(@PathVariable("id") Long id){
        Dict dict = dictService.getById(id);
        //TODO 清空缓存的时候,清空的是当前dict的父字典的数据的缓存 思考:为啥不直接删除当前dict
        dictService.removeByIdWithSync(dict);
        return R.ok();
    }

    //更新数据字典(包含了根据id查询数字字典来回显和根据id查询数据字典)
    @ApiOperation(value = "根据id查询信息,数据回显用")
    @GetMapping("/search/{id}")
    public R getById(@PathVariable("id") Long id){
        return R.ok().data("items",dictService.getById(id));
    }
    @ApiOperation(value = "更新数据字典")
    @PutMapping
    public R update(@RequestBody Dict dict){
        dictService.updateByIdWithSync(dict);
        return R.ok();
    }
    //获取所有的字典信息
    @ApiOperation("获取所有字典信息")
    @GetMapping("/getDicts")
    public R getDicts(){
        List<Dict> dicts = dictService.list();
        return R.ok().data("items",dicts);
    }
}

