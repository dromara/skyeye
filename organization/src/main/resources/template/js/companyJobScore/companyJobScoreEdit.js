
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
		var rowNum = 1; //表格的序号
		var usetableTemplate = $("#usetableTemplate").html();
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "companyjobscore003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
			method: "GET",
		 	template: $("#showBaseTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){

				// 加载列表项
				$.each(json.bean.modelField, function(i, item){
					addRow();
					$("#fieldId" + (rowNum - 1)).val(item.nameCn + '(' + item.key + ')');
					$("#fieldId" + (rowNum - 1)).attr("rowKey", item.fieldKey);
					$("#minMoney" + (rowNum - 1)).val(item.minMoney);
					$("#maxMoney" + (rowNum - 1)).val(item.maxMoney);
					$("#remark" + (rowNum - 1)).val(item.remark);
				});

				matchingLanguage();
				form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
						// 获取已选字段数据
						var rowTr = $("#useTable tr");
						var tableData = new Array();
						$.each(rowTr, function(i, item) {
							// 获取行编号
							var rowNum = $(item).attr("trcusid").replace("tr", "");
							var row = {
								fieldKey: $("#fieldId" + rowNum).attr("rowKey"),
								minMoney: $("#minMoney" + rowNum).val(),
								maxMoney: $("#maxMoney" + rowNum).val(),
								sortNo: (i + 1),
								remark: $("#remark" + rowNum).val()
							};
							tableData.push(row);
						});
						var params = {
							nameCn: $("#nameCn").val(),
							nameEn: $("#nameEn").val(),
							rowId: parent.rowId,
							jobId: parent.jobId,
							fieldStr: JSON.stringify(tableData)
		 	        	};
		 	        	AjaxPostUtil.request({url:reqBasePath + "companyjobscore004", params: params, type: 'json', method: "PUT", callback: function(json){
		 	        		if (json.returnCode == 0) {
		 	        			parent.layer.close(index);
		 	        			parent.refreshCode = '0';
		 	        		}else{
		 	        			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		 	        		}
		 	        	}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});

		// 新增行
		$("body").on("click", "#addRow", function() {
			addRow();
		});

		// 删除行
		$("body").on("click", "#deleteRow", function() {
			deleteRow();
		});

		// 新增行
		function addRow() {
			var par = {
				id: "row" + rowNum.toString(), //checkbox的id
				trId: "tr" + rowNum.toString(), //行的id
				fieldId: "fieldId" + rowNum.toString(), //字段id
				minMoney: "minMoney"  + rowNum.toString(), //最小薪资范围id
				maxMoney: "maxMoney" + rowNum.toString(), //最大薪资范围id
				remark: "remark" + rowNum.toString() //备注id
			};
			$("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
			form.render('select');
			form.render('checkbox');
			rowNum++;
		}

		// 删除行
		function deleteRow() {
			var checkRow = $("#useTable input[type='checkbox'][name='tableCheckRow']:checked");
			if(checkRow.length > 0) {
				$.each(checkRow, function(i, item) {
					$(item).parent().parent().remove();
				});
			} else {
				winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
			}
		}

		// 字段选择
		$("body").on("click", ".chooseFieldBtn", function(e){
			var trId = $(this).parent().parent().attr("trcusid");
			_openNewWindows({
				url: "../../tpl/wagesFieldType/wagesFieldTypeChoose.html",
				title: "选择薪资字段",
				pageId: "productlist",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						// 获取表格行号
						var thisRowNum = trId.replace("tr", "");
						// 表格名称赋值
						$("#fieldId" + thisRowNum.toString()).val(fieldMation.nameCn + '(' + fieldMation.key + ')');
						$("#fieldId" + thisRowNum.toString()).attr("rowKey", fieldMation.key);
					} else if (refreshCode == '-9999') {
						winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
					}
				}});
		});

	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});