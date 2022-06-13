// ajax请求
var AjaxPostUtil = {
    // 基础选项
    options: {
        // 默认提交的方法,get post
        method: "post",
        // 请求的路径 required
        url: "",
        // 请求的参数
        params: {},
        // 默认异步
        async: true,
        // 返回的内容的类型,text,xml,json
        type: 'text',
        // 回调函数 required
        callback: function() {}
    },

    // 创建XMLHttpRequest对象
    createRequest: function() {
        var xmlhttp;
        try {
            // IE6以上版本
            xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
        } catch(e) {
            try {
                // IE6以下版本
                xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
            } catch(e) {
                try {
                    xmlhttp = new XMLHttpRequest();
                    if(xmlhttp.overrideMimeType) {
                        xmlhttp.overrideMimeType("text/xml");
                    }
                } catch(e) {
                    alert("您的浏览器不支持Ajax");
                }
            }
        }
        return xmlhttp;
    },
    // 设置基础选项
    setOptions: function(newOptions) {
        for(var pro in newOptions) {
            this.options[pro] = newOptions[pro];
        }
        if(isNull(String(newOptions['method']))){
            this.options['method'] = 'POST';
        }
    },
    // 格式化请求参数
    formateParameters: function() {
        var paramsArray = [];
        var params = this.options.params;
        for(var pro in params) {
            var paramValue = params[pro];
            paramsArray.push(pro + "=" + paramValue);
        }
        return paramsArray.join("&");
    },

    // 状态改变的处理
    readystatechange: function(xmlhttp) {
        // 获取返回值
        var returnValue;
        if(xmlhttp.readyState == 4 && (xmlhttp.status == 200 || xmlhttp.status == 0)) {
            //移除请求遮罩层
            layui.$("body").find(".mask-req-str").remove();
            var sessionstatus = xmlhttp.getResponseHeader("SESSIONSTATUS");
            var requestmation = xmlhttp.getResponseHeader("REQUESTMATION");
            if (sessionstatus == "TIMEOUT") {//超时跳转
                var win = window;
                while (win != win.top){
                    win = win.top;
                }
                win.location.href = "../../tpl/index/login.html";//XMLHttpRequest.getResponseHeader("CONTEXTPATH");
            }else if(sessionstatus == "NOAUTHPOINT"){
                returnValue = eval('(' + '{"returnMessage":"您不具备该权限。","returnCode":-9999,"total":0,"rows":"","bean":""}' + ')');
            }
            switch(this.options.type) {
                case "xml":
                    returnValue = xmlhttp.responseXML;
                    break;
                case "json":
                    var jsonText = xmlhttp.responseText;
                    if(requestmation == 'DOWNLOAD'){
                        returnValue = eval('(' + '{"returnMessage":"成功","returnCode":0,"total":0,"rows":"","bean":""}' + ')');
                    }else{
                        if(jsonText) {
                            returnValue = eval("(" + jsonText + ")");
                        }
                    }
                    break;
                default:
                    returnValue = xmlhttp.responseText;
                    break;
            }
            if(returnValue) {
                this.options.callback.call(this, returnValue);
            } else {
                this.options.callback.call(this);
            }
        }else if(xmlhttp.readyState == 4 && (xmlhttp.status == 404)) {
            // 移除请求遮罩层
            layui.$("body").find(".mask-req-str").remove();
            returnValue = eval('(' + '{"returnMessage":"接口请求：404","returnCode":-9999,"total":0,"rows":"","bean":""}' + ')');
            if(returnValue) {
                this.options.callback.call(this, returnValue);
            } else {
                this.options.callback.call(this);
            }
        }
    },

    // 发送Ajax请求
    request: function(options) {
        $("body").append(maskReqStr);
        var ajaxObj = this;
        // 设置参数
        ajaxObj.setOptions.call(ajaxObj, options);
        // 创建XMLHttpRequest对象
        var xmlhttp = ajaxObj.createRequest.call(ajaxObj);
        // 设置回调函数
        xmlhttp.onreadystatechange = function() {
            ajaxObj.readystatechange.call(ajaxObj, xmlhttp);
        };
        // 格式化参数
        var formateParams = ajaxObj.formateParameters.call(ajaxObj);
        // 请求的方式
        var method = ajaxObj.options.method;
        var url = ajaxObj.options.url;
        if("GET" === method.toUpperCase()) {
            url += "?" + formateParams;
        }else if("DELETE" === method.toUpperCase()){
            url += "?_method=" + method.toUpperCase();
        }
        // 建立连接
        /**
         * 同步：提交请求->等待服务器处理->处理完毕返回 这个期间客户端浏览器不能干任何事
         * 异步: 请求通过事件触发->服务器处理（这是浏览器仍然可以作其他事情）->处理完毕
         */
        xmlhttp.open(method, url, ajaxObj.options.async);//异步
        setRequestHeaders(xmlhttp);
        if("GET" === method.toUpperCase()) {
            xmlhttp.send(null);
        } else if("POST" === method.toUpperCase() || "PUT" === method.toUpperCase()) {
            // 如果是POST提交，设置请求头信息
            xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            xmlhttp.send(formateParams);
        } else if("DELETE" === method.toUpperCase()){
            // 如果是DELETE提交，设置请求头信息
            xmlhttp.setRequestHeader("Content-Type", "application/json");
            xmlhttp.send(formateParams);
        }
    }
};