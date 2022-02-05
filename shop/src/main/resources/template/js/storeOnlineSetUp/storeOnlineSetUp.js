
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        table = layui.table,
        laydate = layui.laydate;
    var beanTemplate = $("#beanTemplate").html();

    authBtn('1643984717466');

    // 加载我所在的门店
    shopUtil.queryStaffBelongStoreList(function (json){
        $("#storeId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json));
    });

    form.on('select(storeId)', function(data) {
        var thisRowValue = data.value;
        if(isNull(thisRowValue)){
            $("#formContent").html("");
        }else{
            showGrid({
                id: "formContent",
                url: shopBasePath + "store008",
                params: {rowId: thisRowValue},
                pagination: false,
                method: "GET",
                template: beanTemplate,
                ajaxSendLoadBefore: function(hdb){},
                ajaxSendAfter:function(data){

                    var startTime = laydate.render({
                        elem: '#businessStartTime',
                        format: 'HH:mm',
                        type: 'timeminute',
                        minutesinterval: 15,
                        btns: ['confirm'],
                        done:function(value, date){
                            endTime.config.min = {
                                year: date.year,
                                month: date.month - 1,//关键
                                date: date.date,
                                hours: date.hours,
                                minutes: date.minutes,
                                seconds: date.seconds
                            };
                        }
                    });

                    var endTime = laydate.render({
                        elem: '#businessEndTime',
                        format: 'HH:mm',
                        type: 'timeminute',
                        minutesinterval: 15,
                        btns: ['confirm'],
                        done:function(value, date){
                            startTime.config.max = {
                                year: date.year,
                                month: date.month - 1,//关键
                                date: date.date,
                                hours: date.hours,
                                minutes: date.minutes,
                                seconds: date.seconds
                            };
                        }
                    });

                    $("input:radio[name=onlineBookAppoint][value=" + data.bean.onlineBookAppoint + "]").attr("checked", true);
                    $("input:radio[name=onlineBookType][value=" + data.bean.onlineBookType + "]").attr("checked", true);

                    matchingLanguage();
                    form.render();
                    form.on('submit(formSaveBean)', function (data) {
                        if (winui.verifyForm(data.elem)) {
                            var tableData = new Array();
                            $.each($(".onlineJson"), function(i, item) {
                                var row = {
                                    time: $(item).attr("id").replace("td", ""),
                                    value: $(item).val(),
                                };
                                tableData.push(row);
                            });
                            var onlineBookAppoint = $("input[name='type']:checked").val();
                            if(onlineBookAppoint == 1){
                                // 开启预约
                                if(isNull($("#onlineBookRadix").val())){
                                    winui.window.msg('请输入维修基数', {icon: 2,time: 2000});
                                    return false;
                                }
                            }
                            console.log(tableData)

                            // var params = {
                            //     title: $("#title").val(),
                            //     logo: $("#logo").find("input[type='hidden'][name='upload']").attr("oldurl"),
                            //     type: $("input[name='type']:checked").val(),
                            //     price: $("#price").val(),
                            //     mealNum: $("#mealNum").val(),
                            //     mealExplain: $("#mealExplain").val(),
                            //     state: $("input[name='state']:checked").val(),
                            //     mealAreaMationList: JSON.stringify(mealAreaMationList),
                            //     mealConsumeMationList: JSON.stringify(tableData),
                            // };
                            //
                            // AjaxPostUtil.request({url: shopBasePath + "meal002", params: params, type: 'json', method: "POST", callback: function(json){
                            //     if(json.returnCode == 0){
                            //         parent.layer.close(index);
                            //         parent.refreshCode = '0';
                            //     }else{
                            //         winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                            //     }
                            // }, async: true});
                        }
                        return false;
                    });
                }
            });
        }
    });

    $("body").on("click", "#calc", function() {
        var startTime = $("#businessStartTime").val();
        if(isNull(startTime)){
            winui.window.msg('请选择营业开始时间', {icon: 2,time: 2000});
            return false;
        }
        var endTime = $("#businessEndTime").val();
        if(isNull(endTime)){
            winui.window.msg('请选择营业结束时间', {icon: 2,time: 2000});
            return false;
        }
        var onlineBookRadix = $("#onlineBookRadix").val();
        if(isNull(onlineBookRadix)){
            winui.window.msg('请输入维修基数', {icon: 2,time: 2000});
            return false;
        }
        var time = getTimePointMinute(startTime, endTime, parseInt(onlineBookRadix));
        $("#tableHead").html(getDataUseHandlebars($("#tableHeadTemplate").html(), {rows: time}));
        $("#useTable").html(getDataUseHandlebars($("#tableBodyTemplate").html(), {rows: time}));
    });

    matchingLanguage();
    form.render();

    exports('storeOnlineSetUp', {});
});
