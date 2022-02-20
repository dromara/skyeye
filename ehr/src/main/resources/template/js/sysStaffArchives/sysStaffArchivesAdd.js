
// 员工档案资料
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate,
			textool = layui.textool;
	    // 下拉框模板
	    var selTemplate = getFileContent('tpl/template/select-option.tpl');
	    
	    laydate.render({ 
 			elem: '#archivesTime',
 	 		trigger: 'click'
 		});
	    
	    textool.init({
	    	eleId: 'remark',
	    	maxlength: 200,
	    	tools: ['count', 'copy', 'reset']
	    });
	    
		showGrid({
		 	id: "educationId",
		 	url: reqBasePath + "sysstaffdatadictionary008",
		 	params: {typeId: 2},
		 	pagination: false,
		 	template: selTemplate,
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
		 		form.render('select');
		 	}
	    });

		systemCommonUtil.getSysCompanyList(function(json){
			// 加载企业数据
			$("#managementUnit").html(getDataUseHandlebars(selTemplate, json));
		});

		skyeyeEnclosure.init('enclosureUpload');
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
				var params = {
        			archivesNumber: $("#archivesNumber").val(),
 	        		managementUnit: $("#managementUnit").val(),
 	        		archivesCenter: $("#archivesCenter").val(),
 	        		custodyPlace: $("#custodyPlace").val(),
 	        		educationId: $("#educationId").val(),
 	        		archivesTime: $("#archivesTime").val(),
 	        		whetherArchives: $("#whetherArchives").val(),
	 	        	archivesState: $("#archivesState").val(),
	 	        	remark: $("#remark").val(),
	 	        	staffId: parent.staffId,
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 	        	};
 	        	AjaxPostUtil.request({url:reqBasePath + "sysstaffarchives005", params: params, type:'json', callback:function(json){
 	        		if(json.returnCode == 0){
 	        			parent.layer.close(index);
 	        			parent.refreshCode = '0';
 	        		}else{
 	        			winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
 	        		}
 	        	}});
 	        }
 	        return false;
 	    });
 	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});