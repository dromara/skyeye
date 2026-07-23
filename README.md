# 🌌 SkyEye云 - 智能制造全链路零代码一体化平台
<p align="center">
  <strong>Dromara开源组织 | 基于SpringCloudAlibaba微服务 + Vue3 + Ant Design Vue + uni-app构建</strong>
</p>

<p align="center"> 
  <a href="您的AtomGit项目链接"><img alt="AtomGit G-Star" src="https://gitcode.com/doc_wei-dev/erp-pro/star/new_badge.svg"></a><br>
  <a href="您的AtomGit项目链接"><img alt="AtomGit Star" src="https://gitcode.com/doc_wei-dev/erp-pro/star/badge.svg"></a>
  <a href="您的AtomGit项目链接"><img alt="AtomGit Fork" src="https://gitcode.com/doc_wei-dev/erp-pro/fork/badge.svg"></a>
  <a href="您的AtomGit项目链接"><img alt="AtomGit Download" src="https://gitcode.com/doc_wei-dev/erp-pro/download/badge.svg"></a>
  <img src="https://gitcode.com/doc_wei-dev/erp-pro/star/2025top.svg?style=flat-square&logoSize=14"
  alt="GitCode Star 2025" height="20">
</p>

<div align="center">
  <img src="https://gitee.com/dromara/skyeye/badge/star.svg?theme=blue" alt="gitee Star" height="20">
</div>

<div align="center">
  <img src="https://img.shields.io/github/stars/dromara/skyeye.svg?style=social&label=Stars" alt="Github Star" height="20">
</div>

## 📖 项目简介
SkyEye云 是面向**智能制造行业**的一站式SaaS化零代码全链路管理平台，一套底座覆盖企业数字化全场景：
内置CRM客户管理、ERP进销存、MES生产制造、WMS仓储、OA行政办公、EHR人事薪资、项目管理、商城、财务、售后工单、工作流引擎、智能报表等**上百种业务模块**。
依托自研零代码设计器，无需编写原生CRUD代码，拖拽式快速搭建业务表单、流程、打印模板，支持PC端+移动端（小程序/H5/App）多端同源发布，是中小企业智能制造数字化落地一站式解决方案。

> 项目归属Dromara开源社区，开源主干持续迭代，同时提供商用完整版源码会员服务。

## ✨ 核心优势
- 🧩 **全场景一体化**：一套系统打通产销存、生产、人事、财务、售后全业务，告别多系统割裂
- ⚡ **低代码零开发**：内置表单/布局/工作流/编码/打印五大可视化设计器，零基础快速定制业务
- 📱 **一次开发多端适配**：Vue3+PC + uni-app移动端，一套代码同步发布APP/小程序/H5
- 🏢 **SaaS多租户架构**：原生支持多企业租户隔离、精细化权限管控，适配集团化部署
- 🛠️ **企业友好授权**：商用会员拿到源码后，自研二开产品可去除原项目Copyright与作者标识

## 🚀 快速体验
### 1. 免费体验（仅预览功能）
项目Star后，将Gitee Star截图发送至【Skyeye智能制造云办公】公众号，核验合格后自动下发**预览账号+演示地址**。
![输入图片说明](images/mindMap/tiyan.png)

### 2. 付费体验
一人一号，可体验到系统中绝大多数的功能。50元一个名额，如需开通，可联系下方QQ/微信。

## 📚  源码与商用版本
> 开源仓库不包含所有代码，**全量业务源码需开通Skyeye会员获取**，会员源码支持：学习研究、毕业设计、企业商用二开。
- 官方定价页：[全网特惠价目](http://ip.makerview.cn:30003/skyPortal/#/price)
- 官方门户：http://ip.makerview.cn:30003/skyPortal

## 📚 官方文档&视频教程
| 资源类型 | 访问地址 |
| ---- | ---- |
| 二开开发文档 | http://ip.makerview.cn:30003/skyDoc/ |
| 全量功能清单 | https://kdocs.cn/l/cbf2cgCLrUyz |
| 产品架构流程图 | https://www.kdocs.cn/l/ctDzURtzHWXE |
| B站合集-二开教程 | https://www.bilibili.com/video/BV1w34y1M7ZH/ |
| B站合集-操作教程 | https://www.bilibili.com/video/BV16mwVeKE4X/ |
| 零代码设计器实操 | https://www.bilibili.com/video/BV1rv2CB3E3P |

> 更多细分功能视频持续在B站更新中

## 🐶 沟通交流

> 项目作者坚持长期售后答疑，对标精细化服务标准，个人开发者用心维护迭代。

| 微信公众号(Skyeye智能制造云办公) | 咨询加QQ | 购买加微信 | 收款码 |
|:-------------------:|:-------------------:|:-------------------:|:-------------------:|
|   ![](images/mindMap/微信公众号.jpg) | ![](images/mindMap/qq2.jpg) | ![](images/mindMap/微信.jpg) | ![](images/mindMap/wechatPay.jpg) |

## Skyeye云整体图

![输入图片说明](images/mindMap/Skyeye%E4%BA%91%E7%9B%AE%E6%A0%87.jpeg)

## 项目框架介绍

### 环境依赖

| 依赖 | 版本 | 端口 |
|:---------------------:|:---------------------:|:---------------------:|
| Java | 1.8 | 无 |
| rocket MQ | 4.9.2 | 9876 |
| Redis | 5.0 / 6.0 | 6379 |
| nacos | 2.3.0 | 8848|
| MySQL | 5.7或更高版本，[点我配置](https://blog.csdn.net/qq_42175986/article/details/82384160) | 3308 |

##  :tw-1f31e:  架构介绍

![输入图片说明](images/mindMap/image11.png)

## 🧰 技术架构与环境依赖
### 后端技术栈（Spring Cloud Alibaba微服务）
| 技术组件 | 版本 | 用途 |
| ---- | ---- | ---- |
| Spring Cloud Alibaba | 2.1.0.RELEASE | 微服务基座 |
| Nacos | 2.3.0 | 注册中心+配置中心 |
| RocketMQ | 4.9.2 | 分布式消息队列 |
| Sentinel | 2.1.0.RELEASE | 流量熔断、服务容错 |
| XXL-Job | 2.3.0 | 分布式定时任务 |
| Activiti | 6.8.0 | 工作流引擎 |
| Mybatis-Plus | 3.5.7 | ORM框架 |
| Druid | 1.2.23 | 数据库连接池 |
| Redis | 5.0+/6.x | 缓存中间件 |
| MySQL | 5.7+/8.0 | 业务数据库 |
| Hutool/Lombok | 常用稳定版 | 工具类与代码简化 |

### 前端技术栈
- PC端：Vue3 + Vite5 + Ant Design Vue4
- 移动端：uni-app(Vue3)，一套代码编译安卓/IOS/小程序/H5

### 基础运行环境
- JDK 1.8
- MySQL(默认端口3308)、Redis(6379)、Nacos(8848)、RocketMQ(9876)

##  :tw-1f30f:  PC端效果图

### 基础内容
|功能| 效果图 | 效果图 | 效果图 |
|----|-------|-----|------|
|组件管理|![输入图片说明](images/show/base/20240802001image.png)|![输入图片说明](images/show/base/2024080202image.png)||
|布局/操作/属性管理|![输入图片说明](images/show/base/2024080203image.png)|![输入图片说明](images/show/base/2024080204image.png)|![输入图片说明](images/show/base/20240802005image.png)|
|菜单/角色/编码管理|![输入图片说明](images/show/base/20240802010image.png)|![输入图片说明](images/show/base/20240802011image.png)|![输入图片说明](images/show/base/20240802012image.png)|

### CRM
|功能| 效果图 | 效果图 | 效果图 |
|----|-------|-----|------|
|客户管理(包括合同、跟单、文档等)|![输入图片说明](images/show/crm/image.png)|![输入图片说明](images/show/crm/1image.png)|![输入图片说明](images/show/crm/2image.png)|
|客户管理(包括合同、跟单、文档等)|![输入图片说明](images/show/crm/3image.png)|![输入图片说明](images/show/crm/4image.png)|![输入图片说明](images/show/crm/5image.png)|
|报表分析|![输入图片说明](images/show/crm/6image.png)|![输入图片说明](images/show/crm/7image.png)|![输入图片说明](images/show/crm/8image.png)|

### ERP
|功能| 效果图 | 效果图 | 效果图 |
|----|-------|-----|------|
|商品管理 **(支持一物一码)** |![输入图片说明](images/show/erp/08image.png)|![输入图片说明](images/show/erp/07image.png)|![输入图片说明](images/show/erp/09image.png)|
|采购模块|![输入图片说明](images/show/erp/01image.png)|![输入图片说明](images/show/erp/02image.png)|![输入图片说明](images/show/erp/03image.png)|
|采购模块|![输入图片说明](images/show/erp/04image.png)|![输入图片说明](images/show/erp/05image.png)|![输入图片说明](images/show/erp/06image.png)|
|销售模块|![输入图片说明](images/show/erp/10image.png)|![输入图片说明](images/show/erp/11image.png)|![输入图片说明](images/show/erp/12image.png)|
|报表模块|![输入图片说明](images/show/erp/13image.png)|![输入图片说明](images/show/erp/14image.png)|![输入图片说明](images/show/erp/15image.png)|

### ERP仓库
|功能| 效果图 | 效果图 | 效果图 |
|----|-------|-----|------|
|其他单据管理|![输入图片说明](images/show/erpDepot/image.png)|![输入图片说明](images/show/erpDepot/1image.png)|![输入图片说明](images/show/erpDepot/2image.png)|
|仓库管理|![输入图片说明](images/show/erpDepot/3image.png)|![输入图片说明](images/show/erpDepot/4image.png)||
|盘点管理|![输入图片说明](images/show/erpDepot/5image.png)|![输入图片说明](images/show/erpDepot/6image.png)||
|出入库管理|![输入图片说明](images/show/erpDepot/7image.png)|![输入图片说明](images/show/erpDepot/8image.png)||
|商品条形码|![输入图片说明](images/show/erpDepot/9image.png)|![输入图片说明](images/show/erpDepot/10image.png)||

### MES生产
|功能| 效果图 | 效果图 | 效果图 |
|----|-------|-----|------|
|生产管理|![输入图片说明](images/show/mes/1image.png)|![输入图片说明](images/show/mes/2image.png)|![输入图片说明](images/show/mes/3image.png)|
|设置中心|![输入图片说明](images/show/mes/4image.png)|![输入图片说明](images/show/mes/5image.png)|![输入图片说明](images/show/mes/6image.png)|
|物料管理|![输入图片说明](images/show/mes/7image.png)|![输入图片说明](images/show/mes/8image.png)|![输入图片说明](images/show/mes/9image.png)|
|生产执行|![输入图片说明](images/show/mes/10image.png)|![输入图片说明](images/show/mes/11image.png)|![输入图片说明](images/show/mes/12image.png)|
|物料确认|![输入图片说明](images/show/mes/13image.png)|![输入图片说明](images/show/mes/14image.png)|![输入图片说明](images/show/mes/15image.png)|

### 行政办公
|功能| 效果图 | 效果图 | 效果图 |
|----|-------|-----|------|
|会议室模块|![输入图片说明](images/show/oa/1image.png)|![输入图片说明](images/show/oa/2image.png)||
|用品模块|![输入图片说明](images/show/oa/3image.png)|![输入图片说明](images/show/oa/4image.png)|![输入图片说明](images/show/oa/5image.png)|
|资产模块|![输入图片说明](images/show/oa/6image.png)|![输入图片说明](images/show/oa/7image.png)|![输入图片说明](images/show/oa/8image.png)|
|公文模块|![输入图片说明](images/show/oa/9image.png)|![输入图片说明](images/show/oa/10image.png)|![输入图片说明](images/show/oa/11image.png)|
|印章，证照，车辆|![输入图片说明](images/show/oa/12image.png)|![输入图片说明](images/show/oa/13image.png)|![输入图片说明](images/show/oa/14image.png)|

### 售后管理模块
|功能| 效果图 | 效果图 | 效果图 |
|----|-------|-----|------|
|工单管理|![输入图片说明](images/show/sealService/1image.png)|![输入图片说明](images/show/sealService/2image.png)||
|配件管理|![输入图片说明](images/show/sealService/3image.png)|![输入图片说明](images/show/sealService/4image.png)||
|工人管理|![输入图片说明](images/show/sealService/5image.png)|![输入图片说明](images/show/sealService/6image.png)|![输入图片说明](images/show/sealService/7image.png)|

##  :tw-1f30f:  移动端效果图

> 移动端和PC端功能类似，这里不截那么多图拉。

### 基础模块
| 效果图  | 效果图  | 效果图  | 效果图  |
|--------|-------|-------|-------|
|![输入图片说明](images/show/phone/20240730image.png)|![输入图片说明](images/show/phone/2024073002image.png)|![输入图片说明](images/show/phone/2024073003image.png)|![输入图片说明](images/show/phone/2024073004image.png)|

### ERP

| 效果图  | 效果图  | 效果图  | 效果图  |
|--------|-------|-------|-------|
|![输入图片说明](images/show/phone/2024073005image.png)|![输入图片说明](images/show/phone/2024073006image.png)|![输入图片说明](images/show/phone/2024073007image.png)|![输入图片说明](images/show/phone/2024073008image.png)|

### CRM

| 效果图  | 效果图  | 效果图  | 效果图  |
|--------|-------|-------|-------|
|![输入图片说明](images/show/phone/2024073012image.png)|![输入图片说明](images/show/phone/2024073009image.png)|![输入图片说明](images/show/phone/2024073010image.png)|![输入图片说明](images/show/phone/2024073011image.png)|

### OA

| 效果图  | 效果图  | 效果图  | 效果图  |
|--------|-------|-------|-------|
|![输入图片说明](images/show/phone/2024073013image.png)|![输入图片说明](images/show/phone/2024073014image.png)|![输入图片说明](images/show/phone/2024073015image.png)|![输入图片说明](images/show/phone/2024073016image.png)|

## 📍 项目托管地址
- Gitee(Dromara): https://gitee.com/dromara/skyeye
- GitCode: https://gitcode.com/doc_wei-dev/erp-pro

## 🤝 友情赞助

|  赞助商  |  赞助商  |  赞助商  |  赞助商  |
|--------|-------|-------|-------|
| [![输入图片说明](images/show/sponsor/maxkey_banner.jpg)](https://gitee.com/dromara/MaxKey) | [![输入图片说明](https://minio.tianai.cloud/public/captcha/logo/logo-519x100.png)](https://gitee.com/dromara/tianai-captcha/) | [![输入图片说明](https://infinilabs.cn/img/download/media-assets/infinilabs-slogan.png)](https://easysearch.cn/) ||
