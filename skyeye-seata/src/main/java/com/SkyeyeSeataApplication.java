/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com;

import io.seata.server.ServerApplication;

import java.io.IOException;

/**
 * SkyEye Seata Server 启动入口。
 * <p>
 * 配置：{@code bootstrap.yml}（Nacos）+ {@code application.yml}（Seata TC）。
 * 控制台 HTTP 7091；事务端口 8091。
 * </p>
 *
 * @author skyeye云系列
 */
public class SkyeyeSeataApplication {

    public static void main(String[] args) throws IOException {
        ServerApplication.main(args);
    }

}
