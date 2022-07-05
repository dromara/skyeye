
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

        // 初始化上传
        $("#imagePath").upload(systemCommonUtil.uploadCommon003Config('imagePath', 18, '', 1));

        reportModelTypeUtil.showModelTypeOperator(form, "typeBox", null, null);

        matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var params = {
                    title: $("#title").val(),
                    imagePath: $("#imagePath").find("input[type='hidden'][name='upload']").attr("oldurl"),
                    firstTypeId: $("#firstTypeId").val(),
                    secondTypeId: $("#secondTypeId").val(),
                };
                if(isNull(params.imagePath)){
                    winui.window.msg('请上传图片', {icon: 2, time: 2000});
                    return false;
                }
                AjaxPostUtil.request({url: reportBasePath + "reportbgimage002", params: params, type: 'json', method: "POST", callback: function(json) {
                    parent.layer.close(index);
                    parent.refreshCode = '0';
                }});
            }
            return false;
        });

        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});