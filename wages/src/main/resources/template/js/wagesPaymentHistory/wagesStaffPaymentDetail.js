
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

        const staffId = GetUrlParam("staffId");
        const payMonth = GetUrlParam("payMonth");

        showGrid({
            id: "showForm",
            url: sysMainMation.wagesBasePath + "queryWagesStaffPaymentDetail",
            params: {staffId: staffId, payMonth: payMonth},
            pagination: false,
            method: "GET",
            template: $("#showBaseTemplate").html(),
            ajaxSendLoadBefore: function(hdb) {
            },
            ajaxSendAfter:function (json) {
                // 加载员工信息
                $("#staffMation").html(getUserStaffHtmlMationByStaffId(staffId));

                matchingLanguage();
                form.render();
            }
        });
    });
});