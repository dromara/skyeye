
// 表格的序号
var rowNum = 1;

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'codemirror', 'xml', 'sql', 'form'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form;
    var selOption = getFileContent('tpl/template/select-option.tpl');
    var xmlContent, jsonContent, sqlContent, restRequestBodyContent;
    var id = GetUrlParam("id");

    if (isNull(id)) {
        skyeyeClassEnumUtil.showEnumDataListByClassName("reportDataFromType", 'radio', "dataFromTypeBox", '', form);
        initDataFromBoxContent(dataShowType.getData('dataFromTypeBox'), null)
    } else {
        AjaxPostUtil.request({url: sysMainMation.reportBasePath + "queryReportDataFromById", params: {id: id}, type: 'json', method: 'GET', callback:function(data) {
            $("#name").val(data.bean.name);
            $("#remark").val(data.bean.remark);
            skyeyeClassEnumUtil.showEnumDataListByClassName("reportDataFromType", 'radio', "dataFromTypeBox", data.bean.type, form);

            initDataFromBoxContent(data.bean.type, data.bean)

            var list = []
            if (data.bean.type == 1) {
                // XML数据源
                list = data.bean.xmlEntity.analysisList
            } else if (data.bean.type == 2) {
                // JSON数据源
                list = data.bean.jsonEntity.analysisList
            } else if (data.bean.type == 3) {
                // Rest接口数据源
                list = data.bean.restEntity.analysisList
            } else if (data.bean.type == 4) {
                // SQL数据源
                list = data.bean.sqlEntity.analysisList
            }
            $.each(list, function (i, item) {
                addAnalysisRow();
                $("#key" + (rowNum - 1)).val(item.key);
                $("#name" + (rowNum - 1)).val(item.name);
                $("#remark" + (rowNum - 1)).val(item.remark);
                if (data.bean.type == 4) {
                    $("#dataType" + (rowNum - 1)).val(item.dataType);
                    $("#dataLength" + (rowNum - 1)).val(item.dataLength);
                    $("#dataPrecision" + (rowNum - 1)).val(item.dataPrecision);
                }
            });

            form.render();
        }});
    }

    matchingLanguage();
    form.render();
    form.on('submit(formWriteBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            var params = {
                name: $("#name").val(),
                remark: $("#remark").val(),
                type: dataShowType.getData('dataFromTypeBox'),
                id: isNull(id) ? '' : id
            };
            if (getAnalysisData().length == 0) {
                winui.window.msg('请进行字段解析操作。', {icon: 2, time: 2000});
                return false;
            }
            var otherData = getDataByType();

            params = $.extend(true, params, otherData);
            AjaxPostUtil.request({url: sysMainMation.reportBasePath + "saveReportDataFrom", params: params, type: 'json', method: "POST", callback: function(json) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            }});
        }
        return false;
    });

    function getDataByType() {
        var dataFromType = dataShowType.getData('dataFromTypeBox');
        if (dataFromType == 1) {
            // XML数据源
            return getXMLData();
        } else if (dataFromType == 2) {
            // JSON数据源
            return getJSONData();
        } else if (dataFromType == 3) {
            // Rest接口数据源
            return getRESTData();
        } else if (dataFromType == 4) {
            // SQL数据源
            return getSQLData();
        }
        return {};
    }

    function getXMLData() {
        return {
            xmlEntity: JSON.stringify({
                xmlContent: xmlContent.getValue(),
                analysisList: getAnalysisData()
            })
        };
    }

    function getJSONData() {
        return {
            jsonEntity: JSON.stringify({
                jsonContent: jsonContent.getValue(),
                analysisList: getAnalysisData()
            })
        };
    }

    function getRESTData() {
        return {
            restEntity: JSON.stringify({
                restUrl: $("#restUrl").val(),
                method: $("#restMethod").val(),
                header: getRestRequestHeaderData(),
                requestBody: restRequestBodyContent.getValue(),
                analysisList: getAnalysisData()
            })
        };
    }

    function getSQLData() {
        return {
            sqlEntity: JSON.stringify({
                dataBaseId: $("#dataBaseId").val(),
                sqlContent: sqlContent.getValue(),
                analysisList: getAnalysisData()
            })
        };
    }

    function getAnalysisData(){
        var tableData = new Array();
        var rowTr = $("#analysisTable tr");
        $.each(rowTr, function(i, item) {
            //获取行编号
            var rowNum = $(item).attr("trcusid").replace("tr", "");
            var row = {
                key: $("#key" + rowNum).val(),
                name: $("#name" + rowNum).val(),
                dataType: $("#dataType" + rowNum).val(),
                dataLength: $("#dataLength" + rowNum).val(),
                dataPrecision: $("#dataPrecision" + rowNum).val(),
                remark: $("#remark" + rowNum).val()
            };
            tableData.push(row);
        });
        return tableData;
    }

    function getRestRequestHeaderData(){
        var tableData = new Array();
        var rowTr = $("#restHeaderTable tr");
        $.each(rowTr, function(i, item) {
            //获取行编号
            var rowNum = $(item).attr("trcusid").replace("tr", "");
            var row = {
                headerKey: $("#headerKey" + rowNum).val(),
                headerValue: $("#headerValue" + rowNum).val(),
                headerDescription: $("#headerDescription" + rowNum).val()
            };
            tableData.push(row);
        });
        return JSON.stringify(tableData);
    }

    form.on('radio(dataFromTypeBoxFilter)', function (data) {
        var val = data.value;
        initDataFromBoxContent(val, null);
    });

    function initDataFromBoxContent(val, bean){
        $("#dataBox").html(getAnalysisHtmlByType(val));
        // 加载字段解析信息
        $("#analysisHeader").html($("#analysisHeaderTemplate").html());
        $("#analysisTable").html("");
        initEvent(val, bean);
        form.render();
    }

    function getAnalysisHtmlByType(dataFromType) {
        var tplPath = '';
        if (dataFromType == 1) {
            // XML数据源
            tplPath = "../../tpl/reportDataFrom/dataFromTpl/xmlTemplate.tpl"
        } else if (dataFromType == 2) {
            // JSON数据源
            tplPath = "../../tpl/reportDataFrom/dataFromTpl/jsonTemplate.tpl"
        } else if (dataFromType == 3) {
            // Rest接口数据源
            tplPath = "../../tpl/reportDataFrom/dataFromTpl/restTemplate.tpl"
        } else if (dataFromType == 4) {
            // SQL数据源
            tplPath = "../../tpl/reportDataFrom/dataFromTpl/sqlTemplate.tpl"
        }
        return getFileContent(tplPath);
    }

    function initEvent(dataFromType, bean) {
        // 获取公共的配置信息
        var commonOptions = getCommonCodeMirrorOptions();
        if (dataFromType == 1) {
            // XML数据源
            xmlContent = CodeMirror.fromTextArea(document.getElementById("xmlData"), $.extend(true, commonOptions, {
                mode: "xml",
                theme: "default"
            }));
            if (!isNull(bean)) {
                xmlContent.setValue(bean.xmlEntity.xmlContent);
            }
        } else if (dataFromType == 2) {
            // JSON数据源
            jsonContent = CodeMirror.fromTextArea(document.getElementById("jsonData"), $.extend(true, commonOptions, {
                mode: "xml",
                theme: "default"
            }));
            if (!isNull(bean)) {
                jsonContent.setValue(bean.jsonEntity.jsonContent);
            }
        } else if (dataFromType == 3) {
            // Rest接口数据源
            restRequestBodyContent = CodeMirror.fromTextArea(document.getElementById("requestBody"), $.extend(true, commonOptions, {
                mode: "xml",
                theme: "default"
            }));
            if (!isNull(bean)) {
                $("#restUrl").val(bean.restEntity.restUrl);
                $("#restMethod").val(bean.restEntity.method);
                if (!isNull(bean.restEntity.header)) {
                    $.each(bean.restEntity.header, function (i, item) {
                        addRow();
                        $("#headerKey" + (rowNum - 1)).val(item.headerKey);
                        $("#headerValue" + (rowNum - 1)).val(item.headerValue);
                        $("#headerDescription" + (rowNum - 1)).val(item.headerDescription);
                    });
                }
                restRequestBodyContent.setValue(bean.restEntity.requestBody);
            }
        } else if (dataFromType == 4) {
            // SQL数据源
            showGrid({
                id: "dataBaseId",
                url: sysMainMation.reportBasePath + "queryAllDataBaseList",
                params: {},
                pagination: false,
                template: selOption,
                method: "GET",
                ajaxSendLoadBefore: function(hdb) {},
                ajaxSendAfter:function (json) {
                    if (!isNull(bean)) {
                        $("#dataBaseId").val(bean.sqlEntity.dataBaseId);
                    }
                }
            });
            sqlContent = CodeMirror.fromTextArea(document.getElementById("sqlData"), $.extend(true, commonOptions, {
                mode: "text/x-sql",
                theme: "default"
            }));
            if (!isNull(bean)) {
                sqlContent.setValue(bean.sqlEntity.sqlContent);
            }
        }
    }

    function getCommonCodeMirrorOptions() {
        return {
            mode: "xml",  // 模式
            theme: "eclipse",  // CSS样式选择
            indentUnit: 4,  // 缩进单位，默认2
            smartIndent: true,  // 是否智能缩进
            tabSize: 4,  // Tab缩进，默认4
            readOnly: false,  // 是否只读，默认false
            showCursorWhenSelecting: true,
            lineNumbers: true,  // 是否显示行号
            styleActiveLine: true, //line选择是是否加亮
            matchBrackets: true,
            extraKeys:{
                "F7": function autoFormat(editor) {
                    // 代码格式化
                    var totalLines = editor.lineCount();
                    editor.autoFormatRange({line:0, ch:0}, {line:totalLines});
                }
            }
        };
    }

    /**
     * 字段解析
     */
    $("body").on("click", "#fieldResolution", function(e) {
        var dataFromType = dataShowType.getData('dataFromTypeBox');
        var url = "";
        var params = getResolutionInputParams(dataFromType, e);
        if (params == null) {
            return false;
        }
        if (dataFromType == 1) {
            // XML数据源
            url = "reportcommon002";
        } else if (dataFromType == 2) {
            // JSON数据源
            url = "reportcommon003";
        } else if (dataFromType == 3) {
            // Rest接口数据源
            url = "reportcommon005";
        } else if (dataFromType == 4) {
            // SQL数据源
            url = "reportcommon004";
        }
        AjaxPostUtil.request({url: sysMainMation.reportBasePath + url, params: params, type: 'json', method: "POST", callback: function(json) {
            var data = getDataByDataFromType(dataFromType, json);
            loadFieldResolution(dataFromType, data);
        }});
    });

    /**
     * 字段解析时根据不同的数据源类型获取入参
     *
     * @param dataFromType 数据源类型
     * @returns {boolean}
     */
    function getResolutionInputParams(dataFromType, e){
        var params = {};
        if (dataFromType == 1){
            // XML数据源
            params = {
                xmlText: xmlContent.getValue()
            };
        } else if (dataFromType == 2){
            // JSON数据源
            params = {
                jsonText: jsonContent.getValue()
            };
        } else if (dataFromType == 3){
            // Rest接口数据源
            if(isNull($("#restUrl").val())) {
                winui.window.msg('请填写url', {icon: 2, time: 2000});
                return null;
            }
            params = {
                requestUrl: $("#restUrl").val(),
                requestMethod: $("#restMethod").val(),
                requestHeader: getRestRequestHeaderDataToResolution(),
                requestBody: restRequestBodyContent.getValue()
            };
        } else if (dataFromType == 4){
            // SQL数据源
            if (isNull($("#dataBaseId").val())) {
                winui.window.msg('请选择数据库', {icon: 2, time: 2000});
                return null;
            }
            if (isNull(sqlContent.getValue())) {
                winui.window.msg('请填写sql语句', {icon: 2, time: 2000});
                return null;
            }
            params = {
                sqlText: sqlContent.getValue(),
                dataBaseId: $("#dataBaseId").val()
            };
        }
        return params;
    }

    function getRestRequestHeaderDataToResolution(){
        var tableData = new Array();
        var rowTr = $("#restHeaderTable tr");
        $.each(rowTr, function(i, item) {
            //获取行编号
            var rowNum = $(item).attr("trcusid").replace("tr", "");
            tableData[$("#headerKey" + rowNum).val()] = $("#headerValue" + rowNum).val();
        });
        return JSON.stringify(tableData);
    }

    function getDataByDataFromType(dataFromType, json){
        if (dataFromType == 1){
            // XML数据源
            return json.bean.nodeArray;
        } else if (dataFromType == 2){
            // JSON数据源
            return json.bean.nodeArray;
        } else if (dataFromType == 3){
            // Rest接口数据源
            return json.bean.nodeArray;
        } else if (dataFromType == 4){
            // SQL数据源
            return json.rows;
        }
    }

    /**
     * 加载解析的字段
     *
     * @param dataFromType 数据源类型
     * @param data 数据
     */
    function loadFieldResolution(dataFromType, data){
        $("#analysisTable").html("");
        $.each(data, function (i, item) {
            addAnalysisRow();
            $("#key" + (rowNum - 1)).val(item);
            if (dataFromType == 1){
                // XML数据源
            } else if (dataFromType == 2){
                // JSON数据源
            } else if (dataFromType == 3){
                // Rest接口数据源
            } else if (dataFromType == 4){
                // SQL数据源
                $("#key" + (rowNum - 1)).val(item.name);
                $("#dataType" + (rowNum - 1)).val(item.dataType);
                $("#dataLength" + (rowNum - 1)).val(item.width);
                $("#dataPrecision" + (rowNum - 1)).val(item.decimals);
            }
        });
    }

    /**********************************rest数据源header--start**************************************/
    //新增行
    $("body").on("click", "#addRow", function() {
        addRow();
    });

    //删除行
    $("body").on("click", "#deleteRow", function() {
        deleteRow();
    });

    //新增行
    function addRow() {
        var par = {
            id: "row" + rowNum.toString(), //checkbox的id
            trId: "tr" + rowNum.toString(), //行的id
            headerKey: "headerKey" + rowNum.toString(), // 配置项id
            headerValue: "headerValue" + rowNum.toString(), // 配置值id
            headerDescription: "headerDescription" + rowNum.toString() // 备注id
        };
        $("#restHeaderTable").append(getDataUseHandlebars($("#headerTemplate").html(), par));
        form.render();
        rowNum++;
    }

    //删除行
    function deleteRow() {
        var checkRow = $("#restHeaderTable input[type='checkbox'][name='tableCheckRow']:checked");
        if(checkRow.length > 0) {
            $.each(checkRow, function(i, item) {
                //移除界面上的信息
                $(item).parent().parent().remove();
            });
        } else {
            winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
        }
    }
    /**********************************rest数据源header--end**************************************/

    /**********************************字段解析--start**************************************/
    //新增行
    $("body").on("click", "#addAnalysisRow", function() {
        addAnalysisRow();
    });

    //删除行
    $("body").on("click", "#deleteAnalysisRow", function() {
        deleteAnalysisRow();
    });

    //新增行
    function addAnalysisRow() {
        var par = {
            id: "row" + rowNum.toString(), //checkbox的id
            trId: "tr" + rowNum.toString(), //行的id
            key: "key" + rowNum.toString(), // 字段id
            name: "name" + rowNum.toString(), // 含义id
            dataType: "dataType" + rowNum.toString(), // 字段类型id
            dataLength: "dataLength" + rowNum.toString(), // 字段长度id
            dataPrecision: "dataPrecision" + rowNum.toString(), // 字段精度id
            remark: "remark" + rowNum.toString() // 备注id
        };
        $("#analysisTable").append(getDataUseHandlebars($("#analysisTemplate").html(), par));
        form.render();
        rowNum++;
    }

    //删除行
    function deleteAnalysisRow() {
        var checkRow = $("#analysisTable input[type='checkbox'][name='tableCheckRow']:checked");
        if(checkRow.length > 0) {
            $.each(checkRow, function(i, item) {
                //移除界面上的信息
                $(item).parent().parent().remove();
            });
        } else {
            winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
        }
    }
    /**********************************字段解析--end**************************************/

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});