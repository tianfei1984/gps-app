package cn.com.gps169.common.gis;

import cn.com.gps169.common.model.GpsInfo;
import cn.com.gps169.common.tool.RestfulInvokeService;
import net.sf.json.JSONObject;

/**
 * 位置信息工具类
 * @author tianfei
 *
 */
public class GpsTool {
	
	private static final String KEY = "eb11422e622e0cc276685ee0ea4f59be";
	//www.che08.com/gis/ws/0.1/simplegeocoding?lat=39.990475&lon=116.481499&range&pattern&serviceid=1&encoded=0
	//www.che08.com/gis/ws/0.1/gps/encode?latlon=116.456451%2C40.022356&serviceid=1
	
	/**
	 * 将GPS偏转为高德位置 
	 * @param gpsInfo
	 * @return
	 */
	public static GpsInfo deflectGpsData(GpsInfo gpsInfo) { 
		String url = "http://restapi.amap.com/v3/assistant/coordinate/convert";
		JSONObject params = new JSONObject();
		params.put("locations", gpsInfo.getLongitude()+","+gpsInfo.getLatitude());
		params.put("coordsys", "gps");
		params.put("output", "json");
		params.put("key", KEY);
		JSONObject result = RestfulInvokeService.getData(url, params);
		if(result != null && "1".equals(result.get("status"))){
			String[] str = result.opt("locations").toString().split(",");
			gpsInfo.setLongitude(Double.parseDouble(str[0]));
			gpsInfo.setLatitude(Double.parseDouble(str[1]));
			gpsInfo.setLocation(deflectGps(gpsInfo.getLongitude(), gpsInfo.getLatitude()));
			return gpsInfo;
		}
		
		return gpsInfo;
	}
	
	/**
	 * 逆地址解析
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public static String deflectGps(double longitude,double latitude) {
		String url = "http://restapi.amap.com/v3/geocode/regeo";
		JSONObject params = new JSONObject();
		params.put("location", longitude+","+ latitude);
		params.put("key", "caaa086bdf5666322fba3baf5a6a2c03");
		JSONObject result = RestfulInvokeService.getData(url, params);
		if(result != null && "1".equals(result.getString("status"))){
			return result.optJSONObject("regeocode").optString("formatted_address");
		}
		return "未知";
	}
	
	public static void main(String[] args){
		GpsInfo g = new GpsInfo();
		g.setLongitude(116.481499);
		g.setLatitude(39.990475);
		
		g = deflectGpsData(g);
		System.out.println(g.getLongitude() + "  "+g.getLatitude());
		System.out.println(g.getLocation());
		
		System.out.println(deflectGps(116.309509,40.087528));
	}

}
