/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.util;

import cn.hutool.core.util.StrUtil;
import com.skyeye.exception.CustomException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 知识库 JDBC 工具：只允许安全的表/字段名，禁止拼接任意 SQL。
 */
public final class KnowledgeJdbcHelper {

    public static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static final Pattern IDENTIFIER = Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*$");

    private static final int LOGIN_TIMEOUT_SECONDS = 8;

    private static final int MAX_ROWS = 10000;

    private KnowledgeJdbcHelper() {
    }

    public static void testConnection(String driverClass, String jdbcUrl, String user, String password) {
        try (Connection conn = open(driverClass, jdbcUrl, user, password)) {
            if (conn == null || !conn.isValid(LOGIN_TIMEOUT_SECONDS)) {
                throw new CustomException("数据库连接失败");
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("数据库连接失败: " + e.getMessage());
        }
    }

    public static List<Map<String, Object>> listTables(String driverClass, String jdbcUrl, String user, String password) {
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = open(driverClass, jdbcUrl, user, password)) {
            DatabaseMetaData metaData = conn.getMetaData();
            String catalog = conn.getCatalog();
            try (ResultSet rs = metaData.getTables(catalog, null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    if (StrUtil.isBlank(tableName) || !IDENTIFIER.matcher(tableName).matches()) {
                        continue;
                    }
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", tableName);
                    item.put("name", tableName);
                    item.put("remark", rs.getString("REMARKS"));
                    result.add(item);
                }
            }
            return result;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("读取表列表失败: " + e.getMessage());
        }
    }

    public static List<Map<String, Object>> listColumns(String driverClass, String jdbcUrl, String user,
                                                        String password, String tableName) {
        checkIdentifier(tableName, "表名");
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = open(driverClass, jdbcUrl, user, password)) {
            DatabaseMetaData metaData = conn.getMetaData();
            String catalog = conn.getCatalog();
            try (ResultSet rs = metaData.getColumns(catalog, null, tableName, "%")) {
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    if (StrUtil.isBlank(columnName) || !IDENTIFIER.matcher(columnName).matches()) {
                        continue;
                    }
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", columnName);
                    item.put("name", columnName);
                    item.put("typeName", rs.getString("TYPE_NAME"));
                    item.put("remark", rs.getString("REMARKS"));
                    result.add(item);
                }
            }
            return result;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("读取字段列表失败: " + e.getMessage());
        }
    }

    public static List<Map<String, Object>> queryRows(String driverClass, String jdbcUrl, String user, String password,
                                                      String tableName, List<String> columns, String tenantField,
                                                      String tenantId, String watermarkField, String lastWatermark) {
        checkIdentifier(tableName, "表名");
        if (columns == null || columns.isEmpty()) {
            throw new CustomException("同步字段不能为空");
        }
        for (String column : columns) {
            checkIdentifier(column, "字段名");
        }
        if (StrUtil.isNotBlank(tenantField)) {
            checkIdentifier(tenantField, "租户字段");
        }
        if (StrUtil.isNotBlank(watermarkField)) {
            checkIdentifier(watermarkField, "水位字段");
        }
        StringBuilder sql = new StringBuilder("SELECT ");
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append('`').append(columns.get(i)).append('`');
        }
        sql.append(" FROM `").append(tableName).append('`');
        List<Object> params = new ArrayList<>();
        List<String> where = new ArrayList<>();
        if (StrUtil.isNotBlank(tenantField) && StrUtil.isNotBlank(tenantId)) {
            where.add("`".concat(tenantField).concat("` = ?"));
            params.add(tenantId);
        }
        if (StrUtil.isNotBlank(watermarkField) && StrUtil.isNotBlank(lastWatermark)) {
            where.add("`".concat(watermarkField).concat("` > ?"));
            params.add(lastWatermark);
        }
        if (!where.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", where));
        }
        if (StrUtil.isNotBlank(watermarkField)) {
            sql.append(" ORDER BY `").append(watermarkField).append("` ASC");
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = open(driverClass, jdbcUrl, user, password);
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setMaxRows(MAX_ROWS);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= colCount; i++) {
                        row.put(meta.getColumnLabel(i), rs.getObject(i));
                    }
                    rows.add(row);
                }
            }
            return rows;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("同步查询失败: " + e.getMessage());
        }
    }

    public static void checkIdentifier(String name, String label) {
        if (StrUtil.isBlank(name) || !IDENTIFIER.matcher(name).matches()) {
            throw new CustomException(label + "不合法: " + name);
        }
    }

    private static Connection open(String driverClass, String jdbcUrl, String user, String password) throws Exception {
        if (StrUtil.isBlank(jdbcUrl)) {
            throw new CustomException("请先填写 JDBC 地址");
        }
        String driver = StrUtil.blankToDefault(driverClass, DEFAULT_DRIVER);
        Class.forName(driver);
        DriverManager.setLoginTimeout(LOGIN_TIMEOUT_SECONDS);
        return DriverManager.getConnection(jdbcUrl, user, password);
    }

    public static void closeQuietly(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException ignored) {
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

}
