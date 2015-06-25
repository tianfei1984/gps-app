package cn.com.gps169.bos.service;

import cn.com.gps169.db.model.Suggestion;
import net.sf.json.JSONObject;

/**
 * 意见反馈接口
 * @author tianfei
 *
 */
public interface ISuggestionService {
    
    /**
     * 分页查询意见反馈
     * @param pageIndex
     * @param pageRows
     * @param search
     * @return
     */
    JSONObject findByPage(Integer pageIndex,Integer pageRows);
    
    /**
     * 增加或更新意见反馈信息
     * @param user
     * @return
     */
    String saveOrUpdateUser(Suggestion suggestion);
    
    /**
     * 根据ID查询意见详细
     * @param userId
     * @return
     */
    Suggestion findUserById(int id);

}
