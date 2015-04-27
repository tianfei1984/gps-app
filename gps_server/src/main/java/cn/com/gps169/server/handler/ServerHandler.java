package cn.com.gps169.server.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.gps169.common.cache.IDataAcquireCacheManager;
import cn.com.gps169.common.cache.ITerminalCacheManager;
import cn.com.gps169.db.model.Terminal;
import cn.com.gps169.server.protocol.Jt808Message;
import cn.com.gps169.server.server.Connection;

/**
 * 业务处理器
 * @author tianfei
 *
 */
@Component
public class ServerHandler extends IoHandlerAdapter {
	
	@Autowired
	private UpDataHandler updateHandler;
	
	@Autowired
	private ITerminalCacheManager terminalCacheManager;
	
	@Autowired
	private IDataAcquireCacheManager dataAcquireCacheManager;
	
	/**
	 * 服务器连接状态，KEY:SIM号； VALUE:连接状态
	 */
	private ConcurrentHashMap<String, Connection> serverConns = new ConcurrentHashMap<String, Connection>();
 
	private transient static Logger logger = LoggerFactory.getLogger(ServerHandler.class);
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("服务器与客户端创建连接...");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("服务器与客户端连接打开...");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		String simNo = getSimNo(session);
		if (StringUtils.isNotBlank(simNo)) {
			Connection conn = serverConns.get(simNo);
			if (conn != null) {
				conn.setConnected(false);
				conn.setDisconnectTimes(conn.getDisconnectTimes() + 1);
				// 设置终端离线
				dataAcquireCacheManager.setIsOnline(conn.getTerminalId(), false);
			}
		}
		session.close(true);
		logger.info("终端与本地服务器断开连接, SimNo:" + simNo);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		//心跳超时，断开连接 
		logger.info(String.format("终端与平台超时断开连接，SIM：%s",getSimNo(session)));
		session.close(true);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable e)
			throws Exception {
	    StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
		logger.error(String.format("SIM卡号为:%s 通讯时发生异常,异常信息如下:%s",getSimNo(session), sw.toString()));
	}

	@Override
	public void messageReceived(IoSession session, Object msg)
			throws Exception {
		Jt808Message message = (Jt808Message) msg;
		session.setAttribute("simNo", message.getSimNo());
		Connection conn = getConnection(session.getId(), message);
		if(conn != null){
			message.setConn(conn);
			updateHandler.add(message);
		}
		logger.info("接收消息："+ message.toString());
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
//		logger.info("服务器发送消息成功...");
	}
	
	public Connection getConnection(String simNo){
		return serverConns.get(simNo);
	}
	
	public ConcurrentHashMap<String, Connection> getServerConns() {
		return serverConns;
	}

	private Connection getConnection(long sessionId, Jt808Message msg) {
		if (msg == null || msg.getSimNo() == null) {
			return null;
		}
		Connection conn = serverConns.get(msg.getSimNo());
		if (conn == null) {
			Terminal tmnl = terminalCacheManager.getTerminalBySimNo(msg.getSimNo());
			if(tmnl == null){
				logger.error("sim卡号："+msg.getSimNo()+"的终端未在该平台注册！");
				return null;
			}
			//设置终端在线
			dataAcquireCacheManager.setIsOnline(tmnl.getTerminalId(), true);
			conn = new Connection(msg.getSimNo(), sessionId);
			conn.setTerminalId(tmnl.getTerminalId());
			serverConns.put(msg.getSimNo(), conn);
		} else if (conn.getSessionId() != sessionId) {
			// 重新进行的连接
			conn.setConnected(true);
			conn.setSessionId(sessionId);
			logger.info(String.format("SIM卡号为:%s 的设备重新接入平台", msg.getSimNo()));
		}
		return conn;
	}
	
	/**
	 * 查询SIM卡号
	 * @param session
	 * @return
	 */
	public String getSimNo(IoSession session) {
		return "" + session.getAttribute("simNo");
	}

}
