
// 菜单按钮相关工具类
var indexMenu = {

    /**
     * 分布式部署获取真实的访问路径
     *
     * @param url 系统自定义路径
     * @param menuSysWinUrl 分布式系统域名
     * @returns {string|*}
     */
    getUrlPath: function (url, menuSysWinUrl){
        if((url.substr(0, 7).toLowerCase() == "http://"
            || url.substr(0, 8).toLowerCase() == "https://")
            && url.indexOf("/tpl/") != -1){
            url += '?userToken=' + getCookie("userToken");
            return url;
        } else {
            if (!isNull(menuSysWinUrl)){
                url = menuSysWinUrl + url + '?userToken=' + getCookie("userToken");
            }
        }
        url = systemCommonUtil.getHasVersionUrl(url);
        return url;
    },

    /**
     * 传统模式界面下加载菜单信息
     *
     * @param dataMenu 菜单对象
     * @param icon 菜单图标
     */
    loadTraditionPage: function (dataMenu, icon){
        if(isNull(icon)){
            icon = "";
        }
        var id = dataMenu.attr("data-id");
        var title = dataMenu.attr("data-title");
        var sysWinUrl = dataMenu.attr("data-sysWinUrl");
        var url = dataMenu.attr("data-url");
        url = indexMenu.getUrlPath(url, sysWinUrl);
        // 这时会判断右侧#LAY_app_tabsheader属性下的有lay-id属性的li的数目，即已经打开的tab项数目
        if($("#LAY_app_tabsheader li[lay-id]").length <= 0) {
            // 如果比零小，则直接打开新的tab项
            active.tabAdd(url, id, icon + title);
        } else {
            // 否则判断该tab项是否以及存在
            var isData = false; // 初始化一个标志，为false说明未打开该tab项 为true则说明已有
            $.each($("#LAY_app_tabsheader li[lay-id]"), function() {
                // 如果点击左侧菜单栏所传入的id 在右侧tab项中的lay-id属性可以找到，则说明该tab项已经打开
                if($(this).attr("lay-id") === id) {
                    isData = true;
                }
            })
            if(isData == false) {
                // 标志为false 新增一个tab项
                active.tabAdd(url, id, icon + title);
            }
        }
        // 最后不管是否新增tab，最后都转到要打开的选项页面上
        active.tabChange(id);
        indexMenu.sendMessageToChildIFrame(url, id);
    },

    /**
     * 分布式部署，发送消息给其他项目，实现权限点共享
     *
     * @param url url地址
     * @param id frame的id
     */
    sendMessageToChildIFrame: function (url, id){
        if((url.substr(0, 7).toLowerCase() == "http://"
            || url.substr(0, 8).toLowerCase() == "https://")
            && url.indexOf("/tpl/") != -1){
            var childIframe = $("#" + id).find('iframe')[0];
            // 发送消息，实现权限点共享
            childIframe.contentWindow.postMessage(localStorage.getItem("authpoints"), "*");
        }
    }

}

