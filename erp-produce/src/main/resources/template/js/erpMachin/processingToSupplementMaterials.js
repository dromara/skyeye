
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

    // 加工单转补料单
    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryMachinTransRequestById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
        let data = json.bean;
        // data.pickChildList =data.machinChildList
        // 领料的【编辑布局】
        dsFormUtil.initEditPageForStatic('content', 'FP2023100600002', data, {
            savePreParams: function (params) {
            },
            saveData: function (params) {
                // 保存数据
                AjaxPostUtil.request({url: sysMainMation.erpBasePath + "insertMachinToPickPatch", params: params, type: 'json', method: "POST", callback: function(json) {
                        parent.layer.close(index);
                        parent.refreshCode = '0';
                    }});

            },
            loadComponentCallback: function () {
                // $("select[attrkey='departmentId']").prop('disabled', true);
                $("div[controlType='pickFromType']").remove();
            },
            tableAddRowCallback: function (tableId) {
                // $("#addRow" + tableId).remove();
                // $("div[controlType='simpleTable']").find(".chooseProductBtn").prop('disabled', true);
                // $("div[controlType='simpleTable']").find(".normsId").prop('disabled', true);

            }
        });
    }});

});