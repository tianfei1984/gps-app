package cn.com.gps169.bos.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cn.com.gps169.bos.service.IVehicleService;
import cn.com.gps169.db.dao.VehicleMapper;
import cn.com.gps169.db.model.Vehicle;
import cn.com.gps169.db.model.VehicleExample;
import cn.com.gps169.db.model.VehicleExample.Criteria;

@Service
public class VehicleServiceImpl implements IVehicleService {
	
	@Autowired
	private VehicleMapper vehicleMapper;

	@Override
	public JSONObject queryVehicle(int pageNum, int pageRows,int status,String licensePlate) {
		VehicleExample example = new VehicleExample();
		Criteria criteria = example.or();
		if(status != 0){
			criteria.andStatusEqualTo(status);
		}
		if(StringUtils.isNotBlank(licensePlate)){
			criteria.andLicensePlateLike(licensePlate);
		}
		example.setLimitStart(pageNum);
		example.setLimitEnd(pageRows);
		List<Vehicle> list = vehicleMapper.selectByExample(example);
		JSONObject json = null;
		JSONArray result = new JSONArray();
		for(Vehicle v : list){
			json = new JSONObject();
			json.put("vid", v.getVehicleId());
			json.put("licesePlate", v.getLicensePlate());
			json.put("ein", v.getEin());
			json.put("sim", "");
			json.put("status", v.getStatus());
			result.add(json);
		}
		JSONObject vehicles = new JSONObject();
		vehicles.put("total", 100);
		vehicles.put("rows", result);
		
		return vehicles;
	}

}
