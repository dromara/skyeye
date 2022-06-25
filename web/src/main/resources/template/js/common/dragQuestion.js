

// 问卷-基本题型
var _basemodel;
// 问卷-矩阵题型
var _rectanglemodel;
// 问卷-辅助组件
var _auxiliarymodel;
// 问卷-操作
var _operationmodel;
// 问卷-常用题型
var _commonlyusedmodel;
// 问卷-各种模板
var _varioustemplates;

// 学校试卷-基本题型
var _basemodelSchool;
// 学校试卷-矩阵题型
var _rectanglemodelSchool;
// 学校试卷-辅助组件
var _auxiliarymodelSchool;
// 学校试卷-逻辑设计模板
var _varioustemplatesSchool;

var isDrag = false;
// 题目保存后回调时机比较参数
var quCBNum = 0;// 比较值1
var quCBNum2 = 0;// 比较值2
var curEditObj = null;
var curEditObjOldHtml = "";
var dwDialogObj = null;
var appQuObj = null;
var isSort = false;
// 即将编辑的内容
var ueEditObj = null;
// 问题选项设置
var quOptionDesign = null;
var type;

var element = null;
var form = null;

layui.define(["jquery", "form", "element"], function(exports) {
	var jQuery = layui.jquery;
		form = layui.form;
	element = layui.element;
	(function($) {
		// type：根据这个字段加载对应的文件1.问卷2.试卷
		type = getJSUrlParam()["type"];
		
		if(type == 1){
			/**
			 * 问卷模块
			 */
			// 基本题型
			_basemodel = getFileContent('tpl/common/dragQuestion/dragmodel/basemodel/radioQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/basemodel/checkboxQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/basemodel/fillblankQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/basemodel/scoreQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/basemodel/orderQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/basemodel/mfillblankQuModel.tpl');
			// 矩阵题型
			_rectanglemodel = getFileContent('tpl/common/dragQuestion/dragmodel/rectanglemodel/chenRadioQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/rectanglemodel/chenCheckboxQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/rectanglemodel/chenScoreQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/rectanglemodel/chenFillblankQuModel.tpl');
			// 辅助组件
			_auxiliarymodel = getFileContent('tpl/common/dragQuestion/dragmodel/auxiliarymodel/pageQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/auxiliarymodel/paragraphQuModel.tpl');
			// 操作
			_operationmodel = getFileContent('tpl/common/dragQuestion/dragmodel/operationmodel/surveyAttrSetToolbar.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/operationmodel/surveyStyleEditToolbar.tpl');
			// 常用题型
			_commonlyusedmodel = getFileContent('tpl/common/dragQuestion/dragmodel/commonlyusedmodel/userNameQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/commonlyusedmodel/phoneNoQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/commonlyusedmodel/addressQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/commonlyusedmodel/birthdayQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/commonlyusedmodel/emailQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/commonlyusedmodel/genderQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/commonlyusedmodel/educationQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/commonlyusedmodel/cityQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/commonlyusedmodel/maritalQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/commonlyusedmodel/companyQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/commonlyusedmodel/salaryQuModel.tpl')
							+ getFileContent('tpl/common/dragQuestion/dragmodel/commonlyusedmodel/dateQuModel.tpl');
			// 各种模板
			_varioustemplates = getFileContent('tpl/common/dragQuestion/dwcommon/dwCommonEditRoot.tpl');
		}else if(type == 2){
			/**
			 * 学校试卷模块
			 */
			// 基本题型
		    _basemodelSchool = getFileContent('tpl/common/schoolExam/dragmodel/basemodel/radioQuModel.tpl')
		    				+ getFileContent('tpl/common/schoolExam/dragmodel/basemodel/checkboxQuModel.tpl')
		    				+ getFileContent('tpl/common/schoolExam/dragmodel/basemodel/fillblankQuModel.tpl')
		    				+ getFileContent('tpl/common/schoolExam/dragmodel/basemodel/scoreQuModel.tpl')
		    				+ getFileContent('tpl/common/schoolExam/dragmodel/basemodel/orderQuModel.tpl')
		    				+ getFileContent('tpl/common/schoolExam/dragmodel/basemodel/mfillblankQuModel.tpl');
		    // 矩阵题型
		    _rectanglemodelSchool = getFileContent('tpl/common/schoolExam/dragmodel/rectanglemodel/chenRadioQuModel.tpl')
							+ getFileContent('tpl/common/schoolExam/dragmodel/rectanglemodel/chenCheckboxQuModel.tpl')
							+ getFileContent('tpl/common/schoolExam/dragmodel/rectanglemodel/chenScoreQuModel.tpl')
							+ getFileContent('tpl/common/schoolExam/dragmodel/rectanglemodel/chenFillblankQuModel.tpl');
		    // 辅助组件
		    _auxiliarymodelSchool = getFileContent('tpl/common/schoolExam/dragmodel/auxiliarymodel/pageQuModel.tpl')
							+ getFileContent('tpl/common/schoolExam/dragmodel/auxiliarymodel/paragraphQuModel.tpl');
		    // 逻辑设计模板
		    _varioustemplatesSchool = getFileContent('tpl/common/schoolExam/examcommon/examCommonEditRoot.tpl');
		}
		
		$("body").on("click", ".SeniorEdit", function(e){
			ueEditObj = $(curEditObj).html();
			_openNewWindows({
				url: "../../tpl/dwsurveydesign/designSurveyEditor.html", 
				title: "内容",
				area: ['500px', '500px'],
				pageId: "designSurveyEditor",
				callBack: function(refreshCode){
	                if (refreshCode == '0') {
	                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
	                	setCurEditContent(ueEditObj);
	            		curEditCallback();
	            		ueEditObj = null;
	            		curEditObj = null;
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
	                }
				}});
			return false;
		});
		
		$("body").on("click", ".option_Set", function(){
			showUIDialog($(curEditObj));
			return false;
		});
		
		$("body").on("click", "input[name='quOption_isNote']", function(){
			var optionCk = $(this).prop("checked");
			if(optionCk){
				$(".quOptionFillContentLi,.quOptionFillRequiredLi").show();
				$("#modelUIDialog").dialog("option","height",230);
			}else{
				$(".quOptionFillContentLi,.quOptionFillRequiredLi").hide();
			}
		});
		
		$("body").on("click", "#dwSurveyQuContent .surveyQuItemBody", function(e){
			curEditCallback();
			dwCommonDialogHide();
			$(".surveyQuItemBody").removeClass("hover");
			$(".surveyQuItemBody").find("input[name='hoverTag']").val("0");
			$(this).addClass("hover");
			// 上传插件会被阻止，加一层判断
			var ev = e || window.event;
			var target = ev.target || ev.srcElement;
			if($(target).attr("class") != "skyeye-upload"){
				return false;
			}
		});
		
		// 编辑按钮
		$("body").on("click", "#dwSurveyQuContent .editAble", function(){
			editAble($(this));
			return false;
		});
		
		// 扩展jQuery实例函数
		jQuery.fn.extend({
			/**
			 * 交换任意两个jQuery对象的位置
			 * @param another
			 */
			swap: function(another){
				var me = this;
				var cloneMe = me.clone();
				var temp = $('<span/>');
				another.before(temp);
				me.replaceWith(another);
				temp.replaceWith(cloneMe);
				return this;
			}
		});
		
		// 上移
		$("body").on("click", ".questionUp", function(){
			var nextQuBody = $(this).parents(".li_surveyQuItemBody");
			var prevQuBody = nextQuBody.prev();
			if(prevQuBody[0]){
				// 获取上一项的html
				var prevQuBodyHtml = prevQuBody.html();
				// 在当前项后面追加
				nextQuBody.after('<li class="li_surveyQuItemBody">' + prevQuBodyHtml + '</li>');
				var newNextObj = nextQuBody.next();
				newNextObj.hide();
				newNextObj.slideDown("slow");
				prevQuBody.slideUp("slow", function(){
					// 移除上一项
					prevQuBody.remove();
					// 重置序号
					resetQuItem();
					// 重置左侧设计目录序号
					resetQuLeftItem();
					bindQuHoverItem();
				});
				nextQuBody.find("input[name='saveTag']").val(0);
				newNextObj.find("input[name='saveTag']").val(0);
				form.render();
			}else{
				winui.window.msg("已经是第一个了！", {icon: 2,time: 1000});
			}
		});
		
		// 下移
		$("body").on("click", ".questionDown", function(){
			var prevQuBody = $(this).parents(".li_surveyQuItemBody");
			var nextQuBody = prevQuBody.next();
			if(nextQuBody[0]){
				var nextQuBodyHtml = nextQuBody.html();
				prevQuBody.before("<li class='li_surveyQuItemBody' >" + nextQuBodyHtml + "</li>");
				var newPrevObj = prevQuBody.prev();
				newPrevObj.hide();
				newPrevObj.slideDown("slow");
				nextQuBody.slideUp("slow", function(){
					nextQuBody.remove();
					// 重置序号
					resetQuItem();
					// 重置左侧设计目录序号
					resetQuLeftItem();
					bindQuHoverItem();
				});
				prevQuBody.find("input[name='saveTag']").val(0);
				newPrevObj.find("input[name='saveTag']").val(0);
				form.render();
			}else{
				winui.window.msg("已经是最后一个了！", {icon: 2,time: 1000});
			}
		});
		
		$("body").on("click", ".dwQuSet", function(){
			showDialog($(this));
			var quItemBody = $(this).parents(".surveyQuItemBody");
			resetQuItemHover(quItemBody);
			validateGen();
			return false;
		});
		
		// 逻辑设置 
		$("body").on("click", ".dwQuLogic", function(){
			showDialog($(this));
			var quItemBody = $(this).parents(".surveyQuItemBody");
			var quType = quItemBody.find("input[name='quType']").val();
			// 默认加载图标
			var fristQuItemBody = $("#dwSurveyQuContent .li_surveyQuItemBody").first();
			var method;
			if(type == 1){
				// 问卷
				method = layui.survey["SAVEQUS"];
			}else if(type == 2){
				// 试卷
				method = layui.exam["SAVEQUS"];
			}
			method(fristQuItemBody, function() {
				$(".dwQuDialogCon").hide();
				$("#dwCommonDialog .dwQuDialogLogic").show();
				resetQuItemHover(quItemBody);
				bindDialogRemoveLogic();
				$("#dwQuLogicTable").empty();
				// 逻辑数据回显示
				var quLogicItems = quItemBody.find(".quLogicItem");
				if(quLogicItems[0]) {
					$.each(quLogicItems, function() {
						var skQuId = $(this).find("input[name='skQuId']").val();
						var cgQuItemId = $(this).find("input[name='cgQuItemId']").val();
						var logicType = $(this).find("input[name='logicType']").val();
						// 设置分数 geLe scoreNum
						var geLe = "";
						var scoreNum = "";
						if(quType === "SCORE") {
							geLe = $(this).find("input[name='geLe']").val();
							scoreNum = $(this).find("input[name='scoreNum']").val();
						}
						var thClass = $(this).attr("class");
						thClass = thClass.replace("quLogicItem", "");
						thClass = thClass.replace(" ", "");
						// 回显相应的选项
						addQuDialogLogicTr(false, function() {
							// 执行成功--设置值
							var lastTr = $("#dwQuLogicTable").find("tr").last();
							lastTr.attr("class", thClass);
							lastTr.find(".logicQuOptionSel").val(cgQuItemId);
							lastTr.find(".logicQuSel").val(skQuId);
							lastTr.find(".logicType").val(logicType);
							lastTr.find(".logicQuOptionSel").change();
							lastTr.find(".logicQuSel").change();
							// 设置分数 geLe scoreNum
							if(quType === "SCORE") {
								lastTr.find(".logicScoreGtLt").val(geLe);
								lastTr.find(".logicScoreNum").val(scoreNum);
							}
						}, function() {});
					});
				} else {
					$(".dwQuDialogAddLogic").click();
				}
			});
			return false;
		});
		
		// 添加行选项
		$("body").on("click", ".addOption,.addColumnOption,.addRowOption", function() {
			var quItemBody = $(this).parents(".surveyQuItemBody");
			var quType = quItemBody.find("input[name='quType']").val();
			if(quType == "RADIO") {
				//添加单选选项
				editAble(addRadioItem(quItemBody, ""));
				form.render('radio');
			} else if(quType == "CHECKBOX") {
				editAble(addCheckboxItem(quItemBody, ""));
				form.render('checkbox');
			} else if(quType == "SCORE") {
				editAble(addScoreItem(quItemBody, "新选项"));
			} else if(quType == "ORDERBY") {
				editAble(addOrderquItem(quItemBody, "新选项"));
			} else if(quType == "MULTIFILLBLANK") {
				editAble(addMultiFillblankItem(quItemBody, "新选项"));
			} else if(quType == "CHENRADIO" || quType == "CHENCHECKBOX" || quType == "CHENFBK" || quType == "CHENSCORE") { //矩陈单选题,矩阵多选题
				editAble(addChenItem($(this), quItemBody, "新选项"));
				form.render('radio');
				form.render('checkbox');
			}
			bindQuHoverItem();
			return false;
		});
		
		// 批量添加事件
		$("body").on("click", ".addMoreOption,.addMoreRowOption,.addMoreColumnOption", function(){
			showDialog($(this));
			var quItemBody = $(this).parents(".surveyQuItemBody");
			resetQuItemHover(quItemBody);
			return false;
		});
		
		// 填空题选项设置
		$("body").on("click", ".quFillblankItem .dwFbMenuBtn", function(){
			showUIDialog($(this));
			return false;
		});
		
		$("body").on("click", ".dwOptionUp", function(){
			//判断类型区别table跟ul中的排序
			var quItemBody = $(curEditObj).parents(".surveyQuItemBody");
			var quType = quItemBody.find("input[name='quType']").val();
			var hv = quItemBody.find("input[name='hv']").val();
			if(hv == 3){
				var nextTd = $(curEditObj).parents("td");
				var prevTd = nextTd.prev();
				if(prevTd[0]){
					dwOptionUp(prevTd, nextTd);
				}else{
					var nextTr = $(curEditObj).parents("tr");
					var prevTr = nextTr.prev();
					if(prevTr[0]){
						prevTd = prevTr.find("td").last();
						dwOptionUp_1(prevTr, nextTr);
					}else{
						alert("已经是第一个了！");
					}
				}
			}else{
				var nextLi = null;
				var prevLi = null;
				var nextLiAfterHtml = "";
				if(quType === "RADIO" || quType === "CHECKBOX" || quType === "ORDERBY"){
					nextLi = $(curEditObj).parents("li.quCoItemUlLi");
					prevLi = nextLi.prev();
					var prevLiHtml = prevLi.html();
					nextLiAfterHtml = "<li class='quCoItemUlLi'>" + prevLiHtml + "</li>";
				}else if(quType === "SCORE"){
					nextLi = $(curEditObj).parents("tr.quScoreOptionTr");
					prevLi = nextLi.prev();
					var prevLiHtml = prevLi.html();
					nextLiAfterHtml = "<tr class='quScoreOptionTr'>" + prevLiHtml + "</tr>";
				}else if(quType === "MULTIFILLBLANK"){
					nextLi = $(curEditObj).parents("tr.mFillblankTableTr");
					prevLi = nextLi.prev();
					var prevLiHtml = prevLi.html();
					nextLiAfterHtml = "<tr class='mFillblankTableTr'>" + prevLiHtml + "</tr>";
				}else if(quType === "CHENRADIO" || quType === "CHENCHECKBOX" || quType === "CHENSCORE" || quType === "CHENFBK"){
					nextLi = $(curEditObj).parents("tr.quChenRowTr");
					if(nextLi[0]){
						prevLi = nextLi.prev();
						var prevLiHtml = prevLi.html();
						nextLiAfterHtml = "<tr class='quChenRowTr'>" + prevLiHtml + "</tr>";
					}else{
						nextLi = $(curEditObj).parents("td.quChenColumnTd");
						prevLi = nextLi.prev();
						var prevLiHtml = prevLi.html();
						nextLiAfterHtml = "<td class='quChenColumnTd'>" + prevLiHtml + "</td>";
					}
				}
				if(nextLi!=null){
					if(prevLi[0]){
						$(nextLi).after(nextLiAfterHtml);
						prevLi.hide();
						prevLi.remove();
						var editOffset = nextLi.find("label.editAble").offset();
						$("#dwCommonEditRoot").show();
						$("#dwCommonEditRoot").offset({top:editOffset.top,left:editOffset.left});
						bindQuHoverItem();
						$(curEditObj).click();
						$(nextLi).find("input[name='quItemSaveTag']").val(0);
						$(nextLi).next().find("input[name='quItemSaveTag']").val(0);
						var quItemBody = $(curEditObj).parents(".surveyQuItemBody");
						quItemBody.find("input[name='saveTag']").val(0);
					}else{
						alert("已经是第一个了！");
					}
				}
			}
			return false;
		});
		
		$("body").on("click", ".dwOptionDown", function(){
			//判断类型区别table跟ul中的排序
			var quItemBody = $(curEditObj).parents(".surveyQuItemBody");
			var quType = quItemBody.find("input[name='quType']").val();
			var hv = quItemBody.find("input[name='hv']").val();
			if(hv == 3){
				var prevTd = $(curEditObj).parents("td");
				var nextTd = prevTd.next();
				if(nextTd[0]){
					dwOptionDown(prevTd, nextTd);
				}else{
					var nextTr = $(curEditObj).parents("tr");
					var prevTr = nextTr.prev();
					if(prevTr[0]){
						prevTd = prevTr.find("td").last();
						dwOptionUp_1(prevTr, nextTr);
					}else{
						alert("已经是第一个了！");
					}
				}
			}else{
				var prevLi = null;
				var nextLi = null;
				var prevLiBeforeHtml = "";
				if(quType === "RADIO" || quType === "CHECKBOX" || quType === "ORDERBY"){
					prevLi = $(curEditObj).parents("li.quCoItemUlLi");
					nextLi = prevLi.next();
					var nextLiHtml = nextLi.html();
					prevLiBeforeHtml = "<li class='quCoItemUlLi'>" + nextLiHtml + "</li>";
				}else if(quType === "SCORE"){
					prevLi = $(curEditObj).parents("tr.quScoreOptionTr");
					nextLi = prevLi.next();
					var nextLiHtml = nextLi.html();
					prevLiBeforeHtml = "<tr class='quScoreOptionTr'>" + nextLiHtml + "</tr>";
				}else if(quType === "MULTIFILLBLANK"){
					prevLi = $(curEditObj).parents("tr.mFillblankTableTr");
					nextLi = prevLi.next();
					var nextLiHtml = nextLi.html();
					prevLiBeforeHtml = "<tr class='mFillblankTableTr'>" + nextLiHtml + "</tr>";
				}else if(quType === "CHENRADIO" || quType === "CHENCHECKBOX" || quType === "CHENSCORE" || quType === "CHENFBK"){
					prevLi = $(curEditObj).parents("tr.quChenRowTr");
					if(prevLi[0]){
						nextLi = prevLi.next();
						var nextLiHtml = nextLi.html();
						prevLiBeforeHtml = "<tr class='quChenRowTr'>" + nextLiHtml + "</tr>";
					}else{
						prevLi = $(curEditObj).parents("td.quChenColumnTd");
						nextLi = prevLi.next();
						var nextLiHtml = nextLi.html();
						prevLiBeforeHtml="<td class='quChenColumnTd'>" + nextLiHtml + "</td>";
					}
				}
				
				if(nextLi[0]){
					$(prevLi).before(prevLiBeforeHtml);
					nextLi.hide();
					nextLi.remove();
					var editOffset=prevLi.find("label.editAble").offset();
					$("#dwCommonEditRoot").show();
					$("#dwCommonEditRoot").offset({top:editOffset.top,left:editOffset.left});
					bindQuHoverItem();
					$(curEditObj).click();
					$(prevLi).find("input[name='quItemSaveTag']").val(0);
					$(prevLi).prev().find("input[name='quItemSaveTag']").val(0);
					var quItemBody=$(curEditObj).parents(".surveyQuItemBody");
					quItemBody.find("input[name='saveTag']").val(0);
				}else{
					alert("已经是最后一个了！");
				}
			}
			
			return false;
		});
		
		$("body").on("click", ".dwOptionDel", function(){
			deleteDwOption();
			return false;
		});
		
		$("body").on("change", ".hat_province", function(){
			var thVal = $(this).val();
			$.each(addrs, function(i, item){
				var province = item.province;
				if(province === thVal){
					var citys = item.citys;
					$(".hat_city").empty();
					$(".hat_city").append("<option >－－请选择市－－</option>");
					$.each(citys, function(j, itemCity){
						var city = itemCity.city;
						$(".hat_city").append("<option value='" + city + "'>" + city + "</option>");
					});
				}
			});
		});
		
		$("body").on("change", ".hat_city", function(){
			var thProvince = $(this).prev().val();
			var thCity = $(this).val();
			$.each(addrs, function(i, item){
				var province = item.province;
				if(province === thProvince){
					var citys = item.citys;
					$.each(citys, function(j, itemCity){
						var city = itemCity.city;
						if(city === thCity){
							$(".hat_area").empty();
							$(".hat_area").append("<option >－－请选择区－－</option>");
							var areas = itemCity.areas;
							$.each(areas, function(k,itemArea){
								var area = itemArea.area;
								$(".hat_area").append("<option value='" + area + "'>" + area + "</option>");
							});
						}
					});
				}
			});
		});
		
		// 样式编辑
		$("body").on("click", "#surveyStyleEditToolbar", function(){
			console.log(1);
		});
		
	    // 选项卡切换
	    $("body").on("click", "#tabType li", function(){
	    	$("#tabType li").removeClass("current");
	    	$(this).addClass("current");
	    	$(".tools_tab_div").css('display','none');
	    	$("#" + $(this).attr("id").replace("_li", "")).css('display','block');
	    });
	    
	    // 逻辑设置时添加逻辑项
		$("body").on("click", ".dwQuDialogAddLogic", function(){
			addQuDialogLogicTr(true, function(){}, function(){
				alert("此题已经设置了任意选项!");
			});
			return false;
		});
		
		$("body").on("change", "input[name='setAutoContacts']", function(){
			var check = $(this).prop("checked");
			if(check){
				$(".contactsFieldLi").show();
			}else{
				$(".contactsFieldLi").hide();
			}
		});
		
		$("body").on("click", "#dwDialogSaveMoreItem", function(){
			var quItemBody = $(dwDialogObj).parents(".surveyQuItemBody");
			var quType = quItemBody.find("input[name='quType']").val();
			var areaVal = $("#dwQuMoreTextarea").val();
			var areaValSplits = areaVal.split("\n");
			$.each(areaValSplits, function(i,item){
				item = $.trim(item);
				if(item != ""){
					if(quType == "RADIO"){
						// 添加单选选项
						addRadioItem(quItemBody, item);
					}else if(quType == "CHECKBOX"){
						// 添加多选选项
						addCheckboxItem(quItemBody, item);	
					}else if(quType == "SCORE"){
						addScoreItem(quItemBody, item);
					}else if(quType == "ORDERBY"){
						addOrderquItem(quItemBody, item);
					}else if(quType == "MULTIFILLBLANK"){
						addMultiFillblankItem(quItemBody, item);
					}else if(quType == "CHENRADIO" || quType == "CHENCHECKBOX" || quType == "CHENFBK" || quType == "CHENSCORE"){
						addChenItem(dwDialogObj, quItemBody, item);
					}
				}
			});
			$("#dwQuMoreTextarea").val("");
			bindQuHoverItem();
			dwCommonDialogHide();
		});
		
		/**
	     * 高级编辑
	     */
	    $("body").on("click", ".dwComEditMenuBtn", function(e) {
	    	//dwComEditMenuBtn
	    	var dwMenuUl = $(".dwComEditMenuUl:visible");
	    	//根据当前编辑的对象
	    	var quItemBody = $(curEditObj).parents(".surveyQuItemBody");
	    	var quType = quItemBody.find("input[name='quType']").val();
	    	var curEditClass = $(curEditObj).attr("class");
	    	if(quType == "RADIO" || quType == "CHECKBOX") {
	    		if(curEditClass.indexOf("quCoTitleEdit") < 0) {
	    			$(".dwComEditMenuUl .option_Set_Li").show();
	    		} else {
	    			$(".dwComEditMenuUl .option_Set_Li").hide();
	    		}
	    	} else {
	    		$(".dwComEditMenuUl .option_Set_Li").hide();
	    	}

	    	if(dwMenuUl[0]) {
	    		$(".dwComEditMenuUl").hide();
	    	} else {
	    		$(".dwComEditMenuUl").show();
	    	}
	    	return false;
	    });
	    
	    /**
	     * 关闭设置弹出框
	     */
	    $("body").on("click", "#dwCommonDialogClose", function(){
			dwCommonDialogHide();
			resetQuItemHover(null);
	    });
	    
	    /**
	     * 保存逻辑设置 
	     */
	    $("body").on("click", "#dwDialogSaveLogic", function(){
	    	var quItemBody = $(dwDialogObj).parents(".surveyQuItemBody");
			var quLogicInputCase = quItemBody.find(".quLogicInputCase");
			var quType = quItemBody.find("input[name='quType']").val();
			var dwQuLogicTrs = $("#dwQuLogicTable tr");
			var quLogicItemHtml = $("#quLogicItemModel").html();
			$.each(dwQuLogicTrs, function(){
				var cgQuItemId = $(this).find(".logicQuOptionSel").val();
				var skQuId = $(this).find(".logicQuSel").val();
				var logicType = $(this).find(".logicType").val();
				var quLogicItemClass = $(this).attr("class");
				//判断已经保存过的，保存过的只做修改
				if(skQuId != "" && cgQuItemId != ""){
					var quLogicItem = quLogicInputCase.find("." + quLogicItemClass);
					if(quLogicItem[0]){
						//已经有值--检查值是否有发生变化 
						var oldSkQuId = quLogicItem.find("input[name='skQuId']").val();
						var oldCgQuItemId = quLogicItem.find("input[name='cgQuItemId']").val();
						var oldLogicType = quLogicItem.find("input[name='logicType']").val();
						if(oldSkQuId != skQuId || cgQuItemId != oldCgQuItemId || oldLogicType != logicType){
							quLogicItem.find("input[name='logicSaveTag']").val("0");
							quItemBody.find("input[name='saveTag']").val("0");
							//后来修复的
							quLogicItem.find("input[name='skQuId']").val(skQuId);
							quLogicItem.find("input[name='cgQuItemId']").val(cgQuItemId);
							quLogicItem.find("input[name='logicType']").val(logicType);
						}
						//如果是评分题
						if(quType === "SCORE"){
							var logicScoreGtLt = $(this).find(".logicScoreGtLt").val();
							var logicScoreNum = $(this).find(".logicScoreNum").val();
							quLogicItem.find("input[name='geLe']").val(logicScoreGtLt);
							quLogicItem.find("input[name='scoreNum']").val(logicScoreNum);
							quLogicItem.find("input[name='logicType']").val(logicType);
							//状态
							quLogicItem.find("input[name='logicSaveTag']").val("0");
							quItemBody.find("input[name='saveTag']").val("0");
						}
					}else{
						quLogicInputCase.append(quLogicItemHtml);
						quLogicItem = quLogicInputCase.find(".quLogicItem").last();
						quLogicItem.addClass(quLogicItemClass);
						//修改值
						quLogicItem.find("input[name='quLogicId']").val("");
						quLogicItem.find("input[name='skQuId']").val(skQuId);
						quLogicItem.find("input[name='cgQuItemId']").val(cgQuItemId);
						quLogicItem.find("input[name='visibility']").val("1");
						quLogicItem.find("input[name='logicType']").val(logicType);
						quItemBody.find("input[name='saveTag']").val("0");
						//如果是评分题
						if(quType === "SCORE"){
							var logicScoreGtLt = $(this).find(".logicScoreGtLt").val();
							var logicScoreNum = $(this).find(".logicScoreNum").val();
							quLogicItem.find("input[name='geLe']").val(logicScoreGtLt);
							quLogicItem.find("input[name='scoreNum']").val(logicScoreNum);
							quLogicItem.find("input[name='logicType']").val(logicType);
						}
					}
				}
			});
			refreshQuLogicInfo(quItemBody);
			dwCommonDialogHide();
			return false;
	    });
	    
	    /**
	     * 设置窗口保存事件
	     */
	    $("body").on("click", "#dwDialogQuSetSave", function(){
	    	if (dwDialogObj != null) {
	    		var quItemBody = $(dwDialogObj).parents(".surveyQuItemBody");
	    		var setIsRequired = $("#dwCommonDialog input[name='setIsRequired']:checked");
	    		var setRandOrder = $("#dwCommonDialog input[name='setRandOrder']:checked");
	    		var setHv = $("#dwCommonDialog select[name='setHv']").val();
	    		var setCellCount = $("#dwCommonDialog input[name='setCellCount']").val();
	    		var setAutoContacts = $("#dwCommonDialog input[name='setAutoContacts']:checked");
	    		var setContactsField = $("#dwCommonDialog select[name='setContactsField']").val();
	    		var oldHv = quItemBody.find("input[name='hv']").val();
	    		var oldCellCount = quItemBody.find("input[name='cellCount']").val();
	    		if(!isNull(setIsRequired)){
		    		quItemBody.find("input[name='isRequired']").val(setIsRequired[0] ? 1 : 0);
	    		}
	    		quItemBody.find("input[name='hv']").val(setHv);
	    		quItemBody.find("input[name='randOrder']").val(setRandOrder[0] ? 1 : 0);
	    		quItemBody.find("input[name='cellCount']").val(setCellCount);
	    		quItemBody.find("input[name='saveTag']").val(0);
	    		var quType = quItemBody.find("input[name='quType']").val();
	    		if (quType == "RADIO" || quType == "CHECKBOX" || quType == "FILLBLANK") {
	    			quItemBody.find("input[name='contactsAttr']").val(setAutoContacts[0] ? 1 : 0);
	    			quItemBody.find("input[name='contactsField']").val(setContactsField);
	    		} else if (quType == "SCORE") {
	    			quItemBody.find("input[name='paramInt01']").val(1);
	    			var paramInt02 = $("#dwCommonDialog .scoreMinMax .maxScore");
	    			if (paramInt02[0]) {
	    				quItemBody.find("input[name='paramInt02']").val(paramInt02.val());
	    			}
	    			//根据分数设置评分选项
	    			var paramInt01Val = 1;
	    			var paramInt02Val = paramInt02.val();
	    			var scoreNumTableTr = quItemBody.find(".scoreNumTable tr");
	    			$.each(scoreNumTableTr,
	    			function() {
	    				$(this).empty();
	    				for (var i = paramInt01Val; i <= paramInt02Val; i++) {
	    					$(this).append("<td>" + i + "</td>");
	    				}
	    			});
	    		} else if (quType === "MULTIFILLBLANK") {
	    			var paramInt01 = $("#dwCommonDialog .minMaxLi .minNum");
	    			if (paramInt01[0]) {
	    				quItemBody.find("input[name='paramInt01']").val(paramInt01.val());
	    			}
	    			quItemBody.find("input[name='paramInt02']").val(10);
	    		}
	    		var selVal = $(".option_range").val();
	    		if (selVal == 1) {
	    			//横排 transverse
	    			if (oldHv == 3) {
	    				quTableOptoin2Li(quItemBody);
	    			}
	    			quItemBody.find(".quCoItem ul").addClass("transverse");
	    		} else if (selVal == 2) {
	    			if (oldHv == 3) {
	    				quTableOptoin2Li(quItemBody);
	    			} else {
	    				//竖排
	    				quItemBody.find(".quCoItem ul").removeClass("transverse");
	    				quItemBody.find(".quCoItem ul li").width("");
	    			}
	    		} else if (selVal == 3) {
	    			if (!$("#dwCommonDialogForm").valid()) {
	    				notify("参数不对，请检查！", 800);
	    				return false;
	    			}
	    			if (oldHv == 3) {
	    				if (oldCellCount != setCellCount) {
	    					quTableOption2Table(quItemBody);
	    				}
	    			} else {
	    				quLiOption2Table(quItemBody);
	    			}
	    		}
	    	}
	    	dwCommonDialogHide();
	    	return false;
	    });
	    
	    /**
	     * 问卷设置
	     */
	    $("body").on("click", "#surveyAttrSetToolbar", function(e){
	    	_openNewWindows({
				url: "../../tpl/dwsurveydesign/designSurveyOp.html", 
				title: "问卷设置",
				area: ['500px', '500px'],
				pageId: "designSurveyOp",
				callBack: function(refreshCode){
	                if (refreshCode == '0') {
	                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
	                }
				}});
	    });
	    
	    $("body").on('click', '.layui-tab-title li', function(e) {
	    	var _tabIdIndex = $(this).attr("id").replace("tab", "");
	    	$(this).parent().find("li").removeClass("layui-this");
	    	$.each($(this).parent().find("img"), function(i){
				var src = $(this).attr("src");
				if(src.indexOf("-choose") != -1){
					src = src.replace("-choose.png", '') + '.png';
				}
				$(this).attr("src", src);
			});
			if(_tabIdIndex != 0){
				var src = $(this).find("img").attr("src");
				if(src.indexOf("-choose") != -1){
					src = src.replace("-choose.png", '') + '.png';
				}else{
					src = src.replace(".png", '') + '-choose.png';
				}
				$(this).find("img").attr("src", src);
			}
			$(this).addClass("layui-this");
			$(this).parent().parent().find(".layui-tab-item").removeClass("layui-show");
			$(this).parent().parent().find(".layui-tab-item").eq(_tabIdIndex).addClass("layui-show");
	    });
	    
	    // 是否允许拍照/上传图片变化,layui加载后的监听；试题选项变化事件
	    $("body").on("click", ".layui-elem-field .layui-form-radio," +
	    		".surveyQuItem .quCoItem ul," +
	    		".surveyQuItem .quCoItem table," +
	    		".layui-tab", function(e){
	    	// 设置为保存
	    	$(this).parents(".surveyQuItemBody").find("input[name='saveTag']").val(0);
	    	$(this).parents(".surveyQuItemBody").find(".quItemInputCase input[name='quItemSaveTag']").val(0);
	    });
		
	})(jQuery);
	exports('dragQuestion', null);
});

/**
 * 获取加载指定js后面拼接的参数
 * @return {}
 */
function getJSUrlParam() {
    var js = document.getElementsByTagName("script");
    // 得到当前引用a.js一行的script，并把src用'?'分隔成数组
    var indexJs = 0;
    $.each(js, function(i, item){
    	if(item.src.indexOf("dragQuestion.js") >= 0){
    		indexJs = i;
    		return false;
    	}
    });
    var arraytemp = js[indexJs].src.split('?');
    var obj = new Object();
    // 如果不带参数，则不执行下面的代码
    if (arraytemp.length > 1) {
        var params = arraytemp[1].split('&');
        for (var i = 0; i < params.length; i++) {
            var parm = params[i].split("=");
            // 将key和value定义给obj
            obj[parm[0]] = parm[1];
        }
    }
    return obj;
}

function loadDrag(){
	//拖入题目到问卷中
	$(".dragQuUl li").draggable({
	  	connectToSortable: "#dwSurveyQuContentAppUl",
	  	zIndex: 27000,
	  	cursor: "move",
	  	cursorAt: {
	  		left: 40,
	  		top: 25
	  	},
	  	scroll: true,
	  	scrollSensitivity: 30,
	  	scrollSpeed: 30,
	  	appendTo: "#dw_body_content",
	  	helper: function(event) {
	  		return $(this).find(".dwQuTypeModel").html();
	  	},
	  	start: function(event, ui) {
	  		isDrag = true;
	  		$("#tools_wrap").css({
	  			"zIndex": 30
	  		});
	  		resetQuItemHover(null);
	  		dwCommonDialogHide();
	  		curEditCallback();
	  	},
	  	drag: function(event, ui) {
	  		isDrag = true;
	  	},
	  	stop: function(event, ui) {
	  		if(!isSort) {
	  			$("#tools_wrap").animate({
	  				zIndex: 200
	  			}, 200, function() {
	  				// 重置序号
					resetQuItem();
					// 重置左侧设计目录序号
					resetQuLeftItem();
	  				bindQuHoverItem();
	  			});
	  		}
	  		if(false) {
	  			isDrag = false;
	  			// 判断加入----根据initLine显示状态来判断是否加入进去
	  			if(appQuObj != null) {
	  				$(appQuObj).before($(this).find(".dwQuTypeModel").html());
	  				$(appQuObj).prev().hide();
	  				$(appQuObj).prev().removeClass("quDragBody");
	  				$(appQuObj).prev().show("slow");
	  				// 重置序号
					resetQuItem();
					// 重置左侧设计目录序号
					resetQuLeftItem();
	  				bindQuHoverItem();
	  			}
	  		}
	  	}
	});
	
	$("#dwSurveyQuContentAppUl").sortable({
		revert: true,
		delay: 800,
		placeholder: "showLine",
		tolerance: "pointer",
		opacity: 0.7,
		handle: ".dwQuMove",
		scrollSensitivity: 30,
		scrollSpeed: 30,
		start: function(event, ui) {
			$("#tools_wrap").css({
				"zIndex": 30
			});
			$(".showLine").height(ui.item.height());
			dwCommonDialogHide();
			curEditCallback();
			isSort = true;
		},
		sort: function(event, ui) {
			isSort = true;
			$(".ui-sortable-placeholder").css({
				"background": "red"
			});
		},
		receive: function(event, ui) {
			// 当一个已连接的sortable对象接收到另一个sortable对象的元素后触发此事件。 
		},
		out: function(event, ui) {
			// 当一个元素拖拽移出sortable对象移出并进入另一个sortable对象后触发此事件。 
			isSort = false;
		},
		update: function(event, ui) {
			if(!isDrag) {
				// 根据排序ID，计算出是前排序，还是后排序
				$("#dwSurveyQuContentAppUl input[name='saveTag']").val(0);
			}
		},
		stop: function(event, ui) {
			if(isDrag) {
				isDrag = false;
				isSort = false;
				// 获取checkNameIn用作唯一标识
				var checkNameIn = getRandomValueToString();
				var paramJson = {
					checkNameIn: checkNameIn,
					checkNameInTr1: getRandomValueToString(),
					checkNameInTr2: getRandomValueToString()
				};
				// 加入试题到试卷
				ui.item.html(getDataUseHandlebars(ui.item.find(".dwQuTypeModel").html(), paramJson));
				ui.item.removeClass("ui-draggable");
				ui.item.find(".layui-tab").find("li").removeClass("ui-draggable");
				ui.item.find(".quDragBody").removeClass("quDragBody");
				// 新加入题-选定题目标题
				ui.item.find(".surveyQuItemBody").addClass("hover");
				ui.item.addClass("li_surveyQuItemBody");
				var quType = ui.item.find(".surveyQuItemBody input[name='quType']").val();
				if(quType != "PAGETAG") {
					editAble(ui.item.find(".surveyQuItemBody .quCoTitleEdit"));
				}
				if(type == 2){
					// 试卷才有
					// 加载上传插件
					pageLoadUpLoadAfter(checkNameIn);
				}
				form.render();
			}
			var curItemBodyOffset = ui.item.offset();
			$("html,body").animate({
				scrollTop: curItemBodyOffset.top - 370
			}, 500, function() {
				$("#tools_wrap").css({
					"zIndex": 200
				});
				// 重置序号
				resetQuItem();
				// 重置左侧设计目录序号
				resetQuLeftItem();
				bindQuHoverItem();
			});
		}
	});
}

function pageLoadUpLoadAfter(checkNameIn){
	// 初始化视频上传
	$("#questionVedio" + checkNameIn).upload({
        "action": reqBasePath + "common003",
        "data-num": "1",
        "data-type": vedioType,
        "uploadType": 16,
        "function": function (_this, data) {
            show(_this, data);
        }
    });
    
    // 初始化音频上传
	$("#questionAudio" + checkNameIn).upload({
        "action": reqBasePath + "common003",
        "data-num": "1",
        "data-type": audioType,
        "uploadType": 16,
        "function": function (_this, data) {
            show(_this, data);
        }
    });
    
    // 初始化图片上传
	$("#questionPicture" + checkNameIn).upload({
        "action": reqBasePath + "common003",
        "data-num": "1",
        "data-type": imageType,
        "uploadType": 16,
        "function": function (_this, data) {
            show(_this, data);
        }
    });
    
}

/**
 * 重置问题排序
 */
function resetQuItem(){
	if(isDrag){
		isDrag = false;
	}
	var surveyQuItems = $("#dwSurveyQuContent .surveyQuItemBody");
	var indexNum = 1;
	$.each(surveyQuItems,function(i){
		$(this).find(".quInputCase input[name='orderById']").val(i + 1);
		var quType = $(this).find("input[name='quType']").val();
		if(quType != "PAGETAG" && quType != "PARAGRAPH"){
			$(this).find(".quCoTitle .quCoNum").text((indexNum++) + "、");
		}
	});
	// 更新分标标记
	var pageTags = $("#dwSurveyQuContent .surveyQuItemBody input[name='quType'][value='PAGETAG']");
	var pageTagSize = pageTags.size() + 1;
	$.each(pageTags, function(i){
		var quItemBody = $(this).parents(".surveyQuItemBody");
		var pageQuContent = quItemBody.find(".pageQuContent");
		pageQuContent.text("下一页（" + ( i + 1 ) + "/" + pageTagSize + "）");
	});
}

/**
 * 重置左侧设计目录排序
 */
function resetQuLeftItem(){
	if(isDrag){
		isDrag = false;
	}
	var surveyQuItems = $("#dwSurveyQuContent .surveyQuItemBody");
	var indexNum = 1;
	var sa = "";
	$.each(surveyQuItems,function(i){
		var quType = $(this).find("input[name='quType']").val();
		if(quType != "PAGETAG" && quType != "PARAGRAPH"){
			var id = $(this).find("input[name='quId']").val();
			var title = $(this).find(".quCoTitle .quCoTitleEdit").html();
			if(type == 1){
				// 问卷
				sa += '<h2 class=""><a href="#' + id + '" class="ellipsis" toid="' + id + '">' + (indexNum++) + '、' + title + '</a></h2>';
			}else if(type == 2){
				// 学校试卷
				var fraction = $(this).find("input[name='fraction']").val();
				sa += '<h2 class=""><a href="#' + id + '" class="ellipsis" toid="' + id + '">' + (indexNum++) + '、' + title + '</a>' +
						'（<input class="exam-fraction" value="' + fraction + '" toid="' + id + '"/>分）</h2>';
			}
		}
	});
	$("#dwBodyLeftContent").html(sa);
}

function bindQuHoverItem(){
	$("#dwSurveyQuContent .surveyQuItemBody").unbind();
	$("#dwSurveyQuContent .surveyQuItemBody").hover(function(){
		//显示
		if(isDrag){
			appQuObj = $(this);
		}else{
			//显示
			$(this).addClass("hover");
			$(".pageBorderTop").removeClass("nohover");
			//如果是填空
			appQuObj = $(this);
		}
	}, function(){
		$(".pageBorderTop").addClass("nohover");
		$(this).removeClass("showLine");
		var hoverTag = $(this).find("input[name='hoverTag']").val();
		if(hoverTag != "hover"){
			$(this).removeClass("hover");
		}
		appQuObj = null;
	});
	
	$(".quCoItemUlLi").unbind();
	$(".quCoItemUlLi").hover(function(){
		if(!isDrag){
			$(this).addClass("hover");	
		}
	}, function(){
		var thClass = $(this).attr("class");
		if(thClass.indexOf("menuBtnClick") <= 0){
			$(this).removeClass("hover");
		}
	});
}

function dwOptionUp(prevTd,nextTd){
	var prevTdHtml = prevTd.html();
	$(nextTd).after("<td>"+prevTdHtml+"</td>");
	prevTd.hide();
	prevTd.remove();
	var editOffset = nextTd.find("label.editAble").offset();
	$("#dwCommonEditRoot").show();
	$("#dwCommonEditRoot").offset({top:editOffset.top, left:editOffset.left});
	bindQuHoverItem();
	$(curEditObj).click();
	$(nextTd).find("input[name='quItemSaveTag']").val(0);
	$(nextTd).next().find("input[name='quItemSaveTag']").val(0);
	var quItemBody = $(curEditObj).parents(".surveyQuItemBody");
	quItemBody.find("input[name='saveTag']").val(0);
}

function dwOptionUp_1(prevTr,nextTr){
	var prevTd = prevTr.find("td").last();
	var nextTd = nextTr.find("td").first();
	var prevTdHtml = prevTd.html();
	var nextTdHtml = nextTd.html();
	prevTd.before("<td>" + nextTdHtml + "</td>");
	$(nextTd).after("<td>" + prevTdHtml + "</td>");
	prevTd.hide();
	prevTd.remove();
	nextTd.hide();
	nextTd.remove();
	prevTd = prevTr.find("td").last();
	nextTd = nextTr.find("td").first();
	curEditObj = prevTd.find("label.editAble");
	var editOffset = prevTd.find("label.editAble").offset();
	$("#dwCommonEditRoot").show();
	$("#dwCommonEditRoot").offset({top:editOffset.top, left:editOffset.left});
	bindQuHoverItem();
	$(curEditObj).click();
	$(prevTd).find("input[name='quItemSaveTag']").val(0);
	$(nextTd).find("input[name='quItemSaveTag']").val(0);
	var quItemBody = $(curEditObj).parents(".surveyQuItemBody");
	quItemBody.find("input[name='saveTag']").val(0);
}

function dwOptionDown(prevTd,nextTd){
	var nextTdHtml = nextTd.html();
	$(prevTd).before("<td>" + nextTdHtml + "</td>");
	nextTd.hide();
	nextTd.remove();
	var editOffset = prevTd.find("label.editAble").offset();
	$("#dwCommonEditRoot").show();
	$("#dwCommonEditRoot").offset({top:editOffset.top, left:editOffset.left});
	bindQuHoverItem();
	$(curEditObj).click();
	$(prevTd).find("input[name='quItemSaveTag']").val(0);
	$(prevTd).next().find("input[name='quItemSaveTag']").val(0);
	var quItemBody = $(curEditObj).parents(".surveyQuItemBody");
	quItemBody.find("input[name='saveTag']").val(0);
}

function dwCommonDialogHide(){
	$("#dwCommonDialog").hide();
	$(".menuBtnClick").removeClass("menuBtnClick");
	dwDialogObj = null;
}

function resetQuItemHover(quItemBody){
	$(".surveyQuItemBody").removeClass("hover");
	$(".surveyQuItemBody").find("input[name='hoverTag']").val("0");
	if(quItemBody!=null){
		quItemBody.addClass("hover");
		quItemBody.find("input[name='hoverTag']").val("hover");
	}
}

/**
 * 触发显示题目标题编辑框
 * @param {} editAbleObj
 */
function editAble(editAbleObj){
	dwCommonDialogHide();
	curEditCallback();

	var quItemBody = $(editAbleObj).parents(".surveyQuItemBody");
	resetQuItemHover(quItemBody);

	var thClass = $(editAbleObj).attr("class");
	var editOffset = $(editAbleObj).offset();
	$("#dwCommonEditRoot").removeClass();
	if(thClass.indexOf("quCoTitleEdit") > 0){
		//题目标题
		$("#dwCommonEditRoot").addClass("quEdit");
	}else if(thClass.indexOf("quCoOptionEdit") > 0){
		//题目选项
		$("#dwCommonEditRoot").addClass("quOptionEdit");
	}else if(thClass.indexOf("dwSvyNoteEdit") >= 0){
		//问卷欢迎语
		$("#dwCommonEditRoot").addClass("svyNoteEdit");
	}else if(thClass.indexOf("dwSvyName") >= 0){
		$("#dwCommonEditRoot").addClass("svyName");
	}
	$("#dwCommonEditRoot").show();
	$("#dwCommonEditRoot").offset({top:editOffset.top,left:editOffset.left});
	$("#dwComEditContent").focus();
	$("#dwComEditContent").html($(editAbleObj).html());
	var dwEditWidth = $(editAbleObj).width();

	if(thClass.indexOf("dwSvyNoteEdit") < 0 && thClass.indexOf("dwSvyName") < 0){
		var hv = quItemBody.find("input[name='hv']").val();
		if(hv == 3){
			var dwEditText=$(editAbleObj).text();
			if(dwEditText == ""){
				dwEditWidth = $(editAbleObj).parents("td").width()-52;
			}
			dwEditWidth > 600 ? dwEditWidth = 600:dwEditWidth;
		}else{
			dwEditWidth < 200 ? dwEditWidth = 200:dwEditWidth > 600 ? dwEditWidth = 600:dwEditWidth;
		}
	}else{
		dwEditWidth=680;
	}
	
	$("#dwCommonEditRoot .dwCommonEdit").css("width",dwEditWidth);
	setSelectText($("#dwComEditContent"));
	curEditObj = $(editAbleObj);
	curEditObjOldHtml = $(editAbleObj).html();
}

/**
 * 加载地域
 */
function loadAddr(){
	$.each(addrs, function(i, item){
		var province = item.province;
		$(".hat_province").append("<option value='" + province + "'>" + province + "</option>");
	});
}

function setSelectText(el) {
    try {
        window.getSelection().selectAllChildren(el[0]); // 全选
        window.getSelection().collapseToEnd(el[0]); // 光标置后
    } catch (err) {
        // 在此处理错误
    }
}

/**
 * 显示模式窗口
 * @param {} thDialogObj
 */
function showUIDialog(thDialogObj) {
	quOptionDesign = thDialogObj;
	_openNewWindows({
		url: "../../tpl/dwsurveydesign/modelUIDialog.html", 
		title: "选项设置",
		area: ['500px', '500px'],
		pageId: "modelUIDialog",
		callBack: function(refreshCode){
            if (refreshCode == '0') {
            	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
            } else if (refreshCode == '-9999') {
            	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
            }
		}});
}

/**
 * 显示弹出层
 * @param {} thDialogObj
 */
function showDialog(thDialogObj){
	var thObjClass = thDialogObj.attr("class");
	curEditCallback();
	setShowDialogOffset(thDialogObj);
	var quItemBody = $(thDialogObj).parents(".surveyQuItemBody");
	$("#dwCommonDialog .dwQuDialogCon").hide();
	if(thObjClass.indexOf("addMoreOption") >= 0){
		$("#dwCommonDialog .dwQuAddMore").show();
	}else if(thObjClass.indexOf("dwQuSet") >= 0){
		$("#dwCommonDialog .dwQuSetCon").show();
		var quType = quItemBody.find("input[name='quType']").val();
		var isRequired = quItemBody.find("input[name='isRequired']").val();
		var hv = quItemBody.find("input[name='hv']").val();
		var randOrder = quItemBody.find("input[name='randOrder']").val();
		var cellCount = quItemBody.find("input[name='cellCount']").val();
		var paramInt01 = quItemBody.find("input[name='paramInt01']");
		var paramInt02 = quItemBody.find("input[name='paramInt02']");
		var contactsAttr = quItemBody.find("input[name='contactsAttr']").val();
		var contactsField = quItemBody.find("input[name='contactsField']").val();
		$("#dwCommonDialog input[name='setRandOrder']").prop("checked", false);
		$("#dwCommonDialog select[name='setHv']").val(2);
		$("#dwCommonDialog input[name='setAutoContacts']").prop("checked", false);
		$("#dwCommonDialog .contactsFieldLi").hide();
		$("#dwCommonDialog .contactsAttrLi").hide();
		$("#dwCommonDialog .optionAutoOrder").hide();
		$("#dwCommonDialog .optionRangeHv").hide();
		$("#dwCommonDialog .scoreMinMax").hide();
		$("#dwCommonDialog .minMaxLi").hide();
		// 问卷才有这个判断
		if(!isNull(isRequired)){
			$("#dwCommonDialog input[name='setIsRequired']").prop("checked", false);
			if(isRequired == 1){
				$("#dwCommonDialog input[name='setIsRequired']").prop("checked", true);
			}
		}
		if(randOrder == 1){
			$("#dwCommonDialog input[name='setRandOrder']").prop("checked", true);
		}
		if(hv == 3){
			$("#dwCommonDialog .option_range_3").show();
		}else{
			$("#dwCommonDialog .option_range_3").hide();
		}
		$("#dwCommonDialog select[name='setHv']").val(hv);
		$("#dwCommonDialog input[name='setCellCount']").val(cellCount);

		// 单选，多选 才启用选项随机排列
		if(quType === "RADIO" || quType === "CHECKBOX"){
			$("#dwCommonDialog .optionAutoOrder").show();
			$("#dwCommonDialog .optionRangeHv").show();
		}else if(quType === "ORDERBY"){
			$("#dwCommonDialog .optionAutoOrder").show();
		}else if(quType === "SCORE"){
			$("#dwCommonDialog .optionAutoOrder").show();
			$("#dwCommonDialog .scoreMinMax").show();
			if(paramInt02[0]){
				$("#dwCommonDialog .scoreMinMax .maxScore").val(paramInt02.val());
			}
		}else if(quType === "MULTIFILLBLANK"){
			$("#dwCommonDialog .optionAutoOrder").show();
			$("#dwCommonDialog .minMaxLi").show();
			$("#dwCommonDialog .minMaxLi .minSpan .lgleftLabel").text("最少回答");
			$("#dwCommonDialog .minMaxLi .maxSpan").hide();
			$("#dwCommonDialog .minMaxLi .lgRightLabel").text("项");
			if(paramInt01[0]){
				$("#dwCommonDialog .minMaxLi .minNum").val(paramInt01.val());				
			}
		}

		// 单选，多选，填空题情况下才启用关联到联系设置项
		if((quType == "RADIO" || quType == "CHECKBOX" || quType == "FILLBLANK")){
			$("#dwCommonDialog .contactsAttrLi").show();
			if( contactsAttr == 1){
				$("#dwCommonDialog input[name='setAutoContacts']").prop("checked", true);
				$("#dwCommonDialog .contactsFieldLi").show();
				$("#dwCommonDialog select[name='setContactsField']").val(contactsField);
			}
		}
	}else if(thObjClass.indexOf("dwQuLogic") >= 0){
		$("#dwCommonDialog .dwQuDialogLoad").show();
	}else if(thObjClass.indexOf("dwFbMenuBtn") >= 0){
		$("#dwCommonDialog .dwQuFillDataTypeOption").show();
		var checkType_val = quItemBody.find("input[name='checkType']").val();
		if(checkType_val == ""){
			checkType_val = "NO";
		}
		var checkType = $("#dwCommonDialog select[name='quFill_checkType']");
		checkType.val(checkType_val);
	}else if(thObjClass.indexOf("quCoOptionEdit") >= 0){
		$("#dwCommonDialog .dwQuRadioCheckboxOption").show();
		// 设置回显值 isNote checkType
		var quOption_isNote = $("#dwCommonDialog input[name='quOption_isNote']");
		var quOption_checkType = $("#dwCommonDialog select[name='quOption_checkType']");
		var quOption_isRequiredFill = $("#dwCommonDialog input[name='quOption_isRequiredFill']");
		
		var quOptionParent = $(thDialogObj).parent();
		var isNote_val = quOptionParent.find("input[name='isNote']").val();
		var checkType_val = quOptionParent.find("input[name='checkType']").val();
		var isRequiredFill_val = quOptionParent.find("input[name='isRequiredFill']").val();
		
		if(isNote_val == "1"){
			quOption_isNote.prop("checked", true);
			$(".quOptionFillContentLi,.quOptionFillRequiredLi").show();
		}else{
			quOption_isNote.prop("checked", false);
			$(".quOptionFillContentLi,.quOptionFillRequiredLi").hide();
		}
		if(checkType_val == ""){
			checkType_val = "NO";
		}
		quOption_checkType.val(checkType_val);
		if(isRequiredFill_val == "1"){
			quOption_isRequiredFill.prop("checked",true);
		}else{
			quOption_isRequiredFill.prop("checked",false);
		}
	}else{
		// 暂时加的
		$("#dwCommonDialog .dwQuAddMore").show();
	}
	dwDialogObj = thDialogObj;
}

function getNameCheckIn(quItemBody){
	var item = null;
	if(quItemBody.hasClass("surveyQuItemBody")){
		item = quItemBody;
	}else {
		item = quItemBody.parents(".surveyQuItemBody");
		if(isNull(item) || item.length == 0){
			item = quItemBody.find(".surveyQuItemBody");
		}
	}
	if(isNull(item.find("input[name='quId']").val())){
		return item.attr("checkNameIn");
	}else{
		return item.find("input[name='quId']").val();
	}
}

/**
 * 获取逻辑选项
 * @param {} quItemBody
 * @return {}
 */
function getLogic(quItemBody){
	var quLogicItems = quItemBody.find(".quLogicItem");
	var list = [];
	$.each(quLogicItems, function(i) {
		var logicSaveTag = $(this).find("input[name='logicSaveTag']").val();
		if(logicSaveTag == 0) {
			list.push({
				quLogicId: $(this).find("input[name='quLogicId']").val(),
				cgQuItemId: $(this).find("input[name='cgQuItemId']").val(),
				skQuId: $(this).find("input[name='skQuId']").val(),
				visibility: $(this).find("input[name='visibility']").val(),
				logicType: $(this).find("input[name='logicType']").val(),
				scoreNum: $(this).find("input[name='scoreNum']").val(),
				geLe: $(this).find("input[name='geLe']").val(),
				key: $(this).attr("class").replace("quLogicItem quLogicItem_", "")
			});
		}
	});
	return list;
}

/**
 * 添加单选选项
 * @param {} quItemBody
 * @param {} itemText
 * @return {}
 */
function addRadioItem(quItemBody, itemText) {
	//得判断是否是table类型
	var hv = quItemBody.find("input[name='hv']").val();
	var cellCount = quItemBody.find("input[name='cellCount']").val();
	var newEditObj = null;
	var checkNameIn = getNameCheckIn(quItemBody);
	var paramJson = {
		checkNameIn: checkNameIn
	};
	var quRadioItemHtml = getDataUseHandlebars($("#quRadioItem").html(), paramJson);
	if(hv == 3) {
		//表格处理
		var quTableObj = quItemBody.find(".quCoItem table.tableQuColItem");
		var emptyTdDiv = quTableObj.find("div.emptyTd");
		if(emptyTdDiv[0]) {
			//表示有空位
			var emptyTd = emptyTdDiv.first().parents("td");
			emptyTd.empty();
			emptyTd.append(quRadioItemHtml);
		} else {
			//木有空位，根据cellCount生成新的tr,td
			var appendTr = "<tr>";
			for(var i = 0; i < cellCount; i++) {
				appendTr += "<td>";
				if(i == 0) {
					appendTr += quRadioItemHtml;
				} else {
					appendTr += "<div class='emptyTd'></div>";
				}
				appendTr += "</td>";
			}
			appendTr += "</tr>";
			quTableObj.append(appendTr);
		}
		var tdWidth = parseInt(600 / cellCount);
		var tdLabelWidth = tdWidth - 10;
		quItemBody.find(".quCoItem .tableQuColItem tr td").width(tdWidth);
		quItemBody.find(".quCoItem .tableQuColItem tr td label").width(tdLabelWidth);
		newEditObj = quItemBody.find(".quCoItem table").find(".editAble").last();
	} else {
		var quCoItemUl = quItemBody.find(".quCoItem ul");
		quCoItemUl.append("<li class='quCoItemUlLi'>" + quRadioItemHtml + "</li>");
		quItemBody.find("input[name='saveTag']").val(0);
		newEditObj = quCoItemUl.find("li:last .editAble");
	}
	newEditObj.text(itemText);
	if(itemText == "") {
		newEditObj.css("display", "inline");
	}
	return newEditObj;
}

/**
 * 添加多选选项
 * @param {} quItemBody
 * @param {} itemText
 * @return {}
 */
function addCheckboxItem(quItemBody, itemText) {
	//得判断是否是table类型
	var hv = quItemBody.find("input[name='hv']").val();
	var cellCount = quItemBody.find("input[name='cellCount']").val();
	var newEditObj = null;
	var checkNameIn = getNameCheckIn(quItemBody);
	var paramJson = {
		checkNameIn: checkNameIn
	};
	var quRadioItemHtml = getDataUseHandlebars($("#quCheckboxItem").html(), paramJson);
	if(hv == 3) {
		//表格处理
		var quTableObj = quItemBody.find(".quCoItem table.tableQuColItem");
		var emptyTdDiv = quTableObj.find("div.emptyTd");
		if(emptyTdDiv[0]) {
			//表示有空位
			var emptyTd = emptyTdDiv.first().parents("td");
			emptyTd.empty();
			emptyTd.append(quRadioItemHtml);
		} else {
			//没有空位，根据cellCount生成新的tr,td
			var appendTr = "<tr>";
			for(var i = 0; i < cellCount; i++) {
				appendTr += "<td>";
				if(i == 0) {
					appendTr += quRadioItemHtml;
				} else {
					appendTr += "<div class='emptyTd'></div>";
				}
				appendTr += "</td>";
			}
			appendTr += "</tr>";
			quTableObj.append(appendTr);
		}
		var tdWidth = parseInt(600 / cellCount);
		var tdLabelWidth = tdWidth - 10;
		quItemBody.find(".quCoItem .tableQuColItem tr td").width(tdWidth);
		quItemBody.find(".quCoItem .tableQuColItem tr td label").width(tdLabelWidth);
		newEditObj = quItemBody.find(".quCoItem table").find(".editAble").last();
	} else {
		//ul li处理
		var quCoItemUl = quItemBody.find(".quCoItem ul");
		quCoItemUl.append("<li class='quCoItemUlLi'>" + quRadioItemHtml + "</li>");
		quItemBody.find("input[name='saveTag']").val(0);
		newEditObj = quCoItemUl.find("li:last .editAble");
	}
	newEditObj.text(itemText);
	if(itemText == "") {
		newEditObj.css("display", "inline");
	}
	return newEditObj;
}

/**
 * 添加评分项
 * @param {} quItemBody
 * @param {} itemText
 * @return {}
 */
function addScoreItem(quItemBody, itemText) {
	//得判断是否是table类型
	var newEditObj = null;
	//ul li处理
	var quScoreItemHtml = $("#quScoreItemModel").html();
	var quCoItemTable = quItemBody.find("table.quCoItemTable");
	quCoItemTable.append("<tr class='quScoreOptionTr'>" + quScoreItemHtml + "</tr>");
	quItemBody.find("input[name='saveTag']").val(0);
	var scoreNumTableTr = quCoItemTable.find("tr.quScoreOptionTr:last  .scoreNumTable tr");
	var paramInt02 = quItemBody.find("input[name='paramInt02']").val();
	scoreNumTableTr.empty();
	for(var i = 1; i <= paramInt02; i++) {
		scoreNumTableTr.append("<td>" + i + "</td>");
	}
	quCoItemTable.find("tr.quScoreOptionTr:last input[name='quItemSaveTag']").val(0);
	newEditObj = quCoItemTable.find("tr.quScoreOptionTr:last .editAble");
	newEditObj.text(itemText);
	if(itemText == "") {
		newEditObj.css("display", "inline");
	}
	return newEditObj;
}

/**
 * 添加排序项
 * @param {} quItemBody
 * @param {} itemText
 * @return {}
 */
function addOrderquItem(quItemBody, itemText) {
	//得判断是否是table类型
	var newEditObj = null;
	//ul li处理 <li class="quCoItemUlLi">
	var quOrderItemLeftHtml = $("#quOrderItemLeftModel").html();
	var quOrderItemRightHtml = $("#quOrderItemRightModel").html();

	var quOrderItemLeftUl = quItemBody.find(".quOrderByLeft ul");
	var quOrderByRightTable = quItemBody.find(".quOrderByRight table.quOrderByTable");
	quOrderItemLeftUl.append("<li class='quCoItemUlLi'>" + quOrderItemLeftHtml + "</li>");
	quOrderByRightTable.append("<tr>" + quOrderItemRightHtml + "</tr>");

	quItemBody.find("input[name='saveTag']").val(0);
	newEditObj = quOrderItemLeftUl.find("li:last .editAble");

	newEditObj.text(itemText);
	if(itemText == "") {
		newEditObj.css("display", "inline");
	}
	//quOrderyTableTd
	refquOrderTableTdNum(quOrderByRightTable);
	return newEditObj;
}
function refquOrderTableTdNum(quOrderByRightTable) {
	var quOrderyTableTds = quOrderByRightTable.find(".quOrderyTableTd");
	$.each(quOrderyTableTds, function(i) {
		$(this).text(i + 1);
	});
}

/**
 * 添加多项填空题项
 * @param {} quItemBody
 * @param {} itemText
 * @return {}
 */
function addMultiFillblankItem(quItemBody, itemText) {
	// 得判断是否是table类型
	var newEditObj = null;
	// ul li处理
	var quScoreItemHtml = $("#mFillblankTableModel").html();
	var quCoItemTable = quItemBody.find("table.mFillblankTable");
	quCoItemTable.append("<tr class='mFillblankTableTr'>" + quScoreItemHtml + "</tr>");
	quItemBody.find("input[name='saveTag']").val(0);
	newEditObj = quCoItemTable.find("tr.mFillblankTableTr:last .editAble");
	newEditObj.text(itemText);
	if(itemText == "") {
		newEditObj.css("display", "inline");
	}
	return newEditObj;
}

/**
 * 添加列选项
 * @param {} eventObj
 * @param {} quItemBody
 * @param {} itemText
 * @return {}
 */
function addChenItem(eventObj, quItemBody, itemText) {
	var eventObjClass = eventObj.attr("class");
	if(eventObjClass.indexOf("Column") >= 0) {
		return addChenColumnItem(quItemBody, itemText);
	} else {
		return addChenRowItem(quItemBody, itemText);
	}
}
/**
 * 添加矩阵单选题列选项
 * @param {} quItemBody
 * @param {} itemText
 * @return {}
 */
function addChenColumnItem(quItemBody, itemText) {
	// 得判断是否是table类型
	var newEditObj = null;
	// ul li处理
	var quRadioColumnHtml = $("#quChenColumnModel").html();
	var quCoChenTable = quItemBody.find("table.quCoChenTable");
	var quCoChenTableTrs = quCoChenTable.find("tr");
	var quType = quItemBody.find("input[name='quType']").val();
	$.each(quCoChenTableTrs, function(i) {
		if(i == 0) {
			$(this).append(quRadioColumnHtml);
		} else {
			// 获取checkNameIn用作单选框
			var checkNameIn = $(this).attr("checkNameInTr");
			if(quType == "CHENRADIO") {
				$(this).append("<td><input type='radio' class='chenRadioInput' name='" + checkNameIn + "'></td>");
			} else if(quType == "CHENCHECKBOX") {
				$(this).append('<td><input type="checkbox" class="chenCheckBoxInput"></td>');
			} else if(quType == "CHENFBK") {
				$(this).append("<td><input type='text' class='questionChenColumnValue'></td>");
			} else if(quType == "CHENSCORE") {
				$(this).append('<td><select class="quChenScoreSelect"><option value="0">-评分-</option><option value="1">1分</option><option value="2">2分</option><option value="3">3分</option><option value="4">4分</option><option value="5">5分</option></select></td>');
			}
		}
	});
	quItemBody.find("input[name='saveTag']").val(0);
	newEditObj = quCoChenTable.find("tr:first .editAble:last");
	newEditObj.text(itemText);
	if(itemText == "") {
		newEditObj.css("display", "inline");
	}
	return newEditObj;
}
/**
 * 添加矩阵单选题行选项
 * @param {} quItemBody
 * @param {} itemText
 * @return {}
 */
function addChenRowItem(quItemBody, itemText) {
	// 得判断是否是table类型
	var newEditObj = null;
	// ul li处理
	var quChenRowHtml = $("#quChenRowModel").html();
	var quCoChenTable = quItemBody.find("table.quCoChenTable");
	var quCoChenTableTds = quCoChenTable.find("tr:first td");
	var quType = quItemBody.find("input[name='quType']").val();
	// 获取checkNameIn用作单选框
	var checkNameIn = getRandomValueToString();
	var appendTrHtml = "<tr checkNameInTr='" + checkNameIn + "'>";
	var chenRadioInputRow = getRandomValueToString();
	$.each(quCoChenTableTds, function(i) {
		if(i == 0) {
			appendTrHtml += quChenRowHtml;
		} else {
			if(quType == "CHENRADIO") {
				appendTrHtml += "<td><input type='radio' class='chenRadioInput' name='" + chenRadioInputRow + "'></td>";
			} else if(quType == "CHENCHECKBOX") {
				appendTrHtml += '<td><input type="checkbox" class="chenCheckBoxInput"></td>';
			} else if(quType == "CHENFBK") {
				appendTrHtml += "<td><input type='text' class='questionChenColumnValue'></td>";
			} else if(quType == "CHENSCORE") {
				appendTrHtml += '<td><select class="quChenScoreSelect"><option value="0">-评分-</option><option value="1">1分</option><option value="2">2分</option><option value="3">3分</option><option value="4">4分</option><option value="5">5分</option></select></td>';
			}
		}
	});
	appendTrHtml += "</tr>";
	quCoChenTable.append(appendTrHtml);
	quItemBody.find("input[name='saveTag']").val(0);
	newEditObj = quCoChenTable.find("tr:last .editAble");
	newEditObj.text(itemText);
	if(itemText == "") {
		newEditObj.css("display", "inline");
	}
	return newEditObj;
}

/**
 * 添加逻辑选项
 * @param {} autoClass
 * @param {} trueCallback
 * @param {} falseCallback
 */
function addQuDialogLogicTr(autoClass, trueCallback, falseCallback) {
	//当前题的选项
	var quItemBody = $(dwDialogObj).parents(".surveyQuItemBody");
	var quItemInputCases = quItemBody.find(".quItemInputCase");
	var quLogicInputCase = quItemBody.find(".quLogicInputCase");
	var curQuId = quItemBody.find("input[name='quId']").val();
	var quType = quItemBody.find("input[name='quType']").val();

	var logicQuOptionSels = $("#dwQuLogicTable").find(".logicQuOptionSel");
	var dwLogicQuSels = $("#dwQuLogicTable").find(".logicQuSel");
	//判断有无选项任意选项
	var executeTag = true;
	$.each(logicQuOptionSels, function() {
		var selOptionVal = $(this).val();
		if(selOptionVal == "0") {
			executeTag = false;
			return false;
		}
	});

	if(executeTag) {
		var appendTrHtml = $("#setQuLogicItemTrModel").html();
		if(quType === "SCORE") {
			appendTrHtml = $("#setQuLogicItemTrModel_score").html();
		}
		$("#dwQuLogicTable").append("<tr>" + appendTrHtml + "</tr>");
		var lastTr = $("#dwQuLogicTable").find("tr").last();
		if(quType === "FILLBLANK") {
			lastTr.find(".ifSpanText1").text("如果回答");
		}
		if(autoClass) {
			var quLogicItemNum = quLogicInputCase.find("input[name='quLogicItemNum']");
			var newQuLogicItemNum = (parseInt(quLogicItemNum.val()) + 1);
			quLogicItemNum.val(newQuLogicItemNum);
			var newQuLogicItemClass = "quLogicItem_" + newQuLogicItemNum;
			lastTr.attr("class", newQuLogicItemClass);
		}
		var dwQuOptionSel = lastTr.find(".logicQuOptionSel");
		var eachTag = true;
		if(quType === "CHENRADIO" || quType === "CHENCHECKBOX" || quType === "CHENSCORE" || quType === "CHENFBK") {
			var quChenColumnTds = quItemBody.find(".quChenColumnTd");
			var quChenRowTds = quItemBody.find(".quChenRowTd");
			$.each(quChenRowTds, function() {
				var rowText = $(this).find(".quCoOptionEdit").text();
				var rowQuItemId = $(this).find("input[name='quItemId']").val();
				$.each(quChenColumnTds, function() {
					var colText = $(this).find(".quCoOptionEdit").text();
					var colQuItemId = $(this).find("input[name='quItemId']").val();
					var optionId = rowQuItemId + ":" + colQuItemId;
					eachTag = true;
					$.each(logicQuOptionSels, function() {
						var selOptionVal = $(this).val();
						if(selOptionVal == optionId) {
							eachTag = false;
							return false;
						}
					});
					if(eachTag) {
						dwQuOptionSel.append("<option value='" + optionId + "'>" + rowText + ":" + colText + "</option>");
					}
				});
			});
		} else {
			$.each(quItemInputCases, function() {
				var optionText = $(this).parent().find("label.quCoOptionEdit").text();
				var optionId = $(this).find("input[name='quItemId']").val();
				eachTag = true;
				$.each(logicQuOptionSels, function() {
					var selOptionVal = $(this).val();
					if(selOptionVal == optionId) {
						eachTag = false;
						return false;
					}
				});
				if(eachTag) {
					dwQuOptionSel.append("<option value='" + optionId + "'>" + optionText + "</option>");
				}
			});
		}
		if(logicQuOptionSels.size() == 0) {
			dwQuOptionSel.append("<option value='0'>任意选项</option>");
		} else {
			$("#dwQuLogicTable").find(".logicQuOptionSel option[value='0']").remove();
		}
		if(quType === "FILLBLANK") {
			dwQuOptionSel.val("0");
		}
		var logicQuSel = lastTr.find(".logicQuSel");
		var quItemBodys = $("#dwSurveyQuContent .surveyQuItemBody");
		$.each(quItemBodys, function() {
			if($(this).find(".quCoTitleEdit")[0]) {
				var quCoNumText = $(this).find(".quCoNum").text();
				var quTitleText = $(this).find(".quCoTitleEdit").text();
				var quId = $(this).find("input[name='quId']").val();
				eachTag = true;
				if(curQuId == quId) {
					eachTag = false;
				}
				if(eachTag) {
					$.each(dwLogicQuSels, function() {
						var dwLogicQuSelVal = $(this).val();
						if(dwLogicQuSelVal == quId) {
							eachTag = false;
							return false;
						}
					});
				}
				if(eachTag) {
					logicQuSel.append("<option value='" + quId + "'>" + quCoNumText + quTitleText + "</option>");
				}
			}
		});
		logicQuSel.append("<option value='1'>正常结束（计入结果）</option><option value='2'>提前结束（不计入结果）</option>");
		if(quType === "SCORE") {
			var logicScoreNum = lastTr.find(".logicScoreNum");
			logicScoreNum.empty();
			for(var i = 1; i <= 10; i++) {
				logicScoreNum.append("<option value=\"" + i + "\">" + i + "</option>");
			}
		} else if(quType === "ORDERBY") {
			dwQuOptionSel.empty();
			dwQuOptionSel.append("<option value='0'>回答完成</option>");
			lastTr.find(".ifSpanText1").text("如果本题");
		}
		if(autoClass) {
			logicQuSel.prepend("<option value=''>-请选择题目-</option>");
			dwQuOptionSel.prepend("<option value=''>-请选择选项-</option>");
		}
		bindDialogRemoveLogic();
		trueCallback();
	} else {
		falseCallback();
	}
}

/**
 * 绑定逻辑设置中选项删除事件
 */
function bindDialogRemoveLogic() {
	$(".dialogRemoveLogic").unbind();
	$(".dialogRemoveLogic").click(function() {
		var logicItemTr = $(this).parents("tr");
		var logicItemTrClass = logicItemTr.attr("class");
		//同时移除页面只保存的信息--注意如果已经保存到库中，修改
		var quItemBody = $(dwDialogObj).parents(".surveyQuItemBody");
		var quLogicItem = quItemBody.find("." + logicItemTrClass);
		if(quLogicItem[0]) {
			//有则判断，是否已经加入到数据库
			var quLogicIdVal = quLogicItem.find("input[name='quLogicId']").val();
			if(quLogicIdVal != "") {
				quLogicItem.find("input[name='visibility']").val(0);
				quLogicItem.find("input[name='logicSaveTag']").val(0);
				quItemBody.find("input[name='saveTag']").val(0);
			} else {
				quLogicItem.remove();
			}
			//更新select option信息
			var logicQuOptionSel = logicItemTr.find(".logicQuOptionSel option:selected");
			var logicQuSel = logicItemTr.find(".logicQuSel option:selected");
			if(logicQuOptionSel.val() != "") {
				$("#dwQuLogicTable").find(".logicQuOptionSel").append("<option value='" + logicQuOptionSel.val() + "'>" + logicQuOptionSel.text() + "</option>");
			}
			if(logicQuSel.val() != "") {
				$("#dwQuLogicTable").find(".logicQuSel").append("<option value='" + logicQuSel.val() + "'>" + logicQuSel.text() + "</option>");
			}
		}
		logicItemTr.remove();
		refreshQuLogicInfo(quItemBody);
		return false;
	});
	/*设置逻辑时，选中某个选项时的操作*/
	$(".logicQuSel").unbind();
	$(".logicQuSel").change(function() {
		var thVal = $(this).val();
		//当题目选项中选项 提前结束，或正常结束，则不作处理
		if(thVal !== "1" && thVal !== "2") {
			//移除前面选项中存在的当前被选择的选项
			$("#dwQuLogicTable").find(".logicQuSel").not(this).find("option[value='" + thVal + "']").remove();
		}
	});
}

function exeQuCBNum() {
	if(quCBNum == quCBNum2) {
		quCBNum = 0;
		quCBNum2 = 0;
		//全部题排序号同步一次
		//对如新增插入题-需要同步调整其它题的排序
		//对如删除题-需要同步调整其它题的排序
	}
}

/**
 * 刷新每题的逻辑显示数目
 * @param {} quItemBody
 */
function refreshQuLogicInfo(quItemBody) {
	var quLogicItems = quItemBody.find(".quLogicItem input[name='visibility'][value='1']");
	var quLogicItemSize = quLogicItems.size();
	quItemBody.find(".quLogicInfo").text(quLogicItemSize);
}

function validateGen() {
	$("#dwCommonDialogForm").validate({
		rules: {
			setCellCount: {
				required: true,
				digits: true,
				min: 1
			}
		},
		errorPlacement: function(error, element) {
			element.parent().append(error);
		}
	});
	$("input[name='setCellCount']").unbind();
	$("input[name='setCellCount']").blur(function() {
		$("#dwCommonDialogForm").validate();
	});
	$("input[name='setCellCount']").blur();
}

function notify(msg, delayHid) {
	$(".notification").remove();
	if(delayHid == null) {
		delayHid = 5000;
	}
	$("<div>")
		.appendTo(document.body)
		.text(msg)
		.addClass("notification ui-state-default ui-corner-bottom")
		.position({
			my: "center top",
			at: "center top",
			of: window
		})
		.show({
			effect: "blind"
		})
		.delay(delayHid)
		.hide({
			effect: "blind",
			duration: "slow"
		}, function() {
			$(this).remove();
		});
}

/**
 * 重置选项以及业务逻辑
 * @param {} quItemBody
 * @param {} json
 */
function resetLogicAndItem(quItemBody, json){
	var quItems = json.bean.quItems;
	if(!isNull(quItems)){
		$.each(quItems, function(i, item) {
			var quItemOption = quItemBody.find(".quOption_" + item.title);
			quItemOption.find("input[name='quItemId']").val(item.id);
			quItemOption.find(".quItemInputCase input[name='quItemSaveTag']").val(1);
		});
	}
	//同步logic Id信息
	var quLogics = json.bean.quLogics;
	if(!isNull(quLogics)){
		$.each(quLogics, function(i, item) {
			var logicItem = quItemBody.find(".quLogicItem_" + item.title);
			logicItem.find("input[name='quLogicId']").val(item.id);
			logicItem.find("input[name='logicSaveTag']").val(1);
		});
	}
}

function curEditCallback(){
	if(curEditObj != null){
		var dwEditHtml = $("#dwComEditContent").html();
		setCurEditContent(dwEditHtml);
	}
	$("#dwSurveyNote").removeClass("click");
}

function dwCommonEditHide(){
	$("#dwCommonEditRoot").hide();
	$(".dwComEditMenuUl").hide();
	curEditObj = null;
}

function setCurEditContent(dwEditHtml){
	var thClass = $(curEditObj).attr("class");
	if(dwEditHtml == "" && thClass.indexOf("dwSvyNoteEdit") < 0){
		deleteDwOption();
	}else if(dwEditHtml != curEditObjOldHtml){
		//更新值
		$(curEditObj).html(dwEditHtml);
		//修改保存状态
		setSaveTag0();
		//重置左侧设计目录序号和标题
		resetQuLeftItem();
	}
	dwCommonEditHide();
}

function setShowDialogOffset(thDialogObj) {
	var thObjClass = thDialogObj.attr("class");
	if(thObjClass.indexOf("dwFbMenuBtn") < 0 && thObjClass.indexOf("quCoOptionEdit") < 0) {
		var thOffset = thDialogObj.offset();
		$("#dwCommonDialog").show(0, function() {
			var thOffsetTop = thOffset.top;
			var thOffsetLeft = thOffset.left + 40;
			var dwCommonRefIcon = $("#dwCommonDialog").find(".dwCommonRefIcon");
			dwCommonRefIcon.removeClass("right");
			dwCommonRefIcon.removeClass("left");
			browseWidth = $(window).width();
			browseHeight = $(window).height();
			if((thOffsetLeft - 100) > browseWidth / 2) {
				thOffsetLeft = thOffsetLeft - $("#dwCommonDialog").width() - 50;
				dwCommonRefIcon.addClass("right");
			} else {
				dwCommonRefIcon.addClass("left");
			}
			$("#dwCommonDialog").offset({
				top: thOffsetTop,
				left: thOffsetLeft
			});
		});
	}
}

function setSaveTag0() {
	var quItemBody = $(curEditObj).parents(".surveyQuItemBody");
	quItemBody.find("input[name='saveTag']").val(0);

	var thClass = $(curEditObj).attr("class");
	if(thClass.indexOf("quCoTitleEdit") > 0) {
		//题目标题
		$(curEditObj).parent().find("input[name='quTitleSaveTag']").val(0);
	} else if(thClass.indexOf("quCoOptionEdit") > 0) {
		//题目选项
		$(curEditObj).parent().find("input[name='quItemSaveTag']").val(0);
	} else if(thClass.indexOf("dwSvyNoteEdit") >= 0) {
		//问卷欢迎语
		$("input[name='svyNoteSaveTag']").val(0);
	} else if(thClass.indexOf("dwSvyName") >= 0) {
		$("input[name='svyNmSaveTag']").val(0);
	}
}

/**
 * 删除选项
 */
function deleteDwOption(){
	if(curEditObj!=null){
		var quItemBody = $(curEditObj).parents(".surveyQuItemBody");
		var quType = quItemBody.find("input[name='quType']").val();
		// 类型有："RADIO"，"CHECKBOX"，"SCORE"，"ORDERBY"，"MULTIFILLBLANK"，
		// "CHENRADIO"，"CHENCHECKBOX"，"CHENFBK"，"CHENSCORE"
		if(type == 1){
			// 问卷
			layui.survey[quType]();
		}else if(type == 2){
			// 试卷
			layui.exam[quType]();
		}
	}
}












