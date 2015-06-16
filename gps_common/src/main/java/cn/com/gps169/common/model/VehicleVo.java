package cn.com.gps169.common.model;

import cn.com.gps169.db.model.Vehicle;

/**
 * 车辆 信息包装类
 * @author tianfei
 *
 */
public class VehicleVo extends Vehicle {
    
    private int userId;			//用户ID
    private String longitude; //经度
    private String latitude; //纬度
    private String address; //地址
    private float altitude; //海拔
    private float speed; //车辆速度
    private String lastUploadTime; //最新上传时间
    
    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public float getAltitude() {
		return altitude;
	}

	public void setAltitude(float altitude) {
		this.altitude = altitude;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public String getLastUploadTime() {
		return lastUploadTime;
	}

	public void setLastUploadTime(String lastUploadTime) {
		this.lastUploadTime = lastUploadTime;
	}
}
