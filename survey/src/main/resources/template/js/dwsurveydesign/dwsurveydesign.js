
var quIndex = 0, quLeftIndex = 0;//问题序号

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'validate'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'jqueryUI'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    
	    var svTag = 2;//表示题目是问卷题还是题库中题

	    $("#_basemodel").html(_basemodel);
	    $("#_rectanglemodel").html(_rectanglemodel);
	    $("#_auxiliarymodel").html(_auxiliarymodel);
	    $("#_operationmodel").html(_operationmodel);
	    $("#_commonlyusedmodel").html(_commonlyusedmodel);
	    $("body").append(_varioustemplates);
	    
	    showGrid({
		 	id: "dw_body",
		 	url: sysMainMation.surveyBasePath + "dwsurveydirectory003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/dwsurveydesign/dwsurveydesignbean.tpl'),
		 	ajaxSendLoadBefore: function(hdb, json){
		 		hdb.registerHelper("showIndex",function(v1, options){
		 			return parseInt(v1) + 1;
		 		});
		 		
		 		hdb.registerHelper("showQuestionIndex", function(v1, options) {
					if(v1 == '16' || v1 == '17'){
					} else {
						quIndex++;
						return quIndex;
					}
				});
		 		
		 		hdb.registerHelper("showQuestionLeftIndex", function(v1, options) {
					if(v1 == '16' || v1 == '17'){
					} else {
						quLeftIndex++;
						return quLeftIndex;
					}
				});
		 		
		 		hdb.registerHelper("showParamInt02",function(v1, options){
		 			var str = "";
		 			for(var i = 1; i <= v1; i++){
		 				str += "<td>" + i + "</td>";
		 			}
		 			return str;
		 		});
		 		
		 		hdb.registerHelper('compare1', function(v1, v2, options) {
		 			if(v1 == v2){
		 				return options.fn(this);
		 			} else {
		 				return options.inverse(this);
		 			}
		 		});
		 		
		 		hdb.registerHelper('showQuestion', function(v1, i, options) {
		 			switch (v1) {
		 				case 1://1单选题radio
		 					return new Handlebars.SafeString(getDataUseHandlebars($("#radioTemplate").html(), json.rows[i]));
      						break;
      					case 2://2多选题checkbox
		 					return new Handlebars.SafeString(getDataUseHandlebars($("#checkBoxTemplate").html(), json.rows[i]));
      						break;
  						case 3://3填空题fillblank
		 					return new Handlebars.SafeString(getDataUseHandlebars($("#fillblankTemplate").html(), json.rows[i]));
      						break;
      					case 4://4多项填空题multi-fillblank
		 					return new Handlebars.SafeString(getDataUseHandlebars($("#multiFillblankTemplate").html(), json.rows[i]));
      						break;
      					case 8://8评分题score
		 					return new Handlebars.SafeString(getDataUseHandlebars($("#scoreTemplate").html(), json.rows[i]));
      						break;
      					case 9://9排序题orderby
		 					return new Handlebars.SafeString(getDataUseHandlebars($("#orderbyTemplate").html(), json.rows[i]));
      						break;
      					case 11://11矩阵单选题chen-radio
		 					return new Handlebars.SafeString(getDataUseHandlebars($("#chenRadioTemplate").html(), json.rows[i]));
      						break;
      					case 12://12矩阵填空题chen-fbk
		 					return new Handlebars.SafeString(getDataUseHandlebars($("#chenFbkTemplate").html(), json.rows[i]));
      						break;
      					case 13://13矩阵多选题chen-checkbox
		 					return new Handlebars.SafeString(getDataUseHandlebars($("#chenCheckboxTemplate").html(), json.rows[i]));
      						break;
      					case 16://16分页pagetag
		 					return new Handlebars.SafeString(getDataUseHandlebars($("#pagetagTemplate").html(), json.rows[i]));
      						break;
      					case 17://17段落paragraph
		 					return new Handlebars.SafeString(getDataUseHandlebars($("#paragraphTemplate").html(), json.rows[i]));
      						break;
      					case 18://18矩阵评分题chen-score
		 					return new Handlebars.SafeString(getDataUseHandlebars($("#chenScoreTemplate").html(), json.rows[i]));
      						break;
		 			}
		 		});
		 		
		 		//左侧设计目录显示
		 		hdb.registerHelper('compareShowLeft', function(v1, options) {
		 			if(v1 != '16' && v1 != '17'){
		 				return options.fn(this);
		 			} else {
		 				return options.inverse(this);
		 			}
		 		});
		 		
		 		hdb.registerHelper('compare2', function(v1, v2, options) {
		 			if(v1 == v2){
		 				return 'display: none;';
		 			} else {
		 				return '';
		 			}
		 		});
		 		
		 		hdb.registerHelper("cellCount001",function(v1, options){
		 			var str = "";
		 			var width = 600 / v1;
		 			for(var i = 1; i <= v1; i++){
		 				str += '<td width="' + width + 'px">' + 
									'<input type="radio"><label style="width:' + width + 'px;" class="editAble quCoOptionEdit">{{optionName}}</label>' + 
									'<input type="text" class="optionInpText" style="{{#compare2 isNote 0}}{{/compare2}}" />' + 
									'<div class="quItemInputCase">' + 
										'<input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1">' + 
										'<input type="hidden" name="isNote" value="{{isNote}}">' + 
										'<input type="hidden" name="checkType" value="{{checkType}}">' + 
										'<input type="hidden" name="isRequiredFill" value="{{isRequiredFill}}">' + 
									'</div>' + 
								'</td>';
		 			}
		 			str += '<div class="emptyTd"></div>';
		 			return str;
		 		});
		 		
		 		hdb.registerHelper("cellCount002",function(v1, options){
		 			var str = "";
		 			var width = 600 / v1;
		 			for(var i = 1; i <= v1; i++){
		 				str += '<td width="' + width + 'px">' + 
									'<input type="checkbox"><label style="width:' + width + 'px;" class="editAble quCoOptionEdit">{{optionName}}</label>' + 
									'<input type="text" class="optionInpText" style="{{#compare2 isNote 0}}{{/compare2}}" />' + 
									'<div class="quItemInputCase">' + 
										'<input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1">' + 
										'<input type="hidden" name="isNote" value="{{isNote}}">' + 
										'<input type="hidden" name="checkType" value="{{checkType}}">' + 
										'<input type="hidden" name="isRequiredFill" value="{{isRequiredFill}}">' + 
									'</div>' + 
								'</td>';
		 			}
		 			str += '<div class="emptyTd"></div>';
		 			return str;
		 		});
		 	},
		 	ajaxSendAfter:function (json) {
		 		
			    loadAddr();//初始化地址区域
			    
		 		// 加载拖拽事件
				loadDrag();
				
				matchingLanguage();
		 		form.render();
		 		
		 		//绑定变动
		 		bindQuHoverItem();
		 		
		 		$("#dwSurveyName").click(function(){
					editAble($(this));
					return false;
				});
				$("#dwSurveyNoteEdit").click(function(){
					editAble($(this));
					return false;
				});
		 	}
	    });
	    
	    var survey = {
	    	"RADIO": deleteRadioOption, // 删除单选题选项
	    	"CHECKBOX": deleteCheckboxOption, // 删除多选题选项
	    	"SCORE": deleteScoreOption, // 删除评分题选项
	    	"ORDERBY": deleteOrderquOption, //删除排序题选项
	    	"MULTIFILLBLANK": deleteMultiFillblankOption, // 删除多行天空题选项
	    	"CHENRADIO": deleteChenOption, // 删除矩阵题
	    	"CHENCHECKBOX": deleteChenOption, // 删除矩阵题
	    	"CHENFBK": deleteChenOption, // 删除矩阵题
	    	"CHENSCORE": deleteChenOption, // 删除矩阵题
	    	"SAVEQUS": saveQus // 保存题目信息
	    };
	    layui.survey = survey;
	    
	    // 删除题目
	    $("body").on("click", ".dwQuDelete", function() {
			var quBody = $(this).parents(".surveyQuItemBody");
			layer.confirm("确认要删除此题吗？", { icon: 3, title: '删除题目' }, function (index) {
				layer.close(index);
				var quId = quBody.find("input[name='quId']").val();
				if (!isNull(quId)){
					AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory015", params: {quId: quId}, type: 'json', callback: function (json) {
						quBody.hide("slow", function() {
							$(this).parent().remove();
							//重置序号
							resetQuItem();
							//重置左侧设计目录序号
							resetQuLeftItem();
						});
		 	   		}});
				} else {
					quBody.hide("slow", function() {
						$(this).parent().remove();
						//重置序号
						resetQuItem();
						//重置左侧设计目录序号
						resetQuLeftItem();
					});
				}
			});
			return false;
		});
		
		/**
		 * 删除矩陈单选题选项
		 */
		function deleteChenOption(){
			var curEditTd = $(curEditObj).parents("td");
			var curEditTdClass = curEditTd.attr("class");
			if(curEditTdClass.indexOf("Column") >= 0){
				deleteChenColumnOption();
			} else {
				deleteChenRowOption();
			}
		}
		
		/**
		 * 删除矩阵单选题列选项
		 */
		function deleteChenColumnOption(){
			var optionParent = null;
			optionParent = $(curEditObj).parents("td.quChenColumnTd");
			var quOptionId = $(optionParent).find("input[name='quItemId']").val();
			if (!isNull(quOptionId) && quOptionId != "0" ){
				AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory016", params: {quItemId: quOptionId}, type: 'json', callback: function (json) {
					delQuOptionCallBack(optionParent);
	 	   		}});
			} else {
				delQuOptionCallBack(optionParent);
			}
		}
		/**
		 * 删除矩阵单选题行选项
		 */
		function deleteChenRowOption(){
			var optionParent = null;
			optionParent = $(curEditObj).parents("td.quChenRowTd");
			var quOptionId = $(optionParent).find("input[name='quItemId']").val();
			if (!isNull(quOptionId) && quOptionId != "0" ){
				AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory017", params: {quItemId: quOptionId}, type: 'json', callback: function (json) {
					delQuOptionCallBack(optionParent);
	 	   		}});
			} else {
				delQuOptionCallBack(optionParent);
			}
		}
		
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	    //保存
	    $("body").on("click", "#saveBtn", function() {
	    	curEditCallback();
			dwCommonDialogHide();
			resetQuItemHover(null);
			
			winui.window.msg('保存中', {icon: 1,time: 1000});
			saveSurvey(function(){
				isSaveProgress = false;
				winui.window.msg(systemLanguage["com.skyeye.addOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			});
	    });
	    
	    function saveSurvey(callback) {
			isSaveProgress = true;
			var fristQuItemBody = $("#dwSurveyQuContent .li_surveyQuItemBody").first();
			saveQus(fristQuItemBody, callback);
		}
	    
	    /**
	     * 保存标记说明
	     * saveTag  标记本题有无变动
	     * quTitleSaveTag  标记题标题变动
	     * quItemSaveTag 标记题选项变动
	     * 0=表示有变动，未保存
	     * 1=表示已经保存同步
	     */
	    function saveQus(quItemBody, callback) {
	    	if(quItemBody[0]) {
	    		var quType = quItemBody.find("input[name='quType']").val();
	    		if(quType == "RADIO") {
	    			// 单选题
	    			saveRadio(quItemBody, callback);
	    		} else if(quType == "CHECKBOX") {
	    			// 多选题
	    			saveCheckbox(quItemBody, callback);
	    		} else if(quType == "FILLBLANK") {
	    			saveFillblank(quItemBody, callback);
	    		} else if(quType == "SCORE") {
	    			saveScore(quItemBody, callback);
	    		} else if(quType == "ORDERBY") {
	    			saveOrderqu(quItemBody, callback);
	    		} else if(quType == "PAGETAG") {
	    			savePagetag(quItemBody, callback);
	    		} else if(quType == "PARAGRAPH") {
	    			saveParagraph(quItemBody, callback);
	    		} else if(quType == "MULTIFILLBLANK") {
	    			saveMultiFillblank(quItemBody, callback);
	    		} else if(quType == "CHENRADIO" || quType == "CHENCHECKBOX" || quType == "CHENFBK" || quType == "CHENSCORE") {
	    			saveChen(quItemBody, callback);
	    		} else {
	    			callback();
	    		}
	    	} else {
	    		callback();
	    	}
	    }
	    
	    function getCommonParams(quItemBody){
	    	return {
	    		belongId: parent.rowId,
	    		orderById: quItemBody.find("input[name='orderById']").val(),
	    		tag: svTag,
	    		quId: quItemBody.find("input[name='quId']").val(),
	    		hv: quItemBody.find("input[name='hv']").val(),
	    		randOrder: quItemBody.find("input[name='randOrder']").val(),
				cellCount: quItemBody.find("input[name='cellCount']").val(),
				isRequired: quItemBody.find("input[name='isRequired']").val(),
				quTitle: encodeURI(quItemBody.find(".quCoTitleEdit").html())
	    	};
	    }
	    
	    /** 保存单选题 **/
	    function saveRadio(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var data = {
    				contactsAttr: quItemBody.find("input[name='contactsAttr']").val(),
    				contactsField: quItemBody.find("input[name='contactsField']").val()
	    		};
	    		$.extend(data, getCommonParams(quItemBody));
	    		var quItemOptions = null;
	    		if(quItemBody.find("input[name='hv']").val() == 3) {
	    			//还有是table的情况需要处理
	    			quItemOptions = quItemBody.find(".quCoItem table.tableQuColItem tr td");
	    		} else {
	    			quItemOptions = quItemBody.find(".quCoItem li.quCoItemUlLi");
	    		}
	    		var radioTd = [];
	    		$.each(quItemOptions, function(i) {
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			if(quItemSaveTag == 0) {
	    				var s = {
    						optionValue: encodeURI($(this).find("label.quCoOptionEdit").html()),
    						optionId: $(this).find(".quItemInputCase input[name='quItemId']").val(),
    						isNote: $(this).find(".quItemInputCase input[name='isNote']").val(),
    						checkType: $(this).find(".quItemInputCase input[name='checkType']").val(),
    						isRequiredFill: $(this).find(".quItemInputCase input[name='isRequiredFill']").val(),
    						key: i
    	    			};
	    				radioTd.push(s);
	    			}
	    			//更新 字母 title标记到选项上.
	    			$(this).addClass("quOption_" + i);
	    		});
	    		data.radioTd = JSON.stringify(radioTd);
	    		
	    		// 逻辑选项
	    		var list = [].concat(getLogic(quItemBody));
	    		data.logic = JSON.stringify(list);
	    		
	    		AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory010", params: data, type: 'json', callback: function (json) {
					var quId = json.bean.quId;
					quItemBody.find("input[name='saveTag']").val(1);
					quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
					quItemBody.find("input[name='quId']").val(quId);

					//重置问题选项和业务逻辑
					resetLogicAndItem(quItemBody, json);

					//执行保存下一题
					saveQus(quItemBody.next(), callback);
					//同步-更新题目排序号
					quCBNum2++;
					exeQuCBNum();
	 	   		}});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /**
	     * 删除单选题选项
	     */
	    function deleteRadioOption() {
	    	//判断是否是table类型
	    	var quItemBody = $(curEditObj).parents(".surveyQuItemBody");
	    	var hv = quItemBody.find("input[name='hv']").val();
	    	var optionParent = null;
	    	if(hv == 3) {
	    		optionParent = $(curEditObj).parents("td");
	    	} else {
	    		optionParent = $(curEditObj).parents("li.quCoItemUlLi");
	    	}
	    	var quOptionId = $(optionParent).find("input[name='quItemId']").val();
	    	if(quOptionId != "" && quOptionId != "0") {
	    		AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory018", params: {quItemId: quOptionId}, type: 'json', callback: function (json) {
					delQuOptionCallBack(optionParent);
	 	   		}});
	    	} else {
	    		delQuOptionCallBack(optionParent);
	    	}
	    }

	    /** 保存多选题 **/
	    function saveCheckbox(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var data = {
    				contactsAttr: quItemBody.find("input[name='contactsAttr']").val(),
    				contactsField: quItemBody.find("input[name='contactsField']").val()
	    		};
	    		$.extend(data, getCommonParams(quItemBody));
	    		var quItemOptions = null;
	    		if(quItemBody.find("input[name='hv']").val() == 3) {
	    			//还有是table的情况需要处理
	    			quItemOptions = quItemBody.find(".quCoItem table.tableQuColItem tr td");
	    		} else {
	    			quItemOptions = quItemBody.find(".quCoItem li.quCoItemUlLi");
	    		}
	    		var checkboxTd = [];
	    		$.each(quItemOptions, function(i) {
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			if(quItemSaveTag == 0) {
	    				var s = {
    						optionValue: encodeURI($(this).find("label.quCoOptionEdit").html()),
    						optionId: $(this).find(".quItemInputCase input[name='quItemId']").val(),
    						isNote: $(this).find(".quItemInputCase input[name='isNote']").val(),
    						checkType: $(this).find(".quItemInputCase input[name='checkType']").val(),
    						isRequiredFill: $(this).find(".quItemInputCase input[name='isRequiredFill']").val(),
    						key: i
    	    			};
	    				checkboxTd.push(s);
	    			}
	    			//更新 字母 title标记到选项上.
	    			$(this).addClass("quOption_" + i);
	    		});
	    		data.checkboxTd = JSON.stringify(checkboxTd);
	    		// 逻辑选项
	    		var list = [].concat(getLogic(quItemBody));
	    		data.logic = JSON.stringify(list);
	    		AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory011", params: data, type: 'json', callback: function (json) {
					var quId = json.bean.quId;
					quItemBody.find("input[name='saveTag']").val(1);
					quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
					quItemBody.find("input[name='quId']").val(quId);

					//重置问题选项和业务逻辑
					resetLogicAndItem(quItemBody, json);

					//执行保存下一题
					saveQus(quItemBody.next(), callback);
					//同步-更新题目排序号
					quCBNum2++;
					exeQuCBNum();
	 	   		}});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /**
	     * 删除多选题选项
	     */
	    function deleteCheckboxOption() {
	    	//判断是否是table类型
	    	var quItemBody = $(curEditObj).parents(".surveyQuItemBody");
	    	var hv = quItemBody.find("input[name='hv']").val();
	    	var optionParent = null;
	    	if(hv == 3) {
	    		optionParent = $(curEditObj).parents("td");
	    	} else {
	    		optionParent = $(curEditObj).parents("li.quCoItemUlLi");
	    	}
	    	var quOptionId = $(optionParent).find("input[name='quItemId']").val();
	    	if(quOptionId != "" && quOptionId != "0") {
	    		AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory019", params: {quItemId: quOptionId}, type: 'json', callback: function (json) {
					delQuOptionCallBack(optionParent);
	 	   		}});
	    	} else {
	    		delQuOptionCallBack(optionParent);
	    	}
	    }

	    function delQuOptionCallBack(optionParent) {
	    	var quItemBody = $(optionParent).parents(".surveyQuItemBody");
	    	var quType = quItemBody.find("input[name='quType']").val();
	    	if(quType == "CHECKBOX" || quType == "RADIO") {
	    		var hv = quItemBody.find("input[name='hv']").val();
	    		if(hv == 3) {
	    			//emptyTd
	    			var optionTr = $(optionParent).parents("tr");
	    			var optionNextTr = optionTr.next();
	    			if(optionNextTr[0]) {
	    				//则后面还有是中间选项，则删除，再依次后面的td往前移动
	    				$(optionParent).remove();
	    				moveTabelTd(optionNextTr);
	    			} else {
	    				//非中间选项，删除-再添加一个空td
	    				$(optionParent).remove();
	    				movePareseLastTr(optionTr);
	    			}
	    		} else {
	    			optionParent.remove();
	    		}
	    	} else if(quType == "CHENRADIO" || quType == "CHENCHECKBOX" || quType == "CHENFBK" || quType == "CHENSCORE") {
	    		var quCoChenTable = optionParent.parents("table.quCoChenTable");
	    		var optionParentClass = optionParent.attr("class");
	    		if(optionParentClass.indexOf("Column") >= 0) {
	    			var removeTrs = quCoChenTable.find("tr:gt(0)");
	    			$.each(removeTrs, function() {
	    				$(this).find("td:last").remove();
	    			});
	    			optionParent.remove();
	    		} else {
	    			optionParent.parent().remove();
	    		}
	    	} else {
	    		optionParent.remove();
	    	}
	    	dwCommonEditHide();
	    	bindQuHoverItem();
	    }

	    function moveTabelTd(nextTr) {
	    	if(nextTr[0]) {
	    		var prevTr = nextTr.prev();
	    		var nextTds = nextTr.find("td");
	    		$(nextTds.get(0)).appendTo(prevTr);
	    		//判断当前next是否是最后一个，是则：判断如果没有选项，则删除tr,如果有选项，则填一个空td
	    		var nextNextTr = nextTr.next();
	    		if(!nextNextTr[0]) {
	    			movePareseLastTr(nextTr);
	    		}
	    		moveTabelTd($(nextTr).next());
	    	}
	    }

	    function movePareseLastTr(nextTr) {
	    	var editAbles = nextTr.find(".editAble");
	    	if(editAbles[0]) {
	    		//有选项，则补充一个空td
	    		var editAbleTd = editAbles.parents("td");
	    		editAbleTd.clone().prependTo(nextTr);
	    		nextTr.find("td").last().html("<div class='emptyTd'></div>");
	    	} else {
	    		nextTr.remove();
	    	}
	    }

	    /** 保存填空题 **/
	    function saveFillblank(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var data = {
    				answerInputWidth: quItemBody.find("input[name='answerInputWidth']").val(),
    				answerInputRow: quItemBody.find("input[name='answerInputRow']").val(),
    				contactsAttr: quItemBody.find("input[name='contactsAttr']").val(),
    				contactsField: quItemBody.find("input[name='contactsField']").val(),
    				checkType: quItemBody.find("input[name='checkType']").val()
	    		};
	    		$.extend(data, getCommonParams(quItemBody));
	    		// 逻辑选项
	    		var list = [].concat(getLogic(quItemBody));
	    		data.logic = JSON.stringify(list);
	    		AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory006", params: data, type: 'json', callback: function (json) {
					var quId = json.bean.quId;
					quItemBody.find("input[name='saveTag']").val(1);
					quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
					quItemBody.find("input[name='quId']").val(quId);
					//同步logic Id信息
					var quLogics = json.bean.quLogics;
					if (!isNull(quLogics)){
						$.each(quLogics, function(i, item) {
							var logicItem = quItemBody.find(".quLogicItem_" + item.title);
							logicItem.find("input[name='quLogicId']").val(item.id);
							logicItem.find("input[name='logicSaveTag']").val(1);
						});
					}
					//执行保存下一题
					saveQus(quItemBody.next(), callback);
					//同步-更新题目排序号
					quCBNum2++;
					exeQuCBNum();
	 	   		}});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /** 保存评分题 **/
	    function saveScore(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var data = {
    				paramInt01: quItemBody.find("input[name='paramInt01']").val(),
    				paramInt02: quItemBody.find("input[name='paramInt02']").val()
	    		};
	    		$.extend(data, getCommonParams(quItemBody));
	    		//评分题选项td
	    		var quItemOptions = quItemBody.find(".quCoItem table.quCoItemTable tr td.quOptionEditTd");
	    		var scoreTd = [];
	    		$.each(quItemOptions, function(i) {
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			if(quItemSaveTag == 0) {
	    				var s = {
    						optionValue: encodeURI($(this).find("label.quCoOptionEdit").html()),
    						optionId: $(this).find(".quItemInputCase input[name='quItemId']").val(),
    						key: i
    	    			};
	    				scoreTd.push(s);
	    			}
	    			//更新 字母 title标记到选项上.
	    			$(this).addClass("quOption_" + i);
	    		});
	    		data.scoreTd = JSON.stringify(scoreTd);

	    		// 逻辑选项
	    		var list = [].concat(getLogic(quItemBody));
	    		data.logic = JSON.stringify(list);
	    		AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory007", params: data, type: 'json', callback: function (json) {
					var quId = json.bean.quId;
					quItemBody.find("input[name='saveTag']").val(1);
					quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
					quItemBody.find("input[name='quId']").val(quId);

					//重置问题选项和业务逻辑
					resetLogicAndItem(quItemBody, json);

					//执行保存下一题
					saveQus(quItemBody.next(), callback);
					//同步-更新题目排序号
					quCBNum2++;
					exeQuCBNum();
	 	   		}});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /**
	     * 删除评分Score选项
	     */
	    function deleteScoreOption() {
	    	var optionParent = null;
	    	optionParent = $(curEditObj).parents("tr.quScoreOptionTr");
	    	var quOptionId = $(optionParent).find("input[name='quItemId']").val();
	    	if(quOptionId != "" && quOptionId != "0") {
	    		AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory020", params: {quItemId: quOptionId}, type: 'json', callback: function (json) {
					delQuOptionCallBack(optionParent);
	 	   		}});
	    	} else {
	    		delQuOptionCallBack(optionParent);
	    	}
	    }

	    /** 保存排序题 **/
	    function saveOrderqu(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var data = {};
	    		$.extend(data, getCommonParams(quItemBody));
	    		//评分题选项td
	    		var quItemOptions = quItemBody.find(".quCoItem .quOrderByLeft  li.quCoItemUlLi");
	    		var orderquTd = [];
	    		$.each(quItemOptions, function(i) {
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			if(quItemSaveTag == 0) {
	    				var s = {
    						optionValue: encodeURI($(this).find("label.quCoOptionEdit").html()),
    						optionId: $(this).find(".quItemInputCase input[name='quItemId']").val(),
    						key: i
    	    			};
	    				orderquTd.push(s);
	    			}
	    			$(this).addClass("quOption_" + i);
	    		});
	    		data.orderquTd = JSON.stringify(orderquTd);

	    		// 逻辑选项
	    		var list = [].concat(getLogic(quItemBody));
	    		data.logic = JSON.stringify(list);
	    		
	    		AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory008", params: data, type: 'json', callback: function (json) {
					var quId = json.bean.quId;
					quItemBody.find("input[name='saveTag']").val(1);
					quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
					quItemBody.find("input[name='quId']").val(quId);

					//重置问题选项和业务逻辑
					resetLogicAndItem(quItemBody, json);

					//执行保存下一题
					saveQus(quItemBody.next(), callback);
					//同步-更新题目排序号
					quCBNum2++;
					exeQuCBNum();
	 	   		}});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /**
	     * 删除排序选项
	     */
	    function deleteOrderquOption() {
	    	var optionParent = null;
	    	optionParent = $(curEditObj).parents("li.quCoItemUlLi");
	    	var quItemBody = $(curEditObj).parents(".surveyQuItemBody");
	    	var rmQuOrderTableTr = quItemBody.find(".quOrderByRight table.quOrderByTable tr:last");
	    	var quOptionId = $(optionParent).find("input[name='quItemId']").val();
	    	if(quOptionId != "" && quOptionId != "0") {
	    		AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory021", params: {quItemId: quOptionId}, type: 'json', callback: function (json) {
					delQuOptionCallBack(optionParent);
					rmQuOrderTableTr.remove();
	 	   		}});
	    	} else {
	    		delQuOptionCallBack(optionParent);
	    		rmQuOrderTableTr.remove();
	    	}
	    }

	    /** 保存分页标记 **/
	    function savePagetag(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var data = {};
	    		$.extend(data, getCommonParams(quItemBody));
	    		// 逻辑选项
	    		var list = [].concat(getLogic(quItemBody));
	    		data.logic = JSON.stringify(list);
	    		AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory009", params: data, type: 'json', callback: function (json) {
					var quId = json.bean.quId;
					quItemBody.find("input[name='saveTag']").val(1);
					quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
					quItemBody.find("input[name='quId']").val(quId);

					//重置问题选项和业务逻辑
					resetLogicAndItem(quItemBody, json);

					//执行保存下一题
					saveQus(quItemBody.next(), callback);
					//同步-更新题目排序号
					quCBNum2++;
					exeQuCBNum();
	 	   		}});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /** 保存段落题 **/
	    function saveParagraph(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var data = {};
	    		$.extend(data, getCommonParams(quItemBody));
	    		// 逻辑选项
	    		var list = [].concat(getLogic(quItemBody));
	    		data.logic = JSON.stringify(list);
	    		AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory013", params: data, type: 'json', callback: function (json) {
					var quId = json.bean.quId;
					quItemBody.find("input[name='saveTag']").val(1);
					quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
					quItemBody.find("input[name='quId']").val(quId);
					//同步logic Id信息
					var quLogics = json.bean.quLogics;
					$.each(quLogics, function(i, item) {
						var logicItem = quItemBody.find(".quLogicItem_" + item.title);
						logicItem.find("input[name='quLogicId']").val(item.id);
						logicItem.find("input[name='logicSaveTag']").val(1);
					});
					//执行保存下一题
					saveQus(quItemBody.next(), callback);
					//同步-更新题目排序号
					quCBNum2++;
					exeQuCBNum();
	 	   		}});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /** 保存多项填空题 **/
	    function saveMultiFillblank(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var data = {
    				paramInt01: quItemBody.find("input[name='paramInt01']").val(),
    				paramInt02: quItemBody.find("input[name='paramInt02']").val()
	    		};
	    		$.extend(data, getCommonParams(quItemBody));
	    		//评分题选项td
	    		var quItemOptions = quItemBody.find(".quCoItem table.mFillblankTable tr td.mFillblankTableEditTd");
	    		var multiFillblankTd = [];
	    		$.each(quItemOptions, function(i) {
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			if(quItemSaveTag == 0) {
	    				var s = {
    						optionValue: encodeURI($(this).find("label.quCoOptionEdit").html()),
    						optionId: $(this).find(".quItemInputCase input[name='quItemId']").val(),
    						key: i
    	    			};
	    				multiFillblankTd.push(s);
	    			}
	    			//更新 字母 title标记到选项上.
	    			$(this).addClass("quOption_" + i);
	    		});
	    		data.multiFillblankTd = JSON.stringify(multiFillblankTd);
	    		// 逻辑选项
	    		var list = [].concat(getLogic(quItemBody));
	    		data.logic = JSON.stringify(list);
	    		AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory012", params: data, type: 'json', callback: function (json) {
					var quId = json.bean.quId;
					quItemBody.find("input[name='saveTag']").val(1);
					quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
					quItemBody.find("input[name='quId']").val(quId);

					//重置问题选项和业务逻辑
					resetLogicAndItem(quItemBody, json);

					//执行保存下一题
					saveQus(quItemBody.next(), callback);
					//同步-更新题目排序号
					quCBNum2++;
					exeQuCBNum();
	 	   		}});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /**
	     * 删除多项填空题选项
	     */
	    function deleteMultiFillblankOption() {
	    	var optionParent = null;
	    	optionParent = $(curEditObj).parents("tr.mFillblankTableTr");
	    	var quOptionId = $(optionParent).find("input[name='quItemId']").val();
	    	if(quOptionId != "" && quOptionId != "0") {
	    		AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory022", params: {quItemId: quOptionId}, type: 'json', callback: function (json) {
					delQuOptionCallBack(optionParent);
	 	   		}});
	    	} else {
	    		delQuOptionCallBack(optionParent);
	    	}
	    }

	    /** 保存矩阵单选题 **/
	    function saveChen(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var data = {
    				quType: quItemBody.find("input[name='quType']").val()
	    		};
	    		$.extend(data, getCommonParams(quItemBody));
	    		//矩阵列选项td
	    		var quColumnOptions = quItemBody.find(".quCoItem table.quCoChenTable tr td.quChenColumnTd");
	    		var column = [];
	    		$.each(quColumnOptions, function(i) {
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			if(quItemSaveTag == 0) {
	    				var s = {
    						optionValue: encodeURI($(this).find("label.quCoOptionEdit").html()),
    						optionId: $(this).find(".quItemInputCase input[name='quItemId']").val(),
    						key: i
    	    			};
	    				column.push(s);
	    			}
	    			//更新 字母 title标记到选项上.
	    			$(this).addClass("quColumnOption_" + i);
	    		});
	    		data.column = JSON.stringify(column);
	    		//矩阵行选项td
	    		var quColumnOptions = quItemBody.find(".quCoItem table.quCoChenTable tr td.quChenRowTd");
	    		var row = [];
	    		$.each(quColumnOptions, function(i) {
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			if(quItemSaveTag == 0) {
	    				var s = {
    						optionValue: encodeURI($(this).find("label.quCoOptionEdit").html()),
    						optionId: $(this).find(".quItemInputCase input[name='quItemId']").val(),
    						key: i
    	    			};
	    				row.push(s);
	    			}
	    			//更新 字母 title标记到选项上.
	    			$(this).addClass("quRowOption_" + i);
	    		});
	    		data.row = JSON.stringify(row);
	    		// 逻辑选项
	    		var list = [].concat(getLogic(quItemBody));
	    		data.logic = JSON.stringify(list);
	    		AjaxPostUtil.request({url: sysMainMation.surveyBasePath + "dwsurveydirectory014", params: data, type: 'json', callback: function (json) {
					var quId = json.bean.quId;
					quItemBody.find("input[name='saveTag']").val(1);
					quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
					quItemBody.find("input[name='quId']").val(quId);
					//列选项
					var quColumnItems = json.bean.quColumnItems;
					if (!isNull(quColumnItems)){
						$.each(quColumnItems, function(i, item) {
							var quItemOption = quItemBody.find(".quColumnOption_" + item.title);
							quItemOption.find("input[name='quItemId']").val(item.id);
							quItemOption.find(".quItemInputCase input[name='quItemSaveTag']").val(1);
						});
					}
					//行选项
					var quRowItems = json.bean.quRowItems;
					if (!isNull(quRowItems)){
						$.each(quRowItems, function(i, item) {
							var quItemOption = quItemBody.find(".quRowOption_" + item.title);
							quItemOption.find("input[name='quItemId']").val(item.id);
							quItemOption.find(".quItemInputCase input[name='quItemSaveTag']").val(1);
						});
					}
					//同步logic Id信息
					var quLogics = json.bean.quLogics;
					if (!isNull(quLogics)){
						$.each(quLogics, function(i, item) {
							var logicItem = quItemBody.find(".quLogicItem_" + item.title);
							logicItem.find("input[name='quLogicId']").val(item.id);
							logicItem.find("input[name='logicSaveTag']").val(1);
						});
					}
					//执行保存下一题
					saveQus(quItemBody.next(), callback);
					//同步-更新题目排序号
					quCBNum2++;
					exeQuCBNum();
	 	   		}});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	});
	    
});