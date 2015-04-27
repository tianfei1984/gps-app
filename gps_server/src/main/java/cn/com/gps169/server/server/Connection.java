package cn.com.gps169.server.server;

import java.util.Date;

public class Connection {

	private long sessionId;
	// 终端Sim卡号
	private String simNo;
	// 车牌号
	private String plateNo;
	// 连接时间
	private Date createDate;
	// 最新在线时间
	private Date onlineDate;
	// 收到的包的数量
	private int packageNum;
	// 定位包数量
	private int positionPackageNum;
	// 断开次数
	private int disconnectTimes;
	// 错误包数
	private int errorPacketNum;
	// 是否已连接
	private boolean connected;
	// 终端ID
	private int terminalId;
	// 是否鉴权成功
	private boolean isAuth = false;
	
	public Connection(String simNo,long sessionId){
		this.simNo = simNo;
		this.sessionId = sessionId;
	}
	
	public long getSessionId() {
		return sessionId;
	}
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
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
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getOnlineDate() {
		return onlineDate;
	}
	public void setOnlineDate(Date onlineDate) {
		this.onlineDate = onlineDate;
	}
	public int getPackageNum() {
		return packageNum;
	}
	public void setPackageNum(int packageNum) {
		this.packageNum = packageNum;
	}
	public int getPositionPackageNum() {
		return positionPackageNum;
	}
	public void setPositionPackageNum(int positionPackageNum) {
		this.positionPackageNum = positionPackageNum;
	}
	public int getDisconnectTimes() {
		return disconnectTimes;
	}
	public void setDisconnectTimes(int disconnectTimes) {
		this.disconnectTimes = disconnectTimes;
	}
	public int getErrorPacketNum() {
		return errorPacketNum;
	}
	public void setErrorPacketNum(int errorPacketNum) {
		this.errorPacketNum = errorPacketNum;
	}
	public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public int getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(int terminalId) {
		this.terminalId = terminalId;
	}

	public boolean isAuth() {
		return isAuth;
	}

	public void setAuth(boolean isAuth) {
		this.isAuth = isAuth;
	}
}
