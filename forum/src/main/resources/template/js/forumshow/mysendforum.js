var tagList = new Array();

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'tagEditor'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form;
	
	//公共标题
	$("#forumTitle").html(getFileContent("tpl/forumshow/commontitle.tpl"));
	//菜单
	$("body").append(getFileContent("tpl/forumshow/commonmenu.tpl"));

	var ue = ueEditorUtil.initEditor('container');
	
    $('#tagId').tagEditor({
        initialTags: [],
        placeholder: '请选择标签',
        editorTag: false,
        beforeTagDelete: function(field, editor, tags, val) {
			tagList = [].concat(arrayUtil.removeArrayPointName(tagList, val));
        }
    });
	$("body").on("click", "#chooseTag", function (e) {
		tagReturnList = [].concat(tagList);
		_openNewWindows({
			url: "../../tpl/forumshow/choosetag.html", 
			title: "标签选择",
			pageId: "choosetagpage",
			area: ['600px', '500px'],
			callBack: function (refreshCode) {
				// 重置数据
				tagList = [].concat(systemCommonUtil.tagEditorResetData('tagId', tagReturnList));
			}});
	});
	
	//是否匿名
	form.on('switch(anonymous)', function (data) {
		//同步开关值
		$(data.elem).val(data.elem.checked);
	});
	
	//我的操作
	$("body").on("click", ".suspension-menu-icon", function (e) {
		if($(".drop-down-menu").is(':hidden')){
			$(".drop-down-menu").show();
			$(".suspension-menu-icon").removeClass("rotate").addClass("rotate1");
		} else {
			$(".drop-down-menu").hide();
			$(".suspension-menu-icon").removeClass("rotate1").addClass("rotate");
		}
	});
	
	matchingLanguage();
	form.render();
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	var params = {
    			title: $("#title").val(),
    			forumType: data.field.forumType,
				tagId: systemCommonUtil.tagEditorGetAllData('tagId', tagList)
        	};
        	if (isNull(params.tagId)) {
        		winui.window.msg("请选择标签", {icon: 2, time: 2000});
        		return false;
    		}
        	if($("#anonymous").val() == 'true'){
        		params.anonymous = '2';
        	} else {
        		params.anonymous = '1';
        	}
        	params.content = encodeURIComponent(ue.getContent());
        	if(isNull(params.content)){
        		winui.window.msg("请输入内容", {icon: 2, time: 2000});
        		return false;
        	}
        	params.textConent = encodeURIComponent(ue.getContentTxt());
        	AjaxPostUtil.request({url: sysMainMation.forumBasePath + "forumcontent002", params: params, type: 'json', callback: function (json) {
				winui.window.msg("发布成功", {icon: 1, time: 2000}, function() {
					location.href = '../../tpl/forumshow/myposts.html';
				});
        	}});
        }
        return false;
    });
	
    exports('mysendforum', {});
});
