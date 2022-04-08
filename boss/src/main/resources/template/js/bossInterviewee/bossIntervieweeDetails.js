
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form;

    showGrid({
        id: "showForm",
        url: flowableBasePath + "bossInterviewee004",
        params: {id: parent.rowId},
        pagination: false,
        method: "GET",
        template: $("#beanTemplate").html(),
        ajaxSendLoadBefore: function(hdb, json){
            json.bean.basicResume = stringManipulation.textAreaShow(json.bean.basicResume);
        },
        ajaxSendAfter: function (json) {
            $("#chargePerson").html(json.bean.chargePerson[0].name);
            skyeyeEnclosure.showDetails({"enclosureUpload": json.bean.enclosureInfo});
            matchingLanguage();
            form.render();
        }
    });

    $("body").on("click", "#cancle", function(){
        parent.layer.close(index);
    });
});