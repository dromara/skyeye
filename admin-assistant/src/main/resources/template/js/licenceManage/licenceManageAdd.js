
var userList = new Array();//选择用户返回的集合或者进行回显的集合
var userReturnList = new Array();//选择用户返回的集合或者进行回显的集合
var borrowList = new Array();//选择用户返回的集合或者进行回显的集合

var chooseOrNotMy = "1";//人员列表中是否包含自己--1.包含；其他参数不包含
var chooseOrNotEmail = "2";//人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
var checkType = "2";//人员选择类型，1.多选；其他。单选

// 证照管理
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload', 'tagEditor', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate;

	    // 签发时间
 		laydate.render({ 
 			elem: '#issueTime',
 			type: 'date',
 		  	trigger: 'click'
 		});
 		
 		// 下次年审时间
 		laydate.render({ 
 		  	elem: '#nextAnnualReview',
 		  	type: 'date',
 		  	trigger: 'click'
 		});
 		
 		// 有效期至
 		laydate.render({ 
 		  	elem: '#termOfValidityTime',
 		  	type: 'date',
 		  	trigger: 'click'
 		});
 		
 		form.on('radio(annualReview)', function (data) {
 			var val = data.value;
	    	if(val == '2'){
	    		$("#nextTime").addClass('layui-hide');
	    		$("#nextAnnualReview").val("");
	    	}else if(val == '1'){
	    		$("#nextTime").removeClass('layui-hide');
	    	}else{
	    		winui.window.msg('状态值错误', {icon: 2,time: 2000});
	    	}
        });
 		
 		form.on('radio(termOfValidity)', function (data) {
 			var val = data.value;
	    	if(val == '2'){
	    		$("#termTime").removeClass('layui-hide');
	    	}else if(val == '1'){
	    		$("#termTime").addClass('layui-hide');
	    		$("#termOfValidityTime").val("");
	    	}else{
	    		winui.window.msg('状态值错误', {icon: 2,time: 2000});
	    	}
        });

		skyeyeEnclosure.init('enclosureUpload');
        matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
        			licenceName: $("#licenceName").val(),
        			licenceNum: $("#licenceNum").val(),
        			issuingOrganization: $("#issuingOrganization").val(),
        			issueTime: $("#issueTime").val(),
        			annualReview: data.field.annualReview,
        			nextAnnualReview: $("#nextAnnualReview").val(),
        			termOfValidity: data.field.termOfValidity,
        			termOfValidityTime: $("#termOfValidityTime").val(),
        			roomAddDesc: $("#roomAddDesc").val(),
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 	        	};
 	        	if(userList.length == 0 || isNull($('#licenceAdmin').tagEditor('getTags')[0].tags)){
 	        		params.licenceAdmin = "";
 	        	}else{
        			params.licenceAdmin = userList[0].id;
        		}
 	        	if(borrowList.length == 0 || isNull($('#borrowId').tagEditor('getTags')[0].tags)){
 	        		params.borrowId = "";
 	        	}else{
        			params.borrowId = borrowList[0].id;
        		}
 	        	AjaxPostUtil.request({url: flowableBasePath + "licence002", params:params, type: 'json', callback: function(json){
	 	   			if(json.returnCode == 0){
		 	   			parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	 	   			}else{
	 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 	   			}
	 	   		}});
 	        }
 	        return false;
 	    });

 	    // 管理人
	    $('#licenceAdmin').tagEditor({
	        initialTags: [],
	        placeholder: '请选择管理人',
	        editorTag: false,
	        beforeTagDelete: function(field, editor, tags, val) {
	        	var inArray = -1;
		    	$.each(userList, function(i, item) {
		    		if(val === item.name) {
		    			inArray = i;
		    			return false;
		    		}
		    	});
		    	if(inArray != -1) { //如果该元素在集合中存在
		    		userList.splice(inArray, 1);
		    	}
	        }
	    });
	    // 管理人选择
		$("body").on("click", "#userNameSelPeople", function(e){
			userReturnList = [].concat(userList);
			_openNewWindows({
				url: "../../tpl/common/sysusersel.html", 
				title: "人员选择",
				pageId: "sysuserselpage",
				area: ['80vw', '80vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						//移除所有tag
						var tags = $('#licenceAdmin').tagEditor('getTags')[0].tags;
						for (i = 0; i < tags.length; i++) { 
							$('#licenceAdmin').tagEditor('removeTag', tags[i]);
						}
						userList = [].concat(userReturnList);
					    //添加新的tag
						$.each(userList, function(i, item){
							$('#licenceAdmin').tagEditor('addTag', item.name);
						});
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
	                }
				}});
		});
		
		// 借用人
	    $('#borrowId').tagEditor({
	        initialTags: [],
	        placeholder: '请选择借用人',
	        editorTag: false,
	        beforeTagDelete: function(field, editor, tags, val) {
	        	var inArray = -1;
		    	$.each(borrowList, function(i, item) {
		    		if(val === item.name) {
		    			inArray = i;
		    			return false;
		    		}
		    	});
		    	if(inArray != -1) { //如果该元素在集合中存在
		    		borrowList.splice(inArray, 1);
		    	}
	        }
	    });
	    // 借用人选择
		$("body").on("click", "#borrowNameSelPeople", function(e){
			userReturnList = [].concat(borrowList);
			_openNewWindows({
				url: "../../tpl/common/sysusersel.html", 
				title: "人员选择",
				pageId: "sysuserselpage",
				area: ['80vw', '80vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						//移除所有tag
						var tags = $('#borrowId').tagEditor('getTags')[0].tags;
						for (i = 0; i < tags.length; i++) { 
							$('#borrowId').tagEditor('removeTag', tags[i]);
						}
						borrowList = [].concat(userReturnList);
					    //添加新的tag
						$.each(borrowList, function(i, item){
							$('#borrowId').tagEditor('addTag', item.name);
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
});