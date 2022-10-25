
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
	    
	    //副单位数量计数器
	    var unitIndex = 1;
	    //副单位模板
	    var beanTemplate = $("#beanTemplate").html();
	    var beanDataTemplate = $("#beanDataTemplate").html();
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "materialunit004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/materialUnit/materialUnitEditTemplate.tpl'),
		 	ajaxSendAfter:function (json) {
		 		
		 		$.each(json.bean.unitList, function(i, item) {
		 			if(item.baseUnit.toString() === "1"){//基础数据
		 				$("#unitName").val(item.unitNameValue);
		 			} else {
		 				addDataRow(item);
		 			}
		 		});
		 		
		 		matchingLanguage();
				form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	//获取填写副单位
						var rowTr = $("#useTable tr");
						var tableData = new Array();
						var noError = false; //循环遍历表格数据时，是否有其他错误信息
						$.each(rowTr, function(i, item) {
							var rowNum = $(item).attr("trcusid").replace("tr", "");
							var row = {
								unitName: $("#unitName" + rowNum).val(),
								unitNum: $("#unitNum" + rowNum).val(),
								unitId: $("#unitName" + rowNum).parent().parent().attr("id")
							};
							tableData.push(row);
						});
						var params = {
		        			groupName: $("#groupName").val(),
		 	        		unitName: $("#unitName").val(),
			 	        	unitNameStr: JSON.stringify(tableData),
			 	        	rowId: parent.rowId
		 	        	};
		 	        	AjaxPostUtil.request({url: flowableBasePath + "materialunit005", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
		 	        	}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
		
		//基本单位变化事件
 	    $("body").on("keyup", "#unitName", function() {
			$(".baseNameShow").html($(this).val());
		});
 	    
 	    //新增行
		$("body").on("click", "#addRow", function() {
			addRow();
		});

		//删除行
		$("body").on("click", "#deleteRow", function() {
			deleteRow();
		});
		
		//添加副单位模板
 	    function addRow(){
 	    	var j = {
 	    		trId: "tr" + unitIndex.toString(), //行的id
 	    		id: "row" + unitIndex.toString(), //checkbox的id
 	    		unitName: "unitName" + unitIndex.toString(), //单位名称
 	    		unitNum: "unitNum" + unitIndex.toString(), //数量
 	    		baseName: $("#unitName").val() //基础单位名称
 	    	};
 	    	$("#useTable").append(getDataUseHandlebars(beanTemplate, j));
 	    	form.render('checkbox');
 	    	unitIndex++;
 	    }
 	    
 	    //添加副单位数据回显
 	    function addDataRow(item) {
 	    	var j = {
 	    		trId: "tr" + unitIndex.toString(), //行的id
 	    		id: "row" + unitIndex.toString(), //checkbox的id
 	    		unitName: "unitName" + unitIndex.toString(), //单位名称
 	    		unitNum: "unitNum" + unitIndex.toString(), //数量
 	    		baseName: $("#unitName").val(), //基础单位名称
 	    		unitIdValue: item.unitIdValue, //数据
 	    		unitNameValue: item.unitNameValue, //数据
 	    		unitNumValue: item.unitNumValue //数据
 	    	};
 	    	$("#useTable").append(getDataUseHandlebars(beanDataTemplate, j));
 	    	form.render('checkbox');
 	    	unitIndex++;
 	    }
 	    
 	    //删除行
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
	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});