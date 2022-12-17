{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">标题<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="title" name="title" win-verify="required" placeholder="请输入知识库标题" class="layui-input" value="{{title}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">所属类型<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="typeId" name="typeId" placeholder="请选择类型" class="layui-input" readonly="readonly" typeId=""/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">标签</label>
        <div class="layui-input-block">
            <input type="text" id="label" name="label" placeholder="请填写标签" class="layui-input"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">公告内容<i class="red">*</i></label>
        <div class="layui-input-block">
			<script id="container" name="content" type="text/plain"></script>
		</div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}