
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$;

        showGrid({
            id: "showForm",
            url: reportBasePath + "queryReportBgImageMationById",
            params: {id: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#beanTemplate").html(),
            ajaxSendLoadBefore: function(hdb) {
            },
            ajaxSendAfter:function(j){

                // 加载所属分类
                reportModelTypeUtil.showModelTypeOperator(form, "typeBox", j.bean.firstTypeId, j.bean.secondTypeId);

                // 初始化上传
                $("#imagePath").upload(systemCommonUtil.uploadCommon003Config('imagePath', 18, j.bean.imagePath, 1));

                matchingLanguage();
                form.render();
                form.on('submit(formEditBean)', function (data) {
                    if (winui.verifyForm(data.elem)) {
                        var params = {
                            title: $("#title").val(),
                            imagePath: $("#imagePath").find("input[type='hidden'][name='upload']").attr("oldurl"),
                            firstTypeId: $("#firstTypeId").val(),
                            secondTypeId: $("#secondTypeId").val(),
                            id: parent.rowId
                        };
                        if (isNull(params.imagePath)) {
                            winui.window.msg('请上传图片', {icon: 2, time: 2000});
                            return false;
                        }
                        AjaxPostUtil.request({url: reportBasePath + "editReportBgImageMationById", params: params, type: 'json', method: "PUT", callback: function(json) {
                            parent.layer.close(index);
                            parent.refreshCode = '0';
                        }});
                    }
                    return false;
                });
            }
        });

        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});