
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
	
	// 新增类别
	authBtn('1596955870142');
	
	showLeiBieList();
	// 展示用品类别列表
	function showLeiBieList(){
		table.render({
		    id: 'leibieTable',
		    elem: '#leibieTable',
		    method: 'post',
		    url: reqBasePath + 'assetarticles001',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'typeName', title: '类别名称', width: 120 },
		        { field: 'state', title: '状态', width: 80, templet: function(d){
		        	if(d.state == '2'){
		        		return "<span class='state-down'>下线</span>";
		        	}else if(d.state == '1'){
		        		return "<span class='state-up'>上线</span>";
		        	}else if(d.state == '0'){
		        		return "<span class='state-new'>新建</span>";
		        	}
		        }},
		        { field: 'createName', title: '创建人', width: 120 },
		        { field: 'createTime', title: '创建时间', align: 'center', width: 180 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 257, toolbar: '#tableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}
	
	// 用品类别表的操作事件
	table.on('tool(leibieTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑用品类别
        	edit(data);
        }else if (layEvent === 'delet') { //删除用品类别
        	delet(data);
        }else if (layEvent === 'up') { //上线用品类别
        	up(data);
        }else if (layEvent === 'down') { //下线用品类别
        	down(data);
        }else if (layEvent === 'upMove') { //上移用品类别
        	upMove(data);
        }else if (layEvent === 'downMove') { //下移用品类别
        	downMove(data);
        }
    });
	
	form.render();
	
	// 添加用品类别
	$("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/assetArticlesType/assetArticlesTypeAdd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "assetArticlesTypeAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });
	
	// 删除
	function delet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "assetarticles003", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	// 上线
	function up(data){
		var msg = '确认上线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '上线操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "assetarticles004", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("上线成功", {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	// 下线
	function down(data){
		var msg = '确认下线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '下线操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "assetarticles005", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("下线成功", {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/assetArticlesType/assetArticlesTypeEdit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "assetArticlesTypeEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}
		});
	}
	
	// 上移
	function upMove(data){
        AjaxPostUtil.request({url:reqBasePath + "assetarticles008", params:{rowId: data.id}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
				loadTable();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
	// 下移
	function downMove(data){
        AjaxPostUtil.request({url:reqBasePath + "assetarticles009", params:{rowId: data.id}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
				loadTable();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
	// 刷新用品类别数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    // 刷新用品类别数据
    function loadTable(){
    	table.reload("leibieTable", {where: getTableParams()});
    }
    
    // 搜索类别表单
	$("body").on("click", "#formSearch", function(){
		table.reload("leibieTable", {page: {curr: 1}, where: getTableParams()});
	});
	
	function getTableParams(){
		return {
			typeName: $("#typeName").val()
		};
	}
	
    exports('assetarticleslist', {});
});
