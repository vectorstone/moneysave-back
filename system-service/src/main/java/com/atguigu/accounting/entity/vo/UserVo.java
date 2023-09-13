package com.atguigu.accounting.entity.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author Atguigu
 * @since 2023-08-03
 */
@Data
@ApiModel(value="userVo", description="用户查询vo类")
public class UserVo {
    private String keyword;
    private String createTimeBegin;
    private String createTimeEnd;
}
