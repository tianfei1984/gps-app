package cn.com.gps169.bos.service;

import cn.com.gps169.bos.model.TerminalVo;
import cn.com.gps169.db.model.Terminal;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 终端业务逻辑接口
 * @author tianfei
 *
 */
public interface ITerminalService {
	
	/**
	 * 查询终端信息
	 * @param params
	 * @return
	 */
	JSONObject queryTerminal(int pageNum,int pageRows,int status,String sim);
	
	/**
	 * 增加终端
	 * @param vehicle
	 */
	String addOrUpdateVehicle(TerminalVo terminal);
	
	/**
	 * 根据车辆id查询车辆信息
	 * @param vid
	 * @return
	 */
	Terminal queryTerminalById(int vid);
	
	/**
	 * 查询未绑定的车辆
	 * @return
	 */
	JSONArray getUnbindTmnl();
	
	/**
	 * 终端、车辆解绑
	 * @param vid
	 * @param tid
	 * @return
	 */
	String unbind(int vid,int tid);

}
