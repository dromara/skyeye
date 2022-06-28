
var rowId = "";

var surveyName = "";

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
	
	authBtn('1553649420346');
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'dwsurveydirectory001',
	    where:{surveyName: $("#surveyName").val(), surveyState: $("#surveyState").val()},
	    even:true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'surveyName', width: 300, title: '问卷名称'},
	        { field: 'answerNum', width: 140, title: '答卷'},
	        { field: 'surveyState', width: 120, title: '状态'},
	        { field: 'userName', width: 120, title: systemLanguage["com.skyeye.createName"][languageType]},
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], width: 180 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 300, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'del') { //删除
        	del(data, obj);
        }else if (layEvent === 'edit') { //设计
        	edit(data);
        }else if (layEvent === 'fzWj') { //复制问卷
        	fzWj(data);
        }else if (layEvent === 'resolveWj') { //收集问卷
        	resolveWj(data);
        }else if (layEvent === 'fxWj') { //分析报告
        	fxWj(data);
        }else if (layEvent === 'showFb') { //发布
        	showFb(data, obj);
        }else if (layEvent === 'endSurvey') { //结束调查
        	endSurvey(data, obj);
        }
    });
	
	
	form.render();
	form.on('submit(formSearch)', function (data) {
    	
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
	});
	
	//删除
	function del(data, obj){
		var msg = obj ? '确认删除问卷【' + obj.data.surveyName + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除问卷' }, function (index) {
			layer.close(index);
            
            AjaxPostUtil.request({url: reqBasePath + "dwsurveydirectory025", params:{rowId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//设计
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/dwsurveydesign/dwsurveydesign.html", 
			title: "设计问卷",
			pageId: "dwsurveydesign",
			area: ['100vw', '100vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
	
	//复制问卷
	function fzWj(data){
		rowId = data.id;
		surveyName = data.surveyName;
		_openNewWindows({
			url: "../../tpl/dwsurveydesign/dwsurveydesigncopy.html", 
			title: "复制问卷",
			pageId: "dwsurveydesigncopy",
			area: ['500px', '300px'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
	
	//收集问卷
	function resolveWj(data){
		rowId = data.id;
		surveyName = data.surveyName;
		_openNewWindows({
			url: "../../tpl/dwsurveydesign/surveyRelove.html", 
			title: "收集问卷",
			pageId: "surveyRelove",
			area: ['500px', '300px'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
	
	//分析报告
	function fxWj(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/dwsurveydesign/surveyReport.html", 
			title: "分析报告",
			pageId: "surveyReport",
			maxmin: true,
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
	
	//发布
	function showFb(data, obj){
		var msg = obj ? '确认发布问卷【' + obj.data.surveyName + '】吗？' : '确认发布选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '问卷发布' }, function (index) {
			layer.close(index);
            
            AjaxPostUtil.request({url: reqBasePath + "dwsurveydirectory023", params:{rowId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg("发布成功", {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//结束调查
	function endSurvey(data, obj){
		var msg = obj ? '确认结束问卷【' + obj.data.surveyName + '】的调查吗？' : '确认结束选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '结束调查' }, function (index) {
			layer.close(index);
            
            AjaxPostUtil.request({url: reqBasePath + "dwsurveydirectory030", params:{surveyId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg("结束成功", {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    //新增
    $("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/dwsurveydesign/dwsurveydesignadd.html", 
			title: "新增问卷",
			pageId: "dwsurveydesignadd",
			area: ['500px', '300px'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{surveyName: $("#surveyName").val(), surveyState: $("#surveyState").val()}});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where:{surveyName: $("#surveyName").val(), surveyState: $("#surveyState").val()}});
    }
    
    exports('dwsurveydesignlist', {});
});
