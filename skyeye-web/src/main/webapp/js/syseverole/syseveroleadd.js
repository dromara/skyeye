
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'fsCommon', 'fsTree'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form,
		fsTree = layui.fsTree,
		fsCommon = layui.fsCommon;
	    
		form.render();
		
	    form.on('submit(formAddBean)', function (data) {
	    	//表单验证
	        if (winui.verifyForm(data.elem)) {
	        	var tree = $.fn.zTree.getZTreeObj("treeDemo");
	    	    var nodes = tree.getCheckedNodes(true);
	    	    if(isNull($('#roleName').val())){
	    	    	top.winui.window.msg('角色名称不能为空', {icon: 2,time: 2000});
	    			return false;
	    		}else if(isNull(nodes)){
	    	    	top.winui.window.msg('请选择菜单权限权限', {icon: 2,time: 2000});
	    			return false;
	    	    }else{
	    	    	var menuIds = "";
	    	    	for(var i = 0; i < nodes.length; i++){
	    				if(i == nodes.length-1)
	    					menuIds += nodes[i].id;
	    				else
	    					menuIds += nodes[i].id + ",";
	    			}
		        	var params = {
	        			roleName: $("#roleName").val(),
	        			roleDesc: $("#roleDesc").val(),
	        			menuIds: menuIds
		        	};
		        	
		        	AjaxPostUtil.request({url:reqBasePath + "sys015", params:params, type:'json', callback:function(json){
		 	   			if(json.returnCode == 0){
			 	   			parent.layer.close(index);
			 	        	parent.refreshCode = '0';
		 	   			}else{
		 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		 	   			}
		 	   		}});
	    		}
	        }
	        return false;
	    });
	    
	    /********* tree 处理   start *************/
	
		var trees = {};
	
		var treeDoms = $("ul.fsTree");
		if(treeDoms.length > 0) {
			$(treeDoms).each(function(i) {
				var treeId = $(this).attr("id");
				var funcNo = $(this).attr("funcNo");
				var url = $(this).attr("url");
				var tree = fsTree.render({
					id: treeId,
					funcNo: funcNo,
					url: url + "?userToken=" + getCookie('userToken'),
					getTree: getTree,
					checkEnable: true,
					loadEnable: false
				});
				if(treeDoms.length == 1) {
					trees[treeId] = tree;
				} else {
					//深度拷贝对象
					trees[treeId] = $.extend(true, {}, tree);
				}
			});
			//绑定按钮事件
			fsCommon.buttonEvent("tree", getTree);
		}
		
		function getTree(treeId) {
			if($.isEmpty(trees)) {
				fsCommon.warnMsg("未配置tree！");
				return;
			}
			if($.isEmpty(treeId)) {
				treeId = "treeDemo";
			}
			return trees[treeId];
		}
		
		/********* tree 处理   end *************/
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
	    
});