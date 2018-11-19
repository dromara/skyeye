

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'upload'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	upload = layui.upload;
	
	//初始化数据
    showGrid({
	 	id: "showForm",
	 	url: reqBasePath + "sysevewinlockbgpic001",
	 	params: {},
	 	pagination: true,
	 	pagesize: 18,
	 	template: getFileContent('tpl/sysevewinbgpic/bgpic-item.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 		hdb.registerHelper("compare1", function(v1, options){
				return fileBasePath + v1;
			});
	 	},
	 	options: {'click .del':function(index, row){
				layer.confirm('确认删除选中数据吗？', { icon: 3, title: '删除win系统锁屏桌面图片' }, function (index) {
					layer.close(index);
		            AjaxPostUtil.request({url:reqBasePath + "sysevewinlockbgpic003", params:{rowId: row.id}, type:'json', callback:function(json){
		    			if(json.returnCode == 0){
		    				top.winui.window.msg("删除成功", {icon: 1,time: 2000});
		    				loadTable();
		    			}else{
		    				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		    			}
		    		}});
				});
	 		}, 'click .sel':function(index, row){
	 			layer.open({
	        		type:1,
	        		title:false,
	        		closeBtn:0,
	        		skin: 'demo-class',
	        		shadeClose:true,
	        		content:'<img src="' + fileBasePath + data.picUrl + '" style="max-height:600px;max-width:100%;">',
	        		scrollbar:false
	            });
	 		}
	 	},
	 	ajaxSendAfter:function(json){
	 	}
    });
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
	var uploadInst = upload.render({
		elem: '#addBean', // 绑定元素
		url: reqBasePath + 'common003', // 上传接口
		data: {type: 3},
		done: function(json) {
			// 上传完毕回调
			if(json.returnCode == 0){
				AjaxPostUtil.request({url:reqBasePath + "sysevewinlockbgpic002", params:{picUrl: json.bean.picUrl}, type:'json', callback:function(json){
	    			if(json.returnCode == 0){
	    				top.winui.window.msg("上传成功", {icon: 1,time: 2000});
	    				loadTable();
	    			}else{
	    				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	    			}
	    		}});
			}else{
				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		},
		error: function(e) {
			// 请求异常回调
			console.log(e);
		}
	});
    
    function loadTable(){
    	refreshGrid("showForm", {params:{}});
    }
    
    exports('sysevewinbgpiclist', {});
});
