
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

    var echartsModel = {};
    // 获取echarts模型配置
    AjaxPostUtil.request({url: sysMainMation.reportBasePath + "queryAllMaxVersionReportModel", params: {}, type: 'json', method: "GET", callback: function(json) {
        echartsModel = json.rows;
    }, async: false});

    var bgImages = {};
    // 获取所有背景图片列表信息
    AjaxPostUtil.request({url: sysMainMation.reportBasePath + "getEnabledBgImageList", params: {}, type: 'json', method: "GET", callback: function(json) {
        bgImages = json.rows;
    }, async: false});

    var wordModel = {};
    // 获取已经发布的文字模型
    AjaxPostUtil.request({url: sysMainMation.reportBasePath + "getEnabledWordModelList", params: {}, type: 'json', method: "GET", callback: function(json) {
        wordModel = json.rows;
    }, async: false});

    var imgModel = {};
    // 获取所有小图片列表信息
    AjaxPostUtil.request({url: sysMainMation.reportBasePath + "queryAllEnabledImgModelList", params: {}, type: 'json', method: "GET", callback: function(json) {
            imgModel = json.rows;
        }, async: false});

    var initData = {};
    // 获取初始化数据
    AjaxPostUtil.request({url: sysMainMation.reportBasePath + "queryReportPageById", params: {id: id}, type: 'json', method: "GET", callback: function(json) {
        initData = JSON.parse(getContentStr(json.bean.content));
    }, async: false});

    function getContentStr(str){
        if (!isNull(str)){
            str = str.replace(/%/g, '%25');
            return decodeURIComponent(str);
        }
        return "{}";
    }

    $.skyeyeReportDesigner({
        mouseLineColor: "blue",
        initData: initData,
        headerMenuJson: [{
            "icon": " fa fa-wordpress fa-fw",
            "title": "文字",
            "id": "wordModel",
            "children": wordModel
        }, {
            "icon": " fa fa-photo-video fa-fw",
            "title": "小图片",
            "id": "imgModel",
            "children": imgModel
        }, {
            "icon": " fa fa-area-chart fa-fw",
            "title": "图表",
            "id": "echartsModel",
            "children": echartsModel
        }, {
            "icon": " fa fa-table fa-fw",
            "title": "表格",
            "id": "tableModel",
            "children": [{
                "id": "customSimpleTable",
                "icon": " fa fa-table fa-fw",
                "name": "简单表格",
            }]
        }, {
            "icon": " fa fa-area-chart fa-fw",
            "title": "背景图",
            "id": "bgImages",
            "children": bgImages
        }, {
            "icon": " fa fa-fw fa-save",
            "title": "保存",
            "class": "save-btn",
            "id": "save"
        }]
    });

    form.render();

    exports('pageReportDesign', {});

});