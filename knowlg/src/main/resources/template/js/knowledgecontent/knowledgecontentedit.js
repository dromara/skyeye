
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'eleTree', 'tagEditor'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
			eleTree = layui.eleTree;
	    
	    //初始化类型
		function initType(){
			var el5;
			el5 = eleTree.render({
				elem: '.ele5',
				url: "knowledgetype008?userToken=" + getCookie('userToken') + "&loginPCIp=",
				defaultExpandAll: true,
				expandOnClickNode: false,
				highlightCurrent: true
			});
			$(".ele5").hide();
			$("#typeId").on("click",function (e) {
				e.stopPropagation();
				$(".ele5").toggle();
			});
			eleTree.on("nodeClick(data5)",function(d) {
				$("#typeId").val(d.data.currentData.name);
				$("#typeId").attr("typeId", d.data.currentData.id);
				$(".ele5").hide();
			})
			$(document).on("click",function() {
				$(".ele5").hide();
			})
		}

		showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "knowledgecontent003",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/knowledgecontent/knowledgecontenteditTemplate.tpl'),
		 	ajaxSendAfter:function(json){
				initType();
				$("#typeId").val(json.bean.typeName);
				$("#typeId").attr("typeId", json.bean.typeId);
				var ue = UE.getEditor('container',{
			    	//初始化高度
			    	initialFrameHeight: 700,
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
			    ue.addListener("ready", function () {
			    	ue.setContent(json.bean.content);
			    });

			    var lavel = isNull(json.bean.label) ? [] : json.bean.label.split(',');
				$('#label').tagEditor({
					initialTags: lavel,
					placeholder: '请填写标签'
				});
		 		
			    matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	    	var msg = '确认保存后将重新进行审核，是否确认保存？';
		 			layer.confirm(msg, { icon: 3, title: '保存知识库' }, function (ind) {
		 				layer.close(ind);
		 				if (winui.verifyForm(data.elem)) {
		 					var params = {
	 							rowId: parent.rowId,
	 							title: encodeURIComponent($("#title").val()),
								typeId: isNull($("#typeId").val()) ? "" : $("#typeId").attr("typeId"),
	 							content: encodeURIComponent(ue.getContent()),
								label: $('#label').tagEditor('getTags')[0].tags.toString()
		 					};
							if(isNull(params.typeId)){
								winui.window.msg('请选择所属类型', {icon: 2,time: 2000});
								return false;
							}
		 					if(isNull(ue.getContent())){
		 						winui.window.msg('请填写知识库内容', {icon: 2,time: 2000});
		 						return false;
		 					}else {
		 						if(ue.getContentTxt().length > 200)
		 							params.desc = encodeURI(ue.getContentTxt().substring(0,199));
		 						else
		 							params.desc = encodeURI(ue.getContentTxt());
		 					}
		 					AjaxPostUtil.request({url:reqBasePath + "knowledgecontent004", params:params, type: 'json', callback: function(json){
		 						if(json.returnCode == 0){
		 							parent.layer.close(index);
		 							parent.refreshCode = '0';
		 						}else{
		 							winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		 						}
		 					}});
		 				}
		 			});
		 	        return false;
		 	    });
		 	}
		});
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});