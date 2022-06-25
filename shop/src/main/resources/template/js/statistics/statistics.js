
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
    echarts: '../echarts/echarts',
    echartsTheme: '../echarts/echartsTheme'
}).define(['window', 'jquery', 'winui', 'form', 'echarts', 'laydate'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        laydate = layui.laydate;
    var selOption = getFileContent('tpl/template/select-option.tpl');

    var entTime = getOneYMFormatDate();
    var startTime = getPointYMFormatDate(DateAdd("-m", 5, new Date(entTime)));
    $("#createTime").val(startTime + '~' + entTime);

    laydate.render({
        elem: '#createTime',
        type: 'month',
        range: '~',
        done: function(value, date){
            $("#createTime").val(value);
            resetData();
        }
    });

    form.render();
    // 加载区域
    shopUtil.getShopAreaMation(function (json){
        $("#areaId").html(getDataUseHandlebars($("#selectTemplate").html(), json));
        form.render('select');
    });
    form.on('select(areaId)', function(data) {
        if(isNull(data.value)){
            $("#storeId").html('');
            form.render('select');
            resetData();
        } else {
            shopUtil.queryStoreListByAreaId(data.value, function (json){
                $("#storeId").html(getDataUseHandlebars(selOption, json));
                form.render('select');
                resetData();
            });
        }
    });

    form.on('select(storeId)', function(data) {
        resetData();
    });

    // 加载套餐订单性质
    shopUtil.queryMealOrderNatureList(function (json){
        $("#natureId").html(getDataUseHandlebars(selOption, json));
        form.render('select');
    });
    form.on('select(natureId)', function(data) {
        resetData();
    });

    function getParams(){
        var startTime = "", endTime = "";
        if(!isNull($("#createTime").val())){
            startTime = $("#createTime").val().split('~')[0].trim();
            endTime = $("#createTime").val().split('~')[1].trim();
        }
        return {
            startTime: startTime,
            endTime: endTime,
            areaId: $("#areaId").val(),
            natureId: $("#natureId").val(),
            storeId: isNull($("#storeId").val()) ? "" : $("#storeId").val(),
        };
    }

    resetData()
    var myChart1, myChart2, myChart3, myChart4, myChart5;
    function resetData(){
        AjaxPostUtil.request({url: shopBasePath + "queryStatisticsShop", params: getParams(), type: 'json', method: 'POST', callback: function(json){
            if (json.returnCode == 0) {
                $("#mealOrderMemberByNum").html(json.bean.mealOrderMemberByNum + "个");
                $("#mealOrderNum").html(json.bean.mealOrderNum + "单");
                $("#keepFitOrderNum").html(json.bean.keepFitOrderNum + "单");
                $("#keepFitOrderPrice").html(json.bean.keepFitOrderPrice + "元");
                myChart1 = renderEcharts("echart1", '', '', '', json.bean.monthMealOrderNum);
                myChart2 = renderEcharts("echart2", '', '', '', json.bean.monthKeepFitOrderNum);
                myChart3 = renderEcharts2("echart3", '', '', json.bean.storeMealOrderNum);
                myChart4 = renderEcharts2("echart4", '', '', json.bean.storeKeepFitOrderNum);
                myChart5 = renderEcharts2("echart5", '', '', json.bean.natureMealOrderNum);
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }});
    }

    function renderEcharts(id, title, subtext, yTitle, rows) {
        var myChart = echarts.init(document.getElementById(id));
        var nameStr = new Array();
        var numStr = new Array();
        $.each(rows, function(i, item){
            nameStr.push(item.name);
            numStr.push(item.value);
        });
        var option = getOption(title, '', nameStr, yTitle, numStr, 'bar');
        myChart.setOption(option);
        return myChart;
    }

    function renderEcharts2(id, title, subtext, rows) {
        var myChart = echarts.init(document.getElementById(id));
        var option = getPieChatOption(title, subtext, rows);
        myChart.setOption(option);
        return myChart;
    }

    window.onresize = function(){
        myChart1.resize();
        myChart2.resize();
        myChart3.resize();
        myChart4.resize();
        myChart5.resize();
    }

    exports('statistics', {});
});
