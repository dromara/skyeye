
// 获取指定节点的所有父节点的名字
function getFilePath(treeObj) {
    if (treeObj == null) return "";
    var filename = '<li class="yarnlet folderName" rowid="' + treeObj.id + '"><a title="' + treeObj.name + '">' + treeObj.name + '/</a></li>';
    var pNode = treeObj.getParentNode();
    if (pNode != null) {
        filename = getFilePath(pNode) + filename;
    }
    return filename;
}

// 创建Word文件
function createWordFile(folderId, callBack){
	AjaxPostUtil.request({url:reqBasePath + "fileconsole025", params:{folderId: folderId}, type: 'json', callback: function(json){
		if (json.returnCode == 0) {
			winui.window.msg("创建成功", {icon: 1, time: 2000});
			if(typeof(callBack) == "function") {
				callBack();
			}
		}else{
			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		}
	}});
}

// 创建Excel文件
function createExcelFile(folderId, callBack){
	AjaxPostUtil.request({url:reqBasePath + "fileconsole026", params:{folderId: folderId}, type: 'json', callback: function(json){
		if (json.returnCode == 0) {
			winui.window.msg("创建成功", {icon: 1, time: 2000});
			if(typeof(callBack) == "function") {
				callBack();
			}
		}else{
			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		}
	}});
}

// 创建PPT文件
function createPPTFile(folderId, callBack){
	AjaxPostUtil.request({url:reqBasePath + "fileconsole027", params:{folderId: folderId}, type: 'json', callback: function(json){
		if (json.returnCode == 0) {
			winui.window.msg("创建成功", {icon: 1, time: 2000});
			if(typeof(callBack) == "function") {
				callBack();
			}
		}else{
			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		}
	}});
}

// 创建TXT文件
function createTXTFile(folderId, callBack){
	AjaxPostUtil.request({url:reqBasePath + "fileconsole028", params:{folderId: folderId}, type: 'json', callback: function(json){
		if (json.returnCode == 0) {
			winui.window.msg("创建成功", {icon: 1, time: 2000});
			if(typeof(callBack) == "function") {
				callBack();
			}
		}else{
			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		}
	}});
}

// 创建Html文件
function createHtmlFile(folderId, callBack){
	AjaxPostUtil.request({url:reqBasePath + "fileconsole029", params:{folderId: folderId}, type: 'json', callback: function(json){
		if (json.returnCode == 0) {
			winui.window.msg("创建成功", {icon: 1, time: 2000});
			if(typeof(callBack) == "function") {
				callBack();
			}
		}else{
			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		}
	}});
}

// 创建文件夹
function createFolder(folderId, refreshCallBack, initDragCallBack, currentUserId){
	AjaxPostUtil.request({url:reqBasePath + "fileconsole002", params: {parentId: folderId, catalogName: '新建文件夹'}, type: 'json', callback: function(json){
		if (json.returnCode == 0) {
			initNewFolder(json.bean.id, json.bean.catalogName, json.bean.logoPath, true, currentUserId);
			// 刷新节点
			if(typeof(refreshCallBack) == "function") {
				refreshCallBack();
			}
			// 初始化拖拽
			if(typeof(initDragCallBack) == "function") {
				initDragCallBack();
			}
		}else{
			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		}
	}});
}

// 加载新建的文件夹
function initNewFolder(id, folderName, icon, isEdit, currentUserId){
	$("#file-content").find("div[class='filename-edit']").hide();
	$("#file-content").find("div[class='filename']").show();
	var json = {
		bean: {
			id: id,
			name: folderName,
			icon: icon,
			isShow: 'none',
			createId: currentUserId
		}
	};
	var html = getDataUseHandlebars(folderTemplate, json);
	if(isEdit){
		var dom = $(html);
		dom.find("div[class='filename']").hide();
		dom.find("div[class='filename-edit']").show();
		dom.find("div[class='filename-edit']").find("textarea").val(folderName);
		html = dom.prop("outerHTML");
	}
	$("#file-content").html(html + $("#file-content").html());
	if(isEdit){
		$("#file-content").find('div[rowid="' + id + '"]').find("div[class='filename-edit']").find("textarea").focus();
		$("#file-content").find('div[rowid="' + id + '"]').find("div[class='filename-edit']").find("textarea").select();
	}
}

layui.define(["jquery"], function(exports) {
	var jQuery = layui.jquery;
	(function($) {
		// 阻止冒泡的点击事件
		$("body").on("click", "#file-content .menu-folder .item-select .item-check .checkLabel", function(e){
			e.stopPropagation();
		});
		$("body").on("dblclick", "#file-content .menu-folder .item-select .item-check," +
				"#file-content .menu-folder .item-select .item-check .checkLabel", function(e){
			e.stopPropagation();
		});
		
		// 鼠标移出选项
		$("body").on("mouseleave", ".layui-dropdown-menu", function(e){
			$(this).hide();
		});
		
		// 展示方式
		$("body").on("click", ".showFileType", function(e){
			$(".showFileType").find("img").attr("src", "");
			$(this).find("img").attr("src", "../../assets/images/icon_choose.png");
			if($(this).attr("data-type") === '1'){
				$("#showTypeChoose").find("i").attr("class", "fa fa-th-large");
				$("#showListStyle").html('');
			}else{
				$("#showTypeChoose").find("i").attr("class", "fa fa-th-list");
				$("#showListStyle").html('.right-center-is-content .file{height: 30px; width: 100%; margin-bottom: 0px;}.right-center-is-content .file .ico{width: 24px; height: 24px; float: left; margin-left: 25px;}'
											+ '.right-center-is-content .file .ico img{width: 20px; max-height: 24px}'
											+ '.right-center-is-content .file .filename{-webkit-line-clamp: 1; float: left;width: calc(100% - 250px); text-align: left; padding-left: 10px; height: 30px; line-height: 30px;}'
											+ '.right-center-is-content .file .filename-edit{-webkit-line-clamp: 1; float: left;width: calc(100% - 250px); text-align: left; padding-left: 10px; height: 30px; line-height: 30px;}'
											+ '.right-center-is-content .file .filename-edit textarea{min-height: 30px !important; height: 30px; width: calc(100% - 70px)}'
											+ '.right-center-is-content .file .ico .meta-info{margin-top: -26px; margin-right: 20px;}'
											+ '.right-center-is-content .file .item-menu{top: 7px}'
											+ '.right-center-is-content .file .uploadtime{display: block !important; float: left; width: 130px; line-height: 20px; margin-top: 5px; font-size: 0.9em; color: RGBA(109, 109, 109); border-left: 1px solid RGBA(109, 109, 109);}'
											+ '.right-center-is-content .file .filename-edit img{margin-top: -30px; margin-right: 45px;}'
											+ '.right-center-is-content .file .filename-edit .filename-edit-save{margin-right: 20px;}'
											+ '.right-center-is-content .file .item-select .item-check{width: 25px}'
											+ '.right-center-is-content .file:nth-of-type(odd){background: floralwhite;}');
			}
		});
		
		// 桌面点击时，取消所有选中
		$("body").on("click", "#file-content", function(e){
			$("#file-content .menu-folder .item-select .item-check").find("input:checkbox[name='checkFile']:checked").prop("checked", false);
			$("#file-content .menu-folder").removeClass("active");
			$(".select-op-more").hide();
			$(".ul-context-menu").hide();
			$("#fileRightMenu").hide();
			e.stopPropagation();
		});
		
	})(jQuery);
});
