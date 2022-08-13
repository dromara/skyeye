
//bom表商品选择必备参数
var productMationList = [];//选择的商品列表

//工序选择必备参数
var procedureCheckType = 2;//工序选择类型：1.单选procedureMation；2.多选procedureMationList
var procedureMationList = new Array();

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$,
        	textool = layui.textool;
        
        var ztreeNode = new Array();
        	
	    //下拉框模板
	    var selTemplate = getFileContent('tpl/template/select-option-must.tpl');
	    
	    AjaxPostUtil.request({url: flowableBasePath + "erpbom005", params: {rowId: parent.rowId}, type: 'json', method: "GET", callback: function (json) {
			$("#showForm").html(getDataUseHandlebars($("#mainHtml").html(), json));
			textool.init({eleId: 'remark', maxlength: 200});

			//初始化父件商品信息
			erpOrderUtil.chooseProductMation = {
				productId: json.bean.productId,
				productName: json.bean.productName,
				productModel: json.bean.productModel,
				unitList: json.bean.unitList
			};

			//初始化规格单位
			$("#unitList").val(json.bean.normsId);

			//加载树表格信息
			ztreeNode = json.bean.bomMaterialList;
			$.fn.zTree.init($("#treeDemo"), setting, ztreeNode);
			loadTr();

			matchingLanguage();
			form.render();
        }});
	    
        form.on('submit(formEditBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
            	if(ztreeNode.length == 0){
            		winui.window.msg('请选择子件清单。', {icon: 2, time: 2000});
            		return false;
            	}
            	var childObject = new Array();
            	var wheatherError = false;
            	$.each(ztreeNode, function(i, item){
            		item.needNum = $("#needNum" + item.productId).val();
            		item.type = $("#type" + item.productId).val();
            		item.wastagePrice = $("#wastage" + item.productId).val();
            		item.remark = $("#remark" + item.productId).val();
            		childObject.push(item);
            		if(item.productId == erpOrderUtil.chooseProductMation.productId){
            			winui.window.msg('子件清单中不能包含父件信息。', {icon: 2, time: 2000});
            			wheatherError = true;
            			return false;
            		}
            		if(item.needNum == 0){
            			winui.window.msg('子件数量不能为0。', {icon: 2, time: 2000});
            			wheatherError = true;
            			return false;
            		}
            	});
            	
            	if(wheatherError){
            		return false;
            	}
            	
                var params = {
                    bomTitle: $("#bomTitle").val(),
                    materialId: isNull(erpOrderUtil.chooseProductMation.productId) ? '' : erpOrderUtil.chooseProductMation.productId,//商品id
                    normsId: $("#unitList").val(),
                    remark: $("#remark").val(),
                    sealPrice: getSealPrice(erpOrderUtil.chooseProductMation.unitList, $("#unitList").val()),
                    childStr: JSON.stringify(childObject),
                    rowId: parent.rowId
                };
                
                AjaxPostUtil.request({url: flowableBasePath + "erpbom006", params: params, type: 'json', method: "PUT", callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
                }});
            }
            return false;
        });
        
        function getSealPrice(unitList, normId){
        	var unitIndex = -1;
        	$.each(unitList, function(i, item){
        		if(item.id == normId){
        			unitIndex = i;
        			return false;
        		}
        	});
        	if(unitIndex >= 0){
        		return unitList[unitIndex].salePrice;
        	}
        }
        
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
			},
			edit: {
				enable: true,
				drag: {
					isCopy: false,
					isMove: true,
					prev: true,
					inner: true,
					next: true
				},
				showRenameBtn: false
			},
			callback: {
				beforeDrag: beforeDrag,
				beforeDrop: beforeDrop,
				onDrop: onDrop,
				onRemove: onRemove
			}
        };
        
		//获取表格标题
		var li_head = $("#tableHeader").html();
		function loadTr(){
			var rows = $("#treeDemo").find('li');
			if(rows.length == 0) {
				$("#treeDemo").append(li_head);
				$("#treeDemo").append('<li><div style="text-align: center;line-height: 30px;" >无符合条件数据</div></li>')
			} else {
				rows.eq(0).before(li_head)
			}
			//刷新节点数据重置金额
			$.each(ztreeNode, function(i, item){
				$("#allPrice" + item.productId).html(parseInt(item.needNum) * parseFloat(item.unitPrice));
			});
		}
		
		//在拖拽之前
		function beforeDrag(treeId, treeNodes) {
			return true;
		}
		
		//用于捕获节点拖拽操作结束之前的事件回调函数，并且根据返回值确定是否允许此拖拽操作
		function beforeDrop(treeId, treeNodes, targetNode, moveType) {
			return true;
		}
		
		//拖拽操作结束后的回调函数
		function onDrop(event, treeId, treeNodes, targetNode, moveType) {
			var nodesIndex = -1;//拖拽节点所在索引
			$.each(ztreeNode, function(i, item){
				//拖拽节点
				if(item.productId == treeNodes[0].productId){
					nodesIndex = i;
					return false;
				}
			});
			//"inner"：成为子节点，"prev"：成为同级前一个节点，"next"：成为同级后一个节点
			//如果 moveType = null，表明拖拽无效
			if(!isNull(moveType)){
				if('inner' == moveType){
					//依然为父节点
					if(nodesIndex >= 0){
						ztreeNode[nodesIndex].pId = targetNode.productId;
					}
				}
			}
			//拖拽节点是否为父目录
			if(nodesIndex >= 0){
				ztreeNode[nodesIndex].isParent = 'true';
			}
			
			//修改目标节点为父目录
			if(targetNode){
				targetNode.isParent = 'true';
			}
			//刷新树节点
			refreshTree();
		}
		
		//移除节点
		function onRemove(event, treeId, treeNode) {
			var treeNodeIndex = -1;
			deleteNode(treeNode);
			//刷新树节点
			refreshTree();
		}
		
		//删除节点操作
		function deleteNode(treeNode){
			$.each(ztreeNode, function(i, item){
				if(item.productId == treeNode.productId){
					treeNodeIndex = i;
					return false;
				}
			});
			if(treeNodeIndex >= 0){
				ztreeNode.splice(treeNodeIndex, 1);
			}
			if(!isNull(treeNode.children) && treeNode.children.length > 0){
				$.each(treeNode.children, function(i, item){
					deleteNode(item);
				});
			}
		}
		
		/**
		 * 刷新树节点
		 */
		function refreshTree(){
			var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			treeObj.refresh();
			loadTr();
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
        
		//新增子件
		$("body").on("click", "#addRow", function (e) {
			productMationList = [];
			_openNewWindows({
 				url: "../../tpl/material/materialChooseToProduce.html", 
 				title: "选择商品",
 				pageId: "materialChooseToProduce",
 				area: ['90vw', '90vh'],
 				callBack: function(refreshCode) {
					$.each(productMationList, function(i, item){
						if(!inZtreeNode(item.productId)){
							ztreeNode.push(item);
						}
					});
					$.fn.zTree.init($("#treeDemo"), setting, ztreeNode);
					loadTr();
 				}});
		});
		
		//工序选择
	    $("body").on("click", ".procedureSel", function() {
	    	var proId = $(this).attr("id").replace("procedureSel", "");
	    	var selIndex = -1;
	    	$.each(ztreeNode, function(i, item){
				if(item.productId == proId){
					selIndex = i;
					return false;
				}
			});
			if(selIndex >= 0){
				procedureMationList = [].concat(ztreeNode[selIndex].procedureMationList);
		    	_openNewWindows({
					url: "../../tpl/erpWorkProcedure/erpWorkProcedureChoose.html", 
					title: "工序选择",
					pageId: "erpWorkProcedureChoose",
					area: ['90vw', '90vh'],
					callBack: function(refreshCode) {
						ztreeNode[selIndex].procedureMationList = [].concat(procedureMationList);
						var str = "";
						var title = "";
						$.each(procedureMationList, function(i, item){
							str += '' +
									'<span class="layui-badge layui-bg-gray">' + item.number + '</span>' + item.procedureName + '，';
							title += item.number + '、' + item.procedureName + '\n';
						});
						$("#procedureBox" + proId).html('<img class="procedureSel" id="procedureSel' + proId + '" src="../../assets/images/forum-menu.png"/>' + str);
						$("#procedureBox" + proId).attr('title', title);
					}});
			} else {
				winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
			}
	    });
	    
	    // 商品选择
 	    $("body").on("click", "#productNameSel", function (e) {
			erpOrderUtil.openMaterialChooseChoosePage(function (chooseProductMation) {
				$("#productName").val(chooseProductMation.productName);
				$("#productModel").val(chooseProductMation.productModel);
				$("#unitList").html(getDataUseHandlebars(selTemplate, {rows: chooseProductMation.unitList}));
				form.render("select");
			});
 	    });
		
		/**
		 * 判断该商品是否在树节点里面
		 */
		function inZtreeNode(productId){
			var inNodeIndex = -1;
			$.each(ztreeNode, function(i, item){
 	        	if(item.productId == productId){
 	        		inNodeIndex = i;
 	        		return false;
 	        	}
        	});
        	if(inNodeIndex >= 0){
        		return true;
        	} else {
        		return false;
        	}
		}
		
		// 数量变化
		$("body").on("input", ".needNum", function() {
			var proId = $(this).attr("id").replace("needNum", "");
			var value = parseInt(isNull($(this).val()) ? "0" : $(this).val());
			calculatedTotalPrice(proId, value);
		});
		$("body").on("change", ".needNum", function() {
			var proId = $(this).attr("id").replace("needNum", "");
			var value = parseInt(isNull($(this).val()) ? "0" : $(this).val());
			calculatedTotalPrice(proId, value);
		});
		function calculatedTotalPrice(proId, needNum){
			var inNodeIndex = -1;
			$.each(ztreeNode, function(i, item){
 	        	if(item.productId == proId){
 	        		inNodeIndex = i;
 	        		return false;
 	        	}
        	});
        	if(inNodeIndex >= 0){
        		ztreeNode[inNodeIndex].needNum = needNum;
	        	$("#allPrice" + proId).html(needNum * parseFloat(ztreeNode[inNodeIndex].unitPrice));
	        	//修改节点信息
	        	var zTree = $.fn.zTree.getZTreeObj("treeDemo");
	        	var node = zTree.getNodeByParam("productId", proId);
	        	node.needNum = needNum;
	        	zTree.updateNode(node);
        	}
		}
		
		$("body").on("click", ".diy-input", function() {
            $(this).focus();
        });
		
        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});