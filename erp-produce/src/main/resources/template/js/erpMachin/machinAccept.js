
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

    var id = GetUrlParam("id");
    var machinMation;

    loadData();
    function loadData() {
        var loadAcceptanceBtn = false;
        AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryMachinById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
            machinMation = json.bean;
            $.each(json.bean.machinChildList, function(i, item) {
                item.unitPrice = parseFloat(item.unitPrice).toFixed(2);
                item.quantityNum = item.acceptNum - item.belowNum;
                item.stateName = skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("machinChildStateEnum", 'id', item.state, 'name');
                if (item.state == 'waitForCheckig' && !loadAcceptanceBtn) {
                    loadAcceptanceBtn = true;
                    if (i == (json.bean.machinChildList.length - 1)) {
                        item.operator = '<button type="button" class="layui-btn layui-btn-xs layui-btn-normal acceptance" rowId="' + item.id + '" subType="2">工序验收</button>';
                    } else {
                        item.operator = '<button type="button" class="layui-btn layui-btn-xs layui-btn-normal acceptance" rowId="' + item.id + '" subType="1">工序验收</button>';
                    }
                }
            });
            $("#showForm").html(getDataUseHandlebars($("#useTemplate").html(), json));
        }});
}

    // 工序验收
    $("body").on("click", ".acceptance", function() {
        var id = $(this).attr('rowId');
        var subType = $(this).attr('subType');
        _openNewWindows({
            url: "../../tpl/erpMachin/erpMachinAcceptance.html?id=" + id + '&subType=' + subType + '&needNum=' + machinMation.needNum,
            title: '工序验收',
            pageId: "acceptance",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadData();
            }});
    });

});