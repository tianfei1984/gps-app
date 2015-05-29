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
	
    /**
     * 车辆行驶状态--行驶中
     */
    public static final String VEHICLE_RUNNING_STATUS_RUNNING = "running";
    
    /**
     * 车辆行驶状态--停止
     */
    public static final String VEHICLE_RUNNING_STATUS_STOP = "stop";

}
