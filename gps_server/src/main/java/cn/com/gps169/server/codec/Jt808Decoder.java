package cn.com.gps169.server.codec;

import java.nio.ByteBuffer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.gps169.server.protocol.Jt808Message;
import cn.com.gps169.server.protocol.Jt808MessageBody;
import cn.com.gps169.server.protocol.Jt808MessageHead;
import cn.com.gps169.server.tool.JT808Constants;

/**
 * jt808协议解析器
 * @author tianfei
 *
 */
public class Jt808Decoder implements  ProtocolDecoder {
	
	private transient static Logger logger = LoggerFactory.getLogger(Jt808Decoder.class); 

	@Override
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
			throws Exception {
		while(in.hasRemaining()){
			//报文信息解析
			byte[] b = in.array();
			int length = in.limit();
			in.position(length);
			if(b[0] != JT808Constants.PROTOCOL_0x7E || b[length-1] != JT808Constants.PROTOCOL_0x7E){
				logger.error("数据格式错误！数据内容："+ b);
				return;
			}
			ByteBuffer buff = ByteBuffer.allocate(length);
			for(int i = 1;i<length-1;i++){
				if(b[i] == 0x7d){
					++i;
					if(b[i] == JT808Constants.PROTOCOL_0x02){
						buff.put(JT808Constants.PROTOCOL_0x7E);
					} else if(b[i] == JT808Constants.PROTOCOL_0x01){
						buff.put(JT808Constants.PROTOCOL_0x7D);
					}
					continue;
				}
				buff.put(b[i]);
			}
			buff.flip();	//反转
			Jt808Message msg = new Jt808Message(buff.duplicate());
			Jt808MessageHead head = msg.getHead();
			byte validCode = buff.get();
			for(int i = 1; i < head.getMsgOffer();i++){
				validCode ^= buff.get();
			}
			//判断校验码是否正确
			if(validCode != buff.get()){
				logger.error("校码码错误！");
				return;
			}
			// 判断是否分包消息
			if(head.isSubPackage()){
				//TODO:分包消息处理
				return;
			}
			try{
				if(head.getMessageId() != 0x0002){
					// 消息信息
					msg.setBody(parseBody(msg));
				} else {
					//终端心跳
					msg.setBody(null);
				}
				out.write(msg);
			} catch(Exception e){
				logger.error("异常错误！错误信息："+e.getMessage());
			} finally {
				buff.clear();
			}
		}
	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	private Jt808MessageBody parseBody(Jt808Message msg) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		Class<Jt808MessageBody> bodyClass = (Class<Jt808MessageBody>) Class.forName("cn.com.gps169.server.protocol.impl.JT"+msg.getMessageID());
		Jt808MessageBody body = bodyClass.newInstance();
		body.decodeBody(msg.getHead().getBody());
		
		return body;
	}

}
