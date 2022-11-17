
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool', 'tableTreeDj'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
			textool = layui.textool,
			tableTree = layui.tableTreeDj;
		textool.init({eleId: 'remark', maxlength: 200});

		skyeyeClassEnumUtil.showEnumDataListByClassName("teamObjectType", 'select', "objectType", '', form);
		skyeyeClassEnumUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", '', form);

		loadTreeTable();
		function loadTreeTable() {
			tableTree.render({
				id: 'messageTable',
				elem: '#messageTable',
				data: [{"id": "123qwe", "name": "阿萨斯多", "pId": "0", "lay_is_open": true},
					   {"id": "123qwe1", "name": "阿萨斯多qqq", "pId": "123qwe", "lay_is_open": true}],
				cols: [[
					{ field: 'name', title: '菜单名称', width: 120 },
					{ field: 'menuNameEn', title: '英文名称', width: 150 },
					{ field: 'orderNum', title: '排序', align: 'center', width: 80 },
					{ field: 'desktopName', title: '所属桌面', width: 120 },
					{ field: 'menuParentName', title: '父菜单', width: 100 },
					{ field: 'menuUrl', title: '菜单链接', width: 160 }
				]],
				done: function(json) {
					matchingLanguage();
				}
			}, {
				keyId: 'id',
				keyPid: 'pId',
				title: 'name',
				defaultShow: true,
			});
		}


		loadAuthList();
		form.on('select(objectType)', function(data) {
			loadAuthList();
		});

	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
					dictName: $("#dictName").val(),
					dictCode: $("#dictCode").val(),
					enabled: $("#enabled input:radio:checked").val(),
					remark: $("#remark").val(),
 	        	};
 	        	AjaxPostUtil.request({url: reqBasePath + "writeDictTypeMation", params: params, type: 'json', method: "POST", callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
 	        	}});
 	        }
 	        return false;
 	    });

		 function loadAuthList() {
			 var objectType = $('#objectType').val();
			 teamObjectPermissionUtil.insertPageShow(objectType, 'authList', form);
		 }
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});