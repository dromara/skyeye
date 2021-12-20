{{#each rows}}
	<div class="winui-message-item" rowid="{{id}}">
        <h2>
        	<span>{{title}}</span>
        	<div class="notice-item-remove">
            	<i class="fa fa-trash-o fa-lg"></i>
            </div>
       	</h2>
        <div class="content">{{desc}}</div>
    </div>
{{/each}}