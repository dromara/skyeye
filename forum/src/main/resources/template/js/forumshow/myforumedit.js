var tagList = new Array();

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'tagEditor'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form;
	rowId = GetUrlParam("id");
	
	//公共标题
	$("#forumTitle").html(getFileContent("tpl/forumshow/commontitle.tpl"));
	//菜单
	$("body").append(getFileContent("tpl/forumshow/commonmenu.tpl"));
	
	var index = parent.layer.getFrameIndex(window.name);
	
	showGrid({
	 	id: "showForm",
	 	url: reqBasePath + "forumcontent004",
	 	params: {rowId:rowId},
	 	pagination: false,
	 	template: getFileContent('tpl/forumshow/myforumeditTemplate.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 		//是否匿名
	 		hdb.registerHelper("compare4", function(v1, options){
				if(v1 == '2'){
					return 'checked';
				}else if(v1 == '1'){
					return '';
				}else{
					return '';
				}
			});
	 		hdb.registerHelper("compare5", function(v1, options){
				if(v1 == '2'){
					return 'true';
				}else if(v1 == '1'){
					return 'false';
				}else{
					return 'false';
				}
			});
	 	},
	 	ajaxSendAfter:function(json){
	 		
	 		//回显内容
			var ue = ueEditorUtil.initEditor('container');
		    ue.addListener("ready", function () {
		    	ue.setContent(json.bean.content);
		    });
	 		
	 		//贴子标签
	 		var tagNames = [];
            tagList = [].concat(json.bean.tagName);
            $.each(json.bean.tagName, function(i, item){
                tagNames.push(item.name);
            });
			$('#tagId').tagEditor({
		        initialTags: tagNames,
		        placeholder: '请选择标签',
		        editorTag: false,
		        beforeTagDelete: function(field, editor, tags, val) {
		        	var inArray = -1;
			    	$.each(tagList, function(i, item) {
			    		if(val === item.name) {
			    			inArray = i;
			    			return false;
			    		}
			    	});
			    	if(inArray != -1) { //如果该元素在集合中存在
			    		tagList.splice(inArray, 1);
			    	}
		        }
		    });
			
	 		$("input:radio[name=forumType][value=" + json.bean.forumType + "]").attr("checked", true);
	 		if(json.bean.anonymous == "1"){
	 			$("#anonymous").val("false");
	 		}else{
	 			$("#anonymous").val("true");
	 		}
	 		matchingLanguage();
	 		form.render();
	 	    form.on('submit(formEditBean)', function (data) {
	 	        if (winui.verifyForm(data.elem)) {
	 	        	var params = {
 	        			rowId: rowId,
	 	    			title: $("#title").val(),
	 	    			forumType: data.field.forumType
	 	        	};
	 	        	if(tagList.length == 0 || isNull($('#tagId').tagEditor('getTags')[0].tags)){
	 	        		winui.window.msg("请选择标签", {icon: 2,time: 2000});
	 	        		return false;
	 		        }else{
	 		        	var str = "";
	 		        	$.each(tagList, function (i, item) {
	 		        		str += item.id + ',';
	 		            })
	 	    			params.tagId = str;
	 	    		}
	 	        	if($("#anonymous").val() == 'true'){
	 	        		params.anonymous = '2';
	 	        	}else{
	 	        		params.anonymous = '1';
	 	        	}
	 	        	params.content = encodeURIComponent(ue.getContent());
	 	        	if(isNull(params.content)){
	 	        		winui.window.msg("请输入内容", {icon: 2,time: 2000});
	 	        		return false;
	 	        	}
	 	        	params.textConent = encodeURIComponent(ue.getContentTxt());
	 	        	AjaxPostUtil.request({url:reqBasePath + "forumcontent005", params:params, type: 'json', callback: function(json){
	 		   			if(json.returnCode == 0){
	 		   				winui.window.msg("发布成功", {icon: 1, time: 2000}, function(){
	 		   					location.href = '../../tpl/forumshow/myposts.html';
	 		   				});
	 		   			}else{
	 		   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 		   			}
	 	        	}});
	 	        }
	 	        return false;
	 	    });
	 	}
	});
	
	$("body").on("click", "#chooseTag", function(e){
		tagReturnList = [].concat(tagList);
		_openNewWindows({
			url: "../../tpl/forumshow/choosetag.html", 
			title: "标签选择",
			pageId: "choosetagpage",
			area: ['600px', '500px'],
			callBack: function(refreshCode){
				if (refreshCode == '0') {
					//移除所有tag
					var tags = $('#tagId').tagEditor('getTags')[0].tags;
					for (i = 0; i < tags.length; i++) { 
						$('#tagId').tagEditor('removeTag', tags[i]);
					}
					tagList = [].concat(tagReturnList);
				    //添加新的tag
					$.each(tagList, function(i, item){
						$('#tagId').tagEditor('addTag', item.name);
					});
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	});
	
	//是否匿名
	form.on('switch(anonymous)', function (data) {
		//同步开关值
		$(data.elem).val(data.elem.checked);
	});
	
	$("body").on("click", "#cancle", function(){
		location.href = '../../tpl/forumshow/myposts.html';
    });
	
    exports('myforumedit', {});
});
