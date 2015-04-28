package cn.com.gps169.bos.shiro;

import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.gps169.db.dao.UserMapper;
import cn.com.gps169.db.model.User;
import cn.com.gps169.db.model.UserExample;

/**
 * shiro验证realm
 * @author tianfei
 *
 */
public class UserRealm extends AuthorizingRealm  {
	
	@Autowired
	private UserMapper userMapper;

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
//        String username = (String) principals.getPrimaryPrincipal();
//        
//        Set<Role> roleSet =  userService.findUserByUsername(username).getRoleSet();
//        //角色名的集合
//        Set<String> roles = new HashSet<String>();
//        //权限名的集合
//        Set<String> permissions = new HashSet<String>();
//        
//        Iterator<Role> it = roleSet.iterator();
//        while(it.hasNext()){
//          roles.add(it.next().getName());
//          for(Permission per:it.next().getPermissionSet()){
//            permissions.add(per.getName());
//          }
//        }
//
//        
//        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
//        
//        authorizationInfo.addRoles(roles);
//        authorizationInfo.addStringPermissions(permissions);
        
        
        return null;
    }

    /**
     * 身份验证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();
        UserExample example = new UserExample();
        example.or().andAccountEqualTo(username).andStatusEqualTo(1);
        List<User> users = userMapper.selectByExample(example);
        
        if(users==null || users.isEmpty()){
          //木有找到用户
          throw new UnknownAccountException("帐号或密码错误");
        }
        //当前登录用户
        User user = users.get(0);
        
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getAccount(),user.getPassword(),getName());
        
        return info;
    }

}
