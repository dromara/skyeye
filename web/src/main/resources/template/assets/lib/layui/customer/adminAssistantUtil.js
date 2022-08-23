
// 行政管理模块相关工具
var adminAssistantUtil = {

    /**
     * 车辆管理---查询所有的车牌号用于下拉选择框
     *
     * @param callback 回执函数
     */
    queryAllVehicleList: function (callback) {
        AjaxPostUtil.request({url: flowableBasePath + "vehicle010", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

    /**
     * 资产选择页面
     *
     * @param callback 回调函数
     */
    assetCheckType: false, // 选择类型，默认单选，true:多选，false:单选
    checkAssetMation: [], // 选择时返回的对象
    openAssetChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/assetManage/assetManageChoose.html",
            title: "资产选择",
            pageId: "assetManageChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if (typeof (callback) == "function") {
                    callback(adminAssistantUtil.checkAssetMation);
                }
            }
        });
    },

    /**
     * 未申领资产明细选择页面
     *
     * @param callback 回调函数
     */
    assetReportCheckType: false, // 选择类型，默认单选，true:多选，false:单选
    checkAssetReportMation: [], // 选择时返回的对象
    openAssetReportChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/assetReportManage/assetReportUnUseChoose.html",
            title: "资产选择",
            pageId: "assetReportUnUseChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if (typeof (callback) == "function") {
                    callback(adminAssistantUtil.checkAssetReportMation);
                }
            }
        });
    },

};
