
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
    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryPatchOutLetTransById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
            let data = json.bean;
            // 仓库出库的【编辑布局】
            dsFormUtil.initEditPageForStatic('content', 'FP2024070100006', data, {
                savePreParams: function (params) {
                    params.farmId=data.farmId
                    params.departmentId=data.departmentId
                    params.salesman=data.salesman
                },
                saveData: function (params) {
                    // 保存数据
                    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "insertPatchOutLetToTurnDepot", params: params, type: 'json', method: "POST", callback: function(json) {
                            parent.layer.close(index);
                            parent.refreshCode = '0';
                        }});
                },
                loadComponentCallback: function () {
                    $("div[controlType='correspondentAllEnter']").remove();
                    $("div[controlType='depotOutFromType']").remove();
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
                    $("div[controlType='simpleTable']").find(".warehouse").prop('disabled', true);
                }
            });
        }});

});