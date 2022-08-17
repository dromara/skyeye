
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
            url: reportBasePath + "reportproperty005",
            params: {id: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#showBaseTemplate").html(),
            ajaxSendLoadBefore: function(hdb){
            },
            ajaxSendAfter:function(j){
                skyeyeReportUtil.getReportEditorType("editorType", selOption, function (){
                    $("#editorType").val(j.bean.editorType);
                    form.render('select');
                });

                if(j.bean.optional == 1){
                    var options = JSON.parse(j.bean.options);
                    // 加载属性值
                    $.each(options, function(i, item){
                        addRow();
                        $("#title" + (rowNum - 1)).val(item.title);
                        $("#value" + (rowNum - 1)).val(item.value);
                        $("input:radio[name='defaultChoose" + (rowNum - 1) + "'][value=" + item.defaultChoose + "]").attr("checked", true);
                    });
                }

                $("input:radio[name='optional'][value=" + j.bean.optional + "]").attr("checked", true);
                setAttrValueHide(j.bean.optional);
                // 属性值是否可选的变化变化
                form.on('radio(optional)', function(data) {
                    var val = data.value;
                    setAttrValueHide(val);
                });

                matchingLanguage();
                form.render();
                form.on('submit(formEditBean)', function (data) {
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
                            if(isNull($("#defaultValue").val())) {
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
                            id: parent.rowId
                        };
                        AjaxPostUtil.request({url: reportBasePath + "reportproperty004", params: params, type: 'json', method: "PUT", callback: function(json) {
                            parent.layer.close(index);
                            parent.refreshCode = '0';
                        }});
                    }
                    return false;
                });
            }
        });

        function setAttrValueHide(val){
            if(val == 1){
                $("#canChoose").show();
                $("#canNotChoose").hide();
            } else {
                $("#canChoose").hide();
                $("#canNotChoose").show();
            }
        }

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

        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});