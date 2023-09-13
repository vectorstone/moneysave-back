package com.atguigu.accounting.mapper;

import com.atguigu.accounting.entity.SysRole;
import com.atguigu.accounting.entity.vo.SysRoleQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 角色 Mapper 接口
 * </p>
 *
 * @author Atguigu
 * @since 2023-08-03
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {
    Page<SysRole> selectPage(Page<SysRole> pageParam, @Param("vo") SysRoleQueryVo sysRoleQueryVo);
}
