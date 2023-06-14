
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'laydate'], function(exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        laydate = layui.laydate,
        form = layui.form;

    showGrid({
        id: "showForm",
        url: sysMainMation.bossBasePath + "queryArrangementById",
        params: {id: parent.rowId},
        pagination: false,
        method: "GET",
        template: $("#beanTemplate").html(),
        ajaxSendLoadBefore: function(hdb, json){
            json.bean.basicResume = stringManipulation.textAreaShow(json.bean.basicResume);
        },
        ajaxSendAfter: function (json) {
            skyeyeEnclosure.showDetails({'enclosureUpload': json.bean.interviewMation.enclosureResume});

            // 员工在职状态加载
            skyeyeClassEnumUtil.showEnumDataListByClassName("userStaffState", 'select', "inductionState", '', form);
            form.on('select(inductionState)', function (data) {
                if (data.value == '4') {
                    // 试用期
                    $("#trialTimeBox").show();
                } else {
                    $("#trialTimeBox").hide();
                }
            });

            // 入职时间
            laydate.render({elem: '#entryTime', range: false});

            // 参加工作时间
            laydate.render({elem: '#workTime', range: false});

            // 预计试用结束日期
            laydate.render({elem: '#trialTime', range: false});

            $("#reasonBox").hide();
            form.on('radio(state)', function(data) {
                var thisRowValue = data.value;
                if(thisRowValue == 6){ // 同意入职
                    $("#reasonBox").hide();
                    $(".agreePass").show();
                } else if (thisRowValue == 7){ // 拒绝入职
                    $("#reasonBox").show();
                    $(".agreePass").hide();
                }
            });

            matchingLanguage();
            form.render();
            // 提交
            form.on('submit(formSubBean)', function(data) {
                if(winui.verifyForm(data.elem)) {
                    var state = $("input[name='state']:checked").val();
                    if (state == 6) {
                        // 同意入职
                        if (isNull($("#entryTime").val())) {
                            winui.window.msg('请选择入职时间', {icon: 2, time: 2000});
                            return false;
                        }
                        if (isNull($("#workTime").val())) {
                            winui.window.msg('请选择参加工作时间', {icon: 2, time: 2000});
                            return false;
                        }
                        if (isNull($("#userIdCard").val())) {
                            winui.window.msg('请输入身份证', {icon: 2, time: 2000});
                            return false;
                        }
                        if (isNull($("#inductionState").val())) {
                            winui.window.msg('请选择入职状态', {icon: 2, time: 2000});
                            return false;
                        }
                        var inductionState = $("#inductionState").val();
                        if (inductionState == '4' && isNull($("#trialTime").val())) {
                            winui.window.msg('请选择预计试用结束日期', {icon: 2, time: 2000});
                            return false;
                        }
                    }
                    var params = {
                        id: parent.rowId,
                        state: state,
                        reason: $("#reason").val(),
                        entryTime: $("#entryTime").val(),
                        workTime: $("#workTime").val(),
                        userIdCard: $("#userIdCard").val(),
                        inductionState: inductionState,
                        trialTime: $("#trialTime").val()
                    };
                    AjaxPostUtil.request({url: sysMainMation.bossBasePath + "setInductionResult", params: params, type: 'json', method: "PUT", callback: function(json) {
                        parent.layer.close(index);
                        parent.refreshCode = '0';
                    }});
                }
                return false;
            });

        }
    });

});