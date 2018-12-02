
var rowId = "";
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	table = layui.table;
	//表格渲染
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'sys001',
	    where:{userCode:$("#userCode").val(), userName:$("#userName").val()},
	    even:true,  //隔行变色
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: '序号', type: 'numbers'},
	        { field: 'userCode', title: '账号', width: 120 },
	        { field: 'pwdNumEnc', title: '加密次数', width: 120 },
	        { field: 'userName', title: '用户名', width: 120 },
	        { field: 'userPhoto', title: '头像', align: 'center', width: 120, templet: function(d){
	        	if(isNull(d.userPhoto)){
	        		return '<img src="../../assets/images/os_windows.png" class="photo-img">';
	        	}else{
	        		return '<img src="' + fileBasePath + d.userPhoto + '" class="photo-img">';
	        	}
	        }},
	        { field: 'userIdCard', title: '身份证', width: 160 },
	        { field: 'sexName', title: '性别', width: 60, templet: function(d){
	        	if(d.sexName == '0'){
	        		return "保密";
	        	}else if(d.sexName == '1'){
	        		return "男";
	        	}else if(d.sexName == '2'){
	        		return "女";
	        	}else{
	        		return "参数错误";
	        	}
	        }},
	        { field: 'userLock', title: '是否锁定', width: 120, templet: '#checkboxTpl', unresize: true},
	        { field: 'roleName', title: '角色'},
	        { field: 'createName', title: '创建人', width: 120 },
	        { field: 'createTime', title: '创建时间', width: 180 },
	        { title: '操作', fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
	    ]]
	});
	
	table.on('tool(messageTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值
        if (layEvent === 'unlock') { //解锁
        	unlock(data);
        }else if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'bindRole') { //绑定角色
        	bindRole(data);
        }
    });
	
	//搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
    	//表单验证
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});
	
	// 监听锁定操作
	form.on('checkbox(lockDemo)', function(obj) {
		if(obj.elem.checked){//锁定
			lock(obj.value);
		}else{//解锁
			unlock(obj.value);
		}
	});
	
	//锁定
	function lock(id){
		AjaxPostUtil.request({url:reqBasePath + "sys002", params:{rowId: id}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				top.winui.window.msg("已成功锁定，该账号目前无法登录。", {icon: 1,time: 2000});
			}else{
				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
	//解锁
	function unlock(id){
		AjaxPostUtil.request({url:reqBasePath + "sys003", params:{rowId: id}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				top.winui.window.msg("账号恢复正常。", {icon: 1,time: 2000});
			}else{
				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/syseveuser/syseveuseredit.html", 
			title: "编辑用户",
			pageId: "syseveuseredit",
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
	}
	
	//绑定角色
	function bindRole(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/syseveuser/syseveuserrolebind.html", 
			title: "绑定角色",
			pageId: "syseveuserrolebind",
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
	}
	
	//添加用户
	$("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/syseveuser/syseveuseradd.html", 
			title: "新增用户",
			pageId: "syseveroleadd",
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
    });
	
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{userCode:$("#userCode").val(), userName:$("#userName").val()}});
    }
    
    exports('syseveuserlist', {});
});
