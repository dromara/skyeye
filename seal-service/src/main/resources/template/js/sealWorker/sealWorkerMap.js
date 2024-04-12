
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var $ = layui.$;
	// 人员分布图
	var userList = new Array();
	
    AjaxPostUtil.request({url: sysMainMation.sealServiceBasePath + "queryAllSealWorkerList", params: {}, type: 'json', method: 'GET', callback: function (json) {
		userList = json.rows;
		AMapLoader.load({
			"key": sysMainMation.skyeyeMapKey,
			"version": "2.0",
			"plugins": ['AMap.Scale', 'AMap.ToolBar', 'AMap.Geocoder', 'AMap.Geolocation', 'AMap.CitySearch', 'AMap.PlaceSearch', 'AMap.DistrictSearch',
				'AMap.Bounds'],
		}).then((AMap) => {
			var map = new AMap.Map('container', {
				resizeEnable: true,
				center: [json.rows.length > 0 ? json.rows[0].longitude : '116.397428', json.rows.length > 0 ? json.rows[0].latitude : '39.90923'],
				zoom: 13
			});

			map.clearMap(); // 清除地图覆盖物

			$.each(json.rows, function(i, item) {
				let userMation = isNull(item.userMation) ? {} : item.userMation
				new AMap.Marker({
					map: map,
					content: getIconContent(item.id, userMation.userName, userMation.userPhoto),
					position: [item.longitude, item.latitude],
					offset: new AMap.Pixel(-13, -30)
				});
			});

			var center = map.getCenter();

			var centerText = '工人总数量：' + json.rows.length;
			document.getElementById('centerCoord').innerHTML = centerText;

			// 添加事件监听, 使地图自适应显示到合适的范围
			AMap.event.addDomListener(document.getElementById('setFitView'), 'click', function() {
				var newCenter = map.setFitView();
			});
		}).catch((e) => {
			console.error(e);
		});
		matchingLanguage();
	}});
	
	$("body").on("click", ".custom-content-marker", function (e) {
		var rowId = $(this).attr('rowid');
		$.each(userList, function(i, item) {
			if(rowId === item.id) {
				let userMation = isNull(item.userMation) ? {} : item.userMation
				document.getElementById('tips').innerHTML = '员工姓名：' + userMation.name + '<br>'
															+ '详细地址：' + item.absoluteAddress;
				return false;
			}
		});
	});
	
	function getIconContent(id, name, icon){
		return '<div class="custom-content-marker" rowid="' + id + '">' +
					'<font>' + name + '</font>' + 
		        	'<img src="' + icon + '">' +
		        	'<i class="fa fa-map-marker"></i>' +
		        '</div>';
	}
	
    exports('sealWorkerMap', {});
});