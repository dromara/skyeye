var rowId = "";

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

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: shopBasePath + 'member001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers'},
            { field: 'contacts', title: '会员称呼', align: 'left', width: 140, fixed: 'left', templet: function (d) {
                return '<a lay-event="select" class="notice-title-click">' + d.contacts + '</a>';
            }},
            { field: 'phone', title: '联系电话', align: 'center', width: 100},
            { field: 'email', title: '电子邮箱', align: 'left', width: 120},
            { field: 'address', title: '地址', align: 'left', width: 100},
            { field: 'enabled', title: '状态', align: 'center', width: 80, templet: function (d) {
                if(d.enabled == '1'){
                    return "<span class='state-up'>启用</span>";
                }else if(d.enabled == '2'){
                    return "<span class='state-down'>禁用</span>";
                } else {
                    return "<span class='state-error'>参数错误</span>";
                }
            }},
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 140 }
        ]],
	    done: function(){
	    	matchingLanguage();
	    }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if(layEvent == 'select'){ //详情
            selectMember(data)
        }
    });

    // 详情
    function selectMember(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/member/memberInfo.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "memberinfo",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }
        });
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
            contacts: $("#contacts").val(),
            phone: $("#phone").val(),
            email: $("#email").val(),
            enabled: $("#enabled").val(),
            vinCode: $("#vinCode").val()
        };
    }

    exports('allMemberList', {});
});
