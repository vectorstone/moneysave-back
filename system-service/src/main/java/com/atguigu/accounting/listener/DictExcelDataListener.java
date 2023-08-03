package com.atguigu.accounting.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.accounting.entity.Dict;
import com.atguigu.accounting.entity.vo.DictExcelVo;

import com.atguigu.accounting.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/*
 * @Description:
 * @Author: Gavin
 * @Date: 7/4/2023 8:27 AM
 * */


@Slf4j
public class DictExcelDataListener extends AnalysisEventListener<DictExcelVo> {
    //这个limit变量用来读20条就写入20条,避免一次读取太多,占用太多的内存
    private int limit = 20;
    //创建一个list集合,用来临时的存放要写入到数据库的内容
    private List<DictExcelVo> dictVos = new ArrayList<>();
    //手动装配dictService对象
    private DictService dictService;

    //创建一个带参的构造器,将容器中的dictService对象装配进来
    public DictExcelDataListener(DictService dictService){
        this.dictService = dictService;
    }

    //每读取一行数据,就会执行一次下面的这个方法
    //开启事务管理,因为一次要保存的数据比较的多
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void invoke(DictExcelVo data, AnalysisContext analysisContext) {
        //判断一下,数据库中是否已经有,了,如果有,就不添加到集合中,没有的话才添加到集合中
        //id唯一,编码唯一
        if (isDuplicated(data)){
            //进来这里面,说明这个数据,数据库中已经有了,直接返回
            log.info("提交的表格中有重复的内容,重复的内容为:{}",data.toString());
            return;
        }
        //代码能执行到这里面,说明没有重复的,可以继续添加
        //将读取到的数据添加到临时的集合中
        dictVos.add(data);
        if(dictVos.size() > limit){
            //如果数量超过了设置的阈值,那么就执行保存的操作
            batchSaveDictVos();
        }
    }
    //用来判断数据是否重复的业务逻辑代码
    private boolean isDuplicated(DictExcelVo data){
        //创建一个LambdaQueryWrapper的对象,省去了自己手写表的字段名
        LambdaQueryWrapper<Dict> wrapper = Wrappers.lambdaQuery();
        //这里都是在创建查询的条件
        wrapper.eq(Dict::getId,data.getId()); //第一个条件,数据库中的数据 id = 要导入的excel表中的数据的id
        // if(data.getParentId().equals(1L) ){
        if(data.getParentId().intValue() == 1 ){ //parentId是Long类型的,将其转换为int类型的再来进行比较
           //进来这里面,说明是parentId为1的数据
            //select count(id) from dict where id = data.getId() or dictCode = data.getDictCode()
            wrapper.or().eq(Dict::getDictCode,data.getDictCode());
        }else{
            //进来这里面,说明parentId不为1的数据
            //select count(id) from dict where id = data.getId() or name = data.getName() or (value = data.getValue and parentId = data.getParentId)
            wrapper.or().eq(Dict::getName,data.getName());
            wrapper.or(w -> w.eq(Dict::getValue,data.getValue()).eq(Dict::getParentId,data.getParentId()));
        }
        //如果查询出来的结果 > 0 说明有重复的
        return dictService.count(wrapper) > 0;
    }


    //当所有的数据全部读取完成的时候,会执行下面的这个方法
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //最后一部分读取到数据,肯定是不超过20的,所以需要进行收尾工作
        if(dictVos.size()>0){
            batchSaveDictVos();
        }
    }

    //将数据保存到数据库中的代码抽取出来,因为这个监听器中的两个方法都需要使用这部份逻辑代码
    private void batchSaveDictVos(){
        //将dictVos转换成Dict,并保存到新的集合中,需要dictVos和Dict一一对应
        List<Dict> dicts = dictVos.stream().map(item -> {
            //创建一个Dict对象,用来接收dictVos里面的属性等相关的内容
            Dict dict = new Dict();
            //将dictVo的属性值设置给dict对象,将相同名称+相同类型的属性值,拷贝给另一个对象
            BeanUtils.copyProperties(item,dict);
            //自动填充没生效,所以需要我们手动生成创建时间和更新时间
            dict.setCreateTime(new Date());
            dict.setUpdateTime(new Date());
            return dict;
        }).collect(Collectors.toList());
        //执行批量保存
        dictService.saveBatch(dicts);
        //保存完成后,清空临时存放数据的集合里面的缓存
        dictVos.clear();
    }
}
