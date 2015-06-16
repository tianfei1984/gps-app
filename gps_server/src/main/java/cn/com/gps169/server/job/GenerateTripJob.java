package cn.com.gps169.server.job;

import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.gps169.common.cache.ITripCacheManager;
import cn.com.gps169.common.cache.ICacheManager;
import cn.com.gps169.common.tool.DateUtil;
import cn.com.gps169.db.dao.TripMapper;
import cn.com.gps169.db.model.Trip;
import cn.com.gps169.db.model.TripExample;

/**
 * 生成车辆轨迹任务
 * 
 * @author tianfei
 *
 */
@Component("generateTripJob")
public class GenerateTripJob {

	private static Logger logger = LoggerFactory.getLogger(GenerateTripJob.class);
	
	@Autowired
	private ITripCacheManager tripCacheManager;
	
	@Autowired
	private TripMapper tripMapper;
	
	@Autowired
	private ICacheManager vehicleCacheManager;

	/**
	 * 执行轨迹生成
	 */
	public void execute() {
		Date occurTime = DateUtil.addDate(DateUtil.formatDate(new Date()), -0);
		int occurDay = Integer.parseInt(DateUtil.DATEFORMATER().format(occurTime));
		List<Integer> list = vehicleCacheManager.findAllVehicleIds();
		for(Integer vehicleId : list){
			logger.info(String.format("生成车辆【%d】【%d】行驶轨迹开始",vehicleId,occurDay));
			generateGpsTrip(occurTime, occurDay, vehicleId);
			logger.info(String.format("生成车辆【%d】【%d】行驶轨迹结束",vehicleId,occurDay));
		}
	}

	/*
	 * 生成指定车辆在指定时间段内的行驶轨迹
	 */
	private void generateGpsTrip(Date occurTime, int occurDay,int vehicleId) {
		String strTrip;
		try {
			JSONObject json = tripCacheManager.getGpsTrip(vehicleId, occurDay);
			strTrip = json.optString("trip");
		} catch (Exception e) {
			logger.error("生成轨迹失败，"+e.getMessage());
			return;
		}

		if (StringUtils.isBlank(strTrip)) {
			logger.info(String.format("redis中没有相应的轨迹数据: vid=%d，recvDay=%d", vehicleId, occurDay));
			return;
		}

		JSONArray array = JSONArray.fromObject("["
				+ strTrip.substring(0, strTrip.length() - 1) + "]");
		if (array != null && array.size() > 0) {
			Trip gpsTrip = new Trip();
			gpsTrip.setVehicleId(vehicleId);
			gpsTrip.setGps(array.toString());
			gpsTrip.setRecday(occurDay);
			gpsTrip.setCreated(new Date());
			try {
				//删除当天已经存在轨迹
				TripExample example = new TripExample();
				example.or().andRecdayEqualTo(occurDay);
				tripMapper.deleteByExample(example);
				//保存轨迹信息
				tripMapper.insertSelective(gpsTrip);
				//tripCacheManager.deleteGpsTrip(vehicleId,occurDay);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

}
