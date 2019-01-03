
var ueEditObj = null;//即将编辑的内容

var quOptionDesign = null;//问题选项设置

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['jquery', 'winui', 'validate'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'jqueryUI'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form;
	    
	    var svTag = 2;//表示题目是问卷题还是题库中题

	    // 题目保存后回调时机比较参数
		var quCBNum = 0;// 比较值1
		var quCBNum2 = 0;// 比较值2
		var curEditObj = null;
		var curEditObjOldHtml = "";
		var dwDialogObj = null;
		var isDrag = false;
		var appQuObj = null;
		
		var isSort = false;

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
	    var _varioustemplates = getFileContent('tpl/dwsurveydesign/dwcommon/dwCommonEditRoot.tpl');
	    
	    $("#_basemodel").html(_basemodel);
	    $("#_rectanglemodel").html(_rectanglemodel);
	    $("#_auxiliarymodel").html(_auxiliarymodel);
	    $("#_operationmodel").html(_operationmodel);
	    $("#_commonlyusedmodel").html(_commonlyusedmodel);
	    $("body").append(_varioustemplates);
	    
	    showGrid({
		 	id: "dw_body",
		 	url: reqBasePath + "dwsurveydirectory003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/dwsurveydesign/dwsurveydesignbean.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		hdb.registerHelper("showIndex",function(index, options){
		 			return parseInt(index) + 1;
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
		 			}else{
		 				return options.inverse(this);
		 			}
		 		});
		 	},
		 	ajaxSendAfter:function(json){
		 		
			    loadAddr();//初始化地址区域
			    
		 		console.log(json);
		 		
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
				  				resetQuItem();
				  				bindQuHoverItem();
				  			});
				  		}
				  		if(false) {
				  			isDrag = false;
				  			//判断加入----根据initLine显示状态来判断是否加入进去
				  			if(appQuObj != null) {
				  				$(appQuObj).before($(this).find(".dwQuTypeModel").html());
				  				$(appQuObj).prev().hide();
				  				$(appQuObj).prev().removeClass("quDragBody");
				  				$(appQuObj).prev().show("slow");
				  				resetQuItem();
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
					//helper : "clone",
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
						//当一个已连接的sortable对象接收到另一个sortable对象的元素后触发此事件。 
					},
					out: function(event, ui) {
						//当一个元素拖拽移出sortable对象移出并进入另一个sortable对象后触发此事件。 
						isSort = false;
					},
					update: function(event, ui) {
						if(!isDrag) {
							//根据排序ID，计算出是前排序，还是后排序
							$("#dwSurveyQuContentAppUl input[name='saveTag']").val(0);
						}
					},
					stop: function(event, ui) {
						if(isDrag) {
							isDrag = false;
							isSort = false;
							ui.item.html(ui.item.find(".dwQuTypeModel").html());
							ui.item.removeClass("ui-draggable");
							ui.item.find(".quDragBody").removeClass("quDragBody");
							//新加入题-选定题目标题
							ui.item.find(".surveyQuItemBody").addClass("hover");
							ui.item.addClass("li_surveyQuItemBody");
							var quType = ui.item.find(".surveyQuItemBody input[name='quType']").val();
							if(quType != "PAGETAG") {
								editAble(ui.item.find(".surveyQuItemBody .quCoTitleEdit"));
							}
						}
						var curItemBodyOffset = ui.item.offset();
						$("html,body").animate({
							scrollTop: curItemBodyOffset.top - 370
						}, 500, function() {
							$("#tools_wrap").css({
								"zIndex": 200
							});
							resetQuItem();
							bindQuHoverItem();
						});
					}
				});
				
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
	                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
	                } else if (refreshCode == '-9999') {
	                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
	                }
				}});
	    });
	    
		function resetQuItem(){
			if(isDrag){
				isDrag=false;
			}
			var surveyQuItems=$("#dwSurveyQuContent .surveyQuItemBody");
			var indexNum=1;
			$.each(surveyQuItems,function(i){
				$(this).find(".quInputCase input[name='orderById']").val(i+1);
				var quType=$(this).find("input[name='quType']").val();
				if(quType!="PAGETAG" && quType!="PARAGRAPH"){
					$(this).find(".quCoTitle .quCoNum").text((indexNum++)+"、");
				}
			});
			//更新分标标记
			var pageTags=$("#dwSurveyQuContent .surveyQuItemBody input[name='quType'][value='PAGETAG']");
			var pageTagSize = pageTags.size() + 1;
			$.each(pageTags, function(i){
				var quItemBody = $(this).parents(".surveyQuItemBody");
				var pageQuContent = quItemBody.find(".pageQuContent");
				pageQuContent.text("下一页（" + ( i + 1 ) + "/" + pageTagSize + "）");
			});
		}
		
		function bindQuHoverItem(){
			$(".SeniorEdit").unbind();
			$(".SeniorEdit").click(function(){
				ueEditObj = $(curEditObj).html();
				_openNewWindows({
					url: "../../tpl/dwsurveydesign/designSurveyEditor.html", 
					title: "内容",
					area: ['500px', '500px'],
					pageId: "designSurveyEditor",
					callBack: function(refreshCode){
		                if (refreshCode == '0') {
		                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
		                	setCurEditContent(ueEditObj);
		            		curEditCallback();
		            		ueEditObj = null;
		            		curEditObj = null;
		                } else if (refreshCode == '-9999') {
		                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
		                }
					}});
				return false;
			});
			
			$(".option_Set").unbind();
			$(".option_Set").click(function(){
				showUIDialog($(curEditObj));
				return false;
			});
			
			$("input[name='quOption_isNote']").unbind();
			$("input[name='quOption_isNote']").click(function(){
				var optionCk=$(this).prop("checked");
				if(optionCk){
					$(".quOptionFillContentLi,.quOptionFillRequiredLi").show();
					$("#modelUIDialog").dialog("option","height",230);
				}else{
					$(".quOptionFillContentLi,.quOptionFillRequiredLi").hide();
				}
			});
			
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
			},function(){
				$(".pageBorderTop").addClass("nohover");
				$(this).removeClass("showLine");
				var hoverTag = $(this).find("input[name='hoverTag']").val();
				if(hoverTag != "hover"){
					$(this).removeClass("hover");
				}
				appQuObj = null;
			});
			
			$("#dwSurveyQuContent .surveyQuItemBody").click(function(){
				curEditCallback();
				dwCommonDialogHide();
				$(".surveyQuItemBody").removeClass("hover");
				$(".surveyQuItemBody").find("input[name='hoverTag']").val("0");
				$(this).addClass("hover");
				return false;
			});
			
			$(".quCoItemUlLi").unbind();
			$(".quCoItemUlLi").hover(function(){
				if(!isDrag){
					$(this).addClass("hover");	
				}
			},function(){
				var thClass=$(this).attr("class");
				if(thClass.indexOf("menuBtnClick")<=0){
					$(this).removeClass("hover");
				}
			});
			//绑定编辑
			$("#dwSurveyQuContent .editAble").unbind();
			$("#dwSurveyQuContent .editAble").click(function(){
				editAble($(this));
				return false;
			});
			
			//绑定题目删除事件
			$(".dwQuDelete").unbind();
			$(".dwQuDelete").click(function(){
				var quBody = $(this).parents(".surveyQuItemBody");
				if(confirm("确认要删除此题吗？")){
					var quId = quBody.find("input[name='quId']").val();
					if(quId != ""){
						var url = reqBasePath+"/design/question!ajaxDelete.action";
						var data="quId="+quId;
						$.ajax({
							url:url,
							data:data,
							type:"post",
							success:function(msg){
								if(msg=="true"){
									quBody.hide("slow",function(){$(this).parent().remove();resetQuItem();});
								}else{
									alert("删除失败，请重试！");
								}
							}
						});
					}else{
						quBody.hide("slow",function(){$(this).parent().remove();resetQuItem();});
					}
				}
				return false;
			});
			
			$(".questionUp").unbind();
			$(".questionUp").click(function(){
				var nextQuBody=$(this).parents(".li_surveyQuItemBody");
				var prevQuBody=$(nextQuBody).prev();
				if(prevQuBody[0]){
					var prevQuBodyHtml=prevQuBody.html();
					$(nextQuBody).after("<li class='li_surveyQuItemBody'>"+prevQuBodyHtml+"</li>");
					var newNextObj=$(nextQuBody).next();
					newNextObj.hide();
					newNextObj.slideDown("slow");
					prevQuBody.slideUp("slow",function(){prevQuBody.remove();resetQuItem();bindQuHoverItem();});
					
					nextQuBody.find("input[name='saveTag']").val(0);
					newNextObj.find("input[name='saveTag']").val(0);
				}else{
					notify("已经是第一个了！",1000);
				}
			});
			
			$(".questionDown").unbind();
			$(".questionDown").click(function(){
				var prevQuBody=$(this).parents(".li_surveyQuItemBody");
				var nextQuBody=$(prevQuBody).next();
				if(nextQuBody[0]){
					var nextQuBodyHtml=nextQuBody.html();
					$(prevQuBody).before("<li class='li_surveyQuItemBody' >"+nextQuBodyHtml+"</li>");
					var newPrevObj=$(prevQuBody).prev();
					newPrevObj.hide();
					newPrevObj.slideDown("slow");
					nextQuBody.slideUp("slow",function(){nextQuBody.remove();resetQuItem();bindQuHoverItem();});
					
					prevQuBody.find("input[name='saveTag']").val(0);
					newPrevObj.find("input[name='saveTag']").val(0);
				}else{
					alert("已经是最后一个了！");
				}
			});
			
			$(".dwQuSet").unbind();
			$(".dwQuSet").click(function(){
				showDialog($(this));
				var quItemBody=$(this).parents(".surveyQuItemBody");
				resetQuItemHover(quItemBody);
				validateGen();
				return false;
			});
			
			//逻辑设置 
			$(".dwQuLogic").unbind();
			$(".dwQuLogic").click(function(){
				var quItemBody = $(this).parents(".surveyQuItemBody");
				var quType = quItemBody.find("input[name='quType']").val();
				//默认加载图标
				var fristQuItemBody = $("#dwSurveyQuContent .li_surveyQuItemBody").first();
				saveQus(fristQuItemBody, function() {
					$(".dwQuDialogCon").hide();
					$("#dwCommonDialog .dwQuDialogLogic").show();
					resetQuItemHover(quItemBody);
					bindDialogRemoveLogic();
					$("#dwQuLogicTable").empty();
					//逻辑数据回显示
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
							//回显相应的选项
							addQuDialogLogicTr(false, function() {
								//执行成功--设置值
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
			
			//添加行选项
			$(".addOption,.addColumnOption,.addRowOption").unbind();
			$(".addOption,.addColumnOption,.addRowOption").click(function() {
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
				} else if(quType == "ORDERQU") {
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
			
			//批量添加事件
			$(".addMoreOption,.addMoreRowOption,.addMoreColumnOption").unbind();
			$(".addMoreOption,.addMoreRowOption,.addMoreColumnOption").click(function(){
				showDialog($(this));
				var quItemBody = $(this).parents(".surveyQuItemBody");
				resetQuItemHover(quItemBody);
				return false;
			});
			
			//填空题选项设置
			$(".quFillblankItem .dwFbMenuBtn").unbind();
			$(".quFillblankItem .dwFbMenuBtn").click(function(){
				showUIDialog($(this));
				return false;
			});
			
			$(".dwOptionUp").unbind();
			$(".dwOptionUp").click(function(){
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
					var nextLi=null;
					var prevLi=null;
					var nextLiAfterHtml="";
					if(quType === "RADIO" || quType === "CHECKBOX" || quType === "ORDERQU"){
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
							var editOffset=nextLi.find("label.editAble").offset();
							$("#dwCommonEditRoot").show();
							$("#dwCommonEditRoot").offset({top:editOffset.top,left:editOffset.left});
							bindQuHoverItem();
							$(curEditObj).click();
							$(nextLi).find("input[name='quItemSaveTag']").val(0);
							$(nextLi).next().find("input[name='quItemSaveTag']").val(0);
							var quItemBody=$(curEditObj).parents(".surveyQuItemBody");
							quItemBody.find("input[name='saveTag']").val(0);
						}else{
							alert("已经是第一个了！");
						}
					}
				}
				return false;
			});
			
			function dwOptionUp(prevTd,nextTd){
				var prevTdHtml=prevTd.html();
				$(nextTd).after("<td>"+prevTdHtml+"</td>");
				prevTd.hide();
				prevTd.remove();
				var editOffset=nextTd.find("label.editAble").offset();
				$("#dwCommonEditRoot").show();
				$("#dwCommonEditRoot").offset({top:editOffset.top,left:editOffset.left});
				bindQuHoverItem();
				$(curEditObj).click();
				$(nextTd).find("input[name='quItemSaveTag']").val(0);
				$(nextTd).next().find("input[name='quItemSaveTag']").val(0);
				var quItemBody=$(curEditObj).parents(".surveyQuItemBody");
				quItemBody.find("input[name='saveTag']").val(0);
			}
			
			function dwOptionUp_1(prevTr,nextTr){
				var prevTd=prevTr.find("td").last();
				var nextTd=nextTr.find("td").first();
				
				var prevTdHtml=prevTd.html();
				var nextTdHtml=nextTd.html();
				
				prevTd.before("<td>"+nextTdHtml+"</td>");
				$(nextTd).after("<td>"+prevTdHtml+"</td>");
				
				prevTd.hide();
				prevTd.remove();
				
				nextTd.hide();
				nextTd.remove();
				
				prevTd=prevTr.find("td").last();
				nextTd=nextTr.find("td").first();
				
				curEditObj=prevTd.find("label.editAble");
				var editOffset=prevTd.find("label.editAble").offset();
				$("#dwCommonEditRoot").show();
				$("#dwCommonEditRoot").offset({top:editOffset.top,left:editOffset.left});
				bindQuHoverItem();
				$(curEditObj).click();
				$(prevTd).find("input[name='quItemSaveTag']").val(0);
				$(nextTd).find("input[name='quItemSaveTag']").val(0);
				var quItemBody=$(curEditObj).parents(".surveyQuItemBody");
				quItemBody.find("input[name='saveTag']").val(0);
			}
			
			$(".dwOptionDown").unbind();
			$(".dwOptionDown").click(function(){
				//判断类型区别table跟ul中的排序
				var quItemBody=$(curEditObj).parents(".surveyQuItemBody");
				var quType=quItemBody.find("input[name='quType']").val();
				var hv=quItemBody.find("input[name='hv']").val();
				if(hv==3){
					var prevTd=$(curEditObj).parents("td");
					var nextTd=prevTd.next();
					if(nextTd[0]){
						dwOptionDown(prevTd, nextTd);
					}else{
						var nextTr=$(curEditObj).parents("tr");
						var prevTr=nextTr.prev();
						if(prevTr[0]){
							prevTd=prevTr.find("td").last();
							dwOptionUp_1(prevTr, nextTr);
						}else{
							alert("已经是第一个了！");
						}
					}
				}else{
					var prevLi=null;
					var nextLi=null;
					var prevLiBeforeHtml="";
					if(quType==="RADIO" || quType==="CHECKBOX" || quType==="ORDERQU"){
						prevLi=$(curEditObj).parents("li.quCoItemUlLi");
						nextLi=prevLi.next();
						var nextLiHtml=nextLi.html();
						prevLiBeforeHtml="<li class='quCoItemUlLi'>"+nextLiHtml+"</li>";
					}else if(quType==="SCORE"){
						prevLi=$(curEditObj).parents("tr.quScoreOptionTr");
						nextLi=prevLi.next();
						var nextLiHtml=nextLi.html();
						prevLiBeforeHtml="<tr class='quScoreOptionTr'>"+nextLiHtml+"</tr>";
					}else if(quType==="MULTIFILLBLANK"){
						prevLi=$(curEditObj).parents("tr.mFillblankTableTr");
						nextLi=prevLi.next();
						var nextLiHtml=nextLi.html();
						prevLiBeforeHtml="<tr class='mFillblankTableTr'>"+nextLiHtml+"</tr>";
					}else if(quType==="CHENRADIO" || quType==="CHENCHECKBOX" || quType==="CHENSCORE" || quType==="CHENFBK"){
						prevLi=$(curEditObj).parents("tr.quChenRowTr");
						if(prevLi[0]){
							nextLi=prevLi.next();
							var nextLiHtml=nextLi.html();
							prevLiBeforeHtml="<tr class='quChenRowTr'>"+nextLiHtml+"</tr>";
						}else{
							prevLi=$(curEditObj).parents("td.quChenColumnTd");
							nextLi=prevLi.next();
							var nextLiHtml=nextLi.html();
							prevLiBeforeHtml="<td class='quChenColumnTd'>"+nextLiHtml+"</td>";
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
			
			function dwOptionDown(prevTd,nextTd){
				var nextTdHtml=nextTd.html();
				$(prevTd).before("<td>"+nextTdHtml+"</td>");
				nextTd.hide();
				nextTd.remove();
				var editOffset=prevTd.find("label.editAble").offset();
				$("#dwCommonEditRoot").show();
				$("#dwCommonEditRoot").offset({top:editOffset.top,left:editOffset.left});
				bindQuHoverItem();
				$(curEditObj).click();
				$(prevTd).find("input[name='quItemSaveTag']").val(0);
				$(prevTd).next().find("input[name='quItemSaveTag']").val(0);
				var quItemBody=$(curEditObj).parents(".surveyQuItemBody");
				quItemBody.find("input[name='saveTag']").val(0);
			}
			
			
			$(".dwOptionDel").unbind();
			$(".dwOptionDel").click(function(){
				deleteDwOption();
				return false;
			});
			
			bindAddrChange();
		}
		
		function resetQuItemHover(quItemBody){
			$(".surveyQuItemBody").removeClass("hover");
			$(".surveyQuItemBody").find("input[name='hoverTag']").val("0");
			if(quItemBody!=null){
				quItemBody.addClass("hover");
				quItemBody.find("input[name='hoverTag']").val("hover");
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
		
		function dwCommonDialogHide(){
			$("#dwCommonDialog").hide();
			$(".menuBtnClick").removeClass("menuBtnClick");
			dwDialogObj = null;
		}
		
		//删除选项
		function deleteDwOption(){
			if(curEditObj!=null){
				var quItemBody=$(curEditObj).parents(".surveyQuItemBody");
				var quType=quItemBody.find("input[name='quType']").val();
				if(quType=="RADIO"){
					//添加单选选项
					deleteRadioOption();
				}else if(quType=="CHECKBOX"){
					deleteCheckboxOption();
				}else if(quType=="SCORE"){
					deleteScoreOption();
				}else if(quType=="ORDERQU"){
					deleteOrderquOption();
				}else if(quType=="MULTIFILLBLANK"){
					deleteMultiFillblankOption();
				}else if(quType=="CHENRADIO" || quType=="CHENCHECKBOX" || quType=="CHENFBK" || quType=="CHENSCORE"){
					deleteChenOption();
				}
			}
		}
		
		function curEditCallback(){
			if(curEditObj!=null){
				var dwEditHtml=$("#dwComEditContent").html();
				setCurEditContent(dwEditHtml);
			}
			$("#dwSurveyNote").removeClass("click");
		}

		function setCurEditContent(dwEditHtml){
			var thClass=$(curEditObj).attr("class");
			if(dwEditHtml=="" && thClass.indexOf("dwSvyNoteEdit")<0){
				deleteDwOption();
			}else if(dwEditHtml!=curEditObjOldHtml){
				//更新值
				$(curEditObj).html(dwEditHtml);
				//修改保存状态
				setSaveTag0();
			}
			dwCommonEditHide();
		}

		function dwCommonEditHide(){
			$("#dwCommonEditRoot").hide();
			$(".dwComEditMenuUl").hide();
			curEditObj=null;
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
		
		//加载地域
		function loadAddr(){
			$.each(addrs, function(i, item){
				var province = item.province;
				$(".hat_province").append("<option value='" + province + "'>" + province + "</option>");
			});
			bindAddrChange();
		}
		function bindAddrChange(){
			$(".hat_province").unbind();
			$(".hat_province").change(function(){
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
			
			$(".hat_city").unbind();
			$(".hat_city").change(function(){
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
		}
		
		//触发显示编辑框 
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
		
		function setSelectText(el) {
		    try {
		        window.getSelection().selectAllChildren(el[0]); //全选
		        window.getSelection().collapseToEnd(el[0]); //光标置后
		    } catch (err) {
		        //在此处理错误
		    }
		}
		
		//显示模式窗口
		function showUIDialog(thDialogObj) {
			quOptionDesign = thDialogObj;
			_openNewWindows({
				url: "../../tpl/dwsurveydesign/modelUIDialog.html", 
				title: "选项设置",
				area: ['500px', '500px'],
				pageId: "modelUIDialog",
				callBack: function(refreshCode){
	                if (refreshCode == '0') {
	                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
	                } else if (refreshCode == '-9999') {
	                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
	                }
				}});
		}
	    
		/** 添加列选项 **/
		function addChenItem(eventObj, quItemBody, itemText){
			var eventObjClass = eventObj.attr("class");
			if(eventObjClass.indexOf("Column") >= 0){
				return addChenColumnItem(quItemBody, itemText);
			}else{
				return addChenRowItem(quItemBody, itemText);
			}
		}
		/** 添加矩阵单选题列选项   **/
		function addChenColumnItem(quItemBody,itemText){
			//得判断是否是table类型
			var newEditObj=null;
			//ul li处理
			var quRadioColumnHtml=$("#quChenColumnModel").html();
			var quCoChenTable=quItemBody.find("table.quCoChenTable");
			var quCoChenTableTrs=quCoChenTable.find("tr");
			var quType=quItemBody.find("input[name='quType']").val();
			$.each(quCoChenTableTrs,function(i){
				if(i==0){
					$(this).append(quRadioColumnHtml);
				}else{
					if(quType=="CHENRADIO"){
						$(this).append("<td><input type='radio'> </td>");	
					}else if(quType=="CHENCHECKBOX"){
						$(this).append("<td><input type='checkbox'> </td>");
					}else if(quType=="CHENFBK"){
						$(this).append("<td><input type='text'> </td>");
					}else if(quType=="CHENSCORE"){
						 $(this).append("<td>评分</td>");
					}
				}
			});
			quItemBody.find("input[name='saveTag']").val(0);
			newEditObj=quCoChenTable.find("tr:first .editAble:last");
			
			newEditObj.text(itemText);
			if(itemText==""){
				newEditObj.css("display","inline");
			}
			return newEditObj;
		}

		//添加矩阵单选题行选项
		function addChenRowItem(quItemBody,itemText){
			//得判断是否是table类型
			var newEditObj=null;
			//ul li处理
			var quChenRowHtml=$("#quChenRowModel").html();
			var quCoChenTable=quItemBody.find("table.quCoChenTable");
			var quCoChenTableTds=quCoChenTable.find("tr:first td");
			var quType=quItemBody.find("input[name='quType']").val();
			var appendTrHtml="<tr>";
			$.each(quCoChenTableTds,function(i){
				if(i==0){
					appendTrHtml+=quChenRowHtml;
				}else{
					if(quType=="CHENRADIO"){
						appendTrHtml+="<td><input type='radio'> </td>";
					}else if(quType=="CHENCHECKBOX"){
						appendTrHtml+="<td><input type='checkbox'> </td>";
					}else if(quType=="CHENFBK"){
						appendTrHtml+="<td><input type='text'> </td>";
					}else if(quType=="CHENSCORE"){
						appendTrHtml+="<td>评分</td>";
					}
				}
			});
			appendTrHtml+="</tr>";
			quCoChenTable.append(appendTrHtml);
			
			quItemBody.find("input[name='saveTag']").val(0);
			newEditObj=quCoChenTable.find("tr:last .editAble");
			
			newEditObj.text(itemText);
			if(itemText==""){
				newEditObj.css("display","inline");
			}
			return newEditObj;
		}
		//删除矩陈单选题选项
		function deleteChenOption(){
			var curEditTd=$(curEditObj).parents("td");
			var curEditTdClass=curEditTd.attr("class");
			if(curEditTdClass.indexOf("Column")>=0){
				deleteChenColumnOption();
			}else{
				deleteChenRowOption();
			}
		}
		/** 删除矩阵单选题列选项 **/
		function deleteChenColumnOption(){
			var optionParent=null;
			optionParent=$(curEditObj).parents("td.quChenColumnTd");
			var quOptionId=$(optionParent).find("input[name='quItemId']").val();
			if(quOptionId!="" && quOptionId!="0" ){
				var url=reqBasePath+"/design/qu-chen!ajaxDeleteColumn.action";
				var data="quItemId="+quOptionId;
				$.ajax({
					url:url,
					data:data,
					type:"post",
					success:function(msg){
						if(msg=="true"){
							delQuOptionCallBack(optionParent);
						}
					}
				});
			}else{
				delQuOptionCallBack(optionParent);
			}
		}
		/** 删除矩阵单选题行选项 **/
		function deleteChenRowOption(){
			var optionParent=null;
			optionParent=$(curEditObj).parents("td.quChenRowTd");
			var quOptionId=$(optionParent).find("input[name='quItemId']").val();
			if(quOptionId!="" && quOptionId!="0" ){
				var url=reqBasePath+"/design/qu-chen!ajaxDeleteRow.action";
				var data="quItemId="+quOptionId;
				$.ajax({
					url:url,
					data:data,
					type:"post",
					success:function(msg){
						if(msg=="true"){
							delQuOptionCallBack(optionParent);
						}
					}
				});
			}else{
				delQuOptionCallBack(optionParent);
			}
		}
		
		/**逻辑设置**/
		//添加逻辑选项
		function addQuDialogLogicTr(autoClass,trueCallback,falseCallback){
			//当前题的选项
			var quItemBody=$(dwDialogObj).parents(".surveyQuItemBody");
			var quItemInputCases=quItemBody.find(".quItemInputCase");
			var quLogicInputCase=quItemBody.find(".quLogicInputCase");
			var curQuId=quItemBody.find("input[name='quId']").val();
			var quType=quItemBody.find("input[name='quType']").val();
			
			var logicQuOptionSels=$("#dwQuLogicTable").find(".logicQuOptionSel");
			var dwLogicQuSels=$("#dwQuLogicTable").find(".logicQuSel");
			//判断有无选项任意选项
			var executeTag=true;
			$.each(logicQuOptionSels,function(){
				var selOptionVal=$(this).val();
				if(selOptionVal=="0"){
					executeTag=false;
					return false;
				}
			});
			
			if(executeTag){
				var appendTrHtml=$("#setQuLogicItemTrModel").html();
				if(quType==="SCORE"){
					appendTrHtml=$("#setQuLogicItemTrModel_score").html();
				}
				$("#dwQuLogicTable").append("<tr>"+appendTrHtml+"</tr>");
				var lastTr=$("#dwQuLogicTable").find("tr").last();
				if(quType==="FILLBLANK"){
					lastTr.find(".ifSpanText1").text("如果回答");
				}
				if(autoClass){
					var quLogicItemNum=quLogicInputCase.find("input[name='quLogicItemNum']");
					var newQuLogicItemNum=(parseInt(quLogicItemNum.val())+1);
					quLogicItemNum.val(newQuLogicItemNum);
					var newQuLogicItemClass="quLogicItem_"+newQuLogicItemNum;
					lastTr.attr("class",newQuLogicItemClass);
				}
				var dwQuOptionSel=lastTr.find(".logicQuOptionSel");
				var eachTag=true;
				if(quType==="CHENRADIO" || quType==="CHENCHECKBOX" || quType==="CHENSCORE" || quType==="CHENFBK"){
					var quChenColumnTds=quItemBody.find(".quChenColumnTd");
					var quChenRowTds=quItemBody.find(".quChenRowTd");
					$.each(quChenRowTds,function(){
						var rowText=$(this).find(".quCoOptionEdit").text();
						var rowQuItemId=$(this).find("input[name='quItemId']").val();	
						$.each(quChenColumnTds,function(){
							var colText=$(this).find(".quCoOptionEdit").text();
							var colQuItemId=$(this).find("input[name='quItemId']").val();
							var optionId=rowQuItemId+":"+colQuItemId;
							eachTag=true;
							$.each(logicQuOptionSels,function(){
								var selOptionVal=$(this).val();
								if(selOptionVal==optionId){
									eachTag=false;
									return false;
								}
							});
							if(eachTag){
								dwQuOptionSel.append("<option value='"+optionId+"'>"+rowText+":"+colText+"</option>");	
							}
						});
					});
				}else{
					$.each(quItemInputCases,function(){
						var optionText=$(this).parent().find("label.quCoOptionEdit").text();
						var optionId=$(this).find("input[name='quItemId']").val();
						eachTag=true;
						$.each(logicQuOptionSels,function(){
							var selOptionVal=$(this).val();
							if(selOptionVal==optionId){
								eachTag=false;
								return false;
							}
						});
						if(eachTag){
							dwQuOptionSel.append("<option value='"+optionId+"'>"+optionText+"</option>");	
						}
					});
				}
				if(logicQuOptionSels.size()==0){
					dwQuOptionSel.append("<option value='0'>任意选项</option>");	
				}else{
					$("#dwQuLogicTable").find(".logicQuOptionSel option[value='0']").remove();
				}
				if(quType==="FILLBLANK"){
					dwQuOptionSel.val("0");
				}
				var logicQuSel=lastTr.find(".logicQuSel");
				var quItemBodys=$("#dwSurveyQuContent .surveyQuItemBody");
				$.each(quItemBodys,function(){
					//logicQuSels
					if($(this).find(".quCoTitleEdit")[0]){
						var quCoNumText=$(this).find(".quCoNum").text();
						var quTitleText=$(this).find(".quCoTitleEdit").text();
						var quId=$(this).find("input[name='quId']").val();
						eachTag=true;
						if(curQuId==quId){
							eachTag=false;
						}
						if(eachTag){
							$.each(dwLogicQuSels,function(){
								var dwLogicQuSelVal=$(this).val();
								if(dwLogicQuSelVal==quId){
									eachTag=false;
									return false;
								}
							});
						}
						if(eachTag){
							logicQuSel.append("<option value='"+quId+"'>"+quCoNumText+quTitleText+"</option>");	
						}
					}
				});
				logicQuSel.append("<option value='1'>正常结束（计入结果）</option><option value='2'>提前结束（不计入结果）</option>");
				if(quType==="SCORE"){
					var logicScoreNum=lastTr.find(".logicScoreNum");
					logicScoreNum.empty();
					for(var i=1;i<=10;i++){
						logicScoreNum.append("<option value=\""+i+"\">"+i+"</option>");
					}
				}else if(quType==="ORDERQU"){
					dwQuOptionSel.empty();
					dwQuOptionSel.append("<option value='0'>回答完成</option>");
					lastTr.find(".ifSpanText1").text("如果本题");
				}
				if(autoClass){
					logicQuSel.prepend("<option value=''>-请选择题目-</option>");
					dwQuOptionSel.prepend("<option value=''>-请选择选项-</option>");
				}
				bindDialogRemoveLogic();
				trueCallback();
			}else{
				falseCallback();
			}
			
		}

		//绑定逻辑设置中选项删除事件
		function bindDialogRemoveLogic(){
			$(".dialogRemoveLogic").unbind();
			$(".dialogRemoveLogic").click(function(){
				var logicItemTr=$(this).parents("tr");
				var logicItemTrClass=logicItemTr.attr("class");
				//同时移除页面只保存的信息--注意如果已经保存到库中，修改
				var quItemBody=$(dwDialogObj).parents(".surveyQuItemBody");
				var quLogicItem=quItemBody.find("."+logicItemTrClass);
				if(quLogicItem[0]){
					//有则判断，是否已经加入到数据库
					var quLogicIdVal=quLogicItem.find("input[name='quLogicId']").val();
					if(quLogicIdVal!=""){
						quLogicItem.find("input[name='visibility']").val(0);
						quLogicItem.find("input[name='logicSaveTag']").val(0);
						quItemBody.find("input[name='saveTag']").val(0);
					}else{
						quLogicItem.remove();
					}
					//更新select option信息
					var logicQuOptionSel=logicItemTr.find(".logicQuOptionSel option:selected");
					var logicQuSel=logicItemTr.find(".logicQuSel option:selected");
					if(logicQuOptionSel.val()!=""){
						$("#dwQuLogicTable").find(".logicQuOptionSel").append("<option value='"+logicQuOptionSel.val()+"'>"+logicQuOptionSel.text()+"</option>");
					}
					if(logicQuSel.val()!=""){
						$("#dwQuLogicTable").find(".logicQuSel").append("<option value='"+logicQuSel.val()+"'>"+logicQuSel.text()+"</option>");
					}
				}
				logicItemTr.remove();
				refreshQuLogicInfo(quItemBody);
				return false;
			});
			$(".logicQuSel").unbind();
			$(".logicQuSel").change(function(){
				var thVal=$(this).val();
				//当题目选项中选项 提前结束，或正常结束，则不作处理
				if(thVal!=="1" && thVal!=="2"){
					//移除前面选项中存在的当前被选择的选项
					$("#dwQuLogicTable").find(".logicQuSel").not(this).find("option[value='"+thVal+"']").remove();			
				}
			});
		}
		
		//刷新每题的逻辑显示数目
		function refreshQuLogicInfo(quItemBody){
			var quLogicItems = quItemBody.find(".quLogicItem input[name='visibility'][value='1']");
			var quLogicItemSize = quLogicItems.size();
			quItemBody.find(".quLogicInfo").text(quLogicItemSize);
		}
		
		//显示弹出层
		function showDialog(thDialogObj){
			var thObjClass=thDialogObj.attr("class");
			curEditCallback();
			setShowDialogOffset(thDialogObj);
			var quItemBody=$(thDialogObj).parents(".surveyQuItemBody");
			$("#dwCommonDialog .dwQuDialogCon").hide();
			if(thObjClass.indexOf("addMoreOption")>=0){
				$("#dwCommonDialog .dwQuAddMore").show();
			}else if(thObjClass.indexOf("dwQuSet")>=0){
				$("#dwCommonDialog .dwQuSetCon").show();
				var quType=quItemBody.find("input[name='quType']").val();
				var isRequired=quItemBody.find("input[name='isRequired']").val();
				var hv=quItemBody.find("input[name='hv']").val();
				var randOrder=quItemBody.find("input[name='randOrder']").val();
				var cellCount=quItemBody.find("input[name='cellCount']").val();
				var paramInt01=quItemBody.find("input[name='paramInt01']");
				var paramInt02=quItemBody.find("input[name='paramInt02']");
				var contactsAttr=quItemBody.find("input[name='contactsAttr']").val();
				var contactsField=quItemBody.find("input[name='contactsField']").val();
				$("#dwCommonDialog input[name='setIsRequired']").prop("checked",false);
				$("#dwCommonDialog input[name='setRandOrder']").prop("checked",false);
				$("#dwCommonDialog select[name='setHv']").val(2);
				$("#dwCommonDialog input[name='setAutoContacts']").prop("checked",false);
				$("#dwCommonDialog .contactsFieldLi").hide();
				$("#dwCommonDialog .contactsAttrLi").hide();
				$("#dwCommonDialog .optionAutoOrder").hide();
				$("#dwCommonDialog .optionRangeHv").hide();
				$("#dwCommonDialog .scoreMinMax").hide();
				$("#dwCommonDialog .minMaxLi").hide();
				if(isRequired==1){
					$("#dwCommonDialog input[name='setIsRequired']").prop("checked",true);
				}
				if(randOrder==1){
					$("#dwCommonDialog input[name='setRandOrder']").prop("checked",true);
				}
				if(hv==3){
					$("#dwCommonDialog .option_range_3").show();
				}else{
					$("#dwCommonDialog .option_range_3").hide();
				}
				$("#dwCommonDialog select[name='setHv']").val(hv);
				$("#dwCommonDialog input[name='setCellCount']").val(cellCount);

				//单选，多选 才启用选项随机排列
				if(quType==="RADIO" || quType==="CHECKBOX"){
					$("#dwCommonDialog .optionAutoOrder").show();
					$("#dwCommonDialog .optionRangeHv").show();
				}else if(quType==="ORDERQU"){
					$("#dwCommonDialog .optionAutoOrder").show();
				}else if(quType==="SCORE"){
					$("#dwCommonDialog .optionAutoOrder").show();
					$("#dwCommonDialog .scoreMinMax").show();
					if(paramInt02[0]){
						$("#dwCommonDialog .scoreMinMax .maxScore").val(paramInt02.val());
					}
				}else if(quType==="MULTIFILLBLANK"){
					$("#dwCommonDialog .optionAutoOrder").show();
					$("#dwCommonDialog .minMaxLi").show();
					$("#dwCommonDialog .minMaxLi .minSpan .lgleftLabel").text("最少回答");
					$("#dwCommonDialog .minMaxLi .maxSpan").hide();
					$("#dwCommonDialog .minMaxLi .lgRightLabel").text("项");
					if(paramInt01[0]){
						$("#dwCommonDialog .minMaxLi .minNum").val(paramInt01.val());				
					}
				}

				//单选，多选，填空题情况下才启用关联到联系设置项
				if((quType=="RADIO" || quType=="CHECKBOX" || quType=="FILLBLANK")){
					$("#dwCommonDialog .contactsAttrLi").show();
					if( contactsAttr==1){
						$("#dwCommonDialog input[name='setAutoContacts']").prop("checked",true);
						$("#dwCommonDialog .contactsFieldLi").show();
						$("#dwCommonDialog select[name='setContactsField']").val(contactsField);
					}
				}
			}else if(thObjClass.indexOf("dwQuLogic")>=0){
				$("#dwCommonDialog .dwQuDialogLoad").show();
			}else if(thObjClass.indexOf("dwFbMenuBtn")>=0){
				$("#dwCommonDialog .dwQuFillDataTypeOption").show();
				var checkType_val=quItemBody.find("input[name='checkType']").val();
				if(checkType_val==""){
					checkType_val="NO";
				}
				var checkType=$("#dwCommonDialog select[name='quFill_checkType']");
				checkType.val(checkType_val);
			}else if(thObjClass.indexOf("quCoOptionEdit")>=0){
				$("#dwCommonDialog .dwQuRadioCheckboxOption").show();
				//设置回显值 isNote checkType
				var quOption_isNote=$("#dwCommonDialog input[name='quOption_isNote']");
				var quOption_checkType=$("#dwCommonDialog select[name='quOption_checkType']");
				var quOption_isRequiredFill=$("#dwCommonDialog input[name='quOption_isRequiredFill']");
				
				var quOptionParent=$(thDialogObj).parent();
				var isNote_val=quOptionParent.find("input[name='isNote']").val();
				var checkType_val=quOptionParent.find("input[name='checkType']").val();
				var isRequiredFill_val=quOptionParent.find("input[name='isRequiredFill']").val();
				
				if(isNote_val=="1"){
					quOption_isNote.prop("checked",true);
					$(".quOptionFillContentLi,.quOptionFillRequiredLi").show();
				}else{
					quOption_isNote.prop("checked",false);
					$(".quOptionFillContentLi,.quOptionFillRequiredLi").hide();
				}
				if(checkType_val==""){
					checkType_val="NO";
				}
				quOption_checkType.val(checkType_val);
				if(isRequiredFill_val=="1"){
					quOption_isRequiredFill.prop("checked",true);
				}else{
					quOption_isRequiredFill.prop("checked",false);
				}
				
			}else{
				//暂时加的
				$("#dwCommonDialog .dwQuAddMore").show();
			}
			dwDialogObj = thDialogObj;
		}
		
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	    //保存
	    $("body").on("click", "#saveBtn", function(){
	    	curEditCallback();
			dwCommonDialogHide();
			resetQuItemHover(null);
			
			top.winui.window.msg('保存中', {icon: 1,time: 1000});
			saveSurvey(function(){
				isSaveProgress = false;
				top.winui.window.msg('保存成功', {icon: 1,time: 2000});
			});
	    });
	    
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
	    			//保存单选
	    			saveRadio(quItemBody, callback);
	    		} else if(quType == "CHECKBOX") {
	    			saveCheckbox(quItemBody, callback);
	    		} else if(quType == "FILLBLANK") {
	    			saveFillblank(quItemBody, callback);
	    		} else if(quType == "SCORE") {
	    			saveScore(quItemBody, callback);
	    		} else if(quType == "ORDERQU") {
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
	     ** 新保存单选题
	     **/
	    function saveRadio(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var url = reqBasePath + "/design/qu-radio!ajaxSave.action";
	    		var quType = quItemBody.find("input[name='quType']").val();
	    		var quId = quItemBody.find("input[name='quId']").val();
	    		var orderById = quItemBody.find("input[name='orderById']").val();
	    		var isRequired = quItemBody.find("input[name='isRequired']").val();
	    		var hv = quItemBody.find("input[name='hv']").val();
	    		var randOrder = quItemBody.find("input[name='randOrder']").val();
	    		var cellCount = quItemBody.find("input[name='cellCount']").val();
	    		var contactsAttr = quItemBody.find("input[name='contactsAttr']").val();
	    		var contactsField = quItemBody.find("input[name='contactsField']").val();

	    		var data = "belongId=" + questionBelongId + "&orderById=" + orderById + "&tag=" + svTag + "&quType=" + quType + "&quId=" + quId;
	    		data += "&isRequired=" + isRequired + "&hv=" + hv + "&randOrder=" + randOrder + "&cellCount=" + cellCount;
	    		data += "&contactsAttr=" + contactsAttr + "&contactsField=" + contactsField;

	    		var quTitleSaveTag = quItemBody.find("input[name='quTitleSaveTag']").val();
	    		if(quTitleSaveTag == 0) {
	    			var quTitle = quItemBody.find(".quCoTitleEdit").html();
	    			quTitle = escape(encodeURIComponent(quTitle));
	    			data += "&quTitle=" + quTitle;
	    		}

	    		var quItemOptions = null;
	    		if(hv == 3) {
	    			//还有是table的情况需要处理
	    			quItemOptions = quItemBody.find(".quCoItem table.tableQuColItem tr td");
	    		} else {
	    			quItemOptions = quItemBody.find(".quCoItem li.quCoItemUlLi");
	    		}

	    		$.each(quItemOptions, function(i) {
	    			var optionValue = $(this).find("label.quCoOptionEdit").html();
	    			var optionId = $(this).find(".quItemInputCase input[name='quItemId']").val();
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			var isNote = $(this).find(".quItemInputCase input[name='isNote']").val();
	    			var checkType = $(this).find(".quItemInputCase input[name='checkType']").val();
	    			var isRequiredFill = $(this).find(".quItemInputCase input[name='isRequiredFill']").val();
	    			if(quItemSaveTag == 0) {
	    				optionValue = escape(encodeURIComponent(optionValue));
	    				data += "&optionValue_" + i + "=" + optionValue;
	    				data += "&optionId_" + i + "=" + optionId;
	    				data += "&isNote_" + i + "=" + isNote;
	    				data += "&checkType_" + i + "=" + checkType;
	    				data += "&isRequiredFill_" + i + "=" + isRequiredFill;
	    			}
	    			//更新 字母 title标记到选项上.
	    			$(this).addClass("quOption_" + i);
	    		});

	    		//逻辑选项
	    		var quLogicItems = quItemBody.find(".quLogicItem");
	    		$.each(quLogicItems, function(i) {
	    			var thClass = $(this).attr("class");
	    			thClass = thClass.replace("quLogicItem quLogicItem_", "");

	    			var quLogicId = $(this).find("input[name='quLogicId']").val();
	    			var cgQuItemId = $(this).find("input[name='cgQuItemId']").val();
	    			var skQuId = $(this).find("input[name='skQuId']").val();
	    			var logicSaveTag = $(this).find("input[name='logicSaveTag']").val();
	    			var visibility = $(this).find("input[name='visibility']").val();
	    			var logicType = $(this).find("input[name='logicType']").val();
	    			var itemIndex = thClass;
	    			if(logicSaveTag == 0) {
	    				data += "&quLogicId_" + itemIndex + "=" + quLogicId;
	    				data += "&cgQuItemId_" + itemIndex + "=" + cgQuItemId;
	    				data += "&skQuId_" + itemIndex + "=" + skQuId;
	    				data += "&visibility_" + itemIndex + "=" + visibility;
	    				data += "&logicType_" + itemIndex + "=" + logicType;
	    			}
	    		});
	    		$.ajax({
	    			url: url,
	    			data: data,
	    			type: 'post',
	    			success: function(msg) {
	    				if(msg != "error") {
	    					var jsons = eval("(" + msg + ")");
	    					var quId = jsons.id;
	    					quItemBody.find("input[name='quId']").val(quId);
	    					var quItems = jsons.quItems;
	    					$.each(quItems, function(i, item) {
	    						var quItemOption = quItemBody.find(".quOption_" + item.title);
	    						quItemOption.find("input[name='quItemId']").val(item.id);
	    						quItemOption.find(".quItemInputCase input[name='quItemSaveTag']").val(1);
	    					});

	    					//同步logic Id信息
	    					var quLogics = jsons.quLogics;
	    					$.each(quLogics, function(i, item) {
	    						var logicItem = quItemBody.find(".quLogicItem_" + item.title);
	    						logicItem.find("input[name='quLogicId']").val(item.id);
	    						logicItem.find("input[name='logicSaveTag']").val(1);
	    					});

	    					quItemBody.find("input[name='saveTag']").val(1);
	    					quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);

	    					//执行保存下一题
	    					saveQus(quItemBody.next(), callback);
	    					//同步-更新题目排序号
	    					quCBNum2++;
	    					exeQuCBNum();
	    				}
	    			}
	    		});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /** 添加选项 **/
	    /** 添加单选选项   **/
	    function addRadioItem(quItemBody, itemText) {
	    	//得判断是否是table类型
	    	var hv = quItemBody.find("input[name='hv']").val();
	    	var cellCount = quItemBody.find("input[name='cellCount']").val();
	    	var newEditObj = null;
	    	if(hv == 3) {
	    		//表格处理
	    		var quRadioItemHtml = $("#quRadioItem").html();
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
	    		//ul li处理
	    		var quRadioItemHtml = $("#quRadioItem").html();
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
	    /** 删除单选题选项 **/
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
	    		var url = reqBasePath + "/design/qu-radio!ajaxDelete.action";
	    		var data = "quItemId=" + quOptionId;
	    		$.ajax({
	    			url: url,
	    			data: data,
	    			type: "post",
	    			success: function(msg) {
	    				if(msg == "true") {
	    					delQuOptionCallBack(optionParent);
	    				}
	    			}
	    		});
	    	} else {
	    		delQuOptionCallBack(optionParent);
	    	}
	    }

	    //*******多选题*******//
	    /**
	     ** 新保存多选题
	     **/
	    function saveCheckbox(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {

	    		var url = reqBasePath + "/design/qu-checkbox!ajaxSave.action";
	    		var quType = quItemBody.find("input[name='quType']").val();
	    		var quId = quItemBody.find("input[name='quId']").val();
	    		var orderById = quItemBody.find("input[name='orderById']").val();;
	    		var isRequired = quItemBody.find("input[name='isRequired']").val();
	    		var hv = quItemBody.find("input[name='hv']").val();
	    		var randOrder = quItemBody.find("input[name='randOrder']").val();
	    		var cellCount = quItemBody.find("input[name='cellCount']").val();
	    		var contactsAttr = quItemBody.find("input[name='contactsAttr']").val();
	    		var contactsField = quItemBody.find("input[name='contactsField']").val();

	    		var data = "belongId=" + questionBelongId + "&orderById=" + orderById + "&tag=" + svTag + "&quType=" + quType + "&quId=" + quId;
	    		data += "&isRequired=" + isRequired + "&hv=" + hv + "&randOrder=" + randOrder + "&cellCount=" + cellCount;
	    		data += "&contactsAttr=" + contactsAttr + "&contactsField=" + contactsField;

	    		var quTitleSaveTag = quItemBody.find("input[name='quTitleSaveTag']").val();
	    		if(quTitleSaveTag == 0) {
	    			var quTitle = quItemBody.find(".quCoTitleEdit").html();
	    			quTitle = escape(encodeURIComponent(quTitle));
	    			data += "&quTitle=" + quTitle;
	    		}
	    		var quItemOptions = null;
	    		if(hv == 3) {
	    			//还有是table的情况需要处理
	    			quItemOptions = quItemBody.find(".quCoItem table.tableQuColItem tr td");
	    		} else {
	    			quItemOptions = quItemBody.find(".quCoItem li.quCoItemUlLi");
	    		}

	    		$.each(quItemOptions, function(i) {
	    			var optionValue = $(this).find("label.quCoOptionEdit").html();
	    			var optionId = $(this).find(".quItemInputCase input[name='quItemId']").val();
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			var isNote = $(this).find(".quItemInputCase input[name='isNote']").val();
	    			var checkType = $(this).find(".quItemInputCase input[name='checkType']").val();
	    			var isRequiredFill = $(this).find(".quItemInputCase input[name='isRequiredFill']").val();
	    			if(quItemSaveTag == 0) {
	    				optionValue = escape(encodeURIComponent(optionValue));
	    				data += "&optionValue_" + i + "=" + optionValue;
	    				data += "&optionId_" + i + "=" + optionId;
	    				data += "&isNote_" + i + "=" + isNote;
	    				data += "&checkType_" + i + "=" + checkType;
	    				data += "&isRequiredFill_" + i + "=" + isRequiredFill;
	    			}
	    			//更新 字母 title标记到选项上.
	    			$(this).addClass("quOption_" + i);
	    		});
	    		//逻辑选项
	    		var quLogicItems = quItemBody.find(".quLogicItem");
	    		$.each(quLogicItems, function(i) {
	    			var thClass = $(this).attr("class");
	    			thClass = thClass.replace("quLogicItem quLogicItem_", "");

	    			var quLogicId = $(this).find("input[name='quLogicId']").val();
	    			var cgQuItemId = $(this).find("input[name='cgQuItemId']").val();
	    			var skQuId = $(this).find("input[name='skQuId']").val();
	    			var logicSaveTag = $(this).find("input[name='logicSaveTag']").val();
	    			var visibility = $(this).find("input[name='visibility']").val();
	    			var logicType = $(this).find("input[name='logicType']").val();
	    			var itemIndex = thClass;
	    			if(logicSaveTag == 0) {
	    				data += "&quLogicId_" + itemIndex + "=" + quLogicId;
	    				data += "&cgQuItemId_" + itemIndex + "=" + cgQuItemId;
	    				data += "&skQuId_" + itemIndex + "=" + skQuId;
	    				data += "&visibility_" + itemIndex + "=" + visibility;
	    				data += "&logicType_" + itemIndex + "=" + logicType;
	    			}
	    		});

	    		$.ajax({
	    			url: url,
	    			data: data,
	    			type: 'post',
	    			success: function(msg) {
	    				if(msg != "error") {
	    					var jsons = eval("(" + msg + ")");
	    					var quId = jsons.id;
	    					quItemBody.find("input[name='quId']").val(quId);
	    					var quItems = jsons.quItems;
	    					$.each(quItems, function(i, item) {
	    						var quItemOption = quItemBody.find(".quOption_" + item.title);
	    						quItemOption.find("input[name='quItemId']").val(item.id);
	    						quItemOption.find(".quItemInputCase input[name='quItemSaveTag']").val(1);
	    					});

	    					//同步logic Id信息
	    					var quLogics = jsons.quLogics;
	    					$.each(quLogics, function(i, item) {
	    						var logicItem = quItemBody.find(".quLogicItem_" + item.title);
	    						logicItem.find("input[name='quLogicId']").val(item.id);
	    						logicItem.find("input[name='logicSaveTag']").val(1);
	    					});

	    					quItemBody.find("input[name='saveTag']").val(1);
	    					quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);

	    					//执行保存下一题
	    					saveQus(quItemBody.next(), callback);
	    					//同步-更新题目排序号
	    					quCBNum2++;
	    					exeQuCBNum();
	    				}
	    			}
	    		});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /** 添加选项 **/
	    /** 添加多选选项   **/
	    function addCheckboxItem(quItemBody, itemText) {
	    	//得判断是否是table类型
	    	var hv = quItemBody.find("input[name='hv']").val();
	    	var cellCount = quItemBody.find("input[name='cellCount']").val();
	    	var newEditObj = null;
	    	if(hv == 3) {
	    		//表格处理
	    		var quRadioItemHtml = $("#quCheckboxItem").html();
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
	    		var quRadioItemHtml = $("#quCheckboxItem").html();
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
	    /** 删除多选题选项 **/
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
	    		var url = reqBasePath + "/design/qu-checkbox!ajaxDelete.action";
	    		var data = "quItemId=" + quOptionId;
	    		$.ajax({
	    			url: url,
	    			data: data,
	    			type: "post",
	    			success: function(msg) {
	    				if(msg == "true") {
	    					delQuOptionCallBack(optionParent);
	    				}
	    			}
	    		});
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

	    //*******填空题*******//
	    /**
	     * 新保存填空题
	     **/
	    function saveFillblank(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0 && isNull(quItemBody.find("input[name='quId']").val())) {
	    		var data = {
    				belongId: parent.rowId,
    				orderById: quItemBody.find("input[name='orderById']").val(),
    				tag: svTag,
    				quId: quItemBody.find("input[name='quId']").val(),
    				isRequired: quItemBody.find("input[name='isRequired']").val(),
    				hv: quItemBody.find("input[name='hv']").val(),
    				randOrder: quItemBody.find("input[name='randOrder']").val(),
    				cellCount: quItemBody.find("input[name='cellCount']").val(),
    				answerInputWidth: quItemBody.find("input[name='answerInputWidth']").val(),
    				answerInputRow: quItemBody.find("input[name='answerInputRow']").val(),
    				contactsAttr: quItemBody.find("input[name='contactsAttr']").val(),
    				contactsField: quItemBody.find("input[name='contactsField']").val(),
    				checkType: quItemBody.find("input[name='checkType']").val(),
    				quTitle: '',
	    		};

	    		var quTitleSaveTag = quItemBody.find("input[name='quTitleSaveTag']").val();
	    		if(quTitleSaveTag == 0) {
	    			var quTitle = quItemBody.find(".quCoTitleEdit").html();
	    			data.quTitle = encodeURI(quTitle);
	    		}
	    		//逻辑选项
	    		var quLogicItems = quItemBody.find(".quLogicItem");
	    		var list = [];
	    		$.each(quLogicItems, function(i) {
	    			var logicSaveTag = $(this).find("input[name='logicSaveTag']").val();
	    			if(logicSaveTag == 0) {
	    				var s = {
    						quLogicId: $(this).find("input[name='quLogicId']").val(),
    						cgQuItemId: $(this).find("input[name='cgQuItemId']").val(),
    						skQuId: $(this).find("input[name='skQuId']").val(),
    						visibility: $(this).find("input[name='visibility']").val(),
    						logicType: $(this).find("input[name='logicType']").val(),
    						key: $(this).attr("class").replace("quLogicItem quLogicItem_", ""),
    	    			};
    	    			list.push(s);
	    			}
	    		});
	    		data.logic = JSON.stringify(list);
	    		AjaxPostUtil.request({url:reqBasePath + "dwsurveydirectory006", params:data, type:'json', callback:function(json){
	 	   			if(json.returnCode == 0){
		 	   			var quId = json.bean.id;
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
	 	   			}else{
	 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 	   			}
	 	   		}});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    //*****评分题****//
	    /**
	     * 新保存评分题
	     **/
	    function saveScore(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0 && isNull(quItemBody.find("input[name='quId']").val())) {
	    		var data = {
    				belongId: parent.rowId,
    				orderById: quItemBody.find("input[name='orderById']").val(),
    				tag: svTag,
    				quId: quItemBody.find("input[name='quId']").val(),
    				isRequired: quItemBody.find("input[name='isRequired']").val(),
    				hv: quItemBody.find("input[name='hv']").val(),
    				randOrder: quItemBody.find("input[name='randOrder']").val(),
    				cellCount: quItemBody.find("input[name='cellCount']").val(),
    				paramInt01: quItemBody.find("input[name='paramInt01']").val(),
    				paramInt02: quItemBody.find("input[name='paramInt02']").val(),
    				quTitle: '',
	    		};

	    		var quTitleSaveTag = quItemBody.find("input[name='quTitleSaveTag']").val();
	    		if(quTitleSaveTag == 0) {
	    			var quTitle = quItemBody.find(".quCoTitleEdit").html();
	    			data.quTitle = encodeURI(quTitle);
	    		}
	    		//评分题选项td
	    		var quItemOptions = quItemBody.find(".quCoItem table.quCoItemTable tr td.quOptionEditTd");
	    		var scoreTd = [];
	    		$.each(quItemOptions, function(i) {
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			if(quItemSaveTag == 0) {
	    				var s = {
    						optionValue: encodeURI($(this).find("label.quCoOptionEdit").html()),
    						optionId: $(this).find(".quItemInputCase input[name='quItemId']").val(),
    						key: i,
    	    			};
	    				scoreTd.push(s);
	    			}
	    			//更新 字母 title标记到选项上.
	    			$(this).addClass("quOption_" + i);
	    		});
	    		data.scoreTd = JSON.stringify(scoreTd);

	    		//逻辑选项
	    		var quLogicItems = quItemBody.find(".quLogicItem");
	    		var list = [];
	    		$.each(quLogicItems, function(i) {
	    			var logicSaveTag = $(this).find("input[name='logicSaveTag']").val();
	    			if(logicSaveTag == 0) {
	    				var s = {
    						quLogicId: $(this).find("input[name='quLogicId']").val(),
    						cgQuItemId: $(this).find("input[name='cgQuItemId']").val(),
    						skQuId: $(this).find("input[name='skQuId']").val(),
    						visibility: $(this).find("input[name='visibility']").val(),
    						logicType: $(this).find("input[name='logicType']").val(),
    						scoreNum: $(this).find("input[name='scoreNum']").val(),
    						geLe: $(this).find("input[name='geLe']").val(),
    						key: $(this).attr("class").replace("quLogicItem quLogicItem_", ""),
    	    			};
    	    			list.push(s);
	    			}

	    		});
	    		data.logic = JSON.stringify(list);
	    		AjaxPostUtil.request({url:reqBasePath + "dwsurveydirectory007", params:data, type:'json', callback:function(json){
	 	   			if(json.returnCode == 0){
		 	   			var quId = json.bean.id;
		 	   			quItemBody.find("input[name='saveTag']").val(1);
		 	   			quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
						quItemBody.find("input[name='quId']").val(quId);
						var quItems = json.bean.quItems;
    					$.each(quItems, function(i, item) {
    						var quItemOption = quItemBody.find(".quOption_" + item.orderById);
    						quItemOption.find("input[name='quItemId']").val(item.id);
    						quItemOption.find(".quItemInputCase input[name='quItemSaveTag']").val(1);
    					});
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
	 	   			}else{
	 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 	   			}
	 	   		}});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /** 添加选项 **/
	    /** 添加评分项   **/
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
	    /** 删除评分Score选项 **/
	    function deleteScoreOption() {
	    	var optionParent = null;
	    	optionParent = $(curEditObj).parents("tr.quScoreOptionTr");

	    	var quOptionId = $(optionParent).find("input[name='quItemId']").val();
	    	if(quOptionId != "" && quOptionId != "0") {
	    		var url = reqBasePath + "/design/qu-score!ajaxDelete.action";
	    		var data = "quItemId=" + quOptionId;
	    		$.ajax({
	    			url: url,
	    			data: data,
	    			type: "post",
	    			success: function(msg) {
	    				if(msg == "true") {
	    					delQuOptionCallBack(optionParent);
	    				}
	    			}
	    		});
	    	} else {
	    		delQuOptionCallBack(optionParent);
	    	}
	    }

	    //*****排序题****//
	    /**
	     * 新保存排序题
	     **/
	    function saveOrderqu(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0 && isNull(quItemBody.find("input[name='quId']").val())) {
	    		var data = {
    				belongId: parent.rowId,
    				orderById: quItemBody.find("input[name='orderById']").val(),
    				tag: svTag,
    				quId: quItemBody.find("input[name='quId']").val(),
    				isRequired: quItemBody.find("input[name='isRequired']").val(),
    				hv: quItemBody.find("input[name='hv']").val(),
    				randOrder: quItemBody.find("input[name='randOrder']").val(),
    				cellCount: quItemBody.find("input[name='cellCount']").val(),
    				quTitle: '',
	    		};

	    		var quTitleSaveTag = quItemBody.find("input[name='quTitleSaveTag']").val();
	    		if(quTitleSaveTag == 0) {
	    			var quTitle = quItemBody.find(".quCoTitleEdit").html();
	    			data.quTitle = encodeURI(quTitle);
	    		}
	    		//评分题选项td
	    		var quItemOptions = quItemBody.find(".quCoItem .quOrderByLeft  li.quCoItemUlLi");
	    		var orderquTd = [];
	    		$.each(quItemOptions, function(i) {
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			if(quItemSaveTag == 0) {
	    				var s = {
    						optionValue: encodeURI($(this).find("label.quCoOptionEdit").html()),
    						optionId: $(this).find(".quItemInputCase input[name='quItemId']").val(),
    						key: i,
    	    			};
	    				orderquTd.push(s);
	    			}
	    			$(this).addClass("quOption_" + i);
	    		});
	    		data.orderquTd = JSON.stringify(orderquTd);

	    		//逻辑选项
	    		var quLogicItems = quItemBody.find(".quLogicItem");
	    		var list = [];
	    		$.each(quLogicItems, function(i) {
	    			var logicSaveTag = $(this).find("input[name='logicSaveTag']").val();
	    			if(logicSaveTag == 0) {
	    				var s = {
    						quLogicId: $(this).find("input[name='quLogicId']").val(),
    						cgQuItemId: $(this).find("input[name='cgQuItemId']").val(),
    						skQuId: $(this).find("input[name='skQuId']").val(),
    						visibility: $(this).find("input[name='visibility']").val(),
    						logicType: $(this).find("input[name='logicType']").val(),
    						key: $(this).attr("class").replace("quLogicItem quLogicItem_", ""),
    	    			};
    	    			list.push(s);
	    			}

	    		});
	    		data.logic = JSON.stringify(list);
	    		
	    		AjaxPostUtil.request({url:reqBasePath + "dwsurveydirectory008", params:data, type:'json', callback:function(json){
	 	   			if(json.returnCode == 0){
		 	   			var quId = json.bean.id;
		 	   			quItemBody.find("input[name='saveTag']").val(1);
		 	   			quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
			 	   		quItemBody.find("input[name='quId']").val(quId);
						var quItems = json.bean.quItems;
						$.each(quItems, function(i, item) {
							var quItemOption = quItemBody.find(".quOption_" + item.title);
							quItemOption.find("input[name='quItemId']").val(item.id);
							quItemOption.find(".quItemInputCase input[name='quItemSaveTag']").val(1);
						});
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
	 	   			}else{
	 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 	   			}
	 	   		}});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /** 添加选项 **/
	    /** 添加排序项   **/
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
	    /** 删除排序选项 **/
	    function deleteOrderquOption() {
	    	var optionParent = null;
	    	optionParent = $(curEditObj).parents("li.quCoItemUlLi");
	    	var quItemBody = $(curEditObj).parents(".surveyQuItemBody");
	    	var rmQuOrderTableTr = quItemBody.find(".quOrderByRight table.quOrderByTable tr:last");

	    	var quOptionId = $(optionParent).find("input[name='quItemId']").val();
	    	if(quOptionId != "" && quOptionId != "0") {
	    		var url = reqBasePath + "/design/qu-orderqu!ajaxDelete.action";
	    		var data = "quItemId=" + quOptionId;
	    		$.ajax({
	    			url: url,
	    			data: data,
	    			type: "post",
	    			success: function(msg) {
	    				if(msg == "true") {
	    					delQuOptionCallBack(optionParent);
	    					rmQuOrderTableTr.remove();
	    				}
	    			}
	    		});
	    	} else {
	    		delQuOptionCallBack(optionParent);
	    		rmQuOrderTableTr.remove();
	    	}
	    }

	    //*******分页标记*******//
	    /**
	     ** 新保存分页标记
	     **/
	    function savePagetag(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var url = reqBasePath + "/design/qu-pagetag!ajaxSave.action";
	    		var quType = quItemBody.find("input[name='quType']").val();
	    		var quId = quItemBody.find("input[name='quId']").val();
	    		var orderById = quItemBody.find("input[name='orderById']").val();;
	    		var isRequired = quItemBody.find("input[name='isRequired']").val();
	    		var hv = quItemBody.find("input[name='hv']").val();
	    		var randOrder = quItemBody.find("input[name='randOrder']").val();
	    		var cellCount = quItemBody.find("input[name='cellCount']").val();

	    		var data = "belongId=" + questionBelongId + "&orderById=" + orderById + "&tag=" + svTag + "&quType=" + quType + "&quId=" + quId;
	    		data += "&isRequired=" + isRequired + "&hv=" + hv + "&randOrder=" + randOrder + "&cellCount=" + cellCount;

	    		var quTitleSaveTag = quItemBody.find("input[name='quTitleSaveTag']").val();
	    		if(quTitleSaveTag == 0) {
	    			var quTitle = quItemBody.find(".quCoTitleEdit").html();
	    			quTitle = escape(encodeURIComponent(quTitle));
	    			data += "&quTitle=" + quTitle;
	    		}
	    		//逻辑选项
	    		var quLogicItems = quItemBody.find(".quLogicItem");
	    		$.each(quLogicItems, function(i) {
	    			var thClass = $(this).attr("class");
	    			thClass = thClass.replace("quLogicItem quLogicItem_", "");

	    			var quLogicId = $(this).find("input[name='quLogicId']").val();
	    			var cgQuItemId = $(this).find("input[name='cgQuItemId']").val();
	    			var skQuId = $(this).find("input[name='skQuId']").val();
	    			var logicSaveTag = $(this).find("input[name='logicSaveTag']").val();
	    			var visibility = $(this).find("input[name='visibility']").val();
	    			var logicType = $(this).find("input[name='logicType']").val();
	    			var itemIndex = thClass;
	    			if(logicSaveTag == 0) {
	    				data += "&quLogicId_" + itemIndex + "=" + quLogicId;
	    				data += "&cgQuItemId_" + itemIndex + "=" + cgQuItemId;
	    				data += "&skQuId_" + itemIndex + "=" + skQuId;
	    				data += "&visibility_" + itemIndex + "=" + visibility;
	    				data += "&logicType_" + itemIndex + "=" + logicType;
	    			}

	    		});

	    		$.ajax({
	    			url: url,
	    			data: data,
	    			type: 'post',
	    			success: function(msg) {
	    				if(msg != "error") {
	    					var jsons = eval("(" + msg + ")");
	    					var quId = jsons.id;
	    					quItemBody.find("input[name='quId']").val(quId);

	    					//同步logic Id信息
	    					var quLogics = jsons.quLogics;
	    					$.each(quLogics, function(i, item) {
	    						var logicItem = quItemBody.find(".quLogicItem_" + item.title);
	    						logicItem.find("input[name='quLogicId']").val(item.id);
	    						logicItem.find("input[name='logicSaveTag']").val(1);
	    					});

	    					quItemBody.find("input[name='saveTag']").val(1);
	    					quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
	    					//执行保存下一题
	    					saveQus(quItemBody.next(), callback);
	    					//同步-更新题目排序号
	    					quCBNum2++;
	    					exeQuCBNum();
	    				}
	    			}
	    		});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    //*******段落说明题*******//
	    /**
	     ** 新保存段落题
	     **/
	    function saveParagraph(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var url = reqBasePath + "/design/qu-paragraph!ajaxSave.action";
	    		var quType = quItemBody.find("input[name='quType']").val();
	    		var quId = quItemBody.find("input[name='quId']").val();
	    		var orderById = quItemBody.find("input[name='orderById']").val();;
	    		var isRequired = quItemBody.find("input[name='isRequired']").val();
	    		var hv = quItemBody.find("input[name='hv']").val();
	    		var randOrder = quItemBody.find("input[name='randOrder']").val();
	    		var cellCount = quItemBody.find("input[name='cellCount']").val();

	    		var data = "belongId=" + questionBelongId + "&orderById=" + orderById + "&tag=" + svTag + "&quType=" + quType + "&quId=" + quId;
	    		data += "&isRequired=" + isRequired + "&hv=" + hv + "&randOrder=" + randOrder + "&cellCount=" + cellCount;

	    		var quTitleSaveTag = quItemBody.find("input[name='quTitleSaveTag']").val();
	    		if(quTitleSaveTag == 0) {
	    			var quTitle = quItemBody.find(".quCoTitleEdit").html();
	    			quTitle = escape(encodeURIComponent(quTitle));
	    			data += "&quTitle=" + quTitle;
	    		}
	    		//逻辑选项
	    		var quLogicItems = quItemBody.find(".quLogicItem");
	    		$.each(quLogicItems, function(i) {
	    			var thClass = $(this).attr("class");
	    			thClass = thClass.replace("quLogicItem quLogicItem_", "");

	    			var quLogicId = $(this).find("input[name='quLogicId']").val();
	    			var cgQuItemId = $(this).find("input[name='cgQuItemId']").val();
	    			var skQuId = $(this).find("input[name='skQuId']").val();
	    			var logicSaveTag = $(this).find("input[name='logicSaveTag']").val();
	    			var visibility = $(this).find("input[name='visibility']").val();
	    			var logicType = $(this).find("input[name='logicType']").val();
	    			var itemIndex = thClass;
	    			if(logicSaveTag == 0) {
	    				data += "&quLogicId_" + itemIndex + "=" + quLogicId;
	    				data += "&cgQuItemId_" + itemIndex + "=" + cgQuItemId;
	    				data += "&skQuId_" + itemIndex + "=" + skQuId;
	    				data += "&visibility_" + itemIndex + "=" + visibility;
	    				data += "&logicType_" + itemIndex + "=" + logicType;
	    			}

	    		});

	    		$.ajax({
	    			url: url,
	    			data: data,
	    			type: 'post',
	    			success: function(msg) {
	    				//alert(msg);// resultJson quItemId
	    				if(msg != "error") {
	    					var jsons = eval("(" + msg + ")");
	    					//alert(jsons);
	    					var quId = jsons.id;
	    					quItemBody.find("input[name='quId']").val(quId);

	    					//同步logic Id信息
	    					var quLogics = jsons.quLogics;
	    					$.each(quLogics, function(i, item) {
	    						var logicItem = quItemBody.find(".quLogicItem_" + item.title);
	    						logicItem.find("input[name='quLogicId']").val(item.id);
	    						logicItem.find("input[name='logicSaveTag']").val(1);
	    					});

	    					quItemBody.find("input[name='saveTag']").val(1);
	    					quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);
	    					//执行保存下一题
	    					saveQus(quItemBody.next(), callback);
	    					//同步-更新题目排序号
	    					quCBNum2++;
	    					exeQuCBNum();
	    				}
	    			}
	    		});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /** 多项填空题 **/
	    /**
	     ** 新保存多项填空题
	     **/
	    function saveMultiFillblank(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var url = reqBasePath + "/design/qu-multi-fillblank!ajaxSave.action";
	    		var quType = quItemBody.find("input[name='quType']").val();
	    		var quId = quItemBody.find("input[name='quId']").val();
	    		var orderById = quItemBody.find("input[name='orderById']").val();
	    		var isRequired = quItemBody.find("input[name='isRequired']").val();
	    		var hv = quItemBody.find("input[name='hv']").val();
	    		var randOrder = quItemBody.find("input[name='randOrder']").val();
	    		var cellCount = quItemBody.find("input[name='cellCount']").val();
	    		var paramInt01 = quItemBody.find("input[name='paramInt01']").val();
	    		var paramInt02 = quItemBody.find("input[name='paramInt02']").val();

	    		var data = "belongId=" + questionBelongId + "&orderById=" + orderById + "&tag=" + svTag + "&quType=" + quType + "&quId=" + quId;
	    		data += "&isRequired=" + isRequired + "&hv=" + hv + "&randOrder=" + randOrder + "&cellCount=" + cellCount;
	    		data += "&paramInt01=" + paramInt01 + "&paramInt02=" + paramInt02;

	    		var quTitleSaveTag = quItemBody.find("input[name='quTitleSaveTag']").val();
	    		if(quTitleSaveTag == 0) {
	    			var quTitle = quItemBody.find(".quCoTitleEdit").html();
	    			quTitle = escape(encodeURIComponent(quTitle));
	    			data += "&quTitle=" + quTitle;
	    		}
	    		//评分题选项td
	    		var quItemOptions = quItemBody.find(".quCoItem table.mFillblankTable tr td.mFillblankTableEditTd");
	    		$.each(quItemOptions, function(i) {
	    			var optionValue = $(this).find("label.quCoOptionEdit").html();
	    			var optionId = $(this).find(".quItemInputCase input[name='quItemId']").val();
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			if(quItemSaveTag == 0) {
	    				optionValue = escape(encodeURIComponent(optionValue));
	    				data += "&optionValue_" + i + "=" + optionValue;
	    				data += "&optionId_" + i + "=" + optionId;
	    			}
	    			//更新 字母 title标记到选项上.
	    			$(this).addClass("quOption_" + i);
	    		});

	    		//逻辑选项
	    		var quLogicItems = quItemBody.find(".quLogicItem");
	    		$.each(quLogicItems, function(i) {
	    			var thClass = $(this).attr("class");
	    			thClass = thClass.replace("quLogicItem quLogicItem_", "");

	    			var quLogicId = $(this).find("input[name='quLogicId']").val();
	    			var cgQuItemId = $(this).find("input[name='cgQuItemId']").val();
	    			var skQuId = $(this).find("input[name='skQuId']").val();
	    			var logicSaveTag = $(this).find("input[name='logicSaveTag']").val();
	    			var visibility = $(this).find("input[name='visibility']").val();
	    			var logicType = $(this).find("input[name='logicType']").val();
	    			var itemIndex = thClass;
	    			if(logicSaveTag == 0) {
	    				data += "&quLogicId_" + itemIndex + "=" + quLogicId;
	    				data += "&cgQuItemId_" + itemIndex + "=" + cgQuItemId;
	    				data += "&skQuId_" + itemIndex + "=" + skQuId;
	    				data += "&visibility_" + itemIndex + "=" + visibility;
	    				data += "&logicType_" + itemIndex + "=" + logicType;
	    			}

	    		});
	    		$.ajax({
	    			url: url,
	    			data: data,
	    			type: 'post',
	    			success: function(msg) {
	    				if(msg != "error") {
	    					var jsons = eval("(" + msg + ")");
	    					var quId = jsons.id;
	    					quItemBody.find("input[name='quId']").val(quId);
	    					var quItems = jsons.quItems;
	    					$.each(quItems, function(i, item) {
	    						var quItemOption = quItemBody.find(".quOption_" + item.title);
	    						quItemOption.find("input[name='quItemId']").val(item.id);
	    						quItemOption.find(".quItemInputCase input[name='quItemSaveTag']").val(1);
	    					});

	    					//同步logic Id信息
	    					var quLogics = jsons.quLogics;
	    					$.each(quLogics, function(i, item) {
	    						var logicItem = quItemBody.find(".quLogicItem_" + item.title);
	    						logicItem.find("input[name='quLogicId']").val(item.id);
	    						logicItem.find("input[name='logicSaveTag']").val(1);
	    					});

	    					quItemBody.find("input[name='saveTag']").val(1);
	    					quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);

	    					//执行保存下一题
	    					saveQus(quItemBody.next(), callback);
	    					//同步-更新题目排序号
	    					quCBNum2++;
	    					exeQuCBNum();
	    				}
	    			}
	    		});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /** 添加选项 **/
	    /** 添加多项填空题项   **/
	    function addMultiFillblankItem(quItemBody, itemText) {
	    	//得判断是否是table类型
	    	var newEditObj = null;
	    	//ul li处理
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
	    /** 删除多项填空题选项 **/
	    function deleteMultiFillblankOption() {
	    	var optionParent = null;
	    	optionParent = $(curEditObj).parents("tr.mFillblankTableTr");

	    	var quOptionId = $(optionParent).find("input[name='quItemId']").val();
	    	if(quOptionId != "" && quOptionId != "0") {
	    		var url = reqBasePath + "/design/qu-multi-fillblank!ajaxDelete.action";
	    		var data = "quItemId=" + quOptionId;
	    		$.ajax({
	    			url: url,
	    			data: data,
	    			type: "post",
	    			success: function(msg) {
	    				if(msg == "true") {
	    					delQuOptionCallBack(optionParent);
	    				}
	    			}
	    		});
	    	} else {
	    		delQuOptionCallBack(optionParent);
	    	}
	    }

	    /** 矩阵单选题 **/
	    /**
	     ** 新保存矩阵单选题
	     **/
	    function saveChen(quItemBody, callback) {
	    	var saveTag = quItemBody.find("input[name='saveTag']").val();
	    	if(saveTag == 0) {
	    		var url = reqBasePath + "/design/qu-chen!ajaxSave.action";
	    		var quType = quItemBody.find("input[name='quType']").val();
	    		var quId = quItemBody.find("input[name='quId']").val();
	    		var orderById = quItemBody.find("input[name='orderById']").val();
	    		var isRequired = quItemBody.find("input[name='isRequired']").val();
	    		var hv = quItemBody.find("input[name='hv']").val();
	    		var randOrder = quItemBody.find("input[name='randOrder']").val();
	    		var cellCount = quItemBody.find("input[name='cellCount']").val();

	    		var data = "belongId=" + questionBelongId + "&orderById=" + orderById + "&tag=" + svTag + "&quType=" + quType + "&quId=" + quId;
	    		data += "&isRequired=" + isRequired + "&hv=" + hv + "&randOrder=" + randOrder + "&cellCount=" + cellCount;

	    		var quTitleSaveTag = quItemBody.find("input[name='quTitleSaveTag']").val();
	    		if(quTitleSaveTag == 0) {
	    			var quTitle = quItemBody.find(".quCoTitleEdit").html();
	    			quTitle = escape(encodeURIComponent(quTitle));
	    			data += "&quTitle=" + quTitle;
	    		}
	    		//矩阵列选项td
	    		var quColumnOptions = quItemBody.find(".quCoItem table.quCoChenTable tr td.quChenColumnTd");
	    		$.each(quColumnOptions, function(i) {
	    			var optionValue = $(this).find("label.quCoOptionEdit").html();
	    			var optionId = $(this).find(".quItemInputCase input[name='quItemId']").val();
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			if(quItemSaveTag == 0) {
	    				optionValue = escape(encodeURIComponent(optionValue));
	    				data += "&columnValue_" + i + "=" + optionValue;
	    				data += "&columnId_" + i + "=" + optionId;
	    			}
	    			//更新 字母 title标记到选项上.
	    			$(this).addClass("quColumnOption_" + i);
	    		});

	    		//矩阵行选项td
	    		var quColumnOptions = quItemBody.find(".quCoItem table.quCoChenTable tr td.quChenRowTd");
	    		$.each(quColumnOptions, function(i) {
	    			var optionValue = $(this).find("label.quCoOptionEdit").html();
	    			var optionId = $(this).find(".quItemInputCase input[name='quItemId']").val();
	    			var quItemSaveTag = $(this).find(".quItemInputCase input[name='quItemSaveTag']").val();
	    			if(quItemSaveTag == 0) {
	    				optionValue = escape(encodeURIComponent(optionValue));
	    				data += "&rowValue_" + i + "=" + optionValue;
	    				data += "&rowId_" + i + "=" + optionId;
	    			}
	    			//更新 字母 title标记到选项上.
	    			$(this).addClass("quRowOption_" + i);
	    		});

	    		//逻辑选项
	    		var quLogicItems = quItemBody.find(".quLogicItem");
	    		$.each(quLogicItems, function(i) {
	    			var thClass = $(this).attr("class");
	    			thClass = thClass.replace("quLogicItem quLogicItem_", "");
	    			var quLogicId = $(this).find("input[name='quLogicId']").val();
	    			var cgQuItemId = $(this).find("input[name='cgQuItemId']").val();
	    			var skQuId = $(this).find("input[name='skQuId']").val();
	    			var logicSaveTag = $(this).find("input[name='logicSaveTag']").val();
	    			var visibility = $(this).find("input[name='visibility']").val();
	    			var logicType = $(this).find("input[name='logicType']").val();
	    			var itemIndex = thClass;
	    			if(logicSaveTag == 0) {
	    				data += "&quLogicId_" + itemIndex + "=" + quLogicId;
	    				data += "&cgQuItemId_" + itemIndex + "=" + cgQuItemId;
	    				data += "&skQuId_" + itemIndex + "=" + skQuId;
	    				data += "&visibility_" + itemIndex + "=" + visibility;
	    				data += "&logicType_" + itemIndex + "=" + logicType;
	    			}
	    		});
	    		$.ajax({
	    			url: url,
	    			data: data,
	    			type: 'post',
	    			success: function(msg) {
	    				if(msg != "error") {
	    					var jsons = eval("(" + msg + ")");
	    					//alert(jsons);
	    					var quId = jsons.id;
	    					quItemBody.find("input[name='quId']").val(quId);
	    					//列选项
	    					var quColumnItems = jsons.quColumnItems;
	    					$.each(quColumnItems, function(i, item) {
	    						var quItemOption = quItemBody.find(".quColumnOption_" + item.title);
	    						quItemOption.find("input[name='quItemId']").val(item.id);
	    						quItemOption.find(".quItemInputCase input[name='quItemSaveTag']").val(1);
	    					});
	    					//行选项
	    					var quRowItems = jsons.quRowItems;
	    					$.each(quRowItems, function(i, item) {
	    						var quItemOption = quItemBody.find(".quRowOption_" + item.title);
	    						quItemOption.find("input[name='quItemId']").val(item.id);
	    						quItemOption.find(".quItemInputCase input[name='quItemSaveTag']").val(1);
	    					});

	    					//同步logic Id信息
	    					var quLogics = jsons.quLogics;
	    					$.each(quLogics, function(i, item) {
	    						var logicItem = quItemBody.find(".quLogicItem_" + item.title);
	    						logicItem.find("input[name='quLogicId']").val(item.id);
	    						logicItem.find("input[name='logicSaveTag']").val(1);
	    					});

	    					quItemBody.find("input[name='saveTag']").val(1);
	    					quItemBody.find(".quCoTitle input[name='quTitleSaveTag']").val(1);

	    					//执行保存下一题
	    					saveQus(quItemBody.next(), callback);
	    					//同步-更新题目排序号
	    					quCBNum2++;
	    					exeQuCBNum();
	    				}
	    			}
	    		});
	    	} else {
	    		saveQus(quItemBody.next(), callback);
	    	}
	    }

	    /** 添加列选项 **/
	    function addChenItem(eventObj, quItemBody, itemText) {
	    	var eventObjClass = eventObj.attr("class");
	    	if(eventObjClass.indexOf("Column") >= 0) {
	    		return addChenColumnItem(quItemBody, itemText);
	    	} else {
	    		return addChenRowItem(quItemBody, itemText);
	    	}
	    }
	    /** 添加矩阵单选题列选项   **/
	    function addChenColumnItem(quItemBody, itemText) {
	    	//得判断是否是table类型
	    	var newEditObj = null;
	    	//ul li处理
	    	var quRadioColumnHtml = $("#quChenColumnModel").html();
	    	var quCoChenTable = quItemBody.find("table.quCoChenTable");
	    	var quCoChenTableTrs = quCoChenTable.find("tr");
	    	var quType = quItemBody.find("input[name='quType']").val();
	    	$.each(quCoChenTableTrs, function(i) {
	    		if(i == 0) {
	    			$(this).append(quRadioColumnHtml);
	    		} else {
	    			if(quType == "CHENRADIO") {
	    				$(this).append("<td><input type='radio'> </td>");
	    			} else if(quType == "CHENCHECKBOX") {
	    				$(this).append("<td><input type='checkbox'> </td>");
	    			} else if(quType == "CHENFBK") {
	    				$(this).append("<td><input type='text'> </td>");
	    			} else if(quType == "CHENSCORE") {
	    				$(this).append("<td>评分</td>");
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

	    //添加矩阵单选题行选项
	    function addChenRowItem(quItemBody, itemText) {
	    	//得判断是否是table类型
	    	var newEditObj = null;
	    	//ul li处理
	    	var quChenRowHtml = $("#quChenRowModel").html();
	    	var quCoChenTable = quItemBody.find("table.quCoChenTable");
	    	var quCoChenTableTds = quCoChenTable.find("tr:first td");
	    	var quType = quItemBody.find("input[name='quType']").val();
	    	var appendTrHtml = "<tr>";
	    	$.each(quCoChenTableTds, function(i) {
	    		if(i == 0) {
	    			appendTrHtml += quChenRowHtml;
	    		} else {
	    			if(quType == "CHENRADIO") {
	    				appendTrHtml += "<td><input type='radio'> </td>";
	    			} else if(quType == "CHENCHECKBOX") {
	    				appendTrHtml += "<td><input type='checkbox'> </td>";
	    			} else if(quType == "CHENFBK") {
	    				appendTrHtml += "<td><input type='text'> </td>";
	    			} else if(quType == "CHENSCORE") {
	    				appendTrHtml += "<td>评分</td>";
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
	    //删除矩陈单选题选项
	    function deleteChenOption() {
	    	var curEditTd = $(curEditObj).parents("td");
	    	var curEditTdClass = curEditTd.attr("class");
	    	if(curEditTdClass.indexOf("Column") >= 0) {
	    		// deleteChenRadioColumnOption();
	    		deleteChenColumnOption();
	    	} else {
	    		// deleteChenRadioRowOption();
	    		deleteChenRowOption();
	    	}
	    }
	    /** 删除矩阵单选题列选项 **/
	    function deleteChenColumnOption() {
	    	var optionParent = null;
	    	optionParent = $(curEditObj).parents("td.quChenColumnTd");
	    	var quOptionId = $(optionParent).find("input[name='quItemId']").val();
	    	if(quOptionId != "" && quOptionId != "0") {
	    		var url = reqBasePath + "/design/qu-chen!ajaxDeleteColumn.action";
	    		var data = "quItemId=" + quOptionId;
	    		$.ajax({
	    			url: url,
	    			data: data,
	    			type: "post",
	    			success: function(msg) {
	    				if(msg == "true") {
	    					delQuOptionCallBack(optionParent);
	    				}
	    			}
	    		});
	    	} else {
	    		delQuOptionCallBack(optionParent);
	    	}
	    }
	    /** 删除矩阵单选题行选项 **/
	    function deleteChenRowOption() {
	    	var optionParent = null;
	    	optionParent = $(curEditObj).parents("td.quChenRowTd");
	    	var quOptionId = $(optionParent).find("input[name='quItemId']").val();
	    	if(quOptionId != "" && quOptionId != "0") {
	    		var url = reqBasePath + "/design/qu-chen!ajaxDeleteRow.action";
	    		var data = "quItemId=" + quOptionId;
	    		$.ajax({
	    			url: url,
	    			data: data,
	    			type: "post",
	    			success: function(msg) {
	    				if(msg == "true") {
	    					delQuOptionCallBack(optionParent);
	    				}
	    			}
	    		});
	    	} else {
	    		delQuOptionCallBack(optionParent);
	    	}
	    }

	    /**逻辑设置**/
	    //添加逻辑选项
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
	    				//				var optionText=$(this).prev().text();
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
	    			//logicQuSels
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
	    		} else if(quType === "ORDERQU") {
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

	    //绑定逻辑设置中选项删除事件
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

	    //刷新每题的逻辑显示数目
	    function refreshQuLogicInfo(quItemBody) {
	    	var quLogicItems = quItemBody.find(".quLogicItem input[name='visibility'][value='1']");
	    	var quLogicItemSize = quLogicItems.size();
	    	quItemBody.find(".quLogicInfo").text(quLogicItemSize);
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

	    function setSelectText(el) {
	    	try {
	    		window.getSelection().selectAllChildren(el[0]); //全选
	    		window.getSelection().collapseToEnd(el[0]); //光标置后
	    	} catch(err) {
	    		//在此处理错误
	    	}
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
	    
	    //选项卡切换
	    $("body").on("click", "#tabType li", function(){
	    	$("#tabType li").removeClass("current");
	    	$(this).addClass("current");
	    	$(".tools_tab_div").css('display','none');
	    	$("#" + $(this).attr("id").replace("_li", "")).css('display','block');
	    });
	    
	});
	    
});