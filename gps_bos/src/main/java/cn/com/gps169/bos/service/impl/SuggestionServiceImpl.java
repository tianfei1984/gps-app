package cn.com.gps169.bos.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cn.com.gps169.bos.service.ISuggestionService;
import cn.com.gps169.db.dao.SuggestionMapper;
import cn.com.gps169.db.model.Suggestion;
import cn.com.gps169.db.model.SuggestionExample;

/**
 * @author tianfei
 *
 */
@Service
public class SuggestionServiceImpl implements ISuggestionService {
    
    @Autowired
    private SuggestionMapper suggestionMapper; 

    /* (non-Javadoc)
     * @see cn.com.gps169.bos.service.ISuggestionService#findByPage(java.lang.Integer, java.lang.Integer)
     */
    @Override
    public JSONObject findByPage(Integer pageIndex, Integer pageRows) {
        SuggestionExample example = new SuggestionExample();
        int total = suggestionMapper.countByExample(example);
        example.setLimitStart(pageIndex);
        example.setLimitEnd(pageRows);
        List<Suggestion> list = suggestionMapper.selectByExample(example);
        JSONArray array = new JSONArray();
        for(Suggestion s : list){
            array.add(JSONObject.fromObject(s));
        }
        JSONObject result = new JSONObject();
        result.put("total", total);
        result.put("rows", array);
        
        return result;
    }

    /* (non-Javadoc)
     * @see cn.com.gps169.bos.service.ISuggestionService#saveOrUpdateUser(cn.com.gps169.db.model.Suggestion)
     */
    @Override
    public String saveOrUpdateUser(Suggestion suggestion) {
        if(suggestion.getId() == null){
            suggestionMapper.insert(suggestion);
        } else {
            suggestionMapper.updateByPrimaryKeySelective(suggestion);
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see cn.com.gps169.bos.service.ISuggestionService#findUserById(int)
     */
    @Override
    public Suggestion findUserById(int id) {
        return suggestionMapper.selectByPrimaryKey(id);
    }

}
