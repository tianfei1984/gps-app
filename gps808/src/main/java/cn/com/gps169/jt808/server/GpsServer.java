package cn.com.gps169.jt808.server;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import cn.com.gps169.common.cache.ICacheManager;
import cn.com.gps169.common.gis.GpsTool;
import cn.com.gps169.common.model.GpsInfo;
import cn.com.gps169.common.thread.MyThreadFactory;
import cn.com.gps169.common.tool.DateUtil;

/**
 * GPS位置信息服务
 * @author tianfei
 *
 */
@Component
public class GpsServer {
	
	private static Logger logger = LoggerFactory.getLogger(GpsServer.class);
	
	/**
	 * 位置信息队列
	 */
	private static BlockingQueue<GpsInfo> gpsQueue = new LinkedBlockingQueue<GpsInfo>();
	
	private static int nThreads = Runtime.getRuntime().availableProcessors() * 1;//处理的线程数
    private static int MAX_QUEUQ_SIZE = 100;
    
    // 线程池
    private ExecutorService executorService = new ThreadPoolExecutor(nThreads, nThreads, 1, TimeUnit.HOURS, new ArrayBlockingQueue<Runnable>(MAX_QUEUQ_SIZE),
            new MyThreadFactory("GpsServer-Thread"),new ThreadPoolExecutor.CallerRunsPolicy());
	
	//启动标识
	private volatile boolean startFlag = false;
	
	@Autowired
	private ICacheManager cacheManager;
	
	/**
	 * 启动处理器
	 */
	public void start(){
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
								Thread.sleep(10);
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
	public void stop(){
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
			//判断是否进行GPS解析
			GpsInfo oldGps = cacheManager.findGpsInfoBySim(gpsInfo.getSimNo());
			if(isDeflect(gpsInfo, oldGps)){
				// 位置偏转
				GpsInfo gps = GpsTool.deflectGpsData(gpsInfo);
				if(gps == null){
					//位置偏转失败 TODO:T待优化处理
					logger.error("位置信息偏转失败！"+gpsInfo.toString());
					return;
				}
				// 更新车辆最新位置
				cacheManager.addGpsInfo(gps);
				//更新车辆轨迹点
				cacheManager.addVehicleTrack(gps);
			}
		}
		
		/**
		 * 判断是否需要进行GPS解析
		 * @param currGps
		 * @param oldGps
		 * @return
		 */
		private boolean isDeflect(GpsInfo currGps,GpsInfo oldGps){
			//首次上GPS
			if(oldGps == null){
				return true;
			}
			//判断两点是否相等
			if(oldGps.getLatitude() == currGps.getLatitude() && oldGps.getLongitude() == currGps.getLongitude()){
				return false;
			}
			//两点间隔大于1分钟
			Date currTime = DateUtil.stringToDatetime(currGps.getSendTime());
			Date preTime = DateUtil.stringToDatetime(oldGps.getSendTime());
			if(DateUtil.getSeconds(preTime,currTime) >= 60){
				return true;
			}
			//TODO:判断两点的距离大于100米
			
			
			return false;
		}
	}
	

}
