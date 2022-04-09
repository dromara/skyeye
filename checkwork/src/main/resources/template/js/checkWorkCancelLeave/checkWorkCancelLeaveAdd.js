
// 员工考勤班次
var checkWorkTime = new Array();

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'form'], function(exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form,
        laydate = layui.laydate;
    var rowNum = 1; //表格的序号

    // 销假日期的初始化集合
    var cancelDayElem = new Array();
    var cancelStartElem = new Array();
    var cancelEndElem = new Array();

    var beanTemplate = $("#beanTemplate").html();
    var selOption = getFileContent('tpl/template/select-option.tpl');

    // 获取当前登录员工信息
    systemCommonUtil.getSysCurrentLoginUserMation(function (data){
        $("#useTitle").html("用户销假申请单-" + getYMDFormatDate() + '-' + data.bean.userName);
        $("#useName").html(data.bean.userName);
    });
    // 获取当前员工的考勤班次
    AjaxPostUtil.request({url: flowableBasePath + "checkworktime007", params: {}, type: 'json', method: 'POST', callback: function(json) {
        if(json.returnCode == 0) {
            $.each(json.rows, function (i, item){
                checkWorkTime.push({
                    id: item.timeId,
                    name: item.title,
                    days: item.days,
                    startTime: item.startTime,
                    endTime: item.endTime,
                    restStartTime: item.restStartTime,
                    restEndTime: item.restEndTime,
                    type: item.type
                });
            });
            // 考勤班次变化
            form.on('select(timeId)', function(data) {
                var thisRowNum = data.elem.id.replace("timeId", "");
                var thisRowValue = data.value;
                $("#cancelDay" + thisRowNum).val("");
                $("#cancelStartTime" + thisRowNum).val("");
                $("#cancelEndTime" + thisRowNum).val("");
                $("#cancelHour" + thisRowNum).html("0");
                var timeMation = getInPoingArr(checkWorkTime, "id", thisRowValue);
                if(timeMation != null){
                    cancelDayElem[thisRowNum].config.chooseDay = timeMation.days;
                    var min = {
                        year: getYMDFormatDate().split('-')[0],
                        month: getYMDFormatDate().split('-')[1] - 1,//关键
                        date: getYMDFormatDate().split('-')[2],
                        hours: timeMation.startTime.split(':')[0],
                        minutes: timeMation.startTime.split(':')[1] - 1,
                        seconds: '00'
                    };
                    var max = {
                        year: getYMDFormatDate().split('-')[0],
                        month: getYMDFormatDate().split('-')[1] - 1,//关键
                        date: getYMDFormatDate().split('-')[2],
                        hours: timeMation.endTime.split(':')[0],
                        minutes: timeMation.endTime.split(':')[1] + 1,
                        seconds: '00'
                    };
                    cancelStartElem[thisRowNum].config.min = min;
                    cancelStartElem[thisRowNum].config.max = max;
                    cancelEndElem[thisRowNum].config.min = min;
                    cancelEndElem[thisRowNum].config.max = max;
                    $("#workTime" + thisRowNum).html(timeMation.startTime + " ~ " + timeMation.endTime);
                }else{
                    cancelDayElem[thisRowNum].config.chooseDay = [];
                    $("#workTime" + thisRowNum).html("-");
                }
            });
            form.render();
            matchingLanguage();
            // 初始化一行数据
            addRow();
        } else {
            winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
        }
    }});

    skyeyeEnclosure.init('enclosureUpload');
    // 保存为草稿
    form.on('submit(formAddBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            saveData("1", "");
        }
        return false;
    });

    // 提交审批
    form.on('submit(formSubBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            activitiUtil.startProcess(sysActivitiModel["checkWorkCancelLeave"]["key"], function (approvalId) {
                saveData("2", approvalId);
            });
        }
        return false;
    });

    function saveData(subType, approvalId){
        // 获取销假日期数据
        var rowTr = $("#beanTable tr");
        if(rowTr.length == 0) {
            winui.window.msg('请选择销假日期数据', {icon: 2, time: 2000});
            return false;
        }
        var tableData = new Array();
        var noError = false; //循环遍历表格数据时，是否有其他错误信息
        $.each(rowTr, function(i, item) {
            var rowNum = $(item).attr("trcusid").replace("tr", "");
            if(inTableDataArray($("#timeId" + rowNum).val(), $("#cancelDay" + rowNum).val(), tableData)){
                winui.window.msg('同一班次中不允许出现相同的销假日期.', {icon: 2, time: 2000});
                noError = true;
                return false;
            }
            var row = {
                timeId: $("#timeId" + rowNum).val(),
                timeStartTime: $("#workTime" + rowNum).html().split('~')[0].trim(),
                timeEndTime: $("#workTime" + rowNum).html().split('~')[1].trim(),
                cancelDay: $("#cancelDay" + rowNum).val(),
                cancelStartTime: $("#cancelStartTime" + rowNum).val(),
                cancelEndTime: $("#cancelEndTime" + rowNum).val(),
                cancelHour: $("#cancelHour" + rowNum).html(),
                remark: $("#remark" + rowNum).val()
            };
            tableData.push(row);
        });
        if(noError) {
            return false;
        }

        var params = {
            title: $("#useTitle").html(),
            remark: $("#remark").val(),
            cancelLeaveDayStr: JSON.stringify(tableData),
            enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
            subType: subType, // 表单类型 1.保存草稿  2.提交审批
            approvalId: approvalId
        };
        AjaxPostUtil.request({url: flowableBasePath + "checkworkcancelleave002", params: params, type: 'json', method: 'POST',  callback: function(json) {
            if(json.returnCode == 0) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }});
    }

    // 判断选中的销假日期是否也在数组中
    function inTableDataArray(timeId, cancelDay, array) {
        var isIn = false;
        $.each(array, function(i, item) {
            if(item.timeId === timeId && item.cancelDay === cancelDay) {
                isIn = true;
                return false;
            }
        });
        return isIn;
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
            id: "row" + rowNum.toString(), //checkbox的id
            trId: "tr" + rowNum.toString(), //行的id
            timeId: "timeId" + rowNum.toString(), //班次id
            workTime: "workTime" + rowNum.toString(), //打卡时间id
            cancelDay: "cancelDay" + rowNum.toString(), //销假日期id
            cancelStartTime: "cancelStartTime" + rowNum.toString(), //开始时间id
            cancelEndTime: "cancelEndTime" + rowNum.toString(), //结束时间id
            cancelHour: "cancelHour" + rowNum.toString(), //销假工时id
            remark: "remark" + rowNum.toString() //备注id
        };
        $("#beanTable").append(getDataUseHandlebars(beanTemplate, par));
        $("#timeId" + rowNum.toString()).html(getDataUseHandlebars(selOption, {rows: checkWorkTime}));
        cancelDayElem[rowNum.toString()] = laydate.render({
            elem: '#cancelDay' + rowNum.toString(),
            calendar: true,
            min: getFormatDate()
        });
        cancelStartElem[rowNum.toString()] = laydate.render({
            elem: "#cancelStartTime" + rowNum.toString(),
            format: 'HH:mm',
            type: 'timeminute',
            minutesinterval: 30,
            btns: ['confirm'],
            done: function(value, date){
                var num = $(this)[0].elem.selector.replace("#", "").replace("cancelStartTime", "");
                $("#cancelStartTime" + num).val(value);
                cancelEndElem[num].config.min = {
                    year: date.year,
                    month: date.month - 1,//关键
                    date: date.date,
                    hours: date.hours,
                    minutes: date.minutes,
                    seconds: date.seconds
                };
                // 计算工时
                calcLeaveHour(num);
            }
        });

        cancelEndElem[rowNum.toString()] = laydate.render({
            elem: "#cancelEndTime" + rowNum.toString(),
            format: 'HH:mm',
            type: 'timeminute',
            minutesinterval: 30,
            btns: ['confirm'],
            done: function(value, date){
                var num = $(this)[0].elem.selector.replace("#", "").replace("cancelEndTime", "");
                $("#cancelEndTime" + num).val(value);
                cancelStartElem[num].config.max = {
                    year: date.year,
                    month: date.month - 1,//关键
                    date: date.date,
                    hours: date.hours,
                    minutes: date.minutes,
                    seconds: date.seconds
                };
                // 计算工时
                calcLeaveHour(num);
            }
        });
        form.render('select');
        form.render('checkbox');
        rowNum++;
    }

    // 删除行
    function deleteRow() {
        var checkRow = $("#beanTable input[type='checkbox'][name='tableCheckRow']:checked");
        if(checkRow.length > 0) {
            $.each(checkRow, function(i, item) {
                $(item).parent().parent().remove();
            });
        } else {
            winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
        }
    }

    /**
     * 计算销假工时
     *
     * @param num 表格的序号
     */
    function calcLeaveHour(num){
        var startTime = $("#cancelStartTime" + num).val();
        var endTime = $("#cancelEndTime" + num).val();
        if(!isNull(startTime) && !isNull(endTime)){
            var timeId = $("#timeId" + num).val();
            if(!isNull(timeId)){
                var hour = "0";
                var timeMation = getInPoingArr(checkWorkTime, "id", timeId);
                startTime = startTime + ":00";
                endTime = endTime + ":00";
                // 作息时间是否为空
                if(!isNull(timeMation.restStartTime) && !isNull(timeMation.restEndTime)){
                    var restStartTime = timeMation.restStartTime + ":00";
                    var restEndTime = timeMation.restEndTime + ":00";
                    if(compare_HHmmss(restStartTime, endTime) || compare_HHmmss(startTime, restEndTime)){
                        // 销假结束时间比作息开始时间小或者销假开始时间比作息结束时间大，说明没有与作息时间重合的时间
                        hour = division(timeDifference(startTime, endTime), 60);
                    }else{
                        var overlapTime = getOverlapTime(startTime, endTime, restStartTime, restEndTime);
                        // 时间计算为：选择的销假时间时间减去与作息时间重复的时间段
                        hour = division(subtraction(timeDifference(startTime, endTime), timeDifference(overlapTime[0], overlapTime[1])), 60);
                    }
                }else{
                    hour = division(timeDifference(startTime, endTime), 60);
                }
                $("#cancelHour" + num).html(hour);
            }
        }
    }

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});