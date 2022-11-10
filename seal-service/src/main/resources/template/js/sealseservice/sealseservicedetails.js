
var rowId = "";//情况反馈id/客户id/商品id
var serviceId = "";//工单id

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'jqprint'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    serviceId = parent.rowId;
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "sealseservice010",
		 	params: {rowId: serviceId},
		 	pagination: false,
		 	template: getFileContent('tpl/sealseservice/sealseservicedetailsTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb) {
		 	},
		 	ajaxSendAfter: function (json) {
				// 获取当前登录员工信息
				systemCommonUtil.getSysCurrentLoginUserMation(function (data) {
					$("#orderDetailTitle").html(data.bean.companyName + '售后工单');
				});
		 		//工单协助人
		 		if (!isNull(json.bean.cooperationUserId) && json.bean.cooperationUserId.length > 0){
			    	var str = "";
	    			$.each(json.bean.cooperationUserId, function(i, item) {
	    				str += item.name + ',';
	    			});
	    			$("#cooperationUserId").html(str);
 	        	}
		 		
		 		//相关照片回显
		 		if (!isNull(json.bean.sheetPicture) && json.bean.sheetPicture.length > 0){
			    	var str = "";
	    			$.each(json.bean.sheetPicture.split(','), function(i, item) {
	    				if (!isNull(item)){
		    				str += '<img src="' + item + '" class="photo-img pictureItem">';
	    				}
	    			});
	    			$("#sheetPicture").html(str);
 	        	}

 	        	//完工拍照回显
		 		if (!isNull(json.bean.comPic) && json.bean.comPic.length > 0){
			    	var str = "";
	    			$.each(json.bean.comPic.split(','), function(i, item) {
	    				if (!isNull(item)){
	    					str += '<img src="' + item + '" class="photo-img pictureItem">';
	    				}
	    			});
	    			$("#comPic").html(str);
 	        	}

				// 附件回显
				skyeyeEnclosure.showDetails({
					"enclosureUpload": json.bean.enclosureInfo, // 相关附件回显
					"comEnclosureInfo": json.bean.comEnclosureInfo // 完工附件回显
				});
 	        	
 	        	//客户详情
 	        	if (!isNull(json.bean.customerId)){
 	        		$("#customerName").html('<a class="customerNameMation notice-title-click" rowid="' + json.bean.customerId + '">' + $("#customerName").html() + '</a>');
 	        	}
 	        	
 	        	//商品详情
 	        	if (!isNull(json.bean.productId)){
 	        		$("#productName").html('<a class="productNameMation notice-title-click" rowid="' + json.bean.productId + '">' + $("#productName").html() + '</a>');
 	        	}
 	        	
 	        	//故障关键组件详情
 	        	if (!isNull(json.bean.faultKeyPartsId)){
 	        		$("#faultKeyPartsName").html('<a class="faultKeyPartsNameMation notice-title-click" rowid="' + json.bean.faultKeyPartsId + '">' + $("#faultKeyPartsName").html() + '</a>');
 	        	}
 	        	
 	        	if(json.bean.state == 7){
 	        		$(".stateName").html('已结算');
 	        	} else {
 	        		$(".stateName").html('待结算');
 	        	}
 	        	
 	        	matchingLanguage();
		 		form.render();
		 	}
		});
	    
		// 客户详情
		$("body").on("click", ".customerNameMation", function() {
	    	rowId = $(this).attr("rowid");
			_openNewWindows({
				url: "../../tpl/customerManage/customerDetails.html",
				title: "客户详情",
				pageId: "customerDetails",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
				}});
	    });
	    
	    // 商品详情
		$("body").on("click", ".productNameMation", function() {
	    	rowId = $(this).attr("rowid");
			_openNewWindows({
				url: "../../tpl/material/materialDetails.html",
				title: "商品详情",
				pageId: "materialDetails",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
				}});
	    });
	    
	    // 故障关键组件详情
		$("body").on("click", ".faultKeyPartsNameMation", function() {
	    	rowId = $(this).attr("rowid");
			_openNewWindows({
				url: "../../tpl/material/materialDetails.html",
				title: "组件详情",
				pageId: "materialDetails",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
				}});
	    });
		
	    // 图片查看
	    $("body").on("click", ".pictureItem", function() {
			systemCommonUtil.showPicImg($(this).attr("src"));
	    });

		// 打印
		$("body").on("click", "#jprint", function (e) {
			$("#showForm").jqprint({
				title: '售后工单',
				debug: false, //如果是true则可以显示iframe查看效果（iframe默认高和宽都很小，可以再源码中调大），默认是false
				importCSS: true, //true表示引进原来的页面的css，默认是true。（如果是true，先会找$("link[media=print]")，若没有会去找$("link")中的css文件）
				printContainer: true, //表示如果原来选择的对象必须被纳入打印（注意：设置为false可能会打破你的CSS规则）。
				operaSupport: true//表示如果插件也必须支持歌opera浏览器，在这种情况下，它提供了建立一个临时的打印选项卡。默认是true
			});
		});
	    
	    // 情况反馈详情
	    $("body").on("click", ".details", function() {
	    	rowId = $(this).attr("rowid");
	    	_openNewWindows({
				url: "../../tpl/feedback/feedbackdetails.html", 
				title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
				pageId: "feedbackdetails",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
				}});
	    });
	    
	});
});