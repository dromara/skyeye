{{#bean}}
	<div class="layui-form-item" id="leaveTime">
        <label class="layui-form-label">申诉人：</label>
        <div class="layui-input-block ver-center">
        	{{userName}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">申诉内容：</label>
        <div class="layui-input-block ver-center">
        	{{checkDate}}{{situation}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">原因类型：</label>
        <div class="layui-input-block ver-center">
        	{{appealName}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">具体原因：</label>
       	<div class="layui-input-block ver-center">
            {{appealReason}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">申诉时间：</label>
        <div class="layui-input-block ver-center">
        	{{createTime}}
        </div>
    </div>
    <div class="layui-form-item">
		<label class="layui-form-label">审核结果：</label>
		<div class="layui-input-block winui-radio">
			<input type="radio" name="appealstate" value="1" title="通过" checked="checked"/>
			<input type="radio" name="appealstate" value="2" title="不通过" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">审核意见：</label>
        <div class="layui-input-block">
        	<textarea id="appealRemark" name="appealRemark"  placeholder="请输入审核意见" class="layui-textarea" style="height: 100px;" maxlength="200"></textarea>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">关闭</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}