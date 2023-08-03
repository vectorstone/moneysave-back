package com.atguigu.accounting.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 6/30/2023 7:49 PM
 */
//配置类,必须写@Configuration
@Configuration
public class MybatisPlusConfig {
    //下面的是分页相关的拦截器
    @Bean
    public MybatisPlusInterceptor interceptor(){
        //创建了一个总的拦截器
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        //分页拦截器
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        //Overflow为分页合理化相关的配置
        //分页合理化 查询小于1的页码时,返回第一页的数据
        //查询大于最大页码时,返回最后一页的数据
        paginationInnerInterceptor.setOverflow(true);
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor);
        //乐观锁拦截器
        //mp乐观锁 : 更新数据时会自动先判断版本号是否和数据库数据一致,如果一致
        //那么对版本号+1 同时进行更新操作,否则更新失败
        //这个和redis里面的事务管理很像,也和git里面的版本冲突很像
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}
