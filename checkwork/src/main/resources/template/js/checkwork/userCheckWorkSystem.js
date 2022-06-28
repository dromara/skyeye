
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form;

    AjaxPostUtil.request({url: reqBasePath + "sysfdsettings001", params:{}, type: 'json', method: "GET", callback: function (json) {
        if (json.returnCode == 0) {
            json.bean.holidaysTypeJson = JSON.parse(json.bean.holidaysTypeJson);
            $("#showBox").append(getDataUseHandlebars($("#showTemplate").html(), json));
            $.each(json.bean.holidaysTypeJson, function (i, item) {
                $("#yearHour" + item.holidayNo).val(item.whetherYearHour);
                $("#comLeave" + item.holidayNo).val(item.whetherComLeave);
            });
            form.render();
        } else {
            winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
        }
    }});

    exports('userCheckWorkSystem', {});
});
