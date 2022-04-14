
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
	
	authBtn('1601785417437');
	// 获取当前登录员工信息
	systemCommonUtil.getSysCurrentLoginUserMation(function (data){
		staffId = data.bean.staffId;
	});
	initTable();

    function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'sysstafffamily006',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'name', title: '名称', align: 'left', width: 100 },
		        { field: 'relationshipName', title: '与本人关系', width: 100},
		        { field: 'sex', title: '性别', width: 80, templet: function(d){
                    if(d.sex == '0'){
                        return "保密";
                    }else if(d.sex == '1'){
                        return "男";
                    }else if(d.sex == '2'){
                        return "女";
                    }
                }},
		        { field: 'cardTypeName', title: '证件类型', width: 100},
		        { field: 'cardNumber', title: '证件编号', width: 150},
                { field: 'politicName', title: '政治面貌', width: 100},
                { field: 'workUnit', title: '工作单位', width: 140},
                { field: 'job', title: '职务', width: 120},
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
                { field: 'createTime', title: 'systemLanguage["com.skyeye.entryTime"][languageType]', align: 'center', width: 100},
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
    }
	
	form.render();
	
	// 录入家庭成员
	$("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/sysStaffFamily/sysStaffFamilyAdd.html",
			title: "录入家庭成员",
			pageId: "sysStaffFamilyAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	});
	
	// 编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
			url: "../../tpl/sysStaffFamily/sysStaffFamilyEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "sysStaffFamilyEdit",
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
            AjaxPostUtil.request({url:reqBasePath + "sysstafffamily005", params:{rowId: data.id}, type: 'json', method: "DELETE", callback: function(json){
                if(json.returnCode == 0){
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
                }
            }});
        });
    }

	// 刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });

    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
    	return {
    		staffId: staffId
    	};
	}
    
    exports('sysStaffFamilyMyList', {});
});
