/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.push;

import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.uitls.AppConditions;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;

import java.util.ArrayList;
import java.util.List;

public class PhonePushUtil {

	private static String appId = "lXg5MYSNFb6GVbo9vsZ5W8";
	private static String appKey = "6uMnawZCW089FdQ5FAEyO3";
	private static String masterSecret = "Yzqd8lNrPg7EFQfJoC8Xl5";
	public static String host = "https://sdk.open.api.igexin.com/apiex.htm";
	
	// 在打包后的APP的js中获取
	// var cId = plus.push.getClientInfo().clientid;
	public static IGtPush push;

	static {
		push = new IGtPush(host, appKey, masterSecret);
	}

	/**
	 * 对单个用户推送消息
	 *
	 * @param alias
	 * @param msg
	 * @return
	 */
	public static boolean pushMessageToSingle(String cid, String title, String text, String transMsg) {
		IGtPush push = new IGtPush(host, appKey, masterSecret);
		NotificationTemplate template = notificationTemplate(title, text, transMsg);
		SingleMessage message = new SingleMessage();
		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(24 * 3600 * 1000);
		message.setData(template);
		// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
		message.setPushNetWorkType(0);
		Target target = new Target();
		target.setAppId(appId);
		target.setClientId(cid);
		IPushResult ret = null;
		try {
			ret = push.pushMessageToSingle(message, target);
		} catch (RequestException e) {
			e.printStackTrace();
			ret = push.pushMessageToSingle(message, target, e.getRequestId());
		}
		if (ret != null && ret.getResponse() != null && ret.getResponse().containsKey("result")) {
			System.out.println(ret.getResponse().toString());
			if (ret.getResponse().get("result").toString().equals("ok") && ret.getResponse().containsKey("status")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定应用的所有用户群发推送消息
	 *
	 * @param msg
	 */
	public static boolean pushtoAPP(String text, String transMsg) {
		IGtPush push = new IGtPush(host, appKey, masterSecret);

		NotificationTemplate template = notificationTemplate("title", text, transMsg);
		AppMessage message = new AppMessage();
		message.setData(template);

		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(24 * 1000 * 3600);
		// 推送给App的目标用户需要满足的条件
		AppConditions cdt = new AppConditions();
		List<String> appIdList = new ArrayList<String>();
		appIdList.add(appId);
		message.setAppIdList(appIdList);
		// 手机类型
		List<String> phoneTypeList = new ArrayList<String>();
		// 省份
		List<String> provinceList = new ArrayList<String>();
		// 自定义tag
		List<String> tagList = new ArrayList<String>();

		cdt.addCondition(AppConditions.PHONE_TYPE, phoneTypeList);
		cdt.addCondition(AppConditions.REGION, provinceList);
		cdt.addCondition(AppConditions.TAG, tagList);
		message.setConditions(cdt);

		IPushResult ret = push.pushMessageToApp(message, "msg_toApp");

		if (ret != null && ret.getResponse() != null && ret.getResponse().containsKey("result")) {
			System.out.println(ret.getResponse().toString());
			if (ret.getResponse().get("result").toString().equals("ok")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 对单个用户推送透传消息
	 *
	 * @param alias
	 * @param title
	 * @param content
	 * @return
	 */
	public static boolean pushTransMessageToSingle(String cid, String msg) {
		TransmissionTemplate template = transTemplate(cid, msg);
		SingleMessage message = new SingleMessage();
		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(24 * 3600 * 1000);
		message.setData(template);
		// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
		message.setPushNetWorkType(0);
		Target target = new Target();
		target.setAppId(appId);
		target.setClientId(cid);
		IPushResult ret = null;
		try {
			ret = push.pushMessageToSingle(message, target);
		} catch (RequestException e) {
			e.printStackTrace();
			ret = push.pushMessageToSingle(message, target, e.getRequestId());
		}
		if (ret != null && ret.getResponse() != null && ret.getResponse().containsKey("result")) {
			System.out.println(ret.getResponse().toString());
			if (ret.getResponse().get("result").toString().equals("ok") && ret.getResponse().containsKey("status")) {
				return true;
			}
		}
		return false;
	}

	public static void pushMessageToIBatch(List<String> alias, String msg) {
		IBatch batch = push.getBatch();
		IPushResult ret = null;
		try {
			for (int i = 0; i < alias.size(); i++) {
				// 构建客户a的透传消息a
				constructClientTransMsg(alias.get(i), msg, batch);
			}
			// 构建客户B的点击通知打开网页消息b
			// constructClientLinkMsg(CID_B,"msgB",batch);
			ret = batch.submit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(ret.getResponse().toString());
	}

	private static NotificationTemplate notificationTemplate(String title, String text, String obj) {
		NotificationTemplate template = new NotificationTemplate();
		// 设置APPID与APPKEY
		template.setAppId(appId);
		template.setAppkey(appKey);
		// 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
		template.setTransmissionType(1);
		//透传内容（点击通知后SDK将透传内容传给你的客户端，需要客户端做相应开发）
		template.setTransmissionContent(obj);
		// 设置定时展示时间
		Style0 style = new Style0();
		// 设置通知栏标题与内容
		style.setTitle(title);
		style.setText(text);
		// 配置通知栏图标
		style.setLogo("XXX");
		// 配置通知栏网络图标
		// 设置通知是否响铃，震动，或者可清除
		style.setRing(true);
		style.setVibrate(true);
		style.setClearable(true);
		template.setStyle(style);

		return template;
	}

	private static TransmissionTemplate transTemplate(String cid, String msg) {
		TransmissionTemplate template = new TransmissionTemplate();
		// 设置APPID与APPKEY
		template.setAppId(appId);
		template.setAppkey(appKey);
		template.setTransmissionContent(msg);
		template.setTransmissionType(0); // 这个Type为int型，填写1则自动启动app

		return template;
	}

	// 点击通知打开应用模板
	public static void constructClientTransMsg(String cid, String msg, IBatch batch) throws Exception {

		SingleMessage message = new SingleMessage();
		NotificationTemplate template = new NotificationTemplate();
		// TransmissionTemplate template = new TransmissionTemplate();//自定义模板
		template.setAppId(appId);
		template.setAppkey(appKey);
		template.setTransmissionContent(msg);// 消息内容
		template.setTransmissionType(1); // 这个Type为int型，填写1则自动启动app
		Style0 style = new Style0(); // 设置通知栏标题与内容
		style.setTitle("第一个通知");
		style.setText(msg); // 配置通知栏图标
		style.setLogo("icon.png"); // 配置通知栏网络图标
		style.setLogoUrl("");// 网络图标地址
		// 设置通知是否响铃，震动，或者可清除
		style.setRing(true);
		style.setVibrate(true);
		style.setClearable(true);
		template.setStyle(style);

		message.setData(template);
		message.setOffline(true);
		message.setOfflineExpireTime(60 * 60 * 1000);

		// 设置推送目标，填入appid和clientId
		Target target = new Target();
		target.setAppId(appId);
		target.setClientId(cid);
		batch.add(message, target);
	}

	// 点击通知打开网页消息
	public static void constructClientLinkMsg(String cid, String msg, IBatch batch, String url) throws Exception {
		LinkTemplate template = new LinkTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        Style0 style = new Style0();
        // 设置通知栏标题与内容
        style.setTitle("title");
        style.setText(msg);
        // 配置通知栏图标
        style.setLogo("icon.png");
        // 配置通知栏网络图标
        style.setLogoUrl("push.png");
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        style.setChannel("环球直聘-191022");
        style.setChannelName("环球直聘");
        style.setChannelLevel(3);
        template.setStyle(style);
        // 设置打开的网址地址
        template.setUrl(url);
        // 设置定时展示时间，安卓机型可用
        // 消息覆盖
		SingleMessage message = new SingleMessage();
		message.setData(template);
		message.setOffline(true);
		message.setOfflineExpireTime(60 * 1000);

		// 设置推送目标，填入appid和clientId
		Target target = new Target();
		target.setAppId(appId);
		target.setClientId(cid);
		batch.add(message, target);
	}
	
	public static void main(String[] args) {
		//aeac2c81b0c424b05fb2d79985f4f2f2
		//e28b9d927ada7280429056f06b12df79
		PhonePushUtil.pushMessageToSingle("aeac2c81b0c424b05fb2d79985f4f2f2", "测试消息", "内容", "{'type': 1}");
	}

}
