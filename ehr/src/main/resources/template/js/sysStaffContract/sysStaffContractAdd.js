
// 员工合同
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
	    
	    // 开始时间
	    var insStart = laydate.render({ 
 			elem: '#startTime',
 	 		trigger: 'click',
 	 		done: function(value, date){
 	 			insEnd.config.min = lay.extend({}, date, {
 	 				month: date.month - 1
 	 			});
 	 			insEnd.config.elem[0].focus();
 	 		}
 		});
	    
	    // 结束时间
	    var insEnd = laydate.render({ 
 			elem: '#endTime',
 	 		trigger: 'click',
 	 		done: function(value, date){
 	 			insStart.config.max = lay.extend({}, date, {
 	 				month: date.month - 1
 	 			});
 	 		}
 		});

		// 合同类别
		sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["employeeContractCategory"]["key"], 'select', "typeId", '', form);

		// 合同类型
		sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["employeeContractType"]["key"], 'select', "moldId", '', form);

		systemCommonUtil.getSysCompanyList(function (json) {
			// 加载企业数据
			$("#companyId").html(getDataUseHandlebars(selTemplate, json));
		});

		skyeyeEnclosure.init('enclosureUpload');
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
				var params = {
        			contractNumber: $("#contractNumber").val(),
 	        		typeId: $("#typeId").val(),
 	        		moldId: $("#moldId").val(),
 	        		startTime: $("#startTime").val(),
 	        		endTime: $("#endTime").val(),
	 	        	staffId: parent.staffId,
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
					companyId: $("#companyId").val()
 	        	};
 	        	AjaxPostUtil.request({url: sysMainMation.ehrBasePath + "sysstaffcontract002", params: params, type: 'json', method: "POST", callback: function (json) {
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