layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'textool', 'laydate'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$,
        	textool = layui.textool,
            laydate = layui.laydate;
        
        textool.init({eleId: 'remark', maxlength: 200});

        laydate.render({elem: '#dateScope', range: '~'});
        
	    matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var params = {
                    name: $("#name").val(),
                    state: $("input[name='state']:checked").val(),
                    startTime: $("#dateScope").val().split('~')[0].trim(),
                    endTime: $("#dateScope").val().split('~')[1].trim(),
                    remark: $("#remark").val()
                };
                AjaxPostUtil.request({url: flowableBasePath + "ifssetofbooks002", params: params, type: 'json', method: "POST", callback: function (json) {
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