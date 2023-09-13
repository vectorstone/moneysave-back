package com.atguigu.accounting.service;

import com.atguigu.accounting.entity.SysRole;
import com.atguigu.accounting.entity.vo.AssignRoleVo;
import com.atguigu.accounting.entity.vo.SysRoleQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 6/6/2023 4:53 PM
 */
public interface SysRoleService extends IService<SysRole> {
    Page<SysRole> selectPage(Page<SysRole> pageParam, SysRoleQueryVo sysRoleQueryVo);

    // 查询用户的角色
    Map<String, Object> getRolesByUserId(String userId);

    // 更新用户角色信息
    void doAssign(AssignRoleVo assignRoleVo);
}