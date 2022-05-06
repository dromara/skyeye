{{#each rows}}
	<li>
        <div class="weui-flex js_category">
            <p class="weui-flex__item">{{name}}<span class="weui-badge" style="margin-left: 5px;">{{groupList.length}}</span></p>
        </div>
        <div class="page__category js_categoryInner">
            <div class="weui-cells page__category-content">
            	{{#each groupList}}
	                <a class="weui-cell weui-cell_access js_item" data-id="{{id}}" data-name="{{name}}" href="javascript:;">
	                    <div class="weui-cell__bd">
	                        <p>{{name}}<span class="weui-badge" style="margin-left: 5px;">{{memberNum}}</span></p>
	                    </div>
	                </a>
	            {{/each}}
            </div>
        </div>
    </li>
{{/each}}