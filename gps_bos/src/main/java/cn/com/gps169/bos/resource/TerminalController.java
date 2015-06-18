package cn.com.gps169.bos.resource;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 终端控制器
 * @author tianfei
 *
 */
@Controller
@RequestMapping("terminal")
public class TerminalController {
	
	/**
	 * 分页查询终端信息
	 * @return
	 */
	@RequestMapping("page")
	@ResponseBody
	public String page(HttpServletRequest request){
		int pageNum = Integer.parseInt(request.getParameter("page"));
		int pageRows = Integer.parseInt(request.getParameter("rows"));
		String sStatus = request.getParameter("status");
		int status = StringUtils.isNotBlank(sStatus) ? Integer.parseInt(sStatus) : 0;
		String licensePlate = request.getParameter("licensePlate");
//		JSONObject result = terminalService.queryTerminal((pageNum-1)*pageRows, pageRows,status,licensePlate);
		
		return "";
	}
	
	/**
	 * 查询未绑定的车辆信息
	 * @return
	 */
	@RequestMapping("unbindTmnl")
	@ResponseBody
	public String getUnbindTmnl(){
	    return null;
	}
	
	/**
	 * 查询终端信息
	 * @param vid
	 * @return
	 */
	@RequestMapping("get")
	@ResponseBody
	public String getVehicle(@RequestParam("tid") int tid){
//	    Terminal terminal = terminalService.queryTerminalById(tid);
//	    if(terminal != null){
//	        return JSONObject.fromObject(terminal).toString();
//	    }
	    return null;
	}
	
	/**
	 * 增加车辆
	 * @param vehicle
	 * @return
	 */
//	@RequestMapping(value="add",method=RequestMethod.POST,consumes="application/json")
//	@ResponseBody
//	public String addVehicle(@RequestBody TerminalVo terminal){
//	    JSONObject result = new JSONObject();
//	    result.put("flag", "fail");
//	    if(StringUtils.isBlank(terminal.getImei())){
//	        result.put("msg", "终端识别码不能为空");
//	        return result.toString();
//	    }
//	    String opt = terminalService.addOrUpdateVehicle(terminal); 
//	    if(StringUtils.isBlank(opt)){
//	        result.put("flag", "success");
//	    } else {
//	        result.put("msg", opt);
//	    }
//	    
//	    return result.toString();
//	}
	
	/**
	 * 终端、车辆解绑 
	 * @return
	 */
	@RequestMapping("unbind")
	@ResponseBody
	public String unbind(@RequestParam("vid")int vehicleId,@RequestParam("tid")int tid){
//		terminalService.unbind(vehicleId, tid);
		
		return "sucess";
	}
	
}
