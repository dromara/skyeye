
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$;

        showGrid({
            id: "showForm",
            url: reportBasePath + "reportmodeltype005",
            params: {id: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#showBaseTemplate").html(),
            ajaxSendLoadBefore: function(hdb){
            },
            ajaxSendAfter:function(j){

                showGrid({
                    id: "parentId",
                    url: reportBasePath + "reportmodeltype006",
                    params: {"parentId": 0},
                    pagination: false,
                    method: "GET",
                    template: getFileContent('tpl/template/select-option.tpl'),
                    ajaxSendLoadBefore: function(hdb){
                    },
                    ajaxSendAfter:function(json){
                        $("#parentId").val(j.bean.parentId);
                        form.render('select');
                    }
                });

                matchingLanguage();
                form.render();
                form.on('submit(formEditBean)', function (data) {
                    if (winui.verifyForm(data.elem)) {
                        var params = {
                            name: $("#name").val(),
                            parentId: $("#parentId").val(),
                            id: parent.rowId
                        };
                        AjaxPostUtil.request({url:reportBasePath + "reportmodeltype004", params: params, type:'json', method: "PUT", callback:function(json){
                            if(json.returnCode == 0){
                                parent.layer.close(index);
                                parent.refreshCode = '0';
                            }else{
                                winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
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