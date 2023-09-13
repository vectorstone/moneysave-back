package com.atguigu.accounting.service.impl;

import com.atguigu.accounting.entity.SysMenu;
import com.atguigu.accounting.entity.SysUser;
import com.atguigu.accounting.entity.SysUserRole;
import com.atguigu.accounting.entity.vo.RouterVo;
import com.atguigu.accounting.entity.vo.UserVo;
import com.atguigu.accounting.mapper.SysMenuMapper;
import com.atguigu.accounting.mapper.SysUserMapper;
import com.atguigu.accounting.mapper.SysUserRoleMapper;
import com.atguigu.accounting.service.SysUserService;

import com.atguigu.accounting.utils.MD5;
import com.atguigu.accounting.utils.MenuHelper;
import com.atguigu.accounting.utils.RouterHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Atguigu
 * @since 2023-08-03
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Resource
    SysMenuMapper sysMenuMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Resource
    RedisTemplate redisTemplate;
    @Autowired
    SysUserRoleMapper sysUserRoleMapper;

    @Override
    public Page<SysUser> getPageList(Integer page, Integer limit, UserVo userQueryVo) {
        Page<SysUser> userPage = new Page<>(page,limit);
        //构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery(SysUser.class);
        if(userQueryVo != null){
            if(StringUtils.isNotEmpty(userQueryVo.getCreateTimeBegin()) &&
                    StringUtils.isNotEmpty(userQueryVo.getCreateTimeEnd())){
                wrapper.lt(SysUser::getCreateTime,userQueryVo.getCreateTimeEnd())
                        .gt(SysUser::getCreateTime,userQueryVo.getCreateTimeBegin());
            }
            if(StringUtils.isNotEmpty(userQueryVo.getKeyword())){
                wrapper.and(t->{
                    t.or().like(SysUser::getName,userQueryVo.getKeyword())
                            .or().like(SysUser::getDescription,userQueryVo.getKeyword())
                            .or().like(SysUser::getPhone,userQueryVo.getKeyword())
                            .or().like(SysUser::getUsername,userQueryVo.getKeyword());
                });
            }
        }
        return this.page(userPage,wrapper);
    }

    //根据用户名查询用户信息
    @Override
    public SysUser getByUsername(String username) {
        return this.getOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUsername,username));
    }

    @Override
    public SysUser getInfo(String token) {
        /*
        根据token从redis中获取对应用户的id
        //根据token判断是否合法
        Asserts.AssertTrue(JwtUtils.checkToken(token), ResponseEnum.WEIXIN_FETCH_ACCESSTOKEN_ERROR);
        //token合法的话,根据token解析出对应的id
        Long userId = JwtUtils.getUserId(token); */
        // SysUser sysUser = (SysUser) redisTemplate.boundValueOps("token").get();
        SysUser sysUser = (SysUser)redisTemplate.boundValueOps(token).get();
        //根据id查询userInfo
        // SysUser sysUser = this.getById(userId);
        //返回userInfo前,需要将敏感信息清楚,避免信息泄露
        sysUser.setSalt(null);
        // sysUser.setBindCode(null);
        // sysUser.setIdCard(null);
        sysUser.setName(null);
        sysUser.setPassword(null);
        // sysUser.setMobile(null);
        //返回用户的信息,用来显示
        return sysUser;
    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        return null;
    }

    @Override
    public Map<String, Object> getUserInfoByUserId(Long userId) {

        //先创建一个map集合用来一会给controller返回map集合
        Map<String,Object> userInfoMap = new HashMap<>();

        //2.查询用户信息,并设置到map集合里面
        SysUser sysUser = sysUserMapper.selectById(userId);

        //2.1将用户的信息放进去
        userInfoMap.put("name",sysUser.getName());
        //2.2将用户的头像信息放进去
        userInfoMap.put("avatar",sysUser.getHeadUrl());
        //2.3将用户的角色信息放进去(因为还没有涉及到权限的事情,所以这里先放一个假的数据进去)
        userInfoMap.put("roles","[admin]");

        //3.查询用户的菜单信息(需要判断是不是管理员,如果是管理员的话直接查询所有,如果不是管理员的话需要进行多表查询)
        List<SysMenu> menuByUserId = getMenuByUserId(userId);

        //将获取到的用户的菜单构建成树形结构
        List<SysMenu> sysMenuList = MenuHelper.buildTree(menuByUserId);

        //将构建成树形的menuList再构建成前端所需要的router的结构
        List<RouterVo> routerVos = RouterHelper.buildRouters(sysMenuList);
        userInfoMap.put("routers",routerVos);

        //4.获取用户的按钮的信息
        List<String> btnPermissionByUserId = getBtnPermissionByUserId(userId);
        userInfoMap.put("buttons",btnPermissionByUserId);

        return userInfoMap;
    }
    //根据用户的id获取对应的菜单
    private List<SysMenu> getMenuByUserId(Long userId){
        //3.查询用户的菜单信息(需要判断是不是管理员,如果是管理员的话直接查询所有,如果不是管理员的话需要进行多表查询)
        //这里默认管理员的userId是1
        List<SysMenu> sysMenuList = new ArrayList<>();
        if(userId == 1){
            //进来里面说明是管理员,status得是1,还要排序
            QueryWrapper<SysMenu> sysMenuQueryWrapper = new QueryWrapper<>();
            sysMenuQueryWrapper.eq("status",1);
            sysMenuQueryWrapper.eq("is_deleted",0);
            sysMenuQueryWrapper.orderByAsc("sort_value");
            sysMenuList = sysMenuMapper.selectList(sysMenuQueryWrapper);
        }else{
            //进来这里面说明不是管理员
            sysMenuList = sysMenuMapper.getMenuByUserId(userId);
        }
        //将查询到的menuList构建成树形的结构,并返回,不能在这里构建成true,否则后面的btnPerm只能获取到一个list,查不到数据
        // return MenuHelper.buildTree(sysMenuList);
        return sysMenuList;
    }

    @Override
    public List<String> getUserBtnPersByUserId(Long userId) {
        //根据用户id获取对应的菜单的选项
        List<SysMenu> menuByUserId = getMenuByUserId(userId);
        //遍历得到对应的按钮的权限
        /*List<String> btnPermission = new ArrayList<>();
        menuByUserId.forEach(menu -> {
            if(menu.getType() == 2){
                btnPermission.add(menu.getPerms());
            }
        });*/
        //上面的那块代码的高级写法
        List<String> btnPermission = menuByUserId.stream().filter(menu -> menu.getType() == 2).map(SysMenu::getPerms).collect(Collectors.toList());

        return btnPermission;
    }

    //根据用户的id获取对应的按钮的权限
    @Override
    public List<String> getBtnPermissionByUserId(Long userId) {
        //根据用户id获取对应的菜单的选项
        List<SysMenu> menuByUserId = getMenuByUserId(userId);
        //遍历得到对应的按钮的权限
        /*List<String> btnPermission = new ArrayList<>();
        menuByUserId.forEach(menu -> {
            if(menu.getType() == 2){
                btnPermission.add(menu.getPerms());
            }
        });*/
        //上面的那块代码的高级写法
        List<String> btnPermission = menuByUserId.stream().filter(menu -> menu.getType() == 2).map(SysMenu::getPerms).collect(Collectors.toList());

        return btnPermission;
    }
    @Autowired
    PasswordEncoder passwordEncoder;
    @Override
    public void addUser(SysUser sysUser) {
        //防止传过来的对象里面带的有更新的时间
        sysUser.setUpdateTime(null);
        sysUser.setCreateTime(null);
        //把用户输入的密码生成盐并进行MD5加密
        //生成盐
        String salt = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        // String encryptPassword = MD5.encrypt(sysUser.getPassword());
        // String encryptPassword = MD5.encrypt(MD5.encrypt(sysUser.getPassword()) + salt);
        String encryptPassword = passwordEncoder.encode(sysUser.getPassword());
        sysUser.setPassword(encryptPassword);
        sysUser.setSalt(salt);

        //有的人不删除提示的那个内容,所以增加一个判断头像的链接中是否包含中文
        Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
        Matcher m = p.matcher(sysUser.getHeadUrl());
        if (StringUtils.isBlank(sysUser.getHeadUrl()) || m.find()){
            //如果用户没有设置头像的链接的话,那么就给设置一个默认值
            sysUser.setHeadUrl("https://obsidiantuchuanggavin.oss-cn-beijing.aliyuncs.com/img/20180629210546_CQARA.jpeg");
        }
        sysUser.setStatus(1);
        this.save(sysUser);
        //生成的用户的默认的角色为普通的用户  普通用户的角色id为1701904865820712961
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(sysUser.getId());
        sysUserRole.setRoleId(1701904865820712961L);
        sysUserRoleMapper.insert(sysUserRole);

    }
}
