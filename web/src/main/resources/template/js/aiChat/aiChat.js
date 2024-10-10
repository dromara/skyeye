layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    var $ = layui.$;

    // matchingLanguage();
    // $("body").on("click", ".layadmin-setTheme-color-scheme", function (e) {
    //     var className = $(this).attr("className");
    //     parent.$("body").attr("class", className);
    // });

    document.querySelector('.chat-input button').addEventListener('click', function () {
        var message = document.querySelector('.chat-input textarea').value;
        if (message.trim() !== '') {
            // 这里应该是发送消息到服务器的代码
            console.log('Sending message:', message);

            // 显示消息到聊天框
            var chatBox = document.querySelector('.chat-box');
            var messageElement = document.createElement('div');
            messageElement.className = 'message-style'; // 添加类名,给文本加样式，没写
            // messageElement.textContent = message;

            // 创建头像元素
            var avatar = document.createElement('img');
            avatar.src = '../../assets/images/aichat/AIhead.png'; // 头像图片的路径
            avatar.alt = 'User Avatar'; // 图片的替代文本
            avatar.className='avatar'
            // avatar.style.width = '30px'; // 设置头像宽度
            // avatar.style.height = '30px'; // 设置头像高度，通常宽度和高度会保持一致以保持图片的比例
            // avatar.style.borderRadius = '10%'; // 将头像设置为圆形（可选）
            // avatar.style.margin=' 0 10px 10px 5px';

            // 将头像添加到消息元素中
            messageElement.appendChild(avatar);

            // 创建文本元素并添加到消息元素中
            var textElement = document.createElement('span');
            textElement.className='text'
            textElement.textContent = message;
            // textElement.style.marginLeft = '10px'; // 可选：给文本一些左边距以与头像分隔
            messageElement.appendChild(textElement);

            // 最后，将消息元素添加到聊天框中
            chatBox.appendChild(messageElement);

            // messageElement.innerHTML = `
            // <img src="../../assets/images/aichat/AIchat.png" alt="User Avatar" class="avatar">
            // <div class="text">${message}</div>
            // `;

            // 清空输入框
            document.querySelector('.chat-input textarea').value = '';
            // 滚动到底部
            chatBox.scrollTop = chatBox.scrollHeight;
        }
    });

    // 监听回车键发送消息
    document.getElementById('messageInput').addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            sendMessage();
        }
    });

    exports('blogitem', {});
});
