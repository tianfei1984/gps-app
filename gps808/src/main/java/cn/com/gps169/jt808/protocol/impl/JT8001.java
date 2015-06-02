package cn.com.gps169.jt808.protocol.impl;

import java.nio.ByteBuffer;

import cn.com.gps169.jt808.protocol.EMsgAck;
import cn.com.gps169.jt808.protocol.MessageBody;
import cn.com.gps169.jt808.tool.Tools;

/**
 * 平台通用应答
 * @author tianfei
 *
 */
public class JT8001 extends MessageBody {
	
	private int ackFlowId;		//应答流水号
	private int ackMessageId;		//应答消息ID
	private byte result;			// 结果
	
	public JT8001(int ackFlowId,int ackMessageId,byte result){
		this.ackFlowId = ackFlowId;
		this.ackMessageId = ackMessageId;
		this.result = result;
	}

	@Override
	public int getLength() {
		return 5;
	}

	@Override
	public byte[] encodeBody() {
		ByteBuffer buff = ByteBuffer.allocate(getLength());
		Tools.putUnsignedShort(buff, ackFlowId);
		Tools.putUnsignedShort(buff, ackMessageId);
		buff.put(result);
		
		return buff.array();
	}

	@Override
	public void decodeBody(ByteBuffer buff) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return String.format("平台通用应答：应答流水号【%d】,应答消息ID【%d】,结果【%s】",ackFlowId,ackMessageId,EMsgAck.instaceOf(result).desc());
	}
}
