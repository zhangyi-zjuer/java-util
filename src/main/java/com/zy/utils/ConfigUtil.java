package com.zy.utils;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.zy.utils.excepiton.NoConfigureLoadException;

/**
 * 加载配置文件
 * 
 * @author 张翼
 * @email zhangyi.zjuer@gmail.com
 * @date 2013年12月20日
 */
public class ConfigUtil {
	private static PropertiesConfiguration configure;

	/**
	 * 加载配置文件
	 * 
	 * @param file
	 * @return
	 * @throws ConfigurationException
	 */
	public static PropertiesConfiguration load(String file)
			throws ConfigurationException {
		if (configure == null) {
			synchronized (ConfigUtil.class) {
				if (configure == null) {
					configure = new PropertiesConfiguration();
					configure.setEncoding("utf-8");
					configure.load(file);
				}
			}
		}

		return configure;
	}

	/**
	 * 获取配置信息
	 * 
	 * @return
	 * @throws NoConfigureLoadException
	 */
	public static PropertiesConfiguration getConfigure()
			throws NoConfigureLoadException {
		if (configure == null) {
			throw new NoConfigureLoadException("请先加载配置文件");
		}
		return configure;
	}
}
