package cn.com.gps169.common.mongo;

import java.util.Date;

import com.google.code.morphia.annotations.Entity;

@Entity(noClassnameStored = true)
public class WayPoints {
    private Date time;

    private String address;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
