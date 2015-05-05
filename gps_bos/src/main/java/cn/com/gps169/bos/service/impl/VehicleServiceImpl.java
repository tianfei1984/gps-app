package cn.com.gps169.bos.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cn.com.gps169.bos.service.IVehicleService;
import cn.com.gps169.common.cache.IVehicleCacheManager;
import cn.com.gps169.db.dao.VehicleMapper;
import cn.com.gps169.db.model.Vehicle;
import cn.com.gps169.db.model.VehicleExample;
import cn.com.gps169.db.model.VehicleExample.Criteria;

@Service
public class VehicleServiceImpl implements IVehicleService {
	
	@Autowired
	private VehicleMapper vehicleMapper;
	
	@Autowired
	private IVehicleCacheManager vehicleCacheManager;

	@Override
	public JSONObject queryVehicle(int pageNum, int pageRows,int status,String licensePlate) {
		VehicleExample example = new VehicleExample();
		Criteria criteria = example.or();
		if(status != 0){
			criteria.andStatusEqualTo(status);
		}
		if(StringUtils.isNotBlank(licensePlate)){
			criteria.andLicensePlateLike("%"+licensePlate+"%");
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
			json.put("vin", v.getVin());
			json.put("type", v.getType() == 1 ? "货运车辆" : "其他");
			json.put("status", v.getStatus() == 1 ? "正常":"停用");
			json.put("created", v.getCreated());
			result.add(json);
		}
		JSONObject vehicles = new JSONObject();
		vehicles.put("total", 100);
		vehicles.put("rows", result);
		
		return vehicles;
	}

    /* (non-Javadoc)
     * @see cn.com.gps169.bos.service.IVehicleService#addVehicle(cn.com.gps169.db.model.Vehicle)
     */
    @Override
    public String addOrUpdateVehicle(Vehicle vehicle) {
        Integer vid = vehicle.getVehicleId();
        //判断车辆有限性
        VehicleExample example = new VehicleExample();
        if(vid == null){
            example.or().andLicensePlateEqualTo(vehicle.getLicensePlate());
        } else {
            example.or().andLicensePlateEqualTo(vehicle.getLicensePlate()).andVehicleIdNotEqualTo(vid);
        }
        int count = vehicleMapper.countByExample(example);
        if(count > 0){
            return "车辆的车辆号已经存在";
        }
        example.clear();
        if(vid == null){
            example.or().andEinEqualTo(vehicle.getEin());
        } else {
            example.or().andEinEqualTo(vehicle.getEin()).andVehicleIdNotEqualTo(vid);
        }
        count = vehicleMapper.countByExample(example);
        if(count > 0){
            return "发动机号已经存在";
        }
        if(vid == null){
            vehicle.setCreated(new Date());
            vehicleMapper.insert(vehicle);
        } else {
            vehicle.setUpdated(new Date());
            vehicleMapper.updateByPrimaryKeySelective(vehicle);
        }
        // 更新缓存信息
        vehicleCacheManager.addVehicle(vehicle);
        return null;
    }

    /* (non-Javadoc)
     * @see cn.com.gps169.bos.service.IVehicleService#queryVehicleById(int)
     */
    @Override
    public Vehicle queryVehicleById(int vid) {
        
        return vehicleMapper.selectByPrimaryKey(vid);
    }

    /* (non-Javadoc)
     * @see cn.com.gps169.bos.service.IVehicleService#queryUnbindVeh()
     */
    @Override
    public JSONArray queryUnbindVeh() {
        VehicleExample example = new VehicleExample();
        example.or().andStatusEqualTo(1).andTerminalIdIsNull();
        List<Vehicle> list = vehicleMapper.selectByExample(example);
        JSONArray array = new JSONArray();
        JSONObject veh = null;
        for(Vehicle v : list){
            veh = new JSONObject();
            veh.put("vid", v.getVehicleId());
            veh.put("licensePlate", v.getLicensePlate());
            array.add(veh);
        }
        
        return array;
    }

}
