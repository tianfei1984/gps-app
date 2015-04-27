package cn.com.gps169.common.model;

import java.util.Date;

/**
 * 车辆运行状态
 * @author tianfei
 *
 */
public class RunningState {
	
	private int commandId;// 数据所来自的命令包的ID号
	private int vid;
	private int tid;
	private String simNo;// 车终端卡号
	private String plateNo;// 车牌号
	private Date sendTime;// 发送时间
	private double longitude;// 经度
	private double latitude;// 纬度
	private double velocity;// 速度
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
	private Date receivedTime;
	
	public RunningState(){}
	
	public RunningState(GpsInfo g){
		this.alarmStatus = g.getAlarmStatus();
		this.altitude = g.getAltitude();
		this.commandId = g.getCommandId();
		this.direction = g.getDirection();
		this.fuel = g.getFuel();
		this.latitude = g.getLatitude();
		this.location = g.getLocation();
		this.longitude = g.getLongitude();
		this.mileage = g.getMileage();
		this.plateNo = g.getPlateNo();
		this.receivedTime = g.getSendTime();
		this.recordSpeed = g.getRecordSpeed();
		this.runStatus = g.getRunStatus();
		this.simNo = g.getSimNo();
		this.tid = g.getTid();
		this.vid = g.getVid();
	}

	public Date getReceivedTime() {
		return receivedTime;
	}

	public void setReceivedTime(Date receivedTime) {
		this.receivedTime = receivedTime;
	}

	public int getCommandId() {
		return commandId;
	}

	public void setCommandId(int commandId) {
		this.commandId = commandId;
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

	public String getSimNo() {
		return simNo;
	}

	public void setSimNo(String simNo) {
		this.simNo = simNo;
	}

	public String getPlateNo() {
		return plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public String getAlarmStatus() {
		return alarmStatus;
	}

	public void setAlarmStatus(String alarmStatus) {
		this.alarmStatus = alarmStatus;
	}

	public double getMileage() {
		return mileage;
	}

	public void setMileage(double mileage) {
		this.mileage = mileage;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
