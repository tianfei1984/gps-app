package cn.com.gps169.db.model;

public class TerminalVehicle {
    private Integer terminalVehiclesId;

    private Integer terminalId;

    private Integer vehicleId;

    public Integer getTerminalVehiclesId() {
        return terminalVehiclesId;
    }

    public void setTerminalVehiclesId(Integer terminalVehiclesId) {
        this.terminalVehiclesId = terminalVehiclesId;
    }

    public Integer getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Integer terminalId) {
        this.terminalId = terminalId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }
}