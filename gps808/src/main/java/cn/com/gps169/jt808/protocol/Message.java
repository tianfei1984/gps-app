package cn.com.gps169.jt808.protocol;

import java.nio.ByteBuffer;

import cn.com.gps169.jt808.tool.Tools;

/**
 * JT808消息体
 * @author tianfei
 *
 */
public class Message {
	
	private MessageHead head;		//头消息体
	
	private MessageBody body;		//消息体
	
	public Message(){}
	
	public Message(MessageHead head,MessageBody body){
		this.head = head;
		this.body = body;
		head.setBodyLength((short)body.getLength());
	}
	
	public Message(ByteBuffer buff){
		this.head = new MessageHead(buff);
	}
	
	public String getMessageID(){
        return Tools.ToHexString4Short((short) head.getMessageId());
    }
	
	/**
	 * 查询 SIM卡号
	 * @return
	 */
	public String getSimNo(){
		return head.getSimNo();
	}
	
	public MessageHead getHead() {
		return head;
	}

	public void setHead(MessageHead head) {
		this.head = head;
	}

	public MessageBody getBody() {
		return body;
	}

	public void setBody(MessageBody body) {
		this.body = body;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return head.toString()+"  消息体内容："+ (body == null ? "" : body.toString());
	}
	
	public static void main(String[] args){
		System.out.println(Integer.toHexString(1 & 0xffff | 0x10000).substring(1));
	}
}
