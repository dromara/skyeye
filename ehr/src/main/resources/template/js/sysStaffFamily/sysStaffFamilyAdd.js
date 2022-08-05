
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
			textool = layui.textool;
	    
	    // 家庭成员关系
		sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["employeeFamilySituation"]["key"], 'select', "relationshipId", '', form);

		// 证件类型
		sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["employeeDocumentType"]["key"], 'select', "cardType", '', form);

		// 政治面貌
		sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["employeesPoliticalOutlook"]["key"], 'select', "politicId", '', form);

	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
				var params = {
        			name: $("#name").val(),
 	        		sex: $("input[name='userSex']:checked").val(),
 	        		relationshipId: $("#relationshipId").val(),
 	        		cardType: $("#cardType").val(),
 	        		cardNumber: $("#cardNumber").val(),
 	        		politicId: $("#politicId").val(),
 	        		workUnit: $("#workUnit").val(),
 	        		job: $("#job").val(),
 	        		emergencyContact: $("#emergencyContact").val(),
	 	        	staffId: parent.staffId
 	        	};
 	        	
 	        	AjaxPostUtil.request({url: sysMainMation.ehrBasePath + "sysstafffamily002", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
 	        	}});
 	        }
 	        return false;
 	    });
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});