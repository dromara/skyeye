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
    var id = GetUrlParam("id");

    let tabList = [{
        name: "tab1",
        content: '内容1'
    }, {
        name: "tab2",
        content: '内容2'
    }]

    $("#titleUl").html(getDataUseHandlebars('{{#rows}}<li>{{name}}</li>{{/rows}}', {rows: tabList}));
    $("#contentDiv").html(getDataUseHandlebars('{{#rows}}<div class="layui-tab-item">{{content}}</div>{{/rows}}', {rows: tabList}));

});