package cn.com.gps169.jt808.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import cn.com.gps169.common.cache.ICacheManager;

/**
 * 启动服务事件
 * @author tianfei
 *
 */
public class ServerLister implements ServletContextListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerLister.class);
	
	private ApplicationContext app;
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		app = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		//初始化缓存数据
		LOGGER.info("初始化系统缓存数据开始....");
		//初始化终端缓存数据
		ICacheManager vehicleCacheManager = (ICacheManager) app.getBean("cacheRedisImpl");
		vehicleCacheManager.initCache();
		LOGGER.info("初始化系统缓存数据结束...");
		// 启动MINA服务
		LOGGER.info("启动JT808服务开始....");
		JT808Server minaServer = (JT808Server) app.getBean("jt808Server");
		minaServer.start();
		LOGGER.info("启动JT808服务结束....");
		LOGGER.info("启动GPS服务开始...");
		GpsServer gpsServer = (GpsServer) app.getBean("gpsServer");
		gpsServer.start();
		LOGGER.info("启动GPS服务完成...");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	    
	}
	
	/**
	 *启动业务处理服务
	 */
	private boolean startBusiHandler(){
		
		return true;
	}
	
	/**
	 * 停止业务处理器
	 */
	private void stopBusiHandler(){

	}

}
