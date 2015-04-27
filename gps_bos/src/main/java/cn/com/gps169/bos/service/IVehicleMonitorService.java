package cn.com.gps169.bos.service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 车辆监控业务接口
 * @author tianfei
 *
 */
public interface IVehicleMonitorService {
	
	/**
	 * 根据条件查询车辆信息
	 * @param params
	 * @return
	 */
	JSONArray queryVehicles(JSONObject params);
	
	/**
	 * 查询车辆位置信息
	 * @param vehicleId
	 * @param updateTime
	 * @return
	 */
	JSONObject queryRealLocation(int vehicleId,long updateTime);
	
	/**
	 * 查询车辆轨迹
	 * @return
	 */
	JSONArray queryTrack();
	
	/**
	 * 根据ID查询轨迹点
	 * @param tripId
	 * @return
	 */
	String queryTripById(int tripId);

}
