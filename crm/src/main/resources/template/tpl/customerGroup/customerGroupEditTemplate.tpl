{{#bean}}
	<div class="layui-form-item">
		<label class="layui-form-label">分组名称<i class="red">*</i></label>
		<div class="layui-input-block">
			<input type="text" id="groupName" name="groupName" win-verify="required" placeholder="请输入分组名称" class="layui-input" maxlength="10" value="{{groupName}}" />
		</div>
	</div>
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">附加说明</label>
        <div class="layui-input-block">
        	<textarea id="desc" name="desc" placeholder="请输入附加说明" class="layui-textarea" style="height: 100px;" maxlength="200">{{desc}}</textarea>
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
		<div class="layui-input-block">
			<button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
			<button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
		</div>
	</div>
{{/bean}}