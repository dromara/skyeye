
// 知识点选择必备参数
var schoolKnowledgeCheckType = 2;// 知识点选择类型：1.单选schoolKnowledgeMation；2.多选schoolKnowledgeMationList
var schoolKnowledgeMationList = new Array();

// 要删除的行id
var deleteRowList = new Array();
var deleteColumnList = new Array();

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form,
		    element = layui.element;
		// tab当前下标
		var tabIndex = 0;
		var fileUrl = "";

		// 获取当前登陆用户所属的学校列表
		schoolUtil.queryMyBelongSchoolList(function (json) {
			$("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), json));
			form.render("select");
			loadData();
		});
	    // 学校监听事件
		form.on('select(schoolId)', function(data){
			if (isNull(data.value) || data.value === '请选择') {
				$("#schoolId").html("");
				form.render('select');
			} else {
				// 加载年级
				initGrade();
			}
		});
		
		// 初始化年级
		function initGrade(){
			showGrid({
			 	id: "gradeId",
			 	url: schoolBasePath + "grademation006",
			 	params: {schoolId: $("#schoolId").val()},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb){},
			 	ajaxSendAfter:function(json){
			 		form.render('select');
			 	}
		    });
		}
		// 年级监听事件
		form.on('select(gradeId)', function(data){
			if(isNull(data.value) || data.value === '请选择'){
				$("#subjectId").html("");
				form.render('select');
			}else{
				// 加载科目
				initSubject();
			}
		});
		
		// 初始化科目
		function initSubject(){
			showGrid({
			 	id: "subjectId",
			 	url: schoolBasePath + "schoolsubjectmation007",
			 	params: {gradeId: $("#gradeId").val()},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb){},
			 	ajaxSendAfter:function(json){
			 		form.render('select');
			 	}
		    });
		}
		
		function loadData(){
			// 如果问题id不为空，则说明是编辑，加载编辑信息
			if(!isNull(parent.rowId)){
				AjaxPostUtil.request({url:schoolBasePath + "schoolquestionbank016", params: {rowId: parent.rowId}, type: 'json', callback: function(json){
					if (json.returnCode == 0) {
						$("#schoolId").val(json.bean.schoolId);
						showGrid({
						 	id: "gradeId",
						 	url: schoolBasePath + "grademation006",
						 	params: {schoolId: $("#schoolId").val()},
						 	pagination: false,
						 	template: getFileContent('tpl/template/select-option.tpl'),
						 	ajaxSendLoadBefore: function(hdb){},
						 	ajaxSendAfter:function(data){
						 		$("#gradeId").val(json.bean.gradeId);
						 		showGrid({
								 	id: "subjectId",
								 	url: schoolBasePath + "schoolsubjectmation007",
								 	params: {gradeId: $("#gradeId").val()},
								 	pagination: false,
								 	template: getFileContent('tpl/template/select-option.tpl'),
								 	ajaxSendLoadBefore: function(hdb){},
								 	ajaxSendAfter:function(data){
								 		$("#subjectId").val(json.bean.subjectId);
								 		form.render();
								 	}
							    });
						 	}
					    });
					    $("input:radio[name=type][value=" + json.bean.type + "]").attr("checked", true);
					    $("#fraction").val(json.bean.fraction);
					    // 知识点赋值
					    schoolKnowledgeMationList = [].concat(json.bean.knowledgeList);
					    var str = "";
					    $.each(schoolKnowledgeMationList, function(i, item){
	                		str += '<br><span class="layui-badge layui-bg-blue" style="height: 25px !important; line-height: 25px !important; margin: 5px 0px;">' + item.title + '</span>';
	                	});
	    				$("#schoolKnowledgeChoose").parent().html('<button type="button" class="layui-btn layui-btn-primary layui-btn-xs" id="schoolKnowledgeChoose">知识点选择</button>' + str);
					    
	    				// 题目信息赋值
	    				$(".surveyQuItemBody").html(getDataUseHandlebars($("#template").html(), json));
	    				
	    				// 设置tab
	    				tabIndex = json.bean.fileType;
	    				fileUrl = json.bean.fileUrl;
	    				$('.layui-tab-title li').eq(tabIndex).addClass('layui-this').siblings().removeClass('layui-this');
						$('.layui-tab-item').eq(tabIndex).addClass('layui-show').siblings().removeClass('layui-show');
						
	    				// 设置是否允许拍照/上传图片选中
			 			$("input:radio[name=whetherUpload][value=" + json.bean.whetherUpload + "]").attr("checked", true);
	    				
	    				// 加载答案数据
			    		var answer = $(".surveyQuItemBody").find(".quCoItem table.quCoChenTable tr input.chenRadioInput");
			    		var isDefaultAnswer = isJsonFormat(json.bean.isDefaultAnswer) ? JSON.parse(json.bean.isDefaultAnswer) : [];
			    		var columuLength = $(".surveyQuItemBody").find(".quCoItem table.quCoChenTable tr td.quChenColumnTd").length;
			    		var xIndex = 0;
			    		var yIndex = 1;
			    		$.each(answer, function(i) {
			    			if(i % columuLength == 0){
			    				xIndex++;
			    				yIndex = 1;
			    			}else{
				    			yIndex++;
			    			}
			    			var _this = this;
			    			$.each(isDefaultAnswer, function(j, item) {
				    			if(item.x == xIndex && item.y == yIndex){
				    				$(_this).prop("checked", item.value);
				    			}
				    		});
			    		});
	    				
	    				form.render();
	    				
	    				// 加载上传和切换监听事件
						pageLoadAfter();
	    				
					}else{
						winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
					}
				}});
			}else{
				// 加载年级
		 		initGrade();
		 		var timeJson = {
		 			time1: getRandomValueToString() + "a",
		 			time2: getRandomValueToString() + "b"
		 		};
		 		// 题目信息赋值
				$(".surveyQuItemBody").html(getDataUseHandlebars($("#noDataTemplate").html(), timeJson));
				// 加载上传和切换监听事件
				pageLoadAfter();
			}
		}
 		
		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	    	// 表单验证
 	        if (winui.verifyForm(data.elem)) {
 	        	var quItemBody = $(".surveyQuItemBody");
 	        	
 	        	if(tabIndex != 0){
 	        		// 1-视频,2-音频,3-图片
 	        		fileUrl = quItemBody.find(".layui-tab-content").find(".layui-show").find(".upload").find("input[type='hidden'][name='upload']").attr("oldurl");
 	        		if(isNull(fileUrl)){
 	        			winui.window.msg('请上传文件.', {icon: 2, time: 2000});
	    				return false;
 	        		}
 	        	}else{
 	        		fileUrl = "";
 	        	}
 	        	var params = {
    				quId: quItemBody.find("input[name='quId']").val(),
    				hv: quItemBody.find("input[name='hv']").val(),
    				quType: quItemBody.find("input[name='quType']").val(),
    				randOrder: quItemBody.find("input[name='randOrder']").val(),
    				cellCount: quItemBody.find("input[name='cellCount']").val(),
    				quTitle: encodeURI(quItemBody.find(".quCoTitleEdit").html()),
    				fraction: $("#fraction").val(),
    				schoolId: $("#schoolId").val(),
        			gradeId: $("#gradeId").val(),
        			subjectId: $("#subjectId").val(),
        			type: $("input[name='type']:checked").val(),
        			schoolKnowledgeMationList: JSON.stringify(schoolKnowledgeMationList),
        			deleteRowList: JSON.stringify(deleteRowList),
        			deleteColumnList: JSON.stringify(deleteColumnList),
        			fileUrl: fileUrl,
        			fileType: tabIndex,
        			whetherUpload: data.field.whetherUpload
	    		};
	    		// 矩阵列选项td
	    		var quColumnOptions = quItemBody.find(".quCoItem table.quCoChenTable tr td.quChenColumnTd");
	    		var column = [];
	    		$.each(quColumnOptions, function(i) {
    				var s = {
						optionValue: encodeURI($(this).find("label.quCoOptionEdit").html()),
						optionId: $(this).find(".quItemInputCase input[name='quItemId']").val(),
						key: i
	    			};
    				column.push(s);
	    		});
	    		params.column = JSON.stringify(column);
	    		// 矩阵行选项td
	    		var quRowOptions = quItemBody.find(".quCoItem table.quCoChenTable tr td.quChenRowTd");
	    		var row = [];
	    		$.each(quRowOptions, function(i) {
    				var s = {
						optionValue: encodeURI($(this).find("label.quCoOptionEdit").html()),
						optionId: $(this).find(".quItemInputCase input[name='quItemId']").val(),
						key: i
	    			};
    				row.push(s);
	    		});
	    		params.row = JSON.stringify(row);
	    		if(quColumnOptions.length == 0 || quRowOptions.length == 0){
	    			winui.window.msg('选项不能为空', {icon: 2, time: 2000});
	    			return false;
	    		}
	    		
	    		// 答案
	    		var answer = quItemBody.find(".quCoItem table.quCoChenTable tr input.chenRadioInput");
	    		var isDefaultAnswer = new Array();
	    		var columuLength = quColumnOptions.length;
	    		var xIndex = 0;
	    		var yIndex = 1;
	    		$.each(answer, function(i) {
	    			if(i % columuLength == 0){
	    				xIndex++;
	    				yIndex = 1;
	    			}else{
		    			yIndex++;
	    			}
	    			var s = {
	    				x: xIndex,
	    				y: yIndex,
	    				value: $(this).prop("checked")
	    			};
	    			isDefaultAnswer.push(s);
	    		});
	    		params.isDefaultAnswer = JSON.stringify(isDefaultAnswer);
	    		
    			AjaxPostUtil.request({url:schoolBasePath + "schoolquestionbank015", params: params, type: 'json', callback: function(json){
    				if (json.returnCode == 0) {
    					parent.layer.close(index);
    	 	        	parent.refreshCode = '0';
    				}else{
    					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    				}
    			}});
 	        }
 	        return false;
 	    });
 	    
 	    function pageLoadAfter(){
 	    	// 初始化视频上传
	 		$(".questionVedio").upload({
	            "action": reqBasePath + "common003",
	            "data-num": "1",
	            "data-type": vedioType,
	            "uploadType": 16,
	            "data-value": (tabIndex == 1 && !isNull(fileUrl)) ? fileUrl : "",
	            "function": function (_this, data) {
	                show(_this, data);
	            }
	        });
	        
	        // 初始化音频上传
	 		$(".questionAudio").upload({
	            "action": reqBasePath + "common003",
	            "data-num": "1",
	            "data-type": audioType,
	            "uploadType": 16,
	            "data-value": (tabIndex == 2 && !isNull(fileUrl)) ? fileUrl : "",
	            "function": function (_this, data) {
	                show(_this, data);
	            }
	        });
	        
	        // 初始化图片上传
	 		$(".questionPicture").upload({
	            "action": reqBasePath + "common003",
	            "data-num": "1",
	            "data-type": imageType,
	            "uploadType": 16,
	            "data-value": (tabIndex == 3 && !isNull(fileUrl)) ? fileUrl : "",
	            "function": function (_this, data) {
	                show(_this, data);
	            }
	        });
	        
			element.on('tab(question-insert-type)', function(obj){
				tabIndex = obj.index;
				$.each($(this).parent().find("img"), function(i){
					var src = $(this).attr("src");
					if(src.indexOf("-choose") != -1){
						src = src.replace("-choose.png", '') + '.png';
					}
					$(this).attr("src", src);
				});
				if(obj.index != 0){
					var src = $(this).find("img").attr("src");
					if(src.indexOf("-choose") != -1){
						src = src.replace("-choose.png", '') + '.png';
					}else{
						src = src.replace(".png", '') + '-choose.png';
					}
					$(this).find("img").attr("src", src);
				}
			});
 	    }
		
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});