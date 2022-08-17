var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'table', 'jquery', 'winui', 'form', 'eleTree'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		eleTree = layui.eleTree;
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.knowlgBasePath + 'knowledgecontent016',
	    cellMinWidth: 100,
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			{ field: 'title', title: '标题', width: 350, templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
			}},
			{ field: 'lavel', title: '标签', align: 'left', width: 250, templet: function (d) {
				var lavel = isNull(d.label) ? [] : d.label.split(',');
				var str = "";
				$.each(lavel, function(i, item){
					str += '<span class="layui-badge layui-bg-blue">' + item + '</span>';
				});
				return str;
			}},
	        { field: 'typeName', title: '所属分类',  width: 120 },
			{ field: 'createName', title: '上传人', align: 'left', width: 100 },
	        { field: 'createTime', title: '最后编辑时间', align: 'center', width: 120 }
	    ]],
	    done: function(){
	    	matchingLanguage();
	    	if(!loadKnowlgType){
				initKnowlgType();
	    	}
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { //详情
        	details(data);
        }
    });
	
	var loadKnowlgType = false;
	// 初始化类型
	function initKnowlgType(){
		loadKnowlgType = true;
		var el5;
		el5 = eleTree.render({
			elem: '.ele5',
			url: sysMainMation.knowlgBasePath + "knowledgetype008",
			defaultExpandAll: true,
			expandOnClickNode: false,
			highlightCurrent: true
		});
		$(".ele5").hide();
		$("#typeId").on("click",function (e) {
			e.stopPropagation();
			$(".ele5").toggle();
		});
		eleTree.on("nodeClick(data5)",function(d) {
			$("#typeId").val(d.data.currentData.name);
			$("#typeId").attr("typeId", d.data.currentData.id);
			$(".ele5").hide();
		})
		$(document).on("click",function() {
			$(".ele5").hide();
		})
	}

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			refreshTable();
		}
		return false;
	});

	// 详情
	function details(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/knowledgecontent/knowledgecontentdetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "knowledgecontentdetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}

	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

    function getTableParams() {
    	return {
			title:$("#title").val(),
			label:$("#label").val(),
			typeId: isNull($("#typeId").val()) ? "" : $("#typeId").attr("typeId")
		};
	}

    exports('knowledgeDepotList', {});
});
