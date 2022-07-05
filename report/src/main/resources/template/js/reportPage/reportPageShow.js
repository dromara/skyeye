
var rowId;

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

    rowId = GetUrlParam("rowId");

    var content = $(document.body);
    var initData = {};

    // 获取初始化数据
    AjaxPostUtil.request({url: reportBasePath + "reportpage006", params: {rowId: rowId}, type: 'json', method: "GET", callback: function(json) {
        initData = JSON.parse(getContentStr(json.bean.content));
        var widthScale = getScale(initData.contentWidth, content.width());
        var heightScale = getScale(initData.contentHeight, content.height());

        initEchartsData(widthScale, heightScale);
        initWordMationData(widthScale, heightScale);

        if(!isNull(initData.bgImage)){
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
                var leftNum = multiplication(item.attrMation.attr["custom.move.x"].value, widthScale);
                var topNum = multiplication(item.attrMation.attr["custom.move.y"].value, heightScale);
                item.attrMation.attr["custom.move.x"].value = leftNum;
                item.attrMation.attr["custom.move.y"].value = topNum;
                item.attrMation.attr = getDataFromRest(item.attrMation.attr);
                // 加载模型
                var boxId = addNewModel(item.modelId, item.attrMation);
                $("#" + boxId).css({
                    left: leftNum + "px",
                    top: topNum + "px",
                    width: multiplication(item.width, widthScale),
                    height: multiplication(item.height, heightScale)
                });
                setBoxAttrMation("custom.box.background", boxId, item.attrMation.attr["custom.box.background"].value);
                setBoxAttrMation("custom.box.border-color", boxId, item.attrMation.attr["custom.box.border-color"].value);
            });
        }
    }

    function initWordMationData(widthScale, heightScale) {
        var wordMationList = initData.wordMationList;
        if (!isNull(wordMationList)) {
            $.each(wordMationList, function (i, item) {
                var leftNum = multiplication(item.attrMation.attr["custom.move.x"].value, widthScale);
                var topNum = multiplication(item.attrMation.attr["custom.move.y"].value, heightScale);
                item.attrMation.attr["custom.move.x"].value = leftNum;
                item.attrMation.attr["custom.move.y"].value = topNum;
                var boxId = addNewWordModel(item.modelId, item.attrMation);
                $("#" + boxId).css({
                    left: leftNum + "px",
                    top: topNum + "px",
                    width: multiplication(item.width, widthScale),
                    height: multiplication(item.height, heightScale)
                });
                setBoxAttrMation("custom.box.background", boxId, item.attrMation.attr["custom.box.background"].value);
                setBoxAttrMation("custom.box.border-color", boxId, item.attrMation.attr["custom.box.border-color"].value);
            });
        }
    }

    function getContentStr(str){
        if(!isNull(str)){
            str = str.replace(/%/g, '%25');
            return decodeURIComponent(str);
        }
        return "{}";
    }

    function getDataFromRest(attr){
        var fromId = attr['custom.dataBaseMation'].value.id;
        var needGetData = {};
        $.each(attr, function(key, value){
            if(value.editor == 9){
                var pointValue = attr[key].pointValue;
                if(!isNull(pointValue)){
                    needGetData[pointValue] = attr[key].value;
                }
            }
        });
        if(isNull(fromId) || needGetData.length == 0){
            return attr;
        }
        var params = {
            fromId: fromId,
            needGetDataStr: JSON.stringify(needGetData)
        };
        AjaxPostUtil.request({url: reportBasePath + "reportdatafrom007", params: params, type: 'json', method: "POST", callback: function(json) {
            $.each(json.bean, function(key, value){
                $.each(attr, function(key1, value1){
                    if(value1.editor == 9){
                        var pointValue = attr[key1].pointValue;
                        if(!isNull(pointValue) && key == pointValue){
                            attr[key1].value = value;
                        }
                    }
                });
            });
        }, async: false});
        return attr;
    }

    function addNewModel(modelId, echartsMation){
        if(!isNull(echartsMation)){
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

    // 加载文字模型
    function addNewWordModel(modelId, wordStyleMation){
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

    function getEchartsBox(boxId, modelId){
        var echartsId = "echarts" + boxId;
        var echartsBox = document.createElement("div");
        // 为div设置类名
        echartsBox.className = "echarts-box";
        echartsBox.id = echartsId;
        var box = createBox(boxId, modelId, null);
        box.appendChild(echartsBox);
        return echartsId;
    }

    function getWordBox(boxId, modelId, styleStr, wordStyleMation){
        var wordId = "word" + boxId;
        var wordBox = document.createElement("font");
        // 为div设置类名
        wordBox.className = "word-box";
        wordBox.innerHTML = wordStyleMation["attr"]["custom.textContent"].value;
        wordBox.style = styleStr;
        wordBox.id = wordId;
        var box = createBox(boxId, modelId, setDesignAttr(wordStyleMation));
        box.appendChild(wordBox);
        return wordId;
    }

    function setDesignAttr(wordStyleMation){
        var otherStyle = {
            width: wordStyleMation.defaultWidth + 'px',
            height: wordStyleMation.defaultHeight + 'px'
        };
        return otherStyle;
    }

    function createBox(id, modelId, otherStyle){
        // 创建一个div
        var div = document.createElement("div");
        // 为div设置类名
        div.className = "kuang";
        div.id = id;
        div.dataset.boxId = id;
        div.dataset.modelId = modelId;
        div.style.top = "0px";
        div.style.left = "0px";
        if(!isNull(otherStyle)){
            $.each(otherStyle, function (key, value){
                div.style[key] = value;
            });
        }
        $(document.body)[0].appendChild(div);
        return div;
    }

    form.render();

});