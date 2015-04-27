package cn.com.gps169.server.protocol;

import java.nio.ByteBuffer;

import cn.com.gps169.server.tool.Tools;

/**
 * JT808消息头
 * @author tianfei
 *
 */
public class Jt808MessageHead {
	
	private int messageId;		//消息ID
	private short bodyLength;	// 消息体长度
	private String tephone;			//手机号
	private int flowNo;			//消息流水号
	private byte subpackage;		//分包，1：长消息； 0：无消息分装项
	private byte encryption;		//加密方式。0：不加密； 1：RSA加密算法
	private int packageTotal;		//包总数
	private int packageNo;		//包序号
	private ByteBuffer body;		//消息体
	
	public Jt808MessageHead(){}
	
	public Jt808MessageHead(ByteBuffer buff){
		//消息ID
		this.messageId = Tools.getUnsignedShort(buff);
		//解析消息属性
		decodeAttr(buff);
		// SIM卡号
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<6;i++){
			sb.append(String.format("%02X",Tools.getUnsignedByte(buff)));
		}
		this.tephone = sb.toString();
		this.flowNo = Tools.getUnsignedShort(buff);
		//判断是否长消息
		if(isSubPackage()){
			this.packageTotal = Tools.getUnsignedShort(buff);
			this.packageNo = Tools.getUnsignedShort(buff);
		}
		body = ByteBuffer.allocate(bodyLength);
		for(int i = 0;i < bodyLength; i++){
			body.put(buff.get());
		}
		body.flip();
	}
	
	public ByteBuffer getByteBuffer(){
		ByteBuffer buff = ByteBuffer.allocate(14);
		Tools.putUnsignedShort(buff, getMessageId());
		buff.putShort(encodeAtrr());
		buff.put(Tools.HexString2Bytes(tephone));
		Tools.putUnsignedShort(buff, flowNo);
		if(isSubPackage()){
			//TODO:分包处理
		}
		buff.flip();
		return buff;
	}
	
	/**
	 * 解析消息头属性
	 * @param buff
	 */
	private void decodeAttr(ByteBuffer buff){
		String str = String.format("%016d", Integer.parseInt(Integer.toBinaryString(Tools.getUnsignedShort(buff))));
		this.subpackage = Byte.valueOf(str.substring(2,3),2);
		this.encryption = Byte.valueOf(str.substring(3,6),2);
		this.bodyLength = Short.valueOf(str.substring(6),2);
	}
	
	/**
	 * 
	 * @return
	 */
	private short encodeAtrr(){
		StringBuffer sb = new StringBuffer("00");
		sb.append(String.format("%d", isSubPackage() ? 1 : 0)).append(String.format("%03d", encryption)).
			append(String.format("%010d", Integer.parseInt(Integer.toBinaryString(bodyLength))));
		
		return Short.parseShort(sb.toString(), 2);
	}
	
	/**
	 * 判断是否长消息
	 * @return
	 */
	public boolean isSubPackage(){
		return this.subpackage == 1;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMsgOffer(){
		return getHeadLength() + this.bodyLength;
	}
	
	/**
	 * 头消息体长度
	 * @return
	 */
	public int getHeadLength(){
		return 12 + (isSubPackage() ? 4 : 0);
	}
	
	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public short getBodyLength() {
		return bodyLength;
	}

	public void setBodyLength(short bodyLength) {
		this.bodyLength = bodyLength;
	}

	public String getTephone() {
		return tephone;
	}

	public void setTephone(String tephone) {
		this.tephone = tephone;
	}

	public int getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(int flowNo) {
		this.flowNo = flowNo;
	}

	public byte getSubpackage() {
		return subpackage;
	}

	public void setSubpackage(byte subpackage) {
		this.subpackage = subpackage;
	}

	public byte getEncryption() {
		return encryption;
	}

	public void setEncryption(byte encryption) {
		this.encryption = encryption;
	}

	public int getPackageTotal() {
		return packageTotal;
	}

	public void setPackageTotal(int packageTotal) {
		this.packageTotal = packageTotal;
	}

	public int getPackageNo() {
		return packageNo;
	}

	public void setPackageNo(int packageNo) {
		this.packageNo = packageNo;
	}

	public ByteBuffer getBody() {
		return body;
	}

	public void setBody(ByteBuffer body) {
		this.body = body;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("消息头：");
		sb.append("消息ID:").append("0X"+Tools.ToHexString4Short((short) messageId)).append("消息体长度：").append(getBodyLength()).
			append("终端手机号：").append(this.tephone).append("消息流水号：").append(this.flowNo);
		return sb.toString();
	}
}
