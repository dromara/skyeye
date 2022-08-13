
var proId = "";//项目id

var pageId = "";//页面id

var editPageModelSelectId = "";//正在编辑模板中的页面id
var editPageModelSelectChange = false;//选中的页面，模板是否修改

var form = "";//表单属性

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'dragula', 'swiper', 'slider', 'colorpicker', 'fileUpload', 'layedit'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		table = layui.table;
		form = layui.form;
	
	proId = parent.rowId;//项目id
	$("#groupMemberTab").hide();
	//初始化加载该项目的所有页面
	showGrid({
	 	id: "pageList",
	 	url: sysMainMation.rmprogramBasePath + "rmxcx029",
	 	params: {rowId: parent.rowId},
	 	pagination: false,
	 	template: getFileContent('tpl/rmmysmpropage/pageTemplate.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	options: {
	 		'click .page-click-item':function(index, row){//选择编辑模板中的页面
	 			if(row.id == editPageModelSelectId){//如果选中的页面正是当前编辑模板中的页面，则不做任何操作
	 				
	 			} else {
	 				if(editPageModelSelectChange == true){//编辑了页面但没有保存
	 		 			layer.confirm('当前修改页面没有保存，是否继续吗？', { icon: 3, title: '小程序页面编辑通知' }, function (i) {
	 		 				layer.close(i);
	 		 				$(".page-click-item").removeClass("check-item-shoose");
	 		 				$("#pageList>li:eq(" + index + ")").addClass("check-item-shoose");
	 		 				editPageModelSelectId = row.id;
	 		 				editPageModelSelectChange = false;
	 			 			AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmxcx036", params: {pageId: editPageModelSelectId}, type: 'json', callback: function (json) {
								showDataUseHandlebars("centerText", getFileContent('tpl/rmmysmpropage/pagemodelTemplate.tpl'), json);
	 			 	   		}});
	 		 			});
	 				} else {
	 					$(".page-click-item").removeClass("check-item-shoose");
	 					$("#pageList>li:eq(" + index + ")").addClass("check-item-shoose");
	 					editPageModelSelectId = row.id;
	 					editPageModelSelectChange = false;
	 		 			AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmxcx036", params: {pageId: editPageModelSelectId}, type: 'json', callback: function (json) {
							showDataUseHandlebars("centerText", getFileContent('tpl/rmmysmpropage/pagemodelTemplate.tpl'), json);
	 		 	   		}});
	 				}
	 			}
	 		},
	 		'click .reName':function(index, row){//重命名
	 			pageId = row.id;
	 			_openNewWindows({
	 				url: "../../tpl/rmmysmpropage/editpagebeanitem.html", 
	 				title: "重命名页面",
	 				pageId: "editpagebeanitem",
	 				area: ['700px', '60vh'],
	 				callBack: function(refreshCode) {
						refreshGrid("pageList", {params:{rowId: proId}});
						winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
						//重置中间模块
						editPageModelSelectId = "";
						editPageModelSelectChange = false;
						AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmxcx036", params: {pageId: editPageModelSelectId}, type: 'json', callback: function (json) {
							showDataUseHandlebars("centerText", getFileContent('tpl/rmmysmpropage/pagemodelTemplate.tpl'), json);
						}});
	 				}});
	 		},
	 		'click .toUp':function(index, row){//上移
	 			var params = {
        			proId: proId,
        			rowId: row.id
	        	};
	        	AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmxcx031", params: params, type: 'json', callback: function (json) {
					refreshGrid("pageList", {params:{rowId: proId}});
					//重置中间模块
					editPageModelSelectId = "";
					editPageModelSelectChange = false;
					AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmxcx036", params: {pageId: editPageModelSelectId}, type: 'json', callback: function (json) {
						showDataUseHandlebars("centerText", getFileContent('tpl/rmmysmpropage/pagemodelTemplate.tpl'), json);
					}});
	 	   		}});
	 		},
	 		'click .toDown':function(index, row){//下移
	 			var params = {
        			proId: proId,
        			rowId: row.id
	        	};
	        	AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmxcx032", params: params, type: 'json', callback: function (json) {
					refreshGrid("pageList", {params:{rowId: proId}});
					//重置中间模块
					editPageModelSelectId = "";
					editPageModelSelectChange = false;
					AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmxcx036", params: {pageId: editPageModelSelectId}, type: 'json', callback: function (json) {
						showDataUseHandlebars("centerText", getFileContent('tpl/rmmysmpropage/pagemodelTemplate.tpl'), json);
					}});
	 	   		}});
	 		},
	 		'click .copyPage':function(index, row){//复制
		 		
	 		},
	 		'click .delPage':function(index, row){//删除
	 			var msg = row ? '确认删除页面【' + row.name + '】吗？' : '确认删除选中数据吗？';
	 			layer.confirm(msg, { icon: 3, title: '删除小程序页面' }, function (index) {
	 				layer.close(index);
	 	            
	 	            AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmxcx035", params: {rowId: row.id}, type: 'json', callback: function (json) {
						winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
						refreshGrid("pageList", {params:{rowId: proId}});
						//重置中间模块
						editPageModelSelectId = "";
						editPageModelSelectChange = false;
						AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmxcx036", params:{pageId: editPageModelSelectId}, type: 'json', callback: function (json) {
							showDataUseHandlebars("centerText", getFileContent('tpl/rmmysmpropage/pagemodelTemplate.tpl'), json);
						}});
	 	    		}});
	 			});
	 		}
	 	},
	 	ajaxSendAfter:function (json) {
	 		matchingLanguage();
	 		//初始化加载小程序组件分组
	 		showGrid({
	 		 	id: "groupMember",
	 		 	url: sysMainMation.rmprogramBasePath + "rmxcx027",
	 		 	params: {},
	 		 	pagination: false,
	 		 	template: getFileContent('tpl/rmmysmpropage/groupTemplate.tpl'),
	 		 	ajaxSendLoadBefore: function(hdb){
	 		 	},
	 		 	ajaxSendAfter:function (json) {
	 		 		
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
			if(isNull(editPageModelSelectId)){
				winui.window.msg('请先选择要编辑的页面', {icon: 2, time: 2000});
				$("#centerText").empty();
			} else {
				el.className = 'layui-col-md12 import-item';
				var content = '<div class="layui-col-md12 check-item">' + $(el).attr("htmlContent") + '</div>';//内容
				var operationContent = '<div class="check-item-operation btn-group btn-group-xs btn-base">' +
				'<button type="button" class="btn btn-primary" rel="editHandler" title="编辑"><i class="fa fa-edit"></i></button>' + 
				'<button type="button" class="btn btn-danger" rel="removeHandler" title="删除"><i class="fa fa-trash"></i></button>' + 
				'</div>';
				var JsContent = '<script>layui.define(["jquery"], function(exports) {var jQuery = layui.jquery;(function($) {' + $(el).attr("htmlJsContent") + '})(jQuery);});</script>'
				$(el).html(content + operationContent + JsContent);
				$(el).find(".check-item-operation").hide();
			}
		}
	});
    
    //二级菜单点击
    $('body').on('click', '.js_item', function() {
        var id = $(this).data('id');
        var title = $(this).data('name');
        showGrid({
    	 	id: "memberList",
    	 	url: sysMainMation.rmprogramBasePath + "rmxcx028",
    	 	params: {rowId: id},
    	 	pagination: false,
    	 	template: getFileContent('tpl/rmmysmpropage/groupMemberTemplate.tpl'),
    	 	ajaxSendLoadBefore: function(hdb){
    	 		hdb.registerHelper("compare1", function(v1, options){
					return fileBasePath + "images/upload/smpropic/" + v1;
				});
    	 	},
    	 	ajaxSendAfter:function (json) {
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
    $('body').on('click', '#returnGroupTab', function() {
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
    $('body').on('click', '.js_category', function() {
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
        } else {
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
    
    // 图片预览
    $('body').on('click', '.cursor', function() {
		systemCommonUtil.showPicImg($(this).attr("src"));
    });
    
    // 页面内组件选中组件项
    $('body').on('click', '.check-item', function() {
    	$(".check-item").removeClass("show-operation");
    	$(".check-item").removeClass("check-item-shoose");//移除之前被选中的组件
    	$(".check-item").parent().find(".check-item-operation").hide();//隐藏之前选中的组件的操作
    	$(this).addClass("check-item-shoose");//给当前组件添加选中样式
    	$(this).addClass("show-operation");
    	$(this).parent().find(".check-item-operation").show();//显示当前选中的组件的操作
    	var memberId = $(this).parent().attr("rowId");
    	//获取组件的标签属性
    	AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmxcx040", params: {rowId: memberId}, type: 'json', callback: function (json) {
			if(json.total != 0){
				var str = "";
				var jsRelyOn = "";
				var jsContent = "";
				for(var i = 0; i < json.rows.length; i++){
					if(json.rows[i].selChildData == '1'){
						var modeContent = getDataUseHandlebars(json.rows[i].templateContent, json.rows[i].propertyValue);
						json.rows[i].htmlContent = json.rows[i].htmlContent.replace(/{{content}}/g, modeContent);
					}
					var defaultValue = "";
					if(json.rows[i].propertyUnit == '%'){//百分号计算获取宽高百分比
						defaultValue = Math.ceil($("div#centerText .show-operation").children().width() / $("div#centerText .show-operation").width() * 100);
					} else {
						defaultValue = $("div#centerText .show-operation").children().css(json.rows[i].propertyTag).replace(json.rows[i].propertyUnit, '');
					}
					str = str + json.rows[i].htmlContent.replace(/{{id}}/g, json.rows[i].id).replace(/{{labelContent}}/g, json.rows[i].title)
								.replace(/{{placeholder}}/g, json.rows[i].title).replace(/{{tag}}/g, json.rows[i].propertyTag)
								.replace(/{{unit}}/g, json.rows[i].propertyUnit).replace(/{{out}}/g, json.rows[i].propertyOut)
								.replace(/{{defaultValue}}/g, defaultValue);
					jsRelyOn = jsRelyOn + json.rows[i].jsRelyOn;
					jsContent = jsContent + json.rows[i].jsContent.replace(/{{id}}/g, json.rows[i].id).replace(/%2B/g, '\+').replace(/%26/g, "\&")
														.replace(/{{defaultValue}}/g, defaultValue);
				}
				jsContent = '<script>layui.define(["jquery"], function(exports) {var jQuery = layui.jquery;(function($) {' + jsContent + '})(jQuery);});</script>';
				if(isNull(str)){
					$("#showForm").html(noMatchingBeansMation);
				} else {
					str = str + '<div class="layui-form-item"><div class="layui-input-block"><button class="winui-btn" lay-submit lay-filter="saveProperty">保存属性</button></div></div>';
					$("#showForm").html(str + jsContent);
					form.render();
					form.on('submit(saveProperty)', function (data) {
						console.log(data);
						if (winui.verifyForm(data.elem)) {

						}
						return false;
					});
				}
			} else {
				$("#showForm").html(noMatchingBeansMation);
			}
   		}});
    });
    
    //页面内组件移除按钮
    $('body').on('click', 'button[rel="removeHandler"]', function() {
    	$(this).parent().parent().remove();
    })
    
    //监听页面内容是否变化
    $('body').on('DOMNodeInserted', '#centerText', function() {
    	if(isNull(editPageModelSelectId)){//如果没有选中页面，则不做任何操作
    		
    	} else {
    		editPageModelSelectChange = true;
    	}
    });
    
    //添加页面按钮
    $('body').on('click', '#addPageBean', function() {
		_openNewWindows({
			url: "../../tpl/rmmysmpropage/addpagebeanitem.html", 
			title: "新增页面",
			pageId: "addpagebeanitem",
			area: ['700px', '60vh'],
			callBack: function(refreshCode) {
				refreshGrid("pageList", {params:{rowId: proId}});
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
			}});
    });
    
    //保存页面
    $('body').on('click', '#savePageModelBean', function() {
    	if(!isNull(editPageModelSelectId)){//要编辑的模板页面id不为空
    		editPageModelSelectChange = false;
    		var list = [];//存储模板生成集合
    		$('#centerText').find('.import-item').each(function() {
    			var s = {
    				modelId: $(this).attr("rowId"),
    				pageId: editPageModelSelectId
    			};
    			list.push(s);
    		});
    		AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmxcx037", params: {jsonData: JSON.stringify(list), pageId: editPageModelSelectId}, type: 'json', callback: function (json) {
				winui.window.msg("保存成功", {icon: 1, time: 2000});
    		}});
    	} else {
    		winui.window.msg('请先选择要编辑的页面', {icon: 2, time: 2000});
    	}
    });
    
    //导出选中页为H5
    $('body').on('click', '#exportChoosePageToH5', function() {
    	if(!isNull(editPageModelSelectId)){//要编辑的模板页面id不为空
    		AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmxcx041", params: {pageId: editPageModelSelectId}, type: 'json', callback: function (json) {
				winui.window.msg("导出成功", {icon: 1, time: 2000});
				const link = document.createElement('a');
				link.style.display = 'none';
				link.href = fileBasePath + json.bean.url;
				link.setAttribute(
				  'download',
				  json.bean.fileName
				);
				document.body.appendChild(link);
				link.click();
    		}});
    	} else {
    		winui.window.msg('请先选择要导出的页面', {icon: 2, time: 2000});
    	}
    });
    
    exports('mysmpropagelist', {});
});
