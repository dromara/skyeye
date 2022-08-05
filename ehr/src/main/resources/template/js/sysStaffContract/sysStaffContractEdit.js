
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
	    
	    showGrid({
		 	id: "showForm",
		 	url: sysMainMation.ehrBasePath + "sysstaffcontract003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function (json) {
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

				// 附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

				systemCommonUtil.getSysCompanyList(function(data){
					// 加载企业数据
					$("#companyId").html(getDataUseHandlebars(selTemplate, data));
					$("#companyId").val(json.bean.companyId);
				});

				// 合同类别
				sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["employeeContractCategory"]["key"], 'select', "typeId", json.bean.typeId, form);

				// 合同类型
				sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["employeeContractType"]["key"], 'select', "moldId", json.bean.moldId, form);

			    matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
						var params = {
		        			contractNumber: $("#contractNumber").val(),
		 	        		typeId: $("#typeId").val(),
		 	        		moldId: $("#moldId").val(),
		 	        		startTime: $("#startTime").val(),
		 	        		endTime: $("#endTime").val(),
			 	        	rowId: parent.rowId,
							enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
							companyId: $("#companyId").val()
		 	        	};
		 	        	AjaxPostUtil.request({url: sysMainMation.ehrBasePath + "sysstaffcontract004", params: params, type: 'json', method: "PUT", callback: function (json) {
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