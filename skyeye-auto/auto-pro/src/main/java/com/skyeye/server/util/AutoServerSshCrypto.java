/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.AES;

/**
 * 服务器 SSH 密码加解密。
 * 库内以 ENC: 前缀标识密文；采集时解密为明文再连 SSH。
 * 兼容历史明文：无 ENC: 前缀时原样返回。
 */
public final class AutoServerSshCrypto {

    private static final String PREFIX = "ENC:";

    /**
     * AES-128 密钥，固定 16 字节（128 bit）。不要改长度。
     */
    private static final byte[] KEY = new byte[]{
        'S', 'k', 'y', 'E', 'y', 'e', 'A', 'u',
        't', 'o', 'S', 's', 'h', 'K', 'e', 'y'
    };

    private AutoServerSshCrypto() {
    }

    private static AES aes() {
        if (KEY.length != 16) {
            throw new IllegalStateException("SSH AES key must be 16 bytes, actual=" + KEY.length);
        }
        return new AES(KEY);
    }

    public static boolean isEncrypted(String value) {
        return StrUtil.startWith(value, PREFIX);
    }

    public static String encrypt(String plain) {
        if (StrUtil.isBlank(plain) || isEncrypted(plain)) {
            return plain;
        }
        return PREFIX + aes().encryptBase64(plain);
    }

    public static String decrypt(String stored) {
        if (StrUtil.isBlank(stored) || !isEncrypted(stored)) {
            return stored;
        }
        return aes().decryptStr(StrUtil.removePrefix(stored, PREFIX));
    }
}
