
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;

	showList();
	// 我的用品领用历史列表
	function showList(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'myhasmation004',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '3', type: 'numbers'},
				{ field: 'companyName', title: '所属公司', align: 'left', rowspan: '3', width: 170 },
				{ field: 'articlesName', title: '用品名称', align: 'left', rowspan: '3', width: 120 },
				{ field: 'articlesNum', title: '用品编号', align: 'left', rowspan: '3', width: 200 },
				{ field: 'specifications', title: '用品规格', align: 'left', rowspan: '3', width: 80 },
				{ field: 'typeName', title: '用品类型', align: 'left', rowspan: '3', width: 120 },
				{ field: 'applyUseNum', title: '领用数量', align: 'left', rowspan: '3', width: 80 },
				{ title: '所属单据', align: 'center', colspan: '3'}
		    ],[
				{ field: 'oddTitle', title: '单据标题', align: 'left', width: 240},
				{ field: 'oddNumber', title: '单据编号', align: 'center', width: 180},
				{ field: 'oddCreateTime', title: '单据日期', align: 'center', width: 140}
			]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}

    $("body").on("click", "#reloadmessageTable", function(){
    	loadTable();
    });
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			refreshTable();
		}
		return false;
	});

    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }

	function refreshTable(){
		table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
	}
    
    function getTableParams(){
    	return {
			articlesName: $("#articlesName").val(),
			articlesNum: $("#articlesNum").val()
    	};
    }
    
    exports('myAssetArticlesManagement', {});
});
