
// 客户工具类
var sysCustomerUtil = {

    /**
     * 已经选的的客户信息
     */
    customerMation: {},

    /**
     * 客户选择页面
     *
     * @param callback 回调函数
     */
    openSysCustomerChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/customerManage/customerChoose.html",
            title: "选择客户",
            pageId: "customerChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if(typeof(callback) == "function") {
                    callback(sysCustomerUtil.customerMation);
                }
            }});
    },

};


// 考勤相关工具类
var checkWorkUtil = {

    /**
     * 获取当前登陆人的考勤班次
     *
     * @param callback 回执函数
     */
    getCurrentUserCheckWorkTimeList: function (callback) {
        AjaxPostUtil.request({url: flowableBasePath + "checkworktime007", params: {}, type: 'json', method: "GET", callback: function(json) {
                if(typeof(callback) == "function") {
                    callback(json);
                }
            }, async: false});
    },

};