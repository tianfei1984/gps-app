function set_if_empty(ele, text) {
    var id = "#" + ele.id;
    if ($(id).val() == "" || $(id).val() == null) {
        $(id).val(text);
    }
}
function run_if_empty(ele, fun) {
    var id = "#" + ele.id;
    if ($(id).val() == "" || $(id).val() == null) {
        fun();
    }
}

function do_if_return(e, fun) {
    var keyCode = "";
    if (window.event) {
        keyCode = event.keyCode;
    } else {
        keyCode = e.which;
    }
    if (keyCode == 13) {
        fun();
    }
}

function sortJsonData(jsonArray,jsonField,type){
    SortFun.field = jsonField;
    jsonArray.sort(SortFun);
    if(type == "down"){
        jsonArray.reverse();
    }
}


function SortFun(data1, data2) {
    if (data1[SortFun.field] > data2[SortFun.field]) {
        return 1;
    }
    else if (data1[SortFun.field] < data2[SortFun.field]) {
        return -1;
    }
    return 0;
}

function active_menu_item(group, item) {
    $(group).toggleClass('activated');
    $(item).toggleClass('selected');
}

function active_home_menu_item(item) {
    if (!$('a:first', $(item)).hasClass('home')) {
        return;
    }
    if ($('a:first', $(item)).hasClass('sub-snav-group')
            || $('a:first', $(item)).hasClass('sub-snav-group-selected')) {
        $('a:first', $(item)).toggleClass('sub-snav-group sub-snav-group-selected');
    }
}

function active_tab_item(item) {
    $(item).toggleClass('selected default');
}

function isNumber(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}

jQuery.fn.center = function(parent) {
	  var parent = parent ? parent : window;
      this.css("position", "absolute");
      var windowHeight = $(parent).height();
      var windowWidth = $(parent).width();
      var elemHeight = this.outerHeight();
      var elemWidth = this.outerWidth();
      this.css("top", (Math.abs(windowHeight - elemHeight) / 2) + $(window).scrollTop() + "px");
      this.css("left", ((windowWidth - elemWidth) / 2) + $(window).scrollLeft() + "px");
      return this;
};

$(function() {
    $(".agent-tabs .agent-tab-bar").click(function(e) {
        $(this.parentElement).toggleClass('activated');
        $('ul', $(this.parentElement)).toggleClass('selected');
        return true;
    });
});

function showMessage(message) {
    $("#message").html(message);
    $("#messageShow").center().show();
};
function closeMessage() {
    $("#messageShow").hide();
};

function getMinuteToTimes(minutes) {
    var minute = parseInt(minutes);
    if (minute < 1) {
        return  "1秒以前";
    }
    if (minute > 60) {// 大于60分钟,转换为小时
        minute = minute / 60;
        if (minute > 24) {// 转为小时判断大于24,转换为天数
            minute = minute / 24;
            if (minute > 30) {// 转为天数判断是否大于30,转为月
                minute = minute / 30;
                if (minute > 12) {
                    minute = minute / 12;
                    return Math.round(minute) + "年以前";
                } else {
                    return Math.round(minute) + "月以前";
                }
            } else {
                return Math.round(minute) + "天以前";
            }
        } else {
            return Math.round(minute) + "小时以前";
        }
    } else {
        return Math.round(minute) + "分钟以前";
    }
}
// get url query arguments
function getQueryString(name) {
    try {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = decodeURI(window.location.href).split("?")[1].match(reg);
        if (r != null)
            return unescape(r[2]);
        return null;
    } catch (e) {
        return null;
    }

}

String.prototype.toEscape = function() {

    var result = this.replace(/</g, "&lt;").replace(/>/g, "&gt;");

    return result;
};
function getProjectPre(pageIndex,contianer){
    getProjectContentList(projectName, containerName, linkUrlName, false,pageIndex, isEditable, isDeletable)
}
var projectName;
var containerName;
var linkUrlName;
var isEditable = true;
var isDeletable = true;
function getProjectContentList(project, container, linkUrl, flag,pageIndex, editable, deletable) {
    projectName = project;
    containerName = container;
    linkUrlName = linkUrl;
    isEditable = editable;
    isDeletable = deletable;
    $
            .ajax({
                url : "/cms/ws/0.1/content/"+project+"/list?start="+(parseInt(pageIndex)*10)+"&count=10&_order=publishdate",
                dataType : "json",
                success : function(data) {
                    var items = data.items;
                  //  sortJsonData(items,"publishdate","down");
                    var contentList = "";
                    var url;
                    if(flag){
                        pagination(data.totalCount,getProjectPre);
                    }
                    if (linkUrl) {
                        url = linkUrl;
                    }

                    if (items.length > 0) {
                        for (i = 0; i < items.length; i++) {
                            var itmes = items[i];
                            var editHtml = "";
                            var deleteHtml = "";
                            if (editable) {
                                editHtml = "<a href='"
                                            + url
                                            + ""
                                            + itmes._id
                                            + "' style='cursor:pointer'>编辑</a>";
                            }

                            if (deletable) {
                                deleteHtml = "<a href='#' style='cursor:pointer' onclick=\"deleteContent('"
                                            + itmes._id + "')\">删除</a>";
                            }

                            contentList += "<li><div>"
                                    + itmes.title
                                    + "</div><div>"+itmes.publishdate+"</div><div>" + editHtml + "</div><div>|</div><div>" + deleteHtml + "</div></li>";
                        }
                        $("#" + container).html(contentList);
                    }
                }
            });
}

function deleteContent(contentId) {
    if (confirm("确认删除该内容?")) {
        $.ajax({
            url : "/cms/ws/0.1/content/" + contentId + "/",
            dataType : "json",
            type : "delete",
            success : function(data) {
                window.location.reload(true);
            }
        });
    }

}

function common401Handler(xhr) {
    if (xhr.status == 401) {
        try {
            var errorMessage = eval("(" + xhr.responseText + ")");
            showErrorDialog('alert', errorMessage[0].message, true);
            return true;
        } catch (e) {
            showErrorDialog('alert', '您还没有登录或者会话已过期', true);
            return true;
        }
    }
    return false;
}

var t = 5;
function showErrorDialog(level, text, link) {
    // 显示图片
    var promptImage = '';
    if (level == 'alert')
        promptImage = '<div class="center"><img src="images/cs_icon_warn_20x20.png" class="center"></div>';
    else if (level == 'info')
        promptImage = '<div class="center"><img src="images/cs_icon_ok.png" class="center"></div>';
    else if (level == 'error')
        promptImage = '<div class="center"><img src="images/cs_icon_warn_20x20.png" class="center"></div>';

    // 是否显示链接
    var linkHTML = '';
    // 如果需要链接则显示链接
    if (link) {
        setInterval("writeTime()", 1000);
        setTimeout("relogin()", 5000);
        linkHTML = '   <div class="center" id="message" onclick="javascript:relogin()">系统在<span id="promptTime">5</span>秒钟后自动调转到登录页面  &nbsp;&nbsp;点击直接跳转到首页</div>';
    }

    // 提示div内容
    var errorBody = '<div class="hint"> 提示<div class="close-button"><img src="./images/cs_map_pop_close_btn.png" onclick="closeMessage()"></div></div>'
            + '<div>'
            + '<div class="message" style="">'
            + promptImage
            + '<div class="center" id="message">'
            + text
            + '</div>'
            + linkHTML
            + '</div>'
            + '<div class="center" style="margin-top:20px;" >'
            + '<div class="small_button center" onclick="closeMessage()">关闭</div>' + '</div></div>';
    var errorHtml = '<div class="small-dialog" id="messageShow">' + errorBody + '</div>';
    // 显示DIV并居中
    if ($("#messageShow").length) {
        $("#messageShow").html(errorBody);
    } else {
        $('body').append(errorHtml);
    }
    $("#messageShow").show();
    $("#messageShow").center();
}

function relogin() {
    window.location.href = "/";
}

function writeTime() {
    t--;
    $("#promptTime").html(t);
}

function closeMessage() {
    $("#messageShow").hide();
}
function inputCountLimit(textId, countId,maxLength) {
    var world = $("#" + textId).val().length;
    var maxCount = $("#"+maxLength).text();
    if (world > parseInt(maxCount)) {
        $("#" + textId).val($("#" + textId).val().substring(0, parseInt(maxCount)));
    } else {
        $("#" + countId).text(parseInt(maxCount) - world);
    }
}
function pagination(userCount, func) {
    var pageCount = 1;
    if (userCount > 10) {
        if (parseInt(userCount) % 10 != 0) {
            pageCount = Math.ceil(userCount / 10);
        } else {
            pageCount = parseInt(userCount) / 10;
        }
    }
    $("#paging_panel").pagination(pageCount, {
        num_edge_entries : 1, // 边缘页数
        num_display_entries : 4, // 主体页数
        callback : func,
        items_per_page : 1
    // 每页显示1项
    });
}
/*
 * 字数监控 showCountId 字数显示组件Id maxNumber 最长输入长度
 */
$.fn.artTxtCount = function(showCountId, maxNumber){
    var oldChar;
    var count = function(){
        // val = $(this).val().length;
        var bytelen = $(this).val().replace(/[^\x00-\xff]/g, "xx").length;
        var val=Math.round(bytelen/2);
        if(val <= maxNumber){
            oldChar = $(this).val();
            $("#" + showCountId).html('您还能输入<font class="input_count">' +  (maxNumber - val) + '</font>个字');
        }else{
            // $("#" + showCountId).html('您的输入已超<font class="over_input_count">'
            // + (val - maxNumber) + '</font>个字');
            $(this).val(oldChar);
        };
    };
    $(this).bind('propertychange', count);
    $(this).bind('input', count);
    return this;
};
function smallMessageShow(obj, level, message) {
    var className = "";
    var imgSrc = "";
    $(obj).css("position", "relative");
    if (level == "wait") {
        color = "";
        imgSrc = "images/loading.gif";
    } else if (level == "warning") {
        color = "#BB9513;font-weight: bold";
        imgSrc = "images/icon_warming_small.png";
    } else {
        color = "#BB9513;font-weight: bold";
        imgSrc = "images/icon_fobidden_small.png";
    }
    var $div = "<div id='wait' class='hint-card' style='top:20px;'><div class=left><image src='" + imgSrc
            + "'/></div><div class='left' style='margin-left:5px;height:17px;line-height:17px;color:" + color
            + "'>" + message + "</div></div>";
    $(obj).append($div);
}
function haveArray(element , array){
    for ( var i = 0, length = array.length; i < length; i++ ) {
        if ( element.indexOf(array[ i ]) !=-1 ) {
            return i;
        }
    }
    return -1;
};
$.fn.permitCount = function(maxNumber){
    var oldChar;
    var count = function(){ 
        var bytelen = $(this).val().replace(/[^\x00-\xff]/g, "xx").length;
        var val=bytelen;
        if(val <= maxNumber){
            oldChar = $(this).val(); 
        }else{  
           $(this).val(oldChar);
		}
        
    };
    $(this).bind('propertychange', count);
    $(this).bind('input', count);
    return this;
}; 
$.cookie = function(name, value, options) {
	if ( typeof value != 'undefined') {// name and value given, set cookie
		options = options || {};
		if (value === null) {
			value = '';
			options.expires = -1;
		}
		var expires = '';
		if (options.expires && ( typeof options.expires == 'number' || options.expires.toUTCString)) {
			var date;
			if ( typeof options.expires == 'number') {
				date = new Date();
				date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
			} else {
				date = options.expires;
			}
			expires = '; expires=' + date.toUTCString();
			// use expires attribute, max-age is not supported by IE
		}
		var path = options.path ? '; path=' + options.path : '';
		var domain = options.domain ? '; domain=' + options.domain : '';
		var secure = options.secure ? '; secure' : '';
		document.cookie = [name, '=', encodeURIComponent(value), expires, path, domain, secure].join('');
	} else {// only name given, get cookie
		var cookieValue = null;
		if (document.cookie && document.cookie != '') {
			var cookies = document.cookie.split(';');
			for (var i = 0; i < cookies.length; i++) {
				var cookie = jQuery.trim(cookies[i]);
				// Does this cookie string begin with the name we want?
				if (cookie.substring(0, name.length + 1) == (name + '=')) {
					cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
					break;
				}
			}
		}
		return cookieValue;
	}
};