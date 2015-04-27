$(document).ready(function() {
    var rightContent = $('.mapRightContent:visible');
    var leftContent = $('.mapLeftContent:visible');

    $('span[name=slideDown]:visible').click(function() {
        var self = $(this);
        var parent = $(this).parents('.mapLeftContent');
        var value = self.hasClass("smallLeftBtn");
        if (value) {
            hideArea(parent, 'left');
        } else {
            showArea(parent, 'left');
            hideArea(rightContent, 'right');
        }
    });
    $('span[name=rightHide]:visible').click(function() {
        var self = $(this);
        var parent = $(this).parents('.mapRightContent');
        var width = parent.outerWidth();
        var value = self.hasClass("smallLeftBtn");
        if (value) {
            showArea(parent, 'right');
            hideArea(leftContent, 'left');
        } else {
            hideArea(parent, 'right');
            showArea(leftContent, 'left');
        }
    });
    //显示鼠标工具
    $('.mapTools').hover(function() {
        var self = $(this);
        var ul = $('ul', self);
        ul.slideDown();
    }, function() {
        var self = $(this);
        var ul = $('ul', self);
        ul.slideUp();
    });
    //最大化地图
    $('.mapMaxZoom').click(function() {
        hideArea(leftContent, 'left');
        hideArea(rightContent, 'right');
        $('.spanBtn').click();
    });
    var efencType = {
        "0" : 'rectangle',
        "1" : 'circle',
        '2' : 'polygon',
        '3' : 'polyline',
        '4' : 'marker'
    };
    $('#fenceList input[type=checkbox]').live('click', function() {
        var self = $(this);
        if (this.checked) {
            var area = self.attr('area');
            var efType = self.attr('efType') + "";
            efType = efencType[efType];
            mapTool.showArea(efType, area.split(";"));
        }

    });
    $('.map_popContainer').live('mouseleave',function(){
            $(this).hide();
    })
});
/*隐藏显示左侧右侧区域*/
function hideArea(obj, dirction,callBack) {
    var width = obj.outerWidth();
    var showHide = $('.showHide', obj);
    switch(dirction) {
        case 'left':
            obj.animate({
                "left" : -width
            }, 'slow', function() {
                showHide.removeClass("smallLeftBtn").addClass("smallRightBtn");
                if(callBack){
                    callBack();
                }
            });
            break;
        case 'right':
            obj.animate({
                "right" : -width
            }, 'slow', function() {
                showHide.removeClass("smallRightBtn").addClass("smallLeftBtn");
            });
            break;
    };
};
//显示左右侧内容区
function showArea(obj, dirction) {
    var width = obj.outerWidth();
    var showHide = $('.showHide', obj);
    switch(dirction) {
        case 'left':
            obj.animate({
                "left" : 0
            }, 'slow', function() {
                showHide.removeClass("smallRightBtn").addClass("smallLeftBtn");
            });
            break;
        case 'right':
            obj.animate({
                "right" : 0
            }, 'slow', function() {
                showHide.removeClass("smallLeftBtn").addClass("smallRightBtn");
            });
            break;
    };

};
function getCodeByType(type) {
    switch(type.toLocaleLowerCase()) {
        case "rectangle":
            return 0;
            break;
        case "circle":
            return 1;
            break;
        case "polygon":
            return 2;
            break;
        case "polyline":
            return 3;
            break;
        case "marker":
            return 4;
            break;
        default:
            return "";
    }
};
function getTypeByCode(code) {
    switch(parseInt(code)) {
        case 0:
            return "rectangle";
        case 1:
            return "circle";
        case 2:
            return "polygon";
        case 3:
            return "polyline";
        case 4:
            return "marker";
        default:
            return "";
    }
};
function getBindTypeByCode(code) {
    switch(parseInt(code)) {
        case 0 :
            return "markerMen";
        case 1 :
            return "markerCar";
        case 2:
            return "markerBoat";
        default:
            return "defaultMarker";
    }
}

function getAlertType(code) {
    switch(code) {
        case 0 :
            return "移位告警";
        case 1 :
            return "区域告警";
        case 2 :
            return "偏航告警";
        case 3 :
            return "SOS告警"

    }
}

function getAlertLevel(code) {
    switch(code) {
        case 0 :
            return "严重";
        case 1 :
            return "一般";

    }
}

function getDealStatus(code) {
    switch(code) {
        case 0 :
            return "待处理";
        case 1 :
            return "处理中";
        case 2 :
            return "处理结束";
        default:
            return "未处理";
    }
}

function getMarkerType(code) {
    switch(parseInt(code)) {
        case 0 :
            return "men";
        case 1 :
            return "car";
        case 2 :
            return "boat";
        default:
            return "default";
    }
}

function bulidMapPop(id, htmlArray) {
    var html = [];
    var id = "marker_" + id;
    html.push('<div class="map_popContainer" id="' + id + '">');
    html.push('<table>');
    html.push('<tbody>');
    html.push('<tr class="top">');
    html.push('<td class="map_topLeft"></td>');
    html.push('<td class="map_topMiddle"></td>');
    html.push('<td class="map_topRight"><a class="closeButton" onclick="$(this).parents(\'.map_popContainer\').hide()"></a></td>');
    html.push('</tr>');
    html.push('<tr class="middle">');
    html.push('<td class="map_middleLeft"></td>');
    html.push('<td class="map_middleMiddle">')
    html.push('<table style="border-collapse: collapse">');
    html.push('<tbody>');
    html.push(htmlArray.join(""));
    html.push('</tbody>');
    html.push('</table>');
    html.push('</td>');
    html.push('<td class="map_middleRight"></td>');
    html.push('</tr>')
    html.push('<tr class="bottom">');
    html.push('<td class="map_bottomLeft"></td>');
    html.push('<td class="map_bottomMiddle"><div class="map_bottomTriangle"></div></td>');
    html.push('<td class="map_bottomRight"></td>');
    html.push('</tr>');
    html.push('</tbody>');
    html.push('</table>');
    html.push('</div>');
    return html.join('');
}

function marker_operation(_this) {
    var self = $(_this);
    var parent = self.parents('.map_popContainer');
    var name = self.attr('name');
    var id = parent.attr('id').split("_")[1];
    var rightContent = $('.mapRightContent:visible');
    var leftContent = $('.mapLeftContent:visible');
    var errorCallback = function(xhr) {
        var status = xhr.status;
        if (status == 408) {
            $.carsmart.common.message.alert('请求超时请稍后再试', 'error');
        };
        if (status == 502) {
            $.carsmart.common.message.alert('网关超时请稍后再试', 'error');
        } else {
            var response = xhr.responseText;
            try {
                response = eval('(' + response + ')');
                $.carsmart.common.message.alert(response[0].message, 'error');
            } catch(e) {
                $.carsmart.common.message.alert('请求错误', 'error');
            }
        };
        $('.mapPositionNumber').hide();

    }
    var getTraceHistory = function(id, pageIndex) {
        var id = id;
        var startTime = $('#startTime').val();
        var endTime = $('#endTime').val();
        var data = {
            pageindex : pageIndex,
            pagesize : 20,
            bean : {
                simNumber : id,
                startTime : startTime,
                endTime : endTime
            }
        };
        var url = "/datatransform-ws/ws/0.1/locuRelation/page";
        var success = function(data) {
            var page = data.page + 1;
            var totalPage = data.totalPages;
            var items = data.items;
            var length = items.length;
            var i = 0;
            var htmlWraper = [];
            if (length > 0) {
                for (i; i < length; i++) {
                    var item = items[i];
                    var startTime = item.startTimeStr;
                    var endTime = item.stopTimeStr;
                    var coordinates = item.coordinates;
                    var pointsLen = coordinates.length;
                    var j = 0;
                    var pointArr = [];
                    for (j; j < pointsLen; j++) {
                        var point = coordinates[j];
                        pointArr.push(point.longitude + "," + point.latitude);
                    }
                    var simNumber = item.simNumber;
                    var className = "";
                    if (i % 2 == 0) {
                        className = "evenRow";
                    }
                    var html = '<tr coordinate="' + pointArr.join(";") + '" class="' + className + '"><td>' + simNumber + '</td><td>' + startTime + '到' + endTime + '</td></tr>';
                    htmlWraper.push(html);
                }
                $('#traceList tbody').html(htmlWraper.join(''));
                $('.pageShow', rightContent).html(page);
                $('.totalPage', rightContent).html("-" + totalPage);
                $('.prePage', rightContent).unbind('click').click(function() {
                    var page = $('.pageShow', rightContent).html();
                    page = parseInt(page);
                    if (page == 1) {
                        return false;
                    } else {
                        var type = $('input[name=fence]:checked').val();
                        $('.pageShow', rightContent).html(page - 1);
                        getTraceHistory(id, page - 2);
                    }

                });
                $('.nextPage', rightContent).unbind('click').click(function() {
                    var page = $('.pageShow', rightContent).html();
                    var total = $('.totalPage', rightContent).html();
                    page = parseInt(page) + 1;
                    if (page <= Math.abs(parseInt(total))) {
                        getTraceHistory(id, page - 1);
                    };
                });
            } else {
                $('.pageShow', rightContent).html(0);
                $('.totalPage', rightContent).html("-" + 0);
            }

        }
        postMethodRequestWithData(url, data, success, errorCallback, true);
    }
    //创建轨迹回放方法
    var bulidTraceHtml = function(id) {
        var content = $('.rightcontent', rightContent);
        content.html('');
        var winHeight = $(window).height();
        $('.title', rightContent).html('轨迹回放');
        content.append('<div class="dateWrap"><div class="mg10"><span>开始时间:</span><input type="text" class="Wdate commonInput" id="startTime" onfocus="WdatePicker({startDate:\'%y-%M-%d\',skin:\'ext\',dateFmt:\'yyyy-MM-dd\',maxDate:\'#F{$dp.$D(\\\'endTime\\\')}\',alwaysUseStartDate:true})"></div><div class="mg10"><span>结束时间:</span><input type="text" class="Wdate commonInput" id="endTime" onfocus="WdatePicker({startDate:\'%y-%M-%d\',skin:\'ext\',dateFmt:\'yyyy-MM-dd\',minDate:\'#F{$dp.$D(\\\'startTime\\\')}\',alwaysUseStartDate:true})"></div><div style="text-align:center"><input type="button" value="轨迹查询" class="getHistoryPosition"></div><div class="clear"></div></div>');
        content.append('<table id="traceList" class="tableNoneBorder"><thead> <tr class="theadBg"><th>SIM卡号</th><th>定位时间段</th> </tr></thead><tbody></tbody></table>');
        content.append('<div class="footerContent mg10"><p class="pageShow">1</p><p class="totalPage">-1</p><span class="smallLeftBtn prePage" title="上一页"> </span><span class="smallRightBtn nextPage" title="下一页"> </span></div>')
        $('.getHistoryPosition').unbind().click(function() {
            getTraceHistory(id, 0);
        });
    };
    //发动短信
    var sendTextBrach = function(receiver, content) {
        var url = "/beidou-ws/ws/0.1/sms/dispatchToSend";
        var postData = {
            to : receiver,
            content : content,
            clientId : session.clientId
        }
        $.ajax({
            url : url,
            type : 'post',
            data : $.toJSON(postData),
            contentType : 'application/json ;charset=utf-8',
            dataType : 'json',
            success : function(data) {
                $.carsmart.common.message.alert('发送成功', 'success');
                hideArea(rightContent, 'right');
                showArea(leftContent, 'left');
            },
            error : function(xhr) {
                $.carsmart.common.message.alert('发送失败', 'error');
            }
        });
    };
    // 发动短报文
    var sendMessageBrach = function(receiver, sendDate, content) {
        var url = "/datatransform-ws/ws/0.1/datatransform/sendMessageBatch";
        var postData = {
            receiver : receiver,
            content : content,
            sendDate : sendDate
        }
        $.ajax({
            url : url,
            type : 'post',
            data : $.toJSON(postData),
            contentType : 'application/json ;charset=utf-8',
            dataType : 'json',
            success : function(data) {
                $.carsmart.common.message.alert('发送成功', 'success');
                hideArea(rightContent, 'right');
                showArea(leftContent, 'left');
            },
            error : function(xhr) {
                $.carsmart.common.message.alert('发送失败', 'error');
            }
        });
    };
    // 点击发送短报文按钮
    var showEnterMessage = function(id, name) {
        var ul = $('.mapRightContent:visible .rightcontent');
        var footer = $('.mapRightContent:visible .footer').html("");
        if (name == "message") {
            $('.mapRightContent .title').html('发送短信');
            ul.html('<div><input type="radio" value="0"  name="messageType">发送短报文<input type="radio" checked="checked" value="1" name="messageType">发送短信</div><div class="mt10"><p style="margin:5px;float:left">收件人:</p><input type="text" value="' + id + '" class="commonInput" id="contact"></div><div class="mt10"><p style="margin:5px;float:left">内容:</p><textarea style="width:290px;height:100px;margin:0px" id="shortContent"></textarea></div><div style="text-align:center"><input type="button" value="发送" id="sendShortMessageAlert"></div>')
        } else {
            $('.mapRightContent .title').html('发送短报文');
            ul.html('<div><input type="radio" value="0"  checked="checked" name="messageType">发送短报文<input type="radio" value="1" name="messageType">发送短信</div><div class="mt10"><p style="margin:5px;float:left">收件人:</p><input type="text" value="' + id + '" class="commonInput" id="contact"></div><div class="mt10"><p style="margin:5px;float:left">内容:</p><textarea style="width:290px;height:100px;margin:0px" id="shortContent"></textarea></div><div style="text-align:center"><input type="button" value="发送" id="sendShortMessageAlert"></div>')
        }
        $('#contact').val(id);
        showArea(rightContent, 'right');
        // 发送短报文
        $('#sendShortMessageAlert').click(function() {
            var receiver = $('#contact').val();
            var sendDate = moment(new Date()).format('YYYY-MM-DD HH:mm:ss');
            var content = $('#shortContent').val();
            var messageType = $('input[name=messageType]:checked').val();
            if (messageType == "0") {
                sendMessageBrach(receiver, sendDate, content);
            } else {
                sendTextBrach(receiver, content);
            }
        });
        return true;

    };
    switch(name) {
        case 'phone':
            bulidTraceHtml(id);
            showArea(rightContent, 'right');
            hideArea(leftContent, 'left');
            break;
        case 'message':
        case 'shortMessage':
            if (showEnterMessage(id, name)) {
                hideArea(leftContent, 'left');
            };
            break;
    }
}

function getClientId() {
    var _url = "/datatransform-ws/ws/0.1/monitorManage/getClientList";
    var CLIENTID = new Array();
    CLIENTID.push({
        id : '',
        name : '全部'
    });
    $.ajax({
        url : _url,
        type : 'get',
        async : false,
        success : function(data) {
            if (data) {
                $.each(data, function(index, val) {
                    CLIENTID.push({
                        id : val.id,
                        name : val.name
                    });
                });
            }
        }
    });
    return CLIENTID;
}