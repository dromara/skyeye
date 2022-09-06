
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'colorpicker', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    var colorpicker = layui.colorpicker;
	    
	    AjaxPostUtil.request({url: reqBasePath + "sysevewindragdrop008", params: {rowId: parent.parentRowId}, type: 'json', callback: function (json) {
			$("#menuName").val(json.bean.menuName);
			$("#menuNameEn").val(json.bean.menuNameEn);
			$("#menuIcon").val(json.bean.menuIcon);
			$("#iconShow").attr("class", "fa fa-fw " + json.bean.menuIcon);
			$("#iconShow").parent().css({'background-color': json.bean.menuIconBg});
			$("#iconShow").css({'color': json.bean.menuIconColor});
			$("#menuUrl").val(json.bean.menuUrl);

			$("#menuIconColorinput").val(json.bean.menuIconColor);
			$("#menuIconBginput").val(json.bean.menuIconBg);
			colorpicker.render({
				elem: '#menuIconBg',
				color: json.bean.menuIconBg,
				done: function(color){
					$('#menuIconBginput').val(color);
					$("#iconShow").parent().css({'background-color': color});
				},
				change: function(color){
					$("#iconShow").parent().css({'background-color': color});
				}
			});

			colorpicker.render({
				elem: '#menuIconColor',
				color: json.bean.menuIconColor,
				done: function(color){
					$('#menuIconColorinput').val(color);
					$("#iconShow").css({'color': color});
				},
				change: function(color){
					$("#iconShow").css({'color': color});
				}
			});

			//菜单类型
			$("input:radio[name=menuIconType][value=" + json.bean.menuIconType + "]").attr("checked", true);
			if(json.bean.menuIconType == '1'){//icon
				// 初始化上传
				$("#menuIconPic").upload(systemCommonUtil.uploadCommon003Config('menuIconPic', 12, '', 1));
				$(".menuIconTypeIsTwo").addClass("layui-hide");
			} else if (json.bean.menuIconType == '2'){//图片
				// 初始化上传
				$("#menuIconPic").upload(systemCommonUtil.uploadCommon003Config('menuIconPic', 12, json.bean.menuIconPic, 1));
				$(".menuIconTypeIsOne").addClass("layui-hide");
			}

			matchingLanguage();
			form.render();

			//菜单图标类型变化事件
			form.on('radio(menuIconType)', function (data) {
				var val = data.value;
				if(val == '1'){//icon
					$(".menuIconTypeIsTwo").addClass("layui-hide");
					$(".menuIconTypeIsOne").removeClass("layui-hide");
				} else if (val == '2'){//图片
					$(".menuIconTypeIsTwo").removeClass("layui-hide");
					$(".menuIconTypeIsOne").addClass("layui-hide");
				} else {
					winui.window.msg('状态值错误', {icon: 2, time: 2000});
				}
			});

			form.on('submit(formAddBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					var params = {
						menuName: $("#menuName").val(),
						menuNameEn: $("#menuNameEn").val(),
						menuIcon: $("#menuIcon").val(),
						menuUrl: $("#menuUrl").val(),
						menuIconType: data.field.menuIconType,
						menuIconBg: $('#menuIconBginput').val(),
						menuIconColor: $('#menuIconColorinput').val(),
						rowId: parent.parentRowId
					};

					if (data.field.menuIconType == '1'){
						if(isNull($("#menuIcon").val())) {
							winui.window.msg("请选择菜单图标", {icon: 2, time: 2000});
							return false;
						}
						params.menuIconPic = '';
					} else if (data.field.menuIconType == '2'){
						params.menuIconPic = $("#menuIconPic").find("input[type='hidden'][name='upload']").attr("oldurl");
						if(isNull(params.menuIconPic)){
							winui.window.msg('请上传菜单logo', {icon: 2, time: 2000});
							return false;
						}
						params.menuIcon = '';
						params.menuIconBg = '';
						params.menuIconColor = '';
					} else {
						winui.window.msg("状态值错误。", {icon: 2, time: 2000});
						return false;
					}

					AjaxPostUtil.request({url: reqBasePath + "sysevewindragdrop009", params: params, type: 'json', callback: function (json) {
						parent.childParams = params;
						parent.layer.close(index);
						parent.refreshCode = '0';
					}});
				}
				return false;
			});

			// 菜单图标选中事件
			$("body").on("focus", "#menuIcon", function (e) {
				systemCommonUtil.openSysEveIconChoosePage(function(sysIconChooseClass){
					$("#menuIcon").val(sysIconChooseClass);
					$("#iconShow").css({'color': 'white'});
					$("#iconShow").attr("class", "fa fa-fw " + $("#menuIcon").val());
				});
			});
   		}});
	    
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
	    
});