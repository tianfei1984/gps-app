package cn.com.gps169.db.model;

import java.math.BigDecimal;
import java.util.Date;

public class DailyStat {
    private Integer dailyStatId;

    private Integer vehicleId;

    private Integer occurDate;

    private BigDecimal fuelAmount;

    private BigDecimal mileage;

    private BigDecimal refuel;

    private BigDecimal fuelIncount;

    private BigDecimal mileageIncount;

    private BigDecimal fuelPer100km;

    private BigDecimal feePer100km;

    private Date created;

    public Integer getDailyStatId() {
        return dailyStatId;
    }

    public void setDailyStatId(Integer dailyStatId) {
        this.dailyStatId = dailyStatId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getOccurDate() {
        return occurDate;
    }

    public void setOccurDate(Integer occurDate) {
        this.occurDate = occurDate;
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

    public BigDecimal getRefuel() {
        return refuel;
    }

    public void setRefuel(BigDecimal refuel) {
        this.refuel = refuel;
    }

    public BigDecimal getFuelIncount() {
        return fuelIncount;
    }

    public void setFuelIncount(BigDecimal fuelIncount) {
        this.fuelIncount = fuelIncount;
    }

    public BigDecimal getMileageIncount() {
        return mileageIncount;
    }

    public void setMileageIncount(BigDecimal mileageIncount) {
        this.mileageIncount = mileageIncount;
    }

    public BigDecimal getFuelPer100km() {
        return fuelPer100km;
    }

    public void setFuelPer100km(BigDecimal fuelPer100km) {
        this.fuelPer100km = fuelPer100km;
    }

    public BigDecimal getFeePer100km() {
        return feePer100km;
    }

    public void setFeePer100km(BigDecimal feePer100km) {
        this.feePer100km = feePer100km;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}