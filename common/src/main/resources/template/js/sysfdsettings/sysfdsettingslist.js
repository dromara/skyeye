
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
	    var $ = layui.$,
	    	form = layui.form;
		// 表格的序号
		var rowNum = 1;

		var holidaysTypeJsonTemplate = $("#holidaysTypeJsonTemplate").html(),
			yearHolidaysMationTemplate = $("#yearHolidaysMationTemplate").html();

		showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sysfdsettings001",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
			method: "GET",
		 	template: getFileContent('tpl/sysfdsettings/sysfdsettingsTemplate.tpl'),
		 	ajaxSendAfter:function (json) {
		 		// 设置未指定负责人选中
		 		$("input:radio[name=noChargeId][value=" + json.bean.noChargeId + "]").attr("checked", true);
		 		
		 		// 获取上一次solr数据同步的时间
		        AjaxPostUtil.request({url: reqBasePath + "forumcontent019", params: {}, type: 'json', callback: function (json) {
		            if(json.returnCode == 0) {
		                if(!isNull(json.bean.synchronousTime)){
		                    $("#synchronousTime").text(json.bean.synchronousTime);
		                } else {
		                    $("#synchronousTime").text("无");
		                }
		            }else {
		                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		            }
		        }});

		        // 加载假期类型
				loadHolidayType(json);

				// 加载年假信息
				loadYearHolidays(json);

				// 加载异常考勤制度管理信息
				loadAbnormal(json);

				// 加载ERP以及生产订单的审核设置
				loadErpExamineBasicDesign(json);

				// 加载事件
				loadEvent();

				// 加载表单提交事件
				loadSubmit();
			}
		});

		/**
		 * 加载假期类型
		 *
		 * @param json
		 */
		function loadHolidayType(json) {
			if (!isNull(json.bean.holidaysTypeJson)) {
				$.each(JSON.parse(json.bean.holidaysTypeJson), function (i, item) {
					addHolidaysTypeJsonRow();
					$("#holidayName" + (rowNum - 1)).val(item.holidayName);
					$("#holidayNo" + (rowNum - 1)).val(item.holidayNo);
					$("#offPercentageMoney" + (rowNum - 1)).val(item.offPercentageMoney);
					$("#whetherYearHour" + (rowNum - 1)).val(item.whetherYearHour);
					$("#whetherComLeave" + (rowNum - 1)).val(item.whetherComLeave);
					$("#remark" + (rowNum - 1)).val(item.remark);
				});
			}
		}

		/**
		 * 加载年假信息
		 *
		 * @param json
		 */
		function loadYearHolidays(json) {
			if (!isNull(json.bean.yearHolidaysMation)) {
				$.each(JSON.parse(json.bean.yearHolidaysMation), function (i, item) {
					addYearHolidaysMationRow();
					$("#yearType" + (rowNum - 1)).val(item.yearType);
					$("#yearHour" + (rowNum - 1)).val(item.yearHour);
					$("#yearRemark" + (rowNum - 1)).val(item.remark);
				});
			}
		}

		/**
		 * 加载异常考勤制度管理信息
		 *
		 * @param json
		 */
		function loadAbnormal(json) {
			if (!isNull(json.bean.abnormalMation)) {
				$.each(JSON.parse(json.bean.abnormalMation), function (i, item) {
					$("#abnormal" + (i + 1)).val(item.abnormal);
					$("#abnormalRemark" + (i + 1)).val(item.remark);
					if (item.abnormal === "1") {
						$("#abnormalMoney" + (i + 1)).show();
						$("#abnormalMoneyLabel" + (i + 1)).hide();
						$("#abnormalMoney" + (i + 1)).val(item.abnormalMoney);
					} else if (item.abnormal === "2") {
						$("#abnormalMoney" + (i + 1)).hide();
						$("#abnormalMoneyLabel" + (i + 1)).show();
					}
				});
			}
		}

		/**
		 * 加载ERP以及生产订单的审核设置
		 *
		 * @param json
		 */
		function loadErpExamineBasicDesign(json) {
			if (!isNull(json.bean.erpExamineBasicDesign)) {
				var erpExamineBasicDesign = JSON.parse(json.bean.erpExamineBasicDesign);
				$("#erpOrderExamineMationBox").append(getDataUseHandlebars($("#erpOrderExamineMationTemplate").html(), {rows: erpExamineBasicDesign}));
				$.each(erpExamineBasicDesign, function (i, item) {
					if(item.examineSwitch){
						$("input:radio[name='" + item.code + "'][value='1']").attr("checked", true);
					} else {
						$("input:radio[name='" + item.code + "'][value='2']").attr("checked", true);
					}
				});
			}
		}

		function loadEvent() {
			form.on('select(abnormal)', function (data) {
				var id = data.elem.id;
				var value = data.value;
				var number = id.replace("abnormal", "");
				if (isNull(value)) {
					$("#abnormalMoney" + number).hide();
					$("#abnormalMoneyLabel" + number).hide();
				} else if (value === "1") {
					$("#abnormalMoney" + number).show();
					$("#abnormalMoneyLabel" + number).hide();
				} else if (value === "2") {
					$("#abnormalMoney" + number).hide();
					$("#abnormalMoneyLabel" + number).show();
				}
			});
		}

		function loadSubmit() {
			matchingLanguage();
			form.render();
			form.on('submit(formEditBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					var params = {
						emailType: $("#emailType").val(),
						emailReceiptServer: $("#emailReceiptServer").val(),
						emailReceiptServerPort: $("#emailReceiptServerPort").val(),
						emailSendServer: $("#emailSendServer").val(),
						emailSendServerPort: $("#emailSendServerPort").val(),
						noChargeId: $("input[name='noChargeId']:checked").val(),
						noDocumentaryDayNum: $("#noDocumentaryDayNum").val()
					};

					// 获取假期信息
					var tableData = new Array();
					var noError = false; //循环遍历表格数据时，是否有其他错误信息
					$.each($("#holidaysTypeJsonTable tr"), function (i, item) {
						// 获取行编号
						var rowNum = $(item).attr("trcusid").replace("tr", "");
						// 校验名称
						if (inHolidaysTypeJsonDataArray(tableData, $("#holidayName" + rowNum).val(), $("#holidayNo" + rowNum).val())) {
							winui.window.msg('存在相同的假期名称或标识符，请修改.', {icon: 2, time: 2000});
							noError = true;
							return false;
						}
						if($("#whetherYearHour" + rowNum).val() == 1 && $("#whetherComLeave" + rowNum).val() == 1){
							winui.window.msg('补休和年假不能同时扣除，请修改.', {icon: 2, time: 2000});
							noError = true;
							return false;
						}
						var row = {
							holidayName: $("#holidayName" + rowNum).val(),
							holidayNo: $("#holidayNo" + rowNum).val(),
							offPercentageMoney: $("#offPercentageMoney" + rowNum).val(),
							whetherYearHour: $("#whetherYearHour" + rowNum).val(),
							whetherComLeave: $("#whetherComLeave" + rowNum).val(),
							remark: $("#remark" + rowNum).val()
						};
						tableData.push(row);
					});
					if (noError) {
						return false;
					}
					params.holidaysTypeJson = JSON.stringify(tableData);

					// 获取年假信息
					tableData = new Array();
					$.each($("#yearHolidaysMationTable tr"), function (i, item) {
						// 获取行编号
						var rowNum = $(item).attr("trcusid").replace("tr", "");
						// 校验工作年限
						if (inYearHolidaysMationDataArray(tableData, $("#yearType" + rowNum).val())) {
							winui.window.msg('存在相同的年假类型，请修改.', {icon: 2, time: 2000});
							noError = true;
							return false;
						}
						var row = {
							yearType: $("#yearType" + rowNum).val(),
							yearHour: $("#yearHour" + rowNum).val(),
							remark: $("#yearRemark" + rowNum).val()
						};
						tableData.push(row);
					});
					if (noError) {
						return false;
					}
					params.yearHolidaysMation = JSON.stringify(tableData);

					// 获取异常考勤制度信息
					params.abnormalMation = getAbnormalMation();
					// 获取订单审核标识信息
					params.erpExamineBasicDesign = getErpExamineBasicDesign();

					AjaxPostUtil.request({url: reqBasePath + "sysfdsettings002", params: params, type: 'json', method: "PUT", callback: function (json) {
						if (json.returnCode == 0) {
							winui.window.msg("更改成功！", {icon: 1, time: 2000});
						} else {
							winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
						}
					}});
				}
				return false;
			});
		}

		/**
		 * 获取异常考勤制度信息
		 *
		 * @returns {string}
		 */
		function getAbnormalMation(){
			var tableData = new Array();
			$.each($("#abnormalAttendanceTable tr"), function (i, item) {
				// 获取行编号
				var rowNum = $(item).attr("trcusid").replace("abnormalTr", "");
				var row = {
					abnormalType: $("#abnormalType" + rowNum).attr("typeName"),
					abnormal: $("#abnormal" + rowNum).val(),
					abnormalMoney: $("#abnormal" + rowNum).val() === "1" ? getAbnormalMoney(rowNum) : "",
					remark: $("#abnormalRemark" + rowNum).val()
				};
				tableData.push(row);
			});
			return JSON.stringify(tableData);
		}

		/**
		 * 获取订单审核标识信息
		 *
		 * @returns {any[]}
		 */
		function getErpExamineBasicDesign(){
			var tableData = new Array();
			$.each($("#erpOrderExamineMationBox .examineItem"), function (i, item) {
				var row = {
					title: $(item).attr("title"),
					code: $(item).attr("code"),
					examineSwitch: $("input[name='" + $(item).attr("code") + "']:checked").val() == 1 ? true : false
				};
				tableData.push(row);
			});
			return JSON.stringify(tableData);
		}

	    function getAbnormalMoney(rowNum){
	    	if(isNull($("#abnormalMoney" + rowNum).val())){
	    		return "0";
			}
	    	return $("#abnormalMoney" + rowNum).val();
		}

		function inYearHolidaysMationDataArray(array, yearType) {
			var isIn = false;
			$.each(array, function(i, item) {
				if(item.yearType === yearType) {
					isIn = true;
					return false;
				}
			});
			return isIn;
		}

		function inHolidaysTypeJsonDataArray(array, holidayName, holidayNo) {
			var isIn = false;
			$.each(array, function(i, item) {
				if(item.holidayName === holidayName || item.holidayNo === holidayNo) {
					isIn = true;
					return false;
				}
			});
			return isIn;
		}
	    
        // 同步solr数据
        $("body").on("click", "#formAddBean", function (e) {
            winui.window.msg("数据同步中...", {icon: 6,time: 2000});
            AjaxPostUtil.request({url: reqBasePath + "forumcontent020", params: {}, type: 'json', callback: function (json) {
                if(json.returnCode == 0) {
                    winui.window.msg("数据同步成功", {icon: 1, time: 2000});
                    if(!isNull(json.bean.synchronousTime)){
                        $("#synchronousTime").text(json.bean.synchronousTime);
                    }
                }else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });

        /****************************假期设置开始**************************/
		// 新增行
		$("body").on("click", "#addHolidaysTypeJsonRow", function() {
			addHolidaysTypeJsonRow();
		});

		// 删除行
		$("body").on("click", "#deleteHolidaysTypeJsonRow", function() {
			deleteRow("holidaysTypeJsonTable");
		});

		// 新增行
		function addHolidaysTypeJsonRow() {
			var par = {
				id: "row" + rowNum.toString(), //checkbox的id
				trId: "tr" + rowNum.toString(), //行的id
				holidayName: "holidayName" + rowNum.toString(), //假期名称id
				holidayNo: "holidayNo" + rowNum.toString(), //假期标识符id
				offPercentageMoney: "offPercentageMoney" + rowNum.toString(), //薪资扣除率id
				whetherYearHour: "whetherYearHour" + rowNum.toString(), //是否使用年假id
				whetherComLeave: "whetherComLeave" + rowNum.toString(), //是否使用补休id
				remark: "remark" + rowNum.toString() //备注id
			};
			$("#holidaysTypeJsonTable").append(getDataUseHandlebars(holidaysTypeJsonTemplate, par));
			form.render();
			rowNum++;
		}
		/****************************假期设置结束**************************/

		/****************************年假设置开始**************************/
		// 新增行
		$("body").on("click", "#addYearHolidaysMationRow", function() {
			addYearHolidaysMationRow();
		});

		// 删除行
		$("body").on("click", "#deleteYearHolidaysMationRow", function() {
			deleteRow("yearHolidaysMationTable");
		});

		// 新增行
		function addYearHolidaysMationRow() {
			var par = {
				id: "row" + rowNum.toString(), //checkbox的id
				trId: "tr" + rowNum.toString(), //行的id
				yearType: "yearType" + rowNum.toString(), //年假类型id
				yearHour: "yearHour" + rowNum.toString(), //年假小时数id
				yearRemark: "yearRemark" + rowNum.toString() //备注id
			};
			$("#yearHolidaysMationTable").append(getDataUseHandlebars(yearHolidaysMationTemplate, par));
			form.render();
			rowNum++;
		}
		/****************************年假设置结束**************************/
		// 删除行
		function deleteRow(tableId) {
			var checkRow = $("#" + tableId + " input[type='checkbox'][name='tableCheckRow']:checked");
			if(checkRow.length > 0) {
				$.each(checkRow, function(i, item) {
					//移除界面上的信息
					$(item).parent().parent().remove();
				});
			} else {
				winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
			}
		}

	});
});