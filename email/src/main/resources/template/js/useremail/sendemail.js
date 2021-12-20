
var userReturnList = new Array();//选择用户返回的集合或者进行回显的集合
var chooseOrNotMy = "2";//人员列表中是否包含自己--1.包含；其他参数不包含
var chooseOrNotEmail = "1";//人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
var checkType = "1";//人员选择类型，1.多选；其他。单选

// 邮件模板
var emailSendModel = {};
// 如果邮件是从别的服务器同步来的，那么包含的附件可能在我们的附件库上没有，这个对象负责存储那些我们的附件库上没有的附件
var emailEnclosureList = new Array();

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit', 'tagEditor'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	layedit = layui.layedit;
	    var id = GetUrlParam("id");
	    var forwardId = GetUrlParam("forwardId");
		var enclosureList = [];
		var toPeopleList = [], toCcList = [], toBccList = [];

	    if(!isNull(id)){
	    	// 草稿箱获取数据
			AjaxPostUtil.request({url:reqBasePath + "useremail014", params:{rowId: id}, type:'json', callback:function(json){
				if(json.returnCode == 0){
					$("#typeName").val(json.bean.title);
					$("#content").val(json.bean.content);

					// 初始化收件人对象
					$.each(json.bean.toPeopleList, function(i, item){
						toPeopleList.push(item.email);
					});

					// 初始化抄送人对象
					$.each(json.bean.toCcList, function(i, item){
						toCcList.push(item.email);
					});

					// 初始化暗送人对象
					$.each(json.bean.toBccList, function(i, item){
						toBccList.push(item.email);
					});


					$.each(json.bean.emailEnclosureList, function(i, item){
						if(!isNull(item.sysEnclosureId)){
							enclosureList.push({
								id: item.sysEnclosureId,
								name: item.fileName,
								fileAddress: item.fileAddress
							});
						}else{
							emailEnclosureList.push({
								id: item.id,
								name: item.fileName,
								fileAddress: item.fileAddress
							});
						}
					});
				}else{
					winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				}
			}, async: false});
		}

		if(!isNull(forwardId)){
			// 邮件回复获取数据
			AjaxPostUtil.request({url:reqBasePath + "useremail017", params:{rowId: forwardId}, type:'json', callback:function(json){
				if(json.returnCode == 0){
					$("#typeName").val(json.bean.title);
					var emailContentHeadStr = getContentHead(json.bean.fromPeople, json.bean.sendDate, json.bean.toPeople, json.bean.toCc, json.bean.toBcc, json.bean.title);
					$("#content").val(emailContentHeadStr + json.bean.content);

					// 初始化收件人对象
					$.each(json.bean.toPeopleList, function(i, item){
						toPeopleList.push(item.email);
					});

					// 初始化抄送人对象
					$.each(json.bean.toCcList, function(i, item){
						toCcList.push(item.email);
					});

					// 初始化暗送人对象
					$.each(json.bean.toBccList, function(i, item){
						toBccList.push(item.email);
					});


					$.each(json.bean.emailEnclosureList, function(i, item){
						if(!isNull(item.sysEnclosureId)){
							enclosureList.push({
								id: item.sysEnclosureId,
								name: item.fileName,
								fileAddress: item.fileAddress
							});
						}else{
							emailEnclosureList.push({
								id: item.id,
								name: item.fileName,
								fileAddress: item.fileAddress
							});
						}
					});
				}else{
					winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				}
			}, async: false});
		}
	    
	    layedit.set({
	    	uploadImage: {
	    		url: reqBasePath + "common003", //接口url
    			type: 'post', //默认post
    			data: {
    				type: '13'
    			}
	    	}
	    });
	    
	    var layContent = layedit.build('content', {
	    	tool: [
    	       'html'
    	       ,'strong' //加粗
    	       ,'italic' //斜体
    	       ,'underline' //下划线
    	       ,'del' //删除线
    	       ,'addhr'
    	       ,'|'
    	       ,'removeformat'
    	       ,'fontFomatt'
    	       ,'fontfamily'
    	       ,'fontSize'
    	       ,'colorpicker'
    	       ,'fontBackColor'
    	       ,'face' //表情
    	       ,'|' //分割线
    	       ,'left' //左对齐
    	       ,'center' //居中对齐
    	       ,'right' //右对齐
    	       ,'link' //超链接
    	       ,'unlink' //清除链接
    	       ,'code'
    	       ,'image' //插入图片
    	       ,'attachment'
    	       ,'table'
    	       ,'|'
    	       ,'fullScreen'
    	       ,'preview'
    	       ,'|'
    	       ,'help'
    	     ],
    	     uploadFiles: {
    	 		url: reqBasePath + "common003",
    	 		accept: 'file',
    	 		acceptMime: 'file/*',
    	 		size: '20480',
    	 		data: {
    				type: '13'
    			},
    	 		autoInsert: true, //自动插入编辑器设置
    	 		done: function(data) {
    	 		}
    	 	}
	    });

		$('#toPeople').tagEditor({
			initialTags: toPeopleList,
			placeholder: '填写完成后直接回车即可'
		});

		$('#toCc').tagEditor({
			initialTags: toCcList,
			placeholder: '填写完成后直接回车即可'
		});

		$('#toBcc').tagEditor({
			initialTags: toBccList,
			placeholder: '填写完成后直接回车即可'
		});

		skyeyeEnclosure.initTypeISData({'enclosureUpload': enclosureList});
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	    	if(isNull(id) && isNull(forwardId)){
				save(data, "useremail013");
			}
 	    	if(!isNull(id)){
				// 草稿箱保存
				save(data, "useremail015");
			}
			if(!isNull(forwardId)){
				// 转发保存
				save(data, "useremail015");
			}
 	        return false;
 	    });
 	    
 		form.on('submit(formSendBean)', function (data) {
			if(isNull(id) && isNull(forwardId)){
				save(data, "useremail012");
			}
			if(!isNull(id)){
				// 草稿箱发送
				save(data, "useremail016");
			}
			if(!isNull(forwardId)){
				// 转发发送
				save(data, "useremail019");
			}
		   	return false;
	    });

 	   	function save(data, url){
		   	if (winui.verifyForm(data.elem)) {
			   	if(isNull($('#toPeople').tagEditor('getTags')[0].tags)){
				   	winui.window.msg('请填写收件人', {icon: 2,time: 2000});
				   	return false;
			   	}
			   	if(isNull(layedit.getContent(layContent))){
				   	winui.window.msg('请填写邮件内容', {icon: 2,time: 2000});
				   	return false;
			   	}
			   	var typeName = "(无主题)";
			   	if(!isNull($("#typeName").val())){
				   	typeName = $("#typeName").val();
			   	}

			   	var params = {
				   	typeName: typeName,
				   	content: encodeURIComponent(layedit.getContent(layContent)),
				   	toPeople: $('#toPeople').tagEditor('getTags')[0].tags,
				   	toCc: $('#toCc').tagEditor('getTags')[0].tags,
				   	toBcc: $('#toBcc').tagEditor('getTags')[0].tags,
				   	emailEnclosure: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
				   	emailId: parent.$("#checkEmail").attr('rowid'),
					rowId: id,
					emailEnclosureList: JSON.stringify(emailEnclosureList)
			   	};
			   	AjaxPostUtil.request({url: reqBasePath + url, params: params, type:'json', callback:function(json){
				   	if(json.returnCode == 0){
				   		if(url == "useremail013" || url == "useremail015"){
							winui.window.msg(systemLanguage["com.skyeye.addOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
						}else{
							winui.window.msg('发送成功', {icon: 1,time: 2000});
							location.href = "../../tpl/useremail/sendsuccess.html";
						}
				   	}else{
					   	winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				   	}
			   	}});
		   	}
	   	}
 	    
	    // 人员选择
		$("body").on("click", "#toPeopleSelPeople, #toCcSelPeople, #toBccSelPeople", function(e){
			userReturnList = [];
			var clickId = $(this).attr("id");
			systemCommonUtil.openSysUserStaffChoosePage(function (staffChooseList){
				// 添加新的tag
				$.each(staffChooseList, function(i, item){
					if(clickId == 'toPeopleSelPeople'){
						$('#toPeople').tagEditor('addTag', item.email);
					}else if(clickId == 'toCcSelPeople'){
						$('#toCc').tagEditor('addTag', item.email);
					}else if(clickId == 'toBccSelPeople'){
						$('#toBcc').tagEditor('addTag', item.email);
					}
				});
			});
		});

		$("body").on("click", "#toPeopleSelMail, #toCcSelMail, #toBccSelMail", function(e){
			var clickId = $(this).attr("id");
			mailUtil.openMailChoosePage(function (mailChooseList){
				$.each(mailChooseList, function(i, item){
					if(clickId == 'toPeopleSelMail'){
						$('#toPeople').tagEditor('addTag', item.email);
					}else if(clickId == 'toCcSelMail'){
						$('#toCc').tagEditor('addTag', item.email);
					}else if(clickId == 'toBccSelMail'){
						$('#toBcc').tagEditor('addTag', item.email);
					}
				});
			});
		});

		$("body").on("click", "#chooseEmailModel", function(e){
			_openNewWindows({
				url: "../../tpl/emailSendModel/emailSendModelListChoose.html",
				title: "选择邮件模板",
				pageId: "emailSendModelListChoose",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						$("#typeName").val(emailSendModel.title);
						$('#toPeople').tagEditor('destroy')
						$('#toPeople').tagEditor({
							initialTags: emailSendModel.toPeople.split(','),
							placeholder: '填写完成后直接回车即可'
						});

						$('#toCc').tagEditor('destroy')
						$('#toCc').tagEditor({
							initialTags: emailSendModel.toCc.split(','),
							placeholder: '填写完成后直接回车即可'
						});

						$('#toBcc').tagEditor('destroy')
						$('#toBcc').tagEditor({
							initialTags: emailSendModel.toBcc.split(','),
							placeholder: '填写完成后直接回车即可'
						});
					} else if (refreshCode == '-9999') {
						winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
					}
				}});
		});

		function getContentHead(sendEmail, sendTime, toPeople, toCc, toBcc, title){
			var str = '<div><br><br><br><br><br></div><div style="font-size: 12px;font-family: Arial Narrow;padding:2px 0 2px 0;">------------------&nbsp;原始邮件&nbsp;------------------</div>';
			str += '<div style="font-size: 12px;background:#efefef;padding:8px;">';
			str += '<div><b>发件人:</b>&nbsp;' + sendEmail + '&gt;;</div>';
			str += '<div><b>发送时间:</b>&nbsp;' + sendTime + '</div>';
			str += '<div><b>收件人:</b>&nbsp;' + toPeople + 'com&gt;;<wbr></div>';
			if(!isNull(toCc))
				str += '<div><b>抄送人:</b>&nbsp;' + toCc + 'com&gt;;<wbr></div>';
			if(!isNull(toBcc))
				str += '<div><b>暗送人:</b>&nbsp;' + toBcc + 'com&gt;;<wbr></div>';
			str += '<div></div><div><b>主题:</b>&nbsp;' + title + '</div>';
			str += '</div>';
			return str;
		}
	    
	});
});