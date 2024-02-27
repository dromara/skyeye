
// 表格的序号
var rowNum = 1;

// 已经选择的所有的属性
var choosePropertyList = {};

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
    var isPic = false; // 是否生成预览图
    let id = GetUrlParam("id");

    if (isNull(id)) {
        sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["reportModeType"]["key"], 'radioTree', "typeBox", '', form);
        skyeyeClassEnumUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", '', form);
    } else {
        AjaxPostUtil.request({url: reportBasePath + "queryWordModelById", params: {id: id}, type: 'json', method: 'GET', callback:function(data) {
            $("#name").val(data.bean.name);
            $("#defaultWidth").val(data.bean.defaultWidth);
            $("#defaultHeight").val(data.bean.defaultHeight);
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
            if (!isPic) {
                winui.window.msg("请先生成预览图", {icon: 2, time: 2000});
                return false;
            }
            var oCanvas = document.getElementById("thecanvas");
            var imgData = oCanvas.toDataURL();
            AjaxPostUtil.request({url: reqBasePath + "common004", params: {images: imgData, type: 19}, type: 'json', callback:function(json1){
                var rowTr = $("#useTable tr");
                var tableData = new Array();
                $.each(rowTr, function(i, item) {
                    var rowNum = $(item).attr("trcusid").replace("tr", "");
                    var trId = $(item).attr("trcusid");
                    tableData.push({
                        propertyId: choosePropertyList[trId].id,
                        editor: dataShowType.getData('editor' + rowNum),
                        showToEditor: dataShowType.getData('whetherShow' + rowNum)
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
                    imgPath: json1.bean.picUrl,
                    enabled: dataShowType.getData('enabled'),
                    id: isNull(id) ? '' : id
                };
                AjaxPostUtil.request({url: reportBasePath + "writeWordModel", params: params, type: 'json', method: "POST", callback: function(json) {
                    parent.layer.close(index);
                    parent.refreshCode = '0';
                }});
            }});
        }
        return false;
    });

    // 生成图片
    $("body").on("click", "#createPic", function() {
        loadPic();
    });

    function loadPic() {
        var styleStr = "";
        $.each(choosePropertyList, function (key, value){
            if (!isNull(value)) {
                styleStr += value.attrCode + ":" + value.defaultValue + ";";
            }
        });
        var showPrit = '<font style="' + styleStr + '">Hello, Skyeye</font>';
        $("#printPic").html(showPrit);
        html2canvas($("#printPic"), {
            onrendered: function(canvas) {
                // 添加属性
                canvas.setAttribute('id','thecanvas');
                // 读取属性值
                document.getElementById('images').innerHTML = '';
                document.getElementById('images').appendChild(canvas);
                $("#download").show();
                isPic = true;
            }
        });
    }

    // 下载canvas图片
    $("body").on("click", "#download", function() {
        var oCanvas = document.getElementById("thecanvas");
        var img_data1 = Canvas2Image.saveAsPNG(oCanvas, true).getAttribute('src');
        saveFile(img_data1, 'richer.png');
    });

    // 保存文件函数
    var saveFile = function(data, filename){
        var save_link = document.createElementNS('http://www.w3.org/1999/xhtml', 'a');
        save_link.href = data;
        save_link.download = filename;
        var event = document.createEvent('MouseEvents');
        event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
        save_link.dispatchEvent(event);
    };

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
            }});
    });

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});