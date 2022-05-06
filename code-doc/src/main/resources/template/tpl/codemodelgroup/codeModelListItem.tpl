{{#each rows}}
	<li class="code-model-li">
	    <div style="margin: 0px 5px; width: calc(100% - 10px); padding-top: 5px">
            <label class="layui-form-label" relation="{{id}}" thiscontent="{{modelName}}" style="width: 100%; text-align: left;"
                modeltype="{{modelType}}">{{modelName}}</label>
	    </div>
	    <div style="margin: 0px 5px; width: calc(100% - 10px);">
	    	<div class="layui-col-xs5 padd-le-ri">
	    		类型：{{modelType}}
	    	</div>
	    	<div class="layui-col-xs7 padd-le-ri right">
		    	<button class="layui-btn layui-btn-xs tab-btn-mar-left-3 selModel" title="查看模板" type="button"><i class="fa fa-files-o"></i></button>
		    	<button class="layui-btn layui-btn-xs tab-btn-mar-left-3 selResult" title="查看转换结果" type="button"><i class="fa fa-pencil-square-o"></i></button>
	    	</div>
	    </div>
	</li>
{{/each}}