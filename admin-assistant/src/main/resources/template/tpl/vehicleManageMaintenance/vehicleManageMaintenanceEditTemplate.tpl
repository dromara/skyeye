{{#bean}}
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">主题<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
        	{{maintenanceTitle}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">车牌号<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
	        {{licensePlate}}
	    </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">类型<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="maintenanceType" value="1" title="维修" />
            <input type="radio" name="maintenanceType" value="2" title="保养" />
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">维修保养公司</label>
        <div class="layui-input-block">
        	<input type="text" id="maintenanceCompany" name="maintenanceCompany" placeholder="请输入维修保养公司" class="layui-input" value="{{maintenanceCompany}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">费用<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="number" id="maintenancePrice" name="maintenancePrice" win-verify="required|double" placeholder="请输入费用（元）" class="layui-input" value="{{maintenancePrice}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">时间段<i class="red">*</i></label>
		<div class="layui-input-block">
			<input type="text" class="layui-input" id="maintenanceTime" win-verify="required" placeholder="请选择维修保养时间段" value="{{maintenanceTime}}"/>
		</div>
	</div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">具体内容：<i class="red">*</i></label>
        <div class="layui-input-block">
        	<textarea id="concreteContent" name="concreteContent"  placeholder="请输入具体内容" class="layui-textarea" style="height: 100px;" maxlength="200" win-verify="required">{{concreteContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">备注：</label>
        <div class="layui-input-block">
        	<textarea id="roomAddDesc" name="roomAddDesc"  placeholder="请输入备注" class="layui-textarea" style="height: 100px;" maxlength="200">{{roomAddDesc}}</textarea>
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