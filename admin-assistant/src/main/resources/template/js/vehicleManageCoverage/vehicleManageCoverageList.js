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
	
	// 新增车辆险种
	authBtn('1597467270009');
	
	showCoverageList();
    // 险种管理开始
	function showCoverageList(){
		table.render({
		    id: 'coverageTable',
		    elem: '#coverageTable',
		    method: 'post',
		    url: flowableBasePath + 'coverage001',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'coverageName', title: '险种名称', width: 170 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#coveragetableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}
	
	table.on('tool(coverageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'coveragedelet'){ //删除
        	coveragedelet(data);
        }else if (layEvent === 'coverageedit'){	//编辑
        	coverageedit(data);
        }
    });
	
	form.render();
	
	// 删除险种
	function coveragedelet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "coverage003", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadCoverageTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	// 新增险种
	$("body").on("click", "#addCoverageBean", function(){
    	_openNewWindows({
			url: "../../tpl/vehicleManageCoverage/vehicleManageCoverageAdd.html", 
			title: "新增险种",
			pageId: "vehicleManageCoverageAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadCoverageTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    });
	
	// 编辑险种
	function coverageedit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/vehicleManageCoverage/vehicleManageCoverageEdit.html", 
			title: "编辑险种",
			pageId: "vehicleManageCoverageEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadCoverageTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}

	// 搜索表单
	$("body").on("click", "#coverageformSearch", function(){
		table.reload("coverageTable", {page: {curr: 1}, where: getTableParams()});
	});
	
    $("body").on("click", "#reloadCoverageTable", function(){
    	loadCoverageTable();
    });
    
    function loadCoverageTable(){
    	table.reload("coverageTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		coverageName:$("#coverageName").val()
    	};
    }
    
    exports('vehicleManageCoverageList', {});
});
