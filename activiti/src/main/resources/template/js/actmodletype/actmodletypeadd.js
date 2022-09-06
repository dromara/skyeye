
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'colorpicker', 'textool', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    var colorpicker = layui.colorpicker,
	    	textool = layui.textool;

		// 初始化上传
		$("#menuIconPic").upload(systemCommonUtil.uploadCommon003Config('menuIconPic', 17, '', 1));

	    //初始化动态表单
	    initDsForm();
	    
	    textool.init({eleId: 'remark', maxlength: 200});

		colorpicker.render({
			elem: '#menuIconColor',
			done: function(color){
				$('#menuIconColorInput').val(color);
				$("#iconShow").css({'color': color});
			},
			change: function(color){
				$("#iconShow").css({'color': color});
			}
		});

		colorpicker.render({
			elem: '#backgroundColor',
			done: function(color){
				$('#backgroundColorInput').val(color);
			}
		});

		// 图标类型变化事件
		form.on('radio(menuIconType)', function (data) {
			var val = data.value;
			if(val == '1'){//icon
				$(".menuIconTypeIsTwo").addClass("layui-hide");
				$(".menuIconTypeIsOne").removeClass("layui-hide");
			}else if(val == '2'){//图片
				$(".menuIconTypeIsTwo").removeClass("layui-hide");
				$(".menuIconTypeIsOne").addClass("layui-hide");
			} else {
				winui.window.msg('状态值错误', {icon: 2, time: 2000});
			}
		});

        showGrid({
            id: "actId",
            url: flowableBasePath + "activitimode009",
            params: {},
            pagination: false,
            template: getFileContent('tpl/template/select-option.tpl'),
            ajaxSendLoadBefore: function(hdb) {
            },
            ajaxSendAfter:function (json) {
                form.render('select');
            }
        });
        
        //页面类型变化事件
 		form.on('radio(pageTypes)', function (data) {
 			var val = data.value;
	    	if(val == '1'){//指定页面
	    		$(".TypeIsTwo").addClass("layui-hide");
	    		$(".TypeIsOne").removeClass("layui-hide");
	    	}else if(val == '2'){//动态表单
	    		$(".TypeIsTwo").removeClass("layui-hide");
	    		$(".TypeIsOne").addClass("layui-hide");
	    	} else {
	    		winui.window.msg('状态值错误', {icon: 2, time: 2000});
	    	}
        });
 		
 		//初始化动态表单
 		function initDsForm(){
 			showGrid({
 				id: "dsFormId",
 				url: flowableBasePath + "actmodletype020",
 				params: {},
 				pagination: false,
 				template: getFileContent('tpl/template/select-option-must.tpl'),
 				ajaxSendAfter: function (json) {
 					form.render('select');
 				}
 			})
 		}
 		
 		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
 	        		typeId: parent.rowId,
 	        		title: $("#typeName").val(),
 	        		actId: $("#actId").val(),
 	        		backgroundColor: $('#backgroundColorInput').val(),
 	        		pageTypes: data.field.pageTypes,
 	        		tokenUrl: $("#tokenUrl").val(),
 	        		remark: $("#remark").val(),
					menuIconType: data.field.menuIconType,
					menuIconPic: '',
					menuIcon: $("#menuIcon").val(),
					menuIconColor: $('#menuIconColorInput').val(),
					commonUsed: data.field.commonUsed
 	        	};
 	        	if(params.pageTypes == 1){
        			params.pageUrl = $("#pageUrl").val();
        			params.editPageUrl = $("#editPageUrl").val();
        			params.revokeMapping = $("#revokeMapping").val();
        			if(isNull(params.pageUrl)){
        				winui.window.msg("请输入新增页面地址", {icon: 2, time: 2000});
 	        			return false;
        			}
        			if(isNull(params.editPageUrl)){
        				winui.window.msg("请输入编辑页面地址", {icon: 2, time: 2000});
 	        			return false;
        			}
        			if(isNull(params.revokeMapping)){
        				winui.window.msg("请输入撤销接口", {icon: 2, time: 2000});
 	        			return false;
        			}
        			params.dsFormId = "";
 	        	}else if(params.pageTypes == 2){
 	        		params.pageUrl = "";
 	        		params.editPageUrl = "";
 	        		params.revokeMapping = "";
 	        		params.dsFormId = $("#dsFormId").val();
 	        		if(isNull(params.dsFormId)){
        				winui.window.msg("请选择表单页面", {icon: 2, time: 2000});
 	        			return false;
        			}
 	        	}
				if(data.field.menuIconType == '1'){
					if(isNull($("#menuIcon").val())) {
						winui.window.msg("请选择图标", {icon: 2, time: 2000});
						return false;
					}
					params.menuIconPic = '';
				}else if(data.field.menuIconType == '2'){
					params.menuIconPic = $("#menuIconPic").find("input[type='hidden'][name='upload']").attr("oldurl");
					if(isNull(params.menuIconPic)){
						winui.window.msg('请上传logo', {icon: 2, time: 2000});
						return false;
					}
					params.menuIcon = '';
					params.menuIconColor = '';
				} else {
					winui.window.msg("状态值错误。", {icon: 2, time: 2000});
					return false;
				}

 	        	AjaxPostUtil.request({url:flowableBasePath + "actmodletype003", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
 	        	}});
 	        }
 	        return false;
 	    });

		// 图标选中事件
		$("body").on("focus", "#menuIcon", function (e) {
			systemCommonUtil.openSysEveIconChoosePage(function(sysIconChooseClass){
				$("#menuIcon").val(sysIconChooseClass);
				$("#iconShow").css({'color': 'white'});
				$("#iconShow").attr("class", "fa fa-fw " + $("#menuIcon").val());
			});
		});
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});