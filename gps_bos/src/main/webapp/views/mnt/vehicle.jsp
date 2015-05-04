<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>仓库管理系统</title>
	<link rel="stylesheet" type="text/css" href="<c:url value="/easyui/themes/default/easyui.css" />">
	<link rel="stylesheet" type="text/css" href="<c:url value="/easyui/themes/icon.css" /> ">
	<script type="text/javascript" src="<c:url value="/js/jquery-2.1.3.min.js" /> "></script>
	<script type="text/javascript" src="<c:url value="/easyui/jquery.easyui.min.js" />"></script>
</head>
<body>
	<div style="margin:2px 0;"></div>
	<!-- 数据表 -->
	<table id="dataGrid" class="easyui-datagrid" title="车辆列表" style="width:100%;height:450px;margin-left: 4px;" 
			data-options="rownumbers:true,singleSelect:true,url:'/gps_bos/ws/0.1/vehicle/page',method:'get',toolbar:'#tb',footer:'#ft',pagination:true,
				pageSize:10" >
		<thead>
			<tr>
				<th data-options="field:'vid',width:80">车辆ID</th>
				<th data-options="field:'licesePlate',width:100">车牌号</th>
				<th data-options="field:'ein',width:80,align:'right'">车架号</th>
				<th data-options="field:'sim',width:80,align:'right'">SIM</th>
				<th data-options="field:'status',width:60,align:'center'">车辆状态</th>
				<th data-options="field:'attr1',width:240">创建时间</th>
			</tr>
		</thead>
	</table>
	<!-- 搜索信息 -->
	<div id="tb" style="padding:2px 5px;">
		车辆状态: 
		<input class="easyui-searchbox" data-options="prompt:'请输入车牌号',menu:'#mm',searcher:doSearch" style="width:300px"></input>
		<div id="mm">
			<div data-options="name:'0',iconCls:'icon-ok'">全部</div>
			<div data-options="name:'1'">正常</div>
			<div data-options="name:'2'">停用</div>
		</div>
		<br>
		<a href="javascript:addVehicle();" class="easyui-linkbutton" iconCls="icon-add" plain="true">增加车辆</a>
		<a href="javascript:update();" class="easyui-linkbutton" iconCls="icon-edit" plain="true">修改车辆</a>
		<a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除车辆</a>
	</div>
	<script type="text/javascript">
	function update(){
		var data = $('#dataGrid').datagrid('getSelected');
		if(data){
			alert(data.licesePlate);
		}
	}
	//r搜索
	function doSearch(value,name){
		$('#dataGrid').datagrid("load",{
			licensePlate:value,
			status:name
		});
	}
	//添加车辆
	function addVehicle(){
		$('#veh').window("open");
	}
	function closeForm(){
		$('#veh').window('close');
	}
	$.fn.datebox.defaults.formatter = function(date){
		var y = date.getFullYear();
		var m = date.getMonth()+1;
		var d = date.getDate();
		return y+'-'+m+'-'+d;
	}
	</script>
	<div id="veh" class="easyui-window"" title="增加车辆" closed="true" modal="true" 
		shadow="true" collapsible="false" minimizable="false"  maximizable="false"  style="width:500px;height:300px; background: #fafafa;"">
		<div class="easyui-layout" fit="true" align="center">
	    <form id="ff" method="post">
	    	<table cellpadding="5">
	    		<tr>
	    			<td>Name:</td>
	    			<td><input class="easyui-textbox" type="text" name="name" data-options="required:true"></input></td>
	    			<td>Name:</td>
	    			<td><input class="easyui-textbox" type="text" name="name" data-options="required:true"></input></td>
	    		</tr>
	    		<tr>
	    			<td>Email:</td>
	    			<td><input class="easyui-textbox" type="text" name="email" data-options="required:true,validType:'email'"></input></td>
	    		</tr>
	    		<tr>
	    			<td>Subject:</td>
	    			<td><input class="easyui-textbox" type="text" name="subject" data-options="required:true"></input></td>
	    		</tr>
	    		<tr>
	    			<td>Message:</td>
	    			<td><input class="easyui-textbox" name="message" data-options="multiline:true" style="height:60px"></input></td>
	    		</tr>
	    		<tr>
	    			<td>车辆型号:</td>
	    			<td>
	    				<select class="easyui-combobox" name="language">
	    				<option value="vi">Vietnamese</option>
	    				</select>
	    			</td>
	    		</tr>
	    	</table>
	    </form>
	    <div style="text-align:center;padding:5px">
	    	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()">保存</a>
	    	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="closeForm()">取消</a>
	    </div>
	    </div>
	</div>
	
</body>
</html>