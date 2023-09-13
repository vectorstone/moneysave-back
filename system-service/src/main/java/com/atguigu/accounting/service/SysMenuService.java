package com.atguigu.accounting.service;

import com.atguigu.accounting.entity.SysMenu;
import com.atguigu.accounting.entity.vo.AssignMenuVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author Atguigu
 * @since 2023-08-03
 */
public interface SysMenuService extends IService<SysMenu> {
    List<SysMenu> findNodes();

    void removeMenuById(Long id);

    //根据角色获取菜单
    List<SysMenu> findSysMenuByRoleId(Long roleId);

    //给角色分配菜单
    void doAssign(AssignMenuVo assignMenuVo);
}
