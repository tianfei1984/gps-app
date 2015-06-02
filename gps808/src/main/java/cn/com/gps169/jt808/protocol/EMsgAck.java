package cn.com.gps169.jt808.protocol;

public enum EMsgAck {
	
	SUCESS((byte)0),
	FAILURE((byte)1),
	ERROR((byte)2),
	NOSUPORT((byte)3);

	private byte code;
	private EMsgAck(byte code){
		this.code = code;
	}
	
	public byte value(){
		return code;
	}
	
	public static EMsgAck instaceOf(byte code) {
		switch (code) {
		case 0:
			return SUCESS;
		case 1:
			return FAILURE;
		case 2:
			return ERROR;
		default:
			return NOSUPORT;
		}
	}
	
	public static String desc(){
		if(SUCESS.value() == 0){
			return "成功";
		}
		return "";
	}
}
