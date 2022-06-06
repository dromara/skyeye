
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

        showGrid({
            id: "showForm",
            url: reportBasePath + "reportwordmodel005",
            params: {id: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#showBaseTemplate").html(),
            ajaxSendLoadBefore: function (hdb, json) {
            },
            ajaxSendAfter: function (j) {

                var options = j.bean.options;
                // 加载属性值
                $.each(options, function(i, item){
                    addRow();
                    $("#propertyId" + (rowNum - 1)).html(item.code);
                    if(item.editor == 1){
                        $("#whetherEditor" + (rowNum - 1)).html('是');
                    }else{
                        $("#whetherEditor" + (rowNum - 1)).html('否');
                    }
                    if(item.showToEditor == 1){
                        $("#whetherShow" + (rowNum - 1)).html('是');
                    }else{
                        $("#whetherShow" + (rowNum - 1)).html('否');
                    }
                });

                matchingLanguage();
                form.render();
            }
        });

        // 新增行
        function addRow() {
            var par = {
                id: "row" + rowNum.toString(), // checkbox的id
                trId: "tr" + rowNum.toString(), // 行的id
                propertyId: "propertyId" + rowNum.toString(),
                whetherEditor: "whetherEditor" + rowNum.toString(),
                whetherShow: "whetherShow" + rowNum.toString()
            };
            $("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
            form.render();
            rowNum++;
        }
    });
});