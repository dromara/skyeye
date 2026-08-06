/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.common.constans.ReportConstants;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.service.ReportCommonService;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.HttpRequestUtil;
import com.skyeye.database.service.ReportDataBaseService;
import com.skyeye.eve.entity.ReportDataSource;
import com.skyeye.eve.entity.ReportMetaDataColumn;
import com.skyeye.sql.query.factory.QueryerFactory;
import net.sf.json.JSONArray;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ReportCommonServiceImpl
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/17 21:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "公共接口", groupName = "公共接口", manageShow = false)
public class ReportCommonServiceImpl implements ReportCommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportCommonServiceImpl.class);

    @Autowired
    private ReportDataBaseService reportDataBaseService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    public void testConnection(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String driverClass = params.get("driverClass").toString();
        String url = params.get("url").toString();
        String user = params.get("user").toString();
        String pass = params.containsKey("pass") ? params.get("pass").toString() : "";
        connectionDataBase(driverClass, url, user, pass, outputObject);
    }

    /**
     * 连接数据源
     *
     * @param driverClass  数据源驱动类
     * @param url          数据源连接字符串
     * @param user         用户名
     * @param password     密码
     * @param outputObject 出参以及提示信息的返回值对象
     * @return
     */
    @Override
    public boolean connectionDataBase(final String driverClass, final String url, final String user, final String password, OutputObject outputObject) {
        Connection conn = null;
        try {
            Class.forName(driverClass);
            conn = DriverManager.getConnection(url, user, password);
            return true;
        } catch (final Exception e) {
            LOGGER.warn("testConnection", e);
            if (outputObject != null) {
                outputObject.setreturnMessage(e.getMessage());
            }
            return false;
        } finally {
            this.releaseConnection(conn);
        }
    }

    @Override
    public void parseXmlText(InputObject inputObject, OutputObject outputObject) {
        Element rootElement;
        try {
            Map<String, Object> inputParams = inputObject.getParams();
            // 获取xml文件
            Document document = DocumentHelper.parseText(inputParams.get("xmlText").toString());
            // 获取根目录
            rootElement = document.getRootElement();
        } catch (Exception ex) {
            LOGGER.info("该文本不符合xml文件格式, 故无法解析. ", ex);
            outputObject.setreturnMessage("该文本不符合xml文件格式, 故无法解析.");
            return;
        }
        Map<String, Object> resultBean = new HashMap<>();
        Set<String> nodeSet = new HashSet<>();
        parseSubNode(rootElement, nodeSet, rootElement.getName());
        resultBean.put("nodeArray", new ArrayList<>(nodeSet));
        outputObject.setBean(resultBean);
    }

    // 解析并拼接节点下所有子节点名称
    private void parseSubNode(Element element, Set<String> nodeSet, String path) {
        // 处理元素的属性
        List<org.dom4j.Attribute> attributes = element.attributes();
        for (org.dom4j.Attribute attr : attributes) {
            nodeSet.add(path + "." + "@" + attr.getName());
        }

        List<Element> childElements = element.elements();

        // 处理元素的文本内容
        if (!element.getTextTrim().isEmpty() && element.elements().isEmpty()) {
            nodeSet.add(path + ".#text");
        }

        if (childElements.isEmpty()) {
            // 如果是叶子节点（没有子元素），添加当前路径
            nodeSet.add(path);
            return;
        }

        // 统计子节点名称出现的次数，用于检测重复节点
        Map<String, Integer> elementNameCount = new HashMap<>();
        for (Element child : childElements) {
            String childName = child.getName();
            elementNameCount.put(childName, elementNameCount.getOrDefault(childName, 0) + 1);
        }

        // 处理每个子节点
        for (Element child : childElements) {
            String childName = child.getName();
            String childPath;

            // 如果节点名称出现多次，则使用数组表示法
            if (elementNameCount.get(childName) > 1) {
                childPath = path + "." + childName + "[*]";
            } else {
                childPath = path + "." + childName;
            }

            // 递归处理子节点
            parseSubNode(child, nodeSet, childPath);
        }
    }

    @Override
    public void parseJsonText(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> inputParams = inputObject.getParams();
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(inputParams.get("jsonText").toString(), Map.class);
        Set<String> result = new HashSet<>();
        parseJsonSubNode(map, result, true, "");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("nodeArray", result);
        outputObject.setBean(resultMap);
    }

    /**
     * 解析Json并拼接节点下所有子节点名称
     *
     * @param paramMap    被解析的map
     * @param sets        存放所有解析后的节点名称信息
     * @param isFirstTime 是否首层调用
     * @param name        名称
     */
    @Override
    public void parseJsonSubNode(Map<String, Object> paramMap, Set<String> sets, boolean isFirstTime, String name) {
        if (paramMap == null || paramMap.isEmpty()) {
            // 处理空Map的情况
            if (!isFirstTime && !name.isEmpty()) {
                sets.add(name);
            }
            return;
        }

        Set<Map.Entry<String, Object>> entries = paramMap.entrySet();
        String key;
        Object value;
        for (Map.Entry<String, Object> obj : entries) {
            key = obj.getKey();
            value = obj.getValue();
            String currentPath = getNewName(isFirstTime, name, key);

            if (value == null) {
                // 处理null值
                sets.add(currentPath);
            } else if (value instanceof Map) {
                // 处理嵌套Map
                parseJsonSubNode((Map<String, Object>) value, sets, false, currentPath);
            } else if (value instanceof List) {
                List<Object> tempList = (List<Object>) value;
                // 使用[*]表示数组
                String arrayPath = currentPath + "[*]";

                // 处理空数组的情况
                if (tempList.isEmpty()) {
                    sets.add(arrayPath);
                    continue;
                }

                // 检查数组中是否有null值
                boolean hasNullElement = false;
                for (Object item : tempList) {
                    if (item == null) {
                        hasNullElement = true;
                        break;
                    }
                }

                if (hasNullElement) {
                    sets.add(arrayPath);
                    continue;
                }

                // 检查第一个元素类型，假设数组中的元素类型一致
                Object firstElement = tempList.get(0);

                if (firstElement instanceof Map) {
                    // 处理对象数组
                    for (Object object : tempList) {
                        if (object instanceof Map) {
                            parseJsonSubNode((Map<String, Object>) object, sets, false, arrayPath);
                        } else if (object != null) {
                            // 处理混合类型数组中的非Map元素
                            sets.add(arrayPath);
                        }
                    }
                } else if (firstElement instanceof List) {
                    // 处理嵌套数组
                    for (Object object : tempList) {
                        if (object instanceof List) {
                            List<Object> nestedList = (List<Object>) object;
                            String nestedArrayPath = arrayPath + "[*]";

                            // 处理空嵌套数组
                            if (nestedList.isEmpty()) {
                                sets.add(nestedArrayPath);
                                continue;
                            }

                            // 处理嵌套数组中的第一个元素
                            Object nestedFirstElement = nestedList.get(0);
                            if (nestedFirstElement instanceof Map) {
                                for (Object nestedObject : nestedList) {
                                    if (nestedObject instanceof Map) {
                                        parseJsonSubNode((Map<String, Object>) nestedObject, sets, false, nestedArrayPath);
                                    }
                                }
                            } else {
                                // 嵌套数组中是基本类型
                                sets.add(nestedArrayPath);
                            }
                        }
                    }
                } else {
                    // 基本类型数组
                    sets.add(arrayPath);
                }
            } else {
                // 基本类型值
                sets.add(currentPath);
            }
        }
    }

    // 根据是否首个节点后, 依照不同规则进行拼接字符
    private String getNewName(boolean isFirstTime, String name, String key) {
        return isFirstTime ? key : name.concat(".").concat(key);
    }

    /**
     * 释放数据源
     *
     * @param conn
     */
    private void releaseConnection(final Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (final SQLException ex) {
                LOGGER.warn("测试数据库连接后释放资源失败", ex);
            }
        }
    }

    @Override
    public void queryDataBaseMationList(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> beans = ReportConstants.DataBaseMation.getDataBaseMationList();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 获取连接池类型
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryPoolMationList(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> beans = ReportConstants.PoolMation.getPoolMationList();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public void parseSQLText(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String sqlText = params.get("sqlText").toString();
        String dataBaseId = params.get("dataBaseId").toString();
        LOGGER.info("data base id is {}", dataBaseId);
        try {
            // 1.获取数据源信息
            ReportDataSource dataBase = reportDataBaseService.getReportDataSource(dataBaseId);
            // 2.获取查询的列信息
            List<ReportMetaDataColumn> dataColumns = QueryerFactory.create(dataBase).parseMetaDataColumns(sqlText);

            // 3.处理列信息，转换为统一的节点路径格式
            Set<String> nodePaths = new HashSet<>();

            // 提取主表名或查询别名
            String mainTableName = extractMainTableName(sqlText);

            // 处理列路径 - 使用ReportMetaDataColumn类的正确属性名
            for (ReportMetaDataColumn column : dataColumns) {
                String columnName = column.getName(); // 使用name属性代替之前的columnName

                // 由于ReportMetaDataColumn没有tableName属性，我们可以尝试从列名解析
                // 列名可能是"表名.列名"格式
                String tableName = "";
                if (columnName.contains(".")) {
                    String[] parts = columnName.split("\\.", 2);
                    tableName = parts[0];
                    columnName = parts[1];
                }

                // 优先使用从列名中提取的表名，如果为空则使用主表名
                String prefix = (tableName != null && !tableName.isEmpty()) ? tableName : mainTableName;

                // 如果列来自子查询或复杂表达式，可能没有表名
                if (prefix == null || prefix.isEmpty()) {
                    nodePaths.add(columnName);
                } else {
                    // 格式化为"表名.列名"的形式
                    nodePaths.add(prefix + "." + columnName);
                }
                column.setName("data[*]." + column.getName());
            }

            // 4.构造返回结果
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("nodeArray", new ArrayList<>(nodePaths));
            resultMap.put("columns", dataColumns); // 保留原始列信息

            outputObject.setBean(resultMap);
            outputObject.setBeans(JSONArray.fromObject(dataColumns)); // 保持原有行为兼容
        } catch (Exception e) {
            LOGGER.error("解析SQL失败", e);
            outputObject.setreturnMessage("解析SQL失败: " + e.getMessage());
        }
    }

    /**
     * 从SQL中提取主表名或查询别名
     * 这是一个简化版实现，对于复杂SQL可能需要更复杂的解析
     *
     * @param sql SQL查询语句
     * @return 主表名或空字符串
     */
    private String extractMainTableName(String sql) {
        try {
            // 简化处理：尝试提取FROM后的第一个表名
            sql = sql.toLowerCase().replaceAll("\\s+", " ");
            int fromIndex = sql.indexOf(" from ");
            if (fromIndex < 0) {
                return "";
            }

            // 截取FROM子句
            String fromClause = sql.substring(fromIndex + 6);

            // 移除可能的WHERE、GROUP BY、ORDER BY等子句
            int whereIndex = fromClause.indexOf(" where ");
            if (whereIndex > 0) {
                fromClause = fromClause.substring(0, whereIndex);
            }

            int groupByIndex = fromClause.indexOf(" group by ");
            if (groupByIndex > 0) {
                fromClause = fromClause.substring(0, groupByIndex);
            }

            int orderByIndex = fromClause.indexOf(" order by ");
            if (orderByIndex > 0) {
                fromClause = fromClause.substring(0, orderByIndex);
            }

            int joinIndex = fromClause.indexOf(" join ");
            if (joinIndex > 0) {
                fromClause = fromClause.substring(0, joinIndex);
            }

            // 提取表名（可能包含别名）
            fromClause = fromClause.trim();
            String[] parts = fromClause.split("\\s+");

            // 如果有别名，使用别名
            if (parts.length > 1) {
                return parts[parts.length - 1]; // 最后一个词可能是别名
            } else if (parts.length == 1) {
                return parts[0]; // 只有表名
            }

            return "";
        } catch (Exception e) {
            LOGGER.warn("提取表名失败", e);
            return "";
        }
    }

    @Override
    public void parseRestText(InputObject inputObject, OutputObject outputObject) {
        try {
            Map<String, Object> params = inputObject.getParams();

            // 获取请求参数
            String serviceStr = params.containsKey("serviceStr") ? params.get("serviceStr").toString() : "";
            String requestUrl = params.get("requestUrl").toString();

            // 拼接服务地址和请求URL
            if (!StrUtil.isEmpty(serviceStr)) {
                // 修复逻辑错误，应该是服务地址+请求路径
                if (!serviceStr.endsWith("/") && !requestUrl.startsWith("/")) {
                    requestUrl = serviceStr + "/" + requestUrl;
                } else {
                    requestUrl = serviceStr + requestUrl;
                }
            }

            String requestMethod = params.get("requestMethod").toString();
            String requestHeader = params.get("requestHeader").toString();
            String requestBody = params.containsKey("requestBody") ? params.get("requestBody").toString() : "";

            // 处理请求头
            List<Map<String, Object>> array = JSONUtil.toList(requestHeader, null);
            Map<String, String> requestHeaderKey2Value = array.stream()
                .collect(Collectors.toMap(bean -> bean.get("headerKey").toString(), bean -> bean.get("headerValue").toString()));

            if (tenantEnable) {
                requestHeaderKey2Value.put("tenantId", TenantContext.getTenantId());
            }
            String userToken = GetUserToken.getUserToken(InputObject.getRequest());
            requestHeaderKey2Value.put("userToken", userToken);

            LOGGER.info("发送REST请求: {}, 方法: {}", requestUrl, requestMethod);

            // 发送请求
            String responseData = HttpRequestUtil.getDataByRequest(requestUrl, requestMethod, requestHeaderKey2Value, requestBody);

            if (responseData == null || responseData.trim().isEmpty()) {
                throw new RuntimeException("接口返回空数据");
            }

            // 存放并解析响应结果
            Set<String> result = new HashSet<>();

            try {
                // 尝试作为JSON对象解析
                Map<String, Object> responseMap = JSONUtil.toBean(responseData, Map.class);
                parseJsonSubNode(responseMap, result, true, "");
            } catch (Exception e) {
                try {
                    // 尝试作为JSON数组解析
                    List<Object> responseList = JSONUtil.toList(responseData, Object.class);
                    if (!responseList.isEmpty()) {
                        // 添加顶层数组标记
                        String arrayPath = "[*]";

                        Object firstItem = responseList.get(0);
                        if (firstItem instanceof Map) {
                            // 数组中是对象，递归解析第一个对象的结构
                            parseJsonSubNode((Map<String, Object>) firstItem, result, false, arrayPath);
                        } else {
                            // 数组中是基本类型
                            result.add(arrayPath);
                        }
                    }
                } catch (Exception ex) {
                    // 返回不是JSON格式，作为纯文本处理
                    result.add("response");
                    LOGGER.warn("接口返回的数据不是有效的JSON格式: {}", responseData);
                }
            }

            // 返回结果
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("nodeArray", result);

            // 添加原始响应数据用于调试
            resultMap.put("responseData", responseData);

            outputObject.setBean(resultMap);
        } catch (Exception ex) {
            LOGGER.error("接口解析失败", ex);
            outputObject.setreturnMessage("接口解析失败: " + ex.getMessage());
        }
    }

}
