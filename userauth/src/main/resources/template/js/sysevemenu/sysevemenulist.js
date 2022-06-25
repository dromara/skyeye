
var rowId = "";

var menuId = '';

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'fsCommon', 'fsTree'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		fsTree = layui.fsTree,
		fsCommon = layui.fsCommon,
		table = layui.table;
	var parentId = "";
	
	authBtn('1552958167410');
	
	showGrid({
	 	id: "menuLevel",
	 	url: reqBasePath + "sys021",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/template/select-option.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	ajaxSendAfter:function(json){
	 		form.render('select');
	 	}
	});
	
	// 所属桌面
    showGrid({
         id: "desktop",
         url: reqBasePath + "desktop011",
         params: {},
         pagination: false,
         template: getFileContent('tpl/template/select-option.tpl'),
         ajaxSendLoadBefore: function(hdb){
         },
         ajaxSendAfter:function(json){
             form.render('select');
         }
    });
	
	function initLoadTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'sys006',
		    where: getTableParams(),
		    even:true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'menuName', title: '菜单名称', width: 120, templet: function(d){
		        	return '<a lay-event="details" class="notice-title-click">' + d.menuName + '</a>';
		        }},
		        { field: 'menuNameEn', title: '英文名称', width: 150},
		        { field: 'id', title: '图标', align: 'center', width: 60, templet: function(d){
		        	var str = '';
		        	if(d.menuIconType == '1'){
			        	if(isNull(d.menuIconBg)){
			        		str += '<div class="winui-icon winui-icon-font" style="text-align: center;">';
			        	}else{
			        		str += '<div class="winui-icon winui-icon-font" style="text-align: center; background-color:' + d.menuIconBg + '">';
			        	}
			        	if(isNull(d.menuIconColor)){
			        		str += '<i class="fa fa-fw ' + d.menuIcon + '" style="color: white"></i>';
			        	}else{
			        		str += '<i class="fa fa-fw ' + d.menuIcon + '" style="color: ' + d.menuIconColor + '"></i>';
			        	}
			        	str += '</div>';
		        	}else if(d.menuIconType = '2'){
		        		str = '<img src="' + fileBasePath + d.menuIconPic + '" class="photo-img" lay-event="menuIconPic">';
		        	}
		        	return str;
		        }},
		        { field: 'menuLevel', title: '菜单级别', width: 140, templet: function(d){
		        	if(d.parentId == '0'){
		        		return "创世菜单";
		        	}else{
		        		return "子菜单-->" + d.menuLevel + "级子菜单";
		        	}
		        }},
		        { field: 'desktopName', title: '所属桌面', width: 140},
		        { field: 'isShare', title: '共享', align: 'center', width: 80, templet: function(d){
		        	if(d.isShare == 0){
		        		return '否';
		        	}else if(d.isShare == 1){
		        		return '是';
		        	}else{
		        		return '参数错误';
		        	}
		        }},
		        { field: 'menuParentName', title: '父菜单', width: 100 },
		        { field: 'menuUrl', title: '菜单链接', width: 160 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 300, toolbar: '#tableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'del') { //删除
	        	del(data, obj);
	        }else if (layEvent === 'edit') { //编辑
	        	edit(data);
	        }else if (layEvent === 'top') { //上移
	        	topOne(data);
	        }else if (layEvent === 'lower') { //下移
	        	lowerOne(data);
	        }else if (layEvent === 'authpoint') { //权限点
	        	authpoint(data);
	        }else if(layEvent === 'menuIconPic'){ //图片
	        	layer.open({
	        		type:1,
	        		title:false,
	        		closeBtn:0,
	        		skin: 'demo-class',
	        		shadeClose:true,
	        		content:'<img src="' + fileBasePath + data.menuIconPic + '" style="max-height:600px;max-width:100%;">',
	        		scrollbar:false
	            });
	        }else if (layEvent === 'details') { //详情
                details(data);
            }
	    });
		
	}
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
        }
        return false;
	});
	
	/********* tree 处理   start *************/
	var trees = {};
	var treeDoms = $("ul.fsTree");
	if(treeDoms.length > 0) {
		$(treeDoms).each(function(i) {
			var treeId = $(this).attr("id");
			var funcNo = $(this).attr("funcNo");
			var url = $(this).attr("url");
			var tree = fsTree.render({
				id: treeId,
				funcNo: funcNo,
				url: reqBasePath + url,
				clickCallback: onClickTree,
				onDblClick: onClickTree,
				getTree: getTree
			}, function(id){
				initLoadTable();
			});
			if(treeDoms.length == 1) {
				trees[treeId] = tree;
			} else {
				//深度拷贝对象
				trees[treeId] = $.extend(true, {}, tree);
			}
		});
		//绑定按钮事件
		fsCommon.buttonEvent("tree", getTree);
	}
	
	function getTree(treeId) {
		if($.isEmpty(trees)) {
			fsCommon.warnMsg("未配置tree！");
			return;
		}
		if($.isEmpty(treeId)) {
			treeId = "treeDemo";
		}
		return trees[treeId];
	}
	
	//异步加载的方法
	function onClickTree(event, treeId, treeNode) {
		if(treeNode == undefined) {
			parentId = "";
		} else {
			parentId = treeNode.id;
		}
		loadTable();
	}
	/********* tree 处理   end *************/
	
	// 刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    // 删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "sys011", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	// 详情
    function details(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/sysevemenu/sysevemenudetails.html", 
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "sysevemenudetails",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }});
    }
	
	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysevemenu/sysevemenuedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "sysevemenuedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
	
	// 上移
	function topOne(data){
		AjaxPostUtil.request({url:reqBasePath + "sys022", params:{rowId: data.id}, type: 'json', callback: function(json){
			if (json.returnCode == 0) {
				winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	// 下移
	function lowerOne(data){
		AjaxPostUtil.request({url:reqBasePath + "sys023", params:{rowId: data.id}, type: 'json', callback: function(json){
			if (json.returnCode == 0) {
				winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
    
    // 新增菜单
    $("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/sysevemenu/sysevemenuadd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "sysevemenuadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    });
    
    // 权限点
    function authpoint(data){
		menuId = data.id;
		_openNewWindows({
			url: "../../tpl/sysevemenuauthpoint/sysevemenuauthpointlist.html", 
			title: systemLanguage["com.skyeye.authorityPointPage"][languageType],
			pageId: "sysevemenuauthpoint",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	loadTable()
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		menuName:$("#menuName").val(),
    		menuUrl:$("#menuUrl").val(),
    		parentId:parentId,
    		menuLevel:$("#menuLevel").val(),
    		desktopId:$("#desktop").val(),
    		isShare:$("#isShare").val(),
    		parentMenuName:$("#parentMenuName").val()
    	};
    }
    
    exports('sysevemenulist', {});
});
