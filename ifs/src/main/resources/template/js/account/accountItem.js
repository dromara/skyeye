
var rowId = "";

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
        url: reqBasePath + 'account008',
        where: {rowId:parent.rowId},
        even: true,
        page: true,
        limits: [8, 16, 24, 32, 40, 48, 56],
        limit: 8,
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'defaultNumber', title: '单据编号', align: 'left', width: 250, templet: function(d){
		        var str = '<a lay-event="details" class="notice-title-click">' + d.defaultNumber + '</a>';
		        if(!isNull(d.linkNumber)){
		        	str += '<span class="state-new">[转]</span>';
			        if(d.status == 2){
			        	str += '<span class="state-up"> [正常]</span>';
			        }else{
			        	str += '<span class="state-down"> [预警]</span>';
			        }
		        }
		        return str;
		    }},
            { field: 'subTypeName', title: '单据类型', align: 'left',width: 100},
            { field: 'type', title: '类型', align: 'center',width: 80, templet: function (d) {
                if(d.type == 1){
                    return "<span class='state-down'>出库</span>";
                }else if(d.type == 2){
                    return "<span class='state-up'>入库</span>";
                }else if(d.type == 3){
                    return "<span class='state-up'>其他</span>";
                }else{
                    return "<span class='state-error'>参数错误</span>";
                }
            }},
            { field: 'totalPrice', title: '合计金额', align: 'left',width: 100},
            { field: 'payType', title: '付款类型', align: 'center',width: 80, templet: function (d) {
                if(d.payType == 1){
                    return "<span class='state-up'>现金</span>";
                }else if(d.payType == 2){
                    return "<span class='state-down'>记账</span>";
                }else{
                    return "<span class='state-error'>其他</span>";
                }
            }},
            { field: 'operTime', title: '单据日期', align: 'center', width: 180 }
        ]],
	    done: function(){
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
	function details(data){
		rowId = data.id;
		var url = erpOrderUtil.getErpDetailUrl({subType: data.subType});
		_openNewWindows({
			url: url, 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "warehousingdetailschildpage",
			area: ['100vw', '100vh'],
			callBack: function(refreshCode){
			}});
	}
    
    exports('accountItem', {});
});
