package cn.com.gps169.common.mongo;

import java.util.Date;
import java.util.List;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity(value = "gps_trip", noClassnameStored = true)
public class GpsTrip {
    @Id
    private long id;
    private String imsi;
	private int tid;
    private int vid;
	@Embedded
    private List<GpsRecord> gps;
    @Embedded
    private List<WayPoints> waypnts;
    private Date stime;
    private Date etime;
    private int recvDay;
    private float dura;
    private float dist;
    private float fuel;
    private float f100km;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public List<GpsRecord> getGps() {
        return gps;
    }

    public void setGps(List<GpsRecord> gps) {
        this.gps = gps;
    }

    public List<WayPoints> getWaypnts() {
        return waypnts;
    }

    public void setWaypnts(List<WayPoints> waypnts) {
        this.waypnts = waypnts;
    }

    public Date getStime() {
        return stime;
    }

    public void setStime(Date stime) {
        this.stime = stime;
    }

    public Date getEtime() {
        return etime;
    }

    public void setEtime(Date etime) {
        this.etime = etime;
    }

    public int getRecvDay() {
        return recvDay;
    }

    public void setRecvDay(int recvDay) {
        this.recvDay = recvDay;
    }

    public float getDura() {
        return dura;
    }

    public void setDura(float dura) {
        this.dura = dura;
    }

    public float getDist() {
        return dist;
    }

    public void setDist(float dist) {
        this.dist = dist;
    }

    public float getFuel() {
        return fuel;
    }

    public void setFuel(float fuel) {
        this.fuel = fuel;
    }

    public float getF100km() {
        return f100km;
    }

    public void setF100km(float f100km) {
        this.f100km = f100km;
    }
    public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getVid() {
		return vid;
	}

	public void setVid(int vid) {
		this.vid = vid;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
}
