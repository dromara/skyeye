package com.skyeye.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class AreaUtil {
	
	/** 获取地区api */
	private static final String URL_JD_AREA = "https://d.jd.com/area/get?fid=%d";
	
	/** 初始化省份数据 */
	private static final String[] TABLE_PROVINCE = new String[] { "1", "北京", "2", "上海", "3", "天津", "4", "重庆", "5", "河北",
			"6", "山西", "7", "河南", "8", "辽宁", "9", "吉林", "10", "黑龙江", "11", "内蒙古", "12", "江苏", "13", "山东", "14", "安徽",
			"15", "浙江", "16", "福建", "17", "湖北", "18", "湖南", "19", "广东", "20", "广西", "21", "江西", "22", "四川", "23", "海南",
			"24", "贵州", "25", "云南", "26", "西藏", "27", "陕西", "28", "甘肃", "29", "青海", "30", "宁夏", "31", "新疆", "32", "台湾",
			"42", "香港", "43", "澳门", "84", "钓鱼岛" };

	/**
	 * 初始化省份数据
	 * 
	 * @param conn
	 */
	public static void initArea() {
		try {
			Connection conn = getConn("127.0.0.1", "3306", "eve", "root", "123456");
			for (int nIndex = 0; nIndex < TABLE_PROVINCE.length; nIndex = nIndex + 2) {
				int id = Integer.parseInt(TABLE_PROVINCE[nIndex]);
				String name = TABLE_PROVINCE[nIndex + 1];
				try {
					Statement stat = conn.createStatement();
					String sql = "INSERT INTO t_area VALUES ('" + ToolUtil.getSurFaceId() + "'," + id + ", '" + name + "', 0, 0)";
					stat.execute(sql);
					stat.close();
					System.out.println("查询：" + name + "--级别：0");
					initChildArea(conn, id, 1);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取各省下级地区
	 * 
	 * @param conn 数据库连接对象
	 * @param parentId 所属地区ID
	 * @param level 地区层级，省级：0，市级：1，...
	 */
	public static void initChildArea(Connection conn, int parentId, int level) {
		String url = String.format(URL_JD_AREA, parentId);
		String text = HttpClient.doGet(url);
		if (!StringUtils.isEmpty(text)) {
			JSONArray array = JSON.parseArray(text);
			if (array != null && array.size() > 0) {
				for (int nIndex = 0; nIndex < array.size(); nIndex++) {
					JSONObject object = array.getJSONObject(nIndex);
					int id = object.getInteger("id");
					String name = object.getString("name");
					try {
						Statement stat = conn.createStatement();
						String sql = "INSERT INTO t_area VALUES ('" + ToolUtil.getSurFaceId() + "'," + id + ", '" + name + "'," + parentId + ", " + level + ")";
						stat.execute(sql);
						stat.close();
						System.out.println("查询：" + name + "--级别：" + level);
						initChildArea(conn, id, level + 1);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	/**
	 * 链接数据库
	 * 
	 * @param dbHost 数据库主机地址
	 * @param dbPort 数据库端口
	 * @param dbName 数据库名称
	 * @param dbUser 数据库用户名称
	 * @param dbPassword 数据库登录密码
	 * @return
	 * @throws Exception
	 */
	public static Connection getConn(String dbHost, String dbPort, String dbName, String dbUser, String dbPassword) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		String connStr = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?user=" + dbUser + "&password=" + dbPassword + "&characterEncoding=utf8";
		Connection conn = DriverManager.getConnection(connStr);
		return conn;
	}

	public static void main(String[] args) {
		initArea();
	}

}
