
// 表格的序号
var rowNum = 1;

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
    var usetableTemplate = $("#usetableTemplate").html();
    var selOption = getFileContent('tpl/template/select-option.tpl');
    let id = GetUrlParam("id");

    if (isNull(id)) {
        skyeyeClassEnumUtil.showEnumDataListByClassName("whetherEnum", 'radio', 'optional', '', form);
        setAttrValueHide(dataShowType.getData('optional'));
        skyeyeReportUtil.getReportEditorType("editorType", selOption, function (){
            form.render('select');
        });
    } else {
        AjaxPostUtil.request({url: reportBasePath + "queryPropertyById", params: {id: id}, type: 'json', method: 'GET', callback:function(data) {
            $("#name").val(data.bean.name);
            $("#code").val(data.bean.attrCode);
            skyeyeClassEnumUtil.showEnumDataListByClassName("whetherEnum", 'radio', 'optional', data.bean.optional, form);
            skyeyeReportUtil.getReportEditorType("editorType", selOption, function (){
                $("#editorType").val(data.bean.editorType);
                form.render('select');
            });
            setAttrValueHide(data.bean.optional);
            if (data.bean.optional == 1) {
                // 加载属性值
                $.each(data.bean.propertyValueList, function(i, item) {
                    addRow();
                    $("#name" + (rowNum - 1)).val(item.name);
                    $("#value" + (rowNum - 1)).val(item.value);
                    $("input:radio[name='defaultChoose" + (rowNum - 1) + "Name'][value=" + item.defaultChoose + "]").attr("checked", true);
                });
            } else {
                $("#defaultValue").val(data.bean.defaultValue);
            }
            form.render();
        }});
    }

    // 属性值是否可选的变化变化
    form.on('radio(optionalFilter)', function(data) {
        var val = data.value;
        setAttrValueHide(val);
    });

    function setAttrValueHide(val){
        if (val == 1) {
            $("#canChoose").show();
            $("#canNotChoose").hide();
        } else {
            $("#canChoose").hide();
            $("#canNotChoose").show();
        }
    }

    matchingLanguage();
    form.render();
    form.on('submit(formWriteBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            // 是否可选
            var optional = dataShowType.getData('optional');
            var tableData = new Array();
            if (optional == 1) {
                var rowTr = $("#useTable tr");
                $.each(rowTr, function(i, item) {
                    var rowNum = $(item).attr("trcusid").replace("tr", "");
                    var row = {
                        name: $("#name" + rowNum).val(),
                        value: $("#value" + rowNum).val(),
                        defaultChoose: dataShowType.getData('defaultChoose' + rowNum)
                    };
                    tableData.push(row);
                });
                if (tableData.length == 0) {
                    winui.window.msg('请最少填写一条属性值', {icon: 2, time: 2000});
                    return false;
                }
            } else {
                if (isNull($("#defaultValue").val())) {
                    winui.window.msg('请填写默认值', {icon: 2, time: 2000});
                    return false;
                }
            }

            var params = {
                name: $("#name").val(),
                attrCode: $("#code").val(),
                editorType: $("#editorType").val(),
                optional: optional,
                defaultValue: encodeURIComponent($("#defaultValue").val()),
                propertyValueList: JSON.stringify(tableData),
                id: isNull(id) ? '' : id
            };
            AjaxPostUtil.request({url: reportBasePath + "writeProperty", params: params, type: 'json', method: "POST", callback: function(json) {
                parent.layer.close(index);
                parent.refreshCode = '0';
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
            name: "name" + rowNum.toString(), // 标题id
            value: "value" + rowNum.toString(), // 属性值id
            defaultChoose: "defaultChoose" + rowNum.toString() // 是否默认id
        };
        $("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
        skyeyeClassEnumUtil.showEnumDataListByClassName("whetherEnum", 'radio', par.defaultChoose, '', form);
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