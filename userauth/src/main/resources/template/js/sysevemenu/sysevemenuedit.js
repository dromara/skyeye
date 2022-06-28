
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
	    
	    //初始化数据
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sys008",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/sysevemenu/sysevemenueditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		//是否为系统菜单
		 		hdb.registerHelper("compare2", function(v1, options){
					if(v1 == '1'){
						return 'checked';
					}else if(v1 == '2'){
						return '';
					} else {
						return '';
					}
				});
		 		hdb.registerHelper("compare3", function(v1, options){
					if(v1 == '1'){
						return 'true';
					}else if(v1 == '2'){
						return 'false';
					} else {
						return 'false';
					}
				});
		 		//同步共享
		 		hdb.registerHelper("compare4", function(v1, options){
					if(v1 == '1'){
						return 'checked';
					}else if(v1 == '0'){
						return '';
					} else {
						return '';
					}
				});
		 		hdb.registerHelper("compare5", function(v1, options){
					if(v1 == '1'){
						return 'true';
					}else if(v1 == '0'){
						return 'false';
					} else {
						return 'false';
					}
				});
		 	},
		 	ajaxSendAfter:function (json) {

				// 加载图标信息
				systemCommonUtil.initEditIconChooseHtml('iconMation', form, colorpicker, 12, json.bean);

		 		//所属桌面
                showGrid({
                    id: "desktop",
                    url: reqBasePath + "desktop011",
                    params: {language: languageType},
                    pagination: false,
                    template: getFileContent('tpl/template/select-option-must.tpl'),
                    ajaxSendLoadBefore: function(hdb){
                    },
                    ajaxSendAfter:function(j){
                        $("#desktop").val(json.bean.desktopId);
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
                        	ajaxSendAfter:function(j){
                        		$("#menuSysWinId").val(json.bean.sysWinId);
                        		form.render('select');
                        	}
                        });
                    }
                });
		 		
		 		//菜单级别
		 		if(json.bean.menuLevel == '0'){
		 			$("#parentIdBox").addClass("layui-hide");
		 			$("input:radio[name=menuLevel][value=1]").attr("checked", true);
		 		} else {
		 			$("input:radio[name=menuLevel][value=2]").attr("checked", true);
		 			//初始化父菜单
		 			loadChildMenuAll(json.bean.parentId.split(','));
		 		}
		 		
		 		// 菜单类型
		 		$("input:radio[name=menuType][value=" + json.bean.menuType + "]").attr("checked", true);
		 		
		 		matchingLanguage();
		 		form.render();
		 		
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
		 		
		 		//父菜单变化事件
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
		 		
		 	    form.on('submit(formEditMenu)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		        			menuName: $("#menuName").val(),
		        			menuNameEn: $("#menuNameEn").val(),
							sysWinId: data.field.menuSysWinId,
							desktopId: data.field.desktop,
		        			menuUrl: $("#menuUrl").val(),
		        			menuType: data.field.menuType,
		        			rowId: parent.rowId,
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
		 	        	
		 	        	AjaxPostUtil.request({url: reqBasePath + "sys010", params: params, type: 'json', method: 'PUT', callback: function(json) {
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
		 	}
	    });
	    
	    //初始化当前子菜单的父菜单
	    function loadChildMenuAll(pid){
	    	if(pid.length > 0){
	    		if(!isNull(pid[0])){
		    		AjaxPostUtil.request({url: reqBasePath + "sys009", params:{parentId: parentId}, type: 'json', callback: function (json) {
		    			if (json.returnCode == 0) {
		    				var str = '<dd><select class="menuParent" lay-filter="selectParent" lay-search=""><option value="">请选择</option>';
		    				for(var i = 0; i < json.rows.length; i++){
		    					if(json.rows[i].id != parent.rowId){
			    					if(json.rows[i].id != pid[0]){
			    						str += '<option value="' + json.rows[i].id + '">' + json.rows[i].desktopName + '---------' + json.rows[i].menuName + '---------' + getMenuLevelName(json.rows[i].menuLevel) + '</option>';
			    					} else {
			    						str += '<option value="' + json.rows[i].id + '" selected>' + json.rows[i].desktopName + '---------' + json.rows[i].menuName + '---------' + getMenuLevelName(json.rows[i].menuLevel) + '</option>';
			    					}
		    					}
		    				}
		    				str += '</select></dd>';
		    				$("#lockParentSel").append(str);
		    				form.render('select');
	    					parentId = pid[0];
		    				pid.splice(0, 1);
		    				loadChildMenuAll(pid);
		    			} else {
		    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		    			}
		    		}});
	    		} else {
	    			pid.splice(0, 1);
    				loadChildMenuAll(pid);
	    		}
	    	} else {
	    		loadChildMenu()
	    	}
	    }
	    
 	    //加载同级菜单
 	    function loadChildMenu(){
 	    	AjaxPostUtil.request({url: reqBasePath + "sys009", params:{parentId: parentId}, type: 'json', callback: function (json) {
 	   			if (json.returnCode == 0) {
 	   				var str = '<dd><select class="menuParent" lay-filter="selectParent" lay-search=""><option value="">请选择</option>';
	 	   			for(var i = 0; i < json.rows.length; i++){
	 	   				if(json.rows[i].id != parent.rowId){
	 	   					str += '<option value="' + json.rows[i].id + '">' + json.rows[i].desktopName + '---------' + json.rows[i].menuName + '---------' + getMenuLevelName(json.rows[i].menuLevel) + '</option>';
	 	   				}
	 	   			}
	 	   			str += '</select></dd>';
	 	   			$("#lockParentSel").append(str);
	 	   			form.render('select');
 	   			} else {
 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	   			}
 	   		}});
 	    }
 	    
 	    // 菜单图标选中事件
 	    $("body").on("focus", "#menuIcon", function (e) {
			systemCommonUtil.openSysEveIconChoosePage(function(sysIconChooseClass){
				$("#menuIcon").val(sysIconChooseClass);
				$("#iconShow").css({'color': 'white'});
				$("#iconShow").attr("class", "fa fa-fw " + $("#menuIcon").val());
			});
 	    });
 	    
 	    // 获取菜单级别
 	    function getMenuLevelName(level){
 	    	if(level == '0'){
        		return "创世菜单";
        	} else {
        		return level + "级子菜单";
        	}
 	    }
 	    
 	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});