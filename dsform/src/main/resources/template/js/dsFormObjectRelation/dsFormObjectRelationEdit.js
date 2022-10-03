
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
    var rowNum = 1; // 表格的序号
    var usetableTemplate = $("#usetableTemplate").html();

    showGrid({
        id: "showForm",
        url: flowableBasePath + "dsFormObjectRelation004",
        params: {id: parent.rowId},
        pagination: false,
        method: 'GET',
        template: $("#beanTemplate").html(),
        ajaxSendLoadBefore: function (hdb) {
        },
        ajaxSendAfter: function (json) {
            $.each(json.bean.dsFormPageMations, function (i, item) {
                addRow();
                $("#dsFormChooseList" + (rowNum - 1)).val(item.pageNum);
                $("#dsFormChooseList" + (rowNum - 1)).attr("dataId", item.id);
                $("#pageName" + (rowNum - 1)).html(item.pageName);
            })

            dsFormUtil.loadDsFormPageTypeByPId("firstTypeId", "0");
            $("#firstTypeId").val(json.bean.firstTypeId);

            dsFormUtil.loadDsFormPageTypeByPId("secondTypeId", json.bean.firstTypeId);
            $("#secondTypeId").val(json.bean.secondTypeId);

            form.on('select(firstTypeId)', function(data) {
                var thisRowValue = data.value;
                dsFormUtil.loadDsFormPageTypeByPId("secondTypeId", isNull(thisRowValue) ? "-" : thisRowValue);
                form.render('select');
            });

            matchingLanguage();
            form.render();
            form.on('submit(formEditBean)', function (data) {
                if (winui.verifyForm(data.elem)) {
                    var rowTr = $("#useTable tr");
                    var formPageId = new Array();
                    $.each(rowTr, function(i, item) {
                        // 获取行编号
                        var rowNum = $(item).attr("trcusid").replace("tr", "");
                        formPageId.push($("#dsFormChooseList" + rowNum).attr("dataId"));
                    });

                    var params = {
                        title: $("#title").val(),
                        titleEn: $("#titleEn").val(),
                        codeNum: $("#codeNum").val(),
                        firstTypeId: $("#firstTypeId").val(),
                        secondTypeId: $("#secondTypeId").val(),
                        remark: $("#remark").val(),
                        dsFormPageIds: formPageId.join(","),
                        id: parent.rowId
                    };
                    AjaxPostUtil.request({url: flowableBasePath + "dsFormObjectRelation005", params: params, type: 'json', method: "PUT", callback: function (json) {
                        parent.layer.close(index);
                        parent.refreshCode = '0';
                    }});
                }
                return false;
            });
        }
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
            id: "row" + rowNum.toString(), //checkbox的id
            trId: "tr" + rowNum.toString(), //行的id
            dsFormChooseList: "dsFormChooseList"  + rowNum.toString(), // 动态表单选择id
            pageName: "pageName" + rowNum.toString() // 动态表单页面名称id
        };
        $("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
        form.render('checkbox');
        rowNum++;

        // 加载动添表单选择按钮的点击事件
        var btnId = par.dsFormChooseList + "Btn";
        $("body").on("click", "#" + btnId, function (e) {
            dsFormUtil.chooseType = false; // 单选
            dsFormUtil.openDsFormPageChoosePage(function (){
                $("#" + par.dsFormChooseList).val(dsFormUtil.dsFormChooseMation.pageNum);
                $("#" + par.dsFormChooseList).attr("dataId", dsFormUtil.dsFormChooseMation.id);
                $("#" + par.pageName).html(dsFormUtil.dsFormChooseMation.pageName);
            });
        });
    }

    // 删除行
    function deleteRow() {
        var checkRow = $("#useTable input[type='checkbox'][name='tableCheckRow']:checked");
        if(checkRow.length > 0) {
            $.each(checkRow, function(i, item) {
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