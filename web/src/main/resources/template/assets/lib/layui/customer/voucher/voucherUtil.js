
// 明细账选择凭证的工具函数
var voucherUtil = {

    tableHtmlDom: '<table class="voucher" cellpadding="0" cellspacing="0" id="voucher">' +
        '<thead>' +
        '        <tr>' +
        '            <th class="col_operate"></th>' +
        '            <th class="col_summary">摘要</th>' +
        '            <th class="col_voucher">凭证</th>' +
        '            <th class="col_subject">会计科目</th>' +
        '            <th class="col_money"> <strong class="tit">借方金额</strong>' +
        '                <div class="money_unit"> <span>亿</span> <span>千</span> <span>百</span> <span>十</span> <span>万</span> <span>千</span> <span>百</span> <span>十</span> <span>元</span> <span>角</span> <span class="last">分</span> </div>' +
        '              </th>' +
        '             <th class="col_money"> <strong class="tit">贷方金额</strong>' +
        '                <div class="money_unit"> <span>亿</span> <span>千</span> <span>百</span> <span>十</span> <span>万</span> <span>千</span> <span>百</span> <span>十</span> <span>元</span> <span>角</span> <span class="last">分</span> </div>' +
        '              </th>' +
        '        </tr>' +
        '    </thead>' +
        '    <tbody id="useTable">' +
        '    </tbody>' +
        '    <tfoot>' +
        '    <tr>' +
        '        <td colspan="4" class="col_total">合计：<span id="capAmount"></span></td>' +
        '        <td class="col_debite"><div class="cell_val debit_total" id="debit_total"></div></td>' +
        '        <td class="col_credit"><div class="cell_val credit_total" id="credit_total"></div></td>' +
        '      </tr></tfoot>' +
        '</table>',

    tBodyItemHtmlDom: '<tr class="entry_item">' +
        '            <td class="col_operate"><div class="operate"><i class="fa fa-trash-o del" aria-hidden="true" title="删除"></i></div></td>' +
        '            <td class="col_summary" data-edit="summary">' +
        '               <textarea class="layui-textarea" maxlength="200"></textarea>' +
        '           </td>' +
        '            <td class="col_voucher" data-edit="voucher">' +
        '               <div class="cell_val voucher_val"></div>' +
        '           </td>' +
        '            <td class="col_subject" data-edit="subject">' +
        '               <div class="cell_val subject_val"></div>' +
        '           </td>' +
        '            <td class="col_debite" data-edit="money"><div class="cell_val debit_val"></div></td>' +
        '            <td class="col_credit" data-edit="money"><div class="cell_val credit_val"></div></td>' +
        '        </tr>',

    /**
     * 初始化
     *
     * @param boxId dom的id
     */
    init: function (boxId){
        $("#" + boxId).html(voucherUtil.tableHtmlDom);
        for(var i = 0; i < 2; i++){
            voucherUtil.addItem();
        }

        voucherUtil.initClickEvent();
    },

    initData: function (boxId, data){
        $("#" + boxId).html(voucherUtil.tableHtmlDom);
        for(var i = 0; i < data.length; i++){
            voucherUtil.addItem();
            var _this = $("#useTable").find("tr")[i];
            _this.find(".col_summary").find("textarea").val(); // 摘要

            _this.find(".col_voucher").find(".cell_val").attr("dataId"); // 凭证
            _this.find(".col_voucher").find(".cell_val").html();

            _this.find(".col_subject").find(".cell_val").attr("dataId", ""); // 会计科目
            _this.find(".col_subject").find(".cell_val").html();

            _this.find(".col_debite").find(".cell_val").html(); // 借方金额
            _this.find(".col_credit").find(".cell_val").html(); // 贷方金额
        }

        voucherUtil.initClickEvent();
    },

    initClickEvent: function (){
        $("body").on("click", "td[data-edit]", function(){
            var _this = $(this);
            _this.find(".cell_val").hide();
            switch ($(this).attr("data-edit")) {
                case 'summary':
                    break;
                case 'voucher': // 凭证
                    _this.find(".cell_val").show();
                    sysIfsUtil.openIfsVoucherChoosePage(function (chooseVoucherMation){
                        _this.find(".cell_val").attr("dataId", chooseVoucherMation.id);
                        _this.find(".cell_val").html(chooseVoucherMation.fileName);
                    });
                    break;
                case 'subject': // 会计科目
                    _this.find(".cell_val").show();
                    sysIfsUtil.openSysAccountSubjectChoosePage(function (chooseAccountSubjectMation){
                        _this.find(".cell_val").attr("dataId", chooseAccountSubjectMation.id);
                        _this.find(".cell_val").html(chooseAccountSubjectMation.num + "_" + chooseAccountSubjectMation.name);
                    });
                    break;
                case 'money':
                    if ($(this).find(".edit_money").length == 0) {
                        voucherUtil.creatinput($(this))
                    } else {
                        $(this).find(".edit_money").show().focus();
                        $(this).find(".cell_val").hide();
                    }
                    break;
            }
        });

        $("body").on("click", ".col_operate .del", function(){
            var tBody = $("#useTable").find("tr");
            if(tBody.length <= 2){
                winui.window.msg("最少填写两行数据.", {icon: 2, time: 2000});
                return false;
            }
            $(this).parents("tr").remove();
        });
    },

    creatinput: function (_this) {
        var oInput = $('<input type="text" class="edit_money" autocomplete="off" />').numberField({
            decimal: true,
            max: 999999999.99,
            keyEnable: false
        }).bind('keydown', function (e) {
            var keyValue = e.which, value = $(this).val(), parentTd = $(this).parent(),
                parentTr = $(this).parents('tr.entry_item'), nextTr = parentTr.next();
            if (parentTd.hasClass('col_debite')) {
                parentTr.find('.credit_val').text('');
            } else {
                parentTr.find('.debit_val').text('');
            }
        }).bind('blur', function () {
            var currAmount = $(this).val() === '' ? 0 : Number($(this).val()).toFixed(2).replace(".", "");
            if (currAmount != 0) {
                $(this).parent().find(".cell_val").html(currAmount).show();
            } else {
                $(this).parent().find(".cell_val").html("").show();
            }
            if (voucherUtil.adddebit_total($(".debit_val")) <= 999999999.99) {
                $("#debit_total").text(voucherUtil.adddebit_total($(".debit_val")))
            } else {
                alert("借方金额合计太大")
            }
            if (voucherUtil.adddebit_total($(".credit_val")) <= 999999999.99) {
                $("#credit_total").text(voucherUtil.adddebit_total($(".credit_val")))
            } else {
                $("#credit_total").text(voucherUtil.adddebit_total($(".credit_val")))
                alert("贷方金额合计太大")
            }
            $(this).hide();
        }).appendTo(_this).select();
    },

    adddebit_total: function (obj) {
        var number = 0;
        for (var i = 0; i < obj.length; i++) {
            if (obj.eq(i).text() != '') {
                number = number + parseFloat(obj.eq(i).text())
            }

        }
        return number;
    },

    /**
     * 新增一个新的item
     */
    addItem: function (){
        $("#useTable").append(voucherUtil.tBodyItemHtmlDom);
    },

    /**
     * 获取数据
     */
    getData: function (){
        var tBody = $("#useTable").find("tr");
        if(tBody.length < 2){
            winui.window.msg("请最少填写两行数据.", {icon: 2, time: 2000});
            return null;
        }
        var result = [];
        $.each(tBody, function (i, item){
            var _this = $(item);
            var subjectId = _this.find(".col_subject").find(".cell_val").attr("dataId");
            var debiteMoney = _this.find(".col_debite").find(".cell_val").html(); // 借方金额
            var creditMoney = _this.find(".col_credit").find(".cell_val").html(); // 贷方金额
            if(isNull(subjectId)){
                winui.window.msg("请选择会计科目.", {icon: 2, time: 2000});
                return false;
            }
            if(isNull(debiteMoney) && isNull(creditMoney)){
                winui.window.msg("借方金额和贷方金额最少填写一项.", {icon: 2, time: 2000});
                return false;
            }
            var eachAmount = isNull(debiteMoney) ? creditMoney : debiteMoney;
            var directionType = isNull(debiteMoney) ? 2 : 1;
            result.push({
                remark: _this.find(".col_summary").find("textarea").val(),
                voucherId: _this.find(".col_voucher").find(".cell_val").attr("dataId"),
                subjectId: subjectId,
                eachAmount: eachAmount,
                directionType: directionType
            });
        });
        return result;
    }

}