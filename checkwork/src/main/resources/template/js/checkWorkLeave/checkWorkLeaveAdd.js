
// 请假类型集合
var leaveType = new Array();

// 员工考勤班次
var checkWorkTime = new Array();

// 员工年假/补休
var staffYearHoliday = 0;
var holidayNumber = 0;

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

    // 请假日期的初始化集合
    var leaveDayElem = new Array();
    var leaveStartElem = new Array();
    var leaveEndElem = new Array();

    var beanTemplate = $("#beanTemplate").html();
    var selOption = getFileContent('tpl/template/select-option.tpl');

    // 获取当前登录员工信息
    systemCommonUtil.getSysCurrentLoginUserMation(function (data){
        $("#useTitle").html("用户请假申请单-" + getYMDFormatDate() + '-' + data.bean.userName);
        $("#useName").html(data.bean.userName);
    });
    // 获取当前员工的年假
    AjaxPostUtil.request({url: reqBasePath + "staff010", params: {}, type: 'json', method: 'GET', callback: function(json) {
        if(json.returnCode == 0) {
            staffYearHoliday = json.bean.annualLeave;
            holidayNumber = json.bean.holidayNumber;
            $("#messageTips").html("截至当前剩余年假：" + staffYearHoliday + "小时，剩余补休为：" + holidayNumber + "小时");
            // 获取当前登陆人的考勤班次
            checkWorkUtil.getCurrentUserCheckWorkTimeList(function (json) {
                $.each(json.rows, function (i, item) {
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
                initTypeHtml();
            });
        } else {
            winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
        }
    }});

    // 初始化请假类型
    function initTypeHtml() {
        AjaxPostUtil.request({url: reqBasePath + "sysfdsettings001", params: {}, type: 'json', method: 'GET', callback: function(json) {
            if(json.returnCode == 0) {
                $.each(JSON.parse(json.bean.holidaysTypeJson), function (i, item){
                    leaveType.push({
                        id: item.holidayNo,
                        name: item.holidayName,
                        whetherYearHour: item.whetherYearHour,
                        whetherComLeave: item.whetherComLeave
                    });
                });
                // 考勤班次变化
                form.on('select(timeId)', function(data) {
                    var thisRowNum = data.elem.id.replace("timeId", "");
                    var thisRowValue = data.value;
                    $("#leaveDay" + thisRowNum).val("");
                    $("#leaveStartTime" + thisRowNum).val("");
                    $("#leaveEndTime" + thisRowNum).val("");
                    $("#leaveHour" + thisRowNum).html("0");
                    var timeMation = getInPoingArr(checkWorkTime, "id", thisRowValue);
                    if(timeMation != null){
                        leaveDayElem[thisRowNum].config.chooseDay = timeMation.days;
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
                        leaveStartElem[thisRowNum].config.min = min;
                        leaveStartElem[thisRowNum].config.max = max;
                        leaveEndElem[thisRowNum].config.min = min;
                        leaveEndElem[thisRowNum].config.max = max;
                        $("#workTime" + thisRowNum).html(timeMation.startTime + " ~ " + timeMation.endTime);
                    } else {
                        leaveDayElem[thisRowNum].config.chooseDay = [];
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
    }

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
            activitiUtil.startProcess(sysActivitiModel["checkWorkLeave"]["key"], function (approvalId) {
                saveData("2", approvalId);
            });
        }
        return false;
    });

    function saveData(subType, approvalId){
        // 获取请假日期数据
        var rowTr = $("#beanTable tr");
        if(rowTr.length == 0) {
            winui.window.msg('请选择请假日期数据', {icon: 2, time: 2000});
            return false;
        }
        var tableData = new Array();
        var noError = false; //循环遍历表格数据时，是否有其他错误信息
        var useYearHour = 0;
        var useHolidayNumber = 0;
        $.each(rowTr, function(i, item) {
            var rowNum = $(item).attr("trcusid").replace("tr", "");
            if(inTableDataArray($("#timeId" + rowNum).val(), $("#leaveDay" + rowNum).val(), tableData)){
                winui.window.msg('同一班次中不允许出现相同的请假日期.', {icon: 2, time: 2000});
                noError = true;
                return false;
            }
            var leaveTypeMation = getInPoingArr(leaveType, "id", $("#leaveType" + rowNum).val());
            if(leaveTypeMation.whetherYearHour == 1){
                // 使用年假
                useYearHour = sum(useYearHour, $("#leaveHour" + rowNum).html());
            }
            if(leaveTypeMation.whetherComLeave == 1){
                // 使用补休
                useHolidayNumber = sum(useHolidayNumber, $("#leaveHour" + rowNum).html());
            }
            var row = {
                timeId: $("#timeId" + rowNum).val(),
                timeStartTime: $("#workTime" + rowNum).html().split('~')[0].trim(),
                timeEndTime: $("#workTime" + rowNum).html().split('~')[1].trim(),
                leaveType: $("#leaveType" + rowNum).val(),
                leaveTypeName: $("#leaveType" + rowNum + " option:checked").text(),
                leaveDay: $("#leaveDay" + rowNum).val(),
                leaveStartTime: $("#leaveStartTime" + rowNum).val(),
                leaveEndTime: $("#leaveEndTime" + rowNum).val(),
                leaveHour: $("#leaveHour" + rowNum).html(),
                remark: $("#remark" + rowNum).val()
            };
            tableData.push(row);
        });
        if(noError) {
            return false;
        }
        if(useYearHour > staffYearHoliday){
            winui.window.msg('超出剩余年假，请修改假期类型.', {icon: 2, time: 2000});
            return false;
        }
        if(useHolidayNumber > holidayNumber){
            winui.window.msg('超出剩余补休，请修改假期类型.', {icon: 2, time: 2000});
            return false;
        }

        var params = {
            title: $("#useTitle").html(),
            remark: $("#remark").val(),
            leaveDayStr: JSON.stringify(tableData),
            enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
            subType: subType, // 表单类型 1.保存草稿  2.提交审批
            approvalId: approvalId
        };
        AjaxPostUtil.request({url: flowableBasePath + "checkworkleave002", params: params, type: 'json', method: 'POST',  callback: function(json) {
            if(json.returnCode == 0) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }});
    }

    // 判断选中的请假日期是否也在数组中
    function inTableDataArray(timeId, leaveDay, array) {
        var isIn = false;
        $.each(array, function(i, item) {
            if(item.timeId === timeId && item.leaveDay === leaveDay) {
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
            leaveType: "leaveType" + rowNum.toString(), //请假类型id
            leaveDay: "leaveDay" + rowNum.toString(), //请假日期id
            leaveStartTime: "leaveStartTime" + rowNum.toString(), //开始时间id
            leaveEndTime: "leaveEndTime" + rowNum.toString(), //结束时间id
            leaveHour: "leaveHour" + rowNum.toString(), //请假工时id
            remark: "remark" + rowNum.toString() //备注id
        };
        $("#beanTable").append(getDataUseHandlebars(beanTemplate, par));
        $("#timeId" + rowNum.toString()).html(getDataUseHandlebars(selOption, {rows: checkWorkTime}));
        $("#leaveType" + rowNum.toString()).html(getDataUseHandlebars(selOption, {rows: leaveType}));
        leaveDayElem[rowNum.toString()] = laydate.render({
            elem: '#leaveDay' + rowNum.toString(),
            calendar: true,
            min: getThirdDayToDate()
        });
        leaveStartElem[rowNum.toString()] = laydate.render({
            elem: "#leaveStartTime" + rowNum.toString(),
            format: 'HH:mm',
            type: 'timeminute',
            minutesinterval: 30,
            btns: ['confirm'],
            done: function(value, date){
                var num = $(this)[0].elem.selector.replace("#", "").replace("leaveStartTime", "");
                $("#leaveStartTime" + num).val(value);
                leaveEndElem[num].config.min = {
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

        leaveEndElem[rowNum.toString()] = laydate.render({
            elem: "#leaveEndTime" + rowNum.toString(),
            format: 'HH:mm',
            type: 'timeminute',
            minutesinterval: 30,
            btns: ['confirm'],
            done: function(value, date){
                var num = $(this)[0].elem.selector.replace("#", "").replace("leaveEndTime", "");
                $("#leaveEndTime" + num).val(value);
                leaveStartElem[num].config.max = {
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
     * 计算请假工时
     *
     * @param num 表格的序号
     */
    function calcLeaveHour(num){
        var startTime = $("#leaveStartTime" + num).val();
        var endTime = $("#leaveEndTime" + num).val();
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
                        // 请假结束时间比作息开始时间小或者请假开始时间比作息结束时间大，说明没有与作息时间重合的时间
                        hour = division(timeDifference(startTime, endTime), 60);
                    } else {
                        var overlapTime = getOverlapTime(startTime, endTime, restStartTime, restEndTime);
                        // 时间计算为：选择的请假时间时间减去与作息时间重复的时间段
                        hour = division(subtraction(timeDifference(startTime, endTime), timeDifference(overlapTime[0], overlapTime[1])), 60);
                    }
                } else {
                    hour = division(timeDifference(startTime, endTime), 60);
                }
                $("#leaveHour" + num).html(hour);
            }
        }
    }

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});