layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'textool'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$,
        	textool = layui.textool;
        var selTemplate = getFileContent('tpl/template/select-option.tpl');
        var radioTemplate = getFileContent('tpl/template/radio-property.tpl');

        $("#type").html(getDataUseHandlebars(selTemplate, {rows: accountSubjectUtil.accountSubjectType}));

        // 余额方向
        $("#amountDirectionBox").html(getDataUseHandlebars(radioTemplate, {rows: sysIfsUtil.amountDirection}));

        textool.init({
	    	eleId: 'remark',
	    	maxlength: 200,
	    	tools: ['count', 'copy', 'reset']
	    });

	    matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var params = {
                    name: $("#name").val(),
                    state: $("input[name='state']:checked").val(),
                    amountDirection: $("input[name='radioProperty']:checked").val(),
                    num: $("#num").val(),
                    type: $("#type").val(),
                    remark: $("#remark").val()
                };
                AjaxPostUtil.request({url: flowableBasePath + "ifsaccountsubject002", params: params, type: 'json', method: "POST", callback: function(json){
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