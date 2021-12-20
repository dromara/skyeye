{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">标题<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="title" name="title" win-verify="required" placeholder="请输入标题" class="layui-input" maxlength="50" value="{{title}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">内容</label>
        <div class="layui-input-block">
        	<script id="container" name="content" type="text/plain"></script>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">标签<i class="red">*</i></label>
		<div class="layui-input-block">
			<input type="text" id="tagId" name="tagId" placeholder="请选择标签" class="layui-input" /> <i class="fa fa-user-plus input-icon" id="chooseTag"></i>
		</div>
	</div>
    <div class="layui-form-item">
        <label class="layui-form-label">发布形式<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="forumType" value="1" title="公开" lay-filter="forumType" />
            <input type="radio" name="forumType" value="2" title="私密" lay-filter="forumType" />
        </div>
	</div>
    <div class="layui-form-item">
        <label class="layui-form-label">匿名发送<i class="red">*</i></label>
        <div class="layui-input-block winui-switch">
        	<input id="anonymous" name="anonymous" lay-filter="anonymous" type="checkbox" lay-skin="switch" lay-text="是|否" {{#compare4 anonymous}}{{/compare4}} value="{{#compare5 anonymous}}{{/compare5}}"/>
    	</div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" type="button" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">发布</button>
        </div>
    </div>
{{/bean}}