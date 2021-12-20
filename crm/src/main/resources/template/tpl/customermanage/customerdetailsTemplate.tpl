{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">基本信息</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">客户名称：</label>
        <div class="layui-input-block ver-center">
            {{name}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">拼音：</label>
        <div class="layui-input-block ver-center">
            {{combine}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">分类：</label>
        <div class="layui-input-block ver-center">
            {{typeId}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">客户来源：</label>
        <div class="layui-input-block ver-center">
            {{fromId}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">客户分组：</label>
        <div class="layui-input-block ver-center">
            {{groupName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">所属行业：</label>
        <div class="layui-input-block ver-center">
            {{industryId}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">客户网址：</label>
        <div class="layui-input-block ver-center">
            {{cusUrl}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">国家/地区：</label>
        <div class="layui-input-block ver-center">
            {{country}}
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
        <label class="layui-form-label">邮政编码：</label>
        <div class="layui-input-block ver-center">
            {{postalCode}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">传真：</label>
        <div class="layui-input-block ver-center">
            {{fax}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">客户负责人：</label>
        <div class="layui-input-block ver-center">
            {{chargeUserName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">操机工：</label>
        <div class="layui-input-block ver-center">
            {{mechanicName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">操机工电话：</label>
        <div class="layui-input-block ver-center">
            {{mechanicPhone}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">法人代表：</label>
        <div class="layui-input-block ver-center">
            {{corRepresentative}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">注册资本：</label>
        <div class="layui-input-block ver-center">
            {{regCapital}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">附加说明：</label>
        <div class="layui-input-block ver-center">
        	{{addDesc}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">附件资料：</label>
        <div class="layui-input-block ver-center" id="enclosureUpload">

        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">财务信息</span><hr>
	</div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">银行账户：</label>
        <div class="layui-input-block ver-center">
            {{bankAccount}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">开户名称：</label>
        <div class="layui-input-block ver-center">
            {{accountName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">开户银行：</label>
        <div class="layui-input-block ver-center">
            {{bankName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">银行地址：</label>
        <div class="layui-input-block ver-center">
            {{bankAddress}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">税号：</label>
        <div class="layui-input-block ver-center">
            {{dutyParagraph}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">电话：</label>
        <div class="layui-input-block ver-center">
            {{financePhone}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
	    <div class="layui-tab layui-tab-brief" lay-filter="customerOtherDetail">
			<ul class="layui-tab-title">
				<li class="layui-this">商机</li>
				<li class="">合同</li>
				<li class="">售后服务</li>
				<li class="">跟单记录</li>
				<li class="">联系人</li>
				<li class="">讨论板</li>
			</ul>
			<div class="layui-tab-content" style="min-height: 500px;">
				<div class="layui-tab-item layui-show">
					
			        <div class="layui-form-item">
			            <div class="layui-inline">
			                <label class="layui-form-label">商机名称</label>
			                <div class="layui-input-inline">
			                    <input type="text" id="opportunityTitle" name="opportunityTitle" placeholder="请输入商机名称" class="layui-input" />
			                </div>
			                <label class="layui-form-label">状态</label>
			                <div class="layui-input-inline">
			                    <select id="opportunityState">
			                    	<option value="">全部</option>
			                    	<option value="0">草稿</option>
			                    	<option value="1">审核中</option>
			                    	<option value="2">初期沟通</option>
			                    	<option value="3">方案与报价</option>
			                    	<option value="4">竞争与投标</option>
			                    	<option value="5">商务谈判</option>
			                    	<option value="6">成交</option>
			                    	<option value="7">丢单</option>
			                    	<option value="8">搁置</option>
			                    	<option value="11">审核通过</option>
			                    	<option value="12">审核不通过</option>
			                    </select>
			                </div>
			                <label class="layui-form-label">新增时间</label>
			                <div class="layui-input-inline">
			                    <select id="opportunityTime">
			                    	<option value="">全部</option>
			                    	<option value="0">本周新增</option>
			                    	<option value="1">上周新增</option>
			                    	<option value="2">本月新增</option>
			                    	<option value="3">上月新增</option>
			                    	<option value="4">本季度新增</option>
			                    	<option value="5">上季度新增</option>
			                    </select>
			                </div>
			                <button type="reset" class="layui-btn layui-btn-primary list-form-search"><language showName="com.skyeye.reset"></language></button>
			                <button class="layui-btn list-form-search" type="button" id="opportunitySearch"><language showName="com.skyeye.search2"></language></button>
			            </div>
			        </div>
				    <div style="margin:0 auto;">
				        <table id="opportunityTable" lay-filter="opportunityTable"></table>
					</div>
					
				</div>
				<div class="layui-tab-item">
					
			        <div class="layui-form-item">
			            <div class="layui-inline">
			                <label class="layui-form-label">合同名称</label>
			                <div class="layui-input-inline">
			                    <input type="text" id="contractTitle" name="contractTitle" placeholder="请输入合同名称" class="layui-input" />
			                </div>
			                <label class="layui-form-label">状态</label>
			                <div class="layui-input-inline">
			                    <select id="contractState">
			                    	<option value="">全部</option>
			                    	<option value="0">草稿</option>
			                    	<option value="1">审核中</option>
			                    	<option value="2">执行中</option>
			                    	<option value="3">已关闭</option>
			                    	<option value="4">已撤销</option>
			                    	<option value="5">已搁置</option>
			                    	<option value="11">审核通过</option>
			                    	<option value="12">审核不通过</option>
			                    </select>
			                </div>
			                <label class="layui-form-label">新增时间</label>
			                <div class="layui-input-inline">
			                    <select id="contractTime">
			                    	<option value="">全部</option>
			                    	<option value="0">本周新增</option>
			                    	<option value="1">上周新增</option>
			                    	<option value="2">本月新增</option>
			                    	<option value="3">上月新增</option>
			                    	<option value="4">本季度新增</option>
			                    	<option value="5">上季度新增</option>
			                    </select>
			                </div>
			                <button type="reset" class="layui-btn layui-btn-primary list-form-search"><language showName="com.skyeye.reset"></language></button>
			                <button class="layui-btn list-form-search" type="button" id="contractSearch"><language showName="com.skyeye.search2"></language></button>
			            </div>
			        </div>
				    <div style="margin:0 auto;">
				        <table id="contractTable" lay-filter="contractTable"></table>
					</div>
					
				</div>
				<div class="layui-tab-item">
				
					<div class="layui-form-item">
						<div class="layui-inline">
			                <label class="layui-form-label">单号</label>
			                <div class="layui-input-inline">
			                    <input type="text" id="serviceTitle" name="serviceTitle" placeholder="请输入单号" class="layui-input" />
			                </div>
			                <label class="layui-form-label">状态</label>
			                <div class="layui-input-inline">
			                    <select id="serviceState">
			                    	<option value="">全部</option>
			                    	<option value="1">待派工</option>
			                    	<option value="2">待接单</option>
			                    	<option value="3">待签到</option>
			                    	<option value="4">待完工</option>
			                    	<option value="5">待评价</option>
			                    	<option value="6">待审核</option>
			                    	<option value="7">已完工</option>
			                    </select>
			                </div>
			                <label class="layui-form-label">新增时间</label>
			                <div class="layui-input-inline">
			                    <select id="serviceTime">
			                    	<option value="">全部</option>
			                    	<option value="0">本周新增</option>
			                    	<option value="1">上周新增</option>
			                    	<option value="2">本月新增</option>
			                    	<option value="3">上月新增</option>
			                    	<option value="4">本季度新增</option>
			                    	<option value="5">上季度新增</option>
			                    </select>
			                </div>
			                <button type="reset" class="layui-btn layui-btn-primary list-form-search"><language showName="com.skyeye.reset"></language></button>
			                <button class="layui-btn list-form-search" type="button" id="serviceSearch"><language showName="com.skyeye.search2"></language></button>
			            </div>
			        </div>
			        <div style="margin:0 auto;">
				        <table id="serviceTable" lay-filter="serviceTable"></table>
					</div>
			    
				</div>
				<div class="layui-tab-item">
					
					<div style="margin:0 auto;">
				        <table id="documentaryTable" lay-filter="documentaryTable"></table>
					</div>
					
				</div>
				<div class="layui-tab-item">
					
					<div class="layui-form-item">
						<div class="layui-inline">
			                <label class="layui-form-label">联系人</label>
			                <div class="layui-input-inline">
			                    <input type="text" id="contactsTitle" name="contactsTitle" placeholder="请输入联系人" class="layui-input" />
			                </div>
			                <button type="reset" class="layui-btn layui-btn-primary list-form-search"><language showName="com.skyeye.reset"></language></button>
			                <button class="layui-btn list-form-search" type="button" id="contactsSearch"><language showName="com.skyeye.search2"></language></button>
			            </div>
			        </div>
			        <div style="margin:0 auto;">
				        <table id="contactsTable" lay-filter="contactsTable"></table>
					</div>
					
				</div>
				<div class="layui-tab-item">
					
					<div class="layui-form-item">
						<div class="layui-inline">
			                <label class="layui-form-label">标题</label>
			                <div class="layui-input-inline">
			                    <input type="text" id="contactsTitle" name="contactsTitle" placeholder="请输入标题" class="layui-input" />
			                </div>
			                <button type="reset" class="layui-btn layui-btn-primary list-form-search"><language showName="com.skyeye.reset"></language></button>
			                <button class="layui-btn list-form-search" type="button" id="discussSearch"><language showName="com.skyeye.search2"></language></button>
			            </div>
			        </div>
			        <div style="margin:0 auto;">
				        <table id="discussTable" lay-filter="discussTable"></table>
					</div>
					
				</div>
			</div>
		</div>
	</div>
{{/bean}}