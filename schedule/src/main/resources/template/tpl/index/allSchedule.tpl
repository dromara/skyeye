<div class="layui-collapse">
	{{#if rows}}
		{{#each rows}}
			<div class="layui-colla-item">
		    	<h2 class="layui-colla-title"><font>{{time}}</font><i class="layui-icon layui-colla-icon">&#9660</i></h2>
		    	<div class="layui-colla-content layui-show">
		      		<ul class="layui-timeline">
		      			{{#each items}}
		      				{{#if type}}
							{{#compare1 type}}
								{{else}}
									<li class="layui-timeline-item">
										<i class="layui-icon layui-timeline-axis">&#xe63f;</i>
										<div class="layui-timeline-content layui-text" rowid="{{id}}">
											<h3 class="layui-timeline-title">{{start}}~{{end}} <font class="schedule-item-color" style="background-color: {{backgroundColor}}"></font></h3>
											<p>[{{scheduleImport}}]{{title}}<i class="fa fa-trash schrdule-del" title="删除日程"></i></p>
										</div>
									</li>
							{{/compare1}}
							{{/if}}
						{{/each}}
					</ul>
		    	</div>
		  	</div>
		  {{/each}}
	{{else}}
	    <li class="layui-timeline-item no-schedule-mation">
			<p style="text-align: center;">暂无日程</p>
		</li>
	{{/if}}
  	
</div>