package cn.com.gps169.server.tool;

import cn.com.gps169.common.model.GpsInfo;
import cn.com.gps169.common.tool.RestfulInvokeService;
import net.sf.json.JSONObject;

/**
 * 位置信息工具类
 * @author tianfei
 *
 */
public class LocationTool {
	
	private static final String KEY = "eb11422e622e0cc276685ee0ea4f59be";
	
	/**
	 * 将GPS偏转为高德位置 
	 * @param gpsInfo
	 * @return
	 */
	public static JSONObject deflectGpsData(GpsInfo gpsInfo) { 
		JSONObject json  = null;
		String url = "http://restapi.amap.com/v3/assistant/coordinate/convert";
		JSONObject params = new JSONObject();
		params.put("locations", gpsInfo.getLongitude()+","+gpsInfo.getLatitude());
		params.put("coordsys", "gps");
		params.put("output", "json");
		params.put("key", KEY);
		JSONObject result = RestfulInvokeService.getData(url, params);
		if(result != null && "1".equals(result.get("status"))){
			json = new JSONObject();
			String[] str = result.opt("locations").toString().split(",");
			json.put("longitude", str[0]);
			json.put("latitude", str[1]);
			return json;
		}
		
		return null;
	}
	
	public static void main(String[] args){
		GpsInfo g = new GpsInfo();
		g.setLongitude(116.481499);
		g.setLatitude(39.990475);
		
		deflectGpsData(g);
	}

}
