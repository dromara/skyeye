
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		textool = layui.textool,
		form = layui.form;
	var selOption = getFileContent('tpl/template/select-option.tpl');
	var className = GetUrlParam("className");
	var appId = GetUrlParam("appId");
	var id = GetUrlParam("id");

	var _html = {
		'color': `<div class="layui-form-item layui-col-xs6">
					<label class="layui-form-label">按钮颜色<i class="red">*</i></label>
					<div class="layui-input-block">
						<select id="color" name="color" lay-filter="color" win-verify="required"></select>
					</div>
				</div>`,
		'operateOpenPage': `<div class="layui-form-item layui-col-xs12">
								<span class="hr-title">新开页面</span><hr>
							</div>
							<div class="layui-form-item layui-col-xs12">
								<label class="layui-form-label">新开页面名称<i class="red">*</i></label>
								<div class="layui-input-block">
									<input type="text" id="openPageName" name="openPageName" win-verify="required" placeholder="请输入新开页面名称" class="layui-input" maxlength="200"/>
								</div>
							</div>
							<div class="layui-form-item layui-col-xs12">
								<label class="layui-form-label">打开方式<i class="red">*</i></label>
								<div class="layui-input-block winui-radio" id="openType">
								</div>
							</div>
							<div class="layui-form-item layui-col-xs12">
								<label class="layui-form-label">页面类型<i class="red">*</i></label>
								<div class="layui-input-block winui-radio">
									<input type="radio" name="type" value="1" title="自定义页面" lay-filter="type" checked/>
									<input type="radio" name="type" value="2" title="表单布局" lay-filter="type" />
									<input type="radio" name="type" value="3" title="报表视图" lay-filter="type" />
								</div>
							</div>
							<div id="typeChangeBox">
							</div>
							<div class="layui-form-item layui-col-xs12">
								<label class="layui-form-label">页面入参</label>
								<div class="layui-input-block" id="pageParams">
									
								</div>
							</div>`,
	};

	var attrHtml = '';
	AjaxPostUtil.request({url: reqBasePath + "queryAttrDefinitionList", params: {className: className, appId: appId}, type: 'json', method: "POST", callback: function (json) {
		attrHtml = getDataUseHandlebars(`<option value="">全部</option>{{#each rows}}<option value="{{attrKey}}">{{name}}</option>{{/each}}`, json);
	}, async: false});

	if (!isNull(id)) {
		AjaxPostUtil.request({url: reqBasePath + "queryOperateById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
			$("#name").val(json.bean.name);
			$("#authPointNum").val(json.bean.authPointNum);
			$("#orderBy").val(json.bean.orderBy);
			$("#remark").val(json.bean.remark);
			textool.init({eleId: 'remark', maxlength: 200});
			skyeyeClassEnumUtil.showEnumDataListByClassName("operatePosition", 'select', "position", json.bean.position, form);
			skyeyeClassEnumUtil.showEnumDataListByClassName("eventType", 'select', "eventType", json.bean.eventType, form);
			if (json.bean.position == 'actionBar') {
				$('#positionChangeBox').html(_html['color']);
				skyeyeClassEnumUtil.showEnumDataListByClassName("buttonColorType", 'select', "color", json.bean.color, form);
			}

			if (json.bean.eventType == 'ajax') {
				var businessApi = json.bean.businessApi;
				$('#eventTypeChangeBox').html(commonHtml['businessApi']);
				$("#serviceStr").html(getDataUseHandlebars(selOption, {rows: serviceMap}));
				$("#serviceStr").val(businessApi.serviceStr);
				$("#api").val(businessApi.api);
				skyeyeClassEnumUtil.showEnumDataListByClassName("httpMethodEnum", 'select', "method", businessApi.method, form);

				loadParamsTable('apiParams');
				initTableChooseUtil.deleteAllRow('apiParams');
				$.each(businessApi.params, function(key, value) {
					var params = {
						"key": key,
						"attrKey": value
					};
					initTableChooseUtil.resetData('apiParams', params);
				});
			} else {
				var operateOpenPage = json.bean.operateOpenPage;
				$('#eventTypeChangeBox').html(_html['operateOpenPage']);
				$('#typeChangeBox').html(commonHtml['customPageUrl']);
				$("#openPageName").val(operateOpenPage.name);
				var type = operateOpenPage.type;
				$("input:radio[name=type][value=" + type + "]").attr("checked", true);
				skyeyeClassEnumUtil.showEnumDataListByClassName("pageOpenType", 'radio', "openType", json.bean.openType, form);
				if (type == 1) {
					$('#typeChangeBox').html(commonHtml['customPageUrl']);
					$("#pageUrl").val(operateOpenPage.pageUrl);
				} else if (type == 2) {
					$('#typeChangeBox').html(commonHtml['dsFormPage']);
					dsFormUtil.dsFormChooseMation = operateOpenPage.dsFormPage;
					var serviceName = operateOpenPage.dsFormPage.serviceBeanCustom.serviceBean.name;
					$("#pageUrl").val(serviceName + '【' + operateOpenPage.dsFormPage.name + '】');
				} else if (type == 3) {
					chooseItemMation = json.bean.reportPage;
					$("#pageUrl").val(getNotUndefinedVal(chooseItemMation?.name));
				}

				loadParamsTable('pageParams');
				initTableChooseUtil.deleteAllRow('pageParams');
				$.each(operateOpenPage.params, function(key, value) {
					var params = {
						"key": key,
						"attrKey": value
					};
					initTableChooseUtil.resetData('pageParams', params);
				});
			}
			dsFormColumnUtil.init({
				id: 'attrSymbolsDesignBox',
				title: '按钮显示条件',
				appId: appId,
				className: className
			}, isNull(json.bean.showConditionList) ? [] : json.bean.showConditionList);
		}, async: false});
	} else {
		dsFormColumnUtil.init({
			id: 'attrSymbolsDesignBox',
			title: '按钮显示条件',
			appId: appId,
			className: className
		});
		textool.init({eleId: 'remark', maxlength: 200});
		skyeyeClassEnumUtil.showEnumDataListByClassName("operatePosition", 'select', "position", '', form);
		skyeyeClassEnumUtil.showEnumDataListByClassName("eventType", 'select', "eventType", '', form);
	}

	form.on('select(position)', function(data) {
		if (data.value == 'actionBar') {
			// 操作栏
			$('#positionChangeBox').html(_html['color']);
			skyeyeClassEnumUtil.showEnumDataListByClassName("buttonColorType", 'select', "color", '', form);
		} else {
			$('#positionChangeBox').html('');
		}
	});

	form.on('select(eventType)', function(data) {
		if (isNull(data.value)) {
			$('#eventTypeChangeBox').html('');
			return false;
		}
		if (data.value == 'ajax') {
			// 请求事件
			$('#eventTypeChangeBox').html(commonHtml['businessApi']);
			$("#serviceStr").html(getDataUseHandlebars(selOption, {rows: serviceMap}));
			skyeyeClassEnumUtil.showEnumDataListByClassName("httpMethodEnum", 'select', "method", '', form);
			loadParamsTable('apiParams');
		} else {
			// 新开页面
			$('#eventTypeChangeBox').html(_html['operateOpenPage']);
			$('#typeChangeBox').html(commonHtml['customPageUrl']);
			skyeyeClassEnumUtil.showEnumDataListByClassName("pageOpenType", 'radio', "openType", '', form);
			loadParamsTable('pageParams');
		}
		form.render();
	});

	form.on('radio(type)', function (data) {
		if (data.value == 1) {
			$('#typeChangeBox').html(commonHtml['customPageUrl']);
		} else if (data.value == 2) {
			$('#typeChangeBox').html(commonHtml['dsFormPage']);
		} else if (data.value == 3) {
			$('#typeChangeBox').html(commonHtml['dsFormReportPage']);
		}
	});

	function loadParamsTable(id) {
		initTableChooseUtil.initTable({
			id: id,
			cols: [
				{id: 'key', title: '入参Key', formType: 'input', width: '150', verify: 'required'},
				{id: 'attrKey', title: '属性', formType: 'select', width: '200', verify: 'required', modelHtml: attrHtml}
			],
			deleteRowCallback: function (trcusid) {
			},
			addRowCallback: function (trcusid) {
			},
			form: form
		});
	}

	matchingLanguage();
	form.render();
	form.on('submit(formWriteBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				className: className,
				appId: appId,
				name: $("#name").val(),
				remark: $("#remark").val(),
				position: $("#position").val(),
				color: isNull($("#color").val()) ? '' : $("#color").val(),
				authPointNum: $("#authPointNum").val(),
				eventType: $("#eventType").val(),
				orderBy: $("#orderBy").val(),
				showConditionList: JSON.stringify(dsFormColumnUtil.tableDataList),
				id: isNull(id) ? '' : id
			};

			if (params.eventType == 'ajax') {
				var dataList = initTableChooseUtil.getDataList('apiParams').dataList;
				var apiParams = {};
				$.each(dataList, function (i, item) {
					apiParams[item.key] = item.attrKey;
				});
				var businessApi = {
					serviceStr: $("#serviceStr").val(),
					api: $("#api").val(),
					method: $("#method").val(),
					params: apiParams
				};
				params.businessApi = JSON.stringify(businessApi);
			} else {
				var dataList = initTableChooseUtil.getDataList('pageParams').dataList;
				var pageParams = {};
				$.each(dataList, function (i, item) {
					pageParams[item.key] = item.attrKey;
				});
				var type = $("input[name='type']:checked").val();
				var operateOpenPage = {
					name: $("#openPageName").val(),
					type: type,
					pageUrl: getPageTypeForUrl(type),
					params: pageParams
				};
				params.operateOpenPage = JSON.stringify(operateOpenPage);
				params.openType = dataShowType.getData('openType');
			}

			AjaxPostUtil.request({url: reqBasePath + "writeOperate", params: params, type: 'json', method: 'POST', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		}
		return false;
	});

	$("body").on("click", ".chooseBtn", function() {
		var pageType = $("input[name='type']:checked").val();
		if (pageType == 2) {
			dsFormUtil.openDsFormPageChoosePage(function (dsFormChoose) {
				var serviceName = dsFormChoose.serviceBeanCustom.serviceBean.name;
				$("#pageUrl").val(serviceName + '【' + dsFormChoose.name + '】');
			});
		} else if (pageType == 3) {
			_openNewWindows({
				url: systemCommonUtil.getUrl('FP2024092900001', null),
				title: "报表视图选择",
				pageId: "dsFormReportPageListChoose",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
					$("#pageUrl").val(chooseItemMation.name);
				}});
		}
	});

	function getPageTypeForUrl(pageType) {
		if (pageType == 1) {
			return $("#pageUrl").val();
		} else if (pageType == 2) {
			return dsFormUtil.dsFormChooseMation.id;
		} else if (pageType == 3) {
			return chooseItemMation.id;
		}
		return '';
	}

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});