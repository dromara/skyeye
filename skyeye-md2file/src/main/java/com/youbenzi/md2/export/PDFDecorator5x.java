/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.skyeye.common.constans.Md2FileConstants;
import com.youbenzi.md2.markdown.Block;
import com.youbenzi.md2.markdown.BlockType;
import com.youbenzi.md2.markdown.ValuePart;
import com.youbenzi.md2.util.ImgHelper;
import com.youbenzi.md2.util.MD2FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDFDecorator5x implements Decorator {
	private Document doc;
	private static BaseFont bfChinese;

	private PdfWriter pdfWriter;

	private String sysWaterMark;

	private Pattern pattern = Pattern.compile(".*!\\[.*?\\]\\(.*?\\).*");

	private static Logger LOGGER = LoggerFactory.getLogger(PDFDecorator5x.class);

	static {
		try {
			bfChinese = BaseFont.createFont("MSYH.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		} catch (Exception e1) {
			try {
				bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			} catch (Exception e2) {
			}
			LOGGER.info("没有找到MSYH.TTF字体文件，使用itext自带中文字体。如果需要更好的显示效果，可以添加MSYH.TTF到src目录下");
		}
	}

	Font fontYHNormal = new Font(bfChinese, 12, Font.NORMAL);

	public PDFDecorator5x(Document doc) {
		this.doc = doc;
	}

	public void beginWork(String outputFilePath, String sysWaterMark) throws Exception {
		pdfWriter = PdfWriter.getInstance(doc, new FileOutputStream(outputFilePath));
		this.sysWaterMark = sysWaterMark;
		pdfWriter.setPageEvent(new PDFDecorator5xHelper(this.sysWaterMark));
		doc.open();
	}

	public void decorate(List<Block> list, String webRootNdc) throws Exception {
		for (Block block : list) {
			switch (block.getType()) {
			case CODE:
				doc.add(codeParagraph(block.getValueParts()));
				break;
			case HEADLINE:
				doc.add(headerParagraph(block.getValueParts(), block.getLevel()));
				break;
			case QUOTE:
				List<Element> quotes = quoteParagraph(block.getListData());
				for (Element element : quotes) {
					doc.add(element);
				}
				break;
			case TABLE:
				doc.add(tableParagraph(block.getTableData(), webRootNdc));
				break;
			case ORDERED_LIST:
				List<Element> els = listParagraph(block.getListData(), true, webRootNdc);
				for (Element element : els) {
					doc.add(element);
				}
				break;
			case UNORDERED_LIST:
				els = listParagraph(block.getListData(), false, webRootNdc);
				for (Element element : els) {
					doc.add(element);
				}
				break;
			default:
				doc.add(commonTextParagraph(block.getValueParts(), webRootNdc));
				break;
			}
		}
	}

	public void afterWork(String outputFilePath) {
		doc.close();
	}

	private List<Element> listParagraph(List<Block> listData, boolean isOrder, String webRootNdc) throws Exception {
		List<Element> list = new ArrayList<Element>();
		int j = 1;
		for (Block block : listData) {
			ValuePart[] vps = block.getValueParts();
			ValuePart[] newVps = new ValuePart[vps.length + 1];
			if (isOrder) {
				newVps[0] = new ValuePart(j + ". ");
			} else {
				newVps[0] = new ValuePart("• ");
			}
			for (int i = 1; i < newVps.length; i++) {
				newVps[i] = vps[i - 1];
			}
			list.add(commonTextParagraph(newVps, webRootNdc));
			j++;
		}
		return list;
	}

	private Element headerParagraph(ValuePart[] valueParts, int level) {
		Paragraph paragraph = new Paragraph();
		for (ValuePart valuePart : valueParts) {
			BlockType[] types = valuePart.getTypes();
			Font font = new Font(bfChinese, 30 - 4 * level);
			if (types != null) {
				for (BlockType type : types) {
					formatByType(font, type, valuePart.getLevel());
				}
			}
			Chunk chunk = new Chunk(valuePart.getValue(), font);
			paragraph.add(chunk);
		}
		paragraph.setSpacingBefore(20);
		paragraph.setSpacingAfter(10);
		return paragraph;
	}

	private Element tableParagraph(List<List<String>> tableData, String webRootNdc) {
		int nRows = tableData.size();
		int nCols = 0;
		for (List<String> list : tableData) {
			int s = list.size();
			if (nCols < s) {
				nCols = s;
			}
		}

		PdfPTable pdfPTable = new PdfPTable(nCols);
		pdfPTable.setWidthPercentage(100);
		Font font = new Font(bfChinese, 12);
		for (int i = 0; i < nRows; i++) {
			List<String> colDatas = tableData.get(i);
			for (int j = 0; j < nCols; j++) {
				PdfPCell cell = new PdfPCell();
				if (i == 0) {
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				}
				cell.setPaddingBottom(12);
				cell.setPaddingLeft(12);
				cell.setPaddingRight(12);
				try {
					String cellStr = colDatas.get(j);
					if (pattern.matcher(cellStr).matches()) {
						// 匹配图片成功
						String imgFile = getImagePath(cellStr);
						ImgHelper helper = initImgHelper(pdfPTable, cell, imgFile);
						InputStream result = helper.setImgByUrl(imgFile, webRootNdc);
						if (result == null) {
							cell.addElement(new Chunk(cellStr, font));
						}
					} else {
						cell.addElement(new Chunk(cellStr, font));
					}
				} catch (Exception e) {
					cell.addElement(new Chunk("", font));
				}
				pdfPTable.addCell(cell);
			}
		}

		pdfPTable.setSpacingAfter(5);
		return pdfPTable;
	}

	private String getImagePath(String str) {
		String regular = String.format("%s%s%s", "((http|https|/images).+\\.(", getImageFileStr(), "))");
		Pattern pattern = Pattern.compile(regular);
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	private String getImageFileStr() {
		StringBuffer sb = new StringBuffer();
		Arrays.asList(Md2FileConstants.SYS_FILE_CONSOLE_IS_IMAGES).stream().forEach(url -> sb.append("|").append(url));
		sb.replace(0, 1, "");
		return sb.toString();
	}

	private Element imgParagraph(ValuePart valuePart, String webRootNdc) throws Exception {
		final String imgFile = valuePart.getValue();
		final PdfPTable pdfPTable = new PdfPTable(1);
		final PdfPCell cell = new PdfPCell();
		ImgHelper helper = initImgHelper(pdfPTable, cell, imgFile);
		InputStream result = helper.setImgByUrl(imgFile, webRootNdc);
		if (result == null) {
			ValuePart part = new ValuePart("图片地址：" + imgFile);
			return commonTextParagraph(new ValuePart[] { part }, webRootNdc);
		}
		pdfPTable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
		pdfPTable.addCell(cell);
		pdfPTable.setSpacingAfter(5);
		return pdfPTable;
	}

	private ImgHelper initImgHelper(PdfPTable pdfPTable, PdfPCell cell, String imgFile) {
		return new ImgHelper() {
			@Override
			public void setIntoFile(InputStream is) throws Exception {
				if (is == null) {
					return;
				}
				ByteArrayOutputStream baos = MD2FileUtil.inputStream2ByteArrayOutputStream(is);
				byte[] bs = baos.toByteArray();
				Image image = Image.getInstance(bs);
				InputStream tmpIs = new ByteArrayInputStream(baos.toByteArray());
				int[] wh = getImgWidthHeight(tmpIs);
				int p = wh[0] * 100 / 1000;
				p = p > 100 ? 100 : p;
				pdfPTable.setWidthPercentage(p);
				cell.setBorder(PdfPCell.NO_BORDER);
				cell.addElement(image);
			}
		};
	}

	private Element codeParagraph(ValuePart[] valueParts) throws DocumentException {
		String value = valueParts[0].getValue();
		Paragraph p = new Paragraph(value, new Font(bfChinese, 12));

		PdfPTable pdfPTable = new PdfPTable(1);
		pdfPTable.setWidthPercentage(100);

		PdfPCell cell = new PdfPCell();
		cell.addElement(p);
		cell.setBorder(Rectangle.BOX);
		cell.setPaddingBottom(12);
		cell.setPaddingLeft(12);
		cell.setPaddingRight(12);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		pdfPTable.addCell(cell);
		pdfPTable.setSpacingAfter(5);
		return pdfPTable;
	}

	private List<Element> quoteParagraph(List<Block> listData) throws Exception {
		boolean isFirst = true;
		List<Element> results = new ArrayList<Element>();
		for (int i = 0; i < listData.size(); i++) {
			Block block = listData.get(i);
			if (i > 0) {
				isFirst = false;
			}
			Element element = quoteParagraph(block.getValueParts(), isFirst);
			results.add(element);
		}
		return results;
	}

	private Element quoteParagraph(ValuePart[] valueParts, boolean isFirst) throws Exception {
		Paragraph p = new Paragraph();
		for (ValuePart valuePart : valueParts) {
			BlockType[] types = valuePart.getTypes();
			Font font = new Font(bfChinese);
			if (types != null) {
				for (BlockType type : types) {
					formatByType(font, type, valuePart.getLevel());
				}
			}
			Chunk chunk = new Chunk(valuePart.getValue(), font);
			p.add(chunk);
		}

		PdfPTable pdfPTable = new PdfPTable(2);
		pdfPTable.setWidthPercentage(100);
		pdfPTable.setWidths(new int[] { 1, 20 });

		PdfPCell cell = new PdfPCell();
		if (isFirst) {
			InputStream is = PDFDecorator5x.class.getResourceAsStream("/quote_char.jpg");
			Image image = Image.getInstance(MD2FileUtil.inputStream2ByteArrayOutputStream(is).toByteArray());
			cell.addElement(image);
		} else {
			cell.addElement(new Chunk());
		}
		cell.setBorder(PdfPCell.NO_BORDER);
		pdfPTable.addCell(cell);

		cell = new PdfPCell();
		cell.addElement(p);
		cell.setBorder(PdfPCell.NO_BORDER);
		if (isFirst) {
			cell.setPaddingTop(10);
		}
		cell.setPaddingBottom(5);
		pdfPTable.addCell(cell);

		return pdfPTable;
	}

	private Paragraph commonTextParagraph(ValuePart[] valueParts, String webRootNdc) throws Exception {
		Paragraph p = new Paragraph();
		if (valueParts == null) {
			return p;
		}
		for (ValuePart valuePart : valueParts) {
			BlockType[] types = valuePart.getTypes();
			Font font = new Font(bfChinese);
			boolean hasImg = false;
			if (types != null) {
				for (BlockType type : types) {
					if (type == BlockType.IMG) {
						hasImg = true;
						break;
					}
					formatByType(font, type, valuePart.getLevel());
				}
			}
			if (hasImg) {
				p.add(imgParagraph(valuePart, webRootNdc));
			} else {
				Chunk chunk = new Chunk(valuePart.getValue(), font);
				p.add(chunk);
			}
		}
		p.setSpacingAfter(5);

		return p;
	}

	private void formatByType(Font font, BlockType type, int level) {
		switch (type) {
		case BOLD_WORD:
			font.setStyle(Font.BOLD);
			break;
		case ITALIC_WORD:
			font.setStyle(Font.ITALIC);
			break;
		case STRIKE_WORD:
			font.setStyle(Font.STRIKETHRU);
			break;
		case CODE_WORD:
			font.setColor(BaseColor.RED);
			break;
		case HEADLINE:
			font.setSize(16);
			font.setStyle(Font.BOLD);
			break;
		default:
			break;
		}
	}
}
