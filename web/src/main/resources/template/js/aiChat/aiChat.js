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

    systemCommonUtil.getSysCurrentLoginUserMation(function (data) {
        userMation = data.bean;
        loadChatHistory();
    });

    // 历史记录
    function loadChatHistory () {
        let params = {
            page: page,
            limit: 15,
            holderId: 'cf32034645ca4d7c9ef01e7bba81e3e6'
        }
        AjaxPostUtil.request({url: sysMainMation.aiBasePath + "queryPageMessageList", params: params, type: 'json', method: 'POST', callback: function (json) {
            // 将数据转为正序排序
            let rows = resetList(json.rows);
            rows.sort(function(a, b) {
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
                $('.chat-box').prepend("没有更多数据..").css();
            }
            if (page == 1) {
                // 滚动到底部
                $('.chat-box').scrollTop($('.chat-box').prop('scrollHeight'));
            }
        }});
    }

    // 向上滚动加载
    $(document).ready(function() {
        $('#chat-box').scroll(function(e) {
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

    $("body").on("click", "#sendMessage", function (e) {
        var messageInput = $("#messageInput").val().trim();
        if (!isNull(messageInput)) {
            let params = {
                message: messageInput,
                avatar: fileBasePath + userMation.userPhoto,
                apiKeyId: 'cf32034645ca4d7c9ef01e7bba81e3e6'
            }
            var message = getDataUseHandlebars(beanMessageTemplate, params);
            $('.chat-box').append(message);
            // 清空输入框
            $('textarea').val('');
            // 滚动到底部
            $('.chat-box').scrollTop($('.chat-box').prop('scrollHeight'));
            // 发送请求
            AjaxPostUtil.request({url: "http://192.168.3.8:8120/" + "sendChatMessage", params: params, type: 'json', method: 'POST', callback: function (json) {
                listIndex++;
                // 添加回答
                let bean = {
                    aiId: "aiContentId" + listIndex,
                    content: json.bean.content,
                    avatar: fileBasePath + userMation.userPhoto,
                }
                message = getDataUseHandlebars(beanMessageTemplate, bean);
                $('.chat-box').append(message);
                editormd.markdownToHTML(bean.aiId, {markdown: bean.content});
                // 清空输入框
                $('textarea').val('');
                // 滚动到底部
                $('.chat-box').scrollTop($('.chat-box').prop('scrollHeight'));
            }});
        }
    });

    $("body").on("click", ".layui-nav-item", function (e) {

    });
// 监听回车键发送消息
// document.getElementById('messageInput').addEventListener('keypress', function (e) {
//     if (e.key === 'Enter') {
//         sendMessage();
//     }
// });
    exports('aiChat', {});
});
