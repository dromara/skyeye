
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
    var $ = layui.$,
	    form = layui.form;
	var noteId = parent.noteId;
	matchingLanguage();
	form.render();

	// 显示编辑器内容
	AjaxPostUtil.request({url:reqBasePath + "mynote008", params: {rowId: noteId}, type: 'json', callback: function(json){
		if(json.returnCode == 0){
			parent.$("#noteTitle").val(json.bean.title);
			var param = {
				container: 'luckysheet',
				showtoolbar: true,
				showConfigWindowResize: true, // 图表和数据透视表的配置会在右侧弹出，设置弹出后表格是否会自动缩进
				showinfobar: false, // 是否显示顶部名称栏
				lang: 'zh',
				hook: {
					updated: function (){
						parent.$("#editMyNote").addClass('select');
					}
				}
			};
			if(!isNull(json.bean.content)){
				param["data"] = JSON.parse(json.bean.content);
			}
			luckysheet.create(param);
		}else{
			winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		}
	}});

	$(document).bind("keydown", function(e) {
		if (e.ctrlKey && (e.which == 83)) {
			e.preventDefault();
			parent.$("#editMyNote").click();
			return false;
		}
	});
	
	// 获取编辑器内容
	window.getContent = function(){
		var allSheetData = luckysheet.getluckysheetfile();
		$.each(allSheetData, function(i, item){
			item["celldata"] = getCellData(item.data);
		});
		return JSON.stringify(allSheetData);
	}
	
	// 获取纯文本内容
	window.getNoHtmlContent = function(){
		return sysMainMation.mationTitle + ' Excel笔记';
	}
	
	function getCellData(downOriginData){
		var arr = [];  // 所有的单元格数据组成的二维数组
		// 获取二维数组
		for (var row = 0; row < downOriginData.length; row++) {
		    for (var col = 0; col < downOriginData[row].length; col++) {
		        if (!isNull(downOriginData[row][col])) {
		        	arr.push({
		        		r: row,
		        		c: col,
		        		v: downOriginData[row][col]
		        	});
		        }
		    }
		}
		return arr;
	}
});
