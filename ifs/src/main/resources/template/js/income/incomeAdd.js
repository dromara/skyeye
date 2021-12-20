
var userReturnList = new Array();//选择用户返回的集合或者进行回显的集合
var chooseOrNotMy = "1";//人员列表中是否包含自己--1.包含；其他参数不包含
var chooseOrNotEmail = "2";//人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
var checkType = "2";//人员选择类型，1.多选；其他。单选

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate'], function(exports) {
    winui.renderColor();
    layui.use(['form', 'tagEditor'], function(form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$,
            laydate = layui.laydate;
        var rowNum = 1; //表格的序号
        var initemHtml = "";//收支项目

        var usetableTemplate = $("#usetableTemplate").html();
        var selOption = getFileContent('tpl/template/select-option.tpl');
        var handsPersonList = new Array();//经手人员

        //单据时间
        laydate.render({
            elem: '#operTime',
            type: 'datetime',
            value: getFormatDate(),
            trigger: 'click'
        });

        // 初始化账户
        systemCommonUtil.getSysAccountListByType(function(json){
            // 加载账户数据
            $("#accountId").html(getDataUseHandlebars(selOption, json));
        });

        // 初始化收入项目
        systemCommonUtil.getSysInoutitemListByType(1, function(json){
            // 加载收入项目数据
            initemHtml = getDataUseHandlebars(selOption, json);
            matchingLanguage();
            form.render();
            // 初始化一行数据
            addRow();
        });

        // 数量变化
        $("body").on("input", ".rkMoney", function() {
            calculatedTotalPrice();
        });
        $("body").on("change", ".rkMoney", function() {
            calculatedTotalPrice();
        });

        // 计算总价
        function calculatedTotalPrice(){
            var rowTr = $("#useTable tr");
            var allPrice = 0;
            $.each(rowTr, function(i, item) {
                // 获取行坐标
                var rowNum = $(item).attr("trcusid").replace("tr", "");
                // 获取金额
                var initemMoney = parseFloat(isNull($("#initemMoney" + rowNum).val()) ? "0" : $("#initemMoney" + rowNum).val());
                // 输出金额
                $("#initemMoney" + rowNum).html((initemMoney).toFixed(2));
                allPrice += initemMoney;
            });
            $("#allPrice").html(allPrice.toFixed(2));
        }

        form.on('submit(formAddBean)', function(data) {
            if(winui.verifyForm(data.elem)) {
                var rowTr = $("#useTable tr");
                if(rowTr.length == 0) {
                    winui.window.msg('请选择收入项目.', {icon: 2, time: 2000});
                    return false;
                }
                var tableData = new Array();
                var noError = false; //循环遍历表格数据时，是否有其他错误信息
                $.each(rowTr, function(i, item) {
                    //获取行编号
                    var rowNum = $(item).attr("trcusid").replace("tr", "");
                    if(judgeInPoingArr(tableData, "initemId", $("#initemId" + rowNum).val())){
                        $("#initemId" + rowNum).addClass("layui-form-danger");
                        $("#initemId" + rowNum).focus();
                        winui.window.msg('一张单中不允许出现相同收支项目信息.', {icon: 2, time: 2000});
                        noError = true;
                        return false;
                    }
                    var row = {
                        initemId: $("#initemId" + rowNum).val(),
                        initemMoney: $("#initemMoney" +rowNum).val(),
                        remark: $("#remark" + rowNum).val()
                    };
                    tableData.push(row);
                });
                if(noError) {
                    return false;
                }

                var params = {
                    organId: sysCustomerUtil.customerMation.id,
                    handsPersonId: handsPersonList[0].id,
                    operTime: $("#operTime").val(),
                    accountId: $("#accountId").val(),
                    remark: $("#remark").val(),
                    changeAmount: $("#changeAmount").val(),
                    initemStr: JSON.stringify(tableData)
                };
                AjaxPostUtil.request({url: reqBasePath + "income002", params: params, type: 'json', method: "POST", callback: function(json) {
                    if(json.returnCode == 0) {
                        parent.layer.close(index);
                        parent.refreshCode = '0';
                    } else {
                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                    }
                }});
            }
            return false;
        });

        $('#handsPersonId').tagEditor({
	        initialTags: [],
	        placeholder: '请选择经手人员',
	        editorTag: false,
	        beforeTagDelete: function(field, editor, tags, val) {
	        	var inArray = -1;
		    	$.each(handsPersonList, function(i, item) {
		    		if(val === item.name) {
		    			inArray = i;
		    			return false;
		    		}
		    	});
		    	if(inArray != -1) { //如果该元素在集合中存在
		    		handsPersonList.splice(inArray, 1);
		    	}
	        }
	    });
	    
	    // 人员选择
		$("body").on("click", "#toHandsPersonSelPeople", function(e){
			userReturnList = [].concat(handsPersonList);
            systemCommonUtil.openSysUserStaffChoosePage(function (staffChooseList){
                // 移除所有tag
                var tags = $('#handsPersonId').tagEditor('getTags')[0].tags;
                for (i = 0; i < tags.length; i++) {
                    $('#handsPersonId').tagEditor('removeTag', tags[i]);
                }
                handsPersonList = [].concat(staffChooseList);
                // 添加新的tag
                $.each(handsPersonList, function(i, item){
                    $('#handsPersonId').tagEditor('addTag', item.name);
                });
            });
		});

        // 新增行
        $("body").on("click", "#addRow", function() {
            addRow();
        });

        // 删除行
        $("body").on("click", "#deleteRow", function() {
            deleteRow();
        });

        // 新增行
        function addRow() {
            var par = {
                id: "row" + rowNum.toString(), //checkbox的id
                trId: "tr" + rowNum.toString(), //行的id
                initemId: "initemId" + rowNum.toString(), //收入项目id
                initemMoney: "initemMoney"  + rowNum.toString(), //金额id
                voucherId: "voucherId"  + rowNum.toString(), //凭证id
                accountSubjectId: "accountSubjectId"  + rowNum.toString(), //会计科目id
                remark: "remark" + rowNum.toString() //备注id
            };
            $("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
            //赋值给收支项目
            $("#" + "initemId" + rowNum.toString()).html(initemHtml);
            form.render('select');
            form.render('checkbox');
            rowNum++;
        }

        // 删除行
        function deleteRow() {
            var checkRow = $("#useTable input[type='checkbox'][name='tableCheckRow']:checked");
            if(checkRow.length > 0) {
                $.each(checkRow, function(i, item) {
                    $(item).parent().parent().remove();
                });
            } else {
                winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
            }
        }
        
        // 客户选择
 	    $("body").on("click", "#customMationSel", function(e){
            sysCustomerUtil.openSysCustomerChoosePage(function (customerMation){
                $("#customName").val(customerMation.customName);
            });
 	    });

        // 选择会计科目
        $("body").on("click", ".chooseIfsAccountSubjectBtn", function(e){
            var _this = $(this);
            sysIfsUtil.openSysAccountSubjectChoosePage(function (chooseAccountSubjectMation){
                _this.parent().find("input").attr("accountSubject", JSON.stringify(chooseAccountSubjectMation));
                _this.parent().find("input").val(chooseAccountSubjectMation.num + "_" + chooseAccountSubjectMation.name);
            });
        });

        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});