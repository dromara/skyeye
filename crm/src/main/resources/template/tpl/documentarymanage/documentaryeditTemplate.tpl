{{#bean}}
	<div class="layui-form-item">
        <label class="layui-form-label">商机<i class="red">*</i></label>
        <div class="layui-input-block">
			<select id="opportunityId" name="opportunityId" win-verify="required" lay-filter="opportunityId" lay-search="">
				
			</select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">分类<i class="red">*</i></label>
        <div class="layui-input-block">
			<select id="typeId" name="typeId" win-verify="required" lay-filter="typeId" lay-search="">
				
			</select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">跟单时间<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="documentaryTime" name="documentaryTime" win-verify="required" placeholder="请输入跟单时间" class="layui-input" value="{{documentaryTime}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">详细内容</label>
        <div class="layui-input-block">
        	<textarea id="detail" name="detail"  placeholder="请输入详细内容" class="layui-textarea" style="height: 100px;" maxlength="200">{{detail}}</textarea>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">附件资料</label>
        <div class="layui-input-block" id="enclosureUpload">
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<div class="layui-input-block">
			<button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
			<button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
		</div>
	</div>
{{/bean}}