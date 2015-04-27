/* JavaScript API, amap, AutoNavi Group; */
(function(h) {
	h.DrivingPolicy = h.DrivingPolicy ? h.DrivingPolicy : {
		LEAST_TIME: 0,
		LEAST_FEE: 1,
		LEAST_DISTANCE: 2,
		REAL_TRAFFIC: 4,
		MULTI_POLICIES: 5,
		HIGHWAY: 6,
		FEE_HIGHWAY: 7,
		FEE_TRAFFIC: 8,
		TRAFFIC_HIGHWAY: 9
	};
	h.DragRoute = function(a, d, b, c) {
		this.url = "http://restapi.amap.com/v3/direction/driving";
		this.policy = b;
		this._list = [];
		this._obj = a;
		for (a = 0; a < d.length; a++) this._list.push({
			id: "s_" + this._getGuid(),
			poi: d[a],
			o: 1
		});
		this._path = d;
		this._polylinePath = [];
		this._route = [];
		this._f = 1;
		this._event = {};
		this._markers = [];
		this.opts = this._extend(c, {
			extensions: "base",
			key: h.Conf.key,
			output: "json"
		})
	};
	h.DragRoute.prototype = {
		_getGuid: function() {
			return "g_" + 1E4 * Math.random()
		},
		search: function(f) {
			for (var a = this,
					d = this._obj,
					b = [], c = 0; c < this._list.length; c++) this._event[this._list[c].id] = [],

			function(c, b) {
				var imageOffset;
				if (b == 0) {
					imageOffset = new AMap.Pixel(0, 0);
				} else if (b == (a._list.length - 1)) {
					imageOffset = new AMap.Pixel(0, -88);
				} else {
					imageOffset = new AMap.Pixel(0, -44);
				}
				var g = new h.Marker({
					id: c.id,
					position: c.poi,
					draggable: !0,
					icon: new h.Icon({
						image: "/shangcheng/css/images/map/poi.png",
						size: new AMap.Size(44, 44),
						imageOffset: imageOffset
					})
				});
				g.setMap(d);
				c.marker = g;
				var l = function(e) {
					if (a._f) {
						var d = [];
						e = e.lnglat;
						for (var b = 0; b < a._list.length; b++) a._list[b].id == c.id && (a._list[b].poi = e),
						d.push(a._list[b].poi);
						a._reSearch(d)
					}
					a._f = 0,
					e._index = b,
					h.event.trigger(a, "dragging", e)
				};
				var k = function(e) {
					a._nearLine(),
					e._index = b,
					h.event.trigger(a, "dragend", e)
				};
				g.on("dragging", l);
				g.on("dragend", k);
				a._event[c.id].push({
					obj: g,
					t: "dragging",
					fn: l
				});
				a._event[c.id].push({
					obj: g,
					t: "dragend",
					fn: k
				})
			}(this._list[c], c),
			b.push(this._list[c].poi);
			if (f) {
				this._onComplete(f);
			} else {
				this._roadSearch(b, this._onComplete)
			}
		},
		_roadSearch: function(a, d) {
			var b = a.length,
				c = a[0],
				f = a.slice(-1)[0],
				e = [];
			if (c instanceof h.LngLat && f instanceof h.LngLat) {
				var c = ["origin=" + c.toString(), "destination=" + f.toString(), "strategy=" + this.policy],
					g;
				for (g in this.opts) this.opts.hasOwnProperty(g) && c.push(g + "=" + this.opts[g]);
				for (g = 1; g < b - 1 && 16 > g; g += 1) e.push(a[g].toString());
				e.length && c.push("waypoints=" + e.join(";"));
				b = this.url + (0 < c.length ? "?" + c.join("&") : "");
				b = new h.Http.JSONP(b, {
					callback: "callback"
				});
				b.on("complete", d, this);
				b.on("error", this._onError, this)
			}
		},
		_str2LngLat: function(a) {
			a = a.split(",");
			return new h.LngLat(a[0], a[1])
		},
		_multi2Lnglat: function(a) {
			var c = [];
			a = a.split(";");
			for (var d = 0; d < a.length; d++) c[d] = this._str2LngLat(a[d]);
			return c
		},
		_parseString: function(a) {
			return "object" == typeof a || "undefined" == typeof a ? "" : a
		},
		_parseInt: function(a) {
			a = parseInt(a, 10);
			return isNaN(a) ? 0 : a
		},
		_parseRoutes: function(a) {
			var c = [];
			a = a.path ? [a.path] : a;
			for (var d = 0; d < a.length; d++) {
				var f = a[d];
				c[d] = {
					steps: this._parseStep(f.steps),
					distance: this._parseInt(f.distance),
					time: this._parseInt(f.duration),
					policy: f.strategy,
					tolls: this._parseInt(f.tolls),
					tolls_distance: this._parseInt(f.toll_distance)
				}
			}
			return c
		},
		_parseStep: function(a) {
			for (var c = [], d = this, f = 0; f < a.length; f++) {
				var b = a[f],
					e = this._multi2Lnglat(b.polyline);
				c[f] = {
					start_location: e[0],
					end_location: e[e.length - 1],
					instruction: this._parseString(b.instruction),
					orientation: this._parseString(b.orientation),
					road: this._parseString(b.road),
					distance: this._parseInt(b.distance),
					tolls: this._parseInt(b.tolls),
					toll_distance: this._parseInt(b.toll_distance),
					toll_road: this._parseString(b.toll_road),
					time: this._parseInt(b.duration),
					path: e,
					action: this._parseString(b.action),
					assistant_action: this._parseString(b.assistant_action)
				};
				this._isArray(b.cities) && (c[f].cities = this._parseArray(b.cities,
					function(a, b) {
						var c;
						switch (b) {
							case "districts":
								c = d._parseArray(a);
								break;
							default:
								c = a
						}
						return c
					}));
				this._isArray(b.tmcs) && (c[f].tmcs = this._parseArray(b.tmcs,
					function(a, c) {
						var b;
						switch (c) {
							case "distance":
								b = +a;
								break;
							default:
								b = a
						}
						return b
					}))
			}
			return c
		},
		_parseArray: function(a, c) {
			for (var d = [], e = {},
					b = 0; b < a.length; b++) {
				for (var g in a[b]) a[b].hasOwnProperty(g) && (e[g] = "function" === typeof c ? c(a[b][g], g) : a[b][g]);
				d.push(e)
			}
			return d.length ? d : null
		},
		_isArray: function(a) {
			return "[object Array]" === Object.prototype.toString.call(a) ? !0 : !1
		},
		_onComplete: function(a) {
			if (a.route) {
				this._route = a.route;
			}
			if (a.count) {
				if (this._isArray(this._polylineArr)) {
					for (var i = 0; i < this._polylineArr.length; i++) {
						this._polylineArr[i].setMap(null);
					};
					this._polylineArr = null;
					delete this._polylineArr;
				}
				this._polylineArr = [];
				for (var d = 0; d < a.route.paths.length; d++) {
					for (var b = a.route.paths[d].steps, c = 0; c < b.length; c++) {
						var f = b[c].polyline.split(";"),
							e, p, path = [];
						for (e in f)
							if (f.hasOwnProperty(e)) {
								var g = f[e].split(",");
								g = new h.LngLat(g[0], g[1]);
								path.push(g);
								this._polylinePath.push(g)
							}
						if ((c + 1) < b.length) {
							f = b[c + 1].polyline.split(";");
							if (f.hasOwnProperty(0)) {
								var g = f[0].split(",");
								g = new h.LngLat(g[0], g[1]);
								path.push(g);
							}
						}
						b[c].path = path;
						p = new h.Polyline({
							id: "p" + this.guid + "-" + c,
							path: path,
							strokeColor: "#9400D3",
							strokeOpacity: 1,
							strokeWeight: 3
						});
						p.setMap(this._obj);
						p.setExtData(b[c]);
						this._polylineArr.push(p);
					}
				}
			}
			var c;
			parseInt(a.status, 10) ? (a.route ? (c = {
						info: a.info,
						origin: this._str2LngLat(a.route.origin),
						destination: this._str2LngLat(a.route.destination),
						routes: this._parseRoutes(a.route.paths)
					},
					a.route.taxi_cost && (c.taxi_cost = parseFloat(a.route.taxi_cost))) : c = {
					info: "ok" === a.info.toLowerCase() ? "NO_DATA" : a.info
				},
				h.event.trigger(this, "complete", c)) : h.event.trigger(this, "error", {
				info: a.info
			});
			this._obj.setFitView();
			this._point = new h.Marker({
				id: "m" + this.guid,
				offset: new h.Pixel(-5, -5),
				position: this._polylinePath[0],
				draggable: !0,
				visible: !1,
				content: '<div title="\u62d6\u52a8\u4ee5\u66f4\u6539\u8def\u7ebf" class="drag_mapabc" style="width:11px;height:11px;background:url(http://webapi.amap.com/images/dd-via.png) 0px 0px no-repeat;"></div>'
			});
			this._point.setMap(this._obj);
			this._bind();
			h.event.trigger(this, "search", a);
			"undefined" != typeof DD_belatedPNG && DD_belatedPNG.fix(".drag_mapabc")
		},
		_onError: function(a) {
			e.event.trigger(this, "error", {
				info: "SERVICE_UNAVAILABLE"
			})
		},
		_bind: function() {
			var a = this,
				d = this._obj,
				b = 0,
				c = 0,
				f = function(e) {
					if (!b) return !1;
					e = a._pt2LineDist(a._polylinePath, e.lnglat);
					10 > e.dis ? a._point.setPosition(new AMap.LngLat(e.lng, e.lat)) : (b = 0, c || (a._point.hide(), d.off("mousemove", f)))
				},
				e = function(c) {
					b = 1;
					a._point.show();
					d.on("mousemove", f)
				};
			if (a._isArray(a._polylineArr)) {
				for (var i = 0; i < a._polylineArr.length; i++) {
					var _polyline = a._polylineArr[i];
					_polyline.on("mousemove", e);
					a._event["p" + a.guid + "-" + i] = [];
					a._event["p" + a.guid + "-" + i].push({
						obj: _polyline,
						t: "mousemove",
						fn: e
					});
				};
			}
			var g, l, k, e = function(b) {
					c = 1;
					for (var d = [], e = 0; e < a._list.length; e++) d.push(a._list[e].poi);
					g = a._pt2LineDist(d, b.lnglat);
					k = "s" + a._getGuid()
				};
			a._point.on("dragstart", e);
			a._event["m" + a.guid] = [];
			a._event["m" + a.guid].push({
				obj: a._point,
				t: "dragstart",
				fn: e
			});
			e = function(b) {
				c = 1;
				if (a._f) {
					b = b.lnglat;
					for (var d = [], e = [], f = 0; f < a._list.length; f++) d.push(a._list[f]),
					e.push(a._list[f].poi),
					f == g.i && (d.push({
						id: k,
						poi: new h.LngLat(b.lng, b.lat)
					}), e.push(new h.LngLat(b.lng, b.lat)));
					a._reSearch(e);
					l = d
				}
				a._f = 0
			};
			a._point.on("dragging", e);
			a._event["m" + a.guid].push({
				obj: a._point,
				t: "dragging",
				fn: e
			});
			e = function(b) {
				c = 0;
				a._list = l;
				a._addDragPoi(b.lnglat, k);
				a._point.hide();
				d.off("mousemove", f);
				a._nearLine()
			};
			a._point.on("dragend", e);
			a._event["m" + a.guid].push({
				obj: a._point,
				t: "dragend",
				fn: e
			})
		},
		_reSearch: function(a) {
			var d = this;
			d._roadSearch(a, function(a) {
				if (a.route) {
					d._route = a.route;
				}
				if (a.count) {
					if (d._isArray(d._polylineArr)) {
						for (var i = 0; i < d._polylineArr.length; i++) {
							d._polylineArr[i].setMap(null);
						};
						d._polylineArr = null;
						delete d._polylineArr;
					}
					d._polylineArr = [];
					for (var c = [], f = 0; f < a.route.paths.length; f++)
						for (var e = a.route.paths[f].steps, g = 0; g < e.length; g++) {
							var l = e[g].polyline.split(";"),
								k, p, path = [];
							for (k in l)
								if (l.hasOwnProperty(k)) {
									var m = l[k].split(",");
									m = new h.LngLat(m[0], m[1]);
									path.push(m);
									c.push(m)
								}
							if ((g + 1) < e.length) {
								l = e[g + 1].polyline.split(";");
								if (l.hasOwnProperty(0)) {
									var m = l[0].split(",");
									m = new h.LngLat(m[0], m[1]);
									path.push(m);
								}
							}
							e[g].path = path;
							p = new h.Polyline({
								id: "p" + d.guid + "-" + f,
								path: path,
								strokeColor: "#9400D3",
								strokeOpacity: 1,
								strokeWeight: 3
							});
							p.setMap(d._obj);
							p.setExtData(e[g]);
							d._polylineArr.push(p);
						}
					d._polylinePath = c
				}
				d._f = 1;
				h.event.trigger(d, "search", a)
				var c;
				parseInt(a.status, 10) ? (a.route ? (c = {
							info: a.info,
							origin: d._str2LngLat(a.route.origin),
							destination: d._str2LngLat(a.route.destination),
							routes: d._parseRoutes(a.route.paths)
						},
						a.route.taxi_cost && (c.taxi_cost = parseFloat(a.route.taxi_cost))) : c = {
						info: "ok" === a.info.toLowerCase() ? "NO_DATA" : a.info
					},
					h.event.trigger(d, "complete", c)) : h.event.trigger(d, "error", {
					info: a.info
				});
			})
		},
		_addDragPoi: function(a, d) {
			var b = this._obj,
				c = this,
				f = document.createElement("div");
			f.title = "\u62d6\u52a8\u4ee5\u66f4\u6539\u8def\u7ebf";
			f.className = "drag_mapabc";
			f.style.cssText = "width:11px;height:11px;background:url(http://webapi.amap.com/images/dd-via.png) 0px 0px no-repeat;";
			var e = document.createElement("img");
			e.tilte = "\u5220\u9664\u6b64\u9014\u7ecf\u70b9";
			e.style.cssText = "display:none;position:absolute;left:11px;top:-1px;z-index:2";
			e.src = "http://webapi.amap.com/images/close.gif";
			e.onclick = function() {
				c.removeWay(d)
			};
			f.appendChild(e);
			f = new h.Marker({
				id: d,
				offset: new h.Pixel(-5, -5),
				position: a,
				draggable: !0,
				zIndex: 101,
				content: f
			});
			f.setMap(b);
			b = function(a) {
				e.style.display = "block"
			};
			f.on("mouseover", b);
			var g = function(a) {
				e.style.display = "none"
			};
			f.on("mouseout", g);
			var l = function(a) {
				if (c._f) {
					var b = [];
					a = a.lnglat;
					for (var e = 0; e < c._list.length; e++) c._list[e].id == d && (c._list[e].poi = a),
					b.push(c._list[e].poi);
					c._reSearch(b)
				}
				c._f = 0
			};
			f.on("dragging", l);
			var k = function() {
				c._nearLine()
			};
			f.on("dragend", k);
			c._event[d] = [];
			c._event[d].push({
				obj: f,
				t: "dragging",
				fn: l
			});
			c._event[d].push({
				obj: f,
				t: "dragend",
				fn: k
			});
			c._event[d].push({
				obj: f,
				t: "mouseover",
				fn: b
			});
			c._event[d].push({
				obj: f,
				t: "mouseout",
				fn: g
			});
			h.event.trigger(this, "addway", this);
			"undefined" != typeof DD_belatedPNG && DD_belatedPNG.fix(".drag_mapabc")
		},
		_nearLine: function() {
			for (var a = 0; a < this._list.length; a++) {
				var d = this._pt2LineDist(this._polylinePath, this._list[a].poi);
				this._events && this._events[this._list[a].id] && this._event[this._list[a].id][0].obj.setPosition(new AMap.LngLat(d.lng, d.lat))
			}
			this._obj.setFitView();
		},
		_pt2LineDist: function(a, d) {
			for (var b = {
					dis: Number.MAX_VALUE
				},
					c = 0; c < a.length - 1; c++) {
				var f = this._pt2LineSegmentDist([a[c], a[c + 1]], d);
				f.dis < b.dis && (b = {
					lng: f.lng,
					lat: f.lat,
					dis: f.dis,
					i: c,
					_type: "lnglat"
				})
			}
			b.dis = Math.round((new h.LngLat(d.lng, d.lat)).distance(new h.LngLat(b.lng, b.lat)) / this._obj.getResolution());
			return b
		},
		_pt2LineSegmentDist: function(a, d) {
			var b = 0,
				c = 0,
				b = a[1].lng - a[0].lng,
				c = a[1].lat - a[0].lat,
				f = -(a[0].lat - d.lat) * c - (a[0].lng - d.lng) * b,
				e;
			0 >= f ? (b = a[0].lng, c = a[0].lat) : f >= (e = b * b + c * c) ? (b = a[1].lng, c = a[1].lat) : (b = a[0].lng + f * b / e, c = a[0].lat + f * c / e);
			return {
				lng: b,
				lat: c,
				dis: Math.pow(d.lng - b, 2) + Math.pow(d.lat - c, 2)
			}
		},
		removeWay: function(a) {
			for (var d = [], b = null, c = 0; c < this._list.length; c++)
				if (this._list[c].id == a) {
					for (var f = this._event[a], e = 0; e < f.length; e++) f[e].obj.off(f[e].t, f[e].fn),
					b || (b = f[e].obj);
					this._event[a] = null;
					delete this._event[a];
					this._list.splice(c, 1);
					c--
				} else d.push(this._list[c].poi);
			b.setMap(null);
			this._reSearch(d)
		},
		getWays: function() {
			for (var a = [], d = 1; d < this._list.length - 1; d++) a.push(this._list[d]);
			return a
		},
		getPath: function() {
			return this._polylinePath;
		},
		getPolylines: function() {
			return this._polylineArr;
		},
		getRoute: function() {
			return this._route
		},
		destroy: function() {
			for (var a in this._event) {
				for (var d = this._event[a], b = 0; b < d.length; b++) d[b].obj.off(d[b].t, d[b].fn),
				d[b].obj.setMap(null);
				this._event[a] = null;
				delete this._event[a]
			}
			for (a = 0; a < this._list.length; a++) this._list[a].o || (this._list.splice(a, 1), a--);
			if (this._point instanceof AMap.Marker) {
				this._point.setMap(null);
				this._point = null;
				delete this._point;
			}
			if (this._polylineArr != null && this._polylineArr.hasOwnProperty(0)) {
				for (var i = 0; i < this._polylineArr.length; i++) {
					this._polylineArr[i].setMap(null);
				};
			}
			this._polylineArr = null;
			delete this._polylineArr
		},
		_extend: function(a, d) {
			for (var b in a) a.hasOwnProperty(b) && (d[b] = a[b]);
			return d
		},
		initRoute: function(a) {
			this.search({
				info: 'init route',
				route: a,
				count: "1",
				type: "complete",
				status: "1"
			});
		}
	}
})(AMap);