package cn.com.gps169.jt808.protocol.impl;

import java.nio.ByteBuffer;

import cn.com.gps169.jt808.protocol.MessageBody;
import cn.com.gps169.jt808.tool.Tools;

/**
 * 位置信息
 * @author tianfei
 *
 */
public class JT0200 extends MessageBody {
	
	private long latitude;			//纬度
	private long longitude;			//经度
	private int altitude;				//海拔
	private int speed;					//速度 
	private int direction;				//方向
	private String time;				//时间
	private long mileage;			//	里程
	private int fuel;				//油耗
	private int recorderSpeed;		//记录仪速度
	
	private String status;
	private byte accStauts;
	private byte position;
	private byte latStatus;
	private byte lonStatus;
	private byte businessStatus;
	private byte oilStatus;
	private byte elecStatus;
	private byte doorStatus;
	
	private String alarm;
	private byte sosAlarm;
	private byte overSpeedAlarm;
	private byte fatigueDriving;
	private byte GNSS;
	private byte tmnlUndervoltage;
	private byte tmnlPowerDown;
	private byte LCD;
	private byte TTS;
	private byte camera;
	private byte overTimeStop;
	private byte areaAlarm;
	private byte lineAlarm;
	private byte runOverTime;
	private byte yaw;
	private byte VSS;
	private byte oilAlarm;
	private byte ignitionAlarm;
	private byte displaceAlarm;
	
	

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] encodeBody() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void decodeBody(ByteBuffer buf) {
	    ByteBuffer buff = ByteBuffer.allocate(buf.limit());
	    for(int i = buf.limit();i >0; i--){ 
	        buff.put(buf.get());
	    }
	    buff.flip();
		parseAlarm(Tools.getUnsignedInt(buff));		//告警
		parseStatus(Tools.getUnsignedInt(buff));		//状态
		latitude = Tools.getUnsignedInt(buff);
		longitude = Tools.getUnsignedInt(buff);
		altitude = Tools.getUnsignedShort(buff);
		speed = Tools.getUnsignedShort(buff);
		direction = Tools.getUnsignedShort(buff);
		byte[] b = new byte[6];
		buff.get(b);
		StringBuffer currTime = new StringBuffer();
		currTime.append(String.format("20%02X",b[0])).append("-").append(String.format("%02X",b[1])).append("-")
		.append(String.format("%02X",b[2])).append(" ").append(String.format("%02X",b[3])).append(":")
		.append(String.format("%02X",b[4])).append(":").append(String.format("%02X",b[5]));
		time = currTime.toString();
		//附加信息解析
		while(buff.hasRemaining()){
			byte additionalId = buff.get();
			byte additionalLength = buff.get();
			switch(additionalId){
			case 0x01:
				//里程
				mileage = Tools.getUnsignedInt(buff);
				break;
			case 0x02:
				//油量
				fuel = Tools.getUnsignedShort(buff);
				break;
			case 0x03:
				//行驶记录仪速度
				recorderSpeed = Tools.getUnsignedShort(buff);
				break;
			}
		}
	}
	
	/**
	 * 解析告警状态
	 * @param status
	 */
	private void parseAlarm(long status){
		String str = Long.toBinaryString(status);
		StringBuffer temp = new StringBuffer(); 
		for(int i = 0;i < 32-str.length(); i++){
			temp.append("0");
		}
		temp.append(str);
		str = temp.reverse().toString();
		alarm = str;
		sosAlarm = Byte.parseByte(str.substring(0,1));
		overSpeedAlarm = Byte.parseByte(str.substring(1,2));
		fatigueDriving = Byte.parseByte(str.substring(2,3));
		GNSS = Byte.parseByte(str.substring(4,5));
		tmnlUndervoltage = Byte.parseByte(str.substring(7,8));
		tmnlPowerDown = Byte.parseByte(str.substring(8,9));
		LCD = Byte.parseByte(str.substring(9,10));
		TTS = Byte.parseByte(str.substring(10,11));
		camera = Byte.parseByte(str.substring(11,12));
		overTimeStop = Byte.parseByte(str.substring(19,20));
		areaAlarm = Byte.parseByte(str.substring(20,21));
		lineAlarm = Byte.parseByte(str.substring(21,22));
		yaw = Byte.parseByte(str.substring(23,24));
		VSS = Byte.parseByte(str.substring(24,25));
		oilAlarm = Byte.parseByte(str.substring(25,26));
		ignitionAlarm = Byte.parseByte(str.substring(27,28));
		displaceAlarm = Byte.parseByte(str.substring(28,29));
	}
	
	/**
	 * 解析车辆状态
	 * @param status
	 */
	private void parseStatus(long status){
		String str = Long.toBinaryString(status);
		StringBuffer temp = new StringBuffer(); 
		for(int i = 0;i < 32-str.length(); i++){
			temp.append("0");
		}
		temp.append(str);
		str = temp.reverse().toString();
		this.status = str;
		accStauts = Byte.parseByte(str.substring(0, 1));
		position = Byte.parseByte(str.substring(1,2));
		latStatus = Byte.parseByte(str.substring(2,3));
		lonStatus = Byte.parseByte(str.substring(4,5));
		businessStatus = Byte.parseByte(str.substring(6,7));
		oilStatus = Byte.parseByte(str.substring(10,11));
		elecStatus = Byte.parseByte(str.substring(11,12));
		doorStatus = Byte.parseByte(str.substring(12, 13));
	}
	

	public long getLatitude() {
		return latitude;
	}

	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}

	public long getLongitude() {
		return longitude;
	}

	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}

	public int getAltitude() {
		return altitude;
	}

	public void setAltitude(int altitude) {
		this.altitude = altitude;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAlarm() {
		return alarm;
	}

	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}

	public long getMileage() {
		return mileage;
	}

	public void setMileage(long mileage) {
		this.mileage = mileage;
	}

	public int getFuel() {
		return fuel;
	}

	public void setFuel(int fuel) {
		this.fuel = fuel;
	}

	public int getRecorderSpeed() {
		return recorderSpeed;
	}

	public void setRecorderSpeed(int recorderSpeed) {
		this.recorderSpeed = recorderSpeed;
	}

	@Override
	public String toString() {
		return String.format("经度【%s】 纬度【%s】 海拔【%d】 方向【%d】 时间【%s】 状态【%s】 告警状态【%s】", 
				latitude,longitude,altitude,direction,time,status,alarm);
	}
}
