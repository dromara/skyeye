
{{#each rows}}
	<li class="app-li">
		<div class="icon">
			<img src="{{appLogo}}" rowurl="{{appUrl}}" rowtitle="{{appName}}"  style="height:65px; width:65px; margin-top:15px;">
		</div>
		<div class="text">
			<div class="name">{{appName}}</div>
			<div class="copyright">{{appDesc}}</div>
		</div>
		<div class="btn-group action">
			<button type="button" class="btn btn-sm btn-default" rowid="{{id}}">添加</button>
		</div>
	</li>
{{/each}}
<li class="flex-empty"></li>
<li class="flex-empty"></li>
<li class="flex-empty"></li>
<li class="flex-empty"></li>
<li class="flex-empty"></li>
<li class="flex-empty"></li>
<li class="flex-empty"></li>
<li class="flex-empty"></li>
<li class="flex-empty"></li>
<li class="flex-empty"></li>
<li class="flex-empty"></li>

