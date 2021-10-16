/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.common.util;

public class IPEntry {
    public String beginIp;
    public String endIp;
    public String country;
    public String area;
    public IPEntry() {
        beginIp = endIp = country = area = "";
    }
    public String toString() {
        return this.area + "  " + this.country + "IP范围:" + this.beginIp + "-" + this.endIp;
    }
}
