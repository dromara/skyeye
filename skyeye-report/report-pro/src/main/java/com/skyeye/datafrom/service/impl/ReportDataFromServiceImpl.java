/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye-report
 ******************************************************************************/

package com.skyeye.datafrom.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.HttpMethodEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.HttpRequestUtil;
import com.skyeye.database.service.ReportDataBaseService;
import com.skyeye.datafrom.classenum.ReportDataFromType;
import com.skyeye.datafrom.dao.ReportDataFromDao;
import com.skyeye.datafrom.entity.ReportDataFrom;
import com.skyeye.datafrom.entity.ReportDataFromRest;
import com.skyeye.datafrom.service.*;
import com.skyeye.eve.entity.ReportDataSource;
import com.skyeye.eve.entity.ReportMetaDataRow;
import com.skyeye.exception.CustomException;
import com.skyeye.sql.query.factory.QueryerFactory;
import com.skyeye.util.XmlExercise;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @ClassName: ReportDataFromServiceImpl
 * @Description: 数据来源服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/3 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Slf4j
@Service
@SkyeyeService(name = "数据来源", groupName = "数据来源")
public class ReportDataFromServiceImpl extends SkyeyeBusinessServiceImpl<ReportDataFromDao, ReportDataFrom> implements ReportDataFromService {

    @Autowired
    private ReportDataFromJsonService reportDataFromJsonService;

    @Autowired
    private ReportDataFromRestService reportDataFromRestService;

    @Autowired
    private ReportDataFromSQLService reportDataFromSQLService;

    @Autowired
    private ReportDataFromXMLService reportDataFromXMLService;

    @Autowired
    private ReportDataBaseService reportDataBaseService;

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${spring.application.promote}")
    private String springApplicationPromoteName;

    /**
     * 批量取数专用线程池，Bean 定义见 report-common ExecutorConfig#reportDataBatchExecutor
     */
    @Autowired
    private Executor reportDataBatchExecutor;

    /**
     * 配置本地缓存：永久有效，重启后失效。使用 computeIfAbsent 保证高并发下仅加载一次
     */
    private final Map<String, Map<String, Object>> configCache = new ConcurrentHashMap<>();

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        beans.forEach(bean -> {
            bean.put("typeName", ReportDataFromType.getNameByType(Integer.parseInt(bean.get("type").toString())));
        });
        return beans;
    }

    @Override
    public void updatePrepose(ReportDataFrom entity) {
        ReportDataFrom oldDataFrom = selectById(entity.getId());
        deletePostpose(oldDataFrom);
    }

    @Override
    public void writePostpose(ReportDataFrom entity, String userId) {
        super.writePostpose(entity, userId);
        if (entity.getType() == ReportDataFromType.XML.getKey()) {
            entity.getXmlEntity().setFromId(entity.getId());
            reportDataFromXMLService.createEntity(entity.getXmlEntity(), userId);
        } else if (entity.getType() == ReportDataFromType.JSON.getKey()) {
            entity.getJsonEntity().setFromId(entity.getId());
            reportDataFromJsonService.createEntity(entity.getJsonEntity(), userId);
        } else if (entity.getType() == ReportDataFromType.REST.getKey()) {
            entity.getRestEntity().setFromId(entity.getId());
            reportDataFromRestService.createEntity(entity.getRestEntity(), userId);
        } else if (entity.getType() == ReportDataFromType.SQL.getKey()) {
            entity.getSqlEntity().setFromId(entity.getId());
            reportDataFromSQLService.createEntity(entity.getSqlEntity(), userId);
        }
    }

    @Override
    public void deletePostpose(ReportDataFrom entity) {
        if (entity.getType() == ReportDataFromType.XML.getKey()) {
            reportDataFromXMLService.deleteByFromId(entity.getId());
        } else if (entity.getType() == ReportDataFromType.JSON.getKey()) {
            reportDataFromJsonService.deleteByFromId(entity.getId());
        } else if (entity.getType() == ReportDataFromType.REST.getKey()) {
            reportDataFromRestService.deleteByFromId(entity.getId());
        } else if (entity.getType() == ReportDataFromType.SQL.getKey()) {
            reportDataFromSQLService.deleteByFromId(entity.getId());
        }
    }

    @Override
    public ReportDataFrom selectById(String id) {
        ReportDataFrom reportDataFrom = super.selectById(id);
        if (reportDataFrom.getType() == ReportDataFromType.XML.getKey()) {
            reportDataFrom.setXmlEntity(reportDataFromXMLService.getByFromId(id));
        } else if (reportDataFrom.getType() == ReportDataFromType.JSON.getKey()) {
            reportDataFrom.setJsonEntity(reportDataFromJsonService.getByFromId(id));
        } else if (reportDataFrom.getType() == ReportDataFromType.REST.getKey()) {
            reportDataFrom.setRestEntity(reportDataFromRestService.getByFromId(id));
        } else if (reportDataFrom.getType() == ReportDataFromType.SQL.getKey()) {
            reportDataFrom.setSqlEntity(reportDataFromSQLService.getByFromId(id));
        }
        return reportDataFrom;
    }

    /**
     * 根据数据来源id获取该数据来源下的所有数据并组装成map
     *
     * @param fromId      数据来源id
     * @param needGetKeys 需要获取的key
     * @param inputParams 入参
     * @return 该数据来源下的所有数据并组装成map
     */
    @Override
    public Map<String, Object> getReportDataFromMapByFromId(String fromId, List<String> needGetKeys, String inputParams) {
        // 主线程入口：从 ThreadLocal 读取登录态，再委托给带上下文参数的重载
        String userToken = InputObject.getRequest() != null ? GetUserToken.getUserToken(InputObject.getRequest()) : null;
        String tenantId = tenantEnable ? TenantContext.getTenantId() : null;
        return getReportDataFromMapByFromId(fromId, needGetKeys, inputParams, userToken, tenantId);
    }

    /**
     * 批量并行取数时使用：显式传入 userToken/tenantId，避免子线程读不到 Request 上下文
     */
    private Map<String, Object> getReportDataFromMapByFromId(String fromId, List<String> needGetKeys, String inputParams,
                                                             String userToken, String tenantId) {
        String jsonContent = getJsonStrByFromId(fromId, inputParams, userToken, tenantId);
        Map<String, Object> result = new HashMap<>();
        needGetKeys.forEach(key -> {
            Object value = JsonPath.read(jsonContent, String.format(Locale.ROOT, "$.%s", key));
            result.put(key, value);
        });
        result.put("allData", JSONUtil.toBean(jsonContent, null));
        return result;
    }

    /**
     * 根据数据来源id获取数据并转换成json串
     *
     * @param fromId 数据来源id
     * @return 获取数据并转换成json串
     */
    private String getJsonStrByFromId(String fromId, String inputParams) {
        String userToken = InputObject.getRequest() != null ? GetUserToken.getUserToken(InputObject.getRequest()) : null;
        String tenantId = tenantEnable ? TenantContext.getTenantId() : null;
        return getJsonStrByFromId(fromId, inputParams, userToken, tenantId);
    }

    /**
     * 按数据来源类型拉取原始 JSON 字符串。
     * REST 类型需携带 userToken/tenantId 请求头，批量场景必须在主线程捕获后传入。
     */
    private String getJsonStrByFromId(String fromId, String inputParams, String userToken, String tenantId) {
        // 根据dataFromId获取对应type
        ReportDataFrom reportDataFrom = selectById(fromId);
        if (ObjectUtil.isNotEmpty(reportDataFrom)) {
            if (reportDataFrom.getType() == ReportDataFromType.XML.getKey()) {
                return XmlExercise.xml2json(reportDataFrom.getXmlEntity().getXmlContent());
            } else if (reportDataFrom.getType() == ReportDataFromType.JSON.getKey()) {
                return reportDataFrom.getJsonEntity().getJsonContent();
            } else if (reportDataFrom.getType() == ReportDataFromType.REST.getKey()) {
                ReportDataFromRest restEntity = reportDataFrom.getRestEntity();
                Map<String, String> requestHeaderKey2Value = restEntity.getHeader().stream()
                    .collect(Collectors.toMap(bean -> bean.get("headerKey").toString(), bean -> bean.get("headerValue").toString()));
                if (tenantEnable && tenantId != null) {
                    requestHeaderKey2Value.put("tenantId", tenantId);
                }
                // 子线程无法访问 InputObject.getRequest()，必须使用主线程传入的 token
                if (userToken != null) {
                    requestHeaderKey2Value.put("userToken", userToken);
                }

                // 合并 restEntity.getRequestBody() 和 inputParams
                String mergedRequestBody = mergeRequestBody(restEntity.getRequestBody(), inputParams);

                // 解析请求地址：相对路径时从配置中心获取 baseUrl 并拼接
                String fullUrl = resolveRestUrl(restEntity);

                log.info("报表REST数据源请求 - fromId: {}, name: {}, serviceStr: {}, restUrl: {}, fullUrl: {}, method: {}, headers: {}, requestBody: {}",
                    fromId, reportDataFrom.getName(), restEntity.getServiceStr(), restEntity.getRestUrl(), fullUrl,
                    restEntity.getMethod(), requestHeaderKey2Value, mergedRequestBody);

                String responseData = HttpRequestUtil.getDataByRequest(fullUrl, restEntity.getMethod(), requestHeaderKey2Value, mergedRequestBody);

                log.info("报表REST数据源响应 - fromId: {}, fullUrl: {}, response: {}", fromId, fullUrl, responseData);

                // 如果返回的是标准接口格式，则优先校验 returnCode
                Map<String, Object> responseMap = JSONUtil.toBean(responseData, Map.class);
                if (responseMap != null && responseMap.containsKey("returnCode")) {
                    Object codeObj = responseMap.get("returnCode");
                    int returnCode;
                    if (codeObj instanceof Number) {
                        returnCode = ((Number) codeObj).intValue();
                    } else {
                        returnCode = Integer.parseInt(String.valueOf(codeObj));
                    }
                    if (returnCode != 0) {
                        String message = String.valueOf(responseMap.getOrDefault("returnMessage", "外部服务调用异常"));
                        log.error("报表REST数据源调用失败 - fromId: {}, name: {}, serviceStr: {}, restUrl: {}, fullUrl: {}, method: {}, headers: {}, requestBody: {}, returnCode: {}, returnMessage: {}, response: {}",
                            fromId, reportDataFrom.getName(), restEntity.getServiceStr(), restEntity.getRestUrl(), fullUrl,
                            restEntity.getMethod(), requestHeaderKey2Value, mergedRequestBody,
                            returnCode, message, responseData);
                        throw new CustomException("外部服务调用异常：" + message);
                    }
                }
                return responseData;
            } else if (reportDataFrom.getType() == ReportDataFromType.SQL.getKey()) {
                // 1.获取数据源信息
                ReportDataSource dataBase = reportDataBaseService.getReportDataSource(reportDataFrom.getSqlEntity().getDataBaseId());
                List<ReportMetaDataRow> metaDataRows = QueryerFactory.create(dataBase).getMetaDataRows(reportDataFrom.getSqlEntity().getSqlContent());
                Map<String, Object> result = new HashMap<>();
                result.put("data", resetSqlResultData(metaDataRows));
                return JSON.toJSONString(result);
            }
        }
        return "{}";
    }

    /**
     * 解析 REST 请求地址：绝对路径直接使用，相对路径则从配置中心获取 baseUrl 并拼接
     *
     * @param restEntity REST 数据来源实体
     * @return 完整请求地址
     */
    private String resolveRestUrl(ReportDataFromRest restEntity) {
        String restUrl = restEntity.getRestUrl();
        if (restUrl == null || restUrl.isEmpty()) {
            return restUrl;
        }
        if (restUrl.startsWith("http://") || restUrl.startsWith("https://")) {
            return restUrl;
        }
        String serviceStr = restEntity.getServiceStr();
        if (serviceStr == null || serviceStr.isEmpty()) {
            return restUrl;
        }
        String configKey = serviceStr.contains(".") ? serviceStr.substring(serviceStr.indexOf(".") + 1) : serviceStr;
        Map<String, Object> config = getConfigWithCache(env);
        if (config == null || !config.containsKey(configKey)) {
            return restUrl;
        }
        String baseUrl = config.get(configKey).toString();
        if (baseUrl == null || baseUrl.isEmpty()) {
            return restUrl;
        }
        baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        restUrl = restUrl.startsWith("/") ? restUrl : "/" + restUrl;
        return baseUrl + restUrl;
    }

    /**
     * 带本地缓存的配置获取，永久缓存，重启失效。
     * computeIfAbsent 保证高并发下同一 env 仅由单线程加载，其余线程阻塞等待，避免缓存击穿。
     */
    private Map<String, Object> getConfigWithCache(String env) {
        return configCache.computeIfAbsent(env, k -> {
            URI uri = getUri(springApplicationPromoteName);
            String responseData = HttpRequestUtil.getDataByRequest(uri.toString() + "/configRation.json?env=", HttpMethodEnum.GET_REQUEST.getKey(), null, null);
            return JSONUtil.toBean(responseData, null);
        });
    }

    private URI getUri(String springApplicationName) {
        // 根据服务名获取服务实例
        List<ServiceInstance> allInstances = discoveryClient.getInstances(springApplicationName);
        if (CollectionUtils.isEmpty(allInstances)) {
            throw new CustomException(String.format(Locale.ROOT, "this service[%s] has no instance.", springApplicationName));
        }
        return RandomUtil.randomEle(allInstances).getUri();
    }

    /**
     * 合并请求体和输入参数
     *
     * @param requestBody 请求体（JSON字符串）
     * @param inputParams 输入参数（JSON字符串）
     * @return 合并后的JSON字符串
     */
    private String mergeRequestBody(String requestBody, String inputParams) {
        // 如果两者都为空，返回空字符串
        if (ObjectUtil.isEmpty(requestBody) && ObjectUtil.isEmpty(inputParams)) {
            return "";
        }
        // 如果请求体为空，直接返回输入参数
        if (ObjectUtil.isEmpty(requestBody)) {
            return inputParams;
        }
        // 如果输入参数为空，直接返回请求体
        if (ObjectUtil.isEmpty(inputParams)) {
            return requestBody;
        }

        try {
            // 尝试将两者解析为JSON对象并合并
            Map<String, Object> requestBodyMap = JSONUtil.toBean(requestBody, null);
            Map<String, Object> inputParamsMap = JSONUtil.toBean(inputParams, null);

            // 合并：inputParams 覆盖 requestBody 中的相同键
            Map<String, Object> mergedMap = new HashMap<>(requestBodyMap);
            mergedMap.putAll(inputParamsMap);

            return JSON.toJSONString(mergedMap);
        } catch (Exception e) {
            // 如果解析失败，尝试作为字符串拼接（通常这种情况不应该发生）
            // 如果 inputParams 不是有效的 JSON，优先使用 inputParams
            try {
                JSONUtil.toBean(inputParams, null);
                return inputParams;
            } catch (Exception ex) {
                // 如果两者都不是有效的 JSON，返回 inputParams（因为它是动态参数）
                return inputParams;
            }
        }
    }

    private List<Map<String, Object>> resetSqlResultData(List<ReportMetaDataRow> metaDataRows) {
        List<Map<String, Object>> result = new ArrayList<>();
        metaDataRows.forEach(cells -> {
            Map<String, Object> bean = new HashMap<>();
            cells.getCells().forEach((key, cell) -> {
                bean.put(key, cell.getValue());
            });
            result.add(bean);
        });
        return result;
    }

    /**
     * 根据数据来源信息获取要取的数据
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryReportDataFromMationById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String fromId = params.get("id").toString();
        String needGetDataStr = params.get("needGetDataStr").toString();
        String inputParams = normalizeInputParams(params.get("inputParams"));
        outputObject.setBean(buildReportDataResult(fromId, needGetDataStr, inputParams));
    }

    /**
     * 批量根据数据来源取数（预览页合并请求入口）。
     * <p>
     * batchParamsStr 为 JSON 数组，每项结构：{ id, needGetDataStr, inputParams }。
     * 多项之间无依赖时并行执行；单条时同步处理，避免线程切换开销。
     * 返回 bean 为 List，每项含 id、inputParams、data，供前端按分组键回填。
     */
    @Override
    public void queryReportDataFromMationBatch(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        net.sf.json.JSONArray batchArray = net.sf.json.JSONArray.fromObject(params.get("batchParamsStr").toString());
        int size = batchArray.size();
        if (size == 0) {
            outputObject.setBean(Collections.emptyList());
            return;
        }

        // 必须在主线程捕获：PutObject / TenantContext 均为 ThreadLocal，子线程无法读取
        final String userToken = InputObject.getRequest() != null ? GetUserToken.getUserToken(InputObject.getRequest()) : null;
        final String tenantId = tenantEnable ? TenantContext.getTenantId() : null;
        final TenantEnum isolationType = tenantEnable ? TenantContext.getIsolationType() : null;

        if (size == 1) {
            outputObject.setBean(Collections.singletonList(buildBatchItem(batchArray.getJSONObject(0), userToken, tenantId)));
            return;
        }

        // 预分配结果槽位，按 index 写入以保持与请求数组顺序一致
        @SuppressWarnings("unchecked")
        Map<String, Object>[] batchResults = new Map[size];
        CompletableFuture<?>[] futures = new CompletableFuture[size];
        for (int i = 0; i < size; i++) {
            final int index = i;
            JSONObject item = batchArray.getJSONObject(i);
            futures[i] = CompletableFuture.runAsync(
                () -> batchResults[index] = runBatchItemWithContext(item, userToken, tenantId, isolationType),
                reportDataBatchExecutor);
        }

        try {
            CompletableFuture.allOf(futures).join();
        } catch (CompletionException e) {
            // 任一子任务失败则整批失败，还原 CustomException 供前端展示
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            if (cause instanceof CustomException) {
                throw (CustomException) cause;
            }
            throw new CustomException(cause.getMessage());
        }

        outputObject.setBean(Arrays.asList(batchResults));
    }

    /**
     * 子线程执行批量项：注入租户上下文，否则 MyBatis 租户拦截器报「租户ID不能为空」
     */
    private Map<String, Object> runBatchItemWithContext(JSONObject item, String userToken, String tenantId,
                                                        TenantEnum isolationType) {
        try {
            if (tenantEnable) {
                if (tenantId != null) {
                    TenantContext.setTenantId(tenantId);
                }
                if (isolationType != null) {
                    TenantContext.setIsolationType(isolationType);
                }
            }
            return buildBatchItem(item, userToken, tenantId);
        } finally {
            if (tenantEnable) {
                TenantContext.clear();
            }
        }
    }

    /**
     * 组装单条批量结果，id + inputParams 供前端匹配分组
     */
    private Map<String, Object> buildBatchItem(JSONObject item, String userToken, String tenantId) {
        String fromId = item.getString("id");
        String needGetDataStr = item.getString("needGetDataStr");
        Object inputParamsObj = item.has("inputParams") ? item.get("inputParams") : "{}";
        String inputParams = normalizeInputParams(inputParamsObj);
        Map<String, Object> data = buildReportDataResult(fromId, needGetDataStr, inputParams, userToken, tenantId);
        Map<String, Object> batchItem = new HashMap<>();
        batchItem.put("id", fromId);
        batchItem.put("inputParams", inputParams);
        batchItem.put("data", data);
        return batchItem;
    }

    /**
     * 单条取数时使用 ThreadLocal 上下文
     */
    private Map<String, Object> buildReportDataResult(String fromId, String needGetDataStr, String inputParams) {
        String userToken = InputObject.getRequest() != null ? GetUserToken.getUserToken(InputObject.getRequest()) : null;
        String tenantId = tenantEnable ? TenantContext.getTenantId() : null;
        return buildReportDataResult(fromId, needGetDataStr, inputParams, userToken, tenantId);
    }

    /**
     * 按 needGetDataStr 声明的字段路径，从数据源结果中裁剪出前端需要的键值
     */
    private Map<String, Object> buildReportDataResult(String fromId, String needGetDataStr, String inputParams,
                                                      String userToken, String tenantId) {
        Map<String, Object> needGetData = JSONObject.fromObject(needGetDataStr);
        List<String> needGetKeys = needGetData.entrySet().stream().map(bean -> bean.getKey()).collect(Collectors.toList());
        Map<String, Object> data = getReportDataFromMapByFromId(fromId, needGetKeys, inputParams, userToken, tenantId);
        Map<String, Object> result = new HashMap<>();
        needGetData.forEach((key, value) -> {
            if (data.containsKey(key)) {
                result.put(key, data.get(key));
            }
        });
        return result;
    }

    /**
     * 统一 inputParams 为 JSON 字符串。
     * 网关校验后可能是 Map，直接 toString 会得到非 JSON 格式，需显式序列化。
     */
    private String normalizeInputParams(Object inputParamsObj) {
        if (inputParamsObj == null) {
            return "{}";
        }
        if (inputParamsObj instanceof Map || inputParamsObj instanceof List) {
            return JSONUtil.toJsonStr(inputParamsObj);
        }
        String inputParams = String.valueOf(inputParamsObj);
        return ObjectUtil.isEmpty(inputParams) ? "{}" : inputParams;
    }

}
