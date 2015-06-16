package cn.com.gps169.jt808.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import cn.com.gps169.common.cache.IVehicleCacheManager;

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
		initCache();
		//启动业务服务
		startBusiHandler();
		// 启动MINA服务
		JT808Server minaServer = (JT808Server) app.getBean("jt808Server");
		minaServer.start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	    
	}
	
	/**
	 * 初始化缓存
	 */
	private void initCache(){
		//初始化终端缓存数据
		IVehicleCacheManager vehicleCacheManager = (IVehicleCacheManager) app.getBean("vehicleRedisImpl");
		vehicleCacheManager.initCache();
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
