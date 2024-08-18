
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
	
    showGrid({
	 	id: "showForm",
	 	url: reqBasePath + "querySysIconList",
	 	params: getTableParams(),
	 	pagination: true,
	 	pagesize: 18,
	 	template: getFileContent('tpl/sysEveIcon/icon-item.tpl'),
	 	ajaxSendLoadBefore: function(hdb) {
	 	},
	 	options: {'click .del':function(index, row) {
				layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
					layer.close(index);
		            AjaxPostUtil.request({url: reqBasePath + "deleteSysIconById", params: {id: row.id}, type: 'json', method: 'DELETE', callback: function (json) {
						winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
						loadTable();
		    		}});
				});
	 		},'click .edit':function(index, row) {
	 			edit(row);
	 		}
	 	},
	 	ajaxSendAfter:function (json) {
	 		authBtn('1552963122253');
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
	function edit(data) {
		_openNewWindows({
			url: "../../tpl/sysEveIcon/writeEveIcon.html?id=" + data.id,
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "sysEveIconEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 新增
	$("body").on("click", "#addBean", function() {
		_openNewWindows({
			url: "../../tpl/sysEveIcon/writeEveIcon.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "sysEveIconAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
    	refreshGrid("showForm", {params: getTableParams()});
    }

    function getTableParams() {
    	return {
			keyword: $("#iconClass").val()
    	};
	}
    
    exports('sysEveIconList', {});
});
