
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

    // 生产计划转整单委外
    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryProductionTransWholeById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
            let data = json.bean;
            data.erpOrderItemList=data.productionChildList
            $.each(data.erpOrderItemList, function(index, item){
                item.unitPrice=item.normsMation.estimatePurchasePrice
                item.taxRate=0
            });
            // 整单委外单的【编辑布局】
            dsFormUtil.initEditPageForStatic('content', 'FP2024071300003', data, {
                savePreParams: function (params) {
                },
                saveData: function (params) {
                    // 保存数据
                    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "insertProductionToWhole", params: params, type: 'json', method: "POST", callback: function(json) {
                            parent.layer.close(index);
                            parent.refreshCode = '0';
                        }});
                },
                loadComponentCallback: function () {
                    $("div[controlType='wholeOrderOutFromType']").remove();
                },
                tableAddRowCallback: function (tableId) {
                    $("#addRow" + tableId).remove();
                    $("div[controlType='simpleTable']").find(".chooseProductBtn").prop('disabled', true);
                    $("div[controlType='simpleTable']").find(".normsId").prop('disabled', true);
                }
            });
        }});

});