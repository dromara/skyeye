
var rowId = "";

var staffId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'soulTable'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		soulTable = layui.soulTable;

	// 获取当前登录员工信息
	systemCommonUtil.getSysCurrentLoginUserMation(function (data) {
		staffId = data.bean.staffId;
	});
	initTable();
    function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: sysMainMation.ehrBasePath + 'sysstaffrewardpunish006',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
	    	overflow: {
	            type: 'tips',
	            header: true,
	            total: true
	        },
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
		        { field: 'name', title: '名称', align: 'left', width: 160 },
		        { field: 'price', title: '奖惩金额', align: 'left', width: 100},
		        { field: 'content', title: '奖惩内容', align: 'left', width: 160},
                { field: 'rewardPunishTypeName', title: '奖惩分类', align: 'left', width: 100 },
                { field: 'rewardPunishTime', title: '奖惩时间', align: 'center', width: 100},
                { field: 'awardUnit', title: '授予单位', align: 'left', width: 120},
                { field: 'desc', title: '备注', align: 'left', width: 160 },
                { field: 'jobNumber', title: '员工工号', align: 'left', width: 80 },
                { field: 'userName', title: '员工姓名', align: 'left', width: 100 },
                { field: 'state', title: '员工状态', align: 'center', width: 80, templet: function (d) {
                    if(d.state == '1'){
                        return "在职";
                    } else if (d.state == '2'){
                        return "离职";
                    } else if (d.state == '3'){
                        return "见习";
                    } else if (d.state == '4'){
                        return "试用";
                    } else if (d.state == '5'){
                        return "退休";
                    }
                }},
                { field: 'createTime', title: systemLanguage["com.skyeye.entryTime"][languageType], align: 'center', width: 100}
		    ]],
		    done: function(json) {
		    	matchingLanguage();
		    	soulTable.render(this);
		    }
		});
    }
	
	form.render();
	
	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });

    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
    	return {
    		staffId: staffId
    	};
	}
    
    exports('sysStaffRewardPunishMyList', {});
});
