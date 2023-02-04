
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;
	var selOption = getFileContent('tpl/template/select-option.tpl');
	
	var _html = {
		'color': `<div class="layui-form-item layui-col-xs6">
					<label class="layui-form-label">按钮颜色<i class="red">*</i></label>
					<div class="layui-input-block">
						<select id="color" name="color" lay-filter="color" win-verify="required"></select>
					</div>
				</div>`,
		'businessApi': `<div class="layui-form-item layui-col-xs12">
							<span class="hr-title">请求事件</span><hr>
						</div>
						<div class="layui-form-item layui-col-xs6">
							<label class="layui-form-label">所属服务<i class="red">*</i></label>
							<div class="layui-input-block">
								<select id="serviceStr" name="serviceStr" lay-filter="serviceStr" win-verify="required"></select>
							</div>
						</div>
						<div class="layui-form-item layui-col-xs6">
							<label class="layui-form-label">接口地址<i class="red">*</i></label>
							<div class="layui-input-block">
								<input type="text" id="api" name="api" placeholder="请输入接口地址" win-verify="required" class="layui-input" maxlength="200"/>
							</div>
						</div>
						<div class="layui-form-item layui-col-xs6">
							<label class="layui-form-label">请求方式<i class="red">*</i></label>
							<div class="layui-input-block">
								<select id="method" name="method" lay-filter="method" win-verify="required"></select>
							</div>
						</div>
						<div class="layui-form-item layui-col-xs12">
							<label class="layui-form-label">接口入参</label>
							<div class="layui-input-block" id="apiParams">
								
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
								<label class="layui-form-label">页面类型<i class="red">*</i></label>
								<div class="layui-input-block winui-radio">
									<input type="radio" name="type" value="1" title="自定义页面" lay-filter="type" checked/>
									<input type="radio" name="type" value="2" title="表单布局" lay-filter="type" />
								</div>
							</div>
							<div id="typeChangeBox">
							</div>
							<div class="layui-form-item layui-col-xs12">
								<label class="layui-form-label">页面入参</label>
								<div class="layui-input-block" id="pageParams">
									
								</div>
							</div>`,
		'customPageUrl': `<div class="layui-form-item layui-col-xs12">
							<label class="layui-form-label">页面地址<i class="red">*</i></label>
							<div class="layui-input-block">
								<input type="text" id="pageUrl" name="pageUrl" placeholder="请输入页面地址" win-verify="required" class="layui-input" maxlength="200"/>
							</div>
						</div>`,
		'dsFormPage': `<div class="layui-form-item layui-col-xs12">
							<label class="layui-form-label">表单布局<i class="red">*</i></label>
							<div class="layui-input-block">
								<input type="text" id="pageUrl" name="pageUrl" placeholder="请选择表单布局" win-verify="required" class="layui-input" readonly="readonly"/>
								<i class="fa fa-plus-circle input-icon chooseBtn" style="top: 12px;"></i>
							</div>
						</div>`,
	};

	var attrHtml = '';
	AjaxPostUtil.request({url: reqBasePath + "queryAttrDefinitionList", params: {className: parent.objectId}, type: 'json', method: "POST", callback: function (json) {
		attrHtml = getDataUseHandlebars(`<option value="">全部</option>{{#each rows}}<option value="{{attrKey}}">{{name}}</option>{{/each}}`, json);
	}, async: false});

	skyeyeClassEnumUtil.showEnumDataListByClassName("operatePosition", 'select', "position", '', form);
	skyeyeClassEnumUtil.showEnumDataListByClassName("eventType", 'select', "eventType", '', form);
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
			$('#eventTypeChangeBox').html(_html['businessApi']);
			$("#serviceStr").html(getDataUseHandlebars(selOption, {rows: serviceMap}));
			skyeyeClassEnumUtil.showEnumDataListByClassName("httpMethodEnum", 'select', "method", '', form);
			loadParamsTable('apiParams');
		} else {
			// 新开页面
			$('#eventTypeChangeBox').html(_html['operateOpenPage']);
			$('#typeChangeBox').html(_html['customPageUrl']);
			loadParamsTable('pageParams');
		}
		form.render();
	});

	form.on('radio(type)', function (data) {
		if (data.value == 1) {
			$('#typeChangeBox').html(_html['customPageUrl']);
		} else {
			$('#typeChangeBox').html(_html['dsFormPage']);
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
				className: parent.objectId,
				name: $("#name").val(),
				position: $("#position").val(),
				color: $("#color").val(),
				authPointNum: $("#authPointNum").val(),
				eventType: $("#eventType").val(),
				orderBy: $("#orderBy").val(),
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
				var dataList = initTableChooseUtil.getDataList('apiParams').dataList;
				var pageParams = {};
				$.each(dataList, function (i, item) {
					pageParams[item.key] = item.attrKey;
				});
				var type = $("input[name='type']:checked").val();
				var operateOpenPage = {
					name: $("#openPageName").val(),
					type: type == 1 ? true : false,
					pageUrl: type == 1 ? $("#pageUrl").val() : dsFormUtil.dsFormChooseMation.id,
					params: pageParams
				};
				params.operateOpenPage = JSON.stringify(operateOpenPage);
			}

			AjaxPostUtil.request({url: reqBasePath + "writeOperate", params: params, type: 'json', method: 'POST', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		}
		return false;
	});

	$("body").on("click", ".chooseBtn", function() {
		dsFormUtil.openDsFormPageChoosePage(function (dsFormChoose) {
			var serviceName = dsFormChoose.serviceBeanCustom.serviceBean.name;
			$("#pageUrl").val(serviceName + '【' + dsFormChoose.name + '】');
		});
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});