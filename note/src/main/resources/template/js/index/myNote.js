
var folderId = "1";//当前树所在文件夹目录的位置

var clickId = "";//右键选中的id

var clickType = "";//右键选中的类型

var noteId = "";//当前页面打开的笔记id

var searchTitle = "";//搜索框的值

var isNewNote = true;//默认打开最新笔记页面

var clipboard;//复制对象

var layedit;

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'dropdown', 'fsCommon', 'fsTree', 'viewer', 'contextMenu', 'flow', 'ClipboardJS'], function (exports) {
	winui.renderColor();
	var $ = layui.jquery,
		fsTree = layui.fsTree,
		fsCommon = layui.fsCommon,
		flow = layui.flow,
		device = layui.device();

	//模板对象
	var folderchildlisetTemplate = $("#folderchildlisetTemplate").html();
	
	matchingLanguage();
	/********* tree 处理   start *************/
	var ztree = null;
	fsTree.render({
		id: "treeDemo",
		url: sysMainMation.noteBasePath + "queryFolderByUserId",
		checkEnable: false,
		dragEnable: true,
		loadEnable: true,//异步加载
		showLine: false,
		showIcon: true,
		isDrag: true,
		addDiyDom: addDiyDom,
		clickCallback: zTreeOnClick,
		beforeRename: beforeRename,
		onRename: onRename,
		beforeDrag: beforeDrag,
		beforeDrop: beforeDrop,
		onRightClick: onRightClick,
		onDblClick: function(){
		},
		onAsyncSuccess: function(id){
			var dropdown = new Dropdown();
			dropdown.render();
		}
	}, function(id){
		ztree = $.fn.zTree.getZTreeObj(id);
		// 设置选中节点
		chooseNodeSelect(folderId);
		// 展开我的文件夹节点
		ztree.expandNode(ztree.getNodeByParam("id", '2', null));
	});
	
	function addDiyDom(treeId, treeNode) {
		//css样式以及位置调整
		var spaceWidth = 18;
        var switchObj = $("#" + treeNode.tId + "_switch"),
        icoObj = $("#" + treeNode.tId + "_ico");
        switchObj.remove();
        icoObj.before(switchObj);
        if (treeNode.level >= 1) {
            var spaceStr = "<span style='display: inline-block;width:" + (spaceWidth * treeNode.level) + "px'></span>";
            switchObj.before(spaceStr);
        }
		
		var aObj = $("#" + treeNode.tId + "_a");
		if ($("#diyBtn_" + treeNode.id).length > 0) return;
		aObj.after("");
		aObj.addClass("tree_a");
	};
	
	/**
	 * 树点击事件
	 */
	function zTreeOnClick(event, treeId, treeNode) {
		if($("#editMyNote").hasClass('select')) {
			layer.confirm("存在未保存的内容，是否保存？", {icon: 3, title: '提醒'}, function (index) {
				layer.close(index);
				saveNoteMation();
				treeOnClick(event, treeId, treeNode);
			}, function (index) {
				layer.close(index);
				treeOnClick(event, treeId, treeNode);
			});
		} else {
			treeOnClick(event, treeId, treeNode);
		}
	}

	function treeOnClick(event, treeId, treeNode){
		//如果当前点击的目录不是之前点击的目录
		if(treeNode.id != folderId){
			folderId = treeNode.id;
			$("#searchTitle").val("");
			noteId = "";
			isNewNote = false;
			showListById();//获取文件夹和笔记列表
		}
		//展开
		if(!treeNode.open){//如果节点不展开，则展开
			ztree.expandNode(treeNode);
		}
	}
	
	function onRightClick(event, treeId, treeNode) {
		folderId = treeNode.id;
		// 设置右键节点选中
		chooseNodeSelect(folderId);
		// '最新笔记'不展示右键菜单
		if(folderId != '1'){
			if(folderId === '2'){
				showRMenu('root', event.clientX, event.clientY);
			} else {
				showRMenu('node', event.clientX, event.clientY);
			}
		}
	}
	
	function showRMenu(type, x, y) {
		$("#treeRight .is-file").show();
		if(type == 'root'){
			$("#treeRight .fileReName").hide();
			$("#treeRight .treedeleteFolderAndChild").hide();
		}
		$("#treeRight").show();
		$("#treeRight").css({top: y + "px", left: x + "px", visibility: "visible", position: "absolute"});
		$("body").bind("mousedown", onBodyMouseDown);
	}
	
	function hideRMenu() {
		if ($("#treeRight")) $("#treeRight").css({"visibility": "hidden"});
		$("body").unbind("mousedown", onBodyMouseDown);
	}
	
	function onBodyMouseDown(event){
		if (!($(event.target).parents(".is-file").length>0)) {
			$("#treeRight").css({"visibility" : "hidden"});
		}
	}
	
	// 设置选中节点
	function chooseNodeSelect(folderId){
		var selNode = ztree.getNodeByParam("id", folderId, null);
		ztree.selectNode(selNode);
	}
	
	// 节点名称修改限制
	function beforeRename(treeId,treeNode,newName,isCancel){
        if(newName.length < 1){
        	winui.window.msg("节点名称不能为空", {icon: 2, time: 2000});
            return false;
        }
        return true;
    }
	
	//编辑节点名称
	function onRename(event, treeId, treeNode) {
		AjaxPostUtil.request({url: sysMainMation.noteBasePath + "editFileFolderById", params: {name: treeNode.name, fileType: 'folder', id: treeNode.id}, type: 'json', method: 'POST', callback: function (json) {
			winui.window.msg("保存成功", {icon: 1, time: 2000});
			folderId = treeNode.id;
			showListById();//获取文件夹和笔记列表
   		}});
	}
	
	/**移动节点前处理*/
	function beforeDrag(treeId, treeNodes) {
		for (var i = 0, l = treeNodes.length; i < l; i++) {
			var pid = treeNodes[i].pId;
			if (pid == "0") {
				winui.window.msg("只能移动叶子节点。", {icon: 2, time: 2000});
				return false;
			}
		}
		return true;
	}
	
	/**拖拽释放之后结束前执行*/
	function beforeDrop(treeId, treeNodes, targetNode, moveType) {
		if(targetNode == null || targetNode == "undefined"){
			return false;
		}
		var targetId = targetNode.id;//拖拽目标节点id
		if(targetId == "1"){
			winui.window.msg("不能移动到该节点。", {icon: 2, time: 2000});
			return false;
		}
		var arrId = new Array();//拖拽的节点id数组
		for (var i = 0, l = treeNodes.length; i < l; i++) {
			var parentId = treeNodes[i].pId;
			if(parentId == targetId){
				winui.window.msg("移动的节点已经在该节点下。", {icon: 2, time: 2000});
				return false;
			}
			arrId[i] = treeNodes[i].id;
		}
		AjaxPostUtil.request({url: sysMainMation.noteBasePath + "mynote010", params: {targetId: targetId, arrId : arrId}, type: 'json', callback: function (json) {
			winui.window.msg("保存成功", {icon: 1, time: 2000});
			noteId = "";
			folderId = targetId;
			// 刷新节点
			refreshTreePointNode();
			// 设置选中节点
			chooseNodeSelect(folderId);
			showListById();//获取文件夹和笔记列表
			for(var i = 0, l = arrId.length; i < l; i++){
				var selNode = ztree.getNodeByParam("id", arrId[i], null);
				ztree.removeNode(selNode);//移除节点
			}
   		}});
	}
	
	/** ******* tree 处理 end ************ */

	//刷新树指定节点
	function refreshTreePointNode(){
		//刷新节点
	    var nownode = ztree.getNodesByParam("id", folderId, null);  
	    ztree.reAsyncChildNodes(nownode[0], "refresh");
	}
	
	//树的动画效果
	$("body").on("mouseover", "#treeDemo a", function (e) {
		$("#treeDemo").find("a").removeClass('mouseOver');
		$(this).addClass('mouseOver');
	});
	$("body").on("mouseleave", "#treeDemo", function (e) {
		$("#treeDemo").find("a").removeClass('mouseOver');
	});
	
	//文件夹子文件切换效果
	$("body").on("click", "#folderChildList .folder-item", function (e) {
		var _this = $(this);
		if($("#editMyNote").hasClass('select')){
			layer.confirm("存在未保存的内容，是否保存？", { icon: 3, title: '提醒' }, function (index) {
				layer.close(index);
				saveNoteMation();
				loadNewDocument(_this);
			}, function (index) {
				layer.close(index);
				loadNewDocument(_this);
			});
		} else {
			loadNewDocument(_this);
		}
	});

	// 加载新的文件内容
	function loadNewDocument(_this){
		$("#folderChildList").find(".folder-item").removeClass("select");
		_this.addClass('select');
		var id = _this.attr("id");
		var name = _this.attr("rowname");
		var type = _this.attr("rowtype");
		if(type == "folder"){
			//显示无预览页面
			$("#childframe").attr("src", "../../tpl/note/noteFolder.html");
			$("#noteTitle").val(name);//给标题赋值
			$(".note-operator").addClass("layui-hide");//隐藏保存按钮
			noteId = "";
		} else {
			if(noteId != id){
				noteId = id;//当前选中列表的笔记id
				$("#noteTitle").val(name);//给标题赋值
				// 移除可保存标识
				$("#editMyNote").removeClass('select');
				$(".note-operator").removeClass("layui-hide");//显示保存按钮
				// 显示编辑器，隐藏无预览页面
				loadHtml(type);
			}
		}
	}
	
	//文件夹子文件双击效果
	$("body").on("dblclick", "#folderChildList .folder-item", function (e) {
		var id = $(this).attr("id");
		var type = $(this).attr("rowtype");
		if(type == "folder"){
			noteId = "";
			folderId = id;
			// 设置选中节点
			chooseNodeSelect(folderId);
			ztree.expandNode(ztree.getNodeByParam("id", folderId, null));//展开
			showListById();//获取文件夹和笔记列表
		}
	});
	
	//文件夹子文件右键效果
	$("body").on("contextmenu", "#folderChildList .folder-item", function (e) {
		if(clickId != $(this).attr("id")){
			clickId = $(this).attr("id");
			clickType = $(this).attr("rowtype");
			var name = $(this).attr("rowname");
			$("#folderChildList").find(".folder-item").removeClass("select");
			$("#folderChildList").find(".folder-item[id='" + clickId + "']").addClass('select');
			if(clickType == "folder"){
				//显示无预览页面
				$("#childframe").attr("src", "../../tpl/note/noteFolder.html");
				$("#noteTitle").val(name);//给标题赋值
				$(".note-operator").addClass("layui-hide");//隐藏保存按钮
				noteId = "";
			} else {
				noteId = clickId;//当前选中列表的笔记id
				$("#noteTitle").val(name);//给标题赋值
				$(".note-operator").removeClass("layui-hide");//显示保存按钮
				// 显示编辑器，隐藏无预览页面
				loadHtml(clickType);
			}
		}
	});
	
	//初始化右键
	function initRightMenu(){
    	$(".folder-item").contextMenu({
			width: 190, // width
			itemHeight: 30, // 菜单项height
			bgColor: "#FFFFFF", // 背景颜色
			color: "#0A0A0A", // 字体颜色
			fontSize: 12, // 字体大小
			hoverBgColor: "#99CC66", // hover背景颜色
			target: function(ele) { // 当前元素
			},
			rightClass: 'folder-item,folder-item-title,folder-item-title-icon,folder-item-title-content,folder-item-desc,folder-item-mation,folder-item select,folder-item-mation-time',
			menu: [{
					text: "输出为PDF",
					img: "../../assets/images/decompression-now.png",
					callback: function() {
						AjaxPostUtil.request({url: sysMainMation.noteBasePath + "outputNoteIsZipJob", params: {id: clickId, type: "file"}, type: 'json', method: 'POST', callback: function (json) {
							layer.alert('笔记输出为PDF任务已创建，请前往 <a href="../../tpl/jobSpace/jobSpace.html" style="color: blue;" target="_blank">我的输出</a> 查看。');
				   		}});
					}
				},{
					text: "删除",
					img: "../../assets/images/icon/delete-icon.png",
					callback: function() {
						deleteFileOrNote(clickId);
						if(clickType != "folder"){
							noteId = "";
						}
					}
				},{
					text: "重命名",
					img: "../../assets/images/rename-icon.png",
					callback: function() {
						var obj = $("#folderChildList").find(".folder-item[id='" + clickId + "']");
						var html = obj.find("div[class='folder-item-title-content']").find("span").html();
						var newhtml = "<input value='" + html + "'></input>";
						obj.find("div[class='folder-item-title-content']").html(newhtml);
						obj.find("div[class='folder-item-title-content']").find("input").select();
						obj.find("div[class='folder-item-title-content']").find("input").blur(function(){
							var value = obj.find("div[class='folder-item-title-content']").find("input").val();
							if (value.length > 0) {
								var nowhtml = "<span>" + value + "</span>";
								obj.find("div[class='folder-item-title-content']").html(nowhtml);
								if(html != value){
									AjaxPostUtil.request({url: sysMainMation.noteBasePath + "editFileFolderById", params: {name: value, fileType: clickType, id: clickId}, type: 'json', method: 'POST', callback: function (json) {
										winui.window.msg("保存成功", {icon: 1, time: 2000});
										if (clickType == "folder") {
											// 刷新节点
											refreshTreePointNode();
											// 设置选中节点
											chooseNodeSelect(folderId);
										}
										showListById();//获取文件夹和笔记列表
							   		}});
								}
							} else {
								var nowhtml = "<span>" + html + "</span>";
								obj.find("div[class='folder-item-title-content']").html(nowhtml);
								winui.window.msg("标题不能为空", {icon: 2, time: 2000});
							}
						});
					}
				},{
					text: "移动到",
					img: "../../assets/images/icon/create-copy-icon.png",
					callback: function() {
						_openNewWindows({
							url: "../../tpl/note/noteFileTree.html", 
							title: "移动到",
							pageId: "noteFileTree",
							area: ['300px', '400px'],
							callBack: function (refreshCode) {
								if (refreshCode != "" && refreshCode != undefined) {
									if (refreshCode != "-9999") {
										winui.window.msg("保存成功", {icon: 1, time: 2000});
										noteId = "";
										if (clickType == "folder") {
											folderId = refreshCode;
											//刷新节点
											refreshTreePointNode();
											// 设置选中节点
											chooseNodeSelect(folderId);
											var selNode = ztree.getNodeByParam("id", clickId, null);
											ztree.removeNode(selNode);//移除节点
										}
										// 获取文件夹和笔记列表
										showListById();
									} else if (refreshCode == '-9999') {
					                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
					                }
								}
							}
						});
					}
				},{
                    text: "分享",
                    img: "../../assets/images/icon/fx-icon.png",
                    callback: function() {
                        if(clickType == "folder"){
                            winui.window.msg("目前仅支持分享笔记", {icon: 2, time: 2000});
                        } else {
                            var shareUrl = homePagePath + "tpl/note/shareNote.html?id=" + clickId;
                            var json = {"bean":{"shareUrl":shareUrl}};
                            var html = getDataUseHandlebars(getFileContent('tpl/note/shareNoteTemplate.tpl'), json);
							layer.open({
								 title:"笔记链接",
								 type: 1,
								 area: ['40vw','30vh'],
								 content: html //这里content是一个普通的String
							});
							$("#copyBtn").attr("data-clipboard-text", shareUrl);
							//复制
							clipboard = new ClipboardJS('#copyBtn');
							clipboard.on('success', function (e) {
								winui.window.msg("复制成功", {icon: 1, time: 2000});
							});
							clipboard.on('error', function (e) {
								winui.window.msg("浏览器不支持！", {icon: 2, time: 2000});
							});
                        }
                    }
                }
			]
		});
    }
    
	// 默认展示当前最新的笔记
	showNewNoteList();
	
	// 树操作--新建文件夹
	$("body").on("click", ".treecreateNewFolder", function (e) {
		noteId = "";
		if (folderId == "1") {
			folderId = "2";
		}
		hideRMenu();
		AjaxPostUtil.request({url: sysMainMation.noteBasePath + "writeFolder", params: {parentId: folderId, name: '新建文件夹'}, type: 'json', method: 'POST', callback: function (json) {
			// 刷新节点
			refreshTreePointNode();
			folderId = json.bean.id;
			showListById();
			// 执行延时
			setTimeout(function () {
				var selNode = ztree.getNodeByParam("id", json.bean.id, null);
				ztree.editName(selNode);
			}, 1000);
   		}});
	});
	
	// 树操作--新建笔记
	$("body").on("click", ".treecreateNewNote", function (e) {
		addNote("新建笔记", 1);
	});
	
	// 树操作--新建Markdown笔记
    $("body").on("click", ".treecreateNewNoteMarkdown", function (e) {
        addNote("新建Markdown笔记", 2);
    });
    
    // 树操作--新建Markdown笔记
    $("body").on("click", ".treecreateNewNoteExcel", function (e) {
        addNote("新建Excel笔记", 4);
    });
	
	// 树操作--文件夹或者文件删除
	$("body").on("click", ".treedeleteFolderAndChild", function (e) {
		hideRMenu();
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
			clickType = 'folder';
			deleteFileOrNote(folderId);
		});
	});
	
	// 文件夹或者文件重命名
	$("body").on("click", ".fileReName", function (e) {
		hideRMenu();
		var selNode = ztree.getNodeByParam("id", folderId, null);
		ztree.editName(selNode);
	});
	
	// 树操作--刷新
	$("body").on("click", ".treerefreshContent", function (e) {
		hideRMenu();
		refreshTreePointNode();
		showListById();//获取文件夹和笔记列表
	});
	
	// 树操作--输出压缩包
	$("body").on("click", ".treereOutPutZIPContent", function (e) {
		hideRMenu();
		AjaxPostUtil.request({url: sysMainMation.noteBasePath + "outputNoteIsZipJob", params: {id: folderId, type: "folder"}, type: 'json', method: 'POST', callback: function (json) {
			layer.alert('笔记输出压缩包任务已创建，请前往 <a href="../../tpl/jobSpace/jobSpace.html" style="color: #0000ff;" target="_blank">我的输出</a> 查看。');
   		}});
	});
	
	// 保存新编辑的笔记信息
	$("#editMyNote").click(function() {
		saveNoteMation();
	});

	$(document).bind("keydown", function(e) {
		if (e.ctrlKey && (e.which == 83)) {
			e.preventDefault();
			saveNoteMation();
			return false;
		}
	});

	$("body").on("input", "#noteTitle", function() {
		$("#editMyNote").addClass('select');
	});
	$("body").on("change", "#noteTitle", function (e){
		$("#editMyNote").addClass('select');
	});

	// 保存笔记信息
	function saveNoteMation(){
		if ($("#editMyNote").hasClass('select')) {
			$("#editMyNote").removeClass('select');
		} else {
			return false;
		}
		var tilte = $("#noteTitle").val();
		if (isNull(tilte)) {
			winui.window.msg('请填写笔记标题.', {icon: 2, time: 2000});
			return false;
		}
		var content = childframe.window.getContent();//调用子页面方法获取编辑器内容
		var remark = childframe.window.getNoHtmlContent();//调用子页面方法获取编辑器纯文本内容
		AjaxPostUtil.request({url: sysMainMation.noteBasePath + "writeNote", params: {id: noteId, name: tilte, content: content, remark: encodeURIComponent(remark)}, type: 'json', method: "POST", callback: function (json) {
			winui.window.msg("保存成功", {icon: 1, time: 2000});
			var obj = $("#folderChildList").find(".folder-item[id='" + noteId + "']");
			obj.find("div[class='folder-item-title-content']").find("span").html(tilte);
			obj.find("div[class='folder-item-desc']").html(remark);
		}});
	}
	
	//搜索查询
	$("#searchTitle").bind("keypress",function(){
		var e =  window.event;
        if (e && e.keyCode == 13) { //回车键的键值为13
        	noteId = "";
        	searchTitle = $("#searchTitle").val();
    		if(isNewNote) {
    			showNewNoteList();//刷新最新笔记列表
    		} else {
    			showListById();//获取文件夹和笔记列表
    		}
    		searchTitle = "";
        }
	});
	
	// 刷新笔记列表
	window.onloadList = function(){
		if(isNewNote) {
			// 刷新最新笔记列表
			showNewNoteList();
		} else {
			// 获取文件夹和笔记列表
			showListById();
		}
	};
	
	// 新增笔记
    function addNote(title, type){
		if (folderId == "1") {
			thisfolderId = "2";
		} else {
			thisfolderId = folderId;
		}
        hideRMenu();
        AjaxPostUtil.request({url: sysMainMation.noteBasePath + "writeNote", params: {parentId: thisfolderId, name: title, type: type}, type: 'json', method: 'POST', callback: function (json) {
			noteId = json.bean.id;
			// 获取文件夹和笔记列表
			showListById();
       }});
    }
	
	// 删除指定文件夹或笔记
	function deleteFileOrNote(id){
        AjaxPostUtil.request({url: sysMainMation.noteBasePath + "deleteFileFolderById", params: {id: id, fileType: clickType}, type: 'json', method: 'DELETE', callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			if (clickType == "folder") {
				var selNode = ztree.getNodeByParam("id", id, null);
				ztree.selectNode(selNode.getParentNode());// 设置选中节点
				// 重置folderid
				folderId = selNode.getParentNode().id;
				ztree.removeNode(selNode);// 移除节点
			}
			showListById();// 获取文件夹和笔记列表
		}});
	}
	
	// 展示最新的笔记
	function showNewNoteList(){
		$("#folderChildList").empty();
		flow.load({
			elem: '#folderChildList', //指定列表容器
			isAuto: true,
			scrollElem: "#folderChildList",
			done: function(page, next) { //到达临界点（默认滚动触发），触发下一页
				var lis = [];
				AjaxPostUtil.request({url: sysMainMation.noteBasePath + "queryNewNoteListByUserId", params: {page: page, limit: 15, keyword: searchTitle}, type: 'json', method: 'POST', callback: function (json) {
					lis.push(getDataUseHandlebars(folderchildlisetTemplate, json));
					next(lis.join(''), (page * 15) < json.total);
					initRightMenu();
					showNoteContent(json.rows);
		   		}});
			}
		});
	}
	
	//根据文件夹id获取文件夹下的文件夹和笔记列表
	function showListById(){
		if(folderId == "1"){
			isNewNote = true;
			showNewNoteList();//展示最新的笔记
		} else {
			AjaxPostUtil.request({url: sysMainMation.noteBasePath + "mynote006", params: {parentId: folderId, search: searchTitle}, type: 'json', callback: function (json) {
				$("#folderChildList").html(getDataUseHandlebars(folderchildlisetTemplate, json));
				initRightMenu();
				showNoteContent(json.rows);
			}});
		}
	}
	
	//显示笔记内容
	function showNoteContent(data) {
		if (data.length > 0){
			if (!isNull(noteId)){
				$(".note-operator").removeClass("layui-hide");//显示保存按钮
				var noteItem =$("#folderChildList").find(".folder-item[id='" + noteId + "']");
				showContentByItem(noteItem);
			} else {//默认
				var thisnote = $("#folderChildList").find(".folder-item[rowtype != 'folder']").length;//获取第一个类型不是文件夹的对象
				if(thisnote > 0){
					$(".note-operator").removeClass("layui-hide");//显示保存按钮
					var noteItem = $("#folderChildList").find(".folder-item[rowtype != 'folder']").eq(0);
					showContentByItem(noteItem);
				} else {
					$('#folderChildList').children().eq(0).addClass('select');//不包含笔记文件时，默认选中第一个文件夹
					$("#childframe").attr("src", "../../tpl/note/noteFolder.html");
					var filename = $('#folderChildList').children().eq(0).attr("rowname");
					$("#noteTitle").val(filename);//给标题赋值
					$(".note-operator").addClass("layui-hide");//隐藏保存按钮
					noteId = "";
				}
			}
		} else {
			$("#childframe").attr("src", "../../tpl/note/noteFolder.html");
			$("#noteTitle").val("");//给标题赋值
			$(".note-operator").addClass("layui-hide");//隐藏保存按钮
			noteId = "";
		}
	}

	// 根据当前对象进行选中并显示内容
	function showContentByItem(noteItem) {
		noteItem.addClass('select');
		var noteType = noteItem.attr("rowtype");// 默认选中的笔记类型;
		noteId = noteItem.attr("id");// 默认选中的笔记类型;
		loadHtml(noteType);
	}
	
	function loadHtml(noteType){
		if (noteType == 1) {// 如果是富文本编辑器类型，则加载富文本编辑器
			$("#childframe").attr("src", "../../tpl/note/noteEdit.html");
		} else if (noteType == 2) {// MarkDown编辑器类型
            $("#childframe").attr("src", "../../tpl/note/noteEditMarkDown.html");
        } else if (noteType == 4) {// excel编辑器类型
            $("#childframe").attr("src", "../../tpl/note/noteEditLuckysheet.html");
        }
	}
	
    exports('myNote', {});
});
