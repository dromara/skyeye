{{#bean}}
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">客户：</label>
        <div class="layui-input-block ver-center">
            {{customerId}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">所在城市：</label>
        <div class="layui-input-block ver-center">
            {{city}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">详细地址：</label>
        <div class="layui-input-block ver-center">
            {{detailAddress}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">合同名称：</label>
        <div class="layui-input-block ver-center">
            {{title}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">合同编号：</label>
        <div class="layui-input-block ver-center">
            {{num}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">合同金额：</label>
        <div class="layui-input-block ver-center">
            {{price}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">签约日期：</label>
        <div class="layui-input-block ver-center">
            {{signingTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">生效日期：</label>
        <div class="layui-input-block ver-center">
            {{effectTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">服务期至：</label>
        <div class="layui-input-block ver-center">
            {{serviceEndTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">联系人：</label>
        <div class="layui-input-block ver-center">
            {{contacts}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">固定电话：</label>
        <div class="layui-input-block ver-center">
            {{workPhone}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">移动电话：</label>
        <div class="layui-input-block ver-center">
            {{mobilePhone}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">QQ号：</label>
        <div class="layui-input-block ver-center">
            {{qq}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">邮箱：</label>
        <div class="layui-input-block ver-center">
            {{email}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">合同所属部门：</label>
        <div class="layui-input-block ver-center">
            {{departmentId}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">合同关联人员：</label>
        <div class="layui-input-block ver-center">
            {{relationUserId}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">状态：</label>
        <div class="layui-input-block ver-center">
            {{stateName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">创建人：</label>
        <div class="layui-input-block ver-center">
        	{{userName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">主要技术条款：</label>
        <div class="layui-input-block ver-center">
            {{technicalTerms}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">主要商务条款：</label>
        <div class="layui-input-block ver-center">
            {{businessTerms}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">附件：</label>
        <div class="layui-input-block ver-center" id="enclosureUploadBtn">
        </div>
    </div>
{{/bean}}