# skyeye

#### 项目介绍

智能办公OA系统[SpringBoot2-快速开发平台]，适用于医院，学校，中小型企业等机构的管理。Activiti5.22+动态表单实现零java代码即可做到复杂业务的流程实施，同时包含文件在线操作、日志、考勤、CRM、项目、拖拽式生成问卷、日程、笔记、计划、行政等多种复杂业务功能。同时，可进行授权二开。</br>

- QQ群号：(群一：[696070023](http://shang.qq.com/wpa/qunwpa?idkey=e9aace2bf3e05f37ed5f0377c3827c6683d970ac0bcc61b601f70dc861053229))(群二：[836039567](https://shang.qq.com/wpa/qunwpa?idkey=7bb6f29b27f772aadca9c7c4e384f7833c64e9c3c947b5e946c7b303d1fe174a))
- 注：开源社区版只限学习，切勿使用此版本商用，内设授权码，默认十天删除所有非基础数据
- 求职简历：[点我](https://gitee.com/doc_wei01/skyeye/blob/master/%E7%AE%80%E5%8E%86.md)
- 体验地址1： [http://skyeye.vipgz1.idcfengye.com](http://skyeye.vipgz1.idcfengye.com?2)；账号/密码：root/123456 
- 体验地址2： [http://skyeyeoa.fzlin.net](http://skyeyeoa.fzlin.net)；账号/密码：root/123456 感谢`疯子林`提供的服务器
- [掘金文档地址](https://juejin.im/post/5d89df81e51d4561fb04c029)

|项目|地址|
|-------|-------|
|主项目地址|https://gitee.com/doc_wei01/skyeye|
|APP端接口微服务地址|https://gitee.com/doc_wei01/app-oaserver|
|APP端地址|https://gitee.com/doc_wei01/oa-app|
|小程序端地址|https://gitee.com/doc_wei01/small-pro|

> APP端开始开发，前端采用VUE，后端采用SpringCloud，APP访问地址：[https://gitee.com/doc_wei01_admin/oa-app](https://gitee.com/doc_wei01_admin/oa-app "https://gitee.com/doc_wei01_admin/oa-app")

`项目持续更新，欢迎进群讨论`

> 3D模型编辑器（图片效果在下面）。很多人会问：有什么用？答案是：用途很多，比如机场建设，工厂监控，小区建设，统计分析等。当前已出第一版，后续会不断更新代码和3D模型。

##### 启动方式

直接运行com.skyeye.SkyEyeApplication即可，启动完成后，访问http://localhost:8081 即可。
初始化账号密码：`root/123456`

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
邮件管理|目前打通与QQ邮箱的交互，可以发邮件，收邮件，保存为草稿等|工作流管理|动态表单结合工作流生成自定义业务流程审核,可进行审批、撤回、回退、节点化表单项编辑设置、驳回、终止转办等功能
论坛|包括标签管理，关键词管理，举报审核等操作，用户可自由发表文章，系统通过过滤算法进行关键词过滤|计划管理|方便公司进行公司计划、部门计划、个人计划的规划，可根据类型（日计划、周计划、月计划、季度计划等）进行定义
动态表单|通过自定义的方式生成提交表单页，可与动态数据进行结合，目前已和工作流结合|行政管理|包含车辆管理、会议室管理、用品管理、印章管理、财产管理、证照管理。所有功能审核已和工作流结合
内部公告|系统内部公告通知，可设置邮件通知，定时通知，人员选择等|通讯录|记录个人、公司内部、公共通讯录信息
知识库|企业文化支柱；[效果地址](https://gitee.com/doc_wei01/knowlg-pro)|CRM客户管理管理|包含客户、商机、跟单、合同等多个模块化功能
ERP进销存管理|包含采购、销售、零售、客户、供应商等多个模块话功能；[效果地址](https://gitee.com/doc_wei01/erp-pro)||

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
|![输入图片说明](https://images.gitee.com/uploads/images/2019/1101/170700_738ed970_1541735.png "在这里输入图片标题")||

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

> 需要了解的请加微信或者进群：wzq_598748873，备注：码云-公司（姓名）。

|QQ群|公众号|微信群|
|-------|-------|-------|
|![](https://images.gitee.com/uploads/images/2018/1205/145236_4fce6966_1541735.jpeg "微信图片_20181205145217.jpg")|![](https://images.gitee.com/uploads/images/2018/1207/083137_48330589_1541735.jpeg "qrcode_for_gh_e7f97ff1beda_258.jpg")|![输入图片说明](https://images.gitee.com/uploads/images/2019/1026/125556_ff89219a_1541735.jpeg "123.jpg")|


#### 合作公司(保留客户信息)：
- 合肥**科技有限责任公司
- 广州**信息有限责任公司

#### 个人购买(保留客户信息)：
- 王*易
- 孙*强
- 周*
- 朱*昌