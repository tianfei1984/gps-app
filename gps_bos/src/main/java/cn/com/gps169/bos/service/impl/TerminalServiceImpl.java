package cn.com.gps169.bos.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cn.com.gps169.bos.service.ITerminalService;
import cn.com.gps169.common.cache.ITerminalCacheManager;
import cn.com.gps169.db.dao.TerminalMapper;
import cn.com.gps169.db.model.Terminal;
import cn.com.gps169.db.model.TerminalExample;
import cn.com.gps169.db.model.TerminalExample.Criteria;

@Service
public class TerminalServiceImpl implements ITerminalService {
    
    @Autowired
    private TerminalMapper terminalMapper;
    
    @Autowired
    private ITerminalCacheManager terminalCacheManager;
    
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    /* (non-Javadoc)
     * @see cn.com.gps169.bos.service.ITermialService#queryTerminal(int, int, int, java.lang.String)
     */
    @Override
    public JSONObject queryTerminal(int pageNum, int pageRows, int status,String sim) {
        TerminalExample example = new TerminalExample();
        Criteria criteria = example.or();
        if(status != 0){
            criteria.andWorkingStatusEqualTo(status);
        }
        if(StringUtils.isNotBlank(sim)){
            criteria.andImsiLike("%"+sim+"%");
        }
        example.setLimitStart(pageNum);
        example.setLimitEnd(pageRows);
        int total = terminalMapper.countByExample(example);
        List<Terminal> list = terminalMapper.selectByExample(example);
        JSONObject json = null;
        JSONArray result = new JSONArray();
        for(Terminal v : list){
            json = new JSONObject();
            json.put("tid", v.getTerminalId());
            json.put("imei", v.getImei());
            json.put("imsi", v.getImsi());
            json.put("bindStatus", v.getBindTime() == null ? "未绑定": "已绑定");
            json.put("type", v.getTerminalStyle());
            json.put("status", v.getWorkingStatus() == 1 ? "正常":"停用");
            json.put("created", v.getBindTime());
            result.add(json);
        }
        JSONObject vehicles = new JSONObject();
        vehicles.put("total", total);
        vehicles.put("rows", result);
        
        return vehicles;
    }

    /* (non-Javadoc)
     * @see cn.com.gps169.bos.service.ITermialService#addOrUpdateVehicle(cn.com.gps169.db.model.Terminal)
     */
    @Override
    public String addOrUpdateVehicle(Terminal terminal) {
        Integer tid = terminal.getTerminalId();
        //判断终端有限性
        TerminalExample example = new TerminalExample();
        if(tid == null){
            example.or().andImeiEqualTo(terminal.getImei());
        } else {
            example.or().andImeiEqualTo(terminal.getImei()).andTerminalIdNotEqualTo(tid);
        }
        int count = terminalMapper.countByExample(example);
        if(count > 0){
            return "终端实别码已经存在";
        }
        example.clear();
        if(tid == null){
            example.or().andImsiEqualTo(terminal.getImsi());
        } else {
            example.or().andImsiEqualTo(terminal.getImsi()).andTerminalIdNotEqualTo(tid);
        }
        count = terminalMapper.countByExample(example);
        if(count > 0){
            return "手机卡号已经存在";
        }
        if(tid == null){
            terminal.setHandleTime(new Date());
            terminalMapper.insert(terminal);
        } else {
            terminal.setHandleTime(new Date());
            terminalMapper.updateByPrimaryKeySelective(terminal);
        }
        // 更新缓存信息
        terminalCacheManager.addOrUpdateTerminal(terminal);
        return null;
    }

    /* (non-Javadoc)
     * @see cn.com.gps169.bos.service.ITermialService#queryTerminalById(int)
     */
    @Override
    public Terminal queryTerminalById(int tid) {
        return terminalMapper.selectByPrimaryKey(tid);
    }

    /* (non-Javadoc)
     * @see cn.com.gps169.bos.service.ITerminalService#getUnbindVeh()
     */
    @Override
    public JSONArray getUnbindTmnl() {
        List<String> list = sqlSessionTemplate.selectList("cn.com.gps169.db.sql.CustomSqlMapper.listUnbindTmnl", null);
        
        return null;
    }

}
