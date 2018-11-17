{{#each rows}}
	<div class="layui-col-xs6 padding-l-r-10">
		<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
			<legend>{{ip}}</legend>
		</fieldset>
		<ul class="layui-timeline" style="height: 400px; overflow-y: scroll;">
			{{#each log}}
				<li class="layui-timeline-item">
					<i class="layui-icon layui-timeline-axis"></i>
					<div class="layui-timeline-content layui-text">
						<h3 class="layui-timeline-title">{{executeTime}}</h3>
						<p>操作：{{args}}，时长：{{usedTime}}</p>
					</div>
				</li>
			{{/each}}
		</ul>
	</div>
{{/each}}