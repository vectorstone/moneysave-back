[前端项目](https://github.com/vectorstone/moneysave-front-authenticate.git)
[后端项目](https://github.com/vectorstone/moneysave-back.git)
# 使用说明
## 背景:
家庭记账项目属于自己开源的一个独立制作的项目，适合于家庭内部使用，成员之间的记账数据实现了隔离处理，支持将市面上一些记账app的数据一键导入功能，利用Echarts展示分析图表，部署简单，方便家庭内部进行支出统计和分析。
主要包含了权限管理模块、Excel数据导入导出模块、Echarts图表展示模块、账单的增删改查模块。

![](https://obsidiantuchuanggavin.oss-cn-beijing.aliyuncs.com/img/capture20230920153703.gif)
## 登录和注册
可以登录和注册
![](https://obsidiantuchuanggavin.oss-cn-beijing.aliyuncs.com/img/Pasted%20image%2020230920154151.png)
## 首页
登录后默认以月为单位展示过去半年内的消费支出情况
可以选择时间段查看支出的趋势图(选择完时间后,点击绿色的v实现数据的查询以及图表的绘制)
![](https://obsidiantuchuanggavin.oss-cn-beijing.aliyuncs.com/img/Pasted%20image%2020230920154247.png)

点击click me,surprise会将用户的所有的数据以大比例海尺图的形式展示
![](https://obsidiantuchuanggavin.oss-cn-beijing.aliyuncs.com/img/Pasted%20image%2020230920154337.png)

![](https://obsidiantuchuanggavin.oss-cn-beijing.aliyuncs.com/img/Pasted%20image%2020230920154418.png)

共有六级菜单
![](https://obsidiantuchuanggavin.oss-cn-beijing.aliyuncs.com/img/Pasted%20image%2020230920154442.png)

管理员具有修改用户的权限,普通的用户只能操作自己的数据,并且每个用户之间的数据互相隔离,保证安全和隐私

## 账单页面
账单页面可以实现将excel数据导入本系统中,也可以下载本系统中的所有的数据为excel格式
可以对账单进行增删改查
![](https://obsidiantuchuanggavin.oss-cn-beijing.aliyuncs.com/img/Pasted%20image%2020230920154604.png)
## 字典管理页面
字典管理页面中包含了所有的账单的分类信息,普通的用户仅仅可以新增分类,无法修改和删除分类
同样可以通过上传的形式将excel表格中的分类数据导入到本系统中
![](https://obsidiantuchuanggavin.oss-cn-beijing.aliyuncs.com/img/Pasted%20image%2020230920154702.png)
## excel上传功能注意事项
excel请按照后端项目根目录下的accountBook.xlsx和categoryDict.xlsx的格式,否则会上传失败
## 数据分析页面
该页面中的内容和首页里面的内容几乎一致
## 菜单,角色,用户管理
管理员可以根据实际的需要进行相对应的修改
# 项目部署
## 说明
初始状态只有admin一个管理员用户,默认密码: 123456
## 环境需求
JDK1.8 , Maven3.6.3 , MySQL8.0 , Redis
## 配置文件修改
将文件clone到本地后请修改如下的配置文件
system-service/src/main/resources/application.yaml
### 修改MySQL数据库连接信息
配置文件中我使用了jasypt来保护敏感信息,建议大家都这么做,具体的使用步骤参考我的这篇post
[jasypt加密工具的使用](http://wswxgpp.eu.org/2023/09/07/springboot%E9%A1%B9%E7%9B%AE%E4%B8%AD%E9%81%BF%E5%85%8D%E6%9A%B4%E9%9C%B2%E6%95%8F%E6%84%9F%E4%BF%A1%E6%81%AF%E7%9A%84%E6%96%B9%E6%B3%95/)
### 修改redis数据库的连接信息
也使用了加密的处理,参考上面加密工具的使用来进行设置

## 前端项目部署
clone下来前端项目后,在项目的根目录下执行如下命令:
```sh
npm run build:prod # 打包的时候运行的命令
```
之后会在目标的targer文件夹里面生成对应的打包好的文件(后面这些文件需要上传到nginx的html目录里面)
## nginx
需要部署nginx反向代理服务器,通过docker的方式或者编译安装的方式都可以,使用的过程中没有什么区别
### nginx配置文件如下
```nginx
server {
	listen 80;
	server_name 服务器的ip地址;

client_max_body_size 20m;

	location / {
			root /var/www/html;
			index index.html index.htm;
	}


  location /prod-api/ {
  		#root /www/vod;
  		#index index.html index.htm;
      proxy_pass http://127.0.0.1:8888/;
  }


	error_page 500 502 503 504 /50x.html;
	location = /50x.html{
			root html;
	}
}

```

## 数据库表
请将项目根目录下的SQL脚本导入到你的本地的数据库中
