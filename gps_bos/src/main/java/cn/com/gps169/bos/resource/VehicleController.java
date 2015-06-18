package cn.com.gps169.bos.resource;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.gps169.bos.service.IVehicleService;
import cn.com.gps169.db.model.Vehicle;

/**
 * 终端控制器
 * @author tianfei
 *
 */
@Controller
@RequestMapping("vehicle")
public class VehicleController {
	
	@Autowired
	private IVehicleService vehicleService;
	
	/**
	 * 分页查询车辆信息
	 * @return
	 */
	@RequestMapping("page")
	@ResponseBody
	public String vehiclePage(HttpServletRequest request){
		int pageNum = Integer.parseInt(request.getParameter("page"));
		int pageRows = Integer.parseInt(request.getParameter("rows"));
		String sStatus = request.getParameter("status");
		int status = StringUtils.isNotBlank(sStatus) ? Integer.parseInt(sStatus) : 0;
		String licensePlate = request.getParameter("licensePlate");
		JSONObject result = vehicleService.queryVehicle((pageNum-1)*pageRows, pageRows,status,licensePlate);
		
		return result.toString();
	}
	
	/**
	 * 查询车辆信息
	 * @param vid
	 * @return
	 */
	@RequestMapping("get")
	@ResponseBody
	public String getVehicle(@RequestParam("vid") int vid){
	    Vehicle vehicle = vehicleService.queryVehicleById(vid);
	    if(vehicle != null){
	        return JSONObject.fromObject(vehicle).toString();
	    }
	    return null;
	}
	
	/**
	 * 增加车辆
	 * @param vehicle
	 * @return
	 */
	@RequestMapping(value="add",method=RequestMethod.POST,consumes="application/json")
	@ResponseBody
	public String addVehicle(@RequestBody Vehicle vehicle){
	    JSONObject result = new JSONObject();
	    result.put("flag", "fail");
	    if(StringUtils.isBlank(vehicle.getPlateNo())){
	        result.put("msg", "车牌号不能为空");
	        return result.toString();
	    }
	    if(StringUtils.isBlank(vehicle.getEin())){
	        result.put("msg", "车辆发动机号不能为空");
	        return result.toString();
	    }
	    String opt = vehicleService.addOrUpdateVehicle(vehicle); 
	    if(StringUtils.isBlank(opt)){
	        result.put("flag", "success");
	    } else {
	        result.put("msg", opt);
	    }
	    
	    return result.toString();
	}
	
	/**
	 * 查询未绑定的车辆信息
	 * @return
	 */
	@RequestMapping("unbindVeh")
	@ResponseBody
	public String getUnbindVeh(){
	    JSONArray result = vehicleService.queryUnbindVeh();
	    
	    return result.toString();
	}
	
}
