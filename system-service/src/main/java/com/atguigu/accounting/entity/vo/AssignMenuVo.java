package com.atguigu.accounting.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel(description = "分配菜单")
@Data
public class AssignMenuVo implements Serializable {

    @ApiModelProperty(value = "角色id")
    private Long roleId;

    @ApiModelProperty(value = "菜单id列表")
    private List<Long> menuIdList;

}