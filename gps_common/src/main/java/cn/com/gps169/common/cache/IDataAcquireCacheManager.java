package cn.com.gps169.common.cache;

import net.sf.json.JSONObject;

/**
 * 数据采集缓存
 * @author tianfei
 *
 */
public interface IDataAcquireCacheManager {
	
	/**
	 * 设置终端在线
	 * @param terminalId
	 * @param isOnline
	 */
	void setIsOnline(Integer terminalId, Boolean isOnline);
	
	/**
	 * 设置车辆运行状态:行驶、停止 
	 * @param vehicleId
	 * @param status
	 */
	void setRunningStatus(Integer vehicleId, String status);
	
	/**
	 * 设置车辆位置 信息
	 * @param gpsInfo
	 */
	void setGps(JSONObject gpsInfo);
	
	/**
	 * 查询车辆位置信息
	 * @param vehicleId
	 * @return
	 */
	JSONObject getGps(int vehicleId);

}
