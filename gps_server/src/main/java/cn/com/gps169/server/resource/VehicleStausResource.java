package cn.com.gps169.server.resource;

import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.gps169.common.cache.IDataAcquireCacheManager;
import cn.com.gps169.common.cache.IRunningStatusCacheManager;
import cn.com.gps169.common.cache.ITerminalCacheManager;
import cn.com.gps169.common.cache.ITmnlVehiCacheManager;
import cn.com.gps169.common.cache.ITripCacheManager;
import cn.com.gps169.common.cache.ICacheManager;
import cn.com.gps169.common.model.RunningState;
import cn.com.gps169.common.tool.DateUtil;
import cn.com.gps169.db.dao.TripMapper;
import cn.com.gps169.db.model.Trip;
import cn.com.gps169.db.model.TripExample;
import cn.com.gps169.server.job.GenerateDailyStatJob;
import cn.com.gps169.server.job.GenerateTripJob;

/**
 * 车辆状态查询接口
 * @author tianfei
 *
 */
@Controller
@RequestMapping("vstatus")
public class VehicleStausResource {
	
	@Autowired
	private IDataAcquireCacheManager dataAcquireCacheManager;
	
	@Autowired
	private IRunningStatusCacheManager runningStatusCacheManager;
	
	@Autowired
	private ITripCacheManager tripCacheManager;
	
	@Autowired
	private GenerateDailyStatJob generateDailyStatJob;
	
	@Autowired
	private GenerateTripJob generateTripJob;
	
	@Autowired
	private ITerminalCacheManager terminalCacheManager;
	
	@Autowired
	private ICacheManager vehicleCacheManager;
	
	@Autowired
	private ITmnlVehiCacheManager tmnlVehCacheManager;
	
	@Autowired
	private TripMapper tripMapper;
	
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
	 * 查询车辆当前位置信息
	 * @param vehicleId
	 * @return
	 */
	@RequestMapping(value = "location",method=RequestMethod.GET)
	public @ResponseBody String getVehicleGps(@RequestParam("vid")int vehicleId,@RequestParam("updated") long updated){
		JSONObject json = dataAcquireCacheManager.getGps(vehicleId);
		System.out.println(".");
		if(json != null && json.optLong("updated") > updated){
		    JSONObject j = new JSONObject();
		    j.put("longitude", json.get("longitude"));
		    j.put("latitude", json.get("latitude"));
		    j.put("speed", json.get("speed"));
		    j.put("updated", json.get("updated"));
		    json.put("position", j);
			return json.toString();
		}
		return "{}";
	}
	
	/**
	 * 查询所有车辆位置信息
	 * @return
	 */
	@RequestMapping("allVehicles")
	public @ResponseBody String getAllGps(){
	    List<Integer> list = vehicleCacheManager.findAllVehicleIds();
	    JSONArray array = new JSONArray();
	    for(int vId : list){
	        JSONObject json = dataAcquireCacheManager.getGps(vId);
	        if(json != null){
	            array.add(json);
	        }
	    }
	    
	    return array.toString();
	}
	
	/**
	 * 查询车辆当前运行状态
	 * @param vehicleId
	 * @return
	 */
	@RequestMapping(value="rstatus",method=RequestMethod.GET)
	public @ResponseBody String getRunningSatus(@RequestParam("vid")int vehicleId) { 
		Date occurTime = DateUtil.addDate(DateUtil.formatDate(new Date()), 0);
		RunningState rs = runningStatusCacheManager.findLatestRunningState(vehicleId, occurTime);
		if(rs != null){
			return JSONObject.fromObject(rs).toString();
		}
		return "没有车辆运行状态";
	}
	
	/**
	 * 查询车辆当前轨迹点
	 * @param vehicleId
	 * @return
	 */
	@RequestMapping(value="trip",method=RequestMethod.GET)
	public @ResponseBody String getTrip(@RequestParam("vid")int vehicleId){
	    TripExample example = new TripExample();
	    example.or().andVehicleIdEqualTo(vehicleId);
	    List<Trip> list = tripMapper.selectByExampleWithBLOBs(example);
	    
		if(list != null && !list.isEmpty()){
			return list.get(0).getGps();
		}
		return "没有轨迹点";
	}
	
	/**
	 * 生成日统计
	 * @return
	 */
	@RequestMapping(value="dailyJob",method=RequestMethod.GET)
	public @ResponseBody String generateDailyStatJob(){
		generateDailyStatJob.execute();
		return "success";
	}
	
	/**
	 * 生成日行程
	 * @return
	 */
	@RequestMapping(value="tripJob",method=RequestMethod.GET)
	public @ResponseBody String generateTripJob(){
		generateTripJob.execute();
		
		return "success";
	}
	
	/**
	 * 刷新缓存信息
	 * @return
	 */
	@RequestMapping(value="refreshCache",method=RequestMethod.GET)
	public @ResponseBody String refreshCache(){
		terminalCacheManager.initCache();
		vehicleCacheManager.initCache();
		tmnlVehCacheManager.initCache();
		
		return "success";
	}
	
	
}
