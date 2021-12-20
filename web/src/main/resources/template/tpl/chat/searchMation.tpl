{{#each rows}}
	<div class="msg-box">
		<div class="msg-group-mation">
			<div class="msg-group-mation-img">
				<img alt="" src="{{#compareimg groupImg}}{{/compareimg}}" />
			</div>
			<div class="msg-group-mation-title">
				<div class="msg-group-mation-name">
					<font>{{groupName}}</font>
				</div>
				<div class="msg-group-mation-num">
					<i class="fa layui-icon layui-icon-user fa-fw group-icon"></i>
					<font>{{newGroupNum}}/{{groupUserNum}}</font>
				</div>
			</div>
			<div class="msg-group-mation-desc">
				{{#compare1 groupDesc}}{{/compare1}}
			</div>
		</div>
		<div class="msg-group-opratio">
			{{#compare2 inId id newGroupNum groupUserNum}}{{/compare2}}
		</div>
	</div>
{{/each}}