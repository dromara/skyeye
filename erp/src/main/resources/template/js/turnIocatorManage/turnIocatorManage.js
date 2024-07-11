var folderId = "2";//当前所在目录的位置

var operaterId = "";//即将进行删除，重命名等操作的id

var folderTemplate;
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'dropdown', 'fsCommon', 'fsTree', 'viewer', 'contextMenu', 'ClipboardJS', 'colorpicker', 'jqueryUI', 'webuploader','tableTreeDj'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        fsTree = layui.fsTree,
        fsCommon = layui.fsCommon,
        colorpicker = layui.colorpicker,
        device = layui.device(),
        tableTree = layui.tableTreeDj;
    var currentUserId = "";
    // 获取当前登录员工信息
    systemCommonUtil.getSysCurrentLoginUserMation(function (data) {
        currentUserId = data.bean.id;
    });

    //遮罩层显示
    $(".fileconsole-mask").show();
    matchingLanguage();

    //下拉按钮
    var dropdown = new Dropdown();
    dropdown.render();


    var ztree = null;
    fsTree.render({
        id: "treeDemo",
        url: sysMainMation.diskCloudBasePath + "fileconsole001",
        checkEnable: false,
        loadEnable: true,//异步加载
        showLine: false,
        showIcon: true,
        addDiyDom: addDiyDom,
        clickCallback: zTreeOnClick,
        onRightClick: onRightClick,
        onDblClick: function(){},
        onAsyncSuccess: function(id){}
    }, function(id){
        ztree = $.fn.zTree.getZTreeObj(id);
        loadThisFolderChild();
    });


    function addDiyDom(treeId, treeNode) {
        // css样式以及位置调整
        var spaceWidth = 8;
        var switchObj = $("#" + treeNode.tId + "_switch"),
            icoObj = $("#" + treeNode.tId + "_ico");
        switchObj.remove();
        icoObj.before(switchObj);
        if (treeNode.level >= 1) {
            var spaceStr = "<span style='display: inline-block;width:" + (spaceWidth * treeNode.level) + "px'></span>";
            switchObj.before(spaceStr);
        }

        // 操作数据
        var aObj = $("#" + treeNode.tId + "_a");
        if ($("#diyBtn_" + treeNode.id).length > 0) return;
        aObj.after("");
        aObj.addClass("tree_a");
        aObj.attr("ztreerowid", treeNode.id);
    }

    // 树节点右键
    function onRightClick(event, treeId, treeNode) {
        folderId = treeNode.id;
        // 设置右键节点选中
        chooseNodeSelect(folderId);
        var par = {
            id: folderId,
            name: treeNode.name
        };
        $("#treeRight").html(getDataUseHandlebars($("#treeRightTemplate").html(), {bean: par}));
        {
            showRMenu('root', event.clientX, event.clientY);
        }
    }

    // 展示树节点右键菜单
    function showRMenu(type, x, y) {
        $("#treeRight .is-file").show();
        if(type == 'root'){
            $("#treeRight .add").hide();
            $("#treeRight .edit").hide();
            $("#treeRight .remove").hide();
        }
        $("#treeRight").show();
        $("#treeRight").css({top: y + "px", left: x + "px", visibility: "visible", position: "absolute"});
        $("body").bind("mousedown", onBodyMouseDown);
    }

    // 隐藏树节点右键菜单
    function hideRMenu() {
        if ($("#treeRight")) $("#treeRight").css({"visibility": "hidden"});
        $("body").unbind("mousedown", onBodyMouseDown);
    }

    function onBodyMouseDown(event){
        if (!($(event.target).parents(".is-file").length>0)) {
            $("#treeRight").css({"visibility" : "hidden"});
        }
    }

    // 刷新树指定节点
    function refreshTreePointNode(){
        // 刷新节点
        var nownode = ztree.getNodesByParam("id", folderId, null);
        ztree.reAsyncChildNodes(nownode[0], "refresh");
    }

    // 树的动画效果
    $("body").on("mouseover", "#treeDemo a", function (e) {
        $("#treeDemo").find("a").removeClass('mouseOver');
        $(this).addClass('mouseOver');
    });
    $("body").on("mouseleave", "#treeDemo", function (e) {
        $("#treeDemo").find("a").removeClass('mouseOver');
    });

    // 设置选中节点
    function chooseNodeSelect(nodeId){
        var selNode = ztree.getNodeByParam("id", nodeId, null);
        ztree.selectNode(selNode);
    }

    // 右侧内容右键操作
    $("body").on("contextmenu", "#file-content .file", function (e) {
        var _this = $(this);
        operaterId = _this.attr("rowid");
        // 1.加载右侧右键内容
        initFileContentRight(_this, e);
        // 2.加载选中
        setChooseFileContent(_this);
    });

    // 加载右侧右键内容
    function initFileContentRight(_this, e) {
        var par = {
            id: _this.attr("rowid"),
            name: _this.find(".title").html(),
            fileType: _this.attr("filetype")
        };
        $("#fileRightMenu").show();
        $("#fileRightMenu").html(getDataUseHandlebars($("#fileRightMenuTemplate").html(), {bean: par}));
        $("#fileRightMenu").css({top: e.clientY + "px", left: e.clientX + "px", visibility: "visible", position: "absolute"});
        if ($.inArray(_this.attr("filetype"), officeType) == -1) {
            $(".openByOnlyOffice").hide();
            $(".openByMicrosoftOffice").hide();
        }
    }


    // 加载选中
    function setChooseFileContent(_this){
        $(".select-op-more").find("button[class*='btn-custom']").show();
        $(".select-op-more").find("li[class*='is-file']").show();
        $(".select-op-more").show();
        $("#file-content").find("div").removeClass("active");
        // 获取创建人
        var createId = _this.attr("createId");
        // 权限控制
        authControllerFile(createId);
        // 其他选中取消
        $("#file-content .menu-folder .item-select .item-check").find("input:checkbox[name='checkFile']:checked").prop("checked", false);
        // 添加选中样式
        _this.addClass("active");
        // 设置当前选中
        _this.children(".item-select").children(".item-check").find("input:checkbox[name='checkFile']").prop("checked", true);
    }

    // 树节点点击事件
    function zTreeOnClick(event, treeId, treeNode) {
        var folderName = getFilePath(treeNode);
        $(".yarnball").html('<li class="yarnlet first"><a title="/">/</a></li>' + folderName);
        if(treeNode.id != folderId){
            folderId = treeNode.id;
            loadThisFolderChild();
        }
        // 如果节点不展开，则展开
        if(!treeNode.open){
            ztree.expandNode(treeNode);
        }
    };

    function getFileMation(id, callback) {
        AjaxPostUtil.request({url: sysMainMation.diskCloudBasePath + "queryFileConsoleById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
            if (typeof callback == "function") {
                callback(json.bean)
            }
        }});
    }

    // 文件夹或者文件重命名
    $("body").on("click", ".fileReName", function (e) {
        reNameSpecially();
        $(".layui-dropdown-menu").hide();
    });

    // 文件名保存
    $("body").on("click", ".filename-edit-save", function (e) {
        if(isNull($(this).parent().find('textarea').val())) {
            winui.window.msg('请填写文件名', {icon: 2, time: 2000});
        } else {
            $(this).parent().parent().css({"z-index": 0});
            editFolderById($(this).parent());
        }
    });

    // 文件名取消修改
    $("body").on("click", ".filename-edit-cancle", function (e) {
        $(this).parent().parent().css({"z-index": 0});
        $(this).parent().parent().find("div[class='filename']").show();
        $(this).parent().hide();
    });

    // 文件夹或者文件删除
    $("body").on("click", ".deleteFolderAndChild", function (e) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            var checkItems = $("#file-content .menu-folder .item-select .item-check").find("input:checkbox[name='checkFile']:checked");
            var deleteArray = new Array();
            $.each(checkItems, function(i, item) {
                var checkFile = $(item).parent().parent().parent();
                deleteArray.push({
                    id: checkFile.attr("rowid"),
                    fileType: checkFile.attr("filetype")
                });
            });
            $(".layui-dropdown-menu").hide();
            $(".select-op-more").hide();
            //如果选中项为空
            if(deleteArray.length == 0){
                winui.window.msg('请选择要删除的文件或文件夹', {icon: 2, time: 2000});
                return false;
            }
            deleteFolderAndChild(deleteArray, function() {
                // 刷新节点
                refreshTreePointNode();
                // 加载该目录下的文件
                loadThisFolderChild();
            });
        });
    });



    // // 删除文件夹以及该文件夹下的所有子内容
    // function deleteFolderAndChild(deleteArray, callBack){
    //     AjaxPostUtil.request({url: sysMainMation.diskCloudBasePath + "fileconsole004", params: {fileList: JSON.stringify(deleteArray)}, type: 'json', method: 'POST', callback: function (json) {
    //             winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    //             callBack();
    //         }});
    // }


    // 新建
    $("body").on("click", "#createNewFolder", function (e) {
        hideRMenu();
        createFolder(folderId, refreshTreePointNode, initMenuToBox, currentUserId);
    });

    // 树操作--新建
    $("body").on("click", ".treecreateNewFolder", function (e) {
        hideRMenu();
        loadThisFolderChild();
        createFolder(folderId, refreshTreePointNode, initMenuToBox, currentUserId);
    });

    // 树操作--刷新
    $("body").on("click", ".treerefreshContent", function (e) {
        refreshTreePointNode();
        loadThisFolderChild();
    });

    // 默认初始化树
    var initTreeSel = false;


    initRightMenu();
    //初始化右键
    function initRightMenu() {
        $("body").contextMenu({
            width: 190, // width
            itemHeight: 30, // 菜单项height
            bgColor: "#FFFFFF", // 背景颜色
            color: "#0A0A0A", // 字体颜色
            fontSize: 12, // 字体大小
            hoverBgColor: "#99CC66", // hover背景颜色
            target: function(ele) { // 当前元素
            },
            menu: [{
                text: "新建",
                img: "../../assets/images/icon/create-folder-icon.png",
                callback: function() {
                    createFolder(folderId, refreshTreePointNode, initMenuToBox, currentUserId);
                }
            }, { // 菜单项
                text: "新建文件",
                img: "../../assets/images/my-file-icon.png",
                children: [{
                    text: "txt文件",
                    img: "../../assets/images/txt-icon.png",
                    callback: function() {
                        createNumpyFile("txt", folderId, loadThisFolderChild());
                    }
                }, {
                    text: "html文件",
                    img: "../../assets/images/icon/html-icon.png",
                    callback: function() {
                        createNumpyFile("html", folderId, loadThisFolderChild());
                    }
                }, {
                    text: "--"
                }]
            }, {
                text: "--"
            },  {
                text: "--"
            }, {
                text: "刷新",
                img: "../../assets/images/refresh-icon.png",
                callback: function() {
                    loadThisFolderChild();
                }
            }
            ]
        });
    }

    // 负责view的销毁
    function removeOneFile(file) {
        var $li = $('#' + file.id);
        delete percentages[file.id];
        updateAllTotalProgress();
        $li.off().find('.file-panel').off().end().remove();
    }

    exports('turnIocatorManage', {});
});
