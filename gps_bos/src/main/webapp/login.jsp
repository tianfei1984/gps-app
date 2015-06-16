<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset=utf-8>
<link rel="shortcut icon" href="./css/images/webicon.png"
	type="image/x-icon" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no" />
<meta content="yes" name="apple-mobile-web-app-capable" />
<meta content="telephone=no" name="format-detection" />
<meta content="black" name="apple-mobile-web-app-status-bar-style" />
<title>登录</title>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/login.css" />" />
<script type="text/javascript"
	src="<c:url value="/js/jquery-2.1.3.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/js/md5.js" />"></script>
<script type="text/javascript" src="<c:url value="/js/cs_base.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/js/jquery.json-2.2.js" />"></script>
<script type="text/javascript" src="<c:url value="/js/login.js" />"></script>
</head>
<body>
	<div class="login-wrap">
		<div class="login-wrap-inner">
			<div class="login-logo-wrapper">
				<h1>AnyTimeAnyWhere</h1>
			</div>
			<div class="login-container">
				<div class="login-content">
					<div class="login-content-main">
						<form method="post">
							<ul>
								<li>
									<div class="login-input">
										<span class="icon login-name-icon"></span><input id="email"
											type="text" value=""
											onBlur="javascript:index.checkUserLoginOneFocus(this.id);"
											onFocus="javascript:index.removeerrorInfo('emailerror');"
											onkeydown="do_if_return(event, index.newSubmit)"
											placeholder="请输入登录账号" />
									</div>
									<div id="emailerror" class="on-error" style="display: none">请输入登录账号</div>
								</li>
								<li>
									<div class="login-input">
										<span class="icon login-password-icon"></span><input
											id="password" type="password" value=""
											onBlur="javascript:index.checkUserLoginOneFocus(this.id);"
											onFocus="javascript:index.removeerrorInfo('errorInfo');"
											onKeyDown="do_if_return(event, index.newSubmit)"
											placeholder="请输入密码" />
									</div>
									<div id="errorInfo" class="on-error" style="display: none">请输入密码</div>
									<div class="op-tips">
										<span class="arrow"></span>大写锁定已打开
									</div>
								</li>
								<li>
									<div class="clearfix">
										<div class="check-code-box" style="display: none">
											<div class="clearfix">
												<span class="check-code"> <span
													class="icon login-code-icon"></span> <input id="captcha"
													type="text"
													onFocus="javascript:index.removeerrorInfo('captchaerror');"
													onkeydown="do_if_return(event, index.newSubmit)" />
												</span> <span class="captcha-img"><span><img
														alt="验证码" title="点击图片刷新验证码" align="absmiddle"
														id="captchimg"
														onclick="index.refreshCaptchaImg('captchimg');return false;" /></span></span>
											</div>
											<a href="javascript:void(0)" class="forgot-password">忘记密码？</a>
										</div>
										<div class="buttons-wrap">
											<span id="loginBtn" class="ui-btn-login"
												onClick="index.newSubmit()">登&nbsp;录</span>
										</div>
									</div>
									<div id="captchaerror" class="on-error" style="display: none"></div>
								</li>
							</ul>
							<input id="domain" type="hidden" value="10" />
						</form>
					</div>
				</div>
			</div>
			<div class="footer">© ****&nbsp;&nbsp;版权所有</div>
			<div class="version-tips">（访问本网站建议您使用Chrome、IE9及以上版本浏览器，至少1024*768分辨率）</div>
		</div>
	</div>
	<script type="text/javascript">
$(document).ready(function(){
    function  detectCapsLock(event){
        var e = event||window.event;
        var oTip = $('.op-tips');
        var keyCode  =  e.keyCode||e.which; // 按键的keyCode 
        var isShift  =  e.shiftKey ||(keyCode  ==   16 ) || false ; // shift键是否按住
         if (
         ((keyCode >=   65   &&  keyCode  <=   90 )  &&   !isShift) // Caps Lock 打开，且没有按住shift键 
         || ((keyCode >=   97   &&  keyCode  <=   122 )  &&  isShift)// Caps Lock 打开，且按住shift键
         ){
            oTip.show();
         }else{
            oTip.hide();
         } 
    }
    $('#password').keypress(function(e){
        detectCapsLock(e);
    });
});
</script>
</body>
</html>