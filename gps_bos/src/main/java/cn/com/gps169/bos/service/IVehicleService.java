package cn.com.gps169.bos.service;

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

}
