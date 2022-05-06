{{#bean}}
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">名称<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="title" name="title" placeholder="请输入文档名称" class="layui-input"  win-verify="required" maxlength="100" value="{{title}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">分类<i class="red">*</i></label>
        <div class="layui-input-block">
            <select id="fileType" name="fileType" win-verify="required">
        		
        	</select>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">所属项目<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select id="proId" name="proId" win-verify="required">
        		
        	</select>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">文档内容<i class="red">*</i></label>
        <div class="layui-input-block">
        	<script id="content" name="content" type="text/plain"></script>
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
            <button class="winui-btn typeOne layui-hide" lay-submit lay-filter="formEditBean">保存为草稿</button>
            <button class="winui-btn typeOne layui-hide" lay-submit lay-filter="formSubBean">提交审批</button>
            <button class="winui-btn typeTwo layui-hide" lay-submit lay-filter="subBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}