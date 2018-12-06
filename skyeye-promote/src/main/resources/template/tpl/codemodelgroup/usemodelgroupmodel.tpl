{{#each rows}}
	<li class="code-model-li">
	    <div class="weui-flex js_category layui-col-xs10 padd-le-ri layui-col-xs-offset1">
	        <div>
	            <label class="layui-form-label" relation="{{id}}" thiscontent="{{modelName}}">{{modelName}}<i class="red">*</i></label>
	            <div class="layui-input-block">
	            	<input type="text" id="{{id}}" name="{{id}}" modeltype="{{modelType}}" modelname="{{modelName}}" win-verify="required" placeholder="请选择数据库表检所生成" class="layui-input" maxlength="50"/>
	            </div>
	        </div>
	    </div>
	    <div class="weui-flex js_category layui-col-xs10 padd-le-ri layui-col-xs-offset1">
	    	<div class="weui-flex js_category layui-col-xs5 padd-le-ri">
	    		类型：{{modelType}}
	    	</div>
	    	<div class="weui-flex js_category layui-col-xs7 padd-le-ri right">
		    	<button class="layui-btn layui-btn-sm tab-btn-mar-left-3 selModel" title="查看模板" type="button"><i class="fa fa-files-o"></i></button>
		    	<button class="layui-btn layui-btn-sm tab-btn-mar-left-3 selResult" title="查看转换结果" type="button"><i class="fa fa-pencil-square-o"></i></button>
		    	<button class="layui-btn layui-btn-sm tab-btn-mar-left-3 createResult" title="转换结果" type="button"><i class="fa fa-clipboard"></i></button>
	    	</div>
	    </div>
	</li>
{{/each}}