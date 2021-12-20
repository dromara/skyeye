{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <span class="detail-title" id="orderDetailTitle"></span><hr>
    </div>
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">基础信息</span><hr>
	</div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">工单号：</label>
        <div class="layui-input-block ver-center">
            {{orderNum}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">状态：</label>
        <div class="layui-input-block ver-center">
            {{stateName}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">服务类型：</label>
        <div class="layui-input-block ver-center">
            {{serviceTypeName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">报单时间：</label>
        <div class="layui-input-block ver-center">
            {{declarationTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">客户：</label>
        <div class="layui-input-block ver-center" id="customerName">
            {{customerName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">联系人：</label>
        <div class="layui-input-block ver-center">
            {{contacts}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">联系电话：</label>
        <div class="layui-input-block ver-center">
            {{phone}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">邮箱：</label>
        <div class="layui-input-block ver-center">
            {{email}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">QQ号：</label>
        <div class="layui-input-block ver-center">
            {{qq}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">所在地区：</label>
        <div class="layui-input-block ver-center">
            {{addressProvince}}  {{addressCity}}  {{addressArea}}  {{addressTownship}}  {{addressDetailed}}
        </div>
    </div>
    
    <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">产品信息</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">产品名称：</label>
        <div class="layui-input-block ver-center" id="productName">
            {{productName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">规格型号：</label>
        <div class="layui-input-block ver-center">
            {{productNorms}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">序列号：</label>
        <div class="layui-input-block ver-center">
            {{productSerialNum}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">质保类型：</label>
        <div class="layui-input-block ver-center">
            {{productWarranty}}
        </div>
    </div>
    
    <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">工单信息</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">紧急程度：</label>
        <div class="layui-input-block ver-center">
            {{urgencyName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">处理方式：</label>
        <div class="layui-input-block ver-center">
            {{modeName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">服务内容：</label>
        <div class="layui-input-block ver-center">
            {{content}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">工单接收人：</label>
        <div class="layui-input-block ver-center">
        	{{receiver}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">指定预约时间：</label>
        <div class="layui-input-block ver-center">
        	{{pointSubscribeTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">工单协助人：</label>
        <div class="layui-input-block ver-center" id="cooperationUserId">
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">相关照片：</label>
        <div class="layui-input-block ver-center" id="sheetPicture">
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">相关附件：</label>
        <div class="layui-input-block ver-center" id="enclosureUpload">
        </div>
    </div>
    
    <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">接单信息</span><hr>
	</div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">预约时间：</label>
        <div class="layui-input-block ver-center">
            {{subscribeTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">接单备注：</label>
        <div class="layui-input-block ver-center">
            {{receiptRemark}}
        </div>
    </div>
    
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">签到信息</span><hr>
	</div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">签到地址：</label>
        <div class="layui-input-block ver-center">
            {{registerAddress}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">签到时间：</label>
        <div class="layui-input-block ver-center">
            {{registerTime}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">签到备注：</label>
        <div class="layui-input-block ver-center">
            {{remark}}
        </div>
    </div>
	
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">完工信息</span><hr>
	</div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">实际开工时间：</label>
        <div class="layui-input-block ver-center">
            {{comStarTime}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">实际完工时间：</label>
        <div class="layui-input-block ver-center">
            {{comTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">故障类型：</label>
        <div class="layui-input-block ver-center">
        	{{faultName}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">工时：</label>
        <div class="layui-input-block ver-center">
        	{{comWorkTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">故障关键组件：</label>
        <div class="layui-input-block ver-center" id="faultKeyPartsName">
        	{{dPartName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">真实故障：</label>
        <div class="layui-input-block ver-center">
        	{{actualFailure}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">解决方案：</label>
        <div class="layui-input-block ver-center">
        	{{solution}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">完工情况：</label>
        <div class="layui-input-block ver-center">
            {{comExecution}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">完工拍照：</label>
        <div class="layui-input-block ver-center" id="comPic">
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">完工附件：</label>
        <div class="layui-input-block ver-center" id="comEnclosureInfo">
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">完工备注：</label>
        <div class="layui-input-block ver-center">
        	{{comRemark}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">应收材料费：</label>
        <div class="layui-input-block ver-center">
        	{{materialCost}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">应收服务费：</label>
        <div class="layui-input-block ver-center">
        	{{coverCost}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">应收其他费：</label>
        <div class="layui-input-block ver-center">
        	{{otherCost}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">应收合计：</label>
        <div class="layui-input-block ver-center">
        	{{allPrice}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">经办人：</label>
        <div class="layui-input-block ver-center">
        	{{createName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">客户结算状态：</label>
        <div class="layui-input-block ver-center stateName">
        	
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">网点结算状态：</label>
        <div class="layui-input-block ver-center stateName">
        	
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">员工结算状态：</label>
        <div class="layui-input-block ver-center stateName">
        	
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">厂商结算状态：</label>
        <div class="layui-input-block ver-center stateName">
        	
        </div>
    </div>
	
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">评价信息</span><hr>
	</div>
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">评价类型：</label>
        <div class="layui-input-block ver-center">
        	{{evaluateTypeName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">评价内容：</label>
        <div class="layui-input-block ver-center">
        	{{evaluateContent}}
        </div>
    </div>
	
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">配件使用明细</span><hr>
	</div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">配件使用明细：</label>
        <div class="layui-input-block">
        	<table class="layui-table">
                <thead>
                    <tr>
                        <th style="min-width: 120px;">配件名称</th>
                        <th style="min-width: 80px;">单位</th>
                        <th style="min-width: 80px;">数量</th>
                        <th style="min-width: 80px;">单价</th>
                        <th style="min-width: 80px;">金额</th>
                        <th style="min-width: 100px;">备注</th>
                    </tr>
                </thead>
                <tbody id="useTable" class="insurance-table">
					{{#each materialMation}}
						<tr>
							<td>{{materialName}}</td>
							<td>{{unitName}}</td>
							<td>{{operNumber}}</td>
							<td>{{unitPrice}}</td>
							<td>{{allPrice}}</td>
							<td>{{remark}}</td>
						</tr>
					{{/each}}
                </tbody>
			</table>
        </div>
    </div>
	
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">情况反馈单</span><hr>
	</div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">情况反馈单：</label>
        <div class="layui-input-block">
        	<table class="layui-table">
                <thead>
                    <tr>
                        <th style="min-width: 80px;">反馈类型</th>
                        <th style="min-width: 80px;">反馈人</th>
                        <th style="min-width: 140px;">录入时间</th>
                        <th style="min-width: 100px;">反馈内容</th>
                        <th style="min-width: 80px;">操作</th>
                    </tr>
                </thead>
                <tbody id="useTable" class="insurance-table">
					{{#each feedbackMation}}
						<tr>
							<td>{{typeName}}</td>
							<td>{{feedbackUserName}}</td>
							<td>{{feedbackCreateTime}}</td>
							<td>{{content}}</td>
							<td><a class="layui-btn layui-btn-xs layui-btn-normal details" rowid="{{id}}">详情</a></td>
						</tr>
					{{/each}}
                </tbody>
			</table>
        </div>
    </div>
	
{{/bean}}