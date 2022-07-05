
//工单接收人信息
var serviceUser = {};

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'tagEditor'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    var cooperationUser = new Array();//工单协助人集合
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "sealseservice013",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/stateisstayworker/dispatchedworkerTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter: function (json) {
		 		matchingLanguage();
		 		form.render();
		 	    form.on('submit(dispatchedWorker)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		        			rowId: parent.rowId,
		        			serviceUserId: isNull(serviceUser.userId) ? "" : serviceUser.userId,
							cooperationUserId: systemCommonUtil.tagEditorGetAllData('cooperationUserId', cooperationUser),//工单协助人，可为空
		 	        	};
		 	        	// 接收人
		 	        	if(isNull(params.serviceUserId)){
		                	winui.window.msg('请选择工单接收人', {icon: 2, time: 2000});
		                	return false;
		                }
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "sealseservice014", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
	 		 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
	    
		//工单接收人选择
	    $("body").on("click", "#serviceUserIdSelPeople", function (e) {
			_openNewWindows({
 				url: "../../tpl/serviceworker/serviceworkershowlist.html", 
 				title: "选择接收人",
 				pageId: "serviceworkershowlist",
 				area: ['90vw', '90vh'],
 				callBack: function(refreshCode){
					$("#serviceUserId").val(serviceUser.userName);
 				}});
		});
		
		//工单协助人
	    $('#cooperationUserId').tagEditor({
	        initialTags: [],
	        placeholder: '请选择工单协助人',
	        beforeTagDelete: function(field, editor, tags, val) {
				cooperationUser = [].concat(arrayUtil.removeArrayPointName(cooperationUser, val));
	        }
	    });
		//工单协助人选择
		$("body").on("click", "#cooperationUserIdSelPeople", function (e) {
			systemCommonUtil.userReturnList = [].concat(cooperationUser);
			systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				cooperationUser = [].concat(systemCommonUtil.tagEditorResetData('cooperationUserId', userReturnList));
			});
		});
	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});