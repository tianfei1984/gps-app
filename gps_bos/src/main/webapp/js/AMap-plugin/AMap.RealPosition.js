(function(h) {
	/**
	 * @description 跟踪定位对象
	 * @class RealPosition
	 * @param {AMap.Map} a 地图对象（非空）
	 * @param {Object} RealPositionOptions 跟踪参数
	 */
	h.RealPosition = function(a, opts) {
		if (a == undefined || !(a instanceof h.Map)) {
			if (window.console) {
				console.log('map对象不能为空');
			}
			return;
		}
		this.m = a;
		this.opts = opts;
		this._id = opts.id;
		this._marker = opts.marker;
		this.timeout = opts.timeout || 15 * 1000;
		this._pass = [];
		this._results = [];
		this._event = {};
		this._interval = {};
		this.init();
	};
	h.RealPosition.prototype = {
		_getGuid: function() {
			return "g_" + 1E4 * Math.random()
		},
		_updateMarker: function() {
			var d = this,
				c = d.opts,
				a = c.markerOpts,
				b = d._marker;
			if (b) {
				if (a.markerClass != undefined) {
					b.setContent($('<div class="' + a.markerClass + '"></div>')[0]);
				} else if (a.markerContent != undefined) {
					'string' == typeof a.markerContent && (a.markerContent = $(a.markerContent)[0]);
					b.setContent(a.markerContent);
				}
				if (a.offset != undefined && a.offset instanceof h.Pixel) {
					b.setOffset(a.offset);
				}
				if (a.autoRotation != undefined) {
					b.set('autoRotation', a.autoRotation); !! !a.autoRotation && b.setAngle(0);
				}
			}
			if (d._pass.length == 0) {
				d._pass.push(b.getPosition());
			}
			d._marker = b;
			!d.m.getBounds().contains(b.getPosition()) && d.m.panTo(b.getPosition());
		},
		_updatePolyline: function() {
			var d = this,
				_p = d._polyline.getPath();
			_p.push(d._marker.getPosition());
			d._polyline.setPath(_p);
			d._polyline.setMap(d.m);
		},
		_addPolyline: function() {
			var d = this,
				c = d.opts,
				options = $.extend({
					id: 'jrad-rp-polyline-' + parseInt(Math.random() * 1000000), // polyline id
					strokeColor: "#4CA9E7",
					strokeOpacity: 0.8,
					strokeWeight: 3,
					strokeStyle: "dashed",
					strokeDasharray: [8, 4]
				}, c.polylineOpts);
			if (options.path) {
				options.path = d._pass.concat(options.path);
			}
			options.path = d._pass.slice(0);
			d._polyline = new h.Polyline(options);
		},
		_bindEvent: function() {
			var d = this;
			d._event[d._getGuid()] = h.event.addListener(d._marker, 'moving', function(e) {
				d._updatePolyline();
			});
			d._event[d._getGuid()] = h.event.addListener(d._marker, 'moveend', function(e) {
				d.pathIndex++;
				d._moveNext();
			});
			if ( !! d.opts.autoPan) {
				d._event[d._getGuid()] = h.event.addListener(d._marker, 'moving', function(e) {
					var size = d.m.getSize();
					var _w = size.width;
					var _h = size.height; // 地图高度
					// 设定边框
					var _ow = 'object' == typeof d.opts.autoPan ? d.opts.autoPan.w : 70;
					var _oh = 'object' == typeof d.opts.autoPan ? d.opts.autoPan.h : 32;
					var pixel = d.m.lnglatTocontainer(this.getPosition());
					var px = pixel.x;
					var py = pixel.y; // 点高度
					var $details = $('.bottom-wrap.show-wrap');
					if ($details.length != 0) {
						_h -= $details.outerHeight();
					}
					if (px < _ow + 100 || _ow + px + 100 > _w || py < _oh + 100 || _oh + py + 100 > _h) {
						d.m.panTo(this.getPosition());
					}
				});
			}
		},
		_unbindEvent: function() {
			var d = this;
			for (var p in d._event) d._event.hasOwnProperty(p) && h.event.removeListener(d._event[p]);
			d._event = null;
			delete d._event;
		},
		_moveNext: function() {
			var d = this,
				m = d._marker,
				r = d._result,
				s = r.position.speed;
			if (d.pathArr && d.pathArr.hasOwnProperty(d.pathIndex) != undefined && d.pathArr[d.pathIndex] instanceof h.LngLat) {
				var currentPosition = m.getPosition();
				if (currentPosition.distance(d.pathArr[d.pathIndex]) > 0) {
					m.moveTo(d.pathArr[d.pathIndex], s || 1);
				} else {
					h.event.trigger(m, 'moveend');
				}
			} else {
				d._setInterval();
			}
		},
		_getPath: function(a, d) {
			var b = a.length,
				c = a[0],
				f = a[1];
			if (c != undefined && c instanceof h.LngLat && f != undefined && f instanceof h.LngLat) {
				c = ["origin=" + c.toString(), "destination=" + f.toString(), "strategy=0"];
				var opts = {
					extensions: "base",
					key: "eb11422e622e0cc276685ee0ea4f59be",
					output: "json"
				};
				for (g in opts) opts.hasOwnProperty(g) && c.push(g + "=" + opts[g]);
				b = "http://restapi.amap.com/v3/direction/driving" + (0 < c.length ? "?" + c.join("&") : "");
				b = new h.Http.JSONP(b, {
					callback: "callback"
				});
				h.event.addListener(b, 'complete', d, this);
			}
		},
		_searchRoute: function() {
			var d = this;
			d.pathArr = null,
			delete d.pathArr,
			d.pathIndex = 0;
			if (d._pass.hasOwnProperty(d.currentIndex + 1)) {
				var c = d._pass.slice(d.currentIndex, d.currentIndex + 2);
				/*if (c[0].distance(c[1]) > 1) {
					d._getPath(c, function(a) {
						if (a.count) {
							for (d.pathArr = [], f = 0; f < a.route.paths.length; f++)
								for (var e = a.route.paths[f].steps, g = 0; g < e.length; g++) {
									var l = e[g].polyline.split(";"),
										k;
									for (k in l)
										if (l.hasOwnProperty(k)) {
											var m = l[k].split(",");
											m = new h.LngLat(m[0], m[1]);
											d.pathArr.push(m)
										}
									if ((g + 1) < e.length) {
										l = e[g + 1].polyline.split(";");
										if (l.hasOwnProperty(0)) {
											var m = l[0].split(",");
											m = new h.LngLat(m[0], m[1]);
											d.pathArr.push(m)
										}
									}
								}
							d._moveNext();
						}
					});
				} else {*/
					d.pathArr = d._pass.slice(d.currentIndex + 1, d.currentIndex + 2);
					d._moveNext();
				//}
			}
		},
		isUnEmpty: function(o) {
			if (o == undefined) return false;
			if (o.length > 0) return true;
			if (o.length == 0) return false;
			for (var p in o)
				if (o.hasOwnProperty(p)) return true;
			return false;
		},
		_setInterval: function() {
			var d = this,
				a = d.opts.ajaxOpts,
				b = a.success,
				c = a.preProcess,
				e = a.preQuery;
			a.success = function(data) {
				if (d.isUnEmpty(data)) {
					var l = data;
					c && typeof c == 'function' && (l = c.apply(d, [data]));
					if (l && l.lng && l.lat) {
						d._result = data;
						l = new h.LngLat(l.lng, l.lat);
						var current = d._pass[d.currentIndex],
							distance = current.distance(l);
						if (current.distance(l) > 0) {
							d._pass.splice(d.currentIndex + 1, d.currentIndex + 2, l);
							d._searchRoute();
							d.currentIndex++;
						}
					}
					b && typeof b == 'function' && b.apply(d, [data]);
				} else {
					d._interval['locating'] = window.setTimeout(function() {
						if (d._interval.hasOwnProperty('locating')) {
							window.clearTimeout(d._interval['locating']);
						}
						d._setInterval();
					}, d.timeout);
				}
			};
			typeof e == 'function' && e.apply(a, [d._result]);
			$.ajax(a);
		},
		getMarker: function() {
			return this._marker;
		},
		getPolyline: function() {
			return this._polyline;
		},
		getMarkerArr: function() {
			return this._pass;
		},
		destroy: function(callback) {
			try {
				var d = this; !! d._marker && d._marker.stopMove(); !! d._marker && !! d.pathArr && d.pathArr.length != 0 && d._marker.setPosition(d.pathArr.slice(-1)[0]);
				for (var p in d._interval) d._interval.hasOwnProperty(p) && window.clearInterval(d._interval[p]);
				d._interval = null, delete d._interval;
				d._unbindEvent();
				d._polyline instanceof h.Polyline && (d._polyline.setMap(null), d._polyline = null, delete d._polyline);
				d._pass = null, delete d._pass;
				d.pathArr = null, delete d.pathArr;
				d._result = null, delete d._result;
			} catch (e) { !! window.console && console.error(e.stack);
			} finally {
				typeof callback == 'function' && callback.apply(d, [d._marker]);
				typeof d.opts.recover == 'function' && d.opts.recover.apply(d, [d._marker]);
			}
		},
		start: function() {
			this.currentIndex = 0,
			this._bindEvent(),
			this._setInterval()
		},
		init: function() {
			var d = this;
			d._updateMarker(),
			d._addPolyline(),
			d.start()
		}
	};
})(AMap);