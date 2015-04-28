package cn.com.gps169.bos.resource;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
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
     * 
     * @param params
     * @return
     */
    @RequestMapping(value="loginWithoutCaptcha",method=RequestMethod.POST,consumes="application/json")
    public @ResponseBody String loginWithoutCaptcha(@RequestBody JSONObject params){
        System.out.println("用户名称："+params.optString("username"));
        System.out.println("用户密码："+params.optString("password"));
        
        return params.toString();
    }
    
    @RequestMapping(value="loing",method=RequestMethod.POST,consumes="application/json")
    public @ResponseBody String login(@RequestBody JSONObject params){
        System.out.println("用户名称："+params.optString("username"));
        System.out.println("用户密码："+params.optString("password"));
        System.out.println("验证码："+params.optString("captcha"));
        return params.toString();
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
