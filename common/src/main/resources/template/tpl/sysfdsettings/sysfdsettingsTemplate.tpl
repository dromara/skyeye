{{#bean}}
    <div class="layui-tab">
        <ul class="layui-tab-title">
            <li class="layui-this">邮箱服务器</li>
            <li>论坛数据</li>
            <li>CRM</li>
            <li>考勤制度</li>
            <li>订单审核制度</li>
        </ul>
        <div class="layui-tab-content">

            <div class="layui-tab-item layui-show">
                <div class="layui-form-item layui-col-xs12">
                    <label class="layui-form-label">邮箱类型<i class="red">*</i></label>
                    <div class="layui-input-block">
                        <input type="text" id="emailType" name="emailType" placeholder="请填写邮箱类型" class="layui-input" win-verify="required" value="{{emailType}}"/>
                    </div>
                </div>
                <div class="layui-form-item layui-col-xs6">
                    <label class="layui-form-label">收件服务器<i class="red">*</i></label>
                    <div class="layui-input-block">
                        <input type="text" id="emailReceiptServer" name="emailReceiptServer" placeholder="请填写收件服务器" class="layui-input" win-verify="required" value="{{emailReceiptServer}}"/>
                    </div>
                </div>
                <div class="layui-form-item layui-col-xs6">
                    <label class="layui-form-label">SSL 端口<i class="red">*</i></label>
                    <div class="layui-input-block">
                        <input type="text" id="emailReceiptServerPort" name="emailReceiptServerPort" placeholder="请填写收件SSL端口" class="layui-input" win-verify="required" value="{{emailReceiptServerPort}}"/>
                    </div>
                </div>
                <div class="layui-form-item layui-col-xs6">
                    <label class="layui-form-label">发件服务器<i class="red">*</i></label>
                    <div class="layui-input-block">
                        <input type="text" id="emailSendServer" name="emailSendServer" placeholder="请填写发件服务器" class="layui-input" win-verify="required" value="{{emailSendServer}}"/>
                    </div>
                </div>
                <div class="layui-form-item layui-col-xs6">
                    <label class="layui-form-label">SSL 端口<i class="red">*</i></label>
                    <div class="layui-input-block">
                        <input type="text" id="emailSendServerPort" name="emailSendServerPort" placeholder="请填写发件SSL端口" class="layui-input" win-verify="required" value="{{emailSendServerPort}}"/>
                    </div>
                </div>
            </div>

            <div class="layui-tab-item">
                <div class="layui-form-item layui-col-xs6">
                    <label class="layui-form-label">用法说明：</label>
                    <div class="layui-input-block">
                        上次同步时间：<span id="synchronousTime"></span><button class="layui-btn layui-btn-xs layui-btn-normal" type="button" id="formAddBean">一键同步</button><br>
                        <div class="layui-form-mid layui-word-aux">solr更换时，将数据库数据同步到solr中</div>
                    </div>
                </div>
            </div>

            <div class="layui-tab-item">
                <div class="layui-form-item layui-col-xs12">
                    <span class="hr-title">CRM公海限制条件</span><hr>
                </div>
                <div class="layui-form-item layui-col-xs6">
                    <label class="layui-form-label">未跟单天数：</label>
                    <div class="layui-input-block">
                        <input type="text" id="noDocumentaryDayNum" name="noDocumentaryDayNum" placeholder="请填写天数" class="layui-input" value="{{noDocumentaryDayNum}}" win-verify="number"/>
                        <div class="layui-form-mid layui-word-aux">N天未跟单自动进入公海</div>
                    </div>
                </div>
                <div class="layui-form-item layui-col-xs6">
                    <label class="layui-form-label">未指定负责人：</label>
                    <div class="layui-input-block winui-radio">
                        <input type="radio" name="noChargeId" value="1" title="是"/>
                        <input type="radio" name="noChargeId" value="2" title="否" />
                        <div class="layui-form-mid layui-word-aux" style="width: 100%;">未指定责任人自动进入公海</div>
                    </div>
                </div>
            </div>

            <div class="layui-tab-item">
                <div class="layui-form-item layui-col-xs12">
                    <span class="hr-title">请假扣薪制度</span><hr>
                </div>
                <div class="layui-form-item layui-col-xs12">
                    <label class="layui-form-label">列表项<i class="red">*</i></label>
                    <div class="layui-input-block">
                        <div class="winui-toolbar">
                            <div class="winui-tool" style="text-align: left;">
                                <button id="addHolidaysTypeJsonRow" class="winui-toolbtn" type="button"><i class="fa fa-plus" aria-hidden="true"></i>新增行</button>
                                <button id="deleteHolidaysTypeJsonRow" class="winui-toolbtn" type="button"><i class="fa fa-trash-o" aria-hidden="true"></i>删除行</button>
                            </div>
                        </div>
                        <table class="layui-table">
                            <thead>
                                <tr>
                                    <th style="width: 30px;"></th>
                                    <th style="width: 150px;">类型名称<i class="red">*</i></th>
                                    <th style="width: 120px;">标识符(唯一)<i class="red">*</i></th>
                                    <th style="width: 80px;">薪资扣除率(百分比)<i class="red">*</i></th>
                                    <th style="width: 150px;">是否使用年假(如果选择[是]，则在请假时会自动扣除年假信息)</th>
                                    <th style="width: 150px;">是否使用补休(如果选择[是]，则在请假时会自动扣除员工补休池剩余补休时间信息)</th>
                                    <th style="min-width: 180px;">备注(该信息会展示给员工查看)</th>
                                </tr>
                            </thead>
                            <tbody id="holidaysTypeJsonTable" class="insurance-table">
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="layui-form-item layui-col-xs12">
                    <span class="hr-title">年假制度</span><hr>
                </div>
                <div class="layui-form-item layui-col-xs12">
                    <label class="layui-form-label">列表项<i class="red">*</i></label>
                    <div class="layui-input-block">
                        <div class="winui-toolbar">
                            <div class="winui-tool" style="text-align: left;">
                                <button id="addYearHolidaysMationRow" class="winui-toolbtn" type="button"><i class="fa fa-plus" aria-hidden="true"></i>新增行</button>
                                <button id="deleteYearHolidaysMationRow" class="winui-toolbtn" type="button"><i class="fa fa-trash-o" aria-hidden="true"></i>删除行</button>
                            </div>
                        </div>
                        <table class="layui-table">
                            <thead>
                                <tr>
                                    <th style="width: 30px;"></th>
                                    <th style="width: 150px;">工作年限<i class="red">*</i></th>
                                    <th style="width: 120px;">年假(小时)<i class="red">*</i></th>
                                    <th style="min-width: 180px;">备注</th>
                                </tr>
                            </thead>
                            <tbody id="yearHolidaysMationTable" class="insurance-table">
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="layui-form-item layui-col-xs12">
                    <span class="hr-title">异常考勤制度</span><hr>
                </div>
                <div class="layui-form-item layui-col-xs12">
                    <div class="winui-tip alert-info">异常考勤扣薪比例设置。</div>
                </div>
                <div class="layui-form-item layui-col-xs12">
                    <label class="layui-form-label">列表项<i class="red">*</i></label>
                    <div class="layui-input-block">
                        <table class="layui-table">
                            <thead>
                                <tr>
                                    <th style="width: 150px;">异常类型<i class="red">*</i></th>
                                    <th style="width: 120px;">扣薪方式<i class="red">*</i></th>
                                    <th style="width: 120px;">扣除费用<i class="red">*</i></th>
                                    <th style="min-width: 180px;">备注</th>
                                </tr>
                            </thead>
                            <tbody id="abnormalAttendanceTable" class="insurance-table">
                                <tr trcusid="abnormalTr1">
                                    <td id="abnormalType1" typeName="leaveEarly">早退</td>
                                    <td>
                                        <select id="abnormal1" lay-search win-verify="required" lay-filter="abnormal">
                                            <option value="">请选择</option>
                                            <option value="1">按次扣费</option>
                                            <option value="2">按时间扣费</option>
                                        </select>
                                    </td>
                                    <td>
                                        <input type="text" class="layui-input" id="abnormalMoney1" win-verify="money" style="display: none"/>
                                        <label id="abnormalMoneyLabel1" style="display: none">由系统自动计算时间并按照薪资比例进行扣费</label>
                                    </td>
                                    <td><input type="text" class="layui-input" id="abnormalRemark1"/></td>
                                </tr>
                                <tr trcusid="abnormalTr2">
                                    <td id="abnormalType2" typeName="late">迟到</td>
                                    <td>
                                        <select id="abnormal2" lay-search win-verify="required" lay-filter="abnormal">
                                            <option value="">请选择</option>
                                            <option value="1">按次扣费</option>
                                            <option value="2">按时间扣费</option>
                                        </select>
                                    </td>
                                    <td>
                                        <input type="text" class="layui-input" id="abnormalMoney2" win-verify="money" style="display: none"/>
                                        <label id="abnormalMoneyLabel2" style="display: none">由系统自动计算时间并按照薪资比例进行扣费</label>
                                    </td>
                                    <td><input type="text" class="layui-input" id="abnormalRemark2"/></td>
                                </tr>
                                <tr trcusid="abnormalTr3">
                                    <td id="abnormalType3" typeName="miner">矿工</td>
                                    <td>
                                        <select id="abnormal3" lay-search win-verify="required" lay-filter="abnormal">
                                            <option value="">请选择</option>
                                            <option value="1">按次扣费</option>
                                            <option value="2">按时间扣费</option>
                                        </select>
                                    </td>
                                    <td>
                                        <input type="text" class="layui-input" id="abnormalMoney3" win-verify="money" style="display: none"/>
                                        <label id="abnormalMoneyLabel3" style="display: none">由系统自动计算时间并按照薪资比例进行扣费</label>
                                    </td>
                                    <td><input type="text" class="layui-input" id="abnormalRemark3"/></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="layui-tab-item" id="sysOrderBasicDesignMationBox">

            </div>

        </div>
    </div>

    <div class="layui-form-item layui-col-xs12">
	    <div class="layui-form-item layui-col-xs6 layui-col-xs-offset3">
        <div class="layui-input-block">
            <button class="winui-btn" lay-submit lay-filter="formEditBean" auth="1556942485344"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}