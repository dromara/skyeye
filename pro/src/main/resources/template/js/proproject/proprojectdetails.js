
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "proproject005",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/proproject/proprojectdetailsTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb) {
		 	},
		 	ajaxSendAfter: function (json) {
				// 获取当前登录员工信息
				systemCommonUtil.getSysCurrentLoginUserMation(function (data) {
					$("#orderDetailTitle").html(data.bean.companyName + '项目详情信息');
				});

				// 附件回显
				skyeyeEnclosure.showDetails({
					"businessEnclosureInfoListBox": json.bean.businessEnclosureInfoList, // 业务需求和目标的附件回显
					"projectEnclosureInfoListBox": json.bean.projectEnclosureInfoList, // 项目组织和分工的附件回显
					"planEnclosureInfoListBox": json.bean.planEnclosureInfoList, // 实施计划和方案的附件回显
					"resultsEnclosureInfoListBox": json.bean.resultsEnclosureInfoList, // 项目成果和总结的附件回显
				});

 	        	matchingLanguage();
		 		form.render();
		 	}
		});
	    
	});
});