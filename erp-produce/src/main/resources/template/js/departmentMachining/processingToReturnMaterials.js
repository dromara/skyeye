
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
    let initFirst = false

    // 加工单转退料单
    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryMachinTransReturnById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
        let data = json.bean;
        // 退料单的【编辑布局】
        dsFormUtil.initEditPageForStatic('content', 'FP2023100600006', data, {
            savePreParams: function (params) {
            },
            saveData: function (params) {
                // 保存数据
                AjaxPostUtil.request({url: sysMainMation.erpBasePath + "insertMachinToPickReturn", params: params, type: 'json', method: "POST", callback: function(json) {
                        parent.layer.close(index);
                        parent.refreshCode = '0';
                    }});

            },
            loadComponentCallback: function () {
                $("div[controlType='pickFromType']").remove();
            },
            tableDeleteRowCallback: function (tableId) {
                if (!initFirst) {
                    initFirst = true;
                    $("#addRow" + tableId).click();
                }
            }
        });
    }});

});