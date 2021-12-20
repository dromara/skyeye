var chooseId = "";
var chooseName = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'table', 'contextMenu'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	authBtn('1563601960859');//新增目录权限
	authBtn('1563602200836');//新增菜单权限
	
	var appWorkPageTemplate = $('#appWorkPageTemplate').html();//获取菜单目录模板
	
	initRightMenu();//初始化右键
	showMenu();//加载左侧菜单目录
	
	$("body").on("click", "#reloadTable", function(){//刷新目录及菜单
		showMenu();
    });
	
	$("body").on("click", "#reloadTableData", function(){//刷新菜单
		showList();
    });
	
	$("body").on("click", "#formSearch", function(){//搜索
		table.reload("messageTable", {page: {curr: 1}, where:{chooseId: chooseId, title: $("#title").val()}});
    });
	
	$("body").on("click", ".setting a", function(e){//左侧目录列表点击事件
		$(".setting a").removeClass("selected");
		$(this).addClass("selected");
		chooseId = $(this).attr("rowid");
	    showList();
	});
	
	//新增分类
	$("body").on("click", "#addBean", function(){
		AjaxPostUtil.request({url:reqBasePath + "appworkpage002", params: {}, type:'json', callback:function(json){
   			if(json.returnCode == 0){
   				chooseId = json.bean.id;
   				showList();
   				$(".setting a").removeClass("selected");
   				$("#setting").find("a[rowid='" + chooseId + "']").remove();
   				var obj = $("#setting");
   			    obj.append("<input value='新增分类' class='layui-input setting-a-input' style='margin-top: 5px;'/>");
   			    obj.find("input").select();
   			    obj.find("input").blur(function(){
   			    	var value = obj.find("input").val();
   			    	if(value.length > 0){
   			    		AjaxPostUtil.request({url:reqBasePath + "appworkpage012", params: {rowId: chooseId, title: value}, type:'json', callback:function(json){
   		   		   			if(json.returnCode == 0){
   		   		   				showMenu();
   		   		   			}else{
   		   						winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
   		   					}
   		   		   		}});
   			    	}else{
   			    		showMenu();
   			    		winui.window.msg("标题不能为空", {icon: 2,time: 2000});
   			    	}
   			    });
   			}else{
   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
   			}
   		}});
    });
	
	//新增菜单
	$("body").on("click", "#addBeanApp", function(){
		_openNewWindows({
			url: "../../tpl/appworkpage/appworkpageadd.html", 
			title: "新增菜单",
			pageId: "appworkpageadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	$("#setting").find("a").removeClass('selected');
                	//根据新增页面选中的目录进行选中并展示列表
                	$("#setting").find("a[rowid='" + chooseId + "']").addClass('selected');
    		 	    showList();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
		}});
    });
	
	//加载目录
	function showMenu(){
		AjaxPostUtil.request({url:reqBasePath + "appworkpage001", params: {}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				if(json.total > 0){
					var str = getDataUseHandlebars(appWorkPageTemplate, json);
					$("#setting").html(str);
		 			var noteItem = "";
			 		if(!isNull(chooseId)){
			 			noteItem =$("#setting").find("a[rowid='" + chooseId + "']");
			 		}else{
			 			noteItem = $("#setting").find("a").eq(0);
			 			chooseId = noteItem.attr("rowid");
			 		}
			 		noteItem.addClass('selected');
		 		}
				showList();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
	function showList(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'appworkpage003',
		    where: {chooseId: chooseId, title: $("#title").val()},
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'title', title: '页面名称', align: 'left', width: 120 },
		        { field: 'logo', title: 'logo', width: 60, templet: function(d){
		        	var str = '';
		        	if(isNull(d.logo)){
		        	}else{
		        		str = '<img src="' + fileBasePath + d.logo + '" class="photo-img" lay-event="iconPath">';
		        	}
		        	return str;
		        }},
		        { field: 'url', title: '页面路径', align: 'left', width: 300},
		        { field: 'state', title: '状态', width: 80, align: 'center', templet: function(d){
		        	if(d.state == '1'){
		        		return "<span class='state-new'>新建</span>";
		        	}else if(d.state == '2'){
		        		return "<span class='state-up'>上线</span>";
		        	}else if(d.state == '3'){
		        		return "<span class='state-down'>下线</span>";
		        	}else{
		        		return "参数错误";
		        	}
		        }},
		        { field: 'createId', title: '创建人', align: 'center', width: 100},
		        { field: 'createTime', title: '创建时间', align: 'center', width: 140},
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 320, toolbar: '#tableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
    }
	
	table.on('tool(messageTable)', function (obj) { 
        var data = obj.data; 
        var layEvent = obj.event; 
        if (layEvent === 'delete') { //删除
        	del(data, obj);
        }else if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'top') { //上移
        	topOne(data);
        }else if (layEvent === 'lower') { //下移
        	lowerOne(data);
        }else if (layEvent === 'up') { //上线
        	up(data);
        }else if (layEvent === 'down') { //下线
        	down(data);
        }else if (layEvent === 'authpoint') { //权限点
        	authpoint(data);
        }
    });
	
	//编辑菜单
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/appworkpage/appworkpageedit.html", 
			title: "编辑菜单",
			pageId: "appworkpageedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	showList();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
	
	//删除菜单
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "appworkpage007", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
    				showList();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	//上移
	function topOne(data){
		AjaxPostUtil.request({url:reqBasePath + "appworkpage008", params:{rowId: data.id, parentId: data.parentId}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
				showList();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
	//下移
	function lowerOne(data){
		AjaxPostUtil.request({url:reqBasePath + "appworkpage009", params:{rowId: data.id, parentId: data.parentId}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
				showList();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
	//上线
	function up(data, obj){
		var msg = obj ? '确认将【' + obj.data.title + '】上线吗？' : '确认将选中数据上线吗？';
		layer.confirm(msg, { icon: 3, title: '上线操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "appworkpage010", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("上线成功", {icon: 1,time: 2000});
    				showList();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	//下线
	function down(data, obj){
		var msg = obj ? '确认将【' + obj.data.title + '】下线吗？' : '确认将选中数据下线吗？';
		layer.confirm(msg, { icon: 3, title: '下线操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "appworkpage011", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("下线成功", {icon: 1,time: 2000});
    				showList();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	//权限点
    function authpoint(data){
		menuId = data.id;
		_openNewWindows({
			url: "../../tpl/appworkpageauthpoint/appworkpageauthpointlist.html", 
			title: "权限点",
			pageId: "appworkpageauthpoint",
			maxmin: true,
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
    
	//右键
	$("body").on("contextmenu", "#setting a", function(e){
		chooseId = $(this).attr("rowid");
		chooseName = $(this).attr("rowname");
		$("#setting").find("a").removeClass("selected");
		$("#setting").find("a[rowid='" + chooseId + "']").addClass("selected");
		showList();
	});
	
	//初始化右键
	function initRightMenu(){
		var arrayObj = new Array();
		if(auth('1563602067772')){
			arrayObj.push({
				text: "上线",
				img: "../../assets/images/up-state.png",
				callback: function() {
					var msg = '确认将【' + chooseName + '】上线吗？';
					layer.confirm(msg, { icon: 3, title: '上线操作' }, function (index) {
						layer.close(index);
			            AjaxPostUtil.request({url:reqBasePath + "appworkpage014", params:{rowId: chooseId}, type:'json', callback:function(json){
			    			if(json.returnCode == 0){
			    				showMenu();
			    				winui.window.msg("上线成功", {icon: 1,time: 2000});
			    			}else{
			    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			    			}
			    		}});
					});
				}
			})
		}
		if(auth('1563602096558')){
			arrayObj.push({
				text: "下线",
				img: "../../assets/images/down-state.png",
				callback: function() {
					var msg = '确认将【' + chooseName + '】下线吗？';
					layer.confirm(msg, { icon: 3, title: '下线操作' }, function (index) {
						layer.close(index);
			            AjaxPostUtil.request({url:reqBasePath + "appworkpage015", params:{rowId: chooseId}, type:'json', callback:function(json){
			    			if(json.returnCode == 0){
			    				showMenu();
			    				winui.window.msg("下线成功", {icon: 1,time: 2000});
			    			}else{
			    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			    			}
			    		}});
					});
				}
			})
		}
		if(auth('1563602118670')){
			arrayObj.push({
				text: "上移",
				img: "../../assets/images/up-move.png",
				callback: function() {
					AjaxPostUtil.request({url:reqBasePath + "appworkpage016", params:{rowId: chooseId}, type:'json', callback:function(json){
						if(json.returnCode == 0){
							showMenu();
							winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
						}else{
							winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
						}
					}});
				}
			})
		}
		if(auth('1563602137602')){
			arrayObj.push({
				text: "下移",
				img: "../../assets/images/down-move.png",
				callback: function() {
					AjaxPostUtil.request({url:reqBasePath + "appworkpage017", params:{rowId: chooseId}, type:'json', callback:function(json){
						if(json.returnCode == 0){
							showMenu();
							winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
						}else{
							winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
						}
					}});
				}
			})
		}
		if(auth('1563602040850')){
			arrayObj.push({
				text: "删除",
				img: "../../assets/images/delete-icon.png",
				callback: function() {
					layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
						layer.close(index);
				        AjaxPostUtil.request({url:reqBasePath + "appworkpage013", params:{rowId: chooseId}, type:'json', callback:function(json){
							if(json.returnCode == 0){
								winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
								chooseId = "";
								showMenu();
							}else{
								winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
							}
						}});
					});
				}
			})
		}
		if(auth('1563602006679')){
			arrayObj.push({
				text: "重命名",
				img: "../../assets/images/rename-icon.png",
				callback: function() {
	   				var obj = $("#setting");
	   				obj.find("a[rowid='" + chooseId + "']").html("<input value='"+chooseName+"' class='layui-input setting-a-input' style='margin-top: 5px;'/>");
	   			    obj.find("input").select();
	   			    obj.find("input").blur(function(){
	   			    	var value = obj.find("input").val();
	   			    	if(value.length > 0){
	   			    		if(value != chooseName){
	   			    			AjaxPostUtil.request({url:reqBasePath + "appworkpage012", params: {rowId: chooseId, title: value}, type:'json', callback:function(json){
				   		   			if(json.returnCode == 0){
				   		   				showMenu();
				   		   			}else{
				   						winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				   					}
				   		   		}});
	   			    		}else{
	   			    			showMenu();
	   			    		}
	   			    	}else{
	   			    		showMenu();
	   			    		winui.window.msg("标题不能为空", {icon: 2,time: 2000});
	   			    	}
	   			    });
				}
			})
		}
		if(arrayObj.length > 0){
			$("#setting").contextMenu({
				width: 190, // width
				itemHeight: 30, // 菜单项height
				bgColor: "#FFFFFF", // 背景颜色
				color: "#0A0A0A", // 字体颜色
				fontSize: 12, // 字体大小
				hoverBgColor: "#99CC66", // hover背景颜色
				target: function(ele) { // 当前元素
				},
				rightClass: 'setting-a,setting-a selected',
				menu: arrayObj
			});
		}
    };
	
	form.render();
    exports('appworkpage', {});
});
