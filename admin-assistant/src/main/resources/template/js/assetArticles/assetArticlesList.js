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
	
	// 获取已经上线的用品类别列表
	adminAssistantUtil.queryAssetArticlesTypeUpStateList(function (data){
		$("#typeId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), data));
		form.render('select');
	});

	showliebiaoList();
	// 展示用品列表
	function showliebiaoList(){
		table.render({
		    id: 'liebiaoTable',
		    elem: '#liebiaoTable',
		    method: 'post',
		    url: flowableBasePath + 'assetarticles012',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'articlesName', title: '名称', width: 200, templet: function (d) {
		        	return '<a lay-event="liebiaoDedails" class="notice-title-click">' + d.articlesName + '</a>';
		        }},
		        { field: 'typeName', title: '类别', width: 200 },
		        { field: 'articlesNum', title: '编号', width: 270},
		        { field: 'specifications', title: '规格', width: 100},
		        { field: 'residualNum', title: '库存数量', width: 100 },
		        { field: 'company', title: '所属公司', width: 170 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 120, toolbar: '#liebiaoTableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}
	
	// 用品列表的操作事件
	table.on('tool(liebiaoTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'liebiaoDedails') { //用品详情
        	liebiaoDedails(data);
        }else if (layEvent === 'liebiaoEdit') { //用品编辑
        	liebiaoEdit(data);
        }else if (layEvent === 'liebiaoDelete') { //删除用品
        	liebiaoDelete(data);
        }
    });
	
	form.render();
	
	// 新增用品
	$("body").on("click", "#addArticlesBean", function() {
    	_openNewWindows({
			url: "../../tpl/assetarticles/assetarticlesadd.html", 
			title: "新增用品",
			pageId: "assetarticlesadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadLiebiaoTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    });
	
	// 用品详情
	function liebiaoDedails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/assetarticles/assetarticlesdetails.html", 
			title: "用品详情",
			pageId: "assetarticlesdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}
		});
	}
	
	// 删除用品
	function liebiaoDelete(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "assetarticles014", params: {rowId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadLiebiaoTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
    
	// 编辑用品
	function liebiaoEdit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/assetarticles/assetarticlesedit.html", 
			title: "编辑用品",
			pageId: "assetarticlesedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}
		});
	}
	
    // 刷新用品数据
    $("body").on("click", "#reloadLieBiaoTable", function() {
		loadTable();
    });
    
    // 刷新用品列表数据
    function loadTable(){
    	table.reload("liebiaoTable", {where: getTableParams()});
    }
    
	// 搜索列表表单
	$("body").on("click", "#liebiaoSearch", function() {
    	table.reload("liebiaoTable", {page: {curr: 1}, where: getTableParams()});
	});
	
    function getTableParams(){
    	return {
    		articlesName: $("#articlesName").val(),
    		typeId: $("#typeId").val()
    	};
    }
    
    exports('assetArticlesList', {});
});
