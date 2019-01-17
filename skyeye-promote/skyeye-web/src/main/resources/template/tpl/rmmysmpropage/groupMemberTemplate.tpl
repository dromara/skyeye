{{#each rows}}
	<div class="layui-col-sm6 layui-col-md6 layui-col-lg6" htmlContent="{{htmlContent}}" htmlJsContent="{{htmlJsContent}}" rowId="{{id}}">
		<div class="layui-card">
        	<img src="{{#compare1 printsPicUrl}}{{/compare1}}" style="width:100%;height:auto" class="cursor" />
		</div>
    </div>
{{/each}}