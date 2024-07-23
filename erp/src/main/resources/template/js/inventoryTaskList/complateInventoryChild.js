
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui','tableSelect', 'form'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form;
        tableSelect = layui.tableSelect;
    var id = GetUrlParam("id");

    showGrid({
        id: "showForm",
        url: sysMainMation.erpBasePath + "queryInventoryChildById",
        params: {id: id},
        method: 'GET',
        pagination: false,
        template: $("#beanTemplate").html(),
        ajaxSendAfter:function (json) {
            matchingLanguage();
            form.render();
        }
    });

    //获取的条形码是入库状态的
    tableSelect.render({
        elem: '#code',	//定义输入框input对象
        checkedKey: 'id', //表格的唯一键值，非常重要，影响到选中状态 必填
        searchKey: 'keyword',	//搜索输入框的name值 默认keyword
        searchPlaceholder: '请输入编码',	//搜索输入框的提示文字 默认关键词搜索
        where: {objectId: id},
        table: {	//定义表格参数，与LAYUI的TABLE模块一致，只是无需再定义表格elem
            url: sysMainMation.erpBasePath + 'queryInventoryChildCodeList',
            method: 'post',
            page: true,
            limits: [8, 16, 24, 32, 40, 48, 56],
            limit: 8,
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
                { field: 'codeNum', title: '条形码', width: 250, templet: function (d) {
                        return '<a rowId="' + d.id + '" class="notice-title-click">' + d.codeNum + '</a>';
                    }},
            ]]
        },
        done: function (elem, data) {
        }
    });


    form.on('submit(getBean)', function (data) {
        if(isNull($("#profitNum").val()) ){
            $("#profitNum").val(0);
        }
        if(isNull($("#lossNum").val())){
            $("#lossNum").val(0);
        }
        if (winui.verifyForm(data.elem)) {
            let params = {
                id: id,
                realNumber: $("#realNumber").val(),
                profitNum: $("#profitNum").val(),
                lossNum: $("#lossNum").val(),
                profitNormsCode: $("#profitNormsCode").val(),
                lossNormsCode: $("#lossNormsCode").val(),
            }
            AjaxPostUtil.request({
                url: sysMainMation.erpBasePath + "complateInventoryChild",
                params: params,
                type: 'json',
                method: 'POST',
                callback: function (json) {
                    parent.layer.close(index);
                    parent.refreshCode = '0';
                }
            });
        }
        return false;
    });

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});



