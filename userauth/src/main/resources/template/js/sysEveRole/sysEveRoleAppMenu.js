
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'fsCommon', 'fsTree'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form,
			fsTree = layui.fsTree,
			fsCommon = layui.fsCommon;
	    
	    var checkeRows = '';
	    
	    matchingLanguage();
		form.render();
	    form.on('submit(formEditBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	var tree = $.fn.zTree.getZTreeObj("treeDemo");
	    	    var nodes = tree.getCheckedNodes(true);
	    	    if(isNull(nodes)){
	    	    	winui.window.msg('请选择菜单权限', {icon: 2, time: 2000});
	    			return false;
	    	    } else {
	    	    	var menuIds = "";//菜单
                    var pointIds = "";//权限点
	    	    	for(var i = 0; i < nodes.length; i++){
	    	    		if(i == nodes.length-1){
                            if(nodes[i].type == "authPoint"){
                                pointIds += nodes[i].id;
                            } else {
                                menuIds += nodes[i].id;
                            }
                        } else {
                            if(nodes[i].type == "authPoint"){
                                pointIds += nodes[i].id + ",";
                            } else {
                                menuIds += nodes[i].id + ",";
                            }
                        }
	    			}
		        	var params = {
	        			menuIds: menuIds,
	        			pointIds: pointIds,
	        			id: parent.rowId
		        	};
		        	
		        	AjaxPostUtil.request({url: reqBasePath + "sys039", params: params, type: 'json', callback: function (json) {
						parent.layer.close(index);
						parent.refreshCode = '0';
		 	   		}});
	    		}
	        }
	        return false;
	    });
	    
	    /********* tree 处理   start *************/
		var tree;

		AjaxPostUtil.request({url: reqBasePath + "sys038", params: {rowId: parent.rowId}, type: 'json', callback: function (json) {
			$("#roleName").text(json.bean.roleName);
			$("#roleDesc").text(json.bean.roleDesc);
			checkeRows = json.rows;

			tree = fsTree.render({
				id: "treeDemo",
				url: reqBasePath + "sys037",
				getTree: getTree,
				checkEnable: true,
				loadEnable: false,
				showLine: false,
				showIcon: false,
				addDiyDom: addDiyDom,
				fontCss: setFontCss
			}, function(id){
				var zTreeObj = $.fn.zTree.getZTreeObj(id);
				var zTree = zTreeObj.getCheckedNodes(false);
				for (var i = 0; i < zTree.length; i++) {
					for(var j = 0; j < checkeRows.length; j++){
						if(zTree[i].id == checkeRows[j].menuId){
							zTreeObj.checkNode(zTree[i], true);
						}
					}
				}
				var li_head = '<li class="head"><a>' +
					'<div class="diy" style="width: 40%">菜单权限</div>' +
					'<div class="diy">所属系统</div>' +
					'<div class="diy">菜单类型</div>' +
					'</a></li>';
				var rows = $("#treeDemo").find('li');
				if(rows.length > 0) {
					rows.eq(0).before(li_head);
				} else {
					$("#" + treeId).append(li_head);
					$("#" + treeId).append('<li ><div style="text-align: center;line-height: 30px;" >无符合条件数据</div></li>')
				}
			});
			// 绑定按钮事件
			fsCommon.buttonEvent("tree", getTree);
   		}});
		
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
			var aObj = $("#" + treeNode.tId + "_a");
			var switchObj = $("#" + treeNode.tId + "_switch");
			var icoObj = $("#" + treeNode.tId + "_ico");
			var spanObj = $("#" + treeNode.tId + "_span");
			aObj.attr('title', '');
			aObj.append('<div class="diy swich" style="width: 40%"></div>');
			switchObj.remove();
			spanObj.remove();
			icoObj.remove();

			var div = $("#" + treeNode.tId).find('.swich').eq(0);
			div.append(switchObj);
			div.append(spanObj);

			switchObj.before("<span style='height:1px; display: inline-block; width:" + (15 * treeNode.level) + "px'></span>");
			// 所属系统
			aObj.append('<div class="diy">' + treeNode.sysName + '</div>');
			// 菜单类型
			aObj.append('<div class="diy">' + treeNode.pageType + '</div>');
		}
		
		
		var lastValue = "", nodeList = [], hiddenNodes = [];
		$("#menuName").val('');
		var key = $("#menuName");
		key.bind("focus", function(e) {
			if (key.hasClass("empty")) {
				key.removeClass("empty");
			}
		}).bind("blur",  function(e) {
			if (key.get(0).value === "") {
				key.addClass("empty");
			}
		}).bind("propertychange", searchNode) //property(属性)change(改变)的时候，触发事件
		.bind("input", searchNode);
		
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
	    	if (!isNull(childs)){
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
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
	    
});