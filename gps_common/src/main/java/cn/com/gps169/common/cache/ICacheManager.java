package cn.com.gps169.common.cache;

import java.util.List;
import cn.com.gps169.common.model.GpsInfo;
import cn.com.gps169.common.model.VehicleVo;


/**
 * 车辆缓存
 * @author tianfei
 *
 */
public interface ICacheManager {
	
	/**
	 * 初始化缓存
	 */
	void initCache();
	
	/**
	 * 根据SIM卡号查询车辆信息
	 * @param simnNo
	 * @return
	 */
	VehicleVo findVehicleBySim(String simNo);
	
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
	 * 根据用户ID查询用户下车辆集合
	 * @param userId
	 * @return
	 */
	List<VehicleVo> findVehiclesByUserId(int userId);
	
	/**
	 * 更新车辆静态数据缓存信息
	 * @param vehicleVo
	 * @return
	 */
	VehicleVo updateVehicle(VehicleVo vehicleVo);
	
	/**
	 * 根据SIM查询车辆当前GPS位置信息
	 * @param simNo
	 * @return
	 */
	GpsInfo findGpsInfoBySim(String simNo);
	
	/**
	 * 
	 * @param gpsInfo
	 * @return
	 */
	GpsInfo addGpsInfo(GpsInfo gpsInfo);
	
	/**
	 * 添加GPS至车辆轨迹集合
	 * @param gpsInfo
	 */
	void addVehicleTrack(GpsInfo gpsInfo);
	
	/**
	 * 查询车辆轨迹
	 * @param simNo
	 * @param recvDay
	 * @return
	 */
	List<GpsInfo> findVehicleTrip(int simNo, long recvDay);
	
	/**
	 * 删除车辆某日轨迹点
	 * @param simNo
	 * @param recvDay
	 */
	void deleteVehicleTrip(int simNo, long recvDay);

}
