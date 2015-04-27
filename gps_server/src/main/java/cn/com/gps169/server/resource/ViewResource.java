package cn.com.gps169.server.resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("view")
public class ViewResource {
	
	@RequestMapping(value="/welcome",method=RequestMethod.GET)
	public String test(){
		System.out.println("hello!田百态");
		return "index";
	}
	
	@RequestMapping(value="/json",method=RequestMethod.POST ,consumes="application/json")
	public @ResponseBody String json(@RequestBody JSONObject jj){
		System.out.println(jj.toString());
		JSONObject json = new JSONObject();
		json.put("name", "tianfei");
		
		return json.toString();
	}
	
	/**
	 * 跳转至轨迹回放页面
	 * @return
	 */
	@RequestMapping(value="track")
	public String goTrack(){
	    return "track";
	}
	
	/**
	 * 跳转至车辆定位页面
	 * @return
	 */
	@RequestMapping("location")
	public String goLocation(){
		return "location";
	}

}
