
var folderId = "1";//当前树所在文件夹目录的位置
var folderName = "";//当前树所在文件夹目录的位置名称
var fileType = "";//表格编辑时的文件类型
var fileId = "";//表格编辑时的文件id

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'fsCommon', 'fsTree', 'viewer', 'contextMenu'], function (exports) {
	winui.renderColor();
	
	var $ = layui.jquery,
		fsTree = layui.fsTree,
		fsCommon = layui.fsCommon,
		table = layui.table;
	
	//模板对象
	var treeNorightTemplate = $('#treeNorightTemplate').html(),
		treerightTemplate = $("#treerightTemplate").html();
	
	/********* tree 处理   start *************/
	var ztree = null;
	fsTree.render({
		id: "treeDemo",
		url: "sysenclosure001?userToken=" + getCookie('userToken') + "&loginPCIp=" + returnCitySN["cip"],
		checkEnable: false,
		loadEnable: true,//异步加载
		showLine: false,
		showIcon: true,
		clickCallback: zTreeOnClick,
		onDblClick: function(){
		},
		onAsyncSuccess: function(id){
		}
	}, function(id){
		ztree = $.fn.zTree.getZTreeObj(id);
		loadThisFolderChild();//加载子文件夹
	});
	
	/**
	 * 点击事件
	 */
	function zTreeOnClick(event, treeId, treeNode) {
		if(treeNode.id != folderId){
			folderId = treeNode.id;
			loadThisFolderChild();
		}
		//展开
		if(!treeNode.open){//如果节点不展开，则展开
			ztree.expandNode(treeNode);
		}
	};
	
	/********* tree 处理   end *************/
	
	
	//默认初始化树
	var initTreeSel = false;
	
	/**
	 * 加载子文件夹过度动画
	 */
	function loadThisFolderChild(){
		setTimeout(function(){
			loadThisFolderChildData();
		}, 200);
	}
	
	/**
	 * 加载子文件夹
	 */
	function loadThisFolderChildData(){
		
		if(!initTreeSel){
			initTreeSel = true;
			ztree.expandNode(ztree.getNodeByParam("id", folderId, null));//展开指定节点-我的文档
			ztree.selectNode(ztree.getNodeByParam("id", folderId, null));//选中指定节点-我的文档
			table.render({
			    id: 'messageTable',
			    elem: '#messageTable',
			    method: 'post',
			    url: reqBasePath + 'sysenclosure004',
			    where: {parentId: folderId},
			    even: true,
			    page: false,
			    cols: [[
			        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			        { field: 'name', title: '文件名', align: 'left', width: 400 },
			        { field: 'fileSize', title: '文件大小', align: 'center', width: 120},
			        { field: 'createTime', title: '上传时间', align: 'center', width: 150},
			        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
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
		        }else if (layEvent === 'download'){ //下载
		        	download(fileBasePath + data.fileAddress, data.name);
		        }
		    });
		}else{
			table.reload("messageTable", {where:{parentId: folderId}});
		}
		
	}
	
	//编辑
	function edit(data){
		var node = ztree.getNodeByParam("id", folderId, null);
		folderName = getFilePath(node);
		fileType = data.fileType;
		fileId = data.id;
		var _title = "";
		if(data.fileType === 'folder'){
			_title = "编辑文件夹";
		}else{
			_title = "编辑文件";
		}
		_openNewWindows({
			url: "../../tpl/sysenclosure/editenclosure.html", 
			title: _title,
			pageId: "editenclosurepage",
			area: ['400px', '200px'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	refreshTreePointNode();
        			loadThisFolderChild();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
	
	//刷新树指定节点
	function refreshTreePointNode(){
		//刷新节点
	    var nownode = ztree.getNodesByParam("id", folderId, null);  
	    ztree.reAsyncChildNodes(nownode[0], "refresh");
	}
	
	//新增文件夹
	$("body").on("click", "#addFolderBean", function(e){
		var node = ztree.getNodeByParam("id", folderId, null);
		folderName = getFilePath(node);
		if(folderName.split('/').length >= 4){
			winui.window.msg("只允许添加两层级别的文件夹", {icon: 5,time: 2000});
			return;
		}
		_openNewWindows({
			url: "../../tpl/sysenclosure/addenclosure.html", 
			title: "新增文件夹",
			pageId: "addenclosurepage",
			area: ['400px', '200px'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	refreshTreePointNode();
        			loadThisFolderChild();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	});
	
	//上传附件
	$("body").on("click", "#addSenclosureBean", function(e){
		_openNewWindows({
			url: "../../tpl/sysenclosure/enclosureupload.html", 
			title: "上传附件",
			pageId: "enclosureuploadpage",
			area: ['400px', '350px'],
			callBack: function(refreshCode){
    			loadThisFolderChild();
			}});
	});
	
	//刷新数据
	$("body").on("click", "#reloadTable", function(e){
		refreshTreePointNode();
		loadThisFolderChild();
	});
	
	//获取指定节点的所有父节点的名字
	function getFilePath(treeObj) {
        if (treeObj == null) return "";
        var filename = treeObj.name + '/';
        var pNode = treeObj.getParentNode();
        if (pNode != null) {
            filename = getFilePath(pNode) + filename;
        }
        return filename;
    }
	
    exports('myNote', {});
});
