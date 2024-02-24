{{#bean}}
	<div class="layui-col-lg12">
      	<label class="layui-form-label">标题：</label>
		<div class="layui-input-block ver-center">
		  	{{name}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">开始时间：</label>
		<div class="layui-input-block ver-center">
		  	{{startTime}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">结束时间：</label>
		<div class="layui-input-block ver-center">
		  	{{endTime}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">提醒时间：</label>
		<div class="layui-input-block ver-center">
		  	{{remindTime}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">创建时间：</label>
		<div class="layui-input-block ver-center">
		  	{{createTime}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">日程类型：</label>
		<div class="layui-input-block ver-center">
		  	{{typeName}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">重要性：</label>
		<div class="layui-input-block ver-center">
		  	{{importedName}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">状态：</label>
		<div class="layui-input-block ver-center">
		  	{{stateName}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">备注：</label>
		<div class="layui-input-block ver-center">
		  	{{{remark}}}
		</div>
    </div>
{{/bean}}