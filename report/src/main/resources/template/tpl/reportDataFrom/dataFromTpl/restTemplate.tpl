<div class="layui-form-item layui-col-xs6">
    <label class="layui-form-label">所属服务<i class="red">*</i></label>
    <div class="layui-input-block">
        <select id="serviceStr" name="serviceStr" lay-filter="serviceStr" win-verify="required" lay-search=""></select>
    </div>
</div>
<div class="layui-form-item layui-col-xs6">
    <label class="layui-form-label">接口地址<i class="red">*</i></label>
    <div class="layui-input-block">
        <input type="text" id="restUrl" name="restUrl" win-verify="required" placeholder="请输入接口地址" class="layui-input"/>
    </div>
</div>
<div class="layui-form-item layui-col-xs6">
    <label class="layui-form-label">请求类型<i class="red">*</i></label>
    <div class="layui-input-block">
        <select id="restMethod" name="restMethod" lay-search win-verify="required">
            <option value="POST">POST</option>
            <option value="GET">GET</option>
            <option value="PUT">PUT</option>
            <option value="PATCH">PATCH</option>
            <option value="DELETE">DELETE</option>
            <option value="COPY">COPY</option>
            <option value="HEAD">HEAD</option>
            <option value="OPTIONS">OPTIONS</option>
            <option value="LINK">LINK</option>
            <option value="UNLINK">UNLINK</option>
            <option value="PURGE">PURGE</option>
            <option value="LOCK">LOCK</option>
            <option value="UNLOCK">UNLOCK</option>
            <option value="PROPFIND">PROPFIND</option>
            <option value="VIEW">VIEW</option>
        </select>
    </div>
</div>
<div class="layui-form-item layui-col-xs12">
    <label class="layui-form-label">请求头<i class="red">*</i></label>
    <div class="layui-input-block">
        <div class="winui-toolbar">
            <div class="winui-tool" style="text-align: left;">
                <button id="addRow" class="winui-toolbtn" type="button"><i class="fa fa-plus" aria-hidden="true"></i>新增行</button>
                <button id="deleteRow" class="winui-toolbtn" type="button"><i class="fa fa-trash-o" aria-hidden="true"></i>删除行</button>
            </div>
        </div>
        <table class="layui-table">
            <thead>
            <tr>
                <th style="width: 30px;"></th>
                <th style="width: 150px;">KEY<i class="red">*</i></th>
                <th style="width: 150px;">VALUE<i class="red">*</i></th>
                <th style="width: 200px;">DESCRIPTION</th>
            </tr>
            </thead>
            <tbody id="restHeaderTable" class="insurance-table">
            </tbody>
        </table>
    </div>
</div>
<div class="layui-form-item layui-col-xs12">
    <label class="layui-form-label">请求体</label>
    <div class="layui-input-block">
        <textarea id="requestBody" name="requestBody" class="layui-textarea" style="height: 100px;"></textarea>
    </div>
</div>

<!-- headerTemplate -->
<script type="text/x-handlebars-template" id="headerTemplate">
    <tr trcusid="{{trId}}">
        <td><input type="checkbox" rowId="{{id}}" lay-filter="checkboxProperty" name="tableCheckRow"/></td>
        <td><input type="text" class="layui-input" id="{{headerKey}}" win-verify="required" /></td>
        <td><input type="text" class="layui-input" id="{{headerValue}}" win-verify="required" /></td>
        <td><input type="text" class="layui-input" id="{{headerDescription}}" /></td>
    </tr>
</script>


<!-- analysisHeaderTemplate -->
<script type="text/x-handlebars-template" id="analysisHeaderTemplate">
    <tr>
        <th style="width: 30px;"></th>
        <th style="width: 150px;">字段<i class="red">*</i></th>
        <th style="width: 150px;">含义</th>
        <th style="width: 200px;">备注</th>
    </tr>
</script>

<!-- analysisTemplate -->
<script type="text/x-handlebars-template" id="analysisTemplate">
    <tr trcusid="{{trId}}">
        <td><input type="checkbox" rowId="{{id}}" lay-filter="checkboxProperty" name="tableCheckRow"/></td>
        <td><input type="text" class="layui-input" id="{{key}}" win-verify="required" /></td>
        <td><input type="text" class="layui-input" id="{{name}}" /></td>
        <td><input type="text" class="layui-input" id="{{remark}}" /></td>
    </tr>
</script>