
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'laydate'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$,
            laydate = layui.laydate;

        laydate.render({elem: '#nextServiceTime', range: false});

        matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var params = {
                    id: parent.rowId,
                    serviceTechnicianId: shopUtil.staffMation.staffId,
                    nextServiceMileage: $("#nextServiceMileage").val(),
                    nextServiceTime: $("#nextServiceTime").val()
                };

                AjaxPostUtil.request({url: shopBasePath + "complateKeepFitOrder", params: params, type: 'json', method: "POST", callback: function (json) {
                    parent.layer.close(index);
                    parent.refreshCode = '0';
                }, async: true});
            }
            return false;
        });

        // // 维修技师选择
        // $("body").on("click", ".chooseServiceTechnicianBtn", function (e) {
        //     shopUtil.openStoreStaffChoosePage(function (staffMation){
        //         $("#serviceTechnician").val(staffMation.userName);
        //     });
        // });

        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});