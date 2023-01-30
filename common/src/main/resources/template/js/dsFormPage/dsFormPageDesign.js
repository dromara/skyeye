
var pageId = GetUrlParam("pageId");
var layedit, form;

// 选中的表单布局组件信息
var contentData;
// 表单布局里的组件列表
var contentList = [];
// 子页面调用的函数
var sortDataIn;
// 业务对象的属性信息
var attrList = [];

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'dragula', 'tagEditor'].concat(dsFormUtil.mastHaveImport), function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$;
		layedit = layui.layedit,
		form = layui.form;
	var inPageTitle = "已存在布局中，不可选择.";
	var noComponentTitle = "未绑定组件，不可选择.";
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

	AjaxPostUtil.request({url: reqBasePath + "dsformpage006", params: {id: pageId}, type: 'json', method: 'GET', callback: function (json) {
		// 加载页面内容
		loadPageMation({rows: json.bean.dsFormPageContents});
 	}});
 	
 	// 加载新增加的控件信息
    function loadNewControl(item) {
		item = dsFormUtil.loadComponent('showForm', item);
		$("#showForm div[contentId='" + item.id + "']").append(`<div class="btn-base">
			<button type="button" class="btn btn-danger removeThis" title="删除"><i class="fa fa-trash"></i></button>
		</div>`);

		var attrKey = item.attrKey;
		if (!isNull(attrKey)) {
			$(`#attrBox div[attrKey=${attrKey}]`).attr('title', inPageTitle);
			$(`#attrBox div[attrKey=${attrKey}]`).find('i').removeClass("i-label-class");
			$(`#attrBox div[attrKey=${attrKey}]`).addClass("no-choose");
			$(`#attrBox div[attrKey=${attrKey}]`).find('label').addClass("no-choose");
		}

		contentList.push(item);
		form.render();
    }

	// 删除
	$("body").on("click", ".removeThis", function (e) {
		var contentId = $(this).parents('.layui-form-item').attr("contentId");
		$("#showForm div[contentId='" + contentId + "']").remove();
		var content = getInPoingArr(contentList, 'id', contentId);
		if (!isNull(content.attrKey)) {
			$(`#attrBox div[attrKey=${content.attrKey}]`).attr('title', '');
			$(`#attrBox div[attrKey=${content.attrKey}]`).removeClass("no-choose");
			$(`#attrBox div[attrKey=${content.attrKey}]`).find('i').addClass("i-label-class");
			$(`#attrBox div[attrKey=${content.attrKey}]`).find('label').removeClass("no-choose");
		}
		contentList = arrayUtil.removeArrayPointKey(contentList, 'id', contentId);
		contentData = null;
		$("#editPageContent").attr("src", "../../tpl/dsFormPage/editPageContent.html");
	});

	function loadPageMation(json) {
		$("#attrBox").html(getDataUseHandlebars($("#leftAttrBoxItem").html(), {rows: attrList}));
		$.each(json.rows, function (i, item) {
			loadNewControl(item);
		});
		matchingLanguage();
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
				// 加载属性
				loadClassAttr();
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
						item.showTitle = noComponentTitle;
					} else {
						var temp = getInPoingArr(contentList, 'attrKey', item.attrKey);
						if (!isNull(temp)) {
							item.class = "no-choose";
							item.showTitle = inPageTitle;
						} else {
							item.iLabelClass = "i-label-class";
						}
					}
				});
				attrList = [].concat(json.rows);
			},
			ajaxSendAfter:function (json) {
				form.render();
				dragula([document.getElementById('btnBox'), document.getElementById('attrBox'), document.getElementById('showForm')], {
					copy: function (el, source) {//复制
						var check = checkMove(el);
						if (!check) {
							return false;
						}
						return source === document.getElementById('btnBox') || source === document.getElementById('attrBox');
					},
					accepts: function (el, target) {//移动
						var check = checkMove(el);
						if (!check) {
							return false;
						}
						return target != document.getElementById('btnBox') && target != document.getElementById('attrBox');
					}
				}).on('drop', function (el, container) {//放置
					if ($(container).attr("id") == 'showForm') {
						var attrKey = $(el).attr("attrKey");
						attrKey = isNull(attrKey) ? '' : attrKey;
						addPageContent($(el).attr("componentId"), attrKey);
					}
				});
			}
		});
	}

	function checkMove(el) {
		if (el.className.indexOf('no-choose') >= 0) {
			return false;
		}
		return true;
	}
 	
	function addPageContent(id, attrKey) {
		var dsFormComponent = getInPoingArr(componentList, 'id', id);
		if (isNull(dsFormComponent)) {
			return false;
		}

		var params  = {
			width: 'layui-col-xs12',
			formContentId: id,
			dsFormComponent: dsFormComponent,
			title: dsFormComponent.name,
			attrKey: attrKey,
			attrDefinition: !isNull(attrKey) ? getInPoingArr(attrList, 'attrKey', attrKey) : null,
			id: getRandomValueToString()
       	};
		loadNewControl(params);
		sortDataIn();
	}
	
    // 页面控件点击事件
    $("body").on("click", "#showForm .layui-form-item", function() {
		initFormItemClick($(this));
    });

	function initFormItemClick(_this) {
		$("#showForm .layui-form-item").removeClass('ui-sortable-placeholder-choose');
		$("#showForm .layui-form-item").find('.btn-base').hide();
		_this.addClass("ui-sortable-placeholder-choose");
		var contentId = _this.attr("contentId");
		$("#showForm div[contentId='" + contentId + "']").find('.btn-base').show();
		contentData = getInPoingArr(contentList, 'id', contentId);
		$("#editPageContent").attr("src", "../../tpl/dsFormPage/editPageContent.html");
	}

	sortDataIn = function () {
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

	var tip_index;
	$("body").on("mouseenter", ".attr-mation", function (e) {
		var title = $(this).attr('title');
		if (isNull(title)) {
			return false;
		}
		tip_index = layer.tips(title, this, {time: 0, tips: 3});
	}).on('mouseleave', ".attr-mation", function() {
		layer.close(tip_index);
	});
    
	// 保存
	$("body").on("click", "#save", function() {
		for (var i = 0; i < contentList.length; i++) {
			var item = contentList[i];
			if (isNull(item.attrKey) && $.inArray('attrKeyBox', item.dsFormComponent.attrKeys) >= 0) {
				winui.window.msg("存在无关联属性的组件，请移除.", {icon: 2, time: 2000});
				initFormItemClick($("#showForm div[contentId='" + item.id + "']"));
				return false;
			}
		}
		sortDataIn();
		var params = {
			pageId: pageId,
			dsFormPageContentList: JSON.stringify(contentList)
		}
		AjaxPostUtil.request({url: reqBasePath + "writeDsFormPageContent", params: params, type: 'json', method: 'POST', callback: function (json) {
			winui.window.msg("保存成功", {icon: 1, time: 2000});
			parent.refreshCode = '0';
   		}});
    });
});