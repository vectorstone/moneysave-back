package com.atguigu.accounting.controller;


import com.atguigu.accounting.entity.SysMenu;
import com.atguigu.accounting.entity.vo.AssignMenuVo;
import com.atguigu.accounting.service.SysMenuService;
import com.atguigu.accounting.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author Atguigu
 * @since 2023-08-03
 */
@Api(tags = "菜单管理模块")
@RestController
@RequestMapping("/admin/system/sysMenu")
@Transactional
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;

    @PreAuthorize("hasAnyAuthority('bnt.sysMenu.list')")
    @GetMapping("")
    @ApiOperation("获取菜单")
    public Result<List<SysMenu>> getMenusList(){
        return Result.ok(sysMenuService.findNodes());
    }
    //根据id查询菜单
    //这个方法自始至终好像都没有用上
    @PreAuthorize("hasAnyAuthority('bnt.sysMenu.list')")
    // @PreAuthorize("hasAnyAuthority('bnt.sysMenu.update')")
    @GetMapping("/getById/{id}")
    @ApiOperation("根据id查询菜单")
    public Result<SysMenu> getById(
            @ApiParam(name = "id",value = "菜单id",required = true)
            @PathVariable("id") Long id
    ){
        SysMenu byId = sysMenuService.getById(id);
        return Result.ok(byId);
    }

    //增加菜单
    @PreAuthorize("hasAnyAuthority('bnt.sysMenu.add')")
    @PostMapping("")
    @ApiOperation("增加菜单")
    public Result save(
            @ApiParam(name = "permission",value = "需要增加的菜单对象",required = true)
            @RequestBody SysMenu permission
    ){
        sysMenuService.save(permission);
        return Result.ok();
    }

    //修改菜单
    @PreAuthorize("hasAnyAuthority('bnt.sysMenu.update')")
    @PutMapping("")
    @ApiOperation("修改菜单")
    public Result update(
            @ApiParam(name = "permission",value = "需要修改的菜单",required = true)
            @RequestBody SysMenu permission
    ){
        sysMenuService.updateById(permission);
        return Result.ok();
    }

    //根据id删除菜单
    @PreAuthorize("hasAnyAuthority('bnt.sysMenu.remove')")
    @DeleteMapping("/{id}")
    @ApiOperation("根据id删除菜单")
    public Result deleteMenu(
            @ApiParam(name = "id",value = "需要删除的菜单的id",required = true)
            @PathVariable("id") Long id
    ){
        sysMenuService.removeMenuById(id);
        return Result.ok();
    }
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~下面的是角色分配菜单的方法~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //根据角色获取菜单
    @PreAuthorize("hasAnyAuthority('bnt.sysRole.assignAuth')")
    @GetMapping("/toAssign/{roleId}")
    @ApiOperation("根据角色id获取菜单")
    public Result<List<SysMenu>> toAssign(
            @ApiParam(name = "roleId",value = "角色id",required = true)
            @PathVariable("roleId") Long roleId
    ){
        List<SysMenu> sysMenuList = sysMenuService.findSysMenuByRoleId(roleId);
        return Result.ok(sysMenuList);

    }

    //给角色分配菜单
    @PreAuthorize("hasAnyAuthority('bnt.sysRole.assignAuth')")
    @ApiOperation("给角色分配菜单")
    @PostMapping("/doAssign")
    public Result doAssign(
            @ApiParam(name = "assignMenuVo",value = "菜单分配的角色信息对象",required = true)
            @RequestBody AssignMenuVo assignMenuVo)
    {
        sysMenuService.doAssign(assignMenuVo);
        return Result.ok();
    }
}

