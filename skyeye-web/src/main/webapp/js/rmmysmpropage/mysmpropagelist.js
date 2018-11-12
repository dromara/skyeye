
var proId = "";//项目id

var pageId = "";//页面id

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form', 'dragula', 'swiper'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	table = layui.table;
	
	proId = parent.rowId;//项目id
	
	$("#groupMemberTab").hide();
	//初始化加载该项目的所有页面
	showGrid({
	 	id: "pageList",
	 	url: reqBasePath + "rmxcx029",
	 	params: {rowId: parent.rowId},
	 	pagination: false,
	 	template: getFileContent('tpl/rmmysmpropage/pageTemplate.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	options: {
	 		'click .reName':function(index, row){//重命名
	 			pageId = row.id;
	 			_openNewWindows({
	 				url: "../../tpl/rmmysmpropage/editpagebeanitem.html", 
	 				title: "重命名页面",
	 				pageId: "editpagebeanitem",
	 				callBack: function(refreshCode){
	 	                if (refreshCode == '0') {
	 	                	refreshGrid("pageList", {params:{rowId: proId}});
	 	                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
	 	                } else if (refreshCode == '-9999') {
	 	                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
	 	                }
	 				}});
	 		},
	 		'click .toUp':function(index, row){//上移
	 			var params = {
        			proId: proId,
        			rowId: row.id,
	        	};
	        	AjaxPostUtil.request({url:reqBasePath + "rmxcx031", params:params, type:'json', callback:function(json){
	 	   			if(json.returnCode == 0){
	 	   				refreshGrid("pageList", {params:{rowId: proId}});
	 	   			}else{
	 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 	   			}
	 	   		}});
	 		},
	 		'click .toDown':function(index, row){//下移
	 			var params = {
        			proId: proId,
        			rowId: row.id,
	        	};
	        	AjaxPostUtil.request({url:reqBasePath + "rmxcx032", params:params, type:'json', callback:function(json){
	 	   			if(json.returnCode == 0){
	 	   				refreshGrid("pageList", {params:{rowId: proId}});
	 	   			}else{
	 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 	   			}
	 	   		}});
	 		},
	 		'click .copyPage':function(index, row){//复制
		 		
	 		},
	 		'click .delPage':function(index, row){//删除
	 			var msg = row ? '确认删除页面【' + row.name + '】吗？' : '确认删除选中数据吗？';
	 			layer.confirm(msg, { icon: 3, title: '删除小程序页面' }, function (index) {
	 				layer.close(index);
	 	            //向服务端发送删除指令
	 	            AjaxPostUtil.request({url:reqBasePath + "rmxcx035", params:{rowId: row.id}, type:'json', callback:function(json){
	 	    			if(json.returnCode == 0){
	 	    				top.winui.window.msg("删除成功", {icon: 1,time: 2000});
	 	    				refreshGrid("pageList", {params:{rowId: proId}});
	 	    			}else{
	 	    				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 	    			}
	 	    		}});
	 			});
	 		}
	 	},
	 	ajaxSendAfter:function(json){
	 		//初始化加载小程序组件分组
	 		showGrid({
	 		 	id: "groupMember",
	 		 	url: reqBasePath + "rmxcx027",
	 		 	params: {},
	 		 	pagination: false,
	 		 	template: getFileContent('tpl/rmmysmpropage/groupTemplate.tpl'),
	 		 	ajaxSendLoadBefore: function(hdb){
	 		 	},
	 		 	ajaxSendAfter:function(json){
	 		 		
	 		 	}
	 		});
	 	}
	});
	
	var winH = $(window).height();
    var categorySpace = 10;
    
    dragula([document.getElementById('memberList'), document.getElementById('centerText')], {
		copy: function (el, source) {//复制
			return source === document.getElementById('memberList');
		},
		accepts: function (el, target) {//移动
			return target !== document.getElementById('memberList');
		}
	}).on('drop', function (el, container) {//放置
		if($(container).attr("id") == 'centerText'){//放置在手机里面
			el.className = 'layui-col-md12';
			var content = '<div class="layui-col-md12 check-item">' + $(el).attr("htmlContent") + '</div>';//内容
			var operationContent = '<div class="check-item-operation btn-group btn-group-xs btn-base">' +
								'<button type="button" class="btn btn-primary" rel="editHandler" title="编辑"><i class="fa fa-edit"></i></button>' + 
								'<button type="button" class="btn btn-danger" rel="removeHandler" title="删除"><i class="fa fa-trash"></i></button>' + 
								'</div>';
			var JsContent = '<script>layui.define(["jquery"], function(exports) {var jQuery = layui.jquery;(function($) {' + $(el).attr("htmlJsContent") + '})(jQuery);});</script>'
			$(el).html(content + operationContent + JsContent);
			$(el).find(".check-item-operation").hide();
		}
	});
    
    //二级菜单点击
    $('body').on('click', '.js_item', function(){
        var id = $(this).data('id');
        var title = $(this).data('name');
        showGrid({
    	 	id: "memberList",
    	 	url: reqBasePath + "rmxcx028",
    	 	params: {rowId: id},
    	 	pagination: false,
    	 	template: getFileContent('tpl/rmmysmpropage/groupMemberTemplate.tpl'),
    	 	ajaxSendLoadBefore: function(hdb){
    	 		hdb.registerHelper("compare1", function(v1, options){
					return fileBasePath + "assets/smpropic/" + v1;
				});
    	 	},
    	 	ajaxSendAfter:function(json){
    	 	}
    	});
        $("#groupTitle").html(title);
        $("#groupTab").animate({  
            width : "hide",  
            opacity: "0",
            paddingLeft : "hide",  
            paddingRight : "hide",  
            marginLeft : "hide",  
            marginRight : "hide"  
        }, 500);
        $("#groupMemberTab").animate({  
            width : "show",  
            opacity: "1",
            paddingLeft : "show",  
            paddingRight : "show",  
            marginLeft : "show",  
            marginRight : "show"  
        }, 500); 
    });
    
    //返回分组列表
    $('body').on('click', '#returnGroupTab', function(){
    	$("#groupMemberTab").animate({  
            width : "hide",  
            opacity: "0",
            paddingLeft : "hide",  
            paddingRight : "hide",  
            marginLeft : "hide",  
            marginRight : "hide"  
        }, 500);
        $("#groupTab").animate({  
            width : "show",  
            opacity: "1",
            paddingLeft : "show",  
            paddingRight : "show",  
            marginLeft : "show",  
            marginRight : "show"  
        }, 500); 
    });
    
    //展开一级菜单
    $('body').on('click', '.js_category', function(){
        var $this = $(this),
            $inner = $this.next('.js_categoryInner'),
            $page = $this.parents('.page'),
            $parent = $(this).parent('li');
        var innerH = $inner.data('height');
        bear = $page;

        if(!innerH){
            $inner.css('height', 'auto');
            innerH = $inner.height();
            $inner.removeAttr('style');
            $inner.data('height', innerH);
        }

        if($parent.hasClass('js_show')){
            $parent.removeClass('js_show');
        }else{
            $parent.siblings().removeClass('js_show');

            $parent.addClass('js_show');
            if(this.offsetTop + this.offsetHeight + innerH > $page.scrollTop() + winH){
                var scrollTop = this.offsetTop + this.offsetHeight + innerH - winH + categorySpace;

                if(scrollTop > this.offsetTop){
                    scrollTop = this.offsetTop - categorySpace;
                }

                $page.scrollTop(scrollTop);
            }
        }
    });
    
    //图片预览
    $('body').on('click', '.cursor', function(){
    	layer.open({
    		type:1,
    		title:false,
    		closeBtn:0,
    		skin: 'demo-class',
    		shadeClose:true,
    		content:'<img src="' + $(this).attr("src") + '" style="max-height:600px;max-width:100%;">',
    		scrollbar:false
        });
    });
    
    //页面内组件选中组件项
    $('body').on('click', '.check-item', function(){
    	$(".check-item").removeClass("check-item-shoose");//移除之前被选中的组件
    	$(".check-item").parent().find(".check-item-operation").hide();//隐藏之前选中的组件的操作
    	$(this).addClass("check-item-shoose");//给当前组件添加选中样式
    	$(this).parent().find(".check-item-operation").show();//显示当前选中的组件的操作
    });
    
    //页面内组件移除按钮
    $('body').on('click', 'button[rel="removeHandler"]', function(){
    	$(this).parent().parent().remove();
    })
    
    //添加页面按钮
    $('body').on('click', '#addPageBean', function(){
		_openNewWindows({
			url: "../../tpl/rmmysmpropage/addpagebeanitem.html", 
			title: "新增页面",
			pageId: "addpagebeanitem",
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	refreshGrid("pageList", {params:{rowId: proId}});
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
    });
    
    exports('mysmpropagelist', {});
});
