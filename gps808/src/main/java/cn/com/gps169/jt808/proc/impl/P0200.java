package cn.com.gps169.jt808.proc.impl;

import java.util.Calendar;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import cn.com.gps169.common.cache.ICacheManager;
import cn.com.gps169.common.model.GpsInfo;
import cn.com.gps169.common.model.VehicleVo;
import cn.com.gps169.common.tool.DateUtil;
import cn.com.gps169.jt808.proc.Proc;
import cn.com.gps169.jt808.protocol.Message;
import cn.com.gps169.jt808.protocol.impl.JT0200;
import cn.com.gps169.jt808.server.GpsServer;
import cn.com.gps169.jt808.tool.JT808Constants;

/**
 * 位置信息汇报处理器
 * @author tianfei
 *
 */
@Component("p0200")
public class P0200 extends Proc {
	
	private transient Logger Logger = LoggerFactory.getLogger(P0200.class);
	
	@Autowired
	private GpsServer gpsServer;
	
	@Autowired
	private ICacheManager cacheManager;

	@Override
	public void proc(Message msg) {
		JT0200 body = (JT0200) msg.getBody();
		Date dt = DateUtil.stringToDatetime(body.getTime());
		if (body.getTime() == null) {
			Logger.error(String.format("sim卡:%s,车牌号为：%s,上传的定位包中含有无效的日期:%s，直接丢弃", msg.getSimNo(),
					msg.getSimNo() ,body.getTime()));
			return;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		// 发送的旧数据,无效乱数据
		if (c.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR)) {
			Logger.info(String.format("位置包数据无效:发送时间:%s,直接丢弃该数据包", DateUtil.TIMEFORMATER1().format(dt)));
			return;
		}
		if (body.getLatitude() <= 0 || body.getLongitude() <= 0) {
			Logger.info(String.format("经纬度值为零,直接丢弃该位置数据包 :发送时间:%s", DateUtil.TIMEFORMATER1().format(dt)));
			return;
		}
		VehicleVo vehicleVo = cacheManager.findVehicleBySim(msg.getSimNo());
		if(vehicleVo != null){
			//设置车辆行驶状态
			if(body.getSpeed() > 1 && vehicleVo.getMovingStatus() == JT808Constants.VEHICLE_RUNNING_STATUS_STOP){
			    vehicleVo.setMovingStatus(JT808Constants.VEHICLE_RUNNING_STATUS_MOVING);
			    cacheManager.updateVehicle(vehicleVo);
			} else if(vehicleVo.getMovingStatus() == JT808Constants.VEHICLE_RUNNING_STATUS_MOVING) {
			    vehicleVo.setMovingStatus(JT808Constants.VEHICLE_RUNNING_STATUS_STOP);
			    cacheManager.updateVehicle(vehicleVo);
			}
		} else {
			Logger.error("车辆不存在 ！");
			return;
		}
		//组装GPS信息
		GpsInfo gps = new GpsInfo();
		gps.setSendTime(body.getTime());
		gps.setPlateNo(vehicleVo.getPlateNo());
		gps.setSimNo(msg.getSimNo());
		gps.setLatitude(0.000001 * body.getLatitude());
		gps.setLongitude(0.000001 * body.getLongitude());
		gps.setSpeed(0.1 * body.getSpeed());
		gps.setDirection(body.getDirection());
		gps.setStatus(body.getStatus());
		gps.setAlarmStatus(body.getAlarm());
		gps.setMileage(0.1 * body.getMileage());
		gps.setOilNum(0.1 * body.getFuel());
		gps.setAltitude(body.getAltitude());
		gps.setStatus(body.getStatus());
		//位置信息处理
		gpsServer.addGps(gps);
	}
}
