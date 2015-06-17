package cn.com.gps169.jt808.proc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import cn.com.gps169.common.cache.ICacheManager;
import cn.com.gps169.jt808.protocol.EMsgAck;
import cn.com.gps169.jt808.protocol.Message;

/**
 * jt808处理器接口
 * @author tianfei
 *
 */
@Component
public abstract class  Proc {
	
	/**
	 * 操作结果
	 */
	protected byte optResult = EMsgAck.SUCESS.value();
	
	@Autowired
	protected ICacheManager cacheManager;
	
	
	/**
	 * JT808事件处理
	 * @param msg
	 */
	public abstract void proc(Message msg);
	
	/**
	 * 返回请求响应
	 * @param response
	 */
	public void writeResponse(Message response) {
	}

}
