
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		table = layui.table;

	table.render({
	    id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'material012',
        where: {mUnitId: parent.mUnitId},
        even: true,
        page: true,
        limits: [8, 16, 24, 32, 40, 48, 56],
        limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'defaultNumber', title: '单据编号', align: 'left', width: 250, templet: function (d) {
		        var str = '<a lay-event="details" class="notice-title-click">' + d.defaultNumber + '</a>';
		        if (!isNull(d.linkNumber)){
		        	str += '<span class="state-new">[转]</span>';
			        if(d.status == 2){
			        	str += '<span class="state-up"> [正常]</span>';
			        } else {
			        	str += '<span class="state-down"> [预警]</span>';
			        }
		        }
		        return str;
		    }},
		    { field: 'subTypeName', title: '单据类型', align: 'left', width: 100},
	        { field: 'unitPrice', title: '单价(元)', align: 'left', width: 100 },
	        { field: 'operNumber', title: '数量', align: 'left', width: 80, templet: function (d) {
	        	if(d.subType == 12){//拆分单
	        		if(d.mType == 2){//普通子件
	        			return d.operNumber;
	        		} else {
	        			return "-" + d.operNumber;
	        		}
	        	} else if (d.subType == 13){//组装单
	        		if(d.mType == 1){//普通子件
	        			return d.operNumber;
	        		} else {
	        			return "-" + d.operNumber;
	        		}
	        	} else {
			        if(d.type == 2){
			        	return d.operNumber;
			        } else {
			        	return "-" + d.operNumber;
			        }
	        	}
		    }},
	        { field: 'taxLastMoney', title: '含税合计(元)', align: 'left', width: 100 },
	        { field: 'operTime', title: '单据日期', align: 'center', width: 140 }
	    ]],
	    done: function(json) {
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { //详情
        	details(data);
        }
    });
    
    // 详情
	function details(data) {
		rowId = data.id;
		var url = erpOrderUtil.getErpDetailUrl(data);
		_openNewWindows({
			url: url, 
			title: "单据详情",
			pageId: "orderMationDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
    exports('materialStockList', {});
    
});
