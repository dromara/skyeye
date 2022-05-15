{{#bean}}
	<div class="layui-collapse">
        <div class="layui-colla-item">
            <h2 class="layui-colla-title"></h2>
            <div class="layui-colla-content">

            </div>
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">模板内容</span><hr>
	</div>
    <div class="layui-form-item">
        <label class="layui-form-label">模板标题<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="contentName" name="contentName" win-verify="required" placeholder="请输入模板标题" class="layui-input" maxlength="20" value="{{contentName}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">HTML内容类型<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select id="htmlType" name="htmlType" class="layui-input" win-verify="required" lay-filter="selectParent">
                <option value="html">html</option>
            </select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">HTML模板内容<i class="red">*</i></label>
        <div class="layui-input-block">
        	<textarea id="htmlContent">{{htmlContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">JS内容类型<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select id="jsType" name="jsType" class="layui-input" win-verify="required" lay-filter="selectParent">
                <option value="javascript">javascript</option>
            </select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">JS模板内容</label>
        <div class="layui-input-block">
        	<textarea id="jsContent">{{jsContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">关联数据<i class="red">*</i></label>
        <div class="layui-input-block winui-switch">
            <input id="linkedData" name="linkedData" lay-filter="linkedData" type="checkbox" lay-skin="switch" lay-text="是|否" {{#compare2 linkedData}}{{/compare2}} value="{{#compare3 linkedData}}{{/compare3}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-hide dataTpl">
        <label class="layui-form-label">数据展示模板<i class="red">*</i></label>
        <div class="layui-input-block">
            <select id="dataShowTpl" name="dataShowTpl" lay-filter="dataShowTpl">
            	
            </select>
        </div>
    </div>
    <div class="layui-form-item layui-hide dataTpl">
        <div class="layui-input-block" id="templateContent">
        	
        </div>
    </div>
    <div class="layui-form-item layui-hide dataTpl">
        <label class="layui-form-label">默认数据<i class="red">*</i></label>
        <div class="layui-input-block">
        	<textarea id="defaultData" name="defaultData" class="layui-textarea">{{defaultData}}</textarea>
        	<div class="layui-form-mid layui-word-aux">数据样式为：[{"id":"1","name":"男",...},{"id":"2","name":"女",...}]</div>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}