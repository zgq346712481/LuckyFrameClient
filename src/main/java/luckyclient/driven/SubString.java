package luckyclient.driven;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;

import luckyclient.publicclass.LogUtil;

/**
 * 公用驱动
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年1月15日
 */
public class SubString {
	/**
	 * 截取指定字符串的中间字段
	 * 
	 * @param str
	 * @param startstr
	 * @param endstr
	 * @return
	 */
	public static String subCentreStr(String str, String startstr, String endstr) {
		try{
			int startnum=0;
			int endnum=str.length();
			if(!"".equals(startstr)){
				startnum=str.indexOf(startstr) + startstr.length();
			}
			if(!"".equals(endstr)){
				endnum=str.indexOf(endstr, str.indexOf(startstr) + startstr.length());
			}
			String getstr = str.substring(startnum,endnum);
			return getstr;
		}catch(Exception e){
			LogUtil.APP.error("subCentreStr截取字符串出现异常，请检查参数！",e);
			return "截取字符串出现异常，请检查参数！";
		}
	}

	/**
	 * 截取字符串从指定字符开始
	 * 
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subStartStr(String str, String startstr) {
		try{
			String getstr = str.substring(str.indexOf(startstr) + startstr.length());
			return getstr;
		}catch(Exception e){
			LogUtil.APP.error("subStartStr截取字符串出现异常，请检查参数！",e);
			return "截取字符串出现异常，请检查参数！";
		}
	}

	/**
	 * 截取字符串到指定字符结束
	 * 
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subEndStr(String str, String endstr) {
		try{
			String getstr = str.substring(0, str.indexOf(endstr));
			return getstr;
		}catch(Exception e){
			LogUtil.APP.error("subEndStr截取字符串出现异常，请检查参数！",e);
			return "截取字符串出现异常，请检查参数！";
		}
	}

	/**
	 * 通过字符串位置截取指定字符串的中间字段
	 * 
	 * @param str
	 * @param startstr
	 * @param endstr
	 * @return
	 */
	public static String subCentreNum(String str, String startnum, String endnum) {
		String getstr = "";
		if("".equals(startnum)){
			startnum="0";
		}
		if("".equals(endnum)){
			endnum=String.valueOf(str.length());
		}
		try{
			if (isInteger(startnum) && isInteger(endnum)) {
				int start = Integer.valueOf(startnum);
				int end = Integer.valueOf(endnum);
				if (start > end) {
					getstr = "截取字符串开始位置数字不能大于结束位置数字";
				} else if (start < 0 || end < 0) {
					getstr = "截取字符串位置的数字不能小于0";
				} else if (start > str.length() || end > str.length()) {
					getstr = "截取字符串位置的数字不能大于字符串本身的长度【" + str.length() + "】";
				} else {
					getstr = str.substring(start, end);
				}
			} else {
				getstr = "指定的开始或是结束位置字符不是数字类型，请检查！";
			}

			return getstr;
		}catch(Exception e){
			LogUtil.APP.error("subCentreNum截取字符串出现异常，请检查参数！",e);
			return "截取字符串出现异常，请检查参数！";
		}
	}

	/**
	 * 通过字符串位置截取字符串从指定字符开始
	 * 
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subStartNum(String str, String startnum) {
		String getstr = "";
		try{
			if (isInteger(startnum)) {
				int start = Integer.valueOf(startnum);
				if (start < 0) {
					getstr = "截取字符串位置的数字不能小于0";
				} else if (start > str.length()) {
					getstr = "截取字符串位置的数字不能大于字符串本身的长度【" + str.length() + "】";
				} else {
					getstr = str.substring(start);
				}
			} else {
				getstr = "指定的开始位置字符不是数字类型，请检查！";
			}

			return getstr;
		}catch(Exception e){
			LogUtil.APP.error("subStartNum截取字符串出现异常，请检查参数！",e);
			return "截取字符串出现异常，请检查参数！";
		}
	}

	/**
	 * 截取字符串到指定字符结束
	 * 
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subEndNum(String str, String endnum) {
		String getstr = "";
		try{
			if (isInteger(endnum)) {
				int end = Integer.valueOf(endnum);
				if (end < 0) {
					getstr = "截取字符串位置的数字不能小于0";
				} else if (end > str.length()) {
					getstr = "截取字符串位置的数字不能大于字符串本身的长度【" + str.length() + "】";
				} else {
					getstr = str.substring(0, end);
				}
			} else {
				getstr = "指定的结束位置字符不是数字类型，请检查！";
			}

			return getstr;
		}catch(Exception e){
			LogUtil.APP.error("subEndNum截取字符串出现异常，请检查参数！",e);
			return "截取字符串出现异常，请检查参数！";
		}
	}

	public static String subStrRgex(String str, String rgex, String num) {
		List<String> list = new ArrayList<String>();
		try{
			Pattern pattern = Pattern.compile(rgex);// 匹配的模式
			Matcher m = pattern.matcher(str);
			while (m.find()) {
//				int i = 1;
				list.add(m.group());
//				i++;
			}

			String getstr = "";
			if (isInteger(num)) {
				int index = Integer.valueOf(num);
				if (index < 0) {
					getstr = "截取字符串索引数字不能小于0";
				} else if (index > str.length()) {
					getstr = "截取字符串索引的数字不能大于字符串本身的长度【" + str.length() + "】";
				} else if (index > list.size()) {
					getstr = "未能在指定字符串中根据正则式找到匹配的字符串或是指定的索引数字大于能找到的匹配字符串索引量";
				} else {
					getstr = list.get(index - 1);
				}
			} else {
				getstr = "指定的索引位置字符不是数字类型，请检查！";
			}
			return getstr;
		}catch(Exception e){
			LogUtil.APP.error("subStrRgex截取字符串出现异常，请检查参数！",e);
			return "截取字符串出现异常，请检查参数！";
		}
	}

	private static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 初始化返回JSON中Value的值
	 */
	private static String JSONVALUE = "【获取JSON KEY中的Value异常】";

	/**
	 * 用于计数KEY的序号
	 */
	private static int COUNTER = 1;

	/**
	 * 遍历JSON对象
	 * 
	 * @param json
	 * @param key
	 * @param keyindex
	 * @return
	 */
	private static JSONObject parseJsonString(String json, String key, int keyindex) {
		LinkedHashMap<String, Object> jsonMap = JSON.parseObject(json,
				new TypeReference<LinkedHashMap<String, Object>>() {
				}, Feature.OrderedField);
		for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
			parseJsonMap(entry, key, keyindex);
		}
		return new JSONObject(jsonMap);
	}

	/**
	 * 遍历后JSON对象中的key以及value
	 * 
	 * @param entry
	 * @param key
	 * @param keyindex
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map.Entry<String, Object> parseJsonMap(Map.Entry<String, Object> entry, String key, int keyindex) {
		// 如果是单个map继续遍历
		if (entry.getValue() instanceof Map) {
			LinkedHashMap<String, Object> jsonMap = JSON.parseObject(entry.getValue().toString(),
					new TypeReference<LinkedHashMap<String, Object>>() {
					}, Feature.OrderedField);
			for (Map.Entry<String, Object> entry2 : jsonMap.entrySet()) {
				parseJsonMap(entry2, key, keyindex);
			}
		}
		// 如果是list就提取出来
		if (entry.getValue() instanceof List) {
			List list = (List) entry.getValue();
			for (int i = 0; i < list.size(); i++) {
				// 如果还有，循环提取
				//list.set(i, parseJsonString(list.get(i).toString(), key, keyindex));
				//如何还有，循环提取
				try{
					list.set(i, parseJsonString(list.get(i).toString(), key, keyindex));
				}catch(JSONException jsone){
					if(key.equals(entry.getKey())){
						if(keyindex==COUNTER){
							JSONVALUE = entry.getValue().toString();
						}			
						COUNTER++;
					}
					break;
				}
			}
		}
		// 获取key中的value
		if (key.equals(entry.getKey())) {
			if (keyindex == COUNTER) {
				JSONVALUE = entry.getValue().toString();
			}
			COUNTER++;
		}

		return entry;
	}

	/**
	 * 获取JSON或是JSONArray对象指定序号Key中的Value
	 * 
	 * @param json
	 * @param key
	 * @param indexstr
	 * @return
	 */
	public static String getJsonValue(String json, String key, String indexstr) {
		json = json.trim();
		int index = 1;
		String result = JSONVALUE;
		if (isInteger(indexstr) && !"0".equals(indexstr)) {
			index = Integer.valueOf(indexstr);
		} else {
			result = JSONVALUE + "指定的key值序号不是大于0的整数(序号从1开始)，请检查！";
			return result;
		}

		if (json.startsWith("{") && json.endsWith("}")) {
			try {
				JSONObject jsonStr = JSONObject.parseObject(json, Feature.OrderedField);
				parseJsonString(jsonStr.toString(), key, index);
				result = JSONVALUE;
			} catch (Exception e) {
				result = JSONVALUE + "格式化成JSON异常，请检查参数：" + json;
				return result;
			}
		} else if (json.startsWith("[") && json.endsWith("]")) {
			try {
				// JSONArray jsonarr = JSONArray.parseArray(json);
				// 直接使用fastjson的接口实现有序解析
				JSONArray jsonarr = JSONArray.parseObject(json.getBytes("UTF-8"), JSONArray.class, Feature.OrderedField);
				for (int i = 0; i < jsonarr.size(); i++) {
					JSONObject jsonStr = jsonarr.getJSONObject(i);
					parseJsonString(jsonStr.toJSONString(), key, index);
					if (!JSONVALUE.startsWith("【获取JSON KEY中的Value异常】")) {
						result = JSONVALUE;
						break;
					}
				}
			} catch (Exception e) {
				result = JSONVALUE + "格式化成JSONArray异常，请检查参数：" + json;
				return result;
			}
		} else {
			result = JSONVALUE + "格式化成JSON或是JSONArray时出现异常，请检查参数：" + json;
		}

		if (result.equals("【获取JSON KEY中的Value异常】")) {
			result = JSONVALUE + "没有找到对应的KEY值，请确认！";
		}

		COUNTER = 1;
		JSONVALUE = "【获取JSON KEY中的Value异常】";
		return result;
	}

}
