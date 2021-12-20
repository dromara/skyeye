{{#each rows}}
	<div class="app-store-item">
		<div class="app-store-item-content">
			<div class="layui-col-xs12 app-store-item-top sysDetails" rowid="{{id}}">
				<img class="app-store-pic" src="{{#compare1 sysPic}}{{/compare1}}">
			</div>
		</div>
		<div class="layui-col-xs12 app-store-item-bottom">
		</div>
		<div class="layui-col-xs12 app-store-item-bottom-card">
			<button class="layui-btn layui-btn-normal layui-btn-sm sysExperience" rowid="{{id}}" sysurl="{{sysUrl}}">试用</button>
			{{#compare2 powerId id}}{{/compare2}}
		</div>
		<div class="layui-col-xs12 app-store-item-center">
			<p>{{sysName}}</p>
		</div>
	</div>
{{/each}}