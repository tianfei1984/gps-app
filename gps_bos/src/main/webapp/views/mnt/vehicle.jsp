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
				<th data-options="field:'vid',width:80,align:'center'">车辆ID</th>
				<th data-options="field:'licesePlate',width:120,align:'center'">车牌号码</th>
				<th data-options="field:'ein',width:200,align:'center'">发动机号</th>
				<th data-options="field:'vin',width:200,align:'center'">车驾号码</th>
				<th data-options="field:'vin',width:80,align:'center'">品牌</th>
				<th data-options="field:'status',width:100,align:'center'">车辆状态</th>
				<th data-options="field:'type',width:100,align:'center'">车辆类别</th>
				<th data-options="field:'created',width:140,align:'center'">创建时间</th>
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
		&nbsp;&nbsp;&nbsp;
		<a href="javascript:openForm();" class="easyui-linkbutton" iconCls="icon-add" plain="true">增加车辆</a>
		<a href="javascript:update();" class="easyui-linkbutton" iconCls="icon-edit" plain="true">修改车辆</a>
	</div>
	<script type="text/javascript">
	function update(){
		var data = $('#dataGrid').datagrid('getSelected');
		if(data){
			$('#ff').form('load','/gps_bos/ws/0.1/vehicle/get?vid='+data.vid);
			openForm();
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
	function openForm(){
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
	function submitForm(){
		var data = $("#ff").serializeArray(); //自动将form表单封装成json
		var d = {};
		//将form转换成json
		$.each(data,function(){
			d[this.name] = this.value;
		});
		$.ajax({
	           type: 'post',
	           url: '/gps_bos/ws/0.1/vehicle/add',
	           async: false,
	           data: JSON.stringify(d),
	           dataType : 'JSON',
	           cache: false,
	           contentType: 'application/json;charset=utf-8',
	           success: function(result) {
	        	   if(result.flag == 'success'){
	        		   closeForm();
	        		   $.messager.alert('增加车辆',"增加车辆成功",'info');
	        	   } else {
	        		   $.messager.alert('增加车辆',result.msg,'warning');
	        	   }
	           },
	           error:function(e){
	        	   $.messager.alert('增加车辆',"系统错误，请与管理员联系",'error');
	           }
		});
	}
	</script>
	<div id="veh" class="easyui-window"" title="增加车辆" closed="true" modal="true" 
		shadow="true" collapsible="false" minimizable="false"  maximizable="false"  style="width:550px;height:300px; background: #fafafa;"">
		<div class="easyui-layout" fit="true" align="center">
	    <form id="ff" method="post" >
	    	<table cellpadding="5">
	    		<tr>
	    			<td>车牌号码:</td>
	    			<td><input class="easyui-textbox" type="text" name="licensePlate" data-options="required:true,missingMessage:'车辆号码不能为空'"></input></td>
	    			<td>发动机号:</td>
	    			<td><input class="easyui-textbox" type="text" name="ein" data-options="required:true,missingMessage:'发动机号不能为空'"></input></td>
	    		</tr>
	    		<tr>
	    			<td>车架号码:</td>
	    			<td><input class="easyui-textbox" type="text" name="vin"></input></td>
	    			<td>品牌:</td>
                    <td>
                        <select class="easyui-combobox" name="styleId">
                        <option value="0">请选择</option>
                        </select>
                    </td>
	    		</tr>
	    		<tr>
	    			<td>车型:</td>
                    <td>
                        <select class="easyui-combobox" name="modelId">
                        <option value="0">请选择</option>
                        </select>
                    </td>
                    <td>年款:</td>
                    <td>
                        <select class="easyui-combobox" name="brandId">
                        <option value="0">请选择</option>
                        </select>
                    </td>
	    		</tr>
	    		<tr>
	    		     <td>车辆类型:</td>
                    <td>
                        <select class="easyui-combobox" name="type">
                        <option value="1">货运车辆</option>
                        </select>
                    </td>
                     <td>车辆状态:</td>
                    <td>
                        <select class="easyui-combobox" name="status">
                        <option value="1">正常</option>
                        <option value="2">停用</option>
                        </select>
                    </td>
	    		</tr>
	    	</table>
	    </form>
	    <div style="text-align:center;padding:20px">
	    	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()">保存</a>
	    	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="closeForm()">取消</a>
	    </div>
	    </div>
	</div>
	
</body>
</html>