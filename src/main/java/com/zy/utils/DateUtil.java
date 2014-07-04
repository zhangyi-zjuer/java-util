package com.zy.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 日期处理
 * 
 * @author 张翼
 * @email zhangyi.zjuer@gmail.com
 * @date 2013年12月30日
 */
public class DateUtil {

	/**
	 * 获取日期
	 * 
	 * @param format
	 *            日期格式，如"yyyy-MM-dd"
	 * @param delta
	 *            日期间隔，如-1表示昨天，0表示今天
	 * @return
	 */
	public static String getDate(String format, int delta) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, delta);
		String day = new SimpleDateFormat(format).format(cal.getTime());
		return day;
	}

	/**
	 * 获取当天日期
	 * 
	 * @param format
	 *            日期格式
	 * @return
	 */
	public static String getDate(String format) {
		return getDate(format, 0);
	}
}
