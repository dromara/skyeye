{{#each rows}}
	<div class="invitation-box">
		<div class="invitation-box-time">
			<font>{{createTime}}</font>
		</div>
		<div class="invitation-mation">
			<div class="top-right-book"></div>
			<div class="invitation-mation-img">
				<img alt="{{groupName}}" src="{{#compareimg userPhoto}}{{/compareimg}}" />
			</div>
			<div class="invitation-mation-title">
				<font class="title-first-class">{{userName}}</font>
				{{#compare1 inGroupType}}{{/compare1}}
				<font class="title-second-class">{{groupName}}</font>
			</div>
			<div class="invitation-optrator">
				{{#compare2 state id}}{{/compare2}}
    		</div>
		</div>
	</div>
{{/each}}