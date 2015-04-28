package cn.com.gps169.bos.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * @author tianfei
 *
 */
public class UserRealm extends AuthorizingRealm  {

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
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

    /* (non-Javadoc)
     * @see org.apache.shiro.realm.AuthenticatingRealm#doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken arg0) throws AuthenticationException {
//        String username = (String) token.getPrincipal();
//        User user = userService.findUserByUsername(username);
//        
//        if(user==null){
//          //木有找到用户
//          throw new UnknownAccountException("没有找到该账号");
//        }
//        /* if(Boolean.TRUE.equals(user.getLocked())) {  
//                  throw new LockedAccountException(); //帐号锁定  
//              } */
//        
//        /**
//         * 交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以在此判断或自定义实现  
//         */
//        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(),getName());
        
        
        return null;
    }

}
