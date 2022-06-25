
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$;
        
        AjaxPostUtil.request({url: flowableBasePath + "erpbom003", params: {rowId: parent.rowId}, type: 'json', callback: function(json){
            if (json.returnCode == 0) {
                $("#showForm").html(getDataUseHandlebars($("#mainHtml").html(), json));
            	$.fn.zTree.init($("#treeDemo"), setting, json.bean.bomMaterialList);
				loadTr();
				$.each(json.bean.bomMaterialList, function(i, item){
					$("#allPrice" + item.productId).html(parseInt(item.needNum) * parseFloat(item.unitPrice));
				});
				matchingLanguage();
				form.render();
            }else{
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }});
        
        /********* tree 处理   start *************/
        var setting = {
        	id: "treeDemo",
			check : {
	            enable : false
			},
			view: {
				showLine: false,
				showIcon: false,
				addDiyDom: addDiyDom,
				fontCss: setFontCss,
				expandSpeed: 'speed'
			},
			async: {//异步加载
				enable: false
			},
			data: {
				key: {
					name: 'productName'
				},
				simpleData: {
					enable: true,
					idKey: 'productId',
					pIdKey: 'pId',
					rootPId: 0
				}
			}
        };
		
		//获取表格标题
		var li_head = $("#tableHeader").html();
		function loadTr(){
			var rows = $("#treeDemo").find('li');
			if(rows.length == 0) {
				$("#treeDemo").append(li_head);
				$("#treeDemo").append('<li><div style="text-align: center;line-height: 30px;" >无符合条件数据</div></li>')
			}else{
				rows.eq(0).before(li_head)
			}
		}
		
		/**
		 * 自定义DOM节点
		 */
		function addDiyDom(treeId, treeNode) {
			var spaceWidth = 15;
			var liObj = $("#" + treeNode.tId);
			var aObj = $("#" + treeNode.tId + "_a");
			var switchObj = $("#" + treeNode.tId + "_switch");
			var icoObj = $("#" + treeNode.tId + "_ico");
			var spanObj = $("#" + treeNode.tId + "_span");
			aObj.attr('title', '');
			aObj.append('<div class="diy swich"></div>');
			var div = $(liObj).find('div').eq(0);
			switchObj.remove();
			spanObj.remove();
			icoObj.remove();
			div.append(switchObj);
			div.append(spanObj);
			var spaceStr = "<span style='height:1px;display: inline-block;width:" + (spaceWidth * treeNode.level) + "px'></span>";
			switchObj.before(spaceStr);
			aObj.append(getDataUseHandlebars($("#tableBody").html(), treeNode));
			//设置商品来源选中
			$("#type" + treeNode.productId).val(treeNode.type);
			form.render("select");
		}
		
		function setFontCss(treeId, treeNode) {  
		    return (!!treeNode.highlight) ? {color:"#00ff66", "font-weight":"bold"} : {color:"#333", "font-weight":"normal"};  
		}
		/********* tree 处理   end *************/
        
    });
});