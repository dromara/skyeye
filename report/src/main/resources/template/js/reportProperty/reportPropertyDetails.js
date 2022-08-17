
// 表格的序号
var rowNum = 1;

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
        var usetableTemplate = $("#usetableTemplate").html();
        var selOption = getFileContent('tpl/template/select-option.tpl');

        showGrid({
            id: "showForm",
            url: reportBasePath + "reportproperty006",
            params: {id: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#showBaseTemplate").html(),
            ajaxSendLoadBefore: function(hdb){
            },
            ajaxSendAfter:function(j){
                skyeyeReportUtil.getReportEditorType('', selOption, function (data) {
                    var name = getInPoingArr(data, 'id', j.bean.editorType, 'name');
                    $("#editorType").html(name);
                });

                if(j.bean.optional == 1){
                    $("#optional").html('可选');
                    $("#canChoose").show();
                    var options = JSON.parse(j.bean.options);
                    // 加载属性值
                    $.each(options, function(i, item){
                        addRow();
                        $("#title" + (rowNum - 1)).html(item.title);
                        $("#value" + (rowNum - 1)).html(item.value);
                        if(item.defaultChoose == 1){
                            $("#defaultChoose" + (rowNum - 1)).html('是');
                        } else {
                            $("#defaultChoose" + (rowNum - 1)).html('否');
                        }
                    });
                } else {
                    $("#optional").html('不可选');
                    $("#canNotChoose").show();
                }

                matchingLanguage();
                form.render();
            }
        });

        // 新增行
        function addRow() {
            var par = {
                id: "row" + rowNum.toString(), // checkbox的id
                trId: "tr" + rowNum.toString(), // 行的id
                title: "title" + rowNum.toString(), // 标题id
                value: "value" + rowNum.toString(), // 属性值id
                defaultChoose: "defaultChoose" + rowNum.toString() // 是否默认id
            };
            $("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
            form.render();
            rowNum++;
        }

    });
});