layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form;
	    
	    //基本题型
	    var _basemodel = getFileContent('tpl/dwsurveydesign/dragmodel/basemodel/radioQuModel.tpl')
	    				+ getFileContent('tpl/dwsurveydesign/dragmodel/basemodel/checkboxQuModel.tpl')
	    				+ getFileContent('tpl/dwsurveydesign/dragmodel/basemodel/fillblankQuModel.tpl')
	    				+ getFileContent('tpl/dwsurveydesign/dragmodel/basemodel/scoreQuModel.tpl')
	    				+ getFileContent('tpl/dwsurveydesign/dragmodel/basemodel/orderQuModel.tpl')
	    				+ getFileContent('tpl/dwsurveydesign/dragmodel/basemodel/mfillblankQuModel.tpl');
	    
	    //矩阵题型
	    var _rectanglemodel = getFileContent('tpl/dwsurveydesign/dragmodel/rectanglemodel/chenRadioQuModel.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/rectanglemodel/chenCheckboxQuModel.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/rectanglemodel/chenScoreQuModel.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/rectanglemodel/chenFillblankQuModel.tpl');
	    
	    //辅助组件
	    var _auxiliarymodel = getFileContent('tpl/dwsurveydesign/dragmodel/auxiliarymodel/pageQuModel.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/auxiliarymodel/paragraphQuModel.tpl');
	    
	    //操作
	    var _operationmodel = getFileContent('tpl/dwsurveydesign/dragmodel/operationmodel/surveyAttrSetToolbar.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/operationmodel/surveyStyleEditToolbar.tpl');
	    
	    //常用题型
	    var _commonlyusedmodel = getFileContent('tpl/dwsurveydesign/dragmodel/commonlyusedmodel/userNameQuModel.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/commonlyusedmodel/phoneNoQuModel.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/commonlyusedmodel/addressQuModel.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/commonlyusedmodel/birthdayQuModel.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/commonlyusedmodel/emailQuModel.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/commonlyusedmodel/genderQuModel.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/commonlyusedmodel/educationQuModel.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/commonlyusedmodel/cityQuModel.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/commonlyusedmodel/maritalQuModel.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/commonlyusedmodel/companyQuModel.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/commonlyusedmodel/salaryQuModel.tpl')
						+ getFileContent('tpl/dwsurveydesign/dragmodel/commonlyusedmodel/dateQuModel.tpl');
	    
	    //各种模板
	    var _varioustemplates = getFileContent('tpl/dwsurveydesign/varioustemplates.tpl');
	    
	    $("#_basemodel").html(_basemodel);
	    $("#_rectanglemodel").html(_rectanglemodel);
	    $("#_auxiliarymodel").html(_auxiliarymodel);
	    $("#_operationmodel").html(_operationmodel);
	    $("#_commonlyusedmodel").html(_commonlyusedmodel);
	    $("body").append(_varioustemplates);
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "dwsurveydirectory003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/dwsurveydesign/dwsurveydesignbean.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		
		 		
		 		form.render();
			    form.on('submit(formAddBean)', function (data) {
			    	//表单验证
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
		        			surveyName: $("#surveyName").val(),
			        	};
//			        	AjaxPostUtil.request({url:reqBasePath + "dwsurveydirectory002", params:params, type:'json', callback:function(json){
//			 	   			if(json.returnCode == 0){
//				 	   			parent.layer.close(index);
//				 	        	parent.refreshCode = '0';
//			 	   			}else{
//			 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
//			 	   			}
//			 	   		}});
			        }
			        return false;
			    });
		 	}
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	    //选项卡切换
	    $("body").on("click", "#tabType li", function(){
	    	$("#tabType li").removeClass("current");
	    	$(this).addClass("current");
	    	$(".tools_tab_div").css('display','none');
	    	$("#" + $(this).attr("id").replace("_li", "")).css('display','block');
	    });
	    
	});
	    
});