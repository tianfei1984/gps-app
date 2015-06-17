package cn.com.gps169.common.model;

/**
 * GPS历史数据,保存GPS上传的实时定位信息，状态信息等
 * 
 */

public class GpsInfo {
    private String simNo;   //SIM
    private String plateNo; //车牌号
	private double longitude;// 经度
	private double latitude;// 纬度
	private double speed;// 速度
	private String location;// 对经纬度的地理位置解析
	private int direction;// 方向,0～359，正北为0，顺时针
	private String alarmStatus;// 报警位状态
	private double mileage;// 里程
	private double oilNum;// 油量
	private double altitude;// 海拔
	private String status;// 状态
	private String sendTime;// 发送时间
	 
	/**
     * @return the simNo
     */
    public String getSimNo() {
        return simNo;
    }

    /**
     * @param simNo the simNo to set
     */
    public void setSimNo(String simNo) {
        this.simNo = simNo;
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

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

    /**
     * @return the oilNum
     */
    public double getOilNum() {
        return oilNum;
    }

    /**
     * @param oilNum the oilNum to set
     */
    public void setOilNum(double oilNum) {
        this.oilNum = oilNum;
    }

    /**
     * @return the sendTime
     */
    public String getSendTime() {
        return sendTime;
    }

    /**
     * @param sendTime the sendTime to set
     */
    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    /**
     * @return the plateNo
     */
    public String getPlateNo() {
        return plateNo;
    }

    /**
     * @param plateNo the plateNo to set
     */
    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }
}