
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'soulTable'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        table = layui.table,
        soulTable = layui.soulTable;
    var selTemplate = getFileContent('tpl/template/select-option.tpl');
    //门店会员
    // 加载当前用户所属门店
    AjaxPostUtil.request({url: sysMainMation.shopBasePath + "storeStaff005", params: {}, type: 'json', method: "GET", callback: function(json) {
            $("#storeId").html(getDataUseHandlebars(selTemplate, json));
            form.render('select');
            initTable();
        }, async: false});



    var storeId = "";
    form.on('select(storeId)', function(data) {
        var thisRowValue = data.value;
        storeId = isNull(thisRowValue) ? "" : thisRowValue;
        loadTable();
    });

    function initTable(){
        table.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'post',
            url: sysMainMation.shopBasePath + 'member001',
            where: getTableParams(),
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            overflow: {
                type: 'tips',
                hoverTime: 300, // 悬停时间，单位ms, 悬停 hoverTime 后才会显示，默认为 0
                minWidth: 150, // 最小宽度
                maxWidth: 500 // 最大宽度
            },
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
                { field: 'name', title: '会员姓名', align: 'left', width: 250 },
                { field: 'phone', title: '联系电话', align: 'left', width: 250 },
                { field: 'email', title: '邮箱', align: 'left', width: 250 },
                { field: 'remark', title: '备注', align: 'left', width: 450 },
            ]],
            done: function(json) {
                matchingLanguage();
                soulTable.render(this);

                initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入会员姓名", function () {
                    table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
                }, `<label class="layui-form-label">门店</label><div class="layui-input-inline">
						<select id="storeId" name="storeId" lay-filter="storeId" lay-search="">
					</select></div>`);
            }
        });
    }

    form.render();
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        let params = {
            holderId: storeId,
        };
        return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('storeMembersList', {});
});
