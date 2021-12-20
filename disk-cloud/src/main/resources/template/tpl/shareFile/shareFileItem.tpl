{{#each rows}}
	<div class="file-item" rowid="{{id}}" filetype="{{fileType}}">
		<div class="check_box">
			<input type="checkbox" id="check{{id}}" name="fileShareCheck" />
			<label for="check{{id}}" class="checkLabel"></label>
		</div>
		<div class="file_name">
			<img src="{{iconPath}}" alt="{{fileName}}"/>
			<font>{{fileName}}</font>
		</div>
		<div class="size">
			<div class="download">
				<i class="fa fa-download"></i>
			</div>
			<font>{{fileSize}}</font>
		</div>
		<div class="time">{{createTime}}</div>
	</div>
{{/each}}