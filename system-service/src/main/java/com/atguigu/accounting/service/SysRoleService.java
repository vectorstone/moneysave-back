package com.atguigu.accounting.service;

import com.atguigu.accounting.entity.SysRole;
import com.atguigu.accounting.entity.vo.AssignRoleVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 角色 服务类
 * </p>
 *
 * @author Atguigu
 * @since 2023-08-03
 */
public interface SysRoleService extends IService<SysRole> {

    Map<String, Object> getRolesByUserId(String userId);

    void doAssign(AssignRoleVo assignRoleVo);
}
