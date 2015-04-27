package cn.com.gps169.db.dao;

import cn.com.gps169.db.model.TerminalVehicle;
import cn.com.gps169.db.model.TerminalVehicleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TerminalVehicleMapper {
    int countByExample(TerminalVehicleExample example);

    int deleteByExample(TerminalVehicleExample example);

    int deleteByPrimaryKey(Integer terminalVehiclesId);

    int insert(TerminalVehicle record);

    int insertSelective(TerminalVehicle record);

    List<TerminalVehicle> selectByExample(TerminalVehicleExample example);

    TerminalVehicle selectByPrimaryKey(Integer terminalVehiclesId);

    int updateByExampleSelective(@Param("record") TerminalVehicle record, @Param("example") TerminalVehicleExample example);

    int updateByExample(@Param("record") TerminalVehicle record, @Param("example") TerminalVehicleExample example);

    int updateByPrimaryKeySelective(TerminalVehicle record);

    int updateByPrimaryKey(TerminalVehicle record);
}