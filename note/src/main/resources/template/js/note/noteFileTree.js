
var clickId = "";//父页面传递的id

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fsTree'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	clickId = parent.clickId;
	clickType = parent.clickType;
	
	var $ = layui.jquery,
		fsTree = layui.fsTree;
	
	matchingLanguage();
	/********* tree 处理   start *************/
	var ztree = null;
	fsTree.render({
		id: "treeDemo",
		url: sysMainMation.noteBasePath + "queryFolderByUserId?moveId=" + clickId,
		checkEnable: true,
		chkStyle: "radio",
		loadEnable: true,//异步加载
		showLine: false,
		showIcon: true,
		onDblClick: function(){
		},
		onAsyncSuccess: function(id){
		}
	}, function(id){
		ztree = $.fn.zTree.getZTreeObj(id);
		var selNode = ztree.getNodeByParam("id", "1", null);
		ztree.removeNode(selNode);//移除节点
	});
	
	/** ******* tree 处理 end ************ */
	
	//保存
	$("body").on("click", "#MoveTo", function (e) {
		nodes = ztree.getCheckedNodes(true);
		if(nodes == undefined || nodes == ""){
			winui.window.msg("请选择节点", {icon: 2, time: 2000});
			return;
		}
		var chooseId = nodes[0].id;
		if(clickType == "folder"){
			var arrId = new Array();
			arrId[0] = clickId;
			AjaxPostUtil.request({url: sysMainMation.noteBasePath + "mynote010", params: {targetId: chooseId, arrId : arrId}, type: 'json', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = chooseId;
	   		}});
		} else {
			AjaxPostUtil.request({url: sysMainMation.noteBasePath + "mynote011", params: {toId: chooseId, moveId : clickId}, type: 'json', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
	   		}});
		}
	});
	
    exports('myNote', {});
});
