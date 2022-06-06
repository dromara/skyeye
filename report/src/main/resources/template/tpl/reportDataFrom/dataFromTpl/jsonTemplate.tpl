<div class="layui-form-item layui-col-xs12">
    <label class="layui-form-label">JSON数据<i class="red">*</i></label>
    <div class="layui-input-block">
        <textarea id="jsonData" name="jsonData" class="layui-textarea" style="height: 100px;"></textarea>
    </div>
</div>

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
        <td><input type="text" class="layui-input" id="{{title}}" /></td>
        <td><input type="text" class="layui-input" id="{{remark}}" /></td>
    </tr>
</script>