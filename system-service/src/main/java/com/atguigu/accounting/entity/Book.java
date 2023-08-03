package com.atguigu.accounting.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Atguigu
 * @since 2023-07-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Book对象", description="对应的账单数据库的对象")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "创建日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    //设置自动填充的时机 指定新增时和更新时填充字段,我们这里只让更新的时候自动填充,插入的时候
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "创建日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    //设置自动填充的时机 指定新增时和更新时填充字段,我们这里只让更新的时候自动填充,插入的时候
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "收支类型")
    @TableField(value = "type")
    private String type;

    @ApiModelProperty(value = "金额")
    @TableField(value = "amount")
    private BigDecimal amount;

    @ApiModelProperty(value = "类别")
    @TableField(value = "category")
    private String category;

    @ApiModelProperty(value = "子类")
    @TableField(value = "subcategory")
    private String subcategory;

    @ApiModelProperty(value = "账户")
    @TableField(value = "account")
    private String account;

    @ApiModelProperty(value = "账本名称")
    @TableField(value = "book")
    private String book;

    @ApiModelProperty(value = "报销账户")
    @TableField(value = "reimbursement_account")
    private String reimbursementAccount;

    @ApiModelProperty(value = "报销金额")
    @TableField(value = "reimbursement_amount")
    private BigDecimal reimbursementAmount;

    @ApiModelProperty(value = "备注")
    @TableField(value = "remark")
    private String remark;

    @ApiModelProperty(value = "标签")
    @TableField(value = "label")
    private String label;

    @ApiModelProperty(value = "地址")
    @TableField(value = "address")
    private String address;

    @ApiModelProperty(value = "创建用户的id")
    @TableField(value = "user")
    private Long user;

    @ApiModelProperty(value = "附件1的oss路径")
    @TableField(value = "attachment1")
    private String attachment1;

    @ApiModelProperty(value = "附件2的oss路径")
    @TableField(value = "attachment2")
    private String attachment2;

    @ApiModelProperty(value = "附件3的oss路径")
    @TableField(value = "attachment3")
    private String attachment3;

    @ApiModelProperty(value = "删除标记(0:不可用 1:可用)")
    @TableField(value = "is_deleted")
    @TableLogic
    private Boolean deleted;
}
