{{#bean}}
	<form class="layui-form" action="" id="showForm" autocomplete="off">
		<div class="layui-form-item">
            <label class="layui-form-label">
            	<i class="fa fa-check-circle-o" style="font-size: 48px; color: rgb(3, 144, 255);"></i>
            </label>
            <div class="layui-input-block ver-center">
	            <div class="layui-form-mid layui-word-aux">
	            	<font style="color: rgb(3, 144, 255);">成功生成分享链接</font><br>
	            	<font style="font-size: 11px;">通过OA论坛、OA聊天、QQ、微信、微博等分享给好友吧</font>
	            </div>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">链接</label>
            <div class="layui-input-block">
                <input type="text" id="shareUrl" value="{{shareUrl}}" class="layui-input" readonly/>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label"></label>
            <div class="layui-input-block">
                <button class="layui-btn layui-btn-normal" id="copyBtn" type="button" data-clipboard-text="">复制</button>
            </div>
        </div>
     </form>
{{/bean}}