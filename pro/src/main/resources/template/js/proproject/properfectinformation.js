
// 项目成果与总结
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate;
	    var ue;

	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "proproject015",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter: function (json) {
		 		// 业务需求和目标的附件回显
			    if(json.bean.businessEnclosureInfoList.length != 0 && json.bean.businessEnclosureInfoList != ""){
			    	var str = "";
	    			$.each([].concat(json.bean.businessEnclosureInfoList), function(i, item){
	    				str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
	    			});
	    			$("#businessEnclosureInfoListBox").html(str);
 	        	}
 	        	
 	        	// 项目组织和分工的附件回显
			    if(json.bean.projectEnclosureInfoList.length != 0 && json.bean.projectEnclosureInfoList != ""){
			    	var str = "";
	    			$.each([].concat(json.bean.projectEnclosureInfoList), function(i, item){
	    				str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
	    			});
	    			$("#projectEnclosureInfoListBox").html(str);
 	        	}
 	        	
 	        	// 实施计划和方案的附件回显
			    if(json.bean.planEnclosureInfoList.length != 0 && json.bean.planEnclosureInfoList != ""){
			    	var str = "";
	    			$.each([].concat(json.bean.planEnclosureInfoList), function(i, item){
	    				str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
	    			});
	    			$("#planEnclosureInfoListBox").html(str);
 	        	}
 	        	
 	        	// 项目成果和总结的附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.resultsEnclosureInfoList});

				ue = ueEditorUtil.initEditor('resultsContent');
			    ue.addListener("ready", function () {
			    	if(!isNull(json.bean.resultsContent))
			    		ue.setContent(json.bean.resultsContent);
			    	else
			    		ue.setContent("在此处填写您的[项目成果和总结]");
			    });
			    
			    laydate.render({
					elem: '#actualStartTime',
					value: json.bean.actualStartTime
				});
				
				laydate.render({
					elem: '#actualEndTime',
					value: json.bean.actualEndTime
				});
 	        	
				matchingLanguage();
		 		form.render();
		 		//保存按钮
		 		form.on('submit(formPerfectBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
	 	        			rowId: parent.rowId,
	 	        			actualStartTime: $("#actualStartTime").val(),
	 	        			actualEndTime: $("#actualEndTime").val(),
	 	        			subType: '1',
							resultsEnclosureInfoStr: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
	 	 	        	};
	 	 	        	//获取内容
		 	        	params.resultsContent = encodeURIComponent(ue.getContent());
		 	        	if(isNull(params.resultsContent)){
			        		winui.window.msg("请填写项目成果和总结", {icon: 2, time: 2000});
			        		return false;
			        	}
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "proproject016", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
	 		 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	    
		 	    //完结按钮
		 		form.on('submit(formPerfectEndBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
	 	        			rowId: parent.rowId,
	 	        			actualStartTime: $("#actualStartTime").val(),
	 	        			actualEndTime: $("#actualEndTime").val(),
	 	        			subType: '2',
							resultsEnclosureInfoStr: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
	 	 	        	};
	 	 	        	//获取内容
		 	        	params.resultsContent = encodeURIComponent(ue.getContent());
		 	        	if(isNull(params.resultsContent)){
			        		winui.window.msg("请填写项目成果和总结", {icon: 2, time: 2000});
			        		return false;
			        	}
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "proproject016", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
	 		 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});

	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});