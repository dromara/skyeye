
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

    // 预生产计划 转 生产计划
    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryProductionPlanTransById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
            let data = json.bean;
            // 因为要调生产计划的编辑布局，所以把预生产计划里的productionPlanChildList给到生产计划里的productionChildList
            data.productionChildList = data.productionPlanChildList
            // 生产计划的【编辑布局】
            dsFormUtil.initEditPageForStatic('content', 'FP2023092200002', data, {
                saveData: function (params) {
                    // 保存数据
                    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "insertProductionPlanToProduction", params: params, type: 'json', method: "POST", callback: function(json) {
                            parent.layer.close(index);
                            parent.refreshCode = '0';
                        }});
                },

                // 移除来源单据
                loadComponentCallback: function () {
                    $("div[controlType='productionFromType']").remove();
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