
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
    var serviceClassName = sysServiceMation["materialReceiptForm"]["key"];

    // 生产计划转部门加工单
    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryProductionTransById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
            let data = json.bean;
            data.machinChildList=data.productionChildList
            //部门加工单的【编辑布局】
            dsFormUtil.initEditPageForStatic('content', 'FP2023100300002', data, {
                savePreParams: function (params) {
                },
                saveData: function (params) {
                    // 保存数据
                    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "insertProductionToMachin", params: params, type: 'json', method: "POST", callback: function(json) {
                            parent.layer.close(index);
                            parent.refreshCode = '0';
                        }});
                },
                // 转的时候隐藏来源单据
                loadComponentCallback: function () {
                    $("div[controlType='productionFromType']").remove();
                },
                tableAddRowCallback: function (tableId) {
                    $("#addRow" + tableId).remove();
                    $("div[controlType='simpleTable']").find(".chooseProductBtn").prop('disabled', true);
                    $("div[controlType='simpleTable']").find(".normsId").prop('disabled', true);
                }
            });
        }});
});