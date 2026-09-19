---
description: "Java Service层编写规范和标准"
alwaysApply: false
globs:
  - "**/service/**/*.java"
  - "**/service/*.java"
---

# Java Service层编写规范

## 目录结构

Service层应遵循以下目录结构：
```
模块名-pro/src/main/java/com/skyeye/模块名/
  service/
    Service接口.java
    impl/
      Service实现类.java
```

## Service接口规范

### 基本结构
- 继承 `SkyeyeBusinessService<Entity>`
- 定义业务方法（非CRUD方法）
- 方法注释必须包含参数说明和返回值说明

### 命名规范
- 接口名：`XxxService`（如：`ProSchemeService`）
- 方法名：动词开头，驼峰命名（如：`querySchemeListByProjectId`）

### 示例
```java
public interface ProSchemeService extends SkyeyeBusinessService<ProScheme> {
    /**
     * 根据项目id查询方案列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void querySchemeListByProjectId(InputObject inputObject, OutputObject outputObject);
}
```

## Service实现类规范

### 基本结构
- 继承 `SkyeyeBusinessServiceImpl<Dao, Entity>`
- 实现对应的Service接口
- 使用 `@Service` 和 `@SkyeyeService` 注解

### 注解规范
```java
@Service
@SkyeyeService(name = "功能名称", groupName = "功能分组")
public class XxxServiceImpl extends SkyeyeBusinessServiceImpl<XxxDao, Xxx> implements XxxService {
}
```

### 常用重写方法
- `getQueryWrapper(CommonPageInfo)` - 自定义查询条件
- `createEntity(Entity, String)` - 创建前处理
- `createPrepose(Entity)` - 创建前置处理
- `updateEntity(Entity, String)` - 更新前处理
- `writePostpose(Entity, String)` - 保存后处理
- `getDataFromDb(String)` - 从数据库获取数据（可扩展关联数据）
- `deletePostpose(String)` - 删除后处理
- `validatorEntity(Entity)` - 实体校验

### 查询方法规范
- 使用 `QueryWrapper` 构建查询条件
- 使用 `MybatisPlusUtil.toColumns()` 进行类型安全的列名引用
- 使用枚举类进行状态判断（如：`EnableEnum.ENABLE_USING.getKey()`）

### 示例
```java
@Override
public QueryWrapper<ProScheme> getQueryWrapper(CommonPageInfo commonPageInfo) {
    QueryWrapper<ProScheme> queryWrapper = super.getQueryWrapper(commonPageInfo);
    queryWrapper.eq(MybatisPlusUtil.toColumns(ProScheme::getWhetherLast), WhetherEnum.ENABLE_USING.getKey());
    return queryWrapper;
}

@Override
protected void writePostpose(ProScheme entity, String userId) {
    super.writePostpose(entity, userId);
    // 保存关联数据
    budgetDetailService.saveList(entity.getId(), entity.getBudgetDetailList());
}
```

## 依赖注入

- 使用 `@Autowired` 注入其他Service
- 避免循环依赖
- 子表Service通常注入到主表Service中

## 入参取值

从 `inputObject.getParams()` 取字段时，直接 `params.get("字段名").toString()`，禁止再封装 `getStr`、`MapUtilGet` 等取值方法。

```java
Map<String, Object> params = inputObject.getParams();
String name = params.get("name").toString();
```

## 接口必填

`@ApiImplicitParam` 的 `required` 含 `required` 时（`required`、`required,num`、`required,json`），空值在接口层已经校验。Service 不要再对这个字段做非空判断，直接取值。

没标必填的字段才处理缺省。数值范围、业务状态这类校验照常写。

## 异常处理

- 使用 `CustomException` 抛出业务异常
- 在 `validatorEntity` 方法中进行数据校验

