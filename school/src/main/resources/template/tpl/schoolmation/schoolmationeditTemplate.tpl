{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">学校名称<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="schoolName" name="schoolName" win-verify="required" placeholder="请输入学校名称" class="layui-input" maxlength="50" value="{{schoolName}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">学校类型<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="schoolType" value="1" title="父学校" lay-filter="schoolType"/>
            <input type="radio" name="schoolType" value="2" title="附属学校" lay-filter="schoolType" />
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12" id="parentIdBox">
        <label class="layui-form-label">父学校<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select id="OverAllSchool" lay-filter="OverAllSchool" lay-search="">
        		<option value="">请选择</option>
        	</select>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">经度<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="longitude" win-verify="required" placeholder="请进行地图定位" disabled class="layui-input"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">纬度<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="latitude" win-verify="required" placeholder="请进行地图定位" disabled class="layui-input"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">地址<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="userAddressInput" win-verify="required" placeholder="请进行地图定位" disabled class="layui-input"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">地图定位<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
        	<div class="layui-col-xs12" style="height:400px;">
                <div class="col-sm-12" style="height:100%;" id="qpDiv">
                    <div id="container" class="form-group" style="height:100%;">
                        
                    </div>
                    <div id="tip">
					    <font style="float: left;">省：</font><select id='province' win-verify="required" lay-filter="province" lay-search=""></select>
					    <font style="float: left;">市：</font><select id='city' win-verify="required" lay-filter="city" lay-search=""></select>
					    <font style="float: left;">区：</font><select id='district' win-verify="required" lay-filter="district" lay-search=""></select>
						<font style="float: left;" id="qp">全屏</font>
					</div>
                </div>
			</div>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">数据权限<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="power" value="1" title="查看所有" lay-filter="power" />
            <input type="radio" name="power" value="2" title="只看本校" lay-filter="power" />
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">学校简介</label>
        <div class="layui-input-block">
        	<textarea id="content" name="content" style="display: none;">{{schoolDesc}}</textarea>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}