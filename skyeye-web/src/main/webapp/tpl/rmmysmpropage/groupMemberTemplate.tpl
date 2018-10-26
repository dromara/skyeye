{{#each rows}}
	<div class="layui-col-sm6 layui-col-md6 layui-col-lg6">
		<div class="layui-card">
        	<img src="{{#compare1 printsPicUrl}}{{/compare1}}" data-htmlContent="{{htmlContent}}" data-htmlJsContent="{{htmlJsContent}}" data-id="{{id}}" style="width:100%;height:auto" class="cursor" />
		</div>
    </div>
{{/each}}