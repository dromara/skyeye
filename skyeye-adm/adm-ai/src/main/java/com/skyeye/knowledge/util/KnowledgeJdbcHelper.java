/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.util;

import cn.hutool.core.collection.CollectionUtil;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 知识库 JDBC 工具：只允许安全的表/字段名，禁止拼接任意 SQL。
 */
public final class KnowledgeJdbcHelper {

    public static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";

    /** 单批拉取行数，避免大表一次加载导致 OOM */
    public static final int BATCH_SIZE = 500;

    private static final Pattern IDENTIFIER = Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*$");

    private static final int LOGIN_TIMEOUT_SECONDS = 8;

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
            fillTableRemarks(conn, catalog, result);
            return result;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("读取表列表失败: " + e.getMessage());
        }
    }

    /**
     * 同步前校验：表是否存在，以及配置字段是否都在表中。
     */
    public static void validateTableAndColumns(String driverClass, String jdbcUrl, String user, String password,
                                               String tableName, Collection<String> columns) {
        checkIdentifier(tableName, "表名");
        if (CollectionUtil.isEmpty(columns)) {
            throw new CustomException("同步字段不能为空");
        }
        for (String column : columns) {
            checkIdentifier(column, "字段名");
        }
        try (Connection conn = open(driverClass, jdbcUrl, user, password)) {
            if (!tableExists(conn, tableName)) {
                throw new CustomException("源库不存在表: " + tableName);
            }
            Set<String> existColumns = loadColumnNames(conn, tableName);
            List<String> missing = new ArrayList<>();
            for (String column : columns) {
                if (!existColumns.contains(column) && !containsIgnoreCase(existColumns, column)) {
                    missing.add(column);
                }
            }
            if (!missing.isEmpty()) {
                throw new CustomException("表 " + tableName + " 不存在字段: " + String.join(", ", missing));
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("校验表结构失败: " + e.getMessage());
        }
    }

    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        String catalog = conn.getCatalog();
        try (ResultSet rs = metaData.getTables(catalog, null, tableName, new String[]{"TABLE"})) {
            if (rs.next()) {
                return true;
            }
        }
        // 部分驱动对大小写敏感，再按小写/大写试一次
        try (ResultSet rs = metaData.getTables(catalog, null, tableName.toLowerCase(), new String[]{"TABLE"})) {
            if (rs.next()) {
                return true;
            }
        }
        try (ResultSet rs = metaData.getTables(catalog, null, tableName.toUpperCase(), new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private static Set<String> loadColumnNames(Connection conn, String tableName) throws SQLException {
        Set<String> names = new HashSet<>();
        DatabaseMetaData metaData = conn.getMetaData();
        String catalog = conn.getCatalog();
        try (ResultSet rs = metaData.getColumns(catalog, null, tableName, "%")) {
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                if (StrUtil.isNotBlank(columnName)) {
                    names.add(columnName);
                }
            }
        }
        if (names.isEmpty()) {
            try (ResultSet rs = metaData.getColumns(catalog, null, tableName.toLowerCase(), "%")) {
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    if (StrUtil.isNotBlank(columnName)) {
                        names.add(columnName);
                    }
                }
            }
        }
        if (names.isEmpty()) {
            try (ResultSet rs = metaData.getColumns(catalog, null, tableName.toUpperCase(), "%")) {
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    if (StrUtil.isNotBlank(columnName)) {
                        names.add(columnName);
                    }
                }
            }
        }
        return names;
    }

    private static boolean containsIgnoreCase(Set<String> names, String target) {
        for (String name : names) {
            if (name != null && name.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从 information_schema 补充表注释（无注释则跳过）
     */
    private static void fillTableRemarks(Connection conn, String catalog, List<Map<String, Object>> tables) {
        if (tables == null || tables.isEmpty()) {
            return;
        }
        boolean needFill = false;
        for (Map<String, Object> table : tables) {
            if (StrUtil.isBlank(str(table.get("remark")))) {
                needFill = true;
                break;
            }
        }
        if (!needFill) {
            return;
        }
        String schema = StrUtil.blankToDefault(catalog, null);
        if (StrUtil.isBlank(schema)) {
            try {
                schema = conn.getCatalog();
            } catch (SQLException ignored) {
                return;
            }
        }
        if (StrUtil.isBlank(schema)) {
            return;
        }
        String sql = "SELECT TABLE_NAME, TABLE_COMMENT FROM information_schema.TABLES "
            + "WHERE TABLE_SCHEMA = ? AND TABLE_TYPE = 'BASE TABLE'";
        Map<String, String> commentMap = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, schema);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    String comment = rs.getString("TABLE_COMMENT");
                    if (StrUtil.isNotBlank(tableName) && StrUtil.isNotBlank(comment)) {
                        commentMap.put(tableName, comment);
                    }
                }
            }
        } catch (Exception ignored) {
            return;
        }
        for (Map<String, Object> table : tables) {
            if (StrUtil.isNotBlank(str(table.get("remark")))) {
                continue;
            }
            String remark = commentMap.get(str(table.get("id")));
            if (StrUtil.isNotBlank(remark)) {
                table.put("remark", remark);
            }
        }
    }

    private static String str(Object value) {
        return value == null ? StrUtil.EMPTY : String.valueOf(value);
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
            fillColumnRemarks(conn, catalog, tableName, result);
            return result;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("读取字段列表失败: " + e.getMessage());
        }
    }

    /**
     * 读取表注释（优先 information_schema）。
     */
    public static String loadTableComment(String driverClass, String jdbcUrl, String user, String password,
                                          String tableName) {
        checkIdentifier(tableName, "表名");
        try (Connection conn = open(driverClass, jdbcUrl, user, password)) {
            return loadTableComment(conn, tableName);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("读取表注释失败: " + e.getMessage());
        }
    }

    /**
     * 读取字段名 -> 注释，供同步正文展示字段含义。
     */
    public static Map<String, String> loadColumnCommentMap(String driverClass, String jdbcUrl, String user,
                                                           String password, String tableName) {
        checkIdentifier(tableName, "表名");
        Map<String, String> result = new LinkedHashMap<>();
        try (Connection conn = open(driverClass, jdbcUrl, user, password)) {
            DatabaseMetaData metaData = conn.getMetaData();
            String catalog = conn.getCatalog();
            try (ResultSet rs = metaData.getColumns(catalog, null, tableName, "%")) {
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    String remark = rs.getString("REMARKS");
                    if (StrUtil.isNotBlank(columnName) && StrUtil.isNotBlank(remark)) {
                        result.put(columnName, remark.trim());
                    }
                }
            }
            fillColumnCommentMap(conn, catalog, tableName, result);
            return result;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("读取字段注释失败: " + e.getMessage());
        }
    }

    private static String loadTableComment(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        String catalog = conn.getCatalog();
        try (ResultSet rs = metaData.getTables(catalog, null, tableName, new String[]{"TABLE"})) {
            if (rs.next()) {
                String remark = rs.getString("REMARKS");
                if (StrUtil.isNotBlank(remark)) {
                    return remark.trim();
                }
            }
        }
        String schema = StrUtil.blankToDefault(catalog, conn.getCatalog());
        if (StrUtil.isBlank(schema)) {
            return StrUtil.EMPTY;
        }
        String sql = "SELECT TABLE_COMMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, schema);
            ps.setString(2, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return StrUtil.blankToDefault(rs.getString("TABLE_COMMENT"), StrUtil.EMPTY).trim();
                }
            }
        }
        return StrUtil.EMPTY;
    }

    private static void fillColumnRemarks(Connection conn, String catalog, String tableName,
                                            List<Map<String, Object>> columns) {
        if (columns == null || columns.isEmpty()) {
            return;
        }
        Map<String, String> commentMap = new LinkedHashMap<>();
        for (Map<String, Object> column : columns) {
            String remark = str(column.get("remark"));
            if (StrUtil.isNotBlank(remark)) {
                commentMap.put(str(column.get("id")), remark);
            }
        }
        fillColumnCommentMap(conn, catalog, tableName, commentMap);
        for (Map<String, Object> column : columns) {
            if (StrUtil.isNotBlank(str(column.get("remark")))) {
                continue;
            }
            String remark = commentMap.get(str(column.get("id")));
            if (StrUtil.isNotBlank(remark)) {
                column.put("remark", remark);
            }
        }
    }

    private static void fillColumnCommentMap(Connection conn, String catalog, String tableName,
                                               Map<String, String> commentMap) {
        if (commentMap == null) {
            return;
        }
        String schema = StrUtil.blankToDefault(catalog, null);
        if (StrUtil.isBlank(schema)) {
            try {
                schema = conn.getCatalog();
            } catch (SQLException ignored) {
                return;
            }
        }
        if (StrUtil.isBlank(schema)) {
            return;
        }
        String sql = "SELECT COLUMN_NAME, COLUMN_COMMENT FROM information_schema.COLUMNS "
            + "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, schema);
            ps.setString(2, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    String comment = rs.getString("COLUMN_COMMENT");
                    if (StrUtil.isNotBlank(columnName) && StrUtil.isNotBlank(comment)) {
                        commentMap.putIfAbsent(columnName, comment.trim());
                    }
                }
            }
        } catch (Exception ignored) {
            // 非 MySQL 或权限不足时跳过
        }
    }

    /**
     * 按主键/水位游标分批查询，避免大表一次全量加载。
     *
     * @param idField         主键字段（用于 keyset 翻页）
     * @param lastId          上一批最后一条主键，空表示从起点
     * @param watermarkField  增量水位字段，空表示全量按主键翻页
     * @param lastWatermark   上一批水位游标
     * @param limit           本批条数
     */
    public static List<Map<String, Object>> queryRowsBatch(String driverClass, String jdbcUrl, String user, String password,
                                                           String tableName, List<String> columns, String tenantField,
                                                           String tenantId, String idField, String lastId,
                                                           String watermarkField, String lastWatermark, int limit) {
        checkIdentifier(tableName, "表名");
        checkIdentifier(idField, "主键字段");
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
        int pageSize = limit <= 0 ? BATCH_SIZE : limit;

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

        boolean useWatermark = StrUtil.isNotBlank(watermarkField);
        if (useWatermark) {
            if (StrUtil.isNotBlank(lastWatermark)) {
                // (watermark > lastWm) OR (watermark = lastWm AND id > lastId)
                where.add("(`".concat(watermarkField).concat("` > ? OR (`")
                    .concat(watermarkField).concat("` = ? AND `")
                    .concat(idField).concat("` > ?))"));
                params.add(lastWatermark);
                params.add(lastWatermark);
                params.add(StrUtil.blankToDefault(lastId, StrUtil.EMPTY));
            } else if (StrUtil.isNotBlank(lastId)) {
                where.add("`".concat(idField).concat("` > ?"));
                params.add(lastId);
            }
            sql.append(where.isEmpty() ? "" : " WHERE " + String.join(" AND ", where));
            sql.append(" ORDER BY `").append(watermarkField).append("` ASC, `").append(idField).append("` ASC");
        } else {
            if (StrUtil.isNotBlank(lastId)) {
                where.add("`".concat(idField).concat("` > ?"));
                params.add(lastId);
            }
            if (!where.isEmpty()) {
                sql.append(" WHERE ").append(String.join(" AND ", where));
            }
            sql.append(" ORDER BY `").append(idField).append("` ASC");
        }
        sql.append(" LIMIT ?");
        params.add(pageSize);

        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = open(driverClass, jdbcUrl, user, password);
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
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
