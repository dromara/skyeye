
// 以下两个参数开启团队权限时有值   只是复制过来了
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
    let initFirst = false



    // 工序验收的【编辑布局】
    dsFormUtil.initEditPageForStatic('content', 'FP2024071500001', {}, {
        savePreParams: function (params) {
            params.machinProcedureFarmId=id
        },
        saveData: function (params) {
            // 保存数据
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "writeMachinProcedureAccept", params: params, type: 'json', method: "POST", callback: function(json) {
                    parent.layer.close(index);
                    parent.refreshCode = '0';
                }});
        },
        // loadComponentCallback: function () {
        //     $("div[controlType='supplier']").remove();
        //     $("div[controlType='purchaseDeliveryFromType']").remove();
        // },
        // tableAddRowCallback: function (tableId) {
        //     // $("#addRow" + tableId).remove();
        //     $("div[controlType='simpleTable']").find(".unitPrice").prop('disabled', true);
        //     $("div[controlType='simpleTable']").find(".amountOfMoney").prop('disabled', true);
        //     $("div[controlType='simpleTable']").find(".taxRate").prop('disabled', true);
        //     $("div[controlType='simpleTable']").find(".taxMoney").prop('disabled', true);
        //     $("div[controlType='simpleTable']").find(".taxUnitPrice").prop('disabled', true);
        //     $("div[controlType='simpleTable']").find(".taxLastMoney").prop('disabled', true);
        //     $("div[controlType='simpleTable']").find(".chooseProductBtn").prop('disabled', true);
        //     $("div[controlType='simpleTable']").find(".normsId").prop('disabled', true);
        //     $("div[controlType='simpleTable']").find(".qualityInspection").prop('disabled', true);
        //     $("div[controlType='simpleTable']").find(".taxLastinspectionRatio").prop('disabled', true);
        //
        // }

        tableDeleteRowCallback: function (tableId) {
            if (!initFirst) {
                if (!isNull(tableId)) {
                    initFirst = true;
                    $("#addRow" + tableId).click();
                }
                // initFirst = false;
            }
        }
    });

});