layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    var $ = layui.$;

    document.querySelector('.chat-input button').addEventListener('click', function () {
        var message = document.querySelector('.chat-input textarea').value;
        if (message.trim() !== '') {
            // 这里应该是发送消息到服务器的代码
            console.log('Sending message:', message);
            // 显示消息到聊天框
            var chatBox = document.querySelector('.chat-box');
            var messageElement = document.createElement('div');
            messageElement.className = 'message-style'; // 添加类名,给文本加样式，没写
            // 创建头像元素
            var avatar = document.createElement('img');
            avatar.src = '../../assets/images/aichat/AIhead.png'; // 头像图片的路径
            avatar.alt = 'User Avatar'; // 图片的替代文本
            avatar.className='avatar'
            // 将头像添加到消息元素中
            messageElement.appendChild(avatar);
            // 创建文本元素并添加到消息元素中
            var textElement = document.createElement('span');
            textElement.className='message-bubble'
            textElement.textContent = message;
            messageElement.appendChild(textElement);
            // 将消息元素添加到聊天框中
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

    // 长按移动
    document.addEventListener('DOMContentLoaded', function () {
        var btn = document.getElementById('aiBtn');
        var isDragging = false;
        var startX, startY, startMouseX, startMouseY;

        btn.addEventListener('mousedown', function (e) {
            // 记录鼠标按下时的初始位置
            startX = e.clientX - btn.getBoundingClientRect().left;
            startY = e.clientY - btn.getBoundingClientRect().top;
            startMouseX = e.clientX;
            startMouseY = e.clientY;
            isDragging = true;

            // 监听鼠标移动和松开事件
            document.addEventListener('mousemove', onMouseMove);
            document.addEventListener('mouseup', onMouseUp);
        });

        function onMouseMove(e) {
            if (isDragging) {
                // 更新按钮的位置
                var newX = e.clientX - startMouseX + startX;
                var newY = e.clientY - startMouseY + startY;
                btn.style.right = 'auto';
                btn.style.bottom = 'auto';
                btn.style.left = newX + 'px';
                btn.style.top = newY + 'px';
            }
        }

        function onMouseUp() {
            isDragging = false;
            // 移除鼠标移动和松开事件监听器
            document.removeEventListener('mousemove', onMouseMove);
            document.removeEventListener('mouseup', onMouseUp);
        }
    });

    // exports('blogitem', {});
});
