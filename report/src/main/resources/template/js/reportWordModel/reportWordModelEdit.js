
// 表格的序号
var rowNum = 1;

// 已经选择的所有的属性
var choosePropertyList = {};

// 当前选中的属性
var reportProperty;

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
        var isPic = false; // 是否生成预览图

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
                    $("#propertyId" + (rowNum - 1)).val(item.code);
                    $("input:radio[name='whetherEditor" + (rowNum - 1) + "'][value=" + item.editor + "]").attr("checked", true);
                    $("input:radio[name='whetherShow" + (rowNum - 1) + "'][value=" + item.showToEditor + "]").attr("checked", true);
                    var trId = "tr" + (rowNum - 1);
                    choosePropertyList[trId] = item;
                });

                matchingLanguage();
                form.render();
                form.on('submit(formEditBean)', function (data) {
                    if (winui.verifyForm(data.elem)) {
                        if (!isPic) {
                            winui.window.msg("请先生成预览图", {icon: 2, time: 2000});
                            return false;
                        }
                        var oCanvas = document.getElementById("thecanvas");
                        var imgData = oCanvas.toDataURL();
                        AjaxPostUtil.request({url: reqBasePath + "common004", params: {images: imgData, type: 19}, type: 'json', callback: function (json1) {
                            if (json1.returnCode == 0) {
                                var rowTr = $("#useTable tr");
                                var tableData = new Array();
                                $.each(rowTr, function (i, item) {
                                    var rowNum = $(item).attr("trcusid").replace("tr", "");
                                    var trId = $(item).attr("trcusid");
                                    var row = {
                                        propertyId: choosePropertyList[trId].id,
                                        editor: $("input[name='whetherEditor" + rowNum + "']:checked").val(),
                                        showToEditor: $("input[name='whetherShow" + rowNum + "']:checked").val()
                                    };
                                    tableData.push(row);
                                });
                                if (tableData.length == 0) {
                                    winui.window.msg('请最少选择一条属性值', {icon: 2, time: 2000});
                                    return false;
                                }

                                var params = {
                                    title: $("#title").val(),
                                    defaultWidth: $("#defaultWidth").val(),
                                    defaultHeight: $("#defaultHeight").val(),
                                    options: JSON.stringify(tableData),
                                    logo: json1.bean.picUrl,
                                    id: parent.rowId
                                };
                                AjaxPostUtil.request({url: reportBasePath + "reportwordmodel004", params: params, type: 'json', method: "PUT", callback: function (json) {
                                    if (json.returnCode == 0) {
                                        parent.layer.close(index);
                                        parent.refreshCode = '0';
                                    } else {
                                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                                    }
                                }});
                            } else {
                                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                            }
                        }});
                    }
                    return false;
                });
            }
        });

        // 生成图片
        $("body").on("click", "#createPic", function(){
            var styleStr = "";
            $.each(choosePropertyList, function (key, value){
                if(!isNull(value)){
                    styleStr += value.code + ":" + value.defaultValue + ";";
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
        });
        $("#createPic").click();

        // 下载canvas图片
        $("body").on("click", "#download", function(){
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
                whetherEditor: "whetherEditor" + rowNum.toString(),
                whetherShow: "whetherShow" + rowNum.toString()
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
        $("body").on("click", ".choosePropertyBtn", function(e){
            var trId = $(this).parent().parent().attr("trcusid");
            _openNewWindows({
                url: "../../tpl/reportProperty/reportPropertyChoose.html",
                title: "选择属性",
                pageId: "reportPropertyChoose",
                area: ['90vw', '90vh'],
                callBack: function(refreshCode){
                    if (refreshCode == '0') {
                        // 获取表格行号
                        var thisRowNum = trId.replace("tr", "");
                        // 商品赋值
                        choosePropertyList[trId] = reportProperty;
                        // 表格属性名称赋值
                        $("#propertyId" + thisRowNum.toString()).val(choosePropertyList[trId].title);
                    } else if (refreshCode == '-9999') {
                        winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                    }
                }});
        });

        $("body").on("click", "#cancle", function(){
            parent.layer.close(index);
        });
    });
});