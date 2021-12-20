{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">工序名称<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="name" name="name" value="{{name}}" win-verify="required" placeholder="请输入工序名称" class="layui-input" maxlength="25"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">工序编号<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="number" name="number" value="{{number}}" win-verify="required" placeholder="请输入工序编号" class="layui-input" maxlength="30"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">参考单价<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="unitPrice" name="unitPrice" value="{{unitPrice}}" win-verify="required|money" placeholder="请输入参考单价" class="layui-input"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">部门<i class="red">*</i></label>
        <div class="layui-input-block">
            <select id="departmentId" name="departmentId" lay-filter="departmentId" win-verify="required" >
            	
           	</select>
           	<div class="layui-form-mid layui-word-aux">该列表为当前用户所在企业的部门列表</div>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">工序类别<i class="red">*</i></label>
        <div class="layui-input-block">
            <select id="procedureType" name="procedureType" win-verify="required" >

            </select>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">工序内容</label>
        <div class="layui-input-block">
            <textarea id="content" name="content" placeholder="请输入工序内容" maxlength="200" class="layui-textarea" style="height: 100px;">{{content}}</textarea>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">工序操作员</label>
        <div class="layui-input-block">
            <input type="text" id="procedureUserId" name="procedureUserId" placeholder="请选择工序操作员" class="layui-input"/>
            <i class="fa fa-user-plus input-icon" id="procedureUserIdSelPeople"></i>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}