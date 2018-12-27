layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'jqueryUI'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form;
	    
	    var svTag=2;//表示题目是问卷题还是题库中题

	    // 题目保存后回调时机比较参数
		var quCBNum = 0;// 比较值1
		var quCBNum2 = 0;// 比较值2
		var curEditObj = null;
		var curEditObjOldHtml = "";
		var dwDialogObj = null;
		var ueEditObj = null;// UE编辑器，关联的编辑对象
		var isDrag = false;
		var appQuObj = null;
		var myeditor = null;
		var ueDialog = null;
		
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
	    var _varioustemplates = getFileContent('tpl/dwsurveydesign/varioustemplates.tpl');
	    
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
	    
	    //问卷设置
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
			var pageTagSize=pageTags.size()+1;
			$.each(pageTags,function(i){
				var quItemBody=$(this).parents(".surveyQuItemBody");
				var pageQuContent=quItemBody.find(".pageQuContent");
				pageQuContent.text("下一页（"+(i+1)+"/"+pageTagSize+"）");
			});
		}
		
		function bindQuHoverItem(){
			$(".SeniorEdit").unbind();
			$(".SeniorEdit").click(function(){
				ueDialog.dialog( "open" );
				ueEditObj=curEditObj;
				myeditor.destroy();
				myeditor = null;
				myeditor = UE.getEditor("dialogUeditor",{
				    initialContent: "",//初始化编辑器的内容
				    elementPathEnabled:false,
			        wordCount:false,
			        autosave:false,
				    initialFrameWidth : 678,
				    initialFrameHeight : 300
				});
				//先出加载提示图标
				myeditor.ready(function(){
					setTimeout(function(){
						if(curEditObj!=null){
							myeditor.setContent($(curEditObj).html());
							myeditor.focus(true);
						}
			        },800);
				});
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
					appQuObj=$(this);
				}else{
					//显示
					$(this).addClass("hover");
					$(".pageBorderTop").removeClass("nohover");
					//如果是填空
					appQuObj=$(this);
				}
			},function(){
				$(".pageBorderTop").addClass("nohover");
				$(this).removeClass("showLine");
				var hoverTag=$(this).find("input[name='hoverTag']").val();
				if(hoverTag!="hover"){
					$(this).removeClass("hover");
				}
				appQuObj=null;
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
				var quBody=$(this).parents(".surveyQuItemBody");
				if(confirm("确认要删除此题吗？")){
					var quId=quBody.find("input[name='quId']").val();
					if(quId!=""){
						var url=ctx+"/design/question!ajaxDelete.action";
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
				showDialog($(this));
				var quItemBody=$(this).parents(".surveyQuItemBody");
				var quType=quItemBody.find("input[name='quType']").val();
				//默认加载图标
				var fristQuItemBody=$("#dwSurveyQuContent .li_surveyQuItemBody").first();
				saveQus(fristQuItemBody,function(){
					$(".dwQuDialogCon").hide();
					$("#dwCommonDialog .dwQuDialogLogic").show();
					resetQuItemHover(quItemBody);
					bindDialogRemoveLogic();
					$("#dwQuLogicTable").empty();
					//逻辑数据回显示
					var quLogicItems=quItemBody.find(".quLogicItem");
					if(quLogicItems[0]){
						$.each(quLogicItems,function(){
							var skQuId=$(this).find("input[name='skQuId']").val();
							var cgQuItemId=$(this).find("input[name='cgQuItemId']").val();
							var logicType=$(this).find("input[name='logicType']").val();
							// 设置分数 geLe scoreNum
							var geLe="";
							var scoreNum="";
							if(quType==="SCORE"){
								geLe=$(this).find("input[name='geLe']").val();
								scoreNum=$(this).find("input[name='scoreNum']").val();
							}
							var thClass=$(this).attr("class");
							thClass=thClass.replace("quLogicItem", "");
							thClass=thClass.replace(" ", "");
							//回显相应的选项
							addQuDialogLogicTr(false,function(){
								//执行成功--设置值
								var lastTr=$("#dwQuLogicTable").find("tr").last();
								lastTr.attr("class",thClass);
								lastTr.find(".logicQuOptionSel").val(cgQuItemId);
								lastTr.find(".logicQuSel").val(skQuId);
								lastTr.find(".logicType").val(logicType);
								lastTr.find(".logicQuOptionSel").change();
								lastTr.find(".logicQuSel").change();
								// 设置分数 geLe scoreNum
								if(quType==="SCORE"){
									lastTr.find(".logicScoreGtLt").val(geLe);
									lastTr.find(".logicScoreNum").val(scoreNum);
								}
							},function(){});
						});
					}else{
						$(".dwQuDialogAddLogic").click();
					}
				});
				return false;
			});
			
			//添加行选项
			$(".addOption,.addColumnOption,.addRowOption").unbind();
			$(".addOption,.addColumnOption,.addRowOption").click(function(){
				var quItemBody=$(this).parents(".surveyQuItemBody");
				var quType=quItemBody.find("input[name='quType']").val();
				if(quType=="RADIO"){
					//添加单选选项
					editAble(addRadioItem(quItemBody,""));
				}else if(quType=="CHECKBOX"){
					editAble(addCheckboxItem(quItemBody, ""));
				}else if(quType=="SCORE"){
					editAble(addScoreItem(quItemBody, "新选项"));
				}else if(quType=="ORDERQU"){
					editAble(addOrderquItem(quItemBody, "新选项"));
				}else if(quType=="MULTIFILLBLANK"){
					editAble(addMultiFillblankItem(quItemBody, "新选项"));
				}else if(quType=="CHENRADIO" || quType=="CHENCHECKBOX" || quType=="CHENFBK" || quType=="CHENSCORE"){//矩陈单选题,矩阵多选题
					editAble(addChenItem($(this),quItemBody, "新选项"));
				}
				bindQuHoverItem();
				return false;
			});
			
			//批量添加事件
			$(".addMoreOption,.addMoreRowOption,.addMoreColumnOption").unbind();
			$(".addMoreOption,.addMoreRowOption,.addMoreColumnOption").click(function(){
				showDialog($(this));
				var quItemBody=$(this).parents(".surveyQuItemBody");
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
				var quItemBody=$(curEditObj).parents(".surveyQuItemBody");
				var quType=quItemBody.find("input[name='quType']").val();
				var hv=quItemBody.find("input[name='hv']").val();
				if(hv==3){
					var nextTd=$(curEditObj).parents("td");
					var prevTd=nextTd.prev();
					if(prevTd[0]){
						dwOptionUp(prevTd, nextTd);
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
					var nextLi=null;
					var prevLi=null;
					var nextLiAfterHtml="";
					if(quType==="RADIO" || quType==="CHECKBOX" || quType==="ORDERQU"){
						nextLi=$(curEditObj).parents("li.quCoItemUlLi");
						prevLi=nextLi.prev();
						var prevLiHtml=prevLi.html();
						nextLiAfterHtml="<li class='quCoItemUlLi'>"+prevLiHtml+"</li>";
					}else if(quType==="SCORE"){
						nextLi=$(curEditObj).parents("tr.quScoreOptionTr");
						prevLi=nextLi.prev();
						var prevLiHtml=prevLi.html();
						nextLiAfterHtml="<tr class='quScoreOptionTr'>"+prevLiHtml+"</tr>";
					}else if(quType==="MULTIFILLBLANK"){
						nextLi=$(curEditObj).parents("tr.mFillblankTableTr");
						prevLi=nextLi.prev();
						var prevLiHtml=prevLi.html();
						nextLiAfterHtml="<tr class='mFillblankTableTr'>"+prevLiHtml+"</tr>";
					}else if(quType==="CHENRADIO" || quType==="CHENCHECKBOX" || quType==="CHENSCORE" || quType==="CHENFBK"){
						nextLi=$(curEditObj).parents("tr.quChenRowTr");
						if(nextLi[0]){
							prevLi=nextLi.prev();
							var prevLiHtml=prevLi.html();
							nextLiAfterHtml="<tr class='quChenRowTr'>"+prevLiHtml+"</tr>";
						}else{
							nextLi=$(curEditObj).parents("td.quChenColumnTd");
							prevLi=nextLi.prev();
							var prevLiHtml=prevLi.html();
							nextLiAfterHtml="<td class='quChenColumnTd'>"+prevLiHtml+"</td>";
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

		function setShowDialogOffset(thDialogObj){
			var thObjClass=thDialogObj.attr("class");
			if(thObjClass.indexOf("dwFbMenuBtn")<0 && thObjClass.indexOf("quCoOptionEdit")<0){
				var thOffset=thDialogObj.offset();
				$("#dwCommonDialog").show(0,function(){
					var thOffsetTop=thOffset.top;
					var thOffsetLeft=thOffset.left+40;
					var dwCommonRefIcon=$("#dwCommonDialog").find(".dwCommonRefIcon");
					dwCommonRefIcon.removeClass("right");
					dwCommonRefIcon.removeClass("left");
					browseWidth=$(window).width();			
					browseHeight=$(window).height();
					if((thOffsetLeft-100)>browseWidth/2){
						thOffsetLeft=thOffsetLeft-$("#dwCommonDialog").width()-50;
						dwCommonRefIcon.addClass("right");
					}else{
						dwCommonRefIcon.addClass("left");
					}
					$("#dwCommonDialog").offset({ top: thOffsetTop, left: thOffsetLeft });
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

			var quItemBody=$(editAbleObj).parents(".surveyQuItemBody");
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
		function showUIDialog(thDialogObj){
			var thObjClass = thDialogObj.attr("class");
			$("#modelUIDialog").dialog("open");
			$(".dwQuDialogCon").hide();
			if(thObjClass.indexOf("dwFbMenuBtn") >= 0){
				$("#modelUIDialog .dwQuFillDataTypeOption").show();
				$("#modelUIDialog").dialog("open");
				var quItemBody = $(thDialogObj).parents(".surveyQuItemBody");
				var checkType_val = quItemBody.find("input[name='checkType']").val();
				var answerInputWidth_val = quItemBody.find("input[name='answerInputWidth']").val();
				var answerInputRow_val = quItemBody.find("input[name='answerInputRow']").val();
				if(checkType_val == ""){
					checkType_val = "NO";
				}
				var checkType = $("#modelUIDialog select[name='quFill_checkType']");
				checkType.val(checkType_val);
				var qu_inputWidth = $("#modelUIDialog input[name='qu_inputWidth']");
				var qu_inputRow = $("#modelUIDialog input[name='qu_inputRow']");
				if(answerInputWidth_val == ""){
					answerInputWidth_val = "300";
				}
				if(answerInputRow_val == ""){
					answerInputRow_val = "1";
				}
				qu_inputWidth.val(answerInputWidth_val);
				qu_inputRow.val(answerInputRow_val);
				resetQuItemHover(quItemBody);
				$(thDialogObj).parents(".quCoItemUlLi").addClass("menuBtnClick");
				$("#modelUIDialog").dialog("option","height",220);
			}else if(thObjClass.indexOf("quCoOptionEdit")>=0) {
				$("#modelUIDialog .dwQuRadioCheckboxOption").show();
				//设置回显值 isNote checkType
				var quOption_isNote=$("#modelUIDialog input[name='quOption_isNote']");
				var quOption_checkType=$("#modelUIDialog select[name='quOption_checkType']");
				var quOption_isRequiredFill=$("#modelUIDialog input[name='quOption_isRequiredFill']");
				var quOptionParent=$(thDialogObj).parent();
				var isNote_val=quOptionParent.find("input[name='isNote']").val();
				var checkType_val=quOptionParent.find("input[name='checkType']").val();
				var isRequiredFill_val=quOptionParent.find("input[name='isRequiredFill']").val();
				if(isNote_val=="1"){
					quOption_isNote.prop("checked",true);
					$(".quOptionFillContentLi,.quOptionFillRequiredLi").show();
					$("#modelUIDialog").dialog("option","height",250);
				}else{
					quOption_isNote.prop("checked",false);
					$(".quOptionFillContentLi,.quOptionFillRequiredLi").hide();
					$("#modelUIDialog").dialog("option","height",180);
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
			}else if(thObjClass.indexOf("surveyAttrSetToolbar_li")>=0){
				$("#modelUIDialog .dwSurveyAttrSetDialog").show();
				$("#modelUIDialog").dialog("option","height",390);
			}
			dwDialogObj=thDialogObj;
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
				var url=ctx+"/design/qu-chen!ajaxDeleteColumn.action";
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
				var url=ctx+"/design/qu-chen!ajaxDeleteRow.action";
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
	    
	    //选项卡切换
	    $("body").on("click", "#tabType li", function(){
	    	$("#tabType li").removeClass("current");
	    	$(this).addClass("current");
	    	$(".tools_tab_div").css('display','none');
	    	$("#" + $(this).attr("id").replace("_li", "")).css('display','block');
	    });
	    
	});
	    
});