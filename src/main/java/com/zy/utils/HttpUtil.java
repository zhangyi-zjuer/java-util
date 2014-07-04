package com.zy.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

/**
 * 发送http的get和post请求，使用时先调用init初始化。
 * 
 * @author 张翼
 * @email zhangyi.zjuer@gmail.com
 * @date 2013年12月11日
 */
public class HttpUtil {
	private static final Logger logger = Logger.getLogger(HttpUtil.class);
	private static int _requestRetry = 0;
	private static int _timeout;
	private static boolean _useProxy = false;
	private static String _proxyHost;
	private static int _proxyPort;
	private static int _defaultRetrySleep = 1000;

	static {
		/* 关闭htmlunit的日志记录 */
		Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
	}

	public static WebClient getWebClient() {

		if (_requestRetry < 1) {
			logger.error("未初始化");
			System.exit(1);
		}

		WebClient client;
		if (_useProxy && _proxyHost != null) {
			client = new WebClient(BrowserVersion.FIREFOX_17, _proxyHost,
					_proxyPort);
		} else {
			client = new WebClient();
		}
		WebClientOptions options = client.getOptions();
		options.setCssEnabled(false);
		options.setJavaScriptEnabled(false);
		options.setTimeout(_timeout); // 设置超时为2分钟
		options.setUseInsecureSSL(true);
		return client;
	}

	/**
	 * 初始化
	 * 
	 * @param requestRetry
	 *            请求重试次数
	 * @param timeout
	 *            请求超时时间
	 * @param useProxy
	 *            是否使用代理
	 * @param proxyIP
	 *            代理ip
	 * @param proxyPort
	 *            代理端口
	 */
	public static void init(int requestRetry, int defaultRetrySleep,
			int timeout, boolean useProxy, String proxyIP, int proxyPort) {
		_requestRetry = requestRetry;
		_timeout = timeout;
		_useProxy = useProxy;
		_proxyHost = proxyIP;
		_proxyPort = proxyPort;
		_defaultRetrySleep = defaultRetrySleep;

	}

	/**
	 * 通过配置文件配置初始化 <br>
	 * 配置需要文件包含: <br>
	 * http.request.retry: http出错重试次数,默认为5次 <br>
	 * http.request.retry.sleep: 默认重试间隔, 默认为1000ms<br>
	 * http.request.timeout: http请求超时时间 <br>
	 * http.proxy.on: 是否使用代理 <br>
	 * http.proxy.host: 代理地址 <br>
	 * http.proxy.port: 代理端口<br>
	 * 
	 * @param configure
	 */
	public static void init(PropertiesConfiguration configure) {
		_requestRetry = configure.getInt("http.request.retry", 5);
		_timeout = configure.getInt("http.request.timeout", 2 * 60 * 1000);
		_useProxy = configure.getBoolean("http.proxy.on", false);
		_proxyHost = configure.getString("http.proxy.host");
		_proxyPort = configure.getInt("http.proxy.port");
		_defaultRetrySleep = configure.getInt("http.request.retry.sleep", 1000);

	}

	/**
	 * 通过配置文件配置初始化 <br>
	 * 配置需要文件包含: <br>
	 * http.request.retry: http出错重试次数,默认为5次 <br>
	 * http.request.retry.sleep: 默认重试间隔, 默认为1000ms<br>
	 * http.request.timeout: http请求超时时间 <br>
	 * http.proxy.on: 是否使用代理 <br>
	 * http.proxy.host: 代理地址 <br>
	 * http.proxy.port: 代理端口
	 * 
	 * @param configFile
	 *            配置文件
	 * @throws ConfigurationException
	 */
	public static void init(String configFile) throws ConfigurationException {
		PropertiesConfiguration configure = ConfigUtil.load(configFile);
		init(configure);
	}

	/**
	 * 发送get请求
	 * 
	 * @param url
	 *            请求地址
	 * @param client
	 *            浏览器对象
	 * @param charset
	 *            编码
	 * @param retrySleepTime
	 *            重试等待时间，单位ms, 小于等于0表示不等待直接重试
	 * @return
	 * @throws Exception
	 */
	public static String get(String url, WebClient client, String charset,
			int retrySleepTime) throws Exception {
		Exception ex = null;
		for (int i = 0; i < _requestRetry; i++) {
			try {
				WebResponse webReponse = client.getPage(url).getWebResponse();
				return inputStream2String(webReponse.getContentAsStream(),
						charset);
			} catch (Exception e) {
				logger.error((i + 1) + "/" + _requestRetry + "\t"
						+ e.getMessage());
				ex = e;
				if (retrySleepTime > 0 && i + 1 < _requestRetry) { // 重试等待
					Thread.sleep(retrySleepTime);
				}
			}
		}

		throw ex;
	}

	/**
	 * 发送get请求
	 * 
	 * @param url
	 *            请求地址
	 * @param client
	 *            浏览器对象
	 * 
	 * @param retrySleepTime
	 *            重试等待时间，单位ms, 小于等于0表示不等待直接重试
	 * @return
	 * @throws Exception
	 */
	public static String get(String url, WebClient client, int retrySleepTime)
			throws Exception {
		Exception ex = null;
		for (int i = 0; i < _requestRetry; i++) {
			try {
				WebResponse webReponse = client.getPage(url).getWebResponse();
				return webReponse.getContentAsString();
			} catch (Exception e) {
				logger.error((i + 1) + "/" + _requestRetry + "\t"
						+ e.getMessage());
				ex = e;
				if (retrySleepTime > 0 && i + 1 < _requestRetry) { // 重试等待
					Thread.sleep(retrySleepTime);
				}
			}
		}

		throw ex;
	}

	/**
	 * 
	 * @param is
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	private static String inputStream2String(InputStream is, String charset)
			throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is,
				charset));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}

	/**
	 * 发送get请求,重试使用默认等待时间。<br>
	 * 如果重试需要等待请使用 {@link HttpUtil#get(String, WebClient, int)}
	 * 
	 * @param url
	 *            请求地址
	 * @param client
	 *            浏览器对象
	 * @return
	 * @throws Exception
	 */
	public static String get(String url, WebClient client) throws Exception {
		return get(url, client, _defaultRetrySleep);
	}

	/**
	 * 发送get请求,重试使用默认等待时间。<br>
	 * 如果重试需要指定等待请使用 {@link HttpUtil#get(String, WebClient, String, int)}
	 * 
	 * @param url
	 *            请求地址
	 * @param client
	 *            浏览器对象
	 * @param charset
	 *            字符编码
	 * @return
	 * @throws Exception
	 */
	public static String get(String url, WebClient client, String charset)
			throws Exception {
		return get(url, client, charset, _defaultRetrySleep);
	}

	/**
	 * 发送post请求
	 * 
	 * @param url
	 *            请求地址
	 * @param body
	 *            请求数据
	 * @param client
	 *            浏览器对象
	 * @param retrySleepTime
	 *            重试等待时间，单位ms, 小于等于0表示不等待直接重试
	 * @param headers
	 *            额外的header
	 * @return
	 * @throws Exception
	 */
	public static String post(String url, String body, WebClient client,
			int retrySleepTime, String... headers) throws Exception {
		Exception ex = null;
		for (int i = 0; i < _requestRetry; i++) {
			try {
				WebRequest request = new WebRequest(new URL(url),
						HttpMethod.POST);
				request.setCharset("UTF-8");
				request.setRequestBody(body);

				for (String header : headers) {
					String[] tmp = header.split("=");
					request.setAdditionalHeader(tmp[0], tmp[1].trim());
				}

				return client.getPage(request).getWebResponse()
						.getContentAsString();
			} catch (Exception e) {
				ex = e;
				logger.error((i + 1) + "/" + _requestRetry + "\t"
						+ e.getMessage());

				if (retrySleepTime > 0 && i + 1 < _requestRetry) { // 重试等待
					Thread.sleep(retrySleepTime);
				}
			}
		}

		throw ex;
	}

	/**
	 * 发送post请求 ,失败后不等待直接重试<br>
	 * 如果重试需要等待请使用
	 * {@link HttpUtil#post(String, String, WebClient, int, String...)}
	 * 
	 * @param url
	 *            请求地址
	 * @param body
	 *            请求数据
	 * @param client
	 *            浏览器对象
	 * @param headers
	 *            额外的header
	 * @return
	 * @throws Exception
	 */
	public static String post(String url, String body, WebClient client,
			String... headers) throws Exception {
		return post(url, body, client, 0, headers);
	}

	/**
	 * 发送post请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param client
	 *            浏览器对象
	 * @param retrySleepTime
	 *            重试等待时间，单位ms, 小于等于0表示不等待直接重试
	 * @param headers
	 *            额外的Http header
	 * @return
	 * @throws Exception
	 */
	public static String post(String url, List<NameValuePair> params,
			WebClient client, int retrySleepTime, String... headers)
			throws Exception {
		Exception ex = null;
		for (int i = 0; i < _requestRetry; i++) {
			try {
				WebRequest request = new WebRequest(new URL(url),
						HttpMethod.POST);
				request.setCharset("UTF-8");
				request.setRequestParameters(params);

				for (String header : headers) {
					String[] tmp = header.split("=");
					request.removeAdditionalHeader(tmp[0]);
					request.setAdditionalHeader(tmp[0], tmp[1].trim());
				}

				return client.getPage(request).getWebResponse()
						.getContentAsString();
			} catch (Exception e) {
				ex = e;
				logger.error((i + 1) + "/" + _requestRetry + "\t"
						+ e.getMessage());
				if (retrySleepTime > 0 && i + 1 < _requestRetry) { // 重试等待
					Thread.sleep(retrySleepTime);
				}
			}
		}

		throw ex;
	}

	/**
	 * 发送post请求, 失败后不等待直接重试<br>
	 * 如果重试需要等待请使用
	 * {@link HttpUtil#post(String, List, WebClient, int, String...)}
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param client
	 *            浏览器对象
	 * @param headers
	 *            额外的http header
	 * @return
	 * @throws Exception
	 */
	public static String post(String url, List<NameValuePair> params,
			WebClient client, String... headers) throws Exception {
		return post(url, params, client, 0, headers);
	}

	/**
	 * 判断url是否正常
	 * 
	 * @param url
	 * @param mustContain
	 *            必须包含的字符串
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws FailingHttpStatusCodeException
	 */
	public static boolean isValidUrl(String url, String mustContain)
			throws IOException {
		WebClient client = HttpUtil.getWebClient();

		String response = client.getPage(url).getWebResponse()
				.getContentAsString();
		if (response == null || !response.contains(mustContain)) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) throws Exception {
		HttpUtil.init(10, 0, 100, true, "192.168.6.231", 3128);
		String url = "http://192.168.8.87:55855/simple_address?address="
				+ URLEncoder.encode("西湖区华星路4号华星路学院路口", "UTF-8") + "&cityname="
				+ URLEncoder.encode("杭州", "UTF-8");
		System.out.println(url);
		String response = HttpUtil.get(url, HttpUtil.getWebClient(), "UTF-8");
		System.out.println(response);
		
	}

}
