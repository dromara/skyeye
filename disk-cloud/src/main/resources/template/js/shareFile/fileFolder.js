
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fsCommon', 'fsTree'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
		var $ = layui.$
			fsTree = layui.fsTree,
			fsCommon = layui.fsCommon;
		
		/********* tree 处理   start *************/
		var ztree = null;
		fsTree.render({
			id: "treeDemo",
			url: reqBasePath + "fileconsole001?userToken=" + getCookie('userToken') + "&loginPCIp=" + returnCitySN["cip"],
			checkEnable: true,
			loadEnable: true,//异步加载
			showLine: false,
			chkStyle: 'radio',
			showIcon: true,
			onDblClick: function(){
			},
			onAsyncSuccess: function(id){
			}
		}, function(id){
			ztree = $.fn.zTree.getZTreeObj(id);
		});
		
		matchingLanguage();
		var saveBtn = true;
		//保存
		$("body").on("click", "#saveBtn", function(){
			if(saveBtn){
				var node = ztree.getCheckedNodes();
				if(isNull(node)){//如果节点为空
					winui.window.msg("请选择目录。", {icon: 2,time: 2000});
				}else{
					var jsonStr = JSON.stringify(parent.chooseSaveIds);
					var folderId = node[0].id;
					var params = {
						folderId: folderId,
						jsonStr: jsonStr
					};
					saveBtn = false;
					winui.window.msg("文件正在保存，期间请勿进行其他操作。", {icon: 7,time: 4000});
					AjaxPostUtil.request({url:reqBasePath + "fileconsole023", params: params, type:'json', callback:function(json){
						saveBtn = true;
						if(json.returnCode == 0){
							parent.layer.close(index);
							parent.refreshCode = '0';
						}else{
							winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
						}
					}});
				}
			}else{
				winui.window.msg("文件正在保存，期间请勿进行其他操作。", {icon: 7,time: 4000});
			}
		});
		
		/********* tree 处理   end *************/
		
	});
});

