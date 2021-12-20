
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form;

    showGrid({
        id: "showBox",
        url: reqBasePath + "checkworktime007",
        params: {},
        pagination: false,
        method: "POST",
        template: $("#showTemplate").html(),
        ajaxSendLoadBefore: function(hdb, json){
        },
        ajaxSendAfter:function(json){
            $.each(json.rows, function (i, item){
                var type = item.type;
                if(type == 1){
                    resetSingleBreak(item.timeId);
                }else if(type == 2){
                    resetWeekend(item.timeId);
                }else if(type == 3){
                    resetSingleAndDoubleBreak(item.timeId);
                }else if(type == 4){
                    resetCustomizeDay(item.days, item.timeId);
                }
            });
        }
    });

    form.render();

    exports('loginUserCheckWorkTime', {});
});
