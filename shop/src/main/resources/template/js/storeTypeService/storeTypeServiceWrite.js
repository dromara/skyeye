// 以下两个参数开启团队权限时有值
var objectId = '', objectKey = '';
var objectParams = {};
// 根据以下两个参数判断：工作流的判断是否要根据serviceClassName的判断
var serviceClassName;

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery'], function (exports) {
    winui.renderColor();
    var $ = layui.$;
    var id = GetUrlParam("id");
    var storeId = GetUrlParam("storeId");

    if (isNull(id)) {
        if (isNull(storeId)) {
            winui.window.msg('请选择门店', {icon: 2, time: 2000});
            return false;
        }
        dsFormUtil.initAddPageForStatic('content', 'FP2024092100004', {
            savePreParams: function (params) {
                params.storeId = storeId;
            }
        });
    } else {
        AjaxPostUtil.request({
            url: sysMainMation.shopBasePath + "selectStoreTypeById",
            params: {id: id},
            type: 'json',
            method: 'POST',
            callback: function (json) {
                let data = json.bean;
                storeId = json.bean.storeId;
                dsFormUtil.initEditPageForStatic('content', 'FP2024092100005', data, {
                    savePreParams: function (params) {
                        params.storeId = storeId;
                    }
                });
            }
        });
    }
});