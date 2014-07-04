package com.zy.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.zy.utils.excepiton.NullDatabaseUrlException;

/**
 * 数据库工具，使用时先调用init函数进行初始化
 * 
 * @author 张翼
 * @email zhangyi.zjuer@gmail.com
 * @date 2013年12月27日
 */
public class DBUtil {
	private static final Logger logger = Logger.getLogger(DBUtil.class);
	private static String dbUrl;

	/**
	 * 数据库连接初始化
	 * 
	 * @param url
	 * @param driver
	 */
	public static void init(String url, String driver) {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			logger.error("", e);
			System.exit(1);
		}

		if (dbUrl == null) {
			synchronized (DBUtil.class) {
				if (dbUrl == null) {
					dbUrl = url;
				}
			}
		}
	}

	/**
	 * 通过配置文件初始化<br>
	 * 配置文件需包含：<br>
	 * db.url: 数据库URL<br>
	 * db.driver: 数据库驱动
	 * 
	 * @param configure
	 */
	public static void init(PropertiesConfiguration configure) {
		DBUtil.init(configure.getString("db.url"),
				configure.getString("db.driver"));
	}

	/**
	 * 通过配置文件初始化<br>
	 * 配置文件需包含：<br>
	 * db.url: 数据库URL<br>
	 * db.driver: 数据库驱动
	 * 
	 * @param configure
	 */
	public static void init(String configFile) throws ConfigurationException {
		PropertiesConfiguration configure = ConfigUtil.load(configFile);
		init(configure);
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return
	 * @throws SQLException
	 * @throws NullDatabaseUrlException
	 */
	public static Connection getConnection() throws SQLException,
			NullDatabaseUrlException {
		if (dbUrl == null) {
			throw new NullDatabaseUrlException();
		}
		return DriverManager.getConnection(dbUrl);
	}

	/**
	 * 获取数据库连接
	 * 
	 * @param driver
	 * @param url
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws NullDatabaseUrlException
	 */
	public static Connection getConnection(String driver, String url)
			throws ClassNotFoundException, SQLException,
			NullDatabaseUrlException {
		if (url == null) {
			throw new NullDatabaseUrlException();
		}
		Class.forName(driver);
		return DriverManager.getConnection(url);
	}

	/**
	 * 插入一条数据库记录
	 * 
	 * @param tableName
	 *            数据库表
	 * @param params
	 *            插入的字段
	 * @param conn
	 *            数据库连接
	 * @throws SQLException
	 */
	public static void insertToDB(String tableName, Map<String, Object> params,
			Connection conn) throws SQLException {
		String sql = generateInsertSql(tableName, params);
		Statement stat = conn.createStatement();
		stat.executeUpdate(sql);
		stat.close();
	}

	/**
	 * 更新数据库
	 * 
	 * @param tableName
	 *            数据表
	 * @param params
	 *            更新的值
	 * @param limit
	 *            更新类型,如 ID = 1 and Source = 'DianPing'
	 * @param conn
	 *            数据库连接
	 * @throws SQLException
	 */
	public static void updateDB(String tableName, Map<String, Object> params,
			String limit, Connection conn) throws SQLException {
		String sql = generateUpdateSql(tableName, params, limit);
		Statement stat = conn.createStatement();
		stat.executeUpdate(sql);
		stat.close();
	}

	/**
	 * 删除数据库条目
	 * 
	 * @param tableName
	 *            数据表
	 * @param params
	 *            限制条件
	 * @param conn
	 *            数据库连接
	 * @throws SQLException
	 */
	public static void deleteFromDB(String tableName,
			Map<String, Object> params, Connection conn) throws SQLException {
		String sql = generateDeleteSql(tableName, params);
		Statement stat = conn.createStatement();
		stat.executeUpdate(sql);
		stat.close();
	}

	/**
	 * 生成插入的sql语句
	 * 
	 * @param table
	 *            数据库表
	 * @param m
	 *            插入的字段
	 * @return sql语句
	 */
	@SuppressWarnings("rawtypes")
	public static String generateInsertSql(String table, Map<String, Object> m) {
		List<String> keys = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		Iterator iter = m.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			Object val = m.get(key);
			String value = "";

			if (val instanceof Boolean) {
				if ((Boolean) val) {
					value = "1";
				} else {
					value = "0";
				}
			} else if (val == null) {
				value = "NULL";
			} else {
				value = "'" + StringEscapeUtils.escapeSql(val.toString()) + "'";
			}

			keys.add(key);
			values.add(value);
		}
		String sql = String.format("INSERT INTO %s( %s ) VALUES( %s );", table,
				StringUtils.join(keys, ","), StringUtils.join(values, ","));
		return sql;
	}

	/**
	 * 生成update的sql语句
	 * 
	 * @param table
	 *            数据库表
	 * @param m
	 *            更新字段
	 * @param limit
	 *            更新类型,如 ID = 1 and Source = 'DianPing'
	 * @return sql语句
	 */
	private static String generateUpdateSql(String table,
			Map<String, Object> m, String limit) {
		List<String> setValues = generateValues(m);

		String sql = String.format("UPDATE %s SET %s WHERE %s;", table,
				StringUtils.join(setValues, ", ").trim(), limit);

		return sql;
	}

	private static String generateDeleteSql(String table, Map<String, Object> m) {
		List<String> limitValues = generateValues(m);
		String sql = String.format("DELETE FROM %s WHERE %s;", table,
				StringUtils.join(limitValues, " AND "));
		return sql;
	}

	/**
	 * map对象生成key=value的形式
	 * 
	 * @param m
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static List<String> generateValues(Map<String, Object> m) {
		List<String> values = new ArrayList<String>();
		Iterator iter = m.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			Object val = m.get(key);
			String value = "";
			if (val instanceof Boolean) {
				if ((Boolean) val) {
					value = "1";
				} else {
					value = "0";
				}
			} else if (val == null) {
				value = "NULL";
			} else {
				value = "'" + StringEscapeUtils.escapeSql(val.toString()) + "'";
			}
			values.add(key + " = " + value);
		}
		return values;
	}
}
