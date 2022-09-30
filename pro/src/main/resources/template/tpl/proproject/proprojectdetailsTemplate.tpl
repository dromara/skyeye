{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <span class="detail-title" id="orderDetailTitle"></span><hr>
    </div>
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">项目基本信息</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">项目名称：</label>
        <div class="layui-input-block ver-center">
            {{projectName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">项目分类：</label>
        <div class="layui-input-block ver-center">
            {{typeName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">项目编号：</label>
        <div class="layui-input-block ver-center">
            {{projectNumber}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">所属部门：</label>
        <div class="layui-input-block ver-center">
            {{departmentName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">计划开始时间：</label>
        <div class="layui-input-block ver-center">
            {{startTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">计划完成时间：</label>
        <div class="layui-input-block ver-center">
            {{endTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">客户名称：</label>
        <div class="layui-input-block ver-center">
            {{customerName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">关联合同：</label>
        <div class="layui-input-block ver-center">
            {{contractName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">对方联系人：</label>
        <div class="layui-input-block ver-center">
            {{contactName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">固定电话：</label>
        <div class="layui-input-block ver-center">
            {{telphone}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">移动电话：</label>
        <div class="layui-input-block ver-center">
            {{mobile}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">QQ号：</label>
        <div class="layui-input-block ver-center">
            {{qq}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">邮箱：</label>
        <div class="layui-input-block ver-center">
            {{mail}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">预估工作量：</label>
        <div class="layui-input-block ver-center">
            {{estimatedWorkload}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">预估成本费用：</label>
        <div class="layui-input-block ver-center">
            {{estimatedCost}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">状态：</label>
        <div class="layui-input-block ver-center">
            {{stateName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">业务需求和目标</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">需求和目标：</label>
        <div class="layui-input-block ver-center">
            {{{businessContent}}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">附件：</label>
        <div class="layui-input-block ver-center" id="businessEnclosureInfoListBox">
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">项目组织和分工</span><hr>
	</div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">项目经理：</label>
        <div class="layui-input-block ver-center">
        	{{projectManagerName}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">项目赞助人：</label>
        <div class="layui-input-block ver-center">
            {{projectSponsorName}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">项目组成员：</label>
        <div class="layui-input-block ver-center">
            {{projectMembersName}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
    	<label class="layui-form-label">分工明细：</label>
        <div class="layui-input-block ver-center">
            {{{projectContent}}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">附件：</label>
        <div class="layui-input-block ver-center" id="projectEnclosureInfoListBox">
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">实施计划和方案</span><hr>
	</div>
	<div class="layui-form-item layui-col-xs12">
    	<label class="layui-form-label">计划和方案：</label>
        <div class="layui-input-block ver-center">
            {{{planContent}}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">附件：</label>
        <div class="layui-input-block ver-center" id="planEnclosureInfoListBox">
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">项目成果和总结</span>
		<hr>
	</div>
	<div class="layui-form-item layui-col-xs6">
		<label class="layui-form-label">实际开始时间：</label>
		<div class="layui-input-block ver-center">
			{{actualStartTime}}
		</div>
	</div>
	<div class="layui-form-item layui-col-xs6">
		<label class="layui-form-label">实际完成时间：</label>
		<div class="layui-input-block ver-center">
			{{actualEndTime}}
		</div>
	</div>
	<div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">成果和总结：</label>
        <div class="layui-input-block ver-center">
            {{{resultsContent}}}
        </div>
	</div>
	<div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">附件：</label>
		<div class="layui-input-block ver-center" id="resultsEnclosureInfoListBox">
		</div>
	</div>
{{/bean}}