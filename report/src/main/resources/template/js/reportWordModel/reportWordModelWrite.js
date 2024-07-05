
// 表格的序号
var rowNum = 1;

// 已经选择的所有的属性
var choosePropertyList = {};

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'fileUpload'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form;
    var usetableTemplate = $("#usetableTemplate").html();
    let id = GetUrlParam("id");

    if (isNull(id)) {
        sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["reportModeType"]["key"], 'radioTree', "typeBox", '', form);
        skyeyeClassEnumUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", '', form);
        // 初始化上传
        $("#logo").upload(systemCommonUtil.uploadCommon003Config('logo', 19, '', 1));
    } else {
        AjaxPostUtil.request({url: reportBasePath + "queryWordModelById", params: {id: id}, type: 'json', method: 'GET', callback:function(data) {
            $("#name").val(data.bean.name);
            $("#defaultWidth").val(data.bean.defaultWidth);
            $("#defaultHeight").val(data.bean.defaultHeight);
            // 初始化上传
            $("#logo").upload(systemCommonUtil.uploadCommon003Config('logo', 20, data.bean.imgPath, 1));
            sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["reportModeType"]["key"], 'radioTree', "typeBox", data.bean.typeId, form);
            skyeyeClassEnumUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", data.bean.enabled, form);
            // 加载属性值
            $.each(data.bean.wordModelAttrList, function(i, item) {
                addRow();
                if (!isNull(item.propertyMation)) {
                    $("#propertyId" + (rowNum - 1)).val(item.propertyMation.name);
                    var trId = "tr" + (rowNum - 1);
                    choosePropertyList[trId] = item.propertyMation;
                }
                $("input:radio[name='editor" + (rowNum - 1) + "Name'][value=" + item.editor + "]").attr("checked", true);
                $("input:radio[name='whetherShow" + (rowNum - 1) + "Name'][value=" + item.showToEditor + "]").attr("checked", true);
            });
            loadPic();
            form.render();
        }});
    }

    matchingLanguage();
    form.render();
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            var rowTr = $("#useTable tr");
            var tableData = new Array();
            $.each(rowTr, function(i, item) {
                var rowNum = $(item).attr("trcusid").replace("tr", "");
                var trId = $(item).attr("trcusid");
                tableData.push({
                    propertyId: choosePropertyList[trId].id,
                    editor: dataShowType.getData('editor' + rowNum),
                    showToEditor: dataShowType.getData('whetherShow' + rowNum),
                    orderBy: i + 1
                });
            });
            if (tableData.length == 0) {
                winui.window.msg('请最少选择一条属性值', {icon: 2, time: 2000});
                return false;
            }

            var params = {
                name: $("#name").val(),
                defaultWidth: $("#defaultWidth").val(),
                defaultHeight: $("#defaultHeight").val(),
                wordModelAttrList: JSON.stringify(tableData),
                typeId: dataShowType.getData('typeBox'),
                imgPath: $("#logo").find("input[type='hidden'][name='upload']").attr("oldurl"),
                enabled: dataShowType.getData('enabled'),
                id: isNull(id) ? '' : id
            };
            AjaxPostUtil.request({url: reportBasePath + "writeWordModel", params: params, type: 'json', method: "POST", callback: function(json) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            }});
        }
        return false;
    });

    function loadPic() {
        var styleStr = "";
        $.each(choosePropertyList, function (key, value){
            if (!isNull(value)) {
                styleStr += value.attrCode + ":" + value.defaultValue + ";";
            }
        });
        var wordBox = document.createElement("font");
        wordBox.innerHTML = "Hello, Skyeye";
        wordBox.style = styleStr;
        $("#printPic")[0].appendChild(wordBox);
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
            propertyId: "propertyId" + rowNum.toString(),
            editor: "editor" + rowNum.toString(),
            whetherShow: "whetherShow" + rowNum.toString()
        };
        $("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
        skyeyeClassEnumUtil.showEnumDataListByClassName("whetherEnum", 'radio', par.editor, '', form);
        skyeyeClassEnumUtil.showEnumDataListByClassName("whetherEnum", 'radio', par.whetherShow, '', form);
        form.render();
        rowNum++;
    }

    // 删除行
    function deleteRow() {
        var checkRow = $("#useTable input[type='checkbox'][name='tableCheckRow']:checked");
        if(checkRow.length > 0) {
            $.each(checkRow, function(i, item) {
                var trId = $(item).parent().parent().attr("trcusid");
                choosePropertyList[trId] = undefined;
                // 移除界面上的信息
                $(item).parent().parent().remove();
            });
            loadPic();
        } else {
            winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
        }
    }

    // 属性选择
    $("body").on("click", ".choosePropertyBtn", function (e) {
        var trId = $(this).parent().parent().attr("trcusid");
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024022700002', null),
            title: "选择属性",
            pageId: "reportPropertyChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                var thisRowNum = trId.replace("tr", "");
                choosePropertyList[trId] = chooseItemMation;
                $("#propertyId" + thisRowNum.toString()).val(choosePropertyList[trId].name);
                loadPic();
            }});
    });

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});