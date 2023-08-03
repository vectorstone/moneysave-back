package com.atguigu.accounting.entity.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 7/29/2023 1:46 PM
 */
@Data
@ApiModel("查询条件的对象")
public class SearchVo {

    @ApiModelProperty(value = "起始日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    //设置自动填充的时机 指定新增时和更新时填充字段,我们这里只让更新的时候自动填充,插入的时候
    @TableField(fill = FieldFill.INSERT)
    private Date startTime;

    @ApiModelProperty(value = "结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    //设置自动填充的时机 指定新增时和更新时填充字段,我们这里只让更新的时候自动填充,插入的时候
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date endTime;

    @ApiModelProperty(value = "收支类型")
    private String type;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "类别")
    private String category;

    @ApiModelProperty(value = "子类")
    @TableField("subCategory")
    private String subcategory;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "创建用户的id")
    @TableId(value = "user", type = IdType.ASSIGN_ID)
    private Long user;
}
