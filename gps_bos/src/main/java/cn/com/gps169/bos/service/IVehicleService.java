package cn.com.gps169.bos.service;

import cn.com.gps169.db.model.Vehicle;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 车辆业务逻辑接口
 * @author tianfei
 *
 */
public interface IVehicleService {
	
	/**
	 * 查询车辆信息
	 * @param params
	 * @return
	 */
	JSONObject queryVehicle(int pageNum,int pageRows,int status,String licensePlate);
	
	/**
	 * 增加车辆 
	 * @param vehicle
	 */
	String addOrUpdateVehicle(Vehicle vehicle);
	
	/**
	 * 根据车辆id查询车辆信息
	 * @param vid
	 * @return
	 */
	Vehicle queryVehicleById(int vid);

	/**
	 * 查询未绑定的车辆
	 * @return
	 */
	JSONArray queryUnbindVeh();
	
}
