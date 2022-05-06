
// 车辆保险
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload', 'tagEditor', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate;
	    var insuranceaddtableTemplate = $('#insuranceaddtableTemplate').html();
	    
		form.on('checkbox(checkboxProperty)', function(data){
 			var rowId = $(this).attr("rowId");
 			var fu = $("#addTable").find("input[rowId=" + rowId + "]").parent();
 			if (data.elem.checked == true){
 				fu.next().find("input").removeAttr('disabled');
 				fu.next().next().find("input").removeAttr('disabled');
 				fu.next().next().next().find("input").removeAttr('disabled');
 				fu.next().find("input").removeClass('layui-disabled');
 				fu.next().next().find("input").removeClass('layui-disabled');
 				fu.next().next().next().find("input").removeClass('layui-disabled');
 			}else{
 				fu.next().find("input").attr('disabled','disabled');
 				fu.next().next().find("input").attr('disabled','disabled');
 				fu.next().next().next().find("input").attr('disabled','disabled');
 				fu.next().find("input").addClass('layui-disabled');
 				fu.next().next().find("input").addClass('layui-disabled');
 				fu.next().next().next().find("input").addClass('layui-disabled');
 				fu.next().find("input").val("");
 				fu.next().next().find("input").val("");
 				fu.next().next().next().find("input").val("");
 				bftotal();
 			}
 		});
 		
		$("body").on("blur", "#addTable input",function(){
 			bftotal();
 		})
 		
 		//计算总的保费
 		function bftotal(){
 			var bftotal = 0;
 			$.each($('input:checkbox:checked'),function(){
            	var rowId = $(this).attr("rowId");//选中的复选框的值
            	var fu = $("#addTable").find("input[rowId=" + rowId + "]").parent();
            	var bf = "";
            	bf = fu.next().find("input").val();//对应的保费
            	if(!isNull(bf)){
            		bftotal = parseFloat(bftotal) + parseFloat(bf);
	            	bftotal = bftotal.toFixed(2); 
            	}
            });
 			$("#insuranceAllPrice").html(bftotal);
 		}
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "insurance004",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/vehicleManageInsurance/vehicleManageInsuranceEditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		// 投保有效期时间
				laydate.render({
					elem: '#validityTime',
					type: 'date',
					range: true,
					trigger: 'click'
				});
				
		 		$("#insuranceAllPrice").html(json.bean.insuranceAllPrice);
		 		AjaxPostUtil.request({url: flowableBasePath + "coverage006", params:{}, type: 'json', callback: function(thisjson){
		    		if(thisjson.returnCode == 0) {
		    			var row = thisjson.rows;
		 		 		for(var i = 0;i < thisjson.total; i++){
		 		 			var params = {
	 		 	        		id: row[i].id,
	 		 	        		name: row[i].name
	 		 	        	};
		 		 			$("#addTable").append(getDataUseHandlebars(insuranceaddtableTemplate, params));
		 		 			form.render('checkbox');
		 		 		}
	 			 		var arr = json.bean.coverageId.split(",");
	 					for(var i = 0; i < arr.length; i++){
	 						var str = arr[i].split("-");
	 						for(var j = 0; j < str.length; j++){
	 							$('input:checkbox[rowId="' + str[0] + '"]').attr("checked", true);
	 							var fu = $("#addTable").find("input[rowId=" + str[0] + "]").parent();
	 		 	            	fu.next().find("input").val(str[1]);//对应的保费
	 		 	            	fu.next().next().find("input").val(str[2]);//对应的保额
	 		 	            	if(str.length > 3){
	 		 	            		fu.next().next().next().find("input").val(str[3]);//对应的备注
	 		 	            	}
	 		 	            	fu.next().find("input").removeAttr('disabled');
	 		 	 				fu.next().next().find("input").removeAttr('disabled');
	 		 	 				fu.next().next().next().find("input").removeAttr('disabled');
	 		 	 				fu.next().find("input").removeClass('layui-disabled');
	 		 	 				fu.next().next().find("input").removeClass('layui-disabled');
	 		 	 				fu.next().next().next().find("input").removeClass('layui-disabled');
	 						}
	 					}
						// 附件回显
						skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

	 	 	        	matchingLanguage();
	 			 		form.render();
	 			 	    form.on('submit(formEditBean)', function (data) {
	 			 	        if (winui.verifyForm(data.elem)) {
	 			 	        	var params = {
 			 	        			rowId: parent.rowId,
 			 	 	        		insuranceCompany: $("#insuranceCompany").val(),
 			 	 	        		insuredTelephone: $("#insuredTelephone").val(),
 			 	 	        		validityStartTime: $("#validityTime").val().split(" - ")[0],
 			 	 	        		validityEndTime: $("#validityTime").val().split(" - ")[1],
 			 	 	        		insuranceAllPrice: $("#insuranceAllPrice").val(),
 			 	 	        		roomAddDesc: $("#roomAddDesc").val(),
 			 	 	        		insuranceAllPrice: $("#insuranceAllPrice").html(),
									enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 			 	 	        	};
	 			 	        	var coverageIds = "";
	 			 	        	var istrue = true;
	 			 	            $.each($('input:checkbox:checked'),function(){
	 			 	            	if(istrue){
	 			 	            		var rowId = $(this).attr("rowId");//选中的复选框的值
	 			 	 	            	var fu = $("#addTable").find("input[rowId=" + rowId + "]").parent();
	 			 	 	            	var bf = fu.next().find("input").val();//对应的保费
	 			 	 	            	var be = fu.next().next().find("input").val();//对应的保额
	 			 	 	            	if(isNull(bf)){
	 			 	 	            		winui.window.msg('请输入选中的险种对应的保费', {icon: 2,time: 2000});
	 			 	            			istrue = false;
	 			 	 	            	}else{
	 			 	 	            		var str = /^0{1}([.]\d{1,2})?$|^[1-9]\d*([.]{1}[0-9]{1,2})?$/;
	 			 	    	 	        	var flag = str.test(bf);
	 			 	    	                if(!flag){
	 			 	    	                	winui.window.msg('保费小数点后最多两位！', {icon: 2,time: 2000});
	 			 	    	                	istrue = false;
	 			 	    	                }else{
	 			 	    	                	if(isNull(be)){
			 			 	 	            		winui.window.msg('请输入选中的险种对应的保额', {icon: 2,time: 2000});
			 			 	 	            		istrue = false;
			 			 	 	            	}else{
			 			 	    	 	        	var flag = str.test(be);
			 			 	    	                if(!flag){
			 			 	    	                	winui.window.msg('保额小数点后最多两位！', {icon: 2,time: 2000});
			 			 	    	                	istrue = false;
			 			 	    	                }
			 			 	 	            	}
	 			 	    	                }
	 			 	 	            	}
	 			 	 	            	var desc = fu.next().next().next().find("input").val();//对应的备注
	 			 	 	            	str = rowId + "," + bf + "," + be + "," + desc;
	 			 	 	            	coverageIds = coverageIds + str + ";";
	 			 	            	}
	 			 	            });
	 			 	            if(istrue){
		 			 	            params.coverageIds = coverageIds;
		 			 	            if(isNull(params.coverageIds)){
		 				        		winui.window.msg('请选择险种', {icon: 2,time: 2000});
		 				        		return false;
		 				        	}
	 				 	            if(params.insuredTelephone != ""){
	 				 	        		var mobile = /^0?1[3|4|5|8][0-9]\d{8}$/,phone = /^0[\d]{2,3}-[\d]{7,8}$/;
	 				 	 	        	var flag = mobile.test(params.insuredTelephone) || phone.test(params.insuredTelephone);
	 				 	                if(!flag){
	 				 	                	winui.window.msg('请输入正确的投保电话', {icon: 2,time: 2000});
	 				 	 	        		return false;
	 				 	                }
	 				 	        	}
	 			 	 	        	AjaxPostUtil.request({url: flowableBasePath + "insurance005", params:params, type: 'json', callback: function(json){
	 			 		 	   			if(json.returnCode == 0){
	 			 			 	   			parent.layer.close(index);
	 			 			 	        	parent.refreshCode = '0';
	 			 		 	   			}else{
	 			 		 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 			 		 	   			}
	 			 		 	   		}});
	 			 	            }
	 			 	        }
	 			 	        return false;
	 			 	    });
		    		}else {
		    			winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		    		}
				  }
			    });
		 	}
		});
		
	    $("body").on("click", ".enclosureItem", function(){
	    	download(fileBasePath + $(this).attr("rowpath"), $(this).html());
	    });
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});