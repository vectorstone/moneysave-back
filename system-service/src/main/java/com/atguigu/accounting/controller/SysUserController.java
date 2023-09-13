package com.atguigu.accounting.controller;


import com.atguigu.accounting.entity.SysUser;
import com.atguigu.accounting.entity.SysUserRole;
import com.atguigu.accounting.entity.vo.LoginVo;
import com.atguigu.accounting.entity.vo.UserVo;
import com.atguigu.accounting.mapper.SysUserRoleMapper;
import com.atguigu.accounting.result.R;
import com.atguigu.accounting.result.ResponseEnum;
import com.atguigu.accounting.service.SysUserService;
import com.atguigu.accounting.utils.BusinessException;
import com.atguigu.accounting.utils.MD5;
import com.atguigu.accounting.utils.Result;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author Atguigu
 * @since 2023-08-03
 */
@RestController
@RequestMapping("/admin/user")
@Api(tags = "用户管理模块")
@CrossOrigin //开启跨域
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    RedisTemplate redisTemplate;


    @PreAuthorize("hasAnyAuthority('bnt.sysUser.list')")
    @ApiOperation("获取用户分页列表")
    @GetMapping("/{page}/{limit}")
    public R getUserLists(
            @ApiParam(name = "page",value = "当前页",required = true)
            @PathVariable("page") Integer page,
            @ApiParam(name = "limit",value = "每页记录数",required = true)
            @PathVariable("limit") Integer limit,
            @ApiParam(name = "sysUserQueryVo",value = "查询条件",required = false)
            UserVo userQueryVo
    ){
        Page<SysUser> userList = sysUserService.getPageList(page,limit,userQueryVo);
        return R.ok().data("items",userList);
    }

    //根据id查询用户
    @PreAuthorize("hasAnyAuthority('bnt.sysUser.list')")
    @ApiOperation("根据id查询用户")
    @GetMapping("/{id}")
    public R getSysUserById(
            @ApiParam(name = "id",value = "用户id",required = true)
            @PathVariable("id") String id
    ){
        return R.ok().data("sysUser", sysUserService.getById(id));
    }

    //新增
    // @PreAuthorize("hasAnyAuthority('bnt.sysUser.add')")
    @PostMapping("save")
    @ApiOperation("新增用户")
    public R addUser(@ApiParam(name = "sysUser",value = "新增的用户数据",required = true)
                         @RequestBody SysUser sysUser){
        sysUserService.addUser(sysUser);
        return R.ok();
    }
    //修改1 data传参
    @PreAuthorize("hasAnyAuthority('bnt.sysUser.add')")
    @PutMapping("")
    @ApiOperation("修改用户:data传参")
    public R editUser(
            @ApiParam(name = "sysUser",value = "修改的用户数据",required = true)
            @RequestBody SysUser sysUser
    ){
        //updateTime有数据库自己生成,防止用户修改这个时间,所以在这里设置为空
        sysUser.setUpdateTime(null);
        boolean b = sysUserService.updateById(sysUser);
        return R.ok();
    }

    //根据id删除
    @PreAuthorize("hasAnyAuthority('bnt.sysUser.remove')")
    @ApiOperation("根据id删除用户")
    @DeleteMapping("/{id}")
    public R removeById(
            @ApiParam(name = "id",value = "用户id",required = true)
            @PathVariable("id") String id
    ){
        sysUserService.removeById(id);
        return R.ok();
    }

    //根据id批量删除
    @PreAuthorize("hasAnyAuthority('bnt.sysUser.remove')")
    @ApiOperation("批量删除用户")
    @DeleteMapping("/remove")
    public R removeBatch(
            @ApiParam(name = "idList",value = "用户id集合或数组",required = true)
            @RequestBody List<String> idList
    ){
        sysUserService.removeByIds(idList);
        return R.ok();
    }
    @PreAuthorize("hasAnyAuthority('bnt.account.list')")
    @ApiOperation("检查用户名是否唯一")
    @GetMapping("/checkUsername")
    public R checkUsername(@RequestParam("username")String username){
        int count = sysUserService.count(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUsername, username));
        if(count != 0){
            //说明用户名不唯一
            throw new BusinessException(ResponseEnum.USER_EXSIT_ERROR);
        }
        return R.ok();
    }
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo){
        SysUser sysUser = sysUserService.getByUsername(loginVo.getUsername());
        if(null == sysUser) {
            throw new BusinessException(ResponseEnum.ACCOUNT_ERROR);
        }
        if(!MD5.encrypt(loginVo.getPassword()).equals(sysUser.getPassword())) {
            throw new BusinessException(ResponseEnum.PASSWORD_ERROR);
        }
        if(sysUser.getStatus() == 0) {
            throw new BusinessException(ResponseEnum.ACCOUNT_STOP);
        }


        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisTemplate.boundValueOps(token).set(sysUser,2, TimeUnit.HOURS);
        // String token = JwtUtils.createToken(sysUser.getId(), sysUser.getUsername());
        //将生成的token返回给前端
        Map<String, Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }
    @ApiOperation("用户登录成功后获取用户的信息(头像用户名以及权限信息)")
    @GetMapping("/info")
    public Result getUserInfo(@RequestHeader("token")String token){

        //根据token从redis数据库中获取用户的id信息
        SysUser sysUser = (SysUser) redisTemplate.boundValueOps(token).get();

        //获取用户的信息(用户信息,菜单信息,对应的按钮权限)
        Map<String,Object> userInfoMap = sysUserService.getUserInfoByUserId(sysUser.getId());

        // return R.ok().data("userInfoMap",userInfoMap);
        return Result.ok(userInfoMap);

        /* //获取用户的详细信息,包含头像,用户名以及最重要的权限等信息
        Map<String,Object> map = sysUserService.getUserInfo(token);
        return R.ok().data(map); */
    }
    @ApiOperation("logout")
    @PostMapping("/logout")
    public Result logout(
            @ApiParam(name = "request",value = "HttpServletRequest请求",required = true)
            HttpServletRequest request
    ){
        //获取请求头中的token
        String token = request.getHeader("token");

        //从redis数据库中删除该用户的token
        redisTemplate.delete(token);

        return Result.ok();
    }
}


