{{#bean}}
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">所属学校<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select id="schoolId" lay-filter="schoolId" lay-search="" win-verify="required">
        	</select>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">科目<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="subjectName" name="subjectName" win-verify="required" placeholder="请输入科目" class="layui-input" value="{{subjectName}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">编号</label>
        <div class="layui-input-block">
        	<input type="text" id="subjectNo" name="subjectNo" placeholder="请输入科目编号" class="layui-input" maxlength="50" value="{{subjectNo}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">简介</label>
        <div class="layui-input-block">
        	<textarea id="desc" name="desc" class="layui-textarea" style="height: 100px;">{{desc}}</textarea>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}