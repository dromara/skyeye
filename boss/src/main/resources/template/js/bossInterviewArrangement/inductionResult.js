
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
        url: flowableBasePath + "queryBossInterviewArrangementById",
        params: {id: parent.rowId},
        pagination: false,
        method: "GET",
        template: $("#beanTemplate").html(),
        ajaxSendLoadBefore: function(hdb, json){
            json.bean.basicResume = stringManipulation.textAreaShow(json.bean.basicResume);
            json.bean.stateName = bossUtil.showStateName(json.bean.state);
        },
        ajaxSendAfter: function (json) {
            skyeyeEnclosure.showDetails({'enclosureUpload': json.bean.enclosureInfo});

            // 入职时间
            laydate.render({
                elem: '#entryTime',
                range: false
            });

            // 参加工作时间
            laydate.render({
                elem: '#workTime',
                range: false
            });

            $("#reasonBox").hide();
            form.on('radio(state)', function(data) {
                var thisRowValue = data.value;
                if(thisRowValue == 6){ // 同意入职
                    $("#reasonBox").hide();
                    $(".agreePass").show();
                }else if(thisRowValue == 7){ // 拒绝入职
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
                    if(state == 6) {
                        if(isNull($("#entryTime").val())){
                            winui.window.msg('请选择入职时间', {icon: 2,time: 2000});
                            return false;
                        }
                        if(isNull($("#workTime").val())){
                            winui.window.msg('请选择参加工作时间', {icon: 2,time: 2000});
                            return false;
                        }
                        if(isNull($("#userIdCard").val())){
                            winui.window.msg('请输入身份证', {icon: 2,time: 2000});
                            return false;
                        }
                    }
                    var params = {
                        id: parent.rowId,
                        state: state,
                        reason: $("#reason").val(),
                        entryTime: $("#entryTime").val(),
                        workTime: $("#workTime").val(),
                        userIdCard: $("#userIdCard").val()
                    };
                    AjaxPostUtil.request({url: flowableBasePath + "setInductionResult", params: params, type: 'json', method: "PUT", callback: function(json) {
                        if(json.returnCode == 0) {
                            parent.layer.close(index);
                            parent.refreshCode = '0';
                        } else {
                            winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                        }
                    }});
                }
                return false;
            });

        }
    });

});