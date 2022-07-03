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
                if (typeof (callback) == "function") {
                    callback(sysSupplierUtil.supplierMation);
                }
            }
        });
    },

};