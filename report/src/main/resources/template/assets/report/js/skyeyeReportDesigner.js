var form;

// 已经添加上的echarts图表
var inPageEcharts = {};
var inPageEchartsObject = {};

// 已经添加上的文字模型
var inPageWordMation = {};

// 支持的编辑器类型
var editorType = {};
layui.define(["jquery", 'form', 'element'], function(exports) {
	var jQuery = layui.jquery;
	form = layui.form;
	var element = layui.element;
	(function($) {
		$.skyeyeReportDesigner = function(params) {
			var defaults = {
				'rulerColor': "RGB(135, 221, 252)", // 标尺颜色
				'rulerFontColor': "burlywood", // 标尺字体颜色
				'headerBgColor': 'burlywood', // 菜单栏背景颜色
				'initData': {}, // 初始化数据
				'headerMenuJson': [], // 菜单栏
				// excel配置
				excelConfig: {
					def:{
						width: '70px',
						row: 2,
						col: 8
					}
				}
			};
			params = $.extend({}, defaults, params);

			var flag = $("#skyeyeScaleBox").size() === 0 ? true : false;

			// box拖拽的八个点
			var editoptions = {
				left_top: true,
				left: true,
				right: true,
				top: true,
				bottom: true,
				right_top: true,
				left_bottom: true,
				right_bottom: true,
			};

			// 图表自定义属性
			var echartsCustomOptions = {
				"custom.dataBaseMation": { "value": "", "edit": 1, "desc": "数据来源", "title": "数据来源", "editor": "99", "editorChooseValue": "", "typeName": "数据源"},
				"custom.move.x": { "value": "0", "edit": 1, "desc": "鼠标拖动距离左侧的像素", "title": "X坐标", "editor": "98", "editorChooseValue": "", "typeName": "坐标"},
				"custom.move.y": { "value": "0", "edit": 1, "desc": "鼠标拖动距离顶部的像素", "title": "Y坐标", "editor": "98", "editorChooseValue": "", "typeName": "坐标"},
				"custom.box.background": { "value": "rgba(255, 255, 255, 1)", "edit": 1, "desc": "盒子背景", "title": "盒子背景颜色", "editor": "3", "editorChooseValue": "", "typeName": "盒子"},
				"custom.box.border-color": { "value": "rgba(255, 255, 255, 1)", "edit": 1, "desc": "盒子边框", "title": "盒子边框颜色", "editor": "3", "editorChooseValue": "", "typeName": "盒子"}
			};

			var f = {
				box: function() {
					var width = $(window).width();
					var height = $(window).height();
					// 整个box的宽高
					x.height(height).width(width);
				},

				ui: function() {
					rh.html("");
					rv.html("");
					// 创建标尺数值
					for(var i = 0; i < $(window).width(); i += 1) {
						if(i % 50 === 0) {
							$('<span class="n">' + i + '</span>').css("left", i + 2).appendTo(rh);
						}
					}
					// 垂直标尺数值
					for(var i = 0; i < $(window).height(); i += 1) {
						if(i % 50 === 0) {
							$('<span class="n">' + i + '</span>').css("top", i + 2).appendTo(rv);
						}
					}
					rh.css({
						'background': 'url(../../assets/report/images/ruler_h.png), linear-gradient(' + params.rulerColor + ', ' + params.rulerColor + ')'
					});
					rv.css({
						'background': 'url(../../assets/report/images/ruler_v.png), linear-gradient(' + params.rulerColor + ', ' + params.rulerColor + ')'
					});
					// 标尺字体颜色
					$(".n").css("color", params.rulerFontColor);
				},

				// ie6的支持
				ie6: function() {
					if(!window.XMLHttpRequest) {
						$(window).scroll(function() {
							var t = $(document).scrollTop();
							x.css("top", t);
						});
						if(flag) {
							$(window).trigger("scroll");
						}
					}
				},

				// 判断是否为空
				isNull: function(str){
					if(str == null || str == ""){
						return true;
					}
					return false;
				},

				// 加载菜单栏
				loadHeader: function(){
					var str = "";
					$.each(params.headerMenuJson, function(i, item) {
						var customClass = f.isNull(item.class) ? "" : item.class;
						var customId = f.isNull(item.id) ? "" : "id='" + item.id + "'";
						if(f.isNull(item.children)){
							str += '<a class="def-nav ' + customClass + '" href="javascript:;" ' + customId + '>' +
								'<i class="icon ' + item.icon + '"></i>' +
								'<span>' + item.title + '</span>' +
								'</a>';
						} else {
							if(item.id == 'echartsModel'){
								str = f.getEchartsHtml(item, str);
							} else if(item.id == 'bgImages'){
								str = f.getBgImageHtml(item, str);
							} else if(item.id == 'wordModel'){
								str = f.getWordModelHtml(item, str);
							} else{
								str = f.getEchartsHtml(item, str);
							}
						}
						str += '<span class="separate"></span>';
					});
					skyeyeHeader.find(".navs").html(str);
					skyeyeHeader.css({
						"background-color": params.headerBgColor
					});
					skyeyeHeader.find(".separate").css({
						"background-color": params.headerBgColor
					});
					$(".def-nav").hover(function (e) {
						$(this).find(".pulldown-nav").addClass("hover");
						$(this).find(".pulldown-nav").find(".f-icon").removeClass("arrow-bottom");
						$(this).find(".pulldown-nav").find(".f-icon").addClass("arrow-top");
						$(this).find(".pulldown").show();
					}, function (e) {
						$(this).find(".pulldown").hide();
						$(this).find(".pulldown-nav").removeClass("hover");
						$(this).find(".pulldown-nav").find(".f-icon").addClass("arrow-bottom");
						$(this).find(".pulldown-nav").find(".f-icon").removeClass("arrow-top");
					});
					f.setHeaderMenuClickEvent();
				},

				// 标题按钮--有子按钮时，父按钮公共部分
				getCommonParentHeader: function (str, item) {
					str += '<div class="def-nav has-pulldown-special">' +
						'<a class="pulldown-nav" href="javascript:void(0);">' +
						'<em class="f-icon arrow-bottom"></em>' +
						'<i class="icon' + item.icon + '"></i>' +
						'<span>' + item.title + '</span>' +
						'</a>' +
						'<div class="pulldown app-url">' +
						'<div class="child-menu">';
					return str;
				},

				// 标题按钮--echarts模型获取html
				getEchartsHtml: function(item, str) {
					str = f.getCommonParentHeader(str, item);
					$.each(item.children, function (j, bean) {
						if (f.isNull(bean.icon)) {
							str += '<a class="li disk layui-col-xs3" href="javascript:void(0);" rowId="' + bean.id + '" title="' + bean.title + '">' +
								'<img class="image" src="' + bean.image + '"/>' +
								'<span class="text">' + bean.title + '</span>' +
								'</a>';
						} else {
							str += '<a class="li disk layui-col-xs3" href="javascript:void(0);" rowId="' + bean.id + '" title="' + bean.title + '">' +
								'<i class="icon' + bean.icon + '"></i>' +
								'<span class="text">' + bean.title + '</span>' +
								'</a>';
						}
					});
					str += '</div>' +
						'</div>' +
						'</div>';
					return str;
				},

				// 标题按钮--文字模型获取html
				getWordModelHtml: function(item, str) {
					str = f.getCommonParentHeader(str, item);
					$.each(item.children, function (j, bean) {
						str += '<a class="li wordModle layui-col-xs3" href="javascript:void(0);" rowId="' + bean.id + '" title="' + bean.title + '">' +
							'<img class="image" src="' + bean.logo + '"/>' +
							'<span class="text">' + bean.title + '</span>' +
							'</a>';
					});
					str += '</div>' +
						'</div>' +
						'</div>';
					return str;
				},

				// 标题按钮--背景图片模型获取html
				getBgImageHtml: function(item, str) {
					str = f.getCommonParentHeader(str, item);
					$.each(item.children, function (j, bean) {
						str += '<a class="li bgImage layui-col-xs3" href="javascript:void(0);" rowId="' + bean.id + '" title="' + bean.title + '">' +
							'<img class="image" src="' + bean.imagePath + '"/>' +
							'<span class="text">' + bean.title + '</span>' +
							'</a>';
					});
					str += '</div>' +
						'</div>' +
						'</div>';
					return str;
				},

				// 报表点击事件
				setHeaderMenuClickEvent: function (){

					// echarts模型点击事件
					skyeyeHeader.find(".disk").click(function () {
						var modelId = $(this).attr("rowId");
						var echartsMation = f.getEchartsMationById(modelId);
						f.addNewModel(modelId, echartsMation);
					});

					// 背景图片点击事件
					skyeyeHeader.find(".bgImage").click(function () {
						var bgImageSrc = $(this).find("img").attr("src");
						skyeyeReportContent.css({
							"background-image": "url(" + bgImageSrc + ")",
							"background-size": skyeyeReportContent.width() + "px " + skyeyeReportContent.height() + "px"
						});
					});

					// 文字模型点击事件
					skyeyeHeader.find(".wordModle").click(function () {
						var modelId = $(this).attr("rowId");
						var wordStyleMation = f.getWordStyleMationById(modelId);
						f.addNewWordModel(modelId, wordStyleMation);
					});

				},

				// 加载echarts模型
				addNewModel: function(modelId, echartsMation){
					if(!f.isNull(echartsMation)){
						var option = getEchartsOptions(echartsMation);
						// 获取boxId
						var boxId = modelId + getRandomValueToString();
						// 获取echarts图表id
						var echartsId = f.getEchartsBox(boxId, modelId);
						// 加载图表
						var newChart = echarts.init(document.getElementById(echartsId));
						newChart.setOption(option);
						$("#" + echartsId).resize(function () {
							newChart.resize();
						});
						// 加入页面属性
						echartsMation.attr = $.extend(true, {}, echartsCustomOptions, echartsMation.attr);
						inPageEcharts[boxId] = $.extend(true, {}, echartsMation);
						inPageEchartsObject[boxId] = newChart;
						return boxId;
					}
					return "";
				},

				// 加载文字模型
				addNewWordModel: function(modelId, wordStyleMation){
					var styleStr = getWordStyleStr(wordStyleMation.attr);
					// 获取boxId
					var boxId = modelId + getRandomValueToString();
					// 获取文字模型id
					var wordId = f.getWordBox(boxId, modelId, styleStr, wordStyleMation);
					// 加入页面属性
					wordStyleMation.attr = $.extend(true, {}, echartsCustomOptions, wordStyleMation.attr);
					inPageWordMation[boxId] = $.extend(true, {}, wordStyleMation);
					return boxId;
				},

				// 根据id获取echarts信息
				getEchartsMationById: function(id){
					var echartsMation;
					$.each(params.headerMenuJson, function(i, item) {
						if(!f.isNull(item.children) && item.id == 'echartsModel'){
							echartsMation = getInPoingArr(item.children, "id", id);
							return false;
						}
					});
					return echartsMation;
				},

				// 根据id获取文字模型的style信息
				getWordStyleMationById: function(id){
					var wordMation;
					$.each(params.headerMenuJson, function(i, item) {
						if(!f.isNull(item.children) && item.id == 'wordModel'){
							wordMation = getInPoingArr(item.children, "id", id);
							return false;
						}
					});
					return wordMation;
				},

				getEchartsBox: function (boxId, modelId){
					var echartsId = "echarts" + boxId;
					var echartsBox = document.createElement("div");
					// 为div设置类名
					echartsBox.className = "echarts-box";
					echartsBox.id = echartsId;
					var box = f.createBox(boxId, modelId, null);
					box.appendChild(echartsBox);
					echartsBox.onmousedown = ee => {
						var id = $("#" + echartsId).parent().attr("id");
						f.setMoveEvent(ee, $("#" + id));
						// 阻止事件冒泡（针对父元素的move）
						ee.stopPropagation();
					};
					return echartsId;
				},

				getWordBox: function (boxId, modelId, styleStr, wordStyleMation){
					var wordId = "word" + boxId;
					var wordBox = document.createElement("font");
					// 为div设置类名
					wordBox.className = "word-box";
					wordBox.innerHTML = wordStyleMation["attr"]["custom.textContent"].value;
					wordBox.style = styleStr;
					wordBox.id = wordId;
					var box = f.createBox(boxId, modelId, f.setDesignAttr(wordStyleMation));
					box.appendChild(wordBox);
					wordBox.onmousedown = ee => {
						var id = $("#" + wordId).parent().attr("id");
						f.setMoveEvent(ee, $("#" + id));
						// 阻止事件冒泡（针对父元素的move）
						ee.stopPropagation();
					};
					return wordId;
				},

				setDesignAttr: function(wordStyleMation){
					var otherStyle = {
						width: wordStyleMation.defaultWidth + 'px',
						height: wordStyleMation.defaultHeight + 'px'
					};
					return otherStyle;
				},

				createBox: function(id, modelId, otherStyle){
					f.removeEchartsEditMation();
					// 创建一个div
					var div = document.createElement("div");
					// 为div设置类名
					div.className = "kuang active";
					div.id = id;
					div.dataset.boxId = id;
					div.dataset.modelId = modelId;
					div.style.top = "0px";
					div.style.left = "0px";
					if(!f.isNull(otherStyle)){
						$.each(otherStyle, function (key, value){
							div.style[key] = value;
						});
					}
					skyeyeReportContent[0].appendChild(div);
					// 遍历this.editoptions
					for (let attr in editoptions) {
						if (editoptions[attr]) {
							// 循环创建左，左上，左下，右，右上，右下，上，下方位的小点
							var dian = document.createElement("div");
							dian.className = "dian " + attr;
							dian.dataset.belongId = id;
							// 设置类型为对应的attr
							dian.dataset.type = attr;
							// 将类名为”dian“的div添加到div中
							div.appendChild(dian);
							// 当按下对应方位的小点时
							dian.onmousedown = ee => {
								var ee = ee || window.event;
								// 先获取鼠标距离屏幕的left与top值
								var clientXY = {
									x: ee.clientX,
									y: ee.clientY
								}
								var id = ee.target.dataset.belongId;
								var that = $("#" + id);
								// 获取鼠标按下时ele的宽高
								var eleWH = {
									width: $("#" + id).width(),
									height: $("#" + id).height(),
								}
								// 阻止事件冒泡（针对父元素的move）
								ee.stopPropagation();
								// 通过e.target获取精准事件源对应的type值
								var type = ee.target.dataset.type;
								// 鼠标按下对应方位小点移动时，调用mousemove
								document.onmousemove = function (e) {
									// 查找type中是否包含”right“
									if (type.indexOf('right') > -1) {
										// 如果拖拽后的宽度小于最小宽度，就return出去
										if (50 > eleWH.width + e.clientX - clientXY.x) {
											return;
										}
										// ele拖拽后的宽度为：初始width+拖拽后鼠标距离屏幕的距离 - 第一次按下时鼠标距离屏幕的距离
										that.width(eleWH.width + e.clientX - clientXY.x);
									}
									// 与”right“相同原理
									if (type.indexOf("bottom") > -1) {
										if (35 > eleWH.height + e.clientY - clientXY.y) {
											return;
										}
										that.css({
											height: (eleWH.height + e.clientY - clientXY.y) + "px"
										});
									}

									if (type.indexOf("top") > -1) {
										if (35 > eleWH.height - e.clientY + clientXY.y) {
											return;
										}
										var top = f.getTop(e, 0);
										// ele拖拽后的高度为：初始height-拖拽后鼠标距离屏幕的距离 + 第一次按下时鼠标距离屏幕的距离
										// 重新设置ele的top值为此时鼠标距离屏幕的y值
										that.css({
											height: (eleWH.height - e.clientY + clientXY.y) + "px",
											top: top + "px"
										});
									}
									// 与”top“相同原理
									if (type.indexOf("left") > -1) {
										if (50 > eleWH.width - e.clientX + clientXY.x) {
											return;
										}
										var maxLeft = skyeyeReportContent.width() - that.width();
										var left = f.getLeft(e, 0, maxLeft);
										// 重新设置ele的left值为此时鼠标距离屏幕的x值
										that.css({
											width: f.getWidth(eleWH, e, clientXY, skyeyeReportContent.width()) + "px",
											left: left + "px"
										});
									}
								}
								document.onmouseup = function () {
									document.onmousemove = null;
									document.onmouseup = null;
								}
							}
						}
					}
					return div;
				},

				setMoveEvent: function (ee, box){
					// 获取事件对象
					var ee = ee || window.event;
					var maxLeft = skyeyeReportContent.width() - box.width();
					// 鼠标按下移动时调用mousemove
					document.onmousemove = e => {
						if(box.hasClass("active")){
							// 元素ele移动的距离left
							var left = f.getLeft(e, ee.offsetX, maxLeft);
							// 元素ele移动的距离top
							var top = f.getTop(e, ee.offsetY);
							box.css({
								left: left + "px",
								top: top + "px"
							});
						} else {
							f.setChooseReportItem(box);
						}
					}
					// 当鼠标弹起时，清空onmousemove与onmouseup
					document.onmouseup = () => {
						document.onmousemove = null;
						document.onmouseup = null;
					}
				},

				/**
				 * 获取拖拽的元素距离顶部的最终距离
				 *
				 * @param e 事件对象
				 * @param y 鼠标按下时，鼠标相对于元素的y坐标
				 * @returns {number|number}
				 */
				getTop: function (e, y){
					var top = e.clientY - y - 104;
					top = top < 0 ? 0 : top;
					var chooseEcharts = skyeyeReportContent.find(".active").eq(0);
					var boxId = chooseEcharts.data("boxId");
					var moveYId = $("div[modelKey='custom.move.y'][boxId='" + boxId + "']").find("input").attr("id");
					$("#" + moveYId).val(top);
					dataValueChange(top, $("#" + moveYId));
					return top;
				},

				/**
				 * 获取拖拽的元素距离左侧的最终距离
				 *
				 * @param e 事件对象
				 * @param x 标按下时，鼠标相对于元素的x坐标
				 * @param maxLeft 允许的最大左边距
				 * @returns {number|number}
				 */
				getLeft: function (e, x, maxLeft){
					var left = e.clientX - x - 44;
					left = left < 0 ? 0 : left;
					left = left > maxLeft ? maxLeft : left;
					var chooseEcharts = skyeyeReportContent.find(".active").eq(0);
					var boxId = chooseEcharts.data("boxId");
					var moveXId = $("div[modelKey='custom.move.x'][boxId='" + boxId + "']").find("input").attr("id");
					$("#" + moveXId).val(left);
					dataValueChange(left, $("#" + moveXId));
					return left;
				},

				/**
				 * 获取报表元素允许的宽度
				 * @param eleWH 获取鼠标按下时ele的宽高
				 * @param e 事件对象
				 * @param clientXY 鼠标距离屏幕的left与top值
				 * @param maxWidth 允许的最大宽度
				 * @returns {*}
				 */
				getWidth: function (eleWH, e, clientXY, maxWidth){
					var width = eleWH.width - e.clientX + clientXY.x;
					width = width > maxWidth ? maxWidth : width;
					return width;
				},

				/**
				 * 删除选中项
				 */
				deleteChooseItem: function(){
					var chooseEcharts = skyeyeReportContent.find(".active").eq(0);
					if(f.isNull(chooseEcharts)){
						return;
					}
					var boxId = chooseEcharts.data("boxId");
					$("#" + boxId).remove();
					delete inPageEchartsObject[boxId];
					delete inPageEcharts[boxId];
				},

				// 快捷键
				tableKeyDown: function(e) {
					let eCode = e.keyCode ? e.keyCode : e.which ? e.which : e.charCode;
					if (e.ctrlKey && eCode === 90) {
						// 撤销

					}else if(eCode === 46){
						// delete键
						f.deleteChooseItem();
					}
				},

				// 记录缓存，方便撤销
				recordData: function() {

				},

				initExcelEvent: function(){
					// 不触发‘移除所有图表的编辑信息’的事件的对象的class--颜色选择器
					var notTriggerRemove = ["layui-colorpicker-main"];
					// 图表点击事件
					$("body").on('click', ".echarts-box, .word-box", function (e) {
						f.setChooseReportItem($(this));
						e.stopPropagation();
					});

					// 内容点击
					$("body").on('click', skyeyeReportContent, function (e) {
						var pass = true;
						$.each(notTriggerRemove, function (i, item){
							if($(e.target).parents("." + item).length > 0 || $(e.target).attr('class').indexOf(item) != -1){
								pass = false;
							}
						});
						if(pass){
							f.removeEchartsEditMation();
						}
					});

					// 面板切换
					$("body").on('click', '.layui-colla-title', function (e) {
						$(this).parent().find('.layui-colla-content').toggleClass('layui-show');
						if($(this).parent().find('.layui-colla-content').hasClass('layui-show')){
							$(this).find('.f-icon').removeClass('arrow-bottom').addClass("arrow-top");
						} else {
							$(this).find('.f-icon').removeClass('arrow-top').addClass("arrow-bottom");
						}
					});

					// 编辑器点击防止触发父内容事件
					$("body").on('click', ".form-box", function (e) {
						e.stopPropagation();
					});

					// 保存
					$("body").on('click', "#save", function (e) {
						var eachartsList = f.getEchartsListToSave();
						var wordMationList = f.getWordMationListToSave();

						var params = {
							contentWidth: skyeyeReportContent.width(),
							contentHeight: skyeyeReportContent.height(),
							bgImage: skyeyeReportContent.css("backgroundImage").replace('url(','').replace(')', ''),
							modelList: eachartsList,
							wordMationList: wordMationList
						};
						AjaxPostUtil.request({url: reportBasePath + "reportpage007", params: {rowId: rowId, content: encodeURIComponent(JSON.stringify(params))}, type: 'json', method: "POST", callback: function(json) {
							winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
						}});
					});
				},

				getEchartsListToSave: function (){
					var eachartsList = new Array();
					$.each(skyeyeReportContent.find(".kuang"), function(i, item){
						if($(item).find(".echarts-box").length > 0){
							var boxId = $(item).data("boxId");
							var echartsMation = inPageEcharts[boxId];
							eachartsList.push({
								modelId: $(item).data("modelId"),
								attrMation: echartsMation,
								width: $(item).width(),
								height: $(item).height()
							});
						}
					});
					return eachartsList;
				},

				getWordMationListToSave: function (){
					var wordMationList = new Array();
					$.each(skyeyeReportContent.find(".kuang"), function(i, item){
						if($(item).find(".word-box").length > 0) {
							var boxId = $(item).data("boxId");
							var wordMation = inPageWordMation[boxId];
							wordMationList.push({
								modelId: $(item).data("modelId"),
								attrMation: wordMation,
								width: $(item).width(),
								height: $(item).height()
							});
						}
					});
					return wordMationList;
				},

				// 设置选中项
				setChooseReportItem: function(_this){
					if(!_this.parent().hasClass("active")){
						f.removeEchartsEditMation();
						// 被选中项
						_this.parent().find(".dian").show();
						_this.parent().addClass("active");
						f.loadEchartsEditor();
					}
				},

				// 移除所有图表的编辑信息
				removeEchartsEditMation: function(){
					$(".dian").hide();
					$(".kuang").removeClass("active");
					$("#showForm").parent().remove();
				},

				// 加载echarts报表编辑器
				loadEchartsEditor: function (){
					var chooseObject = skyeyeReportContent.find(".active").eq(0);
					var boxId = chooseObject.data("boxId");
					var objectMation = getDataChooseMation(boxId);
					f.addNewFormBox();
					if(!f.isNull(objectMation)) {
						$("#showForm").html("");
						var indexNumber = 1;
						var newArray = f.restAttrToArrayByTypeName(objectMation.attr);
						$('<div class="layui-collapse" id="showFormPanel"></div>').appendTo($("#showForm").get(0));
						$.each(newArray, function(typeName, attr) {
							var panelHTML = '<div class="layui-colla-item"><h2 class="layui-colla-title"><em class="f-icon arrow-bottom"></em><span>' + typeName + '</span></h2><div class="layui-colla-content layui-show" id="' + typeName + '"></div>';
							$(panelHTML).appendTo($("#showFormPanel").get(0));
							$.each(attr, function (key, val) {
								if (val.edit == 1) {
									// 可以编辑
									var formItem = editorType[val.editor];
									if (!f.isNull(formItem)) {
										// 如果表单类型中支持的编辑器类型存在，则去解析
										var data = f.getFormItemData(key, val, boxId, indexNumber);
										if (!f.isNull(formItem.showValueTemplate)) {
											// 一般单选，多选，下拉框会用到，加载可选择的数据项
											var showValueTemplate = getDataUseHandlebars(formItem.showValueTemplate, {rows: data.bean.editorChooseValue});
											data.bean.showValueTemplate = showValueTemplate;
										}
										// 获取解析后的html
										var html = getDataUseHandlebars('{{#bean}}' + formItem.html + '{{/bean}}', data);
										$(html).appendTo($("#" + typeName).get(0));
										if (!f.isNull(formItem.js)) {
											// 获取解析后的js
											var js = getDataUseHandlebars('{{#bean}}' + formItem.js + '{{/bean}}', data);
											var jsCon = '<script>layui.define(["jquery"], function(exports) {var jQuery = layui.jquery;(function($) {' + js + '})(jQuery);});</script>';
											$("#" + typeName).append(jsCon);
										}
									} else {
										f.loadEchartsEditorISDetail(key, val, boxId, indexNumber, typeName);
									}
								} else {
									f.loadEchartsEditorISDetail(key, val, boxId, indexNumber, typeName);
								}
								indexNumber++;
							});
						});
						// 到底啦警示
						$('<fieldset class="layui-elem-field layui-field-title" style="margin-top: 30px; text-align: center;"><legend style="font-size: 12px; color: gray;">到底啦</legend></fieldset>').appendTo($("#showFormPanel").get(0));
						form.render();
						// 表单提示语内容展示
						$('*[lay-tips]').on('mouseenter', function() {
							var content = $(this).attr('lay-tips');
							this.index = layer.tips('<div style="font-size: 14px; color: #eee;">'+ content + '</div>', this, {
								time: -1,
								maxWidth: 280,
								tips: [3, '#3A3D49']
							});
						}).on('mouseleave', function() {
							layer.close(this.index);
						});
					}
				},

				// 加载编辑器‘详情’类型的展示
				loadEchartsEditorISDetail: function (key, val, boxId, indexNumber, typeName){
					var formItem = editorType["100"];
					var data = f.getFormItemData(key, val, boxId, indexNumber);
					var html = getDataUseHandlebars('{{#bean}}' + formItem.html + '{{/bean}}', data);
					$(html).appendTo($("#" + typeName).get(0));
				},

				// 将echarts属性根据类型名称进行分组
				restAttrToArrayByTypeName: function(attr) {
					var array = {};
					$.each(attr, function(key, val){
						var typeName = f.isNull(val.typeName) ? "未分组" : val.typeName;
						var simpleTypeNameList = array[typeName];
						if(f.isNull(simpleTypeNameList)){
							simpleTypeNameList = {};
						}
						simpleTypeNameList[key] = val;
						array[typeName] = simpleTypeNameList
					});
					return array;
				},

				// 将echarts的数据格式转化为form表单的数据格式
				getFormItemData: function(key, val, boxId, indexNumber){
					var editorChooseValue = f.getEditorChooseValue(val, boxId, indexNumber);
					var value = val.value;
					if(isJSON(val.value)){
						value = JSON.parse(val.value);
					}
					if(val.editor == '2'){
						// 编辑器类型为数据框：如果是数组，则需要转成json串
						if(value instanceof Array){
							value = JSON.stringify(value);
						}
					}
					return {
						"bean": {
							modelKey: key,
							boxId: boxId,
							defaultWidth: "layui-col-xs12",
							labelContent: val.title,
							context: value,
							pointValue: val.pointValue,
							desc: val.desc,
							editorChooseValue: editorChooseValue,
							indexNumber: indexNumber // 第几个组件
						}
					};
				},

				getEditorChooseValue: function (val, boxId, indexNumber) {
					if (val.editor == "9") {
						val.editorChooseValue = inPageEcharts[boxId].attr["custom.dataBaseMation"].value.analysisData;
					}
					var editorChooseValue = []
					if (isJSON(val.editorChooseValue)) {
						editorChooseValue = JSON.parse(val.editorChooseValue);
					} else if (typeof val.editorChooseValue == 'object'
						&& (JSON.stringify(val.editorChooseValue).indexOf('{') == 0 || JSON.stringify(val.editorChooseValue).indexOf('[') == 0)) {
						editorChooseValue = val.editorChooseValue;
					}
					$.each(editorChooseValue, function (i, item) {
						item["boxId"] = boxId;
						item["indexNumber"] = indexNumber;
					});
					return editorChooseValue;
				},

				// 添加一个新的form表单
				addNewFormBox: function(){
					var editForm = '<div class="form-box">' +
						'<fieldset class="layui-elem-field layui-field-title">' +
						'  <legend>属性</legend>' +
						'</fieldset>' +
						'<form class="layui-form layui-col-xs12" action="" id="showForm" autocomplete="off" style="height: calc(100% - 50px); position: absolute; overflow-y: auto"></form>' +
						'</div>';
					skyeyeReportContent.append(editForm);
				},

				// 加载编辑器类型
				initEditorType: function (){
					$.getJSON("../../assets/report/json/skyeyeEditor.json", function (data) {
						editorType = data;
					});
				},

				// reportContent的八个角
				getEightCape: function(){
					var capeResult = '<div class="cape cape_left1"></div>' +
						'<div class="cape cape_left1_top"></div>' +
						'<div class="cape cape_right1"></div>' +
						'<div class="cape cape_right1_top"></div>' +
						'<div class="cape cape_left2"></div>' +
						'<div class="cape cape_left2_bottom"></div>' +
						'<div class="cape cape_right2"></div>' +
						'<div class="cape cape_right2_bottom"></div>';
					return capeResult;
				},

				initData: function(){
					var widthScale = getScale(params.initData.contentWidth, skyeyeReportContent.width());
					var heightScale = getScale(params.initData.contentHeight, skyeyeReportContent.height());
					// 初始化echarts模型
					f.initEchartsData(widthScale, heightScale);
					// 初始化文字模型
					f.initWordMationData(widthScale, heightScale);
					// 初始化背景
					if(!f.isNull(params.initData.bgImage)){
						skyeyeReportContent.css({
							"background-image": "url(" + params.initData.bgImage + ")",
							"background-size": skyeyeReportContent.width() + "px " + skyeyeReportContent.height() + "px"
						});
					}
					f.removeEchartsEditMation();
				},

				initEchartsData: function(widthScale, heightScale) {
					var modelList = params.initData.modelList;
					if (!f.isNull(modelList)) {
						$.each(modelList, function (i, item) {
							var leftNum = multiplication(item.attrMation.attr["custom.move.x"].value, widthScale);
							var topNum = multiplication(item.attrMation.attr["custom.move.y"].value, heightScale);
							item.attrMation.attr["custom.move.x"].value = leftNum;
							item.attrMation.attr["custom.move.y"].value = topNum;
							var boxId = f.addNewModel(item.modelId, item.attrMation);
							$("#" + boxId).css({
								left: leftNum + "px",
								top: topNum + "px",
								width: multiplication(item.width, widthScale),
								height: multiplication(item.height, heightScale)
							});
							setBoxAttrMation("custom.box.background", boxId, item.attrMation.attr["custom.box.background"].value);
							setBoxAttrMation("custom.box.border-color", boxId, item.attrMation.attr["custom.box.border-color"].value);
						});
					}
				},

				initWordMationData: function(widthScale, heightScale) {
					var wordMationList = params.initData.wordMationList;
					if (!f.isNull(wordMationList)) {
						$.each(wordMationList, function (i, item) {
							var leftNum = multiplication(item.attrMation.attr["custom.move.x"].value, widthScale);
							var topNum = multiplication(item.attrMation.attr["custom.move.y"].value, heightScale);
							item.attrMation.attr["custom.move.x"].value = leftNum;
							item.attrMation.attr["custom.move.y"].value = topNum;
							var boxId = f.addNewWordModel(item.modelId, item.attrMation);
							$("#" + boxId).css({
								left: leftNum + "px",
								top: topNum + "px",
								width: multiplication(item.width, widthScale),
								height: multiplication(item.height, heightScale)
							});
							setBoxAttrMation("custom.box.background", boxId, item.attrMation.attr["custom.box.background"].value);
							setBoxAttrMation("custom.box.border-color", boxId, item.attrMation.attr["custom.box.border-color"].value);
						});
					}
				},

				// 初始化执行
				init: function() {
					f.box();
					f.ui();
					f.ie6();
					f.loadHeader();
					f.initExcelEvent();
					// 加载编辑器类型
					f.initEditorType();
					// 初始化数据
					f.initData();
				}
			};

			if(flag) {
				$('<div class="skyeyeScaleBox" id="skyeyeScaleBox" onselectstart="return false;">' +
					'<div id="skyeyeScaleRulerH" class="skyeyeScaleRuler_h"></div>' +
					'<div id="skyeyeScaleRulerV" class="skyeyeScaleRuler_v"></div>' +
					'</div>' +
					'<div class="hd-main clearfix" id="skyeyeHeader">' +
					'<font class="logo-title">Skyeye系列-报表设计器</font>' +
					'<div class="navs"></div>' +
					'</div>' +
					f.getEightCape() +
					'<div class="report-content" id="reportContent">' +
					'</div>').appendTo($("body"));
			} else {
				$("#skyeyeScaleBox").show();
			}
			//整个标尺盒子对象，垂直标尺与水平标尺对象，虚线对象，弹出框对象，单选对象，文本对象，按钮对象
			var x = $("#skyeyeScaleBox"),
				rh = $("#skyeyeScaleRulerH"),
				rv = $("#skyeyeScaleRulerV"),
				skyeyeHeader = $("#skyeyeHeader"), // 头部菜单栏
				skyeyeReportContent = $("#reportContent"); // 编辑器内容

			// 初始化
			f.init();

			/*浏览器拉伸时，标尺自适应*/
			$(window).resize(function() {
				f.box();
				f.ui();
			});

			$(document).keydown(function (e) {
				f.tableKeyDown(e);
			});

		};

	})(jQuery);
	exports('skyeyeReportDesigner', null);
});

// 编辑器变化事件
function dataValueChange(value, _this){
	var modelKey = _this.parents('.layui-form-item').attr('modelKey');
	var boxId = _this.parents('.layui-form-item').attr('boxId');
	// 控件类型
	var controlType = _this.parents('.layui-form-item').attr('controlType');
	value = getValueByControlType(controlType, value, _this.parents('.layui-form-item'));
	var _chooseMation = getDataChooseMation(boxId);
	layui.$.each(_chooseMation.attr, function(key, val){
		if(modelKey == key){
			if(controlType == 'dynamicData'){
				// 动态数据
				val.pointValue = value;
				val.editorChooseValue = _chooseMation.attr["custom.dataBaseMation"].value.analysisData;
			} else {
				val.value = getVal(value);
			}
		}
	});

	if(_chooseMation.menuType == 'echartsModel'){
		resetChartsModel(boxId);
	} else if(_chooseMation.menuType == 'wordModel'){
		resetWordModel(boxId);
	}

	afterRunBack(controlType, value);

	// 设置box盒子属性
	setBoxAttrMation(modelKey, boxId, value);
}

function resetChartsModel(boxId) {
	var echartsMation = inPageEcharts[boxId];
	var option = getEchartsOptions(echartsMation);
	// 加载图表
	var newChart = inPageEchartsObject[boxId];
	newChart.setOption(option);
}

function resetWordModel(boxId){
	var wordMation = inPageWordMation[boxId];
	var styleStr = getWordStyleStr(wordMation.attr);
	$("#" + boxId).find(".word-box").attr("style", styleStr);
}

function getDataChooseMation(boxId){
	var _object = inPageEcharts[boxId];
	if(!isNull(_object)){
		_object.menuType = 'echartsModel';
	}
	if(isNull(_object)){
		_object = inPageWordMation[boxId];
		if(!isNull(_object)){
			_object.menuType = 'wordModel';
		}
	}
	return _object;
}

function setBoxAttrMation(modelKey, boxId, value){
	if(modelKey.indexOf("custom.box") >= 0){
		// 设置图表盒子属性
		var attr = modelKey.replace("custom.box.", "");
		layui.$("#" + boxId).css(attr, value);
	}
	if(modelKey == 'custom.textContent'){
		layui.$("#" + boxId).find(".word-box").html(value);
	}
}

function getValueByControlType(controlType, value, parentBox){
	if(controlType == 'inputMoreColor'){
		// 多行颜色选择器
		var array = new Array();
		layui.$.each(parentBox.find(".customer-attr"), function (i, item){
			array.push(layui.$(item).val());
		});
		value = array;
	}
	return value;
}

function afterRunBack(controlType, value){
	if(controlType == 'dataBaseFrom'){
		// 动态数据
		$('[controlType="dynamicData"]').find('select').html(getDataUseHandlebars(editorType["9"].showValueTemplate, {rows: value.analysisData}));
		form.render('select');
	}
}

// 获取echarts的配置信息
function getEchartsOptions(echartsMation){
	var list = new Array();
	layui.$.each(echartsMation.attr, function(key, val) {
		val["useKey"] = key;
		list.push(val);
	});
	list = list.sort(compare("useKey",1));
	var echartsJson = {};
	layui.$.each(list, function(i, val) {
		var value = getVal(val.value);
		var key = val.useKey;
		echartsJson = layui.$.extend(true, {}, echartsJson, calcKeyHasPointToJson(echartsJson, key, [], value));
	});
	layui.$.each(echartsJson, function(key, val) {
		if(judgeIsArray(key)){
			var parentKey = getInArrayStrIndex(key)
			var parentIndex = getInArrayNumIndex(key);
			echartsJson[parentKey][parentIndex] = val;
			delete echartsJson[key];
		}
	});
	return echartsJson;
}

// 获取文字模型样式信息
function getWordStyleStr(propertyList){
	var styleStr = "";
	$.each(propertyList, function (key, value){
		if(!isNull(value) && key.indexOf("custom.") < 0){
			styleStr += value.attrCode + ":" + value.value + ";";
		}
	});
	return styleStr;
}

function calcKeyHasPointToJson(echartsJson, key, parentKey, value){
	var keyArr = key.split(".");
	if (isJSON(value)) {
		value = JSON.parse(value);
	}
	var resultValue = value;
	if(keyArr.length > 1){
		parentKey.push(key.substring(0, (key.indexOf("."))));
		resultValue = calcKeyHasPointToJson(echartsJson, key.substr(key.indexOf(".") + 1), parentKey, value);
	}
	var thisKey = keyArr[0];
	if(judgeIsArray(thisKey)){
		var mation;
		var funStr = "echartsJson";
		layui.$.each(parentKey, function(i, val) {
			if(judgeIsArray(val)){
				var parentKey = getInArrayStrIndex(val)
				var parentIndex = getInArrayNumIndex(val);
				mation = echartsJson[parentKey][parentIndex];
				funStr += '["' + parentKey + '"][' + parentIndex + ']';
			} else {
				mation = echartsJson[val];
				funStr += '["' + val + '"]';
			}
		});
		mation = layui.$.extend(true, {}, mation, resultValue);
		funStr += ' = mation';
		eval(funStr);
		var result = {};
		result[thisKey] = mation;
		return result;
	} else {
		var result = {};
		result[thisKey] = resultValue;
		return result;
	}
}

function judgeIsArray(key){
	if(key.indexOf('[') >= 0 && key.indexOf(']') >= 0){
		return true;
	}
	return false;
}

function getInArrayNumIndex(key){
	var startIndex = key.indexOf('[') + 1;
	var endIndex = key.indexOf(']');
	return parseInt(key.substring(startIndex, endIndex));
}

function getInArrayStrIndex(key){
	var endIndex = key.indexOf('[');
	return key.substring(0, endIndex);
}

function getVal(value){
	if("true" == value){
		return true;
	} else if("false" == value){
		return false;
	}
	return value;
}

/**
 * 获取比例
 *
 * @param oldSize 上一个版本打开的宽度/高度
 * @param newContentSize 当前打开的宽度/高度
 * @returns {*}
 */
function getScale(oldSize, newContentSize){
	var a1 = parseFloat(isNull(newContentSize) ? 0 : newContentSize);
	var a2 = parseFloat(isNull(oldSize) ? 0 : oldSize);
	if(a2 == 0){
		return 0;
	}
	return (a1 / a2).toFixed(6);
}

function compare(propertyName, order) {
	return function (object1, object2) {
		var value1 = isNull(object1[propertyName]) ? 0 : object1[propertyName].length;
		var value2 = isNull(object2[propertyName]) ? 0 : object2[propertyName].length;
		if(order == 0){
			if (value2 < value1) {
				return -1;
			} else if (value2 > value1) {
				return 1;
			} else {
				return 0;
			}
		}if(order == 1){
			if (value2 > value1) {
				return -1;
			} else if (value2 < value1) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
