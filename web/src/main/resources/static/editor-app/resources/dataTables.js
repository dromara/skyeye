/**
 * 通用表格组件，基于jquery-DataTable组件的扩展
 *
 * @param tableId
 *            table组件id
 * @param queryId
 *            query查询id
 * @param searchDiv
 *            查询条件div的id
 * @author doc_wei qq:598748873
 *
 */
(function($, window, document, undefined) {
	//'use strict';

	window.Table = function(config) {
		return new CommonTable(config.tableId, config.queryId, config.searchDiv, config.config);
	}

	//window.CommonTable=window.CommonTable;
	window.CommonTable = function(tableId, queryId, searchDiv, config) {
		this.tableId = tableId;
		this.queryId = queryId;
		this.searchDiv = searchDiv;
		this.data = null;
		this.loaded = false;
		this.config = config;
		this.serverCallback = null;

		// 用作缓存一些数据
		var dataCache = $("#dataCache" + tableId);
		if(dataCache.length == 0) {
			dataCache = $("<div></div>");
			dataCache.attr("id", "dataCache" + tableId);
			$(document.body).append(dataCache);
		}
		this.dataCache = dataCache;
		this.dataCache.data("queryId", this.queryId);
		//绑定查询事件
		var searchButton = $("#" + searchDiv + " button[data-btn-type='search']");
		this.searchButton = searchButton;
		var resetButton = $("#" + searchDiv + " button[data-btn-type='reset']");
		this.resetButton = resetButton;
		//用户自定义列
		var customButton = $("#" + searchDiv + " button[data-btn-type='custom']");
		this.customButton = customButton;
		//通用导出
		var exportButton = $("#" + searchDiv + " button[data-btn-type='export']");
		this.exportButton = exportButton;
		//模板导出
		var exportByTempBtn = $("#" + searchDiv + " button[data-btn-type='export-template']");
		this.exportByTempBtn = exportByTempBtn;
		// 表格横向自适应
		$("#" + this.tableId).css("width", "100%");
		// 初始化表格
		this.initTable(tableId, queryId, searchDiv);

	}

	/**
	 * 初始化表格
	 */
	CommonTable.prototype.initTable = function(tableId, queryId, searchDiv) {
		this.data = this.getServerData(null, tableId);
		if(this.data == null) return;
		this.dataCache.data("data", this.data);
		var that = this;
		var columns = [];
		var hiddenCols = this.getCustomHiddenColumns(queryId, this.customButton.data('pagename'));
		for(var i = 0; i < this.data.query.columnList.length; i++) {
			var column = this.data.query.columnList[i];
			var obj = {};
			obj["data"] = column.key;
			obj["title"] = column.header;
			obj["name"] = column.id || column.key;
			obj["visible"] = !column.hidden;
			obj["sortable"] = column.allowSort;
			obj["class"] = "text-" + column.align;
			if(column.fnRender) {
				var fnRender = null;
				try {
					fnRender = eval(column.fnRender)
				} catch(e) {
					if(e instanceof ReferenceError) {
						modals.error(column.fnRender + " 未定义！");
					}
				}
				obj["mRender"] = fnRender;
			}
			//判断是否通过自定义隐藏
			if(hiddenCols && hiddenCols.length > 0) {
				for(var j = 0; j < hiddenCols.length; j++) {
					if((column.id || column.key) == hiddenCols[j])
						obj["visible"] = false;
				}
			}
			columns.push(obj);
		}
		var allowPaging = this.data.query.allowPaging;
		var rowId = this.data.query.key;
		this.table = $('#' + tableId).DataTable($.extend({
			"paging": allowPaging, // 分页
			"lengthChange": allowPaging, // 每页记录数可选项
			"lengthMenu": [
				[10, 20, 50, -1],
				[10, 20, 50, "全部"]
			],
			"searching": false, // 过滤
			"ordering": true, // 排序
			"rowId": rowId,
			"info": allowPaging, // 分页明细
			"autoWidth": false,
			//"stateSave" : true,// 这样就可以在删除返回时，保留在同一页上
			"dom": 'rt<"row"<"col-sm-5"il><"col-sm-7"p>>',
			"processing": true, // 是否显示取数据时的那个等待提示
			"pagingType": "full_numbers", // 分页样式
			"language": { // 中文支持
				"sUrl": basePath + "/static/editor-app/resources/zh_CN.json"
			},
			"displayLength": that.data.pageInfo.pageSize, // 每页记录条数，默认为10
			"serverSide": true,
			"ajaxDataProp": "data",
			"ajaxSource": reqBasePath + "activitimode011",
			"fnServerData": $.proxy(that.fillDataTable, that),
			"fnInitComplete": $.proxy(that.fnInitComplete, that),
			"singleSelect": true, //单选
			"aoColumns": columns

		}, that.config));

		if(this.searchButton) {
			this.searchButton.click(function() {
				that.table.page('first').draw(false);
				// 执行查询的回调函数
				if(that.searchButton.data("callback")) {
					eval(that.searchButton.data("callback"));
				}
			});
		}

		//用户自定义列
		if(this.customButton) {
			this.customButton.click(function() {
				var pagename = that.customButton.data("pagename");
				if(!pagename) {
					modals.info("该按钮未定义data-pagename属性，请先定义");
					return;
				}
				modals.openWin({
					winId: 'customWin',
					title: '【' + that.data.query.tableName + '】自定义列',
					width: '400px',
					url: basePath + "/query/tableConfig?queryId=" + that.data.query.id + "&pageName=" + pagename,
					hideFunc: function() {
						that.setVisible();
					}
				});
			})
		}

		if(this.resetButton) {
			this.resetButton.click(function() {
				//清除查询条件
				that.clearSearchDiv(that.searchDiv);
				//清除排序、分页、重置初始长度
				that.table.order([]).page.len(10).draw();
				if(that.resetButton.data("callback")) {
					eval(that.resetButton.data("callback"));
				}
			});
		}
		//通用导出
		if(this.exportButton) {
			this.exportButton.click(function() {
				if(that.exportButton.data("beforecall")) {
					eval(that.exportButton.data("beforecall"));
				}
				var excelConfig = {
					sheetName: that.exportButton.data("sheetName"),
					sheetTitle: that.exportButton.data("sheetTitle"),
					sheetSubTitle: that.exportButton.data("sheetSubTitle"),
					sheetMethod: that.exportButton.data("sheetMethod")
				};
				if(!that.exportButton.data("tableName")) {
					modals.info("该按钮未定义data-tableName属性，请先定义");
					return;
				}
				that.exportData(that.exportButton.data("tableName"), excelConfig);
			})
		}
		//模板导出
		if(this.exportByTempBtn) {
			this.exportByTempBtn.click(function() {
				if(that.exportByTempBtn.data("beforecall")) {
					eval(that.exportByTempBtn.data("beforecall"));
				}
				var excelConfig = {
					sheetName: that.exportButton.data("sheetName"),
					sheetTitle: that.exportButton.data("sheetTitle"),
					sheetSubTitle: that.exportButton.data("sheetSubTitle"),
					sheetMethod: that.exportButton.data("sheetMethod")
				};
				if(!that.exportByTempBtn.data("tableName") || !that.exportByTempBtn.data("template")) {
					modals.info("该按钮需要定义data-tableName和data-template属性，请先定义");
					return;
				}
				that.exportByTemplate(that.exportByTempBtn.data("template"), that.exportByTempBtn.data("tableName"), excelConfig);
			})
		}
	}

	//自定义单元格的可见性
	CommonTable.prototype.setVisible = function() {
		var hiddenCols = this.getCustomHiddenColumns(queryId, this.customButton.data('pagename'));
		if(!hiddenCols)
			return;
		var dataArr = this.table.columns().dataSrc();
		var self = this;
		$.each(dataArr, function(index, columnName) {
			var column = self.data.query.columnList[index];
			if(column.hidden) {
				self.table.column(index).visible(false, false);
			} else {
				self.table.column(index).visible(true, false);
			}
			$.each(hiddenCols, function(hindex, hcolName) {
				if((column.id || column.key) == hcolName)
					self.table.column(index).visible(false, false);
			})
		})
		this.table.columns.adjust().draw(false);
	}

	//根据名称获取ColumnIndex
	CommonTable.prototype.getColumnIndexByName = function(name) {
		var dataArr = this.table.columns().dataSrc();
		var colIndex = null;
		for(var i = 0; i < dataArr.length; i++) {
			if(dataArr[i] == name) {
				colIndex = i;
				break;
			} else {
				var column = this.data.query.columnList[i];
				if(column.id == name) {
					colIndex = i;
					break;
				}
			}
		}
		return colIndex;
	}

	CommonTable.prototype.clearSearchDiv = function(selector) {
		var sel = $(selector).length > 0 ? $(selector) : $("#" + this.searchDiv);
		sel.find(':input[name]:not(:radio):not([data-noreset])').val('');
		sel.find(':radio').attr('checked', false);
		sel.find(':radio[data-flag]').iCheck('update');
		sel.find(':checkbox').attr('checked', false);
		sel.find(':checkbox[data-flag]').iCheck('update');
		sel.find('select:not(.select2)').val("");
		sel.find("select.select2").select2().val("").trigger("change");
	}

	// 表格初始化后移动查询组件位置 oSettings=配置；json=数据记录；
	CommonTable.prototype.fnInitComplete = function(oSettings, json) {
		// 移动查询框的位置 与记录/页同行
		var _this = this;
		// 列头文本居中
		$("#" + this.tableId + " thead tr th").removeClass("text-left").removeClass("text-right").addClass("text-center");

		//行单选
		if(oSettings.oInit.singleSelect == true) {
			$('#' + this.tableId + ' tbody').on('click', 'tr', function() {
				if(!$(this).hasClass('selected')) {
					_this.table.$('tr.selected').removeClass('selected');
					$(this).addClass('selected');

					if(oSettings.oInit.rowClick) {
						oSettings.oInit.rowClick.call(this, _this.getSelectedRowData(), $(this).hasClass('selected'));
					}
				}
			});
		} else if(oSettings.oInit.singleSelect == false) {
			$('#' + this.tableId + ' tbody').on('click', 'tr', function() {
				$(this).toggleClass('selected');
			})
		}

		if(oSettings.oInit.loadComplete) {
			oSettings.oInit.loadComplete.call(this);
		}

		//如果分页不可选 则空出位置 让条件区域更宽
		if(!oSettings.oInit.lengthChange) {
			$("#" + this.tableId + "_wrapper div.row").eq(0).find("div.col-sm-3").remove();
			$("#" + this.tableId + "_wrapper div.row").eq(0).find("div.col-sm-9").removeClass("col-sm-9").addClass("col-sm-12");
		}

		//Y轴滚动时，设置列头自适应
		if(oSettings.oInit.scrollY) {
			setTimeout(function() {
				_this.table.columns.adjust();
			}, 200);
		}
	}

	CommonTable.prototype.fixHeaderWidth = function() {
		var _this = this;
		var width = $("#" + this.tableId).find("tbody tr:first").width();
		if(width > 0) {
			$("#" + this.tableId).find("tbody tr:first td").each(function(index, item) {
				var thwidth = $("#" + _this.tableId).find("thead tr:first th").eq(index).css('width')
				$("#" + _this.tableId).find("thead tr:first th").eq(index).css("width", $(item).width());
				$("#" + _this.tableId + "_wrapper div.dataTables_scrollHeadInner table").css("width", width).parent().css("width", width);
				$("#" + _this.tableId + "_wrapper div.dataTables_scrollHeadInner table").find("thead tr:first th").eq(index).css("width", $(item).width());
			})
		} else {
			this.fixHeaderWidth();
		}

	}

	CommonTable.prototype.getSelectedRowId = function() {
		if(this.table.row('.selected').length > 0)
			return this.table.row('.selected').id();
		return null;
	}
	/**
	 * 获取当前选中行的数据 单选
	 */
	CommonTable.prototype.getSelectedRowData = function() {
		if(this.table.row('.selected').length > 0)
			return this.table.row('.selected').data();
		return null;
	}

	CommonTable.prototype.getRowDataByRowId = function(id) {
		if(this.table.row("#" + id).length > 0)
			return this.table.row("#" + id).data();
		return null;
	}
	/**
	 * 获取当前选中行的数据 多选
	 */
	CommonTable.prototype.getSelectedRowsData = function() {
		var datas = null;
		var rows = this.table.rows('.selected').data();
		if(rows.length == 0)
			return datas;
		datas = [];
		for(var i = 0; i < rows.length; i++) {
			datas.push(rows[i]);
		}
		return datas;
	}

	//新增，刷新界面
	CommonTable.prototype.reloadData = function() {
		this.table.page('first').draw(false);
	}

	//刷新当前页面，并定位到行
	CommonTable.prototype.reloadRowData = function(rowId) {
		var dataCache = $("#dataCache" + this.tableId);
		var pageInfo = dataCache.data("pageInfo");
		var pageIndex = pageInfo == null ? "first" : pageInfo.pageNum - 1;
		this.table.page(pageIndex).draw(false);
		if(rowId) { //定位选中到当前行
			this.selectRow(rowId);
		}
	}

	//选中行
	CommonTable.prototype.selectRow = function(rowId, triggerEvent) {
		if(rowId) {
			this.selectRowWithSelector("#" + rowId, triggerEvent)
		}
	}

	//选中第一行
	CommonTable.prototype.selectFirstRow = function(triggerEvent) {
		this.selectRowWithSelector("tr:first", triggerEvent);
	}

	//通用选择
	CommonTable.prototype.selectRowWithSelector = function(selector, triggerEvent) {
		if(selector) {
			if(triggerEvent) {
				this.table.$(selector).click();
			} else {
				this.table.$('tr.selected').removeClass('selected');
				this.table.$(selector).addClass('selected');
			}
		}
	}

	/**
	 * 清除行选中
	 */
	CommonTable.prototype.clearSelection = function() {
		this.table.row('.selected').remove().draw(false);
	}

	// 获取查询框中的查询数据
	// isCondition默认为true;likeOption默认false 即不拼接%
	CommonTable.prototype.fnGetConditions = function(searchDiv) {
		var searchDiv = $("#" + searchDiv);
		var conditions = [];
		if(searchDiv !== null && searchDiv.length > 0) {
			var ele = searchDiv.find(':input[name]');
			ele.each(function(i) {
				if($(this).attr("readonly") == "readonly" || $(this).attr("disabled") == "disabled")
					return;
				var map = {};
				var key = $(this).attr("name");
				// alert("key:"+key+" id:"+$(this).attr("id"));
				var isExist = false;
				for(var j = 0; j < conditions.length; j++) {
					if(key == conditions[j].key) {
						isExist = true;
						map = conditions[j];
						break;
					}
				}
				var value = $(this).val();
				var type = $(this).attr("type");

				var likeOption = $(this).attr("likeOption"); // 是否进行模糊查询，默认值为false
				var isCondition = $(this).attr("isCondition"); // 在前台条件div中是否作为1=1条件的一部分
				var operator = $(this).attr("operator"); // 操作符
				if(!isCondition)
					isCondition = "true";
				if(!likeOption)
					likeOption = "false";
				if(!operator)
					operator = "";
				if((type && (type.toLowerCase() == 'checkbox' || type.toLowerCase() == 'radio'))) {
					if($(this).attr("checked") != "checked") {
						value = "";
					}
				}
				if((type && (type == 'select-one'))) {
					if(!value) {
						value = "";
					}
				}
				// alert(key+" "+type+" "+likeOption);
				if((type && (type == "text" || type == "search") && likeOption == "true")) { // 如果是用户手动输入项，需要在前后各加一个百分号
					if(value && value != "") {
						value = "%" + value + "%";
					}
				}
				if(!type && likeOption == "true" && value) {
					value = "%" + value + "%";
				}
				if(isExist) {
					map.value += "," + value;
				} else {
					map.key = key;
					map.value = value;
					map.isCondition = isCondition;
					map.operator = operator;
					conditions.push(map);
					// alert("key:"+key+" value:"+value);
				}
			});
		} else {
			// no search conditions found.
		}
		//console.log("conditons:"+JSON.stringify(conditions));
		return conditions;
	}

	/**
	 * 获取服务器中的数据
	 *
	 * @param pageInfo
	 *            分页信息
	 * @param tableId
	 *            table的ID
	 */
	CommonTable.prototype.getServerData = function(pageInfo, tableId) {
		var dataCache = $("#dataCache" + tableId);
		var reqParam = {
			queryId: dataCache.data("queryId"),
			pageName: this.customButton.data("pagename"),
			pageInfo: pageInfo,
			query: null,
			sortInfo: dataCache.data("sortInfo"),
			conditions: this.fnGetConditions(this.searchDiv)
		};
		dataCache.data("pageInfo", pageInfo);
		var retData = null;
		//注释以上部分，统一用ajaxPost处理，以便处理session超时（ajax请求超时）
		ajaxPost(reqBasePath + "activitimode011", {
			"reqObj": this.toJSONString(reqParam)
		}, function(result, status) {
			retData = result;
		});
		if(retData == null) return null;
		var start = 0;
		if(pageInfo) {
			start = pageInfo.pageSize * (pageInfo.pageNum - 1)
		}
		var columns = retData.query.columnList;
		//通过 numberFormat render endableTooltip改变单元格的值
		//遍历 先列 后行
		for(var j = 0; j < columns.length; j++) {
			var column = columns[j];
			for(var i = 0; i < retData.rows.length; i++) {
				retData.rows[i]["rowIndex"] = start + i + 1;
				//获取关联对象的值如message.sendSubject
				var value_str = "retData.rows[i]." + column.key;
				var value = eval(value_str);
				// 格式化日期
				if(column.dateFormat) {
					eval(value_str + "=formatDate(value, column.dateFormat)");
					value = eval(value_str);
				}
				// 格式化数字
				if(column.numberFormat) {
				}
				//扩展enableTooltip
				if(column.enableTooltip) {
					//重构为支持关联对象方式
					var title = eval("retData.rows[i]." + (column.tooltip || column.key));
					var maxLen = parseInt(column.maxLen || 20);
					if(value && value.length > maxLen) {
						value = value.substring(0, maxLen) + "...";
						eval(value_str + "=\"<span data-toggle='tooltip' data-placement='right' data-html='true'  title='\" + title + \"'>\" + value + \"</span>\"");
						value = eval(value_str);
					}
				}
				// 格式化render
				if(column.render) {
					//替换通过[column.key]的参数  用于替换动态参数
					var columnRender = this.getRenderValueByMatcher(column.render, retData.rows[i]);
					var obj = this.getRenderObject(columnRender);
					if(value != null) {
						if(obj.type == "eq") {
							//替换值 render="type=eq,0=临时保存,1=提交" type=eq可缺省
							eval(value_str + "=obj[" + value_str + "]");
							value = eval(value_str);
						} else if(obj.type == "window") {
							//弹出模态窗体 render="type=window,url=/message/show?id=[id],title=查看[name],width=900
							if(!obj.winId || !obj.url) {
								modals.warn("render配置中type=window缺少url和winId参数");
								return false;
							}
							obj.url = basePath + obj.url;
							eval(value_str + "=\"<a href='#' onclick='javascript:modals.openWin(\" + JSON.stringify(obj) + \")'>\" + retData.rows[i][column.key] + \"</a>\"");
							value = eval(value_str);
						} else if(obj.type == "link") {
							//超链接，自定义方法使用，在界面要定义该方法
							var invoke_str = "";
							if(obj.params) {
								var params = obj.params.replace(/;/g, ",");
								invoke_str = obj.method + "(" + params + ")";
							} else {
								if(retData.rows[i][retData.query.key]) {
									var value_key = eval("retData.rows[i]." + retData.query.key);
									invoke_str = obj.method + "('" + value_key + "')";
								} else {
									modals.error("render配置获取" + retData.query.key + "出错，请检查query的key配置");
									return false;
								}
							}
							eval(value_str + "=\"<a href='#' onclick=\" +invoke_str+ \">\" + value + \"</a>\"");
							value = eval(value_str);
						}
					}
				}
			}
		}
		return retData;
	}

	/**
	 * 通用导出，默认导出单个sheet页
	 * @param tableName 表名
	 * @param sheetConfig 单个sheet的配置
	 */
	CommonTable.prototype.exportData = function(tableName, sheetConfig) {
		var dataCache = $("#dataCache" + this.tableId);
		var reqParam = {
			queryId: dataCache.data("queryId"),
			pageName: this.customButton.data("pagename"),
			sortInfo: dataCache.data("sortInfo"),
			conditions: this.fnGetConditions(this.searchDiv),
			sheetName: sheetConfig.sheetName,
			sheetTitle: sheetConfig.sheetTitle,
			sheetSubTitle: sheetConfig.sheetSubTitle,
			sheetMethod: sheetConfig.sheetMethod
		};
		var queryConditions = [];
		queryConditions.push(reqParam);
		this.exportMultiData(tableName, queryConditions);
	}

	/**
	 * 基于模板的导出 jxls
	 * @param template 模板名称
	 * @param tableName 表名
	 * @param sheetConfig 单个sheet的配置
	 */
	CommonTable.prototype.exportByTemplate = function(template, tableName, sheetConfig) {
		var dataCache = $("#dataCache" + this.tableId);
		var reqParam = {
			queryId: dataCache.data("queryId"),
			pageName: this.customButton.data("pagename"),
			sortInfo: dataCache.data("sortInfo"),
			conditions: this.fnGetConditions(this.searchDiv),
			sheetName: sheetConfig.sheetName,
			sheetTitle: sheetConfig.sheetTitle,
			sheetSubTitle: sheetConfig.sheetSubTitle,
			sheetMethod: sheetConfig.sheetMethod
		};
		ajaxPost(basePath + "/query/exportDataByTemplate", {
			reqParam: JSON.stringify(reqParam),
			tableName: tableName,
			template: template
		}, function(data) {
			if(data.success) {
				window.location.href = basePath + '/query/downExport?tempfile=' + data.data + "&tableName=" + encodeURIComponent(tableName);
			} else {
				modals.error(data.message);
			}
		})

	}

	//多个sheet页面导出
	CommonTable.prototype.exportMultiData = function(tableName, queryConditions) {
		ajaxPost(basePath + "/query/exportData", {
			reqObjs: JSON.stringify(queryConditions),
			tableName: tableName
		}, function(data) {
			if(data.success) {
				window.location.href = basePath + '/query/downExport?tempfile=' + data.data + "&tableName=" + encodeURIComponent(tableName);
			}
		})
	}

	//获取用户自定义的隐藏列
	CommonTable.prototype.getCustomHiddenColumns = function(queryId, pageName) {
		var retData = null;
		if(!pageName)
			return retData;
		ajaxPost(basePath + "/query/getSelectedColumns", {
			queryId: queryId,
			pageName: pageName
		}, function(hideCols) {
			retData = hideCols;
		});
		return retData;
	}

	//匹配出[name]类似的字符串，并替换真实的值
	CommonTable.prototype.getRenderValueByMatcher = function(render, rowObj) {
		var pattern = /\[[^\]]+\]/g;
		var names = render.match(pattern); //["[id]","[name]"]的数组
		if(!names || names.length == 0) {
			return render;
		}
		for(var i = 0; i < names.length; i++) {
			var columnName = names[i].replace("[", "").replace("]", "");
			var value = eval("rowObj." + columnName);
			if(!value) {
				modals.error("render配置错误，列" + columnName + "不存在");
				return render;
			}
			//后面的replace防止文本中有html字符
			render = render.replace(names[i], value.replace(/<[^>]+>/g, ""));
		}
		return render;
	}

	/**
	 * 获取render,并转化为对象数组
	 */
	CommonTable.prototype.getRenderObject = function(render) {
		var arr = render.split(",");
		var obj = new Object();
		for(var i = 0; i < arr.length; i++) {
			var strA = arr[i].split("=");
			obj[strA[0]] = arr[i].substring(arr[i].indexOf("=") + 1);
		}
		if(!obj.type)
			obj.type = "eq";
		return obj;
	}

	/**
	 * 换页、排序、查询按钮调用此方法
	 *
	 * @param sSource
	 *            服务器请求方法
	 * @param aoData
	 *            基本信息
	 * @param fnCallback
	 *            重绘dataTable的回调函数
	 * @param oSettings
	 *            dataTable全局配置
	 */
	CommonTable.prototype.fillDataTable = function(sSource, aoData, fnCallback, oSettings) {
		var result = this.data;
		var map = oSettings.oAjaxData;
		var dataCache = $("#dataCache" + oSettings.sTableId);
		if(this.loaded) { // 换页
			var pageInfo = {};
			pageInfo.pageSize = map.iDisplayLength;
			pageInfo.pageNum = map.iDisplayStart % map.iDisplayLength == 0 ? map.iDisplayStart / map.iDisplayLength + 1 :
				map.iDisplayStart / map.iDisplayLength;
			// 构造排序
			var columnNames = map.sColumns.split(',');
			var sortArr = [];
			for(var i = 0; i < map.iSortingCols; i++) {
				if(map["iSortCol_" + i] != 0) // 过滤掉rowIndex的排序
					sortArr.push(columnNames[map["iSortCol_" + i]] + " " + map["sSortDir_" + i]);
			}
			dataCache.data("sortInfo", sortArr.join());
			result = this.getServerData(pageInfo, oSettings.sTableId);
			this.data = result;

		} else { // 首次加载
			result = this.data;
			this.loaded = true;
		}
		var obj = {};
		obj['data'] = result.rows;
		obj["iTotalRecords"] = result.pageInfo.count;
		obj["iTotalDisplayRecords"] = result.pageInfo.count;
		fnCallback(obj);
		//序号排序
		$("table.table thead tr").each(function() {
			$(this).find("th").eq(0).removeClass("sorting_asc").addClass("sorting_disabled");
		});
		//加载完成以后做一些其他处理
		if(this.serverCallback) {
			this.serverCallback.call(this, oSettings);
		}

	}

	CommonTable.prototype.toJSONString = function(value) {
		var _array_tojson = Array.prototype.toJSON;
		delete Array.prototype.toJSON;
		var r = JSON.stringify(value);
		Array.prototype.toJSON = _array_tojson;
		return r;
	}

})(jQuery, window, document);