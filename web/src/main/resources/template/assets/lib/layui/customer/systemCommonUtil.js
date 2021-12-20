// 系统工具函数
var systemCommonUtil = {

    /**
     * 已经选择的员工列表
     */
    staffChooseList: [],

    /**
     * 已经选择的图标资源列表
     */
    sysIconChooseClass: [],

    /**
     * 获取系统账户信息
     *
     * @param callback 回执函数
     */
    getSysAccountListByType: function (callback){
        AjaxPostUtil.request({url: reqBasePath + "account009", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(json.returnCode == 0) {
                if(typeof(callback) == "function") {
                    callback(json);
                }
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    },

    /**
     * 获取系统企业信息
     *
     * @param callback 回执函数
     */
    getSysCompanyList: function (callback){
        AjaxPostUtil.request({url: reqBasePath + "companymation008", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(json.returnCode == 0) {
                if(typeof(callback) == "function") {
                    callback(json);
                }
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    },

    /**
     * 获取系统收支项目信息
     *
     * @param type 收支项目类型  1.收入  2.支出
     * @param callback 回执函数
     */
    getSysInoutitemListByType: function (type, callback){
        AjaxPostUtil.request({url: reqBasePath + "inoutitem007", params: {type: type}, type: 'json', method: "GET", callback: function(json) {
            if(json.returnCode == 0) {
                if(typeof(callback) == "function") {
                    callback(json);
                }
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    },

    /**
     * 加载图片
     *
     * @param src 图片地址
     */
    showPicImg: function (src){
        var pageWidth = systemCommonUtil.pageWidth();
        var imgWidth = pageWidth * 0.8;
        var left = pageWidth * 0.1;
        var str = '<div id="sysShowPicBox" style="position: absolute; left: ' + left + 'px; top: 80px">' +
            '<img id="sysShowPicImg" src="' + src + '" style="width: ' + imgWidth + 'px;" /></div>';
        layer.open({
            type: 1,
            title: false,
            closeBtn: 1,
            skin: 'pic-show-bg',
            area: ['100vw', '100vh'],
            shadeClose: true,
            content: str,
            scrollbar: false
        });
        $("#sysShowPicImg").on('click', function (e){
            e.stopPropagation();
        });
        var myimage = document.getElementById("sysShowPicImg");
        systemCommonUtil.drag($("#sysShowPicBox"));
        myimage.addEventListener("DOMMouseScroll", MouseWheelHandler, false);
        myimage.attachEvent ? myimage.attachEvent("onmousewheel", MouseWheelHandler)
            : myimage.addEventListener("mousewheel", MouseWheelHandler, false);
        function MouseWheelHandler(e) {
            // cross-browser wheel delta
            var e = window.event || e; // old IE support
            var delta = Math.max(-1, Math.min(1, (e.wheelDelta || -e.detail)));
            myimage.style.width = Math.max(50, Math.min(3600, myimage.width + (30 * delta))) + "px";
            return false;
        }
    },

    /**
     * 对象拖动
     *
     * @param obj
     */
    drag: function(obj) {
        obj.bind("mousedown", start);
        function start(event) {
            if (event.button == 0) { //判断是否点击鼠标左键
                /*
                 * clientX和clientY代表鼠标当前的横纵坐标
                 * offset()该方法返回的对象包含两个整型属性：top 和 left，以像素计。此方法只对可见元素有效。
                 * bind()绑定事件，同样unbind解绑定，此效果的实现最后必须要解绑定，否则鼠标松开后拖拽效果依然存在
                 * getX获取当前鼠标横坐标和对象离屏幕左侧距离之差（也就是left）值，
                 * getY和getX同样道理，这两个差值就是鼠标相对于对象的定位，因为拖拽后鼠标和拖拽对象的相对位置是不变的
                 */
                gapX = event.clientX - obj.offset().left;
                gapY = event.clientY - 80;
                // mousemove事件必须绑定到$(document)上，鼠标移动是在整个屏幕上的
                $(document).bind("mousemove", move);
                // 此处的$(document)可以改为obj
                $(document).bind("mouseup", stop);
            }
            return false; //阻止默认事件或冒泡
        }

        function move(event) {
            obj.css({
                "left": (event.clientX - gapX) + "px",
                "top": (event.clientY - gapY) + "px"
            });
            return false; //阻止默认事件或冒泡
        }

        function stop() {
            // 解绑定，这一步很必要，前面有解释
            $(document).unbind("mousemove", move);
            $(document).unbind("mouseup", stop);
        }
    },

    /**
     * 获取当前页面的高度
     * @returns {(function())|number|*|number}
     */
    pageHeight: function (){
        return document.compatMode == "CSS1Compat"? document.documentElement.clientHeight :
            document.body.clientHeight;
    },

    /**
     * 获取当前页面的宽度
     *
     * @returns {(function())|number|*|number}
     */
    pageWidth: function (){
        return document.compatMode == "CSS1Compat"? document.documentElement.clientWidth :
            document.body.clientWidth;
    },

    /**
     * 员工选择页面
     *
     * @param callback 回调函数
     */
    openSysUserStaffChoosePage: function (callback){
        _openNewWindows({
            url: "../../tpl/common/sysusersel.html",
            title: "员工选择",
            pageId: "sysuserselpage",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    if(typeof(callback) == "function") {
                        callback(systemCommonUtil.staffChooseList);
                    }
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    },

    /**
     * 系统资源图标选择页面
     *
     * @param callback 回调函数
     */
    openSysEveIconChoosePage: function (callback){
        _openNewWindows({
            url: "../../tpl/sysEveIcon/sysEveIconListChoose.html",
            title: "图标选择",
            pageId: "sysEveIconListChoose",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    if(typeof(callback) == "function") {
                        callback(systemCommonUtil.sysIconChooseClass);
                    }
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    },

    /**
     * tagEditor组件移除所有tag
     *
     * @param id 对象id
     */
    tagEditorRemoveAll: function(id){
        var tags = $('#' + id).tagEditor('getTags')[0].tags;
        for (i = 0; i < tags.length; i++) {
            $('#' + id).tagEditor('removeTag', tags[i]);
        }
    }

};