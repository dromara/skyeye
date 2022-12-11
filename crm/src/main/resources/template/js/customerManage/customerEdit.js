
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		textool = layui.textool;

	showGrid({
		id: "showForm",
		url: flowableBasePath + "queryCustomerMationById",
		params: {id: parent.rowId},
		pagination: false,
		method: 'GET',
		template: $("#beanTemplate").html(),
		ajaxSendLoadBefore: function(hdb) {},
		ajaxSendAfter: function (json) {
			// 客户分类
			sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["crmCustomerType"]["key"], 'select', "typeId", json.bean.typeId, form);

			// 客户来源
			sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["crmCustomerFrom"]["key"], 'select', "fromId", json.bean.fromId, form);

			// 客户所属行业
			sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["crmCustomerIndustry"]["key"], 'select', "industryId", json.bean.industryId, form);

			// 客户分组
			sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["crmCustomerGroup"]["key"], 'select', "groupId", json.bean.groupId, form);

			teamObjectPermissionUtil.buildTeamTemplate('teamTemplateId', 1, json.bean.teamTemplateId);

			textool.init({eleId: 'remark', maxlength: 200});
			// 附件回显
			skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

			matchingLanguage();
			form.render();
			form.on('submit(formEditBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					var params = {
						id: parent.rowId,
						name: $("#name").val(),
						combine: $("#combine").val(),
						cusUrl: $("#cusUrl").val(),
						country: $("#country").val(),
						city: $("#city").val(),
						detailAddress: $("#detailAddress").val(),
						postalCode: $("#postalCode").val(),
						fax: $("#fax").val(),
						corRepresentative: $("#corRepresentative").val(),
						regCapital: $("#regCapital").val(),
						remark: $("#remark").val(),
						bankAccount: $("#bankAccount").val(),
						accountName: $("#accountName").val(),
						bankName: $("#bankName").val(),
						bankAddress: $("#bankAddress").val(),
						dutyParagraph: $("#dutyParagraph").val(),
						financePhone: $("#financePhone").val(),
						groupId: $("#groupId").val(),
						typeId: $("#typeId").val(),
						fromId: $("#fromId").val(),
						industryId: $("#industryId").val(),
						teamTemplateId: $("#teamTemplateId").val(),
						enclosureInfo: JSON.stringify({enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')})
					};

					AjaxPostUtil.request({url: flowableBasePath + "writeCustomerMation", params: params, type: 'json', method: 'POST', callback: function (json) {
						parent.layer.close(index);
						parent.refreshCode = '0';
					}});
				}
				return false;
			});
		}
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});