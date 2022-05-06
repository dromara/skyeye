layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var $ = layui.$;

        showGrid({
            id: "showForm",
            url: flowableBasePath + "queryBossInterviewRegularWorkerDetailsById",
            params: {id: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#beanTemplate").html(),
            ajaxSendLoadBefore: function(hdb, json) {
                json.bean.remark = stringManipulation.textAreaShow(json.bean.remark);
                json.bean.stateName = activitiUtil.showStateName2(json.bean.state, 1);
            },
            ajaxSendAfter: function(json){
                // 附件回显
                skyeyeEnclosure.showDetails({'enclosureUpload': json.bean.enclosureInfo});

                matchingLanguage();
            }
        });

        $("body").on("click", ".enclosureItem", function(){
            download(fileBasePath + $(this).attr("rowpath"), $(this).html());
        });
    });
});