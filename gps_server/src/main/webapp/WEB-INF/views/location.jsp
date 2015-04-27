<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<!DOCTYPE HTML>
<html>
<head>
<meta name="viewport" content="initial-scale=1.0,user-scalable=no">
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>Hello,world</title>
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
<c:set var="ctx" value="${pageContext.request.contextPath }"></c:set>
<script type="text/javascript" src="http://webapi.amap.com/maps?v=1.3&key=d05cbaac9f35812b93de8ab502c65e35"></script>
<script type="text/javascript" src="${ctx}/js/jquery-2.1.3.min.js"></script>
<script type="text/javascript" src="${ctx}/js/AMap-plugin/AMap.RealPosition.js"></script>
<script type="text/javascript">

$(document).ready(function(){
	var intervalVehicleId;
	markers = {}; // 即时所有汽车点
	vehicleArr = {};// 即时所有车辆对象
	var mapObj = null;
	var interval; //定时器
	realPosition = null;// 跟踪对象
	$.ajax({
	       type: 'get',
	       url: '/hypt/ws/0.1/monitor/allVehicles',
	       async: false,
	       dataType : 'JSON',
	       cache: false,
	       contentType: 'application/json;charset=utf-8',
	       success: function(items) {
				//地图对象
			    var position=new AMap.LngLat(items[0].longitude, items[0].latitude);
			    mapObj=new AMap.Map("container",{
			        view: new AMap.View2D({//创建地图二维视口
			        center:position,//创建中心点坐标
			        zoom:14, //设置地图缩放级别
			       rotation:0 //设置地图旋转角度
			       }),lang:"zh_cn"//设置地图语言类型，默认：中文简体
			    });
	    	   $.each(items, function(key, item) {
	    		   vehicleArr[item.vid] = item;
	              var marker = new AMap.Marker(
	                  {
	                      position : new AMap.LngLat(item.longitude, item.latitude),//基点位置                 
	                      offset : new AMap.Pixel(-14, -34),//相对于基点的偏移位置                 
	                      icon : "http://code.mapabc.com/images/car_03.png"
	                  });
	               marker.setMap(mapObj);
	               var info = [];                 
	               info.push("<b> 车辆信息</b>");                 
	               info.push("  车牌 :  京A88888"); 
	               info.push("  车速 : "+item.velocity);
	               info.push("  油量 : "+item.gas);
	               info.push("  里程 : "+item.mileage);
	               info.push("  地址 : 北京市望京阜通东大街方恒国际中心A座16层");
	               var inforWindow = new AMap.InfoWindow({                 
	                    offset:new AMap.Pixel(0,-23),                 
	                    content:info.join("<br>")                 
	                  });  
	              AMap.event.addListener(marker,"click",function(e){                 
	                    inforWindow.open(mapObj,marker.getPosition());                 
	              }); 
	              markers[item.vid] = marker;
	    	   });
	      }
	
	});
	//开启实时跟踪
	var startRealPosition = function() {
        var vehicleId = 1,
        data = vehicleArr[vehicleId];
        // test跟踪
            // test跟踪
            var opts = {
                ajaxOpts: {
                    url: '/hypt/ws/0.1/monitor/location',
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
                    //markerContent: '<div class="map-marker-licensePlate"><div class="car"></div></div>',
                    // offset: new AMap.Pixel(-35, -16),
                    autoRotation: true
                },
                timeout:3000,
                autoPan: true,
                recover: function(vehicleMarker) {
                	alert('recover');
                }
            };
            realPosition = new AMap.RealPosition(mapObj, opts);
    };
    
    // 停止跟踪
    var stopRealPosition = function() {
        if(realPosition && realPosition instanceof AMap.RealPosition) {
            realPosition.destroy();
        }
        // 清空跟踪车辆对象
        //$('a[data-opt="vehicle-position"]',wraper).text('跟踪模式');
        realPosition = null, delete realPosition;
        intervalVehicleId = null, delete intervalVehicleId;
    };
    
	$("#start").click(function(){
	    //interval = setInterval(startRealPosition,"3000"); 
		startRealPosition();
	});
	$("#stop").click(function(){
		alert(1);
		clearTimeout(interval);
		stopRealPosition();
	});
});
</script>
</head>

<body>
    <div>
        <input type="button" value="开始跟踪" id="start">
        <input type="button" value="停止跟踪" id="stop">
    </div>
	<div id="container"></div>
</body>
</html>