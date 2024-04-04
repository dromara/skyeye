<div class="layui-form-item layui-col-xs12">
    <label class="layui-form-label">SQL数据库<i class="red">*</i></label>
    <div class="layui-input-block">
        <select id="dataBaseId" name="dataBaseId" lay-search win-verify="required"></select>
    </div>
</div>
<div class="layui-form-item layui-col-xs12">
    <label class="layui-form-label">SQL语句<i class="red">*</i></label>
    <div class="layui-input-block">
        <textarea id="sqlData" name="sqlData" class="layui-textarea" style="height: 100px;"></textarea>
    </div>
</div>


<!-- analysisHeaderTemplate -->
<script type="text/x-handlebars-template" id="analysisHeaderTemplate">
    <tr>
        <th style="width: 30px;"></th>
        <th style="width: 150px;">字段<i class="red">*</i></th>
        <th style="width: 150px;">含义</th>
        <th style="width: 120px;">字段类型</th>
        <th style="width: 120px;">字段长度</th>
        <th style="width: 120px;">字段精度</th>
        <th style="width: 200px;">备注</th>
    </tr>
</script>

<!-- analysisTemplate -->
<script type="text/x-handlebars-template" id="analysisTemplate">
    <tr trcusid="{{trId}}">
        <td><input type="checkbox" rowId="{{id}}" lay-filter="checkboxProperty" name="tableCheckRow"/></td>
        <td><input type="text" class="layui-input" id="{{key}}" win-verify="required" /></td>
        <td><input type="text" class="layui-input" id="{{name}}" /></td>
        <td><input type="text" class="layui-input" id="{{dataType}}" /></td>
        <td><input type="text" class="layui-input" id="{{dataLength}}" /></td>
        <td><input type="text" class="layui-input" id="{{dataPrecision}}" /></td>
        <td><input type="text" class="layui-input" id="{{remark}}" /></td>
    </tr>
</script>