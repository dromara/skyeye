{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">客户信息</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">客户名称：</label>
        <div class="layui-input-block ver-center">
            {{customerId}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">所属行业：</label>
        <div class="layui-input-block ver-center">
            {{industryId}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">所在城市：</label>
        <div class="layui-input-block ver-center">
            {{city}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">详细地址：</label>
        <div class="layui-input-block ver-center">
            {{detailAddress}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">当前状态：</label>
        <div class="layui-input-block ver-center" id="nowState">
            {{industryId}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">商机信息</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">商机名称：</label>
        <div class="layui-input-block ver-center">
            {{title}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">商机来源：</label>
        <div class="layui-input-block ver-center">
        	{{fromId}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">预成交金：</label>
        <div class="layui-input-block ver-center">
            {{estimatePrice}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">预结单日：</label>
        <div class="layui-input-block ver-center">
        	{{estimateEndTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">联系人：</label>
        <div class="layui-input-block ver-center">
            {{contacts}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">部门：</label>
        <div class="layui-input-block ver-center">
            {{department}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">职务：</label>
        <div class="layui-input-block ver-center">
            {{job}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">办公电话：</label>
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
        <label class="layui-form-label">邮件：</label>
        <div class="layui-input-block ver-center">
            {{email}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">QQ：</label>
        <div class="layui-input-block ver-center">
            {{qq}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">主要业务：</label>
        <div class="layui-input-block ver-center">
        	{{businessNeed}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">附件资料：</label>
        <div class="layui-input-block ver-center" id="enclosureUploadBtn">
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">我方负责信息</span><hr>
	</div>
   	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">所属部门：</label>
        <div class="layui-input-block ver-center">
        	{{subDepartments}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">负责人：</label>
        <div class="layui-input-block ver-center">
        	{{responsId}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">参与人:</label>
        <div class="layui-input-block ver-center">
        	{{partId}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">关注人:</label>
        <div class="layui-input-block ver-center">
        	{{followId}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
	    <div class="layui-tab layui-tab-brief">
			<ul class="layui-tab-title">
				<li class="layui-this">状态变更历史</li>
			</ul>
			<div class="layui-tab-content" style="min-height: 500px;">
				<div class="layui-tab-item layui-show">
					
				    <div style="margin:0 auto;">
				        <table class="layui-table">
			                <thead>
			                    <tr>
			                        <th style="width: 80px;">变更人</th>
			                        <th style="width: 100px;">变更前状态</th>
			                        <th style="width: 100px;">变更后状态</th>
			                        <th style="width: 120px;">变更时间</th>
			                    </tr>
			                </thead>
			                <tbody id="useTable" class="insurance-table">
			                	{{#each changeHistory}}
			                		<tr>
				                		<td>{{createName}}</td>
				                		<td><del>{{originalStateName}}</del></td>
				                		<td><span style="color: #5FB878">{{nowStateName}}</span></td>
				                		<td>{{createTime}}</td>
			                		</tr>
			                	{{/each}}
			                </tbody>
						</table>
					</div>
					
				</div>
			</div>
		</div>
	</div>
{{/bean}}