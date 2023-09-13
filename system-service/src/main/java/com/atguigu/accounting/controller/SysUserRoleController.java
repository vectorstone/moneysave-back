package com.atguigu.accounting.controller;


import com.atguigu.accounting.entity.SysRole;
import com.atguigu.accounting.entity.vo.AssignRoleVo;
import com.atguigu.accounting.entity.vo.SysRoleQueryVo;
import com.atguigu.accounting.service.SysRoleService;
import com.atguigu.accounting.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户角色 前端控制器
 * </p>
 *
 * @author Atguigu
 * @since 2023-08-03
 */

@RestController
@RequestMapping("/admin/system/sysRole")
@Api(tags="角色管理模块")
public class SysUserRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    //测试统一返回结果的Result结果集
    @PreAuthorize("hasAnyAuthority('bnt.sysRole.list')")
    @ApiOperation(value = "查询所有角色")
    @GetMapping("")
    public Result<List<SysRole>> selectAll(){
        return Result.ok(sysRoleService.list());
    }

    //测试分页的效果
    @PreAuthorize("hasAnyAuthority('bnt.sysRole.list')")
    @ApiOperation(value = "获取分页列表")
    //restful风格的请求格式
    @GetMapping("/{page}/{limit}")
    public Result getPage(
            //ApiParam是用来标注参数的,方便knife4j生成api文档
            //name值得是参数的名称,value指的是这个参数代表的含义,required = true指的是发起请求的时候必须有这个参数
            @ApiParam(name = "page",value = "当前的页码",required = true) @PathVariable Integer page,
            @ApiParam(name = "limit",value = "每页记录数",required = true) @PathVariable Integer limit,
            //required = false指的是发起请求的时候可以没有这个参数,由于required默认的属性就是false,所以可以省略不写
            @ApiParam(name = "sysRoleQueryVo",value = "查询的对象",required = false) SysRoleQueryVo sysRoleQueryVo
    ){
        //创建分页对象,设置起始页和当前页记录数
        Page<SysRole> pageParam = new Page<>(page,limit);
        //根据查询条件来查询结果,查询的结果也是Page<SysRole>类型的结果
        Page<SysRole> pageModel =  sysRoleService.selectPage(pageParam,sysRoleQueryVo);
        //将查询结果进行Result封装,并返回给前端
        return Result.ok(pageModel);
    }

    //根据id查询角色
    @PreAuthorize("hasAnyAuthority('bnt.sysRole.update')")
    @GetMapping("/{id}")
    @ApiOperation("根据id查询角色")
    public Result getSysRoleById(
            @ApiParam(name = "id",value = "角色id",required = true) @PathVariable  Integer id
    ){
        /*if(id < 5){
            System.out.println(id / 0);
        }else if(id < 10){
            throw new RuntimeException("抛出了一个运行时异常");
        }else{
            throw new GuiguException(ResultCodeEnum.ACCOUNT_STOP);
        }*/
        return Result.ok(sysRoleService.getById(id));
    }

    //新增角色
    @PreAuthorize("hasAnyAuthority('bnt.sysRole.add')")
    @PostMapping("")
    @ApiOperation("新增角色")
    public Result addSysRole(
            @ApiParam(name = "sysRole",value = "角色",required = true) SysRole sysRole
    ){
        sysRole.setCreateTime(null);
        sysRole.setUpdateTime(null);
        sysRole.setId(null);
        return Result.ok(sysRoleService.save(sysRole));
    }
    //修改角色
    @PreAuthorize("hasAnyAuthority('bnt.sysRole.update')")
    @PutMapping("")
    @ApiOperation("修改角色")
    public Result editSysRole(
            @ApiParam(name = "sysRole",value = "角色",required = true) @RequestBody SysRole sysRole
    ){
        sysRole.setUpdateTime(null);
        return Result.ok(sysRoleService.updateById(sysRole));
    }

    //修改角色测试
    @PreAuthorize("hasAnyAuthority('bnt.sysRole.update')")
    @PutMapping("/test")
    @ApiOperation("修改角色")
    public Result editSysRole1(
            @ApiParam(name = "sysRole",value = "角色",required = true)  SysRole sysRole
    ){
        sysRole.setUpdateTime(null);
        return Result.ok(sysRoleService.updateById(sysRole));
    }


    //根据id删除角色
    @PreAuthorize("hasAnyAuthority('bnt.sysRole.remove')")
    @DeleteMapping("/{id}")
    @ApiOperation("根据id删除角色")
    public Result deleteById(
            @ApiParam(name = "id",value = "角色id",required = true) @PathVariable Integer id
    ){
        return Result.ok(sysRoleService.removeById(id));
    }
    //根据id批量删除角色
    @PreAuthorize("hasAnyAuthority('bnt.sysRole.remove')")
    @DeleteMapping("/remove")
    @ApiOperation("根据id批量删除角色")
    public Result batchDelete(@ApiParam(name = "idList",value = "角色id集合,数组",required = true)
                              @RequestBody List<Integer> idList){
        return Result.ok(sysRoleService.removeByIds(idList));
    }

    //查询用户的角色
    @PreAuthorize("hasAnyAuthority('bnt.sysUser.assignRole')")
    @ApiOperation("加载角色列表(包括所有的角色和当前用户拥有的角色id)")
    @GetMapping("/toAssign/{userId}")
    public Result<Map<String,Object>> getAssign(
            @ApiParam(name = "userId",value = "用户id",required = true)
            @PathVariable String userId
    ){
        Map<String,Object> userRolesMap = sysRoleService.getRolesByUserId(userId);
        return Result.ok(userRolesMap);
    }

    //给用户重新分配角色
    @PreAuthorize("hasAnyAuthority('bnt.sysUser.assignRole')")
    @ApiOperation("更新用户角色")
    @PutMapping("/doAssign")
    public Result doAssign(
            @ApiParam(name = "assignRoleVo",value = "用户更新的角色信息",required = true)
            @RequestBody AssignRoleVo assignRoleVo
    ){
        sysRoleService.doAssign(assignRoleVo);
        return Result.ok();
    }
}

