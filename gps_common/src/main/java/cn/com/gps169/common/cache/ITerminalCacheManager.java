package cn.com.gps169.common.cache;

import cn.com.gps169.db.model.Terminal;



/**
 * 终端缓存管理
 * @author tianfei
 *
 */
public interface ITerminalCacheManager {
	
	public void initCache();
	
	Terminal addOrUpdateTerminal(Terminal t) ;
	
	Terminal getTerminalBySimNo(String simNo);

}
