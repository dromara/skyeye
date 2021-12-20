{{#bean}}
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">车辆名称<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="vehicleName" name="vehicleName" win-verify="required" placeholder="请输入车辆名称" class="layui-input" value="{{vehicleName}}" maxlength="25"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">图片<i class="red">*</i></label>
        <div class="layui-input-block">
        	<div class="upload" id="vehicleImg"></div>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">车牌<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="licensePlate" name="licensePlate" win-verify="required" placeholder="请输入车辆车牌号" class="layui-input" value="{{licensePlate}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">车辆规格</label>
        <div class="layui-input-block">
            <input type="text" id="specifications" name="specifications" win-verify="required|number" placeholder="请输入车辆准载人数" class="layui-input" value="{{specifications}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">油耗</label>
        <div class="layui-input-block">
        	<input type="text" id="oilConsumption" name="oilConsumption" placeholder="请输入油耗（km/L）" class="layui-input" value="{{oilConsumption}}" win-verify="double"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">单价</label>
        <div class="layui-input-block">
        	<input type="text" id="unitPrice" name="unitPrice" placeholder="请输入单价（元）" class="layui-input" value="{{unitPrice}}" win-verify="money"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">车辆颜色</label>
        <div class="layui-input-block">
        	<input type="text" id="vehicleColor" name="vehicleColor" placeholder="请输入车辆颜色" class="layui-input" value="{{vehicleColor}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">生产商</label>
        <div class="layui-input-block">
        	<input type="text" id="manufacturer" name="manufacturer" placeholder="请输入生产商" class="layui-input" value="{{manufacturer}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">供应商</label>
        <div class="layui-input-block">
        	<input type="text" id="supplier" name="supplier" placeholder="请输入供应商" class="layui-input" value="{{supplier}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">生产日期</label>
        <div class="layui-input-block">
        	<input type="text" id="manufactureTime" name="manufactureTime" placeholder="请选择生产日期" class="layui-input" value="{{manufactureTime}}" />
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">采购日期</label>
        <div class="layui-input-block">
        	<input type="text" id="purchaseTime" name="purchaseTime" placeholder="请选择采购日期" class="layui-input" value="{{purchaseTime}}" />
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">发动机号</label>
        <div class="layui-input-block">
        	<input type="text" id="engineNumber" name="engineNumber" placeholder="请输入发动机号" class="layui-input" value="{{engineNumber}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">车架号</label>
        <div class="layui-input-block">
        	<input type="text" id="frameNumber" name="frameNumber" placeholder="请输入车架号" class="layui-input" value="{{frameNumber}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">存放区域</label>
        <div class="layui-input-block">
        	<input type="text" id="storageArea" name="storageArea" placeholder="请输入存放区域" class="layui-input" value="{{storageArea}}" maxlength="50"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">管理人</label>
        <div class="layui-input-block">
        	<input type="text" id="vehicleAdmin" name="vehicleAdmin" placeholder="请选择管理人员" class="layui-input"/>
        	<i class="fa fa-user-plus input-icon" id="userNameSelPeople"></i>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">下次年检时间</label>
        <div class="layui-input-block">
        	<input type="text" id="nextInspectionTime" name="nextInspectionTime" placeholder="请选择下次年检时间" class="layui-input" value="{{nextInspectionTime}}" />
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">保险截止期限</label>
        <div class="layui-input-block">
        	<input type="text" id="insuranceDeadline" name="insuranceDeadline" placeholder="请选择保险截止期限" class="layui-input" value="{{insuranceDeadline}}" />
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">上次保养日期</label>
        <div class="layui-input-block">
        	<input type="text" id="prevMaintainTime" name="prevMaintainTime" placeholder="请选择上次保养日期" class="layui-input" value="{{prevMaintainTime}}" />
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">附加描述：</label>
        <div class="layui-input-block">
        	<textarea id="roomAddDesc" name="roomAddDesc"  placeholder="请输入附加描述" class="layui-textarea" style="height: 100px;" maxlength="200">{{roomAddDesc}}</textarea>
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