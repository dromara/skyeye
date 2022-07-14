
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
	    var insuranceaddtableTemplate = $('#insuranceaddtableTemplate').html();
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "insurance006",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/vehicleManageInsurance/vehicleManageInsuranceDetailsTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function (json) {
		 		var coveragestr = "";
		 		var arr = json.bean.coverageId.split(",");
					for(var i = 0; i < arr.length; i++){
						var str = arr[i].split("-");
						for(var j = 0; j < str.length; j++){
							coveragestr = coveragestr + str[0] + ",";
						}
					}
		 		//险种
		 		showGrid({
		 		 	id: "coverageChoose",
		 		 	url: flowableBasePath + "coverage006",
		 		 	params: {},
		 		 	pagination: false,
		 		 	template: getFileContent('tpl/template/checkbox-property.tpl'),
		 		 	ajaxSendLoadBefore: function(hdb){
		 		 	},
		 		 	ajaxSendAfter:function(thisjson){
		 		 		if(!isNull(json.bean.coverageId)){
		 		 			var arr = json.bean.coverageId.split(",");
							for(var i = 0; i < arr.length; i++){
								$('input:checkbox[rowId="' + arr[i] + '"]').attr("checked", true);
							}
		 		 		}
		 		 		form.render('checkbox');
		 		 	}
		 		});

				// 附件回显
				skyeyeEnclosure.showDetails({"enclosureUploadBtn": json.bean.enclosureInfo});

		        matchingLanguage();
			    AjaxPostUtil.request({url: flowableBasePath + "coverage006", params:{}, type: 'json', callback: function(thisjson){
					var row = thisjson.rows;
					var coveragearr = coveragestr.split(",");
					for(var i = 0;i < thisjson.total; i++){
						var params = {
							id: row[i].id,
							name: row[i].name
						};
						var f = false;
						for(var m = 0;m < coveragearr.length; m++){
							if(row[i].id == coveragearr[m]){
								f = true;
							}
						}
						if(f){
							$("#addTable").append(getDataUseHandlebars(insuranceaddtableTemplate, params));
						}
						form.render('checkbox');
					}
					var arr = json.bean.coverageId.split(",");
					for(var i = 0; i < arr.length; i++){
						var str = arr[i].split("-");
						for(var j = 0; j < str.length; j++){
							var fu = $("#addTable").find("span[rowId="+str[0]+"]").parent();
							fu.next().find("span").html(str[1]);//对应的保费
							fu.next().next().find("span").html(str[2]);//对应的保额
							if(str.length > 3){
								fu.next().next().next().find("span").html(str[3]);//对应的备注
							}
						}
					}
			     }
		      });
		 	}
		});

	});
});