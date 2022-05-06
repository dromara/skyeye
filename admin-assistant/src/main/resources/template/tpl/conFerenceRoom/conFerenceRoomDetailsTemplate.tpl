{{#bean}}
	<div class="layui-form-item">
		<label class="layui-form-label">名称：</label>
		<div class="layui-input-block ver-center">
			{{roomName}}
		</div>
	</div>
	<div class="layui-form-item">
		<label class="layui-form-label">图片：</label>
		<div class="layui-input-block ver-center">
			<img src="" class="photo-img" id="roomImg">
		</div>
	</div>
	<div class="layui-form-item">
	    <label class="layui-form-label">编号：</label>
	    <div class="layui-input-block ver-center">
	        {{roomNum}}
	    </div>
	</div>
	<div class="layui-form-item">
	    <label class="layui-form-label">状态：</label>
	    <div class="layui-input-block ver-center {{colorClass}}">
	        {{state}}
	    </div>
	</div>
	<div class="layui-form-item">
	    <label class="layui-form-label">容量：</label>
	    <div class="layui-input-block ver-center">
	        {{roomCapacity}}
	    </div>
	</div>
    <div class="layui-form-item">
        <label class="layui-form-label">位置：</label>
        <div class="layui-input-block ver-center">
            {{roomPosition}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">内部设备：</label>
        <div class="layui-input-block ver-center">
        	{{roomEquipment}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">管理人：</label>
        <div class="layui-input-block ver-center">
        	{{roomAdmin}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">相关描述：</label>
        <div class="layui-input-block ver-center">
        	{{roomAddDesc}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">相关附件：</label>
        <div class="layui-input-block ver-center" id="enclosureUploadBtn">
        </div>
    </div>
{{/bean}}