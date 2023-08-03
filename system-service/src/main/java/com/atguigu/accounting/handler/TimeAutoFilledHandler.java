package com.atguigu.accounting.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 6/30/2023 7:11 PM
 */
@Component // 注入容器中,在使用的时候直接调用即可
public class TimeAutoFilledHandler implements MetaObjectHandler {
    // insertFill为新增时会出发的回调方法 : 如果执行新增操作的对象,有一个属性标注了填充时机为insert时会触发
    @Override
    public void insertFill(MetaObject metaObject) {
        //metaObject表示正在执行新增的对象
        Object createTime = this.getFieldValByName("createTime", metaObject);
        if(createTime == null && metaObject.hasSetter("createTime")){
            //获取到的createTime为空,并且有set方法的时候,出发设置创建时间
            metaObject.setValue("createTime",new Date());
        }

        /* if(metaObject.hasSetter("createTime")){
            //新增的对象必须有这个属性或字段才会触发下面的设置创建时间的代码
            metaObject.setValue("createTime",new Date());
        } */
        //新增时还会创建updateTime
        if(metaObject.hasSetter("updateTime")){
            //同样的,新增的对象必须有这个属性或字段才会触发下面的代码
            metaObject.setValue("updateTime",new Date());
        }
    }

    //updateFill为更新时会触发的回调方法 : 如果执行更新操作的对象,有一个属性标注了填充时机为update时才会触发
    @Override
    public void updateFill(MetaObject metaObject) {
        if(metaObject.hasSetter("updateTime")){
            //需要更新updateTime的对象必须要有这个属性或字段才会触发下面的代码
            metaObject.setValue("updateTime",new Date());
        }
    }
}
