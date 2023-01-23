
var pageId = GetUrlParam("pageId");
var layedit, form;

// 选中的表单布局组件信息
var contentData;

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'jqueryUI', 'tagEditor'].concat(dsFormUtil.mastHaveImport), function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$;
		layedit = layui.layedit,
		form = layui.form;
	// 表单布局里的组件列表
	var contentList = [];
	// 表单控件集合
	var componentList = [];
	
	authBtn('1567732055673');//保存控件
	var className = GetUrlParam("className");
	if (isNull(className)) {
		winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
		return false;
	}

	// 加载表单控件
	loadLeftBoxItem();
	// 加载属性
	loadClassAttr();
	
	AjaxPostUtil.request({url: reqBasePath + "dsformpage006", params: {id: pageId}, type: 'json', method: 'GET', callback: function (json) {
		// 加载页面内容
		loadPageMation({rows: json.bean.dsFormPageContents});
 	}});
 	
 	// 加载新增加的控件信息
    function loadNewControl(item) {
		item = dsFormUtil.loadComponent('showForm', item);
		$("#showForm div[contentId='" + item.id + "']").append(`<div class="btn-base">
			<button type="button" class="btn copyThis" title="复制组件"><i class="fa fa-copy"></i></button>
			<button type="button" class="btn btn-danger removeThis" title="删除"><i class="fa fa-trash"></i></button>
		</div>`);
		contentList.push(item);
		form.render();
    }

	// 复制组件
	$("body").on("click", ".copyThis", function (e) {
		var contentId = $(this).parents('.layui-form-item').attr("contentId");
		$("#showForm div[contentId='" + contentId + "']").remove();
		var dsFormComponent = getInPoingArr(contentList, 'id', contentId, 'dsFormComponent');
		getFormPageControlContent(dsFormComponent.id);
	});

	// 删除
	$("body").on("click", ".removeThis", function (e) {
		var contentId = $(this).parents('.layui-form-item').attr("contentId");
		$("#showForm div[contentId='" + contentId + "']").remove();
		contentList = arrayUtil.removeArrayPointKey(contentList, 'id', contentId);
		contentData = null;
		$("#editPageContent").attr("src", "../../tpl/dsFormPage/editPageContent.html");
	});

	function loadPageMation(json) {
		$.each(json.rows, function(i, item) {
			loadNewControl(item);
		});
		loadFormItemDrop();
		matchingLanguage();
	}

 	function loadFormItemDrop() {
 		$.each($("#showForm").find(".layui-form-item"), function(i, item) {
 			var _this = $(item);
 			if(!_this.hasClass('form-group')){
 				_this.addClass('form-group');
 			}
 			if(!_this.hasClass('draggable')){
 				_this.addClass('draggable');
 			}
 			if(!_this.hasClass('ui-draggable')){
 				_this.addClass('ui-draggable');
 			}
 			if(!_this.hasClass('dropped')){
 				_this.addClass('dropped');
 			}
 		});
 	}
 	
 	// 加载表单控件
 	function loadLeftBoxItem() {
 		showGrid({
		 	id: "btnBox",
		 	url: reqBasePath + "queryAllDsFormComponentList",
		 	params: {},
		 	pagination: false,
			method: 'GET',
		 	template: $("#leftBoxItem").html(),
			ajaxSendLoadBefore: function (hdb, json) {
				$.each(json.bean, function (key, value) {
					$.each(value, function (j, bean) {
						bean.logo = systemCommonUtil.initIconShow(bean);
						componentList.push(bean);
					});
				});
			},
		 	ajaxSendAfter:function (json) {
		 		form.render();
				$(".draggable").draggable({
					appendTo: "body",
					helper: "clone",
					drag: function (event, ui) {},
					stop: function () {}
				});
				$(".droppable").droppable({
					accept: ".draggable",
					helper: "clone",
					hoverClass: "droppable-active",
					drop: function(event, ui) {
						$(".empty-form").remove();
						var _this = $(ui.draggable);
						if (!_this.hasClass("dropped")) {
							getFormPageControlContent(_this.attr("componentId"));
						} else {
							if ($(this)[0] != _this.parent()[0]) {
								var $el = _this.clone().appendTo(this);
								_this.remove()
							}
						}
					}
				}).sortable();
		 	}
		});
 	}

	// 加载属性
	function loadClassAttr() {
		showGrid({
			id: "attrBox",
			url: reqBasePath + "queryAttrDefinitionList",
			params: {className: className},
			pagination: false,
			method: 'POST',
			template: $("#leftAttrBoxItem").html(),
			ajaxSendLoadBefore: function (hdb, json) {
				$.each(json.rows, function (i, item) {
					if (isNull(item.dsFormComponent)) {
						item.class = "no-choose";
					} else {
						item.iLabelClass = "i-label-class";
					}
				});
			},
			ajaxSendAfter:function (json) {

			}
		});
	}
 	
	function getFormPageControlContent(id) {
		var dsFormComponent = {};
		$.each(componentList, function(i, item) {
			if(item.id == id){
				dsFormComponent = item;
			}
		});
		var params  = {
			width: 'layui-col-xs12',
			title: dsFormComponent.name,
			require: '',
			placeholder: '',
			defaultValue: '',
			formContentId: id,
			attrKey: '',
			dsFormComponent: dsFormComponent,
			id: getRandomValueToString()
       	};
		loadNewControl(params);
		sortDataIn();
	}
	
    // 页面控件点击事件
    $("body").on("click", ".form-group", function() {
    	$("#showForm .form-group").removeClass('ui-sortable-placeholder-choose');
		$("#showForm .form-group").find('.btn-base').hide();
    	$(this).addClass("ui-sortable-placeholder-choose");
    	var contentId = $(this).attr("contentId");
		$("#showForm div[contentId='" + contentId + "']").find('.btn-base').show();
    	$.each(contentList, function(i, item) {
			if(item.id === contentId){
				contentData = item;
				$("#editPageContent").attr("src", "../../tpl/dsFormPage/editPageContent.html");
				return;
			}
		});
    });
    
    function sortDataIn() {
    	// 对控件进行排序
		$.each(contentList, function(i, item) {
			var inIndex = $("#showForm div[contentId='" + item.id + "']").index();
			contentList[i].orderBy = inIndex;
		});
    	$("#showForm").empty();
    	contentList.sort(getSortFun('asc', 'orderBy'));
    	var newJson = [].concat(contentList);
    	contentList = [];
    	loadPageMation({rows: newJson});
    }
    
	// 保存
	$("body").on("click", "#save", function() {
		sortDataIn();
		if (contentList.length == 0) {
			winui.window.msg('保存页面不能为空！', {icon: 2, time: 2000});
			return;
		}
		AjaxPostUtil.request({url: reqBasePath + "todo 待写", params: {formedit: JSON.stringify(contentList)}, type: 'json', callback: function (json) {
			winui.window.msg("保存成功", {icon: 1, time: 2000});
			parent.refreshCode = '0';
   		}});
    });
});