
var normsStock;

//选中的仓库id进行库存修改
var chooseDepotId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	normsStock = [].concat(parent.normsStock);
	
	table.render({
	    id: 'messageTable',
        elem: '#messageTable',
        data: $.extend(true, [], normsStock),
	    cols: [[
		    { field: 'depotName', title: '仓库', align: 'left', width: 200},
	        { field: 'initialTock', title: '初始数量', align: 'left', width: 80},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 140, toolbar: '#tableBar'}
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
        }
    });
    
    //添加
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/materialnormstock/materialnormstockadd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "materialnormstockadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				table.reload("messageTable", {data: $.extend(true, [], normsStock)});
			}});
    });
	
	//删除
	function delet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
			var removeIndex = -1;
			$.each(normsStock, function(i, item){
	    		if(item.depotId === data.depotId){
	    			removeIndex = i;
	    			return false;
	    		}
	    	});
	    	if(removeIndex >= 0 ){
	    		normsStock.splice(removeIndex, 1);
	    		winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				table.reload("messageTable", {data: $.extend(true, [], normsStock)});
	    	} else {
	    		winui.window.msg("删除失败", {icon: 2, time: 2000});
	    	}
		});
	}
	
	//编辑
	function edit(data){
		chooseDepotId = data.depotId;
		_openNewWindows({
			url: "../../tpl/materialnormstock/materialnormstockedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "materialnormstockedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				table.reload("messageTable", {data: $.extend(true, [], normsStock)});
			}
		});
	}
	
	form.render();
    form.on('submit(formAddBean)', function (data) {
    	
        if (winui.verifyForm(data.elem)) {
        	parent.normsStock = [].concat(normsStock);
        	parent.layer.close(index);
			parent.refreshCode = '0';
        }
        return false;
    });
    
    $("body").on("click", "#cancle", function() {
    	parent.layer.close(index);
    });
    
    exports('materialnormstock', {});
});
