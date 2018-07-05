layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$;
	    var parentId = "0";
	    
	    //初始化数据
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sys008",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/sysevemenu/sysevemenueditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		//是否为系统菜单
		 		hdb.registerHelper("compare2", function(v1, options){
					if(v1 == '1'){
						return 'checked';
					}else if(v1 == '2'){
						return '';
					}else{
						return '';
					}
				});
		 		hdb.registerHelper("compare3", function(v1, options){
					if(v1 == '1'){
						return 'true';
					}else if(v1 == '2'){
						return 'false';
					}else{
						return 'false';
					}
				});
		 	},
		 	ajaxSendAfter:function(json){
		 		//菜单级别
		 		if(json.bean.menuLevel == '0'){
		 			$("#parentIdBox").addClass("layui-hide");
		 			$("input:radio[name=menuLevel][value=1]").attr("checked", true);
		 		}else{
		 			$("input:radio[name=menuLevel][value=2]").attr("checked", true);
		 			//初始化父菜单
		 			loadChildMenuAll(json.bean.parentId.split(','));
		 		}
		 		//菜单类型
		 		$("input:radio[name=menuType][value=" + json.bean.menuType + "]").attr("checked", true);
		 		
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
			    	}else{
			    		top.winui.window.msg('状态值错误', {icon: 2,time: 2000});
			    	}
		        });
		 		
		 		//系统菜单同步
		 		form.on('switch(isNecessary)', function (data) {
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
							}else{
								parentId = "0";
							}
						}else{
							layui.$(data.elem).parent('dd').nextAll().remove();
							parentId = data.value;
							loadChildMenu();
						}
					}
				});
		 		
		 	    form.on('submit(formEditMenu)', function (data) {
		 	    	//表单验证
		 	        if (winui.verifyForm(data.elem)) {
		 	        	
		 	        	var params = {
		        			menuName: $("#menuName").val(),
		        			titleName: $("#menuTitle").val(),
		        			menuIcon: $("#menuIcon").val(),
		        			menuUrl: $("#menuUrl").val(),
		        			menuType: data.field.menuType,
		        			rowId: parent.rowId
		 	        	};
		 	        	
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
		 	 	    			top.winui.window.msg("请至少选择一级父菜单", {icon: 2,time: 2000});
		 	 	    			return false;
		 	 	    		}else{
		 	 	    			params.parentId = str;
		 	 	    		}
		 	 	    	}else{
		 	 	    		top.winui.window.msg('状态值错误', {icon: 2,time: 2000});
		 	 	    	}
		 	        	if($("#menuSysType").val() == 'true'){
		 	        		params.menuSysType = '1';
		 	        	}else{
		 	        		params.menuSysType = '2';
		 	        	}
		 	        	AjaxPostUtil.request({url:reqBasePath + "sys010", params:params, type:'json', callback:function(json){
			 	   			if(json.returnCode == 0){
				 	   			parent.layer.close(index);
				 	        	parent.refreshCode = '0';
			 	   			}else{
			 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
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
		    		AjaxPostUtil.request({url:reqBasePath + "sys009", params:{parentId: parentId}, type:'json', callback:function(json){
		    			if(json.returnCode == 0){
		    				var str = '<dd><select class="menuParent" lay-filter="selectParent" lay-search=""><option value="">请选择</option>';
		    				for(var i = 0; i < json.rows.length; i++){
		    					if(json.rows[i].id != parent.rowId){
			    					if(json.rows[i].id != pid[0]){
			    						str += '<option value="' + json.rows[i].id + '">' + json.rows[i].menuName + '---------' + getMenuLevelName(json.rows[i].menuLevel) + '</option>';
			    					}else{
			    						str += '<option value="' + json.rows[i].id + '" selected>' + json.rows[i].menuName + '---------' + getMenuLevelName(json.rows[i].menuLevel) + '</option>';
			    					}
		    					}
		    				}
		    				str += '</select></dd>';
		    				$("#lockParentSel").append(str);
		    				form.render('select');
	    					parentId = pid[0];
		    				pid.splice(0, 1);
		    				loadChildMenuAll(pid);
		    			}else{
		    				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		    			}
		    		}});
	    		}else{
	    			pid.splice(0, 1);
    				loadChildMenuAll(pid);
	    		}
	    	}else{
	    		loadChildMenu()
	    	}
	    }
	    
 	    //加载同级菜单
 	    function loadChildMenu(){
 	    	AjaxPostUtil.request({url:reqBasePath + "sys009", params:{parentId: parentId}, type:'json', callback:function(json){
 	   			if(json.returnCode == 0){
 	   				var str = '<dd><select class="menuParent" lay-filter="selectParent" lay-search=""><option value="">请选择</option>';
	 	   			for(var i = 0; i < json.rows.length; i++){
	 	   				if(json.rows[i].id != parent.rowId){
	 	   					str += '<option value="' + json.rows[i].id + '">' + json.rows[i].menuName + '---------' + getMenuLevelName(json.rows[i].menuLevel) + '</option>';
	 	   				}
	 	   			}
	 	   			str += '</select></dd>';
	 	   			$("#lockParentSel").append(str);
	 	   			form.render('select');
 	   			}else{
 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
 	   			}
 	   		}});
 	    }
 	    
 	    //获取菜单级别
 	    function getMenuLevelName(level){
 	    	if(level == '0'){
        		return "创世菜单";
        	}else{
        		return level + "级子菜单";
        	}
 	    }
 	    
 	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
});