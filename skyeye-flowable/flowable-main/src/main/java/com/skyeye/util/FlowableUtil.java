/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;
import com.skyeye.activiti.service.ActivitiUserService;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.entity.ActGroupUser;
import com.skyeye.eve.service.ActGroupUserService;
import com.skyeye.eve.service.IAuthUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: FlowableUtil
 * @Description: 流程工具类
 * @author: skyeye云系列--卫志强
 * @date: 2025/11/23 12:05
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public class FlowableUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowableUtil.class);

    /**
     * 评估 Flowable 表达式
     * 
     * <p><b>功能说明：</b></p>
     * <ul>
     *   <li>支持简单变量引用：${userId}</li>
     *   <li>支持对象属性访问：${user.id}、${user.name}</li>
     *   <li>支持条件表达式：${amount > 1000}、${dept == 'finance'}</li>
     *   <li>支持方法调用：${date().plusDays(1)}</li>
     *   <li>支持字符串拼接：${'prefix_' + userId}</li>
     * </ul>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>评估流程定义中的 assignee、candidateUsers、candidateGroups 等表达式</li>
     *   <li>评估条件表达式中的变量值</li>
     *   <li>在流程启动前预评估表达式值</li>
     * </ul>
     * 
     * <p><b>示例：</b></p>
     * <pre>{@code
     * // 简单变量引用
     * Map<String, Object> vars = new HashMap<>();
     * vars.put("userId", "123");
     * String result = FlowableUtil.evaluateExpression("${userId}", vars);
     * // 结果: "123"
     * 
     * // 对象属性访问
     * Map<String, Object> user = new HashMap<>();
     * user.put("id", "123");
     * user.put("name", "张三");
     * vars.put("user", user);
     * String result = FlowableUtil.evaluateExpression("${user.id}", vars);
     * // 结果: "123"
     * 
     * // 条件表达式
     * vars.put("amount", 5000);
     * Boolean result = FlowableUtil.evaluateExpression("${amount > 1000}", vars);
     * // 结果: true
     * }</pre>
     * 
     * @param expression 表达式字符串，如 "${userId}" 或 "${user.id}"
     * @param variables 变量映射，key 为变量名，value 为变量值
     * @return 表达式评估结果，如果表达式不是 ${...} 格式，则返回原字符串
     */
    public static Object evaluateExpression(String expression, Map<String, Object> variables) {
        if (expression == null || expression.trim().isEmpty()) {
            return expression;
        }

        // 如果变量为空，直接返回原表达式
        if (variables == null || variables.isEmpty()) {
            return expression;
        }

        // 检查是否是表达式格式 ${...}
        if (!expression.startsWith("${") || !expression.endsWith("}")) {
            // 如果不是表达式格式，直接返回原字符串
            return expression;
        }

        try {
            // 提取表达式内容（移除 ${} 包装）
            String exprContent = expression.substring(2, expression.length() - 1).trim();
            
            if (exprContent.isEmpty()) {
                return null;
            }

            // 先尝试简单变量引用（最快路径）
            Object simpleResult = evaluateSimpleExpression(exprContent, variables);
            if (simpleResult != null) {
                return simpleResult;
            }

            // 尝试对象属性访问
            Object propertyResult = evaluatePropertyExpression(exprContent, variables);
            if (propertyResult != null) {
                return propertyResult;
            }

            // 如果提供了 ExpressionManager，使用 Flowable 的表达式引擎
            // 注意：这里需要调用方传入 ExpressionManager，因为工具类不应该依赖 Spring 上下文
            // 如果需要使用 Flowable 表达式引擎，请使用重载方法 evaluateExpression(String, Map, ExpressionManager)

            // 如果都不匹配，返回 null
            LOGGER.warn("无法评估表达式: {}, 变量: {}", expression, variables.keySet());
            return null;

        } catch (Exception e) {
            LOGGER.warn("评估表达式异常: {}", expression, e);
            return null;
        }
    }

    /**
     * 评估 Flowable 表达式（使用 Flowable 表达式引擎）
     * 
     * <p><b>功能说明：</b></p>
     * <ul>
     *   <li>使用 Flowable 的 ExpressionManager 评估表达式</li>
     *   <li>支持所有 Flowable 支持的表达式语法</li>
     *   <li>包括方法调用、条件表达式、字符串操作等</li>
     * </ul>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>需要评估复杂表达式时</li>
     *   <li>需要支持 Flowable 内置函数（如 date()、user() 等）</li>
     *   <li>需要支持完整的 EL 表达式语法</li>
     * </ul>
     * 
     * <p><b>示例：</b></p>
     * <pre>{@code
     * // 使用 Flowable 表达式引擎
     * ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();
     * Map<String, Object> vars = new HashMap<>();
     * vars.put("amount", 5000);
     * Object result = FlowableUtil.evaluateExpression("${amount > 1000}", vars, expressionManager);
     * // 结果: true
     * }</pre>
     * 
     * @param expression 表达式字符串，如 "${userId}" 或 "${amount > 1000}"
     * @param variables 变量映射，key 为变量名，value 为变量值
     * @param expressionManager Flowable 表达式管理器
     * @return 表达式评估结果
     */
    public static Object evaluateExpression(String expression, Map<String, Object> variables, ExpressionManager expressionManager) {
        if (expression == null || expression.trim().isEmpty()) {
            return expression;
        }

        // 如果变量为空，直接返回原表达式
        if (variables == null || variables.isEmpty()) {
            return expression;
        }

        // 检查是否是表达式格式 ${...}
        if (!expression.startsWith("${") || !expression.endsWith("}")) {
            // 如果不是表达式格式，直接返回原字符串
            return expression;
        }

        try {
            // 提取表达式内容（移除 ${} 包装）
            String exprContent = expression.substring(2, expression.length() - 1).trim();
            
            if (exprContent.isEmpty()) {
                return null;
            }

            // 如果提供了 ExpressionManager，使用 Flowable 的表达式引擎
            if (expressionManager != null) {
                try {
                    Expression flowableExpression = expressionManager.createExpression(exprContent);
                    if (flowableExpression != null) {
                        // Flowable 表达式引擎需要 VariableContainer，创建一个包装类
                        try {
                            VariableContainer variableContainer = new MapVariableContainer(variables);
                            return flowableExpression.getValue(variableContainer);
                        } catch (Exception e) {
                            // 如果 VariableContainer 方式失败，尝试直接使用 Map（某些版本的 Flowable 可能支持）
                            LOGGER.debug("使用 VariableContainer 评估失败，尝试其他方式: {}", e.getMessage());
                            // 回退到简单评估
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warn("使用 Flowable 表达式引擎评估失败，尝试简单评估: {}", expression, e);
                    // 如果 Flowable 表达式引擎失败，回退到简单评估
                }
            }

            // 回退到简单评估
            return evaluateExpression(expression, variables);

        } catch (Exception e) {
            LOGGER.warn("评估表达式异常: {}", expression, e);
            return null;
        }
    }

    /**
     * 评估简单表达式（直接变量引用）
     * 
     * @param exprContent 表达式内容（不包含 ${}）
     * @param variables 变量映射
     * @return 评估结果，如果不匹配则返回 null
     */
    private static Object evaluateSimpleExpression(String exprContent, Map<String, Object> variables) {
        // 简单变量引用：直接是变量名，如 "userId"
        if (variables.containsKey(exprContent)) {
            return variables.get(exprContent);
        }
        return null;
    }

    /**
     * 评估属性表达式（对象属性访问）
     * 
     * @param exprContent 表达式内容（不包含 ${}）
     * @param variables 变量映射
     * @return 评估结果，如果不匹配则返回 null
     */
    private static Object evaluatePropertyExpression(String exprContent, Map<String, Object> variables) {
        // 对象属性访问：如 "user.id"、"user.name"
        if (exprContent.contains(".")) {
            String[] parts = exprContent.split("\\.", 2);
            if (parts.length == 2) {
                String objectName = parts[0].trim();
                String propertyName = parts[1].trim();
                
                if (variables.containsKey(objectName)) {
                    Object obj = variables.get(objectName);
                    if (obj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> map = (Map<String, Object>) obj;
                        if (map.containsKey(propertyName)) {
                            return map.get(propertyName);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 评估表达式并返回字符串结果
     * 
     * <p>这是 evaluateExpression 的便捷方法，自动将结果转换为字符串</p>
     * 
     * @param expression 表达式字符串
     * @param variables 变量映射
     * @return 字符串结果，如果评估失败或结果为 null，返回 null
     */
    public static String evaluateExpressionAsString(String expression, Map<String, Object> variables) {
        Object result = evaluateExpression(expression, variables);
        return result != null ? result.toString() : null;
    }

    /**
     * 评估表达式并返回字符串结果（使用 Flowable 表达式引擎）
     * 
     * @param expression 表达式字符串
     * @param variables 变量映射
     * @param expressionManager Flowable 表达式管理器
     * @return 字符串结果，如果评估失败或结果为 null，返回 null
     */
    public static String evaluateExpressionAsString(String expression, Map<String, Object> variables, ExpressionManager expressionManager) {
        Object result = evaluateExpression(expression, variables, expressionManager);
        return result != null ? result.toString() : null;
    }

    /**
     * 检查字符串是否是表达式格式
     * 
     * @param str 待检查的字符串
     * @return true 如果是 ${...} 格式，否则返回 false
     */
    public static boolean isExpression(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        return str.startsWith("${") && str.endsWith("}");
    }

    /**
     * 是否为「发起人同部门+岗位」自定义表达式，如 ${skyDeptJob:项目经理}
     */
    public static boolean isSkyDeptJobExpression(String expression) {
        if (!isExpression(expression)) {
            return false;
        }
        String content = extractVariableName(expression);
        return content != null && content.startsWith(ActivitiConstants.SKY_DEPT_JOB_PREFIX);
    }

    /**
     * 是否为「连续上级」自定义表达式，如 ${skyLeader:1}
     */
    public static boolean isSkyLeaderExpression(String expression) {
        if (!isExpression(expression)) {
            return false;
        }
        String content = extractVariableName(expression);
        return content != null && content.startsWith(ActivitiConstants.SKY_LEADER_PREFIX);
    }

    /**
     * 提取 skyDeptJob 表达式中的岗位名称
     */
    public static String extractSkyDeptJobName(String expression) {
        if (!isSkyDeptJobExpression(expression)) {
            return null;
        }
        String content = extractVariableName(expression);
        return content.substring(ActivitiConstants.SKY_DEPT_JOB_PREFIX.length()).trim();
    }

    /**
     * 提取 skyLeader 表达式中的级数，非法时返回 -1
     */
    public static int extractSkyLeaderLevel(String expression) {
        if (!isSkyLeaderExpression(expression)) {
            return -1;
        }
        String content = extractVariableName(expression);
        String levelStr = content.substring(ActivitiConstants.SKY_LEADER_PREFIX.length()).trim();
        try {
            return Integer.parseInt(levelStr);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 提取表达式中的变量名（简单提取，不支持复杂表达式）
     * 
     * @param expression 表达式字符串，如 "${userId}" 或 "${user.id}"
     * @return 变量名，如 "userId" 或 "user.id"，如果不是表达式则返回 null
     */
    public static String extractVariableName(String expression) {
        if (!isExpression(expression)) {
            return null;
        }
        return expression.substring(2, expression.length() - 1).trim();
    }

    /**
     * 解析审批人表达式为用户ID列表（支持 initiator / 表单字段 / skyDeptJob）
     */
    public static List<String> resolveApproverExpressionToUserIds(
            String expression,
            Map<String, Object> businessData,
            ActivitiUserService activitiUserService) {
        List<String> userIds = new ArrayList<>();
        if (ToolUtil.isBlank(expression)) {
            return userIds;
        }
        if (isSkyDeptJobExpression(expression)) {
            String jobName = extractSkyDeptJobName(expression);
            String initiator = businessData != null
                ? Objects.toString(businessData.get(ActivitiConstants.INITIATOR), StrUtil.EMPTY)
                : StrUtil.EMPTY;
            if (StrUtil.isBlank(initiator) || StrUtil.isBlank(jobName) || activitiUserService == null) {
                LOGGER.warn("解析部门岗位审批人失败: initiator={}, jobName={}, serviceNull={}",
                    initiator, jobName, activitiUserService == null);
                return userIds;
            }
            List<Map<String, Object>> users = activitiUserService.resolveUsersByInitiatorDeptAndJob(initiator, jobName);
            if (CollectionUtils.isNotEmpty(users)) {
                for (Map<String, Object> user : users) {
                    if (user != null && user.get("id") != null) {
                        userIds.add(user.get("id").toString());
                    }
                }
            }
            return userIds;
        }
        if (isSkyLeaderExpression(expression)) {
            int level = extractSkyLeaderLevel(expression);
            String initiator = businessData != null
                ? Objects.toString(businessData.get(ActivitiConstants.INITIATOR), StrUtil.EMPTY)
                : StrUtil.EMPTY;
            if (StrUtil.isBlank(initiator) || level < 1 || activitiUserService == null) {
                LOGGER.warn("解析连续上级审批人失败: initiator={}, level={}, serviceNull={}",
                    initiator, level, activitiUserService == null);
                return userIds;
            }
            List<Map<String, Object>> users = activitiUserService.resolveUsersByLeaderLevel(initiator, level);
            if (CollectionUtils.isNotEmpty(users)) {
                for (Map<String, Object> user : users) {
                    if (user != null && user.get("id") != null) {
                        userIds.add(user.get("id").toString());
                    }
                }
            }
            return userIds;
        }
        String resolved = expression;
        if (isExpression(expression)) {
            resolved = evaluateExpressionAsString(expression, businessData);
        }
        if (!ToolUtil.isBlank(resolved)) {
            Arrays.stream(resolved.split(CommonCharConstants.COMMA_MARK))
                .map(String::trim)
                .filter(id -> !ToolUtil.isBlank(id))
                .forEach(userIds::add);
        }
        return userIds;
    }

    /**
     * 从字符串列表中收集ID（支持表达式评估和逗号分隔）
     * 
     * <p><b>功能说明：</b></p>
     * <ul>
     *   <li>遍历字符串列表，评估每个字符串（可能是表达式）</li>
     *   <li>支持逗号分隔的多个ID</li>
     *   <li>自动去重并添加到目标集合中</li>
     * </ul>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>收集候选组ID（candidateGroups）</li>
     *   <li>收集候选用户ID（candidateUsers）</li>
     * </ul>
     * 
     * @param idList 字符串列表，可能包含表达式或逗号分隔的ID
     * @param businessData 业务数据，用于评估表达式
     * @param targetSet 目标集合，用于存储收集到的ID（自动去重）
     */
    private static void collectIdsFromList(List<String> idList, Map<String, Object> businessData, 
                                          Set<String> targetSet) {
        collectIdsFromList(idList, businessData, targetSet, null);
    }

    private static void collectIdsFromList(List<String> idList, Map<String, Object> businessData,
                                          Set<String> targetSet, ActivitiUserService activitiUserService) {
        if (CollectionUtils.isEmpty(idList) || targetSet == null) {
            return;
        }
        
        for (String id : idList) {
            if (isSkyDeptJobExpression(id) || isSkyLeaderExpression(id) || isExpression(id)) {
                List<String> resolvedIds = resolveApproverExpressionToUserIds(id, businessData, activitiUserService);
                targetSet.addAll(resolvedIds);
                continue;
            }
            
            if (!ToolUtil.isBlank(id)) {
                // 支持逗号分隔的多个ID
                Arrays.asList(id.split(CommonCharConstants.COMMA_MARK)).forEach(splitId -> {
                    String trimmedId = splitId.trim();
                    if (!ToolUtil.isBlank(trimmedId)) {
                        targetSet.add(trimmedId);
                    }
                });
            }
        }
    }

    /**
     * 批量获取用户任务节点的审批人列表（优化版本，减少数据库操作）
     * 
     * <p><b>功能说明：</b></p>
     * <ul>
     *   <li>支持表达式评估（assignee、candidateUsers、candidateGroups 可能是表达式）</li>
     *   <li>先收集所有需要查询的ID，然后批量查询，减少数据库操作次数</li>
     *   <li>自动去重，避免重复添加相同的审批人</li>
     * </ul>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>获取用户任务节点的所有审批人（包括候选组、候选用户、默认审批人）</li>
     *   <li>支持表达式动态计算审批人</li>
     * </ul>
     * 
     * <p><b>示例：</b></p>
     * <pre>{@code
     * // 获取用户任务节点的审批人列表
     * List<Map<String, Object>> approvers = FlowableUtil.getNextTaskApproveBatch(
     *     userTask, 
     *     businessData, 
     *     actGroupUserService, 
     *     iAuthUserService
     * );
     * }</pre>
     * 
     * @param userTask 用户任务节点
     * @param businessData 业务数据，用于评估表达式
     * @param actGroupUserService 用户组服务，用于查询组用户（如果为null，则跳过组用户查询）
     * @param iAuthUserService 用户服务，用于查询用户信息（如果为null，则跳过用户查询）
     * @return 审批人列表，已去重并排序
     */
    public static List<Map<String, Object>> getNextTaskApproveBatch(
            UserTask userTask, 
            Map<String, Object> businessData,
            ActGroupUserService actGroupUserService,
            IAuthUserService iAuthUserService) {
        return getNextTaskApproveBatch(userTask, businessData, actGroupUserService, iAuthUserService, null);
    }

    public static List<Map<String, Object>> getNextTaskApproveBatch(
            UserTask userTask,
            Map<String, Object> businessData,
            ActGroupUserService actGroupUserService,
            IAuthUserService iAuthUserService,
            ActivitiUserService activitiUserService) {
        if (userTask == null) {
            return new ArrayList<>();
        }
        
        if (businessData == null) {
            businessData = new HashMap<>();
        }
        
        // 第一步：收集所有需要查询的ID（先构造数据，避免多次数据库操作）
        Set<String> allGroupIds = new HashSet<>();  // 所有组ID（去重）
        Set<String> allUserIds = new HashSet<>();  // 所有用户ID（去重）
        
        // 1. 收集候选组ID
        collectIdsFromList(userTask.getCandidateGroups(), businessData, allGroupIds);
        
        // 2. 收集候选用户ID（含动态规则表达式）
        collectIdsFromList(userTask.getCandidateUsers(), businessData, allUserIds, activitiUserService);
        
        // 3. 收集默认审批人
        String assignee = userTask.getAssignee();
        if (!ToolUtil.isBlank(assignee)) {
            List<String> resolvedAssignees = resolveApproverExpressionToUserIds(
                assignee, businessData, activitiUserService);
            allUserIds.addAll(resolvedAssignees);
        }

        // 第二步：批量查询数据（减少数据库操作次数）
        List<Map<String, Object>> userList = new ArrayList<>();
        Set<String> processedUserIds = new HashSet<>();  // 已处理的用户ID（用于去重）
        
        // 批量查询组用户
        if (!allGroupIds.isEmpty() && actGroupUserService != null) {
            try {
                List<ActGroupUser> actGroupUserList = actGroupUserService.queryActGroupUser(new ArrayList<>(allGroupIds));
                if (CollectionUtils.isNotEmpty(actGroupUserList)) {
                    actGroupUserList.forEach(bean -> {
                        Map<String, Object> userMation = bean.getUserMation();
                        if (CollectionUtil.isNotEmpty(userMation)) {
                            String userId = userMation.get("id").toString();
                            // 避免重复添加（组用户和直接指定的用户可能重复）
                            if (!processedUserIds.contains(userId)) {
                                userList.add(userMation);
                                processedUserIds.add(userId);
                                // 从 allUserIds 中移除已处理的用户ID，避免重复查询
                                allUserIds.remove(userId);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                LOGGER.warn("查询组用户异常: {}", e.getMessage());
            }
        }
        
        // 批量查询剩余的用户信息（排除已从组用户中获取的用户）
        if (!allUserIds.isEmpty() && iAuthUserService != null) {
            String userIdsStr = Joiner.on(CommonCharConstants.COMMA_MARK).join(allUserIds);
            List<Map<String, Object>> users = iAuthUserService.queryDataMationByIds(userIdsStr);
            if (CollectionUtils.isNotEmpty(users)) {
                users.forEach(user -> {
                    Object userId = user != null ? user.get("id") : null;
                    if (userId != null && !processedUserIds.contains(userId.toString())) {
                        userList.add(user);
                        processedUserIds.add(userId.toString());
                    }
                });
            }
        }
        
        // 移除 null 值
        userList.removeAll(Collections.singleton(null));
        
        // 使用 Stream API 进行过滤、排序并最终收集到 ArrayList 中
        List<Map<String, Object>> resultList = userList.stream()
            .filter(Objects::nonNull)  // 确保 item 不是 null
            .map(item -> item.entrySet().stream()
                .filter(entry -> entry != null && entry.getKey() != null &&
                    entry.getValue() != null && !"".equals(entry.getValue()))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (existing, replacement) -> existing,  // 解决重复键的问题
                    LinkedHashMap::new))  // 保持插入顺序
            )
            .filter(Objects::nonNull)  // 确保转换后的 map 不是 null
            .sorted(Comparator.comparing(p -> String.valueOf(p.get("id")), Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.collectingAndThen(
                Collectors.toCollection(ArrayList::new),
                ArrayList::new
            ));
        
        // 根据 userId 去除重复的审批人
        resultList = resultList.stream()
            .collect(Collectors.collectingAndThen(
                 Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(p -> String.valueOf(p.get("id")), Comparator.nullsLast(Comparator.naturalOrder())))),
                 ArrayList::new
             ));
        
        return resultList;
    }

    /**
     * 查找用户任务节点的默认审批人
     * 
     * <p><b>功能说明：</b></p>
     * <ul>
     *   <li>获取用户任务节点的 assignee 属性</li>
     *   <li>支持表达式评估（assignee 可能是表达式，如 ${userId}）</li>
     *   <li>返回默认审批人的用户信息</li>
     * </ul>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>获取用户任务节点的默认审批人（只有一个）</li>
     *   <li>支持表达式动态计算默认审批人</li>
     * </ul>
     * 
     * <p><b>示例：</b></p>
     * <pre>{@code
     * // 获取用户任务节点的默认审批人
     * Map<String, Object> defaultApprover = FlowableUtil.findDefaultApprover(
     *     userTask, 
     *     businessData, 
     *     iAuthUserService
     * );
     * }</pre>
     * 
     * @param userTask 用户任务节点
     * @param businessData 业务数据，用于评估表达式
     * @param iAuthUserService 用户服务，用于查询用户信息（如果为null，则返回null）
     * @return 默认审批人信息，如果未找到则返回null
     */
    public static Map<String, Object> findDefaultApprover(
            UserTask userTask, 
            Map<String, Object> businessData,
            IAuthUserService iAuthUserService) {
        return findDefaultApprover(userTask, businessData, iAuthUserService, null);
    }

    public static Map<String, Object> findDefaultApprover(
            UserTask userTask,
            Map<String, Object> businessData,
            IAuthUserService iAuthUserService,
            ActivitiUserService activitiUserService) {
        if (userTask == null) {
            return null;
        }

        // 获取用户任务节点的 assignee 属性
        String assignee = userTask.getAssignee();
        if (!ToolUtil.isBlank(assignee)) {
            List<String> resolvedIds = resolveApproverExpressionToUserIds(
                assignee, businessData != null ? businessData : new HashMap<>(), activitiUserService);
            if (CollectionUtils.isNotEmpty(resolvedIds) && iAuthUserService != null) {
                return iAuthUserService.queryDataMationById(resolvedIds.get(0));
            }
            if (isSkyDeptJobExpression(assignee) || isSkyLeaderExpression(assignee)) {
                LOGGER.warn("动态审批人规则解析结果为空: {}", assignee);
            }
        }
        return null;
    }

    /**
     * Map 变量容器的简单实现，用于包装 Map 以适配 Flowable 的 VariableContainer 接口
     * 
     * <p>该类将 Map<String, Object> 包装成 VariableContainer，使得可以在没有 Execution 上下文的情况下评估表达式</p>
     */
    private static class MapVariableContainer implements VariableContainer {
        /**
         * 存储变量的 Map
         */
        private final Map<String, Object> variables;
        
        /**
         * 存储临时变量的 Map（可选，如果不需要区分临时变量，可以都存到 variables 中）
         */
        private final Map<String, Object> transientVariables;

        /**
         * 构造函数
         * 
         * @param variables 变量映射，key 为变量名，value 为变量值
         */
        public MapVariableContainer(Map<String, Object> variables) {
            this.variables = variables != null ? new java.util.HashMap<>(variables) : new java.util.HashMap<>();
            this.transientVariables = new java.util.HashMap<>();
        }

        @Override
        public boolean hasVariable(String variableName) {
            if (variableName == null) {
                return false;
            }
            return variables.containsKey(variableName) || transientVariables.containsKey(variableName);
        }

        @Override
        public Object getVariable(String variableName) {
            if (variableName == null) {
                return null;
            }
            // 优先从普通变量中获取，如果没有则从临时变量中获取
            if (variables.containsKey(variableName)) {
                return variables.get(variableName);
            }
            return transientVariables.get(variableName);
        }

        @Override
        public void setVariable(String variableName, Object value) {
            if (variableName != null) {
                variables.put(variableName, value);
            }
        }

        @Override
        public void setTransientVariable(String variableName, Object value) {
            if (variableName != null) {
                transientVariables.put(variableName, value);
            }
        }
    }

}
