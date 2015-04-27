package cn.com.gps169.db.model;

import java.math.BigDecimal;
import java.util.Date;

public class Refuel {
    private Integer refuelId;

    private Integer vehicleId;

    private BigDecimal refuelAmount;

    private BigDecimal fuelAmount;

    private BigDecimal mileage;

    private Date refuelDate;

    private String location;

    private Date created;

    public Integer getRefuelId() {
        return refuelId;
    }

    public void setRefuelId(Integer refuelId) {
        this.refuelId = refuelId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public BigDecimal getRefuelAmount() {
        return refuelAmount;
    }

    public void setRefuelAmount(BigDecimal refuelAmount) {
        this.refuelAmount = refuelAmount;
    }

    public BigDecimal getFuelAmount() {
        return fuelAmount;
    }

    public void setFuelAmount(BigDecimal fuelAmount) {
        this.fuelAmount = fuelAmount;
    }

    public BigDecimal getMileage() {
        return mileage;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }

    public Date getRefuelDate() {
        return refuelDate;
    }

    public void setRefuelDate(Date refuelDate) {
        this.refuelDate = refuelDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location == null ? null : location.trim();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}