package cn.com.gps169.bos.resource;

import java.util.Date;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.com.gps169.bos.service.IVehicleMonitorService;
import cn.com.gps169.common.cache.IDataAcquireCacheManager;
import cn.com.gps169.common.cache.ITripCacheManager;
import cn.com.gps169.common.cache.ICacheManager;
import cn.com.gps169.common.tool.DateUtil;

/**
 * 车辆状态查询接口
 * @author tianfei
 *
 */
@Controller
@RequestMapping("monitor")
public class VehicleMonitorResource {
	
	@Autowired
	private IDataAcquireCacheManager dataAcquireCacheManager;
	
	@Autowired
	private ITripCacheManager tripCacheManager;
	
	@Autowired
	private ICacheManager vehicleCacheManager;
	
	@Autowired
	private IVehicleMonitorService vehicleMonitorService;
	
	@RequestMapping
	public @ResponseBody String desc(){
		JSONObject json = new JSONObject();
		json.put("location?vid", "车辆当前位置信息");
		json.put("rstatus?vid", "查询车辆当前运行状态");
		json.put("trip?vid", "查询车辆当前轨迹点");
		json.put("dailyJob", "生成日统计");
		json.put("tripJob", "生成轨迹");
		
		return json.toString();
	}
	
	/**
	 * 查询所有车辆位置信息
	 * @return
	 */
	@RequestMapping(value="allVehicles" ,method=RequestMethod.POST,consumes="application/json")
	public @ResponseBody String getAllGps(@RequestBody JSONObject params){
	    JSONArray array = vehicleMonitorService.queryVehicles(params);
	    
	    return array.toString();
	}
	

	/**
	 * 查询车辆当前位置信息
	 * @param vehicleId
	 * @return
	 */
	@RequestMapping(value = "location",method=RequestMethod.GET)
	public @ResponseBody String getVehicleGps(@RequestParam("vid")int vehicleId,@RequestParam("updated") long updated){
		JSONObject result = vehicleMonitorService.queryRealLocation(vehicleId, updated);
		System.out.print(".");
		return result.toString();
	}
	
	/**
	 * 查询车辆当前运行状态
	 * @param vehicleId
	 * @return
	 */
	@RequestMapping(value="rstatus",method=RequestMethod.GET)
	public @ResponseBody String getRunningSatus(@RequestParam("vid")int vehicleId) { 
		Date occurTime = DateUtil.addDate(DateUtil.formatDate(new Date()), 0);
//		RunningState rs = runningStatusCacheManager.findLatestRunningState(vehicleId, occurTime);
//		if(rs != null){
//			return JSONObject.fromObject(rs).toString();
//		}
		return "没有车辆运行状态";
	}
	
	@RequestMapping("track")
	public @ResponseBody String getTrack() {
		JSONArray array = vehicleMonitorService.queryTrack();
		
		return array.toString();
	}
	
	/**
	 * 查询车辆当前轨迹点
	 * @param vehicleId
	 * @return
	 */
	@RequestMapping(value="trip",method=RequestMethod.GET)
	public @ResponseBody String getTrip(@RequestParam("tripId")int tripId){
		return vehicleMonitorService.queryTripById(tripId);
	}
	
}
