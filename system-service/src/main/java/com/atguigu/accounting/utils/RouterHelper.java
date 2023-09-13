package com.atguigu.accounting.utils;

import com.atguigu.accounting.entity.SysMenu;
import com.atguigu.accounting.entity.vo.MetaVo;
import com.atguigu.accounting.entity.vo.RouterVo;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *根据菜单数据构建路由的工具类
 */
public class RouterHelper {

    /**
     * 根据菜单构建路由
     * @param menus
     * @return
     */
    public static List<RouterVo> buildRouters(List<SysMenu> menus) {
        List<RouterVo> routers = new LinkedList<RouterVo>();
        if(!CollectionUtils.isEmpty(menus)){
            for (SysMenu menu : menus) {
                RouterVo router = new RouterVo();
                router.setHidden(false);
                router.setAlwaysShow(false);
                router.setPath(getRouterPath(menu));
                router.setComponent(menu.getComponent());
                router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
                List<SysMenu> children = menu.getChildren();
                //如果当前是菜单，需将按钮对应的路由加载出来，如：“角色授权”按钮对应的路由在“系统管理”下面
                if(menu.getType().intValue() == 1) {
                    List<SysMenu> hiddenMenuList = children.stream().filter(item -> !StringUtils.isEmpty(item.getComponent())).collect(Collectors.toList());
                    for (SysMenu hiddenMenu : hiddenMenuList) {
                        RouterVo hiddenRouter = new RouterVo();
                        hiddenRouter.setHidden(true);
                        hiddenRouter.setAlwaysShow(false);
                        hiddenRouter.setPath(getRouterPath(hiddenMenu));
                        hiddenRouter.setComponent(hiddenMenu.getComponent());
                        hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                        routers.add(hiddenRouter);
                    }
                } else {
                    if (!CollectionUtils.isEmpty(children)) {
                        if(children.size() > 0) {
                            router.setAlwaysShow(true);
                        }
                        router.setChildren(buildRouters(children));
                    }
                }
                routers.add(router);
            }
        }
        return routers;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public static String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }
}
