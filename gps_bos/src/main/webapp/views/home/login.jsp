<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>仓库管理系统</title>
	<link rel="stylesheet" href="<c:url value="/easyui/themes/default/easyui.css"/>" type="text/css" media="screen" />
	<link rel="stylesheet" href="<c:url value="/easyui/themes/icon.css"/>" type="text/css" media="screen" />
	<link rel="stylesheet" href="<c:url value="/css/main.css"/>" type="text/css" media="screen" />
	<script src="<c:url value="/easyui/jquery-1.6.2.min.js"/>"></script>
	<script src="<c:url value="/easyui/jquery.easyui.min.js"/>"></script>
	<script src="<c:url value="/easyui/locale/easyui-lang-zh_CN.js"/>"></script>
</head>
<body style="visibility:visible">
	<div class="easyui-dialog" style="width:500px;height:300px;background:#fafafa;overflow:hidden"
			title="登录系统" closable="false" border="false">
		<div class="header" style="height:60px;">
			<div class="toptitle">仓库管理系统</div>
		</div>
		<div style="padding:60px 0;">
			<form action="<c:url value='/home/login'/>" method="post">
				<div style="padding-left:150px">
					<table cellpadding="0" cellspacing="3">
						<tr>
							<td>登录帐号</td>
							<td><input name="username"></input></td>
						</tr>
						<tr>
							<td>登录密码</td>
							<td><input type="password" name="password"></input></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td></td>
						</tr>
						<tr>
							<td></td>
							<td>
								<input class="login" type="submit" value="" style="width:74px;height:21px;border:0"></input>
							</td>
						</tr>
					</table>
				</div>
				
			</form>
		</div>
	</div>
</body>
</html>