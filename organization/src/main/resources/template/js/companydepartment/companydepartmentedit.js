layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form,
		    layedit = layui.layedit;
	    
	    var layContent;
	    
		showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "companydepartment004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/companydepartment/companydepartmenteditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){

				$("#overtimeSettlementType").val(json.bean.overtimeSettlementType);
		 		
		 		layedit.set({
			    	uploadImage: {
			    		url: reqBasePath + "common003", //接口url
		    			type: 'post', //默认post
		    			data: {
		    				type: '13'
		    			}
			    	}
			    });
			    
			    layContent = layedit.build('content', {
			    	tool: [
		    	       'html'
		    	       ,'strong' //加粗
		    	       ,'italic' //斜体
		    	       ,'underline' //下划线
		    	       ,'del' //删除线
		    	       ,'addhr'
		    	       ,'|'
		    	       ,'removeformat'
		    	       ,'fontFomatt'
		    	       ,'fontfamily'
		    	       ,'fontSize'
		    	       ,'colorpicker'
		    	       ,'fontBackColor'
		    	       ,'face' //表情
		    	       ,'|' //分割线
		    	       ,'left' //左对齐
		    	       ,'center' //居中对齐
		    	       ,'right' //右对齐
		    	       ,'link' //超链接
		    	       ,'unlink' //清除链接
		    	       ,'code'
		    	       ,'image' //插入图片
		    	       ,'attachment'
		    	       ,'table'
		    	       ,'|'
		    	       ,'fullScreen'
		    	       ,'preview'
		    	       ,'|'
		    	       ,'help'
		    	     ],
		    	     uploadFiles: {
		    	 		url: reqBasePath + "common003",
		    	 		accept: 'file',
		    	 		acceptMime: 'file/*',
		    	 		size: '20480',
		    	 		data: {
		    				type: '13'
		    			},
		    	 		autoInsert: true, //自动插入编辑器设置
		    	 		done: function(data) {
		    	 		}
		    	 	}
			    });
		 		
			    // 加载公司数据
				systemCommonUtil.getSysCompanyList(function(data){
					// 加载企业数据
					$("#companyId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), data));
					$("#companyId").val(json.bean.companyId);
				});

			    matchingLanguage();
		 		form.render();
		 		form.on('submit(formEditBean)', function (data) {
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
		        			departmentName: $("#departmentName").val(),
		        			departmentDesc: encodeURIComponent(layedit.getContent(layContent)),
		        			companyId: $("#companyId").val(),
							overtimeSettlementType: $("#overtimeSettlementType").val(),
		        			rowId: parent.rowId
			        	};
			        	
			        	AjaxPostUtil.request({url: reqBasePath + "companydepartment005", params: params, type: 'json', method: "PUT", callback: function(json){
			 	   			if (json.returnCode == 0) {
				 	   			parent.layer.close(index);
				 	        	parent.refreshCode = '0';
			 	   			} else {
			 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			 	   			}
			 	   		}});
			        }
			        return false;
			    });
		 	}
	    });
		
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});