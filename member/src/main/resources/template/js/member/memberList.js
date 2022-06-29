var rowId = "";

var memberMation = {};

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
    authBtn('1569133228443');

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
                return shopUtil.getEnableStateName(d.enabled);
            }},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 300, toolbar: '#tableBar'}
        ]],
	    done: function(){
	    	matchingLanguage();
	    }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
            editmember(data);
        }else if (layEvent === 'delete') { //删除
            deletemember(data);
        }else if (layEvent === 'enabled') { //启用
            editEnabled(data);
        }else if(layEvent == 'unenabled'){ //禁用
            editNotEnabled(data)
        }else if(layEvent == 'select'){ //详情
            selectMember(data)
        }else if(layEvent == 'memberCar'){ //车辆信息
            memberCar(data)
        }else if(layEvent == 'mealList'){ //套餐购买信息
            mealList(data)
        }
    });

    // 编辑
    function editmember(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/member/memberEdit.html",
            title: "编辑会员",
            pageId: "memberEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
            }});
    }

    // 删除会员
    function deletemember(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "member004", params: {rowId: data.id}, type: 'json', method: "POST", callback: function (json) {
                if (json.returnCode == 0) {
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 设置启用状态
    function editEnabled(data){
        layer.confirm('确认要更改该会员为启用状态吗？', { icon: 3, title: '状态变更' }, function (index) {
            AjaxPostUtil.request({url: shopBasePath + "member006", params: {rowId: data.id}, type: 'json', method: "PUT", callback: function (json) {
                if (json.returnCode == 0) {
                    winui.window.msg("设置成功。", {icon: 1, time: 2000});
                    loadTable();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }
    
    // 设置禁用状态
    function editNotEnabled(data){
        layer.confirm('确认要更改该会员为禁用状态吗？', { icon: 3, title: '状态变更' }, function (index) {
            AjaxPostUtil.request({url: shopBasePath + "member007", params: {rowId: data.id}, type: 'json', method: "PUT", callback: function (json) {
                if (json.returnCode == 0) {
                    winui.window.msg("设置成功。", {icon: 1, time: 2000});
                    loadTable();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }
	
    // 详情
    function selectMember(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/member/memberInfo.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "memberinfo",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }
        });
    }

    // 车辆信息
    function memberCar(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/memberCar/memberCarList.html",
            title: '车辆信息',
            pageId: "memberCarList",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }
        });
    }

    // 套餐购买信息
    function mealList(data){
        rowId = data.id;
        memberMation = data;
        _openNewWindows({
            url: "../../tpl/mealOrder/memberMealOrderList.html",
            title: '套餐购买信息',
            pageId: "memberMealOrderList",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }
        });
    }

    //添加会员
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/member/memberAdd.html",
            title: "新增会员",
            pageId: "memberAdd",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
            }});
    });

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            table.reload("messageTable", {page: {curr: 1}, where: getTableParams()})
        }
        return false;
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 刷新
    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
        var contacts = $("#contacts").val();
        var phone = $("#phone").val();
        var email = $("#email").val();
        var vinCode = $("#vinCode").val()
        if(isNull(contacts) && isNull(phone) && isNull(email) && isNull(vinCode)){
            contacts = "!-!!@#$%^&*";
        }
        return {
            contacts: contacts,
            phone: phone,
            email: email,
            vinCode: vinCode
        };
    }

    exports('memberList', {});
});
