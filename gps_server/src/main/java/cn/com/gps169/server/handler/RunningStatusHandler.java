package cn.com.gps169.server.handler;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.gps169.common.cache.IRunningStatusCacheManager;
import cn.com.gps169.common.model.GpsInfo;
import cn.com.gps169.common.model.RunningState;
import cn.com.gps169.db.dao.RefuelMapper;
import cn.com.gps169.db.model.Refuel;

/**
 * 车辆运行状态处理器
 * 
 * @author tianfei
 *
 */
@Component
public class RunningStatusHandler {

	private static Logger logger = LoggerFactory.getLogger(RunningStatusHandler.class);

	/**
	 * 上行消息队列
	 */
	private static BlockingQueue<GpsInfo> gpsQueue = new LinkedBlockingQueue<GpsInfo>();

	private ExecutorService executorService = Executors.newFixedThreadPool(20);

	private volatile boolean startFlag = false;

	@Autowired
	private IRunningStatusCacheManager runningStatusCacheManager;

	@Autowired
	private RefuelMapper refuelMapper;

	/**
	 * 启动处理器
	 */
	public void startHandler() {
		synchronized (this) {
			// 启动上行处理器
			if (!startFlag) {
				startFlag = true;
				Thread thread = new Thread(new Runnable() {
					public void run() {
						logger.info("启动车辆运行状态处理器成功！");
						while(startFlag) {
							try {
								executorService.execute(new RunningStatusThread(gpsQueue.take())); // 阻塞方法
								Thread.sleep(300);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							}
						}
					}
				});
				thread.start();
			}
		}
	}
	
	/**
	 * 停止车辆运行状态处理器
	 */
	public void stopHandler(){
		synchronized (this) {
			if(startFlag){
				try {
					while(!gpsQueue.isEmpty()){
							Thread.sleep(200);
					}
					Thread.sleep(200);
					startFlag = false;
					executorService.shutdown();
					logger.info("停止车辆运行状态处理器成功");
				} catch (InterruptedException e) {
					logger.error("停止车辆运行状态处理器异常，异常信息："+e.getMessage());
				}
			}
		}
	}

	/**
	 * 添加待处理的GPS
	 * 
	 * @param gpsInfo
	 */
	public void processData(GpsInfo gpsInfo) {
		try {
			gpsQueue.put(gpsInfo);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class RunningStatusThread implements Runnable {

		private GpsInfo gpsInfo;

		public RunningStatusThread(GpsInfo gpsInfo) {
			this.gpsInfo = gpsInfo;
		}

		@Override
		public void run() {
			// 最近一条车辆状态
			RunningState lastrs = runningStatusCacheManager.findLatestRunningState(gpsInfo.getVid(),new Date());
			if (null != lastrs) {
				long preTime = lastrs.getReceivedTime().getTime();
				long nowTime = gpsInfo.getSendTime().getTime();
				//5分钟内的状态不更新
				if ((nowTime - preTime) < 0 || ((nowTime - preTime) / 60000) < 4) {
					return;
				}
			}
			// 当前车辆状态
			RunningState curr = new RunningState(gpsInfo);
			// 保存当前车辆状态
			runningStatusCacheManager.saveRunningState(curr);
			// 加油记录解析
			if (lastrs != null) {
				saveFule(curr, lastrs);
			}
		}

		/**
		 * 保存加油记录
		 * 
		 * @param curr
		 * @param pre
		 */
		private void saveFule(RunningState curState, RunningState prevState) {
			double curFuelAmount = curState.getFuel();
			double prevFuelAmount = prevState.getFuel();
			double curMileage = curState.getMileage();
			double prevMileage = prevState.getMileage();
			double refuelAmount = curFuelAmount - prevFuelAmount;
			//加油量> 15 &&  距离 《＝10 || 时间 <15小时
			if ((refuelAmount >= 15) && ((curMileage - prevMileage <= 10) ||
					(curState.getReceivedTime().getTime() - prevState.getReceivedTime().getTime() <= 15 * 60 * 1000))) {
				Refuel refuel = new Refuel();
				refuel.setFuelAmount(new BigDecimal(refuelAmount));
				refuel.setRefuelDate(curState.getReceivedTime());
				refuel.setVehicleId(curState.getVid());
				refuel.setMileage(new BigDecimal(curState.getMileage()));
				refuel.setFuelAmount(new BigDecimal(prevFuelAmount)); // 加油前油量
				refuel.setRefuelAmount(new BigDecimal(refuelAmount)); // 加油量
				refuel.setCreated(new Date());
				// 查询加油位置信息
				refuelMapper.insertSelective(refuel);
			}
		}

	}

}
