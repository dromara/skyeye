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

        showGrid({
            id: "showForm",
            url: flowableBasePath + "ifsaccountsubject003",
            params: {rowId: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#beanTemplate").html(),
            ajaxSendLoadBefore: function(hdb){

            },
            ajaxSendAfter:function(json){
            	textool.init({
			    	eleId: 'remark',
			    	maxlength: 200,
			    	tools: ['count', 'copy', 'reset']
			    });

                $("#type").html(getDataUseHandlebars(selTemplate, {rows: accountSubjectUtil.accountSubjectType}));
                $("#type").val(json.bean.type);

                // 余额方向
                $("#amountDirectionBox").html(getDataUseHandlebars(radioTemplate, {rows: sysIfsUtil.amountDirection}));
                $("input:radio[name=radioProperty][value=" + json.bean.amountDirection + "]").attr("checked", true);

                $("input:radio[name=state][value=" + json.bean.state + "]").attr("checked", true);
                
                matchingLanguage();
                form.render();
                form.on('submit(formEditBean)', function (data) {
                    if (winui.verifyForm(data.elem)) {
                        var params = {
                            rowId: parent.rowId,
                            name: $("#name").val(),
                            state: $("input[name='state']:checked").val(),
                            amountDirection: $("input[name='radioProperty']:checked").val(),
                            num: $("#num").val(),
                            type: $("#type").val(),
                            remark: $("#remark").val()
                        };
                        AjaxPostUtil.request({url: flowableBasePath + "ifsaccountsubject004", params: params, type: 'json', method: "PUT", callback: function(json){
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
            }
        });

        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});