/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.export;

import com.youbenzi.md2.markdown.Block;
import com.youbenzi.md2.markdown.BlockType;
import com.youbenzi.md2.markdown.ValuePart;
import com.youbenzi.md2.util.ImgHelper;
import com.youbenzi.md2.util.MD2FileUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.*;
import java.math.BigInteger;
import java.util.List;

public class DocxDecorator implements Decorator {

	private XWPFDocument doc;

	public DocxDecorator(XWPFDocument doc) {
		this.doc = doc;
	}

	public void beginWork(String outputFilePath, String sysWaterMark) {
	};

	public void decorate(List<Block> list, String webRootNdc) throws Exception {
		for (Block block : list) {
			XWPFParagraph paragraph = null;
			if (block.getType() != BlockType.TABLE) {
				paragraph = doc.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.LEFT);
			}
			switch (block.getType()) {
			case CODE:
				codeParagraph(paragraph, block.getValueParts());
				break;
			case HEADLINE:
				headerParagraph(paragraph, block.getValueParts(), block.getLevel());
				break;
			case QUOTE:
				quoteParagraph(block.getListData());
				break;
			case TABLE:
				tableParagraph(block.getTableData());
				break;
			case ORDERED_LIST:
				listParagraph(block.getListData(), true, webRootNdc);
				break;
			case UNORDERED_LIST:
				listParagraph(block.getListData(), false, webRootNdc);
				break;
			default:
				commonTextParagraph(paragraph, block.getValueParts(), webRootNdc);
				break;
			}
		}
	}

	public void afterWork(String outputFilePath) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outputFilePath);
			doc.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void listParagraph(List<Block> listData, boolean isOrder, String webRootNdc) throws Exception {
		int j = 1;
		for (Block block : listData) {
			XWPFParagraph paragraph = doc.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.LEFT);
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
			commonTextParagraph(paragraph, newVps, webRootNdc);
			j++;
		}
	}

	private void tableParagraph(List<List<String>> tableData) {
		int nRows = tableData.size();
		int nCols = tableData.get(0).size();
		long tdW = (9000 / nCols < 1000) ? 1000 : (9000 / nCols); // 计算表格每个单位宽度，最小不小于1000
		XWPFTable table = doc.createTable(nRows, nCols);

		List<XWPFTableRow> rows = table.getRows();
		int rowCt = 0;
		int colCt = 0;
		for (XWPFTableRow row : rows) {
			CTTrPr trPr = row.getCtRow().addNewTrPr();
			CTHeight ht = trPr.addNewTrHeight();
			ht.setVal(BigInteger.valueOf(360)); // 设置表格每行高度

			List<XWPFTableCell> cells = row.getTableCells();
			for (XWPFTableCell cell : cells) {
				CTTcPr tcpr = cell.getCTTc().addNewTcPr();
				tcpr.addNewTcW().setW(BigInteger.valueOf(tdW)); // 设置表格每个单位宽度

				CTVerticalJc va = tcpr.addNewVAlign();
				va.setVal(STVerticalJc.CENTER);
				CTShd ctshd = tcpr.addNewShd();
				ctshd.setColor("auto");
				ctshd.setVal(STShd.CLEAR);
				XWPFParagraph para = cell.getParagraphs().get(0);
				para.setAlignment(ParagraphAlignment.LEFT);
				XWPFRun rh = para.createRun();

				try {
					rh.setText(tableData.get(rowCt).get(colCt)); // 用try
																	// catch处理越界的情况
				} catch (Exception e) {
					rh.setText("");
				}

				if (rowCt == 0) {
					rh.setBold(true);
				}
				rh.setFontSize(12);
				colCt++;
			}
			colCt = 0;
			rowCt++;
		}
	}

	private void codeParagraph(XWPFParagraph p, ValuePart[] valueParts) {

		p.setBorderTop(Borders.BASIC_THIN_LINES);
		p.setBorderBottom(Borders.BASIC_THIN_LINES);
		p.setBorderLeft(Borders.BASIC_THIN_LINES);
		p.setBorderRight(Borders.BASIC_THIN_LINES);

		String value = valueParts[0].getValue();
		String[] vals = value.split("\n");
		for (int i = 0, l = vals.length; i < l; i++) {
			String val = vals[i];
			XWPFRun r = p.createRun();
			r.setBold(false);
			r.setText(val);
			r.setFontFamily("Courier");
			if ((i + 1) < l) {
				r.addBreak();
			}
		}
	}

	private void headerParagraph(XWPFParagraph p, ValuePart[] valueParts, int levle) {
		for (ValuePart valuePart : valueParts) {
			XWPFRun r = p.createRun();
			BlockType[] types = valuePart.getTypes();
			if (types != null) {
				for (BlockType type : types) {
					formatByType(r, type, valuePart.getLevel());
				}
			}
			r.setText(valuePart.getValue());
			r.setFontSize(30 - 4 * levle);
		}
	}

	private void quoteParagraph(List<Block> listData) {
		boolean isFirst = true;
		for (int i = 0; i < listData.size(); i++) {
			Block block = listData.get(i);
			if (i > 0) {
				isFirst = false;
			}

			XWPFParagraph paragraph = doc.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.LEFT);
			quoteParagraph(paragraph, block.getValueParts(), isFirst);
		}
	}

	private void quoteParagraph(XWPFParagraph p, ValuePart[] valueParts, boolean isFirst) {
		if (isFirst) {
			XWPFRun r1 = p.createRun();
			try {
				r1.addPicture(DocxDecorator.class.getResourceAsStream("/quote_char.jpg"),
						XWPFDocument.PICTURE_TYPE_JPEG, "", Units.toEMU(20), Units.toEMU(14));
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (ValuePart valuePart : valueParts) {
			if (!isFirst) {
				XWPFRun rh1 = p.createRun();
				rh1.setText("    ");
				rh1.setFontSize(22);
			}

			XWPFRun rh2 = p.createRun();
			BlockType[] types = valuePart.getTypes();
			if (types != null) {
				for (BlockType type : types) {
					formatByType(rh2, type, valuePart.getLevel());
				}
			}
			if (rh2.getFontSize() == -1) {
				rh2.setFontSize(10);
			}
			rh2.setText(valuePart.getValue());
		}
	}

	private void imgParagraph(final XWPFParagraph p, ValuePart valuePart, String webRootNdc) throws Exception {
		final String imgFile = valuePart.getValue();
		ImgHelper helper = new ImgHelper() {
			@Override
			public void setIntoFile(InputStream is) {
				XWPFRun r = p.createRun();
				if (is == null) { // 如果图片不存在，则直接输出内容
					r.setText("图片地址：" + imgFile);
					return;
				}
				ByteArrayOutputStream baos = null;
				InputStream imgIs = null;
				try {
					baos = MD2FileUtil.inputStream2ByteArrayOutputStream(is);
					InputStream tmpIs = new ByteArrayInputStream(baos.toByteArray());
					int[] ia = computeWidthHeight2Show(getImgWidthHeight(tmpIs));

					imgIs = new ByteArrayInputStream(baos.toByteArray());
					r.addPicture(imgIs, typeOfImg(imgFile), "", Units.toEMU(ia[0]), Units.toEMU(ia[1]));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					closeStream(baos, imgIs);
				}
			}
		};
		helper.setImgByUrl(imgFile, webRootNdc);
	}

	private void commonTextParagraph(XWPFParagraph p, ValuePart[] valueParts, String webRootNdc) throws Exception {

		for (ValuePart valuePart : valueParts) {
			XWPFRun r = p.createRun();
			BlockType[] types = valuePart.getTypes();
			boolean hasImg = false;
			if (types != null) {
				for (BlockType type : types) {
					if (type == BlockType.IMG) {
						hasImg = true;
						break;
					}
					formatByType(r, type, valuePart.getLevel());
				}
			}
			if (hasImg) {
				imgParagraph(p, valuePart, webRootNdc);
			} else {
				r.setText(valuePart.getValue());
				r.setFontSize(14);
			}
		}
	}

	private int typeOfImg(String imgFile) {
		int format = XWPFDocument.PICTURE_TYPE_JPEG;

		if (imgFile.endsWith(".emf")) {
			format = XWPFDocument.PICTURE_TYPE_EMF;
		} else if (imgFile.endsWith(".wmf")) {
			format = XWPFDocument.PICTURE_TYPE_WMF;
		} else if (imgFile.endsWith(".pict")) {
			format = XWPFDocument.PICTURE_TYPE_PICT;
		} else if (imgFile.endsWith(".jpeg") || imgFile.endsWith(".jpg")) {
			format = XWPFDocument.PICTURE_TYPE_JPEG;
		} else if (imgFile.endsWith(".png")) {
			format = XWPFDocument.PICTURE_TYPE_PNG;
		} else if (imgFile.endsWith(".dib")) {
			format = XWPFDocument.PICTURE_TYPE_DIB;
		} else if (imgFile.endsWith(".gif")) {
			format = XWPFDocument.PICTURE_TYPE_GIF;
		} else if (imgFile.endsWith(".tiff")) {
			format = XWPFDocument.PICTURE_TYPE_TIFF;
		} else if (imgFile.endsWith(".eps")) {
			format = XWPFDocument.PICTURE_TYPE_EPS;
		} else if (imgFile.endsWith(".bmp")) {
			format = XWPFDocument.PICTURE_TYPE_BMP;
		} else if (imgFile.endsWith(".wpg")) {
			format = XWPFDocument.PICTURE_TYPE_WPG;
		} else {
			System.out.println(
					"Unsupported picture: " + imgFile + ". Expected emf|wmf|pict|jpeg|png|dib|gif|tiff|eps|bmp|wpg");
		}
		return format;
	}

	private int maxWidth = 400;

	private int[] computeWidthHeight2Show(int[] ia) {
		if (ia == null || ia.length < 2) {
			return new int[] { 100, 100 };
		}
		int w = ia[0];
		int h = ia[1];
		if (w > maxWidth) {
			double d = ((double) w) / maxWidth;
			h = (int) (((double) h) / d);
			w = maxWidth;
		}
		return new int[] { w, h };
	}

	private void closeStream(ByteArrayOutputStream baos, InputStream imgIs) {
		if (baos != null) {
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (imgIs != null) {
			try {
				imgIs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void formatByType(XWPFRun r, BlockType type, int level) {
		switch (type) {
		case BOLD_WORD:
			r.setBold(true);
			break;
		case ITALIC_WORD:
			r.setItalic(true);
			break;
		case STRIKE_WORD:
			r.setStrike(true);
			break;
		case CODE_WORD:
			r.setColor("dd1144");
			break;
		case HEADLINE:
			r.setFontSize(30 - level * 4);
			break;
		default:
			break;
		}
	}
}
