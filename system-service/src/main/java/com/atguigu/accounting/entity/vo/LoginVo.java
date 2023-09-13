package com.atguigu.accounting.entity.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录对象
 */
@Data
@ApiModel("登录对象")
public class LoginVo implements Serializable {
    @ApiModelProperty("登录用户的用户名")
    private String username;
    @ApiModelProperty("登录用户的密码")
    private String password;
}
