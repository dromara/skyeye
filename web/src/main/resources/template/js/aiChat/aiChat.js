layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    var $ = layui.$;
    let beanMessageTemplate = $("#beanMessageTemplate").html();
    let listMessageTemplate = $("#listMessageTemplate").html();
    let userMation = {};
    let listIndex = 0;
    let page = 1;
    let loadData = true;
    let apiKeyId = '555438cb1e3b4e328759680fdfa8d92';//默认接口

    let onMsgStr = '';
    systemCommonUtil.getSysCurrentLoginUserMation(function (data) {
        userMation = data.bean;
        loadChatHistory();

        // 消息
        webSocketUtil.init({
            // url: sysMainMation.aiSocketPath,
            url: 'ws://127.0.0.1:8120/',
            path: 'aiMessageWebSocket',
            userId: data.bean.id,
            onMessage: function (data) {
                let json = JSON.parse(data);
                console.log(json)
                if (!json.end) {
                    onMsgStr += json.message;
                    if (json.orderBy == 0) {
                        // 添加回答
                        let bean = {
                            aiId: "aiContentId" + listIndex,
                            avatar: fileBasePath + userMation.userPhoto,
                        }
                        let str = getDataUseHandlebars(beanMessageTemplate, bean);
                        $('.chat-box').append(str);
                        editormd.markdownToHTML(bean.aiId, {markdown: onMsgStr});
                    } else {
                        $("#aiContentId" + listIndex).html(onMsgStr);
                    }
                } else {
                    $("#aiContentId" + listIndex).html('');
                    editormd.markdownToHTML("aiContentId" + listIndex, {markdown: onMsgStr});
                    onMsgStr = '';
                }
                // 滚动到底部
                $('.chat-box').scrollTop($('.chat-box').prop('scrollHeight'));
            }
        });
    });

    // 历史记录
    function loadChatHistory() {
        let params = {
            page: page,
            limit: 15,
            holderId: 'cf32034645ca4d7c9ef01e7bba81e3e6'
        }
        AjaxPostUtil.request({
            url: sysMainMation.aiBasePath + "queryPageMessageList",
            params: params,
            type: 'json',
            method: 'POST',
            callback: function (json) {
                // 将数据转为正序排序
                let rows = resetList(json.rows);
                rows.sort(function (a, b) {
                    return b.orderBy - a.orderBy; // 升序排序
                });
                $('.chat-box').prepend(getDataUseHandlebars(listMessageTemplate, rows));
                $.each(rows, function (ii, item) {
                    if (!isNull(item.aiId)) {
                        editormd.markdownToHTML(item.aiId, {markdown: item.content});
                    }
                });
                if (page * 15 > json.total && loadData) {
                    loadData = false;
                    $('.chat-box').prepend("<div class='more'>没有更多数据..</div>");
                }
                if (page == 1) {
                    // 滚动到底部
                    $('.chat-box').scrollTop($('.chat-box').prop('scrollHeight'));
                }
            }
        });
    }

    // 向上滚动加载
    $(document).ready(function () {
        $('#chat-box').scroll(function (e) {
            var elem = e.target;
            var scrollTop = elem.scrollTop;
            var scrollHeight = elem.scrollHeight;
            var height = elem.offsetHeight;
            var direction = 'down'; // 默认向下滚动
            if (scrollTop === 0) {
                direction = 'up'; // 当滚动到顶部时
            } else if (scrollTop + height === scrollHeight) {
                direction = 'down'; // 当滚动到底部时
            }
            if (direction === 'up' && loadData) {
                page++;
                loadChatHistory();
            }
        });
    });

    // 遍历历史聊天记录
    function resetList(list) {
        let newList = [];
        $.each(list, function (ii, item) {
            listIndex++;
            newList.push({
                aiId: "aiContentId" + listIndex,
                content: item.content,
                avatar: '../../assets/images/aichat/AIhead.png',
                orderBy: listIndex
            });
            listIndex++;
            newList.push({
                message: item.message,
                avatar: fileBasePath + userMation.userPhoto,
                orderBy: listIndex
            });
        });
        return newList;
    }

    // 点击发送
    $("body").on("click", "#sendMessage", function (e) {
        var messageInput = $("#messageInput").val().trim();
        if (!isNull(messageInput)) {
            let params = {
                content: messageInput,
                avatar: fileBasePath + userMation.userPhoto,
                apiKeyId: apiKeyId,
            }
            var message = getDataUseHandlebars(beanMessageTemplate, params);
            $('.chat-box').append(message);
            // 清空输入框
            $('textarea').val('');
            // 滚动到底部
            $('.chat-box').scrollTop($('.chat-box').prop('scrollHeight'));
            // 发送请求
            AjaxPostUtil.request({
                url: "http://127.0.0.1:8120/" + "sendMessageStream",
                params: params,
                type: 'json',
                method: 'POST',
                callback: function (json) {
                    listIndex++;
                    // 清空输入框
                    $('textarea').val('');
                    // 滚动到底部
                    $('.chat-box').scrollTop($('.chat-box').prop('scrollHeight'));
                }
            });
        }
    });

    $("body").on("click", ".layui-nav-item", function (e) {
        var index = $(".layui-nav-item").index(this);
        if (index == 0) {
            //讯飞
            apiKeyId = '555438cb1e3b4e328759680fdfa8d92';
        } else if (index == 1) {
            //文心
            apiKeyId = '6a13b4ac47bb487c95609c8770c5955b';
        } else if (index == 2) {
            //通义
            apiKeyId = '2227ff7247ee47edaa2f5a3910b907bf';
        }
        console.log(index,apiKeyId);

        // loadApiKey(apiKeyId);
    });
    // $(function() {
    //     console.log(apiKeyId);
    // });
    // (function loop() {
    //     console.log(apiKeyId);
    //     setTimeout(loop, 2000);
    // })();

// 监听回车键发送消息
// document.getElementById('messageInput').addEventListener('keypress', function (e) {
//     if (e.key === 'Enter') {
//         sendMessage();
//     }
// });
    exports('aiChat', {});
});
