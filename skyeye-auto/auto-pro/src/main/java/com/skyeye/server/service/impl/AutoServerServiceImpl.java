/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.api.entity.AutoApi;
import com.skyeye.api.service.AutoApiService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.environment.service.AutoEnvironmentService;
import com.skyeye.exception.CustomException;
import com.skyeye.microservice.entity.AutoMicroservice;
import com.skyeye.microservice.service.AutoMicroserviceService;
import com.skyeye.server.classenum.AutoServerMetricStatusEnum;
import com.skyeye.server.classenum.AutoServerOnlineStatusEnum;
import com.skyeye.server.classenum.AutoServerProbeTypeEnum;
import com.skyeye.server.classnum.AutoServerAuthEnum;
import com.skyeye.server.dao.AutoServerDao;
import com.skyeye.server.entity.AutoServer;
import com.skyeye.server.service.AutoServerMetricHistoryService;
import com.skyeye.server.service.AutoServerService;
import com.skyeye.server.util.AutoServerSshCrypto;
import com.skyeye.server.util.AutoServerSshMetricCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: AutoServerServiceImpl
 * @Description: 服务器管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 8:59
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
@SkyeyeService(name = "服务器管理", groupName = "服务器管理", teamAuth = true)
public class AutoServerServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoServerDao, AutoServer> implements AutoServerService {

    private static final int DEFAULT_TIMEOUT = 3000;
    private static final int DEFAULT_SSH_TIMEOUT = 15000;

    @Autowired
    private AutoEnvironmentService autoEnvironmentService;

    @Autowired
    @Lazy
    private AutoMicroserviceService autoMicroserviceService;

    @Autowired
    @Lazy
    private AutoApiService autoApiService;

    @Autowired
    @Lazy
    private AutoServerMetricHistoryService autoServerMetricHistoryService;

    @Override
    public Class getAuthEnumClass() {
        return AutoServerAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(AutoServerAuthEnum.ADD.getKey(), AutoServerAuthEnum.EDIT.getKey(), AutoServerAuthEnum.DELETE.getKey());
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (tenantEnable) {
            commonPageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryAutoServerList(commonPageInfo);
        autoEnvironmentService.setMationForMap(beans, "environmentId", "environmentMation");
        return beans;
    }

    @Override
    public AutoServer selectById(String id) {
        AutoServer autoServer = super.selectById(id);
        autoEnvironmentService.setDataMation(autoServer, AutoServer::getEnvironmentId);
        return autoServer;
    }

    @Override
    public List<AutoServer> selectByIds(String... ids) {
        List<AutoServer> autoServers = super.selectByIds(ids);
        autoEnvironmentService.setDataMation(autoServers, AutoServer::getEnvironmentId);
        return autoServers;
    }

    @Override
    public void createPrepose(AutoServer entity) {
        fillProbeDefaults(entity);
        fillSshDefaults(entity);
        validateSshConfig(entity, true);
        encryptSshPasswordIfPresent(entity);
        if (StrUtil.isBlank(entity.getOnlineStatus())) {
            entity.setOnlineStatus(AutoServerOnlineStatusEnum.UNKNOWN.getKey());
        }
        if (StrUtil.isBlank(entity.getMetricStatus())) {
            entity.setMetricStatus(AutoServerMetricStatusEnum.UNKNOWN.getKey());
        }
    }

    @Override
    public void updatePrepose(AutoServer entity) {
        fillProbeDefaults(entity);
        fillSshDefaults(entity);
        // 密码留空表示不修改；置 null 避免 MyBatis-Plus 用空串覆盖
        if (StrUtil.isBlank(entity.getSshPassword())) {
            entity.setSshPassword(null);
        } else {
            encryptSshPasswordIfPresent(entity);
        }
        validateSshConfig(entity, false);
    }

    @Override
    public void queryAutoServerListByEnvironmentId(InputObject inputObject, OutputObject outputObject) {
        String environmentId = inputObject.getParams().get("environmentId").toString();
        if (StrUtil.isEmpty(environmentId)) {
            return;
        }
        QueryWrapper<AutoServer> queryWrapper = new QueryWrapper();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoServer::getEnvironmentId), environmentId);
        List<AutoServer> result = list(queryWrapper);
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void queryServerMonitorDashboard(InputObject inputObject, OutputObject outputObject) {
        String objectId = String.valueOf(inputObject.getParams().get("objectId"));
        checkObjectId(objectId);
        List<AutoServer> servers = listByObjectId(objectId);
        outputObject.setBean(buildDashboardBean(objectId, servers));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void probeAutoServersByObjectId(InputObject inputObject, OutputObject outputObject) {
        String objectId = String.valueOf(inputObject.getParams().get("objectId"));
        checkObjectId(objectId);
        List<AutoServer> servers = listByObjectId(objectId);
        for (AutoServer server : servers) {
            probeAndPersist(server);
        }
        // 重新查询，带上缓存刷新后的最新结果
        List<AutoServer> latest = listByObjectId(objectId);
        outputObject.setBean(buildDashboardBean(objectId, latest));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void probeAutoServerById(InputObject inputObject, OutputObject outputObject) {
        String id = String.valueOf(inputObject.getParams().get("id"));
        if (StrUtil.isBlank(id) || "null".equalsIgnoreCase(id)) {
            throw new CustomException("请选择服务器。");
        }
        AutoServer server = selectById(id);
        if (server == null) {
            throw new CustomException("服务器不存在。");
        }
        probeAndPersist(server);
        AutoServer latest = selectById(id);
        Map<String, Object> bean = toServerRow(latest, countMicroserviceByServer(server.getObjectId()), countApiByServer(server.getObjectId()));
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void collectServerMetricsByObjectId(InputObject inputObject, OutputObject outputObject) {
        String objectId = String.valueOf(inputObject.getParams().get("objectId"));
        checkObjectId(objectId);
        List<AutoServer> servers = listByObjectId(objectId);
        int collected = 0;
        for (AutoServer server : servers) {
            if (isSshEnabled(server)) {
                collectAndPersist(server);
                collected++;
            }
        }
        Map<String, Object> bean = buildDashboardBean(objectId, listByObjectId(objectId));
        bean.put("collectedCount", collected);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void collectServerMetricsById(InputObject inputObject, OutputObject outputObject) {
        String id = String.valueOf(inputObject.getParams().get("id"));
        if (StrUtil.isBlank(id) || "null".equalsIgnoreCase(id)) {
            throw new CustomException("请选择服务器。");
        }
        AutoServer server = selectById(id);
        if (server == null) {
            throw new CustomException("服务器不存在。");
        }
        if (!isSshEnabled(server)) {
            throw new CustomException("请先在服务器编辑页启用 SSH 资源采集并配置账号。");
        }
        collectAndPersist(server);
        AutoServer latest = selectById(id);
        Map<String, Object> bean = toServerRow(latest, countMicroserviceByServer(server.getObjectId()), countApiByServer(server.getObjectId()));
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public int collectAllEnabledServerMetrics() {
        QueryWrapper<AutoServer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoServer::getSshEnabled), 1);
        List<AutoServer> servers;
        try {
            servers = list(queryWrapper);
        } catch (Exception e) {
            log.warn("查询待采集服务器失败: {}", e.getMessage());
            return 0;
        }
        int collected = 0;
        for (AutoServer server : servers) {
            if (!isSshEnabled(server) || StrUtil.isBlank(server.getSshPassword())) {
                continue;
            }
            try {
                collectAndPersist(server);
                collected++;
            } catch (Exception e) {
                log.warn("定时采集服务器[{}]失败: {}", server.getId(), e.getMessage());
            }
        }
        return collected;
    }

    private void checkObjectId(String objectId) {
        if (StrUtil.isBlank(objectId) || "null".equalsIgnoreCase(objectId)) {
            throw new CustomException("请先选择项目。");
        }
    }

    private List<AutoServer> listByObjectId(String objectId) {
        QueryWrapper<AutoServer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoServer::getObjectId), objectId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(AutoServer::getName));
        List<AutoServer> servers = list(queryWrapper);
        autoEnvironmentService.setDataMation(servers, AutoServer::getEnvironmentId);
        return servers;
    }

    private Map<String, Object> buildDashboardBean(String objectId, List<AutoServer> servers) {
        Map<String, Long> msCountMap = countMicroserviceByServer(objectId);
        Map<String, Long> apiCountMap = countApiByServer(objectId);

        int online = 0;
        int offline = 0;
        int unknown = 0;
        int metricOk = 0;
        int metricFail = 0;
        int metricUnknown = 0;
        int sshEnabledCount = 0;
        BigDecimal cpuSum = BigDecimal.ZERO;
        int cpuCount = 0;
        BigDecimal memSum = BigDecimal.ZERO;
        int memCount = 0;
        long latencySum = 0;
        int latencyCount = 0;
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Map<String, Object>> envMap = new LinkedHashMap<>();

        for (AutoServer server : servers) {
            String status = StrUtil.blankToDefault(server.getOnlineStatus(), AutoServerOnlineStatusEnum.UNKNOWN.getKey());
            if (StrUtil.equals(status, AutoServerOnlineStatusEnum.ONLINE.getKey())) {
                online++;
            } else if (StrUtil.equals(status, AutoServerOnlineStatusEnum.OFFLINE.getKey())) {
                offline++;
            } else {
                unknown++;
            }
            if (server.getLastLatency() != null && server.getLastLatency() >= 0
                && StrUtil.equals(status, AutoServerOnlineStatusEnum.ONLINE.getKey())) {
                latencySum += server.getLastLatency();
                latencyCount++;
            }
            if (isSshEnabled(server)) {
                sshEnabledCount++;
            }
            String metricStatus = StrUtil.blankToDefault(server.getMetricStatus(), AutoServerMetricStatusEnum.UNKNOWN.getKey());
            if (StrUtil.equals(metricStatus, AutoServerMetricStatusEnum.OK.getKey())) {
                metricOk++;
                if (server.getCpuUsage() != null) {
                    cpuSum = cpuSum.add(server.getCpuUsage());
                    cpuCount++;
                }
                if (server.getMemUsage() != null) {
                    memSum = memSum.add(server.getMemUsage());
                    memCount++;
                }
            } else if (StrUtil.equals(metricStatus, AutoServerMetricStatusEnum.FAIL.getKey())) {
                metricFail++;
            } else {
                metricUnknown++;
            }

            Map<String, Object> row = toServerRow(server, msCountMap, apiCountMap);
            rows.add(row);

            String envId = StrUtil.blankToDefault(server.getEnvironmentId(), "unknown");
            Map<String, Object> envRow = envMap.computeIfAbsent(envId, key -> {
                Map<String, Object> item = new HashMap<>();
                String envName = server.getEnvironmentMation() == null ? "未关联环境"
                    : StrUtil.blankToDefault(server.getEnvironmentMation().getName(), "未关联环境");
                item.put("environmentId", key);
                item.put("environmentName", "unknown".equals(key) ? "未关联环境" : envName);
                item.put("total", 0);
                item.put("online", 0);
                item.put("offline", 0);
                item.put("unknown", 0);
                return item;
            });
            envRow.put("total", ((Number) envRow.get("total")).intValue() + 1);
            if (StrUtil.equals(status, AutoServerOnlineStatusEnum.ONLINE.getKey())) {
                envRow.put("online", ((Number) envRow.get("online")).intValue() + 1);
            } else if (StrUtil.equals(status, AutoServerOnlineStatusEnum.OFFLINE.getKey())) {
                envRow.put("offline", ((Number) envRow.get("offline")).intValue() + 1);
            } else {
                envRow.put("unknown", ((Number) envRow.get("unknown")).intValue() + 1);
            }
        }

        Map<String, Object> bean = new HashMap<>();
        bean.put("total", servers.size());
        bean.put("online", online);
        bean.put("offline", offline);
        bean.put("unknown", unknown);
        bean.put("avgLatency", latencyCount == 0 ? 0 : Math.round(latencySum * 1.0 / latencyCount));
        bean.put("sshEnabledCount", sshEnabledCount);
        bean.put("metricOk", metricOk);
        bean.put("metricFail", metricFail);
        bean.put("metricUnknown", metricUnknown);
        bean.put("avgCpuUsage", cpuCount == 0 ? null : cpuSum.divide(BigDecimal.valueOf(cpuCount), 2, java.math.RoundingMode.HALF_UP));
        bean.put("avgMemUsage", memCount == 0 ? null : memSum.divide(BigDecimal.valueOf(memCount), 2, java.math.RoundingMode.HALF_UP));
        bean.put("serverList", rows);
        bean.put("envList", new ArrayList<>(envMap.values()));
        return bean;
    }

    private Map<String, Object> toServerRow(AutoServer server, Map<String, Long> msCountMap, Map<String, Long> apiCountMap) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", server.getId());
        row.put("name", server.getName());
        row.put("ip", server.getIp());
        row.put("cpu", server.getCpu());
        row.put("disk", server.getDisk());
        row.put("mem", server.getMem());
        row.put("remark", server.getRemark());
        row.put("environmentId", server.getEnvironmentId());
        row.put("environmentName", server.getEnvironmentMation() == null ? "" : server.getEnvironmentMation().getName());
        row.put("probeType", StrUtil.blankToDefault(server.getProbeType(), AutoServerProbeTypeEnum.PING.getKey()));
        row.put("probePort", resolveProbePort(server));
        row.put("probePath", StrUtil.blankToDefault(server.getProbePath(), "/"));
        row.put("probeTimeout", server.getProbeTimeout() == null ? DEFAULT_TIMEOUT : server.getProbeTimeout());
        row.put("onlineStatus", StrUtil.blankToDefault(server.getOnlineStatus(), AutoServerOnlineStatusEnum.UNKNOWN.getKey()));
        row.put("onlineStatusName", onlineStatusName(server.getOnlineStatus()));
        row.put("lastProbeTime", server.getLastProbeTime());
        row.put("lastLatency", server.getLastLatency());
        row.put("lastProbeMsg", server.getLastProbeMsg());
        row.put("sshEnabled", isSshEnabled(server) ? 1 : 0);
        row.put("sshPort", server.getSshPort() == null || server.getSshPort() <= 0 ? 22 : server.getSshPort());
        row.put("sshUser", server.getSshUser());
        row.put("sshTimeout", server.getSshTimeout() == null || server.getSshTimeout() <= 0 ? DEFAULT_SSH_TIMEOUT : server.getSshTimeout());
        row.put("hasSshPassword", StrUtil.isNotBlank(server.getSshPassword()));
        row.put("cpuUsage", server.getCpuUsage());
        row.put("memUsage", server.getMemUsage());
        row.put("diskUsage", server.getDiskUsage());
        row.put("loadAvg", server.getLoadAvg());
        row.put("metricStatus", StrUtil.blankToDefault(server.getMetricStatus(), AutoServerMetricStatusEnum.UNKNOWN.getKey()));
        row.put("metricStatusName", metricStatusName(server.getMetricStatus()));
        row.put("lastMetricTime", server.getLastMetricTime());
        row.put("lastMetricMsg", server.getLastMetricMsg());
        row.put("microserviceCount", msCountMap.getOrDefault(server.getId(), 0L));
        row.put("apiCount", apiCountMap.getOrDefault(server.getId(), 0L));
        return row;
    }

    private Map<String, Long> countMicroserviceByServer(String objectId) {
        QueryWrapper<AutoMicroservice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoMicroservice::getObjectId), objectId);
        List<AutoMicroservice> list = autoMicroserviceService.list(queryWrapper);
        return list.stream()
            .filter(item -> StrUtil.isNotBlank(item.getServerId()))
            .collect(Collectors.groupingBy(AutoMicroservice::getServerId, Collectors.counting()));
    }

    private Map<String, Long> countApiByServer(String objectId) {
        QueryWrapper<AutoApi> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoApi::getObjectId), objectId);
        List<AutoApi> list = autoApiService.list(queryWrapper);
        return list.stream()
            .filter(item -> StrUtil.isNotBlank(item.getServerId()))
            .collect(Collectors.groupingBy(AutoApi::getServerId, Collectors.counting()));
    }

    private void probeAndPersist(AutoServer server) {
        ProbeResult result = doProbe(server);
        AutoServer update = new AutoServer();
        update.setId(server.getId());
        update.setOnlineStatus(result.online ? AutoServerOnlineStatusEnum.ONLINE.getKey() : AutoServerOnlineStatusEnum.OFFLINE.getKey());
        update.setLastProbeTime(DateUtil.getTimeAndToString());
        update.setLastLatency(result.latencyMs);
        update.setLastProbeMsg(StrUtil.maxLength(result.message, 500));
        update.setLastUpdateId(resolveOperatorId());
        update.setLastUpdateTime(DateUtil.getTimeAndToString());
        updateById(update);
        // 同步内存对象，便于后续组装
        server.setOnlineStatus(update.getOnlineStatus());
        server.setLastProbeTime(update.getLastProbeTime());
        server.setLastLatency(update.getLastLatency());
        server.setLastProbeMsg(update.getLastProbeMsg());
        this.refreshCache(server.getId());
    }

    private void collectAndPersist(AutoServer server) {
        String plainPassword;
        try {
            plainPassword = AutoServerSshCrypto.decrypt(server.getSshPassword());
        } catch (Exception e) {
            plainPassword = null;
        }
        AutoServerSshMetricCollector.MetricResult result = AutoServerSshMetricCollector.collect(
            StrUtil.trim(server.getIp()),
            server.getSshPort() == null || server.getSshPort() <= 0 ? 22 : server.getSshPort(),
            StrUtil.trim(server.getSshUser()),
            plainPassword,
            server.getSshTimeout() == null || server.getSshTimeout() <= 0 ? DEFAULT_SSH_TIMEOUT : server.getSshTimeout()
        );
        // 历史明文密码：采集成功后自动改写成密文
        if (result.isSuccess() && StrUtil.isNotBlank(server.getSshPassword())
            && !AutoServerSshCrypto.isEncrypted(server.getSshPassword())) {
            AutoServer pwdUpdate = new AutoServer();
            pwdUpdate.setId(server.getId());
            pwdUpdate.setSshPassword(AutoServerSshCrypto.encrypt(server.getSshPassword()));
            updateById(pwdUpdate);
            server.setSshPassword(pwdUpdate.getSshPassword());
        }
        AutoServer update = new AutoServer();
        update.setId(server.getId());
        update.setMetricStatus(result.isSuccess()
            ? AutoServerMetricStatusEnum.OK.getKey()
            : AutoServerMetricStatusEnum.FAIL.getKey());
        update.setLastMetricTime(DateUtil.getTimeAndToString());
        update.setLastMetricMsg(StrUtil.maxLength(result.getMessage(), 500));
        if (result.isSuccess()) {
            update.setCpuUsage(result.getCpuUsage());
            update.setMemUsage(result.getMemUsage());
            update.setDiskUsage(result.getDiskUsage());
            update.setLoadAvg(result.getLoadAvg());
        }
        update.setLastUpdateId(resolveOperatorId());
        update.setLastUpdateTime(DateUtil.getTimeAndToString());
        updateById(update);

        try {
            autoServerMetricHistoryService.saveCollectHistory(server, result, update.getLastMetricTime());
        } catch (Exception e) {
            log.warn("写入服务器[{}]采集历史失败: {}", server.getId(), e.getMessage());
        }

        server.setMetricStatus(update.getMetricStatus());
        server.setLastMetricTime(update.getLastMetricTime());
        server.setLastMetricMsg(update.getLastMetricMsg());
        if (result.isSuccess()) {
            server.setCpuUsage(result.getCpuUsage());
            server.setMemUsage(result.getMemUsage());
            server.setDiskUsage(result.getDiskUsage());
            server.setLoadAvg(result.getLoadAvg());
        }
        this.refreshCache(server.getId());
    }

    private String resolveOperatorId() {
        try {
            Map<String, Object> logParams = InputObject.getLogParamsStatic();
            if (logParams != null && logParams.get("id") != null) {
                return String.valueOf(logParams.get("id"));
            }
        } catch (Exception ignored) {
            // 定时任务无登录上下文
        }
        return "system";
    }

    private void encryptSshPasswordIfPresent(AutoServer entity) {
        if (StrUtil.isBlank(entity.getSshPassword())) {
            return;
        }
        entity.setSshPassword(AutoServerSshCrypto.encrypt(entity.getSshPassword()));
    }

    private boolean isSshEnabled(AutoServer server) {
        return server != null && server.getSshEnabled() != null && server.getSshEnabled() == 1;
    }

    private void fillSshDefaults(AutoServer entity) {
        if (entity.getSshEnabled() == null) {
            entity.setSshEnabled(0);
        }
        if (entity.getSshPort() == null || entity.getSshPort() <= 0) {
            entity.setSshPort(22);
        }
        if (entity.getSshTimeout() == null || entity.getSshTimeout() <= 0) {
            entity.setSshTimeout(DEFAULT_SSH_TIMEOUT);
        }
    }

    private void validateSshConfig(AutoServer entity, boolean creating) {
        if (!isSshEnabled(entity)) {
            return;
        }
        if (StrUtil.isBlank(entity.getSshUser())) {
            throw new CustomException("启用 SSH 采集时请填写登录用户名。");
        }
        if (creating && StrUtil.isBlank(entity.getSshPassword())) {
            throw new CustomException("启用 SSH 采集时请填写登录密码。");
        }
        if (!creating && StrUtil.isBlank(entity.getSshPassword())) {
            AutoServer old = selectById(entity.getId());
            if (old == null || StrUtil.isBlank(old.getSshPassword())) {
                throw new CustomException("启用 SSH 采集时请填写登录密码。");
            }
        }
    }

    private ProbeResult doProbe(AutoServer server) {
        String ip = StrUtil.trim(server.getIp());
        if (StrUtil.isBlank(ip)) {
            return ProbeResult.fail("IP为空", null);
        }
        String probeType = StrUtil.blankToDefault(server.getProbeType(), AutoServerProbeTypeEnum.PING.getKey());
        int timeout = server.getProbeTimeout() == null || server.getProbeTimeout() <= 0 ? DEFAULT_TIMEOUT : server.getProbeTimeout();
        int port = resolveProbePort(server);
        if (StrUtil.equals(probeType, AutoServerProbeTypeEnum.HTTP.getKey())
            || StrUtil.equals(probeType, AutoServerProbeTypeEnum.HTTPS.getKey())) {
            return probeHttp(probeType, ip, port, StrUtil.blankToDefault(server.getProbePath(), "/"), timeout);
        }
        return probeTcp(ip, port, timeout);
    }

    private ProbeResult probeTcp(String ip, int port, int timeout) {
        long start = System.currentTimeMillis();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), timeout);
            int latency = (int) (System.currentTimeMillis() - start);
            return ProbeResult.ok("TCP " + ip + ":" + port + " 连通", latency);
        } catch (Exception e) {
            int latency = (int) (System.currentTimeMillis() - start);
            return ProbeResult.fail("TCP " + ip + ":" + port + " 失败：" + StrUtil.blankToDefault(e.getMessage(), e.getClass().getSimpleName()), latency);
        }
    }

    private ProbeResult probeHttp(String scheme, String ip, int port, String path, int timeout) {
        String normalizedPath = StrUtil.startWith(path, "/") ? path : "/" + path;
        String url = scheme + "://" + ip + ":" + port + normalizedPath;
        long start = System.currentTimeMillis();
        try {
            HttpResponse response = HttpRequest.get(url)
                .timeout(timeout)
                .setFollowRedirects(true)
                .execute();
            int latency = (int) (System.currentTimeMillis() - start);
            int status = response.getStatus();
            if (status >= 200 && status < 500) {
                // 2xx/3xx/4xx 都说明服务端口可达；5xx 仍视为可达但提示异常
                if (status >= 500) {
                    return ProbeResult.ok("HTTP " + status + "（服务可达但返回异常）", latency);
                }
                return ProbeResult.ok("HTTP " + status, latency);
            }
            return ProbeResult.fail("HTTP 状态异常：" + status, latency);
        } catch (Exception e) {
            int latency = (int) (System.currentTimeMillis() - start);
            return ProbeResult.fail("HTTP 失败：" + StrUtil.blankToDefault(e.getMessage(), e.getClass().getSimpleName()), latency);
        }
    }

    private int resolveProbePort(AutoServer server) {
        if (server.getProbePort() != null && server.getProbePort() > 0) {
            return server.getProbePort();
        }
        String probeType = StrUtil.blankToDefault(server.getProbeType(), AutoServerProbeTypeEnum.PING.getKey());
        if (StrUtil.equals(probeType, AutoServerProbeTypeEnum.HTTPS.getKey())) {
            return 443;
        }
        if (StrUtil.equals(probeType, AutoServerProbeTypeEnum.HTTP.getKey())) {
            return 80;
        }
        return 22;
    }

    private void fillProbeDefaults(AutoServer entity) {
        if (StrUtil.isBlank(entity.getProbeType())) {
            entity.setProbeType(AutoServerProbeTypeEnum.PING.getKey());
        }
        if (entity.getProbeTimeout() == null || entity.getProbeTimeout() <= 0) {
            entity.setProbeTimeout(DEFAULT_TIMEOUT);
        }
        if (StrUtil.isBlank(entity.getProbePath())) {
            entity.setProbePath("/");
        }
    }

    private String onlineStatusName(String status) {
        for (AutoServerOnlineStatusEnum item : AutoServerOnlineStatusEnum.values()) {
            if (StrUtil.equals(item.getKey(), status)) {
                return item.getValue();
            }
        }
        return AutoServerOnlineStatusEnum.UNKNOWN.getValue();
    }

    private String metricStatusName(String status) {
        for (AutoServerMetricStatusEnum item : AutoServerMetricStatusEnum.values()) {
            if (StrUtil.equals(item.getKey(), status)) {
                return item.getValue();
            }
        }
        return AutoServerMetricStatusEnum.UNKNOWN.getValue();
    }

    private static class ProbeResult {
        private final boolean online;
        private final Integer latencyMs;
        private final String message;

        private ProbeResult(boolean online, Integer latencyMs, String message) {
            this.online = online;
            this.latencyMs = latencyMs;
            this.message = message;
        }

        private static ProbeResult ok(String message, Integer latencyMs) {
            return new ProbeResult(true, latencyMs, message);
        }

        private static ProbeResult fail(String message, Integer latencyMs) {
            return new ProbeResult(false, latencyMs, message);
        }
    }

}
