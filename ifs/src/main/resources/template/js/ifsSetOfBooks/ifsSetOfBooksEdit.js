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

        showGrid({
            id: "showForm",
            url: flowableBasePath + "ifssetofbooks003",
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

                laydate.render({
                    elem: '#dateScope',
                    range: '~'
                });
            	
                $("input:radio[name=state][value=" + json.bean.state + "]").attr("checked", true);
                
                matchingLanguage();
                form.render();
                form.on('submit(formEditBean)', function (data) {
                    if (winui.verifyForm(data.elem)) {
                        var params = {
                            rowId: parent.rowId,
                            name: $("#name").val(),
                            state: $("input[name='state']:checked").val(),
                            startTime: $("#dateScope").val().split('~')[0].trim(),
                            endTime: $("#dateScope").val().split('~')[1].trim(),
                            remark: $("#remark").val()
                        };
                        AjaxPostUtil.request({url: flowableBasePath + "ifssetofbooks004", params: params, type: 'json', method: "PUT", callback: function(json){
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

        $("body").on("click", "#cancle", function(){
            parent.layer.close(index);
        });
    });
});