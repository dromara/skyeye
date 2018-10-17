
var rowId = "";
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form;
	
	//初始化数据
    showGrid({
	 	id: "showForm",
	 	url: reqBasePath + "icon001",
	 	params: {iconClass:$("#iconClass").val()},
	 	pagination: true,
	 	pagesize: 18,
	 	template: getFileContent('tpl/syseveicon/icon-item.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 		
	 	},
	 	options: {'click .del':function(index, row){
				layer.confirm('确认删除该ICON吗？', { icon: 3, title: '删除ICON' }, function (index) {
					layer.close(index);
		            AjaxPostUtil.request({url:reqBasePath + "icon003", params:{rowId: row.id}, type:'json', callback:function(json){
		    			if(json.returnCode == 0){
		    				top.winui.window.msg("删除成功", {icon: 1,time: 2000});
		    				loadTable();
		    			}else{
		    				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		    			}
		    		}});
				});
	 		},'click .edit':function(index, row){
	 			edit(row);
	 		}
	 	},
	 	ajaxSendAfter:function(json){
	 	}
    });
	
	//搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
    	//表单验证
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/syseveicon/syseveiconedit.html", 
			title: "编辑ICON",
			pageId: "syseveuseredit",
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
	}
	
	//新增
	$("body").on("click", "#addBean", function(){
		_openNewWindows({
			url: "../../tpl/syseveicon/syseveiconadd.html", 
			title: "新增图标",
			pageId: "syseveiconadd",
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
    });
	
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	refreshGrid("showForm", {params:{iconClass:$("#iconClass").val()}});
    }
    
    exports('syseveiconlist', {});
});
