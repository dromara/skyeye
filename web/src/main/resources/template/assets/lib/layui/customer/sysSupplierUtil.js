// 供应商相关工具类
var sysSupplierUtil = {

    /**
     * 已经选的的供应商信息
     */
    supplierMation: {},

    /**
     * 供应商选择页面
     *
     * @param callback 回调函数
     */
    openSysSupplierChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/supplier/supplierChoose.html",
            title: "选择供应商",
            pageId: "supplierChoosePage",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if (refreshCode == '0') {
                    if (typeof (callback) == "function") {
                        callback(sysSupplierUtil.supplierMation);
                    }
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
            }
        });
    },

};