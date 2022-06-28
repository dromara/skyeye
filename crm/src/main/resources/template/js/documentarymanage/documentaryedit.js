
// 商机管理
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate,
	    	textool = layui.textool;

	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "documentary003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/documentarymanage/documentaryeditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter: function (json) {
		 		textool.init({eleId: 'detail', maxlength: 200});
		 		
		 		// 跟单时间
				laydate.render({elem : '#documentaryTime', type : 'datetime', trigger : 'click'});

				// 获取已上线的跟单分类列表
				sysCustomerUtil.queryCrmDocumentaryTypeIsUpList(function (data){
					$("#typeId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), data));
					$("#typeId").val(json.bean.typeId);
					form.render('select');
					opportunityFrom();
				});

				//商机
				function opportunityFrom(){
					showGrid({
					 	id: "opportunityId",
					 	url: flowableBasePath + "opportunity008",
					 	params: {},
					 	pagination: false,
					 	template: getFileContent('tpl/template/select-option.tpl'),
					 	ajaxSendLoadBefore: function(hdb){
					 	},
					 	ajaxSendAfter:function(j){
					 		$("#opportunityId").val(json.bean.opportunityId);
					 		form.render('select');
					 	}
					});
				}
				// 附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

 	        	matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		 	        		rowId: parent.rowId,
	 	        			detail: $("#detail").val(),
	 	        			documentaryTime: $("#documentaryTime").val(),
							typeId: $("#typeId").val(),
							opportunityId: $("#opportunityId").val(),
							enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
	 	 	        	};
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "documentary004", params: params, type: 'json', callback: function (json) {
	 		 	   			if (json.returnCode == 0){
	 			 	   			parent.layer.close(index);
	 			 	        	parent.refreshCode = '0';
	 		 	   			} else {
	 		 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 		 	   			}
	 		 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});