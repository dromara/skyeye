/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com;

import io.seata.server.ServerApplication;

import java.io.IOException;

/**
 * SkyEye Seata Server 启动入口。
 * <p>
 * 结构参考 {@code skyeye-zuul} 的独立中间件工程；运行的是 Seata 官方 TC（事务协调器），
 * 业务侧 erp / seal-service 等通过 Nacos 发现本服务（application = seata-server）。
 * </p>
 * <p>
 * 默认端口：控制台 HTTP 7091；事务端口 service-port 8091。
 * </p>
 *
 * @author skyeye云系列
 */
public class SkyeyeSeataApplication {

    public static void main(String[] args) throws IOException {
        // 委托官方 Seata Server Spring Boot 启动类
        ServerApplication.main(args);
    }

}
