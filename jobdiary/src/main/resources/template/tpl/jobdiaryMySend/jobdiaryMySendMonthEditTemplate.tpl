{{#bean}}
	<div class="layui-form-item">
	    <div class="layui-form-item layui-col-xs4">
	        <label class="layui-form-label">月报标题</label>
	        <div class="layui-input-block">
	            <input type="text" id="jobMonthTitle" name="jobMonthTitle" placeholder="请输入月报标题" class="layui-input" value="{{jobTitle}}"/>
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
			<label class="layui-form-label">本月完成工作</label>
		</div>
		<div class="layui-inline" style="float: right">
			<label class="layui-form-label">图文模式</label>
			<div class="layui-input-block winui-switch">
				<input id="monthCompleted" name="monthCompleted" lay-filter="monthCompletedImagetext" type="checkbox" lay-skin="switch" lay-text="是|否" value="true" checked="checked"/>
			</div>
		</div>
	</div>
	<div class="layui-form-item" style="align: center; display: none;">
		<textarea id="monthCompletedText" name="monthCompletedText" placeholder="请输入内容" class="layui-textarea">{{#compare1 completedJob}}{{/compare1}}</textarea>
	</div>
	<div class="layui-form-item" style="align: center;">
		<textarea id="monthCompletedContent" name="monthCompletedContent" placeholder="请输入内容" class="layui-textarea">{{{completedJob}}}</textarea>
	</div>
	
	<div class="layui-form-item">
		<div class="layui-inline">
			<label class="layui-form-label">本月工作总结</label>
		</div>
		<div class="layui-inline" style="float: right">
			<label class="layui-form-label">图文模式</label>
			<div class="layui-input-block winui-switch">
				<input id="monthWorkSummary" name="monthWorkSummary" lay-filter="monthWorkSummaryImagetext" type="checkbox" lay-skin="switch" lay-text="是|否" value="true" checked="checked"/>
			</div>
		</div>
	</div>
	<div class="layui-form-item" style="align: center; display: none;">
		<textarea id="monthWorkSummaryText" name="monthWorkSummaryText" placeholder="请输入内容" class="layui-textarea">{{#compare1 thisWorkSummary}}{{/compare1}}</textarea>
	</div>
	<div class="layui-form-item" style="align: center;">
		<textarea id="monthWorkSummaryContent" name="monthWorkSummaryContent" placeholder="请输入内容" class="layui-textarea">{{{thisWorkSummary}}}</textarea>
	</div>
	<div class="layui-form-item">
		<div class="layui-inline">
			<label class="layui-form-label">下月工作计划</label>
		</div>
		<div class="layui-inline" style="float: right">
			<label class="layui-form-label">图文模式</label>
			<div class="layui-input-block winui-switch">
				<input id="monthNextWorkPlan" name="monthNextWorkPlan" lay-filter="monthNextWorkPlanImagetext" type="checkbox" lay-skin="switch" lay-text="是|否" value="true" checked="checked"/>
			</div>
		</div>
	</div>
	<div class="layui-form-item" style="align: center; display: none;">
		<textarea id="monthNextWorkPlanText" name="monthNextWorkPlanText" placeholder="请输入内容" class="layui-textarea">{{#compare1 nextWorkPlan}}{{/compare1}}</textarea>
	</div>
	<div class="layui-form-item" style="align: center;">
		<textarea id="monthNextWorkPlanContent" name="monthNextWorkPlanContent" placeholder="请输入内容" class="layui-textarea">{{{nextWorkPlan}}}</textarea>
	</div>
	
	<div class="layui-form-item">
		<div class="layui-inline">
			<label class="layui-form-label">需协调工作</label>
		</div>
		<div class="layui-inline" style="float: right">
			<label class="layui-form-label">图文模式</label>
			<div class="layui-input-block winui-switch">
				<input id="monthCoordinaJob" name="monthCoordinaJob" lay-filter="monthCoordinaJobImagetext" type="checkbox" lay-skin="switch" lay-text="是|否" value="true" checked="checked"/>
			</div>
		</div>
	</div>
	<div class="layui-form-item" style="align: center; display: none;">
		<textarea id="monthCoordinaJobText" name="monthCoordinaJobText" placeholder="请输入内容" class="layui-textarea">{{#compare1 coordinaJob}}{{/compare1}}</textarea>
	</div>
	<div class="layui-form-item" style="align: center;">
		<textarea id="monthCoordinaJobContent" name="monthCoordinaJobContent" placeholder="请输入内容" class="layui-textarea">{{{coordinaJob}}}</textarea>
	</div>
	
	<div class="layui-form-item">
		<div class="layui-inline">
			<label class="layui-form-label">备注</label>
		</div>
	</div>
	<div class="layui-form-item" style="align: center;">
		<textarea id="monthJobRemark" name="monthJobRemark" placeholder="请输入内容" class="layui-textarea">{{#compare1 jobRemark}}{{/compare1}}</textarea>
	</div>
	<div class="layui-form-item">
        <label class="layui-form-label">附件</label>
        <div class="layui-input-block" id="enclosureUpload">
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
		<div class="layui-input-block">
			<button class="winui-btn" id="monthCancle"><language showName="com.skyeye.cancel"></language></button>
			<button class="winui-btn" lay-submit lay-filter="monthSubmit">发送</button>
		</div>
	</div>
{{/bean}}