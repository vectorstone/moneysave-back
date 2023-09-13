package com.atguigu.accounting.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum ResponseEnum {

    SUCCESS(0, "成功"),
    ERROR(-1, "服务器内部错误"),

    //-1xx 服务器错误
    BAD_SQL_GRAMMAR_ERROR(-101, "sql语法错误"),
    SERVLET_ERROR(-102, "servlet请求异常"), //-2xx 参数校验
    UPLOAD_ERROR(-103, "文件上传错误"),
    EXPORT_DATA_ERROR(104, "数据导出失败"),


    //-2xx 参数校验
    BORROW_AMOUNT_NULL_ERROR(-201, "借款额度不能为空"),
    BORROW_INFO_EXISTS_ERROR(-308,"您存在未完成的借款记录"),
    BORROW_INFO_STATUS_ERROR(-309,"借款信息状态异常"),
    MOBILE_NULL_ERROR(-202, "手机号码不能为空"),
    MOBILE_ERROR(-203, "手机号码不正确"),
    PASSWORD_NULL_ERROR(204, "密码不能为空"),
    CODE_NULL_ERROR(205, "验证码不能为空"),
    CODE_ERROR(206, "验证码错误"),
    MOBILE_EXIST_ERROR(207, "手机号已被注册"),
    LOGIN_MOBILE_ERROR(208, "用户不存在"),
    LOGIN_PASSWORD_ERROR(209, "密码错误"),
    LOGIN_LOCKED_ERROR(210, "用户被锁定"),
    LOGIN_AUTH_ERROR(-211, "未登录"),
    USER_EXSIT_ERROR(-222, "用户名已存在"),

    USER_TYPE_ERROR(-302, "用户类型错误"),

    USER_BIND_IDCARD_EXIST_ERROR(-301, "身份证号码已绑定"),
    USER_NO_BIND_ERROR(302, "用户未绑定"),
    USER_NO_AMOUNT_ERROR(303, "用户信息未审核"),
    USER_AMOUNT_LESS_ERROR(304, "您的借款额度不足"),
    LEND_INVEST_ERROR(305, "当前状态无法投标"),
    LEND_FULL_SCALE_ERROR(306, "已满标，无法投标"),
    LEND_AMOUNT_ERROR(308, "投资金额不正确"),
    NOT_SUFFICIENT_FUNDS_ERROR(307, "余额不足，请充值"),
    WITHDRAW_FUNDS_ERROR(309, "提现金额有误"),
    LEND_RETURN_ERROR(400,"请按照顺序还款"),
    LEND_RETURN_USER_ERROR(402,"选择了错误的还款项目"),

    PAY_UNIFIEDORDER_ERROR(401, "统一下单错误"),

    ALIYUN_SMS_LIMIT_CONTROL_ERROR(-502, "短信短时间内发送过于频繁"),//业务限流
    ALIYUN_SMS_ERROR(-503, "短信发送失败"),//其他失败

    ALIYUN_SMS_COUNT_CONTROL_ERROR(-504, "短信发送失败,一天内次数超过限制"),//次数超过限制

    WEIXIN_CALLBACK_PARAM_ERROR(-601, "回调参数不正确"),
    WEIXIN_FETCH_ACCESSTOKEN_ERROR(-602, "获取access_token失败"),
    WEIXIN_FETCH_USERINFO_ERROR(-603, "获取用户信息失败"),

    //666异常 arithmetic exception
    ARITHMETIC_DIVIDE_ZERO_ERROR(666,"0不能做除数,你可真是个大聪明"),

    NOT_TRUE_ERROR(667,"数据不为真"),
    NOT_FALSE_ERROR(668,"数据不为假"),
    NOT_NULL_ERROR(669,"数据不为空"),
    DATA_NULL_ERROR(700,"数据为空"),
    SAVE_FAIL(701,"保存失败"),
    UPDATE_FAIL(702,"修改失败"),
    HFB_BIND_FAIL(704,"汇付宝账户绑定失败"),
    HFB_MAKE_LOAN_FAIL(705,"汇付宝放款失败"),
    DELETE_FAIL(703,"删除失败"),

    //=======================
    FAIL(2010, "失败"),
    SERVICE_ERROR(2012, "服务异常"),
    DATA_ERROR(2040, "数据异常"),
    ILLEGAL_REQUEST(2050, "非法请求"),
    REPEAT_SUBMIT(2060, "重复提交"),
    ARGUMENT_VALID_ERROR(2100, "参数校验异常"),
    LOGIN_AUTH(2080, "未登陆"),
    PERMISSION(2090, "没有权限"),
    ACCOUNT_ERROR(2140, "账号不正确"),
    PASSWORD_ERROR(2150, "密码不正确"),
    LOGIN_MOBLE_ERROR(2160, "账号不正确"),
    ACCOUNT_STOP(2170, "账号已停用"),
    NODE_ERROR(2180, "该节点下有子节点，不可以删除");
    //=======================

    private Integer code;//状态码
    private String message;//消息
}
