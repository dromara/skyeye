{{#bean}}
	<div class="layui-form-item">
	    <div class="layui-form-item layui-col-xs4">
	        <label class="layui-form-label">周报标题</label>
	        <div class="layui-input-block">
	            <input type="text" id="jobWeekTitle" name="jobWeekTitle" placeholder="请输入周报标题" class="layui-input" value="{{jobTitle}}"/>
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
			<label class="layui-form-label">本周完成工作</label>
		</div>
		<div class="layui-inline" style="float: right">
			<label class="layui-form-label">图文模式</label>
			<div class="layui-input-block winui-switch">
				<input id="weekCompleted" name="weekCompleted" lay-filter="weekCompletedImagetext" type="checkbox" lay-skin="switch" lay-text="是|否" value="true" checked="checked"/>
			</div>
		</div>
	</div>
	<div class="layui-form-item" style="align: center; display: none;">
		<textarea id="weekCompletedText" name="weekCompletedText" placeholder="请输入内容" class="layui-textarea">{{#compare1 completedJob}}{{/compare1}}</textarea>
	</div>
	<div class="layui-form-item" style="align: center;">
		<textarea id="weekCompletedContent" name="weekCompletedContent" placeholder="请输入内容" class="layui-textarea">{{{completedJob}}}</textarea>
	</div>
	
	<div class="layui-form-item">
		<div class="layui-inline">
			<label class="layui-form-label">本周工作总结</label>
		</div>
		<div class="layui-inline" style="float: right">
			<label class="layui-form-label">图文模式</label>
			<div class="layui-input-block winui-switch">
				<input id="weekWorkSummary" name="weekWorkSummary" lay-filter="weekWorkSummaryImagetext" type="checkbox" lay-skin="switch" lay-text="是|否" value="true" checked="checked"/>
			</div>
		</div>
	</div>
	<div class="layui-form-item" style="align: center; display: none;">
		<textarea id="weekWorkSummaryText" name="weekWorkSummaryText" placeholder="请输入内容" class="layui-textarea">{{#compare1 thisWorkSummary}}{{/compare1}}</textarea>
	</div>
	<div class="layui-form-item" style="align: center;">
		<textarea id="weekWorkSummaryContent" name="weekWorkSummaryContent" placeholder="请输入内容" class="layui-textarea">{{{thisWorkSummary}}}</textarea>
	</div>
	<div class="layui-form-item">
		<div class="layui-inline">
			<label class="layui-form-label">下周工作计划</label>
		</div>
		<div class="layui-inline" style="float: right">
			<label class="layui-form-label">图文模式</label>
			<div class="layui-input-block winui-switch">
				<input id="weekNextWorkPlan" name="weekNextWorkPlan" lay-filter="weekNextWorkPlanImagetext" type="checkbox" lay-skin="switch" lay-text="是|否" value="true" checked="checked"/>
			</div>
		</div>
	</div>
	<div class="layui-form-item" style="align: center; display: none;">
		<textarea id="weekNextWorkPlanText" name="weekNextWorkPlanText" placeholder="请输入内容" class="layui-textarea">{{#compare1 nextWorkPlan}}{{/compare1}}</textarea>
	</div>
	<div class="layui-form-item" style="align: center;">
		<textarea id="weekNextWorkPlanContent" name="weekNextWorkPlanContent" placeholder="请输入内容" class="layui-textarea">{{{nextWorkPlan}}}</textarea>
	</div>
	
	<div class="layui-form-item">
		<div class="layui-inline">
			<label class="layui-form-label">需协调工作</label>
		</div>
		<div class="layui-inline" style="float: right">
			<label class="layui-form-label">图文模式</label>
			<div class="layui-input-block winui-switch">
				<input id="weekCoordinaJob" name="weekCoordinaJob" lay-filter="weekCoordinaJobImagetext" type="checkbox" lay-skin="switch" lay-text="是|否" value="true" checked="checked"/>
			</div>
		</div>
	</div>
	<div class="layui-form-item" style="align: center; display: none;">
		<textarea id="weekCoordinaJobText" name="weekCoordinaJobText" placeholder="请输入内容" class="layui-textarea">{{#compare1 coordinaJob}}{{/compare1}}</textarea>
	</div>
	<div class="layui-form-item" style="align: center;">
		<textarea id="weekCoordinaJobContent" name="weekCoordinaJobContent" placeholder="请输入内容" class="layui-textarea">{{{coordinaJob}}}</textarea>
	</div>
	
	<div class="layui-form-item">
		<div class="layui-inline">
			<label class="layui-form-label">备注</label>
		</div>
	</div>
	<div class="layui-form-item" style="align: center;">
		<textarea id="weekJobRemark" name="weekJobRemark" placeholder="请输入内容" class="layui-textarea">{{#compare1 jobRemark}}{{/compare1}}</textarea>
	</div>
	<div class="layui-form-item">
        <label class="layui-form-label">附件</label>
        <div class="layui-input-block" id="enclosureUpload">
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
		<div class="layui-input-block">
			<button class="winui-btn" id="weekCancle"><language showName="com.skyeye.cancel"></language></button>
			<button class="winui-btn" lay-submit lay-filter="weekSubmit">发送</button>
		</div>
	</div>
{{/bean}}