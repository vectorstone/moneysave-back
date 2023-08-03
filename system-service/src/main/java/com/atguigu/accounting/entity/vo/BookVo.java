package com.atguigu.accounting.entity.vo;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 7/28/2023 9:03 PM
 */
@Data
@ApiModel("账单表对应的类")
public class BookVo {
    @ApiModelProperty(name = "createTime",value = "创建日期")
    @ExcelProperty(index = 0,value = "创建日期")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm")
    private Date createTime;

    @ApiModelProperty(name = "type",value = "收支类型")
    @ExcelProperty(index = 1,value = "收支类型")
    private String type;

    @ApiModelProperty(name = "amount",value = "金额")
    @ExcelProperty(index = 2,value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(name = "category",value = "类别")
    @ExcelProperty(index = 3,value = "类别")
    private String category;

    @ApiModelProperty(name = "subcategory",value = "子类")
    @ExcelProperty(index = 4,value = "子类")
    private String subcategory;

    @ApiModelProperty(name = "account",value = "账户")
    @ExcelProperty(index = 5,value = "账户")
    private String account;

    @ApiModelProperty(name = "book",value = "账本名称")
    @ExcelProperty(index = 6,value = "账本名称")
    private String book;

    @ApiModelProperty(name = "reimbursementAccount",value = "报销账户")
    @ExcelProperty(index = 7,value = "报销账户")
    private String reimbursementAccount;

    @ApiModelProperty(name = "reimbursementAmount",value = "报销金额")
    @ExcelProperty(index = 8,value = "报销金额")
    private BigDecimal reimbursementAmount;

    @ApiModelProperty(name = "remark",value = "备注")
    @ExcelProperty(index = 9,value = "备注")
    private String remark;

    @ApiModelProperty(name = "label",value = "标签")
    @ExcelProperty(index = 10,value = "标签")
    private String label;

    @ApiModelProperty(name = "address",value = "地址")
    @ExcelProperty(index = 11,value = "地址")
    private String address;

    @ApiModelProperty(name = "user",value = "创建用户的id")
    @ExcelProperty(index = 12,value = "创建用户的id")
    private Long user;

    @ApiModelProperty(name = "attachment1",value = "附件1的oss路径")
    @ExcelProperty(index = 13,value = "附件1的oss路径")
    private String attachment1;

    @ApiModelProperty(name = "attachment2",value = "附件2的oss路径")
    @ExcelProperty(index = 14,value = "附件2的oss路径")
    private String attachment2;

    @ApiModelProperty(name = "attachment3",value = "附件3的oss路径")
    @ExcelProperty(index = 15,value = "附件3的oss路径")
    private String attachment3;
}
