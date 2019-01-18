
var childIcon = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'colorpicker'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$;
	    var colorpicker = layui.colorpicker;
	    
	    var parentId = "0";
	    
 		form.render();
 		
 		colorpicker.render({
 		    elem: '#menuIconBg',
 		    color: '#1c97f5',
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
 		    color: '#1c97f5',
 		    done: function(color){
 		        $('#menuIconColorinput').val(color);
 		        $("#iconShow").css({'color': color});
 		    },
 		    change: function(color){
 		    	$("#iconShow").css({'color': color});
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
	    	}else{
	    		top.winui.window.msg('状态值错误', {icon: 2,time: 2000});
	    	}
        });
 		
 		//系统菜单同步
 		form.on('switch(isNecessary)', function (data) {
 			//同步开关值
 			$(data.elem).val(data.elem.checked);
 		});
 		
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
 		
 	    form.on('submit(formAddMenu)', function (data) {
 	    	//表单验证
 	        if (winui.verifyForm(data.elem)) {
 	        	
 	        	var params = {
        			menuName: $("#menuName").val(),
        			titleName: $("#menuTitle").val(),
        			menuIcon: $("#menuIcon").val(),
        			menuUrl: $("#menuUrl").val(),
        			menuType: data.field.menuType,
        			menuIconBg: $('#menuIconBginput').val(),
        			menuIconColor: $('#menuIconColorinput').val(),
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
 	        	AjaxPostUtil.request({url:reqBasePath + "sys007", params:params, type:'json', callback:function(json){
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
 	    
 	    //加载同级菜单
 	    function loadChildMenu(){
 	    	AjaxPostUtil.request({url:reqBasePath + "sys009", params:{parentId: parentId}, type:'json', callback:function(json){
 	   			if(json.returnCode == 0){
 	   				var str = '<dd><select class="menuParent" lay-filter="selectParent" lay-search=""><option value="">请选择</option>';
	 	   			for(var i = 0; i < json.rows.length; i++){
	 	   				str += '<option value="' + json.rows[i].id + '">' + json.rows[i].menuName + '---------' + getMenuLevelName(json.rows[i].menuLevel) + '</option>';
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
 	    
 	    //菜单图标选中事件
 	    $("body").on("focus", "#menuIcon", function(e){
 	    	_openNewWindows({
 				url: "../../tpl/sysevemenu/icon.html", 
 				title: "选择ICON图标",
 				pageId: "icon",
 				area: ['640px', '360px'],
 				callBack: function(refreshCode){
 	                if (refreshCode == '0') {
 	                	$("#menuIcon").val(childIcon);
 	                	$("#iconShow").css({'color': 'white'});
 	                	$("#iconShow").attr("class", "fa fa-fw " + $("#menuIcon").val());
 	                } else if (refreshCode == '-9999') {
 	                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
 	                }
 				}});
 	    });
 	    
 	    //初始化加载隐藏创世菜单
 	    $("#parentIdBox").addClass("layui-hide");
	    
 	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
});