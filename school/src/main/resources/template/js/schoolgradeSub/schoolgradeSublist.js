
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'opTable', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form;
	var opTable;

	// 获取当前登陆用户所属的学校列表
	schoolUtil.queryMyBelongSchoolList(function (json) {
		$("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), json));
		form.render("select");
		initTable();
	});

    function initTable(){
		opTable = layui.opTable.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: schoolBasePath + 'schoolgradesubject001',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		    	{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		    	{ field: 'gradeName', width: 150, title: '年级名称'},
		    	{ field: 'subjectNum', title: '科目数', align: 'center', width: 100 },
	            { field: 'schoolName', width: 200, title: '所属学校'},
	            { field: 'yearN', width: 160, align: 'center', title: 'N年后达到这个级别'},
	            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
		    ]],
		    openNetwork: {
				openCols: [
					{field: 'skillName', title: '技能'}
        		],
        		/**
		         *
		         * @param data 当前行数据
		         * @param success 成功
		         * @param message 显示异常消息[没有数据 出错 等]
		         */
        		onNetwork: function (data, success, message) {
        			var str = "";
        			for(var i = 0; i < data.skill.length; i++){
        				str += '<span class="layui-badge layui-bg-orange" style="margin-right:5px;">' + data.skill[i].subjectName + '</span>';
        			}
        			success({skillName: str})
        		}
		    },
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		layui.table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'subBind') { //科目绑定
	        	subBind(data);
	        }
	    });
	    
	    form.render();
    }

	$("body").on("click", "#formSearch", function() {
		refreshTable();
	});
	
	//科目绑定
	function subBind(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/schoolgradeSub/schoolgradeSubBind.html", 
			title: "科目绑定",
			pageId: "schoolgradeSubBind",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	layui.table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	layui.table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

	function getTableParams() {
		return {
			gradeName: $("#gradeName").val(),
			schoolId: $("#schoolId").val()
		};
	}
    
    exports('schoolgradeSublist', {});
});
