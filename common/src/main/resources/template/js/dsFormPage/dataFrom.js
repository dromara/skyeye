
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;
	var selOption = getFileContent('tpl/template/select-option.tpl');

	// 不同的数据来源对应不同的html
	var dataTypeObject = {
		"1": `<div class="layui-form-item layui-col-xs12">
                <label class="layui-form-label">默认数据<i class="red">*</i></label>
                <div class="layui-input-block">
                    <textarea id="defaultData" name="defaultData" win-verify="required" class="layui-textarea"></textarea>
                    <div class="layui-form-mid layui-word-aux">数据样式为：[{"id":"1","name":"男",...},{"id":"2","name":"女",...}]</div>
                </div>
            </div>`,
		"2": `<div class="layui-form-item layui-col-xs12">
                <label class="layui-form-label">枚举数据<i class="red">*</i></label>
                <div class="layui-input-block">
                    <select id="objectId" name="objectId" lay-search="" win-verify="required" lay-filter="objectId"></select>
                </div>
            </div>`,
		"3": `<div class="layui-form-item layui-col-xs12">
                <label class="layui-form-label">数据字典<i class="red">*</i></label>
                <div class="layui-input-block">
                    <select id="objectId" name="objectId" lay-search="" win-verify="required" lay-filter="objectId"></select>
                </div>
            </div>`
	};

	var id = GetUrlParam('id');
	var dataFrom = parent.$(`#${id}`).attr('data');debugger
	dataFrom = isNull(dataFrom) ? {} : JSON.parse(dataFrom);

	var value;
	if (dataFrom.dataType == 4) {
		if (typeof dataFrom.businessApi == 'string') {
			value = JSON.parse(dataFrom.businessApi);
		} else {
			value = dataFrom.businessApi;
		}
	} else {
		value = dataFrom.dataType == 1 ? dataFrom.defaultData : dataFrom.objectId;
	}
	loadLinkData(dataFrom.componentId, dataFrom.dataType, value);

	matchingLanguage();
	form.render();
	form.on('submit(formEditBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				dataType: $("#dataType").val()
			};

			if (params.dataType == 1) {
				// 自定义json
				var defaultDataStr = $("#defaultData").val();
				if (isNull(defaultDataStr)) {
					winui.window.msg("请填写Json串！", {icon: 2, time: 2000});
					return false;
				} else {
					if (isJSON(defaultDataStr)) {
						var defaultKey = getOutKey(defaultDataStr);//取出json串的键
						// 获取数据展示模板
						var tplContentVal = strMatchAllByTwo(dsFormComponent.htmlDataFrom, '{{', '}}');//取出数据模板中用{{}}包裹的词
						removeByValue(tplContentVal, "#each this");
						removeByValue(tplContentVal, "/each");
						if (subset(tplContentVal, defaultKey)) {
							params.defaultData = defaultDataStr;
						} else {
							winui.window.msg('json串内容有误，请重新填写!', {icon: 2, time: 2000});
							return false;
						}
					} else {
						winui.window.msg('json串格式不正确，请重新填写!', {icon: 2, time: 2000});
						return false;
					}
				}
			} else if (params.dataType == 4) {
				// 自定义接口
				var dataList = initTableChooseUtil.getDataList('apiParams').dataList;
				var apiParams = {};
				$.each(dataList, function (i, item) {
					apiParams[item.key] = item.value;
				});
				var businessApi = {
					serviceStr: $("#serviceStr").val(),
					api: $("#api").val(),
					method: $("#method").val(),
					params: apiParams
				};
				params.businessApi = JSON.stringify(businessApi);
			} else {
				params.objectId = $("#objectId").val();
			}

			parent.$(`#${id}`).attr('data', JSON.stringify(params));
			parent.$(`#${id}`).html('更换数据源');
			parent.layer.close(index);
			parent.refreshCode = '0';
		}
		return false;
	});

	function loadLinkData(componentId, dataType, value) {
		// 数据来源类型
		skyeyeClassEnumUtil.showEnumDataListByClassName("attrKeyDataType", 'select', "dataType", dataType, form);
		form.on('select(dataType)', function(data) {
			loadDataMation($('#dataType').val(), '');
		});
		loadDataMation(dataType, value);
	}

	function loadDataMation(dataType, value) {
		$("#dataTypeObjectBox").html(dataTypeObject[dataType]);
		if (dataType == 1) {
			// 自定义
			$("#defaultData").val(value);
		} else if (dataType == 2) {
			// 枚举
			initEnumData();
			$("#objectId").val(value);
		} else if (dataType == 3) {
			// 数据字典
			initDictData();
			$("#objectId").val(value);
		} else if (dataType == 4) {
			// 自定义接口
			$("#dataTypeObjectBox").html(commonHtml['businessApi']);
			$("#serviceStr").html(getDataUseHandlebars(selOption, {rows: serviceMap}));
			loadParamsTable('apiParams');
			if (!isNull(value)) {
				$("#serviceStr").val(value.serviceStr);
				$("#api").val(value.api);
				skyeyeClassEnumUtil.showEnumDataListByClassName("httpMethodEnum", 'select', "method", value.method, form);

				initTableChooseUtil.deleteAllRow('apiParams');
				$.each(value.params, function(key, value) {
					var params = {
						"key": key,
						"value": value
					};
					initTableChooseUtil.resetData('apiParams', params);
				});
			} else {
				skyeyeClassEnumUtil.showEnumDataListByClassName("httpMethodEnum", 'select', "method", '', form);
			}
		} else {
			$("#dataTypeObjectBox").html('');
		}
		form.render('select');
	}

	function loadParamsTable(id) {
		initTableChooseUtil.initTable({
			id: id,
			cols: [
				{id: 'key', title: '入参Key', formType: 'input', width: '150', verify: 'required'},
				{id: 'value', title: '值(目前仅支持objectId和objectKey的传值)', formType: 'input', width: '150', verify: 'required'}
			],
			deleteRowCallback: function (trcusid) {
			},
			addRowCallback: function (trcusid) {
			},
			form: form
		});
	}

	/**
	 * 加载枚举类可选列表
	 */
	function initEnumData() {
		var arr = [];
		$.each(skyeyeClassEnum, function (key, value) {
			arr.push({
				id: key,
				name: value.name
			})
		});
		$("#objectId").html(getDataUseHandlebars(selOption, {rows: arr}));
		form.render('select');
	}

	/**
	 * 加载数据字典可选列表
	 */
	function initDictData() {
		AjaxPostUtil.request({url: reqBasePath + "queryDictTypeListByEnabled", params: {enabled: 1}, type: 'json', method: 'GET', callback: function (json) {
			$("#objectId").html(getDataUseHandlebars(`<option value="">请选择</option>{{#each rows}}<option value="{{dictCode}}">{{name}}</option>{{/each}}`, json));
		}, async: false});
	}

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});

});