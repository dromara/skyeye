var staffId = "";

// 员工详情id
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'tableSelect', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	tableSelect = layui.tableSelect,
			laydate = layui.laydate;

		var startTime = laydate.render({
			elem: '#startTime', //指定元素
			format: 'yyyy-MM-dd',
			min: minDate(),
			theme: 'grid',
			done:function(value, date){
				endTime.config.min = {
					year: date.year,
					month: date.month - 1,//关键
					date: date.date,
					hours: date.hours,
					minutes: date.minutes,
					seconds: date.seconds
				};
			}
		});

		var endTime = laydate.render({
			elem: '#endTime', //指定元素
			format: 'yyyy-MM-dd',
			min: minDate(),
			theme: 'grid',
			done:function(value, date){
				startTime.config.max = {
					year: date.year,
					month: date.month - 1,//关键
					date: date.date,
					hours: date.hours,
					minutes: date.minutes,
					seconds: date.seconds
				}
			}
		});
		// 设置最小可选的日期
		function minDate(){
			var now = new Date();
			return now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate();
		}
		skyeyeClassEnumUtil.showEnumDataListByClassName("userIsTermOfValidity", 'radio', "isTermOfValidity", '', form);

		$(".effectiveDate").hide();
		form.on('radio(isTermOfValidityFilter)', function (data) {
			let val = data.value;
			if (val == 1) {
				$(".effectiveDate").hide();
			} else if (val == 2) {
				$(".effectiveDate").show();
			}
		});

 		form.verify({
 			password : function(value, item) {
	            if(value.length < 6){
	                return "密码长度不能小于6位";
	            }
	        },
	        confirmPwd : function(value, item) {
	            if($("#password").val() != value){
	                return "两次输入密码不一致，请重新输入！";
	            }
	        }
	    });
 		
 		tableSelect.render({
 	    	elem: '#userName',	//定义输入框input对象
 	    	checkedKey: 'id', //表格的唯一键值，非常重要，影响到选中状态 必填
 	    	searchKey: 'userName',	//搜索输入框的name值 默认keyword
 	    	searchPlaceholder: '员工姓名搜索',	//搜索输入框的提示文字 默认关键词搜索
 	    	table: {	//定义表格参数，与LAYUI的TABLE模块一致，只是无需再定义表格elem
 	    		url: reqBasePath + 'sys034',
 	    		method: 'post',
 	    		page: true,
 	    	    limits: [8, 16, 24, 32, 40, 48, 56],
 	    	    limit: 8,
 	    		cols: [[
 	    		    { type: 'radio'},
 					{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
					{ field: 'jobNumber', title: '员工工号', width: 100, templet: function (d) {
						return '<a rowId="' + d.id + '" class="notice-title-click">' + d.jobNumber + '</a>';
					}},
 					{ field: 'userName', title: '员工姓名', width: 100 },
					{ field: 'userSex', title: '性别', width: 60, rowspan: '2', templet: function (d) {
						return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("sexEnum", 'id', d.userSex, 'name');
					}},
 				]]
 	    	},
 	    	done: function (elem, data) {
 	    		var newJson = data.data[0].userName;
 	    		var ids = data.data[0].id;
 	    		elem.val(newJson);
 	    		elem.attr('ts-selected', ids);
				staffId = ids;
 	    	}
 	    });

		// 员工详情
		$("body").on("click", ".notice-title-click", function() {
			rowId = $(this).attr("rowId");
			_openNewWindows({
				url: "../../tpl/sysEveUserStaff/sysEveUserStaffDetails.html",
				title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
				pageId: "sysEveUserStaffDetails",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
				}});
		});
 	    
	    matchingLanguage();
 		form.render();
 		form.on('submit(formAddBean)', function(data) {
			if (winui.verifyForm(data.elem)) {
				var params = {
					staffId: staffId,
					userCode: $("#userCode").val(),
					password: $("#password").val(),
					isTermOfValidity: dataShowType.getData('isTermOfValidity'),
					startTime: $("#startTime").val(),
					endTime: $("#endTime").val()
				}
				AjaxPostUtil.request({url: reqBasePath + "sysAdd005", params: params, type: 'json', method: "POST", callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
				}});
			}
			return false;
 		});
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});