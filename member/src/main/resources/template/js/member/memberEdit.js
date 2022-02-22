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
        showGrid({
            id: "showForm",
            url: shopBasePath + "member003",
            params: {rowId: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#beanTemplate").html(),
            ajaxSendLoadBefore: function(hdb){},
            ajaxSendAfter:function(json){
            	
            	textool.init({
			    	eleId: 'description',
			    	maxlength: 200,
			    	tools: ['count', 'copy', 'reset']
			    });

                // 家庭地址
                initArea(json.bean);//加载省级行政区划

			    matchingLanguage();
                form.render();
                form.on('submit(formEditBean)', function (data) {
                    if (winui.verifyForm(data.elem)) {
                        var params = {
                            rowId: parent.rowId,
                            contacts: $("#contacts").val(),
                            phone: $("#phone").val(),
                            email: $("#email").val(),
                            provinceId: $("#provinceId").val(),
                            cityId: $("#cityId").val(),
                            addressAreaId: $("#addressAreaId").val(),
                            townshipId: $("#townshipId").val(),
                            address: $("#address").val(),
                            description: $("#description").val()
                        };
                        AjaxPostUtil.request({url: shopBasePath + "member005", params: params, type: 'json', method: "PUT", callback: function(json){
                            if(json.returnCode == 0){
                                parent.layer.close(index);
                                parent.refreshCode = '0';
                            }else{
                                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                            }
                        }});
                    }
                    return false;
                });
            }
        });

        //初始化行政区划-省
        function initArea(bean){
            AjaxPostUtil.request({url:reqBasePath + "commontarea001", params:{}, type:'json', method: "POST", callback:function(json){
                if(json.returnCode == 0){
                    var str = '<dd class="layui-col-xs3"><select id="provinceId" win-verify="required" lay-filter="areaProvince" lay-search=""><option value="">请选择</option>';
                    for(var i = 0; i < json.rows.length; i++){
                        str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
                    }
                    str += '</select></dd>';
                    $("#lockParentSel").append(str);
                    if(!isNull(bean.provinceId)){
                        $("#provinceId").val(bean.provinceId);
                        initAreaCity(bean);
                    }
                    form.render('select');
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
                }
            }});
        }

        //初始化行政区划-市
        function initAreaCity(bean){
            AjaxPostUtil.request({url:reqBasePath + "commontarea002", params:{rowId: $("#provinceId").val()}, type:'json', method: "POST", callback:function(json){
                if(json.returnCode == 0){
                    var str = '<dd class="layui-col-xs3"><select id="cityId" win-verify="required" lay-filter="areaCity" lay-search=""><option value="">请选择</option>';
                    for(var i = 0; i < json.rows.length; i++){
                        str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
                    }
                    str += '</select></dd>';
                    $("#lockParentSel").append(str);
                    if(!isNull(bean.cityId)){
                        $("#cityId").val(bean.cityId);
                        initAreaChildArea(bean);
                    }
                    form.render('select');
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
                }
            }});
        }

        //初始化行政区划-县
        function initAreaChildArea(bean){
            AjaxPostUtil.request({url:reqBasePath + "commontarea003", params:{rowId: $("#cityId").val()}, type:'json', method: "POST", callback:function(json){
                if(json.returnCode == 0){
                    var str = '<dd class="layui-col-xs3"><select id="addressAreaId" win-verify="required" lay-filter="area" lay-search=""><option value="">请选择</option>';
                    for(var i = 0; i < json.rows.length; i++){
                        str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
                    }
                    str += '</select></dd>';
                    $("#lockParentSel").append(str);
                    if(!isNull(bean.addressAreaId)){
                        $("#addressAreaId").val(bean.addressAreaId);
                        initAreaTownShip(bean);
                    }
                    form.render('select');
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
                }
            }});
        }

        //初始化行政区划-镇
        function initAreaTownShip(bean){
            AjaxPostUtil.request({url:reqBasePath + "commontarea004", params:{rowId: $("#addressAreaId").val()}, type:'json', method: "POST", callback:function(json){
                if(json.returnCode == 0){
                    var str = '<dd class="layui-col-xs3"><select id="townshipId" lay-filter="areaTownShip" lay-search=""><option value="">请选择</option>';
                    for(var i = 0; i < json.rows.length; i++){
                        str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
                    }
                    str += '</select></dd>';
                    $("#lockParentSel").append(str);
                    if(!isNull(bean.townshipId)){
                        $("#townshipId").val(bean.townshipId);
                    }
                    form.render('select');
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
                }
            }});
        }

        form.on('select(areaProvince)', function(data){
            layui.$(data.elem).parent('dd').nextAll().remove();
            if(isNull(data.value) || data.value == '请选择'){
            }else{
                loadChildCityArea();
            }
        });
        form.on('select(areaCity)', function(data){
            layui.$(data.elem).parent('dd').nextAll().remove();
            if(isNull(data.value) || data.value == '请选择'){
            }else{
                loadChildArea();
            }
        });
        form.on('select(area)', function(data){
            layui.$(data.elem).parent('dd').nextAll().remove();
            if(isNull(data.value) || data.value == '请选择'){
            }else{
                loadChildAreaTownShip();
            }
        });

        //市级行政区划
        function loadChildCityArea(){
            AjaxPostUtil.request({url:reqBasePath + "commontarea002", params:{rowId: $("#provinceId").val()}, type:'json', method: "POST", callback:function(json){
                if(json.returnCode == 0){
                    var str = '<dd class="layui-col-xs3"><select id="cityId" win-verify="required" lay-filter="areaCity" lay-search=""><option value="">请选择</option>';
                    for(var i = 0; i < json.rows.length; i++){
                        str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
                    }
                    str += '</select></dd>';
                    $("#lockParentSel").append(str);
                    form.render('select');
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
                }
            }});
        }

        //县级行政区划
        function loadChildArea(){
            AjaxPostUtil.request({url:reqBasePath + "commontarea003", params:{rowId: $("#cityId").val()}, type:'json', method: "POST", callback:function(json){
                if(json.returnCode == 0){
                    var str = '<dd class="layui-col-xs3"><select id="addressAreaId" win-verify="required" lay-filter="area" lay-search=""><option value="">请选择</option>';
                    for(var i = 0; i < json.rows.length; i++){
                        str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
                    }
                    str += '</select></dd>';
                    $("#lockParentSel").append(str);
                    form.render('select');
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
                }
            }});
        }

        //镇级行政区划
        function loadChildAreaTownShip(){
            AjaxPostUtil.request({url:reqBasePath + "commontarea004", params:{rowId: $("#addressAreaId").val()}, type:'json', method: "POST", callback:function(json){
                if(json.returnCode == 0){
                    var str = '<dd class="layui-col-xs3"><select id="townshipId" lay-filter="areaTownShip" lay-search=""><option value="">请选择</option>';
                    for(var i = 0; i < json.rows.length; i++){
                        str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
                    }
                    str += '</select></dd>';
                    $("#lockParentSel").append(str);
                    form.render('select');
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
                }
            }});
        }

        $("body").on("click", "#cancle", function(){
            parent.layer.close(index);
        });
    });
});