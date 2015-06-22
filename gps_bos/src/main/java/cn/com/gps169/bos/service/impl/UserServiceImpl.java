package cn.com.gps169.bos.service.impl;

import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.gps169.bos.service.IUserService;
import cn.com.gps169.db.dao.UserMapper;
import cn.com.gps169.db.model.User;
import cn.com.gps169.db.model.UserExample;

@Service
public class UserServiceImpl implements IUserService {
	
	@Autowired
	private UserMapper userMapper;

	@Override
	public JSONObject findUserByPage(Integer pageIndex, Integer pageRows, String search,Integer roleType) {
		UserExample example = new UserExample();
		UserExample.Criteria criteria = example.or();
		if(StringUtils.isNotBlank(search)){
			criteria.andUserNameLike("%"+search+"%");
		}
		if(roleType != null){
			criteria.andRoleIdEqualTo(roleType.byteValue());
		}
		int totalNum = userMapper.countByExample(example);
		example.setLimitStart(pageIndex);
		example.setLimitEnd(pageRows);
		List<User> list = userMapper.selectByExample(example);
		JSONArray array = new JSONArray();
		for(User u : list){
			array.add(JSONObject.fromObject(u));
		}
		JSONObject result = new JSONObject();
		result.put("total", totalNum);
		result.put("rows", array);
		
		return result;
	}

	@Override
	public String saveOrUpdateUser(User user) {
		//判断用户帐号是否已经存在
		UserExample example = new UserExample();
		UserExample.Criteria criteria = example.or();
		if(user.getUserId() == null){
			criteria.andAccountEqualTo(user.getAccount());
		} else {
			criteria.andAccountEqualTo(user.getAccount()).andUserIdNotEqualTo(user.getUserId());
		}
		int count = userMapper.countByExample(example);
		if(count > 0){
			return "帐号已经存在，请重新输入";
		}
		if(user.getUserId() == null){
			user.setRegisteredTime(new Date());
			user.setParentUserId(0);
			user.setRoleId((byte) 2);
			userMapper.insert(user);
		} else {
			userMapper.updateByPrimaryKeySelective(user);
		}
		
		return null;
	}

}
