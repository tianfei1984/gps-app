package cn.com.gps169.bos.service;

import java.util.List;

import net.sf.json.JSONObject;
import cn.com.gps169.db.model.User;

/**
 * 用户接口
 * @author tianfei
 *
 */
public interface IUserService {

	/**
	 * 分页查询用户
	 * @param pageIndex
	 * @param pageRows
	 * @param search
	 * @return
	 */
	JSONObject findUserByPage(Integer pageIndex,Integer pageRows,String search,Integer roleType);
	
	/**
	 * 增加或更新用户信息
	 * @param user
	 * @return
	 */
	String saveOrUpdateUser(User user);
	
	/**
	 * 根据用户ID查询用户信息
	 * @param userId
	 * @return
	 */
	User findUserById(int userId);
	
	
}
