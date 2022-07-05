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
        var memberId = "";
        showGrid({
            id: "showForm",
            url: shopBasePath + "memberCar003",
            params: {rowId:parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#beanTemplate").html(),
            ajaxSendLoadBefore: function(hdb){},
            ajaxSendAfter:function(data){
                memberId = data.bean.memberId;
            	textool.init({eleId: 'remark', maxlength: 400});
                $("input:radio[name=insure][value=" + data.bean.insure + "]").attr("checked", true);

			    matchingLanguage();
                form.render();
                form.on('submit(formEditBean)', function (data) {
                    if (winui.verifyForm(data.elem)) {
                        var params = {
                            rowId: parent.rowId,
                            plate: $("#plate").val(),
                            modelType: $("#modelType").val(),
                            vinCode: $("#vinCode").val(),
                            insure: $("input[name='insure']:checked").val(),
                            memberId: memberId,
                            remark: $("#remark").val()
                        };
                        AjaxPostUtil.request({url: shopBasePath + "memberCar005", params: params, type: 'json', method: "PUT", callback: function (json) {
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