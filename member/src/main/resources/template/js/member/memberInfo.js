
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$;
        var simpleTemplate = $("#simpleTemplate").html();

        var type = GetUrlParam("type");

        showGrid({
            id: "showForm",
            url: shopBasePath + "member008",
            params: {rowId: parent.rowId},
            pagination: false,
            method: "GET",
            template: simpleTemplate,
            ajaxSendAfter:function(json){
                $("#enabled").html(json.bean.enabled == "1" ? "<span class='state-up'>启用</span>" : "<span class='state-down'>禁用</span>");

                // 加载车辆信息
                AjaxPostUtil.request({url: shopBasePath + "memberCar001", params: {memberId: parent.rowId}, type: 'json', method: "POST", callback: function(json){
                    if(json.returnCode == 0){
                        $.each(json.rows, function (i, item){
                            if(item.insure == '1'){
                                item.insure = "已购买";
                            }else if(item.insure == '2'){
                                item.insure = "未购买";
                            }
                        });
                        $("#showForm").append(getDataUseHandlebars($("#memberCarTemplate").html(), json));
                    }else{
                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                    }
                }});

                matchingLanguage();
                form.render();
            }
        });

        $("body").on("click", "#cancle", function(){
            parent.layer.close(index);
        });
    });
});