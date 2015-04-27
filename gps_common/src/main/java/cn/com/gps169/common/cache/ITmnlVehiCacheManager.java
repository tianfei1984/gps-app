package cn.com.gps169.common.cache;

import cn.com.gps169.db.model.TerminalVehicle;


/**
 * 车辆、终端关联关系缓存接口
 * @author tianfei
 *
 */
public interface ITmnlVehiCacheManager {
	
	/**
	 * 初始化车辆、终端绑定关系
	 */
	void initCache();
	
	/**
	 * 添加终端、车辆绑定关系至缓存
	 * @param r
	 * @return
	 */
	TerminalVehicle addBindRelation(TerminalVehicle r);
	
	/**
	 * 根据车辆ID查询绑定关系
	 * @param vehicleId
	 * @return
	 */
	TerminalVehicle findCurBindRelationsByVehicleId(int vehicleId);
	
	/**
	 * 根据终端ID查询绑定关系
	 * @param terminalId
	 * @return
	 */
	TerminalVehicle findCurBindRelationsByTerminalId(int terminalId);
}
