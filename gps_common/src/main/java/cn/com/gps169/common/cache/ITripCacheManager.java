package cn.com.gps169.common.cache;

import net.sf.json.JSONObject;

/**
 * 车辆轨迹缓存
 * @author tianfei
 *
 */
public interface ITripCacheManager {

	/**
	 * 保存车辆位置
	 * @param gpsInfo
	 */
	void pushGpsRecord(JSONObject gpsInfo);
	
	/**
	 * 查询车辆轨迹
	 * @param vehicleId
	 * @param recvDay
	 * @return
	 */
	JSONObject getGpsTrip(int vehicleId, int recvDay);
	
	/**
	 * 删除车辆某日轨迹点
	 * @param vehicleId
	 * @param recvDay
	 */
	void deleteGpsTrip(int vehicleId, int recvDay);
	
}
