
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function(exports) {
    winui.renderColor();
    layui.use(['form'], function(form) {
        var $ = layui.$;

        AjaxPostUtil.request({url: flowableBasePath + "checkworkleave003", params: {rowId: parent.rowId}, type: 'json', method: 'GET', callback: function(json) {
            if(json.returnCode == 0) {
                json.bean.stateName = getStateNameByState(json.bean.state, json.bean.stateName);

                var _html = getDataUseHandlebars($("#useTemplate").html(), json);
                $("#showForm").html(_html);
                // 附件回显
                var str = "暂无附件";
                if(json.bean.enclosureInfo.length != 0 && !isNull(json.bean.enclosureInfo)){
                    str = "";
                    $.each([].concat(json.bean.enclosureInfo), function(i, item){
                        str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
                    });
                }
                $("#enclosureUpload").html(str);
                form.render();
                matchingLanguage();
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }});
    });
});