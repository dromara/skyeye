
// 以下两个参数开启团队权限时有值
var objectId = '', objectKey = '';
// 根据以下两个参数判断：工作流的判断是否要根据serviceClassName的判断
var serviceClassName;

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$;
    var id = GetUrlParam("id");

    // 采购合同转采购订单
    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "querySupplierContractTransById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
        let data = json.bean;
        data.erpOrderItemList = data.supplierContractChildList
        // 采购订单的【编辑布局】
        dsFormUtil.initEditPageForStatic('content', 'FP2023042000002', data, {
            savePreParams: function (params) {
                params.xxx="123213213213213213213213"
            },
            saveData: function (params) {
                // 保存数据

            },
            loadComponentCallback: function () {
                $("div[controlType='supplier']").remove();
                $("div[controlType='purchaseOrderFromType']").remove();


            }
        });
    }});

});