package cn.com.gps169.bos.resource;

import javax.ws.rs.QueryParam;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.gps169.bos.service.IUserService;
import cn.com.gps169.db.model.User;

/**
 * 用户管理控制器
 * @author tianfei
 *
 */
@Controller
@RequestMapping("user")
public class UserController {
	
	@Autowired
	private IUserService userService;
	
	/**
	 * 分布查询用户信息
	 * @return
	 */
	@RequestMapping("page")
	@ResponseBody
	public String page(@QueryParam("page")Integer page,@QueryParam("rows")Integer rows,@QueryParam("search")String search,
			@QueryParam("roleType")Integer roleType){
		JSONObject result = userService.findUserByPage((page-1) * rows, rows, search,roleType);
		
		return result.toString();
	}
	
	/**
	 * 增加、修改车主信息
	 * @param user
	 * @return
	 */
	@RequestMapping(value="add",method=RequestMethod.POST,consumes="application/json")
	@ResponseBody
	public String add(@RequestBody User user){
	    if(user == null){
	        return "";
	    }
		return userService.saveOrUpdateUser(user);
	}
	
	/**
	 * 查询用户详细
	 * @param user
	 * @return
	 */
	@RequestMapping("get")
	@ResponseBody
	public String addDriver(@RequestParam int userId){
	    User user = userService.findUserById(userId);
	    user.setPassword("");
	    
		return JSONObject.fromObject(user).toString();
	}
	
	/**
	 * 查询车主
	 * @return
	 */
	@RequestMapping("owner")
	@ResponseBody
	public String getVehicleOwner(){
	    JSONArray array = userService.findVehOwner();
	    
	    return array.toString();
	}

}
