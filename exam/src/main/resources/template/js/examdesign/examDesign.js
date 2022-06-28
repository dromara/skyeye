
var quIndex = 0, quLeftIndex = 0;// 问题序号

// 从题库中选择的试题
var questionMationList = new Array();

// 知识点选择必备参数
var schoolKnowledgeCheckType = 2;// 知识点选择类型：1.单选schoolKnowledgeMation；2.多选schoolKnowledgeMationList
var schoolKnowledgeMationList = new Array();

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'validate', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'jqueryUI'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form,
	    	element = layui.element;
	    
	    var svTag = 2;// 表示题目是问卷题还是题库中题

	    $("#_basemodel").html(_basemodelSchool);
	    $("#_rectanglemodel").html(_rectanglemodelSchool);
	    $("#_auxiliarymodel").html(_auxiliarymodelSchool);
	    $("body").append(_varioustemplatesSchool);
	    
	    var loadPageJson;
	    
	    initPageJson();
	    function initPageJson(callback){
		    AjaxPostUtil.request({url:schoolBasePath + "exam003", params:{rowId: parent.rowId}, type: 'json', callback: function (json) {
	   			if (json.returnCode == 0) {
	   				$.each(json.rows, function(i, item){
	   					item.saveTag = 1;
	   				});
	   				loadPageJson = json;
	   				if(typeof(callback) == "function") {
						callback();
					} else {
		   				initPage();
					}
	   			} else {
	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	   			}
	   		}});
	    }
	    
   		function initPage(){
   			showGrid({
			 	id: "dw_body",
			 	data: loadPageJson,
			 	pagination: false,
			 	template: getFileContent('tpl/examdesign/examDesignBean.tpl'),
			 	ajaxSendLoadBefore: function(hdb, json){
			 		$.each(json.rows, function(i, item){
			 			if(isNull(item.id)){
			 				item.id = getRandomValueToString();
			 				item.noQuId = 1;
			 			}
			 		});
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
			 		// 初始化地址区域
				    loadAddr();
				    
			 		// 加载拖拽事件
					loadDrag();
					
					// 加载答案数据
					loadAnswer(json);
					
					matchingLanguage();
			 		form.render();
			 		
			 		// 绑定变动
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
   		}
   		
   		/**
   		 * 加载答案
   		 */
   		function loadAnswer(json){
   			$.each(json.rows, function(m, bean){
   				// 1.设置视频，音频，图片等的tab页选中
   				var qBodyItem = $("input[name='quId'][value='" + bean.id + "']").parents(".surveyQuItemBody");
   				var layuiTab = qBodyItem.find(".layui-tab");
   				layuiTab.find('.layui-tab-title li').eq(bean.fileType).addClass('layui-this').siblings().removeClass('layui-this');
				layuiTab.find('.layui-tab-item').eq(bean.fileType).addClass('layui-show').siblings().removeClass('layui-show');
				// 2.是否允许拍照/上传图片
				if(isNull(bean.whetherUpload)){
					bean.whetherUpload = 2;
				}
				qBodyItem.find("input:radio[name='whetherUpload" + bean.id + "'][value='" + bean.whetherUpload + "']").attr("checked", true);
				// 3.设置试题附件
				pageLoadAfter(bean.id, bean.fileType, bean.fileUrl);
				// 4.加载答案
				var quType = bean.quTypeName;
				if(quType === "CHENCHECKBOX"){
		    		var answer = qBodyItem.find(".quCoItem table.quCoChenTable tr input.chenCheckBoxInput");
		    		var isDefaultAnswer = isJsonFormat(bean.isDefaultAnswer) ? JSON.parse(bean.isDefaultAnswer) : [];
		    		var columuLength = qBodyItem.find(".quCoItem table.quCoChenTable tr td.quChenColumnTd").length;
		    		var xIndex = 0;
		    		var yIndex = 1;
		    		$.each(answer, function(i) {
		    			if(i % columuLength == 0){
		    				xIndex++;
		    				yIndex = 1;
		    			} else {
			    			yIndex++;
		    			}
		    			var _this = this;
		    			$.each(isDefaultAnswer, function(j, item) {
			    			if(item.x == xIndex && item.y == yIndex){
			    				$(_this).prop("checked", item.value);
			    			}
			    		});
		    		});
    			}else if(quType === "CHENRADIO"){
    				var answer = qBodyItem.find(".quCoItem table.quCoChenTable tr input.chenRadioInput");
		    		var isDefaultAnswer = isJsonFormat(bean.isDefaultAnswer) ? JSON.parse(bean.isDefaultAnswer) : [];
		    		var columuLength = qBodyItem.find(".quCoItem table.quCoChenTable tr td.quChenColumnTd").length;
		    		var xIndex = 0;
		    		var yIndex = 1;
		    		$.each(answer, function(i) {
		    			if(i % columuLength == 0){
		    				xIndex++;
		    				yIndex = 1;
		    			} else {
			    			yIndex++;
		    			}
		    			var _this = this;
		    			$.each(isDefaultAnswer, function(j, item) {
			    			if(item.x == xIndex && item.y == yIndex){
			    				$(_this).prop("checked", item.value);
			    			}
			    		});
		    		});
    			}else if(quType === "CHENSCORE"){
    				var answer = qBodyItem.find(".quCoItem table.quCoChenTable tr select.quChenScoreSelect");
		    		var isDefaultAnswer = isJsonFormat(bean.isDefaultAnswer) ? JSON.parse(bean.isDefaultAnswer) : [];
		    		var columuLength = qBodyItem.find(".quCoItem table.quCoChenTable tr td.quChenColumnTd").length;
		    		var xIndex = 0;
		    		var yIndex = 1;
		    		$.each(answer, function(i) {
		    			if(i % columuLength == 0){
		    				xIndex++;
		    				yIndex = 1;
		    			} else {
			    			yIndex++;
		    			}
		    			var _this = this;
		    			$.each(isDefaultAnswer, function(j, item) {
			    			if(item.x == xIndex && item.y == yIndex){
			    				$(_this).val(item.value);
			    			}
			    		});
		    		});
    			}else if(quType === "CHENFBK"){
    				var answer = qBodyItem.find(".quCoItem table.quCoChenTable tr input.questionChenColumnValue");
		    		var isDefaultAnswer = isJsonFormat(bean.isDefaultAnswer) ? JSON.parse(bean.isDefaultAnswer) : [];
		    		var columuLength = qBodyItem.find(".quCoItem table.quCoChenTable tr td.quChenColumnTd").length;
		    		var xIndex = 0;
		    		var yIndex = 1;
		    		$.each(answer, function(i) {
		    			if(i % columuLength == 0){
		    				xIndex++;
		    				yIndex = 1;
		    			} else {
			    			yIndex++;
		    			}
		    			var _this = this;
		    			$.each(isDefaultAnswer, function(j, item) {
			    			if(item.x == xIndex && item.y == yIndex){
			    				$(_this).val(item.value);
			    			}
			    		});
		    		});
    			}
    			// 说明是从题库中导入的试题
   				if(bean.noQuId == 1){
   					qBodyItem.find("input[name='quId'][value='" + bean.id + "']").val("");
   					bean.id = "";
   				}
   			});
   			form.render();
   		}
	    
	    var exam = {
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
	    layui.exam = exam;
	    
	    // 删除题目
	    $("body").on("click", ".dwQuDelete", function() {
			var quBody = $(this).parents(".surveyQuItemBody");
			layer.confirm("确认要删除此题吗？", { icon: 3, title: '删除题目' }, function (index) {
				layer.close(index);
				var quId = quBody.find("input[name='quId']").val();
				if(!isNull(quId)){
					AjaxPostUtil.request({url:schoolBasePath + "exam015", params:{quId: quId}, type: 'json', callback: function (json) {
		 	   			if (json.returnCode == 0) {
		 	   				quBody.hide("slow", function() {
		 	   					$(this).parent().remove();
		 	   					// 重置序号
								resetQuItem();
								// 重置左侧设计目录序号
								resetQuLeftItem();
		 	   				});
		 	   			} else {
		 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		 	   			}
		 	   		}});
				} else {
					quBody.hide("slow", function() {
						$(this).parent().remove();
						// 重置序号
						resetQuItem();
						// 重置左侧设计目录序号
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
			if(!isNull(quOptionId) && quOptionId != "0" ){
				AjaxPostUtil.request({url:schoolBasePath + "exam016", params:{quItemId: quOptionId}, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
	 	   				delQuOptionCallBack(optionParent);
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
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
			if(!isNull(quOptionId) && quOptionId != "0" ){
				AjaxPostUtil.request({url:schoolBasePath + "exam017", params:{quItemId: quOptionId}, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
	 	   				delQuOptionCallBack(optionParent);
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
	 	   		}});
			} else {
				delQuOptionCallBack(optionParent);
			}
		}
		
		// 知识点选择
		$("body").on("click", ".knowledgeQuLogic", function() {
			var _this = this;
			var oldKnowIds = $(_this).attr("knowledgeIds");
			schoolKnowledgeMationList = new Array();
			if(!isNull(oldKnowIds)){
				$.each(oldKnowIds.split(','), function(i, item){
					schoolKnowledgeMationList.push({
						id: item
					});
				});
			}
			_openNewWindows({
				url: "../../tpl/schoolKnowledgePoints/schoolKnowledgePointsChoose.html", 
				title: "知识点选择",
				pageId: "schoolKnowledgePointsChoose",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						var strIds = new Array();
						$.each(schoolKnowledgeMationList, function(i, item){
							strIds.push(item.id);
						});
						$(_this).attr("knowledgeIds", strIds.toString());
						$(_this).find(".quKnowledgeInfo").html(schoolKnowledgeMationList.length);
						// 设置题目为编辑，可以进行保存
						var quItemBody = $(_this).parents(".surveyQuItemBody");
						quItemBody.find("input[name='saveTag']").val(0);
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
	                }
				}});
		});
		
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	    // 题库选择试题
	    $("body").on("click", "#questionBankBtn", function() {
	    	questionMationList = [];
	    	allSave();
	    	_openNewWindows({
				url: "../../tpl/schoolQuestionBank/schoolQuestionBankChoose.html", 
				title: "试题选择",
				pageId: "schoolQuestionBankChoose",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
	                	initPageJson(function(){
	                		quIndex = 0, quLeftIndex = 0;
	                		$.each(questionMationList, function(i, item){
	                			// 加载试题数据
	                			item.saveTag = 0;
	                			item.orderById = (loadPageJson.rows.length + 1);
	                			loadPageJson.rows.push(item);
	                			// 加载右侧列表数据
	                			loadPageJson.bean.questionLeftList.push({
	                				quType: item.quType,
	                				quTitle: item.quTitle,
	                				fraction: item.fraction
	                			});
	                		});
	                		initPage();
	                	});
	    				form.render();
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
	                }
				}});
	    });
	    
	    // 保存
	    $("body").on("click", "#saveBtn", function() {
	    	allSave();
	    });
	    
	    function allSave(){
	    	curEditCallback();
			dwCommonDialogHide();
			resetQuItemHover(null);
			
			winui.window.msg('保存中', {icon: 1,time: 1000});
			saveSurvey(function(){
				isSaveProgress = false;
				winui.window.msg(systemLanguage["com.skyeye.addOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			});
	    }
	    
	    function saveSurvey(callback){
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
	    
	    /**
	     * 公共获取提交参数部分
	     */
	    function getCommonParams(quItemBody){
	    	var fileUrl = "";
	    	var tabIndex = quItemBody.find(".layui-tab").find(".layui-tab-title").find(".layui-this").index();
	    	var checkNameIn = getNameCheckIn(quItemBody);
	    	var whetherUpload = quItemBody.find("input[name='whetherUpload" + checkNameIn + "']:checked").val();
	    	if(tabIndex != 0 && tabIndex >= 0){
        		// 1-视频,2-音频,3-图片
        		fileUrl = quItemBody.find(".layui-tab-content").find(".layui-show").find(".upload").find("input[type='hidden'][name='upload']").attr("oldurl");
        		if(isNull(fileUrl)){
        			winui.window.msg('请上传文件.', {icon: 2, time: 2000});
        			throw err = new Error('请上传文件');
        		}
        	} else {
        		fileUrl = "";
        	}
        	if(tabIndex == -1){
        		tabIndex = 0;
        	}
	    	return {
	    		belongId: parent.rowId,
	    		orderById: quItemBody.find("input[name='orderById']").val(),
	    		tag: svTag,
	    		quId: quItemBody.find("input[name='quId']").val(),
	    		hv: quItemBody.find("input[name='hv']").val(),
	    		randOrder: quItemBody.find("input[name='randOrder']").val(),
				cellCount: quItemBody.find("input[name='cellCount']").val(),
				quTitle: encodeURI(quItemBody.find(".quCoTitleEdit").html()),
				fraction: isNull(quItemBody.find("input[name='fraction']").val()) ? 0 : quItemBody.find("input[name='fraction']").val(),
				knowledgeIds: isNull(quItemBody.find("li[class='knowledgeQuLogic']").attr("knowledgeIds")) ? 
								"" : quItemBody.find("li[class='knowledgeQuLogic']").attr("knowledgeIds"),
				fileUrl: fileUrl,
    			fileType: tabIndex,
    			whetherUpload: whetherUpload
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
	    		if(data.hv == 3) {
	    			//还有是table的情况需要处理
	    			quItemOptions = quItemBody.find(".quCoItem table.tableQuColItem tr td");
	    		} else {
	    			quItemOptions = quItemBody.find(".quCoItem li.quCoItemUlLi");
	    		}
	    		var radioTd = [];
	    		$.each(quItemOptions, function(i) {
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			if(quItemSaveTag == 0) {
	    				// 是否是默认答案  1.是  2.否
		    			var isDefaultAnswer = 2;
		    			if($(this).find("input[type='radio']").is(':checked')){
		    				isDefaultAnswer = 1
		    			}
	    				var s = {
    						optionValue: encodeURI($(this).find("label.quCoOptionEdit").html()),
    						optionId: $(this).find(".quItemInputCase input[name='quItemId']").val(),
    						isNote: $(this).find(".quItemInputCase input[name='isNote']").val(),
    						checkType: $(this).find(".quItemInputCase input[name='checkType']").val(),
    						isRequiredFill: $(this).find(".quItemInputCase input[name='isRequiredFill']").val(),
    						isDefaultAnswer: isDefaultAnswer,
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
	    		
	    		AjaxPostUtil.request({url:schoolBasePath + "exam010", params:data, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
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
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
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
	    		AjaxPostUtil.request({url:schoolBasePath + "exam018", params:{quItemId: quOptionId}, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
	 	   				delQuOptionCallBack(optionParent);
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
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
	    		if(data.hv == 3) {
	    			//还有是table的情况需要处理
	    			quItemOptions = quItemBody.find(".quCoItem table.tableQuColItem tr td");
	    		} else {
	    			quItemOptions = quItemBody.find(".quCoItem li.quCoItemUlLi");
	    		}
	    		var checkboxTd = [];
	    		$.each(quItemOptions, function(i) {
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			if(quItemSaveTag == 0) {
	    				// 是否是默认答案  1.是  2.否
		    			var isDefaultAnswer = 2;
		    			if($(this).find("input[type='checkbox']").is(':checked')){
		    				isDefaultAnswer = 1
		    			}
	    				var s = {
    						optionValue: encodeURI($(this).find("label.quCoOptionEdit").html()),
    						optionId: $(this).find(".quItemInputCase input[name='quItemId']").val(),
    						isNote: $(this).find(".quItemInputCase input[name='isNote']").val(),
    						checkType: $(this).find(".quItemInputCase input[name='checkType']").val(),
    						isRequiredFill: $(this).find(".quItemInputCase input[name='isRequiredFill']").val(),
    						isDefaultAnswer: isDefaultAnswer,
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
	    		AjaxPostUtil.request({url:schoolBasePath + "exam011", params:data, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
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
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
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
	    		AjaxPostUtil.request({url:schoolBasePath + "exam019", params:{quItemId: quOptionId}, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
	 	   				delQuOptionCallBack(optionParent);
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
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
    				checkType: quItemBody.find("input[name='checkType']").val(),
    				isDefaultAnswer: quItemBody.find("input[class='quFillblankAnswerInput']").val()
	    		};
	    		$.extend(data, getCommonParams(quItemBody));
	    		// 逻辑选项
	    		var list = [].concat(getLogic(quItemBody));
	    		data.logic = JSON.stringify(list);
	    		AjaxPostUtil.request({url:schoolBasePath + "exam006", params:data, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
		 	   			var quId = json.bean.quId;
		 	   			quItemBody.find("input[name='saveTag']").val(1);
		 	   			quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
						quItemBody.find("input[name='quId']").val(quId);
						// 同步logic Id信息
						var quLogics = json.bean.quLogics;
						if(!isNull(quLogics)){
							$.each(quLogics, function(i, item) {
								var logicItem = quItemBody.find(".quLogicItem_" + item.title);
								logicItem.find("input[name='quLogicId']").val(item.id);
								logicItem.find("input[name='logicSaveTag']").val(1);
							});
						}
						// 执行保存下一题
						saveQus(quItemBody.next(), callback);
						// 同步-更新题目排序号
						quCBNum2++;
						exeQuCBNum();
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
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
	    		AjaxPostUtil.request({url:schoolBasePath + "exam007", params:data, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
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
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
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
	    		AjaxPostUtil.request({url:schoolBasePath + "exam020", params:{quItemId: quOptionId}, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
	 	   				delQuOptionCallBack(optionParent);
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
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
	    		
	    		AjaxPostUtil.request({url:schoolBasePath + "exam008", params:data, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
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
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
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
	    		AjaxPostUtil.request({url:schoolBasePath + "exam021", params:{quItemId: quOptionId}, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
	 	   				delQuOptionCallBack(optionParent);
	 	   				rmQuOrderTableTr.remove();
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
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
	    		AjaxPostUtil.request({url:schoolBasePath + "exam009", params:data, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
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
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
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
	    		AjaxPostUtil.request({url:schoolBasePath + "exam013", params:data, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
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
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
	 	   		}});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /** 新保存多项填空题 **/
	    function saveMultiFillblank(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var data = {
    				paramInt01: quItemBody.find("input[name='paramInt01']").val(),
    				paramInt02: quItemBody.find("input[name='paramInt02']").val()
	    		};
	    		$.extend(data, getCommonParams(quItemBody));
	    		// 评分题选项td
	    		var quItemOptions = quItemBody.find(".quCoItem table.mFillblankTable tr");
	    		var multiFillblankTd = [];
	    		$.each(quItemOptions, function(i) {
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			if(quItemSaveTag == 0) {
	    				var s = {
    						optionValue: encodeURI($(this).find("label.quCoOptionEdit").html()),
    						optionId: $(this).find(".quItemInputCase input[name='quItemId']").val(),
    						isDefaultAnswer: $(this).find("input[class='multiFillBlank']").val(),
    						key: i
    	    			};
	    				multiFillblankTd.push(s);
	    			}
	    			// 更新 字母 title标记到选项上.
	    			$(this).addClass("quOption_" + i);
	    		});
	    		data.multiFillblankTd = JSON.stringify(multiFillblankTd);
	    		// 逻辑选项
	    		var list = [].concat(getLogic(quItemBody));
	    		data.logic = JSON.stringify(list);
	    		AjaxPostUtil.request({url:schoolBasePath + "exam012", params:data, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
		 	   			var quId = json.bean.quId;
		 	   			quItemBody.find("input[name='saveTag']").val(1);
		 	   			quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
			 	   		quItemBody.find("input[name='quId']").val(quId);
			 	   		
			 	   		// 重置问题选项和业务逻辑
			 	   		resetLogicAndItem(quItemBody, json);
			 	   		
						// 执行保存下一题
						saveQus(quItemBody.next(), callback);
						// 同步-更新题目排序号
						quCBNum2++;
						exeQuCBNum();
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
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
	    		AjaxPostUtil.request({url:schoolBasePath + "exam022", params:{quItemId: quOptionId}, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
	 	   				delQuOptionCallBack(optionParent);
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
	 	   		}});
	    	} else {
	    		delQuOptionCallBack(optionParent);
	    	}
	    }

	    /** 保存矩阵题 **/
	    function saveChen(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var data = {
    				quType: quItemBody.find("input[name='quType']").val()
	    		};
	    		$.extend(data, getCommonParams(quItemBody));
	    		// 矩阵列选项td
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
	    			// 更新 字母 title标记到选项上.
	    			$(this).addClass("quColumnOption_" + i);
	    		});
	    		data.column = JSON.stringify(column);
	    		// 矩阵行选项td
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
	    			// 更新 字母 title标记到选项上.
	    			$(this).addClass("quRowOption_" + i);
	    		});
	    		
	    		// 答案
	    		if(data.quType === "CHENCHECKBOX"){
		    		var answer = quItemBody.find(".quCoItem table.quCoChenTable tr input.chenCheckBoxInput");
		    		var isDefaultAnswer = new Array();
		    		var columuLength = quColumnOptions.length;
		    		var xIndex = 0;
		    		var yIndex = 1;
		    		$.each(answer, function(i) {
		    			if(i % columuLength == 0){
		    				xIndex++;
		    				yIndex = 1;
		    			} else {
			    			yIndex++;
		    			}
		    			var s = {
		    				x: xIndex,
		    				y: yIndex,
		    				value: $(this).prop("checked")
		    			};
		    			isDefaultAnswer.push(s);
		    		});
		    		data.isDefaultAnswer = JSON.stringify(isDefaultAnswer);
    			}else if(data.quType === "CHENRADIO"){
    				var answer = quItemBody.find(".quCoItem table.quCoChenTable tr input.chenRadioInput");
		    		var isDefaultAnswer = new Array();
		    		var columuLength = quColumnOptions.length;
		    		var xIndex = 0;
		    		var yIndex = 1;
		    		$.each(answer, function(i) {
		    			if(i % columuLength == 0){
		    				xIndex++;
		    				yIndex = 1;
		    			} else {
			    			yIndex++;
		    			}
		    			var s = {
		    				x: xIndex,
		    				y: yIndex,
		    				value: $(this).prop("checked")
		    			};
		    			isDefaultAnswer.push(s);
		    		});
		    		data.isDefaultAnswer = JSON.stringify(isDefaultAnswer);
    			}else if(data.quType === "CHENSCORE"){
    				var answer = quItemBody.find(".quCoItem table.quCoChenTable tr select.quChenScoreSelect");
		    		var isDefaultAnswer = new Array();
		    		var columuLength = quColumnOptions.length;
		    		var xIndex = 0;
		    		var yIndex = 1;
		    		$.each(answer, function(i) {
		    			if(i % columuLength == 0){
		    				xIndex++;
		    				yIndex = 1;
		    			} else {
			    			yIndex++;
		    			}
		    			var s = {
		    				x: xIndex,
		    				y: yIndex,
		    				value: $(this).val()
		    			};
		    			isDefaultAnswer.push(s);
		    		});
		    		data.isDefaultAnswer = JSON.stringify(isDefaultAnswer);
    			}else if(data.quType === "CHENFBK"){
    				var answer = quItemBody.find(".quCoItem table.quCoChenTable tr input.questionChenColumnValue");
		    		var isDefaultAnswer = new Array();
		    		var columuLength = quColumnOptions.length;
		    		var xIndex = 0;
		    		var yIndex = 1;
		    		$.each(answer, function(i) {
		    			if(i % columuLength == 0){
		    				xIndex++;
		    				yIndex = 1;
		    			} else {
			    			yIndex++;
		    			}
		    			var s = {
		    				x: xIndex,
		    				y: yIndex,
		    				value: $(this).val()
		    			};
		    			isDefaultAnswer.push(s);
		    		});
		    		data.isDefaultAnswer = JSON.stringify(isDefaultAnswer);
    			}
	    		
	    		data.row = JSON.stringify(row);
	    		// 逻辑选项
	    		var list = [].concat(getLogic(quItemBody));
	    		data.logic = JSON.stringify(list);
	    		AjaxPostUtil.request({url:schoolBasePath + "exam014", params:data, type: 'json', callback: function (json) {
	 	   			if (json.returnCode == 0) {
		 	   			var quId = json.bean.quId;
		 	   			quItemBody.find("input[name='saveTag']").val(1);
		 	   			quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
			 	   		quItemBody.find("input[name='quId']").val(quId);
			 	   		// 列选项
    					var quColumnItems = json.bean.quColumnItems;
    					if(!isNull(quColumnItems)){
    						$.each(quColumnItems, function(i, item) {
    							var quItemOption = quItemBody.find(".quColumnOption_" + item.title);
    							quItemOption.find("input[name='quItemId']").val(item.id);
    							quItemOption.find(".quItemInputCase input[name='quItemSaveTag']").val(1);
    						});
    					}
    					// 行选项
    					var quRowItems = json.bean.quRowItems;
    					if(!isNull(quRowItems)){
	    					$.each(quRowItems, function(i, item) {
	    						var quItemOption = quItemBody.find(".quRowOption_" + item.title);
	    						quItemOption.find("input[name='quItemId']").val(item.id);
	    						quItemOption.find(".quItemInputCase input[name='quItemSaveTag']").val(1);
	    					});
    					}
    					// 同步logic Id信息
    					var quLogics = json.bean.quLogics;
    					if(!isNull(quLogics)){
    						$.each(quLogics, function(i, item) {
    							var logicItem = quItemBody.find(".quLogicItem_" + item.title);
    							logicItem.find("input[name='quLogicId']").val(item.id);
    							logicItem.find("input[name='logicSaveTag']").val(1);
    						});
    					}
    					// 执行保存下一题
    					saveQus(quItemBody.next(), callback);
    					// 同步-更新题目排序号
    					quCBNum2++;
    					exeQuCBNum();
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
	 	   		}});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    //分数变化事件
	    $("body").on("change", "#dwBodyLeftContent .exam-fraction", function() {
	    	fractionChange($(this));
	    });
	    $("body").on("keyup", "#dwBodyLeftContent .exam-fraction", function() {
	    	fractionChange($(this));
	    });
	    
	    //分数变化
	    function fractionChange(_this){
	    	var quId = _this.attr("toid");//问题id
	    	var value = _this.val();//内容
	    	if((/^(\+|-)?\d+$/.test(value)) && value > 0){
	    		var questionCase = $("#dwSurveyQuContent").find("input[name='quId'][value='" + quId + "']").parent();
	    		questionCase.find("input[name='fraction']").val(value);
	    		questionCase.find("input[name='saveTag']").val("0");
	    	} else {
	    		notify("请输入正整数分数！", 800);
	    	}
	    }
	    
	    function pageLoadAfter(quId, tabIndex, fileUrl){
	    	if(isNull(quId)){
	    		quId = getRandomValueToString();
	    		$("#questionVedio").attr("id", "questionVedio" + quId);
	    		$("#questionAudio").attr("id", "questionAudio" + quId);
	    		$("#questionPicture").attr("id", "questionPicture" + quId);
	    	}
 	    	// 初始化视频上传
	 		$("#questionVedio" + quId).upload({
	            "action": reqBasePath + "common003",
	            "data-num": "1",
	            "data-type": vedioType,
	            "uploadType": 16,
	            "data-value": (tabIndex == 1 && !isNull(fileUrl)) ? fileUrl : "",
	            "function": function (_this, data) {
	                show(_this, data);
	            }
	        });
	        
	        // 初始化音频上传
	 		$("#questionAudio" + quId).upload({
	            "action": reqBasePath + "common003",
	            "data-num": "1",
	            "data-type": audioType,
	            "uploadType": 16,
	            "data-value": (tabIndex == 2 && !isNull(fileUrl)) ? fileUrl : "",
	            "function": function (_this, data) {
	                show(_this, data);
	            }
	        });
	        
	        // 初始化图片上传
	 		$("#questionPicture" + quId).upload({
	            "action": reqBasePath + "common003",
	            "data-num": "1",
	            "data-type": imageType,
	            "uploadType": 16,
	            "data-value": (tabIndex == 3 && !isNull(fileUrl)) ? fileUrl : "",
	            "function": function (_this, data) {
	                show(_this, data);
	            }
	        });
 	    }
	    
	});
	    
});