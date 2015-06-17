package cn.com.gps169.jt808.tool;

public class JT808Constants {
	
	public static final byte PROTOCOL_0x7E = 0x7e;
	public static final byte PROTOCOL_0x7D = 0x7d;
	public static final byte PROTOCOL_0x01 = 0x01;
	public static final byte PROTOCOL_0x02 = 0x02;
	
    /**
     * 终端注册--成功
     */
    public static final byte TERMINAL_REGISTER_SUCCESS = 0;
    /**
     * 终端注册--车辆已注册
     */
    public static final byte TERMINAL_REGISTER_VEHICLE_HAD_EXIST = 1;
    /**
     * 终端注册--数据库中无该车辆
     */
    public static final byte TERMINAL_REGISTER_VEHICLE_NOT_EXIST = 2;
    /**
     * 终端注册--终端已注册
     */
    public static final byte TERMINAL_REGISTER_TERMINAL_HAD_EXIST = 3;
    /**
     * 终端注册--数据库中无该终端
     */
    public static final byte TERMINAL_REGISTER_TERMINAL_NOT_EXIST = 4;
    
	//终端鉴权码
	public static final String AUTHENTICATION_CODE = "1234567890A";
    
    //////////终端状态///////
    /**
     * 缴费状态：待交费
     */
    public static final byte TERMINAL_FLEE_STATUS_WAIT = 1;
    /**
     * 缴费状态：已缴费
     */
    public static final byte TERMINAL_FLEE_STATUS_DONE = 2;
    /**
     * 缴费状态:欠费
     */
    public static final byte TERMINAL_FLEE_STATUS_ARREARAGE = 3;
    
    /**
     * 车辆终端在线状态:在线
     */
    public static final byte VEHICLE_TERMINAL_STATUS_ONLINE = 1;
    /**
     * 车辆终端在线状态：离线
     */
    public static final byte VEHICLE_TERMINAL_ONLINE_OFFLINE = 2;
    /**
     * 车辆运行状态：行程中
     */
    public static final byte VEHICLE_RUNNING_STATUS_MOVING = 1;
    /**
     * 车辆 运行状态：停止
     */
    public static final byte VEHICLE_RUNNING_STATUS_STOP = 2;
    
}
