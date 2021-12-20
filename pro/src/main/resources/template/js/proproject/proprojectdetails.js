
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "proproject005",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/proproject/proprojectdetailsTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter: function(json){
				AjaxPostUtil.request({url: reqBasePath + "login002", params: {}, type: 'json', callback: function(data) {
					if(data.returnCode == 0) {
						$("#orderDetailTitle").html(data.bean.companyName + '项目详情信息');
					} else {
						winui.window.msg(data.returnMessage, {icon: 2, time: 2000});
					}
				}});
		 		//业务需求和目标的附件回显
			    if(json.bean.businessEnclosureInfoList.length != 0 && json.bean.businessEnclosureInfoList != ""){
			    	var str = "";
	    			$.each([].concat(json.bean.businessEnclosureInfoList), function(i, item){
	    				str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
	    			});
	    			$("#businessEnclosureInfoListBox").html(str);
 	        	}
 	        	
 	        	//项目组织和分工的附件回显
			    if(json.bean.projectEnclosureInfoList.length != 0 && json.bean.projectEnclosureInfoList != ""){
			    	var str = "";
	    			$.each([].concat(json.bean.projectEnclosureInfoList), function(i, item){
	    				str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
	    			});
	    			$("#projectEnclosureInfoListBox").html(str);
 	        	}
 	        	
 	        	//实施计划和方案的附件回显
			    if(json.bean.planEnclosureInfoList.length != 0 && json.bean.planEnclosureInfoList != ""){
			    	var str = "";
	    			$.each([].concat(json.bean.planEnclosureInfoList), function(i, item){
	    				str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
	    			});
	    			$("#planEnclosureInfoListBox").html(str);
 	        	}
 	        	
 	        	//项目成果和总结的附件回显
			    if(json.bean.resultsEnclosureInfoList.length != 0 && json.bean.resultsEnclosureInfoList != ""){
			    	var str = "";
	    			$.each([].concat(json.bean.resultsEnclosureInfoList), function(i, item){
	    				str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
	    			});
	    			$("#resultsEnclosureInfoListBox").html(str);
 	        	}
 	        	matchingLanguage();
		 		form.render();
		 	}
		});
	    
	    $("body").on("click", ".enclosureItem", function(){
	    	download(fileBasePath + $(this).attr("rowpath"), $(this).html());
	    });
	});
});