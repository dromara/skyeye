
var customerJS = {
	"handlebars": "../../assets/lib/layui/lay/modules/hdb/handlebars-v4.0.5.js", // 模板引擎
	"showGrid": "../../assets/lib/layui/customer/showGrid.js", // 非表格分页加载插件
	"city": "../../json/city.js", // 地址
	"fontawesomeAll": "../../assets/lib/fontawesome-free-5.11.2/css/all.css", // fontawesome5
	"fontawesomev4": "../../assets/lib/fontawesome-free-5.11.2/css/v4-shims.css", // fontawesome4兼容版
	"systemCommonUtil": "../../assets/lib/layui/customer/common/systemCommonUtil.js", // 系统工具函数
	"activitiUtil": "../../assets/lib/layui/customer/activitiUtil.js", // 工作流工具函数
	"erpOrderUtil": "../../assets/lib/layui/customer/erpOrderUtil.js", // erp工具函数
	"mailUtil": "../../assets/lib/layui/customer/mailUtil.js", // 通讯录工具函数
	"systemDateUtil": "../../assets/lib/layui/customer/systemDateUtil.js", // 日期工具函数
	"calculationUtil": "../../assets/lib/layui/customer/calculationUtil.js", // 加减乘除工具函数
	"skyeyeEnclosure": "../../assets/lib/layui/customer/skyeyeEnclosure.js", // 附件工具函数
	"ajaxPostUtil": "../../assets/lib/layui/customer/ajaxPostUtil.js", // ajax工具函数
	"dsFormUtil": "../../assets/lib/layui/customer/skyeye/dsFormUtil.js", // 动态表单工具函数
	"systemModelUtil": "../../assets/lib/layui/customer/systemModelUtil.js", // 系统编辑器模板工具函数
	"ueEditorUtil": "../../assets/lib/layui/customer/ueEditorUtil.js", // ueEditor编辑器工具函数
	"accountSubjectUtil": "../../assets/lib/layui/customer/accountSubjectUtil.js", // 财务会计相关的工具函数
	"sysCustomerUtil": "../../assets/lib/layui/customer/sysCustomerUtil.js", // CRM相关的工具函数
	"sysIfsUtil": "../../assets/lib/layui/customer/sysIfsUtil.js", // 财务相关的工具函数
	"sysFileUtil": "../../assets/lib/layui/customer/sysFileUtil.js", // 系统文件相关处理工具
	"publicKeyCode": "../../assets/lib/layui/customer/publicKeyCode.js", // 快捷键
	"sysSupplierUtil": "../../assets/lib/layui/customer/sysSupplierUtil.js", // 供应商相关工具
	"sysMemberUtil": "../../assets/lib/layui/customer/sysMemberUtil.js", // 会员相关工具
	"bossUtil": "../../assets/lib/layui/customer/bossUtil.js", // 招聘模块相关工具
	"shopUtil": "../../assets/lib/layui/customer/shopUtil.js", // 商城模块相关工具
	"indexMenu": "../../assets/lib/layui/customer/indexMenu.js", // 菜单按钮模块相关工具
	"proUtil": "../../assets/lib/layui/customer/proUtil.js", // 项目管理模块相关工具
	"adminAssistantUtil": "../../assets/lib/layui/customer/adminAssistantUtil.js", // 行政管理模块相关工具
	"codeDocUtil": "../../assets/lib/layui/customer/codeDocUtil.js", // 代码生成器相关工具类
	"organizationUtil": "../../assets/lib/layui/customer/organizationUtil.js", // 组织机构相关工具类
	"threeUtil": "../../assets/lib/layui/customer/threeUtil.js", // 3D编辑器相关工具类
	"reportModelTypeUtil": "../../assets/lib/layui/customer/reportModelTypeUtil.js", // 模型分类工具类
	"schoolUtil": "../../assets/lib/layui/customer/skyeye/schoolUtil.js", // 学校模块工具类
	"checkWorkUtil": "../../assets/lib/layui/customer/checkWorkUtil.js", // 考勤模块工具类
	"arrayUtil": "../../assets/lib/layui/customer/arrayUtil.js", // 集合工具类
	"initTableChooseUtil": "../../assets/lib/layui/customer/tableUtil/initTableChooseUtil.js", // 加载表格选择的表格插件
	"sysDictDataUtil": "../../assets/lib/layui/customer/skyeye/sysDictDataUtil.js", // 数据字典插件
	"initTableSearchUtil": "../../assets/lib/layui/customer/tableUtil/initTableSearchUtil.js", // 表格高级查询插件
	"skyeyeClassEnumUtil": "../../assets/lib/layui/customer/skyeye/skyeyeClassEnumUtil.js", // 动态枚举插件
	"teamObjectPermissionUtil": "../../assets/lib/layui/customer/teamObjectPermissionUtil.js", // 团队成员权限插件
	"catalogTreeUtil": "../../assets/lib/layui/customer/tree/catalogTreeUtil.js", // 目录功能的工具类
};

//系统基础信息
var sysMainMation = {}; // 系统基础信息json
if(isNull(localStorage.getItem("sysMainMation"))){
	jsGetJsonFile("../../configRation.json", function(data) {
		sysMainMation = data;
		localStorage.setItem("sysMainMation", JSON.stringify(sysMainMation));
		initBaseParams();
	});
} else {
	sysMainMation = JSON.parse(unescape(localStorage.getItem("sysMainMation")));
	initBaseParams();
}

var skyeyeVersion;
// 文件路径
var fileBasePath;
// 接口请求地址
var reqBasePath; // 总项目
var shopBasePath; // 商城项目
var flowableBasePath; // 工作流相关功能的项目
var schoolBasePath; // 学校模块请求地址
var reportBasePath; // 报表模块请求地址
var homePagePath; // 前端请求地址
var webSocketPath;//聊天socket-开发

function initBaseParams(){
	skyeyeVersion = sysMainMation.skyeyeVersion;
	fileBasePath = sysMainMation.fileBasePath;
	reqBasePath = sysMainMation.reqBasePath;
	shopBasePath = sysMainMation.shopBasePath;
	webSocketPath = sysMainMation.webSocketPath;
	flowableBasePath = sysMainMation.flowableBasePath;
	homePagePath = sysMainMation.homePagePath;
	schoolBasePath = sysMainMation.schoolBasePath;
	reportBasePath = sysMainMation.reportBasePath;
}

function getRequestHeaders() {
	return {
		userToken: getCookie('userToken'),
		loginPCIp: returnCitySN["cip"]
	};
}
function setRequestHeaders(xmlhttp) {
	var headers = getRequestHeaders();
	$.each(headers, function (key, value) {
		xmlhttp.setRequestHeader(key, value);
	});
}

// 编辑加载自定义的js文件
layui.each(customerJS, function(key, jsPath){
	if(jsPath.lastIndexOf(".js") >=0){
		document.write('<script type="text/javascript" src="' + jsPath + '?v=' + skyeyeVersion + '"></script>');
	} else {
		document.write('<link rel="stylesheet" type="text/css" href="' + jsPath + '?v=' + skyeyeVersion + '"/>');
	}
});

// 操作添加或者编辑时，判断表格是否需要刷新,为0则刷新，否则则不刷新
var refreshCode = "";
// 返回父页面的数据
var turnData = "";

/**
 * 打开新的窗口
 * @param url
 * @param params
 * @param title
 */
function _openNewWindows(mation){
	if(isNull(mation.url)){
		winui.window.msg("页面路径不能为空", {icon: 2, time: 2000});
		return;
	}
	if(isNull(mation.pageId)){
		winui.window.msg("缺少页面ID", {icon: 2, time: 2000});
		return;
	}
	if(isNull(mation.title)){
		mation.title = "窗口";
	}
	if (!isNull(mation.params)){
		var s = "";
		for(var param in mation.params)
			s += "&" + param + "=" + mation.params[param];
		mation.url = mation.url + "?" + s.slice(1);
	}
	mation.url = systemCommonUtil.getHasVersionUrl(mation.url);
	if(isNull(mation.area)){
		if(mation.maxmin){
			mation.area = ['100vw', '100vh'];
		} else {
			mation.area = ['90vw', '90vh'];
		}
	}
	if(isNull(mation.offset)){
		mation.offset = 'auto';
	}
	if(isNull(mation.maxmin)){//是否最大化
		mation.maxmin = false;
	}
	if(isNull(mation.shade) && mation.shade != false && mation.shade != 0){//遮罩层
		mation.shade = 0.5;
	}
	if(isNull(mation.closeBtn) && mation.closeBtn != '0'){//关闭按钮
		mation.closeBtn = 1;
	}
	if(isNull(mation.skin)){//用户自定义皮肤或者层级
		mation.skin = '';
	}
	refreshCode = "";
	turnData = "";
	var pageIndex = layer.open({
		id: mation.pageId,
		type: 2,
		skin: mation.skin,
		title: mation.title,
		content: mation.url,
		area: mation.area,
		offset: mation.offset,
		maxmin: mation.maxmin,
		shade: mation.shade,
		zIndex: mation.zIndex,
		scrollbar: false,
		closeBtn: mation.closeBtn,
		end: function() {
			if(typeof(mation.callBack) == "function") {
				if (refreshCode == '0') {
					mation.callBack(refreshCode, turnData);
				} else if (refreshCode == '-9999') {
					winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
				}
			}
		},
		success: function(){
			var times = layui.$("#" + mation.pageId).parent().attr("times");
			var zIndex = layui.$("#" + mation.pageId).parent().css("z-index");
			layui.$("#layui-layer-shade" + times).css({'z-index': zIndex});
			if(typeof(mation.success) == "function") {
				mation.success();
			}
		}
	});
	if(mation.maxmin){
		layer.full(pageIndex);
	}
}

/**
 * 根据页面高度进行分页
 * @return {}
 */
function getLimits() {
	var limit = getLimit();
	var limits = new Array();
	for(var i = 1; i <= 7; i++){
		limits.push(limit * i);
	}
	return limits;
}

function getLimit(){
	var clientHeight = document.body.clientHeight;
	var toolbarHeight = $(".winui-toolbar").outerHeight(true);
	var txtcenterHeight = $(".txtcenter").outerHeight(true);
	var winuiTip = $(".winui-tip").outerHeight(true);
	var tabTtileHeight = $(".layui-tab-title").outerHeight(true);
	// 计算表格tbody的高度
	var realHeight = clientHeight - 120
		- (isNull(toolbarHeight) ? 0 : toolbarHeight)
		- (isNull(txtcenterHeight) ? 0 : txtcenterHeight)
		- (isNull(winuiTip) ? 0 : winuiTip)
		- (isNull(tabTtileHeight) ? 0 : tabTtileHeight);
	// 计算limit
	return decimerFiveOrZero(Math.floor(realHeight / 35));
}

function decimerFiveOrZero(number){
	var newNum = Math.floor(number / 5);
	return newNum * 5 < 0 ? 10 : newNum * 5;
}


/**
 * 根据数据展示
 */
function showDataUseHandlebars(id, source, data){
	//预编译模板
	var template = Handlebars.compile(source);
	//匹配json内容
	var html = template(data);
	//输入模板
	layui.$("#" + id).html(html);
}

function getDataUseHandlebars(source, data){
	//预编译模板
	var template = Handlebars.compile(source);
	//匹配json内容
	var html = template(data);
	//输入模板
	return html;
}

var postDownLoadFile = function(options) {
	var config = layui.$.extend(true, {
		method : 'post'
	}, options);
	var $iframe = layui.$('<iframe id="down-file-iframe" />');
	var $form = layui.$('<form target="down-file-iframe" method="' + config.method + '" />');
	$form.attr('action', config.url);
	if (!isNull(config.params)){
		for (var key in config.params) {
			$form.append('<input type="hidden" name="' + key + '" value="' + config.params[key] + '" />');
		}
		$form.append('<input type="hidden" name="userToken" value="' + getCookie('userToken') + '" />');
	}
	// 图片
	if (!isNull(config.data)){
		$form.append('<input type="hidden" name="base64Info" value="' + config.data + '" />');
	}

	$iframe.append($form);
	layui.$(document.body).append($iframe);
	$form[0].submit();
	$iframe.remove();
}

/**
 * 权限验证
 * @param urlNum
 */
function auth(urlNum) {
	var authList = JSON.parse(localStorage.getItem("authpoints"));
	if (!isNull(authList)) {
		for (var i = 0; i < authList.length; i++) {
			if (authList[i].menuNum === urlNum) {
				return true;
			} else {
				// 数据权限分组不为空
				if (!isNull(authList[i].children) && authList[i].children.length > 0) {
					var dataGroup = authList[i].children;
					for (var j = 0; j < dataGroup.length; j++) {
						// 数据权限不为空
						if (!isNull(dataGroup[j].children) && dataGroup[j].children.length > 0) {
							var dataAuthPoint = dataGroup[j].children;
							for (var k = 0; k < dataAuthPoint.length; k++) {
								if (dataAuthPoint[k].menuNum === urlNum) {
									return true;
								}
							}
						}
					}
				}
			}
		}
	} else {
		winui.window.msg('登录超时，即将返回登录页面.', {icon: 2, time: 2000}, function () {
			var win = window;
			while (win != win.top) {
				win = win.top;
			}
			win.location.href = "../../tpl/index/login.html";
		});
	}
	return false;
}

function authBtn(urlNum) {
	if (!auth(urlNum)) {
		layui.$('[auth="' + urlNum + '"]').remove();
	}
}

/**
 * 加载列表接口的数据权限菜单，因为要和表格搜索结合到一起，所以监听事件是要去initTableSearchUtil.js中查看
 *
 * @param tableId 表格id
 * @param urlNum 权限点id
 */
function loadAuthBtnGroup(tableId, urlNum) {
	if (isNull(urlNum)) {
		return;
	}
	var authList = JSON.parse(localStorage.getItem("authpoints"));
	if (!isNull(authList)) {
		var str = '';
		for (var i = 0; i < authList.length; i++) {
			if (authList[i].menuNum === urlNum && authList[i].children.length > 0) {
				// 数据权限分组不为空
				var dataGroup = authList[i].children;
				for (var j = 0; j < dataGroup.length; j++) {
					str += `<div style="" class="type-group" id="${dataGroup[j].menuUrl}">`;
					// 数据权限不为空
					if (dataGroup[j].children.length > 0) {
						var dataAuthPoint = dataGroup[j].children;
						for (var k = 0; k < dataAuthPoint.length; k++) {
							var type = dataAuthPoint[k].menuUrl.split('==')[1].trim();
							var defaultClassName = k == 0 ? 'plan-select' : '';
							str += `<button type="button" class="layui-btn layui-btn-primary type-btn ${defaultClassName}" data-type="${type}" table-id="${tableId}"><i class="layui-icon"></i>${dataAuthPoint[k].authMenuName}</button>`;
						}
					}
					str += `</div>`;
				}
			}
		}
		$(".winui-toolbar").before(str);
	}
}

/***********************************ztree节点查找开始***************************************/

/**
 * 查找子结点，如果找到，返回true，否则返回false-----ztree查询时使用
 */
function searchChildren(keyword, children) {
	if(children == null || children.length == 0) {
		return false;
	}
	for(var i = 0; i < children.length; i++) {
		var node = children[i];
		if(node.name.indexOf(keyword) != -1) {
			return true;
		}
		//递归查找子结点
		var result = searchChildren(keyword, node.children);
		if(result) {
			return true;
		}
	}
	return false;
}

/**
 * 查找当前结点和父结点，如果找到，返回ture，否则返回false
 */
function searchParent(keyword, node) {
	if(node == null) {
		return false;
	}
	if(node.name.indexOf(keyword) != -1) {
		return true;
	}
	//递归查找父结点
	return searchParent(keyword, node.getParentNode());
}
/***********************************ztree节点查找结束***************************************/

/**
 * echarts简单图形参数
 * @param {} title		标题
 * @param {} subtext	描述
 * @param {} xNameData	x轴数据，数组类型
 * @param {} yTitle		y轴标题
 * @param {} yNameData	y轴数据，数组类型
 * @param {} type		图形展示类型。line：折线图；bar：柱形图
 * @return {}
 */
function getOption(title, subtext, xNameData, yTitle, yNameData, type){
	return {
		color: ['#3398DB', '#FFB6C1', '#C71585', '#8B008B', '#4169E1', '#00BFFF', '#008B8B'],
		title: {
			text: title,
			x: 'center',
			subtext: subtext
		},
		tooltip: {
			trigger: 'axis',
			axisPointer: {            			// 坐标轴指示器，坐标轴触发有效
				type: 'shadow'        			// 默认为直线，可选为：'line' | 'shadow'
			}
		},
		toolbox: {
			show: true,							//是否显示工具栏组件
			orient: "horizontal",				//工具栏 icon 的布局朝向'horizontal' 'vertical'
			itemSize: 15,						//工具栏 icon 的大小
			itemGap: 10,						//工具栏 icon 每项之间的间隔
			showTitle: true,					//是否在鼠标 hover 的时候显示每个工具 icon 的标题
			feature: {
				mark: {							//'辅助线开关'
					show: true
				},
				dataView : {					//数据视图工具，可以展现当前图表所用的数据，编辑后可以动态更新
					show: true,					//是否显示该工具。
					title: "数据视图",
					readOnly: false,			//是否不可编辑（只读）
					lang: ['数据视图', '关闭', '刷新'],	//数据视图上有三个话术，默认是['数据视图', '关闭', '刷新']
					backgroundColor: "#fff",	//数据视图浮层背景色。
					textareaColor: "#fff",		//数据视图浮层文本输入区背景色
					textareaBorderColor: "#333",//数据视图浮层文本输入区边框颜色
					textColor: "#000",			//文本颜色。
					buttonColor: "#c23531",		//按钮颜色。
					buttonTextColor: "#fff"		//按钮文本颜色。
				},
				magicType: {					//动态类型切换
					show: true,
					title: "切换",				//各个类型的标题文本，可以分别配置。
					type: ['line', 'bar']		//启用的动态类型，包括'line'（切换为折线图）, 'bar'（切换为柱状图）, 'stack'（切换为堆叠模式）, 'tiled'（切换为平铺模式）
				},
				restore: {						//配置项还原。
					show: true,					//是否显示该工具。
					title: "还原"
				},
				saveAsImage: {					//保存为图片。
					show: true,					//是否显示该工具。
					type: "png",				//保存的图片格式。支持 'png' 和 'jpeg'。
					name: "pic1",				//保存的文件名称，默认使用 title.text 作为名称
					backgroundColor: "#ffffff",	//保存的图片背景色，默认使用 backgroundColor，如果backgroundColor不存在的话会取白色
					title: "保存为图片",
					pixelRatio: 1				//保存图片的分辨率比例，默认跟容器相同大小，如果需要保存更高分辨率的，可以设置为大于 1 的值，例如 2
				},
				dataZoom: {						//数据区域缩放。目前只支持直角坐标系的缩放
					show: true,					//是否显示该工具。
					title: "缩放",				//缩放和还原的标题文本
					xAxisIndex: 0,				//指定哪些 xAxis 被控制。如果缺省则控制所有的x轴。如果设置为 false 则不控制任何x轴。如果设置成 3 则控制 axisIndex 为 3 的x轴。如果设置为 [0, 3] 则控制 axisIndex 为 0 和 3 的x轴
					yAxisIndex: false			//指定哪些 yAxis 被控制。如果缺省则控制所有的y轴。如果设置为 false 则不控制任何y轴。如果设置成 3 则控制 axisIndex 为 3 的y轴。如果设置为 [0, 3] 则控制 axisIndex 为 0 和 3 的y轴
				}
			}
		},
		xAxis: {
			type: 'category',
			axisLabel: {
				interval: 0,
				formatter: function(value) {
					var ret = ""; //拼接加\n返回的类目项  
					var maxLength = 4; //每项显示文字个数  
					var valLength = value.length; //X轴类目项的文字个数  
					var rowN = Math.ceil(valLength / maxLength); //类目项需要换行的行数  
					if(rowN > 1){ //如果类目项的文字大于3,  
						for(var i = 0; i < rowN; i++) {
							var temp = ""; //每次截取的字符串  
							var start = i * maxLength; //开始截取的位置  
							var end = start + maxLength; //结束截取的位置  
							//这里也可以加一个是否是最后一行的判断，但是不加也没有影响，那就不加吧  
							temp = value.substring(start, end) + "\n";
							ret += temp; //凭借最终的字符串  
						}
						return ret;
					} else {
						return value;
					}
				}
			},
			data: xNameData
		},
		yAxis: {
			type: 'value'
		},
		series: [{
			name: yTitle,
			type: isNull(type) ? 'line' : type,
			smooth: true,
			data: yNameData
		}]
	};
}

/**
 * echarts简单图形参数
 * @param {} title		标题
 * @param {} subtext	描述
 * @param {} data	数据
 * @return {}
 */
function getPieChatOption(title, subtext, data){
	return {
		title: {
			text: title,
			subtext: subtext,
			left: 'center'
		},
		tooltip: {
			trigger: 'item'
		},
		legend: {
			data:[],
			bottom: '5%',
			left: 'center'
		},
		series: [
			{
				name: '',
				type: 'pie',
				radius: '50%',
				data: data,
				emphasis: {
					itemStyle: {
						shadowBlur: 10,
						shadowOffsetX: 0,
						shadowColor: 'rgba(0, 0, 0, 0.5)'
					}
				}
			}
		]
	};
}

// 移除指定value值
function removeByValue(arr, val) {
	for (var i = 0; i < arr.length; i++) {
		if (arr[i] == val) {
			arr.splice(i, 1);
			break;
		}
	}
}

// 取出json串的键
function getOutKey(arr) {
	var jsonObj = $.parseJSON(arr);
	var a = [];
	var b = [];
	for (var i = 0; i < jsonObj.length; i++) {
		for (var key in jsonObj[i])
			a.push(key);
		b.push(a);
		a = [];
	}
	return b;
}

// B的子集是否是A的子集
function subset(A,B){
	for(var i = 0; i < B.length; i++){
		if(!isContained(B[i], A)){
			return false;
		}
	}
	return true;
}

// b是否被a包含,是返回true,不是返回false
isContained =(a, b)=>{
	if(!(a instanceof Array) || !(b instanceof Array))
		return false;
	if(a.length < b.length)
		return false;
	var aStr = a.toString();
	for(var i = 0, len = b.length; i < len; i++){
		if(aStr.indexOf(b[i]) == -1)
			return false;
	}
	return true;
}

/*****************工作计划模块开始**************/
// 获取计划周期名称
function getNowCheckTypeName(nowCheckType){
	if(nowCheckType === 'day')
		return '日计划';
	else if(nowCheckType === 'week')
		return '周计划';
	else if(nowCheckType === 'month')
		return '月计划';
	else if(nowCheckType === 'quarter')
		return '季度计划';
	else if(nowCheckType === 'halfyear')
		return '半年计划';
	else if(nowCheckType === 'year')
		return '年计划';
}
/*****************工作计划模块结束**************/

function matchingLanguage(){
	var list = layui.$("language");
	if(list.length > 0){
		$.each(list, function(i, item) {
			if(isNull($(item).html())){
				$(item).html(systemLanguage[$(item).attr("showName")][languageType]);
			}
		});
	}
	list = layui.$("[matchLanguage]");
	if(list.length > 0){
		$.each(list, function(i, item) {
			try{
				var jsonStr = $(item).attr("matchLanguage");
				if (!isNull(jsonStr)){
					jsonStr = jsonStr.replace(/\'/g,"\"");
					var _json = JSON.parse(jsonStr);
					$.each(_json, function(key, value){
						if(key === "html"){
							$(item).html(systemLanguage[value][languageType]);
						} else {
							$(item).attr(key, systemLanguage[value][languageType]);
						}
					});
					$(item).removeAttr("matchLanguage");
				}
			}catch(e){
				console.error(e);
			}
		});
	}
}

function initPasteDragImg(Editor) {
	var doc = document.getElementById(Editor.id)
	doc.addEventListener('paste', function(event) {
		var items = (event.clipboardData || window.clipboardData).items;
		var file = null;
		if(items && items.length) {
			// 搜索剪切板items
			for(var i = 0; i < items.length; i++) {
				if(items[i].type.indexOf('image') !== -1) {
					file = items[i].getAsFile();
					break;
				}
			}
		} else {
			winui.window.msg("当前浏览器不支持", {icon: 2, time: 2000});
			return;
		}
		if(!file) {
			return;
		}
		uploadImg(file, Editor);
	});

	var dashboard = document.getElementById(Editor.id)
	dashboard.addEventListener("dragover", function(e) {
		e.preventDefault()
		e.stopPropagation()
	})
	dashboard.addEventListener("dragenter", function(e) {
		e.preventDefault()
		e.stopPropagation()
	})
	dashboard.addEventListener("drop", function(e) {
		e.preventDefault()
		e.stopPropagation()
		var files = this.files || e.dataTransfer.files;
		uploadImg(files[0], Editor);
	})
}

function uploadImg(file, Editor) {
	var formData = new FormData();
	var fileName = new Date().getTime() + "." + file.name.split(".").pop();
	formData.append('editormd-image-file', file, fileName);
	$.ajax({
		url: Editor.settings.imageUploadURL,
		type: 'post',
		data: formData,
		processData: false,
		contentType: false,
		dataType: 'json',
		success: function(json) {
			if (json.returnCode == 0) {
				var url = json.bean.picUrl;
				var type = url.substr(url.lastIndexOf(".") + 1);
				if($.inArray(type, imageType) >= 0){
					Editor.insertValue("![图片alt](" + url + " ''图片title'')");
				} else {
					Editor.insertValue("[下载附件](" + url + ")");
				}
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}
	});
}

/**
 * 获取员工状态
 * @param d 员工参数
 * @returns {string}
 */
function getStaffStateName(d) {
	if(d.state == '1'){
		return "<span class='state-up'>在职</span>";
	} else if (d.state == '2'){
		return "<span class='state-down'>离职</span>";
	} else if (d.state == '3'){
		return "见习";
	} else if (d.state == '4'){
		return "试用";
	} else if (d.state == '5'){
		return "退休";
	}
}

/**
 * 判断一个值是否在指定的集合中存在
 *
 * @param array 集合
 * @param key 要比较的key
 * @param value 要比较的值
 * @returns {boolean}
 */
function judgeInPoingArr(array, key, value) {
	for(var i = 0; i < array.length; i++){
		if(array[i][key] == value){
			return true;
		}
	}
	return false;
}

/**
 * 获取一个值是否在指定的集合中匹配到的值的指定key
 *
 * @param array 集合
 * @param key 要比较的key
 * @param value 要比较的值
 * @param getKey 要获取的值
 * @returns {null|*}
 */
function getInPoingArr(array, key, value, getKey) {
	for(var i = 0; i < array.length; i++){
		if(array[i][key] == value){
			if(isNull(getKey)){
				return array[i];
			}
			return array[i][getKey];
		}
	}
	return null;
}

/**
 * 获取集合中一个值是否包含在指定的字符串中，如果包含，则返回
 *
 * @param array 集合
 * @param value 要比较的值
 * @returns {null|*}
 */
function getArrIndexOfPointStr(array, value) {
	for(var i = 0; i < array.length; i++){
		if(value.indexOf(array[i]) > -1){
			return array[i];
		}
	}
	return "";
}

/**
 * 获取员工信息
 *
 * @param staffId 员工id
 * @returns {string}
 */
function getUserStaffHtmlMationByStaffId(staffId){
	var html = "";
	var template = getFileContent('tpl/common/userStaff/userStaffMationShowTop.tpl');
	AjaxPostUtil.request({url: reqBasePath + "staff005", params: {rowId: staffId}, type: 'json', method: "GET", callback: function (json) {
		html = getDataUseHandlebars(template, json);
	}, async: false});
	return html;
}

/**
 * 字符串处理
 *
 * @type {{}}
 */
var stringManipulation = {
	textAreaShow: function(str){
		// IE7-8、IE9、FF、chrome。解决textarea中输入的文字，输出到div中文字不换自动换行的问题
		return str.replace(/\r\n/g, '<br/>').replace(/\n/g, '<br/>');
	}
};

////////////////////////////////////////考勤班次开始//////////////////////////////////////////
var checkWorkTimeColor = ['layui-bg-gray', 'layui-bg-blue', 'layui-bg-orange'];
// 类型为1初始化单休
function resetSingleBreak(id){
	var _box;
	if(isNull(id)){
		_box = $(".weekDay");
	} else {
		_box = $("#" + id + " .weekDay");
	}
	$.each(_box, function(i, item) {
		var clas = getArrIndexOfPointStr(checkWorkTimeColor, $(item).attr("class"));
		$(item).removeClass(clas);
		if(i < 6){
			$(item).addClass('layui-bg-blue');
		} else {
			$(item).addClass('layui-bg-gray');
		}
	});
}

// 类型为2初始化双休
function resetWeekend(id){
	var _box;
	if(isNull(id)){
		_box = $(".weekDay");
	} else {
		_box = $("#" + id + " .weekDay");
	}
	$.each(_box, function(i, item) {
		var clas = getArrIndexOfPointStr(checkWorkTimeColor, $(item).attr("class"));
		$(item).removeClass(clas);
		if(i < 5){
			$(item).addClass('layui-bg-blue');
		} else {
			$(item).addClass('layui-bg-gray');
		}
	});
}

// 类型为3初始化单双休
function resetSingleAndDoubleBreak(id){
	var _box;
	if(isNull(id)){
		_box = $(".weekDay");
	} else {
		_box = $("#" + id + " .weekDay");
	}
	$.each(_box, function(i, item) {
		var clas = getArrIndexOfPointStr(checkWorkTimeColor, $(item).attr("class"));
		$(item).removeClass(clas);
		if(i < 5){
			$(item).addClass('layui-bg-blue');
		} else if (i == 5){
			$(item).addClass('layui-bg-orange');
		} else {
			$(item).addClass('layui-bg-gray');
		}
	});
}

// 类型为4初始化自定休
function resetCustomizeDay(days, id){
	resetCustomize(id);
	$.each(days, function(i, item) {
		var _this = $("span[value='" + item.day + "']");
		if (!isNull(id)){
			_this = $("#" + id).find("span[value='" + item.day + "']");
		}
		var clas = getArrIndexOfPointStr(checkWorkTimeColor, _this.attr("class"));
		_this.removeClass(clas);
		if(item.type == 1){
			_this.addClass('layui-bg-blue');
		} else if (item.type == 2){
			_this.addClass('layui-bg-orange');
		}
	});
}

// 类型为4初始化自定休
function resetCustomize(id){
	var _box;
	if(isNull(id)){
		_box = $(".weekDay");
	} else {
		_box = $("#" + id + " .weekDay");
	}
	$.each(_box, function(i, item) {
		var clas = getArrIndexOfPointStr(checkWorkTimeColor, $(item).attr("class"));
		$(item).removeClass(clas);
		$(item).addClass('layui-bg-gray');
	});
}
////////////////////////////////////////考勤班次结束//////////////////////////////////////////

/**
 * 工作流审批状态显示颜色变更
 *
 * @param state 状态
 * @param stateName 状态中文显示
 * @returns {string}
 */
function getStateNameByState(state, stateName){
	if(state == '0'){
		stateName = "<span>" + stateName + "</span>";
	} else if (state == '1'){
		stateName = "<span class='state-new'>" + stateName + "</span>";
	} else if (state == '2'){
		stateName = "<span class='state-up'>" + stateName + "</span>";
	} else if (state == '3'){
		stateName = "<span class='state-down'>" + stateName + "</span>";
	} else if (state == '4'){
		stateName = "<span class='state-down'>" + stateName + "</span>";
	} else if (state == '5'){
		stateName = "<span class='state-error'>" + stateName + "</span>";
	}
	return stateName;
}

function returnModel(lang){
	var type = {
		'Java': 'text/x-java',
		'C/C++': 'text/x-c++src',
		'Objective-C': '',
		'Scala': 'text/x-scala',
		'Kotlin': 'text/x-kotlin',
		'Ceylon': 'text/x-ceylon',
		'xml': 'xml',
		'html': 'xml',
		'css': 'text/css',
		'htmlmixed': 'htmlmixed',
		'htmlhh': 'htmlmixed',
		'javascript': 'text/javascript',
		'nginx': 'text/x-nginx-conf',
		'solr': 'text/x-solr',
		'sql': 'text/x-sql',
		'vue': 'text/x-vue'
	};
	return type[lang];
}

function show(_object, url) {
	if (imageType.indexOf(url.substring(url.lastIndexOf(".") + 1).toLowerCase()) < 0) {
		window.open(url);
		return false;
	}

	var imgs = [];
	if(layui.$.isPlainObject(_object)){
		imgs = _object.find("input[type='hidden'][name='upload']").val().split(",");
	} else {
		imgs = layui.$(_object).find("input[type='hidden'][name='upload']").val().split(",");
	}
	showPicDisk(imgs);
}

/**
 * 展示图片,支持多张图片切换展示
 * @param {} imgs
 */
function showPicDisk(imgs){
	var data = [];
	layui.$.each(imgs, function (k, v) {
		var suffix = v.substring(v.lastIndexOf(".") + 1);
		if (imageType.indexOf(suffix.toLowerCase()) > -1) {
			var json = {
				"alt": "",
				"pid": k, //图片id
				"src": v, //原图地址
				"thumb": "" //缩略图地址
			}
			data.push(json);
		}
	})

	layer.photos({
		photos: {
			"title": "", //相册标题
			"id": 123, //相册id
			"start": 0, //初始显示的图片序号，默认0
			"data": data
		}, //格式见API文档手册页
		anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机
	});
}

/**
 * 获取随机值
 * @return {}
 */
function getRandomValueToString() {
	return Date.parse(new Date()) + "" + getRandom(999);
}

/**
 * 生成指定位数的随机整数
 * @param {} n
 * @return {}
 */
function getRandom(n) {
	return Math.floor(Math.random() * n + 1);
}

/**
 * 获取长度为len的随机字符串
 * @param len
 * @returns {String}
 */
function _getRandomString(len) {
	len = len || 32;
	var $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678'; // 默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1
	var maxPos = $chars.length;
	var pwd = '';
	for (i = 0; i < len; i++) {
		pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
	}
	return pwd;
}

/**
 * 全屏
 */
function fullScreen() {
	var docElm = document.documentElement;
	//W3C
	if(docElm.requestFullscreen) {
		docElm.requestFullscreen();
	}
	//FireFox
	else if(docElm.mozRequestFullScreen) {
		docElm.mozRequestFullScreen();
	}
	//Chrome等
	else if(docElm.webkitRequestFullScreen) {
		docElm.webkitRequestFullScreen();
	}
	//IE11
	else if(docElm.msRequestFullscreen) {
		document.body.msRequestFullscreen();
	}
}

/**
 * 禁用全屏
 */
function exitFullScreen(){
	if (document.fullscreenElement) {
		if(document.exitFullscreen) {
			document.exitFullscreen();
		} else if(document.mozCancelFullScreen) {
			document.mozCancelFullScreen();
		} else if(document.webkitCancelFullScreen) {
			document.webkitCancelFullScreen();
		} else if(document.msExitFullscreen) {
			document.msExitFullscreen();
		}
	}
}

/**
 * 判断是否是url
 * @param URL
 * @returns {Boolean}
 */
function checkURL(URL) {
	var reg= /(https?|http|ftp|file):\/\/[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]/g;
	URL = URL.match(reg);
	if(isNull(URL) || URL.length == 0){
		return false;
	} else {
		return true;
	}
}

/**
 * 获取 blob
 * @param  {String} url 目标文件地址
 * @return {cb}
 */
function getBlob(url,cb) {
	var xhr = new XMLHttpRequest();
	xhr.open('GET', url, true);
	xhr.responseType = 'blob';
	xhr.onload = function() {
		if (xhr.status === 200) {
			cb(xhr.response);
		}
	};
	xhr.send();
}

/**
 * 保存
 * @param  {Blob} blob
 * @param  {String} filename 想要保存的文件名称
 */
function saveAs(blob, filename) {
	if (window.navigator.msSaveOrOpenBlob) {
		navigator.msSaveBlob(blob, filename);
	} else {
		var link = document.createElement('a');
		var body = document.querySelector('body');
		link.href = window.URL.createObjectURL(blob);
		link.download = filename;
		// fix Firefox
		link.style.display = 'none';
		body.appendChild(link);
		link.click();
		body.removeChild(link);
		window.URL.revokeObjectURL(link.href);
	};
}

/**
 * 下载
 * @param  {String} url 目标文件地址
 * @param  {String} filename 想要保存的文件名称
 */
function download(url, filename) {
	getBlob(url, function(blob) {
		saveAs(blob, filename);
	});
};

//下载图片
function downloadImage(path, imgName) {
	var _OBJECT_URL;
	var request = new XMLHttpRequest();
	request.addEventListener('readystatechange', function (e) {
		if (request.readyState == 4) {
			_OBJECT_URL = URL.createObjectURL(request.response);
			var $a = $("<a></a>").attr("href", _OBJECT_URL).attr("download", imgName);
			$a[0].click();
		}
	});
	request.responseType = 'blob';
	request.open('get', path);
	request.send();
}

// 判断是否是json
function isJsonFormat(str) {
	try {
		layui.$.parseJSON(str);
	} catch(e) {
		return false;
	}
	return true;
}

/**
 * 判断str在strs中是否存在，strs的数据格式为'folder-item,select'
 * @param strs
 * @param str
 */
function judgeStrInStrs(strs, str){
	if (!isNull(strs) && !isNull(str)){
		var ss = strs.split(',');
		var strIndex = -1;
		layui.$.each(ss, function(i, item) {
			if(str === item) {
				strIndex = i;
				return false;
			}
		});
		return strIndex;
	}
	return 0;
}

/**
 * 对象数组根据某个字段进行排序
 * @param order 'desc':'降序', 'asc':'升序'
 * @param sortBy 排序字段
 */
function getSortFun(order, sortBy) {
	var ordAlpah = (order == 'asc') ? '>' : '<';
	var sortFun = new Function('a', 'b', 'return a.' + sortBy + ordAlpah + 'b.' + sortBy + '?1:-1');
	return sortFun;
}

/**
 * 判断字符串是否是json字符串
 * @param str
 */
function isJSON(str) {
	if (typeof str == 'string') {
		try {
			var obj = JSON.parse(str);
			if(typeof obj == 'object' && obj ){
				return true;
			} else {
				return false;
			}
		} catch(e) {
			return false;
		}
	} else {
		return false;
	}
}

/**
 * 取出字符串str中以startCode开始，以endCode结束的所有字符串
 * @param str
 * @param startCode
 * @param endCode
 */
function strMatchAllByTwo(str, startCode, endCode){
	if(str.length > 0){
		var arr = [];
		var firstStart = str.indexOf(startCode);
		var firstEnd = str.indexOf(endCode);
		arr.push(str.substr(firstStart + startCode.length, firstEnd - firstStart - endCode.length));
		return arr.concat(strMatchAllByTwo(str.substr(firstEnd + endCode.length), startCode, endCode));
	} else {
		return [];
	}
}

