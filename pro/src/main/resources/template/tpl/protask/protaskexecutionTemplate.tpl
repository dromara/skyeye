{{#bean}}
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">名称：</label>
        <div class="layui-input-block ver-center">
            {{taskName}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">类型：</label>
        <div class="layui-input-block ver-center">
            {{taskTypeName}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">所属项目：</label>
        <div class="layui-input-block ver-center">
            {{{projectName}}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">所属部门：</label>
        <div class="layui-input-block ver-center">
            {{{departments}}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">创建人：</label>
        <div class="layui-input-block ver-center">
            {{createId}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">创建时间：</label>
        <div class="layui-input-block ver-center">
            {{createTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">开始时间：</label>
        <div class="layui-input-block ver-center">
            {{startTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">结束时间：</label>
        <div class="layui-input-block ver-center">
            {{endTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">执行人：</label>
        <div class="layui-input-block ver-center">
        	{{performId}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">预估工作量：</label>
        <div class="layui-input-block ver-center">
        	{{estimatedWorkload}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">实际工作量：</label>
		<div class="layui-input-block ver-center">
			{{actualWorkload}}
		</div>
	</div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">任务说明：</label>
        <div class="layui-input-block ver-center">
			<iframe id="taskInstructionsShowBox" style="width: 100%; border: 0px;" scrolling="no"></iframe>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">附件：</label>
        <div class="layui-input-block ver-center" id="instructionsEnclosureUploadBtn">
            
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">执行结果<i class="red">*</i></label>
        <div class="layui-input-block">
        	<script id="executionResult" name="executionResult" type="text/plain"></script>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">附件</label>
        <div class="layui-input-block" id="enclosureUpload">
        	
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="editBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}