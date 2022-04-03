
var folderId = "2";//当前所在文件夹目录的位置

var operaterId = "";//即将进行删除，重命名等操作的id

var fileUrl = "";//要查看的文件路径

var fileThumbnail = "";//要查看的文件缩略图

var selFileType = "";//要查看的文件类型

var shareId = "";//分享id，查看属性的id

var pastedText = "";//粘贴板内容

var folderTemplate;

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'dropdown', 'fsCommon', 'fsTree', 'viewer', 'contextMenu', 'ClipboardJS', 'colorpicker', 'jqueryUI', 'webuploader'], function (exports) {
	winui.renderColor();
	var clipboard;//复制对象
	var clipboardcut;//剪切对象
	var $ = layui.$,
		fsTree = layui.fsTree,
		fsCommon = layui.fsCommon,
		colorpicker = layui.colorpicker,
		device = layui.device();

	//遮罩层显示
	$(".fileconsole-mask").show();
	matchingLanguage();
	
	//文件模板
	folderTemplate = $('#folderTemplate').html();
	var imagesTemplate = $('#imagesTemplate').html(),
		officeTemplate = $('#officeTemplate').html(),
		vedioTemplate = $('#vedioTemplate').html(),
		packageTemplate = $('#packageTemplate').html(),
		aceTemplate = $("#aceTemplate").html(),
		otherTemplate = $("#otherTemplate").html(),
		epubTemplate = $("#epubTemplate").html();
		
	//下拉按钮
	var dropdown = new Dropdown();
	dropdown.render();
	
	var ztree = null;
	fsTree.render({
		id: "treeDemo",
		url: reqBasePath + "fileconsole001?userToken=" + getCookie('userToken') + "&loginPCIp=" + returnCitySN["cip"],
		checkEnable: false,
		loadEnable: true,//异步加载
		showLine: false,
		showIcon: true,
		addDiyDom: addDiyDom,
		clickCallback: zTreeOnClick,
		onRightClick: onRightClick,
		onDblClick: function(){
		},
		onAsyncSuccess: function(id){
			// 初始化，有东西拖到ztree上时，调用回调函数
			$("#treeDemo").droppable({
				drop: function(event, ui) {
					// 获取目标文件夹
			  		var targetElement = $("#treeDemo .mouseOver");
			  		// 移动文件
		  			moveFile(targetElement.attr("ztreerowid"), ui, null);
				}
			});
		}
	}, function(id){
		ztree = $.fn.zTree.getZTreeObj(id);
		loadThisFolderChild();
	});
	
	function addDiyDom(treeId, treeNode) {
		// css样式以及位置调整
		var spaceWidth = 8;
        var switchObj = $("#" + treeNode.tId + "_switch"),
        icoObj = $("#" + treeNode.tId + "_ico");
        switchObj.remove();
        icoObj.before(switchObj);
        if (treeNode.level >= 1) {
            var spaceStr = "<span style='display: inline-block;width:" + (spaceWidth * treeNode.level) + "px'></span>";
            switchObj.before(spaceStr);
        }
        
        // 操作数据
		var aObj = $("#" + treeNode.tId + "_a");
		if ($("#diyBtn_" + treeNode.id).length > 0) return;
		aObj.after("");
		aObj.addClass("tree_a");
		aObj.attr("ztreerowid", treeNode.id);
	}
	
	// 树节点右键
	function onRightClick(event, treeId, treeNode) {
		folderId = treeNode.id;
		// 设置右键节点选中
		chooseNodeSelect(folderId);
		var par = {
			id: folderId,
			name: treeNode.name
		};
		$("#treeRight").html(getDataUseHandlebars($("#treeRightTemplate").html(), {bean: par}));
		if(folderId === '1' || folderId === '2' || folderId === '3'){
			showRMenu('root', event.clientX, event.clientY);
		}else{
			showRMenu('node', event.clientX, event.clientY);
		}
	}
	
	// 展示树节点右键菜单
	function showRMenu(type, x, y) {
		$("#treeRight .is-file").show();
		if(type == 'root'){
			$("#treeRight .copyUrl").hide();
			$("#treeRight .cutUrl").hide();
			$("#treeRight .treedeleteFolderAndChildToRecycle").hide();
			$("#treeRight .treedeleteFolderAndChild").hide();
			$("#treeRight .treeAddToFavorites").hide();
			$("#treeRight .treeFilePropertyMation").hide();
		}
		$("#treeRight").show();
		$("#treeRight").css({top: y + "px", left: x + "px", visibility: "visible", position: "absolute"});
		$("body").bind("mousedown", onBodyMouseDown);
		// 初始化复制剪切功能
		initClipEvent();
	}
	
	// 隐藏树节点右键菜单
	function hideRMenu() {
		if ($("#treeRight")) $("#treeRight").css({"visibility": "hidden"});
		$("body").unbind("mousedown", onBodyMouseDown);
	}
	
	function onBodyMouseDown(event){
		if (!($(event.target).parents(".is-file").length>0)) {
			$("#treeRight").css({"visibility" : "hidden"});
		}
	}
	
	// 刷新树指定节点
	function refreshTreePointNode(){
		// 刷新节点
	    var nownode = ztree.getNodesByParam("id", folderId, null);  
	    ztree.reAsyncChildNodes(nownode[0], "refresh");
	}
	
	// 树的动画效果
	$("body").on("mouseover", "#treeDemo a", function(e){
		$("#treeDemo").find("a").removeClass('mouseOver');
		$(this).addClass('mouseOver');
	});
	$("body").on("mouseleave", "#treeDemo", function(e){
		$("#treeDemo").find("a").removeClass('mouseOver');
	});
	
	// 设置选中节点
	function chooseNodeSelect(nodeId){
		var selNode = ztree.getNodeByParam("id", nodeId, null);
		ztree.selectNode(selNode);
	}
	
	// 右侧内容右键操作
	$("body").on("contextmenu", "#file-content .file", function(e){
		var _this = $(this);
		operaterId = _this.attr("rowid");
		// 1.加载右侧右键内容
		initFileContentRight(_this, e);
		// 2.加载选中
		setChooseFileContent(_this);
	});
	
	// 加载右侧右键内容
	function initFileContentRight(_this, e){
		var par = {
			id: _this.attr("rowid"),
			name: _this.find(".title").html(),
			fileType: _this.attr("filetype")
		};
		$("#fileRightMenu").show();
		$("#fileRightMenu").html(getDataUseHandlebars($("#fileRightMenuTemplate").html(), {bean: par}));
		$("#fileRightMenu").css({top: e.clientY + "px", left: e.clientX + "px", visibility: "visible", position: "absolute"});
		if($.inArray(_this.attr("filetype"), officeType) == -1){
			$(".openByOnlyOffice").hide();
			$(".openByMicrosoftOffice").hide();
		}
		// 初始化复制剪切功能
		initClipEvent();
	}
	
	// 加载选中
	function setChooseFileContent(_this){
		$(".select-op-more").find("button[class*='btn-custom']").show();
		$(".select-op-more").find("li[class*='is-file']").show();
		$(".select-op-more").show();
		$("#file-content").find("div").removeClass("active");
		// 获取文件创建人
		var createId = _this.attr("createId");
		var fileType = _this.attr("filetype");
		if($.inArray(fileType, packageType) >= 0){//压缩包
			$(".packageOpteare").show();
		}else{
			$(".packageOpteare").hide();
		}
		// 权限控制
		authControllerFile(createId);
		// 其他选中取消
		$("#file-content .menu-folder .item-select .item-check").find("input:checkbox[name='checkFile']:checked").prop("checked", false);
		// 添加选中样式
		_this.addClass("active");
		// 设置当前选中
		_this.children(".item-select").children(".item-check").find("input:checkbox[name='checkFile']").prop("checked", true);
	}
	
	// 初始化赋值剪切功能
	function initClipEvent(){
		// 复制
    	clipboard = new ClipboardJS('.copyUrl');
    	clipboard.on('success', function(e) {
    		$("#fileRightMenu").hide();
    		hideRMenu();
    		pastedText = e.text;
    		winui.window.msg("复制成功", {icon: 1,time: 2000});
    	});
    	clipboard.on('error', function(e) {
    		winui.window.msg("浏览器不支持！", {icon: 2,time: 2000});
    	});
    	
    	// 剪切
    	clipboardcut = new ClipboardJS('.cutUrl');
    	clipboardcut.on('success', function(e) {
    		$("#fileRightMenu").hide();
    		hideRMenu();
    		pastedText = e.text;
    		winui.window.msg("剪切成功", {icon: 1,time: 2000});
    	});
    	clipboardcut.on('error', function(e) {
    		winui.window.msg("浏览器不支持！", {icon: 2,time: 2000});
    	});
	}
	
	// 树节点点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		var folderName = getFilePath(treeNode);
		$(".yarnball").html('<li class="yarnlet first"><a title="/">/</a></li>' + folderName);
		if(treeNode.id != folderId){
			folderId = treeNode.id;
			loadThisFolderChild();
		}
		// 如果节点不展开，则展开
		if(!treeNode.open){
			ztree.expandNode(treeNode);
		}
	};
	
	// 双击文件
	$("body").on("dblclick", "#file-content .file", function(e){
		var _this = $(this);
		var fileType = _this.attr("filetype");
		if(fileType === 'folder'){//文件夹
			folderId = $(this).attr("rowid");
			var title = $(this).attr("title").replace("名称:", "");
			// 加载目录
			initFolderName(folderId);
			// 加载目录下的文件
			loadThisFolderChild();
		}else if($.inArray(fileType, vedioType) >= 0){//视频
			AjaxPostUtil.request({url:reqBasePath + "fileconsole009", params:{rowId: $(this).attr("rowid")}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				fileUrl = json.bean.fileAddress;
    				fileThumbnail = json.bean.fileThumbnail;
    				_openNewWindows({
    					url: "../../tpl/fileconsole/vedioshow.html", 
    					title: json.bean.fileName,
    					pageId: "vedioShow",
    					area: ['80vw', '80vh'],
    					callBack: function(refreshCode){}});
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		}else if($.inArray(fileType, officeType) >= 0){//office文件
			var thisId = $(this).attr("rowid");
			AjaxPostUtil.request({url:reqBasePath + "fileconsole009", params:{rowId: thisId}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				fileUrl = json.bean.fileAddress;
    				selFileType = json.bean.fileType;
    				window.open(reqBasePath + "/tpl/fileconsole/officeshow.html?fileUrl=" + fileUrl + "&selFileType=" + selFileType + "&title=" + json.bean.fileName + "&thisId=" + thisId);
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		}else if($.inArray(fileType, aceType) >= 0){//ace文件
			var thisId = $(this).attr("rowid");
			_openNewWindows({
				url: reqBasePath + "fileconsole024?rowId=" + thisId, 
				title: '在线预览',
				pageId: "aceShow",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){}});
		}else if($.inArray(fileType, epubType) >= 0){//电子书
			AjaxPostUtil.request({url:reqBasePath + "fileconsole009", params:{rowId: $(this).attr("rowid")}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				fileUrl = json.bean.fileAddress;
    				fileThumbnail = json.bean.fileThumbnail;
    				_openNewWindows({
    					url: "../../tpl/fileconsole/epubShow.html", 
    					title: json.bean.fileName,
    					pageId: "epubShow",
    					area: ['90vw', '90vh'],
    					callBack: function(refreshCode){}});
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		}else if($.inArray(fileType, packageType) >= 0){//压缩包
			AjaxPostUtil.request({url:reqBasePath + "fileconsole009", params:{rowId: $(this).attr("rowid")}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				fileUrl = json.bean.fileAddress;
    				fileThumbnail = json.bean.fileThumbnail;
    				_openNewWindows({
    					url: "../../tpl/fileconsole/zipShow.html", 
    					title: '<img src="../../assets/images/rar.png" class="fly-img"/>' + json.bean.fileName,
    					pageId: "epubShow",
    					area: ['630px', '450px'],
    					skin: "zip-show",
    					callBack: function(refreshCode){}});
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		}
	});
	
	// 文件夹或者文件重命名
	$("body").on("click", ".fileReName", function(e){
		reNameSpecially();
		$(".layui-dropdown-menu").hide();
	});
	
	// 文件名保存
	$("body").on("click", ".filename-edit-save", function(e){
		if(isNull($(this).parent().find('textarea').val())){
			winui.window.msg('请填写文件名', {icon: 2,time: 2000});
		}else{
			$(this).parent().parent().css({"z-index": 0});
			editFolderById($(this).parent());
		}
	});
	
	// 文件名取消修改
	$("body").on("click", ".filename-edit-cancle", function(e){
		$(this).parent().parent().css({"z-index": 0});
		$(this).parent().parent().find("div[class='filename']").show();
		$(this).parent().hide();
	});

	// 路径目录点击
	$("body").on("click", ".yarnball .folderName", function(e){
		folderId = $(this).attr("rowid");
		// 加载目录
		initFolderName(folderId);
		// 加载该目录下的文件
		loadThisFolderChild();
	});
	
	// 加载目录
	function initFolderName(folderId){
		var node = ztree.getNodeByParam("id", folderId, null);
		var folderName = "";
		if(!isNull(node)){
			// 如果节点不展开，则展开
			if(!node.open){
				ztree.expandNode(node);
			}
			chooseNodeSelect(folderId);
			folderName = getFilePath(node);
		}else{
			folderName = '<li class="yarnlet folderName"><a title="/">请刷新目录节点/</a></li>';
		}
		$(".yarnball").html('<li class="yarnlet first"><a title="/">/</a></li>' + folderName);
	}
	
	// 文件夹或者文件删除
	$("body").on("click", ".deleteFolderAndChild", function(e){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
			var checkItems = $("#file-content .menu-folder .item-select .item-check").find("input:checkbox[name='checkFile']:checked");
			var deleteArray = new Array();
			$.each(checkItems, function(i, item){
				var checkFile = $(item).parent().parent().parent();
				deleteArray.push({
					rowId: checkFile.attr("rowid"),
					fileType: checkFile.attr("filetype")
				});
			});
			$(".layui-dropdown-menu").hide();
			$(".select-op-more").hide();
			//如果选中项为空
			if(deleteArray.length == 0){
				winui.window.msg('请选择要删除的文件或文件夹', {icon: 2,time: 2000});
				return false;
			}
            deleteFolderAndChild(deleteArray, function(){
            	// 刷新节点
   				refreshTreePointNode();
   				// 加载该目录下的文件
				loadThisFolderChild();
            });
		});
	});
	
	// 删除文件夹以及该文件夹下的所有子内容
	function deleteFolderAndChild(deleteArray, callBack){
		AjaxPostUtil.request({url:reqBasePath + "fileconsole004", params:{fileList: JSON.stringify(deleteArray)}, type: 'json', callback: function(json){
			if(json.returnCode == 0){
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
				callBack();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
	// 放入回收站
	$("body").on("click", ".deleteFolderAndChildToRecycle", function(e){
		layer.confirm("确定将该文件夹及其子文件放入回收站吗?", { icon: 3, title: '回收站' }, function (index) {
			layer.close(index);
			$(".layui-dropdown-menu").hide();
			$(".select-op-more").hide();
			var fileType = $("#file-content div[rowid='" + operaterId + "']").attr("filetype");
            AjaxPostUtil.request({url:reqBasePath + "fileconsole013", params:{rowId: operaterId}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("已放入回收站", {icon: 1,time: 2000});
    				$("#file-content div[rowid='" + operaterId + "']").remove();
    				// 如果删除的对象是文件夹
    				if(fileType === 'folder'){
    					var selNode = ztree.getNodeByParam("id", operaterId, null);
        				ztree.removeNode(selNode);
    				}
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	});
	
	var chooseSaveIds = new Array();//选择的要保存呢的文件或者文件夹id
	// 创建副本
	$("body").on("click", ".createDuplicate", function(e){
		$(".layui-dropdown-menu").hide();
		$(".select-op-more").hide();
		var fileType = $("#file-content div[rowid='" + operaterId + "']").attr("filetype");
		chooseSaveIds.splice(0, chooseSaveIds.length);
		var params = {
			rowId: operaterId,
			rowType: fileType
		};
		chooseSaveIds.push(params);
		var jsonStr = JSON.stringify(chooseSaveIds);
		var params = {
			folderId: folderId, 
			jsonStr: jsonStr
		};
		winui.window.msg("文件副本创建中，期间请勿进行其他操作。", {icon: 7,time: 4000});
		AjaxPostUtil.request({url: reqBasePath + "fileconsole030", params: params, type: 'json', callback: function(json){
			if(json.returnCode == 0){
				winui.window.msg("副本创建成功", {icon: 1,time: 2000});
				if(fileType === 'folder'){//文件夹
					//刷新节点
	   				refreshTreePointNode();
				}
				loadThisFolderChild();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	});
	
	// 属性
	$("body").on("click", ".filePropertyMation", function(e){
		$(".layui-dropdown-menu").hide();
		shareId = operaterId;
		_openNewWindows({
			url: "../../tpl/fileconsole/fileMation.html", 
			title: "属性",
			pageId: "fileMation",
			area: ['400px', '350px'],
			skin: 'add-schedule-mation',
			callBack: function(refreshCode){
			}});
	});
	
	var choosePackageIds = new Array();//选择的要保存呢的文件或者文件夹id
	// 打包压缩包
	$("body").on("click", ".createComPackage", function(e){
		$(".layui-dropdown-menu").hide();
		$(".select-op-more").hide();
		var list = $("#file-content .menu-folder .item-select .item-check").find("input:checkbox[name='checkFile']:checked");
		if(list.length == 0){
			winui.window.msg("请至少选择一个文件或文件夹进行打包。", {icon: 2,time: 2000});
			return;
		}
		choosePackageIds.splice(0, choosePackageIds.length);
		$.each(list, function(i, item){
			var thisRowId = $(this).parent().parent().parent().attr("rowid");
			var thisRowType = $(this).parent().parent().parent().attr("filetype");
			var params = {
				rowId: thisRowId,
				rowType: thisRowType
			};
			choosePackageIds.push(params);
		});
		
		var jsonStr = JSON.stringify(choosePackageIds);
		var params = {
			folderId: folderId, 
			jsonStr: jsonStr
		};
		winui.window.msg("文件打包中，期间请勿进行其他操作.", {icon: 7,time: 4000});
		AjaxPostUtil.request({url: reqBasePath + "fileconsole032", params: params, type: 'json', callback: function(json){
			if(json.returnCode == 0){
				winui.window.msg("打包成功.", {icon: 1,time: 2000});
				loadThisFolderChild();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	});
	
	// 通过onlyoffice打开office文件
	$("body").on("click", ".openByOnlyOffice", function(e){
		var thisId = operaterId;
		$(".layui-dropdown-menu").hide();
		AjaxPostUtil.request({url: reqBasePath + "fileconsole009", params:{rowId: thisId}, type: 'json', callback: function(json){
			if(json.returnCode == 0){
				fileUrl = json.bean.fileAddress;
				selFileType = json.bean.fileType;
				window.open(reqBasePath + "/tpl/fileconsole/officeshow.html?fileUrl=" + fileUrl + "&selFileType=" + selFileType + "&title=" + json.bean.fileName + "&thisId=" + thisId);
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	});
	
	// 通过微软office打开office文件
	$("body").on("click", ".openByMicrosoftOffice", function(e){
		$(".layui-dropdown-menu").hide();
		AjaxPostUtil.request({url: reqBasePath + "fileconsole009", params:{rowId: operaterId}, type: 'json', callback: function(json){
			if(json.returnCode == 0){
				window.open('https://view.officeapps.live.com/op/view.aspx?src=http://gzwp.free.idcfengye.com/' + json.bean.fileAddress);
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	});
	
	// 文件或者文件夹下载
	$("body").on("click", ".fileDownLoad", function(e){
		$(".layui-dropdown-menu").hide();
		var list = $("#file-content .menu-folder .item-select .item-check").find("input:checkbox[name='checkFile']:checked");
		if(list.length == 0){
			winui.window.msg("请至少选择一个文件或文件夹。", {icon: 2,time: 2000});
			return;
		}
		//判断下载项中是否包含文件夹
		var hasFolder = false;
		$.each(list, function(i, item){
			if($(item).parent().parent().parent().attr("filetype") === 'folder'){
				hasFolder = true;
				return false;
			}
		});
		//多个文件下载或者单个文件夹下载
		if(hasFolder || list.length > 1){
			choosePackageIds.splice(0, choosePackageIds.length);
			$.each(list, function(i, item){
				var thisRowId = $(this).parent().parent().parent().attr("rowid");
				var thisRowType = $(this).parent().parent().parent().attr("filetype");
				var params = {
					rowId: thisRowId,
					rowType: thisRowType
				};
				choosePackageIds.push(params);
			});
			
			var jsonStr = JSON.stringify(choosePackageIds);
			var params = {
				jsonStr: jsonStr
			};
			winui.window.msg("文件打包中，期间请勿进行其他操作.", {icon: 7,time: 4000});
			AjaxPostUtil.request({url: reqBasePath + "fileconsole038", params: params, type: 'json', callback: function(json){
				if(json.returnCode == 0){
					winui.window.msg("打包成功，开始下载.", {icon: 1,time: 2000});
					if(isNull(json.bean) || isNull(json.bean.fileAddress)){
						winui.window.msg('打包失败', {icon: 2,time: 2000});
					}else{
						// 下载
						download(json.bean.fileAddress, '压缩包');
					}
				}else{
					winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				}
			}});
		}else{
			//不包含文件夹
			AjaxPostUtil.request({url:reqBasePath + "fileconsole009", params:{rowId: operaterId}, type: 'json', callback: function(json){
				if(json.returnCode == 0){
					if($.inArray(json.bean.fileType, imageType) >= 0){//图片
						downloadImage(fileBasePath + json.bean.fileAddress, json.bean.fileName);
					}else{
						download(json.bean.fileAddress, json.bean.fileName);
					}
				}else{
					winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				}
			}});
		}
	});
	
	// 压缩包解压到当前
	$("body").on("click", ".unzipToTheCurrent", function(e){
		layer.confirm("确定解压该压缩包到当前文件夹吗?", { icon: 3, title: '压缩包解压' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "fileconsole033", params:{rowId: operaterId}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("解压成功。", {icon: 1,time: 2000});
    				refreshTreePointNode();
    				loadThisFolderChild();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	});
	
	// 文件夹或者文件取消选中
	$("body").on("click", ".cancleChoose", function(e){
		$(".select-op-more").hide();
		$("#file-content div").removeClass("active");
		$(".layui-dropdown-menu").hide();
		// 设置多选框取消选中
		$("#file-content .menu-folder .item-select .item-check").find("input:checkbox[name='checkFile']:checked").prop("checked", false);
		e.stopPropagation();//阻止冒泡
	});
	
	// 刷新文件夹内容
	$("body").on("click", ".refreshContent", function(e){
		loadThisFolderChild();
	});
	
	// 上传文件
	$("body").on("click", ".uploadFileBtn", function(e){
		_openNewWindows({
			url: "../../tpl/fileconsole/fileconsoleupload.html", 
			title: "文件上传",
			pageId: "fileconsoleupload",
			area: ['400px', '350px'],
			skin: 'add-schedule-mation',
			callBack: function(refreshCode){
				loadFileSizeCS = false;//重置让系统加载文件大小
				loadThisFolderChild();
			}});
	});
	
	// 上传文件夹
	$("body").on("click", ".uploadFileFolderBtn", function(e){
		_openNewWindows({
			url: "../../tpl/fileconsole/filefolderupload.html", 
			title: "文件夹上传",
			pageId: "filefolderupload",
			area: ['400px', '350px'],
			skin: 'add-schedule-mation',
			callBack: function(refreshCode){
				loadFileSizeCS = false;//重置让系统加载文件大小
				loadThisFolderChild();
			}});
	});
	
	// 回收站
	$("body").on("click", "#recycleBinList", function(e){
		_openNewWindows({
			url: "../../tpl/fileconsole/recycleBinList.html", 
			title: "回收站",
			pageId: "recycleBinListPage",
			area: ['650px', '480px'],
			skin: 'add-schedule-mation',
			callBack: function(refreshCode){
				if (refreshCode == '0') {
					if(folderId === '2'){
						refreshTreePointNode();
						loadThisFolderChild();
					}
				}
			}});
	});
	
	// 我的分享
	$("body").on("click", "#shareFileList", function(e){
		_openNewWindows({
			url: "../../tpl/fileconsole/shareFileList.html", 
			title: "我的分享",
			pageId: "shateFileListPage",
			area: ['700px', '480px'],
			skin: 'add-schedule-mation',
			callBack: function(refreshCode){}});
	});
	
	// 分享
	$("body").on("click", ".shareFileBtn", function(e){
		shareId = operaterId;
		_openNewWindows({
			url: "../../tpl/fileconsole/shareFile.html", 
			title: "分享",
			pageId: "shareFile",
			area: ['500px', '400px'],
			skin: 'add-schedule-mation',
			callBack: function(refreshCode){
				loadThisFolderChild();
			}});
	});
	
	// 在线预览类型
	$("body").on("click", "#showFileInLine", function(e){
		_openNewWindows({
			url: "../../tpl/fileconsole/showFileInLine.html", 
			title: "可支持的在线预览类型",
			pageId: "showFileInLinePage",
			area: ['650px', '480px'],
			skin: 'add-schedule-mation',
			callBack: function(refreshCode){}});
	});
	
	// 新建文件夹
	$("body").on("click", "#createNewFolder", function(e){
		hideRMenu();
		createFolder(folderId, refreshTreePointNode, initMenuToBox);
	});
	
	// 树操作--新建文件夹
	$("body").on("click", ".treecreateNewFolder", function(e){
		hideRMenu();
		loadThisFolderChild();
		createFolder(folderId, refreshTreePointNode, initMenuToBox);
	});
	
	// 树操作--刷新
	$("body").on("click", ".treerefreshContent", function(e){
		refreshTreePointNode();
		loadThisFolderChild();
	});
	
	// 树操作--放入回收站
	$("body").on("click", ".treedeleteFolderAndChildToRecycle", function(e){
		layer.confirm("确定将该文件夹及其子文件放入回收站吗?", { icon: 3, title: '回收站' }, function (index) {
			layer.close(index);
			var fileType = 'folder';
            AjaxPostUtil.request({url:reqBasePath + "fileconsole013", params:{rowId: folderId}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("已放入回收站", {icon: 1,time: 2000});
    				$("#file-content div[rowid='" + folderId + "']").remove();
    				var selNode = ztree.getNodeByParam("id", folderId, null);
    				chooseNodeSelect(folderId);
    				//重置folderid
    				folderId = selNode.getParentNode().id;
    				ztree.removeNode(selNode);//移除节点
    				loadThisFolderChild();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	});
	
	// 树操作--粘贴
	$("body").on("click", ".treePaste", function(e){
		pasteFile();
	});
	
	// 树操作--文件夹或者文件删除
	$("body").on("click", ".treedeleteFolderAndChild", function(e){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
			var deleteArray = new Array();
			deleteArray.push({
				rowId: folderId,
				fileType: 'folder'
			});
			deleteFolderAndChild(deleteArray, function(){
				$("#file-content div[rowid='" + folderId + "']").remove();
				var selNode = ztree.getNodeByParam("id", folderId, null);
				chooseNodeSelect(folderId);
				// 重置folderid
				folderId = selNode.getParentNode().id;
				ztree.removeNode(selNode);// 移除节点
				loadThisFolderChild();
			});
		});
	});
	
	// 树操作--文件夹或者文件分享
	$("body").on("click", ".treeShareFolderAndChild", function(e){
		shareId = folderId;
		hideRMenu();
		_openNewWindows({
			url: "../../tpl/fileconsole/shareFile.html", 
			title: "分享",
			pageId: "shareFile",
			area: ['500px', '400px'],
			skin: 'add-schedule-mation',
			callBack: function(refreshCode){
				loadThisFolderChild();
			}});
	});
	
	//树操作--属性
	$("body").on("click", ".treeFilePropertyMation", function(e){
		$(".layui-dropdown-menu").hide();
		shareId = folderId;
		_openNewWindows({
			url: "../../tpl/fileconsole/fileMation.html", 
			title: "属性",
			pageId: "fileMation",
			area: ['400px', '350px'],
			skin: 'add-schedule-mation',
			callBack: function(refreshCode){}});
	});
	
	var orderBy = '3';
	//排序方式
	$("body").on("click", ".orderBy", function(e){
		if($(this).attr("data-type") != orderBy){
			orderBy = $(this).attr("data-type");
			$(".orderBy").find("img").attr("src", "");
			$(this).find("img").attr("src", "../../assets/images/icon_choose.png");
			loadThisFolderChild();
		}
	});
	
	colorpicker.render({
	    elem: '#fontColorStyle',
	    color: '#FFFFFF',
	    done: function(color){
	        $("#fontColorStyleCss").html('span{color: ' + color + ' !important}button{color: ' + color + ' !important;text-shadow:none !important;}a{color: ' + color + ' !important}i{color: ' + color + ' !important}');
	    },
	    change: function(color){
	    	$("#fontColorStyleCss").html('span{color: ' + color + ' !important}button{color: ' + color + ' !important;text-shadow:none !important;}a{color: ' + color + ' !important}i{color: ' + color + ' !important}');
	    }
	});
	
	// 创建word空文件
	$("body").on("click", "#createWordFile", function(e){
		createWordFile(folderId, loadThisFolderChild());
	});
	
	// 创建excel空文件
	$("body").on("click", "#createExcelFile", function(e){
		createExcelFile(folderId, loadThisFolderChild());
	});
	
	// 创建ppt空文件
	$("body").on("click", "#createPPTFile", function(e){
		createPPTFile(folderId, loadThisFolderChild());
	});
	
	// 创建txt空文件
	$("body").on("click", "#createTXTFile", function(e){
		createTXTFile(folderId, loadThisFolderChild());
	});
	
	// 创建html空文件
	$("body").on("click", "#createHtmlFile", function(e){
		createHtmlFile(folderId, loadThisFolderChild());
	});
	
	//编辑文件夹目录--保存
	//filename-edit对象
	function editFolderById(fileFolderEdit){
		//ajax请求之后的操作
		var fileType = fileFolderEdit.parent().attr("filetype");
		var title = fileFolderEdit.parent().find("div[class='filename']").find("span").html();//获取修改前的名称
		var newName = fileFolderEdit.find("textarea").val();
		if(fileType != 'folder'){
			newName = newName + "." + fileType;
		}
		if(newName != title){//修改前的名称和当前名称不一致，则调用后台接口进行修改
			var id = fileFolderEdit.parent().attr("rowid");
			AjaxPostUtil.request({url:reqBasePath + "fileconsole005", params: {rowId: id, catalogName: newName, fileType: fileType}, type: 'json', callback: function(json){
				if(json.returnCode == 0){
					fileFolderEdit.parent().find("div[class='filename']").show();
					fileFolderEdit.parent().find("div[class='filename']").find("span").html(newName);
					fileFolderEdit.parent().attr("title", "名称:" + newName);
					fileFolderEdit.hide();
					if(fileType === 'folder'){//如果是文件夹
						//刷新树节点
						refreshTreePointNode();
					}
				}else{
					winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				}
			}});
		}else{//如果一致
			fileFolderEdit.parent().find("div[class='filename']").show();
			fileFolderEdit.hide();
		}
	}
	
	// 图片展示对象
	var photoShowObj;
	
	// 默认初始化树
	var initTreeSel = false;
	
	// 加载指定目录下的文件和目录
	function loadThisFolderChild(){
		// 隐藏右上角菜单和树形菜单
		$(".layui-dropdown-menu").hide();
		$(".select-op-more").hide();
		// 遮罩层显示
		$(".fileconsole-mask").show();
		setTimeout(function(){
			loadThisFolderChildList();
		}, 10);
	}
	
	function loadThisFolderChildList(){
		AjaxPostUtil.request({url:reqBasePath + "fileconsole003", params: {folderId: folderId, orderBy: orderBy}, type: 'json', callback: function(j){
			// 遮罩层隐藏
			$(".fileconsole-mask").hide();
   			if(j.returnCode == 0){
   				var jsonStr = "";//实体json对象
   				var str = "";
   				$.each(j.rows, function(i, item){
					item["fileBasePath"] = fileBasePath;
   					jsonStr = {
						bean: item
					};
   					if(item.fileType === 'folder'){//文件夹
   						str += getDataUseHandlebars(folderTemplate, jsonStr);
   					}else if($.inArray(item.fileType, imageType) >= 0){//图片
   						str += getDataUseHandlebars(imagesTemplate, jsonStr);
   					}else if($.inArray(item.fileType, officeType) >= 0){//office
   						str += getDataUseHandlebars(officeTemplate, jsonStr);
   					}else if($.inArray(item.fileType, vedioType) >= 0){//视频
   						str += getDataUseHandlebars(vedioTemplate, jsonStr);
   					}else if($.inArray(item.fileType, packageType) >= 0){//压缩包
   						str += getDataUseHandlebars(packageTemplate, jsonStr);
   					}else if($.inArray(item.fileType, aceType) >= 0){//ace文件
   						str += getDataUseHandlebars(aceTemplate, jsonStr);
   					}else if($.inArray(item.fileType, epubType) >= 0){//电子书
   						str += getDataUseHandlebars(epubTemplate, jsonStr);
   					}else{//其他文件
   						str += getDataUseHandlebars(otherTemplate, jsonStr);
   					}
   				});
   				if(j.rows.length == 0){
   					str = '<p style="width: 100%; color: gray;">此文件夹为空。</p>';
   				}
   				$("#file-content").html(str);
   				$(".item-num").html(j.total + "个项目");
   				//图片展示初始化
   				if(isNull(photoShowObj)){
   					photoShowObj = initPhotoShow();
   				}else{
   					photoShowObj.update();
   				}
   				
   				if(!loadFileSizeCS){
   					//加载当前文件大小
   					loadFileSizeById();
   				}
   				
   				if(!initTreeSel){
   					initTreeSel = true;
   					ztree.expandNode(ztree.getNodeByParam("id", folderId, null));//展开指定节点-我的文档
   					chooseNodeSelect(folderId);
   				}
   				
   				//初始化赋值剪切功能
				initClipEvent();
   		    	
   		    	//初始化拖拽
   		    	initMenuToBox();
   			}else{
   				winui.window.msg(j.returnMessage, {icon: 2,time: 2000});
   			}
   		}, async: false});
	}
	
	var oldPointer = new Pointer();
	var oldPosition = new Position();
	var direction = new Direction();
	var div = $("<div id='chooseSelectBox'></div>").css({
		background: "blue",
		position: "absolute",
		opacity: "0.2"
	}).appendTo($("body"));
	var isDown = false;
	
	initDragSelectItem();
	function initDragSelectItem(){
		$(document).mousedown(function(e) {
			// 当没有文件拖拽时执行
			if(!dragFileIn && e.target.id === 'file-content'){
				e.preventDefault();
				isDown = true;
				oldPointer.x = e.clientX;
				oldPointer.y = e.clientY;
				oldPosition.left = e.clientX,
				oldPosition.top = e.clientY;
				div.css({
					left: e.clientX,
					top: e.clientY
				});
			}
		});
		
		div.extend({
			 checkC: function() {
			 	//当没有文件拖拽时执行
				if(!dragFileIn){
					var $this = this;
					$("#file-content .menu-folder").each(function() {
						if($this.offset().left + $this.width() > $(this).offset().left && 
						  $this.offset().left < $(this).offset().left + $(this).width()
						   && $this.offset().top + $this.height() > $(this).offset().top 
						   && $this.offset().top < $(this).offset().top + $(this).height()) {
						   	//选中
						   	//添加选中样式
							$(this).addClass("active");
							//设置当前选中
							$(this).children(".item-select").children(".item-check").find("input:checkbox[name='checkFile']").prop("checked", true);
						 } else {
						 	//未选中
						 	//取消选中样式
							$(this).removeClass("active");
							//设置当前未选中
							$(this).children(".item-select").children(".item-check").find("input:checkbox[name='checkFile']").prop("checked", false);
						 }
					});
				}
			}
		});
		$(document).mousemove(function(e) {
			//当没有文件拖拽时执行
			if(!dragFileIn){
				if(!isDown) return isDown;
				if(e.clientX > oldPointer.x) {
					direction.horizontal = "Right";
				} else if(e.clientX < oldPointer.x) {
					direction.horizontal = "Left";
				} else {
					direction.horizontal = "";
				}
				if(e.clientY > oldPointer.y) {
					direction.vertical = "Down";
				} else if(e.clientY < oldPointer.y) {
					direction.vertical = "Up";
				} else {
					direction.vertical = "";
				}
				if(!isNull(direction.horizontal + direction.vertical)){
					directionOperation[direction.horizontal + direction.vertical](e);
					div.checkC();
				}
			}else{
				isDown = false;
			}
		});
		$(document).mouseup(function() {
			if(!isDown) return isDown;
			isDown = false;
			div.width(0).height(0);
			//当选中多个文件或者文件夹时的操作
			chooseMoreDisk();
		});
	}
	
	var directionOperation = {
		LeftUp: function(e) {
			div.css({
				width: Math.abs(e.clientX - oldPointer.x),
				height: Math.abs(e.clientY - oldPointer.y),
				top: oldPosition.top - Math.abs(e.clientY - oldPointer.y),
				left: oldPosition.left - Math.abs(e.clientX - oldPointer.x)
			});
		},
		LeftDown: function(e) {
			div.css({
				width: Math.abs(e.clientX - oldPointer.x),
				height: Math.abs(e.clientY - oldPointer.y),
				left: oldPosition.left - Math.abs(e.clientX - oldPointer.x)
			});
		},
		Down: function(e) {
			div.css({
				width: 1,
				height: Math.abs(e.clientY - oldPointer.y)
			});
		},
		Up: function(e) {
			div.css({
				width: 1,
				height: Math.abs(e.clientY - oldPointer.y),
				top: oldPosition.top - Math.abs(e.clientY - oldPointer.y)
			});
		},
		Right: function(e) {
			div.css({
				width: Math.abs(e.clientX - oldPointer.x),
				height: 1
			});
		},
		Left: function(e) {
			div.css({
				width: Math.abs(e.clientX - oldPointer.x),
				height: 1,
				left: oldPosition.left - Math.abs(e.clientX - oldPointer.x)
			});
		},
		RightDown: function(e) {
			div.css({
				width: Math.abs(e.clientX - oldPointer.x),
				height: Math.abs(e.clientY - oldPointer.y)
			});
		},
		RightUp: function(e) {
			div.css({
				width: Math.abs(e.clientX - oldPointer.x),
				height: Math.abs(e.clientY - oldPointer.y),
				top: oldPosition.top - Math.abs(e.clientY - oldPointer.y)
			});
		}
	};
	
	function Pointer(x, y) {
		this.x = x;
		this.y = y;
	}
	function Position(left, top) {
		this.left = left;
		this.top = top;
	}
	function Direction(horizontal, vertical) {
		this.horizontal = horizontal;
		this.vertical = vertical;
	}
	
	//是否处于拖拽中
	var dragFileIn = false;
	// 拖拽代码
	function initMenuToBox(){
		$("#file-content .menu-folder").draggable({
		  	zIndex: 27000,
		  	cursor: "move",//拖拽操作期间的 CSS 光标。
		  	cursorAt: {
		  		left: 40,
		  		top: 25
		  	},//设置拖拽助手（helper）相对于鼠标光标的偏移。坐标可通过一个或两个键的组合成一个哈希给出：{ top, left, right, bottom }。
		  	scroll: true,//如果设置为 true，当拖拽时容器会自动滚动。
		  	scrollSensitivity: 30,//从要滚动的视区边缘起的距离，以像素计。距离是相对于指针的，不是相对于 draggable。如果 scroll 选项是 false 则忽略。
		  	scrollSpeed: 30,//当鼠标指针获取到在 scrollSensitivity 距离内时，窗体滚动的速度。如果 scroll 选项是 false 则忽略。
		  	appendTo: "#file-content",//当拖拽时，draggable 助手（helper）要追加到哪一个元素。
		  	helper: function(event) {
		  		//获取当前选中的文件和文件夹的数量，如果大于1，则是多文件移动
		  		var checkLength = $("#file-content .menu-folder .item-select .item-check").find("input:checkbox[name='checkFile']:checked").length;
		  		//拖拽时，鼠标旁边展示的名称和logo
		  		var name, logoImg;
		  		if(checkLength > 1){
		  			logoImg = "../../assets/images/more-file.png";
		  			name = "多文件移动"
		  		}else{
		  			var _this = $(this);
			  		logoImg = _this.find(".ico").find('img').attr('src');
			  		name = _this.find(".filename").find('span').html();
		  		}
		  		var str = '<div class="move_box"><div class="logo_icon"><img src="' + logoImg + '"/></div><div class="title"><span>' + name + '</span></div></div>';
		  		return str;
		  		
		  	},//允许一个 helper 元素用于拖拽显示。
		  	start: function(event, ui) {
		  		dragFileIn = true;
		  	},//当拖拽开始时触发。
		  	drag: function(event, ui) {
		  		if(!dragFileIn){
		  			dragFileIn = true;
		  		}
		  	},//在拖拽期间当鼠标移动时触发。
		  	stop: function(event, ui) {
	  			dragFileIn = false;
		  	}//当拖拽停止时触发。
		});
		
		//初始化，有东西拖到文件夹上时，调用回调函数
		$("#file-content .folder-box").droppable({
			drop: function(event, ui) {
				//获取目标文件夹
		  		var targetElement = $(this);
		  		//移动文件
		  		moveFile(targetElement.attr("rowid"), ui, null);
		  	}
		});
		
		//初始化，有东西拖到目录上时，调用回调函数
		$(".yarnball .yarnlet").droppable({
			drop: function(event, ui) {
				//获取目标文件夹
		  		var targetElement = $(this);
		  		//获取目标id
		  		var boxId = targetElement.attr("rowid");
		  		//移动文件
		  		moveFile(boxId, ui, null);
			}
		});
		
	}
	
	/**
	 * 移动文件
	 * boxId: 目标id
	 * ui: 移动的ui对象
	 * callback: 回调函数
	 */
	function moveFile(boxId, ui, callback){
		//即将保存的文件集合
  		var pastedSaveIds = new Array();
  		
  		//获取当前选中的文件和文件夹的数量，如果大于1，则是多文件移动
  		var checkLength = $("#file-content .menu-folder .item-select .item-check").find("input:checkbox[name='checkFile']:checked").length;
  		if(checkLength > 1){
  			var moveList = $("#file-content .menu-folder .item-select .item-check").find("input:checkbox[name='checkFile']:checked");
  			$.each(moveList, function(i, item){
  				var _obj = $(item).parent().parent().parent();
  				var pastedJson = {
					rowId: _obj.attr("rowid"),
					rowType: _obj.attr("filetype")
				};
				pastedSaveIds.push(pastedJson);
  			});
  		}else{
  			//获取移动的对象
  			var sourceElement = ui.draggable;
  			//获取移动对象的rowid
  			var thisFolderId = sourceElement.attr("rowid");
  			var pastedJson = {
				rowId: thisFolderId,
				rowType: $('div[rowid="' + thisFolderId + '"]').attr('filetype')
			};
			pastedSaveIds.push(pastedJson);
  		}
  		//开始执行保存
  		if(pastedSaveIds.length > 0 && !isNull(boxId)){
  			//判断可移动项是否包含目标文件夹
  			var inTarget = false;
  			$.each(pastedSaveIds, function(i, item){
  				if(item.rowId == boxId){
  					inTarget = true;
  					return false;
  				}
  			});
  			if(inTarget){
  				winui.window.msg('可移动项不能包含目标文件夹.', { shift: 6 });
  				return false;
  			}
			var jsonStr = JSON.stringify(pastedSaveIds);
			var params = {
				folderId: boxId, 
				jsonStr: jsonStr
			};
			//遮罩层显示
			$(".fileconsole-mask").show();
			AjaxPostUtil.request({url:reqBasePath + "fileconsole035", params: params, type: 'json', callback: function(json){
				if(json.returnCode == 0){
					refreshTreePointNode();//刷新树节点
					loadThisFolderChild();//刷新文件列表
					if(typeof(callback) == "function") {
						callback();
					}
				}else{
					winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				}
			}, async: false});
		}else{
			winui.window.msg('无法获取数据.', { shift: 6 });
		}
	}
	
	var loadFileSizeCS = false;
	//加载总文件大小
	function loadFileSizeById(){
		loadFileSizeCS = true;
		AjaxPostUtil.request({url:reqBasePath + "fileconsole012", params: {}, type: 'json', callback: function(json){
   			if(json.returnCode == 0){
   				$(".memory-num").html(json.bean.size);
   			}else{
   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
   			}
   		}, async: false});
	}
    
	//初始化图片展示效果
	function initPhotoShow(){
		return new Viewer(document.querySelector('#file-content'), {
			url: 'data-original'
		});
	}
	
	//重命名特效
	function reNameSpecially(){
		//获取已经显示的编辑器
		var showFileNameEdit = $("#file-content div[class='filename-edit']:visible");
		$.each(showFileNameEdit, function(i, item){
			$(item).parent().css({"z-index": 0});
			editFolderById($(item));
		});
		var obj = $("#file-content div[rowid='" + operaterId + "']");
		obj.css({"z-index": 1});
		var filetype = obj.attr("filetype");
		var html = "";
		if(filetype === 'folder'){
			html = obj.find("div[class='filename']").find("span").html();
		}else{
			html = obj.find("div[class='filename']").find("span").html().replace("." + filetype, "");
		}
		obj.find("div[class='filename-edit']").show();//显示编辑器
		obj.find("div[class='filename-edit']").find("textarea").val(html);
		obj.find("div[class='filename-edit']").find("textarea").select();
		obj.find("div[class='filename']").hide();//隐藏文字
	}
	
	//列表选中
	$("body").on("change", "#file-content .menu-folder .item-select .item-check", function(e){
		var length = $("#file-content").find(".item-check").length;
		var checkLength = $("#file-content :checkbox:checked").length;
		if(length == checkLength){
			$("#checkAll").prop("checked", true);
		}else{
			$("#checkAll").prop("checked", false);
		}
		e.stopPropagation();
	});
	
	// 点击logo,文件名进行文件选中
	$("body").on("click", "#file-content .file .ico, " +
			"#file-content .file .filename", function(e){
		$(".select-op-more").find("button[class*='btn-custom']").show();
		$(".select-op-more").find("li[class*='is-file']").show();
		$(".select-op-more").show();
		if (e.ctrlKey) {//按下ctrl键
			//隐藏指定项
			chooseMoreDiskHideParams();
		}else{//只有一个被选中时
			$("#file-content").find("div").removeClass("active");
			operaterId = $(this).parent().attr("rowid");
			var createId = $(this).parent().attr("createId");//获取文件创建人
			var fileType = $("#file-content div[rowid='" + operaterId + "']").attr("filetype");
			if($.inArray(fileType, packageType) >= 0){//压缩包
				$(".packageOpteare").show();
			}else{
				$(".packageOpteare").hide();
			}
			//权限控制
			authControllerFile(createId);
			//其他选中取消
			$("#file-content .menu-folder .item-select .item-check").find("input:checkbox[name='checkFile']:checked").prop("checked", false);
		}
		//添加选中样式
		$(this).parent().addClass("active");
		//设置当前选中
		$(this).parent().children(".item-select").children(".item-check").find("input:checkbox[name='checkFile']").prop("checked", true);
		e.stopPropagation();
	});
	
	// 文件或文件夹选中事件，同时阻止冒泡
	$("body").on("click", "#file-content .menu-folder .item-select .item-check", function(e){
		var checked = $(this).find("input[type='checkbox']").prop("checked");
		if(checked){//选中
			$(this).parent().parent().addClass("active");
		}else{
			$(this).parent().parent().removeClass("active");
		}
		//当选中多个文件或者文件夹时的操作
		chooseMoreDisk();
		e.stopPropagation();
	});
	
	// 当选中多个文件或者文件夹时的操作
	function chooseMoreDisk(){
		$(".select-op-more").find("button[class*='btn-custom']").show();
		$(".select-op-more").find("li[class*='is-file']").show();
		$(".select-op-more").show();
		var checkLength = $("#file-content .menu-folder .item-select .item-check").find("input:checkbox[name='checkFile']:checked").length;
		if(checkLength > 1){//有多个被选中的
			//隐藏指定项
			chooseMoreDiskHideParams();
		}else if(checkLength == 1){//只有一个被选中的
			var chooseItem = $("#file-content .menu-folder .item-select .item-check").find("input:checkbox[name='checkFile']:checked").parent().parent().parent();
			operaterId = chooseItem.attr("rowid");
			//权限控制
			authControllerFile(chooseItem.attr("createId"));
			var fileType = chooseItem.attr("filetype");
			if($.inArray(fileType, packageType) >= 0){//压缩包
				$(".packageOpteare").show();
			}else{
				$(".packageOpteare").hide();
			}
		}else{//没有被选中的
			$(".select-op-more").hide();
		}
	}
	
	//当选中多个文件或文件夹时的隐藏内容
	function chooseMoreDiskHideParams(){
		$(".createDuplicate").hide();//创建副本操作隐藏
		$(".packageOpteare").hide();//解压操作隐藏
		$(".fileReName").hide();//重命名隐藏
		$(".cutUrl").hide();//剪切隐藏
		$(".deleteFolderAndChildToRecycle").hide();//回收站隐藏
	}
	
	//文件权限控制
	function authControllerFile(createId){
		//如果当前登陆人是文件创建人,可以删除，回收站，重命名，剪切，否则不能
		if(getCookie('userToken') === createId){
			$(".fileReName").show();//重命名显示
			$(".deleteFolderAndChild").show();//删除显示
			$(".cutUrl").show();//剪切显示
			$(".deleteFolderAndChildToRecycle").show();//回收站显示
			$(".deleteFolderAndChild").show();//删除显示
		}else{
			$(".fileReName").hide();//重命名隐藏
			$(".deleteFolderAndChild").hide();//删除隐藏
			$(".cutUrl").hide();//剪切隐藏
			$(".deleteFolderAndChildToRecycle").hide();//回收站隐藏
			$(".deleteFolderAndChild").hide();//删除隐藏
		}
	}
	
	initRightMenu();
	//初始化右键
	function initRightMenu(){
    	$("body").contextMenu({
			width: 190, // width
			itemHeight: 30, // 菜单项height
			bgColor: "#FFFFFF", // 背景颜色
			color: "#0A0A0A", // 字体颜色
			fontSize: 12, // 字体大小
			hoverBgColor: "#99CC66", // hover背景颜色
			target: function(ele) { // 当前元素
			},
			menu: [{
					text: "新建文件夹",
					img: "../../assets/images/create-folder-icon.png",
					callback: function() {
						createFolder(folderId, refreshTreePointNode, initMenuToBox);
					}
				}, { // 菜单项
					text: "新建文件",
					img: "../../assets/images/my-file-icon.png",
					children: [{
						text: "txt文件",
						img: "../../assets/images/txt-icon.png",
						callback: function() {
							createTXTFile(folderId, loadThisFolderChild());
						}
					}, {
						text: "html文件",
						img: "../../assets/images/html-icon.png",
						callback: function() {
							createHtmlFile(folderId, loadThisFolderChild());
						}
					}, {
						text: "--"
					}, {
						text: "Word  docx 文件",
						img: "../../assets/images/word-icon.png",
						callback: function() {
							createWordFile(folderId, loadThisFolderChild());
						}
					}, {
						text: "Excel xlsx 文件",
						img: "../../assets/images/excel-icon.png",
						callback: function() {
							createExcelFile(folderId, loadThisFolderChild());
						}
					}, {
						text: "PowerPoint pptx 文件",
						img: "../../assets/images/ppt-icon.png",
						callback: function() {
							createPPTFile(folderId, loadThisFolderChild());
						}
					}]
				}, {
					text: "--"
				}, { // 菜单项
					text: "上传",
					img: "../../assets/images/upload-icon.png",
					children: [{
						text: "上传文件",
						img: "../../assets/images/file-icon.png",
						callback: function() {
							_openNewWindows({
								url: "../../tpl/fileconsole/fileconsoleupload.html", 
								title: "文件上传",
								pageId: "fileconsoleupload",
								area: ['400px', '350px'],
								skin: 'add-schedule-mation',
								callBack: function(refreshCode){
									loadFileSizeCS = false;//重置让系统加载文件大小
									loadThisFolderChild();
								}});
						}
					}, {
						text: "上传文件夹",
						img: "../../assets/images/folder-icon.png",
						callback: function() {
							_openNewWindows({
								url: "../../tpl/fileconsole/filefolderupload.html", 
								title: "文件夹上传",
								pageId: "filefolderupload",
								area: ['400px', '350px'],
								skin: 'add-schedule-mation',
								callBack: function(refreshCode){
									loadFileSizeCS = false;//重置让系统加载文件大小
									loadThisFolderChild();
								}});
						}
					}]
				}, {
					text: "--"
				}, {
					text: "粘贴",
					img: "../../assets/images/paste-right-icon.png",
					callback: function() {
						pasteFile();
					}
				}, {
					text: "查看剪贴板",
					img: "../../assets/images/paste-ban-right-icon.png",
					callback: function(e) {
						if(isNull(pastedText)){
							winui.window.msg('请先进行文件或文件夹复制。', {icon: 2,time: 2000});
						}else{
							if(isJsonFormat(pastedText.replace(/'/g, '"'))){//判断是否是json
								var pastedJson = JSON.parse(pastedText.replace(/'/g, '"'));//粘贴板数据
								if(!isNull(pastedJson.id) && !isNull(pastedJson.fileType) && !isNull(pastedJson.name)){
									layer.open({
							            id: '剪贴板内容',
							            type: 1,
							            title: '剪贴板内容',
							            shade: 0.3,
							            area: ['300px', '200px'],
							            content: '当前剪贴板内容：<br>' + '<br>文件名：' + pastedJson.name + "<br><br>文件类型：" + pastedJson.fileType
							        });
								}else{
									winui.window.msg('剪贴板数据格式不正确。', {icon: 2,time: 2000});
								}
							}else{
								winui.window.msg('剪贴板数据格式不正确。', {icon: 2,time: 2000});
							}
						}
					}
				}, {
					text: "--"
				}, {
					text: "已支持文件类型",
					img: "../../assets/images/file-sketch-icon.png",
					callback: function() {
						_openNewWindows({
							url: "../../tpl/fileconsole/showFileInLine.html", 
							title: "可支持的在线预览类型",
							pageId: "showFileInLinePage",
							area: ['650px', '480px'],
							skin: 'add-schedule-mation',
							callBack: function(refreshCode){
							}});
					}
				}, {
					text: "--"
				}, {
					text: "刷新",
					img: "../../assets/images/refresh-icon.png",
					callback: function() {
						loadThisFolderChild();
					}
				}
			]
		});
    }
    
    function pasteFile(){
    	if(isNull(pastedText)){
			winui.window.msg('请先进行文件或文件夹复制。', {icon: 2,time: 2000});
		}else{
			if(isJsonFormat(pastedText.replace(/'/g, '"'))){//判断是否是json
				var pastedJson = JSON.parse(pastedText.replace(/'/g, '"'));//粘贴板数据
				if(!isNull(pastedJson.id) && !isNull(pastedJson.fileType) && !isNull(pastedJson.type)){
					var pastedSaveIds = new Array();//粘贴板数据存储
					var params = {
						rowId: pastedJson.id,
						rowType: pastedJson.fileType
					};
					pastedSaveIds.push(params);
					var jsonStr = JSON.stringify(pastedSaveIds);
					var params = {
						folderId: folderId, 
						jsonStr: jsonStr
					};
					winui.window.msg("文件正在粘贴中，期间请勿进行其他操作。", {icon: 7,time: 4000});
					if(pastedJson.type === '1' || pastedJson.type == 1){
						AjaxPostUtil.request({url:reqBasePath + "fileconsole034", params: params, type: 'json', callback: function(json){
							if(json.returnCode == 0){
								winui.window.msg("文件粘贴成功", {icon: 1,time: 2000});
								if(pastedJson.fileType === 'folder'){//文件夹
									//刷新节点
									refreshTreePointNode();
								}
								loadThisFolderChild();
							}else{
								winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
							}
						}});
					}else if(pastedJson.type === '2' || pastedJson.type == 2){
						AjaxPostUtil.request({url:reqBasePath + "fileconsole035", params: params, type: 'json', callback: function(json){
							if(json.returnCode == 0){
								var selNode = ztree.getNodeByParam("id", pastedJson.id, null);
                                if(!isNull(selNode)){
                                    ztree.removeNode(selNode);//移除节点
                                }
								winui.window.msg("文件粘贴成功", {icon: 1,time: 2000});
								if(pastedJson.fileType === 'folder'){//文件夹
									//刷新节点
									refreshTreePointNode();
								}
								loadThisFolderChild();
							}else{
								winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
							}
						}});
					}
				}else{
					winui.window.msg('剪贴板数据格式不正确。', {icon: 2,time: 2000});
				}
			}else{
				winui.window.msg('剪贴板数据格式不正确。', {icon: 2,time: 2000});
			}
		}
    }
    
    //初始化上传
    var $wrap = $('#file-content'),
    	// 文件容器
    	$queue = $('.filelist'),
    	// 状态栏，包括进度和控制按钮
    	$statusBar = $('.statusBar'),
    	// 文件总体选择信息。
	    $info = $statusBar.find('.info'),
	    // 上传按钮
    	$upload = $('.uploadBtn'),
    	// 总体进度条
	    $progress = $statusBar.find('.progress').hide(),
    	// 添加的文件数量
	    fileCount = 0,
    	// 添加的文件总大小
	    fileSize = 0,
    	// 优化retina, 在retina下这个值是2
	    ratio = window.devicePixelRatio || 1,
    	// 缩略图大小
	    thumbnailWidth = 110 * ratio,
	    thumbnailHeight = 110 * ratio,
    	// 可能有pedding, ready, uploading, confirm, done.
	    state = 'pedding',
    	// 所有文件的进度信息，key为file id
	    percentages = {},
    	supportTransition = (function(){
	        var s = document.createElement('p').style,
	            r = 'transition' in s ||
	                  'WebkitTransition' in s ||
	                  'MozTransition' in s ||
	                  'msTransition' in s ||
	                  'OTransition' in s;
	        s = null;
	        return r;
	    })(),
    	uploader;
    //关闭按钮
    $("body").on("click", ".closeBtn", function(e){
		if(state === 'pedding' || state === 'ready'){
	    	// 移除所有缩略图并将上传文件移出上传序列
	        $.each(uploader.getFiles(), function(i, item){
	        	// 将图片从上传序列移除
	        	uploader.removeFile(item);
	        	// 将图片从缩略图容器移除
	        	var $li = $('#' + item.id);
	        	$li.off().remove();
	        });
	        setOneState('pedding');
	        // 重置文件总个数和总大小
	        fileCount = 0;
	        fileSize = 0;
	        // 重置开始上传按钮
	        $upload.text( '开始上传' ).removeClass( 'disabled' );
	        // 重置uploader，目前只重置了文件队列
	        uploader.reset();
	        // 更新状态等，重新计算文件总个数和总大小
	        updateOneStatus();
	        $(".file-panel-upload").hide();
	        // 刷新文件列表
	        loadThisFolderChild();
		}else{
			winui.window.msg('上传中，无法关闭。', {icon: 2,time: 2000});
		}
	});
	loadFileUploadMethod();
    function loadFileUploadMethod(){
		if (!WebUploader.Uploader.support()) {
		    alert( 'Web Uploader 不支持您的浏览器！如果你使用的是IE浏览器，请尝试升级 flash 播放器');
		    throw new Error( 'WebUploader does not support the browser you are using.' );
		}
		
		var md5;
		//监听分块上传过程中的三个时间点
		WebUploader.Uploader.register({
			"before-send-file": "beforeSendFile",
			"before-send": "beforeSend",
			"after-send-file": "afterSendFile"
		}, {
			//时间点1：所有分块进行上传之前调用此函数
			beforeSendFile: function(file) {
				var deferred = WebUploader.Deferred();
				//1、计算文件的唯一标记，用于断点续传
				(new WebUploader.Uploader()).md5File(file, 0, 10 * 1024 * 1024)
					.progress(function(percentage) {
						$('#' + file.id).find("p.state").text("正在读取文件信息...");
					})
					.then(function(val) {
						md5 = val;
						$('#' + file.id).find("p.state").text("成功获取文件信息...");
						//获取文件信息后进入下一步
						deferred.resolve();
					});
				return deferred.promise();
			},
			//时间点2：如果有分块上传，则每个分块上传之前调用此函数
			beforeSend: function(block) {
				var deferred = WebUploader.Deferred();
				var params = {
					//文件唯一标记
					"md5": md5,
					//当前分块下标
					"chunk": block.chunk,
					//当前分块大小
					"chunkSize": block.end - block.start
				};
				AjaxPostUtil.request({url:reqBasePath + "fileconsole008", params: params, type: 'json', callback: function(json){
	    			if(json.returnCode == 0){
	    				//分块存在，跳过
						deferred.reject();
	    			}else{
	    				//分块不存在或不完整，重新发送该分块内容
						deferred.resolve();
	    			}
	    		}, async: false});
				this.owner.options.formData.md5 = md5;
				deferred.resolve();
				return deferred.promise();
			},
			//时间点3：所有分块上传成功后调用此函数
			afterSendFile: function (data) {
				//如果分块上传成功，则通知后台合并分块
				AjaxPostUtil.request({url:reqBasePath + "fileconsole007", params: {md5: md5, folderId: folderId, name: data.name, size: data.size}, type: 'json', callback: function(json){
	    			if(json.returnCode == 0){
	    			}else{
	    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	    			}
	    		}});
			 }
		});
		
		// 实例化
		uploader = WebUploader.create({
		    formData: {
		    	userToken: getCookie('userToken'),
		    	loginPCIp: returnCitySN["cip"],
		    	folderId: folderId
		    },
		    dnd: '#file-content',
		    paste: document.body,
		    // swf文件路径
		    swf: fileBasePath + '/assets/images/Uploader.swf',
		    disableGlobalDnd: true,//是否禁掉整个页面的拖拽功能
		    chunked: true,//是否要分片处理大文件上传
		    chunkSize: 10 * 1024 * 1024,
		    chunkRetry: 3,//网络问题上传失败后重试次数
		    threads: 1, //上传并发数
		    fileSizeLimit: 2000 * 1024 * 1024,//最大2GB
	        fileSingleSizeLimit: 2000 * 1024 * 1024,
	        resize: false,//不压缩
		    server: reqBasePath + 'fileconsole006',
		    fileNumLimit: 300
		});
		// 添加“添加文件”的按钮，
		uploader.addButton({
		    id: '#filePicker2',
		    label: '继续添加'
		});
		uploader.onUploadProgress = function(file, percentage) {
		    var $li = $('#'+file.id),
		        $percent = $li.find('.progress span');
		    $percent.css('width', percentage * 100 + '%');
		    percentages[file.id][1] = percentage;
		    updateAllTotalProgress();
		};
		uploader.onFileQueued = function(file) {
		    fileCount++;
		    fileSize += file.size;
		    if (fileCount === 1) {
		        $statusBar.show();
		    }
		    addMoreFile(file);
		    setOneState('ready');
		    updateAllTotalProgress();
		};
		uploader.onFileDequeued = function(file) {
		    fileCount--;
		    fileSize -= file.size;
		    if (!fileCount) {
		        setOneState('pedding');
		    }
		    removeOneFile(file);
		    updateAllTotalProgress();
		};
		uploader.on( 'all', function( type ) {
		    var stats;
		    switch( type ) {
		        case 'uploadFinished':
		            setOneState( 'confirm' );
		            break;
		        case 'startUpload':
		            setOneState( 'uploading' );
		            break;
		        case 'stopUpload':
		            setOneState( 'paused' );
		            break;
		    }
		});
		uploader.on('uploadBeforeSend', function(block, data, headers) {
			headers['X-Requested-With']=  'XMLHttpRequest';
			data.userToken = getCookie('userToken');
			data.loginPCIp = returnCitySN["cip"];
			data.folderId = folderId;
			data.md5 = md5;
			data.chunk = block.chunk;
			data.chunkSize = block.end - block.start;
		});
		uploader.onError = function(code) {
		    alert('Eroor: ' + code);
		};
		$upload.on('click', function() {
		    if ($(this).hasClass( 'disabled')) {
		        return false;
		    }
		    if (state === 'ready') {
		        uploader.upload();
		    } else if (state === 'paused') {
		        uploader.upload();
		    } else if (state === 'uploading') {
		        uploader.stop(true);
		    }
		});
		$info.on('click', '.retry', function() {
		    uploader.retry();
		});
		$info.on('click', '.ignore', function() {
		    alert('todo');
		});
		$upload.addClass('state-' + state);
		updateAllTotalProgress();
	}
	
	//当有文件添加进来时执行，负责view的创建
	function addMoreFile(file) {
	    var $li = $( '<li id="' + file.id + '">' + '<p class="title">' + file.name + '</p>' + '<p class="imgWrap"></p>' + '<p class="progress"><span></span></p>' + '</li>' ),
	        $btns = $('<div class="file-panel">' + '<span class="cancel">删除</span>' + '<span class="rotateRight">向右旋转</span>' + '<span class="rotateLeft">向左旋转</span></div>').appendTo( $li ),
	        $prgress = $li.find('p.progress span'),
	        $wrap = $li.find( 'p.imgWrap' ),
	        $info = $('<p class="error"></p>'),
	        showError = function( code ) {
	            switch( code ) {
	                case 'exceed_size':
	                    text = '文件大小超出';
	                    break;
	
	                case 'interrupt':
	                    text = '上传暂停';
	                    break;
	
	                default:
	                    text = '上传失败，请重试';
	                    break;
	            }
	            $info.text( text ).appendTo( $li );
	        };
	    if ( file.getStatus() === 'invalid' ) {
	        showError( file.statusText );
	    } else {
	        $wrap.text( '预览中' );
	        uploader.makeThumb( file, function( error, src ) {
	            if ( error ) {
	                $wrap.text( '不能预览' );
	                return;
	            }
	            var img = $('<img src="'+src+'">');
	            $wrap.empty().append( img );
	        }, thumbnailWidth, thumbnailHeight );
	        percentages[ file.id ] = [ file.size, 0 ];
	        file.rotation = 0;
	    }
	    file.on('statuschange', function( cur, prev ) {
	        if ( prev === 'progress' ) {
	            $prgress.hide().width(0);
	        } else if ( prev === 'queued' ) {
	            $li.off( 'mouseenter mouseleave' );
	            $btns.remove();
	        }
	        // 成功
	        if ( cur === 'error' || cur === 'invalid' ) {
	            showError( file.statusText );
	            percentages[ file.id ][ 1 ] = 1;
	        } else if ( cur === 'interrupt' ) {
	            showError( 'interrupt' );
	        } else if ( cur === 'queued' ) {
	            percentages[ file.id ][ 1 ] = 0;
	        } else if ( cur === 'progress' ) {
	            $info.remove();
	            $prgress.css('display', 'block');
	        } else if ( cur === 'complete' ) {
	            $li.append( '<span class="success"></span>' );
	        }
	        $li.removeClass( 'state-' + prev ).addClass( 'state-' + cur );
	    });
	    $li.on( 'mouseenter', function() {
	        $btns.stop().animate({height: 30});
	    });
	    $li.on( 'mouseleave', function() {
	        $btns.stop().animate({height: 0});
	    });
	    $btns.on( 'click', 'span', function() {
	        var index = $(this).index(), deg;
	        switch ( index ) {
	            case 0:
	                uploader.removeFile(file);
	                return;
	
	            case 1:
	                file.rotation += 90;
	                break;
	
	            case 2:
	                file.rotation -= 90;
	                break;
	        }
	        if ( supportTransition ) {
	            deg = 'rotate(' + file.rotation + 'deg)';
	            $wrap.css({
	                '-webkit-transform': deg,
	                '-mos-transform': deg,
	                '-o-transform': deg,
	                'transform': deg
	            });
	        } else {
	            $wrap.css( 'filter', 'progid:DXImageTransform.Microsoft.BasicImage(rotation='+ (~~((file.rotation/90)%4 + 4)%4) +')');
	        }
	    });
	    $li.appendTo( $queue );
	}
	
	// 负责view的销毁
	function removeOneFile(file) {
	    var $li = $('#' + file.id);
	    delete percentages[file.id];
	    updateAllTotalProgress();
	    $li.off().find('.file-panel').off().end().remove();
	}
	
	function updateAllTotalProgress() {
	    var loaded = 0,
	        total = 0,
	        spans = $progress.children(),
	        percent;
	    $.each(percentages, function(k, v) {
	        total += v[ 0 ];
	        loaded += v[ 0 ] * v[ 1 ];
	    });
	    percent = total ? loaded / total : 0;
	    spans.eq( 0 ).text( Math.round( percent * 100 ) + '%' );
	    spans.eq( 1 ).css( 'width', Math.round( percent * 100 ) + '%' );
	    updateOneStatus();
	}
	
	function updateOneStatus() {
	    var text = '', stats;
	    if (state === 'ready') {
	        text = '选中' + fileCount + '个文件，共' + WebUploader.formatSize(fileSize) + '。';
	    } else if (state === 'confirm') {
	        stats = uploader.getStats();
	        if (stats.uploadFailNum) {
	            text = '已成功上传' + stats.successNum+ '个文件至服务器，'+ stats.uploadFailNum + '个文件上传失败，<a class="retry" href="#">重新上传</a>失败文件或<a class="ignore" href="#">忽略</a>'
	        }
	    } else {
	        stats = uploader.getStats();
	        text = '共' + fileCount + '个（' + WebUploader.formatSize(fileSize)  + '），已上传' + stats.successNum + '张';
	        if (stats.uploadFailNum) {
	            text += '，失败' + stats.uploadFailNum + '张';
	        }
	    }
	    $info.html(text);
	}
	
	function setOneState(val) {
	    var file, stats;
	    if (val === state) {
	        return;
	    }
	    $upload.removeClass('state-' + state);
    	$upload.addClass('state-' + val);
	    state = val;
	    switch (state) {
	        case 'pedding':
	        	$(".file-panel-upload").hide();
	            $queue.parent().removeClass('filled');
	            $queue.hide();
	            $statusBar.addClass('element-invisible');
	            uploader.refresh();
	            break;
	        case 'ready':
	        	$( '#filePicker2' ).removeClass( 'element-invisible');
	        	$(".file-panel-upload").show();
	            $queue.parent().addClass('filled');
	            $queue.show();
	            $statusBar.removeClass('element-invisible');
	            uploader.refresh();
	            break;
	        case 'uploading':
	        	$( '#filePicker2' ).addClass( 'element-invisible' );
	            $progress.show();
	            $upload.text( '暂停上传' );
	            break;
	        case 'paused':
	            $progress.show();
	            $upload.text( '继续上传' );
	            break;
	        case 'confirm':
	            $progress.hide();
	            $upload.text( '开始上传' ).addClass( 'disabled' );
	            stats = uploader.getStats();
	            if ( stats.successNum && !stats.uploadFailNum ) {
	                setOneState( 'finish' );
	                return;
	            }
	            break;
	        case 'finish':
	            stats = uploader.getStats();
	            if (stats.successNum) {
	                alert( '上传成功' );
	                // 移除所有缩略图并将上传文件移出上传序列
	                $.each(uploader.getFiles(), function(i, item){
	                	// 将图片从上传序列移除
	                	uploader.removeFile(item);
	                	// 将图片从缩略图容器移除
	                	var $li = $('#' + item.id);
	                	$li.off().remove();
	                });
	                setOneState('pedding');
	                // 重置文件总个数和总大小
			        fileCount = 0;
			        fileSize = 0;
			        // 重置开始上传按钮
			        $upload.text( '开始上传' ).removeClass( 'disabled' );
			        // 重置uploader，目前只重置了文件队列
			        uploader.reset();
			        // 更新状态等，重新计算文件总个数和总大小
			        updateOneStatus();
	                $(".file-panel-upload").hide();
	                // 刷新文件列表
	                loadThisFolderChild();
	            } else {
	                // 没有成功的文件，重设
	                state = 'done';
	                location.reload();
	            }
	            break;
	    }
	    updateOneStatus();
	}
    
    exports('fileconsolelist', {});
});
