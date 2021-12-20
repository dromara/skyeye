
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'fsCommon', 'fsTree', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form,
			fsTree = layui.fsTree,
			fsCommon = layui.fsCommon,
			textool = layui.textool;
	    
		matchingLanguage();
		form.render();
		
		textool.init({
	    	eleId: 'roleDesc',
	    	maxlength: 250,
	    	tools: ['count', 'copy', 'reset', 'clear']
	    });
		
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	var tree = $.fn.zTree.getZTreeObj("treeDemo");
	    	    var nodes = tree.getCheckedNodes(true);
	    	    if(isNull($('#roleName').val())){
	    	    	winui.window.msg('角色名称不能为空', {icon: 2,time: 2000});
	    			return false;
	    		}else if(isNull(nodes)){
	    	    	winui.window.msg('请选择菜单权限权限', {icon: 2,time: 2000});
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
		 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
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
					url: url + "?userToken=" + getCookie('userToken') + "&loginPCIp=" + returnCitySN["cip"],
					getTree: getTree,
					checkEnable: true,
					loadEnable: false,
					showLine: false,
					showIcon: false,
					addDiyDom: addDiyDom,
					fontCss: setFontCss,
					expandSpeed: 'speed'
				}, function(id){
					var li_head = ' <li class="head"><a><div class="diy">所属系统</div><div class="diy">菜单权限</div><div class="diy">菜单类型</div></a></li>';
   					var rows = $("#" + treeId).find('li');
   					if(rows.length > 0) {
   						rows.eq(0).before(li_head)
   					} else {
   						$("#" + treeId).append(li_head);
   						$("#" + treeId).append('<li ><div style="text-align: center;line-height: 30px;" >无符合条件数据</div></li>')
   					}
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
	    
		/**
		 * 自定义DOM节点
		 */
		function addDiyDom(treeId, treeNode) {
			var spaceWidth = 15;
			var liObj = $("#" + treeNode.tId);
			var aObj = $("#" + treeNode.tId + "_a");
			var switchObj = $("#" + treeNode.tId + "_switch");
			var icoObj = $("#" + treeNode.tId + "_ico");
			var spanObj = $("#" + treeNode.tId + "_span");
			aObj.attr('title', '');
			aObj.append('<div class="diy swich"></div>');
			var div = $(liObj).find('div').eq(0);
			switchObj.remove();
			spanObj.remove();
			icoObj.remove();
			div.append(switchObj);
			div.append(spanObj);
			var spaceStr = "<span style='height:1px;display: inline-block;width:" + (spaceWidth * treeNode.level) + "px'></span>";
			switchObj.before(spaceStr);
			var editStr = '';
			editStr = '<div class="diy">' + (treeNode.sysName == null ? ' ' : treeNode.sysName) + '</div>';
			aObj.before(editStr);
			editStr = '<div class="diy">' + (treeNode.pageType == null ? ' ' : treeNode.pageType) + '</div>';
			aObj.append(editStr);
		}
		
		var lastValue = "", nodeList = [], fontCss = {}, hiddenNodes = [];
		$("#menuName").val('');
		var key = $("#menuName");
		key.bind("focus", focusKey)  
	        .bind("blur", blurKey)  
	        .bind("propertychange", searchNode) //property(属性)change(改变)的时候，触发事件  
	        .bind("input", searchNode);
		
		function focusKey(e) {  
		    if (key.hasClass("empty")) {  
		        key.removeClass("empty");  
		    }  
		}  
		
		function blurKey(e) {  
		    if (key.get(0).value === "") {  
		        key.addClass("empty");  
		    }  
		} 
		
		//搜索树
		function searchNode(e) {
			var zTreeObj = $.fn.zTree.getZTreeObj("treeDemo");
			var value = $.trim(key.get(0).value);
			var keyType = "name";
			
			if (key.hasClass("empty")) {  
		        value = "";  
		    } 
			
			//显示上次搜索后背隐藏的结点
			zTreeObj.showNodes(hiddenNodes);
			//查找不符合条件的叶子节点
		    function filterFunc(node){
		        var _keywords = $("#menuName").val();
		        if(node.name.indexOf(_keywords) != -1) 
		        	return false;
		        if(node.isParent) {
		    		//是父节点时需要判断子节点是否符合条件，是的话则父节点需要保留
		    		var bl = CheckChildNodesIsContainKeyword(node);
		    		return bl;
		    	} else {
		    		//是子节点时，需要判断父节点是否符合条件，是的话则子节点需要保留
		    		var bl2 = CheckParentNodesIsContainKeyword(node);
		    		return bl2;
		    	}
		        return true;
		    };
		    //获取不符合条件的叶子结点
		    hiddenNodes = zTreeObj.getNodesByFilter(filterFunc);
		    //隐藏不符合条件的叶子结点
		    zTreeObj.hideNodes(hiddenNodes);
		    if (lastValue === value) return;  
		    lastValue = value;  
		    if (value === ""){  
		        updateNodes(false);  
		        return;  
		    };
		    updateNodes(false);
		    nodeList = zTreeObj.getNodesByParamFuzzy(keyType, value); //调用ztree的模糊查询功能，得到符合条件的节点  
		    updateNodes(true); //更新节点
	    };
	    
	    //tree搜索时：是父节点时需要判断子节点是否符合条件，是的话则父节点需要保留
	    function CheckChildNodesIsContainKeyword(pNode) {
	    	var childs = pNode.children;
	    	var isexit = true;
	    	if(!isNull(childs)){
	    		for(var i = 0; i < childs.length; i++) {
	    			if(childs[i].isParent) {
	    				isexit = CheckChildNodesIsContainKeyword(childs[i]);
	    				if(!isexit)
	    					return isexit;
	    			} else {
	    				var _keywords = $("#menuName").val();
	    				if(childs[i].name.indexOf(_keywords) != -1)
	    					return false;
	    			}
	    		}
	    	}
	    	
	    	return true;
	    }

	    //tree 搜索时：子节点时，需要判断父节点是否符合条件，是的话则子节点需要保留
	    function CheckParentNodesIsContainKeyword(cNode) {
	    	var pnode = cNode.getParentNode();
	    	if(pnode != null) {
	    		var _keywords = $("#menuName").val();
	    		if(pnode.name.indexOf(_keywords) != -1)
	    			return false;
	    		else {
	    			return CheckParentNodesIsContainKeyword(pnode)
	    		}
	    	}
	    	return true;
	    }

		
		//高亮显示被搜索到的节点  
		function updateNodes(highlight) {  
		    var zTree = $.fn.zTree.getZTreeObj("treeDemo");
		    for( var i = 0; i < nodeList.length; i++) {  
		        nodeList[i].highlight = highlight;//高亮显示搜索到的节点(highlight是自己设置的一个属性)
		        zTree.expandNode(nodeList[i].getParentNode(), true, false, false);//将搜索到的节点的父节点展开  
		        zTree.updateNode(nodeList[i]);//更新节点数据，主要用于该节点显示属性的更新
		    }  
		} 

		function setFontCss(treeId, treeNode) {  
		    return (!!treeNode.highlight) ? {color:"#00ff66", "font-weight":"bold"} : {color:"#333", "font-weight":"normal"};  
		}
		
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
	    
});