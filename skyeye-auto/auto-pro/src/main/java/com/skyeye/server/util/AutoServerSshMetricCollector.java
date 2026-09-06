/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通过 SSH 在 Linux 上只读采集 CPU / 内存 / 磁盘 / 负载。
 * 仅执行固定只读脚本，不支持自定义命令。
 * <p>
 * 注意：采集是从「运行 skyeye-auto 的那台机器」发起的，不是从你本机浏览器/IDEA 发起的。
 */
public final class AutoServerSshMetricCollector {

    private static final Pattern KV = Pattern.compile("(?m)^(CPU|MEM|DISK|LOAD)=(.*)$");

    private static final String METRIC_SCRIPT =
        "export LANG=C LC_ALL=C; "
            + "read -r _ u1 n1 s1 i1 w1 x1 y1 z1 _ < /proc/stat; "
            + "sleep 0.4; "
            + "read -r _ u2 n2 s2 i2 w2 x2 y2 z2 _ < /proc/stat; "
            + "tu1=$((u1+n1+s1+i1+w1+x1+y1+z1)); tu2=$((u2+n2+s2+i2+w2+x2+y2+z2)); "
            + "du=$((tu2-tu1)); di=$((i2-i1)); "
            + "if [ \"$du\" -gt 0 ]; then CPU=$(awk -v d=\"$du\" -v i=\"$di\" 'BEGIN{printf \"%.2f\", (1-i/d)*100}'); "
            + "else CPU=0; fi; "
            + "MEM=$(awk '/MemTotal:/{t=$2} /MemAvailable:/{a=$2} END{if(t>0) printf \"%.2f\", (t-a)/t*100; else print 0}' /proc/meminfo); "
            + "DISK=$(df -P / 2>/dev/null | awk 'NR==2{gsub(/%/,\"\",$5); print $5+0}'); "
            + "LOAD=$(awk '{print $1\"/\"$2\"/\"$3}' /proc/loadavg); "
            + "echo CPU=$CPU; echo MEM=$MEM; echo DISK=$DISK; echo LOAD=$LOAD";

    private AutoServerSshMetricCollector() {
    }

    public static MetricResult collect(String host, int port, String user, String password, int timeoutMs) {
        if (StrUtil.hasBlank(host, user, password)) {
            return MetricResult.fail("SSH账号或密码未配置");
        }
        int sshPort = port <= 0 ? 22 : port;
        int timeout = timeoutMs <= 0 ? 15000 : Math.max(timeoutMs, 3000);
        String fromHost = localHostHint();

        // 1) TCP
        String tcpError = probeTcp(host, sshPort, timeout);
        if (tcpError != null) {
            return MetricResult.fail("TCP连不上 " + host + ":" + sshPort
                + "（从 auto 机器 " + fromHost + "）。原因：" + tcpError);
        }

        // 2) 读 SSH banner：能区分「半通防火墙」与「纯客户端算法问题」
        String bannerError = probeSshBanner(host, sshPort, timeout);
        if (bannerError != null) {
            return MetricResult.fail("TCP已通但读不到 SSH Banner（从 auto 机器 " + fromHost + "）。"
                + "多为中间设备只放行握手、丢弃 SSH 数据，或目标不是真正 sshd。"
                + "详情：" + bannerError);
        }

        long start = System.currentTimeMillis();
        String stage = "connect";
        try (SSHClient ssh = new SSHClient()) {
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.setConnectTimeout(timeout);
            ssh.setTimeout(timeout);
            ssh.connect(host, sshPort);

            stage = "auth";
            ssh.authPassword(user, password);

            stage = "exec";
            String output;
            try (Session session = ssh.startSession()) {
                Command cmd = session.exec(METRIC_SCRIPT);
                output = IOUtils.readFully(cmd.getInputStream()).toString("UTF-8");
                cmd.join(timeout, TimeUnit.MILLISECONDS);
                Integer exit = cmd.getExitStatus();
                if (exit != null && exit != 0 && StrUtil.isBlank(output)) {
                    String err = IOUtils.readFully(cmd.getErrorStream()).toString("UTF-8");
                    return MetricResult.fail("SSH命令执行失败，exit=" + exit + "："
                        + StrUtil.maxLength(StrUtil.cleanBlank(err), 160));
                }
            }

            Map<String, String> map = parseKv(output);
            if (map.isEmpty()) {
                return MetricResult.fail("SSH已连通，但未解析到指标输出："
                    + StrUtil.maxLength(StrUtil.cleanBlank(output), 120));
            }
            BigDecimal cpu = toDecimal(map.get("CPU"));
            BigDecimal mem = toDecimal(map.get("MEM"));
            BigDecimal disk = toDecimal(map.get("DISK"));
            String load = StrUtil.trim(map.get("LOAD"));
            if (cpu == null && mem == null && disk == null && StrUtil.isBlank(load)) {
                return MetricResult.fail("指标为空");
            }
            int cost = (int) (System.currentTimeMillis() - start);
            return MetricResult.ok(cpu, mem, disk, load, "SSH采集成功，耗时 " + cost + "ms");
        } catch (Exception e) {
            String msg = StrUtil.blankToDefault(e.getMessage(), e.getClass().getSimpleName());
            if (StrUtil.containsIgnoreCase(msg, "Auth fail")
                || StrUtil.containsIgnoreCase(msg, "authentication")
                || StrUtil.containsIgnoreCase(msg, "Permission denied")) {
                return MetricResult.fail("SSH认证失败（阶段=" + stage + "）：用户名/密码不正确。详情：" + msg);
            }
            if (isTimeout(e, msg)) {
                return MetricResult.fail("SSH超时（阶段=" + stage + "，TCP/Banner已通，auto=" + fromHost
                    + "）。当前超时 " + timeout + "ms。原始错误："
                    + e.getClass().getSimpleName() + ": " + StrUtil.maxLength(msg, 180));
            }
            return MetricResult.fail("SSH采集失败（阶段=" + stage + "）："
                + e.getClass().getSimpleName() + ": " + StrUtil.maxLength(msg, 200));
        }
    }

    private static boolean isTimeout(Exception e, String msg) {
        if (e instanceof java.net.SocketTimeoutException) {
            return true;
        }
        if (StrUtil.containsIgnoreCase(msg, "timed out") || StrUtil.containsIgnoreCase(msg, "timeout")) {
            return true;
        }
        Throwable cause = e.getCause();
        return cause instanceof java.net.SocketTimeoutException;
    }

    private static String probeTcp(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return null;
        } catch (Exception e) {
            return StrUtil.blankToDefault(e.getMessage(), e.getClass().getSimpleName());
        }
    }

    /**
     * 主动读一行 SSH 协议标识（如 SSH-2.0-OpenSSH_8.x）。
     * TCP 通但这里超时 → 网络半通或目标非 sshd。
     */
    private static String probeSshBanner(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.setSoTimeout(Math.min(timeout, 8000));
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                String line = reader.readLine();
                if (StrUtil.isBlank(line)) {
                    return "空 Banner";
                }
                if (!StrUtil.startWithIgnoreCase(line, "SSH-")) {
                    return "非 SSH Banner: " + StrUtil.maxLength(line, 80);
                }
                return null;
            }
        } catch (Exception e) {
            return StrUtil.blankToDefault(e.getMessage(), e.getClass().getSimpleName());
        }
    }

    private static String localHostHint() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private static Map<String, String> parseKv(String output) {
        Map<String, String> map = new HashMap<>();
        if (StrUtil.isBlank(output)) {
            return map;
        }
        Matcher matcher = KV.matcher(output);
        while (matcher.find()) {
            map.put(matcher.group(1), StrUtil.trim(matcher.group(2)));
        }
        return map;
    }

    private static BigDecimal toDecimal(String raw) {
        if (StrUtil.isBlank(raw) || !NumberUtil.isNumber(raw)) {
            return null;
        }
        BigDecimal value = new BigDecimal(raw).setScale(2, RoundingMode.HALF_UP);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        if (value.compareTo(new BigDecimal("100")) > 0) {
            return new BigDecimal("100.00");
        }
        return value;
    }

    public static class MetricResult {
        private final boolean success;
        private final BigDecimal cpuUsage;
        private final BigDecimal memUsage;
        private final BigDecimal diskUsage;
        private final String loadAvg;
        private final String message;

        private MetricResult(boolean success, BigDecimal cpuUsage, BigDecimal memUsage,
                             BigDecimal diskUsage, String loadAvg, String message) {
            this.success = success;
            this.cpuUsage = cpuUsage;
            this.memUsage = memUsage;
            this.diskUsage = diskUsage;
            this.loadAvg = loadAvg;
            this.message = message;
        }

        public static MetricResult ok(BigDecimal cpu, BigDecimal mem, BigDecimal disk, String load, String message) {
            return new MetricResult(true, cpu, mem, disk, load, message);
        }

        public static MetricResult fail(String message) {
            return new MetricResult(false, null, null, null, null, message);
        }

        public boolean isSuccess() {
            return success;
        }

        public BigDecimal getCpuUsage() {
            return cpuUsage;
        }

        public BigDecimal getMemUsage() {
            return memUsage;
        }

        public BigDecimal getDiskUsage() {
            return diskUsage;
        }

        public String getLoadAvg() {
            return loadAvg;
        }

        public String getMessage() {
            return message;
        }
    }
}
