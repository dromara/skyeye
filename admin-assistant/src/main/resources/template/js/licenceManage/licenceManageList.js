
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
	
	// 新增证照
	authBtn('1566464868733');
	
	showList();
	// 证照列表
	function showList(){
		table.render({
		    id: 'licencelistTable',
		    elem: '#licencelistTable',
		    method: 'post',
		    url: flowableBasePath + 'licence001',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'licenceName', title: '证照名称', width: 170, templet: function (d) {
		        	return '<a lay-event="details" class="notice-title-click">' + d.licenceName + '</a>';
		        }},
		        { field: 'companyName', title: '所属公司', align: 'left', width: 150 },
		        { field: 'issueTime', title: '签发日期', align: 'center', width: 120 },
		        { field: 'nextAnnualReview', title: '下次年审时间', align: 'center', width: 120 },
		        { field: 'termOfValidityTime', title: '有效期至', align: 'center', width: 120 },
		        { field: 'licenceAdmin', title: '管理人', align: 'center', width: 100 },
		        { field: 'borrowName', title: '借用人', align: 'center', width: 100 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#licencelisttableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}
	
	table.on('tool(licencelistTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { //详情
        	details(data);
        }else if (layEvent === 'delet'){ //删除
        	delet(data);
        }else if (layEvent === 'edit'){	//编辑
        	edit(data);
        }
    });
	
	form.render();
	
	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/licenceManage/licenceManageDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "licencedetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 删除
	function delet(data){
		var msg = '确认删除该证照吗？';
		layer.confirm(msg, { icon: 3, title: '删除证照' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "licence003", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

	// 新增证照
	$("body").on("click", "#licencelistaddBean", function() {
    	_openNewWindows({
			url: "../../tpl/licenceManage/licenceManageAdd.html", 
			title: "新增证照",
			pageId: "licenceManageAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/licenceManage/licenceManageEdit.html", 
			title: "编辑证照",
			pageId: "licenceManageEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
    $("body").on("click", "#reloadlicencelistTable", function() {
    	loadTable();
    });
    
    // 搜索表单
	$("body").on("click", "#licencelistformSearch", function() {
    	table.reload("licencelistTable", {page: {curr: 1}, where: getTableParams()});
	});
    
    function loadTable(){
    	table.reload("licencelistTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		licenceName: $("#licenceName").val()
    	};
    }
    
    exports('licenceManageList', {});
});
