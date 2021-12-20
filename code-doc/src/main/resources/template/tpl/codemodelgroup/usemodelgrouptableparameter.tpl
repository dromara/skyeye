<tr style="background-color: ghostwhite;">
	<th style="width: 70px; padding: 5px;">字段名</th>
	<th style="width: 100px; padding: 5px;">字段类型</th>
	<th style="width: 100px; padding: 5px;">允许非空</th>
	<th style="width: 100px; padding: 5px;">备注</th>
</tr>
{{#each rows}}
	<tr>
		<td style="width: 70px; padding: 5px;">{{columnName}}</td>
		<td style="width: 100px; padding: 5px;">{{columnType}}</td>
		<td style="width: 100px; padding: 5px;">{{isNullable}}</td>
		<td style="width: 100px; padding: 5px;">{{columnComment}}</td>
	</tr>
{{/each}}