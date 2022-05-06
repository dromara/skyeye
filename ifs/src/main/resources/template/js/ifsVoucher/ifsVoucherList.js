
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'upload'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		upload = layui.upload;
	
	authBtn('1641208147247');
	
    showGrid({
	 	id: "showForm",
	 	url: flowableBasePath + "ifsVoucher001",
	 	params: getTableParams(),
	 	pagination: true,
	 	pagesize: 18,
	 	template: $("#beanTemplate").html(),
	 	ajaxSendLoadBefore: function(hdb){
	 		hdb.registerHelper("compare1", function(v1, options){
	 			var fileExt = sysFileUtil.getFileExt(v1);
	 			if($.inArray(fileExt[0], imageType) >= 0){
					return fileBasePath + v1;
				}
	 			return '../../assets/images/doc.png';
			});
			hdb.registerHelper("compare2", function(v1, options){
				if(v1 == 1){
					return '<a class="layui-btn layui-btn-danger layui-btn-xs del" auth="1641208155066"><language showName="com.skyeye.deleteBtn"></language></a>';
				}
				return '';
			});
			hdb.registerHelper("compare3", function(v1, options){
				if(v1 == 1){
					return "<span class='state-down'>未整理</span>";
				}
				return "<span class='state-up'>已整理</span>";
			});
	 	},
	 	options: {'click .del':function(index, row){
				layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
					layer.close(index);
		            AjaxPostUtil.request({url: flowableBasePath + "ifsVoucher003", params: {rowId: row.id}, type: 'json', method: "DELETE", callback: function(json){
		    			if(json.returnCode == 0){
		    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
		    				loadTable();
		    			}else{
		    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		    			}
		    		}});
				});
	 		}, 'click .sel':function(index, row){
				var fileExt = sysFileUtil.getFileExt(row.voucherPath);
				if($.inArray(fileExt[0], imageType) >= 0){
					systemCommonUtil.showPicImg(fileBasePath + row.voucherPath);
				}else{
					sysFileUtil.download(row.voucherPath, row.fileName);
				}
	 		}
	 	},
	 	ajaxSendAfter:function(json){
	 		authBtn('1641208155066');
	 		matchingLanguage();
	 	}
    });

    var exts = imageType.concat(officeType).join('|');
    $("#showInfo").html("仅支持以下格式的凭证文件：【" + imageType.concat(officeType).join(', ') + "】");
	upload.render({
		elem: '#addBean', // 绑定元素
		url: reqBasePath + 'common003', // 上传接口
		data: {type: 21},
		exts: exts,
		done: function(json) {
			if(json.returnCode == 0){
				var param = {
					type: 1, // 凭证类型  1.原始凭证  2.手工录入凭证
					voucherPath: json.bean.picUrl,
					fileName: json.bean.fileName
				}
				AjaxPostUtil.request({url: flowableBasePath + "ifsVoucher002", params: param, type: 'json', method: "POST", callback: function(json){
	    			if(json.returnCode == 0){
	    				winui.window.msg("上传成功", {icon: 1, time: 2000});
	    				loadTable();
	    			}else{
	    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	    			}
	    		}});
			}else{
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		},
		error: function(e) {
			// 请求异常回调
			console.log(e);
		}
	});

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			refreshGrid("showForm", {params: getTableParams()});
		}
		return false;
	});

	// 刷新数据
	$("body").on("click", "#reloadTable", function(){
		loadTable();
	});
    
    function loadTable(){
    	refreshGrid("showForm", {params: getTableParams()});
    }

	function getTableParams(){
		return {
			state: $("#state").val(),
		};
	}
    
    exports('ifsVoucherList', {});
});
