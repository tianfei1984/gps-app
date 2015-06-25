<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>星通车辆监控系统</title>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/easyui/themes/default/easyui.css" />">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/easyui/themes/icon.css" /> ">
<script type="text/javascript" src="<c:url value="/js/md5.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/js/jquery-2.1.3.min.js" /> "></script>
<script type="text/javascript"
	src="<c:url value="/easyui/jquery.easyui.min.js" />"></script>
</head>
<body>
	<div style="margin: 2px 0;"></div>
	<!-- 数据表 -->
	<table id="dataGrid" class="easyui-datagrid" title="车辆列表"
		style="width: 100%; height: 100%; margin-left: 4px;">
		<thead>
			<tr>
				<th data-options="field:'userName',width:120,align:'center'">车主/业户</th>
				<th data-options="field:'telephone',width:100,align:'center'">联系电话</th>
				<th data-options="field:'account',width:200,align:'center'">登录帐号</th>
				<th data-options="field:'roleId',width:80,align:'center',formatter:function(value,row,index){ return formatterUserType(value,row,index);}">用户类型</th>
				<th data-options="field:'status',width:100,align:'center',formatter:function(value,row,index){ return formatterUserStatus(value,row,index);}">帐号状态</th>
				<th data-options="field:'registeredTime',width:100,align:'center'">注册时间</th>
				<th data-options="field:'lastLoginTime',width:100,align:'center'">最后登录时间</th>
			</tr>
		</thead>
	</table>
	<!-- 搜索信息 -->
	<div id="search" style="padding: 2px 5px;">
		用户类型: <input class="easyui-searchbox"
			data-options="prompt:'请输入车主/业主名称',menu:'#mm',searcher:doSearch"
			style="width: 300px"></input>
		<div id="mm">
			<div data-options="name:'0',iconCls:'icon-ok'">全部</div>
			<div data-options="name:'2'">车主</div>
			<div data-options="name:'3'">司机</div>
		</div>
		&nbsp;&nbsp;&nbsp; 
	<a href="javascript:openWin();" class="easyui-linkbutton" iconCls="icon-add" plain="true">增加用户</a>
	<a href="javascript:update();" class="easyui-linkbutton" iconCls="icon-edit" plain="true">修改用户</a>
	</div>
	
	<script type="text/javascript">
	$(document).ready(function(){
		$('#dataGrid').datagrid({
			rownumbers:true,
			singleSelect:true,
			fitColumns:true,
			loadMsg:'数据加载中，请稍候...',
			url:'/gps_bos/ws/0.1/user/page',method:'get',
			toolbar:'#search',
			pagination:true
		});
		$('#dataGrid').datagrid('getPager').pagination({
			pageSize: 10,//每页显示的记录条数，默认为10 
	        pageList: [5,10,15],//可以设置每页记录条数的列表 
	        beforePageText: '第',//页数文本框前显示的汉字 
	        afterPageText: '页    共 {pages} 页', 
	        displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录'
		});
		//用户类型切换
		$('#roleId').combo({
			onChange:function(n,o){
				if(n == 2){
					$('#parentUserId').combo('clear');
					$('#parentUserId').combo({
					    required:false
					});
					$('#parentUserId').combo("disable",true);
					$('#parentUserId').combo('setValue',0);
				} else if(n == 3){
					$('#parentUserId').combo("enable",true);
					$('#parentUserId').combo({
					    required:true,
					    missingMessage:'请选择所属用户',
					    invalidMessage:'所属用户不能为空'
					});
				}
			}
		});
	});
	
	//  格式化用户类型 
	function formatterUserType(value,row,index){
		if(value == '2'){
			return '车主';
		} else if(value == '3'){
			return '司机'
		} else {
			return value;
		}
	}
	//格式化帐号状态
	function formatterUserStatus(value,row,index){
		if(value ==0){
			return '正常';
		} else {
			return '停用';
		}
	}
	function update(){
		var data = $('#dataGrid').datagrid('getSelected');
		if(data){
			$('#userForm').form('load','/gps_bos/ws/0.1/user/get?userId='+data.userId);
			 $('#userWin').window("open");
		}
	}
	//r搜索
	function doSearch(value,name){
		$('#dataGrid').datagrid("load",{
			search:value,
			roleType:name
		});
	}
	//增加车主帐号
	function openWin(){
		$('#userForm').form('clear');
		$('#roleId').combo('setValue',2);
		$('#roleId').combo('setText','车主');
		$('#status').combo('setValue',0);
		$('#status').combo('setText','正常');
	    $('#userWin').window("open");
	}
	function closeWin(){
		$('#userWin').window('close');
	}
	$.fn.datebox.defaults.formatter = function(date){
		var y = date.getFullYear();
		var m = date.getMonth()+1;
		var d = date.getDate();
		return y+'-'+m+'-'+d;
	}
	function submitForm(){
		if(!$('#userForm').form('validate')){
			return;
		}
		var password = $.trim($('#password').val());
		var password1 = $.trim($('#password1').val());
		if($('#userId').val() == ''){
			if(password =='' || password1 == ''){
				$.messager.alert('增加用户','密码不能为空','warning');
			    return ;	
			}
		}
		if(password != password1){
			 $.messager.alert('增加用户','两次输入密码不一致','warning');
			 return;
		}
		if($('#roleId').val() == 3){
		}
		var data = $("#userForm").serializeArray(); //自动将form表单封装成json
		var d = {};
		//将form转换成json
		$.each(data,function(){
			if(this.name != 'password1'){
				if(this.name == 'password')
					d[this.name] = hex_md5(password);
				else 
				    d[this.name] = this.value;
			}
		});
		$.ajax({
	           type: 'post',
	           url: '/gps_bos/ws/0.1/user/add',
	           async: false,
	           data: JSON.stringify(d),
	           dataType : 'JSON',
	           cache: false,
	           contentType: 'application/json;charset=utf-8',
	           success: function(result) {
	        	   if(result.flag == 'success'){
	        		   closeWin();
	        		   $.messager.alert('增加用户',"增加用户成功",'info');
	        	   } else {
	        		   $.messager.alert('增加用户',result.msg,'warning');
	        	   }
	           },
	           error:function(e){
	        	   $.messager.alert('增加用户',"系统错误，请与管理员联系",'error');
	           }
		});
	}
	</script>
	<div id="userWin" class="easyui-window" " title="增加车主帐号" closed="true"
		modal="true" shadow="true" collapsible="false" minimizable="false"
		maximizable="false"
		style="width: 600px; height: 300px; background: #fafafa;"">
		<div class="easyui-layout" fit="true" align="center">
			<form id="userForm" method="post">
				<table cellpadding="5">
				    <input type="hidden" id="userId" name="userId">
				    <tr>
                       <td>所属用户:</td>
                       <td>
                           <input class="easyui-combobox" name="parentUserId" id="parentUserId" data-options="
                            url:'/gps_bos/ws/0.1/user/owner',
                            method:'get',
                            valueField:'userId',
                            textField:'userName'">
                       </td>
                       <td>用户类型：</td>
                       <td><select class="easyui-combobox" name="roleId" id="roleId" style="width: 123px;" data-options="required:true">
                                <option value="2">车主</option>
                                <option value="3">司机</option>
                        </select></td>
                    </tr>
					<tr>
						<td>用户名称:</td>
						<td><input class="easyui-textbox" type="text"
							name="userName"
							data-options="required:true,missingMessage:'车主/业户不能为空',invalidMessage:'请输入用户名称'"></input></td>
						<td>登录帐号:</td>
						<td><input class="easyui-textbox" type="text" name="account" 
							data-options="required:true,missingMessage:'登录帐号不能为空',prompt:'车主手机号'"></input></td>
					</tr>
					<tr>
						<td>联系电话:</td>
						<td><input class="easyui-textbox" type="text" name="telephone"></input></td>
						<td>帐号状态:</td>
						<td><select class="easyui-combobox" name="status" id="status" style="width: 123px;">
								<option value="0">正常</option>
								<option value="1">停用</option>
						</select></td>
					</tr>
					<tr>
						<td>密码:</td>
						<td><input class="easyui-textbox" type="password" name="password" id="password"></input></td>
						<td>确认密码:</td>
						<td><input class="easyui-textbox" type="password" name="password1" id="password1"></input></td>
					</tr>
				</table>
			</form>
			<div style="text-align: center; padding: 20px">
				<a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()">保存</a> 
				<a href="javascript:void(0)" class="easyui-linkbutton" onclick="closeWin()">取消</a>
			</div>
		</div>
	</div>
</body>
</html>