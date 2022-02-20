
var userReturnList = new Array();//选择用户返回的集合或者进行回显的集合
var chooseOrNotMy = "1";//人员列表中是否包含自己--1.包含；其他参数不包含
var chooseOrNotEmail = "1";//人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
var checkType = "1";//人员选择类型，1.多选；其他。单选

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
                
                AjaxPostUtil.request({url:reqBasePath + "storehouse002", params:params, type:'json', callback:function(json){
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
	    
	    //仓库负责人选择
		$("body").on("click", "#principalUserIdSelPeople", function(e){
			userReturnList = [].concat(chooseUser);
			_openNewWindows({
				url: "../../tpl/common/sysusersel.html", 
				title: "人员选择",
				pageId: "sysuserselpage",
				area: ['80vw', '80vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						//移除所有tag
						var tags = $('#principal').tagEditor('getTags')[0].tags;
						for (i = 0; i < tags.length; i++) { 
							$('#principal').tagEditor('removeTag', tags[i]);
						}
						chooseUser = [].concat(userReturnList);
					    //添加新的tag
						$.each(chooseUser, function(i, item){
							$('#principal').tagEditor('addTag', item.name);
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