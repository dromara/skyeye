
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

    // 采购订单转采购退货单
    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryPurchaseOrderTransById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
            let data = json.bean;
            // 采购退货的【编辑布局】
            dsFormUtil.initEditPageForStatic('content', 'FP2023042400002', data, {
                savePreParams: function (params) {
                    params.holderId=data.holderId
                    params.holderKey=data.holderKey
                },
                saveData: function (params) {
                    // 保存数据
                    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "insertPurchaseOrderToReturns", params: params, type: 'json', method: "POST", callback: function(json) {
                            parent.layer.close(index);
                            parent.refreshCode = '0';
                        }});
                },
                loadComponentCallback: function () {
                    $("div[controlType='supplier']").remove();
                    $("div[controlType='purchaseReturnsFromType']").remove();
                },
                tableAddRowCallback: function (tableId) {
                    $("#addRow" + tableId).remove();
                    $("div[controlType='simpleTable']").find(".unitPrice").prop('disabled', true);
                    $("div[controlType='simpleTable']").find(".amountOfMoney").prop('disabled', true);
                    $("div[controlType='simpleTable']").find(".taxRate").prop('disabled', true);
                    $("div[controlType='simpleTable']").find(".taxMoney").prop('disabled', true);
                    $("div[controlType='simpleTable']").find(".taxUnitPrice").prop('disabled', true);
                    $("div[controlType='simpleTable']").find(".taxLastMoney").prop('disabled', true);
                    $("div[controlType='simpleTable']").find(".chooseProductBtn").prop('disabled', true);
                    $("div[controlType='simpleTable']").find(".normsId").prop('disabled', true);
                }
            });
        }});

});