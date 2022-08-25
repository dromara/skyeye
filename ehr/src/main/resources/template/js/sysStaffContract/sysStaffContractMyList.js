
var rowId = "";

var staffId = "";

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

    // 获取当前登录员工信息
    systemCommonUtil.getSysCurrentLoginUserMation(function (data) {
        staffId = data.bean.staffId;
    });
    initTable();

    function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: sysMainMation.ehrBasePath + 'sysstaffcontract006',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
		        { field: 'contractNumber', title: '合同编号', align: 'left', width: 160 },
                { field: 'companyName', title: '签属企业', width: 180},
		        { field: 'startTime', title: '开始日期', align: 'center', width: 100},
		        { field: 'endTime', title: '结束日期', align: 'center', width: 100},
                { field: 'typeName', title: '合同类别', width: 120},
                { field: 'moldName', title: '合同类型', width: 120},
                { field: 'contractState', title: '状态', align: 'left', width: 80, templet: function (d) {
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
                { field: 'state', title: '员工状态', align: 'center', width: 80, templet: function (d) {
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
                { field: 'createTime', title: systemLanguage["com.skyeye.entryTime"][languageType], align: 'center', width: 100},
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 100, toolbar: '#tableBar'}
		    ]],
		    done: function(json) {
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'signUp') { // 签约
	        	signUp(data);
	        }
	    });
    }
	
	form.render();
	
	// 签约
    function signUp(data) {
        layer.confirm('确认签约该合同吗？', { icon: 3, title: '签约操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.ehrBasePath + "sysstaffcontract007", params: {rowId: data.id}, type: 'json', method: "GET", callback: function (json) {
                winui.window.msg("签约成功", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });

    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
    	return {
    		staffId: staffId
    	};
	}
    
    exports('sysStaffContractMyList', {});
});
