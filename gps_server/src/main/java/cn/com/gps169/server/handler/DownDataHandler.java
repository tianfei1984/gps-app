package cn.com.gps169.server.handler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.gps169.server.protocol.Jt808Message;
import cn.com.gps169.server.server.Connection;
import cn.com.gps169.server.server.MinaServer;

/**
 * 下行数据处理器
 * @author tianfei
 *
 */
@Component
public class DownDataHandler {
	
	private transient Logger logger = LoggerFactory.getLogger(DownDataHandler.class);
	
	@Autowired
	private ServerHandler serverHandler;
	
	@Autowired
	private MinaServer minaServer;
	
	//启动标识
	private volatile boolean startFlag = false;
	
	/**
	 * 下行数据队列
	 */
	private static BlockingQueue<Jt808Message> downdataQueue = new LinkedBlockingQueue<Jt808Message>();
	
	private ExecutorService executors = Executors.newFixedThreadPool(20);
	
	/**
	 * 处理下行数据
	 * @param msg
	 */
	public static void put(Jt808Message msg){
		try {
			downdataQueue.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 启动下行处理器
	 */
	public void startHandler(){
		synchronized (this) {
			if (!startFlag) {
				startFlag = true;
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						logger.info("启动下午数据处理器成功！");
						while(startFlag){
							try {
								executors.execute(new HandlerThread(downdataQueue.take()));
								TimeUnit.MILLISECONDS.sleep(200);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
				thread.start();
			}
		}
	}
	
	/**
	 * 停止下行处理器
	 */
	public void stopHandler(){
		synchronized (this) {
			if(startFlag){
				try {
					while(!downdataQueue.isEmpty()){
							Thread.sleep(100);
					}
					Thread.sleep(200);
					startFlag = false;
					executors.shutdown();
					logger.info("停止下行处理器成功");
				} catch (InterruptedException e) {
					logger.error("停止下行处理器异常，异常信息："+e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 下午处理线程
	 * @author tianfei
	 *
	 */
	private class HandlerThread implements Runnable {
		
		private Jt808Message response;
		
		public HandlerThread(Jt808Message msg){
			this.response = msg;
		}

		@Override
		public void run() {
			// 下发消息
			Connection conn = serverHandler.getConnection(response.getSimNo());
			if(conn != null){
				IoSession session = minaServer.getSession(conn.getSessionId());
				if(session != null && session.isConnected()){
					logger.info("发送消息："+response.toString());
					session.write(response);
				}
			}
		}
	}
}
