(function(h) {
	window.AMapTools = function() {
		var self = this;
		var args = arguments;
		var $div = null;
		var options = {};
		var completeCallBack = null;
		if (args.length == 1) {
			if (typeof args[0] == "string") {
				$div = args[0];
			} else {
				options = args[0];
			}
		} else if (args.length >= 2) {
			$div = args[0];
			if ('object' == typeof args[1]) {
				options = args[1];
				completeCallBack = args[2];
			} else if ('function' == typeof args[1]) {
				completeCallBack = args[1];
			}
		}
		var mapObj = null,
			overlays = {},
			infoWindow = new h.InfoWindow({
				closeWhenClickMap: true,
				autoMove: true,
				isCustom: true,
				offset: new h.Pixel(0, -40)
			}),
			cluster = null,
			toolbar = null,
			mousetool = null,
			mouseEvent = null,
			overview = null,
			scale = null,
			markerMovingControl = null,
			polyEditor = null,
			circleEditor = null;
		var listenerEvents = null;
		this.overlayTypeCode = {
			"0": "Rectangle",
			0: "Rectangle",
			"Rectangle": 0,
			"1": "Circle",
			1: "Circle",
			"Circle": 1,
			"2": "Polygon",
			2: "Polygon",
			"Polygon": 2,
			"3": "Polyline",
			3: "Polyline",
			"Polyline": 3,
			"4": "Marker",
			4: "Marker",
			"Marker": 4
		};
		this.overlayTypeName = {
			0: "矩形",
			"0": "矩形",
			"rectangle": "矩形",
			"Rectangle": "矩形",
			1: "圆形",
			"1": "圆形",
			"circle": "圆形",
			"Circle": "圆形",
			2: "多边形",
			"2": "多边形",
			"polygon": "多边形",
			"Polygon": "多边形",
			3: "线路",
			"3": "线路",
			"polyline": "线路",
			4: "站点",
			"4": "站点",
			"marker": "站点",
			"Marker": "站点"
		};
		var infoWindowArr = {};
		var mapId = $div || "map";
		var initMap = function() {
			var opt = {
				level: 13,
				resizeEnable: true,
				center: new h.LngLat(116.397428, 39.90923)
			}; !! index.city && (opt.center = index.city.location);
			if (options) {
				$.extend(opt, options);
			}
			listenerEvents = {};
			mapObj = new h.Map(mapId, opt);
			mapObj.plugin(["AMap.ToolBar", "AMap.MouseTool", "AMap.OverView", "AMap.Scale"], function() {
				// 加载工具条
				if (opt.toolbar == undefined || opt.toolbar == true) {
					toolbar = new h.ToolBar({
						direction: opt.direction != undefined ? opt.direction : true,
						ruler: opt.ruler != undefined ? opt.ruler : true,
						autoPosition: opt.autoPosition != undefined ? opt.autoPosition : false,
						locationMarker: opt.locationMarker != undefined ? opt.locationMarker : false,
						offset: new h.Pixel(options.toolbarOffsetx || 0, options.toolbarOffsety || 0)
					});
					mapObj.addControl(toolbar);
				}
				if (opt.MouseTool) {
					mousetool = new h.MouseTool(mapObj);
				}
				if (opt.OverView) {
					overview = new h.OverView();
					mapObj.addControl(overview);
				}
				if (opt.Scale) {
					scale = new h.Scale();
					mapObj.addControl(scale);
				}
			});
			'function' == typeof completeCallBack && AMap.event.addListener(mapObj, 'complete', completeCallBack);
			$('img.amap-logo').remove();
			$('div.amap-copyright').remove();
		};
		initMap();
		this.setToolBarOffset = function(offset) {
			if (toolbar != undefined && offset instanceof h.Pixel) {
				toolbar.setOffset(offset);
			}
		};
		/**
		 * 点聚合
		 * @method markerCluster
		 * @param {Array} markers
		 * @return {AMap.MarkerClusterer} cluster 聚合对象
		 */
		this.markerCluster = function(markers) {
			if (cluster && cluster instanceof h.MarkerClusterer) {
				cluster.setMarkers(markers);
			} else {
				mapObj.plugin(["AMap.MarkerClusterer"], function() {
					cluster = new h.MarkerClusterer(mapObj, markers, {
						averageCenter: true,
						maxZoom: 11,
						minClusterSize: 2
					});
				});
			}
		};
		/**
		 * 向地图增加点
		 * @param {Object} lnglat
		 * @param {Object} opt="{id:id,content:content,custom:true,otherAttr:otherAttr,width:,height:}"
		 */
		this.addMarker = function(lnglat, options) {
			options = options || {};
			if (typeof lnglat == "string") {
				var lng = lnglat.split(",")[0];
				var lat = lnglat.split(",")[1];
				lnglat = new h.LngLat(lng, lat);
			} else if ($.isArray(lnglat)) {
				lnglat = new h.LngLat(lnglat[0], lnglat[1]);
			} else if (typeof lnglat == 'object') {
				lnglat = new h.LngLat(lnglat.lng, lnglat.lat);
			}
			var id = options.id || 'jrad-marker-' + $.uuid;
			if (overlays.hasOwnProperty(id)) {
				overlays[id].setMap(null);
				delete overlays[id];
			}
			var offset = options.offset != undefined || options.offset === false ? options.offset : new h.Pixel(-11.5, -32);
			var opt = {
				id: id,
				// marker id
				position: lnglat,
				// 位置
				offset: offset,
				// 基点为图片左上角，设置相对基点的图片位置偏移量，向左向下为负
				topWhenClick: options.topWhenClick != undefined ? options.topWhenClick : true,
				topWhenMouseOver: options.topWhenMouseOver != undefined ? options.topWhenMouseOver : true,
				visible: options.visible != undefined ? options.visible : true,
				// 可见
				zIndex: options.zIndex != undefined ? options.zIndex : 1,
				// 设置点叠加顺序，在加载多个点有效果，详见设置点叠加顺序示例
				title: options.title,
				draggable: options.draggable || false,
				autoRotation: options.autoRotation != undefined ? options.autoRotation : true
			};
			if (options.markerClass) {
				opt.content = $('<div class="' + options.markerClass + '"></div>')[0];
			} else if (options.icon) {
				opt.icon = options.icon;
			} else if (options.markerContent) {
				'string' == typeof options.markerContent && (options.markerContent = $(options.markerContent)[0]);
				opt.content = options.markerContent;
			} else {
				opt.content = $('<div class="defaultMarker"></div>')[0];
			}
			var m = new h.Marker(opt);
			if (options.extData != undefined) {
				m.setExtData(options.extData);
			}
			if (options.autoTop) {
				h.event.addListener(m, "mouseover", function() {
					m.setTop(true);
					var content = m.getContent();
					'object' == typeof content && $(content).parent().addClass('highlight');
				});
				h.event.addListener(m, "mouseout", function() {
					m.setTop(false);
					var content = m.getContent();
					'object' == typeof content && $(content).parent().removeClass('highlight');
				});
			}
			if (options.content) {
				m.infoWindow = infoWindow;
				h.event.addListener(m, "click", function(e) {
					var content = m.getContent();
					if (!mapObj.getBounds().contains(m.getPosition())) {
						mapObj.panTo(m.getPosition());
					}
					if ('object' == typeof content && $(content).is(':hidden')) {
						return;
					}
					if (typeof options.content == 'function') {
						options.content.apply(infoWindow, [e, m]);
					} else {
						infoWindow.setContent(options.content);
						if (options.winOffset instanceof h.Pixel) {
							infoWindow.set('offset', options.winOffset);
						}
					}
					infoWindow.open(mapObj, m.getPosition());
					if ($.isFunction(options.click)) {
						e.infoWindow = infoWindow;
						options.click.apply(m, [e]);
					}
				});
			}
			overlays[id] = m;
			if (options.isShow == undefined || options.isShow == true) {
				m.setMap(mapObj);
			}
			return m;
		};
		/**
		 * 添加线
		 * @param markerArry
		 * @returns
		 */
		this.addPolyline = function(lnglats, opt) {
			opt = opt || {}, lnglats = lnglats || [];
			var id = opt.id || 'jrad-polyline-' + $.uuid;
			path = [];
			if (overlays.hasOwnProperty(id)) {
				overlays[id].setMap(null);
				delete overlays[id];
			}
			$.each(lnglats, function(i, item) {
				if (typeof item == 'string') {
					var lnglat = item.split(",");
					path.push(new h.LngLat(lnglat[0], lnglat[1]));
				} else if (item instanceof h.LngLat) {
					path.push(item);
				}
			});
			opt.id = id;
			opt = $.extend({
				path: path,
				strokeColor: "#7A98DD",
				//线颜色
				strokeOpacity: 1,
				//线透明度
				strokeWeight: 3,
				//线宽
				strokeDasharray: [10, 5] //补充线样式
			}, opt);
			var polyline = new h.Polyline(opt);
			overlays[id] = polyline;
			polyline.setMap(mapObj);
			return polyline;
		};
		/**
		 * 根据overlayId新增信息窗口到全局变量infoWindow
		 * @param {Object} id
		 * @param {Object} options
		 * @example {content: '',custome: true,autoMove: true,}
		 */
		this.newInfoWindow = function(id, options) {
			var that = this,
				_infoWindow = new h.InfoWindow({
					closeWhenClickMap: true,
					autoMove: true,
					offset: options.winOffset || new h.Pixel(0, -40),
					isCustom: options.isCustom !== undefined ? options.isCustom : true,
					content: options.content
				}),
				id = id || 'jrad-overlay-' + $.uuid,
				m = this.getOverlaysById(id);
			if (m) {
				h.event.addListener(m, "click", function(e) {
					_infoWindow.open(mapObj, that.getOverlayCenter(m));
					if (!mapObj.getBounds().contains(m.getPosition())) {
						mapObj.panTo(m.getPosition());
					} else {
						// 计算窗体是否在map显示区域内
						var size = mapObj.getSize();
						var _width = size.width;
						var _height = size.height;
						var winSize = _infoWindow.getSize();
						var _winWidth = winSize.getWidth() / 2;
						var _winHeight = winSize.getHeight() || _infoWindow.getContent().offsetHeight;
						var pixel = mapObj.lnglatTocontainer(m.getPosition());
						var px = pixel.x;
						var py = pixel.y;
						var $details = $('.bottom-wrap');
						if ($details.hasClass('show-wrap')) {
							_height -= $details.outerHeight();
						}
						if (px < _winWidth || (_width - px < _winWidth) || py < _winHeight - 50 || (_height - py < 100)) {
							mapObj.panTo(m.getPosition());
						}
					}
					if ($.isFunction(options.click)) {
						e.infoWindow = _infoWindow;
						options.click.apply(m, [e]);
					}
				});
				if (m.infoWindow) {
					m.infoWindow.setMap(null);
					delete m.infoWindow;
				}
				m.infoWindow = _infoWindow;
				infoWindowArr[id] = _infoWindow;
			}
			return _infoWindow;
		};
		/**
		 * 更新窗口信息
		 */
		this.updateInfoWindow = function(id, opts) {
			var _infoWindow = infoWindowArr[id] || infoWindow;
			var m = this.getOverlaysById(id);
			if (_infoWindow instanceof h.InfoWindow) {
				if (opts.custome !== undefined) {
					_infoWindow.setIsCustom(opts.custome);
				}
				if (opts.content !== undefined) {
					_infoWindow.setContent(opts.content);
					$(opts.content).delegate('a.pop-up-close', 'click', function(event) {
						_infoWindow.close();
						if ($.isFunction(opts.close)) {
							opts.close.apply(this, [event, m, _infoWindow]);
						}
					});
				}
				if (opts.offset instanceof h.Pixel) {
					_infoWindow.a.offset = opts.offset;
				}
				if (_infoWindow.getIsOpen() && m) {
					_infoWindow.open(mapObj, this.getOverlayCenter(m));
				} else {
					_infoWindow.close();
				}
				return _infoWindow;
			}
		};
		/**
		 * 关闭指定消息窗体，id为null时关闭全部窗体
		 * @param {} ovlerayId
		 */
		this.closeInfoWindow = function(id) {
			if (id !== undefined && id !== '') {
				if (infoWindowArr[id]) {
					infoWindowArr[id].close();
				}
			} else {
				mapObj.clearInfoWindow();
			}
		};
		/**
		 * 更新点
		 * @param {Object|String} m 点对象|点ID
		 * @param {Object} {icon 图片,lnglat 地址,markerClass 图标,zIndex z序 }
		 */
		this.updateMarker = function(m, options) {
			typeof m == 'string' && (m = this.getOverlaysById(m));
			if (!m) {
				return;
			}
			if (options.icon != undefined && (options.icon instanceof h.Icon || typeof options.icon == 'string')) {
				m.setIcon(options.icon);
			}
			if (options.markerClass != undefined) {
				m.setContent($('<div class="' + options.markerClass + '"></div>')[0]);
			}
			if (options.markerContent != undefined) {
				'string' == typeof options.markerContent && (options.markerContent = $(options.markerContent)[0]);
				m.setContent(options.markerContent);
			}
			if (options.pixelX != undefined && options.pixelY != undefined) {
				options.offset = new h.Pixel(options.pixelX, options.pixelY);
			}
			if (options.offset != undefined && options.offset instanceof h.Pixel) {
				m.setOffset(options.offset);
			}
			if (options.lnglat != undefined && options.lnglat instanceof h.LngLat) {
				m.setPosition(options.lnglat);
			}
			if (options.zIndex != undefined) {
				m.setzIndex(options.zIndex);
			}
			if (options.autoRotation != undefined) {
				m.set('autoRotation', options.autoRotation); !! !options.autoRotation && (options.angle = 0);
			}
			if (options.angle != undefined) {
				m.setAngle(options.angle);
			}
			return m;
		};
		/**
		 *更新线段
		 * @param {Object} line 线对象
		 * @param {Object} lnglat 坐标对象
		 */
		this.updateLine = function(line, lnglat) {
			var path = line.getPath();
			path.push(lnglat);
			line.setPath(path);
			line.setMap(mapObj);
			//mapObj.updateOverlay(line);
		};
		this.contains = function(lngLat) {
			return mapObj.getBounds().contains(lngLat);
		};
		/**
		 * 绑定事件
		 * @param {Object|String} overlay 对象|对象id|"map"
		 * @param {String} eventName 事件名称
		 * @param {Function} func 回调函数
		 * @param {Object|String} context 触发对象 func中this指代对象，未设置时默认overlay
		 */
		this.bindOverlayEvent = function(overlay, eventName, func, context) {
			if (overlay != undefined) {
				var isMap = "map";
				if (typeof overlay == 'string') {
					if (isMap.indexOf(overlay.toLocaleLowerCase()) != -1) {
						isMap = true;
						overlay = mapObj;
					} else {
						isMap = false;
						overlay = this.getOverlaysById(overlay) || undefined;
					}
				}
				if (typeof context == 'string') {
					if (isMap.indexOf(context.toLocaleLowerCase()) != -1) {
						isMap = true;
						context = mapObj;
					} else {
						isMap = false;
						context = this.getOverlaysById(context) || undefined;
					}
				}
				var id = isMap ? 'map' : overlay.get('id');
				listenerEvents[id] = h.event.addListener(overlay, eventName, function(e) {
					func.apply(this, [e]);
				}, context);
				return listenerEvents[id];
			}
		};
		this.bind = this.bindOverlayEvent;
		/**
		 * 根据ID 解绑事件
		 * @param {} id 标注物id
		 */
		this.removeListener = function(id) {
			if (listenerEvents.hasOwnProperty(id) && listenerEvents[id] != undefined) {
				h.event.removeListener(listenerEvents[id]);
				delete listenerEvents[id];
			}
		};
		/**
		 * 触发绑定事件
		 * @param {Object|String} overlay 对象|对象id
		 * @param {String} eventName 触发绑定函数
		 * @param {Object} arguments 参数
		 */
		this.trigger = function(overlay, eventName, arguments) {
			if (overlay != undefined) {
				eventName = eventName || 'click';
				if (typeof overlay == 'string') {
					overlay = this.getOverlaysById(overlay);
				}
				if (overlay) {
					h.event.trigger(overlay, eventName, arguments);
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		};
		/**
		 * 清空地图操作
		 */
		this.clearMap = function() { // 清空地图
			for (var p in overlays) overlays.hasOwnProperty(p) && overlays[p] != undefined && delete overlays[p];
			cluster && cluster.clearMarkers();
			mapObj.clearMap();
		};
		/**
		 * 设置鼠标工具为移动
		 */
		this.setMouseMove = function() {
			mousetool.close(true);
			if (editor) {
				editor._circle && editor._circle.setMap(null), editor._poly && editor._poly.setMap(null), editor.close();
				editor = null;
			}
		};
		/**
		 * 根据传入的类型设置鼠标工具
		 * @param {String} type like "Polygon"
		 * @param {Object} func 鼠标绘画成功后的回调函数
		 */
		var editor;
		this.setArea = function(type, callback) {
			mousetool.close(true);
			if (editor) {
				editor._circle && editor._circle.setMap(null), editor._poly && editor._poly.setMap(null), editor.close();
				editor = null;
			}
			switch (type) {
				case 'Polygon':
					mousetool.polygon({
						id: 'jrad-over-editor',
						strokeColor: "#046788",
						strokeOpacity: 0.6,
						strokeWeight: 3,
						fillColor: "#046788",
						fillOpacity: 0.2
					});
					break;
				case 'Rectangle':
					mousetool.rectangle({
						id: 'jrad-over-editor',
						strokeColor: "#046788",
						strokeOpacity: 0.6,
						strokeWeight: 3,
						fillColor: "#046788",
						fillOpacity: 0.2
					});
					break;
				case 'Circle':
					mousetool.circle({
						id: 'jrad-over-editor',
						strokeColor: "#046788",
						strokeOpacity: 0.6,
						strokeWeight: 3,
						fillColor: "#046788",
						fillOpacity: 0.2
					});
					break;
				case 'Polyline':
					mousetool.polyline({
						id: 'jrad-over-editor',
						strokeColor: "#F00",
						strokeOpacity: 0.4,
						strokeWeight: 3,
						strokeStyle: "dashed",
						strokeDasharray: [10, 5]
					});
					break;
				case 'Marker':
					mousetool.marker({
						id: 'jrad-over-editor',
						content: '<div class="defaultMarker"></div>',
						offset: new h.Pixel(-11.5, -32),
						draggable: true // 可拖动
					});
					break;
				case 'Ruler':
					mousetool.rule();
					break;
				default:
					mousetool.close();
			}
			if (mouseEvent != undefined) {
				h.event.removeListener(mouseEvent);
				delete mouseEvent;
			}
			mouseEvent = h.event.addListener(mousetool, "draw", function(e) {
				mousetool.close();
				h.event.removeListener(mouseEvent);
				switch (type) {
					case 'Circle':
						mapObj.plugin(["AMap.CircleEditor"], function() {
							editor = new h.CircleEditor(mapObj, e.obj);
							editor.getOverlay = function() {
								return e.obj;
							};
							editor.open();
							if ($.isFunction(callback)) {
								callback.apply(e.obj, [type, editor]);
							}
						});
						break;
					case 'Polygon':
					case 'Polyline':
					case 'Rectangle':
						mapObj.plugin(["AMap.PolyEditor"], function() {
							editor = new h.PolyEditor(mapObj, e.obj);
							editor.getOverlay = function() {
								return e.obj;
							};
							editor.open();
							if ($.isFunction(callback)) {
								callback.apply(e.obj, [type, editor]);
							}
						});
						break;
					default:
						if ($.isFunction(callback)) {
							callback.apply(e.obj, [type, mousetool]);
						}
				}
			});
		};
		/**
		 * 根据覆盖物类型清空覆盖物
		 * @param {String} overlayType
		 */
		this.clearByType = function(overlayType) {
			var type = "Marker,Polyline,Polygon,Circle";
			if (type.indexOf(overlayType) == -1) {
				throw new Error("叠加物类型为" + type + "中的一个");
				return false;
			}
			if (type.indexOf(',') != -1) {
				type = type.split(',')
			} else {
				type = [type];
			}
			for (var i = 0; i < type.length; i++) {
				var className = type[i];
				if (h[className]) {
					for (var id in overlays) {
						var o = overlays[id];
						if (o instanceof h[className]) {
							o.setMap(null);
							if (o instanceof h.Marker && cluster && cluster instanceof h.MarkerClusterer) {
								cluster.removeMarker(o);
							}
							delete overlays[id];
						}
					}
				}
			}
		};
		/**
		 * 多点规划路径
		 * @param {Object} array
		 * @param {Object} callback
		 */
		this.roueSearchMultiPoi = function(array, callback) {
			var mrs = new h.RouteSearch();
			var opt = {};
			opt.per = 150;
			// 抽吸函数，表示在地图上画导航路径的站点的个数,默认为150
			opt.routeType = 0;
			// 路径计算规则,0表示速度优先（默认）
			var arr = new Array();
			for (var i = 0; i < array.length; i++) {
				var mll = new h.LngLat(arry[i]);
				arr.push(mll);
			}
			callback = callback ? callback : function(data) {
				switch (data.message) {
					case "ok":
						var coors = [];
						var segmengList = data.segmengList;
						var listlength = segmengList.length;
						for (var i = 0; i < listlength; i++) {
							var coor = segmengList[i].coor;
							coors.push(coor);
						}
						var coorArray = coors.split(",");
						var lnglatArray = [];
						var coorlength = coorArray.length;
						if (coorArray[coorlength - 1] == "") {
							coorArray.slice(0, coorlength - 1);
						}
						for (var i = 0; i < coorlength - 2; i = i + 2) {
							lnglatArray.push(coorArray[i] + "," + coorArray[i + 1]);
						}
				}
			};
			mrs.getNaviPath(arr, callback);
		};
		/**
		 * 根据城市名称,区县名称,省份名称,区号进行地图定位
		 * @param {String} name
		 * @param {Function} callback
		 */
		this.setCity = function(name, callback) {
			mapObj.setCity(name, callback);
		};
		this.fixBounds = function(arr) {
			var min_lat = 0;
			var max_lat = 0;
			var min_lng = 0;
			var max_lng = 0;
			$.each(arr, function(index, val) {
				if (min_lat == 0) {
					min_lat = val.lat;
				}
				if (max_lat == 0) {
					max_lat = val.lat;
				}
				if (min_lng == 0) {
					min_lng = val.lng;
				}
				if (max_lng == 0) {
					max_lng = val.lng;
				}
				if (min_lat > val.lat) {
					min_lat = val.lat;
				}
				if (max_lat < val.lat) {
					max_lat = val.lat;
				}
				if (min_lng > val.lng) {
					min_lng = val.lng;
				}
				if (max_lng < val.lng) {
					max_lng = val.lng;
				}
			});
			var southwest = new h.LngLat(min_lng - 5, min_lat - 5);
			var northeast = new h.LngLat(max_lng + 5, max_lat + 5);
			var bounds = new h.Bounds(southwest, northeast);
			mapObj.setBounds(bounds);
		};
		this.setBounds = function(bounds) {
			if ($.isArray(bounds)) {
				var southwest = new h.LngLat(bounds[0].lng, bounds[0].lat);
				var northeast = new h.LngLat(bounds[1].lng, bounds[1].lat);
				bounds = new h.Bounds(southwest, northeast);
			}
			if (bounds instanceof h.Bounds) {
				mapObj.setBounds(bounds);
			}
		};
		this.getBounds = function() {
			var bounds = mapObj.getBounds();
			return bounds.southwest.lng + "," + bounds.southwest.lat + ";" + bounds.northeast.lng + "," + bounds.northeast.lat;
		};
		/**
		 *移动地图中心到图标所在位置
		 * @param {Marker}
		 */
		this.mapPanto = function(overlay) {
			mapObj.panTo(overlay.getPosition());
		};
		/**
		 * 设置地图中心点
		 * @param {} lnglat
		 */
		this.setCenter = function(lnglat) {
			lnglat = new h.LngLat(lnglat.lng, lnglat.lat);
			mapObj.setCenter(lnglat);
		};
		/**
		 * 获取地图中心点
		 * @return {}
		 */
		this.getCenter = function() {
			return mapObj.getCenter();
		};
		/**
		 * 通过标注标识获取标注
		 * @param {} id
		 * @return {}
		 */
		this.getOverlaysById = function(id) {
			return overlays.hasOwnProperty(id) ? overlays[id] : false;
		};
		/**
		 * 获取所有标注
		 * @return {}
		 */
		this.getAllOverlays = function(type) {
			if (type == 'Array') {
				var all = [];
				for (var p in overlays) {
					if (overlays.hasOwnProperty(p) && overlays[p] != undefined) {
						all.push(overlays[p]);
					}
				}
				return all;
			} else {
				return overlays;
			}
		};
		this.setFitView = function(overlays) {
			var _overlays = overlays;
			if(_overlays == undefined) {
				_overlays = [];
				filter(overlays, function(o) {
					_overlays.push(o);
				});
			}
			mapObj.setFitView(_overlays);
		};
		this.getMap = function() {
			return mapObj;
		};
		/**
		 * 绘制驾车导航路线
		 * @param {} start
		 * @param {} end
		 * @param {} steps
		 */
		this.drivingDrawLine = function(start, end, steps) {
			var length = steps.length;
			this.clearMap();
			var extraPath, extraLine, id;
			if (start != null) {
				var startMarker = this.getOverlaysById('jrad-marker-start');
				if (!startMarker) {
					//起点、终点图标
					var sicon = new h.Icon({
						image: "/shangcheng/css/images/map/poi.png",
						size: new h.Size(44, 44),
						imageOffset: new h.Pixel(0, 0)
					});
					startMarker = new h.Marker({
						id: 'jrad-marker-start',
						map: mapObj,
						icon: sicon,
						//复杂图标  
						visible: true,
						position: start
					});
					overlays['jrad-marker-start'] = startMarker;
				} else {
					startMarker.setPosition(start);
				}
				extraPath = [];
				extraPath.push(start);
				extraPath.push(steps[0].path[0]);
				id = 'jrad-polyline-' + $.uuid;
				extraLine = new h.Polyline({
					id: id,
					map: mapObj,
					path: extraPath,
					strokeColor: "#9400D3",
					strokeOpacity: 0.7,
					strokeWeight: 2,
					strokeStyle: "dashed",
					strokeDasharray: [10, 5]
				});
				overlays[id] = extraLine;
			}
			if (end != null) {
				if (!endMarker) {
					var endMarker = this.getOverlaysById('jrad-marker-end');
					var eicon = new h.Icon({
						image: "/shangcheng/css/images/map/poi.png",
						size: new h.Size(44, 44),
						imageOffset: new h.Pixel(0, -88)
					});
					endMarker = new h.Marker({
						id: 'jrad-marker-end',
						map: mapObj,
						icon: eicon,
						//复杂图标
						visible: true,
						position: end
					});
					overlays['jrad-marker-end'] = endMarker;
				} else {
					endMarker.setPosition(end);
				}
				extraPath = [];
				var path_xy = steps.slice(-1)[0].path;
				extraPath.push(end);
				extraPath.push(path_xy.slice(-1)[0]);
				id = 'jrad-polyline-' + $.uuid;
				extraLine = new h.Polyline({
					id: id,
					map: mapObj,
					path: extraPath,
					strokeColor: "#9400D3",
					strokeOpacity: 0.7,
					strokeWeight: 2,
					strokeStyle: "dashed",
					strokeDasharray: [10, 5]
				});
				overlays[id] = extraLine;
			}
			var drawpath, polylineArr = [];
			var options = null;
			for (var s = 0; s < length; s++) {
				drawpath = steps[s].path;
				if (!$.isArray(drawpath) || drawpath.length == 0) {
					continue;
				}
				options = steps[s].options;
				options = $.extend({
					id: 'jrad-polyline-' + $.uuid,
					map: mapObj,
					path: drawpath,
					strokeColor: "#9400D3",
					strokeOpacity: 0.5,
					strokeWeight: 2,
					strokeDasharray: [10, 5]
				}, options);
				var p = new h.Polyline(options);
				overlays[options.id] = p;
				polylineArr.push(p);
			}
			if (polylineArr.length != 0) {
				this.setFitView(polylineArr);
			}
			return polylineArr;
		};
		var filter = function(overlay, callback) {
			try {
				if (overlay == null) {
					for (var id in overlays) overlays.hasOwnProperty(id) && overlays[id] != undefined && filter(overlays[id], callback);
				} else if (typeof overlay == 'string') { !! overlays[overlay] && filter(overlays[overlay], callback);
				} else if (overlay.constructor.toString().indexOf("Array") > -1) {
					for (var i = 0; i < overlay.length; i++) { !! overlay[i] && filter(overlay[i], callback);
					}
				} else {
					if ('object' == typeof overlay) {
						if (overlay instanceof AMap.Marker || overlay instanceof AMap.Polyline || overlay instanceof AMap.Polygon || overlay instanceof AMap.Circle) {
							overlay && callback(overlay);
						} else {
							for (var id in overlay) overlay.hasOwnProperty(id) && overlay[id] != undefined && filter(overlay[id], callback);
						}
					}
				}
			} catch (e) {
				return false;
			}
		};
		var hideOrShow = function(overlay, toggle) {
			overlay[toggle]();
		};
		this.toggle = function(overlay) {
			filter(overlay, function(o) {
				var toggle = !! o.isHidden ? 'show' : 'hide';
				o.isHidden = !! !o.isHidden;
				hideOrShow(o, toggle);
			});
		};
		this.show = function(overlay) {
			filter(overlay, function(o) {
				o.isHidden = false;
				hideOrShow(o, 'show');
			});
		};
		this.hide = function(overlay) {
			filter(overlay, function(o) {
				o.isHidden = true;
				hideOrShow(o, 'hide');
			});
		};
		this.removeOverlays = function(overlay) {
			filter(overlay, function(o) {
				o.setMap(null);
				if (o instanceof h.Marker && cluster && cluster instanceof h.MarkerClusterer) {
					cluster.removeMarker(o);
				}
				var id = o.get('id');
				delete overlays[id];
			});
		};
		// 计算多点的中心点
		var countCenter = function(arr) {
			if (this instanceof h.Polyline) {
				var length = arr.length;
				return arr[parseInt(length / 2)];
			}
			var min_lat = 0;
			var max_lat = 0;
			var min_lng = 0;
			var max_lng = 0;
			$.each(arr, function(index, val) {
				if (min_lat == 0) {
					min_lat = val.lat;
				}
				if (max_lat == 0) {
					max_lat = val.lat;
				}
				if (min_lng == 0) {
					min_lng = val.lng;
				}
				if (max_lng == 0) {
					max_lng = val.lng;
				}
				if (min_lat > val.lat) {
					min_lat = val.lat;
				}
				if (max_lat < val.lat) {
					max_lat = val.lat;
				}
				if (min_lng > val.lng) {
					min_lng = val.lng;
				}
				if (max_lng < val.lng) {
					max_lng = val.lng;
				}
			});
			var center_lng = (min_lng + max_lng) / 2;
			var center_lat = (min_lat + max_lat) / 2;
			return new h.LngLat(center_lng, center_lat);
		};
		// 获取标注物的中心点
		this.getOverlayCenter = function(overlay) {
			if ($.isArray(overlay)) {
				return countCenter(overlay);
			} else if (typeof overlay == 'string') {
				overlay = this.getOverlaysById(overlay);
				if (overlay === false) {
					return mapObj.getCenter();
				}
			} else if (typeof overlay == 'object') {
				var flag = false;
				for (var i = 1; i < 5; i++) {
					flag = flag || overlay instanceof h[this.overlayTypeCode[i]];
				}
				if (!flag) {
					return mapObj.getCenter();
				}
			}
			if (overlay.getPosition) {
				return overlay.getPosition();
			} else if (overlay.getCenter) {
				return overlay.getCenter();
			} else if (overlay.getPath) {
				return countCenter.apply(overlay, [overlay.getPath()]);
			}
		};
		/**
		 * 添加新的图元
		 * @param {} id
		 * @param {} type
		 * @param {} arra
		 * @param {} content
		 */
		this.newArea = function(id, type, arra, content, func) {
			var overlay = null,
				center = null;
			if (overlays.hasOwnProperty(id)) {
				overlays[id].setMap(null);
				delete overlays[id];
			}
			switch (type.toLowerCase()) {
				case "polygon":
				case "rectangle":
					var arr = new Array();
					if (type == "Rectangle" && arra.length == 2) {
						var lnglat = arra[0].split(",");
						var lnglat2 = arra[1].split(",");
						arra.length = 0;
						arra.push(lnglat.join(","));
						arra.push(lnglat[0] + "," + lnglat2[1]);
						arra.push(lnglat2.join(","));
						arra.push(lnglat2[0] + "," + lnglat[1]);
					}
					for (var i = 0; i < arra.length; i++) {
						var lnglat = arra[i].split(",");
						arr.push(new h.LngLat(lnglat[0], lnglat[1]));
					}
					var opt = {
						id: id,
						map: mapObj,
						path: arr,
						strokeColor: "#046788",
						strokeOpacity: 0.6,
						strokeWeight: 3,
						fillColor: "#046788",
						fillOpacity: 0.2
					};
					overlay = new h.Polygon(opt);
					//mapObj.addOverlays(overlay);
					break;
				case "circle":
					var radius = parseFloat(arra[1]);
					var opt = {
						id: id,
						map: mapObj,
						center: new h.LngLat(arra[0].split(",")[0], arra[0].split(",")[1]),
						radius: radius,
						strokeColor: "#046788",
						strokeOpacity: 0.6,
						strokeWeight: 3,
						fillColor: "#046788",
						fillOpacity: 0.2
					};
					overlay = new h.Circle(opt);
					break;
				case "marker":
					overlay = this.addMarker(arra[0], {
						id: id
					});
					break;
				case "polyline":
					var arr = new Array();
					for (var i = 0; i < arra.length; i++) {
						var lnglat = arra[i].split(",");
						arr.push(new h.LngLat(lnglat[0], lnglat[1]));
					}
					var opt = {
						id: id,
						map: mapObj,
						path: arr,
						strokeColor: (typeof $.jRadGetColors == 'function' ? $.jRadGetColors()[0] : "#F00"),
						strokeOpacity: 1,
						strokeWeight: 3,
						strokeStyle: "solid"
					};
					overlay = new h.Polyline(opt);
					break;
			}
			overlays[id] = overlay;
			if (content != undefined && overlay != undefined) {
				var _infoWindow = new h.InfoWindow({
					isCustom: false,
					closeWhenClickMap: true,
					content: content
				});
				center = this.getOverlayCenter(overlay);
				listenerEvents[id] = AMap.event.addListener(overlay, 'click', function(e) {
					if ($.isFunction(func)) {
						var opt = {
							id: id,
							mapTool: self,
							overlay: overlay,
							infoWindow: _infoWindow,
							center: center,
							e: e
						};
						func.apply(this, [opt]);
					} else {
						_infoWindow.open(mapObj, e.lnglat);
					}
				});
			}
			return overlay;
		};
		this.clearMove = function() {
			if (markerMovingControl) markerMovingControl.stop();
			markerMovingControl = null;
		};
		this.startMove = function(speed, callback) {
			var marker = this.getOverlaysById("jrad-marker-history");
			if (marker) {
				var markerArray = marker.markerArray;
				markerMovingControl instanceof h.MarkerMovingControl && markerMovingControl.destory();
				markerMovingControl = new h.MarkerMovingControl(mapObj, marker, markerArray, speed, callback);
			}
		};
		this.pauseMove = function() {
			if (markerMovingControl) {
				markerMovingControl.stop();
				return true;
			} else return false;
		};
		this.stopMove = function() {
			try {
				markerMovingControl.stop();
				markerMovingControl._marker.setPosition(markerMovingControl._path[0]);
				markerMovingControl._currentIndex = 0;
				return true;
			} catch (e) {
				return false;
			}
		};
		this.setMoveto = function(index) {
			if (markerMovingControl) {
				markerMovingControl._currentIndex = index;
				return true;
			} else return false;
		};
		this.start = function() {
			if (markerMovingControl) {
				if (markerMovingControl._currentIndex == 0) {
					markerMovingControl.restart();
				} else {
					markerMovingControl.move();
				}
				return true;
			} else return false;
		};
		this.setSpeed = function(speed) {
			if (markerMovingControl) {
				markerMovingControl.setSpeed(speed);
				return true;
			} else return false;
		};
		this.setCount = function(count) {
			if (markerMovingControl) {
				markerMovingControl.setCount(count);
				return true;
			} else return false;
		};
		// 查询路线
		this.searchDrivingPath = function() {
			AMap.getDrivingPath.apply(AMap, arguments);
		};
	};
	h.MarkerMovingControl = function(map, marker, path, speed, callback) {
		var self = this;
		this._map = map;
		this._marker = marker;
		this._path = path;
		this._currentIndex = 0;
		this._speed = speed || 100;
		this._count = 4;
		this._direction = 0;
		this._percent = 1 / path.length;
		this._callback = callback || null;
		this._marker.setMap(map);
		this._marker.setPosition(path[0]);
		this._listenToMoving = h.event.addListener(this._marker, 'moving', function() {
			var bounds = map.getBounds();
			var position = this.getPosition();
			if (!bounds.contains(position)) {
				map.panTo(position);
			} else {
				var pixel = map.lnglatTocontainer(position),
					size = map.getSize(),
					_width = size.width,
					_height = size.height,
					px = pixel.x,
					py = pixel.y,
					$details = $('.bottom-wrap');
				if ($details.hasClass('show-wrap')) {
					_height -= $details.outerHeight();
				}
				if (px < 100 || (_width - px < 150) || py < 100 || (_height - py < 150)) {
					map.panTo(position);
				}
			}
		});
	};
	/**
	 * 移动marker，会从当前位置开始向前移动
	 */
	h.MarkerMovingControl.prototype.move = function() {
		if (!this._listenToStepend) {
			this._listenToStepend = h.event.addListener(this, 'stepend', function() {
				this.step();
			}, this);
		}
		this.step();
	};
	/**
	 * 停止移动marker，由于控件会记录当前位置，所以相当于暂停
	 */
	h.MarkerMovingControl.prototype.stop = function() {
		$('.jrad-btn-start .icon-font-play').removeClass().addClass('icon-font-play');
		this._marker.stopMove();
	};
	/**
	 * 重新开始，会把marker移动到路径的起点然后开始移动
	 */
	h.MarkerMovingControl.prototype.restart = function() {
		this.stop();
		this._marker.setPosition(this._path[0]);
		this._currentIndex = 0;
		this.move();
	};
	/**
	 * 向前移动一步
	 */
	h.MarkerMovingControl.prototype.step = function() {
		if (this._direction == 0) {
			var nextIndex = this._currentIndex + 1;
			if (nextIndex < this._path.length) {
				if (!this._listenToMoveend) {
					this._listenToMoveend = h.event.addListener(this._marker, 'moveend', function() {
						this._currentIndex++;
						h.event.trigger(this, 'stepend');
					}, this);
				}
				if (this._callback && typeof this._callback == "function") {
					var percent = (this._currentIndex * this._percent) * 100;
					this._callback(percent, nextIndex);
				}
				var _speed = !! this._speed ? this._speed : 1000;
				_speed < 1 && (_speed = 1);
				this._marker.moveTo(this._path[nextIndex], _speed * this._count);
			} else {
				this.stop();
				this._currentIndex = 0;
				if (this._callback && typeof this._callback == "function") {
					this._callback(0, 0);
				}
			}
		}
	};
	/**
	 * 设置marker移动速度
	 */
	h.MarkerMovingControl.prototype.setSpeed = function(speed) {
		this._speed = speed;
	};
	h.MarkerMovingControl.prototype.setCount = function(count) {
		this._count = count;
		this._marker.stopMove();
		this.step();
	};
	h.MarkerMovingControl.prototype.destory = function() {
		this._listenToStepend && (h.event.removeListener(this._listenToStepend), delete this._listenToStepend);
		this._listenToMoving && (h.event.removeListener(this._listenToMoving), delete this._listenToMoving);
		this._marker.stopMove();
	};
	/**
	 * @title 调用高德服务根据多点求道路集合
	 * @method h.getDrivingPath
	 * @param {Array} a 点（AMap.LngLat）集合
	 * @param {Function} d 成功回调函数
	 * @param {Function} r 失败回调函数
	 * @param {Number} s 规划路劲策略
	 */
	h.getDrivingPath = function(a, d, r, s) {
		var b = a.length,
			s = s || 0,
			c = a[0],
			f = a.slice(-1)[0],
			e = [];
		if (c != undefined && c instanceof h.LngLat && f != undefined && f instanceof h.LngLat) {
			c = ["origin=" + c.toString(), "destination=" + f.toString(), "strategy=" + s];
			var opts = {
				extensions: "base",
				key: h.Conf.key,
				output: "json"
			};
			for (g in opts) opts.hasOwnProperty(g) && c.push(g + "=" + opts[g]);
			for (g = 1; g < b - 1 && 16 > g; g += 1) e.push(a[g].toString());
			e.length && c.push("waypoints=" + e.join(";"));
			b = "http://restapi.amap.com/v3/direction/driving" + (0 < c.length ? "?" + c.join("&") : "");
			b = new h.Http.JSONP(b, {
				callback: "callback"
			});
			h.event.addListener(b, 'complete', function(a) {
				var path = [];
				if (a.count) {
					for (var f = 0; f < a.route.paths.length; f++)
						for (var e = a.route.paths[f].steps, g = 0; g < e.length; g++) {
							var l = e[g].polyline.split(";"),
								k;
							for (k in l)
								if (l.hasOwnProperty(k)) {
									var m = l[k].split(",");
									m = new h.LngLat(m[0], m[1]);
									path.push(m)
								}
							if ((g + 1) < e.length) {
								l = e[g + 1].polyline.split(";");
								if (l.hasOwnProperty(0)) {
									var m = l[0].split(",");
									m = new h.LngLat(m[0], m[1]);
									path.push(m)
								}
							}
						}
				}
				typeof d == 'function' && d.apply(h, [a.route, path]);
			}, this);
			typeof r == 'function' && h.event.addListener(b, "error", r, this);
		}
	};
	/**
	 * 多边形包含点
	 * @param {} p 判断的点
	 * @return {Boolean} true 包含，false不包含
	 */
	h.Polygon.prototype.Contains = function(p) {
		if (this.hasOwnProperty('contains') && 'function' == typeof this['contains']) {
			return this.contains(p);
		} else {
			var nCross = 0; // 记录交点个数
			var path = this.getPath();
			var nCount = path.length;
			for (var i = 0; i < nCount; i++) {
				var p1 = path[i];
				var p2 = path[(i + 1) % nCount];
				// 求解 y=p.lat 与 p1p2 的交点
				// p1p2 与 y=p0.y平行
				if (p1.lat == p2.lat) continue;
				// 交点在p1p2延长线上
				if (p.lat < Math.min(p1.lat, p2.lat)) continue;
				// 交点在p1p2延长线上
				if (p.lat >= Math.max(p1.lat, p2.lat)) continue;
				// 求交点的 X 坐标
				var x = parseFloat(p.lat - p1.lat) * parseFloat(p2.lng - p1.lng) / parseFloat(p2.lat - p1.lat) + p1.lng;
				if (x > p.lng) nCross++; // 只统计单边交点 
			}
			// 单边交点为偶数，点在多边形之外
			return (nCross % 2 == 1);
		}
	};
	/**
	 * 圆包含点
	 * @param {} p 判断的点
	 * @return {Boolean} true 包含，false不包含
	 */
	h.Circle.prototype.Contains = function(p) {
		if (this.hasOwnProperty('contains') && 'function' == typeof this['contains']) {
			return this.contains(p);
		} else {
			var c = this.getCenter();
			var radius = this.getRadius();
			var distance = p.distance(c);
			// 到圆心的距离小于等于半径 在圆内
			return distance <= radius;
		}
	};
	/**
	 * 线包含点
	 * @param {} p 判断的点
	 * @return {Boolean} true 包含，false不包含
	 */
	h.Polyline.prototype.Contains = function(p) {
		var path = this.getPath();
		var length = path.length;
		for (var i = 0; i < length - 1; i++) {
			var p1 = path[i],
				p2 = path[(i + 1) % length];
			if (p2.lng == p1.lng) {
				if (p.lng != p1.lng) continue;
				else if (p.lat < p.lat < Math.max(p1.lat, p2.lat) && p.lat > p.lat < Math.min(p1.lat, p2.lat)) {
					return true;
				} else {
					continue;
				}
			} else {
				var k = (p2.lat - p1.lat) / (p2.lng - p1.lng),
					// 斜率
					b = p2.lat - k * p2.lng;
				if ((p.lat - k * p.lng) == b) return true;
			}
		}
		return false;
	};
	h.Circle.prototype.getArea = function() {
		return Math.PI * Math.pow(this.getRadius(), 2);
	};
})(AMap);