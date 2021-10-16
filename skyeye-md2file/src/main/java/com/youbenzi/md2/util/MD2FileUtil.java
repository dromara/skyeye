/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MD2FileUtil {

	public static ByteArrayOutputStream inputStream2ByteArrayOutputStream(InputStream is) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	byte[] buffer = new byte[1024];
    	int len;
    	while ((len = is.read(buffer)) > -1 ) {
    		baos.write(buffer, 0, len);
    	}
    	baos.flush();	
        return baos;
	}
	
}
