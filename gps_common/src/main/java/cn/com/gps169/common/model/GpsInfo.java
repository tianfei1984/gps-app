package cn.com.gps169.common.model;

import java.util.Date;

/**
 * GPS历史数据,保存GPS上传的实时定位信息，状态信息等
 * 
 */

public class GpsInfo {
	private int commandId;// 数据所来自的命令包的ID号
	private int vid;
	private int tid;
	private String simNo;// 车终端卡号
	private String plateNo;// 车牌号
	private Date sendTime;// 发送时间
	private double longitude;// 经度
	private double latitude;// 纬度
	private double speed;// 速度
	private String location;// 对经纬度的地理位置解析
	private int direction;// 方向,0～359，正北为0，顺时针
	private String alarmStatus;// 报警位状态
	private double mileage;// 里程
	private double fuel;// 油量
	private double recordSpeed;// 行驶记录仪速度
	private double altitude;// 海拔
	private boolean valid;// GPS的定位状态，false代表没有定位,被屏蔽或找不到卫星
	private String runStatus;// 车辆行驶状态: 停止,运行
	private String status;// 状态
	 
	public final int getCommandId() {
		return commandId;
	}

	public final void setCommandId(int value) {
		commandId = value;
	}
	public int getVid() {
		return vid;
	}

	public void setVid(int vid) {
		this.vid = vid;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}
	public final String getSimNo() {
		return simNo;
	}

	public final void setSimNo(String value) {
		simNo = value;
	}

	public final String getPlateNo() {
		return plateNo;
	}

	public final void setPlateNo(String value) {
		plateNo = value;
	}

	public final Date getSendTime() {
		return sendTime;
	}

	public final void setSendTime(Date value) {
		sendTime = value;
	}

	public final double getLongitude() {
		return longitude;
	}

	public final void setLongitude(double value) {
		longitude = value;
	}

	public final double getLatitude() {
		return latitude;
	}

	public final void setLatitude(double value) {
		latitude = value;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public final String getLocation() {
		return location;
	}

	public final void setLocation(String value) {
		location = value;
	}

	public final int getDirection() {
		return direction;
	}

	public final void setDirection(int value) {
		direction = value;
	}

	public final String getStatus() {
		return status;
	}

	public final void setStatus(String value) {
		status = value;
	}

	public final String getAlarmStatus() {
		return alarmStatus;
	}

	public final void setAlarmStatus(String value) {
		alarmStatus = value;
	}

	public final double getMileage() {
		return mileage;
	}

	public final void setMileage(double value) {
		mileage = value;
	}

	public double getFuel() {
		return fuel;
	}

	public void setFuel(double fuel) {
		this.fuel = fuel;
	}

	public double getRecordSpeed() {
		return recordSpeed;
	}

	public void setRecordSpeed(double recordSpeed) {
		this.recordSpeed = recordSpeed;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getRunStatus() {
		return runStatus;
	}

	public void setRunStatus(String runStatus) {
		this.runStatus = runStatus;
	}

}