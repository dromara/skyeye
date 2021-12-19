/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.object;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

public class PutObject implements Serializable{
	
    /**
     * @Fields serialVersionUID : 标识
     */
	private static final long serialVersionUID = 1L;
	
	private static ThreadLocal<MessageEntity> model = new ThreadLocal<MessageEntity>();  
	
	public PutObject(){
	}
	
	public PutObject(HttpServletRequest request, HttpServletResponse response){
		model.set(new MessageEntity(request, response));
	}

	public static HttpServletRequest getRequest() {
		if(model.get() == null){
			return null;
		}
        return model.get().getRequest();
	}
	
	public static HttpServletResponse getResponse() {
        return model.get().getResponse();
	}
	
	public static void remove() {  
		model.remove();
    }
	
	public static MessageEntity getModelEntity(){
		return model.get();
	}
	
}
