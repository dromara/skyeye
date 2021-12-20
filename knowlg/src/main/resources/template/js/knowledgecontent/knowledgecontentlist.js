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
	
	authBtn('1570327662893'); //新增知识库
	authBtn('1570329371505'); //批量上传
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'knowledgecontent001',
	    cellMinWidth: 100,
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			{ field: 'title', title: '标题', width: 350, templet: function(d){
				return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
			}},
	        { field: 'state', title: '状态', width: 120, templet: function(d){
	        	if(d.state == '3'){
	        		return "<span class='state-down'>审核不通过</span>";
	        	}else if(d.state == '2'){
	        		return "<span class='state-up'>审核通过</span>";
	        	}else if(d.state == '1'){
	        		return "<span class='state-new'>审核中</span>";
	        	}
	        }},
	        { field: 'typeName', title: '所属分类',  width: 120 },
	        { field: 'createTime', title: '最后编辑时间', align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 160, toolbar: '#tableBar'}
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
        if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'delet') { //删除
        	delet(data);
        }else if (layEvent === 'details') { //详情
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
			url: "knowledgetype008?userToken=" + getCookie('userToken') + "&loginPCIp=",
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

	// 添加
	$("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/knowledgecontent/knowledgecontentadd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "knowledgecontentadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });
	
	// 批量上传
	$("body").on("click", "#addAllBean", function(){
    	_openNewWindows({
			url: "../../tpl/knowledgecontent/filefolderupload.html", 
			title: "批量上传知识库",
			pageId: "filefolderupload",
			area: ['400px', '350px'],
			callBack: function(refreshCode){
				loadTable();
			}});
    });
	
	// 删除
	function delet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "knowledgecontent005", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/knowledgecontent/knowledgecontentedit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "knowledgecontentedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}
		});
	}
	
	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/knowledgecontent/knowledgecontentdetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "knowledgecontentdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}

	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

    function getTableParams(){
    	return {
			title:$("#title").val(),
			state:$("#state").val(),
			typeId: isNull($("#typeId").val()) ? "" : $("#typeId").attr("typeId")
		};
	}

    exports('knowledgetypelist', {});
});
