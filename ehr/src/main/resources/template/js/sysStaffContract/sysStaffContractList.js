
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
        url: reqBasePath + 'sysstaffcontract001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'contractNumber', title: '合同编号', align: 'left', width: 160 },
            { field: 'companyName', title: '签属企业', width: 180},
            { field: 'startTime', title: '开始日期', align: 'center', width: 100},
            { field: 'endTime', title: '结束日期', align: 'center', width: 100},
            { field: 'typeName', title: '合同类别', width: 120},
            { field: 'moldName', title: '合同类型', width: 120},
            { field: 'contractState', title: '状态', align: 'left', width: 80, templet: function(d){
                if(d.contractState == '1'){
                    return "待签约";
                }else if(d.contractState == '2'){
                    return "执行中";
                }else if(d.contractState == '3'){
                    return "过期";
                }
            }},
            { field: 'jobNumber', title: '员工工号', align: 'left', width: 80 },
            { field: 'userName', title: '员工姓名', align: 'left', width: 100 },
            { field: 'state', title: '员工状态', align: 'center', width: 80, templet: function(d){
                if(d.state == '1'){
                    return "在职";
                }else if(d.state == '2'){
                    return "离职";
                }else if(d.state == '3'){
                    return "见习";
                }else if(d.state == '4'){
                    return "试用";
                }else if(d.state == '5'){
                    return "退休";
                }
            }},
            { field: 'createTime', title: '录入时间', align: 'center', width: 100},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 100, toolbar: '#tableBar'}
        ]],
        done: function(){
            matchingLanguage();
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { // 编辑
            edit(data);
        }else if (layEvent === 'delete') { // 删除
            deleteRow(data);
        }
    });

	form.render();
	
	// 编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
			url: "../../tpl/sysStaffContract/sysStaffContractEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "sysStaffContractEdit",
            area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}
		});
	}
	
	// 删除
    function deleteRow(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "sysstaffcontract005", params:{rowId: data.id}, type:'json', method: "DELETE", callback:function(json){
                if(json.returnCode == 0){
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
                }
            }});
        });
    }

    // 搜索表单
    $("body").on("click", "#formSearch", function(){
        table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    });

	// 刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });

    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
    	return {
    		contractNumber: $("#contractNumber").val(),
			userName: $("#userName").val(),
			jobNumber: $("#jobNumber").val(),
			state: $("#state").val()
    	};
	}
    
    exports('sysStaffContractList', {});
});
