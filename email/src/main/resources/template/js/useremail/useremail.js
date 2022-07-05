
// 邮件id
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'flow'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		flow = layui.flow;
	// 收件箱模板
	var inboxEmailMobel = $("#inboxEmailMobel").html();
	// 邮件内容模板
	var emailContentMobel = $("#emailContentMobel").html();
	
	AjaxPostUtil.request({url: reqBasePath + "useremail001", params:{}, type: 'json', callback: function (json) {
		if(json.rows.length === 0){
			$("#emailOperator").hide();//隐藏邮箱模块
			$("#firstAddEmail").show();//显示首次输入邮箱模块
		} else {
			$("#emailOperator").show();//显示邮箱模块
			$("#firstAddEmail").hide();//隐藏首次输入邮箱模块
			$.each(json.rows, function(i, item){
				addEmail(item.id, item.emailAddress);
				if(item.emailCheck == 1 || item.emailCheck === '1'){
					resetCheckEmail(item.id, item.emailAddress);
				}
			});
			$("#inboxEmail").show();//显示收件箱模块
			inboxEmailAselFun();//加载我的收件箱
		}
		matchingLanguage();
	}});
	
	form.render();
	// 添加邮箱
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	AjaxPostUtil.request({url: reqBasePath + "useremail002", params:{emailAddress: $("#emailAddress").val(), emailPassword: $("#emailPassword").val()}, type: 'json', callback: function (json) {
				winui.window.msg('新增成功', {icon: 1, time: 2000});
				$("#firstAddEmail").fadeOut(1000);
				$("#emailOperator").show();
				addEmail(json.bean.id, json.bean.emailAddress);
				if(json.bean.emailCheck === '1' || json.bean.emailCheck == 1){
					resetCheckEmail(json.bean.id, json.bean.emailAddress);
				}
	   		}});
        }
        return false;
    });
	
    //收件箱是否加载
    //草稿箱是否加载
    //已发送邮件是否加载
    //已删除邮件是否加载
    var inboxEmailAselLoad = false,
    	draftsEmailAselLoad = false,
    	sendedEmailAselLoad = false,
    	deleteedEmailAselLoad = false;
    
	$("body").on("click", ".email-setting a", function (e) {
		$(".email-setting a").removeClass("selected");
		$(this).addClass("selected");
		$("#email-choose-title").html($(this).html());
		var labelId = $(this).attr("id");
		$("#showForm").find(".app-list").hide();
		if(labelId === 'inboxEmailAsel'){//收件箱
			$("#inboxEmail").show();
			if(!inboxEmailAselLoad){//初始化加载数据
				inboxEmailAselFun();
			}
		}else if(labelId === 'draftsEmailAsel'){//草稿箱
			$("#draftsEmail").show();
			if(!draftsEmailAselLoad){//初始化加载数据
				draftsEmailAselFun();
			}
		}else if(labelId === 'sendedEmailAsel'){//已发送邮件
			$("#sendedEmail").show();
			if(!sendedEmailAselLoad){//初始化加载数据
				sendedEmailAselFun();
			}
		}else if(labelId === 'deleteedEmailAsel'){//已删除邮件
			$("#deleteedEmail").show();
			if(!deleteedEmailAselLoad){//初始化加载数据
				deleteedEmailAselFun();
			}
		}
	});
	
	/**
	 * 加载收件箱
	 */
	function inboxEmailAselFun(){
		inboxEmailAselLoad = true;
		flow.load({
			elem: '#inboxEmail', //指定列表容器
			scrollElem: '#inboxEmail',
			isAuto: true,
			done: function(page, next) { //到达临界点（默认滚动触发），触发下一页
				var lis = [];
				//以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
				AjaxPostUtil.request({url: reqBasePath + "useremail004", params:{page: page, limit: 12, emailId: $("#checkEmail").attr('rowid')}, type: 'json', callback: function (json) {
					var jsonStr = "";//实体json对象
					$.each(json.rows, function(index, item) {
						jsonStr = {
							bean: item
						};
						lis.push(getDataUseHandlebars(inboxEmailMobel, jsonStr));
					});
					//执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
					//pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
					next(lis.join(''), (page * 12) < json.total);
		   		}});
			}
		});
	}
	
	/**
	 * 加载草稿箱
	 */
	function draftsEmailAselFun(){
		draftsEmailAselLoad = true;
		flow.load({
			elem: '#draftsEmail', //指定列表容器
			scrollElem: '#draftsEmail',
			isAuto: true,
			done: function(page, next) { //到达临界点（默认滚动触发），触发下一页
				var lis = [];
				//以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
				AjaxPostUtil.request({url: reqBasePath + "useremail011", params:{page: page, limit: 12, emailId: $("#checkEmail").attr('rowid')}, type: 'json', callback: function (json) {
					var jsonStr = "";//实体json对象
					$.each(json.rows, function(index, item) {
						jsonStr = {
							bean: item
						};
						lis.push(getDataUseHandlebars(inboxEmailMobel, jsonStr));
					});
					//执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
					//pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
					next(lis.join(''), (page * 12) < json.total);
		   		}});
			}
		});
	}
	
	/**
	 * 加载已发送邮件
	 */
	function sendedEmailAselFun(){
		sendedEmailAselLoad = true;
		flow.load({
			elem: '#sendedEmail', //指定列表容器
			scrollElem: '#sendedEmail',
			isAuto: true,
			done: function(page, next) { //到达临界点（默认滚动触发），触发下一页
				var lis = [];
				//以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
				AjaxPostUtil.request({url: reqBasePath + "useremail007", params:{page: page, limit: 12, emailId: $("#checkEmail").attr('rowid')}, type: 'json', callback: function (json) {
					var jsonStr = "";//实体json对象
					$.each(json.rows, function(index, item) {
						jsonStr = {
							bean: item
						};
						lis.push(getDataUseHandlebars(inboxEmailMobel, jsonStr));
					});
					//执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
					//pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
					next(lis.join(''), (page * 12) < json.total);
		   		}});
			}
		});
	}
	
	/**
	 * 已删除邮件
	 */
	function deleteedEmailAselFun(){
		deleteedEmailAselLoad = true;
		flow.load({
			elem: '#deleteedEmail', //指定列表容器
			scrollElem: '#deleteedEmail',
			isAuto: true,
			done: function(page, next) { //到达临界点（默认滚动触发），触发下一页
				var lis = [];
				//以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
				AjaxPostUtil.request({url: reqBasePath + "useremail009", params:{page: page, limit: 12, emailId: $("#checkEmail").attr('rowid')}, type: 'json', callback: function (json) {
					var jsonStr = "";//实体json对象
					$.each(json.rows, function(index, item) {
						jsonStr = {
							bean: item
						};
						lis.push(getDataUseHandlebars(inboxEmailMobel, jsonStr));
					});
					//执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
					//pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
					next(lis.join(''), (page * 12) < json.total);
		   		}});
			}
		});
	}
	
	//收件箱列表点击
	$("body").on("click", "#inboxEmail li, #sendedEmail li, #deleteedEmail li, #draftsEmail li", function (e) {
		$("#inboxEmail li").removeClass("selected");
		$("#sendedEmail li").removeClass("selected");
		$("#deleteedEmail li").removeClass("selected");
		$("#draftsEmail li").removeClass("selected");
		$(this).addClass("selected");
		rowId = $(this).attr("rowid");
		//根据当前选择的选项进行加载内容页面
		var _selId = $("#emailSetting").find("a[class='selected']").attr("id");
		if(_selId === 'inboxEmailAsel'){//收件箱
			$("#emailContentIframe").attr("src", "../../tpl/useremail/emailshow.html");
		}else if(_selId === 'draftsEmailAsel'){//草稿箱
			$("#emailContentIframe").attr("src", "../../tpl/useremail/sendemail.html?id=" + rowId);
		}else if(_selId === 'sendedEmailAsel'){//已发送邮件
			$("#emailContentIframe").attr("src", "../../tpl/useremail/emailshow.html");
		}else if(_selId === 'deleteedEmailAsel'){//已删除邮件
			$("#emailContentIframe").attr("src", "../../tpl/useremail/emailshow.html");
		} else {
			winui.window.msg('参数错误', {icon: 2, time: 2000});
		}
	});
	
	//收取
	$("body").on("click", "#collectEmail", function (e) {
		var _selId = $("#emailSetting").find("a[class='selected']").attr("id");
		if(_selId === 'inboxEmailAsel'){//收件箱
			AjaxPostUtil.request({url: reqBasePath + "useremail003", params:{emailId: $("#checkEmail").attr('rowid')}, type: 'json', callback: function (json) {
				winui.window.msg("获取邮件中", {icon: 1, time: 2000});
			}});
		}else if(_selId === 'draftsEmailAsel'){//草稿箱
			AjaxPostUtil.request({url: reqBasePath + "useremail010", params:{emailId: $("#checkEmail").attr('rowid')}, type: 'json', callback: function (json) {
				winui.window.msg("获取邮件中", {icon: 1, time: 2000});
			}});
		}else if(_selId === 'sendedEmailAsel'){//已发送邮件
			AjaxPostUtil.request({url: reqBasePath + "useremail006", params:{emailId: $("#checkEmail").attr('rowid')}, type: 'json', callback: function (json) {
				winui.window.msg("获取邮件中", {icon: 1, time: 2000});
			}});
		}else if(_selId === 'deleteedEmailAsel'){//已删除邮件
			AjaxPostUtil.request({url: reqBasePath + "useremail008", params:{emailId: $("#checkEmail").attr('rowid')}, type: 'json', callback: function (json) {
				winui.window.msg("获取邮件中", {icon: 1, time: 2000});
			}});
		} else {
			winui.window.msg('参数错误', {icon: 2, time: 2000});
		}
	});
	
	/**
	 * 重置选中的email
	 */
	function resetCheckEmail(emailId, emailAddress){
		$("#checkEmail").attr('rowid', emailId);
		$("#checkEmail").html(emailAddress + '<span class="layui-nav-more"></span>');
	}
	
	/**
	 * 新增email
	 */
	function addEmail(emailId, emailAddress){
		$("#userEmailList").html('<dd rowid="' + emailId + '"><a href="javascript:;">' + emailAddress + '</a></dd>' + $("#userEmailList").html());
	}
	
	//刷新
	$("body").on("click", "#refreshBean", function (e) {
		var _selId = $("#emailSetting").find("a[class='selected']").attr("id");
		if(_selId === 'inboxEmailAsel'){//收件箱
			$("#inboxEmail").html("");
			inboxEmailAselFun();
		}else if(_selId === 'draftsEmailAsel'){//草稿箱
			$("#draftsEmail").html("");
			draftsEmailAselFun();
		}else if(_selId === 'sendedEmailAsel'){//已发送邮件
			$("#sendedEmail").html("");
			sendedEmailAselFun();
		}else if(_selId === 'deleteedEmailAsel'){//已删除邮件
			$("#deleteedEmail").html("");
			deleteedEmailAselFun();
		} else {
			winui.window.msg('参数错误', {icon: 2, time: 2000});
		}
	});
	
	//写邮件
	$("body").on("click", "#addBean", function (e) {
		rowId = "";
		$("#emailContentIframe").attr("src", "../../tpl/useremail/sendemail.html");
	});
	
	form.render();
	
    exports('useremail', {});
});
