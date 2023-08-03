package com.atguigu.accounting.utils;

import com.atguigu.accounting.utils.BusinessException;
import com.atguigu.accounting.result.ResponseEnum;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 6/30/2023 9:53 PM
 */
//这是个工具类,不需要注入ioc容器了
public class Asserts {
    //断言你是true,那么是true,代码可以正常继续往下执行,如果不是true,那么立马抛异常
    public static void AssertTrue(Boolean flag, ResponseEnum responseEnum){
        if(!flag){
            //只有是false的情况下,才会进来这里面
            throw new BusinessException(responseEnum);
        }
    }
    //断言你是false,那么是false,代码可以正常继续往下执行,如果不是false,那么立马抛异常
    public static void AssertNotTrue(Boolean flag, ResponseEnum responseEnum){
        if(flag){
            //只有是false的情况下,才会进来这里面
            throw new BusinessException(responseEnum);
        }
    }
    //断言是null
    public static void AssertNull(Object obj , ResponseEnum responseEnum){
        //断言你是null,如果是null,代码正常,如果不是null,立马抛异常
        AssertTrue(obj == null,responseEnum);
    }

    //断言不是null
    public static void AssertNotNull(Object obj,ResponseEnum responseEnum){
        //断言你不是null,如果是null,立马抛异常
        AssertTrue(obj != null,responseEnum);
    }
}
