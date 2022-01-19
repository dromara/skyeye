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
	
	authBtn('1559201340836');
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'lightapptype001',
	    where: {typeName:$("#typeName").val(),state:$("#state").val()},
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'typeName', title: '类型名称', align: 'center', width: 120 },
	        { field: 'iconPath', title: 'logo', width: 60, templet: function(d){
	        	if(isNull(d.iconPath)){
	        		return '';
	        	}else{
	        		var str = '';
		        	if(d.iconType == '1'){
			        	str += '<div class="winui-icon winui-icon-font" style="text-align: center;">';
			        	str += '<i class="fa fa-fw ' + d.iconPath + '" style="color: black"></i>';
			        	str += '</div>';
		        	}else if(d.iconType = '2'){
		        		str = '<img src="' + fileBasePath + d.iconPath + '" class="photo-img" lay-event="iconPath">';
		        	}
		        	return str;
	        	}
	        }},
	        { field: 'useNum', title: '轻应用数量', align: 'center', width: 120},
	        { field: 'state', title: '状态', width: 100, align: 'center', templet: function(d){
	        	if(d.state == '1'){
	        		return "<span class='state-new'>新建</span>";
	        	}else if(d.state == '2'){
	        		return "<span class='state-up'>上线</span>";
	        	}else if(d.state == '3'){
	        		return "<span class='state-down'>下线</span>";
	        	}else{
	        		return "参数错误";
	        	}
	        }},
	        { field: 'createId', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120},
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 200},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#tableBar'}
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
        }else if (layEvent === 'iconPath') { //预览
        	layer.open({
        		type:1,
        		title:false,
        		closeBtn:0,
        		skin: 'demo-class',
        		shadeClose:true,
        		content:'<img src="' + fileBasePath + data.iconPath + '" style="max-height:600px;max-width:100%;">',
        		scrollbar:false
            });
        }else if (layEvent === 'delete'){ //删除
        	del(data);
        }else if (layEvent === 'up') { //上线
        	up(data);
        }else if (layEvent === 'down') { //下线
        	down(data);
        }else if (layEvent === 'top') { //上移
        	topOne(data);
        }else if (layEvent === 'lower') { //下移
        	lowerOne(data);
        }
    });
	
	form.render();
	
	
	$("body").on("click", "#formSearchLightApp", function(){
		refreshTable();
	});
	
	$("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{typeName:$("#typeName").val(),state:$("#state").val()}});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where:{typeName:$("#typeName").val(),state:$("#state").val()}});
    }

	//新增
	$("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/lightapptype/lightapptypeadd.html", 
			title: "新增轻应用类型",
			pageId: "lightapptypeadd",
			area: ['800px', '60vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/lightapptype/lightapptypeedit.html", 
			title: "编辑轻应用类型",
			pageId: "lightapptypeedit",
			area: ['800px', '60vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
	
	//删除
	function del(data, obj){
		var msg = obj ? '确认删除【' + obj.data.typeName + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除轻应用类型' }, function (index) {
			layer.close(index);
            
            AjaxPostUtil.request({url:reqBasePath + "lightapptype007", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	//上线
	function up(data, obj){
		var msg = obj ? '确认将【' + obj.data.typeName + '】上线吗？' : '确认将选中数据上线吗？';
		layer.confirm(msg, { icon: 3, title: '轻应用类型上线' }, function (index) {
			layer.close(index);
            //向服务端发送上线指令
            AjaxPostUtil.request({url:reqBasePath + "lightapptype008", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("上线成功", {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	//下线
	function down(data, obj){
		var msg = obj ? '确认将【' + obj.data.typeName + '】下线吗？' : '确认将选中数据下线吗？';
		layer.confirm(msg, { icon: 3, title: '轻应用类型下线' }, function (index) {
			layer.close(index);
            //向服务端发送下线指令
            AjaxPostUtil.request({url:reqBasePath + "lightapptype009", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("下线成功", {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	//上移
	function topOne(data){
		AjaxPostUtil.request({url:reqBasePath + "lightapptype005", params:{rowId: data.id}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
				loadTable();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
	//下移
	function lowerOne(data){
		AjaxPostUtil.request({url:reqBasePath + "lightapptype006", params:{rowId: data.id}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
				loadTable();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
    exports('lightapptypelist', {});
});
