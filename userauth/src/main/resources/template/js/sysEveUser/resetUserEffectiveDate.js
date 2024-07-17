
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate;
	var id = GetUrlParam("id");

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

	AjaxPostUtil.request({url: reqBasePath + "querySysEveUserById", params: {id: id}, type: 'json', method: "GET", callback: function (json) {
		skyeyeClassEnumUtil.showEnumDataListByClassName("userIsTermOfValidity", 'radio', "isTermOfValidity", json.bean.isTermOfValidity, form);
		if (json.bean.isTermOfValidity == 1) {
			$(".effectiveDate").hide();
		} else if (json.bean.isTermOfValidity == 2) {
			$(".effectiveDate").show();
			$("#startTime").val(json.bean.startTime);
			$("#endTime").val(json.bean.endTime);
		}
	}});

	form.on('radio(isTermOfValidityFilter)', function (data) {
		let val = data.value;
		if (val == 1) {
			$(".effectiveDate").hide();
		} else if (val == 2) {
			$(".effectiveDate").show();
		}
	});

	matchingLanguage();
	form.render();
	form.on('submit(resetUserEffectiveDate)', function(data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				id: id,
				isTermOfValidity: dataShowType.getData('isTermOfValidity'),
				startTime: $("#startTime").val(),
				endTime: $("#endTime").val()
			};
			AjaxPostUtil.request({url: reqBasePath + "resetUserEffectiveDate", params: params, type: 'json', method: "POST", callback: function (json) {
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