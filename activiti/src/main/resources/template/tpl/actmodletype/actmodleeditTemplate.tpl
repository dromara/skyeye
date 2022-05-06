{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">类型实体<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="title" name="title" win-verify="required" placeholder="请输入类型实体名称" class="layui-input" value="{{title}}"/>
        </div>
    </div>
    <div class="layui-form-item">
    	<label class="layui-form-label">工作流模型<i class="red">*</i></label>
    	<div class="layui-input-block">
    		<input type="text" id="actId" name="actId" win-verify="required" class="layui-input" value="{{actId}}" readonly="true"/>
    	</div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">页面类型<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="pageTypes" value="1" title="指定页面" lay-filter="pageTypes"/>
            <input type="radio" name="pageTypes" value="2" title="表单页面" lay-filter="pageTypes"/>
        </div>
    </div>
    <div class="layui-form-item TypeIsOne">
        <label class="layui-form-label">新增页面<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="pageUrl" name="pageUrl" placeholder="请输入页面URL" class="layui-input" value="{{pageUrl}}"/>
        	<div class="layui-form-mid layui-word-aux">格式为：../../tpl/model/modellist.html</div>
        </div>
    </div>
    <div class="layui-form-item TypeIsOne">
        <label class="layui-form-label">编辑页面<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="editPageUrl" name="editPageUrl" placeholder="请输入页面URL" class="layui-input" value="{{editPageUrl}}"/>
        	<div class="layui-form-mid layui-word-aux">格式为：../../tpl/model/modellist.html</div>
        </div>
    </div>
    <div class="layui-form-item TypeIsOne">
        <label class="layui-form-label">撤销接口<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="revokeMapping" name="revokeMapping" placeholder="请输入撤销接口" class="layui-input" value="{{revokeMapping}}"/>
        	<div class="layui-form-mid layui-word-aux">格式为：bbb001；直接填写接口名即可</div>
        </div>
    </div>
    <div class="layui-form-item TypeIsTwo">
        <label class="layui-form-label">表单页面<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select lay-filter="dsFormId" lay-search="" id="dsFormId" name="dsFormId">
            
            </select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">图标类型<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="menuIconType" value="1" title="Icon" lay-filter="menuIconType" checked/>
            <input type="radio" name="menuIconType" value="2" title="图片" lay-filter="menuIconType" />
        </div>
    </div>
    <div class="layui-form-item menuIconTypeIsOne">
        <label class="layui-form-label">图标<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="menuIcon" name="menuIcon" placeholder="请输入图标src或者class" class="layui-input" value="{{menuIcon}}" />
        </div>
    </div>
    <div class="layui-form-item menuIconTypeIsOne">
        <label class="layui-form-label">图标预览</label>
        <div class="layui-input-block">
            <div class="layui-col-xs12">
                <div class="layui-col-xs2">
                    <div class="winui-icon winui-icon-font" style="width: 60px; height: 60px;"><i id="iconShow" class="" style="font-size: 48px; line-height: 65px;"></i></div>
                </div>
                <div class="layui-col-xs5">
                    <div class="layui-input-inline" style="width: 120px;">
                        <input type="text" value="" class="layui-input" placeholder="请选择图标颜色" id="menuIconColorinput" />
                    </div>
                    <div id="menuIconColor"></div>
                </div>
            </div>
        </div>
    </div>
    <div class="layui-form-item menuIconTypeIsTwo">
        <label class="layui-form-label">图片<i class="red">*</i></label>
        <div class="layui-input-block">
            <div class="upload" id="menuIconPic"></div>
        </div>
    </div>
    <div class="layui-form-item">
    	<label class="layui-form-label">背景颜色<i class="red">*</i></label>
     	<div class="layui-input-block">
       		<div class="layui-input-inline" style="width: 130px;">
	            <input type="text" class="layui-input" placeholder="请选择图标颜色" id="backgroundColorInput" win-verify="required" value="{{backgroundColor}}"/>
	        </div>
        	<div id="backgroundColor"></div>
    	</div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">回调地址</label>
        <div class="layui-input-block">
        	<input type="text" id="tokenUrl" name="tokenUrl" placeholder="请输入回调地址URL" class="layui-input" win-verify="url" value="{{tokenUrl}}"/>
        	<div class="layui-form-mid layui-word-aux">格式为：http://localhost:8081/tpl/model.html</div>
        	<div class="layui-form-mid layui-word-aux">流程执行完之后进行该地址回调。</div>
        	<div class="layui-form-mid layui-word-aux">动态表单回调参数以自定义id为准。</div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">是否常用/热门<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="commonUsed" value="1" title="是" />
            <input type="radio" name="commonUsed" value="2" title="否" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">备注说明</label>
        <div class="layui-input-block">
            <textarea id="remark" name="remark" placeholder="请输入备注说明" maxlength="200" class="layui-textarea" style="height: 100px;">{{remark}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}