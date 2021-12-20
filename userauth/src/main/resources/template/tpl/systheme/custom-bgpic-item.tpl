	
{{#each rows}}
	<div class="layui-col-xs2 pic-item">
		<div class="layui-col-xs12 pic-item-div-top">
			<img class="bgPicItem1" src="{{#compare1 picUrl}}{{/compare1}}"/>
		</div>
		<div class="layui-col-xs12 pic-item-div">
			<div>
				<a class="layui-btn layui-btn-danger layui-btn-xs del"><language showName="com.skyeye.deleteBtn"></language></a>
			</div>
		</div>
	</div>
{{/each}}