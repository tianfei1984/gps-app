package cn.com.gps169.common.cache;

import java.util.Date;
import java.util.LinkedList;

import cn.com.gps169.common.model.RunningState;


/**
 * 车辆运行状态缓存
 * @author tianfei
 *
 */
public interface IRunningStatusCacheManager {
	
	/**
	 * 查询车辆最新运行状态
	 * @param vehicleId
	 * @return
	 */
	RunningState findLatestRunningState(int vehicleId,Date date);
	
	/**
	 * 保存车辆状态
	 * @param s
	 */
	void saveRunningState(RunningState s) ;
	
	/**
	 * 按时间查询车辆运行集合
	 * @param vehicleId
	 * @param date
	 * @return
	 */
	LinkedList<RunningState> findRunningStates(int vehicleId,Date date);

}
