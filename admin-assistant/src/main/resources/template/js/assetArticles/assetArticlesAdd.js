
var userList = new Array();//选择用户返回的集合或者进行回显的集合

var userReturnList = new Array();//选择用户返回的集合或者进行回显的集合
var chooseOrNotMy = "1";//人员列表中是否包含自己--1.包含；其他参数不包含
var chooseOrNotEmail = "2";//人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
var checkType = "2";//人员选择类型，1.多选；其他。单选

// 用品信息
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload', 'tagEditor'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;

		// 获取已经上线的用品类别列表
		adminAssistantUtil.queryAssetArticlesTypeUpStateList(function (data){
			$("#typeId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), data));
			form.render('select');
		});

		skyeyeEnclosure.init('enclosureUpload');
 		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
        			articlesName: $("#articlesName").val(),
        			typeId: $("#typeId").val(),
        			specifications: $("#specifications").val(),
        			unitOfMeasurement: $("#unitOfMeasurement").val(),
        			initialNum: $("#initialNum").val(),
        			storageArea: $("#storageArea").val(),
        			roomAddDesc: $("#roomAddDesc").val(),
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 	        	};
 	        	if(userList.length == 0 || isNull($('#assetAdmin').tagEditor('getTags')[0].tags)){
 	        		params.assetAdmin = "";
 	        	}else{
        			params.assetAdmin = userList[0].id;
        		}
 	        	AjaxPostUtil.request({url: flowableBasePath + "assetarticles011", params: params, type: 'json', callback: function(json){
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
 	    
 	    // 用品管理人
	    $('#assetAdmin').tagEditor({
	        initialTags: [],
	        placeholder: '请选择用品管理人',
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
	    // 用品管理人选择
		$("body").on("click", "#userNameSelPeople", function(e){
			userReturnList = [].concat(userList);
			_openNewWindows({
				url: "../../tpl/common/sysusersel.html", 
				title: "人员选择",
				pageId: "sysuserselpage",
				area: ['80vw', '80vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						// 移除所有tag
						var tags = $('#assetAdmin').tagEditor('getTags')[0].tags;
						for (i = 0; i < tags.length; i++) { 
							$('#assetAdmin').tagEditor('removeTag', tags[i]);
						}
						userList = [].concat(userReturnList);
					    // 添加新的tag
						$.each(userList, function(i, item){
							$('#assetAdmin').tagEditor('addTag', item.name);
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