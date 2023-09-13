package com.atguigu.accounting.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.atguigu.accounting.entity.Book;
import com.atguigu.accounting.entity.vo.BookVo;
import com.atguigu.accounting.service.BookService;
import com.atguigu.accounting.service.impl.BookServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 7/28/2023 10:50 PM
 */
@Slf4j
public class BookExcelDataListener implements ReadListener<BookVo> {
    //这个limit变量用来读20条就写入20条,避免一次读取太多,占用太多的内存
    private int limit = 20;
    //创建一个list集合,用来临时的存放要写入到数据库的内容
    private List<BookVo> bookVos = new ArrayList<>();
    BookService bookService;
    Long userId;
    //构造方法
    public BookExcelDataListener(BookServiceImpl bookService, Long userId) {
        this.bookService = bookService;
        this.userId = userId;
    }

    //每读取一行数据,就会执行一次下面的这个方法
    //开启事务管理,因为一次要保存的数据比较的多
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void invoke(BookVo bookVo, AnalysisContext analysisContext) {
        //判断一下,数据库中是否已经有,了,如果有,就不添加到集合中,没有的话才添加到集合中
        //id唯一,编码唯一
        if (isDuplicated(bookVo)){
            //进来这里面,说明这个数据,数据库中已经有了,直接返回
            log.info("提交的表格中有重复的内容,重复的内容为:{}",bookVo.toString());
            return;
        }
        //代码能执行到这里面,说明没有重复的,可以继续添加
        //将读取到的数据添加到临时的集合中
        bookVos.add(bookVo);
        if(bookVos.size() > limit){
            //如果数量超过了设置的阈值,那么就执行保存的操作
            batchSaveDictVos();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //最后一部分读取到数据,肯定是不超过20的,所以需要进行收尾工作
        if(bookVos.size()>0){
            batchSaveDictVos();
        }
    }

    //用来判断数据是否重复的业务逻辑代码(通过创建的时间来进行去重)
    private boolean isDuplicated(BookVo bookVo){
        LambdaQueryWrapper<Book> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Book::getCreateTime,bookVo.getCreateTime());
        /* //创建一个LambdaQueryWrapper的对象,省去了自己手写表的字段名
        LambdaQueryWrapper<BookVo> wrapper = Wrappers.lambdaQuery();
        //这里都是在创建查询的条件
        wrapper.eq(BookVo::getId,data.getUser()); //第一个条件,数据库中的数据 id = 要导入的excel表中的数据的id
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
        } */
        //如果查询出来的结果 > 0 说明有重复的
        return bookService.count(wrapper) > 0;
    }

    //将数据保存到数据库中的代码抽取出来,因为这个监听器中的两个方法都需要使用这部份逻辑代码
    private void batchSaveDictVos(){
        //将dictVos转换成Dict,并保存到新的集合中,需要dictVos和Dict一一对应
        List<Book> books = bookVos.stream().map(item -> {
            //创建一个Dict对象,用来接收dictVos里面的属性等相关的内容
            Book book = new Book();
            //将dictVo的属性值设置给dict对象,将相同名称+相同类型的属性值,拷贝给另一个对象
            BeanUtils.copyProperties(item,book);
            book.setUser(userId);//将用户的userId设置进去
            //自动填充没生效,所以需要我们手动生成创建时间和更新时间
            // book.setCreateTime(new Date());
            //subcategory有可能为空,所以这里需要增加一个判断
            if(StringUtils.isNotEmpty(book.getSubcategory())){
                //进来这里面说明subcategory一定不为空,直接执行转换
                return convertSubcategory(book);
            }
            //走到这里说明subcategory为空,那就继续保持为空好了
            return book;

        }).collect(Collectors.toList());
        //执行批量保存
        bookService.saveBatch(books);
        //保存完成后,清空临时存放数据的集合里面的缓存
        bookVos.clear();
    }
    //定义一个方法,用来转换账单数据里面的子类
    private Book convertSubcategory(Book book){
        String subcategory = null;
        switch (book.getSubcategory()){
            case "taxi":
                subcategory = "c taxi";
                break;
            case "保湿":
                subcategory = "hh 护肤-其它";
                break;
            case "保险":
                subcategory = "l 保险";
                break;
            case "吃喝-其他":
                subcategory = "aa 吃喝-其它";
                break;
            case "床上用品":
                subcategory = "e 床上用品";
                break;
            case "打牌":
                subcategory = "f 打牌";
                break;
            case "地铁":
                subcategory = "c 地铁";
                break;
            case "点外卖":
                subcategory = "a 点外卖";
                break;
            case "电费":
                subcategory = "d 电费";
                break;
            case "电影":
                subcategory = "f 电影";
                break;
            case "房租":
                subcategory = "d 房租";
                break;
            case "飞机":
                subcategory = "c 飞机";
                break;
            case "腐败-其他":
                subcategory = "ff 腐败-其它";
                break;
            case "共享单车":
                subcategory = "c 共享单车";
                break;
            case "高铁":
                subcategory = "c 高铁";
                break;
            case "公交":
                subcategory = "c 公交";
                break;
            case "过路费":
                subcategory = "c 过路费";
                break;
            case "话费":
                subcategory = "d 话费";
                break;
            case "家电":
                subcategory = "e 家电";
                break;
            case "家居用品":
                subcategory = "e 家居用品";
                break;
            case "剪头发":
                subcategory = "i 剪头发";
                break;
            case "奖金":
                subcategory = "n 奖金";
                break;
            case "酒品":
                subcategory = "a 酒品";
                break;
            case "零食":
                subcategory = "a 零食";
                break;
            case "买菜":
                subcategory = "a 买菜";
                break;
            case "奶制品":
                subcategory = "a 奶制品";
                break;
            case "内衣":
                subcategory = "b 内衣";
                break;
            case "培训":
                subcategory = "m 培训";
                break;
            case "情趣用品":
                subcategory = "e 情趣用品";
                break;
            case "软件-其他":
                subcategory = "gg 软件-其它";
                break;
            case "上衣":
                subcategory = "b 上衣";
                break;
            case "生活-其他":
                subcategory = "ee 生活-其它";
                break;
            case "数码内外设":
                subcategory = "e 数码内外设";
                break;
            case "水费":
                subcategory = "d 水费";
                break;
            case "水果":
                subcategory = "a 水果";
                break;
            case "甜点":
                subcategory = "a 甜点";
                break;
            case "同辈":
                subcategory = "j 同辈";
                break;
            case "晚辈":
                subcategory = "j 晚辈";
                break;
            case "晚饭":
                subcategory = "a 晚饭（外）";
                break;
            case "微软":
                subcategory = "g 微软";
                break;
            case "洗漱":
                subcategory = "e 洗漱";
                break;
            case "下载、云":
                subcategory = "g 下载、云";
                break;
            case "香烟":
                subcategory = "a 香烟";
                break;
            case "宵夜":
                subcategory = "a 宵夜";
                break;
            case "鞋子":
                subcategory = "b 鞋子";
                break;
            case "学费":
                subcategory = "m 学费";
                break;
            case "学习工具":
                subcategory = "m 学习工具";
                break;
            case "学习-其他":
                subcategory = "mm 学习-其它";
                break;
            case "药品":
                subcategory = "l 药品";
                break;
            case "衣物-其他":
                subcategory = "bb 衣物-其它";
                break;
            case "饮料":
                subcategory = "a 饮料";
                break;
            case "油费":
                subcategory = "c 油费";
                break;
            case "游戏产品":
                subcategory = "f 游戏产品";
                break;
            case "早饭":
                subcategory = "a 早饭（外）";
                break;
            case "长辈":
                subcategory = "j 长辈";
                break;
            case "知识类会员":
                subcategory = "g 知识类会员";
                break;
            case "中饭":
                subcategory = "a 中饭（外）";
                break;
            case "装饰用品":
                subcategory = "e 装饰用品";
                break;
        }
        book.setSubcategory(subcategory);
        return book;
    }
}
