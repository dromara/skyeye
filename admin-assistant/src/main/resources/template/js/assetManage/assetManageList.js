var rowId = "";

var taskType = "";//流程详情的主标题
var processInstanceId = "";//流程id

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate,
		table = layui.table;
	
	// 新增资产
	authBtn('1566465526122');
	
	showAssetList();
	// 资产列表管理开始
	function showAssetList(){
		table.render({
		    id: 'assetlistTable',
		    elem: '#assetlistTable',
		    method: 'post',
		    url: flowableBasePath + 'asset001',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'companyName', title: '所属公司', align: 'center', width: 170 },
		        { field: 'assetName', title: '名称', width: 120, templet: function(d){
		        	return '<a lay-event="assetlistdetails" class="notice-title-click">' + d.assetName + '</a>';
		        }},
		        { field: 'typeId', title: '资产所属类型', align: 'center', width: 100 },
		        { field: 'assetNum', title: '资产编号', width: 100 },
		        { field: 'unitPrice', title: '资产单价', align: 'center', width: 80 },
		        { field: 'employeeId', title: '领用人', align: 'center', width: 80 },
		        { field: 'state', title: '状态', width: 80, align: 'center', templet: function(d){
		        	if(d.state == '1'){
		        		return "<span class='state-up'>正常</span>";
		        	}else if(d.state == '2'){
		        		return "<span class='state-down'>维修</span>";
		        	}else if(d.state == '3'){
		        		return "<span class='state-down'>报废</span>";
		        	} else {
		        		return "参数错误";
		        	}
		        }},
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#assetlisttableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}
	
	// 资产的操作事件
	table.on('tool(assetlistTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'assetlistrepair') { //修复
        	assetlistrepair(data);
        }else if (layEvent === 'assetlistdetails') { //详情
        	assetlistdetails(data);
        }else if (layEvent === 'assetlistscrap'){ //报废
        	assetlistscrap(data);
        }else if (layEvent === 'assetlistnormal'){ //恢复正常
        	assetlistnormal(data);
        }else if (layEvent === 'assetlistdelet'){ //删除
        	assetlistdelet(data);
        }else if (layEvent === 'assetlistedit'){	//编辑
        	assetlistedit(data);
        }
    });
	
	form.render();
	
	// 资产详情
	function assetlistdetails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/assetManage/assetManageDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "assetManageDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 维修
	function assetlistrepair(data){
		var msg = '确认维修该资产吗？';
		layer.confirm(msg, { icon: 3, title: '维修操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "asset008", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
    				loadassetTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	// 报废
	function assetlistscrap(data){
		var msg = '确认报废该资产吗？';
		layer.confirm(msg, { icon: 3, title: '报废操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "asset009", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
    				loadassetTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	// 恢复正常
	function assetlistnormal(data){
		var msg = '确认对该资产恢复正常吗？';
		layer.confirm(msg, { icon: 3, title: '恢复操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "asset007", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
    				loadassetTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}

	// 删除
	function assetlistdelet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "asset003", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadassetTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}

	// 新增资产
	$("body").on("click", "#assetlistaddBean", function() {
    	_openNewWindows({
			url: "../../tpl/assetManage/assetManageAdd.html", 
			title: "新增资产",
			pageId: "assetManageAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadassetTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    });
	
	// 编辑资产
	function assetlistedit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/assetManage/assetManageEdit.html", 
			title: "编辑资产",
			pageId: "assetManageEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadassetTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
	
    $("body").on("click", "#assetlistreloadTable", function() {
    	loadassetTable();
    });
    
    function loadassetTable(){
    	table.reload("assetlistTable", {where: getTableParams()});
    }
    
    // 搜索表单
	$("body").on("click", "#assetlistSearch", function() {
    	table.reload("assetlistTable", {page: {curr: 1}, where: getTableParams()});
	});
    
    function getTableParams(){
    	return {
    		assetName:$("#assetName").val(),
    		state:$("#state").val()
    	};
    }
    
    exports('assetManageList', {});
});
