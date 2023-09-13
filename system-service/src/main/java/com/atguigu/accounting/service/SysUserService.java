package com.atguigu.accounting.service;

import com.atguigu.accounting.entity.SysUser;

import com.atguigu.accounting.entity.vo.UserVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Atguigu
 * @since 2023-08-03
 */
public interface SysUserService extends IService<SysUser> {


    Page<SysUser> getPageList(Integer page, Integer limit, UserVo userQueryVo);

    SysUser getByUsername(String username);

    SysUser getInfo(String token);

    Map<String, Object> getUserInfo(String token);

    Map<String, Object> getUserInfoByUserId(Long userId);

    List<String> getUserBtnPersByUserId(Long id);

    //根据用户的id获取对应的按钮的权限
    List<String> getBtnPermissionByUserId(Long userId);

    void addUser(SysUser sysUser);
}
