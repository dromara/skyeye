---
description: "Java代码风格和最佳实践"
alwaysApply: true
---

# Java代码风格和最佳实践

## 文件头注释

所有Java文件必须包含标准文件头注释：

```java
/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
```

## 类注释规范

所有类必须包含完整的JavaDoc注释：

```java
/**
 * @ClassName: Xxx
 * @Description: 类功能描述
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
```

## 导入语句规范

- 按需导入，不使用 `.*` 通配符
- 导入顺序：标准库 → 第三方库 → 项目内部库
- 使用IDE自动整理导入

## 命名规范

### 类命名
- 实体类：`Xxx`（如：`ProScheme`、`ServiceAbnormalMarking`）
- Service接口：`XxxService`
- Service实现：`XxxServiceImpl`
- Controller：`XxxController`
- Dao：`XxxDao`

### 方法命名
- 查询方法：`query` 开头（如：`queryXxxList`）
- 保存方法：`write` 或 `save` 开头（如：`writeXxx`、`saveXxx`）
- 删除方法：`delete` 开头（如：`deleteXxxById`）
- 业务方法：动词开头，驼峰命名（如：`calculateBudget`、`publishVersionById`）

### 变量命名
- 使用驼峰命名：`projectId`、`schemeCode`
- 布尔值使用 `is`/`has`/`can` 前缀：`isEnabled`、`hasPermission`
- 集合使用复数或 `List` 后缀：`schemeList`、`budgetDetailList`

## 代码组织

### 方法顺序
1. 重写的父类方法（按调用顺序）
2. 实现接口的方法
3. 私有辅助方法

### 代码块组织
- 相关代码放在一起
- 使用空行分隔逻辑块
- 保持方法简洁，单一职责

## 异常处理

- 使用 `CustomException` 抛出业务异常
- 在Service层进行数据校验
- 避免在Controller层处理业务逻辑

## 入参取值

`inputObject.getParams()` 得到的 `Map`，字段一律直接取，不要再包 `getStr`、`MapUtilGet` 这类私有方法。

```java
Map<String, Object> params = inputObject.getParams();
String storeName = params.get("storeName").toString();
String id = inputObject.getLogParams().get("id").toString();
```

## 工具类使用

- 使用 `CalculationUtil` 进行金额计算
- 使用 `MybatisPlusUtil.toColumns()` 进行类型安全的列名引用
- 使用 `StrUtil`（Hutool）进行字符串操作
- 使用 `CollectionUtil`（Hutool）进行集合操作

## 注释规范

- 复杂业务逻辑必须添加注释
- 方法注释必须包含参数说明和返回值说明
- 使用中文注释
- 避免无意义的注释

