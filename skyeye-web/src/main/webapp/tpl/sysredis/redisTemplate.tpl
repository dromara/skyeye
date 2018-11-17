{{#each rows}}
	<div class="layui-col-xs6 padding-l-r-10">
		<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
			<legend>{{ip}}</legend>
		</fieldset>
		<table class="layui-table custom-table">
			<thead>
				<tr>
					<th>键</th>
					<th>值</th>
				</tr>
			</thead>
			<tbody>
				{{#each mation}}
					<tr>
						<td>{{key}}</td>
						<td>{{value}}</td>
					</tr>
				{{/each}}
			</tbody>
		</table>
	</div>
{{/each}}