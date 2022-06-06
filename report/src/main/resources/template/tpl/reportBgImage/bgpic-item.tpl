	
{{#each rows}}
	<div class="layui-col-xs2 pic-item" style="width: 210px; height: 180px;">
		<div class="layui-col-xs12 pic-item-div-top" style="width: 190px; height: 120px;">
			<img class="win-bg-pic sel" src="{{#compare1 imagePath}}{{/compare1}}"/>
		</div>
        <div style="width: 100%; text-align: center; top: 135px; position: absolute;">{{title}}</div>
		<div class="layui-col-xs12 pic-item-div" style="margin-top: 15px;">
			<div>
				<a class="layui-btn layui-btn-danger layui-btn-xs del" auth="1625280815182">删除</a>
			</div>
		</div>
	</div>
{{/each}}