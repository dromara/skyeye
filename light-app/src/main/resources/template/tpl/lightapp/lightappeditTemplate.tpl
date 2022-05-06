{{#bean}}
	<div class="layui-form-item">
		<label class="layui-form-label">应用名称<i class="red">*</i></label>
		<div class="layui-input-block">
			<input type="text" id="appName" name="appName" win-verify="required" placeholder="请输入应用名称" class="layui-input"  maxlength="10" value="{{appName}}" />
		</div>
	</div>
	<div class="layui-form-item menuIcon">
		<label class="layui-form-label">logo<i class="red">*</i></label>
		<div class="layui-input-block">
			<div class="upload" id="appLogo"></div>
		</div>
	</div>
	<div class="layui-form-item">
		<label class="layui-form-label">应用类型<i class="red">*</i></label>
		<div class="layui-input-block">
			<select lay-filter="typeId" lay-search="" id="typeId" win-verify="required">
			
			</select>
		</div>
	</div>
	<div class="layui-form-item">
		<label class="layui-form-label">应用地址<i class="red">*</i></label>
		<div class="layui-input-block">
		    <input type="text" id="appUrl" name="appUrl" win-verify="required|url" placeholder="请输入应用地址" class="layui-input"  value="{{appUrl}}" />
		    <div class="layui-form-mid layui-word-aux">地址格式为：http://xxxxxxxxxx</div>
		</div>
	</div>
	<div class="layui-form-item">
		<label class="layui-form-label">应用描述</label>
		<div class="layui-input-block">
			<input type="text" id="desc" name="desc" placeholder="" class="layui-input" value="{{appdesc}}"/>
		</div>
	</div>
	<div class="layui-form-item layui-col-xs12">
		<div class="layui-input-block">
			<button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
			<button class="winui-btn" lay-submit lay-filter="formEditMenu"><language showName="com.skyeye.save"></language></button>
		</div>
	</div>
{{/bean}}