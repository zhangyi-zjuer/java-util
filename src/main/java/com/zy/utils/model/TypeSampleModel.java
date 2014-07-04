package com.zy.utils.model;

import java.util.List;

/**
 * 特定类型抽样模型
 * 
 * @author 张翼
 * @email zhangyi.zjuer@gmail.com
 * @date 2013年12月13日
 */
public class TypeSampleModel {
	private String typeName; // 抽样字段名称
	private int sampleNum; // 抽样数量
	private double samplePercent; // 抽样百分比
	private String sql; // 该抽样的sql语句
	private List<String> sampleResult; // 抽样结果
	private String remarks = ""; // 备注

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getSampleNum() {
		return sampleNum;
	}

	public void setSampleNum(int sampleNum) {
		this.sampleNum = sampleNum;
	}

	public double getSamplePercent() {
		return samplePercent;
	}

	public void setSamplePercent(double samplePercent) {
		this.samplePercent = samplePercent;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<String> getSampleResult() {
		return sampleResult;
	}

	public void setSampleResult(List<String> sampleResult) {
		this.sampleResult = sampleResult;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
