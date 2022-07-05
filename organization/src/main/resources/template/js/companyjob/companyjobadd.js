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
		var selTemplate = getFileContent('tpl/template/select-option.tpl')
		
		//所属父职位id
		var parentId = "0";
	    
	    layedit.set({
	    	uploadImage: {
	    		url: reqBasePath + "common003", //接口url
    			type: 'post', //默认post
    			data: {
    				type: '13'
    			}
	    	}
	    });
	    
	    var layContent = layedit.build('content', {
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
	    
		systemCommonUtil.getSysCompanyList(function (json) {
			// 加载企业数据
			$("#companyId").html(getDataUseHandlebars(selTemplate, json));
		});
		
		// 公司监听事件
		form.on('select(companyId)', function(data){
			if(isNull(data.value) || data.value === '请选择'){
				$("#departmentId").html("");
				form.render('select');
			} else {
				initDepartment();
			}
		});
		
		// 初始化部门
		function initDepartment(){
			showGrid({
			 	id: "departmentId",
			 	url: reqBasePath + "companydepartment007",
			 	params: {companyId: $("#companyId").val()},
			 	pagination: false,
			 	template: selTemplate,
			 	ajaxSendLoadBefore: function(hdb){},
			 	ajaxSendAfter:function (json) {
			 		form.render('select');
			 	}
		    });
		}
		
		//部门监听事件
		form.on('select(departmentId)', function(data){
			if(isNull(data.value) || data.value === '请选择'){
				$("#pIdBox").html("");
				form.render('select');
			} else {
				parentId = "0";
				$("#pIdBox").html('');
				loadChildJob();
			}
		});
		
		form.on('select(selectParent)', function(data){
			if(data.value != parentId){
				if(isNull(data.value) || data.value == '请选择'){
					layui.$(data.elem).parent('dd').nextAll().remove();
					if(layui.$(data.elem).parent('dd').prev().children('select[class=menuParent]').length > 0){
						parentId = layui.$(data.elem).parent('dd').prev().children('select[class=menuParent]')[0].value;
					} else {
						parentId = "0";
					}
				} else {
					layui.$(data.elem).parent('dd').nextAll().remove();
					parentId = data.value;
					loadChildJob();
				}
			}
		});
		
		//距离左边的左边距基数
		var leftMargin = 20;
		//加载同级菜单
 	    function loadChildJob(){
 	    	var params = {pId: parentId, departmentId: $("#departmentId").val(), rowId: ""};
 	    	AjaxPostUtil.request({url: reqBasePath + "companyjob008", params: params, method: "POST", type: 'json', callback: function (json) {
				var str = '<dd style="margin-left: ' + (leftMargin * $("#pIdBox").children("dd").length) + 'px"><select class="menuParent" lay-filter="selectParent" lay-search=""><option value="">请选择</option>';
				for(var i = 0; i < json.rows.length; i++){
					str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
				}
				str += '</select></dd>';
				$("#pIdBox").append(str);
				form.render('select');
 	   		}});
 	    }
	    
 	    matchingLanguage();
		form.render();
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	var $menu = layui.$('.menuParent');
 	    		var str = "";
 	    		for(var i = 0; i < $menu.length; i++){
 	    			if(!isNull($menu[i].value) && $menu[i].value != '请选择'){
 	    				str += $menu[i].value + ",";
 	    			}
 	    		}
 	    		if(isNull(str)){
 	    			str = "0";
 	    		}
	        	var params = {
        			companyId: $("#companyId").val(),
        			departmentId: $("#departmentId").val(),
        			jobName: $("#jobName").val(),
        			jobDesc: encodeURIComponent(layedit.getContent(layContent)),
        			pId: str
	        	};
	        	
	        	AjaxPostUtil.request({url: reqBasePath + "companyjob002", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
	 	   		}});
	        }
	        return false;
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
	    
});