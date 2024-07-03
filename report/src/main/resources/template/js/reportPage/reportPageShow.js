
var id;

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
    echarts: '../echarts/echarts',
    echartsTheme: '../echarts/echartsTheme'
}).define(['window', 'jquery', 'winui', 'form', 'echarts', 'colorpicker'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form;

    id = GetUrlParam("id");

    var content = $(document.body);
    var initData = {};

    // 获取初始化数据
    AjaxPostUtil.request({url: sysMainMation.reportBasePath + "queryReportPageById", params: {id: id}, type: 'json', method: "GET", callback: function(json) {
        initData = JSON.parse(getContentStr(json.bean.content));
        var widthScale = getScale(initData.contentWidth, content.width());
        var heightScale = getScale(initData.contentHeight, content.height());

        initEchartsData(widthScale, heightScale);
        initWordMationData(widthScale, heightScale);
        initTableMationData(widthScale, heightScale);

        if (!isNull(initData.bgImage)){
            content.css({
                "background-image": "url(" + initData.bgImage + ")",
                "background-size": content.width() + "px " + content.height() + "px"
            });
        }
    }});

    function initEchartsData(widthScale, heightScale) {
        var modelList = initData.modelList;
        if (!isNull(modelList)) {
            $.each(modelList, function (i, item) {
                var leftNum = multiplication(item.attrMation.attr["custom.move.x"].defaultValue, widthScale);
                var topNum = multiplication(item.attrMation.attr["custom.move.y"].defaultValue, heightScale);
                item.attrMation.attr["custom.move.x"].defaultValue = leftNum;
                item.attrMation.attr["custom.move.y"].defaultValue = topNum;
                item.attrMation.attr = getDataFromRest(item.attrMation.attr);
                // 加载模型
                var boxId = addNewModel(item.modelId, item.attrMation);
                $("#" + boxId).css({
                    left: leftNum + "px",
                    top: topNum + "px",
                    width: multiplication(item.width, widthScale),
                    height: multiplication(item.height, heightScale)
                });
                setBoxAttrMation("custom.box.background", boxId, item.attrMation.attr["custom.box.background"].defaultValue);
                setBoxAttrMation("custom.box.border-color", boxId, item.attrMation.attr["custom.box.border-color"].defaultValue);
            });
        }
    }

    function initWordMationData(widthScale, heightScale) {
        var wordMationList = initData.wordMationList;
        if (!isNull(wordMationList)) {
            $.each(wordMationList, function (i, item) {
                var leftNum = multiplication(item.attrMation.attr["custom.move.x"].defaultValue, widthScale);
                var topNum = multiplication(item.attrMation.attr["custom.move.y"].defaultValue, heightScale);
                item.attrMation.attr["custom.move.x"].defaultValue = leftNum;
                item.attrMation.attr["custom.move.y"].defaultValue = topNum;
                var boxId = addNewWordModel(item.modelId, item.attrMation);
                $("#" + boxId).css({
                    left: leftNum + "px",
                    top: topNum + "px",
                    width: multiplication(item.width, widthScale),
                    height: multiplication(item.height, heightScale)
                });
                setBoxAttrMation("custom.box.background", boxId, item.attrMation.attr["custom.box.background"].defaultValue);
                setBoxAttrMation("custom.box.border-color", boxId, item.attrMation.attr["custom.box.border-color"].defaultValue);
            });
        }
    }

    function initTableMationData(widthScale, heightScale) {
        var tableMationList = initData.tableMationList;
        if (!isNull(tableMationList)) {
            $.each(tableMationList, function (i, item) {
                var leftNum = multiplication(item.attrMation.attr["custom.move.x"].defaultValue, widthScale);
                var topNum = multiplication(item.attrMation.attr["custom.move.y"].defaultValue, heightScale);
                item.attrMation.attr["custom.move.x"].defaultValue = leftNum;
                item.attrMation.attr["custom.move.y"].defaultValue = topNum;
                item.attrMation.businessApi = getTableDataFromRest(item.attrMation.attr);

                item.attrMation.isPage = item.attrMation.attr["custom.isPage"]?.defaultValue
                var boxId = addNewTableModel(item.modelId, item.attrMation);
                $("#" + boxId).css({
                    left: leftNum + "px",
                    top: topNum + "px",
                    width: multiplication(item.width, widthScale),
                    height: multiplication(item.height, heightScale)
                });
                setBoxAttrMation("custom.box.background", boxId, item.attrMation.attr["custom.box.background"].defaultValue);
                setBoxAttrMation("custom.box.border-color", boxId, item.attrMation.attr["custom.box.border-color"].defaultValue);
            });
        }
    }

    function getContentStr(str) {
        if (!isNull(str)) {
            str = str.replace(/%/g, '%25');
            return decodeURIComponent(str);
        }
        return "{}";
    }

    function getDataFromRest(attr) {
        if (isNull(attr['custom.dataBaseMation'].defaultValue)) {
            return attr;
        }
        var fromId = attr['custom.dataBaseMation'].defaultValue.id;
        var needGetData = {};
        $.each(attr, function(key, value) {
            if (value.editorType == 9) {
                var pointValue = attr[key].pointValue;
                if (!isNull(pointValue)) {
                    needGetData[pointValue] = attr[key].defaultValue;
                }
            }
        });
        if (isNull(fromId) || needGetData.length == 0) {
            return attr;
        }
        var params = {
            id: fromId,
            needGetDataStr: JSON.stringify(needGetData)
        };
        AjaxPostUtil.request({url: sysMainMation.reportBasePath + "queryReportDataFromMationById", params: params, type: 'json', method: "POST", callback: function(json) {
            $.each(json.bean, function(key, value) {
                $.each(attr, function(key1, value1) {
                    if (value1.editorType == 9) {
                        var pointValue = attr[key1].pointValue;
                        if (!isNull(pointValue) && key == pointValue) {
                            attr[key1].defaultValue = value;
                        }
                    }
                });
            });
        }, async: false});
        return attr;
    }

    function getTableDataFromRest(attr) {
        var businessApi = {};
        var fromId = attr['custom.dataBaseMation'].defaultValue?.id;
        if (isNull(fromId)) {
            return businessApi;
        }
        var params = {
            id: fromId
        };
        AjaxPostUtil.request({url: sysMainMation.reportBasePath + "queryReportDataFromById", params: params, type: 'json', method: "GET", callback: function(json) {
            businessApi.serviceStr = json.bean.restEntity?.serviceStr;
            businessApi.api = json.bean.restEntity?.restUrl;
            businessApi.method = json.bean.restEntity?.method;
        }, async: false});
        return businessApi;
    }

    function addNewModel(modelId, echartsMation) {
        if (!isNull(echartsMation)) {
            var option = getEchartsOptions(echartsMation);
            // 获取boxId
            var boxId = modelId + getRandomValueToString();
            // 获取echarts图表id
            var echartsId = getEchartsBox(boxId, modelId);
            // 加载图表
            var newChart = echarts.init(document.getElementById(echartsId));
            newChart.setOption(option);
            $("#" + echartsId).resize(function () {
                newChart.resize();
            });
            // 加入页面属性
            inPageEcharts[boxId] = $.extend(true, {}, echartsMation);
            inPageEchartsObject[boxId] = newChart;
            return boxId;
        }
        return "";
    }

    // 加载表格模型
    function addNewTableModel(modelId, tableMation) {
        // 获取boxId
        var boxId = modelId + getRandomValueToString();
        // 获取表格图表id
        var tableId = getTableBox(boxId, modelId);
        // 加入页面属性
        tableMation["tableId"] = tableId

        // 加载表格
        dsFormTableUtil.initDynamicTable(tableId, tableMation);

        inPageTable[boxId] = $.extend(true, {}, tableMation);
        return boxId;
    }

    // 加载文字模型
    function addNewWordModel(modelId, wordStyleMation) {
        var styleStr = getWordStyleStr(wordStyleMation.attr);
        // 获取boxId
        var boxId = modelId + getRandomValueToString();
        // 获取文字模型id
        var wordId = getWordBox(boxId, modelId, styleStr, wordStyleMation);
        // 加入页面属性
        wordStyleMation.attr = $.extend(true, {}, wordStyleMation.attr);
        inPageWordMation[boxId] = $.extend(true, {}, wordStyleMation);
        return boxId;
    }

    function getEchartsBox(boxId, modelId) {
        var echartsId = "echarts" + boxId;
        var echartsBox = document.createElement("div");
        // 为div设置类名
        echartsBox.className = "echarts-box";
        echartsBox.id = echartsId;
        var box = createBox(boxId, modelId, null);
        box.appendChild(echartsBox);
        return echartsId;
    }

    function getTableBox(boxId, modelId) {
        var box = createBox(boxId, modelId, null);

        var tableBoxId = "table" + boxId;
        var table = document.createElement("table");
        table.id = tableBoxId;
        box.appendChild(table);

        return tableBoxId;
    }

    function getWordBox(boxId, modelId, styleStr, wordStyleMation) {
        var wordId = "word" + boxId;
        var wordBox = document.createElement("font");
        // 为div设置类名
        wordBox.className = "word-box";
        wordBox.innerHTML = wordStyleMation["attr"]["custom.textContent"].defaultValue;
        wordBox.style = styleStr;
        wordBox.id = wordId;
        var box = createBox(boxId, modelId, setDesignAttr(wordStyleMation));
        box.appendChild(wordBox);
        return wordId;
    }

    function setDesignAttr(wordStyleMation) {
        var otherStyle = {
            width: wordStyleMation.defaultWidth + 'px',
            height: wordStyleMation.defaultHeight + 'px'
        };
        return otherStyle;
    }

    function createBox(id, modelId, otherStyle) {
        // 创建一个div
        var div = document.createElement("div");
        // 为div设置类名
        div.className = "kuang";
        div.id = id;
        div.dataset.boxId = id;
        div.dataset.modelId = modelId;
        div.style.top = "0px";
        div.style.left = "0px";
        if (!isNull(otherStyle)) {
            $.each(otherStyle, function (key, value) {
                div.style[key] = value;
            });
        }
        $(document.body)[0].appendChild(div);
        return div;
    }

    form.render();

});