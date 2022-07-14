
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
	    	element = layui.element;
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "opportunity003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/crmOpportunity/crmopportunitydetailsTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter: function (json) {
				// 附件回显
				skyeyeEnclosure.showDetails({"enclosureUploadBtn": json.bean.enclosureInfo});

 	        	// 状态
 	        	$("#nowState").html(getStateName(json.bean));
 	        	matchingLanguage();
		 		form.render();
		 	}
		});
		
		function getStateName(d){
			if (d.state == '0'){
        		return "草稿";
        	} else if (d.state == '1'){
        		return "<span class='state-new'>审核中</span>";
        	} else if (d.state == '2'){
        		return "<span class='state-new'>初期沟通</span>";
        	} else if (d.state == '3'){
        		return "<span class='state-new'>方案与报价</span>";
        	} else if (d.state == '4'){
        		return "<span class='state-new'>竞争与投标</span>";
        	} else if (d.state == '5'){
        		return "<span class='state-new'>商务谈判</span>";
        	} else if (d.state == '6'){
        		return "<span class='state-up'>成交</span>";
        	} else if (d.state == '7'){
        		return "<span class='state-down'>丢单</span>";
        	} else if (d.state == '8'){
        		return "<span class='state-down'>搁置</span>";
        	} else if (d.state == '11'){
        		return "<span class='state-up'>审核通过</span>";
        	} else if (d.state == '12'){
        		return "<span class='state-down'>审核不通过</span>";
        	} 
		}
	});
});