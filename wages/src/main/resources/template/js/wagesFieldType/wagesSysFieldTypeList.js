
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        table = layui.table;

    // 系统提供的薪资字段列表
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.wagesBasePath + 'wages009',
        where: {},
        even: true,
        page: false,
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'nameCn', title: '中文名称', align: 'left', width: 150 },
            { field: 'nameEn', title: '英文名称', align: 'left', width: 200},
            { field: 'key', title: '字段key', align: 'left', width: 300 },
            { field: 'desc', title: '描述', align: 'left', width: 300}
        ]],
        done: function(json) {
            matchingLanguage();
        }
    });

    form.render();

    exports('wagesSysFieldTypeList', {});
});
