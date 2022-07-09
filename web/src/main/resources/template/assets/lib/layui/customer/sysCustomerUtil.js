
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
            url: "../../tpl/customermanage/customerChoose.html",
            title: "客户选择",
            pageId: "customerChoose",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode) {
                if(typeof(callback) == "function") {
                    callback(sysCustomerUtil.customerMation);
                }
            }});
    },

    /**
     * 获取已上线的客户所属行业列表
     *
     * @param callback 回执函数
     */
    queryCrmCustomerIndustryIsUpList: function (callback){
        AjaxPostUtil.request({url: flowableBasePath + "crmcustomerindustry008", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

}