
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$;

        matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var params = {
                    newMemberId: sysMemberUtil.memberMation.id,
                    memberCarId: parent.rowId,
                    plate: $("#plate").val(),
                };

                AjaxPostUtil.request({url: shopBasePath + "transferMemberCar", params: params, type: 'json', method: "POST", callback: function (json) {
                    parent.layer.close(index);
                    parent.refreshCode = '0';
                }, async: true});
            }
            return false;
        });

        // 会员选择
        $("body").on("click", ".chooseMemberBtn", function (e) {
            sysMemberUtil.openSysMemberChoosePage(function (memberMation){
                $("#memberId").val(memberMation.contacts);
            });
        });

        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});