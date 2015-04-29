<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style type="text/css">
html {
	height: 100%
}

body {
	height: 100%;
	margin: 0px;
	padding: 0px
}

#container {
	height: 100%
}
</style>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>仓库管理系统</title>
	<link rel="stylesheet" type="text/css" href="<c:url value="/easyui/themes/default/easyui.css" />">
	<link rel="stylesheet" type="text/css" href="<c:url value="/easyui/themes/icon.css" /> ">
	<script type="text/javascript" src="<c:url value="/js/jquery-2.1.3.min.js" /> "></script>
	<script type="text/javascript" src="http://webapi.amap.com/maps?v=1.3&key=d05cbaac9f35812b93de8ab502c65e35"></script>
	<script type="text/javascript" src="<c:url value="/js/AMap-plugin/AMap.RealPosition.js" />"></script>
</head>
<body>
	<div style="margin:2px 0;"></div>
	<div class="easyui-layout" style="width:100%;height:100%; margin-left: 4px;">
		<div id="p" data-options="region:'west'" title="车辆列表" style="width:20%;padding:10px">
			<!-- 搜索条件 -->
			<input class="easyui-searchbox" data-options="prompt:'请输入车牌',menu:'#mm',searcher:doSearch" style="width:200px"></input>
		    <div id="mm">
		        <div data-options="name:'0',iconCls:'icon-ok'">全部</div>
		        <div data-options="name:'1'">在线</div>
		        <div data-options="name:'2'">离线</div>
		    </div>
		    <!-- 车辆列表 -->
		    <div style="margin:4px 0;"></div>
			<ul class="easyui-datalist" lines="true" style="width:200px;" id="vehicleList">
			</ul>
			<!-- 分页控件 -->
		</div>
		<div data-options="region:'center'" title="地图">
			<div id="container"></div>
		</div>
	</div>
</body>
<script type="text/javascript">
	// 绘制车辆位置 图  
	var intervalVehicleId;
	markers = {}; // 即时所有汽车点
	vehicleArr = {};// 即时所有车辆对象
	var mapObj = null;
	var interval; //定时器
	realPosition = null;// 跟踪对象
	var cmarkers = [];//聚合对象 

$(document).ready(function(){
	//地图对象
    //var position=new AMap.LngLat(items[0].longitude, items[0].latitude);
    mapObj=new AMap.Map("container",{
        view: new AMap.View2D({//创建地图二维视口
        resizeEnable: true,
       }),lang:"zh_cn"//设置地图语言类型，默认：中文简体
    });
  	//地图中添加地图操作ToolBar插件
	mapObj.plugin(['AMap.ToolBar'],function(){
		//设置地位标记为自定义标记
		var toolBar = new AMap.ToolBar(); 
		mapObj.addControl(toolBar);		
	});	
	// 查询 车辆列表
	doSearch(0, '');
});
//查询车辆
function doSearch(type,content){
	// 清空地图上的覆盖物
	mapObj.clearMap();
	$('#vehicleList').empty();
	$.ajax({
	       type: 'post',
	       url: '/gps_bos/ws/0.1/monitor/allVehicles',
	       async: false,
	       data: JSON.stringify({licensePlate:content}),
	       dataType : 'JSON',
	       cache: false,
	       contentType: 'application/json;charset=utf-8',
	       success: function(items) {
	    	   $.each(items, function(key, item) {
	    		   vehicleArr[item.vid] = item;
	    		  // 构建车辆列表
	    		  var v = '<li>'+item.licensePlate+' <div style="display: inline;"> <a href="javascript:vehLocation('+item.vid+');" alt="车辆定位" title="车辆定位"><img src="<c:url value="/images/3.png"  />" style="height:16px;weight:16px;"></a>'
	    		  v += ' <a id="start'+item.vid+'" href="javascript:startRealPosition('+item.vid+');" alt="车辆跟踪" title="车辆跟踪"><img src="<c:url value="/images/car_03.png"  />" style="height:16px;weight:16px;"></a>';
	    		  v+='<a id="stop'+item.vid+'" href="javascript:stopRealPosition();" style="display:none;">停止跟踪</a>';
	    		  v+='</div></li>';
	    		  $('#vehicleList').append(v);
	              var marker = new AMap.Marker(
	                  {
	                      position : new AMap.LngLat(item.longitude, item.latitude),//基点位置                 
	                      offset : new AMap.Pixel(-14, -34),//相对于基点的偏移位置                 
	                     // icon : "http://code.mapabc.com/images/car_03.png"
	                      icon : "http://webapi.amap.com/images/3.png"
	                  });
	               marker.setMap(mapObj);
	               var info = [];  
	               info.push('<div class="easyui-tabs" style="width:60px;height:80px"><div title="位置信息" style="padding:10px">');
	               info.push("<b> 车辆信息</b>");                 
	               info.push("  车牌 :  "+item.licensePlate); 
	               info.push("  车速 : "+item.speed+" km");
	               info.push("  经纬度 : "+item.longitude+":"+item.latitude);
	               info.push("  定位时间  : "+item.sendTime);
	               info.push("  地址 : ***");
	               info.push('</div><div title="车辆信息" style="padding:10px">');
	               info.push('aabbb</div></div>');
	               var inforWindow = new AMap.InfoWindow({                 
	                    offset:new AMap.Pixel(0,-23),
	                    autoMove:true,
	                    content:info.join(""),
	                    isCustom:true
	                  });  
	              AMap.event.addListener(marker,"click",function(e){                 
	                    inforWindow.open(mapObj,marker.getPosition());                 
	              }); 
	             markers[item.vid] = marker;
	             cmarkers.push(marker);
	    	   });
	    	   //点聚合
			   var cluster;
	    	   mapObj.plugin(["AMap.MarkerClusterer"],function(){
	   				cluster = new AMap.MarkerClusterer(mapObj,cmarkers);
	   		   });
	      }
	
	});
}
//车辆定位
function vehLocation(vid){
	var marker = markers[vid];
	mapObj.setZoom(14);
	// 精确定位至车辆位置 
	mapObj.setCenter(marker.getPosition());
}
//开启实时跟踪
function startRealPosition(vehicleId) {
	intervalVehicleId = vehicleId;
    data = vehicleArr[vehicleId];
    vehLocation(vehicleId);//车辆定位
    // 显示、隐藏跟踪按钮
    $('#start'+vehicleId).hide();
    $('#stop'+vehicleId).show();
    // test跟踪
        // test跟踪
        var opts = {
            ajaxOpts: {
                url: '/gps_bos/ws/0.1/monitor/location',
                async: true,
                dataType : 'JSON',
                cache: false,
                data: {
                    updated: data.updated,
                    vid: vehicleId
                },
                preQuery: function(r) {
                    if(r && r.position && r.position.updated !== '') {
                        this.data.updated = r.position.updated;
                    }
                },
                preProcess: function(r) {
                    if(r.position && !$.isEmptyObject(r.position)) {
                       // $.extend(data,r);
                        //$.extend(r,data);
                        return {lng: r.position.longitude,lat: r.position.latitude};
                    }
                    return null;
                },
                success: function(r) {
					//$('.bottom-wrap[data-form=control-details]',wraper).trigger('initDetails',[r]);
				},
                error: function(xhr) {
                    var result = eval('(' + xhr.responseText + ')');
                    $.jRadShowMSG({message: result[0].message, level: 'error'});
                }
            },
            id: vehicleId,
            marker: markers[vehicleId],
            markerOpts: {
            	icon : "http://code.mapabc.com/images/car_03.png",
                //markerContent: '<div class="map-marker-licensePlate"><div class="car"></div></div>',
                // offset: new AMap.Pixel(-35, -16),
                autoRotation: true
            },
            timeout:3000,
            autoPan: true,
            recover: function(vehicleMarker) {
            	// 显示、隐藏跟踪按钮
           	   $('#start'+vehicleId).show();
          	   $('#stop'+vehicleId).hide();
          	 	vehicleMarker.setIcon('http://webapi.amap.com/images/3.png');
            }
        };
        realPosition = new AMap.RealPosition(mapObj, opts);
};

// 停止跟踪
function stopRealPosition() {
    if(realPosition && realPosition instanceof AMap.RealPosition) {
        realPosition.destroy();
    }
    // 清空跟踪车辆对象
    //$('a[data-opt="vehicle-position"]',wraper).text('跟踪模式');
    realPosition = null, delete realPosition;
    intervalVehicleId = null, delete intervalVehicleId;
};

</script>
<script type="text/javascript" src="<c:url value="/easyui/jquery.easyui.min.js" />"></script>
</html>