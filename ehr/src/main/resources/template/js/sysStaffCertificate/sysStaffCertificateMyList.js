
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
	
	authBtn('1601784150851');
	AjaxPostUtil.request({url: reqBasePath + "login002", params: {}, type: 'json', callback: function(json) {
		if(json.returnCode == 0) {
			staffId = json.bean.staffId;
			initTable();
		} else {
			winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		}
	}});
		
    function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'sysstaffcertificate006',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'certificateNumber', title: '证书编号', align: 'left', width: 160 },
		        { field: 'certificateName', title: '证书名称', width: 120},
		        { field: 'certificateTypeName', title: '证书类型', width: 120},
                { field: 'issueOrgan', title: '签发机构', width: 120},
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
                { field: 'validityType', title: '有效期类型', align: 'left', width: 100, templet: function(d){
                    if(d.validityType == '1'){
                        return "永久有效";
                    }else if(d.validityType == '2'){
                        return "时间段有效";
                    }
                }},
                { field: 'issueTime', title: '签发时间', align: 'center', width: 100 },
                { field: 'validityTime', title: '截至时间', align: 'center', width: 100 },
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
    }
	
	form.render();
	
	// 录入证书
	$("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/sysStaffCertificate/sysStaffCertificateAdd.html",
			title: "录入证书",
			pageId: "sysStaffCertificateAdd",
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
			url: "../../tpl/sysStaffCertificate/sysStaffCertificateEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "sysStaffCertificateEdit",
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
            AjaxPostUtil.request({url:reqBasePath + "sysstaffcertificate005", params:{rowId: data.id}, type:'json', method: "DELETE", callback:function(json){
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
    
    exports('sysStaffCertificateMyList', {});
});
