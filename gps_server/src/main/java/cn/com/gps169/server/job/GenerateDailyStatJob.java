package cn.com.gps169.server.job;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.gps169.common.cache.IRunningStatusCacheManager;
import cn.com.gps169.common.cache.IVehicleCacheManager;
import cn.com.gps169.common.model.RunningState;
import cn.com.gps169.common.tool.DateUtil;
import cn.com.gps169.db.dao.DailyStatMapper;
import cn.com.gps169.db.dao.RefuelMapper;
import cn.com.gps169.db.model.DailyStat;
import cn.com.gps169.db.model.DailyStatExample;
import cn.com.gps169.db.model.Refuel;
import cn.com.gps169.db.model.RefuelExample;

/**
 * 执行日统计
 * 
 * @author tianfei
 *
 */
@Component("generateDailyStatJob")
public class GenerateDailyStatJob {

	private static Logger logger = LoggerFactory
			.getLogger(GenerateDailyStatJob.class);

	@Autowired
	private IVehicleCacheManager vehicleCacheManager;

	@Autowired
	private IRunningStatusCacheManager runningStatusCacheManager;

	@Autowired
	private RefuelMapper refuelMapper;
	
	@Autowired
	private DailyStatMapper dailyStatMapper;

	private static final double MAX_MILEAGE_INCOUNT = 3000;

	private static final double DELTA_MILEAGE_INCOUNT = 50;

	/**
	 * 执行日统计方法
	 */
	public void execute() {
		Date occurTime = DateUtil.addDate(DateUtil.formatDate(new Date()), -1);
		int occurDay = Integer.parseInt(DateUtil.DATEFORMATER().format(occurTime));
		List<Integer> list = vehicleCacheManager.findAllVehicleIds();
		for (int vehicleId : list) {
			logger.info(String.format("对车辆【%d】【%d】执行日统计开始",vehicleId,occurDay));
			updateDailyStat(vehicleId,occurDay, new Date());
			logger.info(String.format("对车辆【%d】【%d】执行日统计结束",vehicleId,occurDay));
		}

	}
	
	/**
	 * 计算车辆日统计信息
	 * @param vehicleId
	 * @param date
	 */
	private void updateDailyStat(int vehicleId,int occurDay, Date occurTime){
		//查询车辆运行状态集合
		RunningState stat = runningStatusCacheManager.findLatestRunningState(vehicleId, occurTime);
		if(stat == null){
			logger.info("没有车辆运行状态数据");
			return;
		}
		BigDecimal fuelIncount = new BigDecimal(0);
		BigDecimal mileageIncount = new BigDecimal(0);
		BigDecimal lper100km = new BigDecimal(0);
		BigDecimal rper100km = new BigDecimal(0);
		//计算里程和油耗
		double[] calcData = calcFuelAndMileage(vehicleId, occurTime);
		if (calcData != null) {
			fuelIncount = BigDecimal.valueOf(calcData[0]);
			mileageIncount = BigDecimal.valueOf(calcData[1]);
		}
		DailyStat dailyStat = new DailyStat();
		dailyStat.setOccurDate(occurDay);
		dailyStat.setFuelAmount(BigDecimal.valueOf(stat.getFuel()));			//当前油量
		dailyStat.setMileage(BigDecimal.valueOf(stat.getMileage()));	//当前里程
		dailyStat.setVehicleId(vehicleId);
		dailyStat.setFuelIncount(fuelIncount.setScale(2, RoundingMode.HALF_UP));	//当日油耗量
		dailyStat.setMileageIncount(mileageIncount);			//当日行驶里程
		dailyStat.setRefuel(BigDecimal.valueOf(getRefule(vehicleId, occurTime)));	//加油量
		dailyStat.setFuelPer100km(lper100km);					//当日百公里油耗
		dailyStat.setFeePer100km(rper100km);					//百公里油费
		dailyStat.setCreated(new Date());
		//清空已经存在日统计
		DailyStatExample example = new DailyStatExample();
		example.or().andOccurDateEqualTo(occurDay);
		dailyStatMapper.deleteByExample(example);
		//添加新的日统计
		dailyStatMapper.insertSelective(dailyStat);
		//TODO:计算车辆月统计
	}

	// 该方法用于在计算或获取日统计信息时计算行驶里程和油耗
	private double[] calcFuelAndMileage(int vehicleId, Date date) {

		logger.debug("计算或获取日统计信息时计算行驶里程和油耗start，传入参数[vehicleId=" + vehicleId
				+ "][date=" + DateUtil.TIMEFORMATER1().format(date) + "]");
		long programStart, programEnd, normalQueryStart, normalQueryEnd;
		programStart = System.currentTimeMillis();

		double[] calcData = null;
		List<RunningState> runningStates = null;
		normalQueryStart = System.currentTimeMillis();
		runningStates = runningStatusCacheManager.findRunningStates(vehicleId,date);
		if (runningStates != null && runningStates.size() > 0) {
			logger.debug("计算当日行驶里程和油耗Start，runningStates长度："+ runningStates.size());
			normalQueryStart = System.currentTimeMillis();
			// 计算当天油耗
			double firstFuel = 0;
			for (int i = 0; i < runningStates.size(); i++) {
				if (runningStates.get(i).getFuel() > 0) {
					firstFuel = runningStates.get(i).getFuel();
					break;
				}
			}
			double lastFuel = 0;
			for (int i = runningStates.size() - 1; i >= 0; i--) {
				if (runningStates.get(i).getFuel() > 0) {
					lastFuel = runningStates.get(i).getFuel();
					break;
				}
			}
			// 计算油耗
			double fuelIncountTemp = getRefule(vehicleId,date) + firstFuel - lastFuel;
			if (fuelIncountTemp < 0) {
				fuelIncountTemp = 0;
			}
			// 计算当天行驶里程
			double firstMileage = 0;
			for (int i = 0; i < runningStates.size(); i++) {
				if (runningStates.get(i).getMileage() > 0) {
					firstMileage = runningStates.get(i).getMileage();
					break;
				}
			}
			double lastMileage = 0;
			for (int i = runningStates.size() - 1; i >= 0; i--) {
				if (runningStates.get(i).getMileage() > 0) {
					lastMileage = runningStates.get(i).getMileage();
					break;
				}
			}
			double mileageIncountTemp = lastMileage - firstMileage;

			if (mileageIncountTemp > MAX_MILEAGE_INCOUNT || mileageIncountTemp < 0) {
				mileageIncountTemp = calcMileage(runningStates);
			}

			calcData = new double[2];
			calcData[0] = fuelIncountTemp;	//耗油量
			calcData[1] = mileageIncountTemp;	//行驶里程

			normalQueryEnd = System.currentTimeMillis();
			logger.debug("计算当日行驶里程和油耗End，共耗时" + (normalQueryEnd - normalQueryStart) + "ms");
		}

		programEnd = System.currentTimeMillis();
		logger.debug("计算或获取日统计信息时计算行驶里程和油耗End，共耗时" + (programEnd - programStart) + "ms");
		return calcData;
	}

	// 计算里程有跳变情况下的当天行驶里程
	private double calcMileage(List<RunningState> runningStates) {
		// 获取合理的行驶里程列表
		List<BigDecimal> list = new LinkedList<BigDecimal>();
		for (RunningState state : runningStates) {
			if (state.getMileage() > 0) {
				list.add(new BigDecimal(state.getMileage()));
			}
		}

		if (list.size() < 2) {
			return 0;
		}

		int mileageIncount = 0;
		int end = list.size() - 1;
		int start = end - 1;

		while (start >= 0) {
			double delta = list.get(start + 1).doubleValue()
					- list.get(start).doubleValue();

			if (delta > DELTA_MILEAGE_INCOUNT || delta < 0) {
				mileageIncount += list.get(end).doubleValue()
						- list.get(start + 1).doubleValue();
				end = start;
				start = start - 1;
			} else {
				start = start - 1;
			}
		}

		mileageIncount += list.get(end).doubleValue() - list.get(start + 1).doubleValue();

		return mileageIncount;
	}
	
	private double getRefule(int vehicleId,Date date){
		//查询时间 
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		Date start = calendar.getTime();
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MINUTE, 59);
		Date end = calendar.getTime();
		// 查询某一天的加油量
		RefuelExample example = new RefuelExample();
		example.or().andRefuelDateGreaterThanOrEqualTo(start).andRefuelDateLessThanOrEqualTo(end).andVehicleIdEqualTo(vehicleId);
		List<Refuel> list = refuelMapper.selectByExample(example);
		double refuleAmount = 0;
		for(Refuel r : list){
			refuleAmount += r.getRefuelAmount().doubleValue();
		}
	
		return refuleAmount;
	}

}
