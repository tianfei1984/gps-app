package cn.com.gps169.jt808.protocol.impl;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import cn.com.gps169.jt808.protocol.MessageBody;
import cn.com.gps169.jt808.tool.JT808Constants;
import cn.com.gps169.jt808.tool.Tools;

/**
 * 终端注册应答
 * @author tianfei
 *
 */
public class JT8100 extends MessageBody {
	
	private int ackFlowId;
	private byte result;
	private String authCode;
	
	public JT8100(int ackFlowId,byte result,String authCode){
		this.ackFlowId = ackFlowId;
		this.result = result;
		this.authCode = authCode;
	}

	@Override
	public int getLength() {
		if(result == JT808Constants.TERMINAL_REGISTER_SUCCESS){
			return 3 + this.authCode.getBytes().length;
		} 
		
		return 3;
	}

	@Override
	public byte[] encodeBody() {
		ByteBuffer buff = ByteBuffer.allocate(getLength());
		Tools.putUnsignedShort(buff, ackFlowId);
		buff.put(result);
		if(result == JT808Constants.TERMINAL_REGISTER_SUCCESS){
			buff.put(authCode.getBytes(Charset.forName("GBK")));
		}
		buff.flip();
		
		return buff.array();
	}

	@Override
	public void decodeBody(ByteBuffer buff) {
	}
	
}
