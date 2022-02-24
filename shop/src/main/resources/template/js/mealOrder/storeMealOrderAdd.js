

// 表格的序号
var rowNum = 1;

// 会员信息
var memberMation = {};

// 已选择的套餐信息
var mealMation = {};

// 当前选中的门店
var storeId = "";

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
        var memberCarHtml = "";

        shopUtil.queryShenkeMealList("123", function (data){

        });

        memberMation = parent.memberMation;
        $("#memberName").html(memberMation.contacts);

        // 加载我所在的门店
        shopUtil.queryStaffBelongStoreList(function (json){
            $("#storeId").html(getDataUseHandlebars($("#selectTemplate").html(), json));
        });
        form.on('select(storeId)', function(data) {
            // 门店变化需要重新选择套餐
            $.each($("#useTable tr"), function(i, item) {
                // 获取行编号
                var rowNum = $(item).attr("trcusid").replace("tr", "");
                $("#mealId" + rowNum.toString()).attr("mealMaion", "");
                $("#mealId" + rowNum.toString()).val("");
                $("#num" + rowNum.toString()).html("");
                $("#price" + rowNum.toString()).html("");
            });
            $("#allPrice").html("0 元");
        });

        textool.init({
            eleId: 'remark',
            maxlength: 400,
            tools: ['count', 'copy', 'reset']
        });

        // 获取会员拥有的车辆信息
        AjaxPostUtil.request({url: shopBasePath + "memberCar001", params: {memberId: memberMation.id}, type: 'json', method: "POST", callback: function(json){
            if(json.returnCode == 0){
                $.each(json.rows, function (i, item){
                    item.name = item.modelType + "(" + item.plate + ")";
                });
                memberCarHtml = getDataUseHandlebars(selOption, json);
            }else{
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});

        addRow();
        matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var tableData = new Array();
                $.each($("#useTable tr"), function(i, item) {
                    // 获取行编号
                    var rowNum = $(item).attr("trcusid").replace("tr", "");
                    var mealMation = JSON.parse($("#mealId" + rowNum.toString()).attr("mealMaion"));
                    var row = {
                        mealId: mealMation.id,
                        memberCarId: $("#carId" + rowNum.toString()).val()
                    };
                    tableData.push(row);
                });
                if(tableData.length == 0){
                    winui.window.msg('请填写套餐信息.', {icon: 2,time: 2000});
                    return false;
                }

                var params = {
                    storeId: $("#storeId").val(),
                    memberId: memberMation.id,
                    remark: $("#remark").val(),
                    type: 2,
                    source: 2,
                    mealList: JSON.stringify(tableData)
                };

                AjaxPostUtil.request({url: shopBasePath + "insertMealOrder", params: params, type: 'json', method: "POST", callback: function(json){
                    if(json.returnCode == 0){
                        parent.layer.close(index);
                        parent.refreshCode = '0';
                    }else{
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
                mealId: "mealId" + rowNum.toString(),
                num: "num" + rowNum.toString(),
                price: "price" + rowNum.toString(),
                carId: "carId" + rowNum.toString()
            };
            $("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
            $("#carId" + rowNum.toString()).html(memberCarHtml);
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

        // 套餐选择
        $("body").on("click", ".chooseMealBtn", function(e){
            var trId = $(this).parent().parent().attr("trcusid");
            storeId = $("#storeId").val();
            if(isNull(storeId)){
                winui.window.msg('请优先选择门店.', {icon: 2,time: 2000});
                return false;
            }
            _openNewWindows({
                url: "../../tpl/meal/mealChoose.html",
                title: "选择套餐",
                pageId: "mealChoose",
                area: ['90vw', '90vh'],
                callBack: function(refreshCode){
                    if (refreshCode == '0') {
                        // 获取表格行号
                        var thisRowNum = trId.replace("tr", "");

                        $("#mealId" + thisRowNum.toString()).val(mealMation.title);
                        $("#num" + thisRowNum.toString()).html(mealMation.mealNum);
                        $("#price" + thisRowNum.toString()).html(mealMation.price);
                        $("#mealId" + thisRowNum.toString()).attr("mealMaion", JSON.stringify(mealMation));
                        calcAllPrice();
                    } else if (refreshCode == '-9999') {
                        winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                    }
                }});
        });

        function calcAllPrice(){
            var allPrice = "0.00";
            $.each($("#useTable tr"), function(i, item) {
                // 获取行编号
                var rowNum = $(item).attr("trcusid").replace("tr", "");
                var mealMationJson = $("#mealId" + rowNum.toString()).attr("mealMaion");
                if(!isNull(mealMationJson)){
                    var mealMation = JSON.parse(mealMationJson);
                    allPrice = sum(allPrice, mealMation.unformatPrice);
                }
            });
            $("#allPrice").html(allPrice + "元");
        }

        $("body").on("click", "#cancle", function(){
            parent.layer.close(index);
        });
    });
});