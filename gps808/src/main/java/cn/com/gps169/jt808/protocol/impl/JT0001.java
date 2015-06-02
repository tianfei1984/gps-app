package cn.com.gps169.jt808.protocol.impl;

import java.nio.ByteBuffer;

import cn.com.gps169.jt808.protocol.EMsgAck;
import cn.com.gps169.jt808.protocol.MessageBody;
import cn.com.gps169.jt808.tool.Tools;

/**
 * 终端通用应答
 * @author tianfei
 *
 */
public class JT0001 extends MessageBody {
	
	private int flowId;		//平台流水号
	private int msgID;		//消息ID
	private byte result;		//结果：

	@Override
	public int getLength() {
		return 5;
	}

	@Override
	public byte[] encodeBody() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void decodeBody(ByteBuffer buff) {
		Tools.putUnsignedShort(buff, flowId);
		Tools.putUnsignedShort(buff, msgID);
		Tools.putUnsignedByte(buff, result);
	}

	public int getFlowId() {
		return flowId;
	}

	public void setFlowId(int flowId) {
		this.flowId = flowId;
	}

	public int getMsgID() {
		return msgID;
	}

	public void setMsgID(int msgID) {
		this.msgID = msgID;
	}

	public int getResult() {
		return result;
	}

	public void setResult(byte result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return String.format("终端通用应答：应答流水号【%d】,应答ID【%d】，结果【%s】",flowId,msgID,EMsgAck.instaceOf(result).desc());
	}

}
