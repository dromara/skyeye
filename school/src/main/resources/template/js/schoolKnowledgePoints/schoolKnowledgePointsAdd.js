
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form;

	    var ue = UE.getEditor('container',{
	    	//初始化高度
	    	initialFrameHeight: 700,
	    	maximumWords: 100000
	    });
	    UE.Editor.prototype._bkGetActionUrl = UE.Editor.prototype.getActionUrl;
	    UE.Editor.prototype.getActionUrl = function(action){
	        if (action == 'uploadimage' || action == 'uploadfile' || action == 'uploadvideo' || action == 'uploadimage'){//上传单个图片,上传附件,上传视频,多图上传
	            return reqBasePath + '/upload/editUploadController/uploadContentPic';
	        } else if(action == 'listimage'){
	        	return reqBasePath + '/upload/editUploadController/downloadContentPic';
	        }else{
	            return this._bkGetActionUrl.call(this, action);
	        }
	    };
	    
	    //初始化学校
		showGrid({
		 	id: "schoolId",
		 	url: reqBasePath + "schoolmation008",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option-must.tpl'),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
				form.render('select');
		 		//加载年级
		 		initGrade();
		 	}
	    });
	    //学校监听事件
		form.on('select(schoolId)', function(data){
			if(isNull(data.value) || data.value === '请选择'){
				$("#schoolId").html("");
				form.render('select');
			}else{
				//加载年级
				initGrade();
			}
		});
		
		//初始化年级
		function initGrade(){
			showGrid({
			 	id: "gradeId",
			 	url: reqBasePath + "grademation006",
			 	params: {schoolId: $("#schoolId").val()},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb){},
			 	ajaxSendAfter:function(json){
			 		form.render('select');
			 	}
		    });
		}
		//年级监听事件
		form.on('select(gradeId)', function(data){
			if(isNull(data.value) || data.value === '请选择'){
				$("#subjectId").html("");
				form.render('select');
			}else{
				//加载科目
				initSubject();
			}
		});
		
		//初始化科目
		function initSubject(){
			showGrid({
			 	id: "subjectId",
			 	url: reqBasePath + "schoolsubjectmation007",
			 	params: {gradeId: $("#gradeId").val()},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb){},
			 	ajaxSendAfter:function(json){
			 		form.render('select');
			 	}
		    });
		}
 		
		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
 	        		title: $("#title").val(),
 	        		content: encodeURIComponent(ue.getContent()),
 	        		schoolId: $("#schoolId").val(),
        			gradeId: $("#gradeId").val(),
        			subjectId: $("#subjectId").val(),
        			typeState: $("input[name='typeState']:checked").val()
 	        	};
    			if(isNull(ue.getContent())){
    				winui.window.msg('请填写内容', {icon: 2,time: 2000});
    				return false;
    			}
    			AjaxPostUtil.request({url:reqBasePath + "knowledgepoints002", params: params, type: 'json', callback: function(json){
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
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});