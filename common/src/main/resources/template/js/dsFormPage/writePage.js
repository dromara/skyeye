
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
	var pageHtml = {
		'simpleTable': `<div class="layui-form-item layui-col-xs6">
							<label class="layui-form-label">是否分页<i class="red">*</i></label>
							<div class="layui-input-block" id="isPage">
								
							</div>
						</div>
						<div class="layui-form-item layui-col-xs6">
							<label class="layui-form-label">搜索框提示语<i class="red">*</i></label>
							<div class="layui-input-block">
								<input type="text" id="searchTips" name="searchTips" win-verify="required" placeholder="请输入提示语" class="layui-input"/>
							</div>
						</div>
						<div class="layui-form-item layui-col-xs6">
							<label class="layui-form-label">是否开启数据权限<i class="red">*</i></label>
							<div class="layui-input-block" id="isDataAuth">
								
							</div>
						</div>`, // 基础表格布局
		'isDataAuth': `<div class="layui-form-item layui-col-xs6" id="dataAuthPointNumBox">
							<label class="layui-form-label">数据权限点编号<i class="red">*</i></label>
							<div class="layui-input-block">
								<input type="text" id="dataAuthPointNum" name="dataAuthPointNum" win-verify="required" placeholder="请输入数据权限点编号" class="layui-input"/>
							</div>
						</div>`,
		'processAttr': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">流程<i class="red">*</i></label>
                            <div class="layui-input-block">
                                <select lay-filter="actFlowId" lay-search="" id="actFlowId" name="actFlowId" win-verify="required"></select>
                            </div>
                        </div>`, // 流程属性布局
		'flowabled': `<div class="layui-form-item layui-col-xs6">
							<label class="layui-form-label">是否开启工作流<i class="red">*</i></label>
							<div class="layui-input-block" id="isFlowable">
								
							</div>
						</div>`, // 工作流
	};

	$("#serviceStr").html(getDataUseHandlebars(selOption, {rows: serviceMap}));

	if (!isNull(parent.rowId)) {
		AjaxPostUtil.request({url: reqBasePath + "dsformpage006", params: {id: parent.rowId}, type: 'json', method: 'GET', callback: function (json) {
			$("#name").val(json.bean.name);
			$("#remark").val(json.bean.remark);
			skyeyeClassEnumUtil.showEnumDataListByClassName("dsFormPageType", 'select', "type", json.bean.type, form);
			$("#type").attr("disabled", true);

			var businessApi = json.bean.businessApi;
			$("#serviceStr").val(businessApi.serviceStr);
			$("#api").val(businessApi.api);
			skyeyeClassEnumUtil.showEnumDataListByClassName("httpMethodEnum", 'select', "method", businessApi.method, form);

			loadOperate(json.bean.operateIdList);
			// 加载其他的dom
			initOtherDom(json.bean.type, json.bean);
		}});
	} else {
		skyeyeClassEnumUtil.showEnumDataListByClassName("dsFormPageType", 'select', "type", '', form);
		skyeyeClassEnumUtil.showEnumDataListByClassName("httpMethodEnum", 'select', "method", '', form);
		loadOperate(null);
	}

	function loadOperate(defaultValue) {
		AjaxPostUtil.request({url: reqBasePath + "queryOperateList", params: {className: parent.objectId}, type: 'json', method: 'POST', callback: function (json) {
			var value = isNull(defaultValue) ? '' : defaultValue.toString();
			dataShowType.showData(json, 'verificationSelect', 'operateIdList', value, form);
		}});
	}

	form.on('select(type)', function(data) {
		initOtherDom(data.value, {});
	});

	function initOtherDom(type, data) {
		if (type == 'simpleTable') {
			$('#otherDom').html(pageHtml[type]);
			skyeyeClassEnumUtil.showEnumDataListByClassName("whetherEnum", 'radio', "isPage", data.isPage, form);
			// 数据权限信息
			skyeyeClassEnumUtil.showEnumDataListByClassName("whetherEnum", 'radio', "isDataAuth", data.isDataAuth, form);
			if (dataShowType.getData("isDataAuth") == 1) {
				$('#otherDom').append(pageHtml['isDataAuth']);
				$('#dataAuthPointNum').val(data.dataAuthPointNum);
			} else {
				$('#dataAuthPointNumBox').remove();
			}

			$("#searchTips").val(data.searchTips);
		} else if (type == 'processAttr') {
			$('#otherDom').html(pageHtml[type]);
			AjaxPostUtil.request({url: flowableBasePath + 'queryActFlowListByClassName', params: {className: parent.objectId}, type: 'json', method: "POST", callback: function (json) {
				$("#actFlowId").html(getDataUseHandlebars(`{{#each rows}}<option value="{{id}}">{{flowName}}</option>{{/each}}`, json));
				if (!isNull(data.actFlowId)) {
					$("#actFlowId").val(data.actFlowId);
					// 禁止更换流程
					$("#actFlowId").attr("disabled", true);
				}
				form.render('select');
			}, async: false});
		} else if (type == 'create' || type == 'edit') {
			AjaxPostUtil.request({url: reqBasePath + "queryServiceBeanCustom", params: {className: parent.objectId}, type: 'json', method: 'GET', callback: function (json) {
				// 判断是否开启了工作流，如果开启了工作流，则将【是否开启工作流】这个选项填充
				if (json.bean.serviceBean.flowable) {
					$('#otherDom').html(pageHtml['flowabled']);
					skyeyeClassEnumUtil.showEnumDataListByClassName("whetherEnum", 'radio', "isFlowable", data.isFlowable + '' != '0' ? 1 : data.isFlowable, form);
				}
			}, async: false});
		} else {
			$('#otherDom').html('');
		}
	}

	form.on('radio(isDataAuthFilter)', function(data) {
		if (dataShowType.getData("isDataAuth") == 1) {
			$('#otherDom').append(pageHtml['isDataAuth']);
		} else {
			$('#dataAuthPointNumBox').remove();
		}
	});

	matchingLanguage();
	form.render();
	form.on('submit(formWriteBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				id: isNull(parent.rowId) ? '' : parent.rowId,
				name: $("#name").val(),
				remark: $("#remark").val(),
				type: $("#type").val(),
				className: parent.objectId,
				isDataAuth: dataShowType.getData("isDataAuth"),
				operateIdList: isNull($('#operateIdList').attr('value')) ? [] : $('#operateIdList').attr('value'),
				dataAuthPointNum: ''
			};

			if (params.type == 'simpleTable') {
				params['isPage'] = dataShowType.getData("isPage");
				params['searchTips'] = $("#searchTips").val();
				if (params.isDataAuth == 1) {
					params.dataAuthPointNum = $('#dataAuthPointNum').val();
				}
			}

			if (params.type == 'processAttr') {
				params['actFlowId'] = $("#actFlowId").val();
			}

			if (params.type == 'create' || params.type == 'edit') {

				params['isFlowable'] = dataShowType.getData("isFlowable");
			}

			var businessApi = {
				serviceStr: $("#serviceStr").val(),
				api: $("#api").val(),
				method: $("#method").val()
			};
			params.businessApi = JSON.stringify(businessApi);
			AjaxPostUtil.request({url: reqBasePath + "writeDsFormPage", params: params, type: 'json', method: "POST", callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		}
		return false;
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});