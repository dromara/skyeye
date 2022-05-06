
var userList = new Array();//选择用户返回的集合或者进行回显的集合
var employeeuserList = new Array();//选择用户返回的集合或者进行回显的集合
var userReturnList = new Array();//选择用户返回的集合或者进行回显的集合
var chooseOrNotMy = "1";//人员列表中是否包含自己--1.包含；其他参数不包含
var chooseOrNotEmail = "2";//人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
var checkType = "2";//人员选择类型，1.多选；其他。单选

// 资产信息
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload', 'tagEditor', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate;

	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "asset004",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/assetManage/assetManageEditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		hdb.registerHelper("compare1", function(v1, options){
					if(isNull(v1)){
						return path + "assets/img/uploadPic.png";
					}else{
						return basePath + v1;
					}
				});
		 	},
		 	ajaxSendAfter:function(json){
		 		//生产日期
		 		laydate.render({ 
		 		  elem: '#manufacturerTime'
		 		  ,type: 'date'
		 	 	  ,max: getFormatDate(),// 设置最大可选的日期
		 	 	  trigger: 'click'
		 		});
		 		
		 		//采购日期
		 		laydate.render({ 
		 		  elem: '#purchaseTime'
		 		  ,type: 'date'
		 	 	  ,max: getFormatDate(),// 设置最大可选的日期
		 	 	  trigger: 'click'
		 		});
		 		
		 		//资产类型
		 		showGrid({
				 	id: "typeId",
				 	url: flowableBasePath + "assettype006",
				 	params: {},
				 	pagination: false,
				 	template: getFileContent('tpl/template/select-option-must.tpl'),
				 	ajaxSendLoadBefore: function(hdb){
				 	},
				 	ajaxSendAfter:function(j){
				 		$("#typeId").val(json.bean.typeId);
				 		form.render('select');
				 	}
				});
		 		
		 		//资产来源
		 		showGrid({
				 	id: "fromId",
				 	url: flowableBasePath + "assetfrom006",
				 	params: {},
				 	pagination: false,
				 	template: getFileContent('tpl/template/select-option-must.tpl'),
				 	ajaxSendLoadBefore: function(hdb){
				 	},
				 	ajaxSendAfter:function(j){
				 		$("#fromId").val(json.bean.fromId);
				 		form.render('select');
				 	}
				});
		 		
		 		//初始化上传
		 		$("#assetImg").upload({
		            "action": reqBasePath + "common003",
		            "data-num": "1",
		            "data-type": "PNG,JPG,jpeg,gif",
		            "uploadType": 6,
		            "data-value": json.bean.assetImg,
		            //该函数为点击放大镜的回调函数，如没有该函数，则不显示放大镜
		            "function": function (_this, data) {
		                show("#assetImg", data);
		            }
		        });
		 		var userNames = [];
		 		userList = [].concat(json.bean.assetAdmin);
		 		$.each(json.bean.assetAdmin, function(i, item){
		 			userNames.push(item.name);
		 		});
		 		//管理人员选择
				$('#assetAdmin').tagEditor({
			        initialTags: userNames,
			        placeholder: '请选择资产管理人',
					editorTag: false,
			        beforeTagDelete: function(field, editor, tags, val) {
			        	var inArray = -1;
				    	$.each(userList, function(i, item) {
				    		if(val === item.name) {
				    			inArray = i;
				    			return false;
				    		}
				    	});
				    	if(inArray != -1) { //如果该元素在集合中存在
				    		userList.splice(inArray, 1);
				    	}
			        }
			    });
				
				var employeeuserNames = [];
		 		employeeuserList = [].concat(json.bean.employeeId);
		 		$.each(json.bean.employeeId, function(i, item){
		 			employeeuserNames.push(item.name);
		 		});
				//领用人选择
				$('#employeeId').tagEditor({
			        initialTags: employeeuserNames,
			        placeholder: '请选择领用人',
					editorTag: false,
			        beforeTagDelete: function(field, editor, tags, val) {
			        	var inArray = -1;
				    	$.each(employeeuserList, function(i, item) {
				    		if(val === item.name) {
				    			inArray = i;
				    			return false;
				    		}
				    	});
				    	if(inArray != -1) { //如果该元素在集合中存在
				    		employeeuserList.splice(inArray, 1);
				    	}
			        }
			    });

				// 附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

    			matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
	 	        			rowId: parent.rowId,
	 	        			assetName: $("#assetName").val(),
	 	        			assetNum: $("#assetNum").val(),
	 	        			specifications: $("#specifications").val(),
	 	        			unitPrice: $("#unitPrice").val(),
	 	        			manufacturer: $("#manufacturer").val(),
	 	        			manufacturerTime: $("#manufacturerTime").val(),
	 	        			supplier: $("#supplier").val(),
	 	        			purchaseTime: $("#purchaseTime").val(),
	 	        			storageArea: $("#storageArea").val(),
	 	        			roomAddDesc: $("#roomAddDesc").val(),
							typeId: $("#typeId").val(),
							fromId: $("#fromId").val(),
							enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
	 	 	        	};
	 	 	        	params.assetImg = $("#assetImg").find("input[type='hidden'][name='upload']").attr("oldurl");
	 	 	        	if(isNull(params.assetImg)){
	 	 	        		winui.window.msg('请上传资产图片', {icon: 2,time: 2000});
	 	 	        		return false;
	 	 	        	}
	 	 	        	if(userList.length == 0 || isNull($('#assetAdmin').tagEditor('getTags')[0].tags)){
	 	 	        		params.assetAdmin = "";
	 	 	        	}else{
	 	        			params.assetAdmin = userList[0].id;
	 	        		}
	 	 	        	if(employeeuserList.length == 0 || isNull($('#employeeId').tagEditor('getTags')[0].tags)){
	 	 	        		params.employeeId = "";
	 	 	        	}else{
	 	        			params.employeeId = employeeuserList[0].id;
	 	        		}
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "asset005", params:params, type: 'json', callback: function(json){
	 		 	   			if(json.returnCode == 0){
	 			 	   			parent.layer.close(index);
	 			 	        	parent.refreshCode = '0';
	 		 	   			}else{
	 		 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 		 	   			}
	 		 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
	    
	    //管理人员选择
		$("body").on("click", "#userNameSelPeople", function(e){
			userReturnList = [].concat(userList);
			_openNewWindows({
				url: "../../tpl/common/sysusersel.html", 
				title: "人员选择",
				pageId: "sysuserselpage",
				area: ['80vw', '80vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						//移除所有tag
						var tags = $('#assetAdmin').tagEditor('getTags')[0].tags;
						for (i = 0; i < tags.length; i++) { 
							$('#assetAdmin').tagEditor('removeTag', tags[i]);
						}
						userList = [].concat(userReturnList);
					    //添加新的tag
						$.each(userList, function(i, item){
							$('#assetAdmin').tagEditor('addTag', item.name);
						});
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
	                }
				}});
		});
		
		//领用人选择
		$("body").on("click", "#employeePeople", function(e){
			userReturnList = [].concat(employeeuserList);
			_openNewWindows({
				url: "../../tpl/common/sysusersel.html", 
				title: "人员选择",
				pageId: "sysuserselpage",
				area: ['80vw', '80vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						//移除所有tag
						var tags = $('#employeeId').tagEditor('getTags')[0].tags;
						for (i = 0; i < tags.length; i++) { 
							$('#employeeId').tagEditor('removeTag', tags[i]);
						}
						employeeuserList = [].concat(userReturnList);
					    //添加新的tag
						$.each(employeeuserList, function(i, item){
							$('#employeeId').tagEditor('addTag', item.name);
						});
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
	                }
				}});
		});
	    
	    $("body").on("click", ".enclosureItem", function(){
	    	download(fileBasePath + $(this).attr("rowpath"), $(this).html());
	    });
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});