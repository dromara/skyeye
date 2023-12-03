
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
    	form = layui.form;
	var objectId = GetUrlParam("objectId");
	var objectKey = GetUrlParam("objectKey");

	tabPageUtil.init({
		id: 'tab',
		prefixData: [{
			title: '详情',
			pageUrl: '../../tpl/sysEveUserStaff/sysEveUserStaffDetails.html'
		}],
		suffixData: [{
			title: '员工档案',
			pageUrl: '../../tpl/sysStaffArchives/sysStaffArchivesList.html'
		}, {
			title: '家庭情况',
			pageUrl: '../../tpl/sysStaffFamily/sysStaffFamilyList.html'
		}, {
			title: '教育信息',
			pageUrl: '../../tpl/sysStaffEducation/sysStaffEducationList.html'
		}, {
			title: '工作履历',
			pageUrl: '../../tpl/sysStaffJobResume/sysStaffJobResumeList.html'
		}, {
			title: '语种能力',
			pageUrl: '../../tpl/sysStaffLanguage/sysStaffLanguageList.html'
		}, {
			title: '证书信息',
			pageUrl: '../../tpl/sysStaffCertificate/sysStaffCertificateList.html'
		}, {
			title: '奖惩信息',
			pageUrl: '../../tpl/sysStaffRewardPunish/sysStaffRewardPunishList.html'
		}, {
			title: '合同信息',
			pageUrl: ''
		}],
		element: layui.element,
		object: {
			objectId: objectId,
			objectKey: objectKey,
		}
	});

	form.render();

});