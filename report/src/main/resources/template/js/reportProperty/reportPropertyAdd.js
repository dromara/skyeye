
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

        skyeyeReportUtil.getReportEditorType("editorType", selOption, function (){
            form.render('select');
        });

        // 属性值是否可选的变化变化
        form.on('radio(optional)', function(data) {
            var val = data.value;
            if(val == 1){
                $("#canChoose").show();
                $("#canNotChoose").hide();
            } else {
                $("#canChoose").hide();
                $("#canNotChoose").show();
            }
        });

        matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                // 是否可选
                var optional = $("input[name='optional']:checked").val();
                var tableData = new Array();
                if(optional == 1){
                    var rowTr = $("#useTable tr");
                    $.each(rowTr, function(i, item) {
                        var rowNum = $(item).attr("trcusid").replace("tr", "");
                        var row = {
                            title: $("#title" + rowNum).val(),
                            value: $("#value" + rowNum).val(),
                            defaultChoose: encodeURIComponent($("input[name='defaultChoose" + rowNum + "']:checked").val())
                        };
                        tableData.push(row);
                    });
                    if(tableData.length == 0){
                        winui.window.msg('请最少填写一条属性值', {icon: 2, time: 2000});
                        return false;
                    }
                } else {
                    if(isNull($("#defaultValue").val())){
                        winui.window.msg('请填写默认值', {icon: 2, time: 2000});
                        return false;
                    }
                }

                var params = {
                    title: $("#title").val(),
                    attrCode: $("#attrCode").val(),
                    editorType: $("#editorType").val(),
                    optional: optional,
                    defaultValue: encodeURIComponent($("#defaultValue").val()),
                    options: JSON.stringify(tableData),
                };
                AjaxPostUtil.request({url:reportBasePath + "reportproperty002", params: params, type:'json', method: "POST", callback: function(json) {
                    if (json.returnCode == 0) {
                        parent.layer.close(index);
                        parent.refreshCode = '0';
                    } else {
                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                    }
                }});
            }
            return false;
        });

        // 新增行
        $("body").on("click", "#addRow", function() {
            addRow();
        });

        // 删除行
        $("body").on("click", "#deleteRow", function() {
            deleteRow();
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

        // 删除行
        function deleteRow() {
            var checkRow = $("#useTable input[type='checkbox'][name='tableCheckRow']:checked");
            if(checkRow.length > 0) {
                $.each(checkRow, function(i, item) {
                    // 移除界面上的信息
                    $(item).parent().parent().remove();
                });
            } else {
                winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
            }
        }

        $("body").on("click", "#cancle", function(){
            parent.layer.close(index);
        });
    });
});