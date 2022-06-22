
var userList = new Array();//选择用户返回的集合或者进行回显的集合

// 会议室信息
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload', 'tagEditor', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	textool = layui.textool;

 		// 初始化上传
 		$("#roomImg").upload({
            "action": reqBasePath + "common003",
            "data-num": "1",
            "data-type": "PNG,JPG,jpeg,gif",
            "uploadType": 6,
            "function": function (_this, data) {
                show("#roomImg", data);
            }
        });
        
        textool.init({
	    	eleId: 'roomAddDesc',
	    	maxlength: 200,
	    	tools: ['count', 'copy', 'reset']
	    });
 		
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
        			roomName: $("#roomName").val(),
        			roomCapacity: $("#roomCapacity").val(),
        			roomPosition: $("#roomPosition").val(),
        			roomEquipment: $("#roomEquipment").val(),
        			roomAddDesc: $("#roomAddDesc").val(),
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 	        	};
 	        	params.roomImg = $("#roomImg").find("input[type='hidden'][name='upload']").attr("oldurl");
 	        	if(isNull(params.roomImg)){
 	        		winui.window.msg('请上传会议室图片', {icon: 2,time: 2000});
 	        		return false;
 	        	}
 	        	if(userList.length == 0 || isNull($('#roomAdmin').tagEditor('getTags')[0].tags)){
 	        		params.roomAdmin = "";
 	        	}else{
        			params.roomAdmin = userList[0].id;
        		}
 	        	AjaxPostUtil.request({url: flowableBasePath + "conferenceroom002", params: params, type: 'json', callback: function(json){
	 	   			if(json.returnCode == 0){
		 	   			parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	 	   			}else{
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
	 	   		}});
 	        }
 	        return false;
 	    });

 	    // 会议室管理人
	    $('#roomAdmin').tagEditor({
	        initialTags: [],
	        placeholder: '请选择会议室管理人',
         	editorTag: false,
	        beforeTagDelete: function(field, editor, tags, val) {
	        	var inArray = -1;
		    	$.each(userList, function(i, item) {
		    		if(val === item.name) {
		    			inArray = i;
		    			return false;
		    		}
		    	});
		    	if(inArray != -1) {
		    		userList.splice(inArray, 1);
		    	}
	        }
	    });
	    
	    // 会议室管理人选择
		$("body").on("click", "#userNameSelPeople", function(e){
			systemCommonUtil.userReturnList = [].concat(userList);
			systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				userList = [].concat(systemCommonUtil.tagEditorResetData('roomAdmin', userReturnList));
			});
		});
 	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});