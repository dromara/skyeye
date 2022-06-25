/**
 * 
 * @param zTreeId ztree对象的id,不需要#
 * @param searchField 输入框选择器
 * @param isHighLight 是否高亮,默认高亮,传入false禁用
 * @param isExpand 是否展开,默认合拢,传入true展开
 * @returns
 */ 
 function fuzzySearch(zTreeId, searchField, isHighLight, isExpand){
    var zTreeObj = $.fn.zTree.getZTreeObj(zTreeId);//获取树对象
    if(!zTreeObj){
        alter("获取树对象失败");
    }
    var nameKey = zTreeObj.setting.data.key.name; //获取name属性的key
    isHighLight = isHighLight===false?false:true;//除直接输入false的情况外,都默认为高亮
    isExpand = isExpand?true:false;
    zTreeObj.setting.view.nameIsHTML = isHighLight;//允许在节点名称中使用html,用于处理高亮
    
    var metaChar = '[\\[\\]\\\\\^\\$\\.\\|\\?\\*\\+\\(\\)]'; //js正则表达式元字符集
    var rexMeta = new RegExp(metaChar, 'gi');//匹配元字符的正则表达式
    
    // 过滤ztree显示数据
    function ztreeFilter(zTreeObj,_keywords,callBackFunc) {
        if(!_keywords){
            _keywords =''; //如果为空，赋值空字符串
        }
        
        // 查找符合条件的叶子节点
        function filterFunc(node) {
            if(node && node.oldname && node.oldname.length>0){
                node[nameKey] = node.oldname; //如果存在原始名称则恢复原始名称
            }
            //node.highlight = false; //取消高亮
            zTreeObj.updateNode(node); //更新节点让之前对节点所做的修改生效
            if (_keywords.length == 0) { 
                //如果关键字为空,返回true,表示每个节点都显示
                zTreeObj.showNode(node);
                zTreeObj.expandNode(node,isExpand); //关键字为空时是否展开节点
                return true;
            }
            //节点名称和关键字都用toLowerCase()做小写处理
            if (node[nameKey] && node[nameKey].toLowerCase().indexOf(_keywords.toLowerCase())!=-1) {
                if(isHighLight){ //如果高亮，对文字进行高亮处理
                    //创建一个新变量newKeywords,不影响_keywords在下一个节点使用
                    //对_keywords中的元字符进行处理,否则无法在replace中使用RegExp
                    var newKeywords = _keywords.replace(rexMeta,function(matchStr){
                        //对元字符做转义处理
                        return '\\' + matchStr;
                        
                    });
                    node.oldname = node[nameKey]; //缓存原有名称用于恢复
                    //为处理过元字符的_keywords创建正则表达式,全局且不分大小写
                    var rexGlobal = new RegExp(newKeywords, 'gi');//'g'代表全局匹配,'i'代表不区分大小写
                    //无法直接使用replace(/substr/g,replacement)方法,所以使用RegExp
                    node[nameKey] = node.oldname.replace(rexGlobal, function(originalText){
                        //将所有匹配的子串加上高亮效果
                        var highLightText =
                            '<span style="color: whitesmoke;background-color: darkred;">'
                            + originalText
                            +'</span>';
                        return  highLightText;                  
                    });
                    //================================================//
                    //node.highlight用于高亮整个节点
                    //配合setHighlight方法和setting中view属性的fontCss
                    //因为有了关键字高亮处理,所以不再进行相关设置
                    //node.highlight = true; 
                    //================================================//
                    zTreeObj.updateNode(node); //update让更名和高亮生效
                }
                zTreeObj.showNode(node);//显示符合条件的节点
                return true; //带有关键字的节点不隐藏
            }
            
            zTreeObj.hideNode(node); // 隐藏不符合要求的节点
            return false; //不符合返回false
        }
        var nodesShow = zTreeObj.getNodesByFilter(filterFunc); //获取匹配关键字的节点
        processShowNodes(nodesShow, _keywords);//对获取的节点进行二次处理
    }
    
    /**
     * 对符合条件的节点做二次处理
     */
    function processShowNodes(nodesShow,_keywords){
        if(nodesShow && nodesShow.length>0){
            //关键字不为空时对关键字节点的祖先节点进行二次处理
            if(_keywords.length>0){ 
                $.each(nodesShow, function(n,obj){
                    var pathOfOne = obj.getPath();//向上追溯,获取节点的所有祖先节点(包括自己)
                    if(pathOfOne && pathOfOne.length>0){ //对path中的每个节点进行操作
                        // i < pathOfOne.length-1, 对节点本身不再操作
                        for(var i=0;i<pathOfOne.length-1;i++){
                            zTreeObj.showNode(pathOfOne[i]); //显示节点
                            zTreeObj.expandNode(pathOfOne[i],true); //展开节点
                        }
                    }
                });             
            } else { //关键字为空则显示所有节点, 此时展开根节点
                var rootNodes = zTreeObj.getNodesByParam('level','0');//获得所有根节点
                $.each(rootNodes,function(n,obj){
                    zTreeObj.expandNode(obj,true); //展开所有根节点
                });
            }
        }
    }
    
    //监听关键字input输入框文字变化事件
    $(searchField).bind('input propertychange', function() {
        var _keywords = $(this).val();
        searchNodeLazy(_keywords); //调用延时处理
    });

    var timeoutId = null;
    // 有输入后定时执行一次，如果上次的输入还没有被执行，那么就取消上一次的执行
    function searchNodeLazy(_keywords) {
        if (timeoutId) { //如果不为空,结束任务
            clearTimeout(timeoutId);
        }
        timeoutId = setTimeout(function() {
            ztreeFilter(zTreeObj,_keywords);    //延时执行筛选方法
            $(searchField).focus();//输入框重新获取焦点
        }, 500);
    }
}