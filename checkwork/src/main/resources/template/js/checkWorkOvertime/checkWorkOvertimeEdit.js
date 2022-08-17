
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

    // 加班日期的初始化集合
    var overtimeDayElem = new Array();
    var overtimeStartElem = new Array();
    var overtimeEndElem = new Array();

    var beanTemplate = $("#beanTemplate").html();

    AjaxPostUtil.request({url: flowableBasePath + "checkworkovertime004", params: {rowId: parent.rowId}, type: 'json', method: 'GET', callback: function(mation) {
        $("#useTitle").html(mation.bean.title);
        $("#useName").html(mation.bean.userName);
        $("#content").html(mation.bean.content);
        $("#remark").val(mation.bean.remark);
        // 附件回显
        skyeyeEnclosure.initTypeISData({'enclosureUpload': mation.bean.enclosureInfo});

        if(mation.bean.state == '1'){
            $(".typeTwo").removeClass("layui-hide");
        } else {
            $(".typeOne").removeClass("layui-hide");
        }

        form.render();
        matchingLanguage();
        $.each(mation.bean.overtimeDay, function(i, item) {
            addRow();
            $("#overtimeDay" + (rowNum - 1).toString()).val(item.overtimeDay);
            $("#overtimeStartTime" + (rowNum - 1).toString()).val(item.overtimeStartTime);
            $("#overtimeEndTime" + (rowNum - 1).toString()).val(item.overtimeEndTime);
            $("#overtimeHour" + (rowNum - 1).toString()).html(item.overtimeHour);
            $("#remark" + (rowNum - 1).toString()).val(item.remark);
            form.render('select');
            form.render('checkbox');
        });
    }});

    // 保存为草稿
    form.on('submit(formEditBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            saveData('1', "");
        }
        return false;
    });

    // 提交审批
    form.on('submit(formSubBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            activitiUtil.startProcess(sysActivitiModel["checkWorkOvertime"]["key"], function (approvalId) {
                saveData("2", approvalId);
            });
        }
        return false;
    });

    // 工作流中保存
    form.on('submit(subBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            saveData('3', "");
        }
        return false;
    });

    function saveData(subType, approvalId) {
        // 获取加班日期数据
        var rowTr = $("#beanTable tr");
        if(rowTr.length == 0) {
            winui.window.msg('请选择加班日期数据', {icon: 2, time: 2000});
            return false;
        }
        var tableData = new Array();
        var noError = false; //循环遍历表格数据时，是否有其他错误信息
        $.each(rowTr, function(i, item) {
            var rowNum = $(item).attr("trcusid").replace("tr", "");
            if(inTableDataArray($("#overtimeDay" + rowNum).val(), tableData)){
                winui.window.msg('同一申请单中不允许出现相同的加班日期.', {icon: 2, time: 2000});
                noError = true;
                return false;
            }
            var row = {
                overtimeDay: $("#overtimeDay" + rowNum).val(),
                overtimeStartTime: $("#overtimeStartTime" + rowNum).val(),
                overtimeEndTime: $("#overtimeEndTime" + rowNum).val(),
                overtimeHour: $("#overtimeHour" + rowNum).html(),
                remark: $("#remark" + rowNum).val()
            };
            tableData.push(row);
        });
        if(noError) {
            return false;
        }

        var params = {
            rowId: parent.rowId,
            content: $("#content").val(),
            remark: $("#remark").val(),
            overtimeDayStr: JSON.stringify(tableData),
            enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
            subType: subType, // 1：保存为草稿  2.提交到工作流  3.在工作流中编辑
            approvalId: approvalId,
        };
        AjaxPostUtil.request({url: flowableBasePath + "checkworkovertime005", params: params, type: 'json', method: 'PUT', callback: function(json) {
            parent.layer.close(index);
            parent.refreshCode = '0';
        }});
    }

    // 判断选中的加班日期是否也在数组中
    function inTableDataArray(overtimeDay, array) {
        var isIn = false;
        $.each(array, function(i, item) {
            if(item.overtimeDay === overtimeDay) {
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
            overtimeDay: "overtimeDay" + rowNum.toString(), //加班日期id
            overtimeStartTime: "overtimeStartTime" + rowNum.toString(), //开始时间id
            overtimeEndTime: "overtimeEndTime" + rowNum.toString(), //结束时间id
            overtimeHour: "overtimeHour" + rowNum.toString(), //加班工时id
            remark: "remark" + rowNum.toString() //备注id
        };
        $("#beanTable").append(getDataUseHandlebars(beanTemplate, par));
        overtimeDayElem[rowNum.toString()] = laydate.render({
            elem: '#overtimeDay' + rowNum.toString(),
            calendar: true,
            min: getFormatDate()
        });
        overtimeStartElem[rowNum.toString()] = laydate.render({
            elem: "#overtimeStartTime" + rowNum.toString(),
            format: 'HH:mm',
            type: 'timeminute',
            minutesinterval: 30,
            btns: ['confirm'],
            min: getFormatDate(),
            done: function(value, date){
                var num = $(this)[0].elem.selector.replace("#", "").replace("overtimeStartTime", "");
                $("#overtimeStartTime" + num).val(value);
                overtimeEndElem[num].config.min = {
                    year: date.year,
                    month: date.month - 1,//关键
                    date: date.date,
                    hours: date.hours,
                    minutes: date.minutes,
                    seconds: date.seconds
                };
                // 计算工时
                calcOvertimeHour(num);
            }
        });

        overtimeEndElem[rowNum.toString()] = laydate.render({
            elem: "#overtimeEndTime" + rowNum.toString(),
            format: 'HH:mm',
            type: 'timeminute',
            minutesinterval: 30,
            btns: ['confirm'],
            min: getFormatDate(),
            done: function(value, date){
                var num = $(this)[0].elem.selector.replace("#", "").replace("overtimeEndTime", "");
                $("#overtimeEndTime" + num).val(value);
                overtimeStartElem[num].config.max = {
                    year: date.year,
                    month: date.month - 1,//关键
                    date: date.date,
                    hours: date.hours,
                    minutes: date.minutes,
                    seconds: date.seconds
                };
                // 计算工时
                calcOvertimeHour(num);
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
     * 计算加班工时
     *
     * @param num 表格的序号
     */
    function calcOvertimeHour(num){
        var startTime = $("#overtimeStartTime" + num).val();
        var endTime = $("#overtimeEndTime" + num).val();
        if (!isNull(startTime) && !isNull(endTime)){
            var hour = division(timeDifference(startTime, endTime), 60);
            $("#overtimeHour" + num).html(hour);
        }
    }

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});