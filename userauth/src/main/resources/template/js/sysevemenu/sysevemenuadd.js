
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'colorpicker', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    var colorpicker = layui.colorpicker;
	    
	    var parentId = "0";

	    // 加载图标信息
		systemCommonUtil.initIconChooseHtml('iconMation', form, colorpicker, 12);

        matchingLanguage();
 		form.render();
 		
 		//所属桌面
        showGrid({
            id: "desktop",
            url: reqBasePath + "desktop011",
            params: {language: languageType},
            pagination: false,
            template: getFileContent('tpl/template/select-option-must.tpl'),
            ajaxSendLoadBefore: function(hdb){
            },
            ajaxSendAfter:function(json){
                form.render('select');
                //所属系统
                showGrid({
                	id: "menuSysWinId",
                	url: reqBasePath + "sys031",
                	params: {},
                	pagination: false,
                	template: getFileContent('tpl/template/select-option.tpl'),
                	ajaxSendLoadBefore: function(hdb){
                	},
                	ajaxSendAfter:function(json){
                		form.render('select');
                	}
                });
            }
        });

 		//菜单级别变化事件
 		form.on('radio(menuLevel)', function (data) {
 			var val = data.value;
	    	if(val == '1'){//创世菜单
	    		$("#parentIdBox").addClass("layui-hide");
	    	}else if(val == '2'){
	    		parentId = "0";
	    		$("#lockParentSel").html("");
	    		$("#parentIdBox").removeClass("layui-hide");
	    		loadChildMenu();
	    	} else {
	    		winui.window.msg('状态值错误', {icon: 2, time: 2000});
	    	}
        });
 		
 		//系统菜单同步
 		form.on('switch(isNecessary)', function (data) {
 			//同步开关值
 			$(data.elem).val(data.elem.checked);
 		});
 		
 		//同步共享
 		form.on('switch(isShare)', function (data) {
 			//同步开关值
 			$(data.elem).val(data.elem.checked);
 		});
 		
 		form.on('select(selectParent)', function(data){
			if(data.value != parentId){
				if(isNull(data.value) || data.value == '请选择'){
					layui.$(data.elem).parent('dd').nextAll().remove();
					if(layui.$(data.elem).parent('dd').prev().children('select[class=menuParent]').length > 0){
						parentId = layui.$(data.elem).parent('dd').prev().children('select[class=menuParent]')[0].value;
					} else {
						parentId = "0";
					}
				} else {
					layui.$(data.elem).parent('dd').nextAll().remove();
					parentId = data.value;
					loadChildMenu();
				}
			}
		});
 		
 	    form.on('submit(formAddMenu)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
        			menuName: $("#menuName").val(),
        			menuNameEn: $("#menuNameEn").val(),
					sysWinId: data.field.menuSysWinId,
					desktopId: data.field.desktop,
        			menuUrl: $("#menuUrl").val(),
        			menuType: data.field.menuType
 	        	};

				// 获取图标信息
				params = systemCommonUtil.getIconChoose(params);
				if (!params["iconChooseResult"]) {
					return false;
				}
 	        	
 	        	if(data.field.menuLevel == '1'){//创世菜单
 	        		params.parentId = '0';
 	 	    	}else if(data.field.menuLevel == '2'){//子菜单
 	 	    		var $menu = layui.$('.menuParent');
 	 	    		var str = "";
 	 	    		for(var i = 0; i < $menu.length; i++){
 	 	    			if(!isNull($menu[i].value) && $menu[i].value != '请选择'){
 	 	    				str += $menu[i].value + ",";
 	 	    			}
 	 	    		}
 	 	    		if(isNull(str)){//父菜单为空
 	 	    			winui.window.msg("请至少选择一级父菜单", {icon: 2, time: 2000});
 	 	    			return false;
 	 	    		} else {
 	 	    			params.parentId = str;
 	 	    		}
 	 	    	} else {
 	 	    		winui.window.msg('状态值错误', {icon: 2, time: 2000});
 	 	    	}
 	        	if($("#menuSysType").val() == 'true'){
 	        		params.menuSysType = '1';
 	        	} else {
 	        		params.menuSysType = '2';
 	        	}
 	        	if($("#isShare").val() == 'true'){
 	        		params.isShare = '1';
 	        	} else {
 	        		params.isShare = '0';
 	        	}
 	        	AjaxPostUtil.request({url: reqBasePath + "sys007", params: params, type: 'json', callback: function(json) {
					if (json.returnCode == 0) {
						parent.layer.close(index);
						parent.refreshCode = '0';
					} else {
						winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
					}
	 	   		}});
 	        }
 	        return false;
 	    });
 	    
 	    //加载同级菜单
 	    function loadChildMenu(){
 	    	AjaxPostUtil.request({url:reqBasePath + "sys009", params:{parentId: parentId}, type: 'json', callback: function(json){
 	   			if (json.returnCode == 0) {
 	   				var str = '<dd><select class="menuParent" lay-filter="selectParent" lay-search=""><option value="">请选择</option>';
	 	   			for(var i = 0; i < json.rows.length; i++){
	 	   				str += '<option value="' + json.rows[i].id + '">' + json.rows[i].desktopName + '---------' + json.rows[i].menuName + '---------' + getMenuLevelName(json.rows[i].menuLevel) + '</option>';
	 	   			}
	 	   			str += '</select></dd>';
	 	   			$("#lockParentSel").append(str);
	 	   			form.render('select');
 	   			} else {
 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	   			}
 	   		}});
 	    }
 	    
 	    //获取菜单级别
 	    function getMenuLevelName(level){
 	    	if(level == '0'){
        		return "创世菜单";
        	} else {
        		return level + "级子菜单";
        	}
 	    }
 	    
 	    //初始化加载隐藏创世菜单
 	    $("#parentIdBox").addClass("layui-hide");
	    
 	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
});