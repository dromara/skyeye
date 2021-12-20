{{#bean}}
	<div class="layui-form-item">
	    <div class="layui-form-item layui-col-xs4">
	        <label class="layui-form-label">日报标题</label>
	        <div class="layui-input-block">
	            <input type="text" id="jobTitle" name="jobTitle" placeholder="请输入日报标题" class="layui-input" value="{{jobTitle}}"/>
	        </div>
	    </div>
        <div class="layui-form-item layui-col-xs8">
	        <label class="layui-form-label">收件人</label>
	        <div class="layui-input-block">
                <input type="text" id="userName" name="userName" placeholder="请选择收件人" class="layui-input"/>
                <i class="fa fa-user-plus input-icon" id="userNameSelPeople"></i>
	        </div>
	    </div>
	</div>
	<div class="layui-form-item">
		<div class="layui-inline">
			<label class="layui-form-label">今日完成工作</label>
		</div>
		<div class="layui-inline" style="float: right">
			<label class="layui-form-label">图文模式</label>
			<div class="layui-input-block winui-switch">
				<input id="todycompleted" name="todycompleted" lay-filter="todycompletedImagetext" type="checkbox" lay-skin="switch" lay-text="是|否" value="true" checked="checked"/>
			</div>
		</div>
	</div>
	<div class="layui-form-item" style="align: center; display: none;">
		<textarea id="completedtext" name="completedtext" placeholder="请输入内容" class="layui-textarea">{{#compare1 completedJob}}{{/compare1}}</textarea>
	</div>
	<div class="layui-form-item" style="align: center;">
		<textarea id="completedcontent" name="completedcontent" placeholder="请输入内容" class="layui-textarea">{{{completedJob}}}</textarea>
	</div>
	
	<div class="layui-form-item">
		<div class="layui-inline">
			<label class="layui-form-label">未完成工作</label>
		</div>
		<div class="layui-inline" style="float: right">
			<label class="layui-form-label">图文模式</label>
			<div class="layui-input-block winui-switch">
				<input id="todyincomplete" name="todyincomplete" lay-filter="todyincompleteImagetext" type="checkbox" lay-skin="switch" lay-text="是|否" value="true" checked="checked"/>
			</div>
		</div>
	</div>
	<div class="layui-form-item" style="align: center; display: none;">
		<textarea id="incompletetext" name="incompletetext" placeholder="请输入内容" class="layui-textarea">{{#compare1 incompleteJob}}{{/compare1}}</textarea>
	</div>
	<div class="layui-form-item" style="align: center;">
		<textarea id="incompletecontent" name="incompletecontent" placeholder="请输入内容" class="layui-textarea">{{{incompleteJob}}}</textarea>
	</div>
	<div class="layui-form-item">
		<div class="layui-inline">
			<label class="layui-form-label">需协调工作</label>
		</div>
		<div class="layui-inline" style="float: right">
			<label class="layui-form-label">图文模式</label>
			<div class="layui-input-block winui-switch">
				<input id="todycoordina" name="todycoordina" lay-filter="todycoordinaImagetext" type="checkbox" lay-skin="switch" lay-text="是|否" value="true" checked="checked"/>
			</div>
		</div>
	</div>
	<div class="layui-form-item" style="align: center; display: none;">
		<textarea id="coordinatext" name="coordinatext" placeholder="请输入内容" class="layui-textarea">{{#compare1 coordinaJob}}{{/compare1}}</textarea>
	</div>
	<div class="layui-form-item" style="align: center;">
		<textarea id="coordinacontent" name="coordinacontent" placeholder="请输入内容" class="layui-textarea">{{{coordinaJob}}}</textarea>
	</div>
	
	<div class="layui-form-item">
		<div class="layui-inline">
			<label class="layui-form-label">备注</label>
		</div>
	</div>
	<div class="layui-form-item" style="align: center;">
		<textarea id="jobRemark" name="jobRemark" placeholder="请输入内容" class="layui-textarea">{{#compare1 jobRemark}}{{/compare1}}</textarea>
	</div>
	<div class="layui-form-item">
        <label class="layui-form-label">附件</label>
        <div class="layui-input-block" id="enclosureUpload">
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
		<div class="layui-input-block">
			<button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
			<button class="winui-btn" lay-submit lay-filter="daysubmit">发送</button>
		</div>
	</div>
{{/bean}}