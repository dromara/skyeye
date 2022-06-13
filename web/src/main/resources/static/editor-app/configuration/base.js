
var basePath = getBaseRootPath();//上传文件展示路径

function getBaseRootPath(){
	var curWwwPath = window.document.location.href;  
    var pathName = window.document.location.pathname;  
    var pos = curWwwPath.indexOf(pathName);  
    var localhostPaht = curWwwPath.substring(0, pos);  
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);  
    return(localhostPaht + "/");//http://127.0.0.1:8080/
}

/**
 * Created by HANZO on 2016/6/17.
 */

/**
 *
 * @param url
 * @param params
 * @param callback
 * @returns {*}
 */
//(function ($) {

function loadPage(url, container) {
    if (!container)
        container = "#mainDiv";
    if (!url.startWith(basePath))
        url = basePath + url;
    jQuery(container).load(url, function (response, status, xhr) {
        if (status == "success") {
            if (response) {
                try {
                    //初始化面包屑导航 add by billjiang
                    var fun_code=jQuery("section.content-header ol.breadcrumb").data("code");
                    $("section.content-header h1").hide();
                    if(fun_code) {
                        ajaxPost(basePath + "/function/getFunctions/" + fun_code, null, function(datas) {
                            if(!datas||datas.length==0)
                                return;
                            $("section.content-header ol.breadcrumb").html("");
                            if(datas.length==1){
                                $("section.content-header ol.breadcrumb").append('<li class="active"> <i class="' + datas[0].icon + '"></i>&nbsp;&nbsp;' + datas[0].name + '</li>');
                            }else {
                                for (var i = 0; i < datas.length; i++) {
                                    var name = datas[i].name;
                                    var icon = datas[i].icon;
                                    if (i == datas.length - 1) {
                                        $("section.content-header ol.breadcrumb").append('<li class="active">' + name + '</li>');
                                    } else if (i == 0) {
                                        $("section.content-header ol.breadcrumb").append('<li><i class="' + icon + '"></i>&nbsp;&nbsp;' + name + '</li>');
                                    } else {
                                        $("section.content-header ol.breadcrumb").append('<li>' + name + '</li>');
                                    }
                                }
                            }
                        })
                    }
                    var result = jQuery.parseJSON(response);
                    if (result.code == 100) {
                        jQuery(container).html("");
                        alert(result.data);
                    }
                } catch (e) {
                    return response;
                }
            }
        }
    });
}

/**
 * Load a url into a page
 * 增加beforeSend以便拦截器在将该请求识别为非ajax请求
 */
var _old_load = jQuery.fn.load;
jQuery.fn.load = function (url, params, callback) {
    //update for HANZO, 2016/12/22
    if (typeof url !== "string" && _old_load) {
        return _old_load.apply(this, arguments);
    }

    var selector, type, response,
        self = this,
        off = url.indexOf(" ");
    if (off > -1) {
        selector = jQuery.trim(url.slice(off));
        url = url.slice(0, off);
    }
    if (jQuery.isFunction(params)) {
        callback = params;
        params = undefined;
    } else if (params && typeof params === "object") {
        type = "POST";
    }
    if (self.length > 0) {
        jQuery.ajaxSetup({cache:true});
        jQuery.ajax({
            url: url,
            beforeSend: function (xhr) {
                xhr.setRequestHeader('X-Requested-With', {
                    toString: function () {
                        return '';
                    }
                });
            },
            type: type || "GET",
            dataType: "html",
            data: params
        }).done(function (responseText) {
            //console.log(responseText);
            response = arguments;
            //页面超时跳转到首页
            if (responseText.startWith("<!--login_page_identity-->")) {
                window.location.href = basePath + "/";
            } else {
                self.html(selector ?
                    jQuery("<div>").append(jQuery.parseHTML(responseText)).find(selector) :
                    responseText);
            }
        }).always(callback && function (jqXHR, status) {
                self.each(function () {
                    callback.apply(this, response || [jqXHR.responseText, status, jqXHR]);
                });
            });
    }

    return this;
};

//递归删除空属性防止把null变成空值
function deleteEmptyProp(obj) {
    for (var a in obj) {
        if (typeof (obj[a]) == "object" && obj[a] != null) {
            deleteEmptyProp(obj[a]);
        }
        else {
            if (!obj[a]) {
                delete obj[a];
            }
        }
    }
    return obj;
}

/**
 * 获取cookie值
 * @param name
 * @returns
 */
function getCookie(name){
	var strcookie = document.cookie;//获取cookie字符串
	var arrcookie = strcookie.split("; ");//分割
	//遍历匹配
	for ( var i = 0; i < arrcookie.length; i++) {
		var arr = arrcookie[i].split("=");
		if (arr[0] == name){
			return arr[1];
		}
	}
	return "";
}

//判断内容是否为空
function isNull(str){
	if(str == null || str == "" || str == '' || str == "null" || str == "undefined"){
		return true;
	}else{
		return false;
	}
}

function ajaxPost(url, params, callback) {
    var result = null;
    var headers = getRequestHeaders();
    headers['CSRFToken'] = jQuery("#csrftoken").val();

    if (params && typeof params == "object") {
        params = deleteEmptyProp(params);
    }

    jQuery.ajax({
        type: 'post',
        async: false,
        url: url,
        data: params,
        dataType: 'json',
        headers: headers,
        //contentType : 'application/json',
        success: function (data, status) {
            result = data;
            if (data && data.returnCode && data.returnCode != 0) {
                modals.error("操作失败，请刷新重试，具体错误：" + data.returnMessage);
                return false;
            }
            if (callback) {
                callback.call(this, data, status);
            }
        },
        error: function (err, err1, err2) {
            console.log("ajaxPost发生异常，请仔细检查请求url是否正确，如下面错误信息中出现success，则表示csrftoken更新，请忽略");
            //console.log(err.responseText);
            if (err && err.readyState && err.readyState == '4') {
                var sessionstatus = err.getResponseHeader("session-status");
                //console.log(err);
                //console.log(err1);
                //console.log(err2);
                if (sessionstatus == "timeout") {
                    //如果超时就处理 ，指定要跳转的页面
                    window.location.href = basePath + "/";
                }
                else if (err1 == "parsererror") {//csrf异常
                    var responseBody = err.responseText;
                    if (responseBody) {
                        try {
                            responseBody = "{'retData':" + responseBody;
                            var resJson = eval('(' + responseBody + ')');
                            jQuery("#csrftoken").val(resJson.csrf.CSRFToken);
                            this.success(resJson.retData, 200);
                        } catch (parseError) {
                            $("#mainDiv").html(err.responseText);
                        }
                    }
                    return;
                } else {
                    modals.error({
                        text: JSON.stringify(err) + '<br/>err1:' + JSON.stringify(err1) + '<br/>err2:' + JSON.stringify(err2),
                        large: true
                    });
                    return;
                }
            }

            modals.error({
                text: JSON.stringify(err) + '<br/>err1:' + JSON.stringify(err1) + '<br/>err2:' + JSON.stringify(err2),
                large: true
            });
        }
    });

    return result;
}

function getServerTime(base_path, format) {
    var result = null;

    var sdate = new Date(ajaxPost(base_path + '/base/getServerTime'));
    if (sdate != 'Invalid Date') {
        result = formatDate(sdate, format || 'yyyy/mm/dd');
    }

    return result;
}

/**
 * 格式化日期
 */
function formatDate(date, format) {
    if (!date)return date;
    date = (typeof date == "number") ? new Date(date) : date;
    return date.Format(format);
}

Date.prototype.Format = function (fmt) {
    var o = {
        "m+": this.getMonth() + 1, // 月份
        "d+": this.getDate(), // 日
        "h+": this.getHours(), // 小时
        "i+": this.getMinutes(), // 分
        "s+": this.getSeconds(), // 秒
        "q+": Math.floor((this.getMonth() + 3) / 3), // 季度
        "S": this.getMilliseconds()
        // 毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

/**
 * 比较两个时间的大小 d1>d2 返回大于0
 * @param d1
 * @param d2
 * @returns {number}
 * @constructor
 */
function DateDiff(d1, d2) {
    var result = Date.parse(d1.replace(/-/g, "/")) - Date.parse(d2.replace(/-/g, "/"));
    return result;
}

/**
 * 字符串转日期
 * @returns {number}
 */
String.prototype.strToDate = function () {
    if (this && this != "") {
        return Date.parse(this.replace(/-/g, "/"));
    }
    else
        return "";
}
/**
 * 将map类型[name,value]的数据转化为对象类型
 */
function getObjectFromMap(aData) {
    var map = {};
    for (var i = 0; i < aData.length; i++) {
        var item = aData[i];
        if (!map[item.name]) {
            map[item.name] = item.value;
        }
    }
    return map;
}


/**
 * 获取下一个编码 000001，000001000006，6
 * 得到结果 000001000007
 */
function getNextCode(prefix, maxCode, length) {
    if (maxCode == null) {
        var str = "";
        for (var i = 0; i < length - 1; i++) {
            str += "0";
        }
        return prefix + str + 1;
    } else {
        var str = "";
        var sno = parseInt(maxCode.substring(prefix.length)) + 1;
        for (var i = 0; i < length - sno.toString().length; i++) {
            str += "0";
        }
        return prefix + str + sno;
    }

}

/**
 * 收缩左边栏时，触发markdown编辑的resize
 */
/*$("[data-toggle='offcanvas']").click(function () {
 if (editor) {
 setTimeout(function () {
 editor.resize()
 }, 500);
 }
 });*/


//获取布尔值
/*String.prototype.BoolValue=function(){
 if(this==undefined)
 return false;
 if(this=="false"||this=="0")
 return false;
 return true;
 }*/

var HtmlUtil = {
    /*1.用浏览器内部转换器实现html转码*/
    htmlEncode: function (html) {
        //1.首先动态创建一个容器标签元素，如DIV
        var temp = document.createElement("div");
        //2.然后将要转换的字符串设置为这个元素的innerText(ie支持)或者textContent(火狐，google支持)
        (temp.textContent != undefined ) ? (temp.textContent = html) : (temp.innerText = html);
        //3.最后返回这个元素的innerHTML，即得到经过HTML编码转换的字符串了
        var output = temp.innerHTML;
        temp = null;
        return output;
    },
    /*2.用浏览器内部转换器实现html解码*/
    htmlDecode: function (text) {
        //1.首先动态创建一个容器标签元素，如DIV
        var temp = document.createElement("div");
        //2.然后将要转换的字符串设置为这个元素的innerHTML(ie，火狐，google都支持)
        temp.innerHTML = text;
        //3.最后返回这个元素的innerText(ie支持)或者textContent(火狐，google支持)，即得到经过HTML解码的字符串了。
        var output = temp.innerText || temp.textContent;
        temp = null;
        return output;
    }
};

String.prototype.startWith = function (s) {
    if (s == null || s == "" || this.length == 0 || s.length > this.length)
        return false;
    if (this.substr(0, s.length) == s)
        return true;
    else
        return false;
    return true;
}

String.prototype.replaceAll = function (s1, s2) {
    return this.replace(new RegExp(s1, "gm"), s2);
}

String.prototype.format = function () {
    if (arguments.length == 0) return this;
    for (var s = this, i = 0; i < arguments.length; i++)
        s = s.replace(new RegExp("\\{" + i + "\\}", "g"), arguments[i]);
    return s;
};


//})(jQuery)