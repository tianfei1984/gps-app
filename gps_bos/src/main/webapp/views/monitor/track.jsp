<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>轨迹回放</title>
<style type="text/css">
body {
	margin: 0;
	height: 100%;
	width: 100%;
	position: absolute;
}

#mapContainer {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
}

#tip {
	height: 45px;
	background-color: #fff;
	padding-left: 10px;
	padding-right: 10px;
	border: 1px solid #969696;
	position: absolute;
	font-size: 12px;
	right: 10px;
	bottom: 30px;
	border-radius: 3px;
	line-height: 45px;
}

#tip input[type='button'] {
	height: 28px;
	line-height: 28px;
	outline: none;
	text-align: center;
	padding-left: 5px;
	padding-right: 5px;
	color: #FFF;
	background-color: #0D9BF2;
	border: 0;
	border-radius: 3px;
	margin-top: 8px;
	margin-left: 5px;
	cursor: pointer;
	margin-right: 10px;
}
</style>
	<link rel="stylesheet" type="text/css" href="<c:url value="/easyui/themes/default/easyui.css" />">
	<link rel="stylesheet" type="text/css" href="<c:url value="/easyui/themes/icon.css" /> ">
	<script type="text/javascript" src="http://webapi.amap.com/maps?v=1.3&key=d05cbaac9f35812b93de8ab502c65e35"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery-2.1.3.min.js" /> "></script>
</head>
<body>
	<div style="margin:2px 0;"></div>
	<div class="easyui-layout" style="width:100%;height:100%; margin-left: 4px;">
		<div id="p" data-options="region:'west'" title="车辆列表" style="width:20%;padding:10px">
		
		<div style="margin:2px;"></div>
		<!-- 行程列表 -->
		<div class="easyui-accordion" style="width:200px;" id="track">
		</div>
		</div>
		<div data-options="region:'center'" title="地图">
			<div id="mapContainer"></div>
			<div id="tip">
				<input type="button" value="开始" onclick="startAnimation()" /> <input
					type="button" value="停止" onclick="stopAnimation()" />
			</div>
		</div>
	</div>

<script type="text/javascript">
		//初始化地图对象，加载地图
		var map = new AMap.Map("mapContainer", {
			resizeEnable : true,
			//二维地图显示视口
			view : new AMap.View2D({
				//地图显示的缩放级别
				zoom : 17
			}),
			continuousZoomEnable : false
		});
		//地图中添加地图操作ToolBar插件
		map.plugin(['AMap.ToolBar'],function(){
			//设置地位标记为自定义标记
			var toolBar = new AMap.ToolBar(); 
			map.addControl(toolBar);		
		});	
		//AMap.event.addListener(map, "complete", completeEventHandler);
		$(document).ready(function(){
			$.ajax({
			       type: 'get',
			       url: '/hypt_bos/ws/0.1/monitor/track',
			       async: false,
			       dataType : 'JSON',
			       cache: false,
			       contentType: 'application/json;charset=utf-8',
			       success: function(items) {
			    	   $.each(items, function(key, item) {
			    		   var t = ' <div title="'+item.licensePlate+'" style="overflow:auto;padding:10px;">';
			    		   t+='<ul class="easyui-datalist" lines="true" style="width:170px;">';
			    		   $.each(item.tracks,function(k,track){
			    			   var l = '<li>'+track.recDay+'  <a href="javascript:completeEventHandler('+track.tid+')">轨迹查看</a></li>'
			    			   t+=l;
			    		   });
			    		   t+='</ul></div>';
						$('#track').append(t);
			    	   });
			       }
			});
			
		});
	    
		//地图图块加载完毕后执行函数
		function completeEventHandler(tripId) {
			$.ajax({
		        type: 'get',
		        url: '/hypt_bos/ws/0.1/monitor/trip?tripId='+tripId,
		        async: false,
		        dataType : 'JSON',
		        cache: false,
		        contentType: 'application/json;charset=utf-8',
		        success: function(items) {
		        	
		        	lineArr = new Array();
		        	var lngX = 116.397428;
		            var latY = 39.90923;
		            var defX = 116.397428;   
                    var defY = 39.90923;
		            $.each(items, function(key, item) {
		            	if(key == 0){
		            		defX = item.longitude;
		            		defY = item.latitude;
		            	}
		        		lngX = item.longitude;
	        			latY = item.latitude;
        				lineArr.push(new AMap.LngLat(lngX, latY));
		        	});
		            marker = new AMap.Marker({
	                    map : map,
	                    //draggable:true, //是否可拖动
	                    position : new AMap.LngLat(defX, defY),//基点位置
	                    icon : "http://code.mapabc.com/images/car_03.png", //marker图标，直接传递地址url
	                    offset : new AMap.Pixel(-26, -13), //相对于基点的位置
	                    autoRotation : true
	                });
		           //绘制轨迹
		            var polyline = new AMap.Polyline({
		                map : map,
		                path : lineArr,
		                strokeColor : "#00A",//线颜色
		                strokeOpacity : 1,//线透明度
		                strokeWeight : 3,//线宽
		                strokeStyle : "solid"//线样式
		            });
		            map.setFitView();
		        }
		    });
        }
			
		function startAnimation() {
			marker.moveAlong(lineArr, 500);
		}
		function stopAnimation() {
			marker.stopMove();
		}
	</script>
<script type="text/javascript" src="<c:url value="/easyui/jquery.easyui.min.js" />"></script>
</body>
</html>

