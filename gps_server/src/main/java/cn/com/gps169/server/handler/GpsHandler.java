package cn.com.gps169.server.handler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.gps169.common.cache.IDataAcquireCacheManager;
import cn.com.gps169.common.cache.ITripCacheManager;
import cn.com.gps169.common.model.GpsInfo;
import cn.com.gps169.common.tool.DateUtil;
import cn.com.gps169.server.tool.LocationTool;

/**
 * GPS位置信息处理器
 * @author tianfei
 *
 */
@Component
public class GpsHandler {
	
	private static Logger logger = LoggerFactory.getLogger(GpsHandler.class);
	
	/**
	 * 位置 信息队列
	 */
	private static BlockingQueue<GpsInfo> gpsQueue = new LinkedBlockingQueue<GpsInfo>();
	
	private ExecutorService executorService =  Executors.newFixedThreadPool(20);
	
	//启动标识
	private volatile boolean startFlag = false;
	
	@Autowired
	private IDataAcquireCacheManager dataAcquireCacheManager;
	
	@Autowired
	private ITripCacheManager tripCacheManager;
	
	/**
	 * 启动处理器
	 */
	public void startHandler(){
		synchronized (this) {
			//启动上行处理器 
			if(!startFlag){
				startFlag = true;
				Thread thread = new Thread(new Runnable() {
					public void run() {
						logger.info("启动GPS数据处理器成功！");
						while(startFlag){
							try {
								executorService.execute(new GpsHandlerThread(gpsQueue.take()));	//阻塞方法
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
	 * 停止GPS处理器
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
					logger.info("停止GPS处理器成功");
				} catch (InterruptedException e) {
					logger.error("停止GPS处理器异常，异常信息："+e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 添加待处理的GPS
	 * @param gpsInfo
	 */
	public void addGps(GpsInfo gpsInfo){
		try {
			gpsQueue.put(gpsInfo);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 位置信息处理线程
	 * @author tianfei
	 *
	 */
	private class GpsHandlerThread implements Runnable{
		
		private GpsInfo gpsInfo;
		
		public GpsHandlerThread(GpsInfo gpsInfo){
			this.gpsInfo = gpsInfo;
		}

		@Override
		public void run() {
			// 位置偏转
			JSONObject json = LocationTool.deflectGpsData(gpsInfo);
			if(json == null){
				//位置偏转失败 TODO:T待优化处理
				logger.error("位置信息偏转失败！"+gpsInfo.toString());
				return;
			}
			//保存位置信息
			JSONObject gpsData = new JSONObject();
			gpsData.put("latitude", json.get("latitude"));
			gpsData.put("longitude", json.get("longitude"));
			gpsData.put("mileage", String.valueOf(gpsInfo.getMileage()));
			gpsData.put("fuel", String.valueOf(gpsInfo.getFuel()));
			gpsData.put("altitude", String.valueOf(gpsInfo.getAltitude()));
			gpsData.put("speed", String.valueOf(gpsInfo.getSpeed()));
			gpsData.put("recordSpeed", String.valueOf(gpsInfo.getRecordSpeed()));
			gpsData.put("vid", gpsInfo.getVid());
			gpsData.put("tid", gpsInfo.getTid());
			String sendTime = DateUtil.TIMEFORMATER1().format(gpsInfo.getSendTime());
			gpsData.put("sendTime", sendTime);
			gpsData.put("updated", gpsInfo.getSendTime().getTime());
			//保存车辆当前位置 
			dataAcquireCacheManager.setGps(gpsData);
			//保存车辆行程轨迹点
			tripCacheManager.pushGpsRecord(gpsData);
			logger.info(String.format("保存车辆【%s  %s】轨迹点成功。",gpsInfo.getVid(),sendTime));
		}
	}
	

}
