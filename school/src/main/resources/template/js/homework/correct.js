layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form;
    var id = GetUrlParam("id");
    var assignmentId = GetUrlParam("assignmentId");
    var fullMarks

    showGrid({
        id: "showForm",
        url: sysMainMation.schoolBasePath + "queryAssignmentById",
        params: {id: assignmentId},
        method: 'GET',
        pagination: false,
        template: $("#beanTemplate").html(),
        ajaxSendAfter: function (json) {
            fullMarks = json.bean.fullMarks;
            matchingLanguage();
            form.render();
        }
    });

    if (!isNull(id)) {
        AjaxPostUtil.request({url: sysMainMation.schoolBasePath + "queryAssignmentSubById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
                $("#score").val(json.bean.score);
                $("#comment").val(json.bean.comment);
                matchingLanguage();
                form.render();
            }});
    }

    form.on('submit(formAddBean)', function (data) {0
        if (parseInt($("#score").val()) > parseInt(fullMarks) || parseInt($("#score").val()) < 0) {
            winui.window.msg('请输入正确评分', {icon: 2, time: 2000});
            return false;
        }
        if (winui.verifyForm(data.elem)) {
            let params = {
                id: id,
                score:  $("#score").val(),
                comment: $("#comment").val(),
            }
            AjaxPostUtil.request({
                url: sysMainMation.schoolBasePath + "readOverAssignmentSubById",
                params: params,
                type: 'json',
                method: 'POST',
                callback: function (json) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            }});
        }
        return false;
    });

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});