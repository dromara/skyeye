
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$;

        showGrid({
            id: "showForm",
            url: reqBasePath + "wagesstaff005",
            params: {staffId: parent.rowId, payMonth: parent.payMonth},
            pagination: false,
            method: "GET",
            template: $("#showBaseTemplate").html(),
            ajaxSendLoadBefore: function(hdb){
            },
            ajaxSendAfter:function (json) {
                // 加载员工信息
                $("#staffMation").html(getUserStaffHtmlMationByStaffId(parent.rowId));

                matchingLanguage();
                form.render();
            }
        });
    });
});