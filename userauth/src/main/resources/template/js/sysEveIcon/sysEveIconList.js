
var rowId = "";
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form;
	
	authBtn('1552963122253');
	
	//初始化数据
    showGrid({
	 	id: "showForm",
	 	url: reqBasePath + "icon001",
	 	params: getTableParams(),
	 	pagination: true,
	 	pagesize: 18,
	 	template: getFileContent('tpl/sysEveIcon/icon-item.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 		
	 	},
	 	options: {'click .del':function(index, row){
				layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
					layer.close(index);
		            AjaxPostUtil.request({url: reqBasePath + "icon003", params:{rowId: row.id}, type: 'json', callback: function (json) {
		    			if (json.returnCode == 0) {
		    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
		    				loadTable();
		    			} else {
		    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		    			}
		    		}});
				});
	 		},'click .edit':function(index, row){
	 			edit(row);
	 		}
	 	},
	 	ajaxSendAfter:function (json) {
	 		authBtn('1552963143937');
	 		authBtn('1552963131245');
	 		matchingLanguage();
	 	}
    });
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});
	
	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysEveIcon/sysEveIconEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "sysEveIconEdit",
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
	
	// 新增
	$("body").on("click", "#addBean", function() {
		_openNewWindows({
			url: "../../tpl/sysEveIcon/sysEveIconAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "sysEveIconAdd",
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
	
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	refreshGrid("showForm", {params: getTableParams()});
    }

    function getTableParams(){
    	return {
    		iconClass: $("#iconClass").val()
    	};
	}
    
    exports('sysEveIconList', {});
});
