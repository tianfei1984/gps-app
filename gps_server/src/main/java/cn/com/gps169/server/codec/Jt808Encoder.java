package cn.com.gps169.server.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import cn.com.gps169.server.protocol.Jt808Message;
import cn.com.gps169.server.tool.JT808Constants;

/**
 * JT808协议编译器
 * @author tianfei
 *
 */
public class Jt808Encoder implements ProtocolEncoder {

	@Override
	public void encode(IoSession session, Object message,ProtocolEncoderOutput out) throws Exception {
		Jt808Message msg = (Jt808Message) message;
		IoBuffer temp = IoBuffer.allocate(100).setAutoExpand(true);
		IoBuffer buff = IoBuffer.allocate(100).setAutoExpand(true);
		//组装消息体
		temp.put(msg.getHead().getByteBuffer());
		temp.put(msg.getBody().encodeBody());
		temp.flip();
		temp.mark();
		//校验码
		byte validCode = temp.get();
		while(temp.hasRemaining()){
			validCode ^= temp.get();
		}
		temp.put(validCode);
		temp.reset();
		//编译
		buff.put(JT808Constants.PROTOCOL_0x7E);
		while(temp.hasRemaining()){
			byte  b = temp.get();
			if(b == JT808Constants.PROTOCOL_0x7E){
				buff.put(JT808Constants.PROTOCOL_0x7D);
				buff.put(JT808Constants.PROTOCOL_0x02);
			} else if(b == JT808Constants.PROTOCOL_0x7D){
				buff.put(JT808Constants.PROTOCOL_0x7D);
				buff.put(JT808Constants.PROTOCOL_0x01);
			} else {
				buff.put(b);
			}
		}
		buff.put(JT808Constants.PROTOCOL_0x7E);
		buff.flip();
		//发送
		session.write(buff);
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
