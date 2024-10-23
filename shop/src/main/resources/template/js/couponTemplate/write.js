layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form,
        laydate = layui.laydate;
    let id = GetUrlParam("id");

    if (isNull(id)) {
        skyeyeClassEnumUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", '', form);
        skyeyeClassEnumUtil.showEnumDataListByClassName("couponTakeType", 'radio', "takeType", '', form);
        skyeyeClassEnumUtil.showEnumDataListByClassName("promotionMaterialScope", 'radio', "productScope", '', form);
        skyeyeClassEnumUtil.showEnumDataListByClassName("couponValidityType", 'radio', "validityType", '', form);
        skyeyeClassEnumUtil.showEnumDataListByClassName("promotionDiscountType", 'radio', "discountType", '', form);
        AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryAllShopMaterialListForChoose", params: {}, type: 'json', method: 'GET', callback: function (json) {
            dataShowType.showData(json, 'verificationSelect', 'couponMaterialList', '', form);
        }});
    } else {
        AjaxPostUtil.request({url: sysMainMation.shopBasePath + "queryCouponById", params: {id: id}, type: 'json', method: 'POST', callback: function (json) {
            let data = json.bean;
            skyeyeClassEnumUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", data.enabled, form);
            skyeyeClassEnumUtil.showEnumDataListByClassName("couponTakeType", 'radio', "takeType", data.takeType, form);
            skyeyeClassEnumUtil.showEnumDataListByClassName("promotionMaterialScope", 'radio', "productScope", data.productScope, form);
            skyeyeClassEnumUtil.showEnumDataListByClassName("couponValidityType", 'radio', "validityType", data.validityType, form);
            skyeyeClassEnumUtil.showEnumDataListByClassName("promotionDiscountType", 'radio', "discountType", data.discountType, form);
            $("#name").val(data.name);
            $("#totalCount").val(data.totalCount);
            $("#takeLimitCount").val(data.takeLimitCount);
            $("#useCount").val(data.useCount);

            if (data.validityType == 1) {
                $("#fixedTime").show();
                $("#receiveTime").hide();
            } else if (data.validityType == 2) {
                $("#fixedTime").hide();
                $("#receiveTime").show();
            }
            $("#validStartTime").val(data.validStartTime);
            $("#validEndTime").val(data.validEndTime);
            $("#fixedStartTime").val(data.fixedStartTime);
            $("#fixedEndTime").val(data.fixedEndTime);

            if (data.discountType == 1) {
                $("#percentBox").hide();
                $("#priceBox").show();
            } else if (data.discountType == 2) {
                $("#percentBox").show();
                $("#priceBox").hide();
            }
            $("#discountPercent").val(data.discountPercent);
            $("#usePrice").val(data.usePrice);
            $("#discountPrice").val(data.discountPrice);
            $("#discountLimitPrice").val(data.discountLimitPrice);
            $("#remark").val(data.remark);

            if (data.productScope == 1) {
                $("#couponMaterialListBox").hide();
            } else if (data.productScope == 2) {
                $("#couponMaterialListBox").show();
            }
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryAllShopMaterialListForChoose", params: {}, type: 'json', method: 'GET', callback: function (json) {
                let materialIdList = [];
                for (let i = 0; i < data.couponMaterialList.length; i++) {
                    materialIdList.push(data.couponMaterialList[i].materialId);
                }
                dataShowType.showData(json, 'verificationSelect', 'couponMaterialList', materialIdList.toString(), form);
            }});
        }});
    }

    var startTime = laydate.render({
        elem: '#validStartTime', //指定元素
        type: 'datetime',
        min: minDate(),
        theme: 'grid',
        done:function(value, date){
            endTime.config.min = {
                year: date.year,
                month: date.month - 1,//关键
                date: date.date,
                hours: date.hours,
                minutes: date.minutes,
                seconds: date.seconds
            };
        }
    });

    var endTime = laydate.render({
        elem: '#validEndTime', //指定元素
        type: 'datetime',
        min: minDate(),
        theme: 'grid',
        done:function(value, date){
            startTime.config.max = {
                year: date.year,
                month: date.month - 1,//关键
                date: date.date,
                hours: date.hours,
                minutes: date.minutes,
                seconds: date.seconds
            }
        }
    });

    // 有效期类型
    form.on('radio(validityTypeFilter)', function (data) {
        let val = data.value;
        if (val == 1) {
            $("#fixedTime").show();
            $("#receiveTime").hide();
        } else if (val == 2) {
            $("#fixedTime").hide();
            $("#receiveTime").show();
        }
    });

    // 适用产品范围
    form.on('radio(productScopeFilter)', function (data) {
        let val = data.value;
        if (val == 1) {
            $("#couponMaterialListBox").hide();
        } else if (val == 2) {
            $("#couponMaterialListBox").show();
        }
    });

    // 折扣类型
    form.on('radio(discountTypeFilter)', function (data) {
        let val = data.value;
        if (val == 1) {
            $("#percentBox").hide();
            $("#priceBox").show();
        } else if (val == 2) {
            $("#percentBox").show();
            $("#priceBox").hide();
        }
    });

    matchingLanguage();
    form.render();
    form.on('submit(formWriteBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            let couponMaterialIdList = isNull($('#couponMaterialList').attr('value')) ? [] : JSON.parse($('#couponMaterialList').attr('value'));
            let couponMaterialList = [];
            for (let i = 0; i < couponMaterialIdList.length; i++) {
                couponMaterialList.push({
                    materialId: couponMaterialIdList[i],
                });
            }
            var params = {
                id: isNull(id)? '' : id,
                name: $("#name").val(),
                enabled: dataShowType.getData('enabled'),
                totalCount: $("#totalCount").val(),
                takeLimitCount: $("#takeLimitCount").val(),
                takeType: dataShowType.getData('takeType'),
                usePrice: $("#usePrice").val(),
                productScope: dataShowType.getData('productScope'),
                validityType: dataShowType.getData('validityType'),
                validStartTime: $("#validStartTime").val(),
                validEndTime: $("#validEndTime").val(),
                fixedStartTime: $("#fixedStartTime").val(),
                fixedEndTime: $("#fixedEndTime").val(),
                discountType: dataShowType.getData('discountType'),
                discountPercent: $("#discountPercent").val(),
                discountPrice: $("#discountPrice").val(),
                discountLimitPrice: $("#discountLimitPrice").val(),
                useCount: $("#useCount").val(),
                couponMaterialList: JSON.stringify(couponMaterialList),
                remark: $("#remark").val(),
            };
            AjaxPostUtil.request({url: sysMainMation.shopBasePath + "writeCoupon", params: params, type: 'json', method: 'POST', callback: function (json) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            }});
        }
        return false;
    });

    // 取消
    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });

});