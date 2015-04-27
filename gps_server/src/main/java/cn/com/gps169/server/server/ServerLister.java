package cn.com.gps169.server.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.com.gps169.common.cache.ITerminalCacheManager;
import cn.com.gps169.common.cache.ITmnlVehiCacheManager;
import cn.com.gps169.common.cache.IVehicleCacheManager;
import cn.com.gps169.server.handler.DownDataHandler;
import cn.com.gps169.server.handler.GpsHandler;
import cn.com.gps169.server.handler.RunningStatusHandler;
import cn.com.gps169.server.handler.UpDataHandler;

/**
 * 启动服务事件
 * @author tianfei
 *
 */
public class ServerLister implements ServletContextListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerLister.class);
	
	private ApplicationContext app;
	//tcp服务
	private MinaServer minaServer = null;
	
	//上行处理器
	private UpDataHandler upDataHandler = null;
	//下行处理器
	private DownDataHandler downDataHandler = null;
	//GPS处理器
	private GpsHandler gpsHandler = null;
	//车辆运行状态处理器
	private RunningStatusHandler runningStatusHandler = null;
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		app = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		//初始化缓存数据
		initCache();
		//启动业务服务
		startBusiHandler();
		// 启动MINA服务
		minaServer = (MinaServer) app.getBean("minaServer");
		minaServer.startServer();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(minaServer != null){
			LOGGER.info("服务器停止开始...");
			minaServer.stopServer();
			stopBusiHandler();
			LOGGER.info("服务器停止完成...");
		}
	}
	
	/**
	 * 初始化缓存
	 */
	private void initCache(){
		//初始化终端缓存数据
		ITerminalCacheManager terminalCacheManager = (ITerminalCacheManager) app.getBean("terminalRedisImpl");
		terminalCacheManager.initCache();
		IVehicleCacheManager vehicleCacheManager = (IVehicleCacheManager) app.getBean("vehicleRedisImpl");
		vehicleCacheManager.initCache();
		ITmnlVehiCacheManager tmnlVehiCacheManager = (ITmnlVehiCacheManager) app.getBean("tmnlVehiRedisImpl");
		tmnlVehiCacheManager.initCache();
	}
	
	/**
	 *启动业务处理服务
	 */
	private boolean startBusiHandler(){
		upDataHandler = (UpDataHandler) app.getBean("upDataHandler");
		downDataHandler = (DownDataHandler) app.getBean("downDataHandler");
		gpsHandler = (GpsHandler) app.getBean("gpsHandler");
		runningStatusHandler = (RunningStatusHandler) app.getBean("runningStatusHandler");
		//启动处理器 
		upDataHandler.startHandler();
		downDataHandler.startHandler();
		gpsHandler.startHandler();
		runningStatusHandler.startHandler();
		
		return true;
	}
	
	/**
	 * 停止业务处理器
	 */
	private void stopBusiHandler(){
		if(upDataHandler != null){
			upDataHandler.stopHandler();
		}
		if(downDataHandler != null){
			downDataHandler.stopHandler();
		}
		if(gpsHandler != null){
			gpsHandler.stopHandler();
		}
		if(runningStatusHandler != null){
			runningStatusHandler.stopHandler();
		}
	}

}
