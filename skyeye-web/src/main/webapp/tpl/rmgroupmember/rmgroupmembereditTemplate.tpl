{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">分类<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
        	{{typeName}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">分组<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
        	{{groupName}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">HTML内容<i class="red">*</i></label>
        <div class="layui-input-block">
        	<textarea id="htmlContent" name="htmlContent" win-verify="required" placeholder="请填写代码块" class="layui-textarea" style="height: 100px;">{{htmlContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">HTML-JS内容</label>
        <div class="layui-input-block">
        	<textarea id="htmlJsContent" name="htmlJsContent" placeholder="请填写代码块" class="layui-textarea" style="height: 100px;">{{htmlJsContent}}</textarea>
        	<div id="htmlJsContentScript"></div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">效果图<i class="red">*</i></label>
        <div class="layui-col-xs8">
            <div class="layui-col-xs12" id="printPic">
                
            </div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">预览图<i class="red">*</i></label>
        <div class="layui-col-xs6">
        	<div class="upload" id="printsPicUrl"></div>
            <span class="help-block m-b-none">格式：png,jpg,jpeg,gif</span>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">WXML内容<i class="red">*</i></label>
        <div class="layui-input-block">
        	<textarea id="wxmlContent" name="wxmlContent" win-verify="required" placeholder="请填写代码块" class="layui-textarea" style="height: 100px;">{{wxmlContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">WXML-JS数据内容</label>
        <div class="layui-input-block">
        	<textarea id="wxmlJsDataContent" name="wxmlJsDataContent" placeholder="请填写代码块" class="layui-textarea" style="height: 100px;">{{wxmlJsDataContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">WXML-JS方法内容</label>
        <div class="layui-input-block">
        	<textarea id="wxmlJsMethodContent" name="wxmlJsMethodContent" placeholder="请填写代码块" class="layui-textarea" style="height: 100px;">{{wxmlJsMethodContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">WXML-JS初始化内容</label>
        <div class="layui-input-block">
        	<textarea id="wxmlJsMethodCreateContent" name="wxmlJsMethodCreateContent" placeholder="请填写代码块" class="layui-textarea" style="height: 100px;">{{wxmlJsMethodCreateContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">保存</button>
        </div>
    </div>
{{/bean}}