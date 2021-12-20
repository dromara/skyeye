{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title" id="exexplaintormpropertyTitle"></span><hr>
	</div>
	<div class="layui-form-item layui-col-xs12" id="exexplaintormpropertyContent" style="padding: 0px 15px; font-size: 12px; color: grey; display: contents;">
    </div>
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">属性内容</span><hr>
	</div>
    <div class="layui-form-item">
        <label class="layui-form-label">属性别名<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="title" name="title" win-verify="required" placeholder="请输入属性别名" class="layui-input" value="{{title}}"/>
            <div class="layui-form-mid layui-word-aux">例如：宽度</div>
        </div>
    </div>
	<div class="layui-form-item">
        <label class="layui-form-label">属性标签<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="propertyTag" name="propertyTag" win-verify="required" placeholder="请输入属性标签" class="layui-input" value="{{propertyTag}}"/>
            <div class="layui-form-mid layui-word-aux">例如：width</div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">属性单位</label>
        <div class="layui-input-block">
            <input type="text" id="propertyUnit" name="propertyUnit" placeholder="请输入属性单位" class="layui-input" value="{{propertyUnit}}"/>
            <div class="layui-form-mid layui-word-aux">例如：width:100%;单位为：%</div>
        </div>
    </div>
	<div class="layui-form-item">
        <label class="layui-form-label">外部属性<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="propertyOut" value="1" title="是" />
            <input type="radio" name="propertyOut" value="2" title="否" /><br/><br/>
            <div class="layui-form-mid layui-word-aux" style="float: none;">例如：width为内部属性，class为外部属性</div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">子查询<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="selChildData" value="1" title="是" lay-filter="selChildData"/>
            <input type="radio" name="selChildData" value="2" title="否" lay-filter="selChildData"/>
        </div>
    </div>
    <div class="layui-form-item" id="dataShowModel">
        <label class="layui-form-label">数据模板<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <select id="displayTemplateId" name="displayTemplateId" class="layui-input" lay-search>
                <option value="">全部</option>
            </select>
        </div>
    </div>
	<div class="layui-form-item">
        <label class="layui-form-label">展现形式<i class="red">*</i></label>
        <div class="layui-input-block">
            <select id="dsFormContentId" name="dsFormContentId" class="layui-input" win-verify="required" lay-filter="selectParent" lay-search>
                <option value="">全部</option>
            </select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">HTML模板</label>
        <div class="layui-input-block">
            <textarea id="htmlModelContent">{{htmlModelContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">JS模板</label>
        <div class="layui-input-block">
            <textarea id="jsModelContent">{{jsModelContent}}</textarea>
        </div>
    </div>
  	<div class="layui-form-item">
        <label class="layui-form-label">HTML内容<i class="red">*</i></label>
        <div class="layui-input-block">
            <textarea id="htmlContent">{{htmlContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">JS内容</label>
        <div class="layui-input-block">
            <textarea id="jsContent">{{jsContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">JS依赖文件</label>
        <div class="layui-input-block">
            <textarea id="jsRelyOnContent">{{jsRelyOn}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}