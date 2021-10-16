/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public abstract class ImgHelper {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ImgHelper.class);

    public InputStream setImgByUrl(String url, String webRootNdc) throws Exception {
        return setImgByUrl(url, webRootNdc, true);
    }

    public InputStream setImgByUrl(String url, String webRootNdc, boolean getImgFromInternet) throws Exception {
        if (!getImgFromInternet) {
            setIntoFile(null);
            return null;
        }
        if(url.startsWith("/images/")){
        	url = webRootNdc + url;
        }
        HttpClient client = HttpClients.custom().build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36");

        InputStream is = null;
        try {
            HttpResponse response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
            	LOGGER.info("无法抓取到图片，抓取地址为：{},状态码为：{}", url, statusCode);
            } else {
                is = response.getEntity().getContent();
                setIntoFile(is);
            }
            return is;
        } catch (Exception e) {
        	LOGGER.info("无法抓取到图片，抓取地址为：{}", url);
        	LOGGER.info("Exception message is: {}", e);
            if (url.startsWith("https")) {
            	LOGGER.info("改用http抓取一次图片");
                url = url.replace("https", "http");
                setImgByUrl(url, webRootNdc);
            } else {
                setIntoFile(null);
            }
        } finally {
            if (is != null) try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }
        return null;
    }

    public int[] getImgWidthHeight(InputStream is) {
        int[] ia = new int[2];
        try {
            BufferedImage bi = ImageIO.read(is);
            ia[0] = bi.getWidth();
            ia[1] = bi.getHeight();
        } catch (Exception e) {
        	LOGGER.warn("getImgWidthHeight failed.", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ia;
    }

    public abstract void setIntoFile(InputStream is) throws Exception;
}
