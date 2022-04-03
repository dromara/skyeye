
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

    dsFormUtil.loadDsFormPageTypeByPId("firstTypeId", "0");
    form.on('select(firstTypeId)', function(data) {
        var thisRowValue = data.value;
        dsFormUtil.loadDsFormPageTypeByPId("secondTypeId", isNull(thisRowValue) ? "-" : thisRowValue);
        form.render('select');
    });

    matchingLanguage();
    form.render();
    form.on('submit(formAddBean)', function (data) {
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
                encoded: $("#encoded").val(),
                firstTypeId: $("#firstTypeId").val(),
                secondTypeId: $("#secondTypeId").val(),
                orderNum: $("#orderNum").val(),
                remark: $("#remark").val(),
                dsFormPageIds: formPageId.join(",")
            };
            AjaxPostUtil.request({url: flowableBasePath + "dsFormObjectRelation002", params: params, type: 'json', method: "POST", callback: function(json){
                if(json.returnCode == 0){
                    parent.layer.close(index);
                    parent.refreshCode = '0';
                }else{
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
        $("body").on("click", "#" + btnId, function(e){
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

    $("body").on("click", "#cancle", function(){
        parent.layer.close(index);
    });
});