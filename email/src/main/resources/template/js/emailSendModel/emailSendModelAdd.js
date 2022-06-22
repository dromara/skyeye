
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
	    
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	    	save(data);
 	        return false;
 	    });

 	   	function save(data){
		   	if (winui.verifyForm(data.elem)) {
			   	if(isNull($('#toPeople').tagEditor('getTags')[0].tags)){
				   	winui.window.msg('请填写收件人', {icon: 2,time: 2000});
				   	return false;
			   	}
			   	var params = {
					title: $("#title").val(),
				   	toPeople: $('#toPeople').tagEditor('getTags')[0].tags,
				   	toCc: $('#toCc').tagEditor('getTags')[0].tags,
				   	toBcc: $('#toBcc').tagEditor('getTags')[0].tags
			   	};
			   	AjaxPostUtil.request({url: reqBasePath + "emailsendmodel002", params: params, type: 'json', method: "POST", callback: function(json){
				   	if(json.returnCode == 0){
						parent.layer.close(index);
						parent.refreshCode = '0';
				   	}else{
					   	winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				   	}
			   	}});
		   	}
	   	}
 	    
	    $('#toPeople').tagEditor({
	        initialTags: [],
	        placeholder: '填写完成后直接回车即可'
	    });
	    
	    $('#toCc').tagEditor({
	        initialTags: [],
	        placeholder: '填写完成后直接回车即可'
	    });
	    
	    $('#toBcc').tagEditor({
	        initialTags: [],
	        placeholder: '填写完成后直接回车即可'
	    });
	    
	    // 人员选择
		$("body").on("click", "#toPeopleSelPeople, #toCcSelPeople, #toBccSelPeople", function(e){
			var clickId = $(this).attr("id");
			systemCommonUtil.userReturnList = [];
			systemCommonUtil.chooseOrNotMy = "2"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "1"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList){
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

		$("body").on("click", "#cancle", function(){
			parent.layer.close(index);
		});
	    
	});
});