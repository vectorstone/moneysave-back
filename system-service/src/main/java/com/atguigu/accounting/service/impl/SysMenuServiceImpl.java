package com.atguigu.accounting.service.impl;

import com.atguigu.accounting.entity.SysMenu;
import com.atguigu.accounting.entity.SysRoleMenu;
import com.atguigu.accounting.entity.vo.AssignMenuVo;
import com.atguigu.accounting.mapper.SysMenuMapper;
import com.atguigu.accounting.mapper.SysRoleMenuMapper;
import com.atguigu.accounting.service.SysMenuService;
import com.atguigu.accounting.utils.GuiguException;
import com.atguigu.accounting.utils.MenuHelper;
import com.atguigu.accounting.utils.ResultCodeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author Atguigu
 * @since 2023-08-03
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<SysMenu> findNodes() {
        //先查询获得所有的菜单
        List<SysMenu> sysMenus = sysMenuMapper.selectList(null);

        List<SysMenu> sysMenuList = new ArrayList<>();
        //使用递归的方法,将这个list创建成树状结构的数据
        for (SysMenu sysMenu : sysMenus) {
            //这个就是递归的程序的入口
            if(sysMenu.getParentId() == 0){
                //进来这里面说明是父节点
                sysMenuList.add(findChildren(sysMenu,sysMenus));
            }
        }
        return sysMenuList;
    }

    //根据id删除菜单
    @Override
    public void removeMenuById(Long id) {
        //这里需要进行判断,如果当前菜单有子菜单,则不能删除,否则可以删除
        QueryWrapper<SysMenu> sysMenuQueryWrapper = new QueryWrapper<>();
        sysMenuQueryWrapper.eq("parent_id",id);
        //这个查询条件的意思是查询一下这个id的菜单的下面是否还有子菜单
        if(sysMenuMapper.selectCount(sysMenuQueryWrapper)>0){
            //如果查询的结果是大于0的,说明当前菜单下还有子菜单,所以不能删除,抛出自定义的异常
            throw new GuiguException(ResultCodeEnum.NODE_ERROR);
            //NODE_ERROR( 218, "该节点下有子节点，不可以删除")
        }
        //如果查询出来当前的这个菜单下面没有子菜单的时候,才可以直接删除
        sysMenuMapper.deleteById(id);
    }

    @Override
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {
        //1.获取所有的菜单 前提是status为1的菜单(也就是被启用的菜单)
        List<SysMenu> sysMenuList = sysMenuMapper.selectList(new QueryWrapper<SysMenu>().eq("status", 1));

        //2.查询角色菜单关系表,获取当前角色分配过的菜单
        QueryWrapper<SysRoleMenu> sysRoleMenuQueryWrapper = new QueryWrapper<>();
        sysRoleMenuQueryWrapper.eq("role_id",roleId);
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuMapper.selectList(sysRoleMenuQueryWrapper);

        //3.根据第二步的查询结果,获取所有分配过的菜单的id
        /*
        原始写法
        sysRoleMenus.stream().map(new Function<SysRoleMenu, Long>() {
            @Override
            public Long apply(SysRoleMenu sysRoleMenu) {
                return sysRoleMenu.getMenuId();
            }
        }).collect(Collectors.toList());
        */

        /*
        简化1
        sysRoleMenus.stream().map(item ->
            item.getMenuId()
        ).collect(Collectors.toList());
        */

        List<Long> menusId = sysRoleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());

        //4.如果所有的菜单list里面,有被分配给当前的这个角色,那么就然后将菜单的isSelect修改为true
        /*
        原始写法
        sysMenuList.forEach(new Consumer<SysMenu>() {
            @Override
            public void accept(SysMenu sysMenu) {
                if(menusId.contains(sysMenu.getId())){
                    sysMenu.setSelect(true);
                }
            }
        });
        */
        sysMenuList.forEach(sysMenu -> sysMenu.setSelect(menusId.contains(sysMenu.getId())));

        //5.将菜单构建成树形结构并返回
        return MenuHelper.buildTree(sysMenuList);
    }

    //给角色分配菜单
    @Override
    public void doAssign(AssignMenuVo assignMenuVo) {
        //删除现有的角色的已分配的菜单
        QueryWrapper<SysRoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id",assignMenuVo.getRoleId());
        sysRoleMenuMapper.delete(queryWrapper);

        //将角色获得的新的菜单插入到数据库中
        /*assignMenuVo.getMenuIdList().stream().forEach(new Consumer<Long>() {
            @Override
            public void accept(Long menuId) {
                //创建对象,设置属性,插入数据库
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(assignMenuVo.getRoleId());
                sysRoleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(sysRoleMenu);
            }
        });*/
        assignMenuVo.getMenuIdList().forEach(menuId-> {
                    //创建对象,设置属性,插入数据库
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(assignMenuVo.getRoleId());
                sysRoleMenu.setMenuId(menuId);
                    // SysRoleMenu sysRoleMenu = new SysRoleMenu(assignMenuVo.getRoleId(), menuId);
                    sysRoleMenuMapper.insert(sysRoleMenu);
                }
        );
    }



    //递归查找子节点
    SysMenu findChildren(SysMenu sysMenu, List<SysMenu> list){
        // SysMenu sysMenu = sMenu;
        sysMenu.setChildren(new ArrayList<>());
        for (SysMenu menu : list) {
            //下面这行代码就是递归的程序的出口
            if(sysMenu.getId() == menu.getParentId()){
                if(sysMenu.getChildren() == null){
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(findChildren(menu,list));
            }
        }
        return sysMenu;
    }
}
