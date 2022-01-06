layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$;

        showGrid({
            id: "showForm",
            url: reqBasePath + "ifsaccountsubject003",
            params: {rowId: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#simpleTemplate").html(),
            ajaxSendAfter:function(json){
                $("#state").html(json.bean.state == "1" ? "<span class='state-up'>启用</span>" : "<span class='state-down'>停用</span>");
                $("#amountDirectionBox").html(sysIfsUtil.getAmountDirectionById(json.bean.amountDirection));
                $("#type").html(getInPoingArr(accountSubjectUtil.accountSubjectType, "id", json.bean.type, "name"));
                matchingLanguage();
                form.render();
            }
        });

    });
});