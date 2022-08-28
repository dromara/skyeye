
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'cookie', 'orgChart', 'element', 'table', 'jsonEditor'].concat(dsFormUtil.mastHaveImport), function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		table = layui.table,
		form = layui.form;
		element = layui.element;
		active = {
			parseTable: function() {
				table.init('parse-table-demo', { //转化静态表格
					limit: 100,
				});
			}
		};
	var type = 'parseTable';
	var defaultTemplate = $('#defaultTemplate').html(),
		fileListTemplate = $('#fileListTemplate').html(),
		fileMationTemplate = $('#fileMationTemplate').html(),
		menuBoxTemplate = $("#menuBoxTemplate").html(),
		modelTemplate = $("#modelTemplate").html();

	$(document).attr("title", sysMainMation.mationTitle);
	$(".sys-logo").html(sysMainMation.mationTitle);

	// 获取当前登录员工信息
	systemCommonUtil.getSysCurrentLoginUserMation(function (data) {
		var str = '<img alt="' + data.bean.userName + '" src="' + fileBasePath + data.bean.userPhoto + '"/>'
			+ '<font>' + data.bean.userName + '</font>'
			+ '<font id="consoleDesk">控制台</font>'
			+ '<font id="exitBtn">退出</font>';

		// 高级查询配置保存操作
		authBtn('1657962237576');

		$("#operatorBtn").html(str);

		// 加载微服务
		loadApiMicroservices();
	}, function (){
		location.href = "../../tpl/index/login.html?url=" + escape("../../tpl/apiPage/apiPage.html");
	});

	/**
	 * 加载微服务
	 */
	function loadApiMicroservices(){
		showGrid({
			id: "apiMicroservicesId",
			url: reqBasePath + "queryApiMicroservices",
			params: {},
			pagination: false,
			template: $("#apiMicroservicesTemplate").html(),
			method: "GET",
			ajaxSendLoadBefore: function(hdb){},
			ajaxSendAfter:function(j){
				form.render('select');
				// 加载系统信息
				loadDefaultMain();
			}
		});
	}

	form.on('select(apiMicroservicesId)', function (data) {
		var value = data.value;
		if (isNull(value)) {
			$("#modelId").html("");
			$("#appList").html("");
		} else {
			loadListMation(value);
		}
	});

	// 模块
	var model = new Array();
	// 分组
	var groupList = new Array();
	// 接口数据
	var jsonList;
	/**
	 * 加载接口列表
	 */
	function loadListMation(appId){
		AjaxPostUtil.request({url: reqBasePath + "queryAllSysEveReqMapping", params: {appId: appId}, type: 'json', method: "GET", callback: function (json) {
			// 1.模块
			model = loadModel(json);
			$("#modelId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), {rows: model}));
			$("#appList").html(getDataUseHandlebars(modelTemplate, {rows: model}));
			// 2.分组
			groupList = loadBox(json);
			jsonList = json.rows;
			// 默认加载第一个模块的接口数据
			loadPointNumApiList(model[0].id);
			matchingLanguage();
			form.render();
		}});
	}

	/**
	 * 加载指定模块的接口
	 * @param modelId 模块id
	 */
	function loadPointNumApiList(modelId) {
		$("#model" + modelId).html("");
		// 分组信息加入页面
		$.each(groupList, function(i, item) {
			if(item.modelId === modelId){
				$("#model" + item.modelId).append(getDataUseHandlebars(menuBoxTemplate, {bean: item}));
			}
		});
		// 接口信息加入页面
		$.each(jsonList, function(i, item) {
			// 获取当前接口所属模块的下标
			var thisModelIndex = getModelIndex(model, item.modelName);
			if(model[thisModelIndex].id === modelId){
				var groupName = "未分类接口";
				if (!isNull(item.groupName)) {
					groupName = item.groupName;
				}
				var boxIndex = getBoxIndex(groupList, groupName, item.modelName);
				$("#child" + groupList[boxIndex].boxId).append(
					getDataUseHandlebars(fileListTemplate, {bean: item}));
			}
		});
	}

	form.on('select(modelId)', function(data) {
		if(isNull(data.value) || data.value === '请选择') {
			$("#departmentId").html("");
			form.render('select');
		} else {
			$("#appList").find(".layui-nav-tree").hide();
			$("#appList").find("ul[id='model" + data.value + "']").show();
			loadPointNumApiList(data.value);
		}
	});

	// 构造模块数据
	function loadModel(json) {
		var box = [];
		$.each(json.rows, function(i, item) {
			if(getModelIndex(box, item.modelName) == -1) {
				box.push({
					id: getRandomValueToString(),
					name: item.modelName,
					show: ((box.length == 0) ? "block" : "none")
				});
			}
		});
		return box;
	}

	// 根据模块名称获取模块下标
	function getModelIndex(box, str){
		var inIndex = -1;
		$.each(box, function(i, item){
			if(item.name == str){
				inIndex = i;
				return false;
			}
		});
		return inIndex;
	}

	function loadBox(json){
		var box = [];
		// 为每个工程模块都加入'未分类接口'
		$.each(model, function(i, item){
			box.push({
				boxId: getRandomValueToString(),
				modelId: model[i].id,
				boxName: '未分类接口'
			});
		});
		// 获取已分类接口
		$.each(json.rows, function(i, item){
			if (!isNull(item.groupName) && getBoxIndex(box, item.groupName, item.modelName) == -1){
				box.push({
					boxId: getRandomValueToString(),
					modelId: model[getModelIndex(model, item.modelName)].id,
					boxName: item.groupName
				});
			}
		});
		return box;
	}

	/**
	 * 获取分组中的下标
	 * @param box 分组集合
	 * @param groupName 分组名称
	 * @param modelName 模块名称
	 * @returns {number}
	 */
	function getBoxIndex(box, groupName, modelName){
		var inIndex = -1;
		var modelId = model[getModelIndex(model, modelName)].id;
		$.each(box, function(i, item){
			if(item.boxName == groupName && modelId == item.modelId){
				inIndex = i;
				return false;
			}
		});
		return inIndex;
	}

	// 接口列表实体点击
	$("body").on("click", "#appList .api-item", function() {
		if(!$(this).hasClass("active")) {
			$("#appList .api-item").removeClass("active");
			$(this).addClass("active");
			var params = {
				appId: $("#apiMicroservicesId").val(),
				rowId: $(this).attr("rowid")
			};
			AjaxPostUtil.request({url: reqBasePath + "queryApiDetails", params: params, type: 'json', method: "GET", callback: function (json) {
				var str = getDataUseHandlebars(fileMationTemplate, json);
				$("#apiMation").html(str);
				// 转化表格
				active[type] ? active[type].call(this) : '';
				$(".apiLi").show();
				if (json.bean.advancedSearch) {
					$("#advancedSearch").show();
					if (!isNull(json.bean.searchParamsId)) {
						searchParamsId = json.bean.searchParamsId;
						if (isNull(jsonEditor)) {
							$("#jsonContent").val(JSON.stringify(json.bean.searchParams, null, 4));
						} else {
							jsonEditor.setValue(JSON.stringify(json.bean.searchParams, null, 4));
						}
					} else {
						searchParamsId = "";
						jsonEditor.setValue("");
					}
				} else {
					$("#advancedSearch").hide();
				}
				// 切换到接口信息的Tab项
				element.tabChange('docTabBrief', "apiMationTab");
				// 加载接口参数
				$("#apiListParams").find(".layui-tab-title").html("");
				$("#apiListParams").find(".layui-tab-content").html("");
				$.each(json.bean.apiParamsList, function (i, item) {
					item.requestBody = JSON.stringify(item.requestBody);
					item.responseBody = JSON.stringify(item.responseBody);
					addTabPatams({
						rows: [item]
					});
				});
			}});
		}
	});

	var jsonEditor;
	var searchParamsId = ""; // 高级查询的配置页信息id
	element.on('tab(docTabBrief)', function(obj){
		if(obj.index == 3 && isNull(jsonEditor)){
			jsonEditor = CodeMirror.fromTextArea(document.getElementById("jsonContent"), {
				mode : "application/json",  // 模式
				theme : "eclipse",  // CSS样式选择
				indentUnit : 4,  // 缩进单位，默认2
				smartIndent : true,  // 是否智能缩进
				tabSize : 4,  // Tab缩进，默认4
				showCursorWhenSelecting : true,
				// 代码折叠
				lineWrapping: true,
				foldGutter: true,
				lineNumbers : true,  // 是否显示行号
				styleActiveLine: true, //line选择是是否加亮
				matchBrackets: true,
				gutters: [
					"CodeMirror-linenumbers",
					"CodeMirror-foldgutter",
					"CodeMirror-lint-markers",      // CodeMirror-lint-markers是实现语法报错功能
				],
				matchBrackets: true, // 括号匹配显示
				autoCloseBrackets: true, // 输入和退格时成对
			});
		}
	});

	// 保存高级查询参数
	$("body").on("click", "#saveAdvancedSearch", function() {
		var params = {
			appId: $("#apiMicroservicesId").val(),
			urlId: $("#appList .active").attr("rowid"),
			paramsConfig: encodeURIComponent(JSON.stringify(JSON.parse(jsonEditor.getValue()))),
			id: searchParamsId
		};
		AjaxPostUtil.request({url: reqBasePath + "writeSearchConfigMation", params: params, type: 'json', method: "POST", callback: function (json) {
			searchParamsId = json.bean.id;
			winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
		}});
	});

	// 退出
	$("body").on("click", "#exitBtn", function() {
		winui.window.confirm('确认注销吗?', {id: 'exit-confim', icon: 3, title: '提示', skin: 'msg-skin-message', success: function(layero, index) {
			var times = $("#exit-confim").parent().attr("times");
			var zIndex = $("#exit-confim").parent().css("z-index");
			$("#layui-layer-shade" + times).css({'z-index': zIndex});
		}}, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url: reqBasePath + "login003", params: {}, type: 'json', method: "POST", callback: function (json) {
				localStorage.setItem('userToken', "");
				location.href = "../../tpl/index/login.html?url=" + escape("../../tpl/apiPage/apiPage.html");
			}});
		});
	});

	$("body").on("click", ".menu-box-none", function (e) {
		if ($(this).parent().hasClass("layui-nav-itemed")) {
			$(this).parent().removeClass("layui-nav-itemed");
		} else {
			$(this).parent().addClass("layui-nav-itemed");
		}
	});

	// 控制台
	$("body").on("click", "#consoleDesk", function() {
		location.href = "../../tpl/index/index.html";
	});

	function loadDefaultMain() {
		AjaxPostUtil.request({url: reqBasePath + "queryLimitRestrictions", params: {}, type: 'json', method: "GET", callback: function (json) {
			var item = {};
			item.reception = getReceptionLimitMation();
			item.backstage = json.rows;
			$("#contentDesc").html(getDataUseHandlebars(defaultTemplate, {bean: item}));
			$('#chart-container').orgchart({
				'data' : getXMLAttribute(),
				'nodeTitle': 'title',
				'nodeContent': 'name',
				'pan': true,
				'zoom': true,
				'interactive': false
			});
			form.render();
		}});
	}

	// 导出为MD文档
	$("body").on("click", "#exportMD", function() {
		var params = {
			appId: $("#apiMicroservicesId").val(),
			rowId: $(this).attr("apiId")
		};
		var fileName = $("#apiMicroservicesId").find("option:selected").text() + "-" + $(this).attr("fileName");
		AjaxPostUtil.request({url: reqBasePath + "queryApiDetails", params: params, type: 'json', method: "GET", callback: function (json) {
			var str = getDataUseHandlebars(getFileContent('tpl/apiPage/mdModelFile.tpl'), json);
			sysFileUtil.saveAs(new Blob([str]), fileName);
		}});
	});

	var requestBody = {};
	var responseBody = {};
	// 新增出入参示例
	$("body").on("click", "#tabAdd", function() {
		addTabPatams(null);
	});

	function addTabPatams(params) {
		var tabParams = params == null ? {
			rows: [{
				id: "RM" + new Date().getTime(),
				title: '新出入参' + (Math.random() * 1000 | 0)
			}]
		} : params;
		element.tabAdd('apiListParams', {
			title: tabParams.rows[0].title,
			content: getDataUseHandlebars($("#apiParamsListTabContent").html(), tabParams),
			id: tabParams.rows[0].id
		});
		$("#apiListParams").find('.layui-tab-title').find('li').append('<i class="layui-icon layui-unselect layui-tab-close">&#x1006;</i>');

		requestBody[tabParams.rows[0].id] = new JsonEditor('#requestBody' + tabParams.rows[0].id, params == null ? {} : JSON.parse(tabParams.rows[0].requestBody));
		responseBody[tabParams.rows[0].id] = new JsonEditor('#responseBody' + tabParams.rows[0].id, params == null ? {} : JSON.parse(tabParams.rows[0].responseBody));

		form.render();
		saveApiParamsBtn(tabParams.rows[0].id);
		element.tabChange('apiListParams', tabParams.rows[0].id);
	}

	function saveApiParamsBtn(id) {
		form.on('submit(formAddBean' + id + ')', function (data) {
			if (winui.verifyForm(data.elem)) {
				var _this = $(this);
				// formId如果是新增，需要新增成功后替换id
				var formId = _this.parents(".apiParamsForm").attr("id");
				var dataId = formId;
				if(formId.startsWith('RM')){
					dataId = "";
				}
				var params = {
					id: dataId,
					title: $("#title" + id).val(),
					requestUrl: $("#appList .active").attr("rowid"),
					requestBody: JSON.stringify(requestBody[id].get()),
					responseBody: JSON.stringify(responseBody[id].get()),
					appId: $("#apiMicroservicesId").val()
				};
				AjaxPostUtil.request({url: reqBasePath + "writeApiMation", params: params, type: 'json', method: "POST", callback: function(json) {
					winui.window.msg("保存成功", {icon: 1, time: 2000});
					if (formId.startsWith('RM')) {
						_this.parents(".apiParamsForm").attr("id", json.bean.id);
						_this.parents(".layui-tab").eq(0).find('li[lay-id="' + id + '"]').attr("lay-id", json.bean.id);
					}
					_this.parents(".layui-tab").eq(0).find('li[lay-id="' + json.bean.id + '"]').html(json.bean.title + '<i class="layui-icon layui-unselect layui-tab-close">&#x1006;</i>');
				}});
			}
			return false;
		});
	}

	// 请求入参内容变化事件
	$("body").on("input", ".requestBodyText", function (e) {
		var id = $(this).attr("id").replace("requestBodyText", "");
		var value = $(this).val();
		try {
			requestBody[id].load(JSON.parse(value));
		} catch (ex) {}
	});
	// 请求出参内容变化事件
	$("body").on("input", ".responseBodyText", function (e) {
		var id = $(this).attr("id").replace("responseBodyText", "");
		var value = $(this).val();
		try {
			requestBody[id].load(JSON.parse(value));
		} catch (ex) {}
	});
	$("body").on("click", ".layui-tab-close", function (e) {
		var formId = $(this).parents(".layui-tab").eq(0).find('.layui-show').find('form').attr("id");
		element.tabDelete('apiListParams', formId);
		$("#" + formId).parent().remove();
		winui.window.msg("删除成功", {icon: 1, time: 2000});
		if (!formId.startsWith('RM')) {
			AjaxPostUtil.request({url: reqBasePath + "deleteApiMationById", params: {id: formId}, type: 'json', method: "DELETE", callback: function(json) {
			}});
		}
	});

	// 获取前端限制条件
	function getReceptionLimitMation(){
		var list = new Array();
		$.each(winui.verify, function (key, value){
			list.push({
				key: key,
				value: value[2]
			})
		});
		return list;
	}

	function getXMLAttribute(){
		var params = {
			title: '标签controller',
			name: '一个xml中只会有一个controller',
			children: [{
				title: 'url[子集]',
				name: '一个新接口的定义',
				children: [{
					title: 'property[子集]',
					name: '接口中的入参',
					children: [{
						title: '属性id',
						name: '前台请求的入参',
					}, {
						title: '属性name',
						name: '后台接受的参数',
					}, {
						title: '属性ref',
						name: '参数的限制条件，多个用逗号隔开',
					}, {
						title: '属性var',
						name: '参数介绍',
					}]
				}, {
					title: '属性id',
					name: '前端请求的地址串',
				}, {
					title: '属性path',
					name: '后台映射的地址',
				}, {
					title: '属性val',
					name: '接口描述',
				}, {
					title: '属性allUse',
					name: '是否需要登录才能使用 1是 0否 2需要登陆才能访问，但无需授权 默认为否',
				}, {
					title: '属性method',
					name: '接口请求方式',
				}, {
					title: '属性groupName',
					name: '所属功能集',
				}]
			}, {
				title: '属性modelName',
				name: '介绍该模块是做什么功能的'
			}]
		}
		return params;
	}
		
});

