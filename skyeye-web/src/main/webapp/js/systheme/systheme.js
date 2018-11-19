
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'fileUpload', 'upload'], function (exports) {
	winui.renderColor();
	layui.use(['layer', 'form'], function (exports) {
	    var $ = layui.jquery, 
	    form = layui.form, 
	    unfinished = '暂未实现',
	    upload = layui.upload;
	
	    $(function () {
	        winui.renderColor();
	        winui.tab.init();
	        //设置预览背景为当前背景
	        $('.background-preview').css('background-image', layui.jquery('body').css('background-image'));
	        //设置锁屏预览背景为当前锁屏预览背景
	        $('.lockscreen-preview').css('background-image', 'url(' + winui.getSetting('lockBgSrc') + ')');
	
	        //设置主题预览中任务栏位置
	        var taskbarMode = winui.getSetting('taskbarMode');
	        $('.taskbar-position input[value=' + taskbarMode + ']').prop('checked', true);
	        //设置主题预览中开始菜单尺寸
	        var startSize = winui.getSetting('startSize');
	        $('.start-size input[value=' + startSize + ']').prop('checked', true);
	        $('.preview-start').removeClass('xs sm lg');
	        $('.preview-start').addClass(startSize);
	
	        form.render();
	        
	        //初始化背景图片
		    showGrid({
			 	id: "background-choose",
			 	url: reqBasePath + "sysevewinbgpic004",
			 	params: {},
			 	pagination: false,
			 	template: getFileContent('tpl/systheme/bg-pic.tpl'),
			 	ajaxSendLoadBefore: function(hdb){
			 		hdb.registerHelper("compare1", function(v1, options){
						return fileBasePath + v1;
					});
			 	},
			 	options: {'click .bgPicItem':function(index, row){
				 		//获取当前图片路径
				        var bgSrc = row.picUrl;
				        //改变预览背景
				        $('.background-preview').css('background-image', 'url(' + bgSrc + ')');
				        //改变父页面背景
				        winui.resetBg(bgSrc);
			 		}
			 	},
			 	ajaxSendAfter:function(json){
			 		initCustomBackGroundPic();
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
	    				layer.confirm('确认删除选中数据吗？', { icon: 3, title: '删除win系统桌面图片' }, function (index) {
	    					layer.close(index);
	    		            AjaxPostUtil.request({url:reqBasePath + "sysevewinbgpic007", params:{rowId: row.id}, type:'json', callback:function(json){
	    		    			if(json.returnCode == 0){
	    		    				top.winui.window.msg("删除成功", {icon: 1,time: 2000});
	    		    				refreshGrid("cus-background-choose", {params:{}});
	    		    			}else{
	    		    				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	    		    			}
	    		    		}});
	    				});
	    	 		}, 'click .bgPicItem1':function(index, row){
				 		//获取当前图片路径
				        var bgSrc = row.picUrl;
				        //改变预览背景
				        $('.background-preview').css('background-image', 'url(' + bgSrc + ')');
				        //改变父页面背景
				        winui.resetBg(bgSrc);
			 		}
	    	 	},
	    	 	ajaxSendAfter:function(json){
	    	 	}
	        });
	    }
	    
	    //背景图片上传
	    var uploadInst = upload.render({
			elem: '#addBean', // 绑定元素
			url: reqBasePath + 'common003', // 上传接口
			data: {type: 4},
			done: function(json) {
				// 上传完毕回调
				if(json.returnCode == 0){
					AjaxPostUtil.request({url:reqBasePath + "sysevewinbgpic005", params:{picUrl: json.bean.picUrl}, type:'json', callback:function(json){
		    			if(json.returnCode == 0){
		    				top.winui.window.msg("上传成功", {icon: 1,time: 2000});
		    				refreshGrid("cus-background-choose", {params:{}});
		    			}else{
		    				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		    			}
		    		}});
				}else{
					top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				}
			},
			error: function(e) {
				// 请求异常回调
				console.log(e);
			}
		});
	    //锁屏界面点击
	    $('.lockscreen-choose>img').on('click', function () {
	        //获取当前图片路径
	        var bgSrc = $(this).prop('src');
	        //改变锁屏预览
	        $('.lockscreen-preview').css('background-image', 'url(' + bgSrc + ')');
	        //设置锁屏背景
	        winui.resetLockBg(bgSrc);
	    })
	    //锁屏界面图片上传
	    $('.lockscreen-upload').on('click', function () {
	        var input = $(this).prev('input[type=file]');
	        input.trigger('click');
	        input.on('change', function () {
	            var src = $(this).val();
	            if (src) {
	                layer.msg('选择了路径【' + src + '】下的图片，返回一张性感的Girl给你')
	                //改变锁屏预览
	                $('.lockscreen-preview').css('background-image', 'url(images/sexy_girl.jpg');
	                //设置锁屏背景
	                winui.resetLockBg('images/sexy_girl.jpg');
	                $(this).val('').off('change');
	            }
	        })
	    });
	    //颜色选择
	    $('.color-choose>div').on('click', function () {
	        var color = Number($(this)[0].classList[0].replace('theme-color-', ''));
	        winui.resetColor(color);
	    });
	
	    form.on('switch(toggleTransparent)', function (data) {
	        if (data.elem.checked) {
	            $(data.elem).siblings('span').text('开');
	        } else {
	            $(data.elem).siblings('span').text('关');
	        }
	        layer.msg(unfinished);
	    });
	
	    form.on('switch(toggleTaskbar)', function (data) {
	        if (data.elem.checked) {
	            $(data.elem).siblings('span').text('开');
	        } else {
	            $(data.elem).siblings('span').text('关');
	        }
	    });
	    //任务栏位置
	    form.on('radio(taskPosition)', function (data) {
	        switch (data.value) {
	            case 'top':
	                winui.resetTaskbar(data.value);
	                break;
	            case 'bottom':
	                winui.resetTaskbar(data.value);
	                break;
	            case 'left':
	                winui.window.msg(unfinished);
	                break;
	            case 'right':
	                winui.window.msg(unfinished);
	                break;
	            default:
	        }
	    });
	    //开始菜单尺寸
	    form.on('radio(startSize)', function (data) {
	        winui.resetStartSize(data.value);
	    });
	});
});