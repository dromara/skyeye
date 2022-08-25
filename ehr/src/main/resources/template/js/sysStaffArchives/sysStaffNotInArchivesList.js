
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
	
	// 员工不在档列表
    initTable();
    function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: sysMainMation.ehrBasePath + 'sysstaffarchives003',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
		        { field: 'archivesNumber', title: '档案编号', align: 'left', width: 160 },
		        { field: 'companyName', title: '管理单位', width: 150 },
		        { field: 'custodyPlace', title: '档案保管地', width: 150 },
		        { field: 'archivesCenter', title: '档案室', width: 150 },
		        { field: 'educationName', title: '档案学历', width: 120},
                { field: 'archivesState', title: '状态', width: 60, align: 'center', templet: function (d) {
                    if(d.archivesState == '1'){
                        return "<span class='state-new'>有效</span>";
                    }else if(d.archivesState == '2'){
                        return "<span class='state-down'>失效</span>";
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
                { field: 'archivesTime', title: '入档时间', align: 'center', width: 100 },
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
	        if (layEvent === 'edit') { // 编辑
	        	edit(data);
	        }
	    });
    }
	
	form.render();
	
	// 编辑
    function edit(data) {
        rowId = data.id;
        _openNewWindows({
			url: "../../tpl/sysStaffArchives/sysStaffArchivesEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "sysStaffArchivesEdit",
            area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}

    // 搜索表单
    $("body").on("click", "#formSearch", function() {
        table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    });

	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });

    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
    	return {
    		archivesNumber: $("#archivesNumber").val(),
			archivesState: $("#archivesState").val(),
			userName: $("#userName").val(),
			jobNumber: $("#jobNumber").val()
    	};
	}
    
    exports('sysStaffNotInArchivesList', {});
});
