// package com.atguigu.accounting.service.impl;
//
// import com.atguigu.accounting.security.MyCustomUser;
// import com.atguigu.accounting.entity.SysUser;
// import com.atguigu.accounting.service.SysUserService;
// import com.atguigu.accounting.utils.BusinessException;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Component;
//
// import java.util.Collections;
// import java.util.List;
//
//
// @Component
// public class UserDetailsServiceImpl implements UserDetailsService {
//
//     @Autowired
//     private SysUserService sysUserService;
//
//     @Override
//     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//         SysUser sysUser = sysUserService.getByUsername(username);
//         if(null == sysUser) {
//             throw new UsernameNotFoundException("用户名不存在！");
//         }
//
//         if(sysUser.getStatus() == 0) {
//             throw new BusinessException("账号已停用");
//         }
//         //查询用户的权限集合,然后设置到用户对象中,后面这个对象会存入到redis中,然后spring security可以从redis中获取到关于这个用户的相关的权限的数据
//         List<String> userPermsList = sysUserService.getUserBtnPersByUserId(sysUser.getId());
//         sysUser.setUserPermsList(userPermsList);
//         return new MyCustomUser(sysUser, Collections.emptyList());
//     }
// }