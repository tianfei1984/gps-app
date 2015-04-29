package cn.com.gps169.bos.resource;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author tianfei
 *
 */
@Controller
@RequestMapping("login")
public class LoginController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    /**
     * 不带验证码的用户身份验证
     * @param params
     * @return
     */
    @RequestMapping(value="loginWithoutCaptcha",method=RequestMethod.POST,consumes="application/json")
    public @ResponseBody String loginWithoutCaptcha(@RequestBody JSONObject params){
    	//用户登陆信息
        String username = params.optString("username");
        String password = params.optString("password");
        JSONObject result = new JSONObject();
        result.put("code", 0);//是否成功
        try{
        	UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        	SecurityUtils.getSubject().login(token);
        	result.put("code", 1);
        } catch (UnknownAccountException e){
            result.put("msg", "账号不存在!");
        } catch (IncorrectCredentialsException e){
        	result.put("msg", "用户名/密码错误!");
        } catch (ExcessiveAttemptsException e) {
        	result.put("msg", "账户错误次数过多,暂时禁止登录!");
        } catch (Exception e){
        	result.put("msg", "未知错误!");
        }
        
        return result.toString();
    }
    
    /**
     * 带验证码的用户登陆验证
     * @param params
     * @return
     */
    @RequestMapping(value="loing",method=RequestMethod.POST,consumes="application/json")
    public @ResponseBody String login(@RequestBody JSONObject params,HttpServletRequest request){
    	//用户登陆信息
        String username = params.optString("username");
        String password = params.optString("password");
        String captcha = params.optString("captcha");
        JSONObject result = new JSONObject();
        result.put("code", 0);
        if(!captcha.equalsIgnoreCase(request.getSession().getAttribute(CaptchaProducer.CAPTCHA_SESSION_KEY).toString())){
        	result.put("code", 2);
        	result.put("msg", "验证码错误！");
        	return result.toString();
        }
        try{
        	UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        	SecurityUtils.getSubject().login(token);
        	result.put("code", 1);
        } catch (UnknownAccountException e){
            result.put("msg", "账号不存在!");
        } catch (IncorrectCredentialsException e){
        	result.put("msg", "用户名/密码错误!");
        } catch (ExcessiveAttemptsException e) {
        	result.put("msg", "账户错误次数过多,暂时禁止登录!");
        } catch (Exception e){
        	result.put("msg", "未知错误!");
        }
        
        return result.toString();
    }
    
    /**
     * 退出
     * @return
     */
    @RequestMapping("logout")
    @ResponseBody
    public String logout(HttpServletRequest request) {
    	JSONObject result = new JSONObject();
    	try{
    		result.put("code", 1);
    		request.getSession().invalidate();
    		SecurityUtils.getSubject().logout();
    	} catch(Exception e){
    		
    	}
    	
    	return result.toString();
    }
    
    /**
     * 生成验证码
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value="captcha",produces=MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getCaptcha(HttpServletRequest request) throws IOException{
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        String capText = CaptchaProducer.GenerateRandomCode();
        request.getSession(true).setAttribute(CaptchaProducer.CAPTCHA_SESSION_KEY, capText);
        final BufferedImage challenge = CaptchaProducer.DrawPicture(capText);
        ImageIO.write(challenge, "jpeg", jpegOutputStream);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<byte[]>(jpegOutputStream.toByteArray(), headers, HttpStatus.CREATED);
    }

}
