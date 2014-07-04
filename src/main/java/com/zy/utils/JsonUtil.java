package com.zy.utils;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Json字符串和对象转换
 * 
 * @author 张翼
 * @email zhangyi.zjuer@gmail.com
 * @date 2013年12月20日
 */
public class JsonUtil {
	private static ObjectMapper mapper = new ObjectMapper();

	/**
	 * java对象转换为json字符串
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static String toJson(Object obj) throws IOException {
		StringWriter writer = new StringWriter();
		JsonGenerator gen = new JsonFactory().createJsonGenerator(writer);
		mapper.writeValue(gen, obj);
		gen.close();
		String json = writer.toString();
		writer.close();
		return json;
	}

	/**
	 * json字符串转换为Java对象
	 * 
	 * @param jsonString
	 *            json字符串
	 * @param clazz
	 * 
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public static <T> T toObj(String jsonString, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {
		T jsonObj = (T) mapper.readValue(jsonString, clazz);
		return jsonObj;
	}

}
