{{#bean}}
	<div class="layui-col-lg12">
      	<label class="layui-form-label">标题：</label>
		<div class="layui-input-block ver-center">
		  	{{scheduleTitle}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">开始时间：</label>
		<div class="layui-input-block ver-center">
		  	{{scheduleStartTime}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">结束时间：</label>
		<div class="layui-input-block ver-center">
		  	{{scheduleEndTime}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">提醒时间：</label>
		<div class="layui-input-block ver-center">
		  	{{scheduleRemindTime}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">创建时间：</label>
		<div class="layui-input-block ver-center">
		  	{{scheduleCreateTime}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">日程类型：</label>
		<div class="layui-input-block ver-center">
		  	{{scheduleTypeName}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">重要性：</label>
		<div class="layui-input-block ver-center">
		  	{{scheduleImport}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">状态：</label>
		<div class="layui-input-block ver-center">
		  	{{scheduleState}}
		</div>
    </div>
    <div class="layui-col-lg12">
      	<label class="layui-form-label">备注：</label>
		<div class="layui-input-block ver-center">
		  	{{{scheduleRemarks}}}
		</div>
    </div>
{{/bean}}