package com.atguigu.accounting.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.atguigu.accounting.entity.Dict;
import com.atguigu.accounting.entity.vo.DictExcelVo;
import com.atguigu.accounting.listener.DictExcelDataListener;
import com.atguigu.accounting.mapper.DictMapper;
import com.atguigu.accounting.result.ResponseEnum;
import com.atguigu.accounting.service.DictService;
import com.atguigu.accounting.utils.Asserts;
import com.atguigu.accounting.utils.BusinessException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author Atguigu
 * @since 2023-07-30
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Resource
    DictService dictService;
    @Override
    public void importDict(MultipartFile file) {
        // 校验判断文件是否合规(文件必须存在,后缀,文件的大小)
        // 判断文件的后缀是否合规
        boolean flag = file.getOriginalFilename().toLowerCase().endsWith(".xls") ||
                file.getOriginalFilename().toLowerCase().endsWith(".xlsx") ||
                file.getOriginalFilename().toLowerCase().endsWith(".cvs");
        Asserts.AssertTrue( flag, ResponseEnum.UPLOAD_ERROR );
        // 判断文件的大小不可以为0 断言文件的大小必须 > 0 ,如果小于0,立马抛异常
        Asserts.AssertTrue(file.getSize() > 0, ResponseEnum.UPLOAD_ERROR);
        // 判断文件必须存在
        Asserts.AssertNotNull(file, ResponseEnum.DATA_NULL_ERROR);


        // 文件上传的核心业务代码
        try {
            // 使用MultipartFile的输入流来读取文件
            EasyExcel.read(file.getInputStream())
                    .head(DictExcelVo.class)
                    .sheet(0)
                    .registerReadListener(new DictExcelDataListener(this))
                    .doRead();
        } catch (Exception e) { // 放大异常的类型
            // 将异常的信息记录到日志文件中
            log.error("出异常了,异常信息为:{}" + ExceptionUtils.getStackTrace(e));
            // 抛出我们自定义的异常
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR);
        }
    }
    // 下载文件的方法
    @Override
    public void exportDicts(HttpServletResponse response) {
        // 1.先查询数据库中的字典文件,然后将其转为DictExcelVo类型的对象
        List<DictExcelVo> dictVos = dictService.list().stream().map(dict -> {
            DictExcelVo dictVo = new DictExcelVo();
            // 使用工具类,将查询出来的对象的属性转换为Dict类型的对象
            BeanUtils.copyProperties(dict, dictVo);
            return dictVo;
        }).collect(Collectors.toList());
        // 在本地的时候,我们可以将数据的集合写入到一个excel文件中
        // 但是通过浏览器的下载,我们需要将数据集合写入到一个内存中的excel文件中再通过输出流写个浏览器

        try {
            // 配置响应头,告诉浏览器应该如何解析响应体中的数据流
            response.setHeader("content-disposition", "attachment;filename=dicts" +
                    new DateTime().toString("yyyyMMdd") + ExcelTypeEnum.XLSX.getValue());

            // 将字典文件集合以流的方式写入到响应体中
            EasyExcel.write(response.getOutputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .head(DictExcelVo.class)
                    .sheet(0)
                    .doWrite(dictVos);
        } catch (Exception e) {
            log.error("数据字典文件下载异常,异常信息为:{}" + e.getStackTrace());
            throw new BusinessException(ResponseEnum.EXPORT_DATA_ERROR);
        }
    }

    // 拼接redis里面的key value写key的前缀  key的值可以从方法的形参列表里面获取,还可以使用表达式获取级联属性

    // @Cacheable(value = "srb:dicts", key = "'cache:'+#pid") // 最终的效果是 srb:dicts::cache:1
    @Override
    public List<Dict> getByPid(Long pid) {
        /*
        使用框架提供的缓存管理器,所以这部份代码不需要我们自己写了
        //增加redis来做缓存
        Object dicts = redisTemplate.opsForValue().get("srb:dicts:" + pid);
        //如果根据key能从redis中查询到结果,则直接返回对应的结果
        if(dicts != null){
            return (List<Dict>) dicts;
        } */
        // 如果redis缓存中查询不到结果,则老老实实的去数据库中查询
        //----------------------------
        // 根据pid查询出来dict集合
        List<Dict> list = dictService.list(Wrappers.lambdaQuery(Dict.class).eq(Dict::getParentId, pid));

        // 为每个dict设置hasChildren属性
        list.stream().map(dict -> {
            // 查询是否有其他的数据parentId = dict的id 如果成立,说明此时的dict存在children
            dict.setHasChildren(dictService.count(Wrappers.lambdaQuery(Dict.class).eq(Dict::getParentId, dict.getId())) > 0);
            return dict;
        }).collect(Collectors.toList());
        //-----------------------------

        // 在return查询的结果前,将查询出来的结果存到redis的缓存中 0705更新,使用框架提供的缓存管理器,所以这部份代码不需要了
        // redisTemplate.opsForValue().set("srb:dicts:" + pid,list,60, TimeUnit.MINUTES);
        return list;
    }

    // 新增数据字典的方法
    // 新增一个数据字典,为了实现同步功能,需要将这个数据字典的父字典全部从缓存中删除,保持数据的同步
    // @CacheEvict(value = "srb:dicts", key = "'cache:'+#dict.parentId")
    @CacheEvict(value = "srb:dicts",allEntries=true) //所有名叫srb:dicts的缓存都被清理掉
    @Override
    public void saveWithSync(Dict dict) {
        // 这里的this指的就是dictService自己本身
        this.save(dict);
    }

    // 根据id删除数据字典的方法
    // 同理,需要保持redis缓存和数据库的同步
    // @CacheEvict(value = "srb:dicts", key = "'cache:'+#dict.parentId")
    @CacheEvict(value = "srb:dicts",allEntries=true) //所有名叫srb:dicts的缓存都被清理掉
    @Override
    public void removeByIdWithSync(Dict dict) {
        this.removeById(dict.getId());
    }

    // 根据id更新数据字典
    // @CacheEvict(value = "srb:dicts", key = "'cache:'+#dict.parentId")
    @CacheEvict(value = "srb:dicts",allEntries=true) //所有名叫srb:dicts的缓存都被清理掉
    @Override
    public void updateByIdWithSync(Dict dict) {
        this.updateById(dict);
    }

    // @Cacheable(value = "srb:dicts", key = "'cache:borrow'") // 最终的效果是 srb:dicts::cache:borrow
    @Override
    public Map<String, Object> getDictList() {
        Map<String,Object> map = new HashMap<>();
        //先获取所有的父节点组成的集合
        List<Dict> fatherDict = getByPid(1L);
        fatherDict.forEach(dict -> {
            if(dict.getHasChildren()){
                //进来这里面,说明是父节点,而且有下一级
                //将该父节点下的每一个子节点都查出来
                //将父节点的dictCode当作key,子节点dict组成的集合当作value,存入map中
                map.put(dict.getDictCode(), this.getByPid(dict.getId()));
            }
        });
        return map;
    }

    // @Cacheable(value = "srb:dicts", key = "'cache:'+#dictCode")
    @Override
    public List<Dict> listByDictCode(String dictCode) {
        return baseMapper.listByDictCode(dictCode);
    }


}
