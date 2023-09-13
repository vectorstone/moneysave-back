package com.atguigu.accounting.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 路由显示信息
 *
 */
@Data
public class MetaVo implements Serializable {
    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    private String title;

    /**
     * 设置该路由的图标，对应路径src/assets/icons/svg
     */
    private String icon;

    public MetaVo() {
    }

    public MetaVo(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }

}

