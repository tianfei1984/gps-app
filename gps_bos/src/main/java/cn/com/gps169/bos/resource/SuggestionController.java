package cn.com.gps169.bos.resource;

import javax.ws.rs.QueryParam;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.com.gps169.bos.service.ISuggestionService;
import cn.com.gps169.db.model.Suggestion;

/**
 * 意见反馈控制类
 * @author tianfei
 *
 */
@Controller
@RequestMapping("suggestion")
public class SuggestionController {
    
    @Autowired
    private ISuggestionService suggestionService;
    
    /**
     * 分布查询用户信息
     * @return
     */
    @RequestMapping("page")
    @ResponseBody
    public String page(@QueryParam("page")Integer page,@QueryParam("rows")Integer rows,@QueryParam("search")String search,
            @QueryParam("type")Integer roleType){
        JSONObject result = suggestionService.findByPage((page-1) * rows, rows);
        
        return result.toString();
    }
    
    /**
     * 查询反馈信息详细
     * @param id
     * @return
     */
    @RequestMapping("get")
    @ResponseBody
    public String get(@RequestParam("id")int id){
        Suggestion s = suggestionService.findUserById(id);
        
        return JSONObject.fromObject(s).toString(); 
    }
    
    /**
     * 修改意见反馈
     * @param suggestion
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public String update(@RequestBody Suggestion suggestion){
        return suggestionService.saveOrUpdateUser(suggestion);
    }
    

}
