var rowId = "";

var memberId = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        table = layui.table;
    authBtn('1644234244354');

    memberId = parent.rowId;

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: shopBasePath + 'memberCar001',
        where: getTableParams(),
        even: true,
        page: false,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers' },
            { field: 'plate', title: '车牌号', align: 'left', width: 100, fixed: 'left', templet: function (d) {
                return '<a lay-event="select" class="notice-title-click">' + d.plate + '</a>';
            }},
            { field: 'vinCode', title: 'VIN码', align: 'center', width: 180},
            { field: 'modelType', title: '车型', align: 'left', width: 120},
            { field: 'insure', title: '是否购买保险', align: 'center', width: 140, templet: function (d) {
                if(d.insure == '1'){
                    return "已购买";
                }else if(d.insure == '2'){
                    return "未购买";
                } else {
                    return "<span class='state-error'>参数错误</span>";
                }
            }},
            { field: 'enabled', title: '状态', align: 'center', width: 80, templet: function (d) {
                return shopUtil.getMemberCarEnableStateName(d.enabled);
            }},
            { field: 'createName', title: '录入人', align: 'left', width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 140 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
        ]],
	    done: function(json) {
	    	matchingLanguage();
	    }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
            editmember(data);
        } else if (layEvent === 'delete') { //删除
            deletemember(data);
        } else if (layEven == 'select'){ //详情
            selectMember(data)
        } else if (layEvent === 'enabled') { // 启用
            editEnabled(data);
        } else if (layEven == 'unenabled'){ // 禁用
            editNotEnabled(data)
        } else if (layEven == 'transferCar'){ // 过户
            transferCar(data)
        }
    });

    // 编辑
    function editmember(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/memberCar/memberCarEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "memberCarEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 删除
    function deletemember(data) {
        layer.confirm("删除后关联套餐以及其他信息无法使用，确认删除吗？", {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "memberCar004", params: {rowId: data.id}, type: 'json', method: "POST", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 设置启用状态
    function editEnabled(data) {
        layer.confirm('确认要更改为启用状态吗？', { icon: 3, title: '状态变更' }, function (index) {
            AjaxPostUtil.request({url: shopBasePath + "editMemberCarEnabledState", params: {rowId: data.id, enabled: shopUtil.enableState["enable"]["type"]}, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg("设置成功。", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 设置禁用状态
    function editNotEnabled(data) {
        layer.confirm('确认要更改为禁用状态吗？', { icon: 3, title: '状态变更' }, function (index) {
            AjaxPostUtil.request({url: shopBasePath + "editMemberCarEnabledState", params: {rowId: data.id, enabled: shopUtil.enableState["disable"]["type"]}, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg("设置成功。", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 详情
    function selectMember(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/memberCar/memberCarInfo.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "memberCarInfo",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }
        });
    }

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/memberCar/memberCarAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "memberCarAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 过户
    function transferCar(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/memberCar/memberCarTransfer.html",
            title: '车辆过户',
            pageId: "memberCarTransfer",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()})
        }
        return false;
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 刷新
    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        return {
            plate: $("#plate").val(),
            vinCode: $("#vinCode").val(),
            memberId: memberId
        };
    }

    exports('memberList', {});
});
