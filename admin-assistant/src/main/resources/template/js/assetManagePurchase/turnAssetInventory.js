
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

    // 资产采购订单转资产采购入库
    AjaxPostUtil.request({url: sysMainMation.admBasePath + "queryAssetPurchaseOrderTransById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
            let data = json.bean;
            // 资产采购入库的【编辑布局】
            dsFormUtil.initEditPageForStatic('content', 'FP2024070500002', data, {
                savePreParams: function (params) {
                    params.fromId=data.oddNumber
                },

                saveData: function (params) {
                    // 保存数据
                    AjaxPostUtil.request({url: sysMainMation.admBasePath + "insertAssetPurchaseOrderToTurnPut", params: params, type: 'json', method: "POST", callback: function(json) {
                        parent.layer.close(index);
                        parent.refreshCode = '0';
                    }});
                },
                tableAddRowCallback: function (tableId) {
                    $("#addRow" + tableId).remove();
                    $("div[controlType='simpleTable']").find(".assetId").prop('disabled', true);
                    $("div[controlType='simpleTable']").find(".fromId").prop('disabled', true);
                    $("div[controlType='simpleTable']").find(".unitPrice").prop('disabled', true);
                    $("div[controlType='simpleTable']").find(".chooseAssetBtn").prop('disabled', true);
                }
            });
        }});

});