

var userAgent = navigator.userAgent;
if (userAgent.indexOf('AlipayClient') > -1) {
	// 支付宝小程序的 JS-SDK 防止 404 需要动态加载，如果不需要兼容支付宝小程序，则无需引用此 JS 文件。
	document.writeln('<script src="https://appx/web-view.min.js"' + '>' + '<' + '/' + 'script>');
} else if (/QQ/i.test(userAgent) && /miniProgram/i.test(userAgent)) {
	// QQ 小程序
	document.write('<script type="text/javascript" src="https://qqq.gtimg.cn/miniprogram/webview_jssdk/qqjssdk-1.0.0.js"><\/script>');
} else if (/miniProgram/i.test(userAgent)) {
	// 微信小程序 JS-SDK 如果不需要兼容微信小程序，则无需引用此 JS 文件。
	document.write('<script type="text/javascript" src="https://res.wx.qq.com/open/js/jweixin-1.4.0.js"><\/script>');
} else if (/toutiaomicroapp/i.test(userAgent)) {
	// 头条小程序 JS-SDK 如果不需要兼容头条小程序，则无需引用此 JS 文件。
	document.write('<script type="text/javascript" src="https://s3.pstatp.com/toutiao/tmajssdk/jssdk-1.0.1.js"><\/script>');
} else if (/swan/i.test(userAgent)) {
	// 百度小程序 JS-SDK 如果不需要兼容百度小程序，则无需引用此 JS 文件。
	document.write('<script type="text/javascript" src="https://b.bdstatic.com/searchbox/icms/searchbox/js/swan-2.0.18.js"><\/script>');
}
if (!/toutiaomicroapp/i.test(userAgent)) {
	if(!isNull(document.querySelector('.post-message-section'))){
		document.querySelector('.post-message-section').style.visibility = 'visible';
	}
}

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var $ = layui.$;
		//手机端加载该页面时加载userToken
		var userToken = GetUrlParam("userToken");
		if(!isNull(userToken)){
			setCookie('userToken', userToken);
		}
		var fileUrl = GetUrlParam("fileUrl");
		var title = decodeURIComponent(GetUrlParam("title"));
		var selFileType = GetUrlParam("selFileType");
		var thisId = GetUrlParam("thisId");
		var documentType = "";
		var mode = "edit";
		
		if (selFileType == "docx" || selFileType == "DOCX") {
			documentType = "text";
		} else if (selFileType == "wps" || selFileType == "WPS") {
			documentType = "text";
		} else if (selFileType == "XLSX" || selFileType == "xlsx") {
			documentType = "spreadsheet";
		} else if (selFileType == "ET" || selFileType == "et") {
			documentType = "spreadsheet";
		} else if (selFileType == "pptx" || selFileType == "PPTX") {
			documentType = "presentation";
		} else if (selFileType == "dps" || selFileType == "DPS") {
			documentType = "presentation";
		} else if (selFileType == "doc" || selFileType == "DOC") {
			documentType = "text";
		} else if (selFileType == "XLS" || selFileType == "xls") {
			documentType = "spreadsheet";
		} else if (selFileType == "csv" || selFileType == "CSV") {
			documentType = "spreadsheet";
		} else if (selFileType == "ppt" || selFileType == "PPT") {
			documentType = "presentation";
		} else if (selFileType == "pdf" || selFileType == "PDF") {
			documentType = "text";
			mode = "view";
		}
		
		$("title").html(title);
		// 获取当前登录员工信息
		var currentUserMation = {};
		systemCommonUtil.getSysCurrentLoginUserMation(function (data){
			currentUserMation = data.bean;
		});
		AjaxPostUtil.request({url: sysMainMation.diskCloudBasePath + "fileconsole036", params: {rowId: thisId}, type: 'json', callback: function(j){
			if(!isNull(j.bean)){
				window.docEditor = new DocsAPI.DocEditor("placeholder", {
					"document": {
						"fileType": selFileType,//定义源查看或编辑文档的文件类型
						"key": thisId + "-" + j.bean.updateTime + "",//定义服务用于文档识别的唯一文档标识符。 如果发送已知密钥，文档将从缓存中获取。 每次文档被编辑和保存时，都必须重新生成密钥。 文档url可以用作密钥，但不包含特殊字符，长度限制为20个符号。（注意如果秘钥值不更换那么看到的文档还是最先加载的缓存文档）
						"title": title,//为查看或编辑的文档定义所需的文件名，当文档被下载时它也将被用作文件名。
						"url": sysMainMation.fileBasePath + fileUrl,//定义存储源查看或编辑文档的绝对URL
						"userdata": thisId
					},
					"documentType": documentType,//文件编辑类型，根据文件的类型在客户端用不通的编辑器来编辑文件主要三种 文档类-text、表格类-spreadsheet、ppt类-presentation
					"editorConfig": {
						"callbackUrl": sysMainMation.diskCloudBasePath + "fileconsole010?id=" + thisId,//文件关闭后回调路劲 这个用来保存文件用的 文件编辑保存后 当你关闭窗口后 server端会请求把你在服务器上的编辑提交到这个路劲 ，所以这个路劲的代码 一般就是上传保存 ；
						"lang": "zh-CN",//"en-US",汉化
						"mode": mode,
						"user": {
							"id": currentUserMation.id,
							"name": currentUserMation.userName
						},
						"customization":{
							"autosave": true,
							"chat": true,
							"commentAuthorOnly": false,
							"compactToolbar": false,
							"zoom": 100,
							"about": true,
							"customer": {//关于
								"address": "广州",
								"info": "",
								"logo": "",
								"mail": "598748873@qq.com",
								"name": "skyeye",
								"www": "https://gitee.com/doc_wei01/skyeye"
							},
							"feedback": {//反馈与支持
								"url": "https://gitee.com/doc_wei01/skyeye",
								"visible": true
							},
							"logo": {//自定义编辑器左上角标题和跳转地址
								"image": "https://example.com/logo.png",
								"imageEmbedded": "https://example.com/logo_em.png",
								"url": "https://gitee.com/doc_wei01/skyeye"
							},
							"goback": {//自定义按钮
								"blank": true,
								"text": "公司",
								"url": "https://gitee.com/doc_wei01/skyeye"
							},
							"help": true,
							"showReviewChanges": false,
							"forcesave": false//在编辑器初始化配置中将其设置为true,在这种情况下，每次用户单击“ 保存”按钮时，将请求发送回ONLYOFFICE Document Server执行保存操作
						}
					},
					"events": {
						"onAppReady": onAppReady,
						"onDocumentStateChange": onDocumentStateChange,
						'onRequestEditRights': onRequestEditRights,
						"onError": onError,
						"onOutdatedVersion": onOutdatedVersion
					},
					"height": "100%",//页面高度
					"width": "100%",//页面宽度
					"type": "desktop"
				});

				matchingLanguage();
			} else {
				winui.window.msg(j.returnMessage, {icon: 2, time: 2000});
			}
		}});

		var onAppReady = function () {
            innerAlert("Document editor ready");
        };
        
        var onDocumentStateChange = function (event) {
            var title = document.title.replace(/\*$/g, "");
            document.title = title + (event.data ? "*" : "");
        };
        
        var onRequestEditRights = function () {
            location.href = location.href.replace(RegExp("mode=view\&?", "i"), "");
        };

        var onError = function (event) {
            if (event)
                innerAlert(event.data);
        };

        var onOutdatedVersion = function (event) {
            location.reload(true);
        };
		
		function s20(){
			var data = ["0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","a",
			          "b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"];
			var result = "";
			for(var i = 0; i < 20; i++){
				var r = Math.floor(Math.random() * 62);		//取得0-62间的随机数，目的是以此当下标取数组data里的值！
				result += data[r];		//输出20次随机数的同时，让rrr加20次，就是20位的随机字符串了。
			}
			return result;
		}
	});
});

