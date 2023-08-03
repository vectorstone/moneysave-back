package com.atguigu.accounting.mapper;

import com.atguigu.accounting.entity.Book;
import com.atguigu.accounting.entity.vo.MonthCostVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Atguigu
 * @since 2023-07-28
 */
public interface BookMapper extends BaseMapper<Book> {

    List<MonthCostVo> getMonthlyCost(@Param("start") String start,@Param("end") String end);

    List<Book> getAllData();
}
