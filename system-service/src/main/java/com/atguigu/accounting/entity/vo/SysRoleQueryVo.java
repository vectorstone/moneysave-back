package com.atguigu.accounting.entity.vo;

import java.io.Serializable;

/**
 * <p>
 * 角色查询实体
 * </p>
 */
public class SysRoleQueryVo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String roleName;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}