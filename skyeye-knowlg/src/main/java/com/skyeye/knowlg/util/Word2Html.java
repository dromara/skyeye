/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.knowlg.util;

import fr.opensagres.poi.xwpf.converter.core.FileImageExtractor;
import fr.opensagres.poi.xwpf.converter.core.FileURIResolver;
import fr.opensagres.poi.xwpf.converter.core.IURIResolver;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Word2Html {

	/**
	 * 将Word2007+转成Html
	 * 
	 * @param file 文件
	 * @param imagesPath 文件内图片路径
	 * @param id id
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> word2007ToHtml(File file, String imagesPath, String id) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("code", 0);// 0失败；1成功
		map.put("content", "转换失败");
		if (!file.exists()) {
			map.put("content", "Sorry File does not Exists!");
		} else {
			/* 判断是否为docx文件 */
			if (file.getName().endsWith(".docx") || file.getName().endsWith(".DOCX") || file.getName().endsWith(".doc") || file.getName().endsWith(".DOC")) {
				try {
					// 1)加载word文档生成XWPFDocument对象
					InputStream in = new FileInputStream(file);
					XWPFDocument document = new XWPFDocument(in);
					// 2)解析XHTML配置（这里设置IURIResolver来设置图片存放的目录）
					File imageFolderFile = new File(imagesPath + id + "\\");
					if(!imageFolderFile.exists()){
						imageFolderFile.mkdirs();
					}
					XHTMLOptions options = XHTMLOptions.create().URIResolver(new FileURIResolver(imageFolderFile));
					options.setExtractor(new FileImageExtractor(imageFolderFile));
					options.setIgnoreStylesIfUnused(false);
					options.URIResolver(new IURIResolver() {
						public String resolve(String uri) {
					    	return "\\images\\upload\\ueditor\\" + id + "\\" + uri;
				    	}
					});
					options.setFragment(true);
					// 可以使用字符数组流获取解析的内容
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					XHTMLConverter.getInstance().convert(document, baos, options);
					String content = baos.toString();
					baos.close();
					map.put("code", 1);// 0失败；1成功
					map.put("content", content);
				}catch (Exception e) {
					map.put("content", "读取失败");
				}
			} else {
				map.put("content", "Enter only as MS Office 2007+ files");
			}
		}
		return map;
	}

	/**
	 * word2003-2007转换成html
	 * 
	 * @param file 文件
	 * @param imagePath 文件内图片路径
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> wordToHtml(File file, String imagePath) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("code", 0);// 0失败；1成功
		map.put("content", "转换失败");
		InputStream input = new FileInputStream(file);
		HWPFDocument wordDocument = new HWPFDocument(input);
		WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
		// 设置图片存储位置
		wordToHtmlConverter.setPicturesManager(new PicturesManager() {
			public String savePicture(byte[] content, PictureType pictureType, String suggestedName, float widthInches, float heightInches) {
				// 首先要判断图片是否能识别
				if (pictureType.equals(PictureType.UNKNOWN)) {
					return "";
				}
				File imgPath = new File(imagePath);
				if (!imgPath.exists()) {// 目录不存在则创建目录
					imgPath.mkdirs();
				}
				File file = new File(imagePath + suggestedName);
				try {
					FileOutputStream os = null;
					try {
						os = new FileOutputStream(file);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					os.write(content);
					os.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 这个地方一般是将文件上传到第三方存储文件的服务器中，然后返回对应图片地址/images/upload/ueditor/文件名.类型
				return "/images/upload/ueditor/" + suggestedName;
			}
		});

		// 解析word文档
        wordToHtmlConverter.processDocument(wordDocument);
        Document htmlDocument = wordToHtmlConverter.getDocument();

        // 也可以使用字符数组流获取解析的内容
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(baos);

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer serializer = factory.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);

        // 也可以使用字符数组流获取解析的内容
        String content = new String(baos.toByteArray());
        baos.close();
        map.put("code", 1);// 0失败；1成功
		map.put("content", content);
		return map;
	}
	
	/**
	 * 获取html内容
	 * @param content
	 * @return
	 */
	public static String getHtmlBodyAndCSS(String content) {
		org.jsoup.nodes.Document document = Jsoup.parse(content);// 获得document
		Elements body = document.getElementsByTag("body");
		Elements css = document.getElementsByTag("style");
		return "<style type='text/css'>" + css.html() + "</style>" + body.html();
	}
	
	/**
	 * 获取html的body内容
	 * @param content
	 * @return
	 */
	public static String getHtmlBody(String content) {
		org.jsoup.nodes.Document document = Jsoup.parse(content);// 获得document
		Elements body = document.getElementsByTag("body");
		return body.html();
	}
	
}
