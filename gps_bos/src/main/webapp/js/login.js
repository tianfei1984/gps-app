function getOrganizationAbbrev() {
	var url = window.location.href;
	if (url.indexOf("backurl=") != -1) {
		url = url.substring(0, url.indexOf("backurl=") - 1);
	}
	var regex = /\/([a-zA-Z]+)(\/)?([a-zA-Z_]+\.jsp)?$/;
	url.match(regex);
	return RegExp.$1;
}
var cookie = {
	set : function(name, value, expireDays){
		var days = expireDays == null ? 30 : expireDays;// 过期天数，默认30天
		var exp = new Date();
		exp.setTime(exp.getTime() + days * 24 * 60 * 60 * 1000);
		document.cookie = name + "="+ value + ";expires=" + exp.toGMTString();
	},
	get : function(name){
		var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
		if(arr != null) {
			return arr[2];
		}
		return null;
	},
	remove : function(name){
		var exp = new Date();
		exp.setTime(exp.getTime() - 1);
		var cval = this.get(name);
		if(cval != null) {
			document.cookie= name + "=" + cval + ";expires=" + exp.toGMTString();
		}
	}
};
var index = {
	logOutMsg: '您尚未登录、请点击确定重新登录！',
	map: null,
	nowShowDiv: "",
	loginCount: 0,
	regex: {
		telephone: /(^((0[1,2]{1}\d{1}-?\d{8})|(0[3-9]{1}\d{2}-?\d{7,8}))$)|(^0?(13[0-9]|15[0-35-9]|18[0123456789]|14[57]|17[678])[0-9]{8}$)/,
		email: /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/
	},
	/**
	 * 登录验证。
	 */
	newSubmit: function() {
		var self = this;
		$('#errorInfo').css("display", "none");
		var account = $("#email").val();
		var password = $("#password").val();
		var abbrev = getOrganizationAbbrev();
		var captcha = $("#captcha").val();
		var url = "/gps_bos/ws/0.1/login/loginWithoutCaptcha";
		if (account == "" || password == "") {
			if (account == "") {
				$('#emailerror').html("请输入登录账号").show();
			}
			if (password == "") {
				$('#errorInfo').html("请输入密码").show();
			}
			return;
		}
		index.loginCount++;
		if (captcha == "" && index.loginCount > 3) {
			url = "/gps_bos/ws/0.1/login/login";
			$('#captchaerror').html("验证码不能为空").show();
			return;
		}
		var loginData = {
			username: account,
			password: hex_md5(password),
			code: abbrev
		};
		if (index.loginCount > 3) {
			loginData.captcha = captcha;
		}
		$.ajax({
			url: url,
			contentType: "application/json;charset=utf-8",
			dataType: "json",
			type: "POST",
			data: $.toJSON(loginData),
			success: function(data) {
				if(data.code == 1){
					var indexUrl = "main.jsp";
					if(!!window.localStorage) {
						window.localStorage.setItem('loginHTML', window.location.href);
						// 超时退出后，登陆调到退出前页面
						var hash = localStorage.getItem('sc-login-hash');
						if(hash != undefined && 'string' == typeof hash && hash !== '') {
							hash.indexOf('#') == -1 && (hash = '#' + hash);
							indexUrl += hash;
						}
						localStorage.removeItem('sc-login-hash');
					}
					window.location = indexUrl;
				} else {
					if (index.loginCount == 3) {
						$(".check-code-box").show();
					}
					var responseText = data.msg;
					$('#errorInfo').html(responseText).show();
					try {
						if (index.loginCount == 3) {
							self.refreshCaptchaImg("captchimg");
						}
					} catch(e) {
					}
				}
			},
			error: function(xhr) {
				alert("登陆异常！");
			}
		});

	},
	/**
	 *退出登录。
	 */
	logout: function() {
		$.ajax({
			url: '/gps_bos/ws/0.1/login/logout',
			async: false,
			dataType: "json",
			success: function(data) {
				alert(data);
				if(window.sessionStorage) {
					sessionStorage.clear();
				}
				window.location = "/gps_bos/login.jsp";
			},error: function(xhr) {
				alert("退出异常！");
			}
		});
	},
	/**
	 * 更新验证图片
	 */
	refreshCaptchaImg: function(imageId) {
		$('#' + imageId).attr("src", "/gps_bos/ws/0.1/login/captcha?a=" + index.getRandomStr());
	},
	/**
	 * 随机数字
	 */
	getRandomStr: function() {
		return parseInt(Math.random() * 1000);
	},
	/**
	 * 用户登录验证
	 * @param id
	 */
	checkUserLoginOneFocus: function(id) {
		var flag = false;
		if (id == "email") {
			var account_no = $('#email').val();
			if (account_no == "" || account_no == null) {
				$('#hiddendotype').val("no");
				$('#emailerror').html("请输入登录账号").show();
			}
			if (account_no.length > 255) {
				$('#hiddendotype').val("no");
				$('#emailerror').html("账号长度过长。").show();
			}
		} else if (id == "password") {
			/**创建密码*/
			var password = $('#password').val();
			if (password == "" || password == null) {
				$('#hiddendotype').val("no");
				$('#errorInfo').html("请输入密码").show();
			} else {
				flag = true;
			}
		} else if (id == "code") {
			var abbrev = $('#code').val();
			if (abbrev == "" || abbrev == null) {
				$('#hiddendotype').val("no");
				$('#codeerror').html("请输入机构代码").show();
			}
		} else if (id == "captcha") {
			var abbrev = $('#captcha').val();
			if (abbrev == "" || abbrev == null) {
				$('#captchaerror').html("请输入验证码").show();
			}
		}
		if (flag) {
			$("#login").attr("disabled", false);
		}
	},
	/**
	 * 移除异常信息
	 */
	removeerrorInfo: function(id) {
		$('#' + id + '').html("").hide();
	}
};
(function(){
	jQuery.fn.initMulitSelect = function() {
		var divWraper = $(this);
		$('.left-select', divWraper).unbind().dblclick(function() {
			var value = $(this).find('option:selected');
			$('.right-select', divWraper).append(value);
		});
		$('.right-select', divWraper).unbind().dblclick(function() {
			var value = $(this).find('option:selected');
			$('.left-select', divWraper).append(value);
		});
		$('.move-right', divWraper).unbind().click(function() {
			var selectOPT = $('.left-select', divWraper).find('option:selected');
			if (selectOPT.length == 0) {
				selectOPT = $('.left-select', divWraper).find('option:eq(0)');
			}
			$('.right-select', divWraper).append(selectOPT);
		});
		$('.move-right2', divWraper).unbind().click(function() {
			var selectOPT = $('.left-select', divWraper).find('option');
			$('.right-select', divWraper).append(selectOPT);
		});
		$('.move-left', divWraper).unbind().click(function() {
			var selectOPT = $('.right-select', divWraper).find('option:selected');
			if (selectOPT.length == 0) {
				selectOPT = $('.right-select', divWraper).find('option:eq(0)');
			}
			$('.left-select', divWraper).append(selectOPT);
		});
		$('.move-left2', divWraper).unbind().click(function() {
			var selectOPT = $('.right-select', divWraper).find('option');
			$('.left-select', divWraper).append(selectOPT);
		});
		return this;
	};
})(jQuery);