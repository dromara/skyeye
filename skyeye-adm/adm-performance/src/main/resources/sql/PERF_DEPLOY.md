# 绩效考核模块上线配置清单

表结构见同目录 `perf_tables.sql`。已有库增量见：

- `perf_alter_source_config.sql`（指标来源参数）
- `perf_alter_phase2.sql`（OKR / 360 字段与评价人表）

## 1. 执行建表 / 增量

- 新环境：执行 `perf_tables.sql`
- 已有库：依次执行 `perf_alter_source_config.sql`、`perf_alter_phase2.sql`

## 2. 菜单（菜单管理）

建议目录「员工绩效」，子菜单 path 与前端路由一致：

| 名称 | path |
|------|------|
| 考核方案 | `/performance/scheme/list` |
| 考核周期 | `/performance/cycle/list` |
| 考核单 | `/performance/assessment/list` |
| 绩效看板 | `/performance/dashboard` |

`sys_win_id` / `desktop_id` / `parent_id` 按本环境已有桌面与父菜单填写。

## 3. 权限点（菜单 → 权限点）

前端已写死 `menuNum`，入库时请使用下列值（或改 Vue 对齐新值）：

### 考核方案 `/performance/scheme/list`

| 名称 | menuNum |
|------|---------|
| 新增/编辑/复制 | `1795595272687` |
| 删除 | `1795595281709` |

### 考核周期 `/performance/cycle/list`

| 名称 | menuNum |
|------|---------|
| 新增/编辑 | `1795595461650` |
| 删除 | `1795595479952` |
| 生成考核单 | `1795595488782` |
| 关闭周期 | `1795595497492` |

### 考核单 `/performance/assessment/list`

| 名称 | menuNum |
|------|---------|
| 编辑打分 | `1795595650748` |
| 删除 | `1795595659092` |
| 提交审批 | `1795595667638` |
| 撤销 | `1795595676377` |
| 作废 | `1795595685751` |

## 4. 单号规则

1. 编码规则管理：新增规则（建议前缀 `JX` + 日期 + 流水）。
2. 业务对象：`com.skyeye.performance.assessment.service.impl.PerfAssessmentServiceImpl` → 绑定该规则。

## 5. Flowable

流程、DsForm 审批布局按现有配置维护即可。

## 6. 枚举

确认并同步（重启 ADM 后一般自动注册）：

- `perfSchemeType`（kpi / okr / 360）
- `perfCycleType`
- `perfCycleState`
- `perfItemSourceType`（manual / checkwork / survey / piecework）
- `perfRaterRole`（self / superior / peer / subordinate）

## 7. 服务地址

生产配置 `performanceBasePath`（开发可回落 `admBasePath`）。

计件自动取分依赖 ERP Feign：`IPieceworkSystemRest` → `queryPieceworkByStaffAndMonths`。

## 8. 消息通知（可选）

类型编码：`PERFORMANCE_RESULT_REMINDER`。审批通过后通知被考核人。

## 9. 自动取分

| 来源 | 说明 |
|------|------|
| 考勤 | 按周期起止日汇总应出/正常/旷工/迟到等计分 |
| 问卷 | 指标「来源参数」填问卷ID；取员工账号答卷 `markFraction` 按问卷满分折算 |
| 计件 | 来源参数 JSON：`{"targetNum":1000,"isNumTime":1,"farmId":""}`；`isNumTime`=1 产量 / =2 工时；按周期月份汇总后相对目标折算 |

生成考核单时自动回填；打分页可「按来源自动取分」。

## 10. 方案类型

| 类型 | 说明 |
|------|------|
| KPI | 常规指标加权打分 |
| OKR | 方案填 Objective；指标即 KR，按完成度打分 |
| 360 | 方案配置评价人权重；生成考核单自动带出自评/上级；同级下级可补选；各角色打分后「汇总到明细」 |

## 11. 二期接口（360）

| 接口 id | 说明 |
|---------|------|
| `queryPerfAssessmentRaters` | 查询评价人 |
| `writePerfAssessmentRater` | 新增/编辑评价人 |
| `deletePerfAssessmentRater` | 删除评价人 |
| `savePerfAssessmentRaterScores` | 保存某评价人打分 |
| `aggregatePerfAssessmentRaterScores` | 加权汇总到考核明细 |
