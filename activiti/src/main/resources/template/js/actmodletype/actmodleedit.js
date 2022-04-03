
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
	    
	    //初始化动态表单
 		function initDsForm(id){
 			showGrid({
 				id: "dsFormId",
 				url: reqBasePath + "actmodletype020",
 				params: {},
 				pagination: false,
 				template: getFileContent('tpl/template/select-option-must.tpl'),
 				ajaxSendAfter: function(json){
 					form.render('select');
 					$("#dsFormId").val(id);
 				}
 			})
 		}
 		
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "actmodletype011",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/actmodletype/actmodleeditTemplate.tpl'),
		 	ajaxSendAfter:function(json){
				$("input:radio[name=commonUsed][value=" + json.bean.commonUsed + "]").attr("checked", true);

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

				colorpicker.render({
					elem: '#backgroundColor',
					color: json.bean.backgroundColor,
					done: function(color){
						$('#menuIconColorinput').val(color);
					}
				});

				$("input:radio[name=menuIconType][value=" + json.bean.menuIconType + "]").attr("checked", true);
				if(json.bean.menuIconType == '1'){//icon
					$("#menuIconPic").upload({
						"action": reqBasePath + "common003",
						"data-num": "1",
						"data-type": "PNG,JPG,jpeg,gif",
						"uploadType": 17,
						"function": function (_this, data) {
							show("#menuIconPic", data);
						}
					});
					$(".menuIconTypeIsTwo").addClass("layui-hide");
				}else if(json.bean.menuIconType == '2'){//图片
					$("#menuIconPic").upload({
						"action": reqBasePath + "common003",
						"data-num": "1",
						"data-type": "PNG,JPG,jpeg,gif",
						"uploadType": 17,
						"data-value": json.bean.menuIconPic,
						"function": function (_this, data) {
							show("#menuIconPic", data);
						}
					});
					$(".menuIconTypeIsOne").addClass("layui-hide");
				}
				$("#iconShow").attr("class", "fa fa-fw " + $("#menuIcon").val());
				if(isNull(json.bean.menuIconColor)){
					$("#iconShow").css({'color': 'white'});
				}else{
					$('#menuIconColorinput').val(json.bean.menuIconColor);
					$("#iconShow").css({'color': json.bean.menuIconColor});
				}

				// 图标类型变化事件
				form.on('radio(menuIconType)', function (data) {
					var val = data.value;
					if(val == '1'){//icon
						$(".menuIconTypeIsTwo").addClass("layui-hide");
						$(".menuIconTypeIsOne").removeClass("layui-hide");
					}else if(val == '2'){//图片
						$(".menuIconTypeIsTwo").removeClass("layui-hide");
						$(".menuIconTypeIsOne").addClass("layui-hide");
					}else{
						winui.window.msg('状态值错误', {icon: 2,time: 2000});
					}
				});
		 		
		 		textool.init({
			    	eleId: 'remark',
			    	maxlength: 200,
			    	tools: ['count', 'copy', 'reset']
			    });
		 		
		 		//初始化动态表单
		 		initDsForm(json.bean.dsFormId);
		 		
		 		//设置页面类型
		 		$("input:radio[name=pageTypes][value=" + json.bean.pageTypes + "]").attr("checked", true);
		 		
		 		if(json.bean.pageTypes == 1){
		 			$(".TypeIsTwo").addClass("layui-hide");
		    		$(".TypeIsOne").removeClass("layui-hide");
		 		}else{
		 			$(".TypeIsTwo").removeClass("layui-hide");
		    		$(".TypeIsOne").addClass("layui-hide");
		 		}
		 		
		 		//页面类型变化事件
		 		form.on('radio(pageTypes)', function (data) {
		 			var val = data.value;
			    	if(val == '1'){//指定页面
			    		$(".TypeIsTwo").addClass("layui-hide");
			    		$(".TypeIsOne").removeClass("layui-hide");
			    	}else if(val == '2'){//动态表单
			    		$(".TypeIsTwo").removeClass("layui-hide");
			    		$(".TypeIsOne").addClass("layui-hide");
			    	}else{
			    		winui.window.msg('状态值错误', {icon: 2,time: 2000});
			    	}
		        });
		 		
		        matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		 	        		rowId: parent.rowId,
		 	        		title: $("#title").val(),
		 	        		pageUrl: $("#pageUrl").val(),
		 	        		backgroundColor: $('#backgroundColorInput').val(),
		 	        		pageTypes: data.field.pageTypes,
		 	        		tokenUrl: $("#tokenUrl").val(),
		 	        		remark: $("#remark").val(),
							menuIconType: data.field.menuIconType,
							menuIconPic: '',
							menuIcon: $("#menuIcon").val(),
							menuIconColor: $('#menuIconColorinput').val(),
							commonUsed: data.field.commonUsed
		 	        	};
		 	        	if(params.pageTypes == 1){
		        			params.pageUrl = $("#pageUrl").val();
		        			params.editPageUrl = $("#editPageUrl").val();
		        			params.revokeMapping = $("#revokeMapping").val();
		        			if(isNull(params.pageUrl)){
		        				winui.window.msg("请输入新增页面地址", {icon: 2,time: 2000});
		 	        			return false;
		        			}
		        			if(isNull(params.editPageUrl)){
		        				winui.window.msg("请输入编辑页面地址", {icon: 2,time: 2000});
		 	        			return false;
		        			}
		        			if(isNull(params.revokeMapping)){
		        				winui.window.msg("请输入撤销接口", {icon: 2,time: 2000});
		 	        			return false;
		        			}
		        			params.dsFormId = "";
		 	        	}else if(params.pageTypes == 2){
		 	        		params.pageUrl = "";
		 	        		params.editPageUrl = "";
		 	        		params.revokeMapping = "";
		 	        		params.dsFormId = $("#dsFormId").val();
		 	        		if(isNull(params.dsFormId)){
		        				winui.window.msg("请选择表单页面", {icon: 2,time: 2000});
		 	        			return false;
		        			}
		 	        	}
						if(data.field.menuIconType == '1'){
							if(isNull($("#menuIcon").val())){
								winui.window.msg("请选择图标", {icon: 2,time: 2000});
								return false;
							}
							params.menuIconPic = '';
						}else if(data.field.menuIconType == '2'){
							params.menuIconPic = $("#menuIconPic").find("input[type='hidden'][name='upload']").attr("oldurl");
							if(isNull(params.menuIconPic)){
								winui.window.msg('请上传logo', {icon: 2,time: 2000});
								return false;
							}
							params.menuIcon = '';
							params.menuIconColor = '';
						}else{
							winui.window.msg("状态值错误。", {icon: 2,time: 2000});
							return false;
						}

		 	        	AjaxPostUtil.request({url:reqBasePath + "actmodletype012", params:params, type: 'json', callback: function(json){
		 	        		if(json.returnCode == 0){
		 	        			parent.layer.close(index);
		 	        			parent.refreshCode = '0';
		 	        		}else{
		 	        			winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		 	        		}
		 	        	}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});

		// 图标选中事件
		$("body").on("focus", "#menuIcon", function(e){
			systemCommonUtil.openSysEveIconChoosePage(function(sysIconChooseClass){
				$("#menuIcon").val(sysIconChooseClass);
				$("#iconShow").css({'color': 'white'});
				$("#iconShow").attr("class", "fa fa-fw " + $("#menuIcon").val());
			});
		});
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});