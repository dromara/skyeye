
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'flow', 'laydate'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
		laydate = layui.laydate,
		flow = layui.flow;
	
	// 选取时间段表格
    laydate.render({
        elem: '#createTime',
        range: '~'
    });
	
	// 时间线日志列表模板
	var timeTreeJobDiary = $("#timeTreeJobDiary").html();
	
	inboxTimeTreeJobDiary();
	// 加载时间线日志
	function inboxTimeTreeJobDiary(){
		flow.load({
			elem: '#timeTreeJobDiaryList', //指定列表容器
			scrollElem: '#timeTreeJobDiaryList',
			isAuto: true,
			done: function(page, next) { //到达临界点（默认滚动触发），触发下一页
				var lis = [];
				var params = {
                    page: page,
                    limit: 12,
                    createName: $("#createName").val(),
                };
                if(isNull($("#createTime").val())){//一定要记得，当createTime为空时
                    params.firstTime = "";
                    params.lastTime = "";
                }else {
                    params.firstTime = $("#createTime").val().split('~')[0].trim();
                    params.lastTime = $("#createTime").val().split('~')[1].trim();
                }
				//以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
				AjaxPostUtil.request({url:reqBasePath + "diary023", params:params, type: 'json', callback: function(json){
		   			if (json.returnCode == 0) {
		   				var jsonStr = "";//实体json对象
		   				$.each(json.rows, function(index, bean) {
		   					if(index == 0 && page == 1){
		   						bean.showClass = 'first';
		   					}else{
		   						bean.showClass = 'date02';
		   					}
		   					$.each(bean.dayChild, function(i, item){
		   						var content = "已完成工作：" + item.completedJob + "<br>";
		   						if(!isNull(item.incompleteJob)){//未完成工作
		   							content += "未完成工作：" + item.incompleteJob + "<br>";
		   						}
		   						if(!isNull(item.workSummary)){//工作总结
		   							if(item.jobType === '2'){//周报
		   								content += "本周工作总结：" + item.workSummary + "<br>";
		   							}else if(item.jobType === '3'){//月报
		   								content += "本月工作总结：" + item.workSummary + "<br>";
		   							}
		   						}
		   						if(!isNull(item.nextPlan)){//工作计划
		   							if(item.jobType === '2'){//周报
		   								content += "下周工作计划：" + item.nextPlan + "<br>";
		   							}else if(item.jobType === '3'){//月报
		   								content += "下月工作计划：" + item.nextPlan + "<br>";
		   							}
		   						}
		   						if(!isNull(item.coordinaJob)){//需协调工作
		   							content += "需协调工作：" + item.coordinaJob + "<br>";
		   						}
		   						if(!isNull(item.jobRemark)){//备注
		   							content += "备注：" + item.jobRemark + "<br>";
		   						}
		   						item.content = content;
		   					});
		   					jsonStr = {
	   							bean: bean
	   						};
							lis.push(getDataUseHandlebars(timeTreeJobDiary, jsonStr));
						});
						matchingLanguage();
		   				//执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
						//pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
						next(lis.join(''), (page * 12) < json.total);
		   			}else{
		   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		   			}
		   		}});
			}
		});
	}
	
	// 搜索
    $("body").on("click", "#formSearch", function(){
        $("#timeTreeJobDiaryList").html("");
        inboxTimeTreeJobDiary();
    });
	
    exports('jobdiaryMyReceiveTimeLine', {});
});
