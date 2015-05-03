package cn.com.gps169.server.handler.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.gps169.db.model.Terminal;
import cn.com.gps169.db.model.TerminalVehicle;
import cn.com.gps169.server.handler.IJt808Handler;
import cn.com.gps169.server.protocol.Jt808Message;
import cn.com.gps169.server.protocol.Jt808MessageHead;
import cn.com.gps169.server.protocol.impl.JT8100;
import cn.com.gps169.server.tool.JT808Constants;

/**
 * 终端注册处理器
 * @author tianfei
 *
 */
@Component("jt0100Handler")
public class JT0100Handler extends IJt808Handler {
	private transient Logger logger = LoggerFactory.getLogger(JT0100Handler.class);
	
	@Override
	public void handle(Jt808Message msg) {
		optResult = JT808Constants.TERMINAL_REGISTER_SUCCESS;
		logger.info("终端注册操作！"+msg.getBody().toString());
		String simNo = msg.getSimNo();
		Terminal tmnl = terminalCacheManager.getTerminalBySimNo(simNo);
		if(tmnl == null){
			logger.info("终端注册失败，终端不存在 ："+ simNo);
			optResult = JT808Constants.TERMINAL_REGISTER_TERMINAL_NOT_EXIST;
		} else {
			// 查询终端关系是否已经绑定
			TerminalVehicle tv = tmnlVehicleCacheManager.findCurBindRelationsByTerminalId(tmnl.getTerminalId());
			if(tv == null){
				logger.info("终端注册失败，终端、车辆没有注册："+tv.getTerminalId());
				optResult = JT808Constants.TERMINAL_REGISTER_VEHICLE_HAD_EXIST;
			} 
//			else {
				//这里不进行终端绑定处理，业务逻辑放在业务系统处理
//				JT0100 body = (JT0100) msg.getBody();
//				//查询 车辆信息
//				String licensePlate = body.getLicensePlate();
//				Vehicle vehicle = vehicleCacheManager.findVehicleByPlate(licensePlate);
//				if(vehicle != null){
//					//判断车辆是否已经注册
//					tv = tmnlVehicleCacheManager.findCurBindRelationsByVehicleId(vehicle.getVehicleId());
//					if(tv != null){
//						logger.info("终端注册失败，车辆已经注册："+vehicle.getLicensePlate());
//						optResult = JT808Constants.TERMINAL_REGISTER_VEHICLE_HAD_EXIST;
//					} else {
//						//终端注册,保存关联关系
//						tv = new TerminalVehicle();
//						tv.setTerminalId(tmnl.getTerminalId());
//						tv.setVehicleId(vehicle.getVehicleId());
//						tmnlVehicleCacheManager.addBindRelation(tv);
//						//保存终端绑定
//						tmnl.setBindTime(new Date());
//						terminalCacheManager.addOrUpdateTerminal(tmnl);
//						logger.info(String.format("车辆与终端绑定成功，终端IMEI：%s,车牌：%s，SIM：%s",tmnl.getImei(),vehicle.getLicensePlate(),simNo));
//					}
//				} else {
//					logger.info("终端注册失败，车辆不存在："+body.getLicensePlate());
//					optResult = JT808Constants.TERMINAL_REGISTER_VEHICLE_NOT_EXIST;
//				}
//			}
		}
		
		// 消息回复
		JT8100 body = new JT8100(msg.getHead().getFlowNo(),optResult,JT808Constants.AUTHENTICATION_CODE);
		Jt808MessageHead head = msg.getHead();
		head.setMessageId(0x8100);
		Jt808Message response = new Jt808Message(head,body);
		writeResponse(response);
	}
}
