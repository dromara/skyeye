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
        <label class="layui-form-label">派工时间：</label>
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
    
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">签到备注</label>
        <div class="layui-input-block">
        	<textarea id="remark" name="remark" placeholder="请输入签到备注" class="layui-textarea" style="height: 100px;" maxlength="200"></textarea>
        </div>
    </div>
    
    <div class="layui-form-item layui-col-xs12">
		<div class="layui-input-block">
			<button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
			<button class="winui-btn" lay-submit lay-filter="signIn">签到</button>
		</div>
	</div>
{{/bean}}