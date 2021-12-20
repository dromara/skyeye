<div class="product-list">
	<dl>
		{{#each rows}}
			<dd class="{{#compare1 @index}}{{/compare1}}">
				<a href="javascript:;" rowid="{{id}}" title="{{name}}">{{name}}</a>
			</dd>
		{{/each}}
	</dl>
</div>