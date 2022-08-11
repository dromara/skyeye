var performIdList = new Array();//执行人返回的集合或者进行回显的集合

// 项目工作量分配
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload', 'laydate', 'tagEditor', 'form'], function(exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate;
	var selOption = getFileContent('tpl/template/select-option.tpl');

	var pId = "";//是否是主任务
	var restWorkload = "";//主任务拆分剩下的工作量
	var ue;

	showGrid({
		id: "showForm",
		url: flowableBasePath + "protask004",
		params: {rowId: parent.rowId},
		pagination: false,
		template: getFileContent('tpl/protask/protaskeditTemplate.tpl'),
		ajaxSendAfter:function (json) {
			pId = json.bean.pId;
			restWorkload = json.bean.restWorkload;

			// 附件回显
			skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});
			$("#startTime").val(json.bean.startTime);
			$("#endTime").val(json.bean.endTime);
			if(json.bean.state == '1'){
				$(".typeTwo").removeClass("layui-hide");
			} else {
				$(".typeOne").removeClass("layui-hide");
			}

			ue = ueEditorUtil.initEditor('taskInstructions');
			ue.addListener("ready", function () {
				ue.setContent(json.bean.taskInstructions);
			});

			//选择开始时间
			var start = laydate.render({
				elem: '#startTime', //指定元素
				range: false,
				btns: ['now', 'confirm'],
				done: function(value, date){
					endMax = end.config.max;
					end.config.min = date;
					end.config.min.month = date.month -1;
				}
			});

			//选择结束时间
			var end = laydate.render({
				elem: '#endTime', //指定元素
				range: false,
				btns: ['now', 'confirm'],
				done: function(value, date){
					if($.trim(value) == ''){
						var curDate = new Date();
						date = {'date': curDate.getDate(), 'month': curDate.getMonth()+1, 'year': curDate.getFullYear()};
					}
					start.config.max = date;
					start.config.max.month = date.month -1;
				}
			});

			// 任务分类
			sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["pmTaskType"]["key"], 'select', "taskType", json.bean.taskType, form);

			// 获取我参与的项目列表
			proUtil.queryMyProjectsList(function (data){
				$("#proId").html(getDataUseHandlebars(selOption, data));
				$("#proId").val(json.bean.proId);
				form.render('select');
			});

			// 获取当前登录用户所属企业的所有部门信息
			systemCommonUtil.queryDepartmentListByCurrentUserBelong(function(data){
				$("#departments").html(getDataUseHandlebars(selOption, data));
				$("#departments").val(json.bean.departments);
				form.render('select');
			});

			var performIdNames = "";
			performIdList = [].concat(json.bean.performId);
			$.each(performIdList, function (i, item) {
				performIdNames += item.name + ',';
			});
			$('#performId').tagEditor({
				initialTags: performIdNames.split(','),
				placeholder: '请选择执行人',
				editorTag: false,
				beforeTagDelete: function(field, editor, tags, val) {
					performIdList = [].concat(arrayUtil.removeArrayPointName(performIdList, val));
				}
			});
			$("body").on("click", "#performIdSelPeople", function(e) {
				systemCommonUtil.userReturnList = [].concat(performIdList);
				systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
				systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
				systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
				systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
					// 重置数据
					performIdList = [].concat(systemCommonUtil.tagEditorResetData('performId', userReturnList));
				});
			});
			matchingLanguage();
			form.render();
		}
	});

	// 保存为草稿
	form.on('submit(formEditBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData('1', "");
		}
		return false;
	});

	// 提交审批
	form.on('submit(formSubBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			activitiUtil.startProcess(sysActivitiModel["proTask"]["key"], function (approvalId) {
				saveData("2", approvalId);
			});
		}
		return false;
	});

	// 工作流中保存
	form.on('submit(subBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData('3', "");
		}
		return false;
	});

	function saveData(subType, approvalId) {
		var params = {
			rowId: parent.rowId,
			taskName: $("#taskName").val(),
			taskType: $("#taskType").val(),
			startTime: $("#startTime").val(),
			endTime: $("#endTime").val(),
			estimatedWorkload: $("#estimatedWorkload").val(),
			taskInstructions: encodeURIComponent(ue.getContent()),
			departments: $("#departments").val(),
			proId: $("#proId").val(),
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 1：保存为草稿  2.提交到工作流  3.在工作流中编辑
			approvalId: approvalId,
			performId: systemCommonUtil.tagEditorGetAllData('performId', performIdList) // 执行人
		};
		//如果是拆分页，且预估工作量大于主任务剩余工作量
		if(pId != 0 && $("#estimatedWorkload").val() > restWorkload){
			winui.window.msg('预估工作量大于主任务剩余工作量，主任务剩余工作量为  <span style="color: blue; font-size:21px">'+ restWorkload + '</span>', {icon: 2, time: 2000});
			return false;
		}
		// 如果执行人为空
		if (isNull(params.performId)) {
			winui.window.msg('请选择执行人', {icon: 2, time: 2000});
			return false;
		}
		if(isNull(ue.getContent())){
			winui.window.msg('请填写任务说明！', {icon: 2, time: 2000});
			return false;
		}
		AjaxPostUtil.request({url: flowableBasePath + "protask005", params: params, type: 'json', callback: function(json) {
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});