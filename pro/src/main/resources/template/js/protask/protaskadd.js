
var performIdList = new Array();//执行人返回的集合或者进行回显的集合

var userReturnList = new Array();//选择用户返回的集合或者进行回显的集合
var chooseOrNotMy = "1";//人员列表中是否包含自己--1.包含；其他参数不包含
var chooseOrNotEmail = "2";//人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
var checkType = "1";//人员选择类型，1.多选；其他。单选

// 项目工作量分配
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'fileUpload', 'laydate', 'tagEditor', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate;
	var selOption = getFileContent('tpl/template/select-option.tpl');

	var ue = UE.getEditor('taskInstructions',{
		//初始化高度
		initialFrameHeight: 400,
		maximumWords: 10000
	});
	UE.Editor.prototype._bkGetActionUrl = UE.Editor.prototype.getActionUrl;
	UE.Editor.prototype.getActionUrl = function(action){
		if (action == 'uploadimage' || action == 'uploadfile' || action == 'uploadvideo' || action == 'uploadimage'){//上传单个图片,上传附件,上传视频,多图上传
			return reqBasePath + '/upload/editUploadController/uploadContentPic?userToken=' + getCookie('userToken');
		} else if(action == 'listimage'){
			return reqBasePath + '/upload/editUploadController/downloadContentPic?userToken=' + getCookie('userToken');
		}else{
			return this._bkGetActionUrl.call(this, action);
		}
	};

	if(parent.isSplitTask == true){
		var parentId = parent.rowId;
	}else{
		var parentId = '0';
	}

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

	taskTypeSelect();

	//所属分类选择
	function taskTypeSelect(){
		showGrid({
			id: "taskType",
			url: flowableBasePath + "protasktype008",
			params: {},
			pagination: false,
			template: getFileContent('tpl/template/select-option.tpl'),
			ajaxSendLoadBefore: function(hdb){
			},
			ajaxSendAfter: function(json){
				form.render('select');
				proIdSelect();
			}
		});
	}

	// 获取我参与的项目列表
	function proIdSelect(){
		proUtil.queryMyProjectsList(function (data){
			$("#proId").html(getDataUseHandlebars(selOption, data));
			form.render('select');
		});
		departmentsSelect();
	}

	//所属部门选择
	function departmentsSelect(){
		showGrid({
			id: "departments",
			url: flowableBasePath + "mycrmcontract006",
			params: {},
			pagination: false,
			template: getFileContent('tpl/template/select-option.tpl'),
			ajaxSendLoadBefore: function(hdb){
			},
			ajaxSendAfter: function(json){
				form.render('select');
			}
		});
	}

	skyeyeEnclosure.init('enclosureUpload');
	matchingLanguage();
	form.render();
	// 保存为草稿
	form.on('submit(formAddBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData("1", "");
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

	function saveData(subType, approvalId){
		var params = {
			taskName: $("#taskName").val(),
			taskType: $("#taskType").val(),
			startTime: $("#startTime").val(),
			endTime: $("#endTime").val(),
			estimatedWorkload: $("#estimatedWorkload").val(),
			taskInstructions: encodeURIComponent(ue.getContent()),
			parentId: parentId,
			departments: $("#departments").val(),
			proId: $("#proId").val(),
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 表单类型 1.保存草稿  2.提交审批
			approvalId: approvalId
		};
		//如果是拆分页，且预估工作量大于主任务剩余工作量
		if(parent.isSplitTask == true && $("#estimatedWorkload").val() > parent.restWorkload){
			winui.window.msg('预估工作量大于主任务剩余工作量，主任务剩余工作量为  <span style="color: blue; font-size:21px">'+ parent.restWorkload + '</span>', {icon: 2,time: 2000});
			return false;
		}
		//如果执行人为空
		if(performIdList.length == 0 || isNull($('#performId').tagEditor('getTags')[0].tags)){
			winui.window.msg('请选择执行人', {icon: 2,time: 2000});
			return false;
		}else{
			var performId = "";
			$.each(performIdList, function (i, item) {
				performId += item.id + ',';
			});
			params.performId = performId;
		}
		if(isNull(ue.getContent())){
			winui.window.msg('请填写任务说明！', {icon: 2,time: 2000});
			return false;
		}
		AjaxPostUtil.request({url: flowableBasePath + "protask002", params: params, type: 'json', callback: function(json){
			if (json.returnCode == 0){
				parent.layer.close(index);
				parent.refreshCode = '0';
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}

	//执行人
	$('#performId').tagEditor({
		initialTags: [],
		placeholder: '请选择执行人',
		editorTag: false,
		beforeTagDelete: function(field, editor, tags, val) {
			var inArray = -1;
			$.each(performIdList, function(i, item) {
				if(val === item.name) {
					inArray = i;
					return false;
				}
			});
			if(inArray != -1) { //如果该元素在集合中存在
				performIdList.splice(inArray, 1);
			}
		}
	});

	//执行人选择
	$("body").on("click", "#performIdSelPeople", function(e){
		userReturnList = [].concat(performIdList);
		_openNewWindows({
			url: "../../tpl/common/sysusersel.html",
			title: "人员选择",
			pageId: "sysuserselpage",
			area: ['80vw', '80vh'],
			callBack: function(refreshCode){
				if (refreshCode == '0') {
					//移除所有tag
					var tags = $('#performId').tagEditor('getTags')[0].tags;
					for (i = 0; i < tags.length; i++) {
						$('#performId').tagEditor('removeTag', tags[i]);
					}
					performIdList = [].concat(userReturnList);
					//添加新的tag
					$.each(performIdList, function(i, item){
						$('#performId').tagEditor('addTag', item.name);
					});
				} else if (refreshCode == '-9999') {
					winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
				}
			}});
	});

	$("body").on("click", "#cancle", function(){
		parent.layer.close(index);
	});
});