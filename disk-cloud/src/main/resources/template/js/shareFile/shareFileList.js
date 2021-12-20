
var folderId = "";//文件夹id，用于进入下一级

var shareId = "";//分享id

var pathArray = new Array();//文件路径数组

var chooseSaveIds = new Array();//选择的要保存呢的文件或者文件夹id

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'cookie'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
		var $ = layui.$;
		
		MiniSite.JsLoader.load(reqBasePath + "json/main.js", function(){
			$.cookie('sysMainMation', "", {path: '/' });
			setCookie('sysMainMation', JSON.stringify(sysMainMation), 'd30');
			$(document).attr("title", sysMainMation.mationTitle);
			$(".sys-logo").html(sysMainMation.mationTitle);
		});
		var rowId = GetUrlParam("id");
	    
		AjaxPostUtil.request({url:reqBasePath + "fileconsole019", params: {rowId: rowId}, type:'json', callback:function(json){
   			if(json.returnCode == 0){
   				if(isNull(json.bean)){//文件不存在
   					location.href = "../../tpl/shareFile/shareFilepwd.html?id=" + rowId;
   				}else{
   					$("#userName").html(json.bean.userName);
   					$("#userPhoto").attr("src", json.bean.userPhoto);
   					$("#userPhoto").attr("alt", json.bean.userName);
   					if(json.bean.shareType == 2){//私密分享
   						if(isNull(getCookie("file" + rowId))){//输入的提取码为空
   							location.href = "../../tpl/shareFile/shareFilepwd.html?id=" + rowId;
   						}else{//输入的提取码不为空
   							AjaxPostUtil.request({url:reqBasePath + "fileconsole020", params: {rowId: rowId, sharePassword: getCookie("file" + rowId)}, type:'json', callback:function(json){
   					   			if(json.returnCode == 0){
   					   				if(isNull(json.bean)){//分享取消
   					   					location.href = "../../tpl/shareFile/shareFilepwd.html?id=" + rowId;
   					   				}else{//加载列表
   					   					loadUserMation();
   					   				}
   					   			}else{//提取码错误
   					   				location.href = "../../tpl/shareFile/shareFilepwd.html?id=" + rowId;
   					   			}
   					   		}});
   						}
   					}else if(json.bean.shareType == 1){//公开分享--加载列表
   						loadUserMation();
   					}
   				}
   				form.render();
   			}else{
   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
   			}
   		}});
		
		//加载用户信息
		function loadUserMation(){
			if(!isNull(getCookie('userToken'))){
				AjaxPostUtil.request({url:reqBasePath + "login002", params:{}, type:'json', callback:function(json){
					if(json.returnCode == 0){
						var str = '<img alt="' + json.bean.userName + '" src="' + fileBasePath + json.bean.userPhoto + '"/>'
						+ '<font>' + json.bean.userName + '</font>'
						+ '<font id="consoleDesk">控制台</font>'
						+ '<font id="exitBtn">退出</font>';
						$("#operatorBtn").html(str);
						$("#file-operator").html('<button class="layui-btn layui-btn-primary" id="saveBtn"><language showName="com.skyeye.save"></language></button>');
					}else{
						$("#operatorBtn").html('<font id="loginBtn">登陆</font>');
					}
					loadFileMation();
				}});
			}else{
				$("#operatorBtn").html('<font id="loginBtn">登陆</font>');
				loadFileMation();
			}
		}
		
		//加载列表
		function loadFileMation(){
			//加载分享基础信息
			AjaxPostUtil.request({url:reqBasePath + "fileconsole021", params: {rowId: rowId}, type:'json', callback:function(json){
	   			if(json.returnCode == 0){
	   				var str = "";
	   				if(json.bean.fileType == 1){//文件夹
	   					str = '<img src="../../assets/images/share-folder.png"/>';
	   				}else if(json.bean.fileType == 2){//文件
	   					str = '<img src="../../assets/images/share-file.png"/>';
	   				}
	   				$("#fileTitle").html(str + json.bean.shareName);
	   				$("#shareTime").html(json.bean.createTime);
	   				folderId = "-1";
	   				shareId = json.bean.id;
	   				$("#filePathShow").html('<a><cite>全部文件</cite></a>');
	   				loadFileList();
	   			}else{//提取码错误
	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	   			}
	   		}});
		}
		
		function loadFileList(){
			$("#checkAll").prop("checked", false);
			showGrid({
			 	id: "fileListContent",
			 	url: reqBasePath + "fileconsole022",
			 	params: {folderId: folderId, shareId: shareId},
			 	pagination: false,
			 	template: getFileContent('tpl/shareFile/shareFileItem.tpl'),
			 	ajaxSendLoadBefore: function(hdb){},
			 	options: {},
			 	ajaxSendAfter:function(json){
					matchingLanguage();
				}
		    });
		}
		
		//鼠标移入移出
		$("body").on("mouseenter", ".file-list-content .file-item", function(){
			$(this).find(".download").show();
		});
		$("body").on("mouseleave", ".file-list-content .file-item", function(){
			$(this).find(".download").hide();
		});
		
		//下载按钮
		$("body").on("click", ".file-list-content .file-item .size .download", function(e){
			if(isNull(getCookie('userToken'))){//用户信息为空，提示登陆
				layer.confirm("该操作需要登陆后才可继续进行。", { icon: 3, title: '系统提示', btn: ['前往登陆','稍后登陆'] }, function (index) {
					layer.close(index);
					location.href = "../../tpl/index/login.html?url=" + escape("../../tpl/shareFile/shareFileList.html?id=" + rowId);
				});
			}else{
				var operaterId = $(this).parent().parent().attr("rowid");
				var fileType = $(this).parent().parent().attr("filetype");
				if(fileType === 'folder'){//文件夹
					winui.window.msg('暂不提供文件夹的下载。', {icon: 2,time: 2000});
				}else{//文件
					AjaxPostUtil.request({url:reqBasePath + "fileconsole009", params:{rowId: operaterId}, type:'json', callback:function(json){
						if(json.returnCode == 0){
							if($.inArray(json.bean.fileType, imageType) >= 0){//图片
								downloadImage(fileBasePath + json.bean.fileAddress, json.bean.fileName);
							}else{
								download(fileBasePath + json.bean.fileAddress, json.bean.fileName);
							}
						}else{
							winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
						}
					}});
				}
			}
			e.stopPropagation();
		});
		
		//文件夹点击
		$("body").on("click", ".file-list-content .file-item", function(){
			var fileType = $(this).attr("filetype");
			if(fileType === 'folder'){//文件夹
				folderId = $(this).attr("rowid");
				reFreshAllFilePath(folderId, $(this).find(".file_name").find("font").html());
				loadFileList();
			}
		});
		
		//保存
		$("body").on("click", "#saveBtn", function(){
			var checkList = $("#fileListContent :checkbox:checked");
			if(checkList.length > 0){
				chooseSaveIds.splice(0, chooseSaveIds.length);
				$.each(checkList, function(i, item){
					var fId = $(item).parent().parent().attr("rowid");
					var fType = $(item).parent().parent().attr("filetype");
					var params = {
						rowId: fId,
						rowType: fType
					};
					chooseSaveIds.push(params);
				});
				_openNewWindows({
					url: "../../tpl/shareFile/fileFolder.html", 
					title: "文件目录",
					pageId: "filefolderpage",
					area: ['300px', '400px'],
					skin: 'add-schedule-mation',
					callBack: function(refreshCode){
						if (refreshCode == '0') {
		                	winui.window.msg("保存成功", {icon: 1,time: 2000});
		                }
					}});
			}else{
				winui.window.msg('请选择需要保存的文件。', {icon: 7,time: 2000});
			}
		});
		
		//全选/全不选
		$("body").on("click", "#checkAll", function(){
			$("#fileListContent :checkbox").prop("checked", $(this).prop("checked"));
		});
		
		//列表选中
		$("body").on("change", "#fileListContent :checkbox", function(e){
			var length = $("#fileListContent").find(".file-item").length;
			var checkLength = $("#fileListContent :checkbox:checked").length;
			if(length == checkLength){
				$("#checkAll").prop("checked", true);
			}else{
				$("#checkAll").prop("checked", false);
			}
			e.stopPropagation();
		});
		//阻止冒泡
		$("body").on("click", "#fileListContent .file-item .check_box", function(e){
			e.stopPropagation();
		});
		$("body").on("click", "#fileListContent .file-item .check_box .checkLabel", function(e){
			e.stopPropagation();
		});
		
		//登陆按钮
		$("body").on("click", "#loginBtn", function(){
			location.href = "../../tpl/index/login.html?url=" + escape("../../tpl/shareFile/shareFileList.html?id=" + rowId);
		});
		
		//退出
		$("body").on("click", "#exitBtn", function(){
			winui.window.confirm('确认注销吗?', {id: 'exit-confim', icon: 3, title: '提示', skin: 'msg-skin-message', success: function(layero, index){
				var times = $("#exit-confim").parent().attr("times");
				var zIndex = $("#exit-confim").parent().css("z-index");
				$("#layui-layer-shade" + times).css({'z-index': zIndex});
			}}, function (index) {
				layer.close(index);
	        	AjaxPostUtil.request({url:reqBasePath + "login003", params:{}, type:'json', callback:function(json){
	        		$.cookie('userToken', "", {path: '/' });
	        		$("#operatorBtn").html('<font id="loginBtn">登陆</font>');
	        		$("#file-operator").html("");
	 	   		}});
	        });
		});
		
		//控制台
		$("body").on("click", "#consoleDesk", function(){
			location.href = "../../tpl/index/index.html";
		});
		
		//全部文件
		$("body").on("click", "#allFileList", function(){
			folderId = '-1';
			$("#filePathShow").html('<a><cite>全部文件</cite></a>');
			//清空路径数组
			pathArray.splice(0, pathArray.length);
			loadFileList();
		});
		
		//上一步
		$("body").on("click", "#upFolderPath", function(){
			if(pathArray.length > 1){
				folderId = pathArray[pathArray.length - 2].id;
			}else{
				folderId = '-1';
			}
			reFreshAllFilePathISUp();
			loadFileList();
		});
		
		//文件目录点击
		$("body").on("click", ".folderNamePath", function(){
			folderId = $(this).attr("rowid");
			var pathArrayIndex = 0;
			$.each(pathArray, function(i, item){
				if(item.id === folderId){
					pathArrayIndex = i;
					return;
				}
			});
			//清空路径数组
			pathArray.splice(pathArrayIndex + 1, pathArray.length);
			loadPath();
			loadFileList();
		});
		
		//进入指定文件
		function reFreshAllFilePath(id, fileName){
			var newFileObj = {
				id: id,
				fileName: fileName
			};
			pathArray.push(newFileObj);
			loadPath();
		}
		
		//上一步
		function reFreshAllFilePathISUp(){
			pathArray.splice(pathArray.length - 1, 1);
			loadPath();
		}
		
		//重新加载文件路径
		function loadPath(){
			if(pathArray.length > 0){
				var str = '<a href="javascript:;" id="upFolderPath">返回上一级</a><span lay-separator="">|</span>'
		            + '<a href="javascript:;" id="allFileList">全部文件</a><span lay-separator="">></span>';
				$.each(pathArray, function(i, item){
					if(i == pathArray.length - 1){
						str += '<a><cite>' + item.fileName + '</cite></a>';
					}else{
						str += '<a href="javascript:;" rowid="' + item.id + '" class="folderNamePath">' + item.fileName + '</a><span lay-separator="">></span>';
					}
				});
				$("#filePathShow").html(str);
			}else{
				$("#filePathShow").html('<a><cite>全部文件</cite></a>');
			}
		}
		
	});
});

