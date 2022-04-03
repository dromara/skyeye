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
	
	// 新增来源
	authBtn('1596982401332');
    
	showAssetfromList();
    // 资产来源管理开始
    function showAssetfromList(){
		table.render({
		    id: 'assetfromTable',
		    elem: '#assetfromTable',
		    method: 'post',
		    url: flowableBasePath + 'assetfrom001',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'fromName', title: '来源名称', width: 170 },
		        { field: 'state', title: '状态', width: 100, align: 'center', templet: function(d){
		        	if(d.state == '0'){
		        		return "<span class='state-new'>新建</span>";
		        	}else if(d.state == '1'){
		        		return "<span class='state-up'>线上</span>";
		        	}else if(d.state == '2'){
		        		return "<span class='state-down'>线下</span>";
		        	}else{
		        		return "参数错误";
		        	}
		        }},
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#assetfromtableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}
	
	table.on('tool(assetfromTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'assetfromdelete'){ //删除
        	assetfromdelet(data);
        }else if (layEvent === 'assetfromedit'){	//编辑
        	assetfromedit(data);
        }else if (layEvent === 'assetfromup'){	//上线
        	assetfromup(data);
        }else if (layEvent === 'assetfromdown'){	//下线
        	assetfromdown(data);
        }else if (layEvent === 'assetfromtop'){	//上移
        	assetfromtopOne(data);
        }else if (layEvent === 'assetfromlower'){	//下移
        	assetfromlowerOne(data);
        }
    });
	
	form.render();
	
	// 删除
	function assetfromdelet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url:flowableBasePath + "assetfrom003", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
    				loadassetfromTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}

	// 新增资产来源
	$("body").on("click", "#assetfromaddBean", function(){
    	_openNewWindows({
			url: "../../tpl/assetManageFrom/assetManageFromAdd.html", 
			title: "新增资产来源",
			pageId: "assetManageFromAdd",
			area: ['60vw', '30vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadassetfromTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });
	
	// 编辑资产来源
	function assetfromedit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/assetManageFrom/assetManageFromEdit.html", 
			title: "编辑资产来源",
			pageId: "assetManageFromEdit",
			area: ['60vw', '30vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadassetfromTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
	
	// 上线
	function assetfromup(data, obj){
		var msg = obj ? '确认将【' + obj.data.fromName + '】上线吗？' : '确认将选中数据上线吗？';
		layer.confirm(msg, { icon: 3, title: '上线操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:flowableBasePath + "assetfrom007", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("上线成功", {icon: 1,time: 2000});
    				loadassetfromTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	// 下线
	function assetfromdown(data, obj){
		var msg = obj ? '确认将【' + obj.data.fromName + '】下线吗？' : '确认将选中数据下线吗？';
		layer.confirm(msg, { icon: 3, title: '下线操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:flowableBasePath + "assetfrom008", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("下线成功", {icon: 1,time: 2000});
    				loadassetfromTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	// 上移
	function assetfromtopOne(data){
		AjaxPostUtil.request({url:flowableBasePath + "assetfrom009", params:{rowId: data.id}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
				loadassetfromTable();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
	// 下移
	function assetfromlowerOne(data){
		AjaxPostUtil.request({url:flowableBasePath + "assetfrom010", params:{rowId: data.id}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
				loadassetfromTable();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
	// 刷新来源数据
    $("body").on("click", "#assetfromreloadTable", function(){
    	loadassetfromTable();
    });
    
	// 刷新来源列表数据
    function loadassetfromTable(){
    	table.reload("assetfromTable", {where: getTableParams()});
    }
    
	// 搜索来源列表表单
	$("body").on("click", "#assetfromSearch", function(){
    	table.reload("assetfromTable", {page: {curr: 1}, where: getTableParams()});
	});
    
	function getTableParams(){
		return {
			fromName:$("#fromName").val(),
			fromState:$("#fromState").val()
		};
	}
    
    exports('assetManageFromList', {});
});
