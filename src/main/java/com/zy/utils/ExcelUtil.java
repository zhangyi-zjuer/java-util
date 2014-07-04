package com.zy.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.zy.utils.model.ExcelSheet;

/**
 * 用于生产Excel文件
 * 
 * @author 张翼
 * @email zhangyi.zjuer@gmail.com
 * @date 2013年12月11日
 */

public class ExcelUtil {

	/**
	 * 生成Excel文件
	 * 
	 * @param filePath
	 *            生成的文件
	 * @param excelSheets
	 *            数据
	 * @throws IOException
	 */
	public static void write(String filePath, List<ExcelSheet> excelSheets)
			throws IOException {
		Workbook book = new HSSFWorkbook();

		int sheetIndex = 0;
		for (ExcelSheet excelSheet : excelSheets) {
			sheetIndex++;
			List<List<String>> data = excelSheet.getData();
			String sheetName = excelSheet.getName();

			if (sheetName == null) {
				sheetName = "Sheet" + sheetIndex;
			}

			Sheet sheet = book.createSheet(sheetName);

			for (int i = 0; i < data.size(); i++) {
				List<String> elements = data.get(i);
				Row row = sheet.createRow(i);
				for (int j = 0; j < elements.size(); j++) {
					String ele = elements.get(j);
					if (ele == null) {
						ele = "";
					}
					row.createCell(j).setCellValue(ele);
				}
			}
		}

		FileOutputStream fileOut = new FileOutputStream(filePath);
		book.write(fileOut);
		fileOut.close();
	}

	/**
	 * 数据导出到excel
	 * 
	 * @param conn
	 *            数据库连接
	 * @param filePath
	 *            输出文件
	 * @param tableName
	 *            表名
	 * @param columns
	 *            列名
	 * @param limit
	 *            限制
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void exportDbDataToExcel(Connection conn, String filePath,
			String tableName, String[] columns, String limit)
			throws SQLException, IOException {
		List<ExcelSheet> excelSheets = new ArrayList<ExcelSheet>();

		List<String> sheetTitle = Arrays.asList(columns);
		List<List<String>> sheetData = new ArrayList<List<String>>();
		sheetData.add(sheetTitle);

		ExcelSheet sheet = new ExcelSheet(tableName, sheetData);

		String sql = "SELECT * FROM " + tableName;

		if (StringUtils.isNotEmpty(limit)) {
			sql += " " + limit;
		}

		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery(sql);

		while (rs.next()) {
			List<String> rowData = new ArrayList<String>();
			for (String column : columns) {
				String ele = rs.getString(column);
				ele = (ele == null) ? "" : ele;
				rowData.add(ele);
			}
			sheetData.add(rowData);
		}

		rs.close();
		stat.close();
		excelSheets.add(sheet);
		write(filePath, excelSheets);
	}

	/**
	 * 数据导出到excel
	 * 
	 * @param conn
	 *            数据库连接
	 * @param filePath
	 *            输出文件
	 * @param tableName
	 *            表名
	 * @param columns
	 *            列名
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void exportDbDataToExcel(Connection conn, String filePath,
			String tableName, String[] columns) throws SQLException,
			IOException {

		exportDbDataToExcel(conn, filePath, tableName, columns, null);
	}

}
