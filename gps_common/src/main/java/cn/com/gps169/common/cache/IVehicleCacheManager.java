package cn.com.gps169.common.cache;

import java.util.List;
import cn.com.gps169.common.model.VehicleVo;


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
	 * 根据SIM卡号查询车辆信息
	 * @param simnNo
	 * @return
	 */
	VehicleVo findVehicleBySim(String simnNo);
	
	/**
	 * 根据车辆ID查询车辆信息
	 * @param vehicleId
	 * @return
	 */
	VehicleVo findVehicleById(int vehicleId);
	
	/**
	 * 增加车辆缓存
	 * @param v
	 * @return
	 */
	VehicleVo addVehicle(VehicleVo v);
	
	/**
	 *根据车牌查询车辆信息
	 * @param licensePlate
	 * @return
	 */
	VehicleVo findVehicleByPlate(String licensePlate);
	
	/**
	 * 查询所有车辆信息
	 * @return
	 */
	List<Integer> findAllVehicleIds();

}
