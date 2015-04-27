package cn.com.gps169.server.handler.impl;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.gps169.common.model.GpsInfo;
import cn.com.gps169.common.tool.DateUtil;
import cn.com.gps169.db.model.Terminal;
import cn.com.gps169.db.model.TerminalVehicle;
import cn.com.gps169.db.model.Vehicle;
import cn.com.gps169.server.handler.GpsHandler;
import cn.com.gps169.server.handler.IJt808Handler;
import cn.com.gps169.server.handler.RunningStatusHandler;
import cn.com.gps169.server.protocol.Jt808Message;
import cn.com.gps169.server.protocol.impl.JT0200;
import cn.com.gps169.server.tool.JT808Constants;

/**
 * 位置信息汇报处理器
 * @author tianfei
 *
 */
@Component("jt0200Handler")
public class JT0200Handler extends IJt808Handler {
	
	private transient Logger Logger = LoggerFactory.getLogger(JT0200Handler.class);
	
	@Autowired
	private GpsHandler gpsHandler;
	
	@Autowired
	private RunningStatusHandler runningStatusHandler;

	@Override
	public void handle(Jt808Message msg) {
		JT0200 body = (JT0200) msg.getBody();
		GpsInfo gi = new GpsInfo();
		Date dt = DateUtil.stringToDatetime(body.getTime());
		if (dt == null) {
			Logger.error(String.format("sim卡:%s,车牌号为：%s,上传的定位包中含有无效的日期:%s，直接丢弃", gi.getSimNo(),
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
		Terminal tmnl = terminalCacheManager.getTerminalBySimNo(msg.getSimNo());
		TerminalVehicle tv = tmnlVehicleCacheManager.findCurBindRelationsByTerminalId(tmnl.getTerminalId());
		Vehicle vehicle = null;
		if(tv != null){
			vehicle = vehicleCacheManager.findVehicleById(tv.getVehicleId());
			if(vehicle != null){
				//设置车辆行驶状态
				gi.setVid(vehicle.getVehicleId());
				if(body.getSpeed() > 1){
					dataAcquireCacheManager.setRunningStatus(vehicle.getVehicleId(), JT808Constants.VEHICLE_RUNNING_STATUS_RUNNING);
					gi.setRunStatus(JT808Constants.VEHICLE_RUNNING_STATUS_RUNNING);
				} else {
					dataAcquireCacheManager.setRunningStatus(vehicle.getVehicleId(), JT808Constants.VEHICLE_RUNNING_STATUS_STOP);
					gi.setRunStatus(JT808Constants.VEHICLE_RUNNING_STATUS_STOP);
				}
			} else {
				Logger.error("车辆不存在 ！");
				return;
			}
		} else {
			Logger.error("终端未绑定车辆，SIM:"+ msg.getSimNo());
			return;
		}
		//组装GPS信息
		gi.setTid(tmnl.getTerminalId());
		gi.setSendTime(dt);
		gi.setPlateNo(vehicle.getLicensePlate());
		gi.setSimNo(msg.getSimNo());
		gi.setLatitude(0.000001 * body.getLatitude());
		gi.setLongitude(0.000001 * body.getLongitude());
		gi.setSpeed(0.1 * body.getSpeed());
		gi.setDirection(body.getDirection());
		gi.setStatus(body.getStatus());
		gi.setAlarmStatus(body.getAlarm());
		gi.setMileage(0.1 * body.getMileage());
		gi.setFuel(0.1 * body.getFuel());
		gi.setRecordSpeed(0.1 * body.getRecorderSpeed());
		gi.setAltitude(body.getAltitude());
		gi.setStatus(body.getStatus());
		//位置信息处理
		gpsHandler.addGps(gi);
		//车辆运行状态处理
		runningStatusHandler.processData(gi);
		//TODO:待处理告警
	}
}
