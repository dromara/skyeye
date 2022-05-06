{{#each rows}}
	{{#if sendId}}
		{{#compare1 sendId userId}}
	        <div class="left-talk-log">
	        	<div class="left-talk-log-title">
	        		<font class="left-talk-log-title-name">{{sendName}}</font>
	        		<font class="left-talk-log-title-time">{{createTime}}</font>
	        	</div>
	        	<div class="left-talk-log-content">
	        		<font>{{content}}</font>
	        	</div>
	        </div>
        {{else}}
	        <div class="right-talk-log">
	        	<div class="right-talk-log-title">
	        		<font class="right-talk-log-title-time">{{createTime}}</font>
	        		<font class="right-talk-log-title-name">{{sendName}}</font>
	        	</div>
	        	<div class="right-talk-log-content">
	        		<font>{{content}}</font>
	        	</div>
	        </div>
	    {{/compare1}}
	{{/if}}
{{/each}}