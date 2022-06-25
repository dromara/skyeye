
var clickId = "";//选中的用户组id
var name = ""; //用户组名

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'tagEditor', 'contextMenu'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	authBtn('1563451355229');//新增用户
	authBtn('1563451312404');//新增用户组
	authBtn('1563451417564');//一键移除用户
	authBtn('1572334408610');//同步人员到工作流
	
	showLeft();
	
	//初始化左侧菜单用户组数据
	function showLeft(){
	    AjaxPostUtil.request({url: flowableBasePath + "actgroup002", params: {}, type: 'json', method: "GET", callback: function(json){
			if (json.returnCode == 0) {
				var str = getDataUseHandlebars($('#userGroupTemplate').html(), json);
				$("#setting").html(str);
				if(json.rows.length > 0){
					clickId = json.rows[0].id;
					$("#setting").find("a[rowid='" + clickId + "']").addClass('selected');
				}
	 			showList();//展示用户组对应的用户列表
		 	    initRightMenu();//初始化右键
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	//初始化右键
	function initRightMenu(){
		var arrayObj = new Array();//创建一个数组
		if(auth('1563451384406')){
			arrayObj.push({
				text: "删除",
				img: "../../assets/images/delete-icon.png",
				callback: function() {
					deleteUserGroup();
				}
			})
		}
		if(auth('1563451371446')){
			arrayObj.push({
				text: "重命名",
				img: "../../assets/images/rename-icon.png",
				callback: function() {
					var obj = $("#setting");
					var html = obj.find("a[rowid='" + clickId + "']").html();
					var newhtml = "<input value='" + html + "' class='layui-input setting-a-input' style='margin-top: 5px;'/>";
					obj.find("a[rowid='" + clickId + "']").html(newhtml);
				    obj.find("input").select();
			    	obj.find("input").blur(function(){
			    		var value = obj.find("input").val();
			    		if(!isNull(value)){
			    			if(html != value){
				    			AjaxPostUtil.request({url: flowableBasePath + "actgroup004", params: {rowId: clickId, groupName: value}, type: 'json', method: "POST", callback: function(json){
				    	   			if (json.returnCode == 0) {
				    	   				obj.find("a[rowid='" + clickId + "']").html(value);
				    	   			} else {
				    					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				    				}
				    	   		}});
			    			} else {
			    				obj.find("a[rowid='" + clickId + "']").html(html);
			    			}
			    		} else {
							obj.find("a[rowid='" + clickId + "']").html(html);
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
 
	//删除用户组
	function deleteUserGroup(){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
	        AjaxPostUtil.request({url: flowableBasePath + "actgroup005", params: {rowId: clickId}, type: 'json', method: "POST", callback: function(json){
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
	};
	
	//用户组点击事件
	$("body").on("click", ".setting-a-input", function(e){
		e.stopPropagation();//阻止冒泡
	});
	$("body").on("contextmenu", ".setting-a-input", function(e){
		e.stopPropagation();//阻止冒泡
	});
	
	//用户组名右键效果
	$("body").on("contextmenu", "#setting a", function(e){
		clickId = $(this).attr("rowid");
		name = $(this).attr("rowname");
		$("#setting").find("a").removeClass("selected");
		$("#setting").find("a[rowid='" + clickId + "']").addClass("selected");
		clickId = $(this).attr("rowid");
		loadTable();
	});
	
	//新增用户组
	$("body").on("click", "#addBean", function(e){
		var obj = $("#setting");
		var newhtml = "<input value='新增用户组' class='layui-input setting-a-input' style='margin-top: 5px;'/>";
	    obj.append(newhtml);
	    obj.find("input").select();
    	obj.find("input").blur(function(){
    		var value = obj.find("input").val();
    		obj.find("input").remove();
    		if(isNull(value)){
    			value = '新增用户组';
    		}
			AjaxPostUtil.request({url: flowableBasePath + "actgroup001", params: {groupName: value}, type: 'json', method: "POST", callback: function(json){
	   			if (json.returnCode == 0) {
	   				clickId = json.bean.id;
	   				var str = '<a rowid="' + clickId + '" rowname="' + value + '" class="setting-a">' + value + '</a>';
	   				$("#setting").append(str);
	   				$("#setting").find("a[rowid='" + clickId + "']").click();
	   			} else {
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
	
	//展示用户组对应的用户列表
	function showList(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'actgroup007',
		    where: getTableParams(),
		    even: true,
		    page: true,
			limits: getLimits(),
			limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'userName', title: '员工姓名', align: 'left', width: 120},
		        { field: 'companyName', title: '公司', align: 'left', width: 200 },
		        { field: 'departmentName', title: '部门', align: 'left', width: 150 },
		        { field: 'jobName', title: '职位', align: 'left', width: 200 },
		        { field: 'email', title: '邮箱', align: 'left', width: 200 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 80, toolbar: '#tableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if(layEvent === 'del'){  //移除
	        	del(data);
	        }
	    });
    }
	
	// 新增用户
	$("body").on("click", "#addUser", function(e){
		systemCommonUtil.userReturnList = [];
		systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
		systemCommonUtil.chooseOrNotEmail = "1"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
		systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
		systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
			var userInfo = "";
			$.each(userReturnList, function (i, item) {
				userInfo += item.id + ',';
			})
			AjaxPostUtil.request({url: flowableBasePath + "actgroup003", params: {rowId: clickId, userId: userInfo}, type: 'json', callback: function(json) {
				if (json.returnCode == 0) {
					loadTable();
				} else {
					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				}
			}});
		});
	});
	
	//一键移除指定用户组下的所有用户
	$("body").on("click", "#delUser", function(e){
		if(!isNull(clickId)){
			var msg = '确认一键移除该用户组下的所有用户吗？';
			layer.confirm(msg, { icon: 3, title: '一键移除所有用户' }, function (index) {
				layer.close(index);
				AjaxPostUtil.request({url: flowableBasePath + "actgroup008", params: {rowId: clickId}, type: 'json', method: "POST", callback: function(json){
					if (json.returnCode == 0) {
						winui.window.msg("移除成功", {icon: 1, time: 2000});
	    				loadTable();
					} else {
						winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
					}
				}});
			});
		} else {
			winui.window.msg("没有可移除用户的用户组！", {icon: 2, time: 2000});
		}
	});
	
	//移除用户
	function del(data){
		var msg = '确认移除该用户吗？';
		layer.confirm(msg, { icon: 3, title: '删除用户' }, function (index) {
			layer.close(index);
			var params = {
				rowId:  data.id
        	};
            AjaxPostUtil.request({url: flowableBasePath + "actgroup006", params: params, type: 'json', method: "POST", callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	// 同步人员数据
    $("body").on("click", "#syncData", function(){
    	AjaxPostUtil.request({url: flowableBasePath + "activitimode015", params: {}, type: 'json', method: "POST", callback: function(json){
			if (json.returnCode == 0) {
            	winui.window.msg("同步成功", {icon: 1, time: 2000});
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
    });
	
	form.render();
	//搜索用户
	$("body").on("click", "#formSearch", function(){
		refreshTable();
	});
	//搜索条件
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

	function getTableParams() {
		return {
			groupId: clickId,
			userName: $("#userName").val()
		};
	}

  	//刷新用户
	$("body").on("click", "#reloadTable", function(){
		loadTable();
	});
	//刷新用户组
	$("body").on("click", "#flashGroup", function(){
		showLeft();
	});

	exports('actGroupList', {});
});
