
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	
	var $ = layui.$;
	
	var userList = new Array();
	
    AjaxPostUtil.request({url: flowableBasePath + "sealseserviceworker007", params: {}, type: 'json', callback: function(json){
		if (json.returnCode == 0) {
			userList = json.rows;
			var map = new AMap.Map('container', {
				resizeEnable: true,
				center: [json.rows.length > 0 ? json.rows[0].longitude : '116.397428', json.rows.length > 0 ? json.rows[0].latitude : '39.90923'],
				zoom: 13
			});
			
			map.clearMap(); // 清除地图覆盖物
			
			$.each(json.rows, function(i, item){
				new AMap.Marker({
					map: map,
					content: getIconContent(item.id, item.userName, item.userPhoto),
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
			
			matchingLanguage();
		}else{
			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		}
	}});
	
	$("body").on("click", ".custom-content-marker", function(e){
		var rowId = $(this).attr('rowid');
		$.each(userList, function(i, item){
			if(rowId === item.id){
				document.getElementById('tips').innerHTML = '员工姓名：' + item.userName + '<br>'
															+ '工单数：' + item.orderNumber + '<br>'
															+ '状态：' + item.stateName + '<br>'
															+ '详细地址：' + item.addDetail;
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
	
    exports('serviceworkermap', {});
});