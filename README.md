# skyeye

#### 项目介绍

win10风格的一套系统，前端采用layui作为前端框架，后端采用SpringBoot作为服务框架，采用自封装的xml对所有请求进行参数校验，以保证接口安全性。</br>

> 系统新增传统风格界面，layui左菜单右内容风格。

|项目|地址|
|-------|-------|
|主项目地址|https://gitee.com/doc_wei01_admin/skyeye|
|APP端接口微服务地址|https://gitee.com/doc_wei01_admin/app-oaserver|
|APP端地址|https://gitee.com/doc_wei01_admin/oa-app|
|小程序端地址|https://gitee.com/doc_wei01_admin/small-pro|

> 该项目遵守MIT许可证，证书见`附件`

> APP端开始开发，前端采用VUE，后端采用SpringCloud，APP访问地址：[https://gitee.com/doc_wei01_admin/oa-app](https://gitee.com/doc_wei01_admin/oa-app "https://gitee.com/doc_wei01_admin/oa-app")

`项目持续更新，欢迎进群讨论`

##### 启动方式

直接运行com.skyeye.SkyEyeApplication即可，启动完成后，访问http://localhost:8081 即可。
初始化账号密码：`root/123456`

### OA主项目结构

- |----skyeye-promote---------------------OA主项目[端口：**8081**]
- - |----skyeye-web---------------------web层项目
- - |----skyeye-service-impl--------------Service服务层
- - |----skyeye-service-------------------ServiceImpl服务层接口
- - |----skyeye-dao----------------------Mapper层接口
- - |----skyeye-common------------------工具层
- - |----skyeye-activiti--------------------工作流
- - |----skyeye-entity---------------------部分操作实体类
- - |----skyeye-guacamole----------------远程控制桌面（开发中）
- - |----skyeye-mq-----------------------ActiveMq消息中间件
- - |----skyeye-quartz--------------------定时任务
- - |----skyeye-redis----------------------redis缓存
- - |----skyeye-websocket-----------------webSocket双工通讯
- - |----skyeye-wx------------------------微信接口层

## 子系统

### OA消息系统项目结构

- |----message-----------------------------消息系统[端口：**8084**]
- - |----message-web---------------------web层项目
- - |----message-service-impl--------------Service服务层
- - |----message-service-------------------ServiceImpl服务层接口
- - |----message-dao----------------------Mapper层接口
- - |----message-common------------------工具层
- - |----message-mq-----------------------ActiveMq消息中间件
- - |----message-quartz--------------------定时任务
- - |----message-redis----------------------redis缓存
- - |----message-websocket-----------------webSocket双工通讯

### OA门户后台管理系统项目结构

- |----gateway-promote---------------------消息系统[端口：**8083**]
- - |----gateway-web---------------------web层项目
- - |----gateway-service-impl--------------Service服务层
- - |----gateway-service-------------------ServiceImpl服务层接口
- - |----gateway-dao----------------------Mapper层接口
- - |----gateway-common------------------工具层
- - |----gateway-entity---------------------部分操作实体类
- - |----gateway-mq-----------------------ActiveMq消息中间件
- - |----gateway-quartz--------------------定时任务
- - |----gateway-redis----------------------redis缓存
- - |----gateway-websocket-----------------webSocket双工通讯

### OA门户展示系统项目结构

- |----gateway-------------------------门户展示系统[端口：**8082**]
- - |----gtw-web---------------------web层项目
- - |----gtw-service-impl--------------Service服务层
- - |----gtw-service-------------------ServiceImpl服务层接口
- - |----gtw-common-----------------工具层
- - |----gtw-redis---------------------redis缓存

#### 服务器部署注意事项

1.ActiveMQ链接地址、账号、密码的修改<br />
2.Redis集群的修改<br />
3.MySQL数据库链接地址、账号、密码的修改<br />
4.webSocket的IP地址修改<br />
5.图片资源路径存储的修改<br />

#### 本地开发环境搭建

- windows搭建nginx负载均衡（[下载](https://download.csdn.net/download/doc_wei/11010749)）
- windows搭建activemq单机版（[下载](https://download.csdn.net/download/doc_wei/11010746)）
- windows搭建redis集群（[下载](https://download.csdn.net/download/doc_wei/11010741)）

##### 注意事项

如果是eclipse导入发现pom文件报错。<br />
错误：org.apache.maven.archiver.MavenArchiver.getManifest<br />
解决办法：https://blog.csdn.net/doc_wei/article/details/84936514<br />

#### 功能介绍

功能|简介|功能|简介
-------|-------|-------|-------
菜单管理|管理系统中的菜单和权限点|员工管理|管理系统中的员工
用户管理|管理所有系统的登录用户|角色管理|管理系统中的所有角色
权限管理|给角色进行赋权|资源图标|系统中允许使用的font图标库
日志管理|所有接口请求信息|APP菜单管理|手机端菜单以及权限管理
多桌面管理|多个桌面程序，用户可通过鼠标滚动进行切换|系统基础设置|系统的基础信息设置（考勤事件，邮箱信息等）
代码生成器|只能适用于该框架的代码生成器，配置模板即可生成，然后下载压缩包解压复制到项目中即可|小程序管理|微信小程序、H5手机自适应页面拖拽生成，可自定义配置小程序组件
在线性能监控|监控jvm缓存、redis集群信息等|流程图规划|规划项目的流程图
问卷调查|拖拽式生成问卷，可分页、复制、查看统计信息等|多桌面|[演示](https://www.bilibili.com/video/av43650484)
聊天功能|[演示](https://www.bilibili.com/video/av43650782)|我的日程|[演示](https://www.bilibili.com/video/av45854959)
自定义桌面菜单|用户可将自己常用的网站添加到系统中方便记录|多系统集成(应用商店)|可以将多个系统进行应用集成，无需多次登陆，无需记录多个网址
轻应用|系统中提供各种小应用，如快递查询、高德地图等，用户可添加到自己的桌面上|开发文档|系统支持二次开发，包含开发文档
工作日志|记录每个员工的日报，周报，月报等，可同时发送多人，按时间轴查看等|考勤管理|记录每个员工的考勤打卡信息，包含报表
我的笔记|员工可记录自己日常的笔记，目前支持MD，富文本，表格操作|报表管理|统计功能信息，可根据客户自定义免费定制
文件管理|公司内部、员工个人的文件管理，支持多格式文件在线查看，文档多人协作，在线解压缩等|附件管理|保留员工所有上传过的附件，方便下次使用
邮件管理|目前打通与QQ邮箱的交互，可以发邮件，收邮件，保存为草稿等|工作流管理|动态表单结合工作流生成自定义业务流程审核
论坛|包括标签管理，关键词管理，举报审核等操作，用户可自由发表文章，系统通过过滤算法进行关键词过滤|计划管理|方便公司进行公司计划、部门计划、个人计划的规划，可根据类型（日计划、周计划、月计划、季度计划等）进行定义
动态表单|通过自定义的方式生成提交表单页，可与动态数据进行结合，目前已和工作流结合|行政管理|包含车辆管理、会议室管理、用品管理、印章管理、财产管理、证照管理。所有功能审核已和工作流结合
内部公告|系统内部公告通知，可设置邮件通知，定时通知，人员选择等|通讯录|记录个人、公司内部、公共通讯录信息
 **APP端** |接口：SpringCloud微服务框架；前端：vue| **小程序端** |微信用户与系统用户绑定进行系统操作

#### 技术扩展

- webSocket技术扩展
    ![输入图片说明](https://images.gitee.com/uploads/images/2019/0205/224843_d8055e22_1541735.png "1.png")

#### 版本介绍

功能|商用版|开源版|功能|商用版|开源版
---|-------|---|---|-------|---
问卷调查|<abbr>有</abbr>|否|我的日程|<abbr>有</abbr>|否
我的笔记|<abbr>有</abbr>|否|自定义快捷方式|<abbr>有</abbr>|否
多系统集成|<abbr>有</abbr>|否|应用商店|<abbr>有</abbr>|否
开发文档|<abbr>有</abbr>|否|文件管理|<abbr>有</abbr>|否
附件管理|<abbr>有</abbr>|否|邮件管理|<abbr>有</abbr>|否
考勤管理|<abbr>有</abbr>|否|报表管理|<abbr>有</abbr>|否
工作日志|<abbr>有</abbr>|否|工作流管理|<abbr>有</abbr>|否

#### 技术选型

##### 后端技术:

技术|名称|官网
---|---|---
SpringBoot|核心框架|http://spring.io/projects/spring-boot
MyBatis|ORM框架|http://www.mybatis.org/mybatis-3/zh/index.html
Druid|数据库连接池|https://github.com/alibaba/druid
Maven|项目构建管理|http://maven.apache.org/
redis|key-value存储系统|https://redis.io/
webSocket|浏览器与服务器全双工(full-duplex)通信|http://www.runoob.com/html/html5-websocket.html
Activiti|工作流引擎|https://www.activiti.org/
spring mvc|视图框架|http://spring.io/
quartz 2.2.2|定时任务|http://www.quartz-scheduler.org/
ActiveMQ|消息队列|http://activemq.apache.org/replicated-leveldb-store.html
solr|企业级搜索应用服务器|https://lucene.apache.org/solr/
Spring Cloud|微服务框架(目前用户APP端接口)|https://springcloud.cc/

##### 前端技术：

技术|名称|官网
---|---|---
jQuery|函式库|http://jquery.com/
zTree|树插件|http://www.treejs.cn/v3/
layui|模块化前端UI|https://www.layui.com/
winui|win10风格UI|https://gitee.com/doc_wei01_admin/skyeye
codemirror|codemirror代码编辑器|https://codemirror.net/
handlebars|js模板引擎|http://www.ghostchina.com/introducing-the-handlebars-js-templating-engine/
webSocket|浏览器与服务器全双工(full-duplex)通信|http://www.runoob.com/html/html5-websocket.html
G6|流程图开发|https://antv.alipay.com/zh-cn/index.html
FullCalendar|日历插件|https://blog.csdn.net/qw_xingzhe/article/details/44920943

#### 代码描述
##### 前后台接口映射

```
<url id="前端请求id" path="后台接口" val="备注" allUse="是否需要登录">
	<property id="前端请求key" name="后台接收key" ref="限制条件（参考项目内文档）" var="key含义"/>
</url>
```

##### 后台代码编写规范

###### 控制层

```
@RequestMapping("后台接口")
@ResponseBody
public void 方法名(InputObject inputObject, OutputObject outputObject) throws Exception{
	服务层接口对象.方法名(inputObject, outputObject);
}
```

###### 服务层

```
@Override
public void 方法名(InputObject inputObject, OutputObject outputObject) throws Exception {
	Map<String, Object> map = inputObject.getParams();//接收参数
	Map<String, Object> user = inputObject.getLogParams();//获取当前登录用户信息
	/**
	 * 业务逻辑
	 */
	outputObject.setBean(bean);//返回单个实体Bean
	outputObject.setBeans(beans);//返回集合
	outputObject.settotal(total);//返回数量
	outputObject.setreturnMessage("信息");//返回前端的错误信息
	outputObject.setreturnMessage("信息", 错误码);//返回前端的错误信息，同时抛出异常（不常用）
}
```



#### 效果图

|效果图|效果图|
| ------------- | ------------- |
|![](https://s2.ax1x.com/2019/04/16/Avo0c8.png "1.png")|![](https://images.gitee.com/uploads/images/2019/0623/142954_6c8612f2_1541735.png "1.png")|
|![](https://images.gitee.com/uploads/images/2018/1107/104734_d9304e60_1541735.png "2.png")|![](https://images.gitee.com/uploads/images/2018/1107/104903_f244dfde_1541735.png "3.png")|
|![](https://s2.ax1x.com/2019/06/23/ZPS5h4.png)|![](https://images.gitee.com/uploads/images/2018/1118/191634_497ea929_1541735.png "微信图片_20181118191516.png")|
|![](https://images.gitee.com/uploads/images/2018/1118/193301_72d0bb49_1541735.png "微信截图_20181118193254.png")|![](https://s2.ax1x.com/2019/04/16/Av7lee.png "微信截图_20181120154643.png")|
|![](https://images.gitee.com/uploads/images/2018/1121/105120_65de9434_1541735.png "1.png")|![](https://images.gitee.com/uploads/images/2019/0623/145248_4a51c610_1541735.png)|
|![](https://images.gitee.com/uploads/images/2019/0122/113516_b0600e8f_1541735.png "1.png")|![](https://images.gitee.com/uploads/images/2018/1207/123501_3248346e_1541735.png "微信截图_20181207123447.png")|
|![](https://images.gitee.com/uploads/images/2019/0113/114947_1c7fa387_1541735.png "微信截图_20190113114922.png")|![](https://images.gitee.com/uploads/images/2019/0202/130711_7ed57951_1541735.png "3.png")|
|![](https://s2.ax1x.com/2019/06/23/ZPC6cd.png)|![](https://images.gitee.com/uploads/images/2019/0623/145351_dd55da65_1541735.png)|
|![](https://images.gitee.com/uploads/images/2019/0623/143810_e76aec71_1541735.png)|![](https://images.gitee.com/uploads/images/2019/0623/144143_1063782a_1541735.png)|
|![](https://images.gitee.com/uploads/images/2019/0904/143828_b7ef9748_1541735.png)|![](https://images.gitee.com/uploads/images/2019/0904/143937_42519798_1541735.png)|

#### 传统风格界面效果图

|效果图|效果图|
| ------------- | ------------- |
|![](https://images.gitee.com/uploads/images/2019/0904/143634_0c9da9ad_1541735.png)|![](https://images.gitee.com/uploads/images/2019/0904/143639_2e442328_1541735.png)|

#### 环境搭建
##### 开发工具:

- MySql: 数据库</br>
- Tomcat: 应用服务器</br>
- SVN|Git: 版本管理</br>
- Nginx: 反向代理服务器</br>
- Varnish: HTTP加速器</br>
- IntelliJ IDEA|Eclipse: 开发IDE</br>
- Navicat for MySQL: 数据库客户端</br>
- Redis Manager：redis视图工具</br>

#### 资源下载

- JDK8 https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html</br>
- Maven http://maven.apache.org/download.cgi</br>
- Redis https://redis.io/download</br>
- Nginx http://nginx.org/en/download.html</br>

#### 在线文档

- [JDK8中文文档](https://blog.fondme.cn/apidoc/jdk-1.8-youdao/)</br>
- [Spring4.x文档](http://spring.oschina.mopaas.com/)</br>
- [Mybatis3官网](http://www.mybatis.org/mybatis-3/zh/index.html)</br>
- [Nginx中文文档](http://tool.oschina.net/apidocs/apidoc?api=nginx-zh)</br>
- [Git官网中文文档](https://git-scm.com/book/zh/v2)</br>

#### 项目交流：

QQ群号：[696070023](http://shang.qq.com/wpa/qunwpa?idkey=e9aace2bf3e05f37ed5f0377c3827c6683d970ac0bcc61b601f70dc861053229)

|QQ群|公众号|
|-------|-------|
|![](https://images.gitee.com/uploads/images/2018/1205/145236_4fce6966_1541735.jpeg "微信图片_20181205145217.jpg")|![](https://images.gitee.com/uploads/images/2018/1207/083137_48330589_1541735.jpeg "qrcode_for_gh_e7f97ff1beda_258.jpg")|


#### 合作公司(保留客户信息)：
- 合肥**科技有限责任公司
- 广州**信息有限责任公司

#### 个人购买(保留客户信息)：
- 王*易
- 孙*强
- 周*
- 朱*昌