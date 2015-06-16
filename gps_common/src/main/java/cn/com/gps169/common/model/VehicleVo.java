package cn.com.gps169.common.model;

import cn.com.gps169.db.model.Vehicle;

/**
 * 车辆 信息包装类
 * @author tianfei
 *
 */
public class VehicleVo extends Vehicle {
    
    private int userId;
    
    

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

}
