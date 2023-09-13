package com.atguigu.accounting.handler;

import com.atguigu.accounting.result.ResponseEnum;
import com.atguigu.accounting.utils.BusinessException;
import com.atguigu.accounting.result.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
// import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 6/30/2023 9:19 PM
 */
@RestControllerAdvice //相当于@RestController + @ControllerAdvice 将异常处理后的结果转为json响应
@Slf4j
public class GlobalException {
    //先来定义一个兜底的异常处理器
    @ExceptionHandler(value = Exception.class)
    public R exception(Exception e){
        //获取异常对象的堆栈日志转为字符串
        log.error("出现异常:{}", ExceptionUtils.getStackTrace(e));
        //这个异常的范围太大了,没法输出具体的信息给前端,先扔个最基本的错误给前端
        return R.error();
    }
    //自定义异常
    @ExceptionHandler(value = BusinessException.class)
    public R businessException(BusinessException e){
        //将获取到的异常对象的堆栈日志转为字符串
        log.error("出现异常:{}",ExceptionUtils.getStackTrace(e));
        return R.error().code(e.getCode()).message(e.getMessage());
    }

    /**
     * Controller上一层相关异常
     */
    @ExceptionHandler({
            NoHandlerFoundException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            MethodArgumentNotValidException.class,
            HttpMediaTypeNotAcceptableException.class,
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            MissingServletRequestPartException.class,
            AsyncRequestTimeoutException.class
    })
    public R handleServletException(Exception e) {
        log.error(e.getMessage(), e);
        //SERVLET_ERROR(-102, "servlet请求异常"),
        return R.error().message(ResponseEnum.SERVLET_ERROR.getMessage()).code(ResponseEnum.SERVLET_ERROR.getCode());
    }

    /**
     * spring security异常
     * @param e
     * @return
     */
    /* @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public R error(AccessDeniedException e) throws AccessDeniedException {
        // return Result.build(null, ResultCodeEnum.PERMISSION);
        return R.setResult(ResponseEnum.PERMISSION);
    } */
}
