package com.zy.utils.model;

import java.util.List;

/**
 * Excel Sheet模型，模型包含两个属性<br>
 * name: sheet名称<br>
 * data: sheet数据
 * 
 * @author stonecold.zhang
 * 
 */
public class ExcelSheet {
	private String name; // sheet名称
	private List<List<String>> data; // sheet数据

	public ExcelSheet() {
	}

	/**
	 * 
	 * @param name
	 *            sheet名称
	 * @param data
	 *            sheet数据
	 */
	public ExcelSheet(String name, List<List<String>> data) {
		super();
		this.name = name;
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<List<String>> getData() {
		return data;
	}

	public void setData(List<List<String>> data) {
		this.data = data;
	}

}
