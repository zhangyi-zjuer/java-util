package com.zy.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.zy.utils.model.TypeSampleModel;

/**
 * 数据库表字段抽样
 * 
 * @author 张翼
 * @email zhangyi.zjuer@gmail.com
 * @date 2013年12月12日
 */
public class SamplingUtil {
	private static final Logger logger = Logger.getLogger(SamplingUtil.class);

	private Set<String> sampledItems = new HashSet<String>(); // 已抽取样本
	private boolean isNonRepeatedSampling; // 非重复抽样
	private final Random r = new Random(); // 随机数生成器
	private static final DecimalFormat df = new DecimalFormat("#0.000"); // 数据格式化，保留小数点后三位

	private String dbDriver; // 数据库驱动
	private String dbUrl; // 数据库url

	/**
	 * 
	 * @param isNonRepeatedSamping
	 *            是否非重复抽样
	 * @param dbDriver
	 *            数据库驱动
	 * @param dbUrl
	 *            数据库url
	 * @throws ClassNotFoundException
	 */
	public SamplingUtil(boolean isNonRepeatedSamping, String dbDriver,
			String dbUrl) throws ClassNotFoundException {
		this.isNonRepeatedSampling = isNonRepeatedSamping;
		this.dbDriver = dbDriver;
		this.dbUrl = dbUrl;

		Class.forName(this.dbDriver);
	}

	/**
	 * 通过配置文件初始化<br>
	 * 配置文件包含参数<br>
	 * sampling.nonrepeat: 非重复抽样<br>
	 * db.driver: 数据库驱动<br>
	 * db.url: 数据库URL
	 * 
	 * @param configure
	 * @throws ClassNotFoundException
	 */
	public SamplingUtil(PropertiesConfiguration configure)
			throws ClassNotFoundException {

		this(configure.getBoolean("sampling.nonrepeat"), configure
				.getString("db.driver"), configure.getString("db.url"));
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return
	 * @throws SQLException
	 */
	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(dbUrl);
	}

	/**
	 * 获取样本总量
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	protected String[] getItemsBySql(String sql) throws SQLException {
		List<String> itemList = new ArrayList<String>();
		Connection conn = getConnection();
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery(sql);

		// System.out.println(sql);

		while (rs.next()) {
			itemList.add(rs.getString(1));
		}

		String[] items = new String[itemList.size()];

		for (int i = 0; i < itemList.size(); i++) {
			items[i] = itemList.get(i);
		}

		stat.close();
		conn.close();

		return items;
	}

	/**
	 * 获取随机抽样数据
	 * 
	 * @param items
	 * @param n
	 * @return
	 */
	public List<String> getRandom(String[] items, int n) {
		int chosedNum = 0;
		int total = items.length;

		List<String> samples = new ArrayList<String>();

		while (chosedNum < n) {
			int index = r.nextInt(total);
			String item = items[index];
			if (isNonRepeatedSampling) { // 非重复抽样
				if (!sampledItems.contains(item)) {
					samples.add(item);
					sampledItems.add(item);
					chosedNum++;
				}
			} else { // 可重复抽样
				samples.add(item);
				chosedNum++;
			}
		}

		return samples;
	}

	/**
	 * 获取sql语句返回的数量
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	private int getTotalBySql(String sql) throws SQLException {
		int total = 0;
		Connection conn = getConnection();
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery(sql);

		if (rs.next()) {
			total = rs.getInt(1);
		}

		rs.close();
		conn.close();

		return total;

	}

	/**
	 * 获取数据库字段总数量
	 * 
	 * @param table
	 *            数据表
	 * @param limit
	 *            sql语句限制条件，null或空字符串表示无限制
	 * @return
	 * @throws SQLException
	 */
	public int getTotal(String table, String limit) throws SQLException {
		String sql = "SELECT COUNT(*) FROM " + table;

		if (limit != null && limit.trim().length() > 0) {
			sql += " WHERE " + limit;
		}
		sql += ";";

		return getTotalBySql(sql);

	}

	/**
	 * 获取各个抽样类型的数量
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	private Map<String, Integer> getTypeNumBySql(String sql)
			throws SQLException {
		Map<String, Integer> typeNums = new HashMap<String, Integer>();
		Connection conn = getConnection();
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery(sql);

		while (rs.next()) {
			typeNums.put(rs.getString(1), rs.getInt(2));
			logger.info(rs.getString(1) + "\t" + rs.getString(2));
		}

		rs.close();
		conn.close();

		return typeNums;
	}

	/**
	 * 获取各个抽样类型的数量
	 * 
	 * @param table
	 *            抽样表
	 * @param typeColumn
	 *            抽样类型
	 * @param limit
	 *            抽样限制
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Integer> getTypeNum(String table, String typeColumn,
			String limit) throws SQLException {
		String sql = "SELECT " + typeColumn + ", COUNT(*) FROM " + table;
		if (limit != null && limit.trim().length() > 0) {
			sql += " WHERE " + limit;
		}

		sql += " GROUP BY " + typeColumn + ";";

		return getTypeNumBySql(sql);
	}

	/**
	 * 写入文件
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	private void writeToFile(String filePath, String content)
			throws IOException {
		Writer writer = new OutputStreamWriter(new FileOutputStream(filePath),
				"utf-8");
		writer.write(content);
		writer.close();
	}

	/**
	 * 获取类型抽样的具体信息
	 * 
	 * @param typeMap
	 *            类型信息
	 * @param table
	 *            抽样表
	 * @param typeColumn
	 *            类型名
	 * @param selectColumn
	 *            抽样名
	 * @param total
	 *            样本总量
	 * @param sampleNun
	 *            抽样总量
	 * @param minSampleNum
	 *            最小抽样数
	 * @param limit
	 *            抽样限制条件
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private List<TypeSampleModel> getTypeSamples(Map<String, Integer> typeMap,
			String table, String typeColumn, String selectColumn, int total,
			int sampleNum, int minSampleNum, String limit) {
		List<TypeSampleModel> typeSamples = new ArrayList<TypeSampleModel>();

		Iterator iter = typeMap.entrySet().iterator();
		List<TypeSampleModel> otherSampleTypes = new ArrayList<TypeSampleModel>(); // 样本数量小于最小样本数，全部归为其它

		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String type = (String) entry.getKey();
			Integer num = (Integer) entry.getValue();

			double samplePercent = 1.0 * num / total; // 类型总体比例
			int typeSampleNum = (int) (sampleNum * samplePercent); // 以总体比例，计算抽样数量

			if ((sampleNum * samplePercent - typeSampleNum) >= 0.5) { // 四舍五入
				typeSampleNum += 1;
			}

			TypeSampleModel typeSample = new TypeSampleModel();
			typeSample.setSampleNum(typeSampleNum);
			typeSample.setSamplePercent(samplePercent);
			typeSample.setTypeName(type);
			if (typeSampleNum >= minSampleNum) { // 抽样数量不小于最小抽样数

				String sql = "SELECT " + selectColumn + " FROM " + table
						+ " WHERE " + typeColumn + "='" + type + "'";
				if (limit != null && limit.trim().length() > 0) {
					sql += " AND " + limit;
				}

				int maxLimit = sampleNum * 100 < 1000000 ? sampleNum * 100
						: 1000000;
				sql += " order by rand() limit " + maxLimit + ";";

				typeSample.setSql(sql);
				typeSamples.add(typeSample);
			} else { // 抽样数量小于最小抽样数
				otherSampleTypes.add(typeSample);
			}
		}

		/**
		 * 抽样数量小于最小抽样数量时，归为其它类型
		 */
		if (otherSampleTypes.size() > 0) {
			double otherPercent = 0.0;
			List<String> otherTypeNames = new ArrayList<String>();

			for (TypeSampleModel otherSampleType : otherSampleTypes) {
				otherPercent += otherSampleType.getSamplePercent();
				otherTypeNames.add("'" + otherSampleType.getTypeName() + "'");
			}

			TypeSampleModel typeSample = new TypeSampleModel();
			typeSample.setSamplePercent(otherPercent);
			typeSample.setSampleNum((int) (sampleNum * otherPercent));
			typeSample.setTypeName("其它");
			typeSample.setRemarks(StringUtils.join(otherTypeNames, ","));

			String sql = "SELECT " + selectColumn + " FROM " + table
					+ " WHERE " + typeColumn + " NOT IN ("
					+ StringUtils.join(otherTypeNames, ",") + ")";
			if (limit != null && limit.trim().length() > 0) {
				sql += " AND " + limit;
			}

			int maxLimit = sampleNum * 100 < 1000000 ? sampleNum * 100
					: 1000000;
			sql += " order by rand() limit " + maxLimit + ";";

			typeSample.setSql(sql);
			typeSamples.add(typeSample);

		}

		return typeSamples;
	}

	/**
	 * 单表抽样
	 * 
	 * @param total
	 *            数据总量
	 * @param sampleNum
	 *            抽样数量
	 * @param sampleMin
	 *            最小抽样数量
	 * @param selectColumn
	 *            抽样字段
	 * @param typeColumn
	 *            抽样类型, 抽样类型为null时为整体抽样
	 * @param limit
	 *            数据限制，如Power != 1
	 * @param table
	 *            抽样表
	 * @param filePath
	 *            抽样结果存放文件
	 * @throws SQLException
	 * @throws IOException
	 */
	public void singleTableSampling(int total, int sampleNum, int sampleMin,
			String selectColumn, String typeColumn, String limit, String table,
			String filePath) throws SQLException, IOException {

		String title = "样本总量：" + total + "\t抽样数量：" + sampleNum;
		Map<String, Integer> typeMap = null;
		if (typeColumn != null && typeColumn.trim().length() > 0) {
			logger.info("Sampling Type: " + typeColumn);
			typeMap = getTypeNum(table, typeColumn, limit);
		}

		if (typeMap == null) { // 无过滤整体抽样
			String selectSql = "SELECT " + selectColumn + " FROM " + table;
			if (limit != null && limit.trim().length() > 0) {
				selectSql += " where " + limit;
			}

			// 限制结果数量最多为100w，防止内存用完
			int maxLimit = sampleNum * 100 < 1000000 ? sampleNum * 100
					: 1000000;

			selectSql += " order by rand() limit " + maxLimit + ";";

			String[] items = getItemsBySql(selectSql);
			List<String> samples = getRandom(items, sampleNum);
			String content = title + "\n\n" + StringUtils.join(samples, "\n");
			writeToFile(filePath, content);
		} else { // 类型抽样
			List<TypeSampleModel> typeSamples = getTypeSamples(typeMap, table,
					typeColumn, selectColumn, total, sampleNum, sampleMin,
					limit);
			String subTitle = "抽样字段： " + typeColumn + "\n\n";
			subTitle += "类型\t数量\t百分比\n";
			List<String> samples = new ArrayList<String>();
			for (TypeSampleModel sampleModel : typeSamples) {
				String typeSamplingInfo = sampleModel.getTypeName() + "\t"
						+ sampleModel.getSampleNum() + "\t"
						+ df.format(sampleModel.getSamplePercent() * 100)
						+ "%\n";
				subTitle += typeSamplingInfo;
				logger.info(typeSamplingInfo.trim());
				String[] items = getItemsBySql(sampleModel.getSql());
				if (items.length > 0 && sampleModel.getSampleNum() > 0) {
					samples.addAll(getRandom(items, sampleModel.getSampleNum()));
				}
			}

			logger.info(subTitle);

			String content = title + "\n" + subTitle + "\n\n"
					+ StringUtils.join(samples, "\n");

			writeToFile(filePath, content);
		}

	}

	public Set<String> getSampledIDs() {
		return sampledItems;
	}

	public void setSampledIDs(Set<String> sampledIDs) {
		this.sampledItems = sampledIDs;
	}

	public boolean isNonRepeatedSampling() {
		return isNonRepeatedSampling;
	}

	public void setNonRepeatedSampling(boolean isNonRepeatedSampling) {
		this.isNonRepeatedSampling = isNonRepeatedSampling;
	}

}
