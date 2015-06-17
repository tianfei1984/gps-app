package cn.com.gps169.jt808.proc.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.gps169.common.model.VehicleVo;
import cn.com.gps169.jt808.proc.Proc;
import cn.com.gps169.jt808.protocol.Message;
import cn.com.gps169.jt808.protocol.MessageHead;
import cn.com.gps169.jt808.protocol.impl.JT0100;
import cn.com.gps169.jt808.protocol.impl.JT8100;
import cn.com.gps169.jt808.tool.JT808Constants;

/**
 * 终端注册处理器
 * @author tianfei
 *
 */
@Component("p0100")
public class P0100 extends Proc {
	private transient Logger logger = LoggerFactory.getLogger(P0100.class);
	
	@Override
	public void proc(Message msg) {
		optResult = JT808Constants.TERMINAL_REGISTER_SUCCESS;
		logger.info("终端注册操作！"+msg.getBody().toString());
		String simNo = msg.getSimNo();
		VehicleVo vehicle = cacheManager.findVehicleBySim(simNo);
		if(vehicle == null){
			logger.info("终端注册失败，终端不存在 ："+ simNo);
			optResult = JT808Constants.TERMINAL_REGISTER_VEHICLE_NOT_EXIST;
		} else {
		    // 判断车辆是否已经缴费
		    if(vehicle.getFleeStatus() == JT808Constants.TERMINAL_FLEE_STATUS_DONE){
		        optResult = JT808Constants.TERMINAL_REGISTER_SUCCESS;
		        JT0100 msgBody = (JT0100) msg.getBody();
		        //更新终端ID
		        logger.info("终端注册成功,sim:"+simNo+" 注册消息："+msgBody.toString());
		    } else {
		        optResult = JT808Constants.TERMINAL_REGISTER_TERMINAL_NOT_EXIST;
		    }
		}
		// 消息回复
		JT8100 body = new JT8100(msg.getHead().getFlowNo(),optResult,JT808Constants.AUTHENTICATION_CODE);
		MessageHead head = msg.getHead();
		head.setMessageId(0x8100);
		Message response = new Message(head,body);
		writeResponse(response);
	}
}
