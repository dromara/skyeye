package com.skyeye.common.object;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class PutObject implements Serializable{
	
	
    /**
     * @Fields serialVersionUID : 标识
     */
	private static final long serialVersionUID = 1L;
	
	private static ThreadLocal<HttpServletRequest> _request = new ThreadLocal<HttpServletRequest>();  
    private static ThreadLocal<HttpServletResponse> _response = new ThreadLocal<HttpServletResponse>();
	
	public PutObject(){
		
	}
	
	public PutObject(HttpServletRequest request, HttpServletResponse response){
		_request.set(request);
		_response.set(response);
	}

	public static HttpServletRequest getRequest() {
		HttpServletRequest request = _request.get();  
        return request;
	}
	
	public static HttpServletResponse getResponse() {
		HttpServletResponse response = _response.get();  
        return response;
	}
	
	public static void removeRequest() {  
        _request.remove();  
        _response.remove();
    }
	
}
