
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form;

    // 获取当前登陆人的考勤班次
    checkWorkUtil.getCurrentUserCheckWorkTimeList(function (json) {
        $.each(json.rows, function (i, item) {
            item.typeName = skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("checkWorkTimeType", 'id', item.type, 'name');
        });

        $("#showBox").html(getDataUseHandlebars($("#showTemplate").html(), json));
        $.each(json.rows, function (i, item) {
            var type = item.type;
            if(type == 1){
                checkWorkUtil.resetSingleBreak(item.id);
            } else if (type == 2){
                checkWorkUtil.resetWeekend(item.id);
            } else if (type == 3){
                checkWorkUtil.resetSingleAndDoubleBreak(item.id);
            } else if (type == 4){
                checkWorkUtil.resetCustomizeDay(item.checkWorkTimeWeekList, item.id);
            }
        });
    });
    form.render();

    exports('loginUserCheckWorkTime', {});
});
