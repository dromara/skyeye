	
{{#each rows}}
	<div class="layui-col-xs2 pic-item">
		<div class="layui-col-xs12 pic-item-div-top">
			<img class="lockBgPicItem1" src="{{#compare1 picUrl}}{{/compare1}}"/>
		</div>
		<div class="layui-col-xs12 pic-item-div">
			<div>
				<a class="layui-btn layui-btn-danger layui-btn-xs lockDel">删除</a>
			</div>
		</div>
	</div>
{{/each}}