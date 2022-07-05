
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
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sysstafffamily003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function (json) {
		 		$("input:radio[name=userSex][value=" + json.bean.sex + "]").attr("checked", true);
			    $("#emergencyContact").val(json.bean.emergencyContact);

				// 家庭成员关系
				sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["employeeFamilySituation"]["key"], 'select', "relationshipId", json.bean.relationshipId, form);

			    // 证件类型
				sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["employeeDocumentType"]["key"], 'select', "cardType", json.bean.cardType, form);

			    // 政治面貌
				sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["employeesPoliticalOutlook"]["key"], 'select', "politicId", json.bean.politicId, form);

			    matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
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
			 	        	rowId: parent.rowId
		 	        	};
		 	        	
		 	        	AjaxPostUtil.request({url: reqBasePath + "sysstafffamily004", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
		 	        	}});
		 	        }
		 	        return false;
		 	    });
		 		form.render();
		 	}
	    });
	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});