
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'cookie'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
	    form = layui.form;
	form.render();
	var rowId = GetUrlParam("id");
	
	$(document).attr("title", sysMainMation.mationTitle);
	$(".sys-logo").html(sysMainMation.mationTitle);

	// 隐藏遮罩层
	$(".fileconsole-mask").hide();
	
	loadUserMation();
	function loadUserMation(){
		if(!isNull(getCookie('userToken'))){
			// 获取当前登录员工信息
			systemCommonUtil.getSysCurrentLoginUserMation(function (data){
				var str = '<img alt="' + data.bean.userName + '" src="' + fileBasePath + data.bean.userPhoto + '"/>'
					+ '<font>' + data.bean.userName + '</font>'
					+ '<font id="consoleDesk">控制台</font>'
					+ '<font id="exitBtn">退出</font>';
				$("#operatorBtn").html(str);
				loadNote();
			});
		}else{
			location.href = "../../tpl/index/login.html?url=" + escape("../../tpl/note/shareNote.html?id=" + rowId);
		}
	}
	
	function loadNote(){
		AjaxPostUtil.request({url:reqBasePath + "mynote013", params: {rowId: rowId}, type: 'json', callback: function(json){
			if(json.returnCode == 0){
				$("#noteTile").html(json.bean.title);
				$("#createName").html(json.bean.createName);
				$("#createTime").html(json.bean.createTime);
				var type = json.bean.type;
				if(type == 1){
					// 如果是富文本编辑器类型，则加载富文本编辑器
					$("#content").html(json.bean.content);
				} else if(type == 2){
					// MarkDown编辑器类型
					editormd.markdownToHTML("content",{markdown: json.bean.content});
				} else if(type == 3){
					
				} else if(type == 4){
					$(".share-file-content").css({
						'width': '98%',
						'margin-left': '1%'
					});
					$("#content").html('<div id="luckysheet" style="margin:0px;padding:0px;position:absolute;width:100%;height:100%;left: 0px;top: 0px;"></div>');
					// excel编辑器类型
					var param = {
						container: 'luckysheet',
						showtoolbar: false,
						allowEdit: false,
						showConfigWindowResize: false, // 图表和数据透视表的配置会在右侧弹出，设置弹出后表格是否会自动缩进
						showinfobar: false, // 是否显示顶部名称栏
						enableAddRow: false,
						enableAddCol: false,
						lang: 'zh'
					};
					if(!isNull(json.bean.content)){
						param["data"] = JSON.parse(json.bean.content);
					}
					luckysheet.create(param);
					$("#luckysheet-sheets-add").remove();
				}
				loadPicImgClick();
				matchingLanguage();
   			}else{
   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
   			}
   		}});
	}

	function loadPicImgClick(){
		$("body").on("click", "img", function(){
			var url = $(this).attr("src");
			systemCommonUtil.showPicImg(url);
		});
	}

    // 退出
    $("body").on("click", "#exitBtn", function(){
        winui.window.confirm('确认注销吗?', {id: 'exit-confim', icon: 3, title: '提示', skin: 'msg-skin-message', success: function(layero, index){
            var times = $("#exit-confim").parent().attr("times");
            var zIndex = $("#exit-confim").parent().css("z-index");
            $("#layui-layer-shade" + times).css({'z-index': zIndex});
        }}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "login003", params: {}, type: 'json', method: "POST", callback: function(json){
                $.cookie('userToken', "", {path: '/' });
                location.href = "../../tpl/index/login.html?url=" + escape("../../tpl/note/shareNote.html?id=" + rowId);
            }});
        });
    });
	
    // 控制台
    $("body").on("click", "#consoleDesk", function(){
        location.href = "../../tpl/index/index.html";
    });
});
