

// 表格的序号
var rowNum = 1;

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'textool'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$,
            textool = layui.textool;
        var selOption = getFileContent('tpl/template/select-option.tpl');

        var usetableTemplate = $("#usetableTemplate").html();

        var storeList = [];
        // 加载我所在的门店
        shopUtil.queryStaffBelongStoreList(function (json){
            storeList = [].concat(json.rows);
            $("#storeId").html(getDataUseHandlebars($("#selectTemplate").html(), json));
        });
        form.on('select(storeId)', function(data) {
            calcMealChoose();
        });

        // 用户类型变化
        form.on('radio(userType)', function(data) {
            var val = data.value;
            if(val == 1){
                $(".memberMation").hide();
                $(".anonymousUserMation").show();
            } else {
                $(".memberMation").show();
                $(".anonymousUserMation").hide();
            }
        });

        var carHasMealList = [];
        // 车辆信息变化
        form.on('select(memberCar)', function(data) {
            var val = data.value;
            if(isNull(val)){
                $("#mealId").html("");
            } else {
                // 获取车辆用于的套餐信息
                AjaxPostUtil.request({url: shopBasePath + "queryMealMationByCarId", params: {carId: val}, type: 'json', method: "GET", callback: function(json){
                    if (json.returnCode == 0) {
                        carHasMealList = [].concat(json.rows);
                        calcMealChoose();
                    } else {
                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                    }
                }, async: false});
            }
        });

        // 套餐变化
        form.on('select(mealId)', function(data) {
            var val = data.value;
            if(isNull(val)){
                $("#mealExplain").html("");
                $("#mealConsume").html("");
            } else {
                var item = getInPoingArr(carHasMealList, "id", val, null);
                $("#mealExplain").html(item.mealExplain);
                $("#mealConsume").html(item.mealConsume);
            }
        });

        function calcMealChoose(){
            // 获取选中门店所属的区域id
            var storeId = $("#storeId").val();
            var chooseStoreAreaId = getInPoingArr(storeList, "id", storeId, "areaId");

            var tempList = [].concat(carHasMealList);
            $.each(tempList, function (i, item){
                if(item.areaId != chooseStoreAreaId){
                    item.choose = "disabled";
                } else {
                    item.choose = "";
                }
            });
            $("#mealId").html(getDataUseHandlebars($("#mealTemplate").html(), {rows: tempList}));
            form.render('select');
        }

        textool.init({
            eleId: 'remark',
            maxlength: 400,
            tools: ['count', 'copy', 'reset']
        });

        matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var tableData = new Array();
                // $.each($("#useTable tr"), function(i, item) {
                //     // 获取行编号
                //     var rowNum = $(item).attr("trcusid").replace("tr", "");
                //     var row = {
                //         consumeExplain: $("#consumeExplain" + rowNum.toString()).val(),
                //         price: $("#price" + rowNum.toString()).val()
                //     };
                //     tableData.push(row);
                // });

                var userType = $("input[name='userType']:checked").val();

                var params = {
                    storeId: $("#storeId").val(),
                    userType: userType,
                    memberId: userType == 1 ? "" : sysMemberUtil.memberMation.id,
                    memberCarId: userType == 1 ? "" : $("#memberCar").val(),
                    memberCarPlate: userType == 1 ? $("#memberCarPlate").val() : $("#memberCar").find("option:selected").text(),
                    mealOrderChildId: userType == 1 ? "" : $("#mealId").val(),
                    // servicePrice: $("#servicePrice").val(),
                    servicePrice: 0,
                    remark: $("#remark").val(),
                    type: 2,
                    consumeMationList: JSON.stringify(tableData)
                };

                if(userType == 1){
                    // 匿名客户
                    if(isNull(params.memberCarPlate)){
                        winui.window.msg('请输入车牌号.', {icon: 2, time: 2000});
                        return false;
                    }
                } else {
                    // 会员
                    if(isNull(params.memberId)){
                        winui.window.msg('请选择会员.', {icon: 2, time: 2000});
                        return false;
                    }
                    if(isNull(params.memberCarId)){
                        winui.window.msg('请选择车辆信息.', {icon: 2, time: 2000});
                        return false;
                    }
                }

                AjaxPostUtil.request({url: shopBasePath + "insertKeepFitOrder", params: params, type: 'json', method: "POST", callback: function(json){
                    if (json.returnCode == 0) {
                        winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000}, function(){
                            location.reload();
                        });
                    } else {
                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                    }
                }, async: true});
            }
            return false;
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
                consumeExplain: "consumeExplain" + rowNum.toString(),
                price: "price" + rowNum.toString()
            };
            $("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
            form.render();
            rowNum++;
        }

        // 删除行
        function deleteRow() {
            var checkRow = $("#useTable input[type='checkbox'][name='tableCheckRow']:checked");
            if(checkRow.length > 0) {
                $.each(checkRow, function(i, item) {
                    // 移除界面上的信息
                    $(item).parent().parent().remove();
                });
                calcAllPrice();
            } else {
                winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
            }
        }

        // 会员选择
        $("body").on("click", ".chooseMemberBtn", function(e){
            sysMemberUtil.openSysMemberChoosePage(function (memberMation){
                $("#memberId").val(memberMation.contacts);
                var params = {
                    memberId: memberMation.id,
                    enabled: 1
                };
                // 获取会员拥有的车辆信息(已启用)
                AjaxPostUtil.request({url: shopBasePath + "memberCar001", params: params, type: 'json', method: "POST", callback: function(json){
                    if (json.returnCode == 0) {
                        $.each(json.rows, function (i, item){
                            item.name = item.modelType + "(" + item.plate + ")";
                        });
                        $("#memberCar").html(getDataUseHandlebars(selOption, json));
                        form.render('select');
                    } else {
                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                    }
                }, async: false});
            });
        });

        $("body").on("input", ".calcPrice", function() {
            calcAllPrice();
        });
        $("body").on("change", ".calcPrice", function() {
            calcAllPrice();
        });

        function calcAllPrice(){
            var allPrice = "0.00";
            $.each($("#useTable tr"), function(i, item) {
                // 获取行编号
                var rowNum = $(item).attr("trcusid").replace("tr", "");
                allPrice = sum(allPrice, $("#price" + rowNum.toString()).val());
            });
            allPrice = sum(allPrice, $("#servicePrice").val());
            $("#allPrice").html(allPrice + "元");
        }

        $("body").on("click", "#cancle", function(){
            parent.layer.close(index);
        });
    });
});