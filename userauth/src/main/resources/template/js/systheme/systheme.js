
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'fileUpload', 'upload'], function (exports) {
	winui.renderColor();
	layui.use(['layer', 'form'], function (exports) {
	    var $ = layui.jquery, 
		    form = layui.form, 
		    unfinished = '暂未实现',
		    upload = layui.upload;
	    
	    var winuiTaskbarTask;//多桌面任务栏集合
	    
	    $(function () {
	        winui.renderColor();
	        winui.tab.init();
	        
	        //初始化桌面背景图片
		    showGrid({
			 	id: "choose-content",
			 	url: reqBasePath + "sysevewinmation001",
			 	params: {},
			 	pagination: false,
			 	template: getFileContent('tpl/systheme/systheme.tpl'),
			 	ajaxSendLoadBefore: function(hdb){
			 		hdb.registerHelper("compare1", function(v1, options){
						return fileBasePath + v1;
					});
			 	},
			 	options: {},
			 	ajaxSendAfter:function(json){
			 		initCustomBackGroundPic();
			 		initCustomLockBackGroundPic();
			 		//桌面背景选择
				    $('.bgPicItem').on('click', function () {
				    	var bgSrc = $(this).attr('picUrl');
				        AjaxPostUtil.request({url: reqBasePath + "sys025", params: {winBgPicUrl: bgSrc}, type: 'json', method: "PUT", callback: function(json){
			 	   			if (json.returnCode == 0) {
				 	   			$('.background-preview').css('background-image', 'url(' + bgSrc + ')');
						        winui.resetBg(bgSrc);
			 	   			}else{
			 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			 	   			}
			 	   		}});
				    });
				    // 桌面锁屏背景选择
				    $('.lockBgPicItem').on('click', function () {
				    	var bgSrc = $(this).attr('picUrl');
				        AjaxPostUtil.request({url: reqBasePath + "sys026", params: {winLockBgPicUrl: bgSrc}, type: 'json', method: "PUT", callback: function(json){
			 	   			if (json.returnCode == 0) {
				 	   			$('.lockscreen-preview').css('background-image', 'url(' + bgSrc + ')');
						        winui.resetLockBg(bgSrc);
			 	   			}else{
			 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			 	   			}
			 	   		}});
				    });
			 		// 颜色选择
				    $('.color-choose>div').on('click', function () {
				        var color = Number($(this)[0].classList[0].replace('theme-color-', ''));
				        AjaxPostUtil.request({url: reqBasePath + "sys024", params: {themeColor: color}, type: 'json', method: "PUT", callback: function(json){
			 	   			if (json.returnCode == 0) {
			 	   				winui.resetColor(color);
			 	   			}else{
			 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			 	   			}
			 	   		}});
				    });
				    
				    //设置预览背景为当前背景
				    $('.background-preview').css('background-image', layui.jquery('body').css('background-image'));
				    //设置锁屏预览背景为当前锁屏预览背景
				    $('.lockscreen-preview').css('background-image', 'url(' + winui.getSetting('lockBgSrc') + ')');
				    //设置主题预览中任务栏位置
				    var taskbarMode = winui.getSetting('taskbarMode');
				    $('.taskbar-position input[value=' + taskbarMode + ']').prop('checked', true);
				    //设置当前菜单栏显示样式
				    var loadBottomMenuIcon = parent.loadBottomMenuIcon;
				    $('.bottomMenuIcon-position input[value=' + loadBottomMenuIcon + ']').prop('checked', true);
				    //设置主题预览中开始菜单尺寸
				    var startSize = winui.getSetting('startSize');
				    $('.start-size input[value=' + startSize + ']').prop('checked', true);
				    $('.preview-start').removeClass('xs sm lg');
				    $('.preview-start').addClass(startSize);
				    $(".preview-start").html(getFileContent('tpl/systheme/menu-model.tpl'));
				    var vagueBgSrc = winui.getSetting('vagueBgSrc');
				    if(vagueBgSrc == '1'){
				    	$("#winBgPicVague").val(false);
				    	$("#winBgPicVague").attr("checked", false);
				    }else{
				    	$("#winBgPicVague").val(true);
				    	$("#winBgPicVague").attr("checked", true);
				    }
				    matchingLanguage();
				    form.render();
			 	}
		    });
	
	        //预览锁屏界面
	        var Week = ['日', '一', '二', '三', '四', '五', '六'];
	        var dateTime = new Date();
	        $('.lockscreen-preview-time').html('<p id="time">' + (dateTime.getHours() > 9 ? dateTime.getHours().toString() : '0' + dateTime.getHours()) + ':' + (dateTime.getMinutes() > 9 ? dateTime.getMinutes().toString() : '0' + dateTime.getMinutes()) + '</p><p id="date">' + (dateTime.getMonth() + 1) + '月' + dateTime.getDate() + '日,星期' + Week[dateTime.getDay()] + '</p>');
	    });
	    
	    //自定义上传的桌面背景图片
	    function initCustomBackGroundPic(){
	    	showGrid({
	    	 	id: "cus-background-choose",
	    	 	url: reqBasePath + "sysevewinbgpic006",
	    	 	params: {},
	    	 	pagination: false,
	    	 	template: getFileContent('tpl/systheme/custom-bgpic-item.tpl'),
	    	 	ajaxSendLoadBefore: function(hdb){
	    	 		hdb.registerHelper("compare1", function(v1, options){
	    				return fileBasePath + v1;
	    			});
	    	 	},
	    	 	options: {'click .del':function(index, row){
		    	 		winui.window.confirm('确认删除选中数据吗？', { icon: 3, title: '删除win系统桌面图片' }, function (index) {
		    	 			winui.window.close(index);
	    		            AjaxPostUtil.request({url:reqBasePath + "sysevewinbgpic007", params:{rowId: row.id}, type: 'json', callback: function(json){
	    		    			if (json.returnCode == 0) {
	    		    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
	    		    				refreshGrid("cus-background-choose", {params:{}});
	    		    			}else{
	    		    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	    		    			}
	    		    		}});
	    				});
	    	 		}, 'click .bgPicItem1':function(index, row){
				        var bgSrc = row.picUrl;
				        AjaxPostUtil.request({url: reqBasePath + "sys025", params: {winBgPicUrl: bgSrc}, type: 'json', method: "PUT", callback: function(json){
			 	   			if (json.returnCode == 0) {
				 	   			$('.background-preview').css('background-image', 'url(' + bgSrc + ')');
						        winui.resetBg(bgSrc);
			 	   			}else{
			 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			 	   			}
			 	   		}});
			 		}
	    	 	},
	    	 	ajaxSendAfter:function(json){
	    	 	}
	        });
	    }
	    
	    //自定义上传的桌面锁屏背景图片
	    function initCustomLockBackGroundPic(){
	    	showGrid({
	    	 	id: "cus-lockscreen-choose",
	    	 	url: reqBasePath + "sysevewinlockbgpic006",
	    	 	params: {},
	    	 	pagination: false,
	    	 	template: getFileContent('tpl/systheme/custom-lockbgpic-item.tpl'),
	    	 	ajaxSendLoadBefore: function(hdb){
	    	 		hdb.registerHelper("compare1", function(v1, options){
	    				return fileBasePath + v1;
	    			});
	    	 	},
	    	 	options: {'click .lockDel':function(index, row){
		    	 		winui.window.confirm('确认删除选中数据吗？', { icon: 3, title: '删除win系统桌面图片' }, function (index) {
		    	 			winui.window.close(index);
	    		            AjaxPostUtil.request({url:reqBasePath + "sysevewinlockbgpic007", params:{rowId: row.id}, type: 'json', callback: function(json){
	    		    			if (json.returnCode == 0) {
	    		    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
	    		    				refreshGrid("cus-lockscreen-choose", {params:{}});
	    		    			}else{
	    		    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	    		    			}
	    		    		}});
	    				});
	    	 		}, 'click .lockBgPicItem1':function(index, row){
	    	 			var bgSrc = row.picUrl;
	    	 			AjaxPostUtil.request({url:reqBasePath + "sys026", params:{winLockBgPicUrl: bgSrc}, type: 'json', callback: function(json){
			 	   			if (json.returnCode == 0) {
				 	   			$('.lockscreen-preview').css('background-image', 'url(' + bgSrc + ')');
						        winui.resetLockBg(bgSrc);
			 	   			}else{
			 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			 	   			}
			 	   		}});
			 		}
	    	 	},
	    	 	ajaxSendAfter:function(json){
	    	 	}
	        });
	    }
	    
	    //桌面背景图片上传
	    var uploadInst = upload.render({
			elem: '#addBean', // 绑定元素
			url: reqBasePath + 'common003', // 上传接口
			data: {type: 4},
			done: function(json) {
				// 上传完毕回调
				if (json.returnCode == 0) {
					AjaxPostUtil.request({url:reqBasePath + "sysevewinbgpic005", params:{picUrl: json.bean.picUrl}, type: 'json', callback: function(json){
		    			if (json.returnCode == 0) {
		    				winui.window.msg("上传成功", {icon: 1, time: 2000});
		    				refreshGrid("cus-background-choose", {params:{}});
		    			}else{
		    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		    			}
		    		}});
				}else{
					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				}
			},
			error: function(e) {
				// 请求异常回调
				console.log(e);
			}
		});
	    
	    //桌面锁屏背景图片上传
	    var uploadInst1 = upload.render({
			elem: '#addBean1', // 绑定元素
			url: reqBasePath + 'common003', // 上传接口
			data: {type: 5},
			done: function(json) {
				// 上传完毕回调
				if (json.returnCode == 0) {
					AjaxPostUtil.request({url:reqBasePath + "sysevewinlockbgpic005", params:{picUrl: json.bean.picUrl}, type: 'json', callback: function(json){
		    			if (json.returnCode == 0) {
		    				winui.window.msg("上传成功", {icon: 1, time: 2000});
		    				refreshGrid("cus-lockscreen-choose", {params:{}});
		    			}else{
		    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		    			}
		    		}});
				}else{
					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				}
			},
			error: function(e) {
				// 请求异常回调
				console.log(e);
			}
		});
	    
	    //任务栏位置
	    form.on('radio(taskPosition)', function (data) {
	    	AjaxPostUtil.request({url:reqBasePath + "sys028", params:{winTaskPosition: data.value}, type: 'json', callback: function(json){
 	   			if (json.returnCode == 0) {
 	   				winui.resetTaskbar(data.value);
 	   			}else{
 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	   			}
 	   		}});
	    });
	    //开始菜单尺寸
	    form.on('radio(startSize)', function (data) {
	    	AjaxPostUtil.request({url:reqBasePath + "sys027", params:{winStartMenuSize: data.value}, type: 'json', callback: function(json){
 	   			if (json.returnCode == 0) {
 	   				winui.resetStartSize(data.value);
 	   			}else{
 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	   			}
 	   		}});
	    });
	    //窗口栏显示
	    form.on('radio(loadBottomMenuIcon)', function (data) {
	    	var parent$ = window.parent.layui.jquery;
	    	winuiTaskbarTask = parent$(".winui-taskbar-task");
	    	AjaxPostUtil.request({url:reqBasePath + "sys030", params:{loadBottomMenuIcon: data.value}, type: 'json', callback: function(json){
 	   			if (json.returnCode == 0) {
 	   				parent.loadBottomMenuIcon = data.value;
 	   				if(data.value === '1'){//只显示图标
 	   					winuiTaskbarTask.each(function () {
	 	   					$(this).find("li").addClass("task-item-icon-box");
	 	   					$(this).find(".title-icon-big").show();
	 	   					$(this).find(".title-icon").hide();
	 	   					$(this).find("font").hide();
 	   					});
 	   				}else{//图标+文字
	 	   				winuiTaskbarTask.each(function () {
	 	   					$(this).find("li").removeClass("task-item-icon-box");
	 	   					$(this).find(".title-icon-big").hide();
		   					$(this).find(".title-icon").show();
		   					$(this).find("font").show();
	 	   				});
 	   				}
 	   			}else{
 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	   			}
 	   		}});
	    });
	    //雾化开关
	    form.on('switch(winBgPicVague)', function (data) {
	    	var winBgPicVague = "";
	    	var winBgPicVagueValue = "5";
 			//同步开关值
 			$(data.elem).val(data.elem.checked);
 			if(data.elem.checked){
 				winBgPicVague = '0';
 			}else{
 				winBgPicVague = '1';
 			}
 			var params = {
				winBgPicVague: winBgPicVague,
				winBgPicVagueValue: winBgPicVagueValue
			};
 			AjaxPostUtil.request({url: reqBasePath + "sys029", params: params, type: 'json', method: "POST", callback: function(json){
 	   			if (json.returnCode == 0) {
 	   				winui.resetVagueBgSrc(winBgPicVague, winBgPicVagueValue);
 	   			}else{
 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	   			}
 	   		}});
 		});
	});
});