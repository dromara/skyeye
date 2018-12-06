<tr style="background-color: ghostwhite;">
	<th style="width: 100px">字段名</th>
	<th style="width: 100px">字段类型</th>
	<th style="width: 100px">允许非空</th>
	<th style="width: 100px">备注</th>
</tr>
{{#each rows}}
	<tr>
		<td style="width: 100px">{{columnName}}</td>
		<td style="width: 100px">{{columnType}}</td>
		<td style="width: 100px">{{isNullable}}</td>
		<td style="width: 100px">{{columnComment}}</td>
	</tr>
{{/each}}