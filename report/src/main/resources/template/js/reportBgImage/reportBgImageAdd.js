
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
        $("#imagePath").upload({
            "action": reqBasePath + "common003",
            "data-num": "1",
            "data-type": "PNG,JPG,jpeg,gif",
            "uploadType": 18,
            "function": function (_this, data) {
                show("#imagePath", data);
            }
        });

        matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var params = {
                    title: $("#title").val(),
                    imagePath: ""
                };
                params.imagePath = $("#imagePath").find("input[type='hidden'][name='upload']").attr("oldurl");
                if(isNull(params.imagePath)){
                    winui.window.msg('请上传图片', {icon: 2, time: 2000});
                    return false;
                }
                AjaxPostUtil.request({url: reportBasePath + "reportbgimage002", params: params, type:'json', method: "POST", callback: function(json) {
                    if (json.returnCode == 0) {
                        parent.layer.close(index);
                        parent.refreshCode = '0';
                    } else {
                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                    }
                }});
            }
            return false;
        });

        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});