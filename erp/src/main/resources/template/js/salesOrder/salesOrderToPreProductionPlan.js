
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

    // 销售订单转预生产计划
    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "querySealsOrderTransProductionPlanById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
        let data = json.bean;
        data.productionPlanChildList=data.erpOrderItemList
        // 预生产计划的【编辑布局】
        dsFormUtil.initEditPageForStatic('content', 'FP2024071100002', data, {
            savePreParams: function (params) {
            },
            saveData: function (params) {
                // 保存数据
                AjaxPostUtil.request({url: sysMainMation.erpBasePath + "insertSealsOrderToProductionPlan", params: params, type: 'json', method: "POST", callback: function(json) {
                    parent.layer.close(index);
                    parent.refreshCode = '0';
                }});
            },
            loadComponentCallback: function () {
                $("div[controlType='productionPlanFromType']").remove();
            },

            // 新增行的回调函数
            tableAddRowCallback: function (tableId) {
                $("#addRow" + tableId).remove();
                $("div[controlType='simpleTable']").find(".chooseProductBtn").prop('disabled', true);
                $("div[controlType='simpleTable']").find(".normsId").prop('disabled', true);
            }
        });
    }});
});