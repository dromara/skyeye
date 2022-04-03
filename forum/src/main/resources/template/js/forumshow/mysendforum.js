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
	
	//公共标题
	$("#forumTitle").html(getFileContent("tpl/forumshow/commontitle.tpl"));
	//菜单
	$("body").append(getFileContent("tpl/forumshow/commonmenu.tpl"));
	
	var ue = UE.getEditor('container',{
    	//初始化高度
    	initialFrameHeight: 800,
    	maximumWords: 100000
    });
    UE.Editor.prototype._bkGetActionUrl = UE.Editor.prototype.getActionUrl;
    UE.Editor.prototype.getActionUrl = function(action){
        if (action == 'uploadimage' || action == 'uploadfile' || action == 'uploadvideo' || action == 'uploadimage'){//上传单个图片,上传附件,上传视频,多图上传
            return reqBasePath + '/upload/editUploadController/uploadContentPic?userToken=' + getCookie('userToken');
        } else if(action == 'listimage'){
        	return reqBasePath + '/upload/editUploadController/downloadContentPic?userToken=' + getCookie('userToken');
        }else{
            return this._bkGetActionUrl.call(this, action);
        }
    };
	
    $('#tagId').tagEditor({
        initialTags: [],
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
	
	//我的操作
	$("body").on("click", ".suspension-menu-icon", function(e){
		if($(".drop-down-menu").is(':hidden')){
			$(".drop-down-menu").show();
			$(".suspension-menu-icon").removeClass("rotate").addClass("rotate1");
		}else{
			$(".drop-down-menu").hide();
			$(".suspension-menu-icon").removeClass("rotate1").addClass("rotate");
		}
	});
	
	matchingLanguage();
	form.render();
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	var params = {
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
        	AjaxPostUtil.request({url:reqBasePath + "forumcontent002", params:params, type: 'json', callback: function(json){
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
	
    exports('mysendforum', {});
});
