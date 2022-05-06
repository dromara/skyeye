{{#each rows}}
	<div class="layui-col-md12 import-item" htmlContent="{{htmlContent}}" htmlJsContent="{{htmlJsContent}}" rowId="{{modelId}}">
		<div class="layui-col-md12 check-item">
			{{{htmlContent}}}
		</div>
		<div class="check-item-operation btn-group btn-group-xs btn-base" style="display: none;">
			<button type="button" class="btn btn-primary" rel="editHandler" title="编辑"><i class="fa fa-edit"></i></button>
			<button type="button" class="btn btn-danger" rel="removeHandler" title="删除"><i class="fa fa-trash"></i></button>
		</div>
		<script>
			layui.define(["jquery"], function(exports) {
				var jQuery = layui.jquery;
				(function($) {
					{{{htmlJsContent}}}
				})(jQuery);
			});
		</script>
    </div>
{{/each}}