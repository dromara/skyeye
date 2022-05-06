var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	// 新增类别
	authBtn('1596981316446');
	
	showAssettypeList();
	// 资产类型列表
    function showAssettypeList(){
		table.render({
		    id: 'assettypeTable',
		    elem: '#assettypeTable',
		    method: 'post',
		    url: flowableBasePath + 'assettype001',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'typeName', title: '类型名称', width: 170 },
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
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#assettypetableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}
	
	table.on('tool(assettypeTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'assettypedelete'){ //删除
        	assettypedelet(data);
        }else if (layEvent === 'assettypeedit'){	//编辑
        	assettypeedit(data);
        }else if (layEvent === 'assettypeup'){	//上线
        	assettypeup(data);
        }else if (layEvent === 'assettypedown'){	//下线
        	assettypedown(data);
        }else if (layEvent === 'assettypetop'){	//上移
        	assettypetopOne(data);
        }else if (layEvent === 'assettypelower'){	//下移
        	assettypelowerOne(data);
        }
    });
	
	form.render();
	
	// 删除
	function assettypedelet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "assettype003", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
    				loadassettypeTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}

	// 新增
	$("body").on("click", "#assettypeaddBean", function(){
    	_openNewWindows({
			url: "../../tpl/assetManageType/assetManageTypeAdd.html", 
			title: "新增资产类型",
			pageId: "assetManageTypeAdd",
			area: ['60vw', '30vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadassettypeTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });
	
	// 编辑
	function assettypeedit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/assetManageType/assetManageTypeEdit.html", 
			title: "编辑资产类型",
			pageId: "assetManageTypeEdit",
			area: ['60vw', '30vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadassettypeTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
	
	// 上线
	function assettypeup(data, obj){
		var msg = obj ? '确认将【' + obj.data.typeName + '】上线吗？' : '确认将选中数据上线吗？';
		layer.confirm(msg, { icon: 3, title: '上线操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "assettype007", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("上线成功", {icon: 1,time: 2000});
    				loadassettypeTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	// 下线
	function assettypedown(data, obj){
		var msg = obj ? '确认将【' + obj.data.typeName + '】下线吗？' : '确认将选中数据下线吗？';
		layer.confirm(msg, { icon: 3, title: '下线操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "assettype008", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("下线成功", {icon: 1,time: 2000});
    				loadassettypeTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	// 上移
	function assettypetopOne(data){
		AjaxPostUtil.request({url: flowableBasePath + "assettype009", params:{rowId: data.id}, type: 'json', callback: function(json){
			if(json.returnCode == 0){
				winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
				loadassettypeTable();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
	// 下移
	function assettypelowerOne(data){
		AjaxPostUtil.request({url: flowableBasePath + "assettype010", params:{rowId: data.id}, type: 'json', callback: function(json){
			if(json.returnCode == 0){
				winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
				loadassettypeTable();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
    $("body").on("click", "#assettypereloadTable", function(){
    	loadassettypeTable();
    });
    
    // 搜索表单
	$("body").on("click", "#assettypeSearch", function(){
    	table.reload("assettypeTable", {page: {curr: 1}, where: getTableParams()});
	});
    
    function loadassettypeTable(){
    	table.reload("assettypeTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		typeName:$("#typeName").val(),
    		typeState:$("#typeState").val()
    	};
    }
    
    exports('assetManageTypeList', {});
});
