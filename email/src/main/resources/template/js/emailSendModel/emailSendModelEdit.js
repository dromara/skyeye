
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'tagEditor'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "emailsendmodel003",
		 	params: {id: parent.rowId},
		 	pagination: false,
			method: "GET",
		 	template: $("#showTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter: function(json){

				$('#toPeople').tagEditor({
					initialTags: json.bean.toPeople.split(','),
					placeholder: '填写完成后直接回车即可'
				});

				$('#toCc').tagEditor({
					initialTags: json.bean.toCc.split(','),
					placeholder: '填写完成后直接回车即可'
				});

				$('#toBcc').tagEditor({
					initialTags: json.bean.toBcc.split(','),
					placeholder: '填写完成后直接回车即可'
				});

		 		matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
					if (winui.verifyForm(data.elem)) {
						if(isNull($('#toPeople').tagEditor('getTags')[0].tags)){
							winui.window.msg('请填写收件人', {icon: 2, time: 2000});
							return false;
						}
						var params = {
							title: $("#title").val(),
							toPeople: $('#toPeople').tagEditor('getTags')[0].tags,
							toCc: $('#toCc').tagEditor('getTags')[0].tags,
							toBcc: $('#toBcc').tagEditor('getTags')[0].tags,
							id: parent.rowId
						};
						AjaxPostUtil.request({url: reqBasePath + "emailsendmodel005", params: params, type: 'json', method: "PUT", callback: function(json){
							if (json.returnCode == 0) {
								parent.layer.close(index);
								parent.refreshCode = '0';
							}else{
								winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
							}
						}});
					}
		 	        return false;
		 	    });

				// 人员选择
				$("body").on("click", "#toPeopleSelPeople, #toCcSelPeople, #toBccSelPeople", function(e){
					userReturnList = [];
					var clickId = $(this).attr("id");
					systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
						// 添加新的tag
						$.each(userReturnList, function(i, item){
							if(clickId == 'toPeopleSelPeople'){
								$('#toPeople').tagEditor('addTag', item.email);
							}else if(clickId == 'toCcSelPeople'){
								$('#toCc').tagEditor('addTag', item.email);
							}else if(clickId == 'toBccSelPeople'){
								$('#toBcc').tagEditor('addTag', item.email);
							}
						});
					});
				});

				$("body").on("click", "#toPeopleSelMail, #toCcSelMail, #toBccSelMail", function(e){
					var clickId = $(this).attr("id");
					mailUtil.openMailChoosePage(function (mailChooseList){
						$.each(mailChooseList, function(i, item){
							if(clickId == 'toPeopleSelMail'){
								$('#toPeople').tagEditor('addTag', item.email);
							}else if(clickId == 'toCcSelMail'){
								$('#toCc').tagEditor('addTag', item.email);
							}else if(clickId == 'toBccSelMail'){
								$('#toBcc').tagEditor('addTag', item.email);
							}
						});
					});
				});

		 	}
		});

	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});