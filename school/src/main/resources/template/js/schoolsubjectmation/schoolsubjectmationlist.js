
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
	
	authBtn('1586094186431');

	// 获取当前登陆用户所属的学校列表
	schoolUtil.queryMyBelongSchoolList(function (json) {
		$("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), json));
		form.render("select");
		initTable();
	});

    function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: schoolBasePath + 'schoolsubjectmation001',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'subjectName', title: '科目', align: 'left', width: 160 },
		        { field: 'subjectNo', title: '编号', align: 'left', width: 140 },
		        { field: 'desc', title: '简介', align: 'left', width: 80, templet: function(d){
		        	return '<i class="fa fa-fw fa-html5 cursor" lay-event="descSel"></i>';
		        }},
		        { field: 'schoolName', title: '所属学校', align: 'left', width: 150 },
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'edit') { //编辑
	        	edit(data);
	        }else if (layEvent === 'delet') { //删除
	        	delet(data);
	        }else if (layEvent === 'descSel') { //descSel内容
	        	layer.open({
		            id: 'descSel',
		            type: 1,
		            title: '简介',
		            shade: 0.3,
		            area: ['90vw', '90vh'],
		            content: data.desc
		        });
	        }
	    });
    }
	
	// 添加
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/schoolsubjectmation/schoolsubjectmationadd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "schoolsubjectmationadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    });
	
	// 删除
	function delet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url:schoolBasePath + "schoolsubjectmation005", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/schoolsubjectmation/schoolsubjectmationedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "schoolsubjectmationedit",
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

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
    	return {
    		subjectName: $("#subjectName").val(),
			subjectNo: $("#subjectNo").val(),
			schoolId: $("#schoolId").val()
    	};
	}
    
    exports('schoolsubjectmationlist', {});
});
