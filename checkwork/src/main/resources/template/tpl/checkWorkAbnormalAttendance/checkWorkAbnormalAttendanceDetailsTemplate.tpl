{{#bean}}
	<div class="layui-form-item">
        <label class="layui-form-label">申请人：</label>
        <div class="layui-input-block ver-center">
        	{{userName}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">申诉内容：</label>
        <div class="layui-input-block ver-center">
        	{{checkDate}}{{situation}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">原因类型：</label>
        <div class="layui-input-block ver-center">
        	{{appealName}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">具体原因：</label>
       	<div class="layui-input-block ver-center">
            {{appealReason}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">申诉时间：</label>
        <div class="layui-input-block ver-center">
        	{{createTime}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">申诉状态：</label>
        <div class="layui-input-block ver-center {{stateColor}}">
        	{{state}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">审批人：</label>
        <div class="layui-input-block ver-center">
        	{{userName}}
        </div>
    </div>
    <div class="layui-form-item" id="approvalTime">
        <label class="layui-form-label">审批时间：</label>
        <div class="layui-input-block ver-center">
        	{{approvalTime}}
        </div>
    </div>
    <div class="layui-form-item" id="appealRemark">
        <label class="layui-form-label">审核意见：</label>
        <div class="layui-input-block ver-center">
        	{{appealRemark}}
        </div>
    </div>
{{/bean}}