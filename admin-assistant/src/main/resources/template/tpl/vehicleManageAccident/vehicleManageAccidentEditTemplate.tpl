{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">标题<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
        	{{accidentTitle}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">车牌号<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
	        {{licensePlate}}
	    </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">司机姓名<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="driver" name="driver" win-verify="required" placeholder="请输入司机姓名" class="layui-input" value="{{driver}}" maxlength="10"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">事故时间<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="accidentTime" name="accidentTime" win-verify="required" placeholder="请输入事故时间" class="layui-input" value="{{accidentTime}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">事故地点<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="accidentArea" name="accidentArea" win-verify="required" placeholder="请输入事故地点" class="layui-input" value="{{accidentArea}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">责任认定</label>
        <div class="layui-input-block">
        	<input type="text" id="confirmationResponsibility" name="confirmationResponsibility" placeholder="请输入责任认定" class="layui-input" value="{{confirmationResponsibility}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">维修厂家</label>
        <div class="layui-input-block">
        	<input type="text" id="manufacturer" name="manufacturer" placeholder="请输入维修厂家" class="layui-input" value="{{manufacturer}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">送修时间段</label>
		<div class="layui-input-block">
			<input type="text" class="layui-input" id="repairTime" placeholder="请选择送修时间段" value="{{repairTime}}"/>
		</div>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">维修费用</label>
        <div class="layui-input-block">
        	<input type="text" id="repairPrice" name="repairPrice" placeholder="请输入维修费用（元）" class="layui-input" value="{{repairPrice}}" win-verify="money"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">车损费</label>
        <div class="layui-input-block">
        	<input type="text" id="lossFeePrice" name="lossFeePrice" placeholder="请输入车损费（元）" class="layui-input" value="{{lossFeePrice}}" win-verify="money"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">保险理赔金额</label>
        <div class="layui-input-block">
        	<input type="text" id="claimsFeePrice" name="claimsFeePrice" placeholder="请输入保险理赔金额（元）" class="layui-input" value="{{claimsFeePrice}}" win-verify="money"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">司机承担费用</label>
        <div class="layui-input-block">
        	<input type="text" id="driverBearPrice" name="driverBearPrice" placeholder="请输入司机承担费用（元）" class="layui-input" value="{{driverBearPrice}}" win-verify="money"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">事故详情</label>
        <div class="layui-input-block">
        	<textarea id="accidentDetail" name="accidentDetail"  placeholder="请输入事故详情" class="layui-textarea" style="height: 100px;" maxlength="200">{{accidentDetail}}</textarea>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">维修内容</label>
        <div class="layui-input-block">
        	<textarea id="repairContent" name="repairContent"  placeholder="请输入维修内容" class="layui-textarea" style="height: 100px;" maxlength="200">{{repairContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">相关附件</label>
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