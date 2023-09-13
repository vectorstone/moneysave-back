package com.atguigu.accounting.service.impl;

import com.atguigu.accounting.entity.SysRole;
import com.atguigu.accounting.entity.SysUserRole;
import com.atguigu.accounting.entity.vo.AssignRoleVo;
import com.atguigu.accounting.mapper.SysRoleMapper;
import com.atguigu.accounting.mapper.SysUserRoleMapper;
import com.atguigu.accounting.service.SysRoleService;
import com.atguigu.accounting.service.SysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author Atguigu
 * @since 2023-08-03
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Resource
    SysUserRoleService sysUserRoleService;
    @Resource
    SysUserRoleMapper sysUserRoleMapper;
    @Override
    public Map<String, Object> getRolesByUserId(String userId) {
        //获取所有的角色列表对象
        List<SysRole> allRoles = this.list();
        //根据用户的id查询该用户当前的角色id
        List<SysUserRole> list = sysUserRoleService.list(Wrappers.lambdaQuery(SysUserRole.class)
                .eq(SysUserRole::getUserId, userId));
        //再将当前用户所对应的角色的id取出来
        List<Long> userRoleIds = list.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        //创建map集合,返回查询出来的结果
        Map<String,Object> map = new HashMap<>();
        map.put("allRoles",allRoles);
        map.put("userRoleIds",userRoleIds);
        return  map;
    }
    @Override
    public void doAssign(AssignRoleVo assignRoleVo) {
        //1.根据用户的id,删除用户的现有角色信息
        //下面的这个方法有点坑,竟然删除的是主键的id,不是按照user_id来删除的
        //UPDATE sys_user_role SET is_deleted=1 WHERE id=? AND is_deleted=0
        //sysUserRoleMapper.deleteById(assignRoleVo.getUserId());
        QueryWrapper<SysUserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",assignRoleVo.getUserId());
        sysUserRoleMapper.delete(wrapper);

        //2.根据用户的角色id列表,更新用户的角色信息
        //2.1获取用户新的角色id列表
        List<Long> roleIdList = assignRoleVo.getRoleIdList();
        //2.2遍历循环用户新的角色id列表
        for (Long roleId : roleIdList) {
            //2.3创建新的用户角色对象
            SysUserRole sysUserRole = new SysUserRole();
            //2.4将角色id设置到用户角色对象中
            sysUserRole.setRoleId(roleId);
            //2.5将用户id设置到用户角色对象中
            sysUserRole.setUserId(assignRoleVo.getUserId());
            //2.6将上述新创建的用户角色对象插入到数据库中
            sysUserRoleMapper.insert(sysUserRole);
        }
    }
}
