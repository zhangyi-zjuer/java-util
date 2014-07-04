package com.zy.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 * 其它常用的工具
 * 
 * @author 张翼
 * @email zhangyi.zjuer@gmail.com
 * @date 2013年12月30日
 */
public class CommonUtil {
	private static final Logger logger = Logger.getLogger(CommonUtil.class);

	/**
	 * 获取命令行参数值
	 * 
	 * @param args
	 *            命令行参数
	 * @param opt
	 *            单字符参数,如-c
	 * @param longOpt
	 *            长参数, 如--config
	 * @param defaultValue
	 *            默认值
	 * @param desc
	 *            参数描述
	 * @return 参数值
	 */
	public static String getCommandLineOption(String[] args, String opt,
			String longOpt, String defaultValue, String desc) {
		String result = defaultValue;
		Options opts = new Options();
		opts.addOption(opt, longOpt, true, desc);
		CommandLineParser parser = new ExtendGnuParser(true);
		try {
			CommandLine cl = parser.parse(opts, args);
			if (cl.hasOption(opt)) {
				result = cl.getOptionValue(opt);
			}
		} catch (ParseException e) {
			logger.error("", e);
		}
		return result;
	}

	/**
	 * 
	 * 获取命令行参数值，参数不存在，则返回null
	 * 
	 * @param args
	 *            命令行参数
	 * @param opt
	 *            单字符参数,如-c
	 * @param longOpt
	 *            长参数, 如--config
	 * @return 参数值
	 */
	public static String getCommandLineOption(String[] args, String opt,
			String longOpt) {
		return getCommandLineOption(args, opt, longOpt, null, "");
	}

	/**
	 * 获取命令行参数值，参数不存在，则返回默认值
	 * 
	 * @param args
	 *            命令行参数
	 * @param opt
	 *            单字符参数,如-c
	 * @param longOpt
	 *            长参数, 如--config
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	public static String getCommandLineOption(String[] args, String opt,
			String longOpt, String defaultValue) {
		return getCommandLineOption(args, opt, longOpt, defaultValue, "");
	}

	/**
	 * 获取Exception字符串
	 * 
	 * @param e
	 * @return
	 */
	public static String getErrorInfoFromException(Exception e) {
		if (e == null) {
			return "";
		}

		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		} catch (Exception ex) {
			logger.error("", ex);
			return "Bad getErrorInfoFromException";
		}
	}

}
