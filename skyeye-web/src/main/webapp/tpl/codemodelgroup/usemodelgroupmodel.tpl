{{#each rows}}
	<li class="page-li">
	    <div class="weui-flex js_category layui-col-xs8 padd-le-ri">
	        <div>
	            <label class="layui-form-label">{{modelName}}<i class="red">*</i></label>
	            <div class="layui-input-block">
	            	<input type="text" id="{{id}}" name="{{id}}" win-verify="required" placeholder="请选择数据库表检所生成" class="layui-input" maxlength="50"/>
	            </div>
	        </div>
	    </div>
	    <div class="weui-flex js_category layui-col-xs4 right">
	    	<button class="layui-btn layui-btn-sm tab-btn-mar-left-3 selModel" title="查看模板" type="button"><i class="fa fa-files-o"></i></button>
	    	<button class="layui-btn layui-btn-sm tab-btn-mar-left-3 selResult" title="查看转换结果" type="button"><i class="fa fa-pencil-square-o"></i></button>
	    </div>
	</li>
{{/each}}