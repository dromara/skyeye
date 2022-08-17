
// 当前编辑选中的项
var curEditObj = null;
// 弹框
var dwDialogObj = null;
// 当前编辑选中项的内容
var curEditObjOldHtml = "";

layui.define(["jquery", 'form'], function(exports) {
	var jQuery = layui.jquery;
	var form = layui.form;
	(function($) {
		
		$(document).click(function(event){
			var _con = $('.dwCommonEdit');// 设置目标区域
			if(!_con.is(event.target) && _con.has(event.target).length === 0){
				curEditCallback();
			}
		});
		
		$("body").on("click", ".dwOptionDel", function() {
			deleteDwOption();
			return false;
		});
		
		// 添加行选项
		$("body").on("click", ".addOption,.addColumnOption,.addRowOption", function() {
			var quItemBody = $(this).parents(".surveyQuItemBody");
			var quType = quItemBody.find("input[name='quType']").val();
			if(quType == "RADIO") {
				// 添加单选选项
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
			return false;
		});
		
		// 准备移除选项---如果选项内容不为空
		function curEditCallback(){
			if(curEditObj != null){
				var dwEditHtml = $("#dwComEditContent").html();
				setCurEditContent(dwEditHtml);
			}
		}
		
		function setCurEditContent(dwEditHtml){
			var thClass = $(curEditObj).attr("class");
			if(dwEditHtml == ""){
				if (!isNull(curEditObj)){
					deleteDwOption();
				}
			}else if(dwEditHtml != curEditObjOldHtml){
				// 更新值
				$(curEditObj).html(dwEditHtml);
			}
			dwCommonEditHide();
		}
		
		// 删除选项
		function deleteDwOption(){
			if (!isNull(curEditObj)){
				var quItemBody = $(curEditObj).parents(".surveyQuItemBody");
				var quType = quItemBody.find("input[name='quType']").val();
				if(quType == "RADIO"){
					// 添加单选选项
					deleteRadioOption();
				}else if(quType == "CHECKBOX"){
					deleteCheckboxOption();
				}else if(quType == "SCORE"){
					deleteScoreOption();
				}else if(quType == "ORDERQU"){
					deleteOrderquOption();
				}else if(quType == "MULTIFILLBLANK"){
					deleteMultiFillblankOption();
				}else if(quType == "CHENRADIO" || quType == "CHENCHECKBOX" 
						|| quType == "CHENFBK" || quType == "CHENSCORE"){
					deleteChenOption();
				}
			}
		}
		
		//触发显示题目标题编辑框 
		function editAble(editAbleObj){
			curEditCallback();

			var thClass = $(editAbleObj).attr("class");
			var editOffset = $(editAbleObj).offset();
			$("#dwCommonEditRoot").removeClass();
			if(thClass.indexOf("quCoTitleEdit") > 0){
				//题目标题
				$("#dwCommonEditRoot").addClass("quEdit");
			}else if(thClass.indexOf("quCoOptionEdit") > 0){
				//题目选项
				$("#dwCommonEditRoot").addClass("quOptionEdit");
			}
			$("#dwCommonEditRoot").show();
			$("#dwCommonEditRoot").offset({top:editOffset.top,left:editOffset.left});
			$("#dwComEditContent").focus();
			$("#dwComEditContent").html($(editAbleObj).html());
			
			$("#dwCommonEditRoot .dwCommonEdit").css("min-width", 200);
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
		
		//批量添加事件
		$("body").on("click", ".addMoreOption,.addMoreRowOption,.addMoreColumnOption", function() {
			var thDialogObj = $(this);
			curEditCallback();
			setShowDialogOffset(thDialogObj);
			$("#dwCommonDialog .dwQuAddMore").show();
			dwDialogObj = thDialogObj;
			return false;
		});
		
		// 批量添加的框保存
		$("#dwDialogSaveMoreItem").click(function(){
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
						// 评分题
						addScoreItem(quItemBody, item);
					}else if(quType == "ORDERQU"){
						addOrderquItem(quItemBody, item);
					}else if(quType == "MULTIFILLBLANK"){
						addMultiFillblankItem(quItemBody, item);
					}else if(quType == "CHENRADIO" || quType == "CHENCHECKBOX" || quType == "CHENFBK" || quType == "CHENSCORE"){
						addChenItem(dwDialogObj, quItemBody, item);
					}
				}
			});
			form.render();
			$("#dwQuMoreTextarea").val("");
			dwCommonDialogHide();
		});
		
		/**
	     * 关闭设置弹出框
	     */
	    $("body").on("click", "#dwCommonDialogClose", function() {
			dwCommonDialogHide();
	    });
		
		//隐藏批量添加的框
		function dwCommonDialogHide(){
			$("#dwCommonDialog").hide();
			$(".menuBtnClick").removeClass("menuBtnClick");
			dwDialogObj = null;
		}
		
		//显示批量添加的框
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
		
		//绑定编辑
		$("body").on("click", ".editAble", function() {
			editAble($(this));
			return false;
		});
		
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
	    		deleteRowList.push(quOptionId);
	    	}
    		delQuOptionCallBack(optionParent);
	    }

	    /** 添加选项 **/
	    /** 添加多选选项   **/
	    function addCheckboxItem(quItemBody, itemText) {
	    	//得判断是否是table类型
	    	var hv = quItemBody.find("input[name='hv']").val();
	    	var newEditObj = null;
	    	if(hv == 3) {
		    	var cellCount = quItemBody.find("input[name='cellCount']").val();
	    		//表格处理
	    		var quCheckboxItem = $("#quCheckboxItem").html();
	    		var quTableObj = quItemBody.find(".quCoItem table.tableQuColItem");
	    		var emptyTdDiv = quTableObj.find("div.emptyTd");
	    		if(emptyTdDiv[0]) {
	    			//表示有空位
	    			var emptyTd = emptyTdDiv.first().parents("td");
	    			emptyTd.empty();
	    			emptyTd.append(quCheckboxItem);
	    		} else {
	    			//没有空位，根据cellCount生成新的tr,td
	    			var appendTr = "<tr>";
	    			for(var i = 0; i < cellCount; i++) {
	    				appendTr += "<td>";
	    				if(i == 0) {
	    					appendTr += quCheckboxItem;
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
	    		var quCheckboxItem = $("#quCheckBoxItem").html();
	    		var quCoItemUl = quItemBody.find(".quCoItem ul");
	    		quCoItemUl.append("<li class='quCoItemUlLi'>" + quCheckboxItem + "</li>");
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
	    		deleteRowList.push(quOptionId);
	    	}
    		delQuOptionCallBack(optionParent);
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
	    }
	    
	    function dwCommonEditHide(){
			$("#dwCommonEditRoot").hide();
			$(".dwComEditMenuUl").hide();
			curEditObj = null;
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
	    /**
	     * 删除评分Score选项
	     */
	    function deleteScoreOption() {
	    	var optionParent = null;
	    	optionParent = $(curEditObj).parents("tr.quScoreOptionTr");
	    	var quOptionId = $(optionParent).find("input[name='quItemId']").val();
	    	if(quOptionId != "" && quOptionId != "0") {
	    		deleteRowList.push(quOptionId);
	    	}
    		delQuOptionCallBack(optionParent);
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
	     * 删除排序选项
	     */
	    function deleteOrderquOption() {
	    	var optionParent = null;
	    	optionParent = $(curEditObj).parents("li.quCoItemUlLi");
	    	var quItemBody = $(curEditObj).parents(".surveyQuItemBody");
	    	var rmQuOrderTableTr = quItemBody.find(".quOrderByRight table.quOrderByTable tr:last");
	    	var quOptionId = $(optionParent).find("input[name='quItemId']").val();
	    	if(quOptionId != "" && quOptionId != "0") {
	    		deleteRowList.push(quOptionId);
	    	}
    		delQuOptionCallBack(optionParent);
    		rmQuOrderTableTr.remove();
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
	    /**
	     * 删除多项填空题选项
	     */
	    function deleteMultiFillblankOption() {
	    	var optionParent = null;
	    	optionParent = $(curEditObj).parents("tr.mFillblankTableTr");
	    	var quOptionId = $(optionParent).find("input[name='quItemId']").val();
	    	if(quOptionId != "" && quOptionId != "0") {
	    		deleteRowList.push(quOptionId);
	    	}
    		delQuOptionCallBack(optionParent);
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
	    			var checkNameIn = $(this).attr("checkNameIn");
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

	    // 添加矩阵单选题行选项
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
	    	var appendTrHtml = "<tr checkNameIn='" + checkNameIn + "'>";
	    	$.each(quCoChenTableTds, function(i) {
	    		if(i == 0) {
	    			appendTrHtml += quChenRowHtml;
	    		} else {
	    			if(quType == "CHENRADIO") {
	    				appendTrHtml += "<td><input type='radio' class='chenRadioInput' name='" + checkNameIn + "'></td>";
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
	    
	    //删除矩陈单选题选项
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
				deleteColumnList.push(quOptionId);
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
				deleteRowList.push(quOptionId);
			}
			delQuOptionCallBack(optionParent);
		}
	    
	    //知识点选择
	    $("body").on("click", "#schoolKnowledgeChoose", function() {
	    	_openNewWindows({
				url: "../../tpl/schoolKnowledgePoints/schoolKnowledgePointsChoose.html", 
				title: "知识点选择",
				pageId: "schoolKnowledgePointsChoose",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
					var str = "";
					$.each(schoolKnowledgeMationList, function(i, item){
						str += '<br><span class="layui-badge layui-bg-blue" style="height: 25px !important; line-height: 25px !important; margin: 5px 0px;">' + item.title + '</span>';
					});
					$("#schoolKnowledgeChoose").parent().html('<button type="button" class="layui-btn layui-btn-primary layui-btn-xs" id="schoolKnowledgeChoose">知识点选择</button>' + str);
				}});
	    });
		
	})(jQuery);
});