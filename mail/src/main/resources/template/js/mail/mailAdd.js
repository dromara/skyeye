
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate;
	    
	    showGrid({
		 	id: "typeId",
		 	url: reqBasePath + "mailGroup006",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option.tpl'),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function (json) {
		 		form.render('select');
		 	}
	    });
	    
	    //生日
		laydate.render({
			elem: '#birthday', //指定元素
			range: false,
			max: getNowFormatDate()
		});
	    
 		//通讯录类型变化事件
 		form.on('radio(category)', function (data) {
 			var val = data.value;
	    	if(val == '1'){//个人通讯录
	    		$("#readonlyBox").addClass("layui-hide");
	    		$("#typeIdBox").removeClass("layui-hide");
	    	}else if(val == '2'){//公共通讯录
	    		$("#readonlyBox").removeClass("layui-hide");
	    		$("#typeIdBox").addClass("layui-hide");
	    	} else {
	    		winui.window.msg('状态值错误', {icon: 2, time: 2000});
	    	}
        });
 		
        matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
        			name: $("#name").val(),
        			nameCall: $("#nameCall").val(),
        			company: $("#company").val(),
        			department: $("#department").val(),
        			personalPhone: $("#personalPhone").val(),
        			workPhone: $("#workPhone").val(),
        			fax: $("#fax").val(),
        			email: $("#email").val(),
        			otherPhone: $("#otherPhone").val(),
        			otherEmail: $("#otherEmail").val(),
        			workAddress: $("#workAddress").val(),
        			workCode: $("#workCode").val(),
        			personAddress: $("#personAddress").val(),
        			personCode: $("#personCode").val(),
        			companyUrl: $("#companyUrl").val(),
        			personUrl: $("#personUrl").val(),
        			birthday: $("#birthday").val(),
        			remarks: $("#remarks").val(),
        			category: data.field.category,
					wechatNum: $("#wechatNum").val(),
        			readonly: "",
        			typeId: ""
 	        	};
 	        	if(data.field.category == '1'){
 	        		params.typeId = $("#typeId").val();
 	        		if(isNull(params.typeId)){
 	        			winui.window.msg("请选择类别", {icon: 2, time: 2000});
 	 	        		return false;
 	        		}
 	        	}else if(data.field.category == '2'){
 	        		params.readonly = data.field.readonly;
 	        	} else {
 	        		winui.window.msg("状态值错误。", {icon: 2, time: 2000});
 	        		return false;
 	        	}
 	        	AjaxPostUtil.request({url: reqBasePath + "maillist002", params: params, type: 'json', method: "POST", callback: function (json) {
	 	   			if (json.returnCode == 0) {
						winui.window.msg("提交成功", {icon: 1, time: 2000}, function() {
							location.reload();
						});
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
	 	   		}});
 	        }
 	        return false;
 	    });
 	    
 	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	    function getNowFormatDate() {
	        var date = new Date();
	        var seperator1 = "-";
	        var seperator2 = ":";
	        var month = date.getMonth() + 1;
	        var strDate = date.getDate();
	        if (month >= 1 && month <= 9) {
	            month = "0" + month;
	        }
	        if (strDate >= 0 && strDate <= 9) {
	            strDate = "0" + strDate;
	        }
	        var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
	            + " " + date.getHours() + seperator2 + date.getMinutes()
	            + seperator2 + date.getSeconds();
	        return currentdate;
	    }
	});
});