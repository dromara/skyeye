
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

		skyeyeClassEnumUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", '', form);
		initTable();
		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
				var result = initTableChooseUtil.getDataList('fieldList');
				if (!result.checkResult) {
					return false;
				}
				var tableData = [].concat(result.dataList);
				$.each(tableData, function(i, item) {
					var thisRowKey = item["trcusid"].replace("tr", "");
					item['fieldKey'] = $("#fieldId" + thisRowKey).attr('rowKey');
				});
				var params = {
					name: $("#name").val(),
					jobId: parent.jobId,
					enabled: $("#enabled input:radio:checked").val(),
					scoreFields: JSON.stringify(tableData)
 	        	};
 	        	AjaxPostUtil.request({url: reqBasePath + "writeCompanyJobScoreMation", params: params, type: 'json', method: 'POST', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
 	        	}});
 	        }
 	        return false;
 	    });

		function initTable() {
			initTableChooseUtil.initTable({
				id: "fieldList",
				cols: [
					{id: 'fieldId', title: '字段', formType: 'chooseInput', width: '120', iconClassName: 'chooseFieldBtn', verify: 'required'},
					{id: 'minMoney', title: '最小薪资范围', formType: 'input', width: '100', verify: 'required|money', value: '0'},
					{id: 'maxMoney', title: '最大薪资范围', formType: 'input', width: '100', verify: 'required|money', value: '0'},
					{id: 'remark', title: '备注', formType: 'input', width: '100'}
				],
				deleteRowCallback: function (trcusid) {
				},
				addRowCallback: function (trcusid) {
				},
				form: form
			});
		}

		// 字段选择
		$("body").on("click", ".chooseFieldBtn", function (e) {
			var trId = $(this).parent().parent().attr("trcusid");
			_openNewWindows({
				url: "../../tpl/wagesFieldType/wagesFieldTypeChoose.html",
				title: "选择薪资字段",
				pageId: "wagesFieldTypeChoose",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
					// 获取表格行号
					var thisRowNum = trId.replace("tr", "");
					// 表格名称赋值
					$("#fieldId" + thisRowNum.toString()).val(fieldMation.name + '(' + fieldMation.key + ')');
					$("#fieldId" + thisRowNum.toString()).attr("rowKey", fieldMation.key);
				}});
		});
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});