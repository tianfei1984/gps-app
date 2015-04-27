package cn.com.gps169.server.handler.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.gps169.server.handler.IJt808Handler;
import cn.com.gps169.server.protocol.EMsgAck;
import cn.com.gps169.server.protocol.Jt808Message;
import cn.com.gps169.server.protocol.Jt808MessageHead;
import cn.com.gps169.server.protocol.impl.JT0102;
import cn.com.gps169.server.protocol.impl.JT8001;
import cn.com.gps169.server.tool.JT808Constants;

/**
 * 终端鉴权处理器
 * @author tianfei
 *
 */
@Component("jt0102Handler")
public class JT0102Handler extends IJt808Handler {
	private static Logger logger = LoggerFactory.getLogger(JT0102Handler.class);
	
	@Override
	public void handle(Jt808Message msg) {
		JT0102 body = (JT0102) msg.getBody();
		String code = body.getAuthorCode();
		//判断终端鉴权码是否正确 
		if(!JT808Constants.AUTHENTICATION_CODE.equals(code)){
			logger.error("终端鉴权失败，"+msg.getSimNo());
			optResult = EMsgAck.FAILURE.value();
		} else {
			//设置连接鉴权成功
			msg.getConn().setAuth(true);
		}
		//消息回复
		Jt808MessageHead resHead = msg.getHead();
		JT8001 rbody = new JT8001(resHead.getFlowNo(),msg.getHead().getMessageId(), optResult);
		resHead.setMessageId(0x8001);
		Jt808Message response = new Jt808Message(resHead,rbody);
		writeResponse(response);
	}
}
