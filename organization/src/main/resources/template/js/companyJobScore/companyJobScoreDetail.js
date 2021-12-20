
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
		 	url: reqBasePath + "companyjobscore009",
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
					$("#fieldId" + (rowNum - 1)).html(item.nameCn + '(' + item.key + ')');
					$("#minMoney" + (rowNum - 1)).html(item.minMoney);
					$("#maxMoney" + (rowNum - 1)).html(item.maxMoney);
					$("#remark" + (rowNum - 1)).html(item.remark);
				});

				matchingLanguage();
				form.render();
		 	}
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

	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});