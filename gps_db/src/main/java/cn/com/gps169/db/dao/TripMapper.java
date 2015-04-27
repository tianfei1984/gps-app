package cn.com.gps169.db.dao;

import cn.com.gps169.db.model.Trip;
import cn.com.gps169.db.model.TripExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TripMapper {
    int countByExample(TripExample example);

    int deleteByExample(TripExample example);

    int deleteByPrimaryKey(Integer tripId);

    int insert(Trip record);

    int insertSelective(Trip record);

    List<Trip> selectByExampleWithBLOBs(TripExample example);

    List<Trip> selectByExample(TripExample example);

    Trip selectByPrimaryKey(Integer tripId);

    int updateByExampleSelective(@Param("record") Trip record, @Param("example") TripExample example);

    int updateByExampleWithBLOBs(@Param("record") Trip record, @Param("example") TripExample example);

    int updateByExample(@Param("record") Trip record, @Param("example") TripExample example);

    int updateByPrimaryKeySelective(Trip record);

    int updateByPrimaryKeyWithBLOBs(Trip record);

    int updateByPrimaryKey(Trip record);
}