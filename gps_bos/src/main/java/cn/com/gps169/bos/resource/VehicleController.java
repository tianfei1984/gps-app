package cn.com.gps169.bos.resource;

import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.gps169.bos.service.IVehicleService;

/**
 * 车辆控制器
 * @author tianfei
 *
 */
@Controller
@RequestMapping("vehicle")
public class VehicleController {
	
	@Autowired
	private IVehicleService vehicleService;
	
	/**
	 * 分页查询车辆信息
	 * @return
	 */
	@RequestMapping("page")
	@ResponseBody
	public String vehiclePage(HttpServletRequest request){
		int pageNum = Integer.parseInt(request.getParameter("page"));
		int pageRows = Integer.parseInt(request.getParameter("rows"));
		int status = Integer.parseInt(request.getParameter("status"));
		String licensePlate = request.getParameter("licensePlate");
		JSONObject result = vehicleService.queryVehicle(pageNum, pageRows,status,licensePlate);
		
		return result.toString();
	}

}
