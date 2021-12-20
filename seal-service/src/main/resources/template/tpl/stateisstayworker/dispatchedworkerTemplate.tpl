{{#bean}}
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">工单号：</label>
        <div class="layui-input-block ver-center">
			{{orderNum}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">服务类型：</label>
        <div class="layui-input-block ver-center">
            {{serviceTypeName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">报单时间：</label>
        <div class="layui-input-block ver-center">
			{{declarationTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">联系人：</label>
        <div class="layui-input-block ver-center">
            {{contacts}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">联系电话：</label>
        <div class="layui-input-block ver-center">
            {{phone}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">邮箱：</label>
        <div class="layui-input-block ver-center">
            {{email}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">QQ：</label>
        <div class="layui-input-block ver-center">
            {{qq}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">详细地址：</label>
        <div class="layui-input-block ver-center">
            {{addressProvince}}  {{addressCity}} {{addressArea}}  {{addressTownship}}  {{addressDetailed}}
        </div>
    </div>
    
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">工单接收人<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="serviceUserId" name="serviceUserId" placeholder="请选择工单接收人" disabled class="layui-input"/>
		    <i class="fa fa-user-plus input-icon" id="serviceUserIdSelPeople"></i>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">工单协助人</label>
        <div class="layui-input-block">
        	<input type="text" id="cooperationUserId" name="cooperationUserId" placeholder="请选择工单协助人" class="layui-input"/>
		    <i class="fa fa-user-plus input-icon" id="cooperationUserIdSelPeople"></i>
        </div>
    </div>
    
    <div class="layui-form-item layui-col-xs12">
		<div class="layui-input-block">
			<button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
			<button class="winui-btn" lay-submit lay-filter="dispatchedWorker">提交</button>
		</div>
	</div>
{{/bean}}