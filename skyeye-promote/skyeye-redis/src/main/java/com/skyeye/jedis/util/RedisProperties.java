package com.skyeye.jedis.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取redis配置信息并装载
 * 
 * @author 卫志强
 *
 */
@Component
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {
	
	private int expireSeconds;
	private String ip1;
	private String ip2;
	private String ip3;
	private String ip4;
	private String ip5;
	private String ip6;
	private String host1;
	private String host2;
	private String host3;
	private String host4;
	private String host5;
	private String host6;
	private int commandTimeout;

	public int getExpireSeconds() {
		return expireSeconds;
	}

	public void setExpireSeconds(int expireSeconds) {
		this.expireSeconds = expireSeconds;
	}

	public int getCommandTimeout() {
		return commandTimeout;
	}

	public void setCommandTimeout(int commandTimeout) {
		this.commandTimeout = commandTimeout;
	}

	public String getIp1() {
		return ip1;
	}

	public void setIp1(String ip1) {
		this.ip1 = ip1;
	}

	public String getIp2() {
		return ip2;
	}

	public void setIp2(String ip2) {
		this.ip2 = ip2;
	}

	public String getIp3() {
		return ip3;
	}

	public void setIp3(String ip3) {
		this.ip3 = ip3;
	}

	public String getIp4() {
		return ip4;
	}

	public void setIp4(String ip4) {
		this.ip4 = ip4;
	}

	public String getIp5() {
		return ip5;
	}

	public void setIp5(String ip5) {
		this.ip5 = ip5;
	}

	public String getIp6() {
		return ip6;
	}

	public void setIp6(String ip6) {
		this.ip6 = ip6;
	}

	public String getHost1() {
		return host1;
	}

	public void setHost1(String host1) {
		this.host1 = host1;
	}

	public String getHost2() {
		return host2;
	}

	public void setHost2(String host2) {
		this.host2 = host2;
	}

	public String getHost3() {
		return host3;
	}

	public void setHost3(String host3) {
		this.host3 = host3;
	}

	public String getHost4() {
		return host4;
	}

	public void setHost4(String host4) {
		this.host4 = host4;
	}

	public String getHost5() {
		return host5;
	}

	public void setHost5(String host5) {
		this.host5 = host5;
	}

	public String getHost6() {
		return host6;
	}

	public void setHost6(String host6) {
		this.host6 = host6;
	}
	
}
