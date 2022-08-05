
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
	
	authBtn('1599919580170');
	
	// 员工无在档列表
    initTable();
    function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: sysMainMation.ehrBasePath + 'sysstaffarchives004',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
                { field: 'jobNumber', title: '员工工号', align: 'left', width: 80 },
                { field: 'userName', title: '员工姓名', align: 'left', width: 100 },
                { field: 'companyName', title: '所属企业', align: 'left', width: 150 },
                { field: 'departmentName', title: '所属部门', align: 'left', width: 150 },
                { field: 'jobName', title: '所属岗位', align: 'left', width: 150 },
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
                { field: 'entryTime', title: '入职时间', align: 'center', width: 100 },
                { field: 'workTime', title: '工作时间', align: 'center', width: 100},
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 100, toolbar: '#tableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'add') { // 新增
	        	add(data);
	        }
	    });
    }
	
	form.render();
	
	// 新增
    function add(data){
        staffId = data.id;
        _openNewWindows({
			url: "../../tpl/sysStaffArchives/sysStaffArchivesAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "sysStaffArchivesAdd",
            area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}

    // 搜索表单
    $("body").on("click", "#formSearch", function() {
        table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    });

	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });

    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
    	return {
			userName: $("#userName").val(),
			jobNumber: $("#jobNumber").val()
    	};
	}
    
    exports('sysStaffNoArchivesList', {});
});
