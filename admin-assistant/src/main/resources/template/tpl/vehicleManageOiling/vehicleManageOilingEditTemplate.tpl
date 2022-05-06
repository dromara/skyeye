{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">标题<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
        	{{oilTitle}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">车牌号<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
	        {{licensePlate}}
	    </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">加油日期<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="oilTime" name="oilTime" win-verify="required" placeholder="请输入加油日期" class="layui-input" value="{{oilTime}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">加油容量<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="number" id="oilCapacity" name="oilCapacity" win-verify="required|double" placeholder="请输入加油容量（升）" class="layui-input" value="{{oilCapacity}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">加油金额<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="number" id="oilPrice" name="oilPrice" win-verify="required|double" placeholder="请输入加油金额（元）" class="layui-input" value="{{oilPrice}}"/>
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