/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.common.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

	/**
	 * 创建excel文档，
	 * 
	 * @param list 数据
	 * @param keys list中map的key数组集合
	 * @param columnNames excel的列名
	 * @throws Exception
	 */
	public static void createWorkBook(String fileName, String sheetName, List<Map<String, Object>> list, String[] keys,
			String[] columnNames, String[] dataType, HttpServletResponse response) throws Exception {
		// 创建excel工作簿
		Workbook wb = new HSSFWorkbook();
		// 创建第一个sheet（页），并命名
		Sheet sheet = wb.createSheet(sheetName);
		// 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
		for (int i = 0; i < keys.length; i++) {
			sheet.setColumnWidth((short) i, (short) (35.7 * 150));
		}

		// 创建第一行
		Row row = sheet.createRow((short) 0);

		//获取样式
        CellStyle titleStyle = getTextCellStyle(wb, true);//标题style
        CellStyle dataStyle = getDataCellStyle(wb, false);//日期style
        CellStyle contextStyle = getTextCellStyle(wb, false);//文本style
		
		// 设置列名
		for (int i = 0; i < columnNames.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(columnNames[i]);
			cell.setCellStyle(titleStyle);
		}
		// 设置格式
		if (dataType.length > 0 && dataType != null) {
			for (short j = 0; j < 500; j++) {
				Row item = sheet.createRow((short) (j + 1));
				for (short i = 0; i < dataType.length; i++) {
					Cell cellItem = item.createCell(i);
					if ("data".equals(dataType[i])) {
						cellItem.setCellStyle(dataStyle);// 日期
					} else {
						cellItem.setCellStyle(contextStyle);
					}
				}
			}
		}
		if (list != null && !list.isEmpty()) {
			// 设置每行每列的值
			for (short i = 0; i < list.size(); i++) {
				// Row 行,Cell 方格 , Row 和 Cell 都是从0开始计数的
				// 创建一行，在页sheet上
				Row row1 = sheet.createRow((short) (i + 1));
				// 在row行上创建一个方格
				for (short j = 0; j < keys.length; j++) {
					Cell cell = row1.createCell(j);
					cell.setCellValue(list.get(i).get(keys[j]) == null ? " " : list.get(i).get(keys[j]).toString());
					cell.setCellStyle(contextStyle);
				}
			}
		}
		// 输出文件
		fileWrite(wb, response, fileName);
	}

	/**
	 * 获取日期格式的列格式
	 * 
	 * @param wb
	 * @param isTitle
	 *            是否是标题
	 * @return
	 */
	public static CellStyle getDataCellStyle(Workbook wb, boolean isTitle) {
		CellStyle cellStyle = wb.createCellStyle();
		DataFormat format = wb.createDataFormat();
		cellStyle.setDataFormat(format.getFormat("yyyy年mm月dd日"));
		return getDataStyle(wb, cellStyle, isTitle);
	}

	/**
	 * 获取文本格式的列格式
	 * 
	 * @param wb
	 * @param isTitle
	 *            是否是标题
	 * @return
	 */
	public static CellStyle getTextCellStyle(Workbook wb, boolean isTitle) {
		CellStyle cellStyle = wb.createCellStyle();
		return getDataStyle(wb, cellStyle, isTitle);
	}

	/**
	 * 获取样式
	 * 
	 * @param wb
	 * @param cellStyle
	 * @param isTitle
	 *            是否是标题
	 * @return
	 */
	public static CellStyle getDataStyle(Workbook wb, CellStyle cellStyle, boolean isTitle) {
		if (isTitle) {// 标题
			Font f = wb.createFont();
			// 创建第一种字体样式（用于列名）
			f.setFontHeightInPoints((short) 15);
			f.setColor(IndexedColors.BLACK.getIndex());
			// 设置第一种单元格的样式（用于列名）
			cellStyle.setFont(f);
		} else {// 值
				// 创建两种字体
			Font f2 = wb.createFont();
			// 创建第二种字体样式（用于值）
			f2.setFontHeightInPoints((short) 12);
			f2.setColor(IndexedColors.BLACK.getIndex());
			// 设置第二种单元格的样式（用于值）
			cellStyle.setFont(f2);
		}
		return cellStyle;
	}

	/**
	 * 输出文件
	 * 
	 * @param wb
	 * @param response
	 * @param fileName
	 * @throws Exception
	 */
	public static void fileWrite(Workbook wb, HttpServletResponse response, String fileName) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			wb.write(os);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] content = os.toByteArray();
		InputStream is = new ByteArrayInputStream(content);
		// 设置response参数，可以打开下载页面
		response.reset();
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		response.setHeader("Content-Disposition",
				"attachment;filename=" + new String((fileName + ".xls").getBytes(), "iso-8859-1"));
		ServletOutputStream out = response.getOutputStream();
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
		} catch (final IOException e) {
			throw e;
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}
	}

	/**
	 * 读取Excel数据内容
	 * @param is
	 * @return 含单元格数据内容的Map对象
	 */
	public static List<List<String>> readExcelContent(InputStream is) {
		List<List<String>> content = new ArrayList<List<String>>();
		POIFSFileSystem fs;
		Workbook wb;
		Sheet sheet;
		Row row;
		String str = "";
		try {
			fs = new POIFSFileSystem(is);
			wb = new HSSFWorkbook(fs);
			sheet = wb.getSheetAt(0);
			// 得到总行数
			int rowNum = sheet.getLastRowNum();
			row = sheet.getRow(0);
			int colNum = row.getPhysicalNumberOfCells();
			// 正文内容应该从第二行开始,第一行为表头的标题
			for (int i = 1; i <= rowNum; i++) {
				row = sheet.getRow(i);
				int j = 0;
				List<String> list = new ArrayList<String>();
				while (j < colNum) {
					// 每个单元格的数据内容用"-"分割开，以后需要时用String类的replace()方法还原数据
					// 也可以将每个单元格的数据设置到一个javabean的属性中，此时需要新建一个javabean
					// str += getStringCellValue(row.getCell((short) j)).trim()
					// +
					// "-";
					str = getCellFormatValue(row.getCell((short) j)).trim();
					list.add(str);
					j++;
				}
				content.add(list);
				str = "";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * 根据HSSFCell类型设置数据
	 * 
	 * @param cell
	 * @return
	 */
	private static String getCellFormatValue(Cell cell) {
		String cellvalue = "";
		if (cell != null) {
			// 取得当前的Cell字符串
			cellvalue = cell.getRichStringCellValue().getString();
		} else {
			cellvalue = "";
		}
		return cellvalue;

	}

}
