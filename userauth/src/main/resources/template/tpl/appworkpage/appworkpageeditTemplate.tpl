{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">页面名称<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="title" name="title" win-verify="required" placeholder="请输入页面名称" class="layui-input"  maxlength="10" value="{{title}}" />
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">logo</label>
        <div class="layui-input-block">
            <div class="upload" id="logo"></div>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">所属目录<i class="red">*</i></label>
		<div class="layui-input-block">
			<select id="parentId" name="parentId" win-verify="required">
						
			</select>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">页面类型<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="urlType" value="1" title="外部系统页面" />
            <input type="radio" name="urlType" value="2" title="自身系统页面" />
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">页面路径</label>
        <div class="layui-input-block">
            <input type="text" id="url" name="url" placeholder="请输入页面路径" class="layui-input" value="{{url}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditMenu"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}