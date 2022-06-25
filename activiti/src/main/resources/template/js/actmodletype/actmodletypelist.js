var rowId = "";

var clickId = "";//选中的申请类型id
var name = ""; //申请类型名

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'contextMenu'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	authBtn('1564106558827');//新增申请类型实体
	authBtn('1564106543479');//新增申请类型
	
	showLeft();
	
	//初始化左侧菜单申请类型数据
	function showLeft(){
		var modleTypeTemplate = $('#modleTypeTemplate').html();
	    AjaxPostUtil.request({url:flowableBasePath + "actmodletype002", params: {}, type: 'json', callback: function(json){
			if (json.returnCode == 0) {
				var str = getDataUseHandlebars(modleTypeTemplate, json);
				$("#setting").html(str);
				if(json.rows.length > 0){
					clickId = json.rows[0].id;
					$("#setting").find("a[rowid='" + clickId + "']").addClass('selected');
				}
	 			showList();//展示申请类型对应的申请类型实体列表
	 			initRightMenu();//初始化右键
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	//初始化右键
	function initRightMenu(){
		var arrayObj = new Array();//创建一个数组
		if(auth('1564106707762')){
			arrayObj.push({
				text: "上线",
				img: "../../assets/images/up-state.png",
				callback: function() {
					upModleType();
				}
			})
		}
		if(auth('1564106721541')){
			arrayObj.push({
				text: "下线",
				img: "../../assets/images/down-state.png",
				callback: function() {
					downModleType();
				}
			})
		}
		if(auth('1564106736541')){
			arrayObj.push({
				text: "上移",
				img: "../../assets/images/up-move.png",
				callback: function() {
					upMoveModleType();
				}
			})
		}
		if(auth('1564106751352')){
			arrayObj.push({
				text: "下移",
				img: "../../assets/images/down-move.png",
				callback: function() {
					downMoveModleType();
				}
			})
		}
		if(auth('1564106588307')){
			arrayObj.push({
				text: "删除",
				img: "../../assets/images/delete-icon.png",
				callback: function() {
					deleteModleType();
				}
			})
		}
		if(auth('1564106574550')){
			arrayObj.push({
				text: "重命名",
				img: "../../assets/images/rename-icon.png",
				callback: function() {
					var obj = $("#setting");
					var html = obj.find("a[rowid='" + clickId + "']").find("span").html();
					var newhtml = "<input value='" + html + "' class='layui-input setting-a-input' style='margin-top: 5px;'/>";
					obj.find("a[rowid='" + clickId + "']").find("span").html(newhtml);
				    obj.find("input").select();
			    	obj.find("input").blur(function(){
			    		var value = obj.find("input").val();
			    		if(!isNull(value)){
			    			if(html != value){
				    			AjaxPostUtil.request({url:flowableBasePath + "actmodletype004", params: {rowId: clickId, title: value}, type: 'json', callback: function(json){
				    	   			if (json.returnCode == 0) {
				    	   				obj.find("a[rowid='" + clickId + "']").find("span").html(value);
				    	   			} else {
				    					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				    				}
				    	   		}});
			    			} else {
			    				obj.find("a[rowid='" + clickId + "']").find("span").html(html);
			    			}
			    		} else {
							obj.find("a[rowid='" + clickId + "']").find("span").html(html);
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
 
	//删除申请类型
	function deleteModleType(){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
	        AjaxPostUtil.request({url:flowableBasePath + "actmodletype005", params:{rowId: clickId}, type: 'json', callback: function(json){
				if (json.returnCode == 0) {
					winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
					$("#setting").find("a[rowid='" + clickId + "']").remove();
					var _obj = $("#setting").find("a[class='setting-a']");
					if(_obj.length > 0){
						_obj.eq(0).click();
					} else {
						clickId = "";
						loadTable();
					}
				} else {
					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				}
			}});
		});
	}
	
	//上线申请类型
	function upModleType(){
		var msg = '确认上线该申请类型吗？';
		layer.confirm(msg, { icon: 3, title: '上线申请类型实体' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:flowableBasePath + "actmodletype015", params:{rowId: clickId}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				$("#setting").find("a[rowid='" + clickId + "']").find("div").remove();
					$("#setting").find("a[rowid='" + clickId + "']").find("span").after('<div class="up-state-point"></div>');
    				winui.window.msg("上线成功", {icon: 1, time: 2000});
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//下线申请类型
	function downModleType(){
		var msg = '确认下线该申请类型吗？';
		layer.confirm(msg, { icon: 3, title: '下线申请类型实体' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:flowableBasePath + "actmodletype016", params:{rowId: clickId}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				$("#setting").find("a[rowid='" + clickId + "']").find("div").remove();
					$("#setting").find("a[rowid='" + clickId + "']").find("span").after('<div class="down-state-point"></div>');
    				winui.window.msg("下线成功", {icon: 1, time: 2000});
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//上移申请类型
	function upMoveModleType(){
        AjaxPostUtil.request({url:flowableBasePath + "actmodletype017", params:{rowId: clickId}, type: 'json', callback: function(json){
			if (json.returnCode == 0) {
				winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				var a = $("#setting").find("a[rowid='" + clickId + "']").prev();
				$("#setting").find(a).remove();
				$("#setting").find("a[rowid='" + clickId + "']").after(a);
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	//下移申请类型
	function downMoveModleType(){
        AjaxPostUtil.request({url:flowableBasePath + "actmodletype018", params:{rowId: clickId}, type: 'json', callback: function(json){
			if (json.returnCode == 0) {
				winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				var a = $("#setting").find("a[rowid='" + clickId + "']").next();
				$("#setting").find(a).remove();
				$("#setting").find("a[rowid='" + clickId + "']").before(a);
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	//申请类型点击事件
	$("body").on("click", ".setting-a-input", function(e){
		e.stopPropagation();//阻止冒泡
	});
	$("body").on("contextmenu", ".setting-a-input", function(e){
		e.stopPropagation();//阻止冒泡
	});
	
	//申请类型名右键效果
	$("body").on("contextmenu", "#setting a", function(e){
		clickId = $(this).attr("rowid");
		name = $(this).attr("rowname");
		$("#setting").find("a").removeClass("selected");
		$("#setting").find("a[rowid='" + clickId + "']").addClass("selected");
		loadTable();
	});
	
	//新增申请类型
	$("body").on("click", "#addBean", function(e){
		var obj = $("#setting");
		var newhtml = "<input value='新增申请类型' class='layui-input setting-a-input' style='margin-top: 5px;' maxlength='6'/>";
		if($("#setting a").size() == 0){
			obj.append(newhtml);
		} else {
			$(newhtml).insertBefore($("#setting a").eq(0));
		}
	    obj.find("input").select();
    	obj.find("input").blur(function(){
    		var value = obj.find("input").val();
    		if(isNull(value)){
    			$(this).select();
    			return;
    		}
			AjaxPostUtil.request({url:flowableBasePath + "actmodletype001", params: {title: value}, type: 'json', callback: function(json){
	   			if (json.returnCode == 0) {
	   				obj.find("input").remove();
	   				clickId = json.bean.id;
	   				var str = '<a rowid="' + clickId + '" rowname="' + value + '" class="setting-a" title="' + value + '"><span>' + value + '</span><div class="new-state-point"></div></a>';
	   				if($("#setting a").size() != 0){
	   					$(str).insertBefore($("#setting a").eq(0));
	   				} else {
	   					obj.append(str);
	   				}
	   				$("#setting").find("a[rowid='" + clickId + "']").click();
	   			} else {
					obj.find("input").select();
					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				}
	   		}});
    	});
	});
	
    //对左侧菜单项的点击事件
	$("body").on("click", "#setting a", function(e){
		$(".setting a").removeClass("selected");
		$(this).addClass("selected");
		clickId = $(this).attr("rowid");
		loadTable();
	});
	
	//展示申请类型对应的申请类型实体列表
	function showList(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'actmodletype007',
		    where: {typeId: clickId, title: $("#title").val()},
		    even: true,
		    page: true,
			limits: getLimits(),
			limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'title', title: '实体名称', align: 'left', width: 120, templet: function(d){
					return '<a lay-event="dedails" class="notice-title-click">' + d.title + '</a>';
				}},
		        { field: 'actId', title: '工作流模型', align: 'left', width: 120},
		        { field: 'pageUrl', title: '新增页面', align: 'left', width: 200 },
		        { field: 'editPageUrl', title: '编辑页面', align: 'left', width: 200 },
		        { field: 'backgroundColor', title: '背景', align: 'center', width: 80 },
		        { field: 'state', title: '状态', align: 'center', width: 100 , templet: function(d){
		        	if(d.state == '1'){
		        		return "<span class='state-new'>新建</span>";
		        	}else if(d.state == '2'){
		        		return "<span class='state-up'>上线</span>";
		        	}else if(d.state == '3'){
		        		return "<span class='state-down'>下线</span>";
		        	} else {
		        		return "参数错误";
		        	}
		        }},
		        { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#tableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
    }

	//监听操作栏事件
	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if(layEvent === 'del'){  //移除
			del(data);
		}else if (layEvent === 'edit') { //编辑
			edit(data);
		}else if (layEvent === 'up') { //上线
			up(data);
		}else if (layEvent === 'down') { //下线
			down(data);
		}else if (layEvent === 'upMove') { //上移
			upMove(data);
		}else if (layEvent === 'downMove') { //下移
			downMove(data);
		}else if (layEvent === 'dedails') { //详情
			dedails(data);
		}
	});
	
	//新增申请类型实体
	$("body").on("click", "#addModle", function(e){
		rowId = clickId;
		_openNewWindows({
			url: "../../tpl/actmodletype/actmodletypeadd.html", 
			title: "新增申请类型实体",
			pageId: "actmodletypeadd",
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
	
	//移除申请类型实体
	function del(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url:flowableBasePath + "actmodletype006", params: {rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//上线申请类型实体
	function up(data){
		var msg = '确认上线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '上线申请类型实体' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:flowableBasePath + "actmodletype009", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg("上线成功", {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//下线申请类型实体
	function down(data){
		var msg = '确认下线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '下线申请类型实体' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:flowableBasePath + "actmodletype010", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg("下线成功", {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//编辑申请类型实体
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/actmodletype/actmodleedit.html", 
			title: "编辑类型实体",
			pageId: "actmodleedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}
		});
	}

	// 详情
	function dedails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/actmodletype/actModleDetails.html",
			title: "详情",
			pageId: "actmodleedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}
		});
	}
	
	//上移申请类型实体
	function upMove(data){
        AjaxPostUtil.request({url:flowableBasePath + "actmodletype013", params:{rowId: data.id, typeId: data.typeId}, type: 'json', callback: function(json){
			if (json.returnCode == 0) {
				winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	//下移申请类型实体
	function downMove(data){
        AjaxPostUtil.request({url:flowableBasePath + "actmodletype014", params:{rowId: data.id, typeId: data.typeId}, type: 'json', callback: function(json){
			if (json.returnCode == 0) {
				winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	form.render();
	//搜索申请类型实体
	$("body").on("click", "#formSearch", function() {
		refreshTable();
	});
	//搜索条件
    function loadTable(){
    	table.reload("messageTable", {where:{typeId: clickId, title: $("#title").val()}});
    }
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where:{typeId: clickId, title: $("#title").val()}});
    }
  	//刷新申请类型实体
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});
	//刷新申请类型
	$("body").on("click", "#flashGroup", function() {
		showLeft();
	});
    exports('actmodletypelist', {});
});
