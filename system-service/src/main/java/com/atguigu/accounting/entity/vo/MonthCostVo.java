package com.atguigu.accounting.entity.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 8/2/2023 10:37 PM
 * cdate     cost
 * 2023-06   4039.04
 * 2023-05   4888.01
 * 2023-04   6119.79
 */
@ApiModel("用来接收数据库查询出来的月份和每月支出的数据模型")
@Data
public class MonthCostVo {
    private String cdate;
    private BigDecimal cost;
}
