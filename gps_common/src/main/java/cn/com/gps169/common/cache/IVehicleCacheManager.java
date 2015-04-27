package cn.com.gps169.common.cache;

import java.util.List;

import cn.com.gps169.db.model.Vehicle;

/**
 * 车辆缓存
 * @author tianfei
 *
 */
public interface IVehicleCacheManager {
	
	/**
	 * 初始化缓存
	 */
	void initCache();
	
	/**
	 * 根据车辆ID查询车辆信息
	 * @param vehicleId
	 * @return
	 */
	Vehicle findVehicleById(int vehicleId);
	
	/**
	 * 增加车辆缓存
	 * @param v
	 * @return
	 */
	Vehicle addVehicle(Vehicle v);
	
	/**
	 *根据车牌查询车辆信息
	 * @param licensePlate
	 * @return
	 */
	Vehicle findVehicleByPlate(String licensePlate);
	
	/**
	 * 查询所有车辆信息
	 * @return
	 */
	List<Integer> findAllVehicleIds();

}
