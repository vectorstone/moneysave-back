package com.atguigu.accounting.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 7/30/2023 7:05 PM
 */
@Data
@ApiModel("接收前端分类的数据字典数据模型")
public class DictExcelVo {
    @ApiModelProperty(value = "id")
    @ExcelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "上级id")
    @ExcelProperty(value = "上级id")
    private Long parentId;

    @ApiModelProperty(value = "名称")
    @ExcelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "值")
    @ExcelProperty(value = "值")
    private Integer value;

    @ApiModelProperty(value = "编码")
    @ExcelProperty(value = "编码")
    private String dictCode;
}
