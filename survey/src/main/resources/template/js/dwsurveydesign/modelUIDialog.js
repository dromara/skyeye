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
	    	form = layui.form;
	    
		var thDialogObj = parent.quOptionDesign;
		var thObjClass = thDialogObj.attr("class");
		
		$(".layui-form").hide();
		
		if(thObjClass.indexOf("dwFbMenuBtn") >= 0) {
			$("#showForm1").show();
			var quItemBody = $(thDialogObj).parents(".surveyQuItemBody");
			var checkType_val = quItemBody.find("input[name='checkType']").val();
			var answerInputWidth_val = quItemBody.find("input[name='answerInputWidth']").val();
			var answerInputRow_val = quItemBody.find("input[name='answerInputRow']").val();
			if(checkType_val == "") {
				checkType_val = "NO";
			}
			var checkType = $("select[name='quFill_checkType']");
			checkType.val(checkType_val);
			var qu_inputWidth = $("input[name='qu_inputWidth']");
			var qu_inputRow = $("input[name='qu_inputRow']");
			if(answerInputWidth_val == "") {
				answerInputWidth_val = "300";
			}
			if(answerInputRow_val == "") {
				answerInputRow_val = "1";
			}
			qu_inputWidth.val(answerInputWidth_val);
			qu_inputRow.val(answerInputRow_val);
			resetQuItemHover(quItemBody);
			$(thDialogObj).parents(".quCoItemUlLi").addClass("menuBtnClick");
		} else if(thObjClass.indexOf("quCoOptionEdit") >= 0) {
			$("#showForm2").show();
			//设置回显值 isNote checkType
			var quOption_isNote = $("input[name='quOption_isNote']");
			var quOption_checkType = $("select[name='quOption_checkType']");
			var quOption_isRequiredFill = $("input[name='quOption_isRequiredFill']");
			var quOptionParent = $(thDialogObj).parent();
			var isNote_val = quOptionParent.find("input[name='isNote']").val();
			var checkType_val = quOptionParent.find("input[name='checkType']").val();
			var isRequiredFill_val = quOptionParent.find("input[name='isRequiredFill']").val();
			if(isNote_val == "1") {
				quOption_isNote.prop("checked", true);
				$("#quOption_checkType_div").show();
				$("#quOption_isRequiredFill_div").show();
			} else {
				quOption_isNote.prop("checked", false);
				$("#quOption_checkType_div").hide();
				$("#quOption_isRequiredFill_div").hide();
			}
			if(checkType_val == "") {
				checkType_val = "NO";
			}
			quOption_checkType.val(checkType_val);
			if(isRequiredFill_val == "1") {
				quOption_isRequiredFill.prop("checked", true);
			} else {
				quOption_isRequiredFill.prop("checked", false);
			}
		}
		
		matchingLanguage();
		form.render();
		form.on('checkbox(quOption_isNote)', function (data) {
			var check = data.elem.checked;
	    	if(check){//选中
	    		$("#quOption_checkType_div").show();
	    		$("#quOption_isRequiredFill_div").show();
	    	} else {
	    		$("#quOption_checkType_div").hide();
	    		$("#quOption_isRequiredFill_div").hide();
	    	}
        });
		
	    form.on('submit(formAddBean1)', function (data) {
	    	
	        if (winui.verifyForm(data.elem)) {
	        	
	        	var quItemBody = $(thDialogObj).parents(".surveyQuItemBody");
	        	//设置回显值 isNote checkType
	        	var quFill_checkType = $("select[name='quFill_checkType']");
	        	var qu_inputWidth = $("input[name='qu_inputWidth']");
	        	var qu_inputRow = $("input[name='qu_inputRow']");

	        	var checkType = quItemBody.find("input[name='checkType']");
	        	//输入框 input 大小调整 quFillblankAnswerInput  quFillblankAnswerTextarea
	        	var answerInputWidth = quItemBody.find("input[name='answerInputWidth']");
	        	var answerInputRow = quItemBody.find("input[name='answerInputRow']");

	        	if(checkType.val() != quFill_checkType.val() || answerInputWidth.val() != qu_inputWidth.val() || answerInputRow.val() != qu_inputRow.val()) {
	        		quItemBody.find("input[name='saveTag']").val(0);
	        	}

	        	var checkTypeVal = quFill_checkType.val();
	        	if(checkTypeVal == "") {
	        		checkTypeVal = "NO";
	        	}
	        	checkType.val(checkTypeVal);

	        	answerInputWidth.val(qu_inputWidth.val());
	        	answerInputRow.val(qu_inputRow.val());

	        	if(qu_inputRow.val() > 1) {
	        		quItemBody.find(".quFillblankAnswerTextarea").show();
	        		quItemBody.find(".quFillblankAnswerInput").hide();
	        		quItemBody.find(".quFillblankAnswerTextarea").attr("rows", qu_inputRow.val());
	        		quItemBody.find(".quFillblankAnswerTextarea").width(qu_inputWidth.val());
	        	} else {
	        		quItemBody.find(".quFillblankAnswerTextarea").hide();
	        		quItemBody.find(".quFillblankAnswerInput").show();
	        		quItemBody.find(".quFillblankAnswerInput").width(qu_inputWidth.val());
	        	}

	        	quItemBody.find(".quCoItemUlLi").removeClass("hover");
	        	resetQuItemHover(null);
	    		
	   			parent.layer.close(index);
	        	parent.refreshCode = '0';
	        }
	        return false;
	    });
	    
	    form.on('submit(formAddBean2)', function (data) {
	    	
	        if (winui.verifyForm(data.elem)) {
	        	var quItemBody = $(thDialogObj).parents(".surveyQuItemBody");
	        	var quOptionParent = $(thDialogObj).parent();
	        	//设置回显值 isNote checkType
	        	var quOption_isNote = $("input[name='quOption_isNote']");
	        	var quOption_checkType = $("select[name='quOption_checkType']");
	        	var quOption_isRequiredFill = $("input[name='quOption_isRequiredFill']");

	        	var isNote = quOptionParent.find("input[name='isNote']");
	        	var checkType = quOptionParent.find("input[name='checkType']");
	        	var isRequiredFill = quOptionParent.find("input[name='isRequiredFill']");

	        	if(quOption_isNote.prop("checked") && (isNote.val() == "0" || isNote.val() == "")) {
	        		quItemBody.find("input[name='saveTag']").val(0);
	        		quOptionParent.find("input[name='quItemSaveTag']").val(0);
	        	}
	        	if(quOption_checkType.val() != checkType.val()) {
	        		quItemBody.find("input[name='saveTag']").val(0);
	        		quOptionParent.find("input[name='quItemSaveTag']").val(0);
	        	}
	        	if(quOption_isRequiredFill.val() != isRequiredFill.val()) {
	        		quItemBody.find("input[name='saveTag']").val(0);
	        		quOptionParent.find("input[name='quItemSaveTag']").val(0);
	        	}
	        	if(quOption_isNote.prop("checked")) {
	        		isNote.val(1);
	        	} else {
	        		isNote.val(0);
	        	}
	        	var checkTypeVal = quOption_checkType.val();
	        	if(checkTypeVal == "") {
	        		checkTypeVal = "NO";
	        	}
	        	checkType.val(checkTypeVal);
	        	if(quOption_isRequiredFill.prop("checked")) {
	        		isRequiredFill.val(1);
	        	} else {
	        		isRequiredFill.val(0);
	        	}
	        	//显示填空框
	        	quOptionParent.find(".optionInpText").show();
	    		
	        	parent.layer.close(index);
	        	parent.refreshCode = '0';
	        }
	        return false;
	    });
	    
	    function resetQuItemHover(quItemBody){
	    	$(".surveyQuItemBody").removeClass("hover");
	    	$(".surveyQuItemBody").find("input[name='hoverTag']").val("0");
	    	if(quItemBody != null){
	    		quItemBody.addClass("hover");
	    		quItemBody.find("input[name='hoverTag']").val("hover");
	    	}
	    }
	    
	    //取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
	    
});