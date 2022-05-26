
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'tagEditor', 'textool'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$,
        	textool = layui.textool;
        
     	var chooseUser = new Array();
     	
     	textool.init({
	    	eleId: 'remark',
	    	maxlength: 200,
	    	tools: ['count', 'copy', 'reset']
	    });
        
	    matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var params = {
                    houseName: $("#houseName").val(),
                    address: $("#address").val(),
                    warehousing: $("#warehousing").val(),
                    truckage: $("#truckage").val(),
                    isDefault: $("input[name='isDefault']:checked").val(),
                    principal: "",
                    remark: $("#remark").val()
                };
                
                //仓库负责人
 	        	if(chooseUser.length > 0 && !isNull($('#principal').tagEditor('getTags')[0].tags)){
 	        		var userId = "";
                    $.each(chooseUser, function (i, item) {
                    	userId += item.id + ',';
                    });
                    params.principal = userId;
                }
                
                AjaxPostUtil.request({url: flowableBasePath + "storehouse002", params: params, type: 'json', callback: function(json){
                    if(json.returnCode == 0) {
                        parent.layer.close(index);
                        parent.refreshCode = '0';
                    }else{
                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                    }
                }});
            }
            return false;
        });
        
        //仓库负责人
	    $('#principal').tagEditor({
	        initialTags: [],
	        placeholder: '请选择仓库负责人',
	        editorTag: false,
	        beforeTagDelete: function(field, editor, tags, val) {
	        	var inArray = -1;
		    	$.each(chooseUser, function(i, item) {
		    		if(val === item.name) {
		    			inArray = i;
		    			return false;
		    		}
		    	});
		    	if(inArray != -1) { //如果该元素在集合中存在
		    		chooseUser.splice(inArray, 1);
		    	}
	        }
	    });
	    
	    // 仓库负责人选择
		$("body").on("click", "#principalUserIdSelPeople", function(e) {
			systemCommonUtil.userReturnList = [].concat(chooseUser);
			systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "1"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (staffChooseList) {
				chooseUser = [].concat(staffChooseList);
				// 重置数据
				systemCommonUtil.tagEditorResetData('principal', chooseUser);
			});
		});

        $("body").on("click", "#cancle", function(){
            parent.layer.close(index);
        });
    });
});