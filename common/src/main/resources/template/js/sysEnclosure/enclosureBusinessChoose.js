
var enclosureList = new Array();

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fsTree'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		fsTree = layui.fsTree;

	var boxId = GetUrlParam('boxId');
	enclosureList = [].concat(JSON.parse(parent.$("#" + boxId).attr(skyeyeEnclosure.enclosureListKey)));

	matchingLanguage();
	form.render();

	var ztree = null;
	/********* tree 处理   start *************/
	fsTree.render({
		id: "treeDemo",
		url: reqBasePath + "queryEnclosureTree",
		checkEnable: true,
		loadEnable: false,//异步加载
		showLine: false,
		showIcon: true,
		onDblClick: function(){
		},
		onAsyncSuccess: function(id){
		},
		onCheck: zTreeOnCheck //选中回调函数
	}, function(id){
		ztree = $.fn.zTree.getZTreeObj(id);
		var zTreeChecked = ztree.getCheckedNodes(false);
		for (var i = 0; i < zTreeChecked.length; i++) {
			for(var j = 0; j < enclosureList.length; j++){
				if(zTreeChecked[i].id == enclosureList[j].id){
					ztree.checkNode(zTreeChecked[i], true, true);
				}
			}
		}
	});

	//选中或取消选中的回调函数
	function zTreeOnCheck(event, treeId, treeNode) {
		//获取选中节点
		var zTree = ztree.getCheckedNodes(true);
		for (var i = 0; i < zTree.length; i++) {
			if (!isNull(zTree[i].objectType) && zTree[i].objectType != "catalog"){
				addToArray({
					id: zTree[i].id,
					name: zTree[i].name,
					fileAddress: zTree[i].fileAddress
				});
			}
		}
		//获取未选中节点
		zTree = ztree.getCheckedNodes(false);
		for (var i = 0; i < zTree.length; i++) {
			if (!isNull(zTree[i].objectType) && zTree[i].objectType != "catalog"){
				removeToArray(zTree[i].id);
			}
		}
	}

	/********* tree 处理   end *************/

	// 取消
	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});

	// 确定
	$("body").on("click", "#confimChoose", function() {
		var nodes = ztree.getCheckedNodes(true);
		for(var i = 0; i < nodes.length; i++){
			var node = nodes[i];
			if (!isNull(node.objectType) && node.objectType != "catalog") {
				addToArray({
					id: node.id,
					name: node.name,
					fileAddress: node.fileAddress
				});
			}
		}
		parent.layer.close(index);
		parent.$("#" + boxId).attr(skyeyeEnclosure.enclosureListKey, JSON.stringify(enclosureList));
		parent.refreshCode = '0';
	});
});

// 向集合中添加元素
function addToArray(data) {
	var inArray = false;
	$.each(enclosureList, function(i, item) {
		if(item.id === data.id){
			inArray = true;
			return false;
		}
	});
	if(!inArray){//如果该元素在集合中不存在
		enclosureList.push({
			id: data.id,
			name: data.name,
			fileAddress: data.fileAddress
		});
	}
}

// 移除集合中的元素
function removeToArray(id){
	var inArray = -1;
	$.each(enclosureList, function(i, item) {
		if(id === item.id) {
			inArray = i;
			return false;
		}
	});
	if(inArray != -1){//如果该元素在集合中存在
		enclosureList.splice(inArray, 1);
	}
}

/**
 * 文件上传成功后的回调函数
 * @param json
 */
function uploadFileCallback(json) {
	var params = {
		name: json.bean.name,
		path: json.bean.fileAddress,
		type: json.bean.fileType,
		size: json.bean.size,
		sizeType: json.bean.fileSizeType,
		catalog: '0'
	};
	AjaxPostUtil.request({url: reqBasePath + "createEnclosure", params: params, type: 'json', method: 'POST', callback: function (data) {
		addToArray({
			id: data.bean.id,
			name: json.bean.name,
			fileAddress: json.bean.fileAddress
		});
	}});
}
