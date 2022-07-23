
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'tableTreeDj', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		tableTree = layui.tableTreeDj;
	
	authBtn('1586314893391');

	// 获取当前登陆用户所属的学校列表
	schoolUtil.queryMyBelongSchoolList(function (json) {
		$("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), json));
		form.render("select");
		initTable();
	});

	function initTable(){
		tableTree.render({
	        id: 'messageTable',
	        elem: '#messageTable',
	        method: 'post',
	        url: schoolBasePath + 'grademation001',
	        where: getTableParams(),
	        cols: [[
	            { field: 'gradeName', width: 150, title: '年级名称'},
	            { field: 'schoolName', width: 200, title: '所属学校'},
	            { field:'yearN', width: 160, align: 'center', title: 'N年后达到这个级别'},
		        { field:'typeName', width: 120, title: '年级状态'},
	            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
	        ]],
		    done: function(){
		    	matchingLanguage();
		    }
	    }, {
			keyId: 'id',
			keyPid: 'pId',
			title: 'gradeName',
		});

		tableTree.getTable().on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'del') { //删除
	        	del(data, obj);
	        }else if (layEvent === 'edit') { //编辑
	        	edit(data);
	        }else if (layEvent === 'upMove') { //上移
	        	upMove(data);
	        }else if (layEvent === 'downMove') { //下移
	        	downMove(data);
	        }
	    });
	    
		form.render();
	}
	
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});
	
	//删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url:schoolBasePath + "grademation003", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/schoolgrademation/schoolgrademationedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "grademationedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    //新增
    $("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/schoolgrademation/schoolgrademationadd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "grademationadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
    
    //上移
	function upMove(data){
        AjaxPostUtil.request({url:schoolBasePath + "grademation007", params:{rowId: data.id}, type: 'json', callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			loadTable();
		}});
	}
	
	//下移
	function downMove(data){
        AjaxPostUtil.request({url:schoolBasePath + "grademation008", params:{rowId: data.id}, type: 'json', callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			loadTable();
		}});
	}
    
    function loadTable(){
		tableTree.reload("messageTable", {where: getTableParams()});
    }

	function getTableParams() {
		return {
			gradeName: $("#gradeName").val(),
			schoolId: $("#schoolId").val()
		};
	}
    
    exports('schoolgrademationlist', {});
});
