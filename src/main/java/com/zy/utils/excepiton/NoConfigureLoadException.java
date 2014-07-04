package com.zy.utils.excepiton;

/**
 * 没有载入配置文件异常
 * 
 * @author stonecold.zhang
 * 
 */
public class NoConfigureLoadException extends Exception {
	private static final long serialVersionUID = -1586399137318790324L;

	public NoConfigureLoadException(String message) {
		super(message);
	}

	public NoConfigureLoadException() {
	}
}
